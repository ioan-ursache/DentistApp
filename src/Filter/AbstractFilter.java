package Filter;

// Abstract Filter Interface, working on T-type generics
public interface AbstractFilter<T> {
    boolean accept(T entity);

    // Default match counter: 1 if accepted, otherwise 0.
    default int countMatches(T entity) {
        return accept(entity) ? 1 : 0;
    }
}
