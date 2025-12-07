package Repository;

import Domain.Identifiable;

import java.io.*;
import java.util.Map;

public abstract class FileRepository<ID, T extends Identifiable<ID>> extends MemoryRepository<ID, T> {
    protected String fileName;

    public FileRepository(String fileName) {
        this.fileName = fileName;
        //readFromFile();
    }

    // Abstract methods to be implemented by the Text/Binary Repositories
    protected abstract void readFromFile();
    protected abstract void writeToFile();

    // Overridden methods ensure file data integrity
    @Override
    public void add(ID id, T entity) {
        super.add(id, entity);
        writeToFile();
    }

    @Override
    public void modify(ID id, T entity) {
        super.modify(id, entity);
        writeToFile();
    }

    @Override
    public void delete(ID id) {
        super.delete(id);
        writeToFile();
    }

}
