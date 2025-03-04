package ru.snake.bot.easydate.consume;

import java.io.File;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.snake.bot.easydate.conversation.text.Escaper;

public class BotClientConsumer extends UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(BotClientConsumer.class);

	private final OkHttpTelegramClient telegramClient;

	public BotClientConsumer(String botToken, Set<Long> whiteList) {
		super(whiteList);

		this.telegramClient = new OkHttpTelegramClient(botToken);
	}

	protected File downloadPhoto(PhotoSize photo) {
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

	protected void sendMessage(long chatId, String text) {
		SendMessage message = SendMessage.builder()
			.chatId(chatId)
			.parseMode(ParseMode.MARKDOWNV2)
			.text(Escaper.escapeMarkdown(text))
			.build();

		try {
			telegramClient.execute(message);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to send message.", e);
		}
	}

	protected void sendMessage(long chatId, final String text, final ReplyKeyboard keyboard) {
		SendMessage message = SendMessage.builder()
			.chatId(chatId)
			.parseMode(ParseMode.MARKDOWNV2)
			.text(Escaper.escapeMarkdown(text))
			.replyMarkup(keyboard)
			.build();

		try {
			telegramClient.execute(message);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to send message.", e);
		}
	}

	protected void sendCallbackAnswer(String queryId) {
		AnswerCallbackQuery answer = AnswerCallbackQuery.builder().callbackQueryId(queryId).build();

		try {
			telegramClient.execute(answer);
		} catch (TelegramApiException e) {
			LOG.warn("Failed to send answer.", e);
		}
	}

	@Override
	public String toString() {
		return "BotClientConsumer [telegramClient=" + telegramClient + "]";
	}

}
