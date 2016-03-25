package ru.ok.model.stream;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

public interface FeedObject {
    void accept(@Nullable String str, @NonNull FeedObjectVisitor feedObjectVisitor);

    void getRefs(@NonNull List<String> list);
}
