package main.infra.adapters.input.graphql.types;

import java.util.List;

public class DetailStatus {
	private String status;
	private List<Detail> detail;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<Detail> getDetail() {
		return detail;
	}
	public void setDetail(List<Detail> detail) {
		this.detail = detail;
	}
}
