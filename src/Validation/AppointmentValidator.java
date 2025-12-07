package Validation;

import Domain.Appointment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AppointmentValidator implements IValidator<Appointment> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public void validate(Appointment appointment) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        if (appointment.getID() == null || appointment.getID().isEmpty()) {
            errors.append("Appointment ID cannot be null or empty.\n");
        }
        if (appointment.getPatientID() == null || appointment.getPatientID().isEmpty()) {
            errors.append("Patient ID for the appointment cannot be null or empty.\n");
        }

        // Basic Date/Time format validation
        // We convert since we implemented datetime on strings.
        try {
            LocalDateTime.parse(appointment.getDatetime(), FORMATTER);
        } catch (DateTimeParseException e) {
            errors.append("Appointment datetime must be in format 'DD-MM-YYYY HH:MM'.\n");
        }

        if (errors.length() > 0) {
            throw new ValidationException(errors.toString());
        }
    }
}
