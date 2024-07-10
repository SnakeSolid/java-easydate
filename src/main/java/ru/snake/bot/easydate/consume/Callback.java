package ru.snake.bot.easydate.consume;

@FunctionalInterface
public interface Callback<T> {

	public void call(final T object) throws Exception;

}
