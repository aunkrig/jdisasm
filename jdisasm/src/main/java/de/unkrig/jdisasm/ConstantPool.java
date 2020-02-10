
/*
 * JDISASM - A Java[TM] class file disassembler
 *
 * Copyright (c) 2001, Arno Unkrig
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
import java.io.InputStream;

import de.unkrig.commons.nullanalysis.Nullable;
import de.unkrig.jdisasm.SignatureParser.SignatureException;

/**
 * Representation of the "constant pool" in a Java class file.
 */
public
class ConstantPool {

    /**
     * Representation of a constant pool entry.
     */
    public
    interface ConstantPoolEntry {
        /** 1 or 2 */
        int size();
    }

    /**
     * Representation of a CONSTANT_Class_info entry.
     */
    public
    class ConstantClassInfo implements ConstantClassOrFloatOrIntegerOrStringInfo {

        /**
         * Fully qualified (dot-separated) class name.
         */
        public final String name;

        public ConstantClassInfo(String name) { this.name = name; }

        @Override public int
        size() { return 1; }

        @Override public String
        toString() {

            String typeName;
            try {
                typeName = ConstantPool.this.signatureParser.decodeClassNameOrFieldDescriptor(this.name).toString();
            } catch (SignatureException e) {
                typeName = this.name;
            }
            return ConstantPool.beautifyTypeName(typeName);
        }
    }

    /**
     * Representation of a CONSTANT_Fieldref_info entry.
     */
    public
    class ConstantFieldrefInfo implements ConstantPoolEntry {

        /**
         * {@code CONSTANT_Fieldref_info.class_index}, see JVMS7 4.4.2
         */
        public final ConstantClassInfo clasS;

        /**
         * {@code CONSTANT_Fieldref_info.name_and_type_index}, see JVMS7 4.4.2
         */
        public final ConstantNameAndTypeInfo nameAndType;

        public
        ConstantFieldrefInfo(ConstantClassInfo clasS, ConstantNameAndTypeInfo nameAndType) {
            this.clasS       = clasS;
            this.nameAndType = nameAndType;
        }

        @Override public int
        size() { return 1; }

        @Override public String
        toString() {
            try {
                return (
                    ConstantPool.this.signatureParser.decodeFieldDescriptor(this.nameAndType.descriptor.bytes)
                    + " "
                    + ConstantPool.beautifyTypeName(this.clasS.name)
                    + "."
                    + this.nameAndType.name.bytes
                );
            } catch (SignatureException e) {
                return (
                    this.nameAndType.descriptor.bytes
                    + " "
                    + ConstantPool.beautifyTypeName(this.clasS.name)
                    + "."
                    + this.nameAndType.name.bytes
                );
            }
        }
    }

    /**
     * Representation of a CONSTANT_Methodref_info entry.
     */
    public
    class ConstantMethodrefInfo extends ConstantInterfaceMethodrefOrMethodrefInfo {

        public
        ConstantMethodrefInfo(ConstantClassInfo clasS, ConstantNameAndTypeInfo nameAndType) {
            super(clasS, nameAndType);
        }
    }

    /**
     * Representation of a CONSTANT_InterfaceMethodref_info entry.
     */
    public
    class ConstantInterfaceMethodrefInfo extends ConstantInterfaceMethodrefOrMethodrefInfo {

        public
        ConstantInterfaceMethodrefInfo(ConstantClassInfo clasS, ConstantNameAndTypeInfo nameAndType) {
            super(clasS, nameAndType);
        }
    }

    /**
     * Representation of a CONSTANT_Methodref_info or a CONSTANT_InterfaceMethodref_info entry.
     */
    public
    class ConstantInterfaceMethodrefOrMethodrefInfo implements ConstantPoolEntry {

        /**
         * {@code CONSTANT_InterfaceMethodref_info.class_index}, see JVMS7 4.4.2
         */
        public final ConstantClassInfo clasS;

