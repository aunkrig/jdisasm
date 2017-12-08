
// *** Disassembly of 'target\classes\de\unkrig\jdisasm\Disassembler.class'.

// Class file version = 50.0 (J2SE 6.0)

package de.unkrig.jdisasm;

public class Disassembler {

    // Enclosing/enclosed types:
    //   ClassFile { public static class ClassFile$AccessFlags }
    //   ClassFile$AccessFlags { static final enum ClassFile$AccessFlags$FlagType }
    //   ClassFile { public class ClassFile$Annotation }
    //   ClassFile { public class ClassFile$AnnotationDefaultAttribute }
    //   ClassFile { public interface ClassFile$Attribute }
    //   ClassFile { public interface ClassFile$AttributeVisitor }
    //   ClassFile { public static final class ClassFile$CodeAttribute }
    //   ClassFile { public static final class ClassFile$ConstantValueAttribute }
    //   ClassFile { public static final class ClassFile$EnclosingMethodAttribute }
    //   ClassFile { public static class ClassFile$ExceptionTableEntry }
    //   ClassFile { public static final class ClassFile$ExceptionsAttribute }
    //   ClassFile { public class ClassFile$Field }
    //   ClassFile { public static final class ClassFile$InnerClassesAttribute }
    //   ClassFile$InnerClassesAttribute { public static class ClassFile$InnerClassesAttribute$ClasS }
    //   ClassFile { public class ClassFile$LineNumberTableAttribute }
    //   ClassFile { public class ClassFile$LocalVariableTableAttribute }
    //   ClassFile$LocalVariableTableAttribute { class ClassFile$LocalVariableTableAttribute$Entry }
    //   ClassFile { public static class ClassFile$LocalVariableTypeTableAttribute }
    //   ClassFile$LocalVariableTypeTableAttribute { public static class ClassFile$LocalVariableTypeTableAttribute$Entry }
    //   ClassFile { public class ClassFile$Method }
    //   ClassFile { public class ClassFile$ParameterAnnotation }
    //   ClassFile { public class ClassFile$RuntimeInvisibleAnnotationsAttribute }
    //   ClassFile { public class ClassFile$RuntimeInvisibleParameterAnnotationsAttribute }
    //   ClassFile { public class ClassFile$RuntimeVisibleAnnotationsAttribute }
    //   ClassFile { public class ClassFile$RuntimeVisibleParameterAnnotationsAttribute }
    //   ClassFile { public static final class ClassFile$SignatureAttribute }
    //   ClassFile { public static class ClassFile$SourceFileAttribute }
    //   ConstantPool { public class ConstantPool$ConstantClassInfo }
    //   ConstantPool { public class ConstantPool$ConstantNameAndTypeInfo }
    //   ConstantPool { public interface ConstantPool$ConstantPoolEntry }
    //   ConstantPool { public static class ConstantPool$ConstantUtf8Info }
    //   [local class] { class Disassembler$1 }
    //   [local class] { class Disassembler$2 }
    //   Disassembler { private static final enum Disassembler$AttributeContext }
    //   Disassembler { class Disassembler$LocalVariable }
    //   Disassembler { public class Disassembler$PrintAttributeVisitor }
    //   SignatureParser { public static class SignatureParser$ArrayTypeSignature }
    //   SignatureParser { public static class SignatureParser$ClassSignature }
    //   SignatureParser { public static class SignatureParser$ClassTypeSignature }
    //   SignatureParser { public interface SignatureParser$FieldTypeSignature }
    //   SignatureParser { public static class SignatureParser$FormalTypeParameter }
    //   SignatureParser { public static class SignatureParser$MethodTypeSignature }
    //   SignatureParser { public interface SignatureParser$Options }
    //   SignatureParser { public static class SignatureParser$SignatureException }
    //   SignatureParser { public interface SignatureParser$ThrowsSignature }
    //   SignatureParser { public interface SignatureParser$TypeSignature }

    private static final java.util.List<ConstantPool$ConstantClassInfo> NO_CONSTANT_CLASS_INFOS;

    private static final java.util.List<SignatureParser$ThrowsSignature> NO_THROWS_SIGNATURES;

    private static final java.util.List<SignatureParser$TypeSignature> NO_TYPE_SIGNATURES;

    private static final java.util.List<SignatureParser$ClassTypeSignature> NO_CLASS_TYPE_SIGNATURES;

    private static final java.util.List<SignatureParser$FormalTypeParameter> NO_FORMAL_TYPE_PARAMETERS;

    private static final java.util.List<ClassFile$ParameterAnnotation> NO_PARAMETER_ANNOTATIONS;

    private java.io.PrintWriter              pw;

    boolean                                  verbose;

    @de.unkrig.commons.nullanalysis.Nullable
    private java.io.File                     sourceDirectory;

    boolean                                  hideLines;

    private boolean                          hideVars;

    boolean                                  symbolicLabels;

    private SignatureParser                  signatureParser;

    private static final java.util.regex.Pattern IS_URL;

    // (Synthetic field)
    static final boolean                     $assertionsDisabled;

    static  {
        // Line 120
        ldc             Disassembler
        invokevirtual   Class.desiredAssertionStatus() => boolean
        ifne            L1
        iconst_1
        goto            L2
L1
        iconst_0
L2
        putstatic       boolean Disassembler.$assertionsDisabled
        // Line 123
        invokestatic    java.util.Collections.emptyList() => java.util.List
        putstatic       java.util.List Disassembler.NO_CONSTANT_CLASS_INFOS
        // Line 124
        invokestatic    java.util.Collections.emptyList() => java.util.List
        putstatic       java.util.List Disassembler.NO_THROWS_SIGNATURES
        // Line 125
        invokestatic    java.util.Collections.emptyList() => java.util.List
        putstatic       java.util.List Disassembler.NO_TYPE_SIGNATURES
        // Line 126
        invokestatic    java.util.Collections.emptyList() => java.util.List
        putstatic       java.util.List Disassembler.NO_CLASS_TYPE_SIGNATURES
        // Line 127
        invokestatic    java.util.Collections.emptyList() => java.util.List
        putstatic       java.util.List Disassembler.NO_FORMAL_TYPE_PARAMETERS
        // Line 128
        invokestatic    java.util.Collections.emptyList() => java.util.List
        putstatic       java.util.List Disassembler.NO_PARAMETER_ANNOTATIONS
        // Line 266
        ldc             "\w\w+:.*"
        invokestatic    java.util.regex.Pattern.compile(String) => java.util.regex.Pattern
        putstatic       java.util.regex.Pattern Disassembler.IS_URL
        return
    }

    public static void main(String[] args) throws java.io.IOException {
        // Line 200
        invokestatic    de.unkrig.jdisasm.protocol.zip.Handler.registerMe()
        // Line 202
        new             Disassembler
        dup
        invokespecial   Disassembler()
        astore_1        [Disassembler d]
        // Line 204
        iconst_0
        istore_2        [int i]
        goto            L1
L12
        // Line 205
        aload_0         [String[] args]
        iload_2         [int i]
        aaload
        astore_3        [String arg]
        // Line 206
        aload_3         [String arg]
        iconst_0
        invokevirtual   String.charAt(int) => char
        bipush          45
        if_icmpne       L2
        aload_3         [String arg]
        invokevirtual   String.length() => int
        iconst_1
        if_icmpne       L3
        goto            L2
L3
        // Line 207
        ldc             "-o"
        aload_3         [String arg]
        invokevirtual   String.equals(Object) => boolean
        ifeq            L4
        // Line 208
        aload_1         [Disassembler d]
        new             java.io.FileOutputStream
        dup
        aload_0         [String[] args]
        iinc            [int i] 1
        iload_2         [int i]
        aaload
        invokespecial   java.io.FileOutputStream(String)
        invokevirtual   Disassembler.setOut(java.io.OutputStream)
        // Line 209
        goto            L5
L4
        // Line 210
        ldc             "-verbose"
        aload_3         [String arg]
        invokevirtual   String.equals(Object) => boolean
        ifeq            L6
        // Line 211
        aload_1         [Disassembler d]
        iconst_1
        invokevirtual   Disassembler.setVerbose(boolean)
        // Line 212
        goto            L5
L6
        // Line 213
        ldc             "-src"
        aload_3         [String arg]
        invokevirtual   String.equals(Object) => boolean
        ifeq            L7
        // Line 214
        aload_1         [Disassembler d]
        new             java.io.File
        dup
        aload_0         [String[] args]
        iinc            [int i] 1
        iload_2         [int i]
        aaload
        invokespecial   java.io.File(String)
        invokevirtual   Disassembler.setSourceDirectory(java.io.File)
        // Line 215
        goto            L5
L7
        // Line 216
        ldc             "-hide-lines"
        aload_3         [String arg]
        invokevirtual   String.equals(Object) => boolean
        ifeq            L8
        // Line 217
        aload_1         [Disassembler d]
        iconst_1
        invokevirtual   Disassembler.setHideLines(boolean)
        // Line 218
        goto            L5
L8
        // Line 219
        ldc             "-hide-vars"
        aload_3         [String arg]
        invokevirtual   String.equals(Object) => boolean
        ifeq            L9
        // Line 220
        aload_1         [Disassembler d]
        iconst_1
        invokevirtual   Disassembler.setHideVars(boolean)
        // Line 221
        goto            L5
L9
        // Line 222
        ldc             "-symbolic-labels"
        aload_3         [String arg]
        invokevirtual   String.equals(Object) => boolean
        ifeq            L10
        // Line 223
        aload_1         [Disassembler d]
        iconst_1
        invokevirtual   Disassembler.setSymbolicLabels(boolean)
        // Line 224
        goto            L5
L10
        // Line 225
        ldc             "-help"
        aload_3         [String arg]
        invokevirtual   String.equals(Object) => boolean
        ifeq            L11
        // Line 227
        getstatic       java.io.PrintStream System.out
        ldc             "Prints a disassembly listing of the given JAVA[TM] class files (or STDIN) to%nST"...
        iconst_1
        anewarray       Object
        dup
        iconst_0
        // Line 240
        ldc             Disassembler
        invokevirtual   Class.getName() => String
        aastore
        // Line 227
        invokevirtual   java.io.PrintStream.printf(String, Object[]) => java.io.PrintStream
        pop
        // Line 242
        iconst_0
        invokestatic    System.exit(int)
        // Line 243
        goto            L5
L11
        // Line 245
        getstatic       java.io.PrintStream System.err
        new             StringBuilder
        dup
        ldc             "Unrecognized command line option \""
        invokespecial   StringBuilder(String)
        aload_3         [String arg]
        invokevirtual   StringBuilder.append(String) => StringBuilder
        ldc             "\"; try \"-help\"."
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokevirtual   java.io.PrintStream.println(String)
        // Line 246
        iconst_1
        invokestatic    System.exit(int)
L5
        // Line 204
        iinc            [int i] 1
L1
        iload_2         [int i]
        aload_0         [String[] args]
        arraylength
        if_icmplt       L12
L2
        // Line 249
        iload_2         [int i]
        aload_0         [String[] args]
        arraylength
        if_icmpne       L13
        // Line 250
        aload_1         [Disassembler d]
        getstatic       java.io.InputStream System.in
        invokevirtual   Disassembler.disasm(java.io.InputStream)
        // Line 251
        goto            L14
L18
        // Line 253
        aload_0         [String[] args]
        iload_2         [int i]
        aaload
        astore_3        [String name]
        // Line 254
        ldc             "-"
        aload_3         [String name]
        invokevirtual   String.equals(Object) => boolean
        ifeq            L15
        // Line 255
        aload_1         [Disassembler d]
        getstatic       java.io.InputStream System.in
        invokevirtual   Disassembler.disasm(java.io.InputStream)
        // Line 256
        goto            L16
L15
        // Line 257
        getstatic       java.util.regex.Pattern Disassembler.IS_URL
        aload_3         [String name]
        invokevirtual   java.util.regex.Pattern.matcher(CharSequence) => java.util.regex.Matcher
        invokevirtual   java.util.regex.Matcher.matches() => boolean
        ifeq            L17
        // Line 258
        aload_1         [Disassembler d]
        new             java.net.URL
        dup
        aload_3         [String name]
        invokespecial   java.net.URL(String)
        invokevirtual   Disassembler.disasm(java.net.URL)
        // Line 259
        goto            L16
L17
        // Line 261
        aload_1         [Disassembler d]
        new             java.io.File
        dup
        aload_3         [String name]
        invokespecial   java.io.File(String)
        invokevirtual   Disassembler.disasm(java.io.File)
L16
        // Line 252
        iinc            [int i] 1
L13
        iload_2         [int i]
        aload_0         [String[] args]
        arraylength
        if_icmplt       L18
L14
        // Line 265
        return
    }

    public Disassembler() {
        // Line 268
        aload_0         [this]
        invokespecial   Object()
        // Line 135
        aload_0         [this]
        new             java.io.PrintWriter
        dup
        getstatic       java.io.PrintStream System.out
        invokespecial   java.io.PrintWriter(java.io.OutputStream)
        putfield        java.io.PrintWriter Disassembler.pw
        // Line 151
        aload_0         [this]
        new             SignatureParser
        dup
        invokespecial   SignatureParser()
        putfield        SignatureParser Disassembler.signatureParser
        // Line 268
        return
    }

    public void setOut(java.io.Writer writer) {
        // Line 274
        aload_0         [this]
        aload_1         [java.io.Writer writer]
        instanceof      java.io.PrintWriter
        ifeq            L1
        aload_1         [java.io.Writer writer]
        checkcast       java.io.PrintWriter
        goto            L2
L1
        new             java.io.PrintWriter
        dup
        aload_1         [java.io.Writer writer]
        invokespecial   java.io.PrintWriter(java.io.Writer)
L2
        putfield        java.io.PrintWriter Disassembler.pw
        return
    }

