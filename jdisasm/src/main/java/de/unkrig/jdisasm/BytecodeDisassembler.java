
/*
 * JDISASM - A Java[TM] class file disassembler
 *
 * Copyright (c) 2017, Arno Unkrig
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.unkrig.jdisasm;

import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import de.unkrig.commons.nullanalysis.NotNullByDefault;
import de.unkrig.commons.nullanalysis.Nullable;
import de.unkrig.jdisasm.ClassFile.BootstrapMethodsAttribute.BootstrapMethod;
import de.unkrig.jdisasm.ClassFile.ExceptionTableEntry;
import de.unkrig.jdisasm.ClassFile.LineNumberTableAttribute;
import de.unkrig.jdisasm.ClassFile.LineNumberTableEntry;
import de.unkrig.jdisasm.ClassFile.Method;
import de.unkrig.jdisasm.ConstantPool.ConstantClassInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantClassOrFloatOrIntegerOrStringInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantDoubleOrLongInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantFieldrefInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantInterfaceMethodrefInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantInterfaceMethodrefOrMethodrefInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantInvokeDynamicInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantMethodrefInfo;

/**
 * Disassembles the bytecode of a class file method.
 */
public
class BytecodeDisassembler {

    /**
     * Static description of an operand of a Java byte code instruction.
     */
    interface Operand {

        /**
         * @return This operand disassembled
         */
        String
        disassemble(BytecodeDisassembler bd) throws IOException;
    }

    private final CountingInputStream                cis;
    final DataInputStream                            dis;
    private final List<ExceptionTableEntry>          exceptionTable;
    @Nullable private final LineNumberTableAttribute lineNumberTableAttribute;
    private final Map<Integer, String>               sourceLines;
    final Method                                     method;
    final Disassembler                               d;

    int                                                     instructionOffset;
    private final Map<Integer /*offset*/, String /*label*/> branchTargets = new HashMap<Integer, String>();
    private final SortedMap<Integer, String>                lines         = new TreeMap<Integer, String>();

    public
    BytecodeDisassembler(
        InputStream                        is,
        List<ExceptionTableEntry>          exceptionTable,
        @Nullable LineNumberTableAttribute lineNumberTableAttribute,
        Map<Integer, String>               sourceLines,
        Method                             method,
        Disassembler                       d
    ) {
        this.cis                      = new CountingInputStream(is);
        this.dis                      = new DataInputStream(this.cis);
        this.exceptionTable           = exceptionTable;
        this.lineNumberTableAttribute = lineNumberTableAttribute;
        this.sourceLines              = sourceLines;
        this.method                   = method;
        this.d                        = d;
    }

