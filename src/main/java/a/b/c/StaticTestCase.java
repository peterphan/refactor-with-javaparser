package a.b.c;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class StaticTestCase {

    // Static fields
    public static NonInclusiveClz STATIC_FIELD = new NonInclusiveClz();
    private static final NonInclusiveClz CONSTANT = new NonInclusiveClz();

    // Static methods
    public static NonInclusiveClz createInstance() {
        return new NonInclusiveClz();
    }

    public static void processStatic(NonInclusiveClz item) {
        // Implementation
    }

    // Generic with bounds
    public <T extends NonInclusiveClz> T getBounded(T input) {
        return input;
    }

    // Wildcard generics
    public void processWildcard(java.util.List<? extends NonInclusiveClz> items) {
        // Implementation
    }

    public void processWildcardSuper(java.util.List<? super NonInclusiveClz> items) {
        // Implementation
    }

    // Functional interfaces
    public Function<NonInclusiveClz, String> getConverter() {
        return obj -> obj.toString();
    }

    public Supplier<NonInclusiveClz> getSupplier() {
        return NonInclusiveClz::new;
    }

    // CompletableFuture and other complex types
    public CompletableFuture<NonInclusiveClz> getAsync() {
        return CompletableFuture.supplyAsync(NonInclusiveClz::new);
    }

    // Nested classes scenario
    public static class NestedClass {
        NonInclusiveClz nestedField;
        
        public NonInclusiveClz getNestedObject() {
            return nestedField;
        }
    }

    // Exception handling
    public NonInclusiveClz riskyOperation() throws Exception {
        try {
            return new NonInclusiveClz();
        } catch (Exception e) {
            NonInclusiveClz fallback = new NonInclusiveClz();
            return fallback;
        }
    }
}