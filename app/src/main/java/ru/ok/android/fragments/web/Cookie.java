package ru.ok.android.fragments.web;

public class Cookie {
    String domain;
    String name;
    String value;

    public Cookie(String domain, String name, String value) {
        this.domain = domain;
        this.name = name;
        this.value = value;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }
}
