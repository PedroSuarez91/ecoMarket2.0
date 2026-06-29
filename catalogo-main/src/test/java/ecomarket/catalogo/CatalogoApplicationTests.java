package ecomarket.catalogo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CatalogoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainClassExists() {
		assertNotNull(CatalogoApplication.class);
	}

}
