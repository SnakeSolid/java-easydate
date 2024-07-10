package ru.snake.bot.easydate;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import picocli.CommandLine;
import ru.snake.bot.easydate.cli.BotCommand;
import ru.snake.bot.easydate.cli.ImageCommand;
import ru.snake.bot.easydate.cli.RootCommand;
import ru.snake.date.conversation.worker.OpenersResult;
import ru.snake.date.conversation.worker.Worker;

public class Main {

	private static final String DEFAULT_URI = "http://localhost:11434/";

	private static final String IMAGE_MODEL_NAME = "llava-llama3";

	private static final String TEXT_MODEL_NAME = "gemma2";

	private static final long DEFAULT_TIMEOUT = 120;

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		int exitCode = new CommandLine(new RootCommand()).addSubcommand("image", new ImageCommand(Main::startImage))
			.addSubcommand("bot", new BotCommand(Main::startBot))
			.execute(args);

		System.exit(exitCode);
	}

	private static void startImage(final File image, final String description) {
		Worker worker = createWorker();

		try {
			OpenersResult result = worker.writeOpeners(image);

			System.out.println(result.getDescription());
			System.out.println("--- --- --- --- --- --- --- --- --- ---");
			System.out.println(result.getEnglish());
			System.out.println("--- --- --- --- --- --- --- --- --- ---");
			System.out.println(result.getRussian());
		} catch (OllamaBaseException e) {
			LOG.error("Failed to communicate with Ollama server.", e);
		} catch (IOException e) {
			LOG.error("Failed to read image file.", e);
		} catch (InterruptedException e) {
			LOG.error("Process interrupted.", e);
		}
	}

	private static void startBot(final String botToken, final Set<Long> allowUsers) {
		Worker worker = createWorker();
		EasyDateBot bot = new EasyDateBot(botToken, allowUsers, worker);

		try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
			botsApplication.registerBot(botToken, bot);
			Thread.currentThread().join();
		} catch (TelegramApiException e) {
			LOG.error("Telegramm API error.", e);
		} catch (InterruptedException e) {
			LOG.error("Thread was interrupted.", e);
		} catch (Exception e) {
			LOG.error("Unknown error.", e);
		}
	}

	private static Worker createWorker() {
		return Worker.builder(DEFAULT_URI)
			.imageModel(IMAGE_MODEL_NAME)
			.textModel(TEXT_MODEL_NAME)
			.timeout(DEFAULT_TIMEOUT)
			.build();
	}

}
