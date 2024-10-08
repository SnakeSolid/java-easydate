package ru.snake.bot.easydate.cli;

import java.io.File;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "image", description = "Suggest openers by image and profile description")
public class ImageCommand implements Runnable {

	private final ImageCallback callback;

	@Option(names = { "-c", "--config" }, description = "LLM configuration file")
	private File config;

	@Option(names = { "-i", "--image" }, description = "Path to image (write openers and exit)", required = true)
	private File image;

	@Option(names = { "-d", "--description" }, description = "Profile description (optional)")
	private String description;

	public ImageCommand(ImageCallback callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		callback.execute(config, image, description);
	}

	@Override
	public String toString() {
		return "ImageCommand [callback=" + callback + ", config=" + config + ", image=" + image + ", description="
				+ description + "]";
	}

}
