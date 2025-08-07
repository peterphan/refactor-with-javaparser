package a.b.c;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MethodTestCase {

    // Method return types
    public NonInclusiveClz getObject() {
        return new NonInclusiveClz();
    }

    public List<NonInclusiveClz> getList() {
        return null;
    }

    public Map<String, NonInclusiveClz> getMap() {
        return null;
    }

    public Optional<NonInclusiveClz> getOptional() {
        return Optional.empty();
    }

    // Method parameters
    public void processObject(NonInclusiveClz obj) {
        // Implementation
    }

    public void processGeneric(List<NonInclusiveClz> items) {
        // Implementation
    }

    public void processMultiple(NonInclusiveClz obj1, List<NonInclusiveClz> obj2, Map<String, NonInclusiveClz> obj3) {
        // Implementation
    }

    // Nested generics
    public List<List<NonInclusiveClz>> getNestedList() {
        return null;
    }

    public Map<String, List<NonInclusiveClz>> getComplexMap() {
        return null;
    }

    // Array types
    public NonInclusiveClz[] getArray() {
        return new NonInclusiveClz[0];
    }

    public void processArray(NonInclusiveClz[] items) {
        // Implementation
    }

    // Local variables in methods
    public void methodWithLocals() {
        NonInclusiveClz local1 = new NonInclusiveClz();
        List<NonInclusiveClz> local2 = null;
        NonInclusiveClz[] local3 = new NonInclusiveClz[5];
    }
}