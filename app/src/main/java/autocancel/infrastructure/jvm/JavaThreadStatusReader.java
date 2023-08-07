package autocancel.infrastructure.jvm;

import autocancel.infrastructure.AbstractInfrastructure;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.JavaThreadID;
import autocancel.infrastructure.ResourceBatch;
import autocancel.utils.id.ID;

import java.util.HashMap;
import java.util.Map;

public class JavaThreadStatusReader extends AbstractInfrastructure {

    public JavaThreadStatusReader() {
        super();
    }

    @Override
    protected void updateResource(ID id, Integer version) {

    }

}