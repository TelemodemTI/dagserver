package main;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.log4j.Logger;

public class BasicTest {
	
	protected static Logger log = Logger.getLogger("DAG");

    @Test
    public void testSum() {
    	log.debug("testing ");
        int a = 5;
        int b = 7;
        int expected = 12;
        int actual = a + b;
        assertEquals(expected, actual);
    }
}
