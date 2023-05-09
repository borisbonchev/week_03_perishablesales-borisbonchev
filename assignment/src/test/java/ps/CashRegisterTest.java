package ps;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CashRegister is the business class to test. It gets bar code scanner input,
 * is able to output to the ui, and uses the SalesService.
 *
 * @author Pieter van den Hombergh / Richard van den Ham
 */
@ExtendWith(MockitoExtension.class)
public class CashRegisterTest {

    Product lamp = new Product("led lamp", "Led Lamp", 250, 1_234, false);
    Product banana = new Product("banana", "Bananas Fyffes", 150, 9_234, true);
    Product cheese = new Product("cheese", "Gouda 48+", 800, 7_687, true);
    Clock clock = Clock.systemDefaultZone();

    Map<String, Product> products = Map.of(
            "lamp", lamp,
            "banana", banana,
            "cheese", cheese
    );

    @Mock
    Printer printer;

    @Mock
    SalesService salesService;

    @Mock
    UI ui;

    @Captor
    private ArgumentCaptor<SalesRecord> salesRecordCaptor;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Captor
    private ArgumentCaptor<String> stringLineCaptor;

    CashRegister cashRegister;

    @BeforeEach
    void setup() {
        cashRegister = new CashRegister(clock, printer, ui, salesService);
    }

    /**
     * Test that after a scan, a non perishable product is looked up and
     * correctly displayed.Have a look at requirements in the JavaDoc of the
     * CashRegister methods. Test product is non-perishable, e.g. led lamp.
     * <ul>
     * <li>Train the mocked salesService and check if a lookup has been
     * done.<li>Check if the mocked UI was asked to display the
     * product.<li>Ensure that ui.displayCalendar is not called.<b>NOTE
     *
     * @throws ps.UnknownProductException
     */
    @Test
    public void lookupAndDisplayNonPerishableProduct() throws UnknownProductException {
        //TODO 1 Implement Test Method and write necessary implementation in scan() method of CashRegister.
        when(salesService.lookupProduct(lamp.getBarcode())).thenReturn(lamp);
        cashRegister.scan(lamp.getBarcode());

        verify(salesService, times(1)).lookupProduct(lamp.getBarcode());
        verify(ui, times(1)).displayProduct(lamp);
        verify(ui, times(0)).displayCalendar();
    }

    /**
     * Test that both the product and calendar are displayed when a perishable
     * product is scanned.
     *
     * @throws UnknownProductException but don't worry about it, since you test
     * with an existing product now.
     */
    @Test
    public void lookupAndDisplayPerishableProduct() throws UnknownProductException {
        //TODO 2 Implement Test Method and write necessary implementation in scan() method of CashRegister
        when(salesService.lookupProduct(banana.getBarcode())).thenReturn(banana);
        cashRegister.scan(banana.getBarcode());

        verify(salesService, times(1)).lookupProduct(banana.getBarcode());
        verify(ui, times(1)).displayProduct(banana);
        verify(ui, times(1)).displayCalendar();
    }

    /**
     * Scan a product, finalize the sales transaction, then verify that the
     * correct salesRecord is sent to the SalesService. Use a non-perishable
     * product. SalesRecord has no equals method (and do not add it), instead
     * use {@code assertThat(...).usingRecursiveComparison().isEqualTo(...)}.
     * Also verify that if you print a receipt after finalizing, there is no output.
     *
     * @throws ps.UnknownProductException
     */
    @Test
    public void finalizeSalesTransaction() throws UnknownProductException {
        //TODO 3 Implement Test Method and write necessary implementation in finalizeSalesTransaction() method of CashRegister
        when(salesService.lookupProduct(lamp.getBarcode())).thenReturn(lamp);

        cashRegister.scan(lamp.getBarcode());

        cashRegister.finalizeSalesTransaction();
        verify(salesService, times(1)).sold(salesRecordCaptor.capture());
        assertThat(salesRecordCaptor.getValue()).usingRecursiveComparison().isEqualTo(new SalesRecord(lamp.getBarcode(), LocalDate.now(clock), lamp.getPrice()));
    }

    /**
     * Verify price reductions. For a perishable product with: 10 days till
     * best-before, no reduction; 2 days till best-before, no reduction; 1 day
     * till best-before, 35% price reduction; 0 days till best-before (so sales
     * date is best-before date), 65% price reduction; -1 days till best-before
     * (product over date), 100% price reduction.
     *
     * Check the correct price using the salesService and an argument captor.
     */
    @ParameterizedTest
    @CsvSource({
        "banana,10,100",
        "banana,2,100",
        "banana,1,65",
        "banana,0,35",
        "banana,-1,0",})
    public void priceReductionNearBestBefore(String productName, int daysBest, int pricePercent) throws UnknownBestBeforeException, UnknownProductException {
        //TODO 4 Implement Test Method and write necessary implementation in correctSalesPrice() method of CashRegister
        when(salesService.lookupProduct(banana.getBarcode())).thenReturn(banana);
        cashRegister.scan(products.get(productName).getBarcode());
        cashRegister.correctSalesPrice(LocalDate.now(clock).plusDays(daysBest));
        cashRegister.finalizeSalesTransaction();
        verify(salesService).sold(salesRecordCaptor.capture());
        assertThat(salesRecordCaptor.getValue().getSalesPrice())
                .isEqualTo(products.get(productName).getPrice() * pricePercent / 100)
                .as("The price of the perishable product is incorrect.");
    }

