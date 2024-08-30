package main.cl.dagserver.domain.model;

import java.util.List;

import lombok.Data;

@Data
public class DirectoryEntryDTO {
	private String path;
	private List<FileEntryDTO> content;
}
