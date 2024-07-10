package ru.snake.date.conversation.worker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatMessageRole;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatRequestBuilder;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatRequestModel;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatResult;
import ru.snake.bot.easydate.Replacer;

public class Worker {

	private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

	private final OllamaAPI imageApi;

	private final OllamaAPI textApi;

	private final String imageModelName;

	private final String textModelName;

	public Worker(
		final OllamaAPI imageApi,
		final OllamaAPI textApi,
		final String imageModelName,
		final String textModelName
	) {
		this.imageApi = imageApi;
		this.textApi = textApi;
		this.imageModelName = imageModelName;
		this.textModelName = textModelName;
	}

	public synchronized OpenersResult writeOpeners(File file)
			throws OllamaBaseException, IOException, InterruptedException {
		LOG.info("Generation openeras for {}", file);

		checkFiles(file);

		String imageDescription = imageQuery(file, text("prompts/image_description.txt"));
		String imageObjects = imageQuery(file, text("prompts/image_objects.txt"));
		String initialPhrases = textQuery(
			Replacer.replace(
				text("prompts/text_openers.txt"),
				Map.of("image_description", imageDescription, "image_objects", imageObjects)
			)
		);
		String translatedPhrases = textQuery(initialPhrases, text("prompts/text_translate.txt"));

		return new OpenersResult(
			imageDescription,
			imageObjects,
			trimLines(initialPhrases),
			trimLines(translatedPhrases)
		);
	}

	private String textQuery(String... messages) throws OllamaBaseException, IOException, InterruptedException {
		if (LOG.isInfoEnabled()) {
			LOG.info("Execute text query: {}", Arrays.asList(messages));
		}

		OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(textModelName);

		for (String message : messages) {
			builder.withMessage(OllamaChatMessageRole.USER, message);
		}

		OllamaChatRequestModel request = builder.build();
		OllamaChatResult chat = textApi.chat(request);
		String result = chat.getResponse();

		LOG.info("Query result: {}", result);

		return result;
	}

	private String imageQuery(File file, String... messages)
			throws OllamaBaseException, IOException, InterruptedException {
		if (messages.length == 0) {
			return "";
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("Execute image query: {}", Arrays.asList(messages));
		}

		StringBuilder builder = new StringBuilder();
		OllamaChatRequestModel request = OllamaChatRequestBuilder.getInstance(imageModelName)
			.withMessage(OllamaChatMessageRole.USER, messages[0], List.of(file))
			.build();
		OllamaChatResult chat = imageApi.chat(request);

		builder.append(chat.getResponse());

		for (int index = 1; index < messages.length; index += 1) {
			request = OllamaChatRequestBuilder.getInstance(imageModelName)
				.withMessages(chat.getChatHistory())
				.withMessage(OllamaChatMessageRole.USER, messages[index])
				.build();
			chat = imageApi.chat(request);

			builder.append("\n\n");
			builder.append(chat.getResponse());
		}

		LOG.info("Query result: {}", builder);

		return builder.toString();
	}

	private static String text(String path) throws IOException {
		LOG.info("Read resource: {}", path);

		try (InputStream stream = ClassLoader.getSystemResourceAsStream(path)) {
			byte[] bytes = stream.readAllBytes();
			String text = new String(bytes);

			return text;
		}
	}

	private static String trimLines(String response) {
		String[] lines = response.split("\n");
		int firstIndex = 0;
		int lastIndex = lines.length;

		while (firstIndex < lines.length) {
			String line = lines[firstIndex];

			if (line.startsWith("*") || line.startsWith("#")) {
				break;
			}

			firstIndex += 1;
		}

		while (firstIndex < lastIndex) {
			String line = lines[lastIndex - 1];

			if (line.startsWith("*") || line.startsWith("#")) {
				break;
			}

			lastIndex -= 1;
		}

		List<String> result = new ArrayList<>();

		for (String line : List.of(lines).subList(firstIndex, lastIndex)) {
			line = line.trim();

			if (line.startsWith("#")) {
				line = line.replace("#", "").replace("*", "").trim();
				line = String.format("*%s*", line);
			} else if (!line.isEmpty()) {
				line = "\u2022 " + line.replace("*", "").replace("\"", "").trim();
			}

			result.add(line);
		}

		return String.join("\n", result);
	}

	private static void checkFiles(File... files) {
		for (File file : files) {
			if (!file.exists()) {
				throw new RuntimeException(String.format("Path %s does not exists.", file));
			}

			if (!file.isFile()) {
				throw new RuntimeException(String.format("Path %s is not regular file.", file));
			}

			if (!file.canRead()) {
				throw new RuntimeException(String.format("Path %s is not readable.", file));
			}
		}
	}

	@Override
	public String toString() {
		return "Worker [imageApi=" + imageApi + ", textApi=" + textApi + ", imageModelName=" + imageModelName
				+ ", textModelName=" + textModelName + "]";
	}

	public static Worker create(WorkerSettings settings) {
		OllamaAPI imageApi = new OllamaAPI(settings.getImageUri());
		imageApi.setRequestTimeoutSeconds(settings.getTimeout());
		imageApi.setVerbose(false);

		OllamaAPI textApi = new OllamaAPI(settings.getTextUri());
		textApi.setRequestTimeoutSeconds(settings.getTimeout());
		textApi.setVerbose(false);

		return new Worker(imageApi, textApi, settings.getImageModel(), settings.getTextModel());
	}

}
