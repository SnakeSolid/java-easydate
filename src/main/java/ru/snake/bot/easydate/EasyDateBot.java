package ru.snake.bot.easydate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import ru.snake.bot.easydate.consume.Context;
import ru.snake.bot.easydate.consume.UpdateConsumer;
import ru.snake.date.conversation.worker.OpenersResult;
import ru.snake.date.conversation.worker.Worker;

public class EasyDateBot extends UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

	private static final String CALLBACK_PROFILE = ":profile";

	private static final String CALLBACK_OPENER = ":opener";

	private static final String CALLBACK_CONVERSATION = ":conversation";

	private static final Logger LOG = LoggerFactory.getLogger(EasyDateBot.class);

	private static final int TARGET_IMAGE_SIZE = 672;

	private final TelegramClient telegramClient;

	private final Worker worker;

	public EasyDateBot(final String botToken, final Set<Long> whiteList, final Worker worker) {
		super(whiteList);

		this.telegramClient = new OkHttpTelegramClient(botToken);
		this.worker = worker;

		onMessage(this::processText);
		onPhotos(this::processPhotos);
		onPhotos(this::processPhotosDescription);
		onCommand("/start", this::commandStart);
		onCommand("/help", this::commandHelp);
		onCommand(this::commandInvalid);
		onCallback(CALLBACK_PROFILE, this::callbackProfile);
		onCallback(CALLBACK_OPENER, this::callbackOpener);
		onCallback(CALLBACK_CONVERSATION, this::callbackConversation);
		onCallback(this::callbackInvalid);
		onAccessDenied(this::accessDenied);
	}

	private void accessDenied(final Context context) throws IOException {
		sendMessage(
			context.getChatId(),
			Replacer.replace("Access denied for user ID = {user_id}.", Map.of("user_id", context.getUserId()))
		);
	}

	private void callbackProfile(final Context context, final String queryId, final String callback)
			throws IOException {
		sendAnswer(queryId);
		sendMessage(context.getChatId(), callback);
	}

	private void callbackOpener(final Context context, final String queryId, final String callback) throws IOException {
		sendAnswer(queryId);
		sendMessage(context.getChatId(), callback);
	}

	private void callbackConversation(final Context context, final String queryId, final String callback)
			throws IOException {
		sendAnswer(queryId);
		sendMessage(context.getChatId(), callback);
	}

	private void callbackInvalid(final Context context, final String queryId, final String callback)
			throws IOException {
		LOG.warn("Unknown callback action: {}", callback);

		sendAnswer(queryId);
	}

	private void commandStart(final Context context, final String command) throws IOException {
		InlineKeyboardRow actionsRow = new InlineKeyboardRow();
		actionsRow.add(InlineKeyboardButton.builder().text("Профиль").callbackData(CALLBACK_PROFILE).build());
		actionsRow.add(InlineKeyboardButton.builder().text("Опенер").callbackData(CALLBACK_OPENER).build());
		actionsRow.add(InlineKeyboardButton.builder().text("Диалог").callbackData(CALLBACK_CONVERSATION).build());

		ReplyKeyboard keyboard = InlineKeyboardMarkup.builder().keyboardRow(actionsRow).build();

		sendMessage(context.getChatId(), Resource.asText("texts/command-start.txt"), keyboard);
	}

	private void commandHelp(final Context context, final String command) throws IOException {
		sendMessage(context.getChatId(), Resource.asText("texts/command-help.txt"));
	}

	private void commandInvalid(final Context context, final String command) throws IOException {
		LOG.warn("Unknown bot command: {}", command);
	}

	private void processText(final Context context, final String text) throws Exception {
		sendMessage(context.getChatId(), "Not implemented yet.");
	}

	private void processPhotos(final Context context, final List<PhotoSize> photos) throws Exception {
		PhotoSize photo = getLargePhoto(photos);
		File file = downloadPhoto(photo);
		file.deleteOnExit();

		try {
			OpenersResult openers = worker.writeOpeners(file);

			LOG.info("Image description: {}", openers.getDescription());
			LOG.info("Image objects: {}", openers.getObjects());
			LOG.info("Openers english: {}", openers.getEnglish());
			LOG.info("Openers russian: {}", openers.getRussian());

			sendMessage(context.getChatId(), openers.getRussian());
		} catch (OllamaBaseException | IOException | InterruptedException e) {
			LOG.warn("Error processing image.", e);
		}

		file.delete();
	}

	private void processPhotosDescription(final Context context, final List<PhotoSize> photos, final String description)
			throws Exception {
		PhotoSize photo = getLargePhoto(photos);
		File file = downloadPhoto(photo);
		file.deleteOnExit();

		try {
			OpenersResult openers = worker.writeOpeners(file, description);

			LOG.info("Image description: {}", openers.getDescription());
			LOG.info("Image objects: {}", openers.getObjects());
			LOG.info("Openers english: {}", openers.getEnglish());
			LOG.info("Openers russian: {}", openers.getRussian());

			sendMessage(context.getChatId(), openers.getRussian());
		} catch (OllamaBaseException | IOException | InterruptedException e) {
			LOG.warn("Error processing image.", e);
		}

		file.delete();
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

	private static PhotoSize getLargePhoto(final List<PhotoSize> photos) {
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

	private void sendMessage(long chatId, String text) {
		SendMessage message = SendMessage.builder()
			.chatId(chatId)
			.parseMode(ParseMode.MARKDOWNV2)
			.text(escape(text))
			.build();

		try {
			telegramClient.execute(message);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to send message.", e);
		}
	}

	private void sendMessage(long chatId, String text, final ReplyKeyboard keyboard) {
		SendMessage message = SendMessage.builder()
			.chatId(chatId)
			.parseMode(ParseMode.MARKDOWNV2)
			.text(escape(text))
			.replyMarkup(keyboard)
			.build();

		try {
			telegramClient.execute(message);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to send message.", e);
		}
	}

	private void sendAnswer(String queryId) {
		AnswerCallbackQuery answer = AnswerCallbackQuery.builder().callbackQueryId(queryId).build();

		try {
			telegramClient.execute(answer);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to send answer.", e);
		}
	}

	private static String escape(final String value) {
		StringBuilder builder = new StringBuilder();

		for (char ch : value.toCharArray()) {
			switch (ch) {
			case '.':
				builder.append("\\.");
				break;

			case '#':
				builder.append("\\#");
				break;

			case '-':
				builder.append("\\-");
				break;

			case '!':
				builder.append("\\!");
				break;

			default:
				builder.append(ch);
				break;
			}
		}

		return builder.toString();
	}

}
