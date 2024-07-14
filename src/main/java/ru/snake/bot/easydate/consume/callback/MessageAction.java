package ru.snake.bot.easydate.consume.callback;

import ru.snake.bot.easydate.consume.Context;

@FunctionalInterface
public interface MessageAction {

	public void consume(final Context context, final String text) throws Exception;

}
