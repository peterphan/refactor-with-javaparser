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
