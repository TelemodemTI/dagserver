package main.cl.dagserver.infra.adapters.input.graphql.types;

import java.util.List;

import lombok.Data;

@Data
public class FileEntry {
	private String name;
	private String type;
	private List<FileEntry> content;
}
