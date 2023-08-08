package autocancel.infrastructure.jvm;

import autocancel.infrastructure.ResourceReader;
import autocancel.utils.id.ID;

public class JavaMemoryReader extends ResourceReader {
    @Override
    public Double readResource(ID id, Integer version) {
        return 0.0;
    }
}
