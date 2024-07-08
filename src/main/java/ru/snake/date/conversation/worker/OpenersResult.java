package ru.snake.date.conversation.worker;

public class OpenersResult {

	private final String description;

	private final String objects;

	private final String english;

	private final String russian;

	public OpenersResult(final String description, final String objects, final String english, final String russian) {
		this.description = description;
		this.objects = objects;
		this.english = english;
		this.russian = russian;
	}

	public String getDescription() {
		return description;
	}

	public String getObjects() {
		return objects;
	}

	public String getEnglish() {
		return english;
	}

	public String getRussian() {
		return russian;
	}

	@Override
	public String toString() {
		return "OpenersResult [description=" + description + ", objects=" + objects + ", english=" + english
				+ ", russian=" + russian + "]";
	}

}