    /**
     * Reads byte code from the given {@link InputStream} and disassemble it.
     */
    public void
    disassembleBytecode(PrintWriter pw) throws IOException {

        // Analyze the TRY bodies.

        SortedMap<Integer /*startPC*/, Set<Integer /*endPC*/>>
        tryStarts = new TreeMap<Integer, Set<Integer>>();

        SortedMap<Integer /*endPC*/, SortedMap<Integer /*startPC*/, List<ExceptionTableEntry>>>
        tryEnds = new TreeMap<Integer, SortedMap<Integer, List<ExceptionTableEntry>>>();

        for (ExceptionTableEntry e : this.exceptionTable) {

            // Register the entry in "tryStarts".
            {
                Set<Integer> s = tryStarts.get(e.startPc);
                if (s == null) {
                    s = new HashSet<Integer>();
                    tryStarts.put(e.startPc, s);
                }
                s.add(e.endPc);
            }

            // Register the entry in "tryEnds".
            {
                SortedMap<Integer, List<ExceptionTableEntry>> m = tryEnds.get(e.endPc);
                if (m == null) {
                    m = new TreeMap<Integer, List<ExceptionTableEntry>>(Collections.reverseOrder());
                    tryEnds.put(e.endPc, m);
                }
                List<ExceptionTableEntry> l = m.get(e.startPc);
                if (l == null) {
                    l = new ArrayList<ExceptionTableEntry>();
                    m.put(e.startPc, l);
                }
                l.add(e);
            }
        }

        for (;;) {
            this.instructionOffset = (int) this.cis.getCount();

            int opcode = this.dis.read();
            if (opcode == -1) break;

            switch (opcode) {

            case 50:   this.da("aaload");                                                                   break;
            case 83:   this.da("aastore");                                                                  break;
            case 1:    this.da("aconst_null");                                                              break;
            case 25:   this.da("aload",           OperandKind.LOCALVARIABLEINDEX1);                         break;
            case 42:   this.da("aload_0",         OperandKind.IMPLICITLOCALVARIABLEINDEX_0);                break;
            case 43:   this.da("aload_1",         OperandKind.IMPLICITLOCALVARIABLEINDEX_1);                break;
            case 44:   this.da("aload_2",         OperandKind.IMPLICITLOCALVARIABLEINDEX_2);                break;
            case 45:   this.da("aload_3",         OperandKind.IMPLICITLOCALVARIABLEINDEX_3);                break;
            case 189:  this.da("anewarray",       OperandKind.CLASS2);                                      break;
            case 176:  this.da("areturn");                                                                  break;
            case 190:  this.da("arraylength");                                                              break;
            case 58:   this.da("astore",          OperandKind.LOCALVARIABLEINDEX1);                         break;
            case 75:   this.da("astore_0",        OperandKind.IMPLICITLOCALVARIABLEINDEX_0);                break;
            case 76:   this.da("astore_1",        OperandKind.IMPLICITLOCALVARIABLEINDEX_1);                break;
            case 77:   this.da("astore_2",        OperandKind.IMPLICITLOCALVARIABLEINDEX_2);                break;
            case 78:   this.da("astore_3",        OperandKind.IMPLICITLOCALVARIABLEINDEX_3);                break;
            case 191:  this.da("athrow");                                                                   break;
            case 51:   this.da("baload");                                                                   break;
            case 84:   this.da("bastore");                                                                  break;
            case 16:   this.da("bipush",          OperandKind.SIGNEDBYTE);                                  break;
            case 52:   this.da("caload");                                                                   break;
            case 85:   this.da("castore");                                                                  break;
            case 192:  this.da("checkcast",       OperandKind.CLASS2);                                      break;
            case 144:  this.da("d2f");                                                                      break;
            case 142:  this.da("d2i");                                                                      break;
            case 143:  this.da("d2l");                                                                      break;
            case 99:   this.da("dadd");                                                                     break;
            case 49:   this.da("daload");                                                                   break;
            case 82:   this.da("dastore");                                                                  break;
            case 152:  this.da("dcmpg");                                                                    break;
            case 151:  this.da("dcmpl");                                                                    break;
            case 14:   this.da("dconst_0");                                                                 break;
            case 15:   this.da("dconst_1");                                                                 break;
            case 111:  this.da("ddiv");                                                                     break;
            case 24:   this.da("dload",           OperandKind.LOCALVARIABLEINDEX1);                         break;
            case 38:   this.da("dload_0",         OperandKind.IMPLICITLOCALVARIABLEINDEX_0);                break;
            case 39:   this.da("dload_1",         OperandKind.IMPLICITLOCALVARIABLEINDEX_1);                break;
            case 40:   this.da("dload_2",         OperandKind.IMPLICITLOCALVARIABLEINDEX_2);                break;
            case 41:   this.da("dload_3",         OperandKind.IMPLICITLOCALVARIABLEINDEX_3);                break;
            case 107:  this.da("dmul");                                                                     break;
            case 119:  this.da("dneg");                                                                     break;
            case 115:  this.da("drem");                                                                     break;
            case 175:  this.da("dreturn");                                                                  break;
            case 57:   this.da("dstore",          OperandKind.LOCALVARIABLEINDEX1);                         break;
            case 71:   this.da("dstore_0",        OperandKind.IMPLICITLOCALVARIABLEINDEX_0);                break;
            case 72:   this.da("dstore_1",        OperandKind.IMPLICITLOCALVARIABLEINDEX_1);                break;
            case 73:   this.da("dstore_1",        OperandKind.IMPLICITLOCALVARIABLEINDEX_2);                break;
            case 74:   this.da("dstore_1",        OperandKind.IMPLICITLOCALVARIABLEINDEX_3);                break;
            case 103:  this.da("dsub");                                                                     break;
            case 89:   this.da("dup");                                                                      break;
            case 90:   this.da("dup_x1");                                                                   break;
            case 91:   this.da("dup_x2");                                                                   break;
            case 92:   this.da("dup2");                                                                     break;
            case 93:   this.da("dup2_x1");                                                                  break;
            case 94:   this.da("dup2_x2");                                                                  break;
            case 141:  this.da("f2d");                                                                      break;
            case 139:  this.da("f2i");                                                                      break;
            case 140:  this.da("f2l");                                                                      break;
            case 98:   this.da("fadd");                                                                     break;
            case 48:   this.da("faload");                                                                   break;
            case 81:   this.da("fastore");                                                                  break;
            case 150:  this.da("fcmpg");                                                                    break;
            case 149:  this.da("fcmpl");                                                                    break;
            case 11:   this.da("fconst_0");                                                                 break;
            case 12:   this.da("fconst_1");                                                                 break;
            case 13:   this.da("fconst_2");                                                                 break;
            case 110:  this.da("fdiv");                                                                     break;
            case 23:   this.da("fload",           OperandKind.LOCALVARIABLEINDEX1);                         break;
            case 34:   this.da("fload_0",         OperandKind.IMPLICITLOCALVARIABLEINDEX_0);                break;
            case 35:   this.da("fload_1",         OperandKind.IMPLICITLOCALVARIABLEINDEX_1);                break;
            case 36:   this.da("fload_2",         OperandKind.IMPLICITLOCALVARIABLEINDEX_2);                break;
            case 37:   this.da("fload_3",         OperandKind.IMPLICITLOCALVARIABLEINDEX_3);                break;
            case 106:  this.da("fmul");                                                                     break;
            case 118:  this.da("fneg");                                                                     break;
            case 114:  this.da("frem");                                                                     break;
            case 174:  this.da("freturn");                                                                  break;
            case 56:   this.da("fstore",          OperandKind.LOCALVARIABLEINDEX1);                         break;
            case 67:   this.da("fstore_0",        OperandKind.IMPLICITLOCALVARIABLEINDEX_0);                break;
            case 68:   this.da("fstore_1",        OperandKind.IMPLICITLOCALVARIABLEINDEX_1);                break;
            case 69:   this.da("fstore_2",        OperandKind.IMPLICITLOCALVARIABLEINDEX_2);                break;
            case 70:   this.da("fstore_3",        OperandKind.IMPLICITLOCALVARIABLEINDEX_3);                break;
            case 102:  this.da("fsub");                                                                     break;
            case 180:  this.da("getfield",        OperandKind.FIELDREF2);                                   break;
            case 178:  this.da("getstatic",       OperandKind.FIELDREF2);                                   break;
            case 167:  this.da("goto",            OperandKind.BRANCHOFFSET2);                               break;
            case 200:  this.da("goto_w",          OperandKind.BRANCHOFFSET4);                               break;
            case 145:  this.da("i2b");                                                                      break;
            case 146:  this.da("i2c");                                                                      break;
            case 135:  this.da("i2d");                                                                      break;
            case 134:  this.da("i2f");                                                                      break;
            case 133:  this.da("i2l");                                                                      break;
            case 147:  this.da("i2s");                                                                      break;
            case 96:   this.da("iadd");                                                                     break;
            case 46:   this.da("iaload");                                                                   break;
            case 126:  this.da("iand");                                                                     break;
            case 79:   this.da("iastore");                                                                  break;
            case 2:    this.da("iconst_m1");                                                                break;
            case 3:    this.da("iconst_0");                                                                 break;
            case 4:    this.da("iconst_1");                                                                 break;
            case 5:    this.da("iconst_2");                                                                 break;
            case 6:    this.da("iconst_3");                                                                 break;
            case 7:    this.da("iconst_4");                                                                 break;
            case 8:    this.da("iconst_5");                                                                 break;
            case 108:  this.da("idiv");                                                                     break;
            case 165:  this.da("if_acmpeq",       OperandKind.BRANCHOFFSET2);                               break;
            case 166:  this.da("if_acmpne",       OperandKind.BRANCHOFFSET2);                               break;
            case 159:  this.da("if_icmpeq",       OperandKind.BRANCHOFFSET2);                               break;
            case 160:  this.da("if_icmpne",       OperandKind.BRANCHOFFSET2);                               break;
            case 161:  this.da("if_icmplt",       OperandKind.BRANCHOFFSET2);                               break;
            case 162:  this.da("if_icmpge",       OperandKind.BRANCHOFFSET2);                               break;
            case 163:  this.da("if_icmpgt",       OperandKind.BRANCHOFFSET2);                               break;
            case 164:  this.da("if_icmple",       OperandKind.BRANCHOFFSET2);                               break;
            case 153:  this.da("ifeq",            OperandKind.BRANCHOFFSET2);                               break;
            case 154:  this.da("ifne",            OperandKind.BRANCHOFFSET2);                               break;
            case 155:  this.da("iflt",            OperandKind.BRANCHOFFSET2);                               break;
            case 156:  this.da("ifge",            OperandKind.BRANCHOFFSET2);                               break;
            case 157:  this.da("ifgt",            OperandKind.BRANCHOFFSET2);                               break;
            case 158:  this.da("ifle",            OperandKind.BRANCHOFFSET2);                               break;
            case 199:  this.da("ifnonnull",       OperandKind.BRANCHOFFSET2);                               break;
            case 198:  this.da("ifnull",          OperandKind.BRANCHOFFSET2);                               break;
            case 132:  this.da("iinc",            OperandKind.LOCALVARIABLEINDEX1, OperandKind.SIGNEDBYTE); break;
            case 21:   this.da("iload",           OperandKind.LOCALVARIABLEINDEX1);                         break;
            case 26:   this.da("iload_0",         OperandKind.IMPLICITLOCALVARIABLEINDEX_0);                break;
            case 27:   this.da("iload_1",         OperandKind.IMPLICITLOCALVARIABLEINDEX_1);                break;
            case 28:   this.da("iload_2",         OperandKind.IMPLICITLOCALVARIABLEINDEX_2);                break;
            case 29:   this.da("iload_3",         OperandKind.IMPLICITLOCALVARIABLEINDEX_3);                break;
            case 104:  this.da("imul");                                                                     break;
            case 116:  this.da("ineg");                                                                     break;
            case 193:  this.da("instanceof",      OperandKind.CLASS2);                                      break;
            case 186:  this.da("invokedynamic",   OperandKind.DYNAMICCALLSITE);                             break;
            case 185:  this.da("invokeinterface", OperandKind.INTERFACEMETHODREF2);                         break;
            case 183:  this.da("invokespecial",   OperandKind.INTERFACEMETHODREFORMETHODREF2);              break;
            case 184:  this.da("invokestatic",    OperandKind.INTERFACEMETHODREFORMETHODREF2);              break;
            case 182:  this.da("invokevirtual",   OperandKind.METHODREF2);                                  break;
            case 128:  this.da("ior");                                                                      break;
            case 112:  this.da("irem");                                                                     break;
            case 172:  this.da("ireturn");                                                                  break;
            case 120:  this.da("ishl");                                                                     break;
            case 122:  this.da("ishr");                                                                     break;
            case 54:   this.da("istore",          OperandKind.LOCALVARIABLEINDEX1);                         break;
            case 59:   this.da("istore_0",        OperandKind.IMPLICITLOCALVARIABLEINDEX_0);                break;
            case 60:   this.da("istore_1",        OperandKind.IMPLICITLOCALVARIABLEINDEX_1);                break;
            case 61:   this.da("istore_2",        OperandKind.IMPLICITLOCALVARIABLEINDEX_2);                break;
            case 62:   this.da("istore_3",        OperandKind.IMPLICITLOCALVARIABLEINDEX_3);                break;
            case 100:  this.da("isub");                                                                     break;
            case 124:  this.da("iushr");                                                                    break;
            case 130:  this.da("ixor");                                                                     break;
            case 168:  this.da("jsr",             OperandKind.BRANCHOFFSET2);                               break;
            case 201:  this.da("jsr_w",           OperandKind.BRANCHOFFSET4);                               break;
            case 138:  this.da("l2d");                                                                      break;
            case 137:  this.da("l2f");                                                                      break;
            case 136:  this.da("l2i");                                                                      break;
            case 97:   this.da("ladd");                                                                     break;
            case 47:   this.da("laload");                                                                   break;
            case 127:  this.da("land");                                                                     break;
            case 80:   this.da("lastore");                                                                  break;
            case 148:  this.da("lcmp");                                                                     break;
            case 9:    this.da("lconst_0");                                                                 break;
            case 10:   this.da("lconst_1");                                                                 break;
            case 18:   this.da("ldc",             OperandKind.INTFLOATCLASSSTRING1);                        break;
            case 19:   this.da("ldc_w",           OperandKind.INTFLOATCLASSSTRING2);                        break;
            case 20:   this.da("ldc2_w",          OperandKind.LONGDOUBLE2);                                 break;
            case 109:  this.da("ldiv");                                                                     break;
            case 22:   this.da("lload",           OperandKind.LOCALVARIABLEINDEX1);                         break;
            case 30:   this.da("lload_0",         OperandKind.IMPLICITLOCALVARIABLEINDEX_0);                break;
            case 31:   this.da("lload_1",         OperandKind.IMPLICITLOCALVARIABLEINDEX_1);                break;
            case 32:   this.da("lload_2",         OperandKind.IMPLICITLOCALVARIABLEINDEX_2);                break;
            case 33:   this.da("lload_3",         OperandKind.IMPLICITLOCALVARIABLEINDEX_3);                break;
            case 105:  this.da("lmul");                                                                     break;
            case 117:  this.da("lneg");                                                                     break;
            case 171:  this.da("lookupswitch",    OperandKind.LOOKUPSWITCH);                                break;
            case 129:  this.da("lor");                                                                      break;
            case 113:  this.da("lrem");                                                                     break;
            case 173:  this.da("lreturn");                                                                  break;
            case 121:  this.da("lshl");                                                                     break;
            case 123:  this.da("lshr");                                                                     break;
            case 55:   this.da("lstore",          OperandKind.LOCALVARIABLEINDEX1);                         break;
            case 63:   this.da("lstore_0",        OperandKind.IMPLICITLOCALVARIABLEINDEX_0);                break;
            case 64:   this.da("lstore_1",        OperandKind.IMPLICITLOCALVARIABLEINDEX_1);                break;
            case 65:   this.da("lstore_2",        OperandKind.IMPLICITLOCALVARIABLEINDEX_2);                break;
            case 66:   this.da("lstore_3",        OperandKind.IMPLICITLOCALVARIABLEINDEX_3);                break;
            case 101:  this.da("lsub");                                                                     break;
            case 125:  this.da("lushr");                                                                    break;
            case 131:  this.da("lxor");                                                                     break;
            case 194:  this.da("monitorenter");                                                             break;
            case 195:  this.da("monitorexit");                                                              break;
            case 197:  this.da("multianewarray",  OperandKind.CLASS2, OperandKind.UNSIGNEDBYTE);            break;
            case 187:  this.da("new",             OperandKind.CLASS2);                                      break;
            case 188:  this.da("newarray",        OperandKind.ATYPE);                                       break;
            case 0:    this.da("nop");                                                                      break;
            case 87:   this.da("pop");                                                                      break;
            case 88:   this.da("pop2");                                                                     break;
            case 181:  this.da("putfield",        OperandKind.FIELDREF2);                                   break;
            case 179:  this.da("putstatic",       OperandKind.FIELDREF2);                                   break;
            case 169:  this.da("ret",             OperandKind.LOCALVARIABLEINDEX1);                         break;
            case 177:  this.da("return");                                                                   break;
            case 53:   this.da("saload");                                                                   break;
            case 86:   this.da("sastore");                                                                  break;
            case 17:   this.da("sipush",          OperandKind.SIGNEDSHORT);                                 break;
            case 95:   this.da("swap");                                                                     break;
            case 170:  this.da("tableswitch",     OperandKind.TABLESWITCH);                                 break;

            case 196:
                {
                    int subopcode = 0xff & this.dis.readByte();
                    switch (subopcode) {

                    case 21:  this.da("wide iload",  OperandKind.LOCALVARIABLEINDEX2);                          break;
                    case 23:  this.da("wide fload",  OperandKind.LOCALVARIABLEINDEX2);                          break;
                    case 25:  this.da("wide aload",  OperandKind.LOCALVARIABLEINDEX2);                          break;
                    case 22:  this.da("wide lload",  OperandKind.LOCALVARIABLEINDEX2);                          break;
                    case 24:  this.da("wide dload",  OperandKind.LOCALVARIABLEINDEX2);                          break;
                    case 54:  this.da("wide istore", OperandKind.LOCALVARIABLEINDEX2);                          break;
                    case 56:  this.da("wide fstore", OperandKind.LOCALVARIABLEINDEX2);                          break;
                    case 58:  this.da("wide astore", OperandKind.LOCALVARIABLEINDEX2);                          break;
                    case 55:  this.da("wide lstore", OperandKind.LOCALVARIABLEINDEX2);                          break;
                    case 57:  this.da("wide dstore", OperandKind.LOCALVARIABLEINDEX2);                          break;
                    case 169: this.da("wide ret",    OperandKind.LOCALVARIABLEINDEX2);                          break;
                    case 132: this.da("wide iinc",   OperandKind.LOCALVARIABLEINDEX2, OperandKind.SIGNEDSHORT); break;

                    default:
                        this.lines.put(
                            this.instructionOffset,
                            "Invalid opcode " + subopcode + " after opcode WIDE"
                        );
                        break;
                    }
                }
                break;

            default:
                this.lines.put(this.instructionOffset, "??? (invalid opcode \"" + opcode + "\")");
                break;
            }
        }

        // Format and print the disassembly lines.
        String indentation = "        ";
        for (Entry<Integer, String> e : this.lines.entrySet()) {
            final int    instructionOffset = e.getKey();
            final String text              = e.getValue();

            // Print ends of TRY bodies.
            for (Iterator<Entry<Integer, SortedMap<Integer, List<ExceptionTableEntry>>>> it = (
                tryEnds.entrySet().iterator()
            ); it.hasNext();) {
                Entry<Integer, SortedMap<Integer, List<ExceptionTableEntry>>> e2 = it.next();

                int endPc = e2.getKey();
                if (endPc > instructionOffset) break;

                SortedMap<Integer, List<ExceptionTableEntry>> startPc2Ete = e2.getValue();
                for (List<ExceptionTableEntry> etes : startPc2Ete.values()) {

                    if (endPc < instructionOffset) {
                        pw.println(
                            "*** Error: "
                            + "Exception table entry ends at invalid code array index "
                            + endPc
                            + " (current instruction offset is "
                            + instructionOffset
                            + ")"
                        );
                    }
                    indentation = indentation.substring(4);
                    pw.print(indentation + "} catch (");
                    for (Iterator<ExceptionTableEntry> it2 = etes.iterator();;) {
                        ExceptionTableEntry ete = it2.next();
                        ConstantClassInfo   ct  = ete.catchType;
                        pw.print(
                            (ct == null ? "[all exceptions] => " : ct + " => ")
                            + this.branchTarget(ete.handlerPc)
                        );
                        if (!it2.hasNext()) break;
                        pw.print(", ");
                    }
                    pw.println(")");
                }
                it.remove();
            }

            // Print instruction offsets only for branch targets.
            {
                String label = this.branchTargets.get(instructionOffset);
                if (label != null) pw.println(label);
            }

            // Print beginnings of TRY bodies.
            for (Iterator<Entry<Integer, Set<Integer>>> it = tryStarts.entrySet().iterator(); it.hasNext();) {
                Entry<Integer, Set<Integer>> sc      = it.next();
                Integer                      startPc = sc.getKey();
                if (startPc > instructionOffset) break;

                for (int i = sc.getValue().size(); i > 0; i--) {
                    if (startPc < instructionOffset) {
                        pw.println(
                            "*** Error: "
                            + "Exception table entry starts at invalid code array index "
                            + startPc
                            + " (current instruction offset is "
                            + instructionOffset
                            + ")"
                        );
                    }
                    pw.println(indentation + "try {");
                    indentation += "    ";
                }
                it.remove();
            }

            // Print source line and/or line number.
            PRINT_SOURCE_LINE: {
                if (this.lineNumberTableAttribute == null) break PRINT_SOURCE_LINE;

                int lineNumber = this.findLineNumber(instructionOffset);
                if (lineNumber == -1) break PRINT_SOURCE_LINE;

                String sourceLine = this.sourceLines.get(lineNumber);
                if (sourceLine == null && this.d.hideLines) break PRINT_SOURCE_LINE;

                StringBuilder sb = new StringBuilder(indentation);
                if (sourceLine == null) {
                    sb.append("// Line ").append(lineNumber);
                } else {
                    sb.append("// ");
                    if (sb.length() < 40) {
                        char[] spc = new char[40 - sb.length()];
                        Arrays.fill(spc, ' ');
                        sb.append(spc);
                    }
                    if (!this.d.hideLines) {
                        sb.append("Line ").append(lineNumber).append(": ");
                    }
                    sb.append(sourceLine);
                }
                pw.println(sb.toString());
            }

            pw.println(indentation + text);
        }
    }

