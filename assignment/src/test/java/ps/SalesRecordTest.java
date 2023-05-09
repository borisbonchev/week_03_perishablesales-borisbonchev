package ps;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author Pieter van den Hombergh / Richard van den Ham
 */
public class SalesRecordTest {

    /**
     * Since SalesRecord is a simple value class, we only test constructor, getter
     * methods and toString.
     */
    @Test
    void testSalesRecordToString() {
        
        LocalDate bestBeforeDate = LocalDate.now().plusDays( 1 );
        LocalDate today = LocalDate.now();
        
        SalesRecord sr = new SalesRecord( 952134574, today, 100 );
        sr.setBestBeforeDate( bestBeforeDate );
        
        assertThat( sr.toString() ).contains(
                "952134574", bestBeforeDate.toString(),
                today.toString(),
                Integer.toString( 100 ),
                Integer.toString( 100 ) );
    }

    @ParameterizedTest
    @CsvSource( value = {
        "'barcode','int',384736876",
        "'bestBeforeDate','date', 1",
        "'soldOnDate','date',0",
        "'salesPrice','int',100"
    } )
    void getters( String property, String type, String expectedValue ) {
        LocalDate today = LocalDate.now();
        LocalDate bestBeforeDate = today.plusDays( 1 );

        SalesRecord sr = new SalesRecord( 384736876, today, 100 );
        sr.setBestBeforeDate(bestBeforeDate);
        
        switch ( type ) {
            case "int":
                assertThat( sr ).extracting( property ).isEqualTo( Integer.parseInt( expectedValue ) );
                break;
            case "date":
                LocalDate expectedDate = today.plusDays( Integer.parseInt( expectedValue ) );
                assertThat( sr ).extracting( property ).isEqualTo( expectedDate );
                break;
        }
    }
    
    @Test
    public void testIncreaseQuantity() {
        
        SalesRecord sr = new SalesRecord( 384736876, null, 100 );
       
        SoftAssertions.assertSoftly( softly -> {
            softly.assertThat( sr.getQuantity() ).as("Quantity after creation must be 1").isEqualTo(1);
            sr.increaseQuantity(5);
            softly.assertThat( sr.getQuantity() ).as("Quantity after adding 5 must be 6").isEqualTo(6);
        } );
    }

}
