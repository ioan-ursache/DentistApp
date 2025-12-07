package Repository;

// Basic Repository Interface, using generic types ID for ID and T for Identifiable entities
public interface IRepository<ID, T> {
    void add(ID id, T entity) throws RepositoryException;
    void modify(ID id, T entity) throws RepositoryException;
    void delete(ID IDtoFind) throws RepositoryException;

    T findByID(ID IDtoFind);
    Iterable<T> getAll();
}
