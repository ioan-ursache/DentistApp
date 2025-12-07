package Repository.AppointmentRepositories;

import Domain.Appointment;
import Repository.RepositoryException;
import Repository.TextFileRepository;

public class AppointmentTextFileRepository extends TextFileRepository<String, Appointment> {

    public  AppointmentTextFileRepository(String fileName) {
        super(fileName);

//        if(elements.isEmpty()) {
//            add("A2025_0001", new Appointment("A2025_0001", "P2025_0001", "01-11-2025 10:00"));
//            add("A2025_0002", new Appointment("A2025_0002", "P2025_0002", "01-11-2025 11:30"));
//            add("A2025_0003", new Appointment("A2025_0003", "P2025_0002", "02-11-2025 09:00"));
//            add("A2025_0004", new Appointment("A2025_0004", "P2025_0003", "02-11-2025 10:30"));
//            add("A2025_0005", new Appointment("A2025_0005", "P2025_0004", "03-11-2025 12:00"));
//        }
        readFromFile();
    }


    @Override
    protected Appointment stringToEntity(String line) {
        String[] parts = line.split(",");
        if (parts.length != 3) throw  new RepositoryException("Invalid format for Appointment");
        return new  Appointment(parts[0], parts[1], parts[2]);
    }

    @Override
    protected String entityToString(Appointment entity) {
        return entity.getID() + "," + entity.getPatientID() + "," + entity.getDatetime();
    }
}
