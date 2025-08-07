package a.b.c;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MethodTestCase {

    // Method return types
    public InclusiveClz getObject() {
        return new InclusiveClz();
    }

    public List<InclusiveClz> getList() {
        return null;
    }

    public Map<String, InclusiveClz> getMap() {
        return null;
    }

    public Optional<InclusiveClz> getOptional() {
        return Optional.empty();
    }

    // Method parameters
    public void processObject(InclusiveClz obj) {
        // Implementation
    }

    public void processGeneric(List<InclusiveClz> items) {
        // Implementation
    }

    public void processMultiple(InclusiveClz obj1, List<InclusiveClz> obj2, Map<String, InclusiveClz> obj3) {
        // Implementation
    }

    // Nested generics
    public List<List<InclusiveClz>> getNestedList() {
        return null;
    }

    public Map<String, List<InclusiveClz>> getComplexMap() {
        return null;
    }

    // Array types
    public InclusiveClz[] getArray() {
        return new InclusiveClz[0];
    }

    public void processArray(InclusiveClz[] items) {
        // Implementation
    }

    // Local variables in methods
    public void methodWithLocals() {
        InclusiveClz local1 = new InclusiveClz();
        List<InclusiveClz> local2 = null;
        InclusiveClz[] local3 = new InclusiveClz[5];
    }
}