    private void
    da(String mnemonic, OperandKind... operandKinds) throws IOException {

        if (operandKinds.length == 0) {
            this.lines.put(this.instructionOffset, mnemonic);
            return;
        }

        Formatter f = new Formatter();
        f.format("%-15s", mnemonic);

        for (int i = 0; i < operandKinds.length; ++i) {
            f.format(" %s", operandKinds[i].accept(this.readOperand));
        }

        this.lines.put(this.instructionOffset, f.toString());
    }

    /**
     * A visitor that reads an instruction operand from the {@link #dis} and transforms it into a human-readable form,
     * suitable for a disassembly listing.
     */
    private final OperandKind.Visitor<String, IOException>
    readOperand = new OperandKind.Visitor<String, IOException>() {

        @Override public String
        visitIntFloatClassString1(OperandKind operandType) throws IOException {

            short  index = (short) (0xff & BytecodeDisassembler.this.dis.readByte());
            String t     = BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                index,
                ConstantClassOrFloatOrIntegerOrStringInfo.class
            ).toString();

            if (BytecodeDisassembler.this.d.verbose) t += " (" + (0xffff & index) + ")";

            return t;
        }

        @Override public String
        visitIntFloatClassString2(OperandKind operandType) throws IOException {

            short  index = BytecodeDisassembler.this.dis.readShort();
            String t     = BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                index,
                ConstantClassOrFloatOrIntegerOrStringInfo.class
            ).toString();

