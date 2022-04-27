
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

import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.ABSTRACT;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.ANNOTATION;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.ENUM;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.FINAL;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.INTERFACE;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.NATIVE;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.PRIVATE;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.PROTECTED;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.PUBLIC;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.STATIC;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.STRICT;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.SYNCHRONIZED;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.SYNTHETIC;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.TRANSIENT;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.VOLATILE;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.unkrig.commons.nullanalysis.Nullable;
import de.unkrig.jdisasm.ConstantPool.ConstantClassInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantDoubleOrFloatOrIntegerOrLongInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantDoubleOrFloatOrIntegerOrLongOrStringInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantMethodHandleInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantNameAndTypeInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantPackageInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantPoolEntry;
import de.unkrig.jdisasm.ConstantPool.ConstantUtf8Info;
import de.unkrig.jdisasm.SignatureParser.SignatureException;

/**
 * Representation of a Java class file.
 */
public
class ClassFile {

    /**
     * The <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1-200-B">minor and major
     * version numbers of this class file</a>.
     */
    public short minorVersion, majorVersion;

    /**
     * The <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1-200-C">number of entries in
     * the constant pool table plus one</a>.
     */
    public ConstantPool constantPool;

    /**
     * A <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1-200-E">mask of flags used to
     * denote access permissions to and properties of this class or interface</a>.
     */
    public AccessFlags accessFlags;

    /**
     * The fully qualified (dot-separated) name of this type.
     */
    public String thisClassName;

    /**
     * The fully qualified name of the package that declares this type, including a trailing ".".
     */
    public String thisClassPackageNamePrefix;

    /**
     * The simple (unqualified) name of this type.
     */
    public String simpleThisClassName;

    /**
     * The fully qualified (dot-separated) name of the superclass of this type; "java.lang.Object" iff this type is an
     * interface; {@code null} iff this type is {@link Object}.
     */
    @Nullable public String superClassName;

    /**
     * Fully qualified (dot-separated) names of the interfaces that this type implements.
     */
    public final List<String> interfaceNames = new ArrayList<String>();

    /**
     * The <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1-200-K">complete description
     * of a field in this class or interface</a>. The fields table includes only those fields that are declared by this
     * class or interface. It does not include items representing fields that are inherited from superclasses or
     * superinterfaces.
     */
    public final List<Field> fields = new ArrayList<Field>();

    /**
     * The <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1-200-M">complete description
     * of a method in this class or interface</a>. If neither of the ACC_NATIVE and ACC_ABSTRACT flags are set in
     * {@link Method#accessFlags}, the Java Virtual Machine instructions implementing the method are also supplied.
     * <p>
     *   The {@link Method} structures represent all methods declared by this class or interface type, including
     *   instance methods, class methods, instance initialization methods, and any class or interface initialization
     *   method. The methods table does not include items representing methods that are inherited from superclasses or
     *   superinterfaces.
     * </p>
     */
    public final List<Method> methods = new ArrayList<Method>();

    /**
     * The optional {@code BootstrapMethodsAttribute} of this class or interface.
     */
    @Nullable public BootstrapMethodsAttribute bootstrapMethodsAttribute;

    /**
     * The optional {@code DeprecatedAttribute} of this class or interface.
     */
    @Nullable public DeprecatedAttribute deprecatedAttribute;

    /**
     * The optional {@code EnclosingMethodAttribute} of this class or interface.
     */
    @Nullable public EnclosingMethodAttribute enclosingMethodAttribute;

    /**
     * The optional {@code InnerClassesAttribute} of this class or interface.
     */
    @Nullable public InnerClassesAttribute innerClassesAttribute;

    /**
     * The optional {@code ModulePackagesAttribute} of this class or interface.
     */
    @Nullable public ModulePackagesAttribute modulePackagesAttribute;

    /**
     * The optional {@code RuntimeInvisibleAnnotations} attribute of this class or interface.
     */
    @Nullable public RuntimeInvisibleAnnotationsAttribute runtimeInvisibleAnnotationsAttribute;

    /**
     * The optional {@code RuntimeVisibleAnnotations} attribute of this class or interface.
     */
    @Nullable public RuntimeVisibleAnnotationsAttribute runtimeVisibleAnnotationsAttribute;

    /**
     * The optional {@code SignatureAttribute} of this class or interface.
     */
    @Nullable public SignatureAttribute signatureAttribute;

    /**
     * The optional {@code SourceFileAttribute} of this class or interface.
     */
    @Nullable public SourceFileAttribute sourceFileAttribute;

    /**
     * The optional {@code SyntheticAttribute} of this class or interface.
     */
    @Nullable public SyntheticAttribute syntheticAttribute;

    /**
     * All attributes of this class.
     */
    public final List<Attribute> allAttributes = new ArrayList<Attribute>();

    /**
     * All unprocessed attributes of this class.
     */
    public final List<Attribute> unprocessedAttributes = new ArrayList<Attribute>();

    private SignatureParser signatureParser = new SignatureParser();

    /**
     * Abstraction for a set of "access flags".
     */
    public static
    class AccessFlags {

        enum FlagType { // SUPPRESS CHECKSTYLE Javadoc:16

            PUBLIC      (0x0001),
            PRIVATE     (0x0002),
            PROTECTED   (0x0004),
            STATIC      (0x0008),
            FINAL       (0x0010),
            SYNCHRONIZED(0x0020),
            VOLATILE    (0x0040), BRIDGE(0x0040),  // <= Same values!
            TRANSIENT   (0x0080), VARARGS(0x0080), // <= Same values!
            NATIVE      (0x0100),
            INTERFACE   (0x0200),
            ABSTRACT    (0x0400),
            STRICT      (0x0800),
            SYNTHETIC   (0x1000),
            ANNOTATION  (0x2000),
            ENUM        (0x4000),
            ;

            private final int value;

            FlagType(int value) { this.value = value; }
        }

        private final int value;

        public
        AccessFlags(int value) { this.value = value; }

        public boolean
        is(FlagType ft) { return (this.value & ft.value) != 0; }

        /**
         * @return Whether one or more of the <var>flagTypes</var> is set
         */
        public boolean
        isAny(FlagType... flagTypes) {
            for (FlagType ft : flagTypes) {
                if (this.is(ft)) return true;
            }
            return false;
        }

        public AccessFlags
        add(FlagType ft) { return (this.value & ft.value) != 0 ? this : new AccessFlags(this.value | ft.value); }

        public AccessFlags
        remove(FlagType ft) { return (this.value & ft.value) == 0 ? this : new AccessFlags(this.value & ~ft.value); }

        /**
         * @return A series of words, in canonical order, separated with one space, and with one trailing space
         */
        @Override public String
        toString() {

            StringBuilder sb = new StringBuilder();

            if (this.is(PUBLIC))       sb.append("public ");
            if (this.is(PRIVATE))      sb.append("private ");
            if (this.is(PROTECTED))    sb.append("protected ");

            if (this.is(ABSTRACT))     sb.append("abstract ");
            if (this.is(STATIC))       sb.append("static ");
            if (this.is(FINAL))        sb.append("final ");
            if (this.is(TRANSIENT))    sb.append("transient ");    // <= In favor of VARARGS
            if (this.is(VOLATILE))     sb.append("volatile ");     // <= In favor of BRIDGE
            if (this.is(SYNCHRONIZED)) sb.append("synchronized ");
            if (this.is(NATIVE))       sb.append("native ");
            if (this.is(STRICT))       sb.append("strictfp ");
            if (this.is(SYNTHETIC))    sb.append("synthetic ");

            if (this.is(ANNOTATION))   sb.append("@");
            if (this.is(INTERFACE))    sb.append("interface ");
            if (this.is(ENUM))         sb.append("enum ");

            return sb.toString();
        }
    }

    public
    ClassFile(DataInputStream dis) throws IOException {

        // Magic number.
        {
            int magic = dis.readInt();
            if (magic != 0xcafebabe) {
                throw new ClassFileFormatException("Wrong magic number 0x" + Integer.toHexString(magic));
            }
        }

        // JDK version.
        this.minorVersion = dis.readShort();
        this.majorVersion = dis.readShort();

        // Load constant pool.
        this.constantPool = new ConstantPool(dis, this.signatureParser);

        // Access flags.
        this.accessFlags = new AccessFlags(dis.readShort());

        try {

	        // Class name.
	        this.thisClassName = this.constantPool.get(dis.readShort(), ConstantClassInfo.class).toString();
	        {
	            int idx = this.thisClassName.lastIndexOf('.') + 1;
	            this.thisClassPackageNamePrefix = this.thisClassName.substring(0, idx);
	            this.simpleThisClassName        = this.thisClassName.substring(idx);
	        }

	        // Superclass.
	        {
	            ConstantClassInfo superclassCci = this.constantPool.getOptional(dis.readShort(), ConstantClassInfo.class);
	            this.superClassName = superclassCci == null ? null : superclassCci.toString();
	        }

	        // Implemented interfaces.
	        for (short i = dis.readShort(); i > 0; --i) {
	            this.interfaceNames.add(this.constantPool.get(dis.readShort(), ConstantClassInfo.class).toString());
	        }

	        // Fields.
	        {
	            short n = dis.readShort();
	            for (short i = 0; i < n; i++) {
	                try {
	                    this.fields.add(new Field(dis));
	                } catch (IOException ioe) {
	                    IOException ioe2 = new IOException("Reading field #" + i + " of " + n + ": " + ioe.getMessage());
	                    ioe2.initCause(ioe);
	                    throw ioe2; // SUPPRESS CHECKSTYLE AvoidHidingCause
	                } catch (RuntimeException re) {
	                    throw new RuntimeException("Reading field #" + i + " of " + n + ": " + re.getMessage(), re);
	                }
	            }
	        }

	        // Methods.
	        {
	            short n = dis.readShort();
	            for (short i = 0; i < n; i++) {
	                try {
	                    this.methods.add(new Method(dis));
	                } catch (IOException ioe) {
	                    IOException ioe2 = new IOException("Reading method #" + i + " of " + n + ": " + ioe.getMessage());
	                    ioe2.initCause(ioe);
	                    throw ioe2; // SUPPRESS CHECKSTYLE AvoidHidingCause
	                } catch (RuntimeException re) {
	                    throw new RuntimeException((
	                        "Class \""
	                        + this.thisClassName
	                        + "\": Reading method #"
	                        + i
	                        + " of "
	                        + n
	                        + ": "
	                        + re.getMessage()
	                    ), re);
	                }
	            }
	        }

	        // Class attributes.
	        this.readAttributes(dis, new AbstractAttributeVisitor() {

	            @Override public void
	            visit(BootstrapMethodsAttribute bma) {
	                ClassFile.this.bootstrapMethodsAttribute = bma;
	                ClassFile.this.allAttributes.add(bma);
	            }

	            @Override public void
	            visit(DeprecatedAttribute da) {
	                ClassFile.this.deprecatedAttribute = da;
	                ClassFile.this.allAttributes.add(da);
	            }

	            @Override public void
	            visit(EnclosingMethodAttribute ema) {
	                ClassFile.this.enclosingMethodAttribute = ema;
	                ClassFile.this.allAttributes.add(ema);
	            }

	            @Override public void
	            visit(InnerClassesAttribute ica) {
	                ClassFile.this.innerClassesAttribute = ica;
	                ClassFile.this.allAttributes.add(ica);
	            }

	            @Override public void
	            visit(ModulePackagesAttribute mpa) {
	                ClassFile.this.modulePackagesAttribute = mpa;
	                ClassFile.this.allAttributes.add(mpa);
	            }

	            @Override public void
	            visit(RuntimeInvisibleAnnotationsAttribute riaa) {
	                ClassFile.this.runtimeInvisibleAnnotationsAttribute = riaa;
	                ClassFile.this.allAttributes.add(riaa);
	            }

	            @Override public void
	            visit(RuntimeVisibleAnnotationsAttribute rvaa) {
	                ClassFile.this.runtimeVisibleAnnotationsAttribute = rvaa;
	                ClassFile.this.allAttributes.add(rvaa);
	            }

	            @Override public void
	            visit(SignatureAttribute sa) {
	                ClassFile.this.signatureAttribute = sa;
	                ClassFile.this.allAttributes.add(sa);
	            }

	            @Override public void
	            visit(SourceFileAttribute sfa) {
	                ClassFile.this.sourceFileAttribute = sfa;
	                ClassFile.this.allAttributes.add(sfa);
	            }

	            @Override public void
	            visit(SyntheticAttribute sa) {
	                ClassFile.this.syntheticAttribute = sa;
	                ClassFile.this.allAttributes.add(sa);
	            }

	            @Override public void
	            visitOther(Attribute a) {
	                ClassFile.this.allAttributes.add(a);
	                ClassFile.this.unprocessedAttributes.add(a);
	            }
	        });
        } catch (RuntimeException re) {
        	throw new RuntimeException("Class \"" + this.thisClassName + "\": " + re.getMessage(), re);
        }
    }

    /**
     * Sets a custom {@link SignatureParser}; that influences how the various descriptors and signatures in the class
     * file are parsed and converted to human-readable strings by {@link Object#toString()}.
     */
    public void
    setSignatureParser(SignatureParser signatureParser) {
        this.signatureParser = signatureParser;
        this.constantPool.setSignatureParser(signatureParser);
    }

    /**
     * @return The major/minor version of this class file translated into a human-readable JDK name
     */
    public String
    getJdkName() {
        switch (this.majorVersion) {
        case 51:
            return "J2SE 7";
        case 50:
            return "J2SE 6.0";
        case 49:
            return "J2SE 5.0";
        case 48:
            return "JDK 1.4";
        case 47:
            return "JDK 1.3";
        case 46:
            return "JDK 1.2";
        case 45:
            return "JDK 1.1";
        default:
            return "Java " + (this.majorVersion - 44);
        }
    }

    /**
     * Representation of a field description in a Java class file.
     */
    public
    class Field {

        // SUPPRESS CHECKSTYLE JavadocVariable:3
        public AccessFlags accessFlags;
        public String      name;
        public String      descriptor;

        // SUPPRESS CHECKSTYLE JavadocVariable:8
        public final List<Attribute>                          allAttributes         = new ArrayList<Attribute>();
        public final List<Attribute>                          unprocessedAttributes = new ArrayList<Attribute>();
        @Nullable public ConstantValueAttribute               constantValueAttribute;
        @Nullable public DeprecatedAttribute                  deprecatedAttribute;
        @Nullable public RuntimeInvisibleAnnotationsAttribute runtimeInvisibleAnnotationsAttribute;
        @Nullable public RuntimeVisibleAnnotationsAttribute   runtimeVisibleAnnotationsAttribute;
        @Nullable public SignatureAttribute                   signatureAttribute;
        @Nullable public SyntheticAttribute                   syntheticAttribute;

        public
        Field(DataInputStream dis) throws IOException {
            this.accessFlags = new AccessFlags(dis.readShort());
            this.name        = ClassFile.this.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;
            this.descriptor  = ClassFile.this.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;

            // Read field attributes.
            ClassFile.this.readAttributes(dis, new AbstractAttributeVisitor() {

                @Override public void
                visit(ConstantValueAttribute cva) {
                    Field.this.constantValueAttribute = cva;
                    Field.this.allAttributes.add(cva);
                }

                @Override public void
                visit(DeprecatedAttribute da) {
                    Field.this.deprecatedAttribute = da;
                    Field.this.allAttributes.add(da);
                }

                @Override public void
                visit(RuntimeInvisibleAnnotationsAttribute riaa) {
                    Field.this.runtimeInvisibleAnnotationsAttribute = riaa;
                    Field.this.allAttributes.add(riaa);
                }

                @Override public void
                visit(RuntimeVisibleAnnotationsAttribute rvaa) {
                    Field.this.runtimeVisibleAnnotationsAttribute = rvaa;
                    Field.this.allAttributes.add(rvaa);
                }

                @Override public void
                visit(SignatureAttribute sa) {
                    Field.this.signatureAttribute = sa;
                    Field.this.allAttributes.add(sa);
                }

                @Override public void
                visit(SyntheticAttribute sa) {
                    Field.this.syntheticAttribute = sa;
                    Field.this.allAttributes.add(sa);
                }

                @Override public void
                visitOther(Attribute ai) {
                    Field.this.allAttributes.add(ai);
                    Field.this.unprocessedAttributes.add(ai);
                }
            });
        }
    }

    /**
     * Representation of a method in a Java class file.
     */
    public
    class Method {

        // SUPPRESS CHECKSTYLE JavadocVariableCheck:3
        public AccessFlags accessFlags;
        public String      name;
        public String      descriptor;

        // SUPPRESS CHECKSTYLE JavadocVariableCheck:13
        final List<Attribute>                                   allAttributes         = new ArrayList<Attribute>();
        final List<Attribute>                                   unprocessedAttributes = new ArrayList<Attribute>();
        @Nullable AnnotationDefaultAttribute                    annotationDefaultAttribute;
        @Nullable CodeAttribute                                 codeAttribute;
        @Nullable DeprecatedAttribute                           deprecatedAttribute;
        @Nullable ExceptionsAttribute                           exceptionsAttribute;
        @Nullable MethodParametersAttribute                     methodParametersAttribute;
        @Nullable RuntimeInvisibleAnnotationsAttribute          runtimeInvisibleAnnotationsAttribute;
        @Nullable RuntimeInvisibleParameterAnnotationsAttribute runtimeInvisibleParameterAnnotationsAttribute;
        @Nullable RuntimeVisibleAnnotationsAttribute            runtimeVisibleAnnotationsAttribute;
        @Nullable RuntimeVisibleParameterAnnotationsAttribute   runtimeVisibleParameterAnnotationsAttribute;
        @Nullable SignatureAttribute                            signatureAttribute;
        @Nullable SyntheticAttribute                            syntheticAttribute;

