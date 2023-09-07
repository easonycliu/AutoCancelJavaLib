package autocancel.utils.resource;

public class EvictableMemoryResource extends MemoryResource {

    public EvictableMemoryResource(ResourceName name) {
        super(name);
    }

    @Override
    public Double getSlowdown() {
        return 0.0;
    }

}