            if (BytecodeDisassembler.this.d.verbose) t += " (" + (0xffff & index) + ")";

            return t;
        }

        @Override public String
        visitLongDouble2(OperandKind operandType) throws IOException {

            short  index = BytecodeDisassembler.this.dis.readShort();
            String t     = BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                index,
                ConstantDoubleOrLongInfo.class
            ).toString();

            if (BytecodeDisassembler.this.d.verbose) t += " (" + (0xffff & index) + ")";

            return t;
        }

        @Override public String
        visitFieldref2(OperandKind operandType) throws IOException {

            short                index = BytecodeDisassembler.this.dis.readShort();
            ConstantFieldrefInfo fr    = BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                index,
                ConstantFieldrefInfo.class
            );

            String t = (
                BytecodeDisassembler.this.d.decodeFieldDescriptor(fr.nameAndType.descriptor.bytes)
                + " "
                + fr.clasS
                + '.'
                + fr.nameAndType.name.bytes
            );

            if (BytecodeDisassembler.this.d.verbose) t += " (" + (0xffff & index) + ")";

            return t;
        }

        @Override public String
        visitMethodref2(OperandKind operandType) throws IOException {

            short                 index = BytecodeDisassembler.this.dis.readShort();
            ConstantMethodrefInfo mr    = BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                index,
                ConstantMethodrefInfo.class
            );

            String t = BytecodeDisassembler.this.d.decodeMethodDescriptor(mr.nameAndType.descriptor.bytes).toString(
                mr.clasS.toString(),
                mr.nameAndType.name.bytes
            );

            if (BytecodeDisassembler.this.d.verbose) t += " (" + (0xffff & index) + ")";

            return t;
        }

        @Override public String
        visitInterfaceMethodref2(OperandKind operandType) throws IOException {

            short index = BytecodeDisassembler.this.dis.readShort();
            BytecodeDisassembler.this.dis.readByte();
            BytecodeDisassembler.this.dis.readByte();

            ConstantInterfaceMethodrefInfo imr = BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                index,
                ConstantInterfaceMethodrefInfo.class
            );

            String t = BytecodeDisassembler.this.d.decodeMethodDescriptor(imr.nameAndType.descriptor.bytes).toString(
                imr.clasS.toString(),
                imr.nameAndType.name.bytes
            );

            if (BytecodeDisassembler.this.d.verbose) t += " (" + (0xffff & index) + ")";

            return t;
        }

        @Override public String
        visitInterfaceMethodrefOrMethodref2(OperandKind operandType) throws IOException {

            short index = BytecodeDisassembler.this.dis.readShort();

            ConstantInterfaceMethodrefOrMethodrefInfo
            imromr = BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                index,
                ConstantInterfaceMethodrefOrMethodrefInfo.class
            );

            String t = BytecodeDisassembler.this.d.decodeMethodDescriptor(imromr.nameAndType.descriptor.bytes).toString(
                imromr.clasS.toString(),
                imromr.nameAndType.name.bytes
            );

            if (BytecodeDisassembler.this.d.verbose) t += " (" + (0xffff & index) + ")";

            return t;
        }

        @Override public String
        visitClass2(OperandKind operandType) throws IOException {

            short index = BytecodeDisassembler.this.dis.readShort();

            String t = BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                index,
                ConstantClassInfo.class
            ).toString();

            if (BytecodeDisassembler.this.d.verbose) t += " (" + (0xffff & index) + ")";

            return t;
        }

        @Override public String
        visitLocalVariableIndex1(OperandKind operandType) throws IOException {

            short index = (short) (0xff & BytecodeDisassembler.this.dis.readByte());

            // For an initial assignment (e.g. 'istore 7'), the local variable is only visible AFTER this instruction.
            return BytecodeDisassembler.this.d.getLocalVariable(
                index,
                BytecodeDisassembler.this.instructionOffset + 2,
                BytecodeDisassembler.this.method
            ).toString();
        }

        @Override public String
        visitLocalVariableIndex2(OperandKind operandType) throws IOException {

            short index = BytecodeDisassembler.this.dis.readShort();

            // For an initial assignment (e.g. 'wide istore 300'), the local variable is only visible AFTER this
            // instruction.
            return BytecodeDisassembler.this.d.getLocalVariable(
                index,
                BytecodeDisassembler.this.instructionOffset + 4,
                BytecodeDisassembler.this.method
            ).toString();
        }

        @Override public String
        visitImplicitLocalVariableIndex(OperandKind operandType, int index) {

            return BytecodeDisassembler.this.d.getLocalVariable(
                (short) index,
                BytecodeDisassembler.this.instructionOffset + 1,
                BytecodeDisassembler.this.method
            ).toString();
        }

        @Override public String
        visitBranchOffset2(OperandKind operandType) throws IOException {

            return BytecodeDisassembler.this.branchTarget(
                BytecodeDisassembler.this.instructionOffset + BytecodeDisassembler.this.dis.readShort()
            );
        }

        @Override public String
        visitBranchOffset4(OperandKind operandType) throws IOException {

            return BytecodeDisassembler.this.branchTarget(
                BytecodeDisassembler.this.instructionOffset + BytecodeDisassembler.this.dis.readInt()
            );
        }

        @Override public String
        visitSignedByte(OperandKind operandType) throws IOException {
            return Integer.toString(BytecodeDisassembler.this.dis.readByte());
        }

        @Override public String
        visitUnsignedByte(OperandKind operandType) throws IOException {
            return Integer.toString(0xff & BytecodeDisassembler.this.dis.readByte());
        }

        @Override public String
        visitSignedShort(OperandKind operandType) throws IOException {
            return Integer.toString(BytecodeDisassembler.this.dis.readShort());
        }

        @Override public String
        visitAtype(OperandKind operandType) throws IOException {

            byte b = BytecodeDisassembler.this.dis.readByte();

            return (
                b ==  4 ? "BOOLEAN" :
                b ==  5 ? "CHAR"    :
                b ==  6 ? "FLOAT"   :
                b ==  7 ? "DOUBLE"  :
                b ==  8 ? "BYTE"    :
                b ==  9 ? "SHORT"   :
                b == 10 ? "INT"     :
                b == 11 ? "LONG"    :
                Integer.toString(0xff & b)
            );
        }

        @Override public String
        visitTableswitch(OperandKind operandType) throws IOException {
            int npads = 3 - (BytecodeDisassembler.this.instructionOffset % 4);
            for (int i = 0; i < npads; ++i) {
                byte padByte = BytecodeDisassembler.this.dis.readByte();
                if (padByte != 0) {
                    throw new RuntimeException(
                        "'tableswitch' pad byte #"
                        + i
                        + " is not zero, but "
                        + (0xff & padByte)
                    );
                }
            }

            StringBuilder sb = new StringBuilder("default => ");
            sb.append(
                BytecodeDisassembler.this.branchTarget(
                    BytecodeDisassembler.this.instructionOffset + BytecodeDisassembler.this.dis.readInt()
                )
            );

            int low  = BytecodeDisassembler.this.dis.readInt();
            int high = BytecodeDisassembler.this.dis.readInt();
            for (int i = low; i <= high; ++i) {
                sb.append(", ").append(i).append(" => ");
                sb.append(
                    BytecodeDisassembler.this.branchTarget(
                        BytecodeDisassembler.this.instructionOffset + BytecodeDisassembler.this.dis.readInt()
                    )
                );
            }
            return sb.toString();
        }

        @Override public String
        visitLookupswitch(OperandKind operandType) throws IOException {

            int npads = 3 - (BytecodeDisassembler.this.instructionOffset % 4);
            for (int i = 0; i < npads; ++i) {
                byte padByte = BytecodeDisassembler.this.dis.readByte();
                if (padByte != (byte) 0) {
                    throw new RuntimeException(
                        "'lookupswitch' pad byte #"
                        + i
                        + " is not zero, but "
                        + (0xff & padByte)
                    );
                }
            }

            StringBuilder sb = new StringBuilder("default => ");
            sb.append(
                BytecodeDisassembler.this.branchTarget(
                    BytecodeDisassembler.this.instructionOffset + BytecodeDisassembler.this.dis.readInt()
                )
            );

            int npairs = BytecodeDisassembler.this.dis.readInt();
            for (int i = 0; i < npairs; ++i) {

                int match  = BytecodeDisassembler.this.dis.readInt();
                int offset = BytecodeDisassembler.this.instructionOffset + BytecodeDisassembler.this.dis.readInt();

                sb.append(", ").append(match).append(" => ").append(BytecodeDisassembler.this.branchTarget(offset));
            }
            return sb.toString();
        }

        @Override public String
        visitDynamicCallsite(OperandKind operandType) throws IOException {

            short index = BytecodeDisassembler.this.dis.readShort();

            if (BytecodeDisassembler.this.dis.readByte() != 0 || BytecodeDisassembler.this.dis.readByte() != 0) {
                throw new RuntimeException("'invokevirtual' pad byte is not zero");
            }

            BootstrapMethod bm = BytecodeDisassembler.this.method.getBootstrapMethodsAttribute().bootstrapMethods.get(
                BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                    index,
                    ConstantInvokeDynamicInfo.class
                ).bootstrapMethodAttrIndex
            );

            return bm + "." + BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                index,
                ConstantInvokeDynamicInfo.class
            ).nameAndType;
        }
    };

    /**
     * @return -1 iff the offset is not associated with a line number
     */
    private int
    findLineNumber(int offset) {

        LineNumberTableAttribute lnta = this.lineNumberTableAttribute;
        if (lnta == null) return -1;

        for (LineNumberTableEntry lnte : lnta.entries) {
            if (lnte.startPc == offset) return lnte.lineNumber;
        }
        return -1;
    }

    private String
    branchTarget(int offset) {

        String label = this.branchTargets.get(offset);

        if (label == null) {
            label = this.d.symbolicLabels ? "L" + (1 + this.branchTargets.size()) : "#" + offset;
            this.branchTargets.put(offset, label);
        }

        return label;
    }

    /**
     * An {@link InputStream} that counts how many bytes have been read so far.
     */
    @NotNullByDefault(false) private static
    class CountingInputStream extends FilterInputStream {

        CountingInputStream(InputStream is) {
            super(is);
        }

        @Override public int
        read() throws IOException {
            int res = super.read();
            if (res != -1) ++this.count;
            return res;
        }

        @Override public int
        read(byte[] b, int off, int len) throws IOException {
            int res = super.read(b, off, len);
            if (res != -1) this.count += res;
            return res;
        }

        public long getCount() { return this.count; }

        private long count;
    }
}
