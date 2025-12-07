package Repository.PatientRepositories;

import Domain.Patient;
import Repository.MemoryRepository;

public class PatientRepository extends MemoryRepository<String, Patient> {
    public PatientRepository() {
        //preload
        add("P2025_0001", new Patient("P2025_0001", "Alice Johnson", "alice@mail.com", "0729454567"));
        add("P2025_0002", new Patient("P2025_0002", "Bob Carter", "bob@gmail.com", "0732565678"));
        add("P2025_0003", new Patient("P2025_0003", "Clara Dune", "clara@yahoo.com", "0729567947"));
        add("P2025_0004", new Patient("P2025_0004", "David York", "david@mail.com", "0723678950"));
        add("P2025_0005", new Patient("P2025_0005", "Eve Johnson", "eve@gmail.com", "0720708111"));

    }
}
