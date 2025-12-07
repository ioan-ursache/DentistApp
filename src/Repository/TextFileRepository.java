package Repository;

import Domain.Identifiable;
import Filter.AbstractFilter;
import org.w3c.dom.Text;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public abstract class TextFileRepository<ID, T extends Identifiable<ID>> extends FileRepository<ID, T> {

    public TextFileRepository(String fileName) {
        super(fileName);
    }

    // Conversion methods
    protected abstract T stringToEntity(String string);
    protected abstract String entityToString(T entity);

    @Override
    protected void readFromFile() {
        elements.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                T entity = stringToEntity(line);
                elements.put(entity.getID(), entity);
            }
        } catch (FileNotFoundException ignored) {
            // File might not exist yet
        } catch (IOException e) {
            throw new RepositoryException("Error leading from file" + e.getMessage());
        }
    }

    @Override
    protected void writeToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (T entity : elements.values()) {
                bw.write(entityToString(entity));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RepositoryException("Error writing to file: " + e.getMessage());
        }
    }
}
