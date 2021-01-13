
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
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.BRIDGE;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.ENUM;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.FINAL;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.INTERFACE;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.PUBLIC;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.STATIC;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.SYNCHRONIZED;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.SYNTHETIC;
import static de.unkrig.jdisasm.ClassFile.AccessFlags.FlagType.VARARGS;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import de.unkrig.commons.nullanalysis.NotNullByDefault;
import de.unkrig.commons.nullanalysis.Nullable;
import de.unkrig.jdisasm.ClassFile.AccessFlags;
import de.unkrig.jdisasm.ClassFile.Annotation;
import de.unkrig.jdisasm.ClassFile.AnnotationDefaultAttribute;
import de.unkrig.jdisasm.ClassFile.Attribute;
import de.unkrig.jdisasm.ClassFile.AttributeVisitor;
import de.unkrig.jdisasm.ClassFile.BootstrapMethodsAttribute;
import de.unkrig.jdisasm.ClassFile.BootstrapMethodsAttribute.BootstrapMethod;
import de.unkrig.jdisasm.ClassFile.CodeAttribute;
import de.unkrig.jdisasm.ClassFile.ConstantValueAttribute;
import de.unkrig.jdisasm.ClassFile.DeprecatedAttribute;
import de.unkrig.jdisasm.ClassFile.EnclosingMethodAttribute;
import de.unkrig.jdisasm.ClassFile.ExceptionsAttribute;
import de.unkrig.jdisasm.ClassFile.Field;
import de.unkrig.jdisasm.ClassFile.InnerClassesAttribute;
import de.unkrig.jdisasm.ClassFile.LineNumberTableAttribute;
import de.unkrig.jdisasm.ClassFile.LineNumberTableEntry;
import de.unkrig.jdisasm.ClassFile.LocalVariableTableAttribute;
import de.unkrig.jdisasm.ClassFile.LocalVariableTypeTableAttribute;
import de.unkrig.jdisasm.ClassFile.Method;
import de.unkrig.jdisasm.ClassFile.MethodParametersAttribute;
import de.unkrig.jdisasm.ClassFile.MethodParametersAttribute.Parameter;
import de.unkrig.jdisasm.ClassFile.ModulePackagesAttribute;
import de.unkrig.jdisasm.ClassFile.ParameterAnnotation;
import de.unkrig.jdisasm.ClassFile.RuntimeInvisibleAnnotationsAttribute;
import de.unkrig.jdisasm.ClassFile.RuntimeInvisibleParameterAnnotationsAttribute;
import de.unkrig.jdisasm.ClassFile.RuntimeVisibleAnnotationsAttribute;
import de.unkrig.jdisasm.ClassFile.RuntimeVisibleParameterAnnotationsAttribute;
import de.unkrig.jdisasm.ClassFile.SignatureAttribute;
import de.unkrig.jdisasm.ClassFile.SourceFileAttribute;
import de.unkrig.jdisasm.ClassFile.StackMapFrame;
import de.unkrig.jdisasm.ClassFile.StackMapTableAttribute;
import de.unkrig.jdisasm.ClassFile.SyntheticAttribute;
import de.unkrig.jdisasm.ClassFile.UnknownAttribute;
import de.unkrig.jdisasm.ConstantPool.ConstantClassInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantNameAndTypeInfo;
import de.unkrig.jdisasm.ConstantPool.ConstantPoolEntry;
import de.unkrig.jdisasm.SignatureParser.ArrayTypeSignature;
import de.unkrig.jdisasm.SignatureParser.ClassSignature;
import de.unkrig.jdisasm.SignatureParser.ClassTypeSignature;
import de.unkrig.jdisasm.SignatureParser.FieldTypeSignature;
import de.unkrig.jdisasm.SignatureParser.FormalTypeParameter;
import de.unkrig.jdisasm.SignatureParser.MethodTypeSignature;
import de.unkrig.jdisasm.SignatureParser.Options;
import de.unkrig.jdisasm.SignatureParser.SignatureException;
import de.unkrig.jdisasm.SignatureParser.ThrowsSignature;
import de.unkrig.jdisasm.SignatureParser.TypeSignature;

/**
 * A Java bytecode disassembler, comparable to JAVAP, which is part of ORACLE's JDK.
 * <p>
 *   Notice that this tool does not depend on any other classes or libraries (other than the standard JDK library).
 * </p>
 * <p>
 *   The disassembly is optimized to produce minimal DIFFs for changed class files: E.g. code offsets and local
 *   variable indexes are only printed if really necessary.
 * </p>
 */
public
class Disassembler {

    // SUPPRESS CHECKSTYLE LineLength:6
    private static final List<ConstantClassInfo>   NO_CONSTANT_CLASS_INFOS   = Collections.<ConstantClassInfo>emptyList();
    private static final List<ThrowsSignature>     NO_THROWS_SIGNATURES      = Collections.<ThrowsSignature>emptyList();
    private static final List<TypeSignature>       NO_TYPE_SIGNATURES        = Collections.<TypeSignature>emptyList();
    private static final List<ClassTypeSignature>  NO_CLASS_TYPE_SIGNATURES  = Collections.<ClassTypeSignature>emptyList();
    private static final List<FormalTypeParameter> NO_FORMAL_TYPE_PARAMETERS = Collections.<FormalTypeParameter>emptyList();
    private static final List<ParameterAnnotation> NO_PARAMETER_ANNOTATIONS  = Collections.<ParameterAnnotation>emptyList();

    // Configuration variables.

    /**
     * Where to print the output.
     */
    private PrintWriter pw = new PrintWriter(System.out, true);

    boolean showClassPoolIndexes;

    boolean dumpConstantPool;

    boolean printAllAttributes;

    boolean printStackMap;

    /**
     * An empty array means "do not attempt to find the source file".
     */
    private File[] sourcePath = new File[0];

    /**
     * Whether to print line numbers.
     */
    boolean showLineNumbers = true;

    /**
     * Whether to resolve the local variable names.
     */
    boolean showVariableNames = true;

    /**
     * Whether to print symbolic labels instead of offsets.
     */
    boolean symbolicLabels;

    /**
     * If false, then only the labels of branch targets are printed.
     */
    boolean printAllOffsets;

    private SignatureParser signatureParser = new SignatureParser();

    private enum AttributeContext { CLASS, FIELD, METHOD }

    /**
     * Generates a "disassembly document" from one or more Java class files.
     * <dl>
     *   <dt>{@code jdisasm [} <var>options</var> {@code ]} <var>files</var></dt>
     *   <dd>
     *     Disassemble the given <var>files</var>, which must be Java class files.
     *   </dd>
     *   <dt>{@code jdisasm [} <var>options</var> {@code ]}</dt>
     *   <dd>
     *     Read STDIN and disassemble; STDIN must be Java class file.
     *   </dd>
     * </dl>
     * <h3>Options</h3>
     * <dl>
     *   <dt>{@code -o} <var>file</var></dt>
     *   <dd>
     *     Store the disassembly document in the <var>file</var>, instead of printing it to STDOUT
     *   </dd>
     *   <dt>{@code -verbose}</dt>
     *   <dd>
     *     Equivalent with "{@code -show-class-pool-indexes -dump-constant-pool -print-all-attributes -print-stack-map}"
     *   </dd>
     *   <dt>{@code -show-class-pool-indexes}</dt>
     *   <dd>
     *     Within the bytecode diassembly, print the constant pool index for each constant pool reference
     *   </dd>
     *   <dt>{@code -dump-constant-pool}</dt>
     *   <dd>
     *     Print the constant pool at the top of each class
     *   </dd>
     *   <dt>{@code -print-all-attributes}</dt>
     *   <dd>
     *     Also print the UNRECOGNIZED attributes
     *   </dd>
     *   <dt>{@code -print-stack-map}</dt>
     *   <dd>
     *     Print the StackMapTable attribute
     *   </dd>
     *   <dt>{@code -src} <var>source-path</var></dt>
     *   <dd>
     *     Interweave the output with the class file's source code
     *   </dd>
     *   <dt>{@code -hide-lines}</dt>
     *   <dd>
     *     Don't print line number information
     *   </dd>
     *   <dt>{@code -hide-vars}</dt>
     *   <dd>
     *     Don't print local variable name information
     *   </dd>
     *   <dt>{@code -symbolic-labels}</dt>
     *   <dd>
     *     Use symbolic labels for offsets in the code attribute of methods
     *   </dd>
     *   <dt>{@code -help}</dt>
     *   <dd>
     *     Print this text and exit
     *   </dd>
     * </dl>
     */
    public static void
    main(String[] args) throws IOException {

        // To disassemble .class files in zip (.jar, .ear, ...) archives, register a stream handler for the 'zip'
        // scheme.
        de.unkrig.jdisasm.protocol.zip.Handler.registerMe();

        Disassembler d = new Disassembler();
        int          i;
        for (i = 0; i < args.length; ++i) {

            String arg = args[i];
            if (arg.charAt(0) != '-' || arg.length() == 1) break;
            if ("--".equals(arg)) { i++; break; }

            if ("-o".equals(arg)) {
                d.setOut(new FileOutputStream(args[++i]));
            } else
            if ("-verbose".equals(arg)) {
                d.setVerbose(true);
            } else
            if ("-show-class-pool-indexes".equals(arg)) {
                d.setShowClassPoolIndexes(true);
            } else
            if ("-dump-constant-pool".equals(arg)) {
                d.setDumpConstantPool(true);
            } else
            if ("-print-all-attributes".equals(arg)) {
                d.setPrintAllAttributes(true);
            } else
            if ("-print-stack-map".equals(arg)) {
                d.setPrintStackMap(true);
            } else
            if ("-src".equals(arg)) {
                d.setSourcePath(Disassembler.splitPath(args[++i]));
            } else
            if ("-hide-lines".equals(arg)) {
                d.setShowLineNumbers(false);
            } else
            if ("-hide-vars".equals(arg)) {
                d.setShowVariableNames(false);
            } else
            if ("-symbolic-labels".equals(arg)) {
                d.setSymbolicLabels(true);
            } else
            if ("-help".equals(arg)) {

                System.out.printf((
                    ""
                    + "Prints a disassembly listing of the given JAVA[TM] class files (or STDIN) to%n"
                    + "STDOUT.%n"
                    + "Usage:%n"
                    + "  java %1$s [ <option> ] ... [ <class-file-name> | <class-file-url> | '-' ] ...%n"
                    + "Valid options are:%n"
                    + "  -o <output-file>         Store disassembly output in a file.%n"
                    + "  -show-class-pool-indexes Within the bytecode diassembly, print the index%n"
                    + "                           for each constant pool reference.%n"
                    + "  -dump-constant-pool      Print the constant pool.%n"
                    + "  -print-all-attributes    Also print the UNRECOGNIZED attributes.%n"
                    + "  -print-stack-map         Print the StackMapTable attribute.%n"
                    + "  -verbose                 Equivalent with \"-show-class-pool-indexes%n"
                    + "                           -dump-constant-pool -print-all-attributes%n"
                    + "                           -print-stack-map\".%n"
                    + "  -src <source-path>       Interweave the output with the class file's source code.%n"
                    + "  -hide-lines              Don't print line numbers.%n"
                    + "  -hide-vars               Don't resolve local variable names.%n"
                    + "  -symbolic-labels         Use symbolic labels instead of offsets.%n"
                    + "  -help                    Print this text and exit.%n"
                ), Disassembler.class.getName());

                System.exit(0);
            } else
            {
                System.err.println("Unrecognized command line option \"" + arg + "\"; try \"-help\".");
                System.exit(1);
            }
        }
        if (i == args.length) {
            d.disasm(System.in);
        } else {
            for (; i < args.length; ++i) {
                String name = args[i];
                if ("-".equals(name)) {
                    d.disasm(System.in);
                } else
                if (Disassembler.IS_URL.matcher(name).matches()) {
                    d.disasm(new URL(name));
                } else
                {
                    d.disasm(new File(name));
                }
            }
        }
    }

    private static File[]
    splitPath(@Nullable String path) {

        if (path == null) return new File[0];

        StringTokenizer st = new StringTokenizer(path, File.pathSeparator);

        File dir = new File(st.nextToken());

        if (!st.hasMoreTokens()) return new File[] { dir };

        List<File> result = new ArrayList<File>();
        result.add(dir);

        while (st.hasMoreTokens()) result.add(new File(st.nextToken()));

        return result.toArray(new File[result.size()]);
    }

    private static final Pattern IS_URL = Pattern.compile("\\w\\w+:.*");

    public Disassembler() {}

    /**
     * @param writer Where to write all output
     */
    public void
    setOut(Writer writer) {
        this.pw = writer instanceof PrintWriter ? (PrintWriter) writer : new PrintWriter(writer, true);
    }

    /**
     * @param stream Where to write all output
     */
    public void
    setOut(OutputStream stream) { this.pw = new PrintWriter(stream, true); }

    /**
     * @param stream Where to write all output
     */
    public void
    setOut(OutputStream stream, String charsetName) throws UnsupportedEncodingException {
        this.pw = new PrintWriter(new OutputStreamWriter(stream, charsetName), true);
    }

