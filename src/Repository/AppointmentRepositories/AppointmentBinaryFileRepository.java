package Repository.AppointmentRepositories;

import Domain.Appointment;
import Repository.BinaryFileRepository;

public class AppointmentBinaryFileRepository extends BinaryFileRepository<String, Appointment> {
    public AppointmentBinaryFileRepository(String fileName) {
        super(fileName);
        // If file is empty or missing, preload initial data
//        if (elements.isEmpty()) {
//            add("A2025_0001", new Appointment("A2025_0001", "P2025_0001", "01-11-2025 10:00"));
//            add("A2025_0002", new Appointment("A2025_0002", "P2025_0002", "01-11-2025 11:30"));
//            add("A2025_0003", new Appointment("A2025_0003", "P2025_0002", "02-11-2025 09:00"));
//            add("A2025_0004", new Appointment("A2025_0004", "P2025_0003", "02-11-2025 10:30"));
//            add("A2025_0005", new Appointment("A2025_0005", "P2025_0004", "03-11-2025 12:00"));
//            // writeToFile is called automatically by add()
//        }
        readFromFile();
    }
}