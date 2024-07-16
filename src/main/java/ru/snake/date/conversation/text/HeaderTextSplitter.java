package ru.snake.date.conversation.text;

import java.util.ArrayList;
import java.util.List;

import ru.snake.date.conversation.worker.data.ProfileDescription;

public class HeaderTextSplitter {

	private final String[] prefixes;

	public HeaderTextSplitter(String... prefixes) {
		this.prefixes = prefixes;
	}

	public List<ProfileDescription> split(String text, String... prefixes) {
		List<ProfileDescription> result = new ArrayList<>();
		String header = null;
		StringBuilder content = new StringBuilder();

		for (String line : text.split("\n")) {
			line = line.strip();

			if (checkHeader(line)) {
				String contentString = content.toString().strip();

				if (header != null && !contentString.isEmpty()) {
					ProfileDescription description = new ProfileDescription(header, contentString);

					result.add(description);
					content.setLength(0);
				}

				header = toHeader(line);
			} else if (header != null) {
				content.append(line);
				content.append('\n');
			}
		}

		String contentString = content.toString().strip();

		if (header != null && !contentString.isEmpty()) {
			ProfileDescription description = new ProfileDescription(header, contentString);

			result.add(description);
		}

		return result;
	}

	private String toHeader(String line) {
		String result = line;

		for (String prefix : prefixes) {
			result = result.replace(prefix, "");
		}

		return result.strip();
	}

	private boolean checkHeader(String line) {
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
