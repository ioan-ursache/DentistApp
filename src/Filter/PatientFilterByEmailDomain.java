package Filter;

import Domain.Patient;

public class PatientFilterByEmailDomain implements  AbstractFilter<Patient> {
    private final String emailDomain;
    public PatientFilterByEmailDomain(String emailDomain) {
        this.emailDomain = emailDomain;
    }

    @Override
    public boolean accept(Patient entity) {
        return entity.getEmail().toLowerCase().endsWith(emailDomain.toLowerCase());
    }
}
