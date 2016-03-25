package ru.ok.model.stream;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import ru.ok.android.utils.Logger;
import ru.ok.model.stream.entities.BaseEntity;
import ru.ok.model.stream.entities.BaseEntityBuilder;

public class EntityReferenceResolver {
    public static Map<String, BaseEntity> resolveEntityRefs(Map<String, BaseEntityBuilder> entityBuilders) {
        HashMap<String, BaseEntity> resolvedEntities = new HashMap();
        for (Entry<String, BaseEntityBuilder> entry : entityBuilders.entrySet()) {
            BaseEntityBuilder entityBuilder = (BaseEntityBuilder) entry.getValue();
            if (entityBuilder == null) {
                Logger.m184w("Null value");
            } else {
                try {
                    resolvedEntities.put(entry.getKey(), entityBuilder.preBuild());
                } catch (FeedObjectException e) {
                    Logger.m187w(e, "Failed to build entity object: %s", e);
                }
            }
        }
        for (Entry<String, BaseEntityBuilder> entry2 : entityBuilders.entrySet()) {
            try {
                ((BaseEntityBuilder) entry2.getValue()).build(resolvedEntities);
            } catch (Exception e2) {
                Logger.m187w(e2, "Failed to parse entity: %s", e2);
            }
        }
        return resolvedEntities;
    }
}
