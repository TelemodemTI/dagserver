package main.cl.dagserver.infra.adapters.input.graphql.types;

import java.util.List;

public class Node {

	private Integer index;
	private List<String> operations;
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public List<String> getOperations() {
		return operations;
	}
	public void setOperations(List<String> operations) {
		this.operations = operations;
	}
}