    public void setOut(java.io.OutputStream stream) {
        // Line 280
        aload_0         [this]
        new             java.io.PrintWriter
        dup
        aload_1         [java.io.OutputStream stream]
        invokespecial   java.io.PrintWriter(java.io.OutputStream)
        putfield        java.io.PrintWriter Disassembler.pw
        return
    }

    public void setOut(java.io.OutputStream stream, String charsetName) throws java.io.UnsupportedEncodingException {
        // Line 287
        aload_0         [this]
        new             java.io.PrintWriter
        dup
        new             java.io.OutputStreamWriter
        dup
        aload_1         [java.io.OutputStream stream]
        aload_2         [String charsetName]
        invokespecial   java.io.OutputStreamWriter(java.io.OutputStream, String)
        invokespecial   java.io.PrintWriter(java.io.Writer)
        putfield        java.io.PrintWriter Disassembler.pw
        // Line 288
        return
    }

    public void setVerbose(boolean verbose) {
        // Line 294
        aload_0         [this]
        iload_1         [boolean verbose]
        putfield        boolean Disassembler.verbose
        return
    }

    public void setSourceDirectory(@de.unkrig.commons.nullanalysis.Nullable java.io.File sourceDirectory) {
        // Line 301
        aload_0         [this]
        aload_1         [java.io.File sourceDirectory]
        putfield        java.io.File Disassembler.sourceDirectory
        return
    }

    public void setHideLines(boolean hideLines) {
        // Line 307
        aload_0         [this]
        iload_1         [boolean hideLines]
        putfield        boolean Disassembler.hideLines
        return
    }

    public void setHideVars(boolean hideVars) {
        // Line 313
        aload_0         [this]
        iload_1         [boolean hideVars]
        putfield        boolean Disassembler.hideVars
        return
    }

    public void setSymbolicLabels(boolean symbolicLabels) {
        // Line 319
        aload_0         [this]
        iload_1         [boolean symbolicLabels]
        putfield        boolean Disassembler.symbolicLabels
        return
    }

    private void print(String s) {
        // Line 321
        aload_0         [this]
        getfield        java.io.PrintWriter Disassembler.pw
        aload_1         [String s]
        invokevirtual   java.io.PrintWriter.print(String)
        return
    }

    private void println() {
        // Line 322
        aload_0         [this]
        getfield        java.io.PrintWriter Disassembler.pw
        invokevirtual   java.io.PrintWriter.println()
        return
    }

    private void println(String s) {
        // Line 323
        aload_0         [this]
        getfield        java.io.PrintWriter Disassembler.pw
        aload_1         [String s]
        invokevirtual   java.io.PrintWriter.println(String)
        return
    }

    private void printf(String format, Object... args) {
        // Line 324
        aload_0         [this]
        getfield        java.io.PrintWriter Disassembler.pw
        aload_1         [String format]
        aload_2         [Object[] args]
        invokevirtual   java.io.PrintWriter.printf(String, Object[]) => java.io.PrintWriter
        pop
        return
    }

    public void disasm(java.io.File file) throws java.io.IOException {
        // Line 331
        new             java.io.FileInputStream
        dup
        aload_1         [java.io.File file]
        invokespecial   java.io.FileInputStream(java.io.File)
        astore_2        [java.io.InputStream is]
        try {
            try {
                // Line 333
                aload_0         [this]
                getfield        java.io.PrintWriter Disassembler.pw
                invokevirtual   java.io.PrintWriter.println()
                // Line 334
                aload_0         [this]
                getfield        java.io.PrintWriter Disassembler.pw
                new             StringBuilder
                dup
                ldc_w           "// *** Disassembly of '"
                invokespecial   StringBuilder(String)
                aload_1         [java.io.File file]
                invokevirtual   StringBuilder.append(Object) => StringBuilder
                ldc_w           "'."
                invokevirtual   StringBuilder.append(String) => StringBuilder
                invokevirtual   StringBuilder.toString() => String
                invokevirtual   java.io.PrintWriter.println(String)
                // Line 335
                aload_0         [this]
                aload_2         [java.io.InputStream is]
                invokevirtual   Disassembler.disasm(java.io.InputStream)
            } catch (java.io.IOException => L4, RuntimeException => L5)
            // Line 336
            goto            L1
L4
            astore_3        [java.io.IOException ioe]
            // Line 337
            new             java.io.IOException
            dup
            new             StringBuilder
            dup
            ldc_w           "Disassembling '"
            invokespecial   StringBuilder(String)
            aload_1         [java.io.File file]
            invokevirtual   StringBuilder.append(Object) => StringBuilder
            ldc_w           "': "
            invokevirtual   StringBuilder.append(String) => StringBuilder
            aload_3         [java.io.IOException ioe]
            invokevirtual   java.io.IOException.getMessage() => String
            invokevirtual   StringBuilder.append(String) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            invokespecial   java.io.IOException(String)
            astore          [java.io.IOException ioe2]
            // Line 338
            aload           [java.io.IOException ioe2]
            aload_3         [java.io.IOException ioe]
            invokevirtual   java.io.IOException.initCause(Throwable) => Throwable
            pop
            // Line 339
            aload           [java.io.IOException ioe2]
            athrow
L5
            // Line 340
            astore_3        [RuntimeException re]
            // Line 341
            new             RuntimeException
            dup
            new             StringBuilder
            dup
            ldc_w           "Disassembling '"
            invokespecial   StringBuilder(String)
            aload_1         [java.io.File file]
            invokevirtual   StringBuilder.append(Object) => StringBuilder
            ldc_w           "': "
            invokevirtual   StringBuilder.append(String) => StringBuilder
            aload_3         [RuntimeException re]
            invokevirtual   RuntimeException.getMessage() => String
            invokevirtual   StringBuilder.append(String) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            aload_3         [RuntimeException re]
            invokespecial   RuntimeException(String, Throwable)
            athrow
        } catch ([all exceptions] => L6)
L6
        // Line 342
        astore          [v4]
        try {
            // Line 343
            aload_2         [java.io.InputStream is]
            invokevirtual   java.io.InputStream.close()
        } catch (java.io.IOException => L7)
        goto            L2
L7
        astore          [v5]
L2
        // Line 344
        aload           [v4]
        athrow
L1
        try {
            // Line 343
            aload_2         [java.io.InputStream is]
            invokevirtual   java.io.InputStream.close()
        } catch (java.io.IOException => L8)
        goto            L3
L8
        astore          [v5]
L3
        // Line 345
        return
    }

    public void disasm(java.net.URL location) throws java.io.IOException {
        // Line 352
        aload_1         [java.net.URL location]
        invokevirtual   java.net.URL.openConnection() => java.net.URLConnection
        invokevirtual   java.net.URLConnection.getInputStream() => java.io.InputStream
        astore_2        [java.io.InputStream is]
        try {
            try {
                // Line 354
                aload_0         [this]
                getfield        java.io.PrintWriter Disassembler.pw
                invokevirtual   java.io.PrintWriter.println()
                // Line 355
                aload_0         [this]
                getfield        java.io.PrintWriter Disassembler.pw
                new             StringBuilder
                dup
                ldc_w           "// *** Disassembly of '"
                invokespecial   StringBuilder(String)
                aload_1         [java.net.URL location]
                invokevirtual   StringBuilder.append(Object) => StringBuilder
                ldc_w           "'."
                invokevirtual   StringBuilder.append(String) => StringBuilder
                invokevirtual   StringBuilder.toString() => String
                invokevirtual   java.io.PrintWriter.println(String)
                // Line 356
                aload_0         [this]
                aload_2         [java.io.InputStream is]
                invokevirtual   Disassembler.disasm(java.io.InputStream)
            } catch (java.io.IOException => L4, RuntimeException => L5)
            // Line 357
            goto            L1
L4
            astore_3        [java.io.IOException ioe]
            // Line 358
            new             java.io.IOException
            dup
            new             StringBuilder
            dup
            ldc_w           "Disassembling '"
            invokespecial   StringBuilder(String)
            aload_1         [java.net.URL location]
            invokevirtual   StringBuilder.append(Object) => StringBuilder
            ldc_w           "': "
            invokevirtual   StringBuilder.append(String) => StringBuilder
            aload_3         [java.io.IOException ioe]
            invokevirtual   java.io.IOException.getMessage() => String
            invokevirtual   StringBuilder.append(String) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            invokespecial   java.io.IOException(String)
            astore          [java.io.IOException ioe2]
            // Line 359
            aload           [java.io.IOException ioe2]
            aload_3         [java.io.IOException ioe]
            invokevirtual   java.io.IOException.initCause(Throwable) => Throwable
            pop
            // Line 360
            aload           [java.io.IOException ioe2]
            athrow
L5
            // Line 361
            astore_3        [RuntimeException re]
            // Line 362
            new             RuntimeException
            dup
            new             StringBuilder
            dup
            ldc_w           "Disassembling '"
            invokespecial   StringBuilder(String)
            aload_1         [java.net.URL location]
            invokevirtual   StringBuilder.append(Object) => StringBuilder
            ldc_w           "': "
            invokevirtual   StringBuilder.append(String) => StringBuilder
            aload_3         [RuntimeException re]
            invokevirtual   RuntimeException.getMessage() => String
            invokevirtual   StringBuilder.append(String) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            aload_3         [RuntimeException re]
            invokespecial   RuntimeException(String, Throwable)
            athrow
        } catch ([all exceptions] => L6)
L6
        // Line 363
        astore          [v4]
        try {
            // Line 364
            aload_2         [java.io.InputStream is]
            invokevirtual   java.io.InputStream.close()
        } catch (java.io.IOException => L7)
        goto            L2
L7
        astore          [v5]
L2
        // Line 365
        aload           [v4]
        athrow
L1
        try {
            // Line 364
            aload_2         [java.io.InputStream is]
            invokevirtual   java.io.InputStream.close()
        } catch (java.io.IOException => L8)
        goto            L3
L8
        astore          [v5]
L3
        // Line 366
        return
    }

    public void disasm(java.io.InputStream stream) throws java.io.IOException {
        try {
            // Line 374
            aload_0         [this]
            new             java.io.DataInputStream
            dup
            aload_1         [java.io.InputStream stream]
            invokespecial   java.io.DataInputStream(java.io.InputStream)
            invokespecial   Disassembler.disassembleClassFile(java.io.DataInputStream)
            // Line 375
            goto            L1
        } catch ([all exceptions] => L2)
L2
        astore_2        [v1]
        // Line 376
        aload_0         [this]
        getfield        java.io.PrintWriter Disassembler.pw
        invokevirtual   java.io.PrintWriter.flush()
        // Line 377
        aload_2         [v1]
        athrow
L1
        // Line 376
        aload_0         [this]
        getfield        java.io.PrintWriter Disassembler.pw
        invokevirtual   java.io.PrintWriter.flush()
        // Line 378
        return
    }

