package ru.ok.android.fragments.web;

import java.util.ArrayList;
import java.util.List;

public class JSFunction {
    private String hook;
    private String name;
    private List<String> params;

    public JSFunction(String hook, String name) {
        this.params = new ArrayList();
        this.hook = hook;
        this.name = name;
    }

    public void addParam(String param) {
        this.params.add(param);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("javascript:");
        builder.append("window").append('.');
        builder.append(this.hook).append('.');
        builder.append(this.name);
        builder.append('(').append(getParamsString()).append(')');
        return builder.toString();
    }

    private String getParamsString() {
        if (this.params.size() == 0) {
            return "null";
        }
        StringBuilder builder = new StringBuilder();
        for (String param : this.params) {
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append(param);
        }
        return builder.toString();
    }
}
