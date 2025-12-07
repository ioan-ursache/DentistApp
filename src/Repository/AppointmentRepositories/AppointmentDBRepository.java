package Repository.AppointmentRepositories;

import Domain.Appointment;
import Domain.Patient;
import Repository.DBRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/** Database Appointment Repository
 * The following class extends the Database Repository class, performing operations on the Patient Table.
 */
public class AppointmentDBRepository extends DBRepository<String, Appointment> {

    @Override
    public void add(String ID, Appointment entity){
        openConnection();
        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO appointments VALUES (?, ?, ?);")) {
            ps.setString(1, entity.getID());
            ps.setString(2, entity.getPatientID());
            ps.setString(3, entity.getDatetime());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    @Override
    public void modify(String ID, Appointment entity) {
        openConnection();
        try(PreparedStatement ps = conn.prepareStatement("UPDATE appointments SET patientID = ?, datetime = ? WHERE ID = ?;")) {
            ps.setString(1, entity.getPatientID());
            ps.setString(2, entity.getDatetime());
            ps.setString(3, ID);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    @Override
    public void delete(String ID) {
        openConnection();
        try(PreparedStatement ps = conn.prepareStatement("DELETE FROM appointments WHERE ID = ?;")) {
            ps.setString(1, ID);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    @Override
    public Appointment findByID(String IDtoFind) {
        openConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments WHERE ID = ?;")) {
            ps.setString(1, IDtoFind);
            ResultSet rs = ps.executeQuery();
            rs.next();
            String id = rs.getString("id");
            String patientID = rs.getString("patientID");
            String datetime = rs.getString("datetime");
            return new Appointment(id, patientID, datetime);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    @Override
    public Iterable<Appointment> getAll() {
        ArrayList<Appointment> appointments = new ArrayList<>();
        openConnection();
        try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments;");
            ResultSet rs = ps.executeQuery())
        {
            while(rs.next()) {
                String id = rs.getString("id");
                String patientID = rs.getString("patientID");
                String datetime = rs.getString("datetime");
                appointments.add(new Appointment(id, patientID, datetime));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection();
        }
        return appointments;
    }
}
