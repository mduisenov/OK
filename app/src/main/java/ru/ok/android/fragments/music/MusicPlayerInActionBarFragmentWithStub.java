package ru.ok.android.fragments.music;

import ru.ok.android.services.transport.exception.NoConnectionException;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.OnStubButtonClickListener;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.State;
import ru.ok.android.ui.custom.emptyview.SmartEmptyViewAnimated.Type;

public abstract class MusicPlayerInActionBarFragmentWithStub extends MusicPlayerInActionBarFragment implements OnStubButtonClickListener {
    private boolean dbLoadCompleted;
    protected SmartEmptyViewAnimated emptyView;
    private boolean webLoadCompleted;

    protected void dbLoadCompleted() {
        this.dbLoadCompleted = true;
        if (this.webLoadCompleted) {
            this.emptyView.setState(State.LOADED);
        }
    }

    protected void showProgressStub() {
        this.emptyView.setState(State.LOADING);
        this.emptyView.setVisibility(0);
    }

    protected void onWebLoadError(Object description) {
        this.webLoadCompleted = true;
        this.emptyView.setType(description instanceof NoConnectionException ? Type.NO_INTERNET : Type.ERROR);
        if (this.dbLoadCompleted) {
            this.emptyView.setState(State.LOADED);
        }
    }

    protected void onWebLoadSuccess(Type type, boolean hasData) {
        this.webLoadCompleted = true;
        this.emptyView.setType(type);
        if (this.dbLoadCompleted && !hasData) {
            this.emptyView.setState(State.LOADED);
        }
    }
}
