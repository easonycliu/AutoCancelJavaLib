package autocancel.utils.resource;

import autocancel.utils.logger.Logger;

public class EvictableMemoryResource extends MemoryResource {

    public EvictableMemoryResource(Boolean global) {
        super(global);
    }

    public EvictableMemoryResource(ResourceName name, Boolean global) {
        super(name, global);
    }

    @Override
    public Double getSlowdown() {
        Double slowdown = 0.0;
        if (!this.global) {
            // TODO: implement it
        }
        else {
            Logger.systemWarn("Global resource shouldn't use get slowdown, use getContionLevel instead");
        }
        return slowdown;
    }

    @Override
    public Double getContentionLevel() {
        Double contentionLevel = 0.0;
        if (this.global) {
            // TODO: implement it
        }
        else {
            Logger.systemWarn("Only global resource can call getContionLevel");
        }
        return contentionLevel;
    }

}
