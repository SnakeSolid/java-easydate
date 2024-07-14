package ru.snake.bot.easydate.consume.callback;

import ru.snake.bot.easydate.consume.Context;

@FunctionalInterface
public interface AccessDeniedAction {

	public void consume(final Context context) throws Exception;

}
