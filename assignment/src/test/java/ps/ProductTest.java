package ps;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author Pieter van den Hombergh
 */
public class ProductTest {

    /**
     * Since Product is a simple value class, we only test constructor, getter
     * methods and toString.
     */
    @Test
    void testProductToString() {

        Product p = new Product( "elstar", "Elstar Appels, los", 100, 952134574, true );
        assertThat( p.toString() ).contains( "elstar", "Elstar Appels, los", "100", "952134574", "perishable" );

    }

    @ParameterizedTest
    @CsvSource( value = {
        "'shortName','string', 'fyffes'",
        "'description','string','Fyffes Bananen'",
        "'price','int',100",
        "'barcode','int',384736876",
        "'perishable','boolean',true"
    } )
    void getters( String property, String type, String expectedValue ) {
        Product p = new Product( "fyffes", "Fyffes Bananen", 100, 384736876, true );
        switch ( type ) {
            case "int":
                assertThat( p ).extracting( property ).isEqualTo( Integer.parseInt( expectedValue ) );
                break;
            case "boolean":
                assertThat( p ).extracting( property ).isEqualTo( Boolean.parseBoolean( expectedValue ) );
                break;
            case "string":
                assertThat( p ).extracting( property ).isEqualTo( expectedValue );
                break;
        }
    }
}
