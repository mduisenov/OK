package ru.ok.android.ui.custom.photo;

import ru.ok.android.fresco.UriProvider;

public interface PhotoScaleDataProvider extends UriProvider {
    int getDisplayedHeight();

    int getDisplayedWidth();

    int getDisplayedX();

    int getDisplayedY();

    int getRealHeight();

    int getRealWidth();
}
