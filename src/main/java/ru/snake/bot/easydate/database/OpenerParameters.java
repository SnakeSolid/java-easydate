package ru.snake.bot.easydate.database;

public class OpenerParameters {

	private final String fileId;

	private final String description;

	public OpenerParameters(final String fileId) {
		this.fileId = fileId;
		this.description = null;
	}

	public OpenerParameters(final String fileId, final String description) {
		this.fileId = fileId;
		this.description = description;
	}

	public String getFileId() {
		return fileId;
	}

	public boolean hasDescription() {
		return description != null && !description.isBlank();
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "OpenerParameters [fileId=" + fileId + ", description=" + description + "]";
	}

}
