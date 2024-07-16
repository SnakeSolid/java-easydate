package ru.snake.bot.easydate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import ru.snake.bot.easydate.consume.BotClientConsumer;
import ru.snake.bot.easydate.consume.Context;
import ru.snake.bot.easydate.database.ChatState;
import ru.snake.bot.easydate.database.Database;
import ru.snake.bot.easydate.database.OpenerParameters;
import ru.snake.date.conversation.worker.Worker;
import ru.snake.date.conversation.worker.data.OpenersResult;
import ru.snake.date.conversation.worker.data.ProfileResult;

public class EasyDateBot extends BotClientConsumer implements LongPollingSingleThreadUpdateConsumer {

	private static final String CALLBACK_PROFILE = ":profile";

	private static final String CALLBACK_PROFILE_REDO = ":profile_redo";

	private static final String CALLBACK_OPENER = ":opener";

	private static final String CALLBACK_OPENER_REDO = ":opener_redo";

	private static final String CALLBACK_CONVERSATION = ":conversation";

	private static final Logger LOG = LoggerFactory.getLogger(EasyDateBot.class);

	private static final int TARGET_IMAGE_SIZE = 672;

	private final Database database;

	private final Worker worker;

	public EasyDateBot(final String botToken, final Set<Long> whiteList, final Database database, final Worker worker) {
		super(botToken, whiteList);

		this.database = database;
		this.worker = worker;

		onMessage(this::processText);
		onPhotos(this::processPhotos);
		onPhotos(this::processPhotosDescription);
		onCommand("/start", this::commandStart);
		onCommand("/help", this::commandHelp);
		onCommand(this::commandInvalid);
		onCallback(CALLBACK_PROFILE, this::callbackProfile);
		onCallback(CALLBACK_PROFILE_REDO, this::callbackProfileRedo);
		onCallback(CALLBACK_OPENER, this::callbackOpener);
		onCallback(CALLBACK_OPENER_REDO, this::callbackOpenerRedo);
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
		database.setChatState(context.getChatId(), ChatState.PROFILE_DESCRIPTION);

		sendCallbackAnswer(queryId);
		sendMessage(context.getChatId(), Resource.asText("texts/profile_description.txt"));
	}

	private void callbackProfileRedo(final Context context, final String queryId, final String callback) {
		String text = database.getProfileText(context.getChatId());

		generateProfileDescription(context.getChatId(), text);
	}

	private void callbackOpener(final Context context, final String queryId, final String callback) throws IOException {
		database.setChatState(context.getChatId(), ChatState.GENERATE_OPENER);

		sendCallbackAnswer(queryId);
		sendMessage(context.getChatId(), "Not implemented yet.");
	}

	private void callbackOpenerRedo(final Context context, final String queryId, final String callback) {
		OpenerParameters parameters = database.getChatOpener(context.getChatId());

		if (parameters != null) {
			generateOpeners(context.getChatId(), parameters);
		}
	}

	private void generateOpeners(long chatId, OpenerParameters parameters) {
		PhotoSize photo = PhotoSize.builder().fileId(parameters.getFileId()).build();
		File file = downloadPhoto(photo);

		if (file == null) {
			try {
				sendMessage(chatId, Resource.asText("image_download_fail.txt"), keyboardMenu());
			} catch (IOException e) {
				LOG.warn("Error processing image.", e);
			}

			return;
		}

		file.deleteOnExit();

		try {
			OpenersResult openers;

			if (parameters.hasDescription()) {
				openers = worker.writeOpeners(file, parameters.getDescription());
			} else {
				openers = worker.writeOpeners(file);
			}

			sendMessage(chatId, openers.getRussian(), keyboardOpeners());
		} catch (OllamaBaseException | IOException | InterruptedException e) {
			LOG.warn("Error processing image.", e);
		}

		file.delete();
	}

	private void callbackConversation(final Context context, final String queryId, final String callback)
			throws IOException {
		database.setChatState(context.getChatId(), ChatState.CONTINUE_CONVERSATION);

		sendCallbackAnswer(queryId);
		sendMessage(context.getChatId(), "Not implemented yet.");
	}

	private void callbackInvalid(final Context context, final String queryId, final String callback)
			throws IOException {
		LOG.warn("Unknown callback action: {}", callback);

		sendCallbackAnswer(queryId);
	}

