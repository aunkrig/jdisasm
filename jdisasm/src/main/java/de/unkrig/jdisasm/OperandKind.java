
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

/**
 * Representation of the "kind" of and instruction operand. E.g. the instruction with the mnemonic {@code
 * multianewarray} has two operands; the first is of kind {@link OperandKind#CLASS2}, and the second operand is of
 * kind {@link OperandKind#UNSIGNEDBYTE}.
 * <p>
 *   Instruction operands are not to be confused with the values on the "operand stack", which are also called
 *   operands.
 * </p>
 */
public
enum OperandKind {

    // SUPPRESS CHECKSTYLE Javadoc:31
    CLASSFLOATINTSTRINGMETHODHANDLEMETHODTYPEDYNAMIC   { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitClassFloatIntStringMethodHandleMethodTypeDynamic(this);  } },
    CLASSFLOATINTSTRINGMETHODHANDLEMETHODTYPEDYNAMIC_W { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitClassFloatIntStringMethodHandleMethodTypeDynamicW(this); } },
    DOUBLELONGDYNAMIC_W                                { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitDoubleLongDynamicW(this);                                } },

    FIELDREF2                                          { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitFieldref2(this);                                         } },

    METHODREF2                                         { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitMethodref2(this);                                        } },
    INTERFACEMETHODREF2                                { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitInterfaceMethodref2(this);                               } },
    INTERFACEMETHODREFORMETHODREF2                     { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitInterfaceMethodrefOrMethodref2(this);                    } },

    CLASS2                                             { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitClass2(this);                                            } },

    LOCALVARIABLEINDEX1                                { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitLocalVariableIndex1(this);                               } },
    LOCALVARIABLEINDEX2                                { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitLocalVariableIndex2(this);                               } },
    IMPLICITLOCALVARIABLEINDEX_0                       { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitImplicitLocalVariableIndex(this, 0);                     } },
    IMPLICITLOCALVARIABLEINDEX_1                       { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitImplicitLocalVariableIndex(this, 1);                     } },
    IMPLICITLOCALVARIABLEINDEX_2                       { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitImplicitLocalVariableIndex(this, 2);                     } },
    IMPLICITLOCALVARIABLEINDEX_3                       { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitImplicitLocalVariableIndex(this, 3);                     } },

    BRANCHOFFSET2                                      { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitBranchOffset2(this);                                     } },
    BRANCHOFFSET4                                      { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitBranchOffset4(this);                                     } },

    SIGNEDBYTE                                         { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitSignedByte(this);                                        } },
    UNSIGNEDBYTE                                       { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitUnsignedByte(this);                                      } },
    SIGNEDSHORT                                        { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitSignedShort(this);                                       } },
    ATYPE                                              { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitAtype(this);                                             } },

    TABLESWITCH                                        { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitTableswitch(this);                                       } },
    LOOKUPSWITCH                                       { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitLookupswitch(this);                                      } },

    DYNAMICCALLSITE                                    { @Override public <R, EX extends Throwable> R accept(Visitor<R, EX> v) throws EX { return v.visitDynamicCallsite(this);                                   } },

    ;

    /**
     * The reciprocal of {@link OperandKind}; useful for implementing the VISITOR pattern.
     *
     * @param <R>  The return type of the {@code visit*()} methods
     * @param <EX> The exception that the {@code visit*()} methods may throw
     */
    public
    interface Visitor<R, EX extends Throwable> {

        // SUPPRESS CHECKSTYLE Javadoc:28
        R visitClassFloatIntStringMethodHandleMethodTypeDynamic(OperandKind operandType)                  throws EX;
        R visitClassFloatIntStringMethodHandleMethodTypeDynamicW(OperandKind operandType)                  throws EX;
        R visitDoubleLongDynamicW(OperandKind operandType)                           throws EX;

        R visitFieldref2(OperandKind operandType)                             throws EX;

        R visitMethodref2(OperandKind operandType)                            throws EX;
        R visitInterfaceMethodref2(OperandKind operandType)                   throws EX;
        R visitInterfaceMethodrefOrMethodref2(OperandKind operandType)        throws EX;

        R visitClass2(OperandKind operandType)                                throws EX;

        R visitLocalVariableIndex1(OperandKind operandType)                   throws EX;
        R visitLocalVariableIndex2(OperandKind operandType)                   throws EX;
        R visitImplicitLocalVariableIndex(OperandKind operandType, int index) throws EX;

        R visitBranchOffset2(OperandKind operandType)                         throws EX;
        R visitBranchOffset4(OperandKind operandType)                         throws EX;

        R visitSignedByte(OperandKind operandType)                            throws EX;
        R visitUnsignedByte(OperandKind operandType)                          throws EX;
        R visitSignedShort(OperandKind operandType)                           throws EX;
        R visitAtype(OperandKind operandType)                                 throws EX;

        R visitTableswitch(OperandKind operandType)                           throws EX;
        R visitLookupswitch(OperandKind operandType)                          throws EX;

        R visitDynamicCallsite(OperandKind operandType)                       throws EX;
    }

    /**
     * Invokes the respective method of the given <var>visitor</var>.
     */
    public abstract <R, EX extends Throwable> R accept(Visitor<R, EX> visitor) throws EX;
}
