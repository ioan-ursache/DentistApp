package Repository;

import Domain.Identifiable;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

// Abstract Binary File Repository
public abstract class BinaryFileRepository<ID, T extends Identifiable<ID> & Serializable> extends FileRepository<ID, T> {

    public BinaryFileRepository(String fileName) {
        super(fileName);
    }

    @Override
    protected void readFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            // Read the entire map back
            elements = (Map<ID, T>)  ois.readObject();
        } catch (FileNotFoundException | EOFException e) {
            // File doesn't exist or is empty - initialize with empty map
            elements = new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RepositoryException("Error reading binary file: " + e.getMessage());
        }
    }

    @Override
    protected void writeToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            // Write the entire map
            oos.writeObject(elements);
        } catch (IOException e) {
            throw new RepositoryException("Error writing binary file: " + e.getMessage());
        }
    }
}