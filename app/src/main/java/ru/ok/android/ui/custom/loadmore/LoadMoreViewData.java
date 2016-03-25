package ru.ok.android.ui.custom.loadmore;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import ru.ok.android.ui.custom.loadmore.LoadMoreView.LoadMoreState;

public class LoadMoreViewData extends Observable {
    LoadMoreState currentState;
    final Map<LoadMoreState, Integer> customStates;
    LoadMoreState permanentState;

    public LoadMoreViewData() {
        this.currentState = LoadMoreState.IDLE;
        this.permanentState = LoadMoreState.DISABLED;
        this.customStates = new HashMap();
    }

    public void setCurrentState(LoadMoreState state) {
        this.currentState = state;
        setChanged();
        notifyObservers();
    }

    public void setPermanentState(LoadMoreState state) {
        this.permanentState = state;
        setChanged();
        notifyObservers();
    }

    public LoadMoreState getCurrentState() {
        return this.currentState;
    }

    public LoadMoreState getPermanentState() {
        return this.permanentState;
    }

    public void setMessageForState(LoadMoreState state, int resourceId) {
        this.customStates.put(state, Integer.valueOf(resourceId));
    }

    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    public synchronized void deleteObserver(Observer observer) {
        super.deleteObserver(observer);
    }
}
