package ru.ok.model.stream.entities;

import java.io.IOException;
import ru.ok.android.storage.serializer.SimpleSerialException;
import ru.ok.android.storage.serializer.SimpleSerialInputStream;
import ru.ok.android.storage.serializer.SimpleSerialOutputStream;
import ru.ok.model.stream.ActionCountInfo;
import ru.ok.model.stream.entities.FeedPollEntity.Answer;

public final class AnswerSerializer {
    public static void write(SimpleSerialOutputStream out, Answer answer) throws IOException {
        out.writeInt(1);
        out.writeString(answer.id);
        out.writeString(answer.text);
        out.writeObject(answer.voteInfo);
    }

    public static Answer read(SimpleSerialInputStream in) throws IOException {
        int version = in.readInt();
        if (version == 1) {
            return new Answer(in.readString(), in.readString(), (ActionCountInfo) in.readObject());
        }
        throw new SimpleSerialException("Unsupported serial version: " + version);
    }
}
