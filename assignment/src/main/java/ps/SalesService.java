package ps;

/**
 * Service to lookup product and to register sales.
 * 
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public interface SalesService {

    /**
     * Lookup a product by bar code.
     * @param barcode input
     * @return the product if valid bar code
     * @throws UnknownProductException if bar code is not found
     */
    Product lookupProduct( int barcode ) throws UnknownProductException;

    /**
     * Register a salesRecord.
     * @param salesRecord
     */
    void sold( SalesRecord salesRecord );
}
