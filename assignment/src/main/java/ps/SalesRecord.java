package ps;

import java.time.LocalDate;

/**
 * Simple sales record. Used to store one line of a sales transaction.
 *
 * Optimistically the bestBefore is LocalDate.MAX for a non-perishable product.
 *
 * @author Pieter van den Hombergh / Richard van den Ham
 */
public class SalesRecord {

    private final int barcode;
    private LocalDate bestBeforeDate = LocalDate.MAX;
    private final LocalDate soldOnDate;
    private int salesPrice;
    private int quantity = 1;

    /**
     * Initialize salesRecord. When it's created (by scanning a product), the initial
     * quantity is set to 1. When the same product is scanned again, the quantity is increased.
     * @param barcode
     * @param soldOnDate
     * @param labelPrice 
     */
    public SalesRecord(int barcode, LocalDate soldOnDate, int labelPrice ) {
        this.barcode = barcode;
        this.soldOnDate = soldOnDate;
        this.salesPrice = labelPrice;
    }

    /**
     * Bar code identifier of Product.
     * @return bar code
     */
    public int getBarcode() {
        return barcode;
    }

    /**
     * GetBestBeforeDate. This date is set by the cashier for perishable products.
     * If it's not set or if the product is not perishable, it will return LocalDate.MAX.
     * @return bestBeforeDate
     */
    public LocalDate getBestBeforeDate() {
        return bestBeforeDate;
    }
    
    /**
     * setBestBeforeDate. Update-able in order to set a best before date for perishable products. 
     * @param bestBefore 
     */
    public void setBestBeforeDate(LocalDate bestBefore) {
        this.bestBeforeDate = bestBefore;
    }

    /**
     * getSoldOnDate.
     * @return date on which the sales record was created. 
     */
    public LocalDate getSoldOnDate() {
        return soldOnDate;
    }

    /**
     * getSalesPrice. For non perishable products, this will always be equal
     * to the catalogue price. For perishable products there might be a discount.
     * In the latter case, the sales price is the price after discount for 1 product.
     * @return sales price per item
     */
    public int getSalesPrice() {
        return salesPrice;
    }

    /**
     * setSalesPrice. In order to update the sales price for a perishable product that
     * is close to best before date.
     * @param salesPrice 
     */
    public void setSalesPrice(int salesPrice) {
        this.salesPrice = salesPrice;
    }
    
    /**
     * getQuantity. Basically the number of times this product was scanned and therefore sold.
     * @return quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * increaseQuantityBy. To increase the quantity with a given number of items. In 
     * our use case typically invoked with value 1 (to increase quantity by 1 after scan 
     * of same product).
     * @param increaseBy 
     */
    public void increaseQuantity(int increaseBy) {
        this.quantity += increaseBy;
    }

    /**
     * Textual representation of SalesRecord. Contains bar code,
     * best before date, soldOn date, quantity and actual sales price.
     * @return 
     */
    @Override
    public String toString() {
        return "SalesRecord{" + "barcode=" + barcode
                + ", bestBefore=" + bestBeforeDate + ", soldOn=" + soldOnDate + " qty " + quantity
                + ", salesPrice=" + salesPrice + '}';
    }
}
