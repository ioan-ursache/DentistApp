package Validation;

import Domain.Patient;

public class PatientValidator implements IValidator<Patient> {
    @Override
    public void validate(Patient patient) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        if (patient.getID() == null || patient.getID().isEmpty()) {
            errors.append("Patient ID cannot be null or empty.\n");
        }
        if (patient.getName() == null || patient.getName().trim().isEmpty()) {
            errors.append("Patient name cannot be empty.\n");
        }
        if (patient.getEmail() == null || !patient.getEmail().contains("@")) {
            errors.append("Patient email must contain an '@' symbol.\n");
        }
        if (patient.getNumber() == null || patient.getNumber().length() < 7) {
            errors.append("Patient phone number is too short.\n");
        }

        if (errors.length() > 0) {
            throw new ValidationException(errors.toString());
        }
    }

}
