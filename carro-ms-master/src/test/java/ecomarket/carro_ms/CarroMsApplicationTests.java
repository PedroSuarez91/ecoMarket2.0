package ecomarket.carro_ms;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CarroMsApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainClassExists() {
		assertNotNull(CarroMsApplication.class);
	}

}
