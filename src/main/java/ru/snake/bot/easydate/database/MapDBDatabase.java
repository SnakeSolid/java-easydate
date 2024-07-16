package ru.snake.bot.easydate.database;

import java.util.Map;

import org.mapdb.DB;
import org.mapdb.Serializer;

public class MapDBDatabase implements Database {

	private final Map<Long, ChatState> chatStates;

	private final Map<Long, String> profileTexts;

	private final Map<Long, OpenerParameters> openerParameters;

	private final Map<Long, String> converations;

	private MapDBDatabase(
		final Map<Long, ChatState> chatStates,
		final Map<Long, String> profileTexts,
		final Map<Long, OpenerParameters> openerParameters,
		final Map<Long, String> converations
	) {
		this.chatStates = chatStates;
		this.profileTexts = profileTexts;
		this.openerParameters = openerParameters;
		this.converations = converations;
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
	public void setChatOpener(final long chatId, final OpenerParameters parameters) {
		openerParameters.put(chatId, parameters);
	}

	@Override
	public OpenerParameters getChatOpener(final long chatId) {
		return openerParameters.get(chatId);
	}

	public void setConversation(final long chatId, final String text) {
		converations.put(chatId, text);
	}

	public String getConversation(final long chatId) {
		return converations.getOrDefault(chatId, "");
	}

	@Override
	public String toString() {
		return "MapDBDatabase [chatStates=" + chatStates + ", profileTexts=" + profileTexts + ", openerParameters="
				+ openerParameters + ", converations=" + converations + "]";
	}

	public static Database from(DB db) {
		Map<Long, ChatState> chatStates = db.hashMap("chatStates", Serializer.LONG, ChatStateSerializer.instance())
			.createOrOpen();
		Map<Long, String> profileTexts = db.hashMap("profileTexts", Serializer.LONG, Serializer.STRING).createOrOpen();
		Map<Long, OpenerParameters> openerParameters = db
			.hashMap("openerParameters", Serializer.LONG, OpenerParametersSerializer.instance())
			.createOrOpen();
		Map<Long, String> converations = db.hashMap("converations", Serializer.LONG, Serializer.STRING).createOrOpen();

		return new MapDBDatabase(chatStates, profileTexts, openerParameters, converations);
	}

}