    /**
     * Equivalent with "{@code -show-class-pool-indexes -dump-constant-pool -print-all-attributes -print-stack-map}".
     */
    public void
    setVerbose(boolean verbose) {
        this.setShowClassPoolIndexes(verbose);
        this.setDumpConstantPool(verbose);
        this.setPrintAllAttributes(verbose);
        this.setPrintStackMap(verbose);
    }

    /**
     * Whether to print constant pool indexes in the output.
     */
    public void
    setShowClassPoolIndexes(boolean showClassPoolIndexes) { this.showClassPoolIndexes = showClassPoolIndexes; }

    /**
     * Whether to print the entire constant pool in the output.
     */
    public void
    setDumpConstantPool(boolean dumpConstantPool) { this.dumpConstantPool = dumpConstantPool; }

    /**
     * Whether to print all attributes (otherwise only the unprocessed attributes).
     */
    public void
    setPrintAllAttributes(boolean printAllAttributes) { this.printAllAttributes = printAllAttributes; }

    /**
     * Whether to print stack map frames interleaved with bytecode disassembls.
     */
    public void
    setPrintStackMap(boolean printStackMap) { this.printStackMap = printStackMap; }

    /**
     * Where to look for source files; an empty array disables source file loading. Source file loading is disabled by
     * default.
     */
    public void
    setSourcePath(File[] value) { this.sourcePath = value; }

    /**
      * @deprecated Use {@link #setSourcePath(File[])} instead
      */
    @Deprecated public void
    setSourceDirectory(@Nullable File sourceDirectory) {
        this.setSourcePath(sourceDirectory == null ? new File[0] : new File[] { sourceDirectory });
    }

    /**
     * @param value Whether to print line numbers in the disassembly (defaults to {@code true})
     */
    public void
    setShowLineNumbers(boolean value) { this.showLineNumbers = value; }

    /**
     * @deprecated Use {@link #setShowLineNumbers(boolean)} instead
     */
    @Deprecated public void
    setHideLines(boolean hideLines) { this.setShowLineNumbers(!hideLines); }

    /**
     * @param value Whether local variable names are resolved in the disassembly (defaults to {@code true})
     */
    public void
    setShowVariableNames(boolean value) { this.showVariableNames = value; }

    /**
     * @deprecated Use {@link #setShowVariableNames(boolean)} instead
     */
    @Deprecated public void
    setHideVars(boolean hideVars) { this.setShowVariableNames(!hideVars); }

    /**
     * @param symbolicLabels Whether use numeric labels ('#123') or symbolic labels /'L12') in the bytecode disassembly
     */
    public void
    setSymbolicLabels(boolean symbolicLabels) { this.symbolicLabels = symbolicLabels; }

    /**
     * If false, then only the labels of branch targets are printed
     */
    public void
    setPrintAllOffsets(boolean printAllOffsets) { this.printAllOffsets = printAllOffsets; }

    private void print(String s)                       { this.pw.print(s);             }
    private void println()                             { this.pw.println();            }
    private void println(String s)                     { this.pw.println(s);           }
    private void printf(String format, Object... args) { this.pw.printf(format, args); }

    /**
     * Reads a class file from the given <var>file</var> and disassembles it.
     */
    public void
    disasm(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        try {
            this.println();
            this.println("// *** Disassembly of '" + file + "'.");
            this.disasm(is);
        } catch (IOException ioe) {
            IOException ioe2 = new IOException("Disassembling '" + file + "': " + ioe.getMessage());
            ioe2.initCause(ioe);
            throw ioe2; // SUPPRESS CHECKSTYLE AvoidHidingCause
        } catch (RuntimeException re) {
            throw new RuntimeException("Disassembling '" + file + "': " + re.getMessage(), re);
        } finally {
            try { is.close(); } catch (IOException ex) {}
        }
    }

    /**
     * Reads a class file from the given <var>location</var> and disassembles it.
     */
    public void
    disasm(URL location) throws IOException {
        InputStream is = location.openConnection().getInputStream();
        try {
            this.println();
            this.println("// *** Disassembly of '" + location + "'.");
            this.disasm(is);
        } catch (IOException ioe) {
            IOException ioe2 = new IOException("Disassembling '" + location + "': " + ioe.getMessage());
            ioe2.initCause(ioe);
            throw ioe2; // SUPPRESS CHECKSTYLE AvoidHidingCause
        } catch (RuntimeException re) {
            throw new RuntimeException("Disassembling '" + location + "': " + re.getMessage(), re);
        } finally {
            try { is.close(); } catch (IOException ex) {}
        }
    }

    /**
     * Reads a class file from the given <var>stream</var> and disassembles it.
     */
    public void
    disasm(InputStream stream) throws IOException {

        DataInputStream
        dis = stream instanceof DataInputStream ? (DataInputStream) stream : new DataInputStream(stream);

        this.disasm(new ClassFile(dis));
    }

