package main.application.ports.output;

import java.util.List;
import java.util.Map;

public interface QuartzOutputPort {
	public List<Map<String,Object>> listScheduled() throws Exception;
}
