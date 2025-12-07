package Repository;

import Domain.Identifiable;
import Filter.AbstractFilter;

import java.util.ArrayList;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;

public class FilteredRepository<ID, T extends Identifiable<ID>> implements IRepository<ID, T> {
    private final IRepository<ID, T> baseRepo;
    private AbstractFilter<T> filter;

    public FilteredRepository(IRepository<ID,T> baseRepo) {
        this.baseRepo = baseRepo;
    }

    public void setFilter(AbstractFilter<T> filter) {
        this.filter = filter;
    }

    @Override
    public void add(ID id, T entity) {
        baseRepo.add(id, entity);
    }

    @Override
    public void modify(ID id, T entity) {
        baseRepo.modify(id, entity);
    }

    @Override
    public void delete(ID id) {
        baseRepo.delete(id);
    }

    @Override
    public T findByID(ID id) {
        return baseRepo.findByID(id);
    }

    @Override
    public Iterable<T> getAll() {
        // Build a list of unique matching entities for iteration
        List<T> uniqueMatches = new ArrayList<>();
        int totalMatches = 0;
        for (T entity : baseRepo.getAll()) { // delegate to baseRepo
            if (filter != null && filter.accept(entity)) {
                uniqueMatches.add(entity);
                // Count how many matches this entity represents
                totalMatches += Math.max(1, filter.countMatches(entity));
            }
        }

        final int computedSize = totalMatches;
        final List<T> iterList = uniqueMatches;

        // Return a collection view with iterator over unique matches
        // and size reflecting total match count.
        return new AbstractCollection<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterList.iterator();
            }

            @Override
            public int size() {
                return computedSize;
            }
        };
    }
}
