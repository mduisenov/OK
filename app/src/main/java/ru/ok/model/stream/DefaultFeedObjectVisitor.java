package ru.ok.model.stream;

import ru.ok.model.stream.entities.BaseEntityBuilder;

public class DefaultFeedObjectVisitor implements FeedObjectVisitor {
    public void visit(String ref, Feed feed) {
    }

    public void visit(String ref, BaseEntityBuilder entity) {
    }
}