    /**
     * @param dis A Java class file
     */
    public void
    disasm(ClassFile cf) throws IOException {

        // Print JDK version.
        this.println();
        this.println(
            "// Class file version = " + cf.majorVersion + "." + cf.minorVersion + " (" + cf.getJdkName() + ")"
        );

        final String tcpn = cf.thisClassName.substring(0, cf.thisClassName.lastIndexOf('.') + 1);

        // Configure a custom signature parser that is in effect while the disassembly is generated; that signature
        // parser makes long class names more readable.
        this.signatureParser = new SignatureParser(new Options() {

            @Override public String
            beautifyPackageNamePrefix(String packageNamePrefix) {
                return (
                    tcpn.equals(packageNamePrefix) || "java.lang.".equals(packageNamePrefix)
                    ? ""
                    : packageNamePrefix
                );
            }
        });

        cf.setSignatureParser(this.signatureParser);

        // Print package declaration.
        if (tcpn.length() > 0) {
            this.println();
            this.println("package " + tcpn.substring(0, tcpn.length() - 1) + ";");
        }

        // Print enclosing method info.
        EnclosingMethodAttribute ema = cf.enclosingMethodAttribute;
        if (ema != null) {

            ConstantNameAndTypeInfo m          = ema.method;
            String                  methodName = m == null ? "[initializer]" : m.name.bytes;

            this.println();
            this.println(
                "// This class is enclosed by method '"
                + ema.clasS
                + ("<init>".equals(methodName) ? "(...)" : "." + methodName + "(...)")
                + "'."
            );
        }

        this.println();

        // Print SYNTHETIC notice.
        if (cf.accessFlags.is(ClassFile.AccessFlags.FlagType.SYNTHETIC) || cf.syntheticAttribute != null) {
            this.println("// This is a synthetic class.");
        }

        // Print DEPRECATED notice.
        if (cf.deprecatedAttribute != null) this.println("/** @deprecated */");

        // Print type annotations.
        {
            RuntimeInvisibleAnnotationsAttribute riaa = cf.runtimeInvisibleAnnotationsAttribute;
            if (riaa != null) {
                for (Annotation a : riaa.annotations) this.println(a.toString());
            }
        }
        {
            RuntimeVisibleAnnotationsAttribute rvaa = cf.runtimeVisibleAnnotationsAttribute;
            if (rvaa != null) {
                for (Annotation a : rvaa.annotations) this.println(a.toString());
            }
        }

        AccessFlags af = (
            cf.accessFlags
            .remove(SYNCHRONIZED) // Has no meaning but is always set for backwards compatibility
            .remove(SYNTHETIC)    // SYNTHETIC has already been printed as a comment.
        );

        // Suppress redundant "abstract" modifier for interfaces.
        if (af.is(INTERFACE)) af = af.remove(ABSTRACT);

        // Suppress redundant "final" modifier for enums.
        if (af.is(ENUM)) af = af.remove(FINAL);

        // Print type access flags.
        this.print(Disassembler.typeAccessFlagsToString(af));

        // Print name.
        {
            SignatureAttribute sa = cf.signatureAttribute;
            if (sa != null) {
                this.print(this.decodeClassSignature(sa.signature).toString(cf.simpleThisClassName));
            } else {
                this.print(cf.simpleThisClassName);
            }
        }

        // Print EXTENDS clause.
        {
            String scn = cf.superClassName;
            if (scn != null && !"java.lang.Object".equals(scn)) this.print(" extends " + scn);
        }

        // Print IMPLEMENTS clause.
        {
            List<String> ifs = cf.interfaceNames;
            if (cf.accessFlags.is(ANNOTATION)) {
                if (ifs.contains("java.lang.annotation.Annotation")) {
                    ifs = new ArrayList<String>(ifs);
                    ifs.remove("java.lang.annotation.Annotation");
                } else {
                    this.print(
                        " /* WARNING: "
                        + "This annotation type does not implement \"java.lang.annotation.Annotation\"! */"
                    );
                }
            }
            if (!ifs.isEmpty()) {
                Iterator<String> it = ifs.iterator();
                this.print(" implements " + it.next());
                while (it.hasNext()) this.print(", " + it.next());
            }
        }

        this.println(" {");

        // Dump the constant pool.
        if (this.dumpConstantPool) {
            this.println();
            this.println("    // Constant pool dump:");
            ConstantPool cp = cf.constantPool;
            for (int i = 1; i < cp.getSize();) {
                ConstantPoolEntry constantPoolEntry = cp.get((short) i, ConstantPoolEntry.class);

                String pet = constantPoolEntry.getClass().getSimpleName();
                if (pet.startsWith("Constant")) pet = pet.substring(8);
                if (pet.endsWith("Info")) pet = pet.substring(0, pet.length() - 4);

                this.printf("    //   #%-5d %-21s %s%n", i, "(" + pet + "):", constantPoolEntry);
                i += constantPoolEntry.size();
            }
        }

        // Print enclosing/enclosed types.
        {
            InnerClassesAttribute ica = cf.innerClassesAttribute;
            if (ica != null) {
                this.println();
                this.println("    // Enclosing/enclosed types:");
                for (InnerClassesAttribute.ClasS c : ica.classes) {
                    this.println("    //   " + Disassembler.toString(c));
                }
            }
        }

        // Print module packages.
        {
            ModulePackagesAttribute mpa = cf.modulePackagesAttribute;
            if (mpa != null) {
                this.println();
                this.println("    // Module packages:");
                for (String packageName : mpa.packages) {
                    this.println("    //   " + packageName);
                }
            }
        }

        // Print fields.
        this.disassembleFields(cf.fields);

        // Before disassembling the class's methods, locate, read and load the source file.
        Map<Integer, String> sourceLines;
        {
            SourceFileAttribute sfa = cf.sourceFileAttribute;

            sourceLines = null;
            for (File sourceDirectory : this.sourcePath) {

                // Attempt to locate the source file through the class file's "SourceFile" attribute.
                if (sfa != null) {
                    sourceLines = Disassembler.loadFile(new File(sourceDirectory, sfa.sourceFile));
                    if (sourceLines != null) break;
                }

                // Attempt to locate the source file through the (top-level) class name.
                String toplevelClassName;
                {
                    toplevelClassName = cf.thisClassName;
                    int idx = toplevelClassName.indexOf('$');
                    if (idx != -1) toplevelClassName = toplevelClassName.substring(0, idx);
                }
                sourceLines = Disassembler.loadFile(new File(
                    sourceDirectory,
                    toplevelClassName.replace('.', File.separatorChar) + ".java"
                ));
                if (sourceLines != null) break;
            }
        }

        // Disassemble all methods.
        for (Method m : cf.methods) {
            this.disassembleMethod(m, sourceLines);
        }

        this.println("}");

        // Print class attributes.
        this.printAttributes(cf.unprocessedAttributes, cf.allAttributes, "// ", AttributeContext.CLASS);
    }

    @Nullable private static Map<Integer, String>
    loadFile(File file) throws FileNotFoundException, IOException {

        if (!file.exists()) return null;

        Map<Integer, String> result = new HashMap<Integer, String>();

        LineNumberReader lnr = new LineNumberReader(new FileReader(file));
        try {
            for (;;) {
                String sl = lnr.readLine();
                if (sl == null) break;
                result.put(lnr.getLineNumber(), sl);
            }
        } finally {
            try { lnr.close(); } catch (Exception e) {}
        }

        return result;
    }

