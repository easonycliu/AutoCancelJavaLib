package autocancel.core.utils;

public class CancellableID {
    
    private int ID;

    @Override
    public boolean equals(Object o) {
        return this.ID == ((CancellableID) o).ID;
    }

    @Override
    public int hashCode() {
        return ID;
    }
}
