package Service;

import Domain.Patient;
import Repository.MemoryRepository;
import Repository.RepositoryException;
import Service.PatientService;
import Validation.PatientValidator;
import Validation.ValidationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import java.util.Collection;

public class PatientServiceTest {
    private MemoryRepository<String, Patient> repo;
    private PatientService service;

    @Before
    public void setUp() {
        repo = new MemoryRepository<>();
        PatientValidator validator = new PatientValidator();
        // Use the MemoryRepository for deterministic testing
        service = new PatientService(repo, validator);

        // Initial data setup for testing
        repo.add("1", new Patient("1", "Alice Johnson", "alice@mail.com", "111222333"));
        repo.add("2", new Patient("2", "Bob Carter", "bob@gmail.com", "444555666"));
    }

    // --- Add Tests ---

    @Test
    public void testAddPatient_Success() {
        try {
            service.addPatient("3", "New Patient", "new@mail.com", "0000000");
            Assert.assertNotNull(repo.findByID("3"));
        } catch (Exception e) {
            Assert.fail("Add failed unexpectedly: " + e.getMessage());
        }
    }

    @Test(expected = RepositoryException.class)
    public void testAddPatient_ThrowsRepositoryException_DuplicateID() throws RepositoryException, ValidationException {
        // Attempt to add patient with existing ID "1"
        service.addPatient("1", "Duplicate", "dup@mail.com", "0000000");
    }

    // --- Validation Tests ---

    @Test(expected = ValidationException.class)
    public void testAddPatient_ThrowsValidationException_EmptyName() throws RepositoryException, ValidationException {
        // Name cannot be empty (space is invalid)
        service.addPatient("4", " ", "valid@mail.com", "0000000");
    }

    @Test(expected = ValidationException.class)
    public void testAddPatient_ThrowsValidationException_InvalidEmail() throws RepositoryException, ValidationException {
        // Email must contain '@'
        service.addPatient("4", "Valid Name", "invalid.email.com", "0000000");
    }

    @Test(expected = ValidationException.class)
    public void testAddPatient_ThrowsValidationException_ShortPhone() throws RepositoryException, ValidationException {
        // Phone must be at least 7 characters long
        service.addPatient("4", "Valid Name", "valid@email.com", "123");
    }

    // --- Modify Tests ---

    @Test
    public void testModifyPatient_Success() {
        String newName = "Alice J. Smith";
        try {
            service.modifyPatient("1", newName, "alice@work.com", "9999999");
            Assert.assertEquals(newName, repo.findByID("1").getName());
            Assert.assertEquals("alice@work.com", repo.findByID("1").getEmail());
        } catch (Exception e) {
            Assert.fail("Modify failed unexpectedly: " + e.getMessage());
        }
    }

    @Test(expected = RepositoryException.class)
    public void testModifyPatient_ThrowsRepositoryException_NonExistingID() throws RepositoryException, ValidationException {
        // Attempt to modify patient with ID "99"
        service.modifyPatient("99", "Non Existent", "no@where.com", "0000000");
    }

    @Test(expected = ValidationException.class)
    public void testModifyPatient_ThrowsValidationException() throws RepositoryException, ValidationException {
        // Attempt to modify patient "1" with invalid data (bad email)
        service.modifyPatient("1", "Valid", "bad.email", "0000000");
    }

    // --- Delete Tests ---

    @Test
    public void testDeletePatient_Success() {
        try {
            service.deletePatient("2");
            Assert.assertNull(repo.findByID("2"));
        } catch (Exception e) {
            Assert.fail("Delete failed unexpectedly: " + e.getMessage());
        }
    }

    @Test(expected = RepositoryException.class)
    public void testDeletePatient_ThrowsRepositoryException() {
        service.deletePatient("99");
    }

    // --- Filter Tests ---

    @Test
    public void testFilterByName() {
        Iterable<Patient> results = service.filterByName("JohNso"); // Case insensitive
        int count = 0;
        for (Patient p : results) {
            Assert.assertEquals("Alice Johnson", p.getName());
            count++;
        }
        Assert.assertEquals(1, count);
    }

    @Test
    public void testFilterByEmail() {
        Iterable<Patient> results = service.filterByEmail("gmail.com");
        int count = 0;
        for (Patient p : results) {
            Assert.assertEquals("Bob Carter", p.getName());
            count++;
        }
        Assert.assertEquals(1, count);
    }

    @Test
    public void testGetAllPatients() {
        try {
            Collection<Patient> all = (Collection<Patient>) service.getAllPatients();
            Assert.assertEquals(2, all.size());
        } catch (RepositoryException e) {
            Assert.fail("getAllPatients failed unexpectedly: " + e.getMessage());
        }
    }
}