    private void disassembleClassFile(java.io.DataInputStream dis) throws java.io.IOException {
        // Line 387
        new             ClassFile
        dup
        aload_1         [java.io.DataInputStream dis]
        invokespecial   ClassFile(java.io.DataInputStream)
        astore_2        [ClassFile cf]
        // Line 390
        aload_0         [this]
        invokespecial   Disassembler.println()
        // Line 391
        aload_0         [this]
        // Line 392
        new             StringBuilder
        dup
        ldc_w           "// Class file version = "
        invokespecial   StringBuilder(String)
        aload_2         [ClassFile cf]
        getfield        short ClassFile.majorVersion
        invokevirtual   StringBuilder.append(int) => StringBuilder
        ldc_w           "."
        invokevirtual   StringBuilder.append(String) => StringBuilder
        aload_2         [ClassFile cf]
        getfield        short ClassFile.minorVersion
        invokevirtual   StringBuilder.append(int) => StringBuilder
        ldc_w           " ("
        invokevirtual   StringBuilder.append(String) => StringBuilder
        aload_2         [ClassFile cf]
        invokevirtual   ClassFile.getJdkName() => String
        invokevirtual   StringBuilder.append(String) => StringBuilder
        ldc_w           ")"
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        // Line 391
        invokespecial   Disassembler.println(String)
        // Line 395
        aload_2         [ClassFile cf]
        getfield        String ClassFile.thisClassName
        iconst_0
        aload_2         [ClassFile cf]
        getfield        String ClassFile.thisClassName
        bipush          46
        invokevirtual   String.lastIndexOf(int) => int
        iconst_1
        iadd
        invokevirtual   String.substring(int, int) => String
        astore_3        [String tcpn]
        // Line 399
        aload_0         [this]
        new             SignatureParser
        dup
        new             Disassembler$1
        dup
        aload_0         [this]
        aload_3         [String tcpn]
        invokespecial   Disassembler$1(Disassembler, String)
        invokespecial   SignatureParser(SignatureParser$Options)
        putfield        SignatureParser Disassembler.signatureParser
        // Line 411
        aload_2         [ClassFile cf]
        aload_0         [this]
        getfield        SignatureParser Disassembler.signatureParser
        invokevirtual   ClassFile.setSignatureParser(SignatureParser)
        // Line 414
        aload_3         [String tcpn]
        invokevirtual   String.length() => int
        ifle            L1
        // Line 415
        aload_0         [this]
        invokespecial   Disassembler.println()
        // Line 416
        aload_0         [this]
        new             StringBuilder
        dup
        ldc_w           "package "
        invokespecial   StringBuilder(String)
        aload_3         [String tcpn]
        iconst_0
        aload_3         [String tcpn]
        invokevirtual   String.length() => int
        iconst_1
        isub
        invokevirtual   String.substring(int, int) => String
        invokevirtual   StringBuilder.append(String) => StringBuilder
        ldc_w           ";"
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.println(String)
L1
        // Line 420
        aload_2         [ClassFile cf]
        getfield        ClassFile$EnclosingMethodAttribute ClassFile.enclosingMethodAttribute
        astore          [ClassFile$EnclosingMethodAttribute ema]
        // Line 421
        aload           [ClassFile$EnclosingMethodAttribute ema]
        ifnull          L2
        // Line 423
        aload           [ClassFile$EnclosingMethodAttribute ema]
        getfield        ConstantPool$ConstantNameAndTypeInfo ClassFile$EnclosingMethodAttribute.method
        astore          [ConstantPool$ConstantNameAndTypeInfo m]
        // Line 424
        aload           [ConstantPool$ConstantNameAndTypeInfo m]
        ifnonnull       L3
        ldc_w           "[initializer]"
        goto            L4
L3
        aload           [ConstantPool$ConstantNameAndTypeInfo m]
        getfield        ConstantPool$ConstantUtf8Info ConstantPool$ConstantNameAndTypeInfo.name
        getfield        String ConstantPool$ConstantUtf8Info.bytes
L4
        astore          [String methodName]
        // Line 426
        aload_0         [this]
        invokespecial   Disassembler.println()
        // Line 427
        aload_0         [this]
        // Line 428
        new             StringBuilder
        dup
        ldc_w           "// This class is enclosed by method '"
        invokespecial   StringBuilder(String)
        // Line 429
        aload           [ClassFile$EnclosingMethodAttribute ema]
        getfield        ConstantPool$ConstantClassInfo ClassFile$EnclosingMethodAttribute.clasS
        invokevirtual   StringBuilder.append(Object) => StringBuilder
        // Line 430
        ldc_w           "<init>"
        aload           [String methodName]
        invokevirtual   String.equals(Object) => boolean
        ifeq            L5
        ldc_w           "(...)"
        goto            L6
L5
        new             StringBuilder
        dup
        ldc_w           "."
        invokespecial   StringBuilder(String)
        aload           [String methodName]
        invokevirtual   StringBuilder.append(String) => StringBuilder
        ldc_w           "(...)"
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
L6
        invokevirtual   StringBuilder.append(String) => StringBuilder
        // Line 431
        ldc_w           "'."
        invokevirtual   StringBuilder.append(String) => StringBuilder
        // Line 428
        invokevirtual   StringBuilder.toString() => String
        // Line 427
        invokespecial   Disassembler.println(String)
L2
        // Line 435
        aload_0         [this]
        invokespecial   Disassembler.println()
        // Line 438
        aload_2         [ClassFile cf]
        getfield        ClassFile$AccessFlags ClassFile.accessFlags
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.SYNTHETIC
        invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
        ifne            L7
        aload_2         [ClassFile cf]
        getfield        ClassFile$SyntheticAttribute ClassFile.syntheticAttribute
        ifnull          L8
L7
        // Line 439
        aload_0         [this]
        ldc_w           "// This is a synthetic class."
        invokespecial   Disassembler.println(String)
L8
        // Line 443
        aload_2         [ClassFile cf]
        getfield        ClassFile$DeprecatedAttribute ClassFile.deprecatedAttribute
        ifnull          L9
        aload_0         [this]
        ldc_w           "/** @deprecated */"
        invokespecial   Disassembler.println(String)
L9
        // Line 447
        aload_2         [ClassFile cf]
        getfield        ClassFile$RuntimeInvisibleAnnotationsAttribute ClassFile.runtimeInvisibleAnnotationsAttribute
        astore          [ClassFile$RuntimeInvisibleAnnotationsAttribute riaa]
        // Line 448
        aload           [ClassFile$RuntimeInvisibleAnnotationsAttribute riaa]
        ifnull          L10
        // Line 449
        aload           [ClassFile$RuntimeInvisibleAnnotationsAttribute riaa]
        getfield        java.util.List ClassFile$RuntimeInvisibleAnnotationsAttribute.annotations
        invokeinterface java.util.List.iterator() => java.util.Iterator
        astore          [v6]
        goto            L11
L12
        aload           [v6]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       ClassFile$Annotation
        astore          [ClassFile$Annotation a]
        aload_0         [this]
        aload           [ClassFile$Annotation a]
        invokevirtual   ClassFile$Annotation.toString() => String
        invokespecial   Disassembler.println(String)
L11
        aload           [v6]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L12
L10
        // Line 453
        aload_2         [ClassFile cf]
        getfield        ClassFile$RuntimeVisibleAnnotationsAttribute ClassFile.runtimeVisibleAnnotationsAttribute
        astore          [ClassFile$RuntimeVisibleAnnotationsAttribute rvaa]
        // Line 454
        aload           [ClassFile$RuntimeVisibleAnnotationsAttribute rvaa]
        ifnull          L13
        // Line 455
        aload           [ClassFile$RuntimeVisibleAnnotationsAttribute rvaa]
        getfield        java.util.List ClassFile$RuntimeVisibleAnnotationsAttribute.annotations
        invokeinterface java.util.List.iterator() => java.util.Iterator
        astore          [v6]
        goto            L14
L15
        aload           [v6]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       ClassFile$Annotation
        astore          [ClassFile$Annotation a]
        aload_0         [this]
        aload           [ClassFile$Annotation a]
        invokevirtual   ClassFile$Annotation.toString() => String
        invokespecial   Disassembler.println(String)
L14
        aload           [v6]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L15
L13
        // Line 460
        aload_2         [ClassFile cf]
        getfield        ClassFile$AccessFlags ClassFile.accessFlags
        // Line 461
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.SYNCHRONIZED
        invokevirtual   ClassFile$AccessFlags.remove(ClassFile$AccessFlags$FlagType) => ClassFile$AccessFlags
        // Line 462
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.SYNTHETIC
        invokevirtual   ClassFile$AccessFlags.remove(ClassFile$AccessFlags$FlagType) => ClassFile$AccessFlags
        // Line 459
        astore          [ClassFile$AccessFlags af]
        // Line 466
        aload           [ClassFile$AccessFlags af]
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.INTERFACE
        invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
        ifeq            L16
        aload           [ClassFile$AccessFlags af]
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.ABSTRACT
        invokevirtual   ClassFile$AccessFlags.remove(ClassFile$AccessFlags$FlagType) => ClassFile$AccessFlags
        astore          [ClassFile$AccessFlags af]
L16
        // Line 469
        aload           [ClassFile$AccessFlags af]
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.ENUM
        invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
        ifeq            L17
        aload           [ClassFile$AccessFlags af]
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.FINAL
        invokevirtual   ClassFile$AccessFlags.remove(ClassFile$AccessFlags$FlagType) => ClassFile$AccessFlags
        astore          [ClassFile$AccessFlags af]
L17
        // Line 472
        aload_0         [this]
        aload           [ClassFile$AccessFlags af]
        invokestatic    Disassembler.typeAccessFlagsToString(ClassFile$AccessFlags) => String
        invokespecial   Disassembler.print(String)
        // Line 476
        aload_2         [ClassFile cf]
        getfield        ClassFile$SignatureAttribute ClassFile.signatureAttribute
        astore          [ClassFile$SignatureAttribute sa]
        // Line 477
        aload           [ClassFile$SignatureAttribute sa]
        ifnull          L18
        // Line 478
        aload_0         [this]
        aload_0         [this]
        aload           [ClassFile$SignatureAttribute sa]
        getfield        String ClassFile$SignatureAttribute.signature
        invokespecial   Disassembler.decodeClassSignature(String) => SignatureParser$ClassSignature
        aload_2         [ClassFile cf]
        getfield        String ClassFile.simpleThisClassName
        invokevirtual   SignatureParser$ClassSignature.toString(String) => String
        invokespecial   Disassembler.print(String)
        // Line 479
        goto            L19
L18
        // Line 480
        aload_0         [this]
        aload_2         [ClassFile cf]
        getfield        String ClassFile.simpleThisClassName
        invokespecial   Disassembler.print(String)
L19
        // Line 486
        aload_2         [ClassFile cf]
        getfield        String ClassFile.superClassName
        astore          [String scn]
        // Line 487
        aload           [String scn]
        ifnull          L20
        ldc_w           "java.lang.Object"
        aload           [String scn]
        invokevirtual   String.equals(Object) => boolean
        ifne            L20
        aload_0         [this]
        new             StringBuilder
        dup
        ldc_w           " extends "
        invokespecial   StringBuilder(String)
        aload           [String scn]
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.print(String)
L20
        // Line 492
        aload_2         [ClassFile cf]
        getfield        java.util.List ClassFile.interfaceNames
        astore          [java.util.List<String> ifs]
        // Line 493
        aload_2         [ClassFile cf]
        getfield        ClassFile$AccessFlags ClassFile.accessFlags
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.ANNOTATION
        invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
        ifeq            L21
        // Line 494
        aload           [java.util.List<String> ifs]
        ldc_w           "java.lang.annotation.Annotation"
        invokeinterface java.util.List.contains(Object) => boolean
        ifeq            L22
        // Line 495
        new             java.util.ArrayList
        dup
        aload           [java.util.List<String> ifs]
        invokespecial   java.util.ArrayList(java.util.Collection)
        astore          [java.util.List<String> ifs]
        // Line 496
        aload           [java.util.List<String> ifs]
        ldc_w           "java.lang.annotation.Annotation"
        invokeinterface java.util.List.remove(Object) => boolean
        pop
        // Line 497
        goto            L21
L22
        // Line 498
        aload_0         [this]
        // Line 499
        ldc_w           " /* WARNING: This annotation type does not implement \"java.lang.annotation.Anno"...
        // Line 498
        invokespecial   Disassembler.print(String)
L21
        // Line 504
        aload           [java.util.List<String> ifs]
        invokeinterface java.util.List.isEmpty() => boolean
        ifne            L23
        // Line 505
        aload           [java.util.List<String> ifs]
        invokeinterface java.util.List.iterator() => java.util.Iterator
        astore          [java.util.Iterator<String> it]
        // Line 506
        aload_0         [this]
        new             StringBuilder
        dup
        ldc_w           " implements "
        invokespecial   StringBuilder(String)
        aload           [java.util.Iterator<String> it]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       String
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.print(String)
        // Line 507
        goto            L24
L25
        aload_0         [this]
        new             StringBuilder
        dup
        ldc_w           ", "
        invokespecial   StringBuilder(String)
        aload           [java.util.Iterator<String> it]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       String
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.print(String)
L24
        aload           [java.util.Iterator<String> it]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L25
L23
        // Line 511
        aload_0         [this]
        ldc_w           " {"
        invokespecial   Disassembler.println(String)
        // Line 514
        aload_0         [this]
        getfield        boolean Disassembler.verbose
        ifeq            L26
        // Line 515
        aload_0         [this]
        invokespecial   Disassembler.println()
        // Line 516
        aload_0         [this]
        ldc_w           "    // Constant pool dump:"
        invokespecial   Disassembler.println(String)
        // Line 517
        aload_2         [ClassFile cf]
        getfield        ConstantPool ClassFile.constantPool
        astore          [ConstantPool cp]
        // Line 518
        iconst_0
        istore          [int i]
        goto            L27
L30
        // Line 519
        aload           [ConstantPool cp]
        iload           [int i]
        i2s
        ldc_w           ConstantPool$ConstantPoolEntry
        invokevirtual   ConstantPool.getOptional(short, Class) => ConstantPool$ConstantPoolEntry
        astore          [ConstantPool$ConstantPoolEntry constantPoolEntry]
        // Line 520
        aload           [ConstantPool$ConstantPoolEntry constantPoolEntry]
        ifnonnull       L28
        goto            L29
L28
        // Line 521
        aload_0         [this]
        new             StringBuilder
        dup
        ldc_w           "    //   #"
        invokespecial   StringBuilder(String)
        iload           [int i]
        invokevirtual   StringBuilder.append(int) => StringBuilder
        ldc_w           ": "
        invokevirtual   StringBuilder.append(String) => StringBuilder
        aload           [ConstantPool$ConstantPoolEntry constantPoolEntry]
        invokevirtual   Object.toString() => String
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.println(String)
L29
        // Line 518
        iinc            [int i] 1
L27
        iload           [int i]
        aload           [ConstantPool cp]
        invokevirtual   ConstantPool.getSize() => int
        if_icmplt       L30
L26
        // Line 527
        aload_2         [ClassFile cf]
        getfield        ClassFile$InnerClassesAttribute ClassFile.innerClassesAttribute
        astore          [ClassFile$InnerClassesAttribute ica]
        // Line 528
        aload           [ClassFile$InnerClassesAttribute ica]
        ifnull          L31
        // Line 529
        aload_0         [this]
        invokespecial   Disassembler.println()
        // Line 530
        aload_0         [this]
        ldc_w           "    // Enclosing/enclosed types:"
        invokespecial   Disassembler.println(String)
        // Line 531
        aload           [ClassFile$InnerClassesAttribute ica]
        getfield        java.util.List ClassFile$InnerClassesAttribute.classes
        invokeinterface java.util.List.iterator() => java.util.Iterator
        astore          [v7]
        goto            L32
L33
        aload           [v7]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       ClassFile$InnerClassesAttribute$ClasS
        astore          [ClassFile$InnerClassesAttribute$ClasS c]
        // Line 532
        aload_0         [this]
        new             StringBuilder
        dup
        ldc_w           "    //   "
        invokespecial   StringBuilder(String)
        aload           [ClassFile$InnerClassesAttribute$ClasS c]
        invokestatic    Disassembler.toString(ClassFile$InnerClassesAttribute$ClasS) => String
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.println(String)
L32
        // Line 531
        aload           [v7]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L33
L31
        // Line 538
        aload_0         [this]
        aload_2         [ClassFile cf]
        getfield        java.util.List ClassFile.fields
        invokespecial   Disassembler.disassembleFields(java.util.List)
        // Line 541
        new             java.util.HashMap
        dup
        invokespecial   java.util.HashMap()
        astore          [java.util.Map<Integer, String> sourceLines]
        // Line 543
        aload_0         [this]
        getfield        java.io.File Disassembler.sourceDirectory
        ifnull          L34
        // Line 544
        aload_2         [ClassFile cf]
        getfield        ClassFile$SourceFileAttribute ClassFile.sourceFileAttribute
        astore          [ClassFile$SourceFileAttribute sfa]
        // Line 545
        aload           [ClassFile$SourceFileAttribute sfa]
        ifnonnull       L35
        goto            L34
L35
        // Line 547
        new             java.io.File
        dup
        aload_0         [this]
        getfield        java.io.File Disassembler.sourceDirectory
        aload           [ClassFile$SourceFileAttribute sfa]
        getfield        String ClassFile$SourceFileAttribute.sourceFile
        invokespecial   java.io.File(java.io.File, String)
        astore          [java.io.File sourceFile]
        // Line 549
        aload           [java.io.File sourceFile]
        invokevirtual   java.io.File.exists() => boolean
        ifne            L36
        // Line 552
        aload_2         [ClassFile cf]
        getfield        String ClassFile.thisClassName
        astore          [String toplevelClassName]
        // Line 553
        aload           [String toplevelClassName]
        bipush          36
        invokevirtual   String.indexOf(int) => int
        istore          [int idx]
        // Line 554
        iload           [int idx]
        iconst_m1
        if_icmpeq       L37
        aload           [String toplevelClassName]
        iconst_0
        iload           [int idx]
        invokevirtual   String.substring(int, int) => String
        astore          [String toplevelClassName]
L37
        // Line 556
        new             java.io.File
        dup
        // Line 557
        aload_0         [this]
        getfield        java.io.File Disassembler.sourceDirectory
        // Line 558
        new             StringBuilder
        dup
        aload           [String toplevelClassName]
        bipush          46
        getstatic       char java.io.File.separatorChar
        invokevirtual   String.replace(char, char) => String
        invokestatic    String.valueOf(Object) => String
        invokespecial   StringBuilder(String)
        ldc_w           ".java"
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        // Line 556
        invokespecial   java.io.File(java.io.File, String)
        astore          [java.io.File sourceFile]
L36
        // Line 561
        aload           [java.io.File sourceFile]
        invokevirtual   java.io.File.exists() => boolean
        ifne            L38
        goto            L34
L38
        // Line 563
        new             java.io.LineNumberReader
        dup
        new             java.io.FileReader
        dup
        aload           [java.io.File sourceFile]
        invokespecial   java.io.FileReader(java.io.File)
        invokespecial   java.io.LineNumberReader(java.io.Reader)
        astore          [java.io.LineNumberReader lnr]
L41
        try {
            // Line 566
            aload           [java.io.LineNumberReader lnr]
            invokevirtual   java.io.LineNumberReader.readLine() => String
            astore          [String sl]
            // Line 567
            aload           [String sl]
            ifnonnull       L39
            goto            L40
L39
            // Line 568
            aload           [java.util.Map<Integer, String> sourceLines]
            aload           [java.io.LineNumberReader lnr]
            invokevirtual   java.io.LineNumberReader.getLineNumber() => int
            invokestatic    Integer.valueOf(int) => Integer
            aload           [String sl]
            invokeinterface java.util.Map.put(Object, Object) => Object
            pop
            // Line 565
            goto            L41
        } catch ([all exceptions] => L45)
L45
        // Line 570
        astore          [v10]
        try {
            // Line 571
            aload           [java.io.LineNumberReader lnr]
            invokevirtual   java.io.LineNumberReader.close()
        } catch (Exception => L46)
        goto            L42
L46
        astore          [v11]
L42
        // Line 572
        aload           [v10]
        athrow
L40
        try {
            // Line 571
            aload           [java.io.LineNumberReader lnr]
            invokevirtual   java.io.LineNumberReader.close()
        } catch (Exception => L47)
        goto            L34
L47
        astore          [v11]
L34
        // Line 576
        aload_2         [ClassFile cf]
        getfield        java.util.List ClassFile.methods
        invokeinterface java.util.List.iterator() => java.util.Iterator
        astore          [v7]
        goto            L43
L44
        aload           [v7]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       ClassFile$Method
        astore          [ClassFile$Method m]
        // Line 577
        aload_0         [this]
        aload           [ClassFile$Method m]
        aload           [java.util.Map<Integer, String> sourceLines]
        invokespecial   Disassembler.disassembleMethod(ClassFile$Method, java.util.Map)
L43
        // Line 576
        aload           [v7]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L44
        // Line 580
        aload_0         [this]
        ldc_w           "}"
        invokespecial   Disassembler.println(String)
        // Line 583
        aload_0         [this]
        aload_2         [ClassFile cf]
        getfield        java.util.List ClassFile.attributes
        ldc_w           "// "
        bipush          8
        anewarray       ClassFile$Attribute
        dup
        iconst_0
        // Line 584
        aload_2         [ClassFile cf]
        getfield        ClassFile$DeprecatedAttribute ClassFile.deprecatedAttribute
        aastore
        dup
        iconst_1
        // Line 585
        aload           [ClassFile$EnclosingMethodAttribute ema]
        aastore
        dup
        iconst_2
        // Line 586
        aload_2         [ClassFile cf]
        getfield        ClassFile$InnerClassesAttribute ClassFile.innerClassesAttribute
        aastore
        dup
        iconst_3
        // Line 587
        aload_2         [ClassFile cf]
        getfield        ClassFile$RuntimeInvisibleAnnotationsAttribute ClassFile.runtimeInvisibleAnnotationsAttribute
        aastore
        dup
        iconst_4
        // Line 588
        aload_2         [ClassFile cf]
        getfield        ClassFile$RuntimeVisibleAnnotationsAttribute ClassFile.runtimeVisibleAnnotationsAttribute
        aastore
        dup
        iconst_5
        // Line 589
        aload_2         [ClassFile cf]
        getfield        ClassFile$SignatureAttribute ClassFile.signatureAttribute
        aastore
        dup
        bipush          6
        // Line 590
        aload_2         [ClassFile cf]
        getfield        ClassFile$SourceFileAttribute ClassFile.sourceFileAttribute
        aastore
        dup
        bipush          7
        // Line 591
        aload_2         [ClassFile cf]
        getfield        ClassFile$SyntheticAttribute ClassFile.syntheticAttribute
        aastore
        // Line 592
        getstatic       Disassembler$AttributeContext Disassembler$AttributeContext.CLASS
        // Line 583
        invokespecial   Disassembler.printAttributes(java.util.List, String, ClassFile$Attribute[], Disassembler$AttributeContext)
        // Line 593
        return
    }

