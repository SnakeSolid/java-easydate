package ru.snake.bot.easydate.consume.callback;

import ru.snake.bot.easydate.consume.Context;

@FunctionalInterface
public interface CommandAction {

	public void consume(final Context context, final String command) throws Exception;

}
