package ru.snake.bot.easydate.conversation.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListTextSplitter extends BaseReplacer {

	private final Set<String> headerPrefixes;

	private final Set<String> itemPrefixes;

	public ListTextSplitter(final Set<String> headerPrefixes, final Set<String> itemPrefixes) {
		this.headerPrefixes = headerPrefixes;
		this.itemPrefixes = itemPrefixes;
	}

	public List<TextList> split(String text, String... prefixes) {
		List<TextList> result = new ArrayList<>();
		TextListBuilder builder = TextList.builder();

		for (String line : text.split("\n")) {
			line = line.strip();

			if (checkHeader(line)) {
				if (builder.hasHeader() && !builder.isEmpty()) {
					result.add(builder.build());

					builder = TextList.builder();
				}

				String header = toHeader(line);

				builder.header(header);
			} else if (checkItem(line) && builder.hasHeader()) {
				String item = toItem(line);

				builder.item(item);
			}
		}

		if (builder.hasHeader() && !builder.isEmpty()) {
			result.add(builder.build());
		}

		return result;
	}

	private String toHeader(String line) {
		return trim(headerPrefixes, line);
	}

	private String toItem(String line) {
		return trim(itemPrefixes, line);
	}

	private boolean checkHeader(String line) {
		return check(headerPrefixes, line);
	}

	private boolean checkItem(String line) {
		return check(itemPrefixes, line);
	}

	@Override
	public String toString() {
		return "ListTextSplitter [headerPrefixes=" + headerPrefixes + ", itemPrefixes=" + itemPrefixes + "]";
	}

}
