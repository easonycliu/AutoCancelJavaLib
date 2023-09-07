package autocancel.utils.resource;

public class JVMHeapResource extends MemoryResource {
    
    public JVMHeapResource() {
        super(ResourceName.JVMHEAP);
    }

    @Override
    public Double getSlowdown() {
        return 0.0;
    }
}
