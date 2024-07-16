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

	public String asString() {
		StringBuilder builder = new StringBuilder();

		for (ProfileDescription description : descriptions) {
			if (!builder.isEmpty()) {
				builder.append("\n\n");
			}

			builder.append(String.format("*%s*\n\n%s", description.getHeader(), description.getContent()));
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return "ProfileResult [descriptions=" + descriptions + "]";
	}

}