    /**
     * When multiple products are scanned, the resulting lines on the receipt
     * should be perishable first, not perishables last. Scan a banana, led lamp
     * and a cheese. The products should appear on the printed receipt in
     * banana, cheese, lamp order. The printed product line on the receipt
     * should contain description, (reduced) salesprice per piece and the
     * quantity.
     *
     */
    @Test
    public void printInProperOrder() throws UnknownBestBeforeException, UnknownProductException {
        //TODO 5 Implement Test Method and write necessary implementation in printReceipt() method of CashRegister
        when(salesService.lookupProduct(banana.getBarcode())).thenReturn(banana);
        when(salesService.lookupProduct(lamp.getBarcode())).thenReturn(lamp);
        when(salesService.lookupProduct(cheese.getBarcode())).thenReturn(cheese);
        cashRegister.scan(cheese.getBarcode());
        cashRegister.scan(lamp.getBarcode());
        cashRegister.scan(banana.getBarcode());
        cashRegister.printReceipt();
        cashRegister.finalizeSalesTransaction();
        verify(printer,times(3)).println(stringLineCaptor.capture());
        verify(salesService,times(3)).sold(salesRecordCaptor.capture());
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(stringLineCaptor.getAllValues().get(0))
                    .contains("Product: " + cheese.getDescription() + ", priced at " + cheese.getPrice() + ", with quantity = " + salesRecordCaptor.getAllValues().get(0).getQuantity());
            softly.assertThat(stringLineCaptor.getAllValues().get(1))
                    .contains("Product: " + banana.getDescription() + ", priced at " + banana.getPrice() + ", with quantity = " + salesRecordCaptor.getAllValues().get(1).getQuantity());
            softly.assertThat(stringLineCaptor.getAllValues().get(2))
                    .contains("Product: " + lamp.getDescription() + ", priced at " + lamp.getPrice() + ", with quantity = " + salesRecordCaptor.getAllValues().get(2).getQuantity());
        });
    }

    /**
     * Test that invoking correctSalesPrice with null parameter results in
     * exception.
     *
     * @throws UnknownProductException (but that one is irrelevant). First scan
     * (scan) a perishable product. Afterwards invoke correctSalesPrice with
     * null parameter. An UnknownProductException should be thrown.
     */
    @Test
    public void correctSalesPriceWithBestBeforeIsNullThrowsException() throws UnknownProductException {
        //TODO 6 Implement Test Method and write necessary implementation in correctSalesPrice() method of CashRegister
        ThrowableAssert.ThrowingCallable code = () -> {
            when(salesService.lookupProduct(banana.getBarcode())).thenReturn(banana);
            cashRegister.scan(banana.getBarcode());
            cashRegister.correctSalesPrice(null);
        };
        assertThatCode(code).isExactlyInstanceOf(UnknownBestBeforeException.class);
    }

    /**
     * Test scanning an unknown product results in error message on GUI.
     */
    @Test
    public void lookupUnknownProductShouldDisplayErrorMessage() throws UnknownProductException {
        //TODO 7 Implement Test Method and write necessary implementation in scan() method of CashRegister
        when(salesService.lookupProduct(000000)).thenThrow(UnknownProductException.class);
        cashRegister.scan(000000);

        verify(ui, times(1)).displayErrorMessage("This product is unknown");
    }

    /**
     * Test that a product that is scanned twice, is registered in the
     * salesService with the proper quantity AND make sure printer prints the
     * proper quantity as well.
     *
     * @throws UnknownProductException
     */
    @Test
    public void scanProductTwiceShouldIncreaseQuantity() throws UnknownProductException {
        //TODO 8 Implement Test Method and write necessary implementation in scan() method of CashRegister

        when(salesService.lookupProduct(lamp.getBarcode())).thenReturn(lamp);

        cashRegister.scan(lamp.getBarcode());
        cashRegister.scan(lamp.getBarcode());

        cashRegister.finalizeSalesTransaction();
        verify(salesService, times(1)).sold(salesRecordCaptor.capture());

        assertThat(salesRecordCaptor.getValue().getQuantity()).as("Quantity should increase.").isEqualTo(2);
    }
}
