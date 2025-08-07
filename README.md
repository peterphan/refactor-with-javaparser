# JavaParser LexicalPreservingPrinter Bug Demonstration

This project demonstrates a bug in JavaParser's `LexicalPreservingPrinter` where changes to variable declaration types are not properly preserved during code refactoring operations.

## What This Code Does

This is a comprehensive test suite that showcases an automated refactoring scenario where:

1. **Source Classes**: The project contains pairs of classes with "NonInclusive" and "Inclusive" naming conventions:
   - `NonInclusiveClz` → `InclusiveClz`
   - `NonInclusiveAbstract` → `InclusiveAbstract` 
   - `NonInclusiveInterface` → `InclusiveInterface`

2. **Refactoring Targets**: Multiple test files demonstrate the bug across different Java language features:
   - **`RefactorMe.java`**: Basic field declarations, inheritance, and interface implementation
   - **`MethodTestCase.java`**: Method signatures, parameters, return types, local variables, and array types
   - **`StaticTestCase.java`**: Static fields/methods, generic bounds, wildcards, functional interfaces, and nested classes
   - **`AnnotationTestCase.java`**: Annotations, constructors, synchronized methods, final variables, lambdas, and enums

3. **Automated Refactoring**: The test code uses JavaParser to automatically replace all "NonInclusive" type references with their "Inclusive" counterparts while attempting to preserve the original code formatting and style.

## The Bug Being Demonstrated

The JavaParser `LexicalPreservingPrinter` has inconsistent behavior:

- ✅ **Works correctly** for: class inheritance, interface implementation, and object instantiation
- ❌ **Fails to preserve changes** for: variable declarations and field types

## Project Structure

```
src/
├── main/java/a/b/c/
│   ├── RefactorMe.java              # Basic refactoring test case
│   ├── MethodTestCase.java          # Method signatures and local variables
│   ├── StaticTestCase.java          # Static members and complex generics
│   ├── AnnotationTestCase.java      # Annotations and advanced features
│   ├── NonInclusiveClz.java         # Original class
│   ├── InclusiveClz.java           # Target replacement class
│   ├── NonInclusiveAbstract.java   # Original abstract class
│   ├── InclusiveAbstract.java      # Target replacement abstract class
│   ├── NonInclusiveInterface.java  # Original interface
│   └── InclusiveInterface.java     # Target replacement interface
└── test/
    ├── java/
    │   ├── RefactorTest.java        # Comprehensive test suite
    │   └── TestRunner.java          # Standalone analysis tool
    └── resources/
        ├── RefactorMeExpected.java          # Expected output (what should happen)
        ├── RefactorMeBug.java               # Actual buggy output (what happens)
        ├── RefactorMeExpectedWithoutStyles.java # AST toString() output (proves logic works)
        └── MethodTestCaseExpected.java      # Expected output for method test case

```

## Test Cases Overview

### 1. `testRefactorNonInclusive()` - Original Bug Demo
Demonstrates the core bug with field declarations not being preserved properly.

### 2. `testMethodSignatureRefactoring()` - Method Signatures
Tests method return types, parameters, and local variables:
```java
// Should be refactored but may not be preserved:
public NonInclusiveClz getObject() { ... }           → public InclusiveClz getObject() { ... }
public void processObject(NonInclusiveClz obj) { ... } → public void processObject(InclusiveClz obj) { ... }
NonInclusiveClz local1 = new NonInclusiveClz();      → InclusiveClz local1 = new InclusiveClz();
```

### 3. `testStaticAndComplexScenarios()` - Advanced Features
Tests static members, generic bounds, wildcards, and functional interfaces:
```java
// Complex scenarios that should be refactored:
public static NonInclusiveClz STATIC_FIELD = ...;                    → InclusiveClz
public <T extends NonInclusiveClz> T getBounded(T input) { ... }     → extends InclusiveClz
Function<NonInclusiveClz, String> getConverter() { ... }             → Function<InclusiveClz, String>
```

### 4. `testAnnotationsAndAdvancedFeatures()` - Language Features
Tests annotations, constructors, synchronized methods, and enums:
```java
// Various language constructs:
@Deprecated private NonInclusiveClz deprecatedField;           → InclusiveClz
public AnnotationTestCase(NonInclusiveClz initialValue) { ... } → InclusiveClz
final NonInclusiveClz finalVar = new NonInclusiveClz();       → InclusiveClz
```

### 5. `testComprehensiveBugAnalysis()` - Full Analysis
Runs all test files and provides detailed statistics about which types of refactoring work vs. fail.

## Expected vs Actual Behavior

### Expected Behavior
After refactoring, ALL type references should be updated consistently:
```java
InclusiveClz _nonInclusivefield;                    // ✅ Should be changed
List<InclusiveClz> _nonInclusiveClzList;           // ✅ Should be changed  
Map<String, InclusiveClz> _nonInclusiveMap;        // ✅ Should be changed
public InclusiveClz getObject() { ... }            // ✅ Should be changed
public void processObject(InclusiveClz obj) { ... } // ✅ Should be changed
```

### Actual Behavior  
Variable declarations and some method signatures retain the old type names despite AST changes:
```java
NonInclusiveClz _nonInclusivefield;                // ❌ Not preserved by LexicalPreservingPrinter
List<NonInclusiveClz> _nonInclusiveClzList;        // ❌ Not preserved by LexicalPreservingPrinter
Map<String, NonInclusiveClz> _nonInclusiveMap;     // ❌ Not preserved by LexicalPreservingPrinter
```

While inheritance and instantiation work correctly:
```java
public class RefactorMe extends InclusiveAbstract implements InclusiveInterface {
  // ...
  _nonInclusivefield = new InclusiveClz();          // ✅ Correctly preserved
}
```

## Technologies Used

- **Java 21**: Core programming language
- **JavaParser 3.24.2**: AST parsing and manipulation library
- **JUnit 5**: Testing framework  
- **Gradle 8.5**: Build system

## How to Run the Tests

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test Cases
```bash
# Original bug demonstration
./gradlew :test --tests "RefactorTest.testRefactorNonInclusive"

# Method signature testing
./gradlew :test --tests "RefactorTest.testMethodSignatureRefactoring"

# Comprehensive analysis
./gradlew :test --tests "RefactorTest.testComprehensiveBugAnalysis"
```

### Run Standalone Analysis Tool
```bash
./gradlew runAnalysis
```

## Test Results Analysis

The comprehensive test suite reveals:

1. **AST Logic is Correct**: `CompilationUnit.toString()` shows the refactoring logic works properly across all scenarios
2. **Inconsistent Preservation**: `LexicalPreservingPrinter.print()` fails to preserve certain types of changes
3. **Pattern Recognition**: The bug primarily affects variable declarations, field types, and some method signatures
4. **Scope of Impact**: Affects basic fields, generic types, method parameters, local variables, and static fields

## Bug Impact Categories

| Java Feature | AST Change Works | Lexical Preservation Works | Status |
|--------------|------------------|----------------------------|---------|
| Class inheritance | ✅ | ✅ | Working |
| Interface implementation | ✅ | ✅ | Working |
| Object instantiation | ✅ | ✅ | Working |
| Field declarations | ✅ | ❌ | **BROKEN** |
| Method parameters | ✅ | ❌ | **BROKEN** |
| Local variables | ✅ | ❌ | **BROKEN** |
| Static fields | ✅ | ❌ | **BROKEN** |
| Generic type parameters | ✅ | ❌ | **BROKEN** |

This project serves as a comprehensive, reproducible test suite for reporting and fixing the JavaParser LexicalPreservingPrinter bug with variable declaration type preservation across multiple Java language features.
