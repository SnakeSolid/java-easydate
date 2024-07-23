package ru.snake.date.conversation.worker.data;

import java.util.List;

import ru.snake.date.conversation.text.TextList;

public class Paragraph {

	private final String header;

	private final List<String> items;

	private Paragraph(final String header, final List<String> items) {
		this.header = header;
		this.items = items;
	}

	public String getHeader() {
		return header;
	}

	public List<String> getItems() {
		return items;
	}

	public String asString() {
		StringBuilder builder = new StringBuilder();
		builder.append('*');
		builder.append(header);
		builder.append("*\n");

		for (String item : items) {
			builder.append(String.format("\n\u2022 %s", item));
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return "Paragraph [header=" + header + ", items=" + items + "]";
	}

	public static Paragraph from(final TextList textList) {
		return new Paragraph(textList.getHeader(), textList.getItems());
	}

}
