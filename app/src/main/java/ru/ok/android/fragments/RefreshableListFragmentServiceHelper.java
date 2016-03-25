package ru.ok.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.ui.adapters.ImageBlockerRecyclerProvider;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.utils.Logger;

public class RefreshableListFragmentServiceHelper extends RefreshableRecyclerFragmentHelper {
    private long lastTime;
    private final int msgRefresh;
    private final String tag;

    public RefreshableListFragmentServiceHelper(BaseFragment fragment, Context context, String refreshSettingsName, int emptyViewTextResId, int msgRefresh, String tag) {
        super(fragment, context, refreshSettingsName, emptyViewTextResId);
        this.msgRefresh = msgRefresh;
        this.tag = tag;
    }

    public boolean onStartRefresh(boolean manual) {
        if (manual || System.currentTimeMillis() - this.lastTime > 120000) {
            GlobalBus.send(this.msgRefresh, new BusEvent());
        } else {
            notifyRefreshSuccessful(null);
        }
        return true;
    }

    protected void onRefreshSuccessful(Bundle bundle) {
        boolean z = true;
        this.lastTime = System.currentTimeMillis();
        Logger.m173d("[%s] count=%d", this.tag, Integer.valueOf(bundle.getInt("COUNT", 0)));
        if (bundle.getInt("COUNT", 0) > 0) {
            z = false;
        }
        notifyRefreshSuccessful(Boolean.valueOf(z));
    }

    protected void onRefreshFailed(Bundle output) {
        Logger.m173d("[%s]", this.tag);
        notifyRefreshFailed(ErrorType.from(output));
    }

    public <TAdapter extends Adapter & ImageBlockerRecyclerProvider> void onFragmentCreateView(View mainFragmentView, TAdapter adapter) {
        super.onFragmentCreateView(mainFragmentView, adapter);
        GlobalBus.register(this);
    }

    public void onFragmentDestroyView() {
        super.onFragmentDestroyView();
        GlobalBus.unregister(this);
    }

    @Subscribe(on = 2131623946, to = 2131624263)
    public void onEvent(BusEvent event) {
        if (event.resultCode == -1) {
            onRefreshSuccessful(event.bundleOutput);
        } else if (event.resultCode == -2) {
            onRefreshFailed(event.bundleOutput);
        }
    }
}