        /**
         * {@code CONSTANT_InterfaceMethodref_info.name_and_type_index}, see JVMS7 4.4.2
         */
        public final ConstantNameAndTypeInfo nameAndType;

        public
        ConstantInterfaceMethodrefOrMethodrefInfo(ConstantClassInfo clasS, ConstantNameAndTypeInfo nameAndType) {
            this.clasS       = clasS;
            this.nameAndType = nameAndType;
        }

        @Override public int
        size() { return 1; }

        @Override public String
        toString() {
            try {
                return (
                    ConstantPool.beautifyTypeName(this.clasS.name)
                    + "."
                    + (
                        ConstantPool.this.signatureParser.decodeMethodDescriptor(this.nameAndType.descriptor.bytes)
                        .toString(ConstantPool.beautifyTypeName(this.clasS.name), this.nameAndType.name.bytes)
                    )
                );
            } catch (SignatureException e) {
                return ConstantPool.beautifyTypeName(this.clasS.name) + ":::" + this.nameAndType;
            }
        }
    }

    /**
     * Representation of a CONSTANT_String_info entry.
     */
    public static
    class ConstantStringInfo
    implements ConstantClassOrFloatOrIntegerOrStringInfo, ConstantDoubleOrFloatOrIntegerOrLongOrStringInfo {

        /**
         * {@code CONSTANT_String_info.string_index}, see JVMS7 4.4.3
         */
        public final String string;

        public
        ConstantStringInfo(String string) { this.string = string; }

        @Override public int
        size() { return 1; }

        @Override public String
        toString() { return ConstantPool.stringToJavaLiteral(this.string); }
    }

    /**
     * Representation of a CONSTANT_Integer_info entry.
     */
    public static
    class ConstantIntegerInfo
    implements ConstantClassOrFloatOrIntegerOrStringInfo, ConstantDoubleOrFloatOrIntegerOrLongOrStringInfo, ConstantDoubleOrFloatOrIntegerOrLongInfo { // SUPPRESS CHECKSTYLE LineLength

        /**
         * {@code CONSTANT_Integer_info.bytes}, see JVMS7 4.4.4
         */
        public final int bytes;

        public
        ConstantIntegerInfo(int bytes) { this.bytes = bytes; }

        @Override public int
        size() { return 1; }

        @Override public String
        toString() { return Integer.toString(this.bytes); }
    }

    /**
     * Representation of a CONSTANT_Float_info entry.
     */
    public static
    class ConstantFloatInfo implements ConstantClassOrFloatOrIntegerOrStringInfo, ConstantDoubleOrFloatOrIntegerOrLongOrStringInfo, ConstantDoubleOrFloatOrIntegerOrLongInfo { // SUPPRESS CHECKSTYLE LineLength

        /**
         * {@code CONSTANT_Float_info.bytes}, see JVMS7 4.4.4
         */
        public final float bytes;

        public
        ConstantFloatInfo(float bytes) { this.bytes = bytes; }

        @Override public int
        size() { return 1; }

        @Override public String
        toString() { return this.bytes + "F"; }
    }

    /**
     * Representation of a CONSTANT_Long_info entry.
     */
    public static
    class ConstantLongInfo implements ConstantDoubleOrFloatOrIntegerOrLongOrStringInfo, ConstantDoubleOrLongInfo, ConstantDoubleOrFloatOrIntegerOrLongInfo { // SUPPRESS CHECKSTYLE LineLength

        /**
         * {@code CONSTANT_Long_info.bytes}, see JVMS7 4.4.5
         */
        public final long bytes;

        public ConstantLongInfo(long bytes) { this.bytes = bytes; }

        @Override public int
        size() { return 2; }

        @Override public String
        toString() { return this.bytes + "L"; }
    }

    /**
     * Representation of a CONSTANT_Double_info entry.
     */
    public static
    class ConstantDoubleInfo implements ConstantDoubleOrFloatOrIntegerOrLongOrStringInfo, ConstantDoubleOrLongInfo, ConstantDoubleOrFloatOrIntegerOrLongInfo { // SUPPRESS CHECKSTYLE LineLength

        /**
         * {@code CONSTANT_Double_info.bytes}, see JVMS7 4.4.5
         */
        public final double bytes;

        public ConstantDoubleInfo(double bytes) { this.bytes = bytes; }

        @Override public int
        size() { return 2; }

        @Override public String
        toString() { return this.bytes + "D"; }
    }

    /**
     * Representation of a CONSTANT_NameAndType_info entry.
     */
    public
    class ConstantNameAndTypeInfo implements ConstantPoolEntry {

        /**
         * {@code CONSTANT_NameAndType_info.name_index}, see JVMS7 4.4.6
         */
        public final ConstantUtf8Info name;

        /**
         * {@code CONSTANT_NameAndType_info.descriptor_index}, see JVMS7 4.4.6
         */
        public final ConstantUtf8Info descriptor;

        public
        ConstantNameAndTypeInfo(ConstantUtf8Info name, ConstantUtf8Info descriptor) {
            this.name       = name;
            this.descriptor = descriptor;
        }

        @Override public int
        size() { return 1; }

        @Override public String
        toString() {
            try {
                return (
                    this.descriptor.bytes.indexOf('(') == -1
                    ? ConstantPool.this.signatureParser.decodeFieldDescriptor(this.descriptor.bytes) + " " + this.name.bytes // SUPPRESS CHECKSTYLE LineLength
                    : this.name.bytes + ConstantPool.this.signatureParser.decodeMethodDescriptor(this.descriptor.bytes)
                );
            } catch (SignatureException e) {
                return this.name.bytes + this.descriptor.bytes;
            }
        }
    }

    /**
     * Representation of a CONSTANT_Utf8_info entry.
     */
    public static
    class ConstantUtf8Info implements ConstantPoolEntry {

        /**
         * {@code CONSTANT_Utf8_info.bytes}, see JVMS7 4.4.7
         */
        public final String bytes;

        public
        ConstantUtf8Info(String bytes) { this.bytes = bytes; }

        @Override public int
        size() { return 1; }

        @Override public String
        toString() { return '"' + this.bytes + '"'; }
    }

    /**
     * Representation of a CONSTANT_MethodHandle_info entry, see JVMS8 4.4.8.
     */
    public static
    class ConstantMethodHandleInfo implements ConstantPoolEntry {

        private final short             referenceKind;
        private final ConstantPoolEntry reference;

        public
        ConstantMethodHandleInfo(byte referenceKind, ConstantPoolEntry reference) {
            this.referenceKind = referenceKind;
            this.reference     = reference;
        }

        @Override public int
        size() { return 1; }

        @Override public String
        toString() {
            return (
                this.referenceKind == 1 ? "REF_getField"         :
                this.referenceKind == 2 ? "REF_getStatic"        :
                this.referenceKind == 3 ? "REF_putField"         :
                this.referenceKind == 4 ? "REF_putStatic"        :
                this.referenceKind == 5 ? "REF_invokeVirtual"    :
                this.referenceKind == 6 ? "REF_invokeStatic"     :
                this.referenceKind == 7 ? "REF_invokeSpecial"    :
                this.referenceKind == 8 ? "REF_newInvokeSpecial" :
                this.referenceKind == 9 ? "REF_invokeInterface"  :
                "REF_??? (" + this.referenceKind + ")"
            ) + ":" + this.reference.toString(); }
    }

    /**
     * Representation of a CONSTANT_MethodType_info entry.
     */
    public
    class ConstantMethodTypeInfo implements ConstantPoolEntry {

        /**
         * {@code CONSTANT_MethodType_info.bytes}, see JVMS8 4.4.9
         */
        public final ConstantUtf8Info descriptor;

        public
        ConstantMethodTypeInfo(ConstantUtf8Info descriptor) {
            this.descriptor = descriptor;
        }

