package ru.ok.model.stream;

import android.support.annotation.NonNull;
import java.util.Map;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.BaseEntityBuilder;

public class FeedEntitiesAccumulator extends DefaultFeedObjectVisitor {
    @NonNull
    private final Map<String, BaseEntity> allEntities;
    @NonNull
    private final Map<String, BaseEntity> outEntities;

    public FeedEntitiesAccumulator(Map<String, BaseEntity> allEntities, Map<String, BaseEntity> outEntities) {
        this.allEntities = allEntities;
        this.outEntities = outEntities;
    }

    public void visit(String ref, BaseEntityBuilder entityBuilder) {
        BaseEntity entity = (BaseEntity) this.allEntities.get(ref);
        if (entity != null) {
            this.outEntities.put(ref, entity);
            return;
        }
        Logger.m185w("Entity not resolved: %s", ref);
    }
}
