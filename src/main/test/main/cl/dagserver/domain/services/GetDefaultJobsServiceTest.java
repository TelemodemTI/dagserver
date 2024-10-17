package main.cl.dagserver.domain.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import main.cl.dagserver.domain.exceptions.DomainException;


class GetDefaultJobsServiceTest {
	
	private GetDefaultJobsService service = new GetDefaultJobsService();
	
	@Test
	void getDefaultJobsTest() throws DomainException {
		var resp = service.getDefaultJobs();
		assertNotNull(resp);
	}
	
}