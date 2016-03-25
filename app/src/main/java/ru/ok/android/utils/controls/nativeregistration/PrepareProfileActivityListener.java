package ru.ok.android.utils.controls.nativeregistration;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import ru.ok.android.model.UpdateProfileFieldsFlags;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.registration.Location;

public interface PrepareProfileActivityListener {
    void onPrepareProfileActivityError(String str, @NonNull ErrorType errorType);

    void onPrepareProfileActivitySuccess(ArrayList<Location> arrayList, UpdateProfileFieldsFlags updateProfileFieldsFlags);
}
