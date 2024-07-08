package ru.snake.date.conversation.worker;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;

public class WorkerBuilder {

	private final String ollamaUri;

	private String imageModelName;

	private String textModelName;

	private long timeout;

	public WorkerBuilder(final String ollamaUri) {
		this.ollamaUri = ollamaUri;
		this.timeout = 60;
	}

	public WorkerBuilder imageModel(String imageModelName) {
		this.imageModelName = imageModelName;

		return this;
	}

	public WorkerBuilder textModel(String textModelName) {
		this.textModelName = textModelName;

		return this;
	}

	public WorkerBuilder timeout(long timeout) {
		this.timeout = timeout;

		return this;
	}

	public Worker build() {
		OllamaAPI api = new OllamaAPI(ollamaUri);
		api.setRequestTimeoutSeconds(timeout);
		api.setVerbose(false);

		return new Worker(api, imageModelName, textModelName);
	}

	@Override
	public String toString() {
		return "WorkerBuilder [ollamaUri=" + ollamaUri + ", imageModelName=" + imageModelName + ", textModelName="
				+ textModelName + ", timeout=" + timeout + "]";
	}

}
