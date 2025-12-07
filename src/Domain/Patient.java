package Domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
// Imported Serializable directly into Patients/Appointments.
// Logic: Not all Identifiable may need to be serialised inside the program.
import java.io.Serializable;

// Implementation of a Patient entity, described by ID, name, email and phone number.
@XmlRootElement(name = "patient")
@XmlAccessorType(XmlAccessType.FIELD)
public class Patient implements Identifiable<String>, Serializable {
    @XmlElement
    private String id;
    @XmlElement
    private String name;
    @XmlElement
    private String email;
    @XmlElement
    private String phone;

    public Patient() {
        this.id = "";
        this.name = "";
        this.email = "";
        this.phone = "";
    }

    public Patient(String patientID, String patientName, String patientEmail, String patientPhone) {
        // IDs have been chosen as Strings
        if(patientID == null || patientID.isEmpty()) throw new IllegalArgumentException("Patient ID cannot be null or empty");
        this.id = patientID;
        this.name = patientName;
        this.email = patientEmail;
        this.phone = patientPhone;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public void setID(String id) {
        if(id == null || id.isEmpty()) throw new IllegalArgumentException("Patient ID cannot be null or empty");
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String pName) {
        this.name = pName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String pEmail) {
        this.email = pEmail;
    }

    public String getNumber() {
        return phone;
    }

    public void setNumber(String pNumber) {
        this.phone = pNumber;
    }

    @Override
    public String toString() {
        return "Patient (" + this.id +
                " - name: " + this.name +
                " - email: " + this.email +
                " - number: " + this.phone +
                ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, phone);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || getClass() != obj.getClass())
            return false;
        final Patient other = (Patient) obj;
        return this.id.equals(other.id) && this.name.equals(other.name)  && this.email.equals(other.email) && this.phone.equals(other.phone);
    }
}
