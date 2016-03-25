package ru.ok.model.stream.entities;

import java.util.List;
import ru.ok.model.stream.FeedObjectException;

public final class FeedHolidayEntityBuilder extends BaseEntityBuilder<FeedHolidayEntityBuilder, FeedHolidayEntity> {
    public FeedHolidayEntityBuilder() {
        super(23);
    }

    protected FeedHolidayEntity doPreBuild() throws FeedObjectException {
        return new FeedHolidayEntity(getId());
    }

    public void getRefs(List<String> list) {
    }
}
