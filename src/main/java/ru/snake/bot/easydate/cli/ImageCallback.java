package ru.snake.bot.easydate.cli;

import java.io.File;

@FunctionalInterface
public interface ImageCallback {

	public void execute(final File image, final String description);

}