    private void disassembleMethod(ClassFile$Method method, java.util.Map<Integer, String> sourceLines) {
        try {
            // Line 603
            aload_0         [this]
            invokespecial   Disassembler.println()
            // Line 606
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$AccessFlags ClassFile$Method.accessFlags
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.SYNTHETIC
            invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
            ifne            L1
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$SyntheticAttribute ClassFile$Method.syntheticAttribute
            ifnull          L2
L1
            // Line 607
            aload_0         [this]
            ldc_w           "    // (Synthetic method)"
            invokespecial   Disassembler.println(String)
L2
            // Line 611
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$AccessFlags ClassFile$Method.accessFlags
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.BRIDGE
            invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
            ifeq            L3
            aload_0         [this]
            ldc_w           "    // (Bridge method)"
            invokespecial   Disassembler.println(String)
L3
            // Line 614
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$DeprecatedAttribute ClassFile$Method.deprecatedAttribute
            ifnull          L4
            aload_0         [this]
            ldc_w           "    /** @deprecated */"
            invokespecial   Disassembler.println(String)
L4
            // Line 618
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$RuntimeInvisibleAnnotationsAttribute ClassFile$Method.runtimeInvisibleAnnotationsAttribute
            astore_3        [ClassFile$RuntimeInvisibleAnnotationsAttribute riaa]
            // Line 619
            aload_3         [ClassFile$RuntimeInvisibleAnnotationsAttribute riaa]
            ifnull          L5
            // Line 620
            aload_3         [ClassFile$RuntimeInvisibleAnnotationsAttribute riaa]
            getfield        java.util.List ClassFile$RuntimeInvisibleAnnotationsAttribute.annotations
            invokeinterface java.util.List.iterator() => java.util.Iterator
            astore          [v3]
            goto            L6
L7
            aload           [v3]
            invokeinterface java.util.Iterator.next() => Object
            checkcast       ClassFile$Annotation
            astore          [ClassFile$Annotation a]
            aload_0         [this]
            new             StringBuilder
            dup
            ldc_w           "    "
            invokespecial   StringBuilder(String)
            aload           [ClassFile$Annotation a]
            invokevirtual   ClassFile$Annotation.toString() => String
            invokevirtual   StringBuilder.append(String) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            invokespecial   Disassembler.println(String)
L6
            aload           [v3]
            invokeinterface java.util.Iterator.hasNext() => boolean
            ifne            L7
L5
            // Line 624
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$RuntimeVisibleAnnotationsAttribute ClassFile$Method.runtimeVisibleAnnotationsAttribute
            astore_3        [ClassFile$RuntimeVisibleAnnotationsAttribute rvaa]
            // Line 625
            aload_3         [ClassFile$RuntimeVisibleAnnotationsAttribute rvaa]
            ifnull          L8
            // Line 626
            aload_3         [ClassFile$RuntimeVisibleAnnotationsAttribute rvaa]
            getfield        java.util.List ClassFile$RuntimeVisibleAnnotationsAttribute.annotations
            invokeinterface java.util.List.iterator() => java.util.Iterator
            astore          [v3]
            goto            L9
L10
            aload           [v3]
            invokeinterface java.util.Iterator.next() => Object
            checkcast       ClassFile$Annotation
            astore          [ClassFile$Annotation a]
            aload_0         [this]
            new             StringBuilder
            dup
            ldc_w           "    "
            invokespecial   StringBuilder(String)
            aload           [ClassFile$Annotation a]
            invokevirtual   ClassFile$Annotation.toString() => String
            invokevirtual   StringBuilder.append(String) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            invokespecial   Disassembler.println(String)
L9
            aload           [v3]
            invokeinterface java.util.Iterator.hasNext() => boolean
            ifne            L10
L8
            // Line 635
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$AccessFlags ClassFile$Method.accessFlags
            // Line 636
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.SYNTHETIC
            invokevirtual   ClassFile$AccessFlags.remove(ClassFile$AccessFlags$FlagType) => ClassFile$AccessFlags
            // Line 637
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.BRIDGE
            invokevirtual   ClassFile$AccessFlags.remove(ClassFile$AccessFlags$FlagType) => ClassFile$AccessFlags
            // Line 638
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.VARARGS
            invokevirtual   ClassFile$AccessFlags.remove(ClassFile$AccessFlags$FlagType) => ClassFile$AccessFlags
            // Line 634
            astore_3        [ClassFile$AccessFlags maf]
            // Line 642
            aload_1         [ClassFile$Method method]
            invokevirtual   ClassFile$Method.getClassFile() => ClassFile
            getfield        ClassFile$AccessFlags ClassFile.accessFlags
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.INTERFACE
            invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
            ifeq            L11
            // Line 643
            aload_3         [ClassFile$AccessFlags maf]
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.PUBLIC
            invokevirtual   ClassFile$AccessFlags.remove(ClassFile$AccessFlags$FlagType) => ClassFile$AccessFlags
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.ABSTRACT
            invokevirtual   ClassFile$AccessFlags.remove(ClassFile$AccessFlags$FlagType) => ClassFile$AccessFlags
            astore_3        [ClassFile$AccessFlags maf]
L11
            // Line 646
            aload_0         [this]
            new             StringBuilder
            dup
            ldc_w           "    "
            invokespecial   StringBuilder(String)
            aload_3         [ClassFile$AccessFlags maf]
            invokevirtual   StringBuilder.append(Object) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            invokespecial   Disassembler.print(String)
            // Line 652
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$SignatureAttribute ClassFile$Method.signatureAttribute
            astore          [ClassFile$SignatureAttribute sa]
            // Line 654
            aload           [ClassFile$SignatureAttribute sa]
            ifnonnull       L12
            // Line 655
            aload_0         [this]
            aload_1         [ClassFile$Method method]
            getfield        String ClassFile$Method.descriptor
            invokevirtual   Disassembler.decodeMethodDescriptor(String) => SignatureParser$MethodTypeSignature
            goto            L13
L12
            // Line 656
            aload_0         [this]
            aload           [ClassFile$SignatureAttribute sa]
            getfield        String ClassFile$SignatureAttribute.signature
            invokevirtual   Disassembler.decodeMethodTypeSignature(String) => SignatureParser$MethodTypeSignature
L13
            // Line 653
            astore_3        [SignatureParser$MethodTypeSignature mts]
            // Line 658
            aload_3         [SignatureParser$MethodTypeSignature mts]
            getfield        java.util.List SignatureParser$MethodTypeSignature.formalTypeParameters
            invokeinterface java.util.List.isEmpty() => boolean
            ifne            L14
            // Line 659
            aload_3         [SignatureParser$MethodTypeSignature mts]
            getfield        java.util.List SignatureParser$MethodTypeSignature.formalTypeParameters
            invokeinterface java.util.List.iterator() => java.util.Iterator
            astore          [java.util.Iterator<SignatureParser$FormalTypeParameter> it]
            // Line 660
            aload_0         [this]
            new             StringBuilder
            dup
            ldc_w           "<"
            invokespecial   StringBuilder(String)
            aload           [java.util.Iterator<SignatureParser$FormalTypeParameter> it]
            invokeinterface java.util.Iterator.next() => Object
            invokevirtual   StringBuilder.append(Object) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            invokespecial   Disassembler.print(String)
            // Line 661
            goto            L15
L16
            aload_0         [this]
            new             StringBuilder
            dup
            ldc_w           ", "
            invokespecial   StringBuilder(String)
            aload           [java.util.Iterator<SignatureParser$FormalTypeParameter> it]
            invokeinterface java.util.Iterator.next() => Object
            invokevirtual   StringBuilder.append(Object) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            invokespecial   Disassembler.print(String)
L15
            aload           [java.util.Iterator<SignatureParser$FormalTypeParameter> it]
            invokeinterface java.util.Iterator.hasNext() => boolean
            ifne            L16
            // Line 662
            aload_0         [this]
            ldc_w           ">"
            invokespecial   Disassembler.print(String)
L14
            // Line 668
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$ExceptionsAttribute ClassFile$Method.exceptionsAttribute
            astore          [ClassFile$ExceptionsAttribute ea]
            // Line 669
            aload           [ClassFile$ExceptionsAttribute ea]
            ifnonnull       L17
            getstatic       java.util.List Disassembler.NO_CONSTANT_CLASS_INFOS
            goto            L18
L17
            aload           [ClassFile$ExceptionsAttribute ea]
            getfield        java.util.List ClassFile$ExceptionsAttribute.exceptionNames
L18
            astore          [java.util.List<ConstantPool$ConstantClassInfo> exceptionNames]
            // Line 672
            aload_1         [ClassFile$Method method]
            getfield        String ClassFile$Method.name
            astore          [String functionName]
            // Line 674
            ldc_w           "<clinit>"
            aload           [String functionName]
            invokevirtual   String.equals(Object) => boolean
            ifeq            L19
            // Line 675
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$AccessFlags ClassFile$Method.accessFlags
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.STATIC
            invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
            ifeq            L19
            // Line 676
            aload           [java.util.List<ConstantPool$ConstantClassInfo> exceptionNames]
            invokeinterface java.util.List.isEmpty() => boolean
            ifeq            L19
            // Line 677
            aload_3         [SignatureParser$MethodTypeSignature mts]
            getfield        java.util.List SignatureParser$MethodTypeSignature.formalTypeParameters
            invokeinterface java.util.List.isEmpty() => boolean
            ifeq            L19
            // Line 678
            aload_3         [SignatureParser$MethodTypeSignature mts]
            getfield        java.util.List SignatureParser$MethodTypeSignature.parameterTypes
            invokeinterface java.util.List.isEmpty() => boolean
            ifeq            L19
            // Line 679
            aload_3         [SignatureParser$MethodTypeSignature mts]
            getfield        SignatureParser$TypeSignature SignatureParser$MethodTypeSignature.returnType
            getstatic       SignatureParser$TypeSignature SignatureParser.VOID
            if_acmpne       L19
            // Line 680
            aload_3         [SignatureParser$MethodTypeSignature mts]
            getfield        java.util.List SignatureParser$MethodTypeSignature.thrownTypes
            invokeinterface java.util.List.isEmpty() => boolean
            ifeq            L19
            // Line 685
            goto            L20
L19
            // Line 687
            ldc_w           "<init>"
            aload           [String functionName]
            invokevirtual   String.equals(Object) => boolean
            ifeq            L21
            // Line 688
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$AccessFlags ClassFile$Method.accessFlags
            iconst_4
            anewarray       ClassFile$AccessFlags$FlagType
            dup
            iconst_0
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.ABSTRACT
            aastore
            dup
            iconst_1
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.FINAL
            aastore
            dup
            iconst_2
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.INTERFACE
            aastore
            dup
            iconst_3
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.STATIC
            aastore
            invokevirtual   ClassFile$AccessFlags.isAny(ClassFile$AccessFlags$FlagType[]) => boolean
            ifne            L21
            // Line 689
            aload_3         [SignatureParser$MethodTypeSignature mts]
            getfield        java.util.List SignatureParser$MethodTypeSignature.formalTypeParameters
            invokeinterface java.util.List.isEmpty() => boolean
            ifeq            L21
            // Line 690
            aload_3         [SignatureParser$MethodTypeSignature mts]
            getfield        SignatureParser$TypeSignature SignatureParser$MethodTypeSignature.returnType
            getstatic       SignatureParser$TypeSignature SignatureParser.VOID
            if_acmpne       L21
            // Line 694
            aload_0         [this]
            aload_1         [ClassFile$Method method]
            invokevirtual   ClassFile$Method.getClassFile() => ClassFile
            getfield        String ClassFile.simpleThisClassName
            invokespecial   Disassembler.print(String)
            // Line 695
            aload_0         [this]
            // Line 696
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$RuntimeInvisibleParameterAnnotationsAttribute ClassFile$Method.runtimeInvisibleParameterAnnotationsAttribute
            // Line 697
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$RuntimeVisibleParameterAnnotationsAttribute ClassFile$Method.runtimeVisibleParameterAnnotationsAttribute
            // Line 698
            aload_3         [SignatureParser$MethodTypeSignature mts]
            getfield        java.util.List SignatureParser$MethodTypeSignature.parameterTypes
            // Line 699
            aload_1         [ClassFile$Method method]
            // Line 700
            iconst_1
            // Line 701
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$AccessFlags ClassFile$Method.accessFlags
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.VARARGS
            invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
            // Line 695
            invokespecial   Disassembler.printParameters(ClassFile$RuntimeInvisibleParameterAnnotationsAttribute, ClassFile$RuntimeVisibleParameterAnnotationsAttribute, java.util.List, ClassFile$Method, short, boolean)
            // Line 703
            goto            L20
L21
            // Line 707
            aload_0         [this]
            new             StringBuilder
            dup
            invokespecial   StringBuilder()
            aload_3         [SignatureParser$MethodTypeSignature mts]
            getfield        SignatureParser$TypeSignature SignatureParser$MethodTypeSignature.returnType
            invokevirtual   StringBuilder.append(Object) => StringBuilder
            ldc_w           " "
            invokevirtual   StringBuilder.append(String) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            invokespecial   Disassembler.print(String)
            // Line 708
            aload_0         [this]
            aload           [String functionName]
            invokespecial   Disassembler.print(String)
            // Line 709
            aload_0         [this]
            // Line 710
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$RuntimeInvisibleParameterAnnotationsAttribute ClassFile$Method.runtimeInvisibleParameterAnnotationsAttribute
            // Line 711
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$RuntimeVisibleParameterAnnotationsAttribute ClassFile$Method.runtimeVisibleParameterAnnotationsAttribute
            // Line 712
            aload_3         [SignatureParser$MethodTypeSignature mts]
            getfield        java.util.List SignatureParser$MethodTypeSignature.parameterTypes
            // Line 713
            aload_1         [ClassFile$Method method]
            // Line 714
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$AccessFlags ClassFile$Method.accessFlags
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.STATIC
            invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
            ifeq            L22
            iconst_0
            goto            L23
L22
            iconst_1
L23
            // Line 715
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$AccessFlags ClassFile$Method.accessFlags
            getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.VARARGS
            invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
            // Line 709
            invokespecial   Disassembler.printParameters(ClassFile$RuntimeInvisibleParameterAnnotationsAttribute, ClassFile$RuntimeVisibleParameterAnnotationsAttribute, java.util.List, ClassFile$Method, short, boolean)
L20
            // Line 720
            aload_3         [SignatureParser$MethodTypeSignature mts]
            getfield        java.util.List SignatureParser$MethodTypeSignature.thrownTypes
            invokeinterface java.util.List.isEmpty() => boolean
            ifne            L24
            // Line 721
            aload_3         [SignatureParser$MethodTypeSignature mts]
            getfield        java.util.List SignatureParser$MethodTypeSignature.thrownTypes
            invokeinterface java.util.List.iterator() => java.util.Iterator
            astore          [java.util.Iterator<SignatureParser$ThrowsSignature> it]
            // Line 722
            aload_0         [this]
            new             StringBuilder
            dup
            ldc_w           " throws "
            invokespecial   StringBuilder(String)
            aload           [java.util.Iterator<SignatureParser$ThrowsSignature> it]
            invokeinterface java.util.Iterator.next() => Object
            invokevirtual   StringBuilder.append(Object) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            invokespecial   Disassembler.print(String)
            // Line 723
            goto            L25
L26
            aload_0         [this]
            new             StringBuilder
            dup
            ldc_w           ", "
            invokespecial   StringBuilder(String)
            aload           [java.util.Iterator<SignatureParser$ThrowsSignature> it]
            invokeinterface java.util.Iterator.next() => Object
            invokevirtual   StringBuilder.append(Object) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            invokespecial   Disassembler.print(String)
L25
            aload           [java.util.Iterator<SignatureParser$ThrowsSignature> it]
            invokeinterface java.util.Iterator.hasNext() => boolean
            ifne            L26
            // Line 724
            goto            L27
L24
            // Line 725
            aload           [java.util.List<ConstantPool$ConstantClassInfo> exceptionNames]
            invokeinterface java.util.List.isEmpty() => boolean
            ifne            L27
            // Line 726
            aload           [java.util.List<ConstantPool$ConstantClassInfo> exceptionNames]
            invokeinterface java.util.List.iterator() => java.util.Iterator
            astore          [java.util.Iterator<ConstantPool$ConstantClassInfo> it]
            // Line 727
            aload_0         [this]
            new             StringBuilder
            dup
            ldc_w           " throws "
            invokespecial   StringBuilder(String)
            aload           [java.util.Iterator<ConstantPool$ConstantClassInfo> it]
            invokeinterface java.util.Iterator.next() => Object
            invokevirtual   StringBuilder.append(Object) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            invokespecial   Disassembler.print(String)
            // Line 728
            goto            L28
L29
            aload_0         [this]
            new             StringBuilder
            dup
            ldc_w           ", "
            invokespecial   StringBuilder(String)
            aload           [java.util.Iterator<ConstantPool$ConstantClassInfo> it]
            invokeinterface java.util.Iterator.next() => Object
            invokevirtual   StringBuilder.append(Object) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            invokespecial   Disassembler.print(String)
L28
            aload           [java.util.Iterator<ConstantPool$ConstantClassInfo> it]
            invokeinterface java.util.Iterator.hasNext() => boolean
            ifne            L29
L27
            // Line 733
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$AnnotationDefaultAttribute ClassFile$Method.annotationDefaultAttribute
            astore          [ClassFile$AnnotationDefaultAttribute ada]
            // Line 734
            aload           [ClassFile$AnnotationDefaultAttribute ada]
            ifnull          L30
            aload_0         [this]
            new             StringBuilder
            dup
            ldc_w           "default "
            invokespecial   StringBuilder(String)
            aload           [ClassFile$AnnotationDefaultAttribute ada]
            getfield        ClassFile$ElementValue ClassFile$AnnotationDefaultAttribute.defaultValue
            invokevirtual   StringBuilder.append(Object) => StringBuilder
            invokevirtual   StringBuilder.toString() => String
            invokespecial   Disassembler.print(String)
L30
            // Line 739
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$CodeAttribute ClassFile$Method.codeAttribute
            astore          [ClassFile$CodeAttribute ca]
            // Line 740
            aload           [ClassFile$CodeAttribute ca]
            ifnonnull       L31
            // Line 741
            aload_0         [this]
            ldc_w           ";"
            invokespecial   Disassembler.println(String)
            // Line 742
            goto            L32
L31
            // Line 743
            aload_0         [this]
            ldc_w           " {"
            invokespecial   Disassembler.println(String)
            try {
                // Line 745
                new             BytecodeDisassembler
                dup
                // Line 746
                new             java.io.ByteArrayInputStream
                dup
                aload           [ClassFile$CodeAttribute ca]
                getfield        byte[] ClassFile$CodeAttribute.code
                invokespecial   java.io.ByteArrayInputStream(byte[])
                // Line 747
                aload           [ClassFile$CodeAttribute ca]
                getfield        java.util.List ClassFile$CodeAttribute.exceptionTable
                // Line 748
                aload           [ClassFile$CodeAttribute ca]
                getfield        ClassFile$LineNumberTableAttribute ClassFile$CodeAttribute.lineNumberTableAttribute
                // Line 749
                aload_2         [java.util.Map<Integer, String> sourceLines]
                // Line 750
                aload_1         [ClassFile$Method method]
                // Line 751
                aload_0         [this]
                // Line 745
                invokespecial   BytecodeDisassembler(java.io.InputStream, java.util.List, ClassFile$LineNumberTableAttribute, java.util.Map, ClassFile$Method, Disassembler)
                // Line 752
                aload_0         [this]
                getfield        java.io.PrintWriter Disassembler.pw
                invokevirtual   BytecodeDisassembler.disassembleBytecode(java.io.PrintWriter)
            } catch (java.io.IOException => L35)
            // Line 753
            goto            L33
L35
            astore          [v5]
L33
            // Line 756
            aload_0         [this]
            ldc_w           "    }"
            invokespecial   Disassembler.println(String)
L32
            // Line 761
            aload_0         [this]
            aload_1         [ClassFile$Method method]
            getfield        java.util.List ClassFile$Method.attributes
            ldc_w           "    // "
            bipush          10
            anewarray       ClassFile$Attribute
            dup
            iconst_0
            // Line 762
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$AnnotationDefaultAttribute ClassFile$Method.annotationDefaultAttribute
            aastore
            dup
            iconst_1
            // Line 763
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$CodeAttribute ClassFile$Method.codeAttribute
            aastore
            dup
            iconst_2
            // Line 764
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$DeprecatedAttribute ClassFile$Method.deprecatedAttribute
            aastore
            dup
            iconst_3
            // Line 765
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$ExceptionsAttribute ClassFile$Method.exceptionsAttribute
            aastore
            dup
            iconst_4
            // Line 766
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$RuntimeInvisibleAnnotationsAttribute ClassFile$Method.runtimeInvisibleAnnotationsAttribute
            aastore
            dup
            iconst_5
            // Line 767
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$RuntimeInvisibleParameterAnnotationsAttribute ClassFile$Method.runtimeInvisibleParameterAnnotationsAttribute
            aastore
            dup
            bipush          6
            // Line 768
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$RuntimeVisibleAnnotationsAttribute ClassFile$Method.runtimeVisibleAnnotationsAttribute
            aastore
            dup
            bipush          7
            // Line 769
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$RuntimeVisibleParameterAnnotationsAttribute ClassFile$Method.runtimeVisibleParameterAnnotationsAttribute
            aastore
            dup
            bipush          8
            // Line 770
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$SignatureAttribute ClassFile$Method.signatureAttribute
            aastore
            dup
            bipush          9
            // Line 771
            aload_1         [ClassFile$Method method]
            getfield        ClassFile$SyntheticAttribute ClassFile$Method.syntheticAttribute
            aastore
            // Line 772
            getstatic       Disassembler$AttributeContext Disassembler$AttributeContext.METHOD
            // Line 761
            invokespecial   Disassembler.printAttributes(java.util.List, String, ClassFile$Attribute[], Disassembler$AttributeContext)
        } catch (RuntimeException => L36)
        // Line 773
        goto            L34
L36
        astore_3        [RuntimeException rte]
        // Line 774
        new             RuntimeException
        dup
        new             StringBuilder
        dup
        ldc_w           "Method '"
        invokespecial   StringBuilder(String)
        aload_1         [ClassFile$Method method]
        getfield        String ClassFile$Method.name
        invokevirtual   StringBuilder.append(String) => StringBuilder
        ldc_w           "' "
        invokevirtual   StringBuilder.append(String) => StringBuilder
        aload_1         [ClassFile$Method method]
        getfield        String ClassFile$Method.descriptor
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        aload_3         [RuntimeException rte]
        invokespecial   RuntimeException(String, Throwable)
        athrow
L34
        // Line 776
        return
    }

