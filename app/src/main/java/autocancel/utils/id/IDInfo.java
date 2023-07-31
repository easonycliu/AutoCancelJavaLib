package autocancel.utils.id;

import autocancel.utils.id.ID;

import java.time.Instant;

public class IDInfo<ObjectID extends ID> {
    
    private Instant timestamp;

    private ObjectID id;

    public IDInfo(Instant timestamp, ObjectID id) {
        this.timestamp = timestamp;
        this.id = id;
    }

    public IDInfo(ObjectID id) {
        this.timestamp = Instant.now();
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("Time: %s, %s", this.timestamp.toString(), id.toString());
    }

    @Override
    public boolean equals(Object o) {
        return this.id.getClass() == ((IDInfo<?>)o).getID().getClass() && 
        this.id.equals(((IDInfo<?>)o).getID());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public ObjectID getID() {
        return this.id;
    }
}
