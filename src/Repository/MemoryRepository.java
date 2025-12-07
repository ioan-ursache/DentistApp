package Repository;

import Domain.Identifiable;

import java.util.*;

// Memory Repository, implements the IRepository class on a HashMap data structure
public class MemoryRepository<ID, T extends Identifiable<ID>> implements IRepository<ID, T> {

    protected Map<ID, T> elements = new  HashMap<>();

    @Override
    public void add(ID id, T entity) throws RepositoryException {
        // If the ID already exists, addition is not allowed, as otherwise we would accidentally rewrite data.
        if(elements.containsKey(id))
            throw new RepositoryException("Element with id " + id + " already exists");
        elements.put(id, entity);
    }

    @Override
    public void modify(ID id, T entity) throws RepositoryException {
        // If the ID does not yet exist, updates do no apply, as there is no object to modify.
        if(!elements.containsKey(id))
            throw new RepositoryException("Element with id " + id + " does not exist");
        elements.put(id, entity);
    }

    @Override
    public void delete(ID id) throws RepositoryException {
        // If the ID does not yet exist, deletion is not allowed, as there is nothing to delete.
        if(!elements.containsKey(id))
            throw new RepositoryException("Element with id " + id + " does not exist");
        elements.remove(id);
    }

    @Override
    public T findByID(ID id) {
        return elements.get(id);
    }

    @Override
    public Iterable<T> getAll() {
        return elements.values();
    }
}
