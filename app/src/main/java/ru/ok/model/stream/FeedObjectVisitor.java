package ru.ok.model.stream;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ru.ok.model.stream.entities.BaseEntityBuilder;

public interface FeedObjectVisitor {
    void visit(@Nullable String str, @NonNull Feed feed);

    void visit(@Nullable String str, @NonNull BaseEntityBuilder baseEntityBuilder);
}
