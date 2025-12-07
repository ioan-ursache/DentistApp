package Repository;

import Domain.Identifiable;

import java.sql.*;

/**
 * Abstract database repository. Implements methods to manage the URL and the connection to databases that extend it.
 * @param <ID> the type of the ID of the entities stored in the repository
 * @param <T> the type of the entities stored in the repository
 */
public abstract class DBRepository<ID, T extends Identifiable<ID>> implements IRepository<ID, T> {
    /**
     * Assumes that the database has already been created.
     */
    public String JDBC_URL = "jdbc:sqlite:C:\\Users\\ursac\\IdeaProjects\\Assignment4-5\\data\\database.db";
    protected Connection conn = null;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found. Make sure the JAR is on the classpath.", e);
        }
    }

    // Function to manage the connection to the database
    /**
     * Opens a connection to the database
     * @throws RuntimeException
     */
    protected void openConnection() throws RuntimeException {
        try {
            if(conn == null || conn.isClosed())
                conn = DriverManager.getConnection(JDBC_URL);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Closes the connection to the database
     * @throws RuntimeException
     */
    protected void closeConnection() throws RuntimeException {
        try {
            if(conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Functions to add, modify and delete entities from the database.
    @Override
    public void add(ID id, T entity) throws RepositoryException {

    }

    @Override
    public void modify(ID id, T entity) throws RepositoryException, RuntimeException {

    }

    @Override
    public void delete(ID id) throws RepositoryException, RuntimeException {

    }

    // Functions to manage the database selection.
    public void setJDBC_URL(String url) {
        JDBC_URL = url;
    }

    public String getJDBC_URL() {
        return JDBC_URL;
    }
}
