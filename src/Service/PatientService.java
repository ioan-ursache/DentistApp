package Service;

import Domain.Patient;
import Repository.IRepository;
import Repository.RepositoryException;
import Repository.FilteredRepository;
import Filter.*;

// Added validation
import Validation.IValidator;
import Validation.ValidationException;

public class PatientService {
    private final IRepository<String, Patient> repo;
    private final IValidator<Patient> validator;

    public PatientService(IRepository<String, Patient> repo, IValidator<Patient> validator) {
        this.repo = repo;
        this.validator = validator;
    }

    public void addPatient(String id, String name, String email, String phone) throws RepositoryException, ValidationException {
        try {
            Patient patient = new Patient(id, name, email, phone);
            validator.validate(patient); // Validation
            repo.add(patient.getID(), patient);
        } catch (RepositoryException | IllegalArgumentException e) {
            throw e;
        }
    }

    public void modifyPatient(String id, String name, String email, String phone) throws RepositoryException, ValidationException {
        try {
            Patient patient = new Patient(id, name, email, phone);
            validator.validate(patient); // Validation
            repo.modify(patient.getID(), patient);
        }  catch (RepositoryException | IllegalArgumentException e) {
            throw e;
        }
    }

    public void deletePatient(String id) throws RepositoryException {
        try {
            repo.delete(id);
        }  catch (RepositoryException e) {
            throw e;
        }
    }

    public Iterable<Patient> getAllPatients() throws RepositoryException {
        try {
            return repo.getAll();
        }
        catch (RepositoryException e) {
            throw e;
        }
    }

    public Iterable<Patient> filterByName(String keyword) {
        FilteredRepository<String, Patient> filtered = new FilteredRepository<>(repo);
        filtered.setFilter(new PatientFilterByName(keyword));
        return filtered.getAll();
    }

    public Iterable<Patient> filterByEmail(String domain) {
        FilteredRepository<String, Patient> filtered = new FilteredRepository<>(repo);
        filtered.setFilter(new PatientFilterByEmailDomain(domain));
        return filtered.getAll();
    }

}
