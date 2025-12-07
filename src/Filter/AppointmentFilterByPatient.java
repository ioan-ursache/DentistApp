package Filter;

import Domain.Appointment;

public class AppointmentFilterByPatient implements AbstractFilter<Appointment> {
    String patientId;

    public AppointmentFilterByPatient(String patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean accept(Appointment entity) {
        return entity.getPatientID().equals(patientId);
    }

}
