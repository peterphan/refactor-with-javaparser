# JavaParser LexicalPreservingPrinter Bug Demonstration

This project demonstrates a bug in JavaParser's `LexicalPreservingPrinter` where changes to variable declaration types are not properly preserved during code refactoring operations.

## What This Code Does

This is a minimal reproduction case that showcases an automated refactoring scenario where:

1. **Source Classes**: The project contains pairs of classes with "NonInclusive" and "Inclusive" naming conventions:
   - `NonInclusiveClz` → `InclusiveClz`
   - `NonInclusiveAbstract` → `InclusiveAbstract` 
   - `NonInclusiveInterface` → `InclusiveInterface`

2. **Refactoring Target**: The main class `RefactorMe.java` uses the "NonInclusive" types in various contexts:
   - Class inheritance (`extends NonInclusiveAbstract`)
   - Interface implementation (`implements NonInclusiveInterface`)
   - Field declarations (`NonInclusiveClz _nonInclusivefield`)
   - Generic type parameters (`List<NonInclusiveClz>`, `Map<String, NonInclusiveClz>`)
   - Object instantiation (`new NonInclusiveClz()`)

3. **Automated Refactoring**: The test code uses JavaParser to automatically replace all "NonInclusive" type references with their "Inclusive" counterparts while attempting to preserve the original code formatting and style.

## The Bug Being Demonstrated

The JavaParser `LexicalPreservingPrinter` has inconsistent behavior:

- ✅ **Works correctly** for: class inheritance, interface implementation, and object instantiation
- ❌ **Fails to preserve changes** for: variable declarations and field types

## Project Structure

```
src/
├── main/java/a/b/c/
│   ├── RefactorMe.java              # Source file to be refactored
│   ├── NonInclusiveClz.java         # Original class
│   ├── InclusiveClz.java           # Target replacement class
│   ├── NonInclusiveAbstract.java   # Original abstract class
│   ├── InclusiveAbstract.java      # Target replacement abstract class
│   ├── NonInclusiveInterface.java  # Original interface
│   └── InclusiveInterface.java     # Target replacement interface
└── test/
    ├── java/
    │   └── RefactorTest.java        # Test demonstrating the bug
    └── resources/
        ├── RefactorMeExpected.java          # Expected output (what should happen)
        ├── RefactorMeBug.java               # Actual buggy output (what happens)
        └── RefactorMeExpectedWithoutStyles.java # AST toString() output (proves logic works)

```

## Expected vs Actual Behavior

### Expected Behavior
After refactoring, variable declarations should be updated to use the new type names:
```java
InclusiveClz _nonInclusivefield;                    // ✅ Should be changed
List<InclusiveClz> _nonInclusiveClzList;           // ✅ Should be changed  
Map<String, InclusiveClz> _nonInclusiveMap;        // ✅ Should be changed
```

### Actual Behavior  
Variable declarations retain the old type names despite AST changes:
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

- **Java**: Core programming language
- **JavaParser 3.24.2**: AST parsing and manipulation library
- **JUnit 5**: Testing framework  
- **Gradle**: Build system

## How to Run the Test

Execute the failing test to see the bug in action:

```bash
./gradlew :test --tests "RefactorTest.testRefactorNonInclusive"
```

## Test Output

The test demonstrates three key points:

1. **AST Logic is Correct**: `lcu.toString()` shows the refactoring logic works properly
2. **Bug Reproduction**: `LexicalPreservingPrinter.print(lcu)` produces incorrect output for variable declarations  
3. **Assertion Failure**: The test fails when comparing expected vs actual output

```
> Task :test FAILED

RefactorTest > testRefactorNonInclusive() FAILED
    org.opentest4j.AssertionFailedError at RefactorTest.java:50

1 test completed, 1 failed
```

This project serves as a minimal, reproducible test case for reporting and fixing the JavaParser LexicalPreservingPrinter bug with variable declaration type preservation.
