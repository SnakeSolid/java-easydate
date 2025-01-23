package ru.snake.bot.easydate.conversation.worker.data;

import java.util.List;
import java.util.stream.Collectors;

import ru.snake.bot.easydate.conversation.text.TextList;

public class ConverationResult {

	private final List<Paragraph> paragraphs;

	private ConverationResult(final List<Paragraph> paragraphs) {
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
		return "ConverationResult [paragraphs=" + paragraphs + "]";
	}

	public static ConverationResult from(final List<TextList> descriptions) {
		return new ConverationResult(descriptions.stream().map(Paragraph::from).collect(Collectors.toList()));
	}

}
