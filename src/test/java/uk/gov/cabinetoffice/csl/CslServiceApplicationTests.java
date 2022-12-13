package uk.gov.cabinetoffice.csl;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
class CslServiceApplicationTests {

	@Autowired
	private ConfigurableApplicationContext context;

	@Test
	void contextLoads() {
	}

	@Test
	public void test() {
		assertTrue(context.isActive());
	}
}
