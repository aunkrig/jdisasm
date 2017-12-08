
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
import java.io.IOException;

import de.unkrig.commons.nullanalysis.NotNullByDefault;
import de.unkrig.commons.nullanalysis.Nullable;

/**
 * Decodes the bytecode of a class file method.
 *
 * @param <R>  The return type of the {@link #decoded(String, OperandKind...)} method
 * @param <EX> The exception that {@link #decoded(String, OperandKind...)} my throw
 */
public abstract
class BytecodeDecoder<R, EX extends Throwable> {

    /**
     * Reads one (or two) opcode bytes from the <var>dis</var>, and invokes {@link #decoded(String, OperandKind...)}
     * with the instruction mnemonic and the applicable operand kinds for the instruction. On end-of-input,
     * {@link #decoded(String, OperandKind...)} is instead invoked with mnemonic {@code "end"}.
     * <p>
     *   Notice that the <em>operands</em> are <em>not</em> read from the <var>dis</var>!
     * </p>
     *
     * @throws ClassFileFormatException Invalid opcode encountered
     */
    @NotNullByDefault(false) public R
    decode(DataInputStream dis) throws IOException, EX {

        int opcode = dis.read();

        switch (opcode) {

        case -1:   return this.decoded("end");

        case 50:   return this.decoded("aaload");
        case 83:   return this.decoded("aastore");
        case 1:    return this.decoded("aconst_null");
        case 25:   return this.decoded("aload",           OperandKind.LOCALVARIABLEINDEX1);
        case 42:   return this.decoded("aload_0",         OperandKind.IMPLICITLOCALVARIABLEINDEX_0);
        case 43:   return this.decoded("aload_1",         OperandKind.IMPLICITLOCALVARIABLEINDEX_1);
        case 44:   return this.decoded("aload_2",         OperandKind.IMPLICITLOCALVARIABLEINDEX_2);
        case 45:   return this.decoded("aload_3",         OperandKind.IMPLICITLOCALVARIABLEINDEX_3);
        case 189:  return this.decoded("anewarray",       OperandKind.CLASS2);
        case 176:  return this.decoded("areturn");
        case 190:  return this.decoded("arraylength");
        case 58:   return this.decoded("astore",          OperandKind.LOCALVARIABLEINDEX1);
        case 75:   return this.decoded("astore_0",        OperandKind.IMPLICITLOCALVARIABLEINDEX_0);
        case 76:   return this.decoded("astore_1",        OperandKind.IMPLICITLOCALVARIABLEINDEX_1);
        case 77:   return this.decoded("astore_2",        OperandKind.IMPLICITLOCALVARIABLEINDEX_2);
        case 78:   return this.decoded("astore_3",        OperandKind.IMPLICITLOCALVARIABLEINDEX_3);
        case 191:  return this.decoded("athrow");
        case 51:   return this.decoded("baload");
        case 84:   return this.decoded("bastore");
        case 16:   return this.decoded("bipush",          OperandKind.SIGNEDBYTE);
        case 52:   return this.decoded("caload");
        case 85:   return this.decoded("castore");
        case 192:  return this.decoded("checkcast",       OperandKind.CLASS2);
        case 144:  return this.decoded("d2f");
        case 142:  return this.decoded("d2i");
        case 143:  return this.decoded("d2l");
        case 99:   return this.decoded("dadd");
        case 49:   return this.decoded("daload");
        case 82:   return this.decoded("dastore");
        case 152:  return this.decoded("dcmpg");
        case 151:  return this.decoded("dcmpl");
        case 14:   return this.decoded("dconst_0");
        case 15:   return this.decoded("dconst_1");
        case 111:  return this.decoded("ddiv");
        case 24:   return this.decoded("dload",           OperandKind.LOCALVARIABLEINDEX1);
        case 38:   return this.decoded("dload_0",         OperandKind.IMPLICITLOCALVARIABLEINDEX_0);
        case 39:   return this.decoded("dload_1",         OperandKind.IMPLICITLOCALVARIABLEINDEX_1);
        case 40:   return this.decoded("dload_2",         OperandKind.IMPLICITLOCALVARIABLEINDEX_2);
        case 41:   return this.decoded("dload_3",         OperandKind.IMPLICITLOCALVARIABLEINDEX_3);
        case 107:  return this.decoded("dmul");
        case 119:  return this.decoded("dneg");
        case 115:  return this.decoded("drem");
        case 175:  return this.decoded("dreturn");
        case 57:   return this.decoded("dstore",          OperandKind.LOCALVARIABLEINDEX1);
        case 71:   return this.decoded("dstore_0",        OperandKind.IMPLICITLOCALVARIABLEINDEX_0);
        case 72:   return this.decoded("dstore_1",        OperandKind.IMPLICITLOCALVARIABLEINDEX_1);
        case 73:   return this.decoded("dstore_1",        OperandKind.IMPLICITLOCALVARIABLEINDEX_2);
        case 74:   return this.decoded("dstore_1",        OperandKind.IMPLICITLOCALVARIABLEINDEX_3);
        case 103:  return this.decoded("dsub");
        case 89:   return this.decoded("dup");
        case 90:   return this.decoded("dup_x1");
        case 91:   return this.decoded("dup_x2");
        case 92:   return this.decoded("dup2");
        case 93:   return this.decoded("dup2_x1");
        case 94:   return this.decoded("dup2_x2");
        case 141:  return this.decoded("f2d");
        case 139:  return this.decoded("f2i");
        case 140:  return this.decoded("f2l");
        case 98:   return this.decoded("fadd");
        case 48:   return this.decoded("faload");
        case 81:   return this.decoded("fastore");
        case 150:  return this.decoded("fcmpg");
        case 149:  return this.decoded("fcmpl");
        case 11:   return this.decoded("fconst_0");
        case 12:   return this.decoded("fconst_1");
        case 13:   return this.decoded("fconst_2");
        case 110:  return this.decoded("fdiv");
        case 23:   return this.decoded("fload",           OperandKind.LOCALVARIABLEINDEX1);
        case 34:   return this.decoded("fload_0",         OperandKind.IMPLICITLOCALVARIABLEINDEX_0);
        case 35:   return this.decoded("fload_1",         OperandKind.IMPLICITLOCALVARIABLEINDEX_1);
        case 36:   return this.decoded("fload_2",         OperandKind.IMPLICITLOCALVARIABLEINDEX_2);
        case 37:   return this.decoded("fload_3",         OperandKind.IMPLICITLOCALVARIABLEINDEX_3);
        case 106:  return this.decoded("fmul");
        case 118:  return this.decoded("fneg");
        case 114:  return this.decoded("frem");
        case 174:  return this.decoded("freturn");
        case 56:   return this.decoded("fstore",          OperandKind.LOCALVARIABLEINDEX1);
        case 67:   return this.decoded("fstore_0",        OperandKind.IMPLICITLOCALVARIABLEINDEX_0);
        case 68:   return this.decoded("fstore_1",        OperandKind.IMPLICITLOCALVARIABLEINDEX_1);
        case 69:   return this.decoded("fstore_2",        OperandKind.IMPLICITLOCALVARIABLEINDEX_2);
        case 70:   return this.decoded("fstore_3",        OperandKind.IMPLICITLOCALVARIABLEINDEX_3);
        case 102:  return this.decoded("fsub");
        case 180:  return this.decoded("getfield",        OperandKind.FIELDREF2);
        case 178:  return this.decoded("getstatic",       OperandKind.FIELDREF2);
        case 167:  return this.decoded("goto",            OperandKind.BRANCHOFFSET2);
        case 200:  return this.decoded("goto_w",          OperandKind.BRANCHOFFSET4);
        case 145:  return this.decoded("i2b");
        case 146:  return this.decoded("i2c");
        case 135:  return this.decoded("i2d");
        case 134:  return this.decoded("i2f");
        case 133:  return this.decoded("i2l");
        case 147:  return this.decoded("i2s");
        case 96:   return this.decoded("iadd");
        case 46:   return this.decoded("iaload");
        case 126:  return this.decoded("iand");
        case 79:   return this.decoded("iastore");
        case 2:    return this.decoded("iconst_m1");
        case 3:    return this.decoded("iconst_0");
        case 4:    return this.decoded("iconst_1");
        case 5:    return this.decoded("iconst_2");
        case 6:    return this.decoded("iconst_3");
        case 7:    return this.decoded("iconst_4");
        case 8:    return this.decoded("iconst_5");
        case 108:  return this.decoded("idiv");
        case 165:  return this.decoded("if_acmpeq",       OperandKind.BRANCHOFFSET2);
        case 166:  return this.decoded("if_acmpne",       OperandKind.BRANCHOFFSET2);
        case 159:  return this.decoded("if_icmpeq",       OperandKind.BRANCHOFFSET2);
        case 160:  return this.decoded("if_icmpne",       OperandKind.BRANCHOFFSET2);
        case 161:  return this.decoded("if_icmplt",       OperandKind.BRANCHOFFSET2);
        case 162:  return this.decoded("if_icmpge",       OperandKind.BRANCHOFFSET2);
        case 163:  return this.decoded("if_icmpgt",       OperandKind.BRANCHOFFSET2);
        case 164:  return this.decoded("if_icmple",       OperandKind.BRANCHOFFSET2);
        case 153:  return this.decoded("ifeq",            OperandKind.BRANCHOFFSET2);
        case 154:  return this.decoded("ifne",            OperandKind.BRANCHOFFSET2);
        case 155:  return this.decoded("iflt",            OperandKind.BRANCHOFFSET2);
        case 156:  return this.decoded("ifge",            OperandKind.BRANCHOFFSET2);
        case 157:  return this.decoded("ifgt",            OperandKind.BRANCHOFFSET2);
        case 158:  return this.decoded("ifle",            OperandKind.BRANCHOFFSET2);
        case 199:  return this.decoded("ifnonnull",       OperandKind.BRANCHOFFSET2);
        case 198:  return this.decoded("ifnull",          OperandKind.BRANCHOFFSET2);
        case 132:  return this.decoded("iinc",            OperandKind.LOCALVARIABLEINDEX1, OperandKind.SIGNEDBYTE);
        case 21:   return this.decoded("iload",           OperandKind.LOCALVARIABLEINDEX1);
        case 26:   return this.decoded("iload_0",         OperandKind.IMPLICITLOCALVARIABLEINDEX_0);
        case 27:   return this.decoded("iload_1",         OperandKind.IMPLICITLOCALVARIABLEINDEX_1);
        case 28:   return this.decoded("iload_2",         OperandKind.IMPLICITLOCALVARIABLEINDEX_2);
        case 29:   return this.decoded("iload_3",         OperandKind.IMPLICITLOCALVARIABLEINDEX_3);
        case 104:  return this.decoded("imul");
        case 116:  return this.decoded("ineg");
        case 193:  return this.decoded("instanceof",      OperandKind.CLASS2);
        case 186:  return this.decoded("invokedynamic",   OperandKind.DYNAMICCALLSITE);
        case 185:  return this.decoded("invokeinterface", OperandKind.INTERFACEMETHODREF2);
        case 183:  return this.decoded("invokespecial",   OperandKind.INTERFACEMETHODREFORMETHODREF2);
        case 184:  return this.decoded("invokestatic",    OperandKind.INTERFACEMETHODREFORMETHODREF2);
        case 182:  return this.decoded("invokevirtual",   OperandKind.METHODREF2);
        case 128:  return this.decoded("ior");
        case 112:  return this.decoded("irem");
        case 172:  return this.decoded("ireturn");
        case 120:  return this.decoded("ishl");
        case 122:  return this.decoded("ishr");
        case 54:   return this.decoded("istore",          OperandKind.LOCALVARIABLEINDEX1);
        case 59:   return this.decoded("istore_0",        OperandKind.IMPLICITLOCALVARIABLEINDEX_0);
        case 60:   return this.decoded("istore_1",        OperandKind.IMPLICITLOCALVARIABLEINDEX_1);
        case 61:   return this.decoded("istore_2",        OperandKind.IMPLICITLOCALVARIABLEINDEX_2);
        case 62:   return this.decoded("istore_3",        OperandKind.IMPLICITLOCALVARIABLEINDEX_3);
        case 100:  return this.decoded("isub");
        case 124:  return this.decoded("iushr");
        case 130:  return this.decoded("ixor");
        case 168:  return this.decoded("jsr",             OperandKind.BRANCHOFFSET2);
        case 201:  return this.decoded("jsr_w",           OperandKind.BRANCHOFFSET4);
        case 138:  return this.decoded("l2d");
        case 137:  return this.decoded("l2f");
        case 136:  return this.decoded("l2i");
        case 97:   return this.decoded("ladd");
        case 47:   return this.decoded("laload");
        case 127:  return this.decoded("land");
        case 80:   return this.decoded("lastore");
        case 148:  return this.decoded("lcmp");
        case 9:    return this.decoded("lconst_0");
        case 10:   return this.decoded("lconst_1");
        case 18:   return this.decoded("ldc",             OperandKind.CLASSFLOATINTSTRING1);
        case 19:   return this.decoded("ldc_w",           OperandKind.CLASSFLOATINTSTRING2);
        case 20:   return this.decoded("ldc2_w",          OperandKind.DOUBLELONG2);
        case 109:  return this.decoded("ldiv");
        case 22:   return this.decoded("lload",           OperandKind.LOCALVARIABLEINDEX1);
        case 30:   return this.decoded("lload_0",         OperandKind.IMPLICITLOCALVARIABLEINDEX_0);
        case 31:   return this.decoded("lload_1",         OperandKind.IMPLICITLOCALVARIABLEINDEX_1);
        case 32:   return this.decoded("lload_2",         OperandKind.IMPLICITLOCALVARIABLEINDEX_2);
        case 33:   return this.decoded("lload_3",         OperandKind.IMPLICITLOCALVARIABLEINDEX_3);
        case 105:  return this.decoded("lmul");
        case 117:  return this.decoded("lneg");
        case 171:  return this.decoded("lookupswitch",    OperandKind.LOOKUPSWITCH);
        case 129:  return this.decoded("lor");
        case 113:  return this.decoded("lrem");
        case 173:  return this.decoded("lreturn");
        case 121:  return this.decoded("lshl");
        case 123:  return this.decoded("lshr");
        case 55:   return this.decoded("lstore",          OperandKind.LOCALVARIABLEINDEX1);
        case 63:   return this.decoded("lstore_0",        OperandKind.IMPLICITLOCALVARIABLEINDEX_0);
        case 64:   return this.decoded("lstore_1",        OperandKind.IMPLICITLOCALVARIABLEINDEX_1);
        case 65:   return this.decoded("lstore_2",        OperandKind.IMPLICITLOCALVARIABLEINDEX_2);
        case 66:   return this.decoded("lstore_3",        OperandKind.IMPLICITLOCALVARIABLEINDEX_3);
        case 101:  return this.decoded("lsub");
        case 125:  return this.decoded("lushr");
        case 131:  return this.decoded("lxor");
        case 194:  return this.decoded("monitorenter");
        case 195:  return this.decoded("monitorexit");
        case 197:  return this.decoded("multianewarray",  OperandKind.CLASS2, OperandKind.UNSIGNEDBYTE);
        case 187:  return this.decoded("new",             OperandKind.CLASS2);
        case 188:  return this.decoded("newarray",        OperandKind.ATYPE);
        case 0:    return this.decoded("nop");
        case 87:   return this.decoded("pop");
        case 88:   return this.decoded("pop2");
        case 181:  return this.decoded("putfield",        OperandKind.FIELDREF2);
        case 179:  return this.decoded("putstatic",       OperandKind.FIELDREF2);
        case 169:  return this.decoded("ret",             OperandKind.LOCALVARIABLEINDEX1);
        case 177:  return this.decoded("return");
        case 53:   return this.decoded("saload");
        case 86:   return this.decoded("sastore");
        case 17:   return this.decoded("sipush",          OperandKind.SIGNEDSHORT);
        case 95:   return this.decoded("swap");
        case 170:  return this.decoded("tableswitch",     OperandKind.TABLESWITCH);

        case 196:
            int subopcode = 0xff & dis.readByte();
            switch (subopcode) {

            case 21:  return this.decoded("wide iload",  OperandKind.LOCALVARIABLEINDEX2);
            case 23:  return this.decoded("wide fload",  OperandKind.LOCALVARIABLEINDEX2);
            case 25:  return this.decoded("wide aload",  OperandKind.LOCALVARIABLEINDEX2);
            case 22:  return this.decoded("wide lload",  OperandKind.LOCALVARIABLEINDEX2);
            case 24:  return this.decoded("wide dload",  OperandKind.LOCALVARIABLEINDEX2);
            case 54:  return this.decoded("wide istore", OperandKind.LOCALVARIABLEINDEX2);
            case 56:  return this.decoded("wide fstore", OperandKind.LOCALVARIABLEINDEX2);
            case 58:  return this.decoded("wide astore", OperandKind.LOCALVARIABLEINDEX2);
            case 55:  return this.decoded("wide lstore", OperandKind.LOCALVARIABLEINDEX2);
            case 57:  return this.decoded("wide dstore", OperandKind.LOCALVARIABLEINDEX2);
            case 169: return this.decoded("wide ret",    OperandKind.LOCALVARIABLEINDEX2);
            case 132: return this.decoded("wide iinc",   OperandKind.LOCALVARIABLEINDEX2, OperandKind.SIGNEDSHORT);

            default:
                throw new ClassFileFormatException("Invalid opcode " + opcode + " after WIDE");
            }

        default:
            throw new ClassFileFormatException("Invalid opcode " + opcode);
        }
    }

    /**
     * Is invoked exactly <em>once</em> by each invocation of {@link #decode(DataInputStream)}.
     */
    @Nullable public abstract R
    decoded(String mnemonic, OperandKind... operandKinds) throws EX;
}
