package ru.snake.bot.easydate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Replacer {

	private static final Pattern PATTERN = Pattern.compile("\\{([_0-9A-Za-z]+)}");

	public static String replace(String text, Map<String, String> perameters) {
		StringBuilder builder = new StringBuilder();
		Matcher matcher = PATTERN.matcher(text);
		int offset = 0;

		while (matcher.find()) {
			String name = matcher.group(1);
			int start = matcher.start();
			int end = matcher.end();

			if (offset < start) {
				Object part = text.substring(offset, start);

				builder.append(part);
			}

			String value = perameters.getOrDefault(name, "");

			builder.append(value);

			offset = end;
		}

		builder.append(text.substring(offset));

		return builder.toString();
	}

	private Replacer() {
		// Hide public constructor.
	}

}
