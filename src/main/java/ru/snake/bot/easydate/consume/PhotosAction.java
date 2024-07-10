package ru.snake.bot.easydate.consume;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.PhotoSize;

@FunctionalInterface
public interface PhotosAction {

	public void consume(final long chatId, final long userId, final List<PhotoSize> photos, final String text)
			throws Exception;

}
