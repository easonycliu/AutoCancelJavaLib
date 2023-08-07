package autocancel.infrastructure;

import autocancel.utils.id.ID;
import autocancel.utils.Resource.ResourceType;

public interface ResourceReader {
    public Double readResource(ID id, ResourceType type);
}
