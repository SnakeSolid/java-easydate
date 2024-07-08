package ru.snake.bot.easydate;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Settings {

	private final String botToken;

	private final Set<Long> allowUsers;

	private final File imagePath;

	public Settings(final String botToken, final Set<Long> allowUsers, final File imagePath) {
		this.botToken = botToken;
		this.allowUsers = allowUsers;
		this.imagePath = imagePath;
	}

	public String getBotToken() {
		return botToken;
	}

	public Set<Long> getAllowUsers() {
		return allowUsers;
	}

	public File getImagePath() {
		return imagePath;
	}

	@Override
	public String toString() {
		return "Settings [botToken=" + botToken + ", allowUsers=" + allowUsers + ", imagePath=" + imagePath + "]";
	}

	public static Settings parse(String[] args) {
		Options options = new Options();
		Option tokenOption = Option.builder("t")
			.longOpt("bot-token")
			.desc("Telegram bot access token")
			.hasArg()
			.argName("TOKEN")
			.type(String.class)
			.build();
		Option allowOption = Option.builder("a")
			.longOpt("allow-user")
			.desc("Allow access to user")
			.hasArg()
			.argName("ID")
			.type(Long.class)
			.build();
		Option imageOption = Option.builder("i")
			.longOpt("image")
			.desc("Path to image (write openers and exit)")
			.hasArg()
			.argName("IMAGE")
			.type(File.class)
			.build();
		options.addOption(tokenOption);
		options.addOption(allowOption);
		options.addOption(imageOption);

		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine;
		File imagePath;

		try {
			commandLine = parser.parse(options, args);
			imagePath = (File) commandLine.getParsedOptionValue(imageOption);
		} catch (ParseException e) {
			System.err.println(e.getMessage());

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("remotecontrol", options);

			return null;
		}

		String botToken = commandLine.getOptionValue(tokenOption);
		Set<Long> allowUsers = new HashSet<>();

		if (commandLine.hasOption(allowOption)) {
			for (String userId : commandLine.getOptionValues(allowOption)) {
				allowUsers.add(Long.parseLong(userId));
			}
		}

		return new Settings(botToken, allowUsers, imagePath);
	}

}
