package ru.snake.bot.easydate.conversation.text;

import java.util.Set;

public class BaseReplacer {

	protected static String trim(Set<String> prefixes, String line) {
		String result = line;

		for (String prefix : prefixes) {
			result = result.replace(prefix, "");
		}

		return result.replace("\"", "").replace("\'", "").strip();
	}

	protected static boolean check(Set<String> prefixes, String line) {
		for (String prefix : prefixes) {
			if (line.startsWith(prefix)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return "BaseReplacer []";
	}

}
