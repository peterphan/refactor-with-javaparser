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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class RefactorTest {

  private static final String HOME = System.getProperty("user.dir");
  private static final String JAVA_PATH = HOME + "/src/main/java";

  @Test
  public void testRefactorNonInclusive() throws Exception {
    setupSymbolResolution();
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

    String expectedContentWithoutStyles = getResourceFileContents("RefactorMeExpectedWithoutStyles.java");
    String expectedContentWithStyles = getResourceFileContents("RefactorMeExpected.java");
    String notExpectedContent = getResourceFileContents("RefactorMeBug.java");

    assertEquals(expectedContentWithoutStyles, lcu.toString());

    // This should NOT pass, but it does =( The types in variable declarations are not being preserved
    assertEquals(notExpectedContent, LexicalPreservingPrinter.print(lcu));

    // This line fails. The types in variable declarations are not being preserved
    assertEquals(expectedContentWithStyles, LexicalPreservingPrinter.print(lcu));
  }

  @Test
  public void testMethodSignatureRefactoring() throws Exception {
    setupSymbolResolution();
    CompilationUnit cu = StaticJavaParser.parse(new File(JAVA_PATH + "/a/b/c/MethodTestCase.java"));
    CompilationUnit lcu = LexicalPreservingPrinter.setup(cu);
    
    Map<String, String> refactorMappings = Map.of(
        "a.b.c.NonInclusiveClz", "InclusiveClz"
    );

    refactorMappings.forEach((fromClz, toClz) -> {
      lcu.findAll(ClassOrInterfaceType.class).forEach(cit -> renameClz(cit, fromClz, toClz));
    });

    String result = LexicalPreservingPrinter.print(lcu);
    String astResult = lcu.toString();

    // Verify AST changes are correct
    assertTrue(astResult.contains("InclusiveClz getObject()"), "AST should contain refactored return type");
    assertTrue(astResult.contains("List<InclusiveClz> getList()"), "AST should contain refactored generic return type");
    assertTrue(astResult.contains("processObject(InclusiveClz obj)"), "AST should contain refactored parameter type");
    assertTrue(astResult.contains("InclusiveClz local1"), "AST should contain refactored local variable type");

    // These assertions will likely fail due to the LexicalPreservingPrinter bug
    System.out.println("=== Method Test Case Results ===");
    System.out.println("AST toString() contains correct types: " + 
        astResult.contains("InclusiveClz getObject()"));
    System.out.println("LexicalPreservingPrinter contains correct types: " + 
        result.contains("InclusiveClz getObject()"));
    
    // Document the expected failures
    assertNotEquals(astResult, result, "LexicalPreservingPrinter output should differ from AST toString()");
  }

  @Test
  public void testStaticAndComplexScenarios() throws Exception {
    setupSymbolResolution();
    CompilationUnit cu = StaticJavaParser.parse(new File(JAVA_PATH + "/a/b/c/StaticTestCase.java"));
    CompilationUnit lcu = LexicalPreservingPrinter.setup(cu);
    
    Map<String, String> refactorMappings = Map.of(
        "a.b.c.NonInclusiveClz", "InclusiveClz"
    );

    refactorMappings.forEach((fromClz, toClz) -> {
      lcu.findAll(ClassOrInterfaceType.class).forEach(cit -> renameClz(cit, fromClz, toClz));
    });

    String result = LexicalPreservingPrinter.print(lcu);
    String astResult = lcu.toString();

    // Test static fields
    assertTrue(astResult.contains("static InclusiveClz STATIC_FIELD"), 
        "AST should contain refactored static field type");
    
    // Test generic bounds
    assertTrue(astResult.contains("<T extends InclusiveClz>"), 
        "AST should contain refactored generic bound");
    
    // Test wildcard generics
    assertTrue(astResult.contains("? extends InclusiveClz"), 
        "AST should contain refactored wildcard generic");
    
    // Test functional interfaces
    assertTrue(astResult.contains("Function<InclusiveClz, String>"), 
        "AST should contain refactored functional interface type");

    System.out.println("=== Static Test Case Results ===");
    System.out.println("AST contains refactored static fields: " + 
        astResult.contains("static InclusiveClz STATIC_FIELD"));
    System.out.println("LexicalPreservingPrinter contains refactored static fields: " + 
        result.contains("static InclusiveClz STATIC_FIELD"));
    
    // These are expected to be different due to the bug
    assertNotEquals(astResult, result, "LexicalPreservingPrinter should have preservation issues");
  }

  @Test
  public void testAnnotationsAndAdvancedFeatures() throws Exception {
    setupSymbolResolution();
    CompilationUnit cu = StaticJavaParser.parse(new File(JAVA_PATH + "/a/b/c/AnnotationTestCase.java"));
    CompilationUnit lcu = LexicalPreservingPrinter.setup(cu);
    
    Map<String, String> refactorMappings = Map.of(
        "a.b.c.NonInclusiveClz", "InclusiveClz"
    );

    refactorMappings.forEach((fromClz, toClz) -> {
      lcu.findAll(ClassOrInterfaceType.class).forEach(cit -> renameClz(cit, fromClz, toClz));
    });

    String result = LexicalPreservingPrinter.print(lcu);
    String astResult = lcu.toString();

    // Test field with annotations
    assertTrue(astResult.contains("@Deprecated"), "AST should preserve annotations");
    assertTrue(astResult.contains("private InclusiveClz deprecatedField"), 
        "AST should contain refactored annotated field type");
    
    // Test constructor parameters
    assertTrue(astResult.contains("AnnotationTestCase(InclusiveClz initialValue)"), 
        "AST should contain refactored constructor parameter type");
    
    // Test final variables
    assertTrue(astResult.contains("final InclusiveClz finalVar"), 
        "AST should contain refactored final variable type");
    
    // Test enum with associated values
    assertTrue(astResult.contains("private final InclusiveClz associatedValue"), 
        "AST should contain refactored enum field type");

    System.out.println("=== Annotation Test Case Results ===");
    System.out.println("AST contains refactored constructor params: " + 
        astResult.contains("AnnotationTestCase(InclusiveClz initialValue)"));
    System.out.println("LexicalPreservingPrinter contains refactored constructor params: " + 
        result.contains("AnnotationTestCase(InclusiveClz initialValue)"));
    
    // Document which scenarios work vs don't work
    boolean constructorParamsWork = result.contains("AnnotationTestCase(InclusiveClz initialValue)");
    boolean fieldTypesWork = result.contains("private InclusiveClz deprecatedField");
    boolean finalVarsWork = result.contains("final InclusiveClz finalVar");
    
    System.out.println("Constructor parameters preserved: " + constructorParamsWork);
    System.out.println("Field types preserved: " + fieldTypesWork);
    System.out.println("Final variables preserved: " + finalVarsWork);
  }

  @Test
  public void testComprehensiveBugAnalysis() throws Exception {
    // This test runs all scenarios and provides a comprehensive analysis
    setupSymbolResolution();
    
    String[] testFiles = {
        "RefactorMe.java",
        "MethodTestCase.java", 
        "StaticTestCase.java",
        "AnnotationTestCase.java"
    };
    
    Map<String, String> refactorMappings = Map.of(
        "a.b.c.NonInclusiveClz", "InclusiveClz",
        "a.b.c.NonInclusiveAbstract", "InclusiveAbstract",
        "a.b.c.NonInclusiveInterface", "InclusiveInterface"
    );
    
    System.out.println("=== Comprehensive JavaParser LexicalPreservingPrinter Bug Analysis ===");
    
    for (String fileName : testFiles) {
      System.out.println("\n--- Testing: " + fileName + " ---");
      
      CompilationUnit cu = StaticJavaParser.parse(new File(JAVA_PATH + "/a/b/c/" + fileName));
      CompilationUnit lcu = LexicalPreservingPrinter.setup(cu);
      
      refactorMappings.forEach((fromClz, toClz) -> {
        lcu.findAll(ClassOrInterfaceType.class).forEach(cit -> renameClz(cit, fromClz, toClz));
      });
      
      String astResult = lcu.toString();
      String lexicalResult = LexicalPreservingPrinter.print(lcu);
      
      // Count occurrences of old vs new types
      long astOldTypes = astResult.lines().mapToLong(line -> 
          refactorMappings.keySet().stream().mapToLong(oldType -> 
              line.split(oldType.substring(oldType.lastIndexOf('.') + 1), -1).length - 1
          ).sum()
      ).sum();
      
      long lexicalOldTypes = lexicalResult.lines().mapToLong(line -> 
          refactorMappings.keySet().stream().mapToLong(oldType -> 
              line.split(oldType.substring(oldType.lastIndexOf('.') + 1), -1).length - 1
          ).sum()
      ).sum();
      
      System.out.println("AST has " + astOldTypes + " unreplaced old type references");
      System.out.println("LexicalPreservingPrinter has " + lexicalOldTypes + " unreplaced old type references");
      System.out.println("Preservation working correctly: " + (astOldTypes == lexicalOldTypes));
      
      if (astOldTypes != lexicalOldTypes) {
        System.out.println("❌ BUG DETECTED: LexicalPreservingPrinter failed to preserve " + 
            (lexicalOldTypes - astOldTypes) + " type changes");
      } else {
        System.out.println("✅ LexicalPreservingPrinter working correctly for this file");
      }
    }
  }

  private String getResourceFileContents(String resource) throws Exception {
    URL url = getClass().getResource(resource);
    return Files.readString(Path.of(url.toURI()));
  }

  private void setupSymbolResolution() {
    CombinedTypeSolver combinedSolver =
        new CombinedTypeSolver(new ReflectionTypeSolver(), new JavaParserTypeSolver(JAVA_PATH));
    JavaSymbolSolver javaSolver = new JavaSymbolSolver(combinedSolver);
    StaticJavaParser.getConfiguration().setSymbolResolver(javaSolver);
  }

  private void renameClz(ClassOrInterfaceType type, String oldFQClz, String newClz) {
    ResolvedType resolvedType = type.resolve();
    if (resolvedType.isReferenceType()) {
      String qualifiedName = resolvedType.asReferenceType().getQualifiedName();
      if (oldFQClz.equals(qualifiedName)) {
        type.setName(newClz);
      }
    }
//    // This is for generics... e.g. List<String>
//    // getTypedArguments() would give you List.of(<String type>)
//    if (clzType.getTypeArguments().isPresent() && !clzType.getTypeArguments().get().isEmpty()) {
//      clzType.getTypeArguments().get().forEach(typedArg -> {
//        renameClz(typedArg, oldFQClz, newClz);
//      });
//    }
  }

}
