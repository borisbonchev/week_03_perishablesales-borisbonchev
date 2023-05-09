package ps;

/**
 * Line printer. Each string is printed on a new line.
 *
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public interface Printer {

    /**
     * Print a line.
     * @param line to print
     */
    void println( String line );

}
