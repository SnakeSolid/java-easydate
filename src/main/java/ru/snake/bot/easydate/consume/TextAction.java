package ru.snake.bot.easydate.consume;

@FunctionalInterface
public interface TextAction {

	public void consume(final long chatId, final long userId, final String text) throws Exception;

}
