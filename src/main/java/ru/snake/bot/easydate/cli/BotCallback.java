package ru.snake.bot.easydate.cli;

import java.util.Set;

@FunctionalInterface
public interface BotCallback {

	public void execute(final String botToken, final Set<Long> allowUsers);

}
