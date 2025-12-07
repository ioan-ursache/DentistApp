package Domain;

// Imported Serializable directly into Patients/Appointments.
// Logic: Not all Identifiable may need to be serialised inside the program.
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "appointment")
@XmlAccessorType(XmlAccessType.FIELD)
public class Appointment implements Identifiable<String>, Serializable {
    @XmlElement
    private String id;
    @XmlElement
    private String patientID;
    @XmlElement
    private String datetime;

    public Appointment() {
        this.id = "";
        this.patientID = "";
        this.datetime = "";
    }

    public Appointment(String aID, String pID, String datetime) {
        // Datetime will take the format: "DD-MM-YYYY HH:MM" by convention
        // IDs have been chosen as Strings
        if(aID == null) throw new IllegalArgumentException("Appointment ID cannot be null or empty");
        this.id = aID;
        this.patientID = pID;
        this.datetime = datetime;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public void setID(String aID) {
        this.id = aID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }




    @Override
    public String toString() {
        return "Appointment(ID:"  + id +
                ", PID:" + patientID + ", date:" +
                datetime + ")";
    }
}

