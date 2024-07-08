package ru.snake.bot.easydate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import ru.snake.date.conversation.worker.OpenersResult;
import ru.snake.date.conversation.worker.Worker;

public class EasyDateBot implements LongPollingSingleThreadUpdateConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(EasyDateBot.class);

	private static final int TARGET_IMAGE_SIZE = 672;

	private final TelegramClient telegramClient;

	private final Set<Long> whiteList;

	private final Worker worker;

	public EasyDateBot(final String botToken, final Set<Long> whiteList, final Worker worker) {
		this.telegramClient = new OkHttpTelegramClient(botToken);
		this.whiteList = whiteList;
		this.worker = worker;
	}

	@Override
	public void consume(Update update) {
		try {
			consumeInternal(update);
		} catch (Error e) {
			LOG.error("Failed to process request.", e);

			// Required to stop application if runtime error occurred.
			System.exit(0);
		}
	}

	private void consumeInternal(Update update) {
		if (update.hasMessage()) {
			Message message = update.getMessage();
			long userId = message.getFrom().getId();
			long chatId = message.getChatId();

			if (!whiteList.contains(userId)) {
				sendMessage(chatId, String.format("Access denied, your ID %d.", userId));

				LOG.warn("Access denied for user ID = {}.", userId);
			} else if (update.getMessage().hasText()) {
				String text = message.getText();

				sendMessage(chatId, text);
			} else if (update.getMessage().hasPhoto()) {
				PhotoSize photo = getLargePhoto(message);

				if (photo == null) {
					sendText(chatId, "Photos not found.");
				} else {
					File file = downloadPhoto(photo);
					file.deleteOnExit();

					try {
						OpenersResult openers = worker.writeOpeners(file);

						LOG.info("Image description: {}", openers.getDescription());
						LOG.info("Openers english: {}", openers.getEnglish());
						LOG.info("Openers russian: {}", openers.getRussian());

						sendMessage(chatId, openers.getRussian());
					} catch (OllamaBaseException | IOException | InterruptedException e) {
						LOG.warn("Error processing image.", e);
					}

					file.delete();
				}
			}
		}
	}

	private File downloadPhoto(PhotoSize photo) {
		String fileId = photo.getFileId();
		GetFile getFile = GetFile.builder().fileId(fileId).build();

		try {
			org.telegram.telegrambots.meta.api.objects.File file = telegramClient.execute(getFile);

			return telegramClient.downloadFile(file);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to get file.", e);
		}

		return null;
	}

	private PhotoSize getLargePhoto(Message message) {
		List<PhotoSize> photos = message.getPhoto();
		PhotoSize bestPhoto = null;
		int averageBest = 0;

		for (PhotoSize photo : photos) {
			int averageSize = (photo.getWidth() + photo.getHeight()) / 2;

			if (bestPhoto == null) {
				bestPhoto = photo;
				averageBest = averageSize;
			} else if (averageBest < TARGET_IMAGE_SIZE && averageBest < averageSize) {
				bestPhoto = photo;
				averageBest = averageSize;
			} else if (averageBest > averageSize) {
				bestPhoto = photo;
				averageBest = averageSize;
			}
		}

		return bestPhoto;
	}

	private void sendText(long chatId, String text) {
		SendMessage message = SendMessage.builder().chatId(chatId).text(text).build();

		try {
			telegramClient.execute(message);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to send message.", e);
		}
	}

	private void sendMessage(long chatId, String text) {
		String escaped = text.replace(".", "\\.").replace("#", "\\#").replace("-", "\\-").replace("!", "\\!");
		SendMessage message = SendMessage.builder().chatId(chatId).parseMode("MarkdownV2").text(escaped).build();

		try {
			telegramClient.execute(message);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to send message.", e);
		}
	}

}