    private void disassembleFields(java.util.List<ClassFile$Field> fields) {
        // Line 780
        aload_1         [java.util.List<ClassFile$Field> fields]
        invokeinterface java.util.List.iterator() => java.util.Iterator
        astore_3        [v2]
        goto            L1
L15
        aload_3         [v2]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       ClassFile$Field
        astore_2        [ClassFile$Field field]
        // Line 781
        aload_0         [this]
        invokespecial   Disassembler.println()
        // Line 785
        aload_2         [ClassFile$Field field]
        getfield        ClassFile$RuntimeInvisibleAnnotationsAttribute ClassFile$Field.runtimeInvisibleAnnotationsAttribute
        astore          [ClassFile$RuntimeInvisibleAnnotationsAttribute riaa]
        // Line 786
        aload           [ClassFile$RuntimeInvisibleAnnotationsAttribute riaa]
        ifnull          L2
        // Line 787
        aload           [ClassFile$RuntimeInvisibleAnnotationsAttribute riaa]
        getfield        java.util.List ClassFile$RuntimeInvisibleAnnotationsAttribute.annotations
        invokeinterface java.util.List.iterator() => java.util.Iterator
        astore          [v5]
        goto            L3
L4
        aload           [v5]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       ClassFile$Annotation
        astore          [ClassFile$Annotation a]
        aload_0         [this]
        new             StringBuilder
        dup
        ldc_w           "    "
        invokespecial   StringBuilder(String)
        aload           [ClassFile$Annotation a]
        invokevirtual   StringBuilder.append(Object) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.println(String)
L3
        aload           [v5]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L4
L2
        // Line 791
        aload_2         [ClassFile$Field field]
        getfield        ClassFile$RuntimeVisibleAnnotationsAttribute ClassFile$Field.runtimeVisibleAnnotationsAttribute
        astore          [ClassFile$RuntimeVisibleAnnotationsAttribute rvaa]
        // Line 792
        aload           [ClassFile$RuntimeVisibleAnnotationsAttribute rvaa]
        ifnull          L5
        // Line 793
        aload           [ClassFile$RuntimeVisibleAnnotationsAttribute rvaa]
        getfield        java.util.List ClassFile$RuntimeVisibleAnnotationsAttribute.annotations
        invokeinterface java.util.List.iterator() => java.util.Iterator
        astore          [v5]
        goto            L6
L7
        aload           [v5]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       ClassFile$Annotation
        astore          [ClassFile$Annotation a]
        aload_0         [this]
        new             StringBuilder
        dup
        ldc_w           "    "
        invokespecial   StringBuilder(String)
        aload           [ClassFile$Annotation a]
        invokevirtual   StringBuilder.append(Object) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.println(String)
L6
        aload           [v5]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L7
L5
        // Line 798
        aload_2         [ClassFile$Field field]
        getfield        ClassFile$AccessFlags ClassFile$Field.accessFlags
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.SYNTHETIC
        invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
        ifne            L8
        aload_2         [ClassFile$Field field]
        getfield        ClassFile$SyntheticAttribute ClassFile$Field.syntheticAttribute
        ifnull          L9
L8
        // Line 799
        aload_0         [this]
        ldc_w           "    // (Synthetic field)"
        invokespecial   Disassembler.println(String)
L9
        // Line 803
        aload_2         [ClassFile$Field field]
        getfield        ClassFile$DeprecatedAttribute ClassFile$Field.deprecatedAttribute
        ifnull          L10
        aload_0         [this]
        ldc_w           "    /** @deprecated */"
        invokespecial   Disassembler.println(String)
L10
        // Line 808
        aload_2         [ClassFile$Field field]
        getfield        ClassFile$SignatureAttribute ClassFile$Field.signatureAttribute
        astore          [ClassFile$SignatureAttribute sa]
        // Line 811
        aload           [ClassFile$SignatureAttribute sa]
        ifnull          L11
        // Line 812
        aload_0         [this]
        aload           [ClassFile$SignatureAttribute sa]
        getfield        String ClassFile$SignatureAttribute.signature
        invokespecial   Disassembler.decodeFieldTypeSignature(String) => SignatureParser$FieldTypeSignature
        goto            L12
L11
        // Line 813
        aload_0         [this]
        aload_2         [ClassFile$Field field]
        getfield        String ClassFile$Field.descriptor
        invokevirtual   Disassembler.decodeFieldDescriptor(String) => SignatureParser$TypeSignature
L12
        // Line 810
        astore          [SignatureParser$TypeSignature typeSignature]
        // Line 816
        new             StringBuilder
        dup
        aload_2         [ClassFile$Field field]
        getfield        ClassFile$AccessFlags ClassFile$Field.accessFlags
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.SYNTHETIC
        invokevirtual   ClassFile$AccessFlags.remove(ClassFile$AccessFlags$FlagType) => ClassFile$AccessFlags
        invokevirtual   ClassFile$AccessFlags.toString() => String
        invokestatic    String.valueOf(Object) => String
        invokespecial   StringBuilder(String)
        aload           [SignatureParser$TypeSignature typeSignature]
        invokevirtual   StringBuilder.append(Object) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        astore          [String prefix]
        // Line 818
        aload_2         [ClassFile$Field field]
        getfield        ClassFile$ConstantValueAttribute ClassFile$Field.constantValueAttribute
        astore          [ClassFile$ConstantValueAttribute cva]
        // Line 819
        aload           [ClassFile$ConstantValueAttribute cva]
        ifnonnull       L13
        // Line 820
        aload_0         [this]
        ldc_w           "    %-40s %s;%n"
        iconst_2
        anewarray       Object
        dup
        iconst_0
        aload           [String prefix]
        aastore
        dup
        iconst_1
        aload_2         [ClassFile$Field field]
        getfield        String ClassFile$Field.name
        aastore
        invokespecial   Disassembler.printf(String, Object[])
        // Line 821
        goto            L14
L13
        // Line 822
        aload_0         [this]
        ldc_w           "    %-40s %-15s = %s;%n"
        iconst_3
        anewarray       Object
        dup
        iconst_0
        aload           [String prefix]
        aastore
        dup
        iconst_1
        aload_2         [ClassFile$Field field]
        getfield        String ClassFile$Field.name
        aastore
        dup
        iconst_2
        aload           [ClassFile$ConstantValueAttribute cva]
        getfield        String ClassFile$ConstantValueAttribute.constantValue
        aastore
        invokespecial   Disassembler.printf(String, Object[])
L14
        // Line 827
        aload_0         [this]
        aload_2         [ClassFile$Field field]
        getfield        java.util.List ClassFile$Field.attributes
        ldc_w           "    // "
        bipush          6
        anewarray       ClassFile$Attribute
        dup
        iconst_0
        // Line 828
        aload_2         [ClassFile$Field field]
        getfield        ClassFile$ConstantValueAttribute ClassFile$Field.constantValueAttribute
        aastore
        dup
        iconst_1
        // Line 829
        aload_2         [ClassFile$Field field]
        getfield        ClassFile$DeprecatedAttribute ClassFile$Field.deprecatedAttribute
        aastore
        dup
        iconst_2
        // Line 830
        aload_2         [ClassFile$Field field]
        getfield        ClassFile$RuntimeInvisibleAnnotationsAttribute ClassFile$Field.runtimeInvisibleAnnotationsAttribute
        aastore
        dup
        iconst_3
        // Line 831
        aload_2         [ClassFile$Field field]
        getfield        ClassFile$RuntimeVisibleAnnotationsAttribute ClassFile$Field.runtimeVisibleAnnotationsAttribute
        aastore
        dup
        iconst_4
        // Line 832
        aload_2         [ClassFile$Field field]
        getfield        ClassFile$SignatureAttribute ClassFile$Field.signatureAttribute
        aastore
        dup
        iconst_5
        // Line 833
        aload_2         [ClassFile$Field field]
        getfield        ClassFile$SyntheticAttribute ClassFile$Field.syntheticAttribute
        aastore
        // Line 834
        getstatic       Disassembler$AttributeContext Disassembler$AttributeContext.FIELD
        // Line 827
        invokespecial   Disassembler.printAttributes(java.util.List, String, ClassFile$Attribute[], Disassembler$AttributeContext)
L1
        // Line 780
        aload_3         [v2]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L15
        // Line 836
        return
    }

