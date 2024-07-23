package ru.snake.bot.easydate;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import picocli.CommandLine;
import ru.snake.bot.easydate.cli.BotCommand;
import ru.snake.bot.easydate.cli.ImageCommand;
import ru.snake.bot.easydate.cli.RootCommand;
import ru.snake.bot.easydate.database.Database;
import ru.snake.date.conversation.worker.Language;
import ru.snake.date.conversation.worker.Worker;
import ru.snake.date.conversation.worker.WorkerSettings;
import ru.snake.date.conversation.worker.data.OpenersResult;

public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		int exitCode = new CommandLine(new RootCommand()).addSubcommand("image", new ImageCommand(Main::startImage))
			.addSubcommand("bot", new BotCommand(Main::startBot))
			.execute(args);

		System.exit(exitCode);
	}

	private static void startImage(final File configFile, final File image, final String description) {
		Worker worker = createWorker(configFile);

		try {
			OpenersResult result;

			if (description == null) {
				result = worker.writeOpeners(image, Language.RUSSIAN);
			} else {
				result = worker.writeOpeners(image, description, Language.RUSSIAN);
			}

			System.out.println(result.asString());
		} catch (OllamaBaseException e) {
			LOG.error("Failed to communicate with Ollama server.", e);
		} catch (IOException e) {
			LOG.error("Failed to read image file.", e);
		} catch (InterruptedException e) {
			LOG.error("Process interrupted.", e);
		}
	}

	private static void startBot(
		final File configFile,
		final File databaseFile,
		final String botToken,
		final Set<Long> allowUsers
	) {
		Worker worker = createWorker(configFile);
		Database database = createDatabase(databaseFile);
		EasyDateBot bot = new EasyDateBot(botToken, allowUsers, database, worker);

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

	private static Database createDatabase(File databaseFile) {
		if (databaseFile != null) {
			return Database.onDisk(databaseFile);
		} else {
			return Database.inMemory();
		}
	}

	private static Worker createWorker(final File configFile) {
		WorkerSettings settings;

		if (configFile == null) {
			settings = WorkerSettings.create();
		} else {
			try {
				settings = WorkerSettings.fromFile(configFile);
			} catch (ConfigurateException e) {
				LOG.error("Failed to load configuration.", e);

				System.exit(1);

				return null;
			}
		}

		return Worker.create(settings);
	}

}
