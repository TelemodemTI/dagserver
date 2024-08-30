package main.cl.dagserver.domain.model;

import java.util.List;

import lombok.Data;

@Data
public class FileEntryDTO {
	private String filename;
	private String type;
	private List<FileEntryDTO> content;
}