    private static String toString(ClassFile$InnerClassesAttribute$ClasS c) {
        // Line 841
        aload_0         [ClassFile$InnerClassesAttribute$ClasS c]
        getfield        ConstantPool$ConstantClassInfo ClassFile$InnerClassesAttribute$ClasS.outerClassInfo
        astore_1        [ConstantPool$ConstantClassInfo oci]
        // Line 842
        aload_0         [ClassFile$InnerClassesAttribute$ClasS c]
        getfield        ConstantPool$ConstantClassInfo ClassFile$InnerClassesAttribute$ClasS.innerClassInfo
        astore_2        [ConstantPool$ConstantClassInfo ici]
        // Line 844
        aload_0         [ClassFile$InnerClassesAttribute$ClasS c]
        getfield        ClassFile$AccessFlags ClassFile$InnerClassesAttribute$ClasS.innerClassAccessFlags
        astore_3        [ClassFile$AccessFlags icafs]
        // Line 847
        aload_3         [ClassFile$AccessFlags icafs]
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.INTERFACE
        invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
        ifeq            L1
        // Line 848
        aload_3         [ClassFile$AccessFlags icafs]
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.ABSTRACT
        invokevirtual   ClassFile$AccessFlags.remove(ClassFile$AccessFlags$FlagType) => ClassFile$AccessFlags
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.STATIC
        invokevirtual   ClassFile$AccessFlags.remove(ClassFile$AccessFlags$FlagType) => ClassFile$AccessFlags
        astore_3        [ClassFile$AccessFlags icafs]
L1
        // Line 851
        new             StringBuilder
        dup
        invokespecial   StringBuilder()
        aload_1         [ConstantPool$ConstantClassInfo oci]
        ifnonnull       L2
        ldc_w           "[local class]"
        goto            L3
L2
        aload_1         [ConstantPool$ConstantClassInfo oci]
L3
        invokevirtual   StringBuilder.append(Object) => StringBuilder
        ldc_w           " { "
        invokevirtual   StringBuilder.append(String) => StringBuilder
        aload_3         [ClassFile$AccessFlags icafs]
        invokestatic    Disassembler.typeAccessFlagsToString(ClassFile$AccessFlags) => String
        invokevirtual   StringBuilder.append(String) => StringBuilder
        aload_2         [ConstantPool$ConstantClassInfo ici]
        invokevirtual   StringBuilder.append(Object) => StringBuilder
        ldc_w           " }"
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        areturn
    }

    private void printAttributes(java.util.List<ClassFile$Attribute> attributes, String prefix, ClassFile$Attribute[] excludedAttributes, Disassembler$AttributeContext context) {
        // Line 861
        new             java.util.ArrayList
        dup
        aload_1         [java.util.List<ClassFile$Attribute> attributes]
        invokespecial   java.util.ArrayList(java.util.Collection)
        astore          [java.util.List<ClassFile$Attribute> tmp]
        // Line 864
        aload_0         [this]
        getfield        boolean Disassembler.verbose
        ifne            L1
        // Line 865
        aload           [java.util.List<ClassFile$Attribute> tmp]
        aload_3         [ClassFile$Attribute[] excludedAttributes]
        invokestatic    java.util.Arrays.asList(Object[]) => java.util.List
        invokeinterface java.util.List.removeAll(java.util.Collection) => boolean
        pop
L1
        // Line 867
        aload           [java.util.List<ClassFile$Attribute> tmp]
        invokeinterface java.util.List.isEmpty() => boolean
        ifeq            L2
        return
L2
        // Line 869
        aload           [java.util.List<ClassFile$Attribute> tmp]
        new             Disassembler$2
        dup
        aload_0         [this]
        invokespecial   Disassembler$2(Disassembler)
        invokestatic    java.util.Collections.sort(java.util.List, java.util.Comparator)
        // Line 879
        aload_0         [this]
        new             StringBuilder
        dup
        aload_2         [String prefix]
        invokestatic    String.valueOf(Object) => String
        invokespecial   StringBuilder(String)
        aload_0         [this]
        getfield        boolean Disassembler.verbose
        ifeq            L3
        ldc_w           "Attributes:"
        goto            L4
L3
        ldc_w           "Unprocessed attributes:"
L4
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.println(String)
        // Line 880
        new             Disassembler$PrintAttributeVisitor
        dup
        aload_0         [this]
        new             StringBuilder
        dup
        aload_2         [String prefix]
        invokestatic    String.valueOf(Object) => String
        invokespecial   StringBuilder(String)
        ldc_w           "  "
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        aload           [Disassembler$AttributeContext context]
        invokespecial   Disassembler$PrintAttributeVisitor(Disassembler, String, Disassembler$AttributeContext)
        astore          [Disassembler$PrintAttributeVisitor visitor]
        // Line 881
        aload           [java.util.List<ClassFile$Attribute> tmp]
        invokeinterface java.util.List.iterator() => java.util.Iterator
        astore          [v4]
        goto            L5
L6
        aload           [v4]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       ClassFile$Attribute
        astore          [ClassFile$Attribute a]
        aload           [ClassFile$Attribute a]
        aload           [Disassembler$PrintAttributeVisitor visitor]
        invokeinterface ClassFile$Attribute.accept(ClassFile$AttributeVisitor)
L5
        aload           [v4]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L6
        // Line 882
        return
    }

