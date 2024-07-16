package ru.snake.bot.easydate.database;

import java.util.Map;

import org.mapdb.DB;
import org.mapdb.Serializer;

public class MapDBDatabase implements Database {

	private final Map<Long, ChatState> chatStates;

	private final Map<Long, String> profileTexts;

	private MapDBDatabase(final Map<Long, ChatState> chatStates, final Map<Long, String> profileTexts) {
		this.chatStates = chatStates;
		this.profileTexts = profileTexts;
	}

	@Override
	public void setChatState(long chatId, ChatState state) {
		chatStates.put(chatId, state);
	}

	@Override
	public ChatState getChatState(long chatId) {
		return chatStates.getOrDefault(chatId, ChatState.DEFAULT);
	}

	@Override
	public void setProfileText(long chatId, String text) {
		profileTexts.put(chatId, text);
	}

	@Override
	public String getProfileText(long chatId) {
		return profileTexts.getOrDefault(chatId, "");
	}

	@Override
	public String toString() {
		return "MapDBDatabase [chatStates=" + chatStates + ", profileTexts=" + profileTexts + "]";
	}

	public static Database from(DB db) {
		Map<Long, ChatState> chatStates = db.hashMap("chatStates", Serializer.LONG, ChatStateSerializer.instance())
			.createOrOpen();
		Map<Long, String> profileTexts = db.hashMap("profileTexts", Serializer.LONG, Serializer.STRING).createOrOpen();

		return new MapDBDatabase(chatStates, profileTexts);
	}

}