        @Override public int
        size() { return 1; }

        @Override public String
        toString() {
            try {
                return ConstantPool.this.signatureParser.decodeMethodDescriptor(this.descriptor.bytes).toString();
            } catch (SignatureException e) {
                return this.descriptor.bytes;
            }
        }
    }

    /**
     * Representation of a CONSTANT_InvokeDynamic_info entry.
     */
    public static
    class ConstantInvokeDynamicInfo implements ConstantPoolEntry {

        /**
         * {@code CONSTANT_InvokeDynamic_info.bytes}, see JVMS8 4.4.10
         */
        public final short bootstrapMethodAttrIndex;

        /**
         * {@code CONSTANT_InvokeDynamic_info.name_and_type_index}, see JVMS8 4.4.10
         */
        public final ConstantNameAndTypeInfo nameAndType;

        public
        ConstantInvokeDynamicInfo(short bootstrapMethodAttrIndex, ConstantNameAndTypeInfo nameAndType) {
            this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
            this.nameAndType              = nameAndType;
        }

        @Override public int
        size() { return 1; }

        @Override public String
        toString() { return this.bootstrapMethodAttrIndex + ":" + this.nameAndType.name.bytes; }
    }

    /**
     * Representation of a CONSTANT_Module_info entry.
     *
     * @since Java 9
     */
    public
    class ConstantModuleInfo implements ConstantPoolEntry {

        /**
         * {@code CONSTANT_Module_info.name_index}, see JVMS11 4.4.11
         */
        public final ConstantUtf8Info name;

        public
        ConstantModuleInfo(ConstantUtf8Info name) { this.name = name; }

        @Override public int
        size() { return 1; }

        @Override public String
        toString() { return this.name.bytes; }
    }

    /**
     * Representation of a CONSTANT_Package_info entry.
     *
     * @since Java 9
     */
    public
    class ConstantPackageInfo implements ConstantPoolEntry {

        /**
         * {@code CONSTANT_Package_info.name_index}, see JVMS11 4.4.12
         */
        public final ConstantUtf8Info name;

        public
        ConstantPackageInfo(ConstantUtf8Info name) { this.name = name; }

        @Override public int
        size() { return 1; }

        @Override public String
        toString() { return this.name.bytes; }
    }

    /**
     * A throw-in interface for convenient access to class-or-float-or-integer-or-string operands (for opcodes {@code
     * ldc} and {@code ldc_w}).
     */
    public
    interface ConstantClassOrFloatOrIntegerOrStringInfo extends ConstantPoolEntry {}

    /**
     * A throw-in interface for convenient access to double-or-float-or-integer-or-long operands (for element values
     * in annotations).
     */
    public
    interface ConstantDoubleOrFloatOrIntegerOrLongInfo extends ConstantPoolEntry {}

    /**
     * A throw-in interface for convenient access to double-or-float-or-integer-or-long-or-string operands (for the
     * {@code ConstantValue} attribute).
     */
    public
    interface ConstantDoubleOrFloatOrIntegerOrLongOrStringInfo extends ConstantPoolEntry {}

    /**
     * A throw-in interface for convenient access to double-or-long operands (for opcode {@code ldc2_w}).
     */
    public
    interface ConstantDoubleOrLongInfo extends ConstantPoolEntry {}

    private SignatureParser signatureParser;

    /**
     * The entries of this pool, as read from a class file by {@link #ConstantPool}.
     */
    final ConstantPoolEntry[] entries;

