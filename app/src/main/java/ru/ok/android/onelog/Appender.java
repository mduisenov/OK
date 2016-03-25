package ru.ok.android.onelog;

import android.support.annotation.NonNull;
import java.io.Flushable;
import ru.ok.onelog.Item;

public interface Appender extends Flushable {
    void append(@NonNull Item item);

    void flush();
}
