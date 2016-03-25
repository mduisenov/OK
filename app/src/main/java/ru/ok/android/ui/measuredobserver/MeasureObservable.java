package ru.ok.android.ui.measuredobserver;

import android.view.View;
import java.util.Observable;
import java.util.Observer;

public interface MeasureObservable {

    public static class MeasureObservableHelper extends Observable {
        public void onMeasure(View view) {
            setChanged();
            notifyObservers(view);
        }
    }

    void addMeasureObserver(Observer observer);
}
