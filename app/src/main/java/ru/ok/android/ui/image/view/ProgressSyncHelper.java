package ru.ok.android.ui.image.view;

import java.lang.ref.WeakReference;
import ru.ok.android.ui.custom.ProgressWheelView;

public class ProgressSyncHelper {
    private WeakReference<ProgressWheelView> pivotViewRef;

    public final void registerPivotView(ProgressWheelView pivotProgressView) {
        this.pivotViewRef = new WeakReference(pivotProgressView);
    }

    public final int getSpinProgress() {
        if (this.pivotViewRef == null || this.pivotViewRef.get() == null) {
            return 0;
        }
        return ((ProgressWheelView) this.pivotViewRef.get()).getSpinProgress();
    }
}
