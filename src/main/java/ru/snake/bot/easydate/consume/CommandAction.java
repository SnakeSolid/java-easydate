package ru.snake.bot.easydate.consume;

@FunctionalInterface
public interface CommandAction {

	public void consume(final long chatId, final long userId, final String command) throws Exception;

}
