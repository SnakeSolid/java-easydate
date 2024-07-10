package ru.snake.bot.easydate.consume;

@FunctionalInterface
public interface AccessDeniedAction {

	public void consume(final long chatId, final long userId) throws Exception;

}
