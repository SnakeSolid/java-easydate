package ru.snake.bot.easydate.conversation.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListGroupSplitter extends BaseReplacer {

	private final Set<String> prefixes;

	public ListGroupSplitter(String... prefixes) {
		this.prefixes = Set.of(prefixes);
	}

	public ListGroupSplitter(final Set<String> prefixes) {
		this.prefixes = prefixes;
	}

	public List<String> split(String text) {
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
		return trim(prefixes, line);
	}

	private boolean checkItem(String line) {
		return check(prefixes, line);
	}

	@Override
	public String toString() {
		return "HeaderTextSplitter [prefixes=" + prefixes + "]";
	}

}