    /**
     * Disassembles one method.
     */
    private void
    disassembleMethod(Method method, @Nullable Map<Integer, String> sourceLines) {
        try {

            // One blank line before each method declaration.
            this.println();

            // Print SYNTHETIC notice.
            if (method.accessFlags.is(SYNTHETIC) || method.syntheticAttribute != null) {
                this.println("    // (Synthetic method)");
            }

            // Print BRIDGE notice.
            if (method.accessFlags.is(BRIDGE)) this.println("    // (Bridge method)");

            // Print DEPRECATED notice.
            if (method.deprecatedAttribute != null) this.println("    /** @deprecated */");

            // Print method annotations.
            {
                RuntimeInvisibleAnnotationsAttribute riaa = method.runtimeInvisibleAnnotationsAttribute;
                if (riaa != null) {
                    for (Annotation a : riaa.annotations) this.println("    " + a.toString());
                }
            }
            {
                RuntimeVisibleAnnotationsAttribute rvaa = method.runtimeVisibleAnnotationsAttribute;
                if (rvaa != null) {
                    for (Annotation a : rvaa.annotations) this.println("    " + a.toString());
                }
            }

            // Print method access flags.
            {

                // Remove "pseudo modifiers" - these are handled elsewhere.
                AccessFlags maf = (
                    method.accessFlags
                    .remove(SYNTHETIC) // <= Has already been reported above
                    .remove(BRIDGE)    // <= Has already been reported above
                    .remove(VARARGS)   // <= Is handled below
                );

                // Remove redundant modifiers "public abstract" from interface mathods.
                if (method.getClassFile().accessFlags.is(INTERFACE)) {
                    maf = maf.remove(PUBLIC).remove(ABSTRACT);
                }

                Disassembler.this.print("    " + maf);
            }

            // Print formal type parameters.
            MethodTypeSignature mts;
            {
                SignatureAttribute sa = method.signatureAttribute;
                mts = (
                    sa == null
                    ? this.decodeMethodDescriptor(method.descriptor)
                    : this.decodeMethodTypeSignature(sa.signature)
                );
                if (!mts.formalTypeParameters.isEmpty()) {
                    Iterator<FormalTypeParameter> it = mts.formalTypeParameters.iterator();
                    this.print("<" + it.next());
                    while (it.hasNext()) this.print(", " + it.next());
                    this.print(">");
                }
            }

            List<ConstantClassInfo> exceptionNames;
            {
                ExceptionsAttribute ea = method.exceptionsAttribute;
                exceptionNames = ea == null ? Disassembler.NO_CONSTANT_CLASS_INFOS : ea.exceptionNames;
            }

            String functionName = method.name;
            if (
                "<clinit>".equals(functionName)
                && method.accessFlags.is(STATIC)
                && exceptionNames.isEmpty()
                && mts.formalTypeParameters.isEmpty()
                && mts.parameterTypes.isEmpty()
                && mts.returnType == SignatureParser.VOID
                && mts.thrownTypes.isEmpty()
            ) {

                // Need to do NOTHING here because "static" has already been printed, and "{" will be printed later.
                ;
            } else
            if (
                "<init>".equals(functionName)
                && !method.accessFlags.isAny(ABSTRACT, FINAL, INTERFACE, STATIC) // <= forbidden for construtors.
                && mts.formalTypeParameters.isEmpty()
                && mts.returnType == SignatureParser.VOID
            ) {

                // Print constructor name and parameters.
                this.print(method.getClassFile().simpleThisClassName);
                this.printParameters(
                    method.runtimeInvisibleParameterAnnotationsAttribute,
                    method.runtimeVisibleParameterAnnotationsAttribute,
                    method.methodParametersAttribute,
                    mts.parameterTypes,
                    method,
                    (short) 1,
                    method.accessFlags.is(VARARGS)
                );
            } else
            {

                // Print method return type, name and parameters.
                this.print(mts.returnType + " ");
                this.print(functionName);
                this.printParameters(
                    method.runtimeInvisibleParameterAnnotationsAttribute,
                    method.runtimeVisibleParameterAnnotationsAttribute,
                    method.methodParametersAttribute,
                    mts.parameterTypes,
                    method,
                    method.accessFlags.is(STATIC) ? (short) 0 : (short) 1, // firstIndex
                    method.accessFlags.is(VARARGS)                         // varargs
                );
            }

            // Print thrown types.
            if (!mts.thrownTypes.isEmpty()) {
                Iterator<ThrowsSignature> it = mts.thrownTypes.iterator();
                this.print(" throws " + it.next());
                while (it.hasNext()) this.print(", " + it.next());
            } else
            if (!exceptionNames.isEmpty()) {
                Iterator<ConstantClassInfo> it = exceptionNames.iterator();
                this.print(" throws " + it.next());
                while (it.hasNext()) this.print(", " + it.next());
            }

            // Annotation default.
            {
                AnnotationDefaultAttribute ada = method.annotationDefaultAttribute;
                if (ada != null) this.print("default " + ada.defaultValue);
            }

            // Code.
            {
                CodeAttribute ca = method.codeAttribute;
                if (ca == null) {
                    this.println(";");
                } else {
                    this.println(" {");
                    try {
                        new BytecodeDisassembler(
                            new ByteArrayInputStream(ca.code),
                            ca.exceptionTable,
                            ca.lineNumberTableAttribute,
                            ca.stackMapTableAttribute,
                            sourceLines,
                            method,
                            mts.parameterTypes.toArray(new SignatureParser.TypeSignature[mts.parameterTypes.size()]),
                            this
                        ).disassembleBytecode(this.pw);
                    } catch (IOException ignored) {
                        ;
                    }
                    this.println("    }");

                    this.printAttributes(ca.unprocessedAttributes, ca.allAttributes, "    ", AttributeContext.METHOD);
                }
            }

            // Print method attributes.
            this.printAttributes(
                method.unprocessedAttributes,
                method.allAttributes,
                "    // ",
                AttributeContext.METHOD
            );
        } catch (RuntimeException rte) {
            throw new RuntimeException("Method '" + method.name + "' " + method.descriptor, rte);
        }
    }

    private void
    disassembleFields(List<Field> fields) {
        for (Field field : fields) {
            this.println();

            // Print field annotations.
            {
                RuntimeInvisibleAnnotationsAttribute riaa = field.runtimeInvisibleAnnotationsAttribute;
                if (riaa != null) {
                    for (Annotation a : riaa.annotations) this.println("    " + a);
                }
            }
            {
                RuntimeVisibleAnnotationsAttribute rvaa = field.runtimeVisibleAnnotationsAttribute;
                if (rvaa != null) {
                    for (Annotation a : rvaa.annotations) this.println("    " + a);
                }
            }

            // print SYNTHETIC notice.
            if (field.accessFlags.is(SYNTHETIC) || field.syntheticAttribute != null) {
                this.println("    // (Synthetic field)");
            }

            // Print DEPRECATED notice.
            if (field.deprecatedAttribute != null) this.println("    /** @deprecated */");

            // Print field access flags, type, name and initializer.
            {
                SignatureAttribute sa = field.signatureAttribute;

                TypeSignature typeSignature = (
                    sa != null
                    ? this.decodeFieldTypeSignature(sa.signature)
                    : this.decodeFieldDescriptor(field.descriptor)
                );

                String prefix = field.accessFlags.remove(SYNTHETIC).toString() + typeSignature;

                ConstantValueAttribute cva = field.constantValueAttribute;
                if (cva == null) {
                    this.printf("    %-40s %s;%n", prefix, field.name);
                } else {
                    this.printf("    %-40s %-15s = %s;%n", prefix, field.name, cva.constantValue);
                }
            }

            // Print field attributes.
            this.printAttributes(field.unprocessedAttributes, field.allAttributes, "    // ", AttributeContext.FIELD);
        }
    }

