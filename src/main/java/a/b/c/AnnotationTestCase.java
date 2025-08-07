package a.b.c;

import java.util.List;

// Test with annotations
public class AnnotationTestCase {

    // Field with annotations
    @Deprecated
    private NonInclusiveClz deprecatedField;

    // Constructor with parameters
    public AnnotationTestCase(NonInclusiveClz initialValue) {
        this.deprecatedField = initialValue;
    }

    // Multiple constructors
    public AnnotationTestCase() {
        this(new NonInclusiveClz());
    }

    public AnnotationTestCase(List<NonInclusiveClz> items) {
        this.deprecatedField = items.isEmpty() ? null : items.get(0);
    }

    // Annotated methods
    @Override
    public String toString() {
        NonInclusiveClz temp = this.deprecatedField;
        return temp != null ? temp.toString() : "null";
    }

    @SuppressWarnings("unchecked")
    public <T> T unsafeCast(NonInclusiveClz obj) {
        return (T) obj;
    }

    // Synchronized methods
    public synchronized NonInclusiveClz getSynchronized() {
        return this.deprecatedField;
    }

    public synchronized void setSynchronized(NonInclusiveClz value) {
        this.deprecatedField = value;
    }

    // Final variables
    public void testFinalVariables() {
        final NonInclusiveClz finalVar = new NonInclusiveClz();
        final List<NonInclusiveClz> finalList = java.util.Collections.emptyList();
    }

    // Lambda expressions with the type
    public void testLambdas() {
        java.util.List<NonInclusiveClz> items = java.util.Arrays.asList();
        items.stream()
             .filter(item -> item != null)
             .map(NonInclusiveClz::toString)
             .forEach(System.out::println);
    }

    // Enum with the type
    public enum TestEnum {
        VALUE1(new NonInclusiveClz()),
        VALUE2(new NonInclusiveClz());

        private final NonInclusiveClz associatedValue;

        TestEnum(NonInclusiveClz value) {
            this.associatedValue = value;
        }

        public NonInclusiveClz getValue() {
            return associatedValue;
        }
    }
}