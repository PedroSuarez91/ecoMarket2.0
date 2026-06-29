package ecomarket.factura_ms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FacturaMsApplicationTests {

    @Test
    void mainEjecutaAplicacion() {
        FacturaMsApplication.main(new String[]{
        });
    }
}