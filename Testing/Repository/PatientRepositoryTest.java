package Repository;

import Domain.Patient;
import Filter.PatientFilterByName;
import Filter.PatientFilterByEmailDomain;
import Repository.PatientRepositories.PatientBinaryFileRepository;
import Repository.PatientRepositories.PatientTextFileRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import java.util.Collection;
import java.util.Iterator;

public class PatientRepositoryTest {
    private MemoryRepository<String, Patient> repo;
    private Patient p1, p2, p3;
    private PatientBinaryFileRepository binaryRepo;
    private PatientTextFileRepository textRepo;

    @Before
    public void setUp() {
        repo = new MemoryRepository<>();
        p1 = new Patient("1", "Alice Johnson", "alice@mail.com", "111");
        p2 = new Patient("2", "Bob Carter", "bob@gmail.com", "222");
        p3 = new Patient("3", "Clara Dune", "clara@mail.com", "333");

        repo.add(p1.getID(), p1);
        repo.add(p2.getID(), p2);
        repo.add(p3.getID(), p3);
    }

    @Test
    public void testAddSuccess() {
        Patient p4 = new Patient("4", "David", "d@test.com", "444");
        repo.add(p4.getID(), p4);
        Assert.assertEquals(4, ((Collection<?>) repo.getAll()).size());
        Assert.assertEquals(p4, repo.findByID("4"));
    }

    @Test(expected = RepositoryException.class)
    public void testAddThrowsExceptionOnDuplicateID() {
        repo.add(p1.getID(), new Patient("1", "Duplicate", "dup@mail.com", "999"));
    }

    @Test
    public void testModifySuccess() {
        Patient p_modified = new Patient("2", "Robert Carter", "rob@gmail.com", "888");
        repo.modify(p_modified.getID(), p_modified);
        Patient retrieved = repo.findByID("2");
        Assert.assertEquals("Robert Carter", retrieved.getName());
        Assert.assertEquals("rob@gmail.com", retrieved.getEmail());
    }

    @Test(expected = RepositoryException.class)
    public void testModifyThrowsExceptionOnNonExistingID() {
        Patient p_non_existent = new Patient("99", "Ghost", "g@a.com", "000");
        repo.modify(p_non_existent.getID(), p_non_existent);
    }

    @Test
    public void testDeleteSuccess() {
        repo.delete("1");
        Assert.assertEquals(2, ((Collection<?>) repo.getAll()).size());
        Assert.assertNull(repo.findByID("1"));
    }

    @Test(expected = RepositoryException.class)
    public void testDeleteThrowsExceptionOnNonExistingID() {
        repo.delete("99");
    }

    @Test
    public void testFindByID() {
        Assert.assertEquals(p2, repo.findByID("2"));
        Assert.assertNull(repo.findByID("99"));
    }

    @Test
    public void testGetAll() {
        Assert.assertEquals(3, ((Collection<?>) repo.getAll()).size());
    }

    @Test
    public void testFilterByName() {
        // Must use Patient as T must implement Serializable for FilteredRepository
        FilteredRepository<String, Patient> filteredRepo = new FilteredRepository<>(repo);
        filteredRepo.setFilter(new PatientFilterByName("Alice"));

        Iterable<Patient> results = filteredRepo.getAll();
        Iterator<Patient> it = results.iterator();

        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(p1, it.next());
        Assert.assertFalse(it.hasNext());

        filteredRepo.setFilter(new PatientFilterByName("ohNso")); // Case insensitive filter
        results = filteredRepo.getAll();
        Assert.assertEquals(2, ((Collection<?>) results).size()); // Alice and Johnson (from Alice Johnson)
    }

    @Test
    public void testFilterByEmailDomain() {
        FilteredRepository<String, Patient> filteredRepo = new FilteredRepository<>(repo);
        filteredRepo.setFilter(new PatientFilterByEmailDomain("@mail.com"));

        Iterable<Patient> results = filteredRepo.getAll();
        Assert.assertEquals(2, ((Collection<?>) results).size()); // Alice and Clara

        filteredRepo.setFilter(new PatientFilterByEmailDomain("gmail.com"));
        results = filteredRepo.getAll();
        Assert.assertEquals(1, ((Collection<?>) results).size()); // Bob
        Assert.assertEquals(p2, results.iterator().next());
    }

    // additional tests for file repositories
    @Test
    public void testWriteToFile() {

    }

    @Test
    public void testReadFromFile() {

    }
}