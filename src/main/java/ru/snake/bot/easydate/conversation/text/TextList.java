package ru.snake.bot.easydate.conversation.text;

import java.util.Collections;
import java.util.List;

public class TextList {

	private final String header;

	private final List<String> items;

	private TextList(final String header, final List<String> items) {
		this.header = header;
		this.items = items;
	}

	public String getHeader() {
		return header;
	}

	public List<String> getItems() {
		return items;
	}

	@Override
	public String toString() {
		return "TextList [header=" + header + ", items=" + items + "]";
	}

	public static TextListBuilder builder() {
		return new TextListBuilder();
	}

	public static TextList from(final String header, final List<String> items) {
		return new TextList(header, Collections.unmodifiableList(items));
	}

}
