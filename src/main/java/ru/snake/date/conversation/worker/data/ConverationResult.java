package ru.snake.date.conversation.worker.data;

import java.util.List;

public class ConverationResult {

	private final List<String> items;

	public ConverationResult(final List<String> items) {
		this.items = items;
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	public List<String> getItems() {
		return items;
	}

	public String asString() {
		StringBuilder builder = new StringBuilder();

		for (String item : items) {
			builder.append(String.format("\u2022 %s\n\n", item));
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return "ConverationResult [items=" + items + "]";
	}

}
