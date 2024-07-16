package ru.snake.bot.easydate.database;

import java.io.File;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public interface Database {

	public void setChatState(final long chatId, final ChatState state);

	public ChatState getChatState(final long chatId);

	public void setProfileText(final long chatId, final String text);

	public String getProfileText(final long chatId);

	public void setChatOpener(final long chatId, final OpenerParameters parameters);

	public OpenerParameters getChatOpener(final long chatId);

	public void setConversation(final long chatId, final String text);

	public String getConversation(final long chatId);

	public static Database onDisk(File databasePath) {
		DB db = DBMaker.fileDB(databasePath).make();

		return MapDBDatabase.from(db);
	}

	public static Database inMemory() {
		DB db = DBMaker.memoryDB().make();

		return MapDBDatabase.from(db);
	}

}
