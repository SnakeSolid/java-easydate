package ru.snake.date.conversation.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ru.snake.date.conversation.worker.data.ProfileDescription;

public class HeaderTextSplitter extends BaseReplacer {

	private final Set<String> prefixes;

	public HeaderTextSplitter(String... prefixes) {
		this.prefixes = Set.of(prefixes);
	}

	public HeaderTextSplitter(Set<String> prefixes) {
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
		return trim(prefixes, line);
	}

	private boolean checkHeader(String line) {
		return check(prefixes, line);
	}

	@Override
	public String toString() {
		return "HeaderTextSplitter [prefixes=" + prefixes + "]";
	}

}
