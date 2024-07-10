package ru.snake.bot.easydate.cli;

import java.util.Set;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "bot", description = "Start Telegram bot")
public class BotCommand implements Runnable {

	private final BotCallback callback;

	@Option(names = { "-t", "--bot-token" }, description = "Telegram bot access token", required = true)
	private String botToken;

	@Option(names = { "-u", "--allow-user" }, description = "Allow access to user", required = true)
	private Set<Long> allowUsers;

	public BotCommand(final BotCallback callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		callback.execute(botToken, allowUsers);
	}

	@Override
	public String toString() {
		return "BotCommand [callback=" + callback + ", botToken=" + botToken + ", allowUsers=" + allowUsers + "]";
	}

}