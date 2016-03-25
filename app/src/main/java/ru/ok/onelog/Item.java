package ru.ok.onelog;

import java.util.List;
import java.util.Map;

public interface Item {
    String collector();

    int count();

    Map<String, String> custom();

    List<String> data();

    List<String> groups();

    String operation();

    long time();

    int type();
}
