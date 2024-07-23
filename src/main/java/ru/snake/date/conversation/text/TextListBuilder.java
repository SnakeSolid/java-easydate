package ru.snake.date.conversation.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextListBuilder {

	private String header;

	private final List<String> items;

	public TextListBuilder() {
		this.header = "";
		this.items = new ArrayList<>();
	}

	public TextListBuilder header(final String header) {
		this.header = header;

		return this;
	}

	public TextListBuilder item(final String item) {
		items.add(item);

		return this;
	}

	public boolean hasHeader() {
		return !header.isBlank();
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	public TextList build() {
		return TextList.from(header, Collections.unmodifiableList(items));
	}

	@Override
	public String toString() {
		return "TextListBuilder [header=" + header + ", items=" + items + "]";
	}

}
