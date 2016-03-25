package ru.ok.android.ui.custom.animationlist;

import android.animation.Animator;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Interpolator;
import java.util.List;
import ru.ok.android.ui.custom.animationlist.AnimateChangesListView.BoundsAnimationListener;

public final class UpdateListDataCommand<D> {
    public final long animationDuration;
    public final ListCellCreateAnimationCreator createAnimationCreator;
    public final D data;
    public final boolean doNotAnimate;
    public final boolean doNotChangeData;
    public final ListCellCreateAnimationCreator initialCreateAnimationCreator;
    public final Interpolator interpolator;
    public final ListFinalPositionCallback<D> listFinalPositionCallback;
    public final ListInitialPositionCallback<D> listInitialPositionCallback;
    public final OnDataSetCallback<D> onDataSetCallback;
    public final ListCellRemoveAnimationCreator removeAnimation;
    public final ListRestorePositionCallback restorePositionCallback;
    public final boolean saveListPosition;
    public final ListCellCreateAnimationCreator slideInAnimationCreator;
    public final ListCellRemoveAnimationCreator slideOutAnimationCreator;

    public interface ListCellCreateAnimationCreator {
        void createAnimations(View view, List<Animator> list);
    }

    public interface ListCellRemoveAnimationCreator {
        void createAnimations(Drawable drawable, RowInfo rowInfo, List<Animator> list, BoundsAnimationListener boundsAnimationListener);
    }

    public interface ListFinalPositionCallback<D> {
        boolean setFinalPosition(D d, D d2, Object obj);
    }

    public interface ListInitialPositionCallback<D> {
        boolean isWantToChangePosition(D d, D d2);

        void setInitialPosition();
    }

    public interface ListRestorePositionCallback {
        void onRestorePosition(RowInfo rowInfo, int i);
    }

    public interface OnDataSetCallback<D> {
        void onPostDataSet(D d);

        Object onPreDataSet(D d);
    }

    public static class UpdateListDataCommandBuilder<D> {
        private long animationDuration;
        private ListCellCreateAnimationCreator createAnimationCreator;
        private D data;
        private OnDataSetCallback<D> dataSetCallback;
        private boolean doNotAnimate;
        private boolean doNotChangeData;
        private ListCellCreateAnimationCreator initialCreateAnimationCreator;
        private Interpolator interpolator;
        private ListFinalPositionCallback<D> listFinalPositionCallback;
        private ListInitialPositionCallback<D> listInitialPositionCallback;
        private ListCellRemoveAnimationCreator removeAnimationCreator;
        private ListRestorePositionCallback restorePositionCallback;
        private boolean saveListPosition;
        private ListCellCreateAnimationCreator slideInAnimationCreator;
        private ListCellRemoveAnimationCreator slideOutAnimationCreator;

        public UpdateListDataCommandBuilder() {
            this.dataSetCallback = null;
            this.listInitialPositionCallback = null;
            this.listFinalPositionCallback = null;
            this.saveListPosition = true;
            this.animationDuration = 500;
            this.interpolator = null;
            this.initialCreateAnimationCreator = null;
            this.createAnimationCreator = null;
            this.slideOutAnimationCreator = null;
            this.slideInAnimationCreator = null;
            this.removeAnimationCreator = null;
        }

        public UpdateListDataCommandBuilder<D> withData(D data) {
            this.data = data;
            return this;
        }

        public UpdateListDataCommandBuilder<D> withOnDataSet(OnDataSetCallback<D> onDataSetCallback) {
            this.dataSetCallback = onDataSetCallback;
            return this;
        }

        public UpdateListDataCommandBuilder<D> withListInitialPosition(ListInitialPositionCallback<D> listInitialPositionCallback) {
            this.listInitialPositionCallback = listInitialPositionCallback;
            return this;
        }

        public UpdateListDataCommandBuilder<D> withListFinalPosition(ListFinalPositionCallback<D> listFinalPositionCallback) {
            this.listFinalPositionCallback = listFinalPositionCallback;
            return this;
        }

        public UpdateListDataCommandBuilder<D> saveListPosition(boolean saveListPosition) {
            this.saveListPosition = saveListPosition;
            return this;
        }

        public UpdateListDataCommandBuilder<D> withDuration(long animationDurationMillis) {
            this.animationDuration = animationDurationMillis;
            return this;
        }

        public UpdateListDataCommandBuilder<D> withInterpolator(Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public UpdateListDataCommandBuilder<D> withCreateAnimation(ListCellCreateAnimationCreator creator) {
            this.createAnimationCreator = creator;
            return this;
        }

        public UpdateListDataCommandBuilder<D> withRemoveAnimation(ListCellRemoveAnimationCreator creator) {
            this.removeAnimationCreator = creator;
            return this;
        }

        public UpdateListDataCommandBuilder<D> withSlideOutAnimation(ListCellRemoveAnimationCreator creator) {
            this.slideOutAnimationCreator = creator;
            return this;
        }

        public UpdateListDataCommandBuilder<D> withSlideInAnimation(ListCellCreateAnimationCreator creator) {
            this.slideInAnimationCreator = creator;
            return this;
        }

        public UpdateListDataCommandBuilder<D> doNotChangeData(boolean doNotChange) {
            this.doNotChangeData = doNotChange;
            return this;
        }

        public UpdateListDataCommandBuilder<D> withRestorePosition(ListRestorePositionCallback callback) {
            this.restorePositionCallback = callback;
            return this;
        }

        public UpdateListDataCommand<D> build() {
            return new UpdateListDataCommand(this.dataSetCallback, this.listInitialPositionCallback, this.listFinalPositionCallback, this.saveListPosition, this.animationDuration, this.interpolator, this.initialCreateAnimationCreator, this.createAnimationCreator, this.slideOutAnimationCreator, this.slideInAnimationCreator, this.removeAnimationCreator, this.restorePositionCallback, this.doNotChangeData, this.doNotAnimate, null);
        }
    }

    private UpdateListDataCommand(D data, OnDataSetCallback<D> onDataSetCallback, ListInitialPositionCallback<D> listInitialPositionCallback, ListFinalPositionCallback<D> listFinalPositionCallback, boolean saveListPosition, long animationDuration, Interpolator interpolator, ListCellCreateAnimationCreator initialCreateAnimationCreator, ListCellCreateAnimationCreator createAnimationCreator, ListCellRemoveAnimationCreator slideOutAnimationCreator, ListCellCreateAnimationCreator slideInAnimationCreator, ListCellRemoveAnimationCreator removeAnimation, ListRestorePositionCallback restorePositionCallback, boolean doNotChangeData, boolean doNotAnimate) {
        this.data = data;
        this.onDataSetCallback = onDataSetCallback;
        this.listInitialPositionCallback = listInitialPositionCallback;
        this.listFinalPositionCallback = listFinalPositionCallback;
        this.saveListPosition = saveListPosition;
        this.animationDuration = animationDuration;
        this.interpolator = interpolator;
        this.initialCreateAnimationCreator = initialCreateAnimationCreator;
        this.createAnimationCreator = createAnimationCreator;
        this.slideOutAnimationCreator = slideOutAnimationCreator;
        this.slideInAnimationCreator = slideInAnimationCreator;
        this.removeAnimation = removeAnimation;
        this.restorePositionCallback = restorePositionCallback;
        this.doNotChangeData = doNotChangeData;
        this.doNotAnimate = doNotAnimate;
    }
}
