package ru.snake.bot.easydate.database;

public enum ChatState {

	/**
	 * No state set for chat.
	 */
	DEFAULT,

	/**
	 * Wait use story to create profile description.
	 */
	PROFILE_DESCRIPTION,

	/**
	 * Generate openers using profile photo and description.
	 */
	GENERATE_OPENER,

	/**
	 * Suggest phrases to continue dialog.
	 */
	CONTINUE_CONVERSATION,

}
