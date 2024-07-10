package ru.snake.bot.easydate.cli;

import java.io.File;
import java.util.Set;

@FunctionalInterface
public interface BotCallback {

	public void execute(final File configFile, final String botToken, final Set<Long> allowUsers);

}
