package main.application.ports.input;

import org.json.JSONObject;

public interface StageApiUsecase {
	JSONObject executeTmp(Integer uncompiled, String dagname, String stepname, String token);
}
