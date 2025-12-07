package Domain;

// Identifiable class to implement basic ID operations
public interface Identifiable<ID> {
    ID getID();
    void setID(ID id);
}