    private static String
    toString(InnerClassesAttribute.ClasS c) {

        ConstantClassInfo oci   = c.outerClassInfo;
        ConstantClassInfo ici   = c.innerClassInfo;
        AccessFlags       icafs = c.innerClassAccessFlags;

        // Notice: "c.innerName" is apparently useless; it seems to always equal the last component of the
        // "c.innerClassInfo.name".

        // Hide ABSTRACT and STATIC flags for interfaces.
        if (icafs.is(INTERFACE)) {
            icafs = icafs.remove(ABSTRACT).remove(STATIC);
        }

        return (
            oci == null
            ? "anonymous class " + ici
            : oci + " { " + Disassembler.typeAccessFlagsToString(icafs) + ici + " }"
        );
    }

    /**
     * Prints the <em>unprocessedAttributes</em>, or, iff {@link Disassembler#verbose}, <em>allAttributes</em>.
     */
    private void
    printAttributes(
        List<Attribute>  unprocessedAttributes,
        List<Attribute>  allAttributes,
        String           prefix,
        AttributeContext context
    ) {
        List<Attribute> tmp = this.printAllAttributes ? allAttributes : unprocessedAttributes;

        if (tmp.isEmpty()) return;

        tmp = new ArrayList<Attribute>(tmp);

        Collections.sort(tmp, new Comparator<Attribute>() {

            @NotNullByDefault(false) @Override public int
            compare(Attribute a1, Attribute a2) { return a1.getName().compareTo(a2.getName()); }
        });

        this.println(prefix + (this.printAllAttributes ? "All attributes:" : "Unprocessed attributes:"));
        PrintAttributeVisitor visitor = new PrintAttributeVisitor(prefix + "  ", context);
        for (Attribute a : tmp) a.accept(visitor);
    }

    /**
     * Prints an {@link Attribute}.
     */
    public
    class PrintAttributeVisitor implements AttributeVisitor {

        private final String           prefix;
        private final AttributeContext context;

        public
        PrintAttributeVisitor(String prefix, AttributeContext context) {
            this.prefix  = prefix;
            this.context = context;
        }

        @Override public void
        visit(AnnotationDefaultAttribute ada) {
            Disassembler.this.println(this.prefix + "AnnotationDefault:");
            Disassembler.this.println(this.prefix + "  " + ada.defaultValue.toString());
        }

        @Override public void
        visit(BootstrapMethodsAttribute bma) {
            Disassembler.this.println(this.prefix + "BootstrapMethods:");
            for (BootstrapMethod bm : bma.bootstrapMethods) {
                Disassembler.this.println(this.prefix + "  " + bm);
            }
        }

        @Override public void
        visit(CodeAttribute ca) {
            Disassembler.this.println(this.prefix + "Code:");
            Disassembler.this.println(this.prefix + "  max_locals = " + ca.maxLocals);
            Disassembler.this.println(this.prefix + "  max_stack = " + ca.maxStack);

            Disassembler.this.println(this.prefix + "  code = {");
            this.print(ca.code);
            Disassembler.this.println(this.prefix + "  }");

            Disassembler.this.printAttributes(
                ca.unprocessedAttributes,
                ca.allAttributes,
                this.prefix + "  ",
                AttributeContext.METHOD
            );
        }

        private void
        print(byte[] data) {
            for (int i = 0; i < data.length; i += 32) {
                Disassembler.this.print(this.prefix + "   ");
                for (int j = 0; j < 32; ++j) {
                    int idx = i + j;
                    if (idx >= data.length) break;
                    Disassembler.this.printf("%c%02x", j == 16 ? '-' : ' ', 0xff & data[idx]);
                }
                Disassembler.this.println();
            }
        }

        @Override public void
        visit(ConstantValueAttribute cva) {
            Disassembler.this.println(this.prefix + "ConstantValue:");
            Disassembler.this.println(this.prefix + "  constant_value = " + cva.constantValue);
        }

        @Override public void
        visit(DeprecatedAttribute da) {
            Disassembler.this.println(this.prefix + "DeprecatedAttribute:");
            Disassembler.this.println(this.prefix + "  -");
        }

        @Override public void
        visit(EnclosingMethodAttribute ema) {
            Disassembler.this.println(this.prefix + "EnclosingMethod:");
            ConstantNameAndTypeInfo m = ema.method;
            Disassembler.this.println(this.prefix + "  class/method = " + (
                m == null
                ? "(none)"
                : Disassembler.this.decodeMethodDescriptor(m.descriptor.bytes).toString(
                    ema.clasS.toString(),
                    m.name.bytes
                )
            ));
        }

        @Override public void
        visit(ExceptionsAttribute ea) {
            Disassembler.this.println(this.prefix + "Exceptions:");
            for (ConstantClassInfo en : ea.exceptionNames) {
                Disassembler.this.println(this.prefix + "  " + en);
            }
        }

        @Override public void
        visit(InnerClassesAttribute ica) {
            Disassembler.this.println(this.prefix + "InnerClasses:");
            for (InnerClassesAttribute.ClasS c : ica.classes) {
                Disassembler.this.println(this.prefix + "  " + Disassembler.toString(c));
            }
        }

        @Override public void
        visit(LineNumberTableAttribute lnta) {
            Disassembler.this.println(this.prefix + "LineNumberTable:");
            for (LineNumberTableEntry e : lnta.entries) {
                Disassembler.this.println(this.prefix + "  " + e.startPc + " => Line " + e.lineNumber);
            }
        }

        @Override public void
        visit(LocalVariableTableAttribute lvta) {
            Disassembler.this.println(this.prefix + "LocalVariableTable:");
            for (LocalVariableTableAttribute.Entry e : lvta.entries) {
                Disassembler.this.println(
                    this.prefix
                    + "  "
                    + (0xffff & e.startPC)
                    + "+"
                    + e.length
                    + ": "
                    + e.index
                    + " = "
                    + Disassembler.this.decodeFieldDescriptor(e.descriptor)
                    + " "
                    + e.name
                );
            }
        }

        @Override public void
        visit(LocalVariableTypeTableAttribute lvtta) {
            Disassembler.this.println(this.prefix + "LocalVariableTypeTable:");
            for (LocalVariableTypeTableAttribute.Entry e : lvtta.entries) {
                Disassembler.this.println(
                    this.prefix
                    + "  "
                    + e.startPC
                    + "+"
                    + e.length
                    + ": "
                    + e.index
                    + " = "
                    + Disassembler.this.decodeFieldTypeSignature(e.signature)
                    + " "
                    + e.name
                );
            }
        }

