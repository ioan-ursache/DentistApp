package Domain;

import Domain.Patient;
import org.junit.Test;
import org.junit.Assert;

public class PatientTest {

    @Test
    public void testPatientConstructorAndGetters() {
        Patient p = new Patient("1", "John Doe", "john@example.com", "1234567");
        Assert.assertEquals("1", p.getID());
        Assert.assertEquals("John Doe", p.getName());
        Assert.assertEquals("john@example.com", p.getEmail());
        Assert.assertEquals("1234567", p.getNumber());
    }

    @Test
    public void testSetters() {
        Patient p = new Patient("2", "Jane Smith", "jane@old.com", "7654321");

        p.setID("20");
        Assert.assertEquals("20", p.getID());

        p.setName("Jane A. Smith");
        Assert.assertEquals("Jane A. Smith", p.getName());

        p.setEmail("jane@new.com");
        Assert.assertEquals("jane@new.com", p.getEmail());

        p.setNumber("0000000");
        Assert.assertEquals("0000000", p.getNumber());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorThrowsExceptionOnNullID() {
        new Patient(null, "Name", "Email", "Phone");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorThrowsExceptionOnEmptyID() {
        new Patient("", "Name", "Email", "Phone");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetIDThrowsExceptionOnNullID() {
        Patient p = new Patient("1", "N", "E", "P");
        p.setID(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetIDThrowsExceptionOnEmptyID() {
        Patient p = new Patient("1", "N", "E", "P");
        p.setID("");
    }

    @Test
    public void testEqualsAndHashCode() {
        Patient p1 = new Patient("A", "Test", "a@a.com", "111");
        Patient p2 = new Patient("A", "Test", "a@a.com", "111");
        Patient p3 = new Patient("B", "Test", "a@a.com", "111");

        // Equals
        Assert.assertEquals(p1, p2); // Should be equal
        Assert.assertNotEquals(p1, p3); // Should not be equal (different ID)
        Assert.assertNotEquals(p1, null); // Not equal to null
        Assert.assertNotEquals(p1, new Object()); // Not equal to different class

        // HashCode
        Assert.assertEquals(p1.hashCode(), p2.hashCode());
        // Assert.assertNotEquals is not reliable for hashcodes, but checking equality works.
    }

    @Test
    public void testToString() {
        Patient p = new Patient("P1", "Mark", "m@c.com", "111222");
        String expected = "Patient (P1 - name: Mark - email: m@c.com - number: 111222)";
        Assert.assertEquals(expected, p.toString());
    }
}