    private void printParameters(@de.unkrig.commons.nullanalysis.Nullable ClassFile$RuntimeInvisibleParameterAnnotationsAttribute ripaa, @de.unkrig.commons.nullanalysis.Nullable ClassFile$RuntimeVisibleParameterAnnotationsAttribute rvpaa, java.util.List<SignatureParser$TypeSignature> parameterTypes, ClassFile$Method method, short firstIndex, boolean varargs) {
        // Line 1143
        aload_1         [ClassFile$RuntimeInvisibleParameterAnnotationsAttribute ripaa]
        ifnonnull       L1
        // Line 1144
        getstatic       java.util.List Disassembler.NO_PARAMETER_ANNOTATIONS
        goto            L2
L1
        // Line 1145
        aload_1         [ClassFile$RuntimeInvisibleParameterAnnotationsAttribute ripaa]
        getfield        java.util.List ClassFile$RuntimeInvisibleParameterAnnotationsAttribute.parameterAnnotations
L2
        // Line 1146
        invokeinterface java.util.List.iterator() => java.util.Iterator
        // Line 1142
        astore          [java.util.Iterator<ClassFile$ParameterAnnotation> ipas]
        // Line 1147
        aload_0         [this]
        ldc_w           "("
        invokespecial   Disassembler.print(String)
        // Line 1149
        aload_2         [ClassFile$RuntimeVisibleParameterAnnotationsAttribute rvpaa]
        ifnonnull       L3
        // Line 1150
        getstatic       java.util.List Disassembler.NO_PARAMETER_ANNOTATIONS
        goto            L4
L3
        // Line 1151
        aload_2         [ClassFile$RuntimeVisibleParameterAnnotationsAttribute rvpaa]
        getfield        java.util.List ClassFile$RuntimeVisibleParameterAnnotationsAttribute.parameterAnnotations
L4
        // Line 1152
        invokeinterface java.util.List.iterator() => java.util.Iterator
        // Line 1148
        astore          [java.util.Iterator<ClassFile$ParameterAnnotation> vpas]
        // Line 1154
        aload_3         [java.util.List<SignatureParser$TypeSignature> parameterTypes]
        invokeinterface java.util.List.iterator() => java.util.Iterator
        astore          [java.util.Iterator<SignatureParser$TypeSignature> it]
        // Line 1155
        aload           [java.util.Iterator<SignatureParser$TypeSignature> it]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifeq            L5
L15
        // Line 1157
        aload           [java.util.Iterator<SignatureParser$TypeSignature> it]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       SignatureParser$TypeSignature
        astore          [SignatureParser$TypeSignature pts]
        // Line 1160
        aload           [java.util.Iterator<ClassFile$ParameterAnnotation> ipas]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifeq            L6
        // Line 1161
        aload           [java.util.Iterator<ClassFile$ParameterAnnotation> ipas]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       ClassFile$ParameterAnnotation
        getfield        java.util.List ClassFile$ParameterAnnotation.annotations
        invokeinterface java.util.List.iterator() => java.util.Iterator
        astore          [v6]
        goto            L7
L8
        aload           [v6]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       ClassFile$Annotation
        astore          [ClassFile$Annotation a]
        aload_0         [this]
        new             StringBuilder
        dup
        invokespecial   StringBuilder()
        aload           [ClassFile$Annotation a]
        invokevirtual   StringBuilder.append(Object) => StringBuilder
        ldc_w           " "
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.print(String)
L7
        aload           [v6]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L8
L6
        // Line 1163
        aload           [java.util.Iterator<ClassFile$ParameterAnnotation> vpas]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifeq            L9
        // Line 1164
        aload           [java.util.Iterator<ClassFile$ParameterAnnotation> vpas]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       ClassFile$ParameterAnnotation
        getfield        java.util.List ClassFile$ParameterAnnotation.annotations
        invokeinterface java.util.List.iterator() => java.util.Iterator
        astore          [v6]
        goto            L10
L11
        aload           [v6]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       ClassFile$Annotation
        astore          [ClassFile$Annotation a]
        aload_0         [this]
        new             StringBuilder
        dup
        invokespecial   StringBuilder()
        aload           [ClassFile$Annotation a]
        invokevirtual   StringBuilder.append(Object) => StringBuilder
        ldc_w           " "
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.print(String)
L10
        aload           [v6]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L11
L9
        // Line 1168
        iload           [boolean varargs]
        ifeq            L12
        aload           [java.util.Iterator<SignatureParser$TypeSignature> it]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L12
        aload           [SignatureParser$TypeSignature pts]
        instanceof      SignatureParser$ArrayTypeSignature
        ifeq            L12
        // Line 1169
        aload_0         [this]
        new             StringBuilder
        dup
        invokespecial   StringBuilder()
        aload           [SignatureParser$TypeSignature pts]
        checkcast       SignatureParser$ArrayTypeSignature
        getfield        SignatureParser$TypeSignature SignatureParser$ArrayTypeSignature.componentTypeSignature
        invokevirtual   StringBuilder.append(Object) => StringBuilder
        ldc_w           "..."
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.print(String)
        // Line 1170
        goto            L13
L12
        // Line 1171
        aload_0         [this]
        aload           [SignatureParser$TypeSignature pts]
        invokeinterface SignatureParser$TypeSignature.toString() => String
        invokespecial   Disassembler.print(String)
L13
        // Line 1175
        aload_0         [this]
        new             StringBuilder
        dup
        bipush          32
        invokestatic    String.valueOf(char) => String
        invokespecial   StringBuilder(String)
        aload_0         [this]
        iload           [short firstIndex]
        iconst_0
        aload           [ClassFile$Method method]
        invokevirtual   Disassembler.getLocalVariable(short, int, ClassFile$Method) => Disassembler$LocalVariable
        getfield        String Disassembler$LocalVariable.name
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.print(String)
        // Line 1177
        aload           [java.util.Iterator<SignatureParser$TypeSignature> it]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L14
        goto            L5
L14
        // Line 1178
        iload           [short firstIndex]
        iconst_1
        iadd
        i2s
        istore          [short firstIndex]
        // Line 1179
        aload_0         [this]
        ldc_w           ", "
        invokespecial   Disassembler.print(String)
        // Line 1156
        goto            L15
L5
        // Line 1182
        aload_0         [this]
        ldc_w           ")"
        invokespecial   Disassembler.print(String)
        // Line 1183
        return
    }

    Disassembler$LocalVariable getLocalVariable(short localVariableIndex, int instructionOffset, ClassFile$Method method) {
        // Line 1189
        aload_3         [ClassFile$Method method]
        getfield        ClassFile$AccessFlags ClassFile$Method.accessFlags
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.STATIC
        invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
        ifeq            L1
        iconst_0
        goto            L2
L1
        iconst_1
L2
        istore          [int firstParameter]
        // Line 1190
        iload_1         [short localVariableIndex]
        iload           [int firstParameter]
        if_icmpge       L3
        // Line 1191
        new             Disassembler$LocalVariable
        dup
        aload_0         [this]
        aconst_null
        ldc_w           "this"
        invokespecial   Disassembler$LocalVariable(Disassembler, SignatureParser$TypeSignature, String)
        areturn
L3
        // Line 1196
        aload_3         [ClassFile$Method method]
        getfield        ClassFile$SignatureAttribute ClassFile$Method.signatureAttribute
        astore          [ClassFile$SignatureAttribute sa]
        // Line 1198
        aload           [ClassFile$SignatureAttribute sa]
        ifnull          L4
        // Line 1199
        aload_0         [this]
        aload           [ClassFile$SignatureAttribute sa]
        getfield        String ClassFile$SignatureAttribute.signature
        invokevirtual   Disassembler.decodeMethodTypeSignature(String) => SignatureParser$MethodTypeSignature
        goto            L5
L4
        // Line 1200
        aload_0         [this]
        aload_3         [ClassFile$Method method]
        getfield        String ClassFile$Method.descriptor
        invokevirtual   Disassembler.decodeMethodDescriptor(String) => SignatureParser$MethodTypeSignature
L5
        // Line 1197
        astore          [SignatureParser$MethodTypeSignature mts]
        // Line 1202
        aload           [SignatureParser$MethodTypeSignature mts]
        getfield        java.util.List SignatureParser$MethodTypeSignature.parameterTypes
        astore          [java.util.List<SignatureParser$TypeSignature> parameterTypes]
        // Line 1206
        iload           [int firstParameter]
        aload           [java.util.List<SignatureParser$TypeSignature> parameterTypes]
        invokeinterface java.util.List.size() => int
        iadd
        istore          [int firstLocalVariable]
        // Line 1209
        iload_1         [short localVariableIndex]
        iload           [int firstLocalVariable]
        if_icmpge       L6
        // Line 1210
        new             StringBuilder
        dup
        ldc_w           "p"
        invokespecial   StringBuilder(String)
        iconst_1
        iload_1         [short localVariableIndex]
        iadd
        iload           [int firstParameter]
        isub
        invokevirtual   StringBuilder.append(int) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        goto            L7
L6
        // Line 1211
        new             StringBuilder
        dup
        ldc_w           "v"
        invokespecial   StringBuilder(String)
        iconst_1
        iload_1         [short localVariableIndex]
        iadd
        iload           [int firstLocalVariable]
        isub
        invokevirtual   StringBuilder.append(int) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
L7
        // Line 1208
        astore          [String defaultName]
        // Line 1214
        aload_3         [ClassFile$Method method]
        getfield        ClassFile$CodeAttribute ClassFile$Method.codeAttribute
        astore          [ClassFile$CodeAttribute ca]
        // Line 1215
        aload           [ClassFile$CodeAttribute ca]
        ifnull          L8
        iload_1         [short localVariableIndex]
        iload           [int firstLocalVariable]
        if_icmpge       L9
        aload_0         [this]
        getfield        boolean Disassembler.hideVars
        ifne            L8
L9
        // Line 1216
        aload           [ClassFile$CodeAttribute ca]
        getfield        ClassFile$LocalVariableTypeTableAttribute ClassFile$CodeAttribute.localVariableTypeTableAttribute
        astore          [ClassFile$LocalVariableTypeTableAttribute lvtta]
        // Line 1217
        aload           [ClassFile$LocalVariableTypeTableAttribute lvtta]
        ifnull          L10
        // Line 1218
        aload           [ClassFile$LocalVariableTypeTableAttribute lvtta]
        getfield        java.util.List ClassFile$LocalVariableTypeTableAttribute.entries
        invokeinterface java.util.List.iterator() => java.util.Iterator
        astore          [v8]
        goto            L11
L14
        aload           [v8]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       ClassFile$LocalVariableTypeTableAttribute$Entry
        astore          [ClassFile$LocalVariableTypeTableAttribute$Entry lvtte]
        // Line 1220
        iload_2         [int instructionOffset]
        aload           [ClassFile$LocalVariableTypeTableAttribute$Entry lvtte]
        getfield        int ClassFile$LocalVariableTypeTableAttribute$Entry.startPC
        if_icmplt       L11
        // Line 1221
        iload_2         [int instructionOffset]
        aload           [ClassFile$LocalVariableTypeTableAttribute$Entry lvtte]
        getfield        int ClassFile$LocalVariableTypeTableAttribute$Entry.startPC
        aload           [ClassFile$LocalVariableTypeTableAttribute$Entry lvtte]
        getfield        int ClassFile$LocalVariableTypeTableAttribute$Entry.length
        iadd
        if_icmpgt       L11
        // Line 1222
        iload_1         [short localVariableIndex]
        aload           [ClassFile$LocalVariableTypeTableAttribute$Entry lvtte]
        getfield        short ClassFile$LocalVariableTypeTableAttribute$Entry.index
        if_icmpne       L11
        // Line 1224
        new             Disassembler$LocalVariable
        dup
        aload_0         [this]
        // Line 1225
        aload_0         [this]
        aload           [ClassFile$LocalVariableTypeTableAttribute$Entry lvtte]
        getfield        String ClassFile$LocalVariableTypeTableAttribute$Entry.signature
        invokespecial   Disassembler.decodeFieldTypeSignature(String) => SignatureParser$FieldTypeSignature
        // Line 1226
        aload_0         [this]
        getfield        boolean Disassembler.hideVars
        ifeq            L12
        aload           [String defaultName]
        goto            L13
L12
        aload           [ClassFile$LocalVariableTypeTableAttribute$Entry lvtte]
        getfield        String ClassFile$LocalVariableTypeTableAttribute$Entry.name
L13
        // Line 1224
        invokespecial   Disassembler$LocalVariable(Disassembler, SignatureParser$TypeSignature, String)
        areturn
L11
        // Line 1218
        aload           [v8]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L14
L10
        // Line 1232
        aload           [ClassFile$CodeAttribute ca]
        getfield        ClassFile$LocalVariableTableAttribute ClassFile$CodeAttribute.localVariableTableAttribute
        astore          [ClassFile$LocalVariableTableAttribute lvta]
        // Line 1233
        aload           [ClassFile$LocalVariableTableAttribute lvta]
        ifnull          L8
        // Line 1234
        aload           [ClassFile$LocalVariableTableAttribute lvta]
        getfield        java.util.List ClassFile$LocalVariableTableAttribute.entries
        invokeinterface java.util.List.iterator() => java.util.Iterator
        astore          [v9]
        goto            L15
L18
        aload           [v9]
        invokeinterface java.util.Iterator.next() => Object
        checkcast       ClassFile$LocalVariableTableAttribute$Entry
        astore          [ClassFile$LocalVariableTableAttribute$Entry lvte]
        // Line 1236
        iload_2         [int instructionOffset]
        aload           [ClassFile$LocalVariableTableAttribute$Entry lvte]
        getfield        short ClassFile$LocalVariableTableAttribute$Entry.startPC
        if_icmplt       L15
        // Line 1237
        iload_2         [int instructionOffset]
        aload           [ClassFile$LocalVariableTableAttribute$Entry lvte]
        getfield        short ClassFile$LocalVariableTableAttribute$Entry.startPC
        aload           [ClassFile$LocalVariableTableAttribute$Entry lvte]
        getfield        short ClassFile$LocalVariableTableAttribute$Entry.length
        iadd
        if_icmpgt       L15
        // Line 1238
        iload_1         [short localVariableIndex]
        aload           [ClassFile$LocalVariableTableAttribute$Entry lvte]
        getfield        short ClassFile$LocalVariableTableAttribute$Entry.index
        if_icmpne       L15
        // Line 1240
        new             Disassembler$LocalVariable
        dup
        aload_0         [this]
        // Line 1241
        aload_0         [this]
        aload           [ClassFile$LocalVariableTableAttribute$Entry lvte]
        getfield        String ClassFile$LocalVariableTableAttribute$Entry.descriptor
        invokevirtual   Disassembler.decodeFieldDescriptor(String) => SignatureParser$TypeSignature
        // Line 1242
        aload_0         [this]
        getfield        boolean Disassembler.hideVars
        ifeq            L16
        aload           [String defaultName]
        goto            L17
L16
        aload           [ClassFile$LocalVariableTableAttribute$Entry lvte]
        getfield        String ClassFile$LocalVariableTableAttribute$Entry.name
L17
        // Line 1240
        invokespecial   Disassembler$LocalVariable(Disassembler, SignatureParser$TypeSignature, String)
        areturn
L15
        // Line 1234
        aload           [v9]
        invokeinterface java.util.Iterator.hasNext() => boolean
        ifne            L18
L8
        // Line 1249
        iload_1         [short localVariableIndex]
        iload           [int firstLocalVariable]
        if_icmpge       L19
        // Line 1250
        new             Disassembler$LocalVariable
        dup
        aload_0         [this]
        aload           [java.util.List<SignatureParser$TypeSignature> parameterTypes]
        iload_1         [short localVariableIndex]
        iload           [int firstParameter]
        isub
        invokeinterface java.util.List.get(int) => Object
        checkcast       SignatureParser$TypeSignature
        aload           [String defaultName]
        invokespecial   Disassembler$LocalVariable(Disassembler, SignatureParser$TypeSignature, String)
        areturn
L19
        // Line 1252
        new             Disassembler$LocalVariable
        dup
        aload_0         [this]
        aconst_null
        aload           [String defaultName]
        invokespecial   Disassembler$LocalVariable(Disassembler, SignatureParser$TypeSignature, String)
        areturn
    }

    private SignatureParser$ClassSignature decodeClassSignature(String cs) {
        try {
            // Line 1259
            aload_0         [this]
            getfield        SignatureParser Disassembler.signatureParser
            aload_1         [String cs]
            invokevirtual   SignatureParser.decodeClassSignature(String) => SignatureParser$ClassSignature
        } catch (SignatureParser$SignatureException => L1)
        areturn
L1
        // Line 1260
        astore_2        [SignatureParser$SignatureException e]
        // Line 1261
        aload_0         [this]
        new             StringBuilder
        dup
        ldc_w           "Decoding class signature '"
        invokespecial   StringBuilder(String)
        aload_1         [String cs]
        invokevirtual   StringBuilder.append(String) => StringBuilder
        ldc_w           "': "
        invokevirtual   StringBuilder.append(String) => StringBuilder
        aload_2         [SignatureParser$SignatureException e]
        invokevirtual   SignatureParser$SignatureException.getMessage() => String
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.error(String)
        // Line 1262
        new             SignatureParser$ClassSignature
        dup
        // Line 1263
        getstatic       java.util.List Disassembler.NO_FORMAL_TYPE_PARAMETERS
        // Line 1264
        aload_0         [this]
        getfield        SignatureParser Disassembler.signatureParser
        getfield        SignatureParser$ClassTypeSignature SignatureParser.object
        // Line 1265
        getstatic       java.util.List Disassembler.NO_CLASS_TYPE_SIGNATURES
        // Line 1262
        invokespecial   SignatureParser$ClassSignature(java.util.List, SignatureParser$ClassTypeSignature, java.util.List)
        areturn
    }

    private SignatureParser$FieldTypeSignature decodeFieldTypeSignature(String fs) {
        try {
            // Line 1273
            aload_0         [this]
            getfield        SignatureParser Disassembler.signatureParser
            aload_1         [String fs]
            invokevirtual   SignatureParser.decodeFieldTypeSignature(String) => SignatureParser$FieldTypeSignature
        } catch (SignatureParser$SignatureException => L1)
        areturn
L1
        // Line 1274
        astore_2        [SignatureParser$SignatureException e]
        // Line 1275
        aload_0         [this]
        new             StringBuilder
        dup
        ldc_w           "Decoding field type signature '"
        invokespecial   StringBuilder(String)
        aload_1         [String fs]
        invokevirtual   StringBuilder.append(String) => StringBuilder
        ldc_w           "': "
        invokevirtual   StringBuilder.append(String) => StringBuilder
        aload_2         [SignatureParser$SignatureException e]
        invokevirtual   SignatureParser$SignatureException.getMessage() => String
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.error(String)
        // Line 1276
        aload_0         [this]
        getfield        SignatureParser Disassembler.signatureParser
        getfield        SignatureParser$ClassTypeSignature SignatureParser.object
        areturn
    }

    SignatureParser$MethodTypeSignature decodeMethodTypeSignature(String ms) {
        try {
            // Line 1283
            aload_0         [this]
            getfield        SignatureParser Disassembler.signatureParser
            aload_1         [String ms]
            invokevirtual   SignatureParser.decodeMethodTypeSignature(String) => SignatureParser$MethodTypeSignature
        } catch (SignatureParser$SignatureException => L1)
        areturn
L1
        // Line 1284
        astore_2        [SignatureParser$SignatureException e]
        // Line 1285
        aload_0         [this]
        new             StringBuilder
        dup
        ldc_w           "Decoding method type signature '"
        invokespecial   StringBuilder(String)
        aload_1         [String ms]
        invokevirtual   StringBuilder.append(String) => StringBuilder
        ldc_w           "': "
        invokevirtual   StringBuilder.append(String) => StringBuilder
        aload_2         [SignatureParser$SignatureException e]
        invokevirtual   SignatureParser$SignatureException.getMessage() => String
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.error(String)
        // Line 1286
        new             SignatureParser$MethodTypeSignature
        dup
        // Line 1287
        getstatic       java.util.List Disassembler.NO_FORMAL_TYPE_PARAMETERS
        // Line 1288
        getstatic       java.util.List Disassembler.NO_TYPE_SIGNATURES
        // Line 1289
        getstatic       SignatureParser$TypeSignature SignatureParser.VOID
        // Line 1290
        getstatic       java.util.List Disassembler.NO_THROWS_SIGNATURES
        // Line 1286
        invokespecial   SignatureParser$MethodTypeSignature(java.util.List, java.util.List, SignatureParser$TypeSignature, java.util.List)
        areturn
    }

    SignatureParser$TypeSignature decodeFieldDescriptor(String fd) {
        try {
            // Line 1298
            aload_0         [this]
            getfield        SignatureParser Disassembler.signatureParser
            aload_1         [String fd]
            invokevirtual   SignatureParser.decodeFieldDescriptor(String) => SignatureParser$TypeSignature
        } catch (SignatureParser$SignatureException => L1)
        areturn
L1
        // Line 1299
        astore_2        [SignatureParser$SignatureException e]
        // Line 1300
        aload_0         [this]
        new             StringBuilder
        dup
        ldc_w           "Decoding field descriptor '"
        invokespecial   StringBuilder(String)
        aload_1         [String fd]
        invokevirtual   StringBuilder.append(String) => StringBuilder
        ldc_w           "': "
        invokevirtual   StringBuilder.append(String) => StringBuilder
        aload_2         [SignatureParser$SignatureException e]
        invokevirtual   SignatureParser$SignatureException.getMessage() => String
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.error(String)
        // Line 1301
        getstatic       SignatureParser$PrimitiveTypeSignature SignatureParser.INT
        areturn
    }

    SignatureParser$MethodTypeSignature decodeMethodDescriptor(String md) {
        try {
            // Line 1308
            aload_0         [this]
            getfield        SignatureParser Disassembler.signatureParser
            aload_1         [String md]
            invokevirtual   SignatureParser.decodeMethodDescriptor(String) => SignatureParser$MethodTypeSignature
        } catch (SignatureParser$SignatureException => L1)
        areturn
L1
        // Line 1309
        astore_2        [SignatureParser$SignatureException e]
        // Line 1310
        aload_0         [this]
        new             StringBuilder
        dup
        ldc_w           "Decoding method descriptor '"
        invokespecial   StringBuilder(String)
        aload_1         [String md]
        invokevirtual   StringBuilder.append(String) => StringBuilder
        ldc_w           "': "
        invokevirtual   StringBuilder.append(String) => StringBuilder
        aload_2         [SignatureParser$SignatureException e]
        invokevirtual   SignatureParser$SignatureException.getMessage() => String
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokespecial   Disassembler.error(String)
        // Line 1311
        new             SignatureParser$MethodTypeSignature
        dup
        // Line 1312
        getstatic       java.util.List Disassembler.NO_FORMAL_TYPE_PARAMETERS
        // Line 1313
        getstatic       java.util.List Disassembler.NO_TYPE_SIGNATURES
        // Line 1314
        getstatic       SignatureParser$TypeSignature SignatureParser.VOID
        // Line 1315
        getstatic       java.util.List Disassembler.NO_THROWS_SIGNATURES
        // Line 1311
        invokespecial   SignatureParser$MethodTypeSignature(java.util.List, java.util.List, SignatureParser$TypeSignature, java.util.List)
        areturn
    }

    private void error(String message) {
        // Line 1351
        aload_0         [this]
        getfield        java.io.PrintWriter Disassembler.pw
        new             StringBuilder
        dup
        ldc_w           "*** Error: "
        invokespecial   StringBuilder(String)
        aload_1         [String message]
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        invokevirtual   java.io.PrintWriter.println(String)
        // Line 1352
        return
    }

    private static String typeAccessFlagsToString(ClassFile$AccessFlags af) {
        // Line 1357
        aload_0         [ClassFile$AccessFlags af]
        invokevirtual   ClassFile$AccessFlags.toString() => String
        astore_1        [String result]
        // Line 1361
        aload_0         [ClassFile$AccessFlags af]
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.ENUM
        invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
        ifne            L1
        aload_0         [ClassFile$AccessFlags af]
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.ANNOTATION
        invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
        ifne            L1
        aload_0         [ClassFile$AccessFlags af]
        getstatic       ClassFile$AccessFlags$FlagType ClassFile$AccessFlags$FlagType.INTERFACE
        invokevirtual   ClassFile$AccessFlags.is(ClassFile$AccessFlags$FlagType) => boolean
        ifne            L1
        // Line 1362
        new             StringBuilder
        dup
        aload_1         [String result]
        invokestatic    String.valueOf(Object) => String
        invokespecial   StringBuilder(String)
        ldc_w           "class "
        invokevirtual   StringBuilder.append(String) => StringBuilder
        invokevirtual   StringBuilder.toString() => String
        astore_1        [String result]
L1
        // Line 1365
        aload_1         [String result]
        areturn
    }

    // (Synthetic method)
    static void access$0(Disassembler p1, String p2) {
        // Line 323
        aload_0         [Disassembler p1]
        aload_1         [String p2]
        invokespecial   Disassembler.println(String)
        return
    }

    // (Synthetic method)
    static void access$1(Disassembler p1, String p2) {
        // Line 321
        aload_0         [Disassembler p1]
        aload_1         [String p2]
        invokespecial   Disassembler.print(String)
        return
    }

    // (Synthetic method)
    static void access$2(Disassembler p1, String p2, Object[] p3) {
        // Line 324
        aload_0         [Disassembler p1]
        aload_1         [String p2]
        aload_2         [Object[] p3]
        invokespecial   Disassembler.printf(String, Object[])
        return
    }

    // (Synthetic method)
    static void access$3(Disassembler p1) {
        // Line 322
        aload_0         [Disassembler p1]
        invokespecial   Disassembler.println()
        return
    }

    // (Synthetic method)
    static String access$4(ClassFile$InnerClassesAttribute$ClasS p1) {
        // Line 839
        aload_0         [ClassFile$InnerClassesAttribute$ClasS p1]
        invokestatic    Disassembler.toString(ClassFile$InnerClassesAttribute$ClasS) => String
        areturn
    }

    // (Synthetic method)
    static SignatureParser$FieldTypeSignature access$5(Disassembler p1, String p2) {
        // Line 1271
        aload_0         [Disassembler p1]
        aload_1         [String p2]
        invokespecial   Disassembler.decodeFieldTypeSignature(String) => SignatureParser$FieldTypeSignature
        areturn
    }

    // (Synthetic method)
    static SignatureParser$ClassSignature access$6(Disassembler p1, String p2) {
        // Line 1257
        aload_0         [Disassembler p1]
        aload_1         [String p2]
        invokespecial   Disassembler.decodeClassSignature(String) => SignatureParser$ClassSignature
        areturn
    }
}
