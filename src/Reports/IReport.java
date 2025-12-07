package Reports;

/**
 * Generic report interface.
 * INPUT:  type of input parameter (e.g., String for ID/date/name)
 * OUTPUT: type of generated report (we'll use String for formatted text)
 */
public interface IReport<INPUT, OUTPUT> {
    OUTPUT execute(INPUT input);
    String getName();
    String getDescription();
}