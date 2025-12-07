package Repository.PatientRepositories;

import Domain.Patient;
import Repository.XMLRepository;

public class PatientXMLRepository extends XMLRepository<String, Patient> {

    private static final String DEFAULT_XML_FILE = "data/patients.xml";

    public PatientXMLRepository() {
        super(DEFAULT_XML_FILE);
        readFromFile();
    }

    public PatientXMLRepository(String xmlFilePath) {
        super(xmlFilePath);
    }

    @Override
    protected Class<Patient> getEntityClass() {
        return Patient.class;
    }
}
