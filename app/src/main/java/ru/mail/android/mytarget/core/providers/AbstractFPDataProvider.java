package ru.mail.android.mytarget.core.providers;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFPDataProvider implements FPDataProvider {
    private Map<String, String> map;

    protected Map<String, String> getMap() {
        return this.map;
    }

    public AbstractFPDataProvider() {
        this.map = new HashMap();
    }

    protected boolean addParam(String name, String value) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        if (value == null) {
            return removeParam(name);
        }
        this.map.put(name, value);
        return true;
    }

    protected boolean removeParam(String name) {
        if (!this.map.containsKey(name)) {
            return false;
        }
        this.map.remove(name);
        return true;
    }

    protected void removeAll() {
        this.map.clear();
    }

    protected String getParam(String name) {
        return (String) this.map.get(name);
    }

    public synchronized Map<String, String> getData() {
        HashMap<String, String> shallowcopy;
        shallowcopy = new HashMap();
        shallowcopy.putAll(this.map);
        return shallowcopy;
    }

    public synchronized void putDataTo(Map<String, String> destination) {
        destination.putAll(this.map);
    }
}
