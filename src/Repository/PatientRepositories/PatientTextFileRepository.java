package Repository.PatientRepositories;

import Domain.Patient;
import Repository.TextFileRepository;

public class PatientTextFileRepository extends TextFileRepository<String, Patient> {
    public PatientTextFileRepository(String fileName) {
        super(fileName);
        // If file is empty or missing, preload initial data
//        if (elements.isEmpty()) {
//            add("P2025_0001", new Patient("P2025_0001", "Alice Johnson", "alice@mail.com", "0729454567"));
//            add("P2025_0002", new Patient("P2025_0002", "Bob Carter", "bob@gmail.com", "0732565678"));
//            add("P2025_0003", new Patient("P2025_0003", "Clara Dune", "clara@yahoo.com", "0729567947"));
//            add("P2025_0004", new Patient("P2025_0004", "David York", "david@mail.com", "0723678950"));
//            add("P2025_0005", new Patient("P2025_0005", "Eve Johnson", "eve@gmail.com", "0720708111"));
//            // writeToFile is called automatically by add()
//        }
        readFromFile();
    }

    @Override
    protected Patient stringToEntity(String line) {
        String[] parts = line.split(",");
        if (parts.length != 4) throw new IllegalArgumentException("Invalid Patient file format.");
        return new Patient(parts[0], parts[1], parts[2], parts[3]);
    }

    @Override
    protected String entityToString(Patient entity) {
        return entity.getID() + "," + entity.getName() + "," + entity.getEmail() + "," + entity.getNumber();
    }
}