        public
        Method(DataInputStream dis) throws IOException {
            this.accessFlags = new AccessFlags(dis.readShort());
            this.name        = ClassFile.this.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;
            this.descriptor  = ClassFile.this.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;

            try {

                // Read method attributes.
                ClassFile.this.readAttributes(dis, new AbstractAttributeVisitor() {

                    @Override public void
                    visit(AnnotationDefaultAttribute ada) {
                        Method.this.annotationDefaultAttribute = ada;
                        Method.this.allAttributes.add(ada);
                    }

                    @Override public void
                    visit(CodeAttribute ca) {
                        Method.this.codeAttribute = ca;
                        Method.this.allAttributes.add(ca);
                    }

                    @Override public void
                    visit(DeprecatedAttribute da) {
                        Method.this.deprecatedAttribute = da;
                        Method.this.allAttributes.add(da);
                    }

                    @Override public void
                    visit(ExceptionsAttribute ea) {
                        Method.this.exceptionsAttribute = ea;
                        Method.this.allAttributes.add(ea);
                    }

                    @Override public void
                    visit(MethodParametersAttribute mpa) {
                        Method.this.methodParametersAttribute = mpa;
                        Method.this.allAttributes.add(mpa);
                    }

                    @Override public void
                    visit(RuntimeInvisibleAnnotationsAttribute riaa) {
                        Method.this.runtimeInvisibleAnnotationsAttribute = riaa;
                        Method.this.allAttributes.add(riaa);
                    }

                    @Override public void
                    visit(RuntimeInvisibleParameterAnnotationsAttribute ripaa) {
                        Method.this.runtimeInvisibleParameterAnnotationsAttribute = ripaa;
                        Method.this.allAttributes.add(ripaa);
                    }

                    @Override public void
                    visit(RuntimeVisibleAnnotationsAttribute rvaa) {
                        Method.this.runtimeVisibleAnnotationsAttribute = rvaa;
                        Method.this.allAttributes.add(rvaa);
                    }

                    @Override public void
                    visit(RuntimeVisibleParameterAnnotationsAttribute rvpaa) {
                        Method.this.runtimeVisibleParameterAnnotationsAttribute = rvpaa;
                        Method.this.allAttributes.add(rvpaa);
                    }

                    @Override public void
                    visit(SignatureAttribute sa) {
                        Method.this.signatureAttribute = sa;
                        Method.this.allAttributes.add(sa);
                    }

                    @Override public void
                    visit(StackMapTableAttribute smta) {

                        // Treat a "StackMapTable" attribute as an "unprocessed attribute", because we don't
                        // disassemble it in-line.
                        super.visit(smta);
                    }

                    @Override public void
                    visit(SyntheticAttribute sa) {
                        Method.this.syntheticAttribute = sa;
                        Method.this.allAttributes.add(sa);
                    }

                    @Override public void
                    visitOther(Attribute ai) {
                        Method.this.allAttributes.add(ai);
                        Method.this.unprocessedAttributes.add(ai);
                    }
                });
            } catch (IOException ioe) {
                IOException ioe2 = new IOException(
                    "Parsing method '" + this.name + "' [" + this.descriptor + "]: " + ioe.getMessage()
                );
                ioe2.initCause(ioe);
                throw ioe2; // SUPPRESS CHECKSTYLE AvoidHidingCause
            } catch (RuntimeException re) {
                throw new RuntimeException(
                    "Parsing method '" + this.name + "' [" + this.descriptor + "]: " + re.getMessage(),
                    re
                );
            }
        }

        public ClassFile
        getClassFile() { return ClassFile.this; }

        /**
         * @return The {@code BootstrapMethods} attribute of the class file
         */
        public BootstrapMethodsAttribute
        getBootstrapMethodsAttribute() {
            BootstrapMethodsAttribute result = ClassFile.this.bootstrapMethodsAttribute;
            if (result == null) throw new RuntimeException("BootstrapMethods attribute missing");
            return result;
        }
    }

    /**
     * Representation of an attribute in a Java class file.
     */
    public
    interface Attribute {

        /**
         * Accepts the <var>visitor</var>.
         */
        void accept(AttributeVisitor visitor);

        /**
         * @return This attribute's name
         */
        String getName();
    }

    /**
     * See JVMS9 4.7.11.
     *
     * @since Java SE 5.0
     */
    public static
    class SourceDebugExtensionAttribute extends UnknownAttribute {

        SourceDebugExtensionAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            super("SourceDebugExtension", dis, cf);
        }
    }

    /**
     * See JVMS9 4.7.20.
     *
     * @since Java SE 8
     */
    public static
    class RuntimeVisibleTypeAnnotationsAttribute extends UnknownAttribute {

        RuntimeVisibleTypeAnnotationsAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            super("RuntimeVisibleTypeAnnotation", dis, cf);
        }
    }

    /**
     * See JVMS9 4.7.21.
     *
     * @since Java SE 8
     */
    public static
    class RuntimeInvisibleTypeAnnotationsAttribute extends UnknownAttribute {

        RuntimeInvisibleTypeAnnotationsAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            super("RuntimeInvisibleTypeAnnotations", dis, cf);
        }
    }

    /**
     * @see   <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.24">JVMS9 4.7.24</a>
     * @since Java SE 8
     */
    public static
    class MethodParametersAttribute implements Attribute {

        /**
         * Representation of an {@code parameter} entry in the {@code MethodParameters_attribute} structure.
         */
        public static
        class Parameter {

            /**
             * The parameter's name as represented by the {@code name_index} field.
             */
            String name;

            /**
             * The parameter's access flags as represented by the {@code access_flags} field (FINAL, SYNTHETIC and/or
             * MANDATED).
             */
            AccessFlags accessFlags;

            public
            Parameter(String name, AccessFlags accessFlags) {
                this.name        = name;
                this.accessFlags = accessFlags;
            }

            @Override public String
            toString() { return this.accessFlags.toString() + this.name; }
        }

        /**
         * The parameters' properties as represented by the {@code parameters} field.
         */
        final List<Parameter> parameters = new ArrayList<Parameter>();

        MethodParametersAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            for (int i = dis.readByte(); i > 0; i--) {
                this.parameters.add(new Parameter(
                    cf.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes,
                    new AccessFlags(dis.readShort())
                ));
            }
        }

        @Override public void
        accept(AttributeVisitor visitor) { visitor.visit(this); }

        @Override public String
        getName() { return "MethodParameters"; }
    }

    /**
     * See JVMS9 4.7.25.
     *
     * @since Java SE 9
     */
    public static
    class ModuleAttribute extends UnknownAttribute {

        ModuleAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            super("Module", dis, cf);
        }
    }

    /**
     * Representation of a {@code ModulePackages} attribute (JVMS 9, 4.7.26).
     *
     * @since Java SE 9
     */
    public static final
    class ModulePackagesAttribute implements Attribute {

        /**
         * The packages of this module.
         */
        public final List<String> packages = new ArrayList<String>();

        ModulePackagesAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            for (int i = dis.readShort(); i > 0; --i) {
                this.packages.add(cf.constantPool.get(dis.readShort(), ConstantPackageInfo.class).name.bytes);
            }
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this);     }
        @Override public String getName()                        { return "ModulePackages"; }
    }

    /**
     * See JVMS9 4.7.27.
     *
     * @since Java SE 9
     */
    public static
    class ModuleMainClassAttribute extends UnknownAttribute {

        ModuleMainClassAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            super("ModuleMainClass", dis, cf);
        }
    }

    /**
     * Reads a set of attributs and has them accept the <var>visitor</var>.
     */
    final void
    readAttributes(DataInputStream dis, AttributeVisitor visitor) throws IOException {
        short n = dis.readShort();
        for (int i = 0; i < n; ++i) {
            try {
                this.readAttribute(dis, visitor);
            } catch (IOException ioe) {
                IOException ioe2 = new IOException("Reading attribute #" + i + " of " + n + ": " + ioe.getMessage());
                ioe2.initCause(ioe);
                throw ioe2; // SUPPRESS CHECKSTYLE AvoidHidingCause
            } catch (RuntimeException re) {
                throw new RuntimeException("Reading attribute #" + i + " of " + n + ": " + re.getMessage(), re);
            }
        }
    }

    private void
    readAttribute(DataInputStream dis, AttributeVisitor visitor) throws IOException {

        String attributeName = this.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;

        try {
            // Read attribute body into byte array and create a DataInputStream.
            ByteArrayInputStream bais;
            {
                int          attributeLength = dis.readInt();
                final byte[] ba              = new byte[attributeLength];
                dis.readFully(ba);
                bais = new ByteArrayInputStream(ba);
            }

            // Parse the attribute body.
            this.readAttributeBody(attributeName, new DataInputStream(bais), visitor);

            // Check for extraneous bytes.
            {
                int av = bais.available();
                if (av > 0) {
                    throw new RuntimeException(av + " extraneous bytes in attribute body");
                }
            }
        } catch (IOException ioe) {
            IOException ioe2 = new IOException("Reading attribute '" + attributeName + "': " + ioe.getMessage());
            ioe2.initCause(ioe);
            throw ioe2; // SUPPRESS CHECKSTYLE AvoidHidingCause
        } catch (RuntimeException re) {
            throw new RuntimeException("Reading attribute \"" + attributeName + "\": " + re.getMessage(), re);
        }
    }

    private void
    readAttributeBody(
        final String          attributeName,
        final DataInputStream dis,
        AttributeVisitor      visitor
    ) throws IOException {

        // Attributes as defined by JVMS8, section 4.7:

        if ("AnnotationDefault".equals(attributeName)) {
            visitor.visit(new AnnotationDefaultAttribute(dis, this));
        } else
        if ("BootstrapMethods".equals(attributeName)) {
            visitor.visit(new BootstrapMethodsAttribute(dis, this));
        } else
        if ("ConstantValue".equals(attributeName)) {
            visitor.visit(new ConstantValueAttribute(dis, this));
        } else
        if ("Code".equals(attributeName)) {
            visitor.visit(new CodeAttribute(dis, this));
        } else
        if ("Deprecated".equals(attributeName)) {
            visitor.visit(new DeprecatedAttribute(dis, this));
        } else
        if ("EnclosingMethod".equals(attributeName)) {
            visitor.visit(new EnclosingMethodAttribute(dis, this));
        } else
        if ("Exceptions".equals(attributeName)) {
            visitor.visit(new ExceptionsAttribute(dis, this));
        } else
        if ("InnerClasses".equals(attributeName)) {
            visitor.visit(new InnerClassesAttribute(dis, this));
        } else
        if ("LineNumberTable".equals(attributeName)) {
            visitor.visit(new LineNumberTableAttribute(dis, this));
        } else
        if ("LocalVariableTable".equals(attributeName)) {
            visitor.visit(new LocalVariableTableAttribute(dis, this));
        } else
        if ("LocalVariableTypeTable".equals(attributeName)) {
            visitor.visit(new LocalVariableTypeTableAttribute(dis, this));
        } else
        if ("MethodParameters".equals(attributeName)) {
            visitor.visit(new MethodParametersAttribute(dis, this));
        } else
        if ("Module".equals(attributeName)) {
            visitor.visit(new ModuleAttribute(dis, this));
        } else
        if ("ModuleMainClass".equals(attributeName)) {
            visitor.visit(new ModuleMainClassAttribute(dis, this));
        } else
        if ("ModulePackages".equals(attributeName)) {
            visitor.visit(new ModulePackagesAttribute(dis, this));
        } else
        if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
            visitor.visit(new RuntimeInvisibleAnnotationsAttribute(dis, this));
        } else
        if ("RuntimeInvisibleParameterAnnotations".equals(attributeName)) {
            visitor.visit(new RuntimeInvisibleParameterAnnotationsAttribute(dis, this));
        } else
        if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
            visitor.visit(new RuntimeInvisibleTypeAnnotationsAttribute(dis, this));
        } else
        if ("RuntimeVisibleAnnotations".equals(attributeName)) {
            visitor.visit(new RuntimeVisibleAnnotationsAttribute(dis, this));
        } else
        if ("RuntimeVisibleParameterAnnotations".equals(attributeName)) {
            visitor.visit(new RuntimeVisibleParameterAnnotationsAttribute(dis, this));
        } else
        if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
            visitor.visit(new RuntimeVisibleTypeAnnotationsAttribute(dis, this));
        } else
        if ("Signature".equals(attributeName)) {
            visitor.visit(new SignatureAttribute(dis, this));
        } else
        if ("SourceDebugExtension".equals(attributeName)) {
            visitor.visit(new SourceDebugExtensionAttribute(dis, this));
        } else
        if ("SourceFile".equals(attributeName)) {
            visitor.visit(new SourceFileAttribute(dis, this));
        } else
        if ("StackMapTable".equals(attributeName)) {
            visitor.visit(new StackMapTableAttribute(dis, this));
        } else
        if ("Synthetic".equals(attributeName)) {
            visitor.visit(new SyntheticAttribute(dis, this));
        } else
        {
            visitor.visit(new UnknownAttribute(attributeName, dis, this));
        }
    }

    /**
     * The visitor for {@link Attribute}.
     */
    public
    interface AttributeVisitor {

        // SUPPRESS CHECKSTYLE JavadocMethod:20
        void visit(AnnotationDefaultAttribute                    ada);
        void visit(CodeAttribute                                 ca);
        void visit(ConstantValueAttribute                        cva);
        void visit(DeprecatedAttribute                           da);
        void visit(EnclosingMethodAttribute                      ema);
        void visit(ExceptionsAttribute                           ea);
        void visit(InnerClassesAttribute                         ica);
        void visit(LineNumberTableAttribute                      lnta);
        void visit(LocalVariableTableAttribute                   lvta);
        void visit(LocalVariableTypeTableAttribute               lvtta);
        void visit(MethodParametersAttribute                     mpa);
        void visit(ModulePackagesAttribute                       mpa);
        void visit(RuntimeInvisibleAnnotationsAttribute          riaa);
        void visit(RuntimeInvisibleParameterAnnotationsAttribute ripaa);
        void visit(RuntimeVisibleAnnotationsAttribute            rvaa);
        void visit(RuntimeVisibleParameterAnnotationsAttribute   rvpaa);
        void visit(SignatureAttribute                            sa);
        void visit(SourceFileAttribute                           sfa);
        void visit(StackMapTableAttribute                        smta);
        void visit(SyntheticAttribute                            sa);
        void visit(BootstrapMethodsAttribute                     bma);

        /**
         * An unknown attribute accepted this visitor.
         */
        void visit(UnknownAttribute unknownAttribute);
    }

    /**
     * Default implementation of the {@link AttributeVisitor}.
     */
    public abstract static
    class AbstractAttributeVisitor implements AttributeVisitor {

        /**
         * Called by the default implementations of the {@code visit(...)} methods.
         */
        public abstract void visitOther(Attribute ai);

        @Override public void visit(BootstrapMethodsAttribute                     bma)   { this.visitOther(bma);   }
        @Override public void visit(AnnotationDefaultAttribute                    ada)   { this.visitOther(ada);   }
        @Override public void visit(CodeAttribute                                 ca)    { this.visitOther(ca);    }
        @Override public void visit(ConstantValueAttribute                        cva)   { this.visitOther(cva);   }
        @Override public void visit(DeprecatedAttribute                           da)    { this.visitOther(da);    }
        @Override public void visit(EnclosingMethodAttribute                      ema)   { this.visitOther(ema);   }
        @Override public void visit(ExceptionsAttribute                           ea)    { this.visitOther(ea);    }
        @Override public void visit(InnerClassesAttribute                         ica)   { this.visitOther(ica);   }
        @Override public void visit(LineNumberTableAttribute                      lnta)  { this.visitOther(lnta);  }
        @Override public void visit(LocalVariableTableAttribute                   lvta)  { this.visitOther(lvta);  }
        @Override public void visit(LocalVariableTypeTableAttribute               lvtta) { this.visitOther(lvtta); }
        @Override public void visit(MethodParametersAttribute                     mpa)   { this.visitOther(mpa);   }
        @Override public void visit(ModulePackagesAttribute                       mpa)   { this.visitOther(mpa);   }
        @Override public void visit(RuntimeInvisibleAnnotationsAttribute          riaa)  { this.visitOther(riaa);  }
        @Override public void visit(RuntimeInvisibleParameterAnnotationsAttribute ripaa) { this.visitOther(ripaa); }
        @Override public void visit(RuntimeVisibleAnnotationsAttribute            rvaa)  { this.visitOther(rvaa);  }
        @Override public void visit(RuntimeVisibleParameterAnnotationsAttribute   rvpaa) { this.visitOther(rvpaa); }
        @Override public void visit(SignatureAttribute                            sa)    { this.visitOther(sa);    }
        @Override public void visit(SourceFileAttribute                           sfa)   { this.visitOther(sfa);   }
        @Override public void visit(StackMapTableAttribute                        smta)  { this.visitOther(smta);  }
        @Override public void visit(SyntheticAttribute                            sa)    { this.visitOther(sa);    }
        @Override public void visit(UnknownAttribute                              a)     { this.visitOther(a);     }
    }

    /**
     * Representation of an attribute with an unknown name.
     */
    public static
    class UnknownAttribute implements Attribute {

        /**
         * The <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7-120">name of the
         * attribute</a>.
         */
        public final String name;

        /**
         * The (unstructured) attribute information.
         */
        public byte[] info;

        UnknownAttribute(String name, DataInputStream dis, ClassFile cf) throws IOException {
            this.name = name;
            this.info = ClassFile.readByteArray(dis, dis.available());
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this); }
        @Override public String getName()                        { return this.name;    }
    }

    /**
     * Representation of a {@code Synthetic} attribute (JVMS9 4.7.8).
     */
    public static
    class SyntheticAttribute implements Attribute {
        public SyntheticAttribute(DataInputStream dis, ClassFile cf) {}

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this); }
        @Override public String getName()                        { return "Synthetic";  }
    }

    /**
     * Representation of a {@code Deprecated} attribute (JVMS9 4.7.15).
     */
    public static
    class DeprecatedAttribute implements Attribute {
        public DeprecatedAttribute(DataInputStream dis, ClassFile cf) {}

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this); }
        @Override public String getName()                        { return "Deprecated"; }
    }

    /**
     * Representation of a {@code InnerClasses} attribute (JVMS 9, 4.7.6).
     */
    public static final
    class InnerClassesAttribute implements Attribute {

        /**
         * Helper class for {@link InnerClassesAttribute}.
         */
        public static
        class ClasS {

            /**
             * The <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.6-300-D.2-1">inner
             * class</a> that is described by the remaining items.
             */
            public final ConstantClassInfo innerClassInfo;

            /**
             * The class or interface of which the {@link #innerClassInfo inner class} is a member.
             * <p>
             *   {@code null} == top-level type or anonymous class
             * </p>
             */
            @Nullable public final ConstantClassInfo outerClassInfo;

            /**
             * The original simple name of the {@link #innerClassInfo inner class}.
             * <p>
             *   {@code null} == anonymous
             * </p>
             */
            @Nullable public final ConstantUtf8Info innerName;

            /**
             * A mask of flags used to denote access permissions to and properties of the {@link #innerClassInfo inner
             * class}.
             */
            public final AccessFlags innerClassAccessFlags;

            public
            ClasS(DataInputStream dis, ClassFile cf) throws IOException {
                this.innerClassInfo        = cf.constantPool.get(dis.readShort(), ConstantClassInfo.class);
                this.outerClassInfo        = cf.constantPool.getOptional(dis.readShort(), ConstantClassInfo.class);
                this.innerName             = cf.constantPool.getOptional(dis.readShort(), ConstantUtf8Info.class);
                this.innerClassAccessFlags = new AccessFlags(dis.readShort());
            }
        }

        /**
         * The inner/outer class relationship relevant for this class.
         */
        public final List<ClasS> classes = new ArrayList<ClasS>();

        InnerClassesAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            for (int i = dis.readShort(); i > 0; --i) {
                this.classes.add(new ClasS(dis, cf));
            }
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this);   }
        @Override public String getName()                        { return "InnerClasses"; }
    }

    /**
     * Representation of the <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.16">{@code
     * RuntimeVisibleAnnotations} attribute</a>.
     */
    public
    class RuntimeVisibleAnnotationsAttribute implements Attribute {

        /**
         * The <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.16-300-D">annotations
         * table</a>.
         */
        public final List<Annotation> annotations = new ArrayList<Annotation>();

        /**
         * Reads and populates this object from the given {@link DataInputStream}.
         */
        RuntimeVisibleAnnotationsAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            for (int i = 0xffff & dis.readShort(); i > 0; --i) {
                this.annotations.add(new Annotation(dis, cf));
            }
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this);                }
        @Override public String getName()                        { return "RuntimeVisibleAnnotations"; }
    }

    /**
     * Representation of the {@code RuntimeInvisibleAnnotations} attribute (JVM9 4.7.17).
     */
    public
    class RuntimeInvisibleAnnotationsAttribute extends RuntimeVisibleAnnotationsAttribute {

        public
        RuntimeInvisibleAnnotationsAttribute(DataInputStream  dis, ClassFile cf) throws IOException {
            super(dis, cf);
        }

        @Override public void accept(AttributeVisitor visitor) { visitor.visit(this); }
    }

    /**
     * Helper class for {@link RuntimeVisibleParameterAnnotationsAttribute}.
     */
    public
    class ParameterAnnotation {

        /**
         * Each value represents a single run-time-visible annotation on the parameter corresponding to the sequence
         * number of this {@link ParameterAnnotation}.
         */
        public final List<Annotation> annotations = new ArrayList<Annotation>();

        public
        ParameterAnnotation(DataInputStream dis, ClassFile cf) throws IOException {
            for (int i = dis.readShort(); i > 0; --i) {
                this.annotations.add(new Annotation(dis, cf));
            }
        }
    }

    /**
     * Representation of the <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.18">{@code
     * RuntimeVisibleParameterAnnotations} attribute</a>.
     */
    public
    class RuntimeVisibleParameterAnnotationsAttribute implements Attribute {

        /**
         *  All of the <a
         *  href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.18-300-D">run-time-visible
         *  annotations on all parameters</a>.
         */
        public final List<ParameterAnnotation> parameterAnnotations = new ArrayList<ParameterAnnotation>();

        RuntimeVisibleParameterAnnotationsAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            for (int i = dis.readByte(); i > 0; --i) {
                this.parameterAnnotations.add(new ParameterAnnotation(dis, cf));
            }
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this);                         }
        @Override public String getName()                        { return "RuntimeVisibleParameterAnnotations"; }
    }

    /**
     * Representation of the {@code RuntimeInvisibleParameterAnnotations} attribute (JVMS9 4.7.19).
     */
    public
    class RuntimeInvisibleParameterAnnotationsAttribute
    extends RuntimeVisibleParameterAnnotationsAttribute {

        public
        RuntimeInvisibleParameterAnnotationsAttribute(DataInputStream  dis, ClassFile cf) throws IOException {
            super(dis, cf);
        }

        @Override public void accept(AttributeVisitor visitor) { visitor.visit(this); }
    }

    /**
     * Representation of the {@code AnnotationDefault} attribute (JVMS9 4.7.22).
     */
    public
    class AnnotationDefaultAttribute implements Attribute {

        /**
         * The <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.20-300-C">default value
         * of the annotation type element</a> whose default value is represented by this {@link
         * AnnotationDefaultAttribute}.
         */
        public ElementValue defaultValue;

        AnnotationDefaultAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            this.defaultValue = ClassFile.this.newElementValue(dis, cf);
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this);        }
        @Override public String getName()                        { return "AnnotationDefault"; }
    }

    /**
     * Representation of the "BootstrapMethods" attribute (JVMS8 4.7.23).
     */
    public static
    class BootstrapMethodsAttribute implements Attribute {

        /**
         * The <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.20-300-C">default value
         * of the annotation type element</a> whose default value is represented by this {@link
         * BootstrapMethodsAttribute}.
         */
        public List<BootstrapMethod> bootstrapMethods = new ArrayList<BootstrapMethod>();

        BootstrapMethodsAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            for (int i = dis.readShort(); i > 0; --i) {
                this.bootstrapMethods.add(new BootstrapMethod(dis, cf));
            }
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this);       }
        @Override public String getName()                        { return "BootstrapMethods"; }

        /**
         * Representation of one entry in the "BootstrapMethods" attribute (JVMS8 4.7.23).
         */
        public static
        class BootstrapMethod {

            private final ConstantMethodHandleInfo bootstrapMethod;
            private final List<ConstantPoolEntry>  bootstrapArguments = new ArrayList<ConstantPoolEntry>();

            public
            BootstrapMethod(DataInputStream dis, ClassFile cf) throws IOException {
                this.bootstrapMethod = cf.constantPool.get(dis.readShort(), ConstantMethodHandleInfo.class);
                for (int i = dis.readShort(); i > 0; --i) {
                    this.bootstrapArguments.add(cf.constantPool.get(dis.readShort(), ConstantPoolEntry.class));
                }
            }

            @Override public String
            toString() {
                StringBuilder               sb = new StringBuilder().append(this.bootstrapMethod).append('(');
                Iterator<ConstantPoolEntry> it = this.bootstrapArguments.iterator();
                if (it.hasNext()) {
                    sb.append(it.next());
                    while (it.hasNext()) sb.append(", ").append(it.next());
                }
                return sb.append(')').toString();
            }
        }
    }

    /**
     * Helper class for the {@code Runtime*visible*Annotations} attributes.
     * <p>
     *   Represents a single <a
     *   href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.16-300-D">run-time-visible
     *   annotation</a> on a program element.
     * </p>
     */
    public
    class Annotation {

        /**
         * Helper class for the {@code Runtime*visible*Annotations} attributes.
         * <p>
         *   Represents a single <a
         *   href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.16-300-D.2-3">element-value
         *   pair</a> in the annotation represented by this {@link Annotation}.
         * </p>
         */
        public
        class ElementValuePair {

            /**
             * A valid field descriptor that denotes the <a
             * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.16-300-D.2-3-1">name of the
             * annotation type element</a> represented by this {@link ElementValuePair}.
             */
            public final String elementName;

            /**
             * The <a
             * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.16-300-D.2-3-2">value of
             * the value item represents the value of the element-value pair</a> represented by this {@link
             * ElementValuePair}.
             */
            public final ElementValue elementValue;

            public
            ElementValuePair(DataInputStream dis, ClassFile cf) throws IOException {
                this.elementName = cf.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;
                try {
                	this.elementValue = ClassFile.this.newElementValue(dis, cf);
                } catch (RuntimeException re) {
                	throw new RuntimeException("Reading annotation element \"" + this.elementName + "\": " + re.getMessage(), re);
                }
            }

            @Override public String
            toString() {
                return (
                    "value".equals(this.elementName)
                    ? this.elementValue.toString()
                    : this.elementName + " = " + this.elementValue.toString()
                );
            }
        }

        /**
         * A <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.16-300-D.2-1">field
         * descriptor representing the annotation type</a> corresponding to this {@link Annotation}.
         */
        public String typeName;

        /**
         * Each value of this list represents a single element-value pair in the {@link Annotation}.
         */
        public final List<ElementValuePair> elementValuePairs = new ArrayList<ElementValuePair>();

        public
        Annotation(DataInputStream dis, ClassFile cf) throws IOException {
            short typeIndex = dis.readShort();
            try {
                this.typeName = ClassFile.this.signatureParser.decodeFieldDescriptor(
                    cf.constantPool.get(typeIndex, ConstantUtf8Info.class).bytes
                ).toString();
            } catch (SignatureException e) {
                throw new ClassFileFormatException("Decoding annotation type: " + e.getMessage(), e);
            }
            for (int i = dis.readUnsignedShort(); i > 0; --i) {
                this.elementValuePairs.add(new ElementValuePair(dis, cf));
            }
        }

        @Override public String
        toString() {
            StringBuilder sb = new StringBuilder("@").append(this.typeName);
            if (!this.elementValuePairs.isEmpty()) {
                Iterator<ElementValuePair> it = this.elementValuePairs.iterator();
                sb.append('(').append(it.next());
                while (it.hasNext()) sb.append(", ").append(it.next());
                return sb.append(')').toString();
            }
            return sb.toString();
        }
    }

    /**
     * Representation of an annotation element value.
     */
    public
    interface ElementValue {
    }

    /**
     * Reads one {@link ElementValue} from the given {@link DataInputStream}.
     */
    ElementValue
    newElementValue(DataInputStream dis, ClassFile cf) throws IOException {
        final byte tag = dis.readByte();
        if ("BCDFIJSZ".indexOf(tag) != -1) {
            final String s = cf.constantPool.get(
                dis.readShort(),
                ConstantDoubleOrFloatOrIntegerOrLongInfo.class
            ).toString();
            return new ElementValue() { @Override public String toString() { return s; } };
        } else
        if (tag == 's') {
            final String s = cf.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;
            return new ElementValue() {
                @Override public String toString() { return ConstantPool.stringToJavaLiteral(s); }
            };
        } else
        if (tag == 'e') {
            String typeName  = cf.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;
            String constName = cf.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;
            try {
                final String s = this.signatureParser.decodeFieldDescriptor(typeName) + "." + constName;
                return new ElementValue() { @Override public String toString() { return s; } };
            } catch (SignatureException se) {
                throw new ClassFileFormatException("Decoding enum constant element value: " + se.getMessage(), se);
            }
        } else
        if (tag == 'c') {
            final String classInfo = cf.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;
            try {
                final String s = this.signatureParser.decodeReturnType(classInfo) + ".class";
                return new ElementValue() { @Override public String toString() { return s; } };
            } catch (SignatureException se) {
                throw new ClassFileFormatException("Decoding class element value: " + se.getMessage(), se);
            }
        } else
        if (tag == '@') {
            final Annotation annotation = new Annotation(dis, cf);
            return new ElementValue() { @Override public String toString() { return annotation.toString(); } };
        } else
        if (tag == '[') {
            final List<ElementValue> values = new ArrayList<ElementValue>();
            for (int i = dis.readUnsignedShort(); i > 0; --i) {
                values.add(this.newElementValue(dis, cf));
            }
            return new ElementValue() {

                @Override public String
                toString() {
                    Iterator<ElementValue> it = values.iterator();
                    if (!it.hasNext()) return "{}";

                    ElementValue firstValue = it.next();
                    if (!it.hasNext()) return firstValue.toString();

                    StringBuilder sb = new StringBuilder("{ ").append(firstValue.toString());
                    do {
                        sb.append(", ").append(it.next().toString());
                    } while (it.hasNext());
                    return sb.append(" }").toString();
                }
            };
        } else
        {
            return new ElementValue() {
                @Override public String toString() { return "[Invalid element value tag '" + (char) tag + "']"; }
            };
        }
    }

    /**
     * Representation of the <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.9">{@code
     * Signature} attribute</a>.
     */
    public static final
    class SignatureAttribute implements Attribute {

        /**
         * <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.9-300-C">A class signature
         * if this {@link SignatureAttribute} is an attribute of a {@link ClassFile}; a method signature if this
         * {@link SignatureAttribute} attribute is an attribute of a {@link Method}; or a field type signature
         * otherwise</a>.
         */
        public final String signature;

        SignatureAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            this.signature = cf.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this); }
        @Override public String getName()                        { return "Signature";  }
    }

    /**
     * Representation of the <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.7">{@code
     * EnclosingMethod} attribute</a>.
     */
    public static final
    class EnclosingMethodAttribute implements Attribute {

        /**
         * The <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.7-300-C">innermost class
         * that encloses the declaration of the current class</a>.
         */
        public ConstantClassInfo clasS;

        /**
         * null == not enclosed by a constructor or a method, i.e. a field initializer
         */
        @Nullable public ConstantNameAndTypeInfo method;

        EnclosingMethodAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            this.clasS  = cf.constantPool.get(dis.readShort(), ConstantClassInfo.class);
            this.method = cf.constantPool.getOptional(dis.readShort(), ConstantNameAndTypeInfo.class);
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this);      }
        @Override public String getName()                        { return "EnclosingMethod"; }
    }

    /**
     * Representation of the <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.5">{@code
     * Exceptions} attribute</a>.
     */
    public static final
    class ExceptionsAttribute implements Attribute {

        /**
         * Each value of the list represents a <a
         * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.5-300-D">class type that this
         * method is declared to throw</a>.
         */
        public final List<ConstantClassInfo> exceptionNames = new ArrayList<ConstantClassInfo>();

        ExceptionsAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            for (int i = dis.readUnsignedShort(); i > 0; --i) {
                this.exceptionNames.add(cf.constantPool.get(dis.readShort(), ConstantClassInfo.class));
            }
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this); }
        @Override public String getName()                        { return "Exceptions"; }
    }

    /**
     * Representation of the <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.3">{@code
     * Code} attribute</a>.
     */
    public static final
    class CodeAttribute implements Attribute {

        /**
         * The <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.3-300-C">maximum depth
         * of the operand stack of this method at any point during execution of the method</a>.
         */
        public final short maxStack;

        /**
         * The <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.3-300-D">number of local
         * variables in the local variable array allocated upon invocation of this method, including the local
         * variables used to pass parameters to the method on its invocation</a>.
         */
        public final short maxLocals;

        /**
         * Gives <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.3-300-F">the actual
         * bytes of Java Virtual Machine code that implement the method</a>.
         */
        public final byte[] code;

        /**
         * Each entry in the list describes <a
         * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.3-300-H">one exception handler
         * in the code array</a>. The order of the handlers in the exception_table array is significant.
         */
        public final List<ExceptionTableEntry> exceptionTable = new ArrayList<ExceptionTableEntry>();

        /**
         * The <var>Code</var> attribute's optional <var>LocalVariableTable</var> attribute.
         */
        @Nullable public LocalVariableTableAttribute localVariableTableAttribute;

        /**
         * The <var>Code</var> attribute's optional <var>LocalVariableTypeTable</var> attribute.
         */
        @Nullable public LocalVariableTypeTableAttribute localVariableTypeTableAttribute;

        /**
         * The <var>Code</var> attribute's optional <var>LineNumberTable</var> attribute.
         */
        @Nullable public LineNumberTableAttribute lineNumberTableAttribute;

        /**
         * The <var>Code</var> attribute's optional <var>StackMapTable</var> attribute.
         */
        @Nullable public StackMapTableAttribute stackMapTableAttribute;

        /**
         * All attributes of this {@link CodeAttribute}.
         */
        public final List<Attribute> allAttributes = new ArrayList<Attribute>();

        /**
         * All unprocessed attributes of this {@link CodeAttribute}.
         */
        public final List<Attribute> unprocessedAttributes = new ArrayList<Attribute>();

        CodeAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            this.maxStack  = dis.readShort();
            this.maxLocals = dis.readShort();

            // Byte code.
            this.code = ClassFile.readByteArray(dis, dis.readInt());

            // Exception table.
            for (int i = dis.readUnsignedShort(); i > 0; --i) {
                this.exceptionTable.add(new ExceptionTableEntry(dis, cf));
            }

            // Code attributes.
            cf.readAttributes(dis, new AbstractAttributeVisitor() {

                @Override public void
                visit(LineNumberTableAttribute lnta) {
                    CodeAttribute.this.lineNumberTableAttribute = lnta;
                    CodeAttribute.this.allAttributes.add(lnta);
                }

                @Override public void
                visit(LocalVariableTableAttribute lvta) {
                    CodeAttribute.this.localVariableTableAttribute = lvta;
                    CodeAttribute.this.allAttributes.add(lvta);
                }
                @Override public void
                visit(LocalVariableTypeTableAttribute lvtta) {
                    CodeAttribute.this.localVariableTypeTableAttribute = lvtta;
                    CodeAttribute.this.allAttributes.add(lvtta);
                }

                @Override public void
                visit(StackMapTableAttribute smta) {
                    CodeAttribute.this.stackMapTableAttribute = smta;
                    CodeAttribute.this.allAttributes.add(smta);
                }

                @Override public void
                visitOther(Attribute a) {
                    CodeAttribute.this.allAttributes.add(a);
                    CodeAttribute.this.unprocessedAttributes.add(a);
                }
            });
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this); }
        @Override public String getName()                        { return "Code";       }
    }

    /**
     * Helper class for {@link CodeAttribute}.
     * <p>
     *   Describes one exception handler in the code array.
     * </p>
     */
    public static
    class ExceptionTableEntry {

        /**
         * The values <a
         * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.3-300-H.1-1">indicate the range
         * in the code array at which the exception handler is active</a>. The value of {@link #startPc} must be a
         * valid index into the code array of the opcode of an instruction. The value of {@link #endPc} either must be
         * a valid index into the code array of the opcode of an instruction or must be equal to {@code code.length},
         * the length of the code array. The value of {@link #startPc} must be less than the value of {@link #endPc}.
         * <p>
         *   The {@link #startPc} is inclusive and {@link #endPc} is exclusive; that is, the exception handler must be
         *   active while the program counter is within the interval [{@link #startPc}, {@link #endPc}).
         * </p>
         */
        public int startPc, endPc;

        /**
         * The value <a
         * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.3-300-H.1-2">indicates the
         * start of the exception handler</a>. The value of the item must be a valid index into the code array and must
         * be the index of the opcode of an instruction.
         */
        public int handlerPc;

        /**
         * A <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.3-300-H.1-3">class of
         * exceptions that this exception handler is designated to catch</a>. The exception handler will be called only
         * if the thrown exception is an instance of the given class or one of its subclasses.
         * <p>
         *   If the value is {@code null}, this exception handler is called for all exceptions. This is used to
         *   implement {@code finally}.
         * </p>
         */
        @Nullable public ConstantClassInfo catchType;

        ExceptionTableEntry(DataInputStream dis, ClassFile cf) throws IOException {
            this.startPc   = dis.readUnsignedShort();
            this.endPc     = dis.readUnsignedShort();
            this.handlerPc = dis.readUnsignedShort();
            this.catchType = cf.constantPool.getOptional(dis.readShort(), ConstantClassInfo.class);
        }

        @Override public String
        toString() {
            return (
                "startPC="
                + this.startPc
                + " endPC="
                + this.endPc
                + " handlerPC="
                + this.handlerPc
                + " catchType="
                + this.catchType
            );
        }
    }

    /**
     * Representation of the <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.10">{@code
     * SourceFile} attribute</a>.
     */
    public static
    class SourceFileAttribute implements Attribute {

        /**
         * The <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.10-300-C">name of the
         * source file from which this class file was compiled</a>. It will not be interpreted as indicating the name
         * of a directory containing the file or an absolute path name for the file; such platform-specific additional
         * information must be supplied by the run-time interpreter or development tool at the time the file name is
         * actually used.
         */
        public String sourceFile;

        SourceFileAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            this.sourceFile = cf.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this); }
        @Override public String getName()                        { return "SourceFile"; }
    }

    /**
     * Representation of the <a href="http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.4">{@code
     * StackMapTable} attribute</a>.
     */
    public
    class StackMapTableAttribute implements Attribute {

        /**
         * The {@code entries} array in the {@code StackMapTable} attribute. See JVMS8 4.7.4.
         */
        final List<StackMapFrame> entries = new ArrayList<StackMapFrame>();

        StackMapTableAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            final int n = dis.readUnsignedShort();
            for (int i = 0; i < n; i++) {
                StackMapFrame smf;
                try {
                    smf = this.readStackMapFrame(dis);
                } catch (IOException ioe) {
                    IOException ioe2 = new IOException("Reading frame #" + i + " of " + n + ": " + ioe.getMessage());
                    ioe2.initCause(ioe);
                    throw ioe2; // SUPPRESS CHECKSTYLE AvoidHidingCause
                } catch (RuntimeException re) {
                    throw new RuntimeException("Reading frame #" + i + " of " + n + ": " + re.getMessage(), re);
                }
                this.entries.add(smf);
            }
        }

        private StackMapFrame
        readStackMapFrame(DataInputStream dis) throws IOException {
            int frameType = 0xff & dis.readByte();
            switch (frameType) {

            case 247:
                return new SameLocals1StackItemFrameExtended(
                    dis.readShort(),                   // offsetDelta
                    this.readVerificationTypeInfo(dis) // stack
                );

            case 248:
            case 249:
            case 250:
                return new ChopFrame(dis.readShort(), 251 - frameType);

            case 251:
                return new SameFrameExtended(dis.readShort());

            case 252:
            case 253:
            case 254:
                return new AppendFrame(
                    dis.readShort(),                                     // offsetDelta
                    this.readVerificationTypeInfos(dis, frameType - 251) // locals
                );

            case 255:
                return new FullFrame(
                    dis.readShort(),                                      // offsetDelta
                    this.readVerificationTypeInfos(dis, dis.readShort()), // locals
                    this.readVerificationTypeInfos(dis, dis.readShort())  // stack
                );

            default:
                if (frameType <= 63) return new SameFrame(frameType);
                if (frameType <= 127) {
                    return new SameLocals1StackItemFrame(
                        frameType - 64,                    // offsetDelta
                        this.readVerificationTypeInfo(dis) // stack
                    );
                }
                if (frameType <= 246) throw new ClassFileFormatException("Reserved frame type " + frameType);
            }

            throw new AssertionError(frameType);
        }

        private VerificationTypeInfo[]
        readVerificationTypeInfos(DataInputStream dis, int n) throws IOException {

            VerificationTypeInfo[] result = new VerificationTypeInfo[n];
            for (int i = 0; i < n; i++) result[i] = this.readVerificationTypeInfo(dis);

            return result;
        }

        private VerificationTypeInfo
        readVerificationTypeInfo(DataInputStream dis) throws IOException {
            int tag = 0xff & dis.readByte();
            switch (tag) {

            case 0: return new TopVariableInfo();
            case 1: return new IntegerVariableInfo();
            case 2: return new FloatVariableInfo();
            case 3: return new DoubleVariableInfo();
            case 4: return new LongVariableInfo();
            case 5: return new NullVariableInfo();
            case 6: return new UninitializedThisVariableInfo();
            case 7: return new ObjectVariableInfo(ClassFile.this.constantPool.get(dis.readShort(), ConstantClassInfo.class)); // SUPPRESS CHECKSTYLE LineLength
            case 8: return new UninitializedVariableInfo(dis.readShort());

            default:
                throw new ClassFileFormatException("Invalid verification_type_info tag " + tag);
            }
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this);    }
        @Override public String getName()                        { return "StackMapTable"; }
    }

    /**
     * Representation of the {@code stack_map_frame} union; see JVMS8 4.7.4.
     */
    public abstract static
    class StackMapFrame {

        /**
         * The {@code offset_delta} value that is implicit to all stack map frames; see JVMS8 4.7.4.
         */
        final int offsetDelta;

        public StackMapFrame(int offsetDelta) { this.offsetDelta = offsetDelta; }

        /**
         * Invokes the "right" {@code visit...()} method of the {@link StackMapFrameVisitor}.
         */
        public abstract <T> T accept(StackMapFrameVisitor<T> smfv);
    }

    /**
     * @param <T> The return type of {@link StackMapFrame#accept(ClassFile.StackMapFrameVisitor)}
     * @see       StackMapFrame#accept(ClassFile.StackMapFrameVisitor)
     */
    public
    interface StackMapFrameVisitor<T> {
        T visitSameFrame(SameFrame sf); // SUPPRESS CHECKSTYLE JavadocMethod:6
        T visitSameLocals1StackItemFrame(SameLocals1StackItemFrame sl1sif);
        T visitSameLocals1StackItemFrameExtended(SameLocals1StackItemFrameExtended sl1sife);
        T visitChopFrame(ChopFrame cf);
        T visitSameFrameExtended(SameFrameExtended sfe);
        T visitAppendFrame(AppendFrame af);
        T visitFullFrame(FullFrame ff);
    }

    /**
     * Representation of the {@code same_frame} structure; see JVMS8 4.7.4.
     */
    public static
    class SameFrame extends StackMapFrame {
        public SameFrame(int offsetDelta) { super(offsetDelta); }

        @Override public <T> T
        accept(StackMapFrameVisitor<T> smfv) { return smfv.visitSameFrame(this); }

        @Override public String
        toString() { return "same_frame(offsetDelta=" + this.offsetDelta + ")"; }
    }

    /**
     * Representation of the {@code same_locals_1_stack_item_frame} structure; see JVMS8 4.7.4.
     */
    public static
    class SameLocals1StackItemFrame extends StackMapFrame {
        public final VerificationTypeInfo stack;

        public
        SameLocals1StackItemFrame(int offsetDelta, VerificationTypeInfo stack) {
            super(offsetDelta);
            this.stack = stack;
        }

        @Override public <T> T
        accept(StackMapFrameVisitor<T> smfv) { return smfv.visitSameLocals1StackItemFrame(this); }

        @Override public String
        toString() {
            return "same_locals_1_stack_item_frame(offsetDelta=" + this.offsetDelta + ", stack=[" + this.stack + "])";
        }
    }

    /**
     * Representation of the {@code same_locals_1_stack_item_frame_extended} structure; see JVMS8 4.7.4.
     */
    public static
    class SameLocals1StackItemFrameExtended extends StackMapFrame {
        public final VerificationTypeInfo stack;

        public
        SameLocals1StackItemFrameExtended(int offsetDelta, VerificationTypeInfo stack) {
            super(offsetDelta);
            this.stack = stack;
        }

        @Override public <T> T
        accept(StackMapFrameVisitor<T> smfv) { return smfv.visitSameLocals1StackItemFrameExtended(this); }

        @Override public String
        toString() {
            return (
                "same_locals_1_stack_item_frame_extended(offsetDelta="
                + this.offsetDelta
                + ", stack=["
                + this.stack
                + "])"
            );
        }
    }

    /**
     * Representation of the {@code chop_frame} structure; see JVMS8 4.7.4.
     */
    public static
    class ChopFrame extends StackMapFrame {
        public final int k;

        public
        ChopFrame(int offsetDelta, int k) { super(offsetDelta); this.k = k; }

        @Override public <T> T
        accept(StackMapFrameVisitor<T> smfv) { return smfv.visitChopFrame(this); }

        @Override public String
        toString() { return "chop_frame(offsetDelta=" + this.offsetDelta + ", locals-=" + this.k + ", stack=[])"; }
    }

    /**
     * Representation of the {@code same_frame_extended} structure; see JVMS8 4.7.4.
     */
    public static
    class SameFrameExtended extends StackMapFrame {

        public
        SameFrameExtended(int offsetDelta) { super(offsetDelta); }

        @Override public <T> T
        accept(StackMapFrameVisitor<T> smfv) { return smfv.visitSameFrameExtended(this); }

        @Override public String
        toString() { return "same_frame_extended(offsetDelta=" + this.offsetDelta + ", stack=[])"; }
    }

    /**
     * Representation of the {@code append_frame} structure; see JVMS8 4.7.4.
     */
    public static
    class AppendFrame extends StackMapFrame {
        public final VerificationTypeInfo[] locals;

        public
        AppendFrame(int offsetDelta, VerificationTypeInfo[] locals) {
            super(offsetDelta);
            this.locals = locals;
        }

        @Override public <T> T
        accept(StackMapFrameVisitor<T> smfv) { return smfv.visitAppendFrame(this); }

        @Override public String
        toString() {
            return (
                "append_frame(offsetDelta="
                + this.offsetDelta
                + ", locals+="
                + Arrays.toString(this.locals)
                + ", stack=[])"
            );
        }
    }

    /**
     * Representation of the {@code full_frame} structure; see JVMS8 4.7.4.
     */
    public static
    class FullFrame extends StackMapFrame {
        public final VerificationTypeInfo[] locals;
        public final VerificationTypeInfo[] stack;

        public
        FullFrame(int offsetDelta, VerificationTypeInfo[] locals, VerificationTypeInfo[] stack) {
            super(offsetDelta);
            this.locals = locals;
            this.stack  = stack;
        }

        @Override public <T> T
        accept(StackMapFrameVisitor<T> smfv) { return smfv.visitFullFrame(this); }

        @Override public String
        toString() {
            return (
                "full_frame(offsetDelta="
                + this.offsetDelta
                + ", locals="
                + Arrays.toString(this.locals)
                + ", stack="
                + Arrays.toString(this.stack)
                + ")"
            );
        }
    }

    /**
     * Representation of the {@code verification_type_info} union; see JVMS8 4.7.4.
     */
    public interface VerificationTypeInfo {}

    /**
     * Representation of the {@code top_variable_info} structure; see JVMS8 4.7.4.
     */
    public static
    class TopVariableInfo implements VerificationTypeInfo {
        @Override public String toString() { return "top"; }
    }

    /**
     * Representation of the {@code integer_variable_info} structure; see JVMS8 4.7.4.
     */
    public static
    class IntegerVariableInfo implements VerificationTypeInfo {
        @Override public String toString() { return "int"; }
    }

    /**
     * Representation of the {@code float_variable_info} structure; see JVMS8 4.7.4.
     */
    public static
    class FloatVariableInfo implements VerificationTypeInfo {
        @Override public String toString() { return "float"; }
    }

    /**
     * Representation of the {@code long_variable_info} structure; see JVMS8 4.7.4.
     */
    public static
    class LongVariableInfo implements VerificationTypeInfo {
        @Override public String toString() { return "long"; }
    }

    /**
     * Representation of the {@code double_variable_info} structure; see JVMS8 4.7.4.
     */
    public static
    class DoubleVariableInfo implements VerificationTypeInfo {
        @Override public String toString() { return "double"; }
    }

    /**
     * Representation of the {@code null_variable_info} structure; see JVMS8 4.7.4.
     */
    public static
    class NullVariableInfo implements VerificationTypeInfo {
        @Override public String toString() { return "null"; }
    }

    /**
     * Representation of the {@code uninitialized_this_variable_info} structure; see JVMS8 4.7.4.
     */
    public static
    class UninitializedThisVariableInfo implements VerificationTypeInfo {
        @Override public String toString() { return "uninitializedThis"; }
    }

    /**
     * Representation of the {@code object_variable_info} structure; see JVMS8 4.7.4.
     */
    public static
    class ObjectVariableInfo implements VerificationTypeInfo {

        private final ConstantClassInfo constantClassInfo;

        public ObjectVariableInfo(ConstantClassInfo constantClassInfo) { this.constantClassInfo = constantClassInfo; }

        @Override public String toString() { return this.constantClassInfo.toString(); }
    }

    /**
     * Representation of the {@code uninitialized_variable_info} structure; see JVMS8 4.7.4.
     */
    public static
    class UninitializedVariableInfo implements VerificationTypeInfo {

        private final short offset;

        public UninitializedVariableInfo(short offset) { this.offset = offset; }

        @Override public String toString() { return "uninitialized(offset=" + this.offset + ")"; }
    }

    /**
     * Representation of the <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.12">{@code
     * LineNumberTable} attribute</a>.
     */
    public
    class LineNumberTableAttribute implements Attribute {

        /**
         * Each entry in the list indicates that the line number in the original source file changes at a given point
         * in the code array.
         */
        public final List<LineNumberTableEntry> entries = new ArrayList<LineNumberTableEntry>();

        LineNumberTableAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            for (int i = dis.readUnsignedShort(); i > 0; --i) {
                this.entries.add(new LineNumberTableEntry(dis));
            }
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this);      }
        @Override public String getName()                        { return "LineNumberTable"; }
    }

    /**
     * Helper class for {@link LineNumberTableAttribute}.
     */
    public static
    class LineNumberTableEntry {

        /**
         * The value must indicate the index into the code array at which the code for a new line in the original
         * source file begins. The value must be less than {@link CodeAttribute#code CodeAttribute.code.length}.
         */
        public int startPc;

        /**
         * The value must give the corresponding line number in the original source file.
         */
        public int lineNumber;

        LineNumberTableEntry(DataInputStream dis) throws IOException {
            this.startPc    = dis.readUnsignedShort();
            this.lineNumber = dis.readUnsignedShort();
        }
    }

    /**
     * The <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.13">LocalVariableTable
     * attribute</a> is an optional variable-length attribute in the attributes table of a {@link CodeAttribute}. It
     * may be used by debuggers to determine the value of a given local variable during the execution of a method.
     * <p>
     *   If {@link LocalVariableTableAttribute}s are present in the attributes table of a given {@link CodeAttribute},
     *   then they may appear in any order. There may be no more than one {@link LocalVariableTableAttribute} per local
     *   variable in the {@link CodeAttribute}.
     * </p>
     */
    public
    class LocalVariableTableAttribute implements Attribute {

        /**
         * Helper class for {@link LocalVariableTableAttribute}.
         */
        class Entry {

            /**
             * The given local variable must have a value at indices into the code array in the interval [{@link
             * #startPC}, {@link #startPC} + {@link #length}), that is, between {@link #startPC} inclusive and {@link
             * #startPC} + {@link #length} exclusive.
             * <p>
             *   The value of {@link #startPC} must be a valid index into the code array of this {@link CodeAttribute}
             *   and must be the index of the opcode of an instruction.
             * </p>
             * <p>
             *   The value of {@link #startPC} + {@link #length} must either be a valid index into the code array of
             *   this {@link CodeAttribute} and be the index of the opcode of an instruction, or it must be the first
             *   index beyond the end of that code array.
             * </p>
             */
            public final short startPC, length;

            /**
             * Represents a valid unqualified name denoting a local variable.
             */
            public final String name;

            /**
             * Representation of a field descriptor encoding the type of a local variable in the source program.
             */
            public final String descriptor;

            /**
             * The given local variable must be at {@link #index} in the local variable array of the current frame. If
             * the local variable at index is of type double or long, it occupies both {@link #index} and {@link
             * #index} + 1.
             */
            public final short index;

            Entry(DataInputStream dis, ClassFile cf) throws IOException {
                this.startPC    = dis.readShort();
                this.length     = dis.readShort();
                this.name       = cf.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;
                this.descriptor = cf.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;
                this.index      = dis.readShort();
            }
        }

        /**
         * Each entry in the list indicates a range of code array offsets within which a local variable has a value.
         * It also indicates the index into the local variable array of the current frame at which that local variable
         * can be found.
         */
        public final List<Entry> entries = new ArrayList<Entry>();

        LocalVariableTableAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            for (int i = dis.readUnsignedShort(); i > 0; --i) {
                this.entries.add(new Entry(dis, cf));
            }
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this);         }
        @Override public String getName()                        { return "LocalVariableTable"; }
    }

    /**
     * Representation of the <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.14">{@code
     * LocalVariableTypeTable} attribute</a>.
     */
    public static
    class LocalVariableTypeTableAttribute implements Attribute {

        /**
         * Indicates a range of code array offsets within which a local variable has a value. It also indicates the
         * index into the local variable array of the current frame at which that local variable can be found.
         */
        public static
        class Entry {

            /**
             * The given local variable must have a value at indices into the code array in the interval [{@link
             * #startPC}, {@link #startPC} + {@link #length}), that is, between {@link #startPC} inclusive and
             * {@link #startPC} + {@link #length} exclusive.
             * <p>
             *   The value of {@link #startPC} must be a valid index into the code array of this {@link CodeAttribute}
             *   and must be the index of the opcode of an instruction.
             * </p>
             * <p>
             *   The value of {@link #startPC} + {@link #length} must either be a valid index into the code array of
             *   this {@link CodeAttribute} and be the index of the opcode of an instruction, or it must be the first
             *   index beyond the end of that code array.
             * </p>
             */
            public final int startPC, length;

            /**
             * Represents a valid unqualified name denoting a local variable.
             */
            public final String name;

            /**
             * Representation of a field type signature encoding the type of a local variable in the source program.
             */
            public final String signature;

            /**
             * The given local variable must be at {@link #index} in the local variable array of the current frame. If
             * the local variable at {@link #index} is of type double or long, it occupies both {@link #index} and
             * {@link #index} + 1.
             */
            public final short index;

            Entry(DataInputStream dis, ClassFile cf) throws IOException {
                this.startPC   = dis.readUnsignedShort();
                this.length    = dis.readUnsignedShort();
                this.name      = cf.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;
                this.signature = cf.constantPool.get(dis.readShort(), ConstantUtf8Info.class).bytes;
                this.index     = dis.readShort();
            }
        }

        /**
         * Each entry in the list indicates a range of code array offsets within which a local variable has a value.
         * It also indicates the index into the local variable array of the current frame at which that local variable
         * can be found.
         */
        public final List<Entry> entries = new ArrayList<Entry>();

        LocalVariableTypeTableAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            for (int i = dis.readUnsignedShort(); i > 0; --i) {
                this.entries.add(new Entry(dis, cf));
            }
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this);             }
        @Override public String getName()                        { return "LocalVariableTypeTable"; }
    }

    /**
     * Representation of the <a
     * href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.2-100">{@code ConstantValue}
     * attribute</a>.
     */
    public static final
    class ConstantValueAttribute implements Attribute {

        /**
         * Gives the constant value represented by this attribute. The constant pool entry must be of a type
         * appropriate to the field.
         */
        public final String constantValue;

        ConstantValueAttribute(DataInputStream dis, ClassFile cf) throws IOException {
            this.constantValue = (
                cf
                .constantPool
                .get(dis.readShort(), ConstantDoubleOrFloatOrIntegerOrLongOrStringInfo.class)
                .toString()
            );
        }

        @Override public void   accept(AttributeVisitor visitor) { visitor.visit(this);    }
        @Override public String getName()                        { return "ConstantValue"; }
    }

    private static byte[]
    readByteArray(DataInputStream dis, int size) throws IOException {
        byte[] res = new byte[size];
        dis.readFully(res);
        return res;
    }

//    public String
//    beautifyTypeName(String typeName) {
//
//        // Strip redundant prefixes from the type name.
//        for (String packageNamePrefix : new String[] { "java.lang.", this.thisClassPackageNamePrefix }) {
//            if (typeName.startsWith(packageNamePrefix)) {
//                return typeName.substring(packageNamePrefix.length());
//            }
//        }
//
//        return typeName;
//    }
}
