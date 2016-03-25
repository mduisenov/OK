package ru.ok.model.stream;

public class EntityRefNotResolvedException extends FeedObjectException {
    private String ref;

    public EntityRefNotResolvedException(String ref) {
        super("Entity reference not resolved: " + ref);
        this.ref = ref;
    }

    public EntityRefNotResolvedException(String ref, String details) {
        super("Entity reference not resolved: " + ref + ", " + details);
        this.ref = ref;
    }
}
