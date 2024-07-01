package main.cl.dagserver.domain.core;

import java.util.Properties;

import main.cl.dagserver.domain.exceptions.DomainException;

public class DagGraphApi {
	private Boolean compiled;
	private Properties prop;
	private Properties opts;
	public void execute(String dagname) throws DomainException {
		if(compiled != null && prop != null && opts != null) {
			if(compiled) {
				
			} else {
				
			}	
		} else {
			throw new DomainException("invalid dag to execute");
		}
	}
	public DagGraphApi setArgs(Properties prop,Properties opts) {
		this.prop = prop;
		this.opts = opts;
		return this;
	}
	public DagGraphApi isCompiled(Boolean type) {
		this.compiled = type;
		return this;
	}
}
