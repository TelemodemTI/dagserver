package main.cl.dagserver.application.ports.input;

import java.util.Map;
import com.nhl.dflib.DataFrame;

import main.cl.dagserver.domain.exceptions.DomainException;

public interface XcomBrowserUsecase {
	Map<String, DataFrame> getEntry(String xcomkey, String token) throws DomainException;
}
