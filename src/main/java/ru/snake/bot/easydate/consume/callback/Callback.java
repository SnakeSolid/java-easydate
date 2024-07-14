package ru.snake.bot.easydate.consume.callback;

@FunctionalInterface
public interface Callback<T> {

	public void call(final T object) throws Exception;

}
