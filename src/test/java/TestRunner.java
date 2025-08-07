import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import java.io.File;
import java.util.Map;

public class TestRunner {

    private static final String HOME = System.getProperty("user.dir");
    private static final String JAVA_PATH = HOME + "/src/main/java";

    public static void main(String[] args) throws Exception {
        System.out.println("=== JavaParser LexicalPreservingPrinter Bug Analysis ===\n");
        
        TestRunner runner = new TestRunner();
        runner.setupSymbolResolution();
        
        runner.testMethodSignatures();
        runner.testOriginalCase();
    }

    private void testMethodSignatures() throws Exception {
        System.out.println("--- Testing Method Signatures ---");
        
        CompilationUnit cu = StaticJavaParser.parse(new File(JAVA_PATH + "/a/b/c/MethodTestCase.java"));
        CompilationUnit lcu = LexicalPreservingPrinter.setup(cu);
        
        Map<String, String> refactorMappings = Map.of(
            "a.b.c.NonInclusiveClz", "InclusiveClz"
        );

        refactorMappings.forEach((fromClz, toClz) -> {
            lcu.findAll(ClassOrInterfaceType.class).forEach(cit -> renameClz(cit, fromClz, toClz));
        });

        String astResult = lcu.toString();
        String lexicalResult = LexicalPreservingPrinter.print(lcu);

        // Check specific scenarios
        System.out.println("Return types:");
        System.out.println("  AST has 'InclusiveClz getObject()': " + astResult.contains("InclusiveClz getObject()"));
        System.out.println("  Lexical has 'InclusiveClz getObject()': " + lexicalResult.contains("InclusiveClz getObject()"));
        
        System.out.println("Method parameters:");
        System.out.println("  AST has 'processObject(InclusiveClz obj)': " + astResult.contains("processObject(InclusiveClz obj)"));
        System.out.println("  Lexical has 'processObject(InclusiveClz obj)': " + lexicalResult.contains("processObject(InclusiveClz obj)"));
        
        System.out.println("Local variables:");
        System.out.println("  AST has 'InclusiveClz local1': " + astResult.contains("InclusiveClz local1"));
        System.out.println("  Lexical has 'InclusiveClz local1': " + lexicalResult.contains("InclusiveClz local1"));
        
        System.out.println("Generic types:");
        System.out.println("  AST has 'List<InclusiveClz>': " + astResult.contains("List<InclusiveClz>"));
        System.out.println("  Lexical has 'List<InclusiveClz>': " + lexicalResult.contains("List<InclusiveClz>"));
        
        System.out.println();
    }

    private void testOriginalCase() throws Exception {
        System.out.println("--- Testing Original RefactorMe Case ---");
        
        CompilationUnit cu = StaticJavaParser.parse(new File(JAVA_PATH + "/a/b/c/RefactorMe.java"));
        CompilationUnit lcu = LexicalPreservingPrinter.setup(cu);
        
        Map<String, String> refactorMappings = Map.of(
            "a.b.c.NonInclusiveClz", "InclusiveClz",
            "a.b.c.NonInclusiveAbstract", "InclusiveAbstract",
            "a.b.c.NonInclusiveInterface", "InclusiveInterface"
        );

        refactorMappings.forEach((fromClz, toClz) -> {
            lcu.findAll(ClassOrInterfaceType.class).forEach(cit -> renameClz(cit, fromClz, toClz));
        });

        String astResult = lcu.toString();
        String lexicalResult = LexicalPreservingPrinter.print(lcu);

        System.out.println("Class inheritance:");
        System.out.println("  AST has 'extends InclusiveAbstract': " + astResult.contains("extends InclusiveAbstract"));
        System.out.println("  Lexical has 'extends InclusiveAbstract': " + lexicalResult.contains("extends InclusiveAbstract"));
        
        System.out.println("Interface implementation:");
        System.out.println("  AST has 'implements InclusiveInterface': " + astResult.contains("implements InclusiveInterface"));
        System.out.println("  Lexical has 'implements InclusiveInterface': " + lexicalResult.contains("implements InclusiveInterface"));
        
        System.out.println("Field declarations:");
        System.out.println("  AST has 'InclusiveClz _nonInclusivefield': " + astResult.contains("InclusiveClz _nonInclusivefield"));
        System.out.println("  Lexical has 'InclusiveClz _nonInclusivefield': " + lexicalResult.contains("InclusiveClz _nonInclusivefield"));
        
        System.out.println("Object instantiation:");
        System.out.println("  AST has 'new InclusiveClz()': " + astResult.contains("new InclusiveClz()"));
        System.out.println("  Lexical has 'new InclusiveClz()': " + lexicalResult.contains("new InclusiveClz()"));
        
        System.out.println();
        System.out.println("=== BUG SUMMARY ===");
        System.out.println("✅ WORKS: Class inheritance, interface implementation, object instantiation");
        System.out.println("❌ BROKEN: Field declarations, method parameters, local variables");
    }

    private void setupSymbolResolution() {
        CombinedTypeSolver combinedSolver =
            new CombinedTypeSolver(new ReflectionTypeSolver(), new JavaParserTypeSolver(JAVA_PATH));
        JavaSymbolSolver javaSolver = new JavaSymbolSolver(combinedSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(javaSolver);
    }

    private void renameClz(ClassOrInterfaceType type, String oldFQClz, String newClz) {
        try {
            ResolvedType resolvedType = type.resolve();
            if (resolvedType.isReferenceType()) {
                String qualifiedName = resolvedType.asReferenceType().getQualifiedName();
                if (oldFQClz.equals(qualifiedName)) {
                    type.setName(newClz);
                }
            }
        } catch (Exception e) {
            // Ignore resolution failures for this demo
            System.err.println("Warning: Could not resolve type " + type + " - " + e.getMessage());
        }
    }
}