package ru.snake.bot.easydate.consume.callback;

import ru.snake.bot.easydate.consume.Context;

@FunctionalInterface
public interface CallbackAction {

	public void consume(final Context context, final String queryId, final String command) throws Exception;

}