    /**
     * Reads a constant pool from the given {@link InputStream}. Afterwards, entries can be retrieved by invoking
     * the {@code getConstant*Info()} method family.
     *
     * @throws ClassCastException             An entry has the "wrong" type
     * @throws NullPointerException           An "unusable" entry (the magic "zero" entry and the entries after a LONG
     *                                        or DOUBLE entry) is referenced
     * @throws ArrayIndexOutOfBoundsException An index is too small or to great
     */
    public
    ConstantPool(final DataInputStream dis, SignatureParser signatureParser) throws IOException {

        this.signatureParser = signatureParser;

        final int count = dis.readUnsignedShort();

        // Read the entries into a temporary data structure - this is necessary because there may be forward
        // references.

        /***/
        abstract
        class RawEntry {

            /**
             * Creates a 'cooked' entry from a 'raw' entry.
             */
            abstract ConstantPoolEntry
            cook();

            /**
             * Returns the {@link ConstantClassInfo} entry with the given <var>index</var>.
             */
            ConstantClassInfo
            getConstantClassInfo(short index) { return (ConstantClassInfo) this.get(index); }

            /**
             * Returns the {@link ConstantNameAndTypeInfo} entry with the given <var>index</var>.
             */
            ConstantNameAndTypeInfo
            getConstantNameAndTypeInfo(short index) { return (ConstantNameAndTypeInfo) this.get(index); }

            /**
             * Returns the {@link ConstantUtf8Info} entry with the given <var>index</var>.
             */
            ConstantUtf8Info
            getConstantUtf8Info(short index) { return (ConstantUtf8Info) this.get(index); }

            /**
             * Returns the {@link ConstantPoolInfo} entry with the given <var>index</var>.
             */
            abstract ConstantPoolEntry
            get(short index);
        }
        final RawEntry[] rawEntries = new RawEntry[count];

        /**
         * Must declare a second 'RawEntry' class, because it references the local variable 'rawEntries'.
         */
        abstract
        class RawEntry2 extends RawEntry {

            @Override ConstantPoolEntry
            get(short index) {
                if (ConstantPool.this.entries[0xffff & index] == null) {
                    ConstantPool.this.entries[0xffff & index] = new ConstantPoolEntry() {
                        @Override public int              size()     { throw new AssertionError(); }
                        @Override @Nullable public String toString() { return null;                }
                    }; // To prevent recursion.
                    ConstantPool.this.entries[0xffff & index] = rawEntries[0xffff & index].cook();
                }
                return ConstantPool.this.entries[0xffff & index];
            }
        }

        for (int i = 1; i < count;) {
            final int idx = i;

            RawEntry re;
            byte     tag = dis.readByte();
            switch (tag) {

            case 7:  // CONSTANT_Class_info
                re = new RawEntry2() {

                    final short nameIndex = dis.readShort();

                    @Override ConstantPoolEntry
                    cook() {
                        return new ConstantClassInfo(this.getConstantUtf8Info(this.nameIndex).bytes.replace('/', '.'));
                    }
                };
                i++;
                break;

            case 9:  // CONSTANT_Fieldref_info
                re = new RawEntry2() {

                    final short classIndex       = dis.readShort();
                    final short nameAndTypeIndex = dis.readShort();

                    @Override ConstantPoolEntry
                    cook() {
                        return new ConstantFieldrefInfo(
                            this.getConstantClassInfo(this.classIndex),
                            this.getConstantNameAndTypeInfo(this.nameAndTypeIndex)
                        );
                    }
                };
                i++;
                break;

            case 10: // CONSTANT_Methodref_info
                re = new RawEntry2() {

                    final short classIndex       = dis.readShort();
                    final short nameAndTypeIndex = dis.readShort();

                    @Override ConstantPoolEntry
                    cook() {
                        return new ConstantMethodrefInfo(
                            this.getConstantClassInfo(this.classIndex),
                            this.getConstantNameAndTypeInfo(this.nameAndTypeIndex)
                        );
                    }
                };
                i++;
                break;

            case 11: // CONSTANT_InterfaceMethodref_info
                re = new RawEntry2() {

                    final short classIndex       = dis.readShort();
                    final short nameAndTypeIndex = dis.readShort();

                    @Override ConstantPoolEntry
                    cook() {
                        return new ConstantInterfaceMethodrefInfo(
                            this.getConstantClassInfo(this.classIndex),
                            this.getConstantNameAndTypeInfo(this.nameAndTypeIndex)
                        );
                    }
                };
                i++;
                break;

            case 8:  // CONSTANT_String_info
                re = new RawEntry2() {

                    final short stringIndex = dis.readShort();

                    @Override ConstantPoolEntry
                    cook() { return new ConstantStringInfo(this.getConstantUtf8Info(this.stringIndex).bytes); }
                };
                i++;
                break;

            case 3:  // CONSTANT_Integer_info
                re = new RawEntry2() {

                    final int byteS = dis.readInt();

                    @Override ConstantPoolEntry
                    cook() { return new ConstantIntegerInfo(this.byteS); }
                };
                i++;
                break;

            case 4:  // CONSTANT_Float_info
                re = new RawEntry2() {

                    final float byteS = dis.readFloat();

                    @Override ConstantPoolEntry
                    cook() { return new ConstantFloatInfo(this.byteS); }
                };
                i++;
                break;

            case 5:  // CONSTANT_Long_info
                re = new RawEntry2() {

                    final long bytes = dis.readLong();

                    @Override ConstantPoolEntry
                    cook() { return new ConstantLongInfo(this.bytes); }
                };
                i += 2;
                break;

            case 6:  // CONSTANT_Double_info
                re = new RawEntry2() {

                    final double bytes = dis.readDouble();

                    @Override ConstantPoolEntry
                    cook() { return new ConstantDoubleInfo(this.bytes); }
                };
                i += 2;
                break;

            case 12: // CONSTANT_NameAndType_info
                re = new RawEntry2() {

                    final short nameIndex       = dis.readShort();
                    final short descriptorIndex = dis.readShort();

                    @Override ConstantPoolEntry
                    cook() {
                        return new ConstantNameAndTypeInfo(
                            this.getConstantUtf8Info(this.nameIndex),
                            this.getConstantUtf8Info(this.descriptorIndex)
                        );
                    }
                };
                i++;
                break;

            case 1:  // CONSTANT_Utf8_info
                re = new RawEntry2() {

                    final String bytes = dis.readUTF();

                    @Override ConstantPoolEntry
                    cook() { return new ConstantUtf8Info(this.bytes); }
                };
                i++;
                break;

            case 15: // CONSTANT_MethodHandle_info
                re = new RawEntry2() {

                    final byte  referenceKind  = dis.readByte();
                    final short referenceIndex = dis.readShort();

                    @Override ConstantPoolEntry
                    cook() { return new ConstantMethodHandleInfo(this.referenceKind, this.get(this.referenceIndex)); }
                };
                i++;
                break;

            case 16: // CONSTANT_MethodType_info
                re = new RawEntry2() {

                    final short descriptorIndex = dis.readShort();

                    @Override ConstantPoolEntry
                    cook() { return new ConstantMethodTypeInfo(this.getConstantUtf8Info(this.descriptorIndex)); }
                };
                i++;
                break;

            case 18: // CONSTANT_InvokeDynamic_info
                re = new RawEntry2() {

                    final short bootstrapMethodAttrIndex = dis.readShort();
                    final short nameAndTypeIndex         = dis.readShort();

                    @Override ConstantPoolEntry
                    cook() {
                        return new ConstantInvokeDynamicInfo(
                            this.bootstrapMethodAttrIndex,
                            this.getConstantNameAndTypeInfo(this.nameAndTypeIndex)
                        );
                    }
                };
                i++;
                break;

            case 19: // CONSTANT_Module_info
                re = new RawEntry2() {

                    final short nameIndex = dis.readShort();

                    @Override ConstantPoolEntry
                    cook() { return new ConstantModuleInfo(this.getConstantUtf8Info(this.nameIndex)); }
                };
                i++;
                break;

            case 20: // CONSTANT_Package_info
                re = new RawEntry2() {

                    final short nameIndex = dis.readShort();

                    @Override ConstantPoolEntry
                    cook() { return new ConstantPackageInfo(this.getConstantUtf8Info(this.nameIndex)); }
                };
                i++;
                break;

            default:
                throw new RuntimeException("Invalid cp_info tag " + (int) tag + " on entry #" + i + " of " + count);
            }

            rawEntries[idx] = re;
        }

        this.entries = new ConstantPoolEntry[count];
        for (int i = 0; i < count; ++i) {
            try {
                if (this.entries[i] == null && rawEntries[i] != null) this.entries[i] = rawEntries[i].cook();
            } catch (RuntimeException re) {
                throw new RuntimeException("Cooking CP entry #" + i + " of " + count + ": " + re.getMessage(), re);
            }
        }
    }

