package ps;

/**
 * Minimal UI.
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public interface UI {
    /**
     * Display product details on this UI.
     * @param p product to show
     */
    void displayProduct( Product p);
    
    /**
     * Display a calendar with selectable dates. Each date entry should behave like a button
     * with event data containing the date of that button.
     */
    void displayCalendar();
    
    
    /**
     * Display an error message on this UI.
     * @param message to display
     */
    void displayErrorMessage( String message );
}
