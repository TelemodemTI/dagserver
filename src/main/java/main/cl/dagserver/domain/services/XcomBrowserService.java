package main.cl.dagserver.domain.services;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.nhl.dflib.DataFrame;
import main.cl.dagserver.application.ports.input.XcomBrowserUsecase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.exceptions.DomainException;

@Service
public class XcomBrowserService extends BaseServiceComponent implements XcomBrowserUsecase {

	@Override
	public Map<String,DataFrame> getEntry(String xcomkey, String token) throws DomainException {
		tokenEngine.untokenize(token, jwtSecret, jwtSigner);
		return this.storage.getEntry(xcomkey);
	}
	
}
