package Repository.AppointmentRepositories;

import Domain.Appointment;
import Repository.XMLRepository;

public class AppointmentXMLRepository extends XMLRepository<String, Appointment> {

    private static final String DEFAULT_XML_FILE = "data/appointments.xml";

    public AppointmentXMLRepository() {
        super(DEFAULT_XML_FILE);
        readFromFile();
    }

    public AppointmentXMLRepository(String xmlFilePath) {
        super(xmlFilePath);
    }

    @Override
    protected Class<Appointment> getEntityClass() {
        return Appointment.class;
    }
}