        @Override public void
        visit(MethodParametersAttribute mpa) {
            Disassembler.this.println(this.prefix + "MethodParameters:");
            for (Parameter p : mpa.parameters) {
                Disassembler.this.println(this.prefix + "  " + p);
            }
        }

        @Override public void
        visit(ModulePackagesAttribute mpa) {
            Disassembler.this.println(this.prefix + "ModulePackages:");
            for (String packagE : mpa.packages) {
                Disassembler.this.println(this.prefix + "  " + packagE.replace('/', '.'));
            }
        }

        @Override public void
        visit(RuntimeInvisibleAnnotationsAttribute riaa) {
            Disassembler.this.println(this.prefix + "RuntimeInvisibleAnnotations:");
            for (Annotation a : riaa.annotations) {
                Disassembler.this.println(this.prefix + "  " + a);
            }
        }

        @Override public void
        visit(RuntimeVisibleAnnotationsAttribute rvaa) {
            Disassembler.this.println(this.prefix + "RuntimeVisibleAnnotations:");
            for (Annotation a : rvaa.annotations) {
                Disassembler.this.println(this.prefix + "  " + a);
            }
        }

        @Override public void
        visit(RuntimeInvisibleParameterAnnotationsAttribute ripaa) {
            Disassembler.this.println(this.prefix + "RuntimeInvisibleParameterAnnotations:");
            for (ParameterAnnotation pa : ripaa.parameterAnnotations) {
                for (Annotation a : pa.annotations) {
                    Disassembler.this.println(this.prefix + "  " + a);
                }
            }
        }

        @Override public void
        visit(RuntimeVisibleParameterAnnotationsAttribute rvpaa) {
            Disassembler.this.println(this.prefix + "RuntimeVisibleParameterAnnotations:");
            for (ParameterAnnotation pa : rvpaa.parameterAnnotations) {
                for (Annotation a : pa.annotations) {
                    Disassembler.this.println(this.prefix + "  " + a);
                }
            }
        }

        @Override public void
        visit(SignatureAttribute sa) {
            Disassembler.this.println(this.prefix + "Signature:");
            switch (this.context) {
            case CLASS:
                Disassembler.this.println(
                    this.prefix
                    + "  "
                    + Disassembler.this.decodeClassSignature(sa.signature).toString("[this-class]")
                );
                break;
            case FIELD:
                Disassembler.this.println(
                    this.prefix
                    + "  "
                    + Disassembler.this.decodeFieldTypeSignature(sa.signature).toString()
                );
                break;
            case METHOD:
                Disassembler.this.println(
                    this.prefix
                    + "  "
                    + Disassembler.this.decodeMethodTypeSignature(
                        sa.signature
                    ).toString("[declaring-class]", "[this-method]")
                );
                break;
            }
        }

        @Override public void
        visit(SourceFileAttribute sfa) {
            Disassembler.this.println(this.prefix + "SourceFile:");
            Disassembler.this.println(this.prefix + "  " + sfa.sourceFile);
        }

        @Override public void
        visit(StackMapTableAttribute smta) {
            Disassembler.this.println(this.prefix + "StackMapTable:");
            int bytecodeOffset = -1;
            for (StackMapFrame smf : smta.entries) {
                bytecodeOffset += 1 + smf.offsetDelta;
                Disassembler.this.println(this.prefix + "  " + bytecodeOffset + ": " + smf);
            }
        }

        @Override public void
        visit(SyntheticAttribute sa) {
            Disassembler.this.println(this.prefix + "Synthetic:");
            Disassembler.this.println(this.prefix + " -");
        }

        @Override public void
        visit(UnknownAttribute ua) {
            Disassembler.this.println(this.prefix + ua.name + ":");
            Disassembler.this.println(this.prefix + "  data = {");
            this.print(ua.info);
            Disassembler.this.println(this.prefix + "}");
        }
    }

    private void
    printParameters(
        @Nullable RuntimeInvisibleParameterAnnotationsAttribute ripaa,
        @Nullable RuntimeVisibleParameterAnnotationsAttribute   rvpaa,
        @Nullable MethodParametersAttribute                     mpa,
        List<TypeSignature>                                     parameterTypes,
        Method                                                  method,
        short                                                   firstIndex,
        boolean                                                 varargs
    ) {
        Iterator<ParameterAnnotation> ipas = (
            ripaa == null
            ? Disassembler.NO_PARAMETER_ANNOTATIONS
            : ripaa.parameterAnnotations
        ).iterator();
        this.print("(");
        Iterator<ParameterAnnotation> vpas = (
            rvpaa == null
            ? Disassembler.NO_PARAMETER_ANNOTATIONS
            : rvpaa.parameterAnnotations
        ).iterator();
        Iterator<MethodParametersAttribute.Parameter> ps = (
            mpa == null
            ? Collections.<MethodParametersAttribute.Parameter>emptyList()
            : mpa.parameters
        ).iterator();

        Iterator<TypeSignature> it = parameterTypes.iterator();
        if (it.hasNext()) {
            for (;;) {
                final TypeSignature pts = it.next();

                // Parameter annotations.
                if (ipas.hasNext()) {
                    for (Annotation a : ipas.next().annotations) this.print(a + " ");
                }
                if (vpas.hasNext()) {
                    for (Annotation a : vpas.next().annotations) this.print(a + " ");
                }

                // Parameter access flags (FINAL, SYNTHETIC and/or MANDATED), see JVMS9 4.7.24.
                if (ps.hasNext()) {
                    this.print(ps.next().accessFlags.toString());
                }

                // Parameter type.
                if (varargs && !it.hasNext() && pts instanceof ArrayTypeSignature) {
                    this.print(((ArrayTypeSignature) pts).componentTypeSignature + "...");
                } else {
                    this.print(pts.toString());
                }

                // Parameter name.
                this.print(' ' + this.getLocalVariable(firstIndex, 0, method).name);

                if (!it.hasNext()) break;
                firstIndex++;
                this.print(", ");
            }
        }

        this.print(")");
    }

