package ru.snake.bot.easydate.database;

import java.io.IOException;

import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;
import org.mapdb.serializer.GroupSerializerObjectArray;

public class OpenerParametersSerializer extends GroupSerializerObjectArray<OpenerParameters> {

	private static final OpenerParametersSerializer INSTANCE = new OpenerParametersSerializer();

	@Override
	public void serialize(DataOutput2 out, OpenerParameters value) throws IOException {
		out.writeUTF(value.getFileId());

		if (value.hasDescription()) {
			out.writeBoolean(true);
			out.writeUTF(value.getDescription());
		} else {
			out.writeBoolean(true);
		}
	}

	@Override
	public OpenerParameters deserialize(DataInput2 input, int available) throws IOException {
		String fileId = input.readUTF();
		boolean hasDescription = input.readBoolean();

		if (hasDescription) {
			String description = input.readUTF();

			return new OpenerParameters(fileId, description);
		} else {
			return new OpenerParameters(fileId);
		}
	}

	@Override
	public boolean isTrusted() {
		return true;
	}

	public static Serializer<OpenerParameters> instance() {
		return INSTANCE;
	}

}
