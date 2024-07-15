package ru.snake.bot.easydate.database;

import java.io.File;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public interface Database {

	public void setState(final long chatId, final ChatState state);

	public ChatState getState(final long chatId);

	public static Database onDisk(File databasePath) {
		DB db = DBMaker.fileDB(databasePath).make();

		return MapDBDatabase.from(db);
	}

	public static Database inMemory() {
		DB db = DBMaker.memoryDB().make();

		return MapDBDatabase.from(db);
	}

}
