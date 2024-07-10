package ru.snake.bot.easydate.cli;

import java.io.File;

@FunctionalInterface
public interface ImageCallback {

	public void execute(final File configFile, final File image, final String description);

}
