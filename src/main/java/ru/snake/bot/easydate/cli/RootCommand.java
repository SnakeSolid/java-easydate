package ru.snake.bot.easydate.cli;

import picocli.CommandLine.Command;

@Command(
	name = "easydate",
	mixinStandardHelpOptions = true,
	version = "0.0.1",
	description = "Suggest openers and continue dialogs."
)
public class RootCommand {
}
