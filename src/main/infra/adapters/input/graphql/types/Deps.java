package main.infra.adapters.input.graphql.types;

import java.util.List;

public class Deps {
	private List<String> onStart;
	private List<String> onEnd;
	public List<String> getOnStart() {
		return onStart;
	}
	public void setOnStart(List<String> onStart) {
		this.onStart = onStart;
	}
	public List<String> getOnEnd() {
		return onEnd;
	}
	public void setOnEnd(List<String> onEnd) {
		this.onEnd = onEnd;
	}
}
