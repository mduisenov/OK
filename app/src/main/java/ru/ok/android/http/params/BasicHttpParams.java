package ru.ok.android.http.params;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class BasicHttpParams extends AbstractHttpParams implements Serializable, Cloneable {
    private static final long serialVersionUID = -7086398485908701455L;
    private final Map<String, Object> parameters;

    public BasicHttpParams() {
        this.parameters = new ConcurrentHashMap();
    }

    public Object getParameter(String name) {
        return this.parameters.get(name);
    }

    public HttpParams setParameter(String name, Object value) {
        if (name != null) {
            if (value != null) {
                this.parameters.put(name, value);
            } else {
                this.parameters.remove(name);
            }
        }
        return this;
    }

    public HttpParams copy() {
        try {
            return (HttpParams) clone();
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException("Cloning not supported");
        }
    }

    public Object clone() throws CloneNotSupportedException {
        BasicHttpParams clone = (BasicHttpParams) super.clone();
        copyParams(clone);
        return clone;
    }

    public void copyParams(HttpParams target) {
        for (Entry<String, Object> me : this.parameters.entrySet()) {
            target.setParameter((String) me.getKey(), me.getValue());
        }
    }

    public Set<String> getNames() {
        return new HashSet(this.parameters.keySet());
    }
}
