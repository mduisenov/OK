package ru.ok.android.ui.custom.animationlist;

import android.graphics.drawable.BitmapDrawable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.ok.android.ui.custom.animationlist.UpdateListDataCommand.ListInitialPositionCallback;

public final class ListAnimationStepData<D> {
    final List<Long> currentIds;
    boolean dataAdapterWasEmpty;
    final List<Long> initialIds;
    final Map<Long, RowInfo> initialRowsInfos;
    boolean layoutPassed;
    final boolean manualPositioningRequired;
    final D oldData;
    Object onPreDataSetResult;
    final UpdateListDataCommand<D> operation;
    boolean positionWasSaved;
    final Map<Long, BitmapDrawable> rowsDrawables;

    public ListAnimationStepData(UpdateListDataCommand<D> operation, D oldData) {
        this.initialRowsInfos = new HashMap();
        this.rowsDrawables = new HashMap();
        this.initialIds = new ArrayList();
        this.currentIds = new ArrayList();
        this.onPreDataSetResult = null;
        this.operation = operation;
        this.oldData = oldData;
        ListInitialPositionCallback<D> listInitialPositionCallback = this.operation.listInitialPositionCallback;
        boolean z = listInitialPositionCallback != null && listInitialPositionCallback.isWantToChangePosition(oldData, operation.data);
        this.manualPositioningRequired = z;
    }
}
