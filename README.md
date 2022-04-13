# refactor-with-javaparser

This repo is meant to showcase a bug with JavaParser's LexicalPreservingPrinter not preserving changes to VariableDeclarations.

The test is changing the type of `NonInclusive*` classes to its `Inclusive*` counterparts.
The source file is https://github.com/peterphan/refactor-with-javaparser/blob/master/src/main/java/a/b/c/RefactorMe.java

## Expected Behavior
After refactoring, we want the new file to look like https://github.com/peterphan/refactor-with-javaparser/blob/master/src/test/resources/RefactorMeExpected.java

## Actual Behavior
Instead, we see the resultant file looking like https://github.com/peterphan/refactor-with-javaparser/blob/master/src/test/resources/RefactorMeBug.java

As a sanity check, when calling `toString()` on the CompilationUnit, we see that the AST changes do look correct: https://github.com/peterphan/refactor-with-javaparser/blob/master/src/test/resources/RefactorMeExpectedWithoutStyles.java