    public static String
    beautifyTypeName(String typeName) {

        // Strip redundant prefixes from the type name.
        for (String packageNamePrefix : new String[] { "java.lang.", }) {
            if (typeName.startsWith(packageNamePrefix)) {
                return typeName.substring(packageNamePrefix.length());
            }
        }

        return typeName;
    }

    /**
     * Sets a custom {@link SignatureParser}; that influences how the various descriptors and signatures in the class
     * file are parsed and converted to human-readable strings by {@link Object#toString()}.
     */
    public void
    setSignatureParser(SignatureParser signatureParser) {
        this.signatureParser = signatureParser;
    }

    /**
     * Checks that the indexed constant pool entry has the given <var>clasS</var>, and returns it.
     */
    public <T extends ConstantPoolEntry> T
    get(short index, Class<T> clasS) {
        int ii = 0xffff & index;
        if (ii == 0 || ii >= this.entries.length) {
            throw new IllegalArgumentException(
                "Illegal constant pool index " + ii + " - only 1..." + (this.entries.length - 1) + " allowed"
            );
        }

        ConstantPoolEntry e = this.entries[ii];
        if (e == null) throw new NullPointerException("Unusable CP entry " + index);
        if (!clasS.isAssignableFrom(e.getClass())) {
            throw new RuntimeException(
                "CP entry #" + index + " is a " + e.getClass().getSimpleName() + ", not a " + clasS.getSimpleName()
            );
        }

        @SuppressWarnings("unchecked") T result = (T) e;
        return result;
    }

    /**
     * @return {@code null} iff {@code index == 0}
     */
    @Nullable public <T extends ConstantPoolEntry> T
    getOptional(short index, Class<T> clasS) {
        if (index == 0) return null;

        int ii = 0xffff & index;
        if (ii >= this.entries.length) {
            throw new IllegalArgumentException(
                "Illegal constant pool index " + ii + " - only 0..." + (this.entries.length - 1) + " allowed"
            );
        }

        ConstantPoolEntry e = this.entries[ii];
        if (e == null) throw new NullPointerException("Unusable CP entry " + index);
        if (!clasS.isAssignableFrom(e.getClass())) {
            throw new RuntimeException(
                "CP entry #" + index + " is a " + e.getClass().getSimpleName() + ", not a " + clasS.getSimpleName()
            );
        }

        @SuppressWarnings("unchecked") T result = (T) e;
        return result;
    }

    /**
     * @return The number of entries in this {@link ConstantPool}
     */
    public int
    getSize() { return this.entries.length; }

    /**
     * Converts a given string into a Java literal by enclosing it in double quotes and escaping any special
     * characters.
     */
    public static String
    stringToJavaLiteral(String s) {
        for (int i = 0; i < s.length();) {
            char c   = s.charAt(i);
            int  idx = "\r\n\"\t\b".indexOf(c);
            if (idx == -1) {
                ++i;
            } else {
                s = s.substring(0, i) + '\\' + "rn\"tb".charAt(idx) + s.substring(i + 1);
                i += 2;
            }
            if (i >= 80) return '"' + s.substring(0, i) + "\"...";
        }
        return '"' + s + '"';
    }
}
