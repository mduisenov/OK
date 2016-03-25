package ru.ok.model.stream;

import java.util.List;
import java.util.Map;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.entities.BaseEntity;

public final class Utils {
    public static <TEntity extends BaseEntity> void resolveRefs(Map<String, BaseEntity> resolvedEntities, List<String> refs, List<TEntity> outEntities, Class<TEntity> entityClass) throws EntityRefNotResolvedException {
        for (String ref : refs) {
            BaseEntity entity = (BaseEntity) resolvedEntities.get(ref);
            if (entity == null) {
                Logger.m185w("Entity not resolved: %s", ref);
            } else if (entityClass.isInstance(entity)) {
                outEntities.add(entity);
            } else {
                Logger.m185w("Unexpected entity type for ref=%s: %s", ref, entity);
            }
        }
    }
}
