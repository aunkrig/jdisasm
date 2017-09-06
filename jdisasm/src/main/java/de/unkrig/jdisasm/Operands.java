
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

import java.io.IOException;

import de.unkrig.jdisasm.BytecodeDisassembler.Operand;
import de.unkrig.jdisasm.ClassFile.BootstrapMethodsAttribute.BootstrapMethod;
import de.unkrig.jdisasm.ConstantPool.ConstantClassInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantClassOrFloatOrIntegerOrStringInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantDoubleOrLongOrStringInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantFieldrefInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantInterfaceMethodrefInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantInterfaceMethodrefOrMethodrefInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantInvokeDynamicInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantMethodrefInfo;

public
class Operands {

    static final Operand INTFLOATCLASSSTRING1 = new Operand() {

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

    static final Operand INTFLOATCLASSSTRING2 = new Operand() {

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

    static final Operand LONGDOUBLE2 = new Operand() {

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

    static final Operand FIELDREF2 = new Operand() {

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

    static final Operand METHODREF2 = new Operand() {

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

    static final Operand INTERFACEMETHODREF2 = new Operand() {

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

    static final Operand INTERFACEMETHODREFORMETHODREF2 = new Operand() {

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

    static final Operand CLASS2 = new Operand() {

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

    static final Operand LOCALVARIABLEINDEX1 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {

            short index = (short) (0xff & bd.dis.readByte());

            // For an initial assignment (e.g. 'istore 7'), the local variable is only visible AFTER this instruction.
            return bd.d.beautify(bd.d.getLocalVariable(index, bd.instructionOffset + 2, bd.method).toString());
        }
    };

    static final Operand LOCALVARIABLEINDEX2 = new Operand() {

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
    static final Operand IMPLICITLOCALVARIABLEINDEX_0 = Operands.implicitLocalVariableIndexOperand(0);
    static final Operand IMPLICITLOCALVARIABLEINDEX_1 = Operands.implicitLocalVariableIndexOperand(1);
    static final Operand IMPLICITLOCALVARIABLEINDEX_2 = Operands.implicitLocalVariableIndexOperand(2);
    static final Operand IMPLICITLOCALVARIABLEINDEX_3 = Operands.implicitLocalVariableIndexOperand(3);


    static final Operand BRANCHOFFSET2 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            return bd.branchTarget(bd.instructionOffset + bd.dis.readShort());
        }
    };

    static final Operand BRANCHOFFSET4 = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            return bd.branchTarget(bd.instructionOffset + bd.dis.readInt());
        }
    };

    static final Operand SIGNEDBYTE = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            return Integer.toString(bd.dis.readByte());
        }
    };

    static final Operand UNSIGNEDBYTE = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            return Integer.toString(0xff & bd.dis.readByte());
        }
    };

    static final Operand ATYPE = new Operand() {

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

    static final Operand SIGNEDSHORT = new Operand() {

        @Override public String
        disassemble(BytecodeDisassembler bd) throws IOException {
            return Integer.toString(bd.dis.readShort());
        }
    };

    static final Operand TABLESWITCH = new Operand() {

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

    static final Operand LOOKUPSWITCH = new Operand() {

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

    static final Operand DYNAMICCALLSITE = new Operand() {

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
}
