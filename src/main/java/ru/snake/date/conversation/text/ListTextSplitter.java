package ru.snake.date.conversation.text;

import java.util.ArrayList;
import java.util.List;

public class ListTextSplitter {

	private final String[] prefixes;

	public ListTextSplitter(String... prefixes) {
		this.prefixes = prefixes;
	}

	public List<String> split(String text, String... prefixes) {
		List<String> result = new ArrayList<>();

		for (String line : text.split("\n")) {
			line = line.strip();

			if (checkItem(line)) {
				String item = toItem(line);

				result.add(item);
			}
		}

		return result;
	}

	private String toItem(String line) {
		String result = line;

		for (String prefix : prefixes) {
			result = result.replace(prefix, "");
		}

		return result.replace("\"", "").replace("\'", "").strip();
	}

	private boolean checkItem(String line) {
		for (String prefix : prefixes) {
			if (line.startsWith(prefix)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return "HeaderTextSplitter [prefixes=" + prefixes + "]";
	}

}
