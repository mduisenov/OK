package ru.ok.model.stream.banner;

import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

public interface StatPixelHolder {
    void addStatPixel(int i, String str);

    void addStatPixels(int i, Collection<String> collection);

    @Nullable
    ArrayList<String> getStatPixels(int i);
}
