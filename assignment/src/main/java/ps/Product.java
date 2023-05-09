package ps;

/**
 * Simple Product. Simple entity class with getter methods, setters and
 * toString(). (Could be replaced by Record as of Java 14).
 *
 *
 * @author Pieter van den Hombergh
 */
public class Product {

    private final String shortName;
    private final String description;
    private final int price;
    private final int barcode;
    private final boolean perishable;

    /**
     * Initialize Product.
     *
     * @param shortName of product.
     * @param description
     * @param price (catalogue price in cents)
     * @param barcode
     * @param perisable indicates if product is perishable
     */
    public Product(String shortName, String description, int price, int barcode, boolean perishable) {
        this.shortName = shortName;
        this.description = description;
        this.price = price;
        this.barcode = barcode;
        this.perishable = perishable;
    }

    /**
     * getShortName of product.
     *
     * @return shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * getDescription of product.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * getPrice of product.
     *
     * @return price in catalogue in cents
     */
    public int getPrice() {
        return price;
    }

    /**
     * getBarcodeof product.
     *
     * @return barCode as integer
     */
    public int getBarcode() {
        return barcode;
    }

    /**
     * isPerishable indicates if a product is perishable or not.
     *
     * @return perishable
     */
    public boolean isPerishable() {
        return perishable;
    }

    /**
     * Get product in textual format.
     * Contains shortName, description, catalogue price, bar code and the
     * text 'perishable' in case a product is perishable.
     * @return 
     */
    @Override
    public String toString() {
        return "Product{" + "shortName=" + shortName
                + ", description=" + description
                + ", price=" + price
                + ", barcode=" + barcode
                + (perishable ? " perishable" : "") + '}';
    }
}
