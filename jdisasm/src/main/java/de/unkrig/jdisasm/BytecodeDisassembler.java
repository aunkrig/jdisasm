
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
import de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType;
import de.unkrig.jdisasm.ClassFile.AppendFrame;
import de.unkrig.jdisasm.ClassFile.BootstrapMethodsAttribute.BootstrapMethod;
import de.unkrig.jdisasm.ClassFile.ChopFrame;
import de.unkrig.jdisasm.ClassFile.ExceptionTableEntry;
import de.unkrig.jdisasm.ClassFile.FullFrame;
import de.unkrig.jdisasm.ClassFile.LineNumberTableAttribute;
import de.unkrig.jdisasm.ClassFile.LineNumberTableEntry;
import de.unkrig.jdisasm.ClassFile.Method;
import de.unkrig.jdisasm.ClassFile.SameFrame;
import de.unkrig.jdisasm.ClassFile.SameFrameExtended;
import de.unkrig.jdisasm.ClassFile.SameLocals1StackItemFrame;
import de.unkrig.jdisasm.ClassFile.SameLocals1StackItemFrameExtended;
import de.unkrig.jdisasm.ClassFile.StackMapFrame;
import de.unkrig.jdisasm.ClassFile.StackMapFrameVisitor;
import de.unkrig.jdisasm.ClassFile.StackMapTableAttribute;
import de.unkrig.jdisasm.ConstantPool.ConstantClassInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantClassOrFloatOrIntegerOrStringOrMethodHandleOrMethodTypeOrDynamicInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantDoubleOrLongOrDynamicInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantFieldrefInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantInterfaceMethodrefInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantInterfaceMethodrefOrMethodrefInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantInvokeDynamicInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantMethodrefInfo;
import de.unkrig.jdisasm.SignatureParser.TypeSignature;

/**
 * Disassembles the bytecode of a class file method.
 */
public
class BytecodeDisassembler {

    private final CountingInputStream                cis;
    private final DataInputStream                    dis;
    private final List<ExceptionTableEntry>          exceptionTable;
    @Nullable private final LineNumberTableAttribute lineNumberTableAttribute;
    @Nullable private final StackMapTableAttribute   stackMapTableAttribute;
    @Nullable private final Map<Integer, String>     sourceLines;
    private final Method                             method;
    private final TypeSignature[]                    parameterTypes;
    private final Disassembler                       d;

    private int                                             instructionOffset;
    private final Map<Integer /*offset*/, String /*label*/> branchTargets = new HashMap<Integer, String>();

    /**
     * Maps instruction offsets to disassembly lines.
     */
    private final SortedMap<Integer, String> lines = new TreeMap<Integer, String>();

    public
    BytecodeDisassembler(
        InputStream                        is,
        List<ExceptionTableEntry>          exceptionTable,
        @Nullable LineNumberTableAttribute lineNumberTableAttribute,
        @Nullable StackMapTableAttribute   stackMapTableAttribute,
        @Nullable Map<Integer, String>     sourceLines,
        Method                             method,
        SignatureParser.TypeSignature[]    parameterTypes,
        Disassembler                       d
    ) {
        this.cis                      = new CountingInputStream(is);
        this.dis                      = new DataInputStream(this.cis);
        this.exceptionTable           = exceptionTable;
        this.lineNumberTableAttribute = lineNumberTableAttribute;
        this.stackMapTableAttribute   = stackMapTableAttribute;
        this.sourceLines              = sourceLines;
        this.method                   = method;
        this.parameterTypes           = parameterTypes;
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

        // Decode the StackMapTable attribute.
        Map<Integer, String> stackMap;
        STACK_MAP_TABLE: {
            stackMap = new HashMap<Integer, String>();

            StackMapTableAttribute smta = this.stackMapTableAttribute;
            if (!this.d.printStackMap || smta == null) break STACK_MAP_TABLE;

            final String[] none = new String[0];

            String[] locals;
            {
                List<String> l = new ArrayList<String>();

                if ("<init>".equals(this.method.name)) {
                    l.add("uninitializedThis");
                } else
                if (!this.method.accessFlags.is(FlagType.STATIC)) {
                    l.add(this.method.getClassFile().thisClassName);
                }

                for (TypeSignature ts : this.parameterTypes) l.add(ts.toString());

                locals = l.toArray(new String[l.size()]);
            }

            String[] stack = none;
            stackMap.put(0, "Locals=" + Arrays.toString(locals) + " Stack=" + Arrays.toString(stack));

            int bytecodeOffset = -1;
            for (StackMapFrame smf : smta.entries) {
                bytecodeOffset += 1 + smf.offsetDelta;

                {
                    final String[] finalLocals = locals;
                    locals = smf.accept(new StackMapFrameVisitor<String[]>() {
                        @Override public String[] visitSameFrame(SameFrame sf)                                                      { return finalLocals;                                                                                  }
                        @Override public String[] visitSameLocals1StackItemFrame(SameLocals1StackItemFrame sl1sif)                  { return finalLocals;                                                                                  }
                        @Override public String[] visitSameLocals1StackItemFrameExtended(SameLocals1StackItemFrameExtended sl1sife) { return finalLocals;                                                                                  }
                        @Override public String[] visitChopFrame(ChopFrame cf)                                                      { return Arrays.copyOf(finalLocals, finalLocals.length - cf.k);                                        }
                        @Override public String[] visitSameFrameExtended(SameFrameExtended sfe)                                     { return finalLocals;                                                                                  }
                        @Override public String[] visitAppendFrame(AppendFrame af)                                                  { return BytecodeDisassembler.concat(finalLocals, BytecodeDisassembler.this.toStringArray(af.locals)); }
                        @Override public String[] visitFullFrame(FullFrame ff)                                                      { return BytecodeDisassembler.this.toStringArray(ff.locals);                                           }
                    });
                }

                stack = smf.accept(new StackMapFrameVisitor<String[]>() {
                    @Override public String[] visitSameFrame(SameFrame sf)                                                      { return none;                                              }
                    @Override public String[] visitSameLocals1StackItemFrame(SameLocals1StackItemFrame sl1sif)                  { return new String[] { String.valueOf(sl1sif.stack) };     }
                    @Override public String[] visitSameLocals1StackItemFrameExtended(SameLocals1StackItemFrameExtended sl1sife) { return new String[] { String.valueOf(sl1sife.stack) };    }
                    @Override public String[] visitChopFrame(ChopFrame cf)                                                      { return none;                                              }
                    @Override public String[] visitSameFrameExtended(SameFrameExtended sfe)                                     { return none;                                              }
                    @Override public String[] visitAppendFrame(AppendFrame af)                                                  { return none;                                              }
                    @Override public String[] visitFullFrame(FullFrame ff)                                                      { return BytecodeDisassembler.this.toStringArray(ff.stack); }
                });

                stackMap.put(bytecodeOffset, "Locals=" + Arrays.toString(locals) + " Stack=" + Arrays.toString(stack));
            }
        }

        // Decodes one instruction and returns one line of disassembly. Requires "this.instructionOffset" to be set
        // correctly. Produces "null" on end-of-input.
        BytecodeDecoder<String, IOException> bytecodeDecoder = new BytecodeDecoder<String, IOException>() {

            @Override @Nullable public String
            decoded(String mnemonic, OperandKind... operandKinds) throws IOException {

                if ("end".equals(mnemonic)) return null;

                if (operandKinds.length == 0) return mnemonic;

                Formatter f = new Formatter();
                f.format("%-15s", mnemonic);

                for (int i = 0; i < operandKinds.length; ++i) {
                    f.format(" %s", operandKinds[i].accept(BytecodeDisassembler.this.readOperand));
                }

                return f.toString();
            }
        };

        // Now decode the bytecode and fill the "this.lines" map.
        for (;;) {

            // "this.readOperands" needs this:
            this.instructionOffset = (int) this.cis.getCount();

            // Decode one instruction into one line of assembly.
            String line = bytecodeDecoder.decode(this.dis);
            if (line == null) break;

            BytecodeDisassembler.this.lines.put(BytecodeDisassembler.this.instructionOffset, line);
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

                String sourceLine = this.sourceLines != null ? this.sourceLines.get(lineNumber) : null;

                if (sourceLine == null) {
                    if (this.d.showLineNumbers) {
                        pw.println(indentation + "// Line " + lineNumber);
                    } else {
                        ;
                    }
                } else {
                    if (this.d.showLineNumbers) {
                        pw.println(indentation + "//                                      Line " + lineNumber + ": " + sourceLine);
                    } else {
                        pw.println(indentation + "//                                      " + sourceLine);
                    }
                }
            }

            // Print stack map.
            PRINT_STACK_FRAME: {
                String smf = stackMap.get(instructionOffset);
                if (smf == null) break PRINT_STACK_FRAME;

                pw.println(indentation + "// " + smf);
            }

            // Print instruction offset.
            String indentation2 = indentation;
            {
                String label = this.branchTargets.get(instructionOffset);
                if (label == null && this.d.printAllOffsets) {
                    label = "#" + instructionOffset;
                }

                if (label != null) {
                    if (label.length() >= indentation.length()) {
                        pw.println(label);
                    } else {
                        indentation2 = label + indentation.substring(label.length());
                    }
                }
            }

            // Print disassembly line.
            pw.println(indentation2 + text);
        }
    }

    protected String[]
    toStringArray(Object[] objects) {
        String[] result = new String[objects.length];
        for (int i = 0; i < result.length; i++) result[i] = String.valueOf(objects[i]);
        return result;
    }

    private static <T> T[]
    concat(T[] lhs, T[] rhs) {
        T[] result = Arrays.copyOf(lhs, lhs.length + rhs.length);
        System.arraycopy(rhs, 0, result, lhs.length, rhs.length);
        return result;
    }

    /**
     * A visitor that reads an instruction operand from the {@link #dis} and transforms it into a human-readable form,
     * suitable for a disassembly listing.
     */
    private final OperandKind.Visitor<String, IOException>
    readOperand = new OperandKind.Visitor<String, IOException>() {

        @Override public String
        visitClassFloatIntStringMethodHandleMethodTypeDynamic(OperandKind operandType) throws IOException {

            short  index = (short) (0xff & BytecodeDisassembler.this.dis.readByte());
            String t     = BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                index,
                ConstantClassOrFloatOrIntegerOrStringOrMethodHandleOrMethodTypeOrDynamicInfo.class
            ).toString();

            if (BytecodeDisassembler.this.d.showClassPoolIndexes) t += " (" + (0xffff & index) + ")";

            return t;
        }

        @Override public String
        visitClassFloatIntStringMethodHandleMethodTypeDynamicW(OperandKind operandType) throws IOException {

            short  index = BytecodeDisassembler.this.dis.readShort();
            String t     = BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                index,
                ConstantClassOrFloatOrIntegerOrStringOrMethodHandleOrMethodTypeOrDynamicInfo.class
            ).toString();

            if (BytecodeDisassembler.this.d.showClassPoolIndexes) t += " (" + (0xffff & index) + ")";

            return t;
        }

        @Override public String
        visitDoubleLongDynamicW(OperandKind operandType) throws IOException {

            short  index = BytecodeDisassembler.this.dis.readShort();
            String t     = BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                index,
                ConstantDoubleOrLongOrDynamicInfo.class
            ).toString();

            if (BytecodeDisassembler.this.d.showClassPoolIndexes) t += " (" + (0xffff & index) + ")";

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

            if (BytecodeDisassembler.this.d.showClassPoolIndexes) t += " (" + (0xffff & index) + ")";

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

            if (BytecodeDisassembler.this.d.showClassPoolIndexes) t += " (" + (0xffff & index) + ")";

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

            if (BytecodeDisassembler.this.d.showClassPoolIndexes) t += " (" + (0xffff & index) + ")";

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

            if (BytecodeDisassembler.this.d.showClassPoolIndexes) t += " (" + (0xffff & index) + ")";

            return t;
        }

        @Override public String
        visitClass2(OperandKind operandType) throws IOException {

            short index = BytecodeDisassembler.this.dis.readShort();

            String t = BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                index,
                ConstantClassInfo.class
            ).toString();

            if (BytecodeDisassembler.this.d.showClassPoolIndexes) t += " (" + (0xffff & index) + ")";

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

        /**
         * E.g.
         * <pre>
         * REF_invokeStatic:java.lang.invoke.LambdaMetafactory:::java.lang.invoke.LambdaMetafactory.metafactory
         *( // BootstrapMethod.methodHandle
         *    java.lang.invoke.MethodHandles$Lookup,
         *    String,
         *    java.lang.invoke.MethodType,
         *    java.lang.invoke.MethodType,
         *    java.lang.invoke.MethodHandle,
         *    java.lang.invoke.MethodType
         * ) => java.lang.invoke.CallSite( // BootstrapMethod.bootstrapArguments
         *    (),
         *    REF_invokeStatic:pkg.foo5.Main:::pkg.foo5.Main.lambda$0(),
         *    ()
         * ).
         * run : () => Runnable       cidy.nameAndType
         * </pre>
         */
        @Override public String
        visitDynamicCallsite(OperandKind operandType) throws IOException {

            short index = BytecodeDisassembler.this.dis.readShort();

            if (BytecodeDisassembler.this.dis.readByte() != 0 || BytecodeDisassembler.this.dis.readByte() != 0) {
                throw new RuntimeException("'invokevirtual' pad byte is not zero");
            }

            ConstantInvokeDynamicInfo
            cidy = BytecodeDisassembler.this.method.getClassFile().constantPool.get(
                index,
                ConstantInvokeDynamicInfo.class
            );
            BootstrapMethod
            bm = BytecodeDisassembler.this.method.getBootstrapMethodsAttribute().bootstrapMethods.get(
                cidy.bootstrapMethodAttrIndex
            );

            return bm + "." + cidy.nameAndType;
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
