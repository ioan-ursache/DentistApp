package Service;

import Domain.Appointment;
import Repository.IRepository;
import Repository.RepositoryException;
import Repository.FilteredRepository;
import Filter.*;

import Validation.IValidator;
import Validation.ValidationException;

public class AppointmentService {
    private final IRepository<String, Appointment> repo;
    private final IValidator<Appointment> validator;

    public AppointmentService(IRepository<String, Appointment> repo, IValidator<Appointment> validator) {
        this.repo = repo;
        this.validator = validator;
    }

    public void addAppointment(String id, String patientId, String datetime) throws RepositoryException, ValidationException {
        try {
            Appointment appointment = new Appointment(id, patientId, datetime);
            validator.validate(appointment); // Validation
            repo.add(id, appointment);
        } catch  (RepositoryException | IllegalArgumentException e) {
            throw e;
        }
    }

    public void modifyAppointment(String id, String patientId, String datetime) throws RepositoryException, ValidationException {
        try {
            Appointment appointment = new Appointment(id, patientId, datetime);
            validator.validate(appointment); // Validation
            repo.modify(id, appointment);
        } catch  (RepositoryException | IllegalArgumentException e) {
            throw e;
        }
    }

    public void deleteAppointment(String Id) throws RepositoryException {
        try {
            repo.delete(Id);
        } catch  (RepositoryException e) {
            throw e;
        }
    }

    public Iterable<Appointment> getAllAppointments() {
        return repo.getAll();
    }

    public Iterable<Appointment> filterByDate(String date) {
        FilteredRepository<String, Appointment> filtered = new FilteredRepository<>(repo);
        filtered.setFilter(new AppointmentFilterByDate(date));
        return filtered.getAll();
    }

    public Iterable<Appointment> filterByPatient(String patientId) {
        FilteredRepository<String, Appointment> filtered = new FilteredRepository<>(repo);
        filtered.setFilter(new AppointmentFilterByPatient(patientId));
        return filtered.getAll();
    }
}
