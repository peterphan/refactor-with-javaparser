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


/**
 * Reproducer for a JavaParser LexicalPreservingPrinter issue where variable declaration
 * types are not preserved after renaming {@code ClassOrInterfaceType} nodes by name
 * using resolved types. The test asserts the AST string (no styles) is correct, and then
 * demonstrates that the styled printer currently fails to reflect the same changes.
 */
public class RefactorTest {

  private static final String HOME = System.getProperty("user.dir");
  private static final String JAVA_PATH = HOME + "/src/main/java";

  @Test
  public void testRefactorNonInclusive() throws Exception {
    setupSymbolResolution();
    CompilationUnit cu = StaticJavaParser.parse(new File(JAVA_PATH + "/a/b/c/RefactorMe.java"));
    CompilationUnit lcu = LexicalPreservingPrinter.setup(cu);
    /*
     * Map from fully-qualified original types to the short names we want to rename to.
     * Short names are used intentionally so existing imports and formatting are preserved
     * by the lexical-preserving printer when possible.
     */
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

    /*
     * Sanity check: the raw AST toString (no styles) reflects the desired renames.
     */
    assertEquals(expectedContentWithoutStyles, lcu.toString());

    /*
     * Known bug demonstration: this assertion should NOT pass, but it currently does,
     * showing that variable declaration types are not being updated by the styled printer.
     */
    assertEquals(notExpectedContent, LexicalPreservingPrinter.print(lcu));

    /*
     * Intended behavior: when fixed upstream, the styled output should match the expected file.
     * This is the assertion that currently fails and documents the desired outcome.
     */
    assertEquals(expectedContentWithStyles, LexicalPreservingPrinter.print(lcu));
  }

  /**
   * Reads a file from test resources into a String.
   */
  private String getResourceFileContents(String resource) throws Exception {
    URL url = getClass().getResource(resource);
    return Files.readString(Path.of(url.toURI()));
  }

  /**
   * Configures JavaParser with a symbol solver that can resolve types from the JDK
   * and from the project source directory so we can obtain fully-qualified names.
   */
  private void setupSymbolResolution() {
    CombinedTypeSolver combinedSolver =
        new CombinedTypeSolver(new ReflectionTypeSolver(), new JavaParserTypeSolver(JAVA_PATH));
    JavaSymbolSolver javaSolver = new JavaSymbolSolver(combinedSolver);
    StaticJavaParser.getConfiguration().setSymbolResolver(javaSolver);
  }

  /**
   * If the resolved qualified name of the provided type matches {@code oldFQClz},
   * rename the simple name to {@code newClz}. This targets declarations and usages uniformly.
   */
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
