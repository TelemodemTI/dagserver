package main.cl.dagserver.application.ports.input;

import org.json.JSONObject;

import com.nhl.dflib.DataFrame;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

import main.cl.dagserver.domain.exceptions.DomainException;

public interface StageApiUsecase {
	JSONObject executeTmp(Integer uncompiled, String dagname, String stepname, String token, String args) throws DomainException;
	void uploadFile(Path tempFile, String uploadPath, String string, String token) throws DomainException;
	Path getFilePath(String folderPath,String filename, String token) throws DomainException;
	Map<String, DataFrame> executeDag(String token,String jarname, String dagname, Map<String, String> args, Boolean wfr) throws DomainException;
	Map<String, DataFrame> executeDag(String jarname, String dagname, JSONObject args) throws DomainException;
	File exportKeystore(String token) throws DomainException;
	void uploadKeystore(Path tempFile, String originalFilename, String token) throws DomainException;
}
