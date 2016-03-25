package ru.ok.model.events;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.PostProcessor;
import io.gsonfire.TypeSelector;

public final class EventsGsonUtils {
    public static final Gson gson;

    /* renamed from: ru.ok.model.events.EventsGsonUtils.1 */
    static class C15231 implements PostProcessor<OdnkEvent> {
        C15231() {
        }

        public void postDeserialize(OdnkEvent odnkEvent, JsonElement jsonElement, Gson gson) {
        }

        public void postSerialize(JsonElement jsonElement, OdnkEvent odnkEvent, Gson gson) {
            if (jsonElement.isJsonObject()) {
                String eventClass;
                if (odnkEvent instanceof DiscussionOdklEvent) {
                    eventClass = "Discussions";
                } else {
                    eventClass = "Default";
                }
                jsonElement.getAsJsonObject().addProperty("class", eventClass);
            }
        }
    }

    /* renamed from: ru.ok.model.events.EventsGsonUtils.2 */
    static class C15242 implements TypeSelector<OdnkEvent> {
        C15242() {
        }

        public Class<? extends OdnkEvent> getClassForElement(JsonElement jsonElement) {
            if (jsonElement.isJsonObject()) {
                JsonElement classElement = jsonElement.getAsJsonObject().get("class");
                if (classElement != null && classElement.isJsonPrimitive()) {
                    if ("Discussions".equals(classElement.getAsString())) {
                        return DiscussionOdklEvent.class;
                    }
                }
            }
            return OdnkEvent.class;
        }
    }

    static {
        gson = createGson();
    }

    private static Gson createGson() {
        GsonFireBuilder gson = new GsonFireBuilder();
        gson.registerPostProcessor(OdnkEvent.class, new C15231());
        gson.registerTypeSelector(OdnkEvent.class, new C15242());
        return gson.createGson();
    }
}
