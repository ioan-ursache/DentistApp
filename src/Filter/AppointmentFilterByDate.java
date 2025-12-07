package Filter;

import Domain.Appointment;

public class AppointmentFilterByDate implements AbstractFilter<Appointment> {
    String AppointmentDate;
    public AppointmentFilterByDate(String AppointmentDate) {
        this.AppointmentDate = AppointmentDate;
    }

    @Override
    public boolean accept(Appointment entity) {
        return entity.getDatetime().startsWith(AppointmentDate);
    }
}
