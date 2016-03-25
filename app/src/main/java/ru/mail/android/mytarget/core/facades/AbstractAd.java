package ru.mail.android.mytarget.core.facades;

import android.content.Context;
import ru.mail.android.mytarget.core.AdAdapter;
import ru.mail.android.mytarget.core.AdAdapter.UpdateListener;
import ru.mail.android.mytarget.core.AdParams;
import ru.mail.android.mytarget.core.models.AdData;

public abstract class AbstractAd implements MyTargetAd {
    protected AdData adData;
    protected AdAdapter adapter;
    protected Context context;
    protected AdParams params;
    private UpdateListener updateListener;

    /* renamed from: ru.mail.android.mytarget.core.facades.AbstractAd.1 */
    class C01701 implements UpdateListener {
        C01701() {
        }

        public void onUpdate() {
            AbstractAd.this.adData = AbstractAd.this.adapter.getDB();
            AbstractAd.this.onLoad(AbstractAd.this.adData);
        }

        public void onError(String error) {
            AbstractAd.this.onLoadError(error);
        }
    }

    protected abstract void onLoad(AdData adData);

    protected abstract void onLoadError(String str);

    public AbstractAd() {
        this.updateListener = new C01701();
    }

    protected void init(AdParams params, Context context) {
        this.params = params;
        this.context = context;
        this.adapter = new AdAdapter();
        this.adapter.setUpdateListener(this.updateListener);
        this.adapter.init(context, params);
    }

    public void load() {
        this.adapter.update(true);
    }
}