	private void commandStart(final Context context, final String command) throws IOException {
		sendMessage(context.getChatId(), Resource.asText("texts/command_start.txt"), keyboardMenu());
	}

	private void commandHelp(final Context context, final String command) throws IOException {
		sendMessage(context.getChatId(), Resource.asText("texts/command_help.txt"));
	}

	private void commandInvalid(final Context context, final String command) throws IOException {
		LOG.warn("Unknown bot command: {}", command);
	}

	private void processText(final Context context, final String text) throws Exception {
		if (database.getChatState(context.getChatId()) == ChatState.PROFILE_DESCRIPTION) {
			database.setProfileText(context.getChatId(), text);

			generateProfileDescription(context.getChatId(), text);
		} else {
			sendMessage(context.getChatId(), "Not implemented yet.");
		}
	}

	private void generateProfileDescription(long chatId, String text) {
		try {
			ProfileResult result = worker.profileDescription(text);

			sendMessage(chatId, result.asString(), keyboardProfile());
		} catch (OllamaBaseException | IOException | InterruptedException e) {
			LOG.warn("Error processing image.", e);
		}
	}

	private void processPhotos(final Context context, final List<PhotoSize> photos) throws Exception {
		PhotoSize photo = getLargestPhoto(photos);
		OpenerParameters parameters = new OpenerParameters(photo.getFileId(), null);
		database.setChatOpener(context.getChatId(), parameters);

		generateOpeners(context.getChatId(), parameters);
	}

	private void processPhotosDescription(final Context context, final List<PhotoSize> photos, final String description)
			throws Exception {
		PhotoSize photo = getLargestPhoto(photos);
		OpenerParameters parameters = new OpenerParameters(photo.getFileId(), description);
		database.setChatOpener(context.getChatId(), parameters);

		generateOpeners(context.getChatId(), parameters);
	}

	private ReplyKeyboard keyboardOpeners() {
		InlineKeyboardRow rowOne = new InlineKeyboardRow();
		rowOne.add(
			InlineKeyboardButton.builder().text("Придумать еще оупенеров").callbackData(CALLBACK_OPENER_REDO).build()
		);

		InlineKeyboardRow rowTwo = new InlineKeyboardRow();
		rowTwo.add(InlineKeyboardButton.builder().text("Профиль").callbackData(CALLBACK_PROFILE).build());
		rowTwo.add(InlineKeyboardButton.builder().text("Опенер").callbackData(CALLBACK_OPENER).build());
		rowTwo.add(InlineKeyboardButton.builder().text("Диалог").callbackData(CALLBACK_CONVERSATION).build());

		ReplyKeyboard keyboard = InlineKeyboardMarkup.builder().keyboardRow(rowOne).keyboardRow(rowTwo).build();

		return keyboard;
	}

	private ReplyKeyboard keyboardProfile() {
		InlineKeyboardRow rowOne = new InlineKeyboardRow();
		rowOne.add(
			InlineKeyboardButton.builder().text("Предложить еще варианты").callbackData(CALLBACK_PROFILE_REDO).build()
		);

		InlineKeyboardRow rowTwo = new InlineKeyboardRow();
		rowTwo.add(InlineKeyboardButton.builder().text("Профиль").callbackData(CALLBACK_PROFILE).build());
		rowTwo.add(InlineKeyboardButton.builder().text("Опенер").callbackData(CALLBACK_OPENER).build());
		rowTwo.add(InlineKeyboardButton.builder().text("Диалог").callbackData(CALLBACK_CONVERSATION).build());

		ReplyKeyboard keyboard = InlineKeyboardMarkup.builder().keyboardRow(rowOne).keyboardRow(rowTwo).build();

		return keyboard;
	}

	private ReplyKeyboard keyboardMenu() {
		InlineKeyboardRow row = new InlineKeyboardRow();
		row.add(InlineKeyboardButton.builder().text("Профиль").callbackData(CALLBACK_PROFILE).build());
		row.add(InlineKeyboardButton.builder().text("Опенер").callbackData(CALLBACK_OPENER).build());
		row.add(InlineKeyboardButton.builder().text("Диалог").callbackData(CALLBACK_CONVERSATION).build());

		ReplyKeyboard keyboard = InlineKeyboardMarkup.builder().keyboardRow(row).build();

		return keyboard;
	}

	private static PhotoSize getLargestPhoto(final List<PhotoSize> photos) {
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

}
