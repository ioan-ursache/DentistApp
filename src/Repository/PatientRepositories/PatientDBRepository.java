package Repository.PatientRepositories;

import Domain.Patient;
import Repository.DBRepository;
import Repository.RepositoryException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/** Database Patient Repository
 * The following class extends the Database Repository class, performing operations on the Patient Table.
 */
public class PatientDBRepository extends DBRepository<String, Patient> {

    @Override
    public void add(String ID, Patient entity) {
        openConnection();
        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO patients VALUES(?, ?, ?, ?);")) {
            ps.setString(1, entity.getID());
            ps.setString(2, entity.getName());
            ps.setString(3, entity.getEmail());
            ps.setString(4, entity.getNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    @Override
    public void modify(String ID, Patient entity) {
        openConnection();
        try(PreparedStatement ps = conn.prepareStatement("UPDATE patients SET name = ?, email = ?, number = ? WHERE id = ?;")) {
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getEmail());
            ps.setString(3, entity.getNumber());
            ps.setString(4, ID);
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
        try(PreparedStatement ps = conn.prepareStatement("DELETE FROM patients WHERE id = ?;")) {
            ps.setString(1, ID);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection();
        }
    }


    @Override
    public Patient findByID(String IDtoFind) {
        openConnection();
        try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients WHERE ID = ?;")) {
            ps.setString(1, IDtoFind);
            ResultSet rs = ps.executeQuery();
            rs.next();
            String id = rs.getString("id");
            String name = rs.getString("name");
            String email = rs.getString("email");
            String number = rs.getString("number");
            return new Patient(id, name, email, number);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    @Override
    public Iterable<Patient> getAll() {
        ArrayList<Patient> patients = new ArrayList<>();
        openConnection();
        try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients;");
            ResultSet rs = ps.executeQuery())
        {
            while(rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String number = rs.getString("number");
                patients.add(new Patient(id, name, email, number));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection();
        }
        return patients;
    }
}
