package ru.snake.bot.easydate.consume;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public class UpdateConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(UpdateConsumer.class);

	private final Set<Long> whiteList;

	private final Map<String, CommandAction> commands;

	private AccessDeniedAction accessDeniedAction;

	private TextAction textAction;

	private PhotosAction photosAction;

	public UpdateConsumer(final Set<Long> whiteList) {
		this.whiteList = whiteList;
		this.commands = new HashMap<>();
		this.accessDeniedAction = null;
		this.textAction = null;
		this.photosAction = null;
	}

	public UpdateConsumer onCommand(final String command, final CommandAction callback) {
		commands.put(command, callback);

		return this;
	}

	public UpdateConsumer onText(final TextAction callback) {
		textAction = callback;

		return this;
	}

	public UpdateConsumer onPhotos(final PhotosAction callback) {
		photosAction = callback;

		return this;
	}

	public void consume(Update update) {
		if (update.hasMessage()) {
			Message message = update.getMessage();

			consumeMessage(message);
		} else if (update.hasEditedMessage()) {
			Message message = update.getEditedMessage();

			consumeMessage(message);
		}
	}

	private void consumeMessage(Message message) {
		long userId = message.getFrom().getId();
		long chatId = message.getChatId();

		if (!whiteList.contains(userId)) {
			consume(accessDeniedAction, action -> action.consume(chatId, userId));

			LOG.warn("Access denied for user ID = {}.", userId);

			return;
		}

		List<MessageEntity> entities = get(message, Message::hasEntities, Message::getEntities);
		String text = get(message, Message::hasText, Message::getText);
		List<PhotoSize> photos = get(message, Message::hasPhoto, Message::getPhoto);
		List<MessageEntity> botCommands = getBotCommands(entities);

		if (!botCommands.isEmpty()) {
			for (MessageEntity entity : botCommands) {
				String botCommand = entity.getText();

				consume(commands.get(botCommand), command -> command.consume(chatId, userId, botCommand));
			}
		} else if (photos != null && text != null) {
			consume(photosAction, action -> action.consume(chatId, userId, photos, text));
		} else if (photos != null) {
			consume(photosAction, action -> action.consume(chatId, userId, photos, text));
		} else if (text != null) {
			consume(textAction, action -> action.consume(chatId, userId, text));
		}
	}

	private List<MessageEntity> getBotCommands(List<MessageEntity> entities) {
		if (entities == null || entities.isEmpty()) {
			return Collections.emptyList();
		}

		List<MessageEntity> result = new ArrayList<>();

		for (MessageEntity entity : entities) {
			if (Objects.equals(EntityType.BOTCOMMAND, entity.getType())) {
				result.add(entity);
			}
		}

		return result;
	}

	private static <T> void consume(T object, Callback<T> action) {
		if (object != null) {
			try {
				action.call(object);
			} catch (Exception e) {
				LOG.error("Failed to process request.", e);
			} catch (Error e) {
				LOG.error("Failed to process request.", e);

				// Required to stop application if runtime error occurred.
				System.exit(0);
			}
		}
	}

	private static <T> T get(Message message, Predicate<Message> predicate, Function<Message, T> mapper) {
		if (predicate.test(message)) {
			return mapper.apply(message);
		}

		return null;
	}

	@Override
	public String toString() {
		return "UpdateConsumer [whiteList=" + whiteList + ", commands=" + commands + ", accessDeniedAction="
				+ accessDeniedAction + ", textAction=" + textAction + ", photosAction=" + photosAction + "]";
	}

	public static UpdateConsumer create(final Set<Long> whiteList) {
		return new UpdateConsumer(whiteList);
	}

}