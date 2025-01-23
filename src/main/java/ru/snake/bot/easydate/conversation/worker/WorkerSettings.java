package ru.snake.bot.easydate.conversation.worker;

import java.io.File;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public class WorkerSettings {

	private static final String DEFAULT_URI = "http://localhost:11434/";

	private static final String IMAGE_MODEL_NAME = "llava-llama3";

	private static final String TEXT_MODEL_NAME = "gemma2";

	private static final long DEFAULT_TIMEOUT = 120;

	private String imageUri;

	private String textUri;

	private String imageModel;

	private String textModel;

	private long timeout;

	private WorkerSettings(
		final String imageUri,
		final String textUri,
		final String imageModel,
		final String textModel,
		final long timeout
	) {
		this.imageUri = imageUri;
		this.textUri = textUri;
		this.imageModel = imageModel;
		this.textModel = textModel;
		this.timeout = timeout;
	}

	public String getImageUri() {
		return imageUri;
	}

	public WorkerSettings withImageUri(String imageUri) {
		this.imageUri = imageUri;

		return this;
	}

	public String getTextUri() {
		return textUri;
	}

	public WorkerSettings withTextUri(String textUri) {
		this.textUri = textUri;

		return this;
	}

	public String getImageModel() {
		return imageModel;
	}

	public WorkerSettings withImageModel(String imageModel) {
		this.imageModel = imageModel;

		return this;
	}

	public String getTextModel() {
		return textModel;
	}

	public WorkerSettings withTextModel(String textModel) {
		this.textModel = textModel;

		return this;
	}

	public long getTimeout() {
		return timeout;
	}

	public WorkerSettings withTimeout(long timeout) {
		this.timeout = timeout;

		return this;
	}

	@Override
	public String toString() {
		return "WorkerSettings [imageUri=" + imageUri + ", textUri=" + textUri + ", imageModel=" + imageModel
				+ ", textModel=" + textModel + ", timeout=" + timeout + "]";
	}

	public static WorkerSettings create() {
		return new WorkerSettings(DEFAULT_URI, DEFAULT_URI, IMAGE_MODEL_NAME, TEXT_MODEL_NAME, DEFAULT_TIMEOUT);
	}

	public static WorkerSettings fromFile(final File configuration) throws ConfigurateException {
		HoconConfigurationLoader loader = HoconConfigurationLoader.builder().file(configuration).build();
		CommentedConfigurationNode root = loader.load();

		return new WorkerSettings(
			root.node("uri", "image").getString(DEFAULT_URI),
			root.node("uri", "text").getString(DEFAULT_URI),
			root.node("model", "image").getString(IMAGE_MODEL_NAME),
			root.node("model", "text").getString(TEXT_MODEL_NAME),
			root.node("timeout").getLong(DEFAULT_TIMEOUT)
		);
	}

}
