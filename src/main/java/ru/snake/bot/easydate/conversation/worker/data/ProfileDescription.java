package ru.snake.bot.easydate.conversation.worker.data;

public class ProfileDescription {

	private final String header;

	private final String content;

	public ProfileDescription(final String header, final String content) {
		this.header = header;
		this.content = content;
	}

	public String getHeader() {
		return header;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "ProfileDescription [header=" + header + ", content=" + content + "]";
	}

}
