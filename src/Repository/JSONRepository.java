package Repository;

import Domain.Identifiable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract JSON repository using Jackson for serialization.
 * Entities are stored in JSON format with automatic serialization.
 * Extends FileRepository to follow the common file-based pattern.
 *
 * @param <ID> the type of the entity ID
 * @param <T>  the type of the entity
 */
public abstract class JSONRepository<ID, T extends Identifiable<ID>> extends FileRepository<ID, T> {

    private final Class<T> entityClass;
    private final ObjectMapper mapper = new ObjectMapper();

    public JSONRepository(String jsonFilePath, Class<T> entityClass) {
        super(jsonFilePath);
        this.entityClass = entityClass;
    }

    /**
     * Get the entity class for Jackson operations.
     */
    protected Class<T> getEntityClass() {
        return entityClass;
    }

    @Override
    protected void readFromFile() {
        File file = new File(fileName);
        if (!file.exists()) {
            // File doesn't exist yet, start with empty repository
            return;
        }

        try {
            // Create a CollectionType for List<T>
            CollectionType listType = mapper.getTypeFactory()
                    .constructCollectionType(List.class, entityClass);

            List<T> entities = mapper.readValue(file, listType);

            if (entities != null) {
                for (T entity : entities) {
                    super.add(entity.getID(), entity);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
    }

    @Override
    protected void writeToFile() {
        try {
            // Get all entities from in-memory repository
            List<T> allEntities = new ArrayList<>();
            for (T entity : super.getAll()) {
                allEntities.add(entity);
            }

            File file = new File(fileName);
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs(); // Ensure directory exists
            }

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, allEntities);

        } catch (IOException e) {
            System.err.println("Error writing JSON file: " + e.getMessage());
        }
    }
}
