package main.cl.dagserver.application.ports.input;

import org.json.JSONObject;

import main.cl.dagserver.domain.exceptions.DomainException;

public interface StageApiUsecase {
	JSONObject executeTmp(Integer uncompiled, String dagname, String stepname, String token) throws DomainException;
}
