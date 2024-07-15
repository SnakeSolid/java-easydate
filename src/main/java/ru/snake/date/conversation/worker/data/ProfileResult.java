package ru.snake.date.conversation.worker.data;

import java.util.List;

public class ProfileResult {

	private final List<ProfileDescription> descriptions;

	public ProfileResult(final List<ProfileDescription> descriptions) {
		this.descriptions = descriptions;
	}

	public List<ProfileDescription> getDescriptions() {
		return descriptions;
	}

	@Override
	public String toString() {
		return "ProfileResult [descriptions=" + descriptions + "]";
	}

}