    /**
     * @return Metadata about the local variable at the given index and instruction offset in the given <em>method</em>
     */
    LocalVariable
    getLocalVariable(short localVariableIndex, int instructionOffset, Method method) {

        // Calculate index of first parameter.
        int firstParameter = method.accessFlags.is(STATIC) ? 0 : 1;
        if (localVariableIndex < firstParameter) {
            return new LocalVariable(null, "this");
        }

        // Compute the method's parameter types from its type signature or descriptor.
        List<TypeSignature> parameterTypes;
        {
            SignatureAttribute  sa  = method.signatureAttribute;
            MethodTypeSignature mts = (
                sa != null
                ? this.decodeMethodTypeSignature(sa.signature)
                : this.decodeMethodDescriptor(method.descriptor)
            );
            parameterTypes = mts.parameterTypes;
        }

        // Determine whether the local variable represents a constructor or method parameter, or a "normal" local
        // variable.
        int parameterIndex; // -1 means we have a non-parameter local variable.
        {
            int plvi = firstParameter; // Parameter's local variable index
            parameterIndex = -1;
            for (int pi = 0; pi < parameterTypes.size(); pi++) {

                if (plvi == localVariableIndex) {
                    parameterIndex = pi;
                    break;
                }

                TypeSignature pt = parameterTypes.get(pi);

                plvi += pt == SignatureParser.LONG || pt == SignatureParser.DOUBLE ? 2 : 1;
            }
        }

        // Compose a nice "default name" for the local variable.
        String defaultName = parameterIndex != -1 ? "p" + parameterIndex : "v" + localVariableIndex;

        CodeAttribute ca = method.codeAttribute;
        if (ca != null) {

            // Attempt to determine the local variable's type and name from the "LocalVariableTypeTable" attribute.
            LocalVariableTypeTableAttribute lvtta = ca.localVariableTypeTableAttribute;
            if (lvtta != null) {
                for (LocalVariableTypeTableAttribute.Entry lvtte : lvtta.entries) {
                    if (
                        instructionOffset >= lvtte.startPC
                        && instructionOffset <= lvtte.startPC + lvtte.length
                        && localVariableIndex == lvtte.index
                    ) {
                        return new LocalVariable(
                            this.decodeFieldTypeSignature(lvtte.signature),
                            this.showVariableNames ? lvtte.name : defaultName
                        );
                    }
                }
            }

            // Attempt to determine the local variable's type and name from the "LocalVariableTable" attribute.
            LocalVariableTableAttribute lvta = ca.localVariableTableAttribute;
            if (lvta != null) {
                for (LocalVariableTableAttribute.Entry lvte : lvta.entries) {
                    if (
                        instructionOffset >= lvte.startPC
                        && instructionOffset <= lvte.startPC + lvte.length
                        && localVariableIndex == lvte.index
                    ) {
                        return new LocalVariable(
                            this.decodeFieldDescriptor(lvte.descriptor),
                            this.showVariableNames ? lvte.name : defaultName
                        );
                    }
                }
            }
        }

        // Attempt to determine the local variable's name from the method's "MethodParameters" attribute (JVMS9 4.7.24).
        MethodParametersAttribute mpa = method.methodParametersAttribute;
        if (parameterIndex != -1 && mpa != null) {

            return new LocalVariable(
                parameterTypes.get(parameterIndex),                                            // typeSignature
                this.showVariableNames ? mpa.parameters.get(parameterIndex).name : defaultName // name
            );
        }

        return new LocalVariable(null, defaultName);
    }

    private ClassSignature
    decodeClassSignature(String cs) {
        try {
            return this.signatureParser.decodeClassSignature(cs);
        } catch (SignatureException e) {
            this.error("Decoding class signature '" + cs + "': " + e.getMessage());
            return new ClassSignature(
                Disassembler.NO_FORMAL_TYPE_PARAMETERS,
                this.signatureParser.object,
                Disassembler.NO_CLASS_TYPE_SIGNATURES
            );
        }
    }

    private FieldTypeSignature
    decodeFieldTypeSignature(String fs) {
        try {
            return this.signatureParser.decodeFieldTypeSignature(fs);
        } catch (SignatureException e) {
            this.error("Decoding field type signature '" + fs + "': " + e.getMessage());
            return this.signatureParser.object;
        }
    }

    /**
     * Exception-proof wrapper for {@link SignatureParser#decodeMethodTypeSignature(String)}.
     */
    MethodTypeSignature
    decodeMethodTypeSignature(String ms) {
        try {
            return this.signatureParser.decodeMethodTypeSignature(ms);
        } catch (SignatureException e) {
            this.error("Decoding method type signature '" + ms + "': " + e.getMessage());
            return new MethodTypeSignature(
                Disassembler.NO_FORMAL_TYPE_PARAMETERS,
                Disassembler.NO_TYPE_SIGNATURES,
                SignatureParser.VOID,
                Disassembler.NO_THROWS_SIGNATURES
            );
        }
    }

    /**
     * Exception-proof wrapper for {@link SignatureParser#decodeFieldDescriptor(String)}.
     */
    TypeSignature
    decodeFieldDescriptor(String fd) {
        try {
            return this.signatureParser.decodeFieldDescriptor(fd);
        } catch (SignatureException e) {
            this.error("Decoding field descriptor '" + fd + "': " + e.getMessage());
            return SignatureParser.INT;
        }
    }

    /**
     * Exception-proof wrapper for {@link SignatureParser#decodeMethodDescriptor(String)}.
     */
    MethodTypeSignature
    decodeMethodDescriptor(String md) {
        try {
            return this.signatureParser.decodeMethodDescriptor(md);
        } catch (SignatureException e) {
            this.error("Decoding method descriptor '" + md + "': " + e.getMessage());
            return new MethodTypeSignature(
                Disassembler.NO_FORMAL_TYPE_PARAMETERS,
                Disassembler.NO_TYPE_SIGNATURES,
                SignatureParser.VOID,
                Disassembler.NO_THROWS_SIGNATURES
            );
        }
    }

    /**
     * Representation of a local variable reference in the {@code Code} attribute.
     */
    class LocalVariable {

        /**
         * The descriptor or the type signature of this local variable. A {@code null} value indicates that the
         * type of this local variable should not be considered and/or reported; this is typically the case for the
         * magic 'this' local variable.
         */
        @Nullable final TypeSignature typeSignature;

        /**
         * The name of this local variable.
         */
        final String name;

        LocalVariable(@Nullable TypeSignature typeSignature, String name) {
            this.typeSignature = typeSignature;
            this.name          = name;
        }

        @Override public String
        toString() {
            TypeSignature ts = this.typeSignature;
            return ts == null ? '[' + this.name + ']' : '[' + ts.toString() + ' ' + this.name + ']';
        }
    }

    private void
    error(String message) { this.println("*** Error: " + message); }

    private static String
    typeAccessFlagsToString(AccessFlags af) {

        String result = af.toString();

        // For enums, annotations and interfaces, the keyword ("enum", "@" and "interface") is already part of the
        // result, but not for classes.
        if (!af.is(ENUM) && !af.is(ANNOTATION) && !af.is(INTERFACE)) {
            result += "class ";
        }

        return result;
    }
}
