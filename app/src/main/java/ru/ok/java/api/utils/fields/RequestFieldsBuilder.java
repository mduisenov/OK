package ru.ok.java.api.utils.fields;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RequestFieldsBuilder {
    private Set<RequestField> fields;
    private CharSequence prefix;

    public RequestFieldsBuilder() {
        this.fields = new HashSet();
    }

    public final RequestFieldsBuilder addField(RequestField requestField) {
        this.fields.add(requestField);
        return this;
    }

    public final RequestFieldsBuilder addFields(RequestField... requestFields) {
        Collections.addAll(this.fields, requestFields);
        return this;
    }

    public final RequestFieldsBuilder withPrefix(CharSequence prefix) {
        this.prefix = prefix;
        return this;
    }

    public String build() {
        if (this.fields.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (RequestField requestField : this.fields) {
            if (sb.length() != 0) {
                sb.append(",");
            }
            if (this.prefix != null) {
                sb.append(this.prefix);
            }
            sb.append(requestField.getName());
        }
        return sb.toString();
    }

    public void clear() {
        this.fields.clear();
        this.prefix = null;
    }

    public String toString() {
        return build();
    }
}
