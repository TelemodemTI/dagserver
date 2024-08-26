package main.cl.dagserver.application.ports.input;

import org.json.JSONObject;
import java.nio.file.Path;

import main.cl.dagserver.domain.exceptions.DomainException;

public interface StageApiUsecase {
	JSONObject executeTmp(Integer uncompiled, String dagname, String stepname, String token) throws DomainException;
	void uploadFile(Path tempFile, String uploadPath, String string, String token) throws DomainException;
	Path getFilePath(String folderPath,String filename, String token) throws DomainException;
}
