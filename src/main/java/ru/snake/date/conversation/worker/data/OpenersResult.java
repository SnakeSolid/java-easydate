package ru.snake.date.conversation.worker.data;

import java.util.List;
import java.util.stream.Collectors;

import ru.snake.date.conversation.text.TextList;

public class OpenersResult {

	private final String description;

	private final String objects;

	private final String english;

	private final String russian;

	private final List<Paragraph> paragraphs;

	private OpenersResult(
		final String description,
		final String objects,
		final String english,
		final String russian,
		final List<Paragraph> paragraphs
	) {
		this.description = description;
		this.objects = objects;
		this.english = english;
		this.russian = russian;
		this.paragraphs = paragraphs;
	}

	public boolean isEmpty() {
		return paragraphs.isEmpty();
	}

	public String asString() {
		StringBuilder builder = new StringBuilder();

		for (Paragraph paragraph : paragraphs) {
			if (!builder.isEmpty()) {
				builder.append("\n\n");
			}

			builder.append(paragraph.asString());
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return "OpenersResult [description=" + description + ", objects=" + objects + ", english=" + english
				+ ", russian=" + russian + ", paragraphs=" + paragraphs + "]";
	}

	public static OpenersResult from(
		final String imageDescription,
		final String imageObjects,
		final String initialPhrases,
		final String translatedPhrases,
		final List<TextList> openers
	) {
		return new OpenersResult(
			imageDescription,
			imageObjects,
			initialPhrases,
			translatedPhrases,
			openers.stream().map(Paragraph::from).collect(Collectors.toList())
		);
	}

}
