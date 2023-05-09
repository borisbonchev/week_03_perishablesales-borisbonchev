package ps;

import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class to be developed test driven with Mockito.
 *
 * @author Pieter van den Hombergh / Richard van den Ham
 */
class CashRegister {

    private final Clock clock;
    private final Printer printer;
    private final UI ui;
    private final SalesService salesService;
    
    // Declare a field to keep a salesCache, which is a mapping between a Product and a SalesRecord.
    // When a product gets scanned multiple times, the quantity of the salesRecord is increased. 
    // A LinkedHashMap has the benefit that, in contrast to the HashMap, the order in which 
    // the items were added is preserved.

    private Map<Product, SalesRecord> salesCache;

    // Declare a field to keep track of the last scanned product, initially being null.
    private Product lastScannedProduct;


    // TODO Declare and initialize fields.


    /**
     * Create a business object
     *
     * @param clock wall clock
     * @param printer to use
     * @param ui to use
     * @param salesService to use
     */
    CashRegister(Clock clock, Printer printer, UI ui, SalesService salesService) {
        this.clock = clock;
        this.printer = printer;
        this.ui = ui;
        this.salesService = salesService;
        this.salesCache = new LinkedHashMap<>();
        this.lastScannedProduct = null;
    }

    /**
     * The scan method is triggered by scanning a product by the cashier.
     * Get the product from the salesService. If the product can't be found, an UnknownProductException is thrown and the
     * error message from the exception is shown on the display (ui).
     * If found, check if there is a salesRecord for this product already. If not, create one. If it exists, update the quantity.
     * In case a perishable product was scanned, the cashier should get a calendar on his/her display.
     * The product is displayed on the display.
     * @param barcode 
     */
    public void scan(int barcode) throws UnknownProductException {

        try {
            Product product = salesService.lookupProduct(barcode);
            SalesRecord salesRecord = new SalesRecord(barcode, LocalDate.now(clock), product.getPrice());

            if (salesCache.containsKey(product)) {
                salesRecord.increaseQuantity(1);
                salesCache.put(product, salesRecord);
            } else {
                salesCache.put(product, salesRecord);
            }

            if (product.isPerishable()) {
                lastScannedProduct = product;
                ui.displayCalendar();
            }

            ui.displayProduct(product);
        } catch (UnknownProductException e) {
            ui.displayErrorMessage("This product is unknown");
        }
    }

    /**
     * Submit the sales to the sales service, finalizing the sales transaction.
     * All salesRecords in the salesCache are stored (one-by-one) in the salesService.
     * All caches are reset.
     */
    public void finalizeSalesTransaction() {
        for (SalesRecord salesRecord : salesCache.values()) {
            salesService.sold(salesRecord);
        }
        salesCache.clear();
    }

    /**
     * Correct the sales price of the last scanned product by considering the
     * given best before date, then submit the product to the service and save
     * in list.
     *
     * This method consults the clock to see if the product is eligible for a
     * price reduction because it is near or at its best before date.
     * 
     * Precondition is that the last scanned product is the perishable product. 
     * You don't need to check that in your code. 
     * 
     * To find the number of days from now till the bestBeforeDate, use
     * LocalDate.now(clock).until(bestBeforeDate).getDays();
     * 
     * Depending on the number of days, update the price in the salesRecord following the
     * pricing strategy as described in the assignment
     *
     * Update the salesRecord belonging to the last scanned product if necessary, so 
     * update the price and set the BestBeforeDate.
     * 
     * @param bestBeforeDate
     * @throws UnknownBestBeforeException in case the best before date is null.
     */
    public void correctSalesPrice(LocalDate bestBeforeDate) throws UnknownBestBeforeException {
        if (bestBeforeDate == null) {
            throw new UnknownBestBeforeException("The before date should not be null");
        }

        int n = LocalDate.now(clock).until(bestBeforeDate).getDays();

        if (n == 1) {
            salesCache.get(lastScannedProduct).setSalesPrice(lastScannedProduct.getPrice()*65/100);
        } else if (n == 0) {
            salesCache.get(lastScannedProduct).setSalesPrice(lastScannedProduct.getPrice()*35/100);
        } else if (n < 0) {
            salesCache.get(lastScannedProduct).setSalesPrice(lastScannedProduct.getPrice()*0);
        }
    }

    /**
     * Print the receipt for all the sold products, to hand the receipt to the
     * customer. The receipt contains lines containing: the product description,
     * the (possibly reduced) sales price per piece and the quantity, separated by
     * a tab.
     * The order of printing is the order of scanning, however Perishable
     * products are printed first. The non-perishables afterwards.
     */
    public void printReceipt() {
        //TODO implement printReceipt
        for (Map.Entry<Product, SalesRecord> entry : salesCache.entrySet()) {
            Product key = entry.getKey();
            SalesRecord value = entry.getValue();

            if (key.isPerishable()) {
                printer.println("Product: " + key.getDescription() + ", priced at " + value.getSalesPrice() + ", with quantity = " + value.getQuantity());
            }
        }

        for (Map.Entry<Product, SalesRecord> entry : salesCache.entrySet()) {
            Product key = entry.getKey();
            SalesRecord value = entry.getValue();

            if (!key.isPerishable()) {
                printer.println("Product: " + key.getDescription() + ", priced at " + value.getSalesPrice() + ", with quantity = " + value.getQuantity());
            }
        }
    }
}
