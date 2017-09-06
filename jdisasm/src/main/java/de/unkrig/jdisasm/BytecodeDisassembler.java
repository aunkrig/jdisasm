
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
import de.unkrig.jdisasm.ConstantPool.ConstantDoubleOrLongOrStringInfo;
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
    private
    interface Operand {

        /**
         * @return This operand disassembled
         */
        String
        disassemble(BytecodeDisassembler bd) throws IOException;
    }

    private static final Operand INTFLOATCLASSSTRING1 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            short  index = (short) (0xff & bd.dis.readByte());
            String t     = bd.method.getClassFile().constantPool.get(
                index,
                ConstantClassOrFloatOrIntegerOrStringInfo.class
            ).toString();
            if (Character.isJavaIdentifierStart(t.charAt(0))) t = bd.d.beautify(t);
            if (bd.verbose) t += " (" + (0xffff & index) + ")";
            return t;
        }
    };

    private static final Operand INTFLOATCLASSSTRING2 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            short  index = bd.dis.readShort();
            String t     = bd.method.getClassFile().constantPool.get(
                index,
                ConstantClassOrFloatOrIntegerOrStringInfo.class
            ).toString();
            if (Character.isJavaIdentifierStart(t.charAt(0))) t = bd.d.beautify(t);
            if (bd.verbose) t += " (" + (0xffff & index) + ")";
            return t;
        }
    };

    private static final Operand LONGDOUBLE2 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            short  index = bd.dis.readShort();
            String t     = bd.method.getClassFile().constantPool.get(
                index,
                ConstantDoubleOrLongOrStringInfo.class
            ).toString();
            if (bd.verbose) t += " (" + (0xffff & index) + ")";
            return t;
        }
    };

    private static final Operand FIELDREF2 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            short index = bd.dis.readShort();

            ConstantFieldrefInfo fr = bd.method.getClassFile().constantPool.get(
                index,
                ConstantFieldrefInfo.class
            );

            String t = (
                bd.d.beautify(bd.d.decodeFieldDescriptor(fr.nameAndType.descriptor.bytes).toString())
                + ' '
                + bd.d.beautify(fr.clasS.name)
                + '.'
                + fr.nameAndType.name.bytes
            );
            if (bd.verbose) t += " (" + (0xffff & index) + ")";
            return t;
        }
    };

    private static final Operand METHODREF2 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {

            short index = bd.dis.readShort();

            ConstantMethodrefInfo mr = bd.method.getClassFile().constantPool.get(
                index,
                ConstantMethodrefInfo.class
            );

            String t = bd.d.beautify(
                bd.d.decodeMethodDescriptor(mr.nameAndType.descriptor.bytes).toString(
                    mr.clasS.name,
                    mr.nameAndType.name.bytes
                )
            );
            if (bd.verbose) t += " (" + (0xffff & index) + ")";
            return t;
        }
    };

    private static final Operand INTERFACEMETHODREF2 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {

            short index = bd.dis.readShort();
            bd.dis.readByte();
            bd.dis.readByte();

            ConstantInterfaceMethodrefInfo imr = bd.method.getClassFile().constantPool.get(
                index,
                ConstantInterfaceMethodrefInfo.class
            );

            String t = bd.d.beautify(
                bd
                .d
                .decodeMethodDescriptor(imr.nameAndType.descriptor.bytes)
                .toString(imr.clasS.name, imr.nameAndType.name.bytes)
            );
            if (bd.verbose) t += " (" + (0xffff & index) + ")";
            return t;
        }
    };

    private static final Operand INTERFACEMETHODREFORMETHODREF2 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {

            short index = bd.dis.readShort();

            ConstantInterfaceMethodrefOrMethodrefInfo
            imromr = bd.method.getClassFile().constantPool.get(
                index,
                ConstantInterfaceMethodrefOrMethodrefInfo.class
            );

            String t = bd.d.beautify(
                bd
                .d
                .decodeMethodDescriptor(imromr.nameAndType.descriptor.bytes)
                .toString(imromr.clasS.name, imromr.nameAndType.name.bytes)
            );
            if (bd.verbose) t += " (" + (0xffff & index) + ")";
            return t;
        }
    };

    private static final Operand CLASS2 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {

            short index = bd.dis.readShort();

            String name = bd.method.getClassFile().constantPool.get(
                index,
                ConstantClassInfo.class
            ).name;

            String t = bd.d.beautify(
                name.startsWith("[")
                ? bd.d.decodeFieldDescriptor(name).toString()
                : name.replace('/', '.')
            );
            if (bd.verbose) t += " (" + (0xffff & index) + ")";
            return t;
        }
    };

    private static final Operand LOCALVARIABLEINDEX1 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {

            short index = (short) (0xff & bd.dis.readByte());

            // For an initial assignment (e.g. 'istore 7'), the local variable is only visible AFTER this instruction.
            return bd.d.beautify(bd.d.getLocalVariable(index, bd.instructionOffset + 2, bd.method).toString());
        }
    };

    private static final Operand LOCALVARIABLEINDEX2 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {

            short index = bd.dis.readShort();

            // For an initial assignment (e.g. 'wide istore 300'), the local variable is only visible AFTER this
            // instruction.
            return bd.d.beautify(bd.d.getLocalVariable(index, bd.instructionOffset + 4, bd.method).toString());
        }
    };

    private static Operand
    implicitLocalVariableIndexOperand(final int lvi) {
        return new Operand() {

            @Override public String
            disassemble(BytecodeDisassembler bd) {
                return bd.d.beautify(
                    bd.d.getLocalVariable((short) lvi, bd.instructionOffset + 1, bd.method)
                    .toString()
                );
            }
        };
    }

    // SUPPRESS CHECKSTYLE LineLength:4
    private static final Operand IMPLICITLOCALVARIABLEINDEX_0 = BytecodeDisassembler.implicitLocalVariableIndexOperand(0);
    private static final Operand IMPLICITLOCALVARIABLEINDEX_1 = BytecodeDisassembler.implicitLocalVariableIndexOperand(1);
    private static final Operand IMPLICITLOCALVARIABLEINDEX_2 = BytecodeDisassembler.implicitLocalVariableIndexOperand(2);
    private static final Operand IMPLICITLOCALVARIABLEINDEX_3 = BytecodeDisassembler.implicitLocalVariableIndexOperand(3);


    private static final Operand BRANCHOFFSET2 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            return bd.branchTarget(bd.instructionOffset + bd.dis.readShort());
        }
    };

    private static final Operand BRANCHOFFSET4 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            return bd.branchTarget(bd.instructionOffset + bd.dis.readInt());
        }
    };

    private static final Operand SIGNEDBYTE = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            return Integer.toString(bd.dis.readByte());
        }
    };

    private static final Operand UNSIGNEDBYTE = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            return Integer.toString(0xff & bd.dis.readByte());
        }
    };

    private static final Operand ATYPE = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {

            byte b = bd.dis.readByte();

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
    };

    private static final Operand SIGNEDSHORT = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            return Integer.toString(bd.dis.readShort());
        }
    };

    private static final Operand TABLESWITCH = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            int npads = 3 - (bd.instructionOffset % 4);
            for (int i = 0; i < npads; ++i) {
                byte padByte = bd.dis.readByte();
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
            sb.append(bd.branchTarget(bd.instructionOffset + bd.dis.readInt()));

            int low  = bd.dis.readInt();
            int high = bd.dis.readInt();
            for (int i = low; i <= high; ++i) {
                sb.append(", ").append(i).append(" => ");
                sb.append(bd.branchTarget(bd.instructionOffset + bd.dis.readInt()));
            }
            return sb.toString();
        }
    };

    private static final Operand LOOKUPSWITCH = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            int npads = 3 - (bd.instructionOffset % 4);
            for (int i = 0; i < npads; ++i) {
                byte padByte = bd.dis.readByte();
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
            sb.append(bd.branchTarget(bd.instructionOffset + bd.dis.readInt()));

            int npairs = bd.dis.readInt();
            for (int i = 0; i < npairs; ++i) {
                int match  = bd.dis.readInt();
                int offset = bd.instructionOffset + bd.dis.readInt();
                sb.append(", ").append(match).append(" => ").append(bd.branchTarget(offset));
            }
            return sb.toString();
        }
    };

    private static final Operand DYNAMICCALLSITE = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            short index = bd.dis.readShort();
            if (bd.dis.readByte() != 0 || bd.dis.readByte() != 0) {
                throw new RuntimeException("'invokevirtual' pad byte is not zero");
            }

            BootstrapMethod bm = bd.method.getBootstrapMethodsAttribute().bootstrapMethods.get(
                bd.method.getClassFile().constantPool.get(
                    index,
                    ConstantInvokeDynamicInfo.class
                ).bootstrapMethodAttrIndex
            );

            return bm + "." + bd.method.getClassFile().constantPool.get(
                index,
                ConstantInvokeDynamicInfo.class
            ).nameAndType;
        }
    };


    private final CountingInputStream                cis;
    private final DataInputStream                    dis;
    private final List<ExceptionTableEntry>          exceptionTable;
    @Nullable private final LineNumberTableAttribute lineNumberTableAttribute;
    private final Map<Integer, String>               sourceLines;
    private final Method                             method;
    private final boolean                            symbolicLabels;
    private final boolean                            verbose;
    private final boolean                            hideLines;
    private final Disassembler                       d;

    private int                                             instructionOffset;
    private final Map<Integer /*offset*/, String /*label*/> branchTargets = new HashMap<Integer, String>();
    private final SortedMap<Integer, String>                lines         = new TreeMap<Integer, String>();

    public
    BytecodeDisassembler(
        InputStream                        is,
        List<ExceptionTableEntry>          exceptionTable,
        @Nullable LineNumberTableAttribute lineNumberTableAttribute,
        Map<Integer, String>               sourceLines,
        Method                             method,
        boolean                            symbolicLabels,
        boolean                            verbose,
        boolean                            hideLines,
        Disassembler                       d
    ) {
        this.cis                      = new CountingInputStream(is);
        this.dis                      = new DataInputStream(this.cis);
        this.exceptionTable           = exceptionTable;
        this.lineNumberTableAttribute = lineNumberTableAttribute;
        this.sourceLines              = sourceLines;
        this.method                   = method;
        this.symbolicLabels           = symbolicLabels;
        this.verbose                  = verbose;
        this.hideLines                = hideLines;
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

        {

            for (;;) {
                this.instructionOffset = (int) this.cis.getCount();

                int opcode = this.dis.read();
                if (opcode == -1) break;

                switch (opcode) {

                // SUPPRESS CHECKSTYLE LineLength:201
                case 50:   this.da("aaload");                                                                                     break;
                case 83:   this.da("aastore");                                                                                    break;
                case 1:    this.da("aconst_null");                                                                                break;
                case 25:   this.da("aload",           BytecodeDisassembler.LOCALVARIABLEINDEX1);                                  break;
                case 42:   this.da("aload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_0);                break;
                case 43:   this.da("aload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_1);                break;
                case 44:   this.da("aload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_2);                break;
                case 45:   this.da("aload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_3);                break;
                case 189:  this.da("anewarray",       BytecodeDisassembler.CLASS2);                                               break;
                case 176:  this.da("areturn");                                                                                    break;
                case 190:  this.da("arraylength");                                                                                break;
                case 58:   this.da("astore",          BytecodeDisassembler.LOCALVARIABLEINDEX1);                                  break;
                case 75:   this.da("astore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_0);                break;
                case 76:   this.da("astore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_1);                break;
                case 77:   this.da("astore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_2);                break;
                case 78:   this.da("astore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_3);                break;
                case 191:  this.da("athrow");                                                                                     break;
                case 51:   this.da("baload");                                                                                     break;
                case 84:   this.da("bastore");                                                                                    break;
                case 16:   this.da("bipush",          BytecodeDisassembler.SIGNEDBYTE);                                           break;
                case 52:   this.da("caload");                                                                                     break;
                case 85:   this.da("castore");                                                                                    break;
                case 192:  this.da("checkcast",       BytecodeDisassembler.CLASS2);                                               break;
                case 144:  this.da("d2f");                                                                                        break;
                case 142:  this.da("d2i");                                                                                        break;
                case 143:  this.da("d2l");                                                                                        break;
                case 99:   this.da("dadd");                                                                                       break;
                case 49:   this.da("daload");                                                                                     break;
                case 82:   this.da("dastore");                                                                                    break;
                case 152:  this.da("dcmpg");                                                                                      break;
                case 151:  this.da("dcmpl");                                                                                      break;
                case 14:   this.da("dconst_0");                                                                                   break;
                case 15:   this.da("dconst_1");                                                                                   break;
                case 111:  this.da("ddiv");                                                                                       break;
                case 24:   this.da("dload",           BytecodeDisassembler.LOCALVARIABLEINDEX1);                                  break;
                case 38:   this.da("dload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_0);                         break;
                case 39:   this.da("dload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_1);                         break;
                case 40:   this.da("dload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_2);                         break;
                case 41:   this.da("dload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_3);                         break;
                case 107:  this.da("dmul");                                                                                       break;
                case 119:  this.da("dneg");                                                                                       break;
                case 115:  this.da("drem");                                                                                       break;
                case 175:  this.da("dreturn");                                                                                    break;
                case 57:   this.da("dstore",          BytecodeDisassembler.LOCALVARIABLEINDEX1);                                  break;
                case 71:   this.da("dstore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_0);                         break;
                case 72:   this.da("dstore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_1);                         break;
                case 73:   this.da("dstore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_2);                         break;
                case 74:   this.da("dstore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_3);                         break;
                case 103:  this.da("dsub");                                                                                       break;
                case 89:   this.da("dup");                                                                                        break;
                case 90:   this.da("dup_x1");                                                                                     break;
                case 91:   this.da("dup_x2");                                                                                     break;
                case 92:   this.da("dup2");                                                                                       break;
                case 93:   this.da("dup2_x1");                                                                                    break;
                case 94:   this.da("dup2_x2");                                                                                    break;
                case 141:  this.da("f2d");                                                                                        break;
                case 139:  this.da("f2i");                                                                                        break;
                case 140:  this.da("f2l");                                                                                        break;
                case 98:   this.da("fadd");                                                                                       break;
                case 48:   this.da("faload");                                                                                     break;
                case 81:   this.da("fastore");                                                                                    break;
                case 150:  this.da("fcmpg");                                                                                      break;
                case 149:  this.da("fcmpl");                                                                                      break;
                case 11:   this.da("fconst_0");                                                                                   break;
                case 12:   this.da("fconst_1");                                                                                   break;
                case 13:   this.da("fconst_2");                                                                                   break;
                case 110:  this.da("fdiv");                                                                                       break;
                case 23:   this.da("fload",           BytecodeDisassembler.LOCALVARIABLEINDEX1);                                  break;
                case 34:   this.da("fload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_0);                         break;
                case 35:   this.da("fload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_1);                         break;
                case 36:   this.da("fload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_2);                         break;
                case 37:   this.da("fload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_3);                         break;
                case 106:  this.da("fmul");                                                                                       break;
                case 118:  this.da("fneg");                                                                                       break;
                case 114:  this.da("frem");                                                                                       break;
                case 174:  this.da("freturn");                                                                                    break;
                case 56:   this.da("fstore",          BytecodeDisassembler.LOCALVARIABLEINDEX1);                                  break;
                case 67:   this.da("fstore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_0);                         break;
                case 68:   this.da("fstore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_1);                         break;
                case 69:   this.da("fstore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_2);                         break;
                case 70:   this.da("fstore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_3);                         break;
                case 102:  this.da("fsub");                                                                                       break;
                case 180:  this.da("getfield",        BytecodeDisassembler.FIELDREF2);                                            break;
                case 178:  this.da("getstatic",       BytecodeDisassembler.FIELDREF2);                                            break;
                case 167:  this.da("goto",            BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 200:  this.da("goto_w",          BytecodeDisassembler.BRANCHOFFSET4);                                        break;
                case 145:  this.da("i2b");                                                                                        break;
                case 146:  this.da("i2c");                                                                                        break;
                case 135:  this.da("i2d");                                                                                        break;
                case 134:  this.da("i2f");                                                                                        break;
                case 133:  this.da("i2l");                                                                                        break;
                case 147:  this.da("i2s");                                                                                        break;
                case 96:   this.da("iadd");                                                                                       break;
                case 46:   this.da("iaload");                                                                                     break;
                case 126:  this.da("iand");                                                                                       break;
                case 79:   this.da("iastore");                                                                                    break;
                case 2:    this.da("iconst_m1");                                                                                  break;
                case 3:    this.da("iconst_0");                                                                                   break;
                case 4:    this.da("iconst_1");                                                                                   break;
                case 5:    this.da("iconst_2");                                                                                   break;
                case 6:    this.da("iconst_3");                                                                                   break;
                case 7:    this.da("iconst_4");                                                                                   break;
                case 8:    this.da("iconst_5");                                                                                   break;
                case 108:  this.da("idiv");                                                                                       break;
                case 165:  this.da("if_acmpeq",       BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 166:  this.da("if_acmpne",       BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 159:  this.da("if_icmpeq",       BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 160:  this.da("if_icmpne",       BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 161:  this.da("if_icmplt",       BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 162:  this.da("if_icmpge",       BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 163:  this.da("if_icmpgt",       BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 164:  this.da("if_icmple",       BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 153:  this.da("ifeq",            BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 154:  this.da("ifne",            BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 155:  this.da("iflt",            BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 156:  this.da("ifge",            BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 157:  this.da("ifgt",            BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 158:  this.da("ifle",            BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 199:  this.da("ifnonnull",       BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 198:  this.da("ifnull",          BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 132:  this.da("iinc",            BytecodeDisassembler.LOCALVARIABLEINDEX1, BytecodeDisassembler.SIGNEDBYTE); break;
                case 21:   this.da("iload",           BytecodeDisassembler.LOCALVARIABLEINDEX1);                                  break;
                case 26:   this.da("iload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_0);                         break;
                case 27:   this.da("iload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_1);                         break;
                case 28:   this.da("iload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_2);                         break;
                case 29:   this.da("iload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_3);                         break;
                case 104:  this.da("imul");                                                                                       break;
                case 116:  this.da("ineg");                                                                                       break;
                case 193:  this.da("instanceof",      BytecodeDisassembler.CLASS2);                                               break;
                case 186:  this.da("invokedynamic",   BytecodeDisassembler.DYNAMICCALLSITE);                                      break;
                case 185:  this.da("invokeinterface", BytecodeDisassembler.INTERFACEMETHODREF2);                                  break;
                case 183:  this.da("invokespecial",   BytecodeDisassembler.INTERFACEMETHODREFORMETHODREF2);                       break;
                case 184:  this.da("invokestatic",    BytecodeDisassembler.INTERFACEMETHODREFORMETHODREF2);                       break;
                case 182:  this.da("invokevirtual",   BytecodeDisassembler.METHODREF2);                                           break;
                case 128:  this.da("ior");                                                                                        break;
                case 112:  this.da("irem");                                                                                       break;
                case 172:  this.da("ireturn");                                                                                    break;
                case 120:  this.da("ishl");                                                                                       break;
                case 122:  this.da("ishr");                                                                                       break;
                case 54:   this.da("istore",          BytecodeDisassembler.LOCALVARIABLEINDEX1);                                  break;
                case 59:   this.da("istore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_0);                         break;
                case 60:   this.da("istore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_1);                         break;
                case 61:   this.da("istore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_2);                         break;
                case 62:   this.da("istore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_3);                         break;
                case 100:  this.da("isub");                                                                                       break;
                case 124:  this.da("iushr");                                                                                      break;
                case 130:  this.da("ixor");                                                                                       break;
                case 168:  this.da("jsr",             BytecodeDisassembler.BRANCHOFFSET2);                                        break;
                case 201:  this.da("jsr_w",           BytecodeDisassembler.BRANCHOFFSET4);                                        break;
                case 138:  this.da("l2d");                                                                                        break;
                case 137:  this.da("l2f");                                                                                        break;
                case 136:  this.da("l2i");                                                                                        break;
                case 97:   this.da("ladd");                                                                                       break;
                case 47:   this.da("laload");                                                                                     break;
                case 127:  this.da("land");                                                                                       break;
                case 80:   this.da("lastore");                                                                                    break;
                case 148:  this.da("lcmp");                                                                                       break;
                case 9:    this.da("lconst_0");                                                                                   break;
                case 10:   this.da("lconst_1");                                                                                   break;
                case 18:   this.da("ldc",             BytecodeDisassembler.INTFLOATCLASSSTRING1);                                 break;
                case 19:   this.da("ldc_w",           BytecodeDisassembler.INTFLOATCLASSSTRING2);                                 break;
                case 20:   this.da("ldc2_w",          BytecodeDisassembler.LONGDOUBLE2);                                          break;
                case 109:  this.da("ldiv");                                                                                       break;
                case 22:   this.da("lload",           BytecodeDisassembler.LOCALVARIABLEINDEX1);                                  break;
                case 30:   this.da("lload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_0);                         break;
                case 31:   this.da("lload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_1);                         break;
                case 32:   this.da("lload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_2);                         break;
                case 33:   this.da("lload",           BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_3);                         break;
                case 105:  this.da("lmul");                                                                                       break;
                case 117:  this.da("lneg");                                                                                       break;
                case 171:  this.da("lookupswitch",    BytecodeDisassembler.LOOKUPSWITCH);                                         break;
                case 129:  this.da("lor");                                                                                        break;
                case 113:  this.da("lrem");                                                                                       break;
                case 173:  this.da("lreturn");                                                                                    break;
                case 121:  this.da("lshl");                                                                                       break;
                case 123:  this.da("lshr");                                                                                       break;
                case 55:   this.da("lstore",          BytecodeDisassembler.LOCALVARIABLEINDEX1);                                  break;
                case 63:   this.da("lstore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_0);                         break;
                case 64:   this.da("lstore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_1);                         break;
                case 65:   this.da("lstore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_2);                         break;
                case 66:   this.da("lstore",          BytecodeDisassembler.IMPLICITLOCALVARIABLEINDEX_3);                         break;
                case 101:  this.da("lsub");                                                                                       break;
                case 125:  this.da("lushr");                                                                                      break;
                case 131:  this.da("lxor");                                                                                       break;
                case 194:  this.da("monitorenter");                                                                               break;
                case 195:  this.da("monitorexit");                                                                                break;
                case 197:  this.da("multianewarray",  BytecodeDisassembler.CLASS2, BytecodeDisassembler.UNSIGNEDBYTE);            break;
                case 187:  this.da("new",             BytecodeDisassembler.CLASS2);                                               break;
                case 188:  this.da("newarray",        BytecodeDisassembler.ATYPE);                                                break;
                case 0:    this.da("nop");                                                                                        break;
                case 87:   this.da("pop");                                                                                        break;
                case 88:   this.da("pop2");                                                                                       break;
                case 181:  this.da("putfield",        BytecodeDisassembler.FIELDREF2);                                            break;
                case 179:  this.da("putstatic",       BytecodeDisassembler.FIELDREF2);                                            break;
                case 169:  this.da("ret",             BytecodeDisassembler.LOCALVARIABLEINDEX1);                                  break;
                case 177:  this.da("return");                                                                                     break;
                case 53:   this.da("saload");                                                                                     break;
                case 86:   this.da("sastore");                                                                                    break;
                case 17:   this.da("sipush",          BytecodeDisassembler.SIGNEDSHORT);                                          break;
                case 95:   this.da("swap");                                                                                       break;
                case 170:  this.da("tableswitch",     BytecodeDisassembler.TABLESWITCH);                                          break;

                case 196:
                    {
                        int subopcode = 0xff & this.dis.readByte();
                        switch (subopcode) {

                        // SUPPRESS CHECKSTYLE LineLength:12
                        case 21:  this.da("wide iload",  BytecodeDisassembler.LOCALVARIABLEINDEX2);                                   break;
                        case 23:  this.da("wide fload",  BytecodeDisassembler.LOCALVARIABLEINDEX2);                                   break;
                        case 25:  this.da("wide aload",  BytecodeDisassembler.LOCALVARIABLEINDEX2);                                   break;
                        case 22:  this.da("wide lload",  BytecodeDisassembler.LOCALVARIABLEINDEX2);                                   break;
                        case 24:  this.da("wide dload",  BytecodeDisassembler.LOCALVARIABLEINDEX2);                                   break;
                        case 54:  this.da("wide istore", BytecodeDisassembler.LOCALVARIABLEINDEX2);                                   break;
                        case 56:  this.da("wide fstore", BytecodeDisassembler.LOCALVARIABLEINDEX2);                                   break;
                        case 58:  this.da("wide astore", BytecodeDisassembler.LOCALVARIABLEINDEX2);                                   break;
                        case 55:  this.da("wide lstore", BytecodeDisassembler.LOCALVARIABLEINDEX2);                                   break;
                        case 57:  this.da("wide dstore", BytecodeDisassembler.LOCALVARIABLEINDEX2);                                   break;
                        case 169: this.da("wide ret",    BytecodeDisassembler.LOCALVARIABLEINDEX2);                                   break;
                        case 132: this.da("wide iinc",   BytecodeDisassembler.LOCALVARIABLEINDEX2, BytecodeDisassembler.SIGNEDSHORT); break;

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
                            (ct == null ? "[all exceptions]" : this.d.beautify(ct.name))
                            + " => "
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
                if (sourceLine == null && this.hideLines) break PRINT_SOURCE_LINE;

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
                    if (!this.hideLines) {
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
    da(String mnemonic, Operand... operands) throws IOException {

        if (operands.length == 0) {
            this.lines.put(this.instructionOffset, mnemonic);
            return;
        }

        Formatter f = new Formatter();
        f.format("%-15s", mnemonic);

        for (int i = 0; i < operands.length; ++i) {
            f.format(" %s", operands[i].disassemble(this));
        }

        this.lines.put(this.instructionOffset, f.toString());
    }

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
            label = this.symbolicLabels ? "L" + (1 + this.branchTargets.size()) : "#" + offset;
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