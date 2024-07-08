package ru.snake.bot.easydate;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import ru.snake.date.conversation.worker.OpenersResult;
import ru.snake.date.conversation.worker.Worker;

public class Main {

	private static final String DEFAULT_URI = "http://localhost:11434/";

	private static final String IMAGE_MODEL_NAME = "llava-llama3";

	private static final String TEXT_MODEL_NAME = "gemma2";

	private static final long DEFAULT_TIMEOUT = 120;

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		Settings settings = Settings.parse(args);

		if (settings == null) {
			return;
		}

		Worker worker = Worker.builder(DEFAULT_URI)
			.imageModel(IMAGE_MODEL_NAME)
			.textModel(TEXT_MODEL_NAME)
			.timeout(DEFAULT_TIMEOUT)
			.build();
		File imagePath = settings.getImagePath();

		if (imagePath != null) {
			try {
				writeOpeners(worker, imagePath);
			} catch (OllamaBaseException e) {
				LOG.error("Failed to communicate with Ollama server.", e);
			} catch (IOException e) {
				LOG.error("Failed to read image file.", e);
			} catch (InterruptedException e) {
				LOG.error("Process interrupted.", e);
			}
		} else {
			EasyDateBot bot = new EasyDateBot(settings.getBotToken(), settings.getAllowUsers(), worker);

			try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
				botsApplication.registerBot(settings.getBotToken(), bot);
				Thread.currentThread().join();
			} catch (TelegramApiException e) {
				LOG.error("Telegramm API error.", e);
			} catch (InterruptedException e) {
				LOG.error("Thread was interrupted.", e);
			} catch (Exception e) {
				LOG.error("Unknown error.", e);
			}
		}
	}

	private static void writeOpeners(Worker worker, File file)
			throws OllamaBaseException, IOException, InterruptedException {
		OpenersResult result = worker.writeOpeners(file);

		System.out.println(result.getDescription());
		System.out.println("--- --- --- --- --- --- --- --- --- ---");
		System.out.println(result.getEnglish());
		System.out.println("--- --- --- --- --- --- --- --- --- ---");
		System.out.println(result.getRussian());
	}

}
