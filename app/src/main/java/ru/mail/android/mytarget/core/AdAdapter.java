package ru.mail.android.mytarget.core;

import android.content.Context;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import ru.mail.android.mytarget.Tracer;
import ru.mail.android.mytarget.core.async.Request.ExecuteListener;
import ru.mail.android.mytarget.core.async.Sender;
import ru.mail.android.mytarget.core.async.http.AdRequest;
import ru.mail.android.mytarget.core.factories.RequestsFactory;
import ru.mail.android.mytarget.core.models.AdData;
import ru.mail.android.mytarget.core.models.AdditionalData;
import ru.mail.android.mytarget.core.models.sections.AppwallSection;
import ru.mail.android.mytarget.core.models.sections.FullscreenSection;
import ru.mail.android.mytarget.core.models.sections.Section;
import ru.mail.android.mytarget.core.providers.FingerprintDataProvider;

public final class AdAdapter {
    private static final int MAX_CONNECT_ATTEMPTS = 3;
    private static AdAdapter instance;
    private AdRequest adRequest;
    private Context context;
    private AdData db;
    private ExecuteListener executeListener;
    private boolean isDestroyed;
    private AdParams params;
    private WeakReference<UpdateListener> updateListenerWeakReference;

    public interface UpdateListener {
        void onError(String str);

        void onUpdate();
    }

    public static AdAdapter getInstance() {
        if (instance == null) {
            instance = new AdAdapter();
        }
        return instance;
    }

    public boolean isDestroyed() {
        return this.isDestroyed;
    }

    public AdData getDB() {
        return this.db;
    }

    public UpdateListener getUpdateListener() {
        if (this.updateListenerWeakReference != null) {
            return (UpdateListener) this.updateListenerWeakReference.get();
        }
        return null;
    }

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListenerWeakReference = null;
        if (updateListener != null) {
            this.updateListenerWeakReference = new WeakReference(updateListener);
        }
    }

    public AdAdapter() {
        this.executeListener = new 1(this);
        Tracer.m37i("AdAdapter created. Version: 4.1.7");
    }

    public void init(Context context, AdParams params) {
        Tracer.m35d("adapter initialize...");
        this.context = context.getApplicationContext();
        this.params = params;
        Tracer.m35d("adapter initialized");
    }

    public void update() {
        update(false);
    }

    public void update(boolean ignoreCache) {
        if (ignoreCache || this.db == null || this.db.isExpired()) {
            this.db = null;
            if (this.adRequest == null) {
                this.adRequest = RequestsFactory.getAdRequest(this.params, ignoreCache);
                this.adRequest.setExecuteListener(this.executeListener);
                Sender.addRequest(this.adRequest, this.context);
            }
        } else if (this.updateListenerWeakReference != null) {
            UpdateListener listener = (UpdateListener) this.updateListenerWeakReference.get();
            if (listener != null) {
                listener.onUpdate();
            }
        }
    }

    public void destroy() {
        this.context = null;
        this.params = null;
        if (this.adRequest != null) {
            this.adRequest.setExecuteListener(null);
            this.adRequest = null;
        }
        this.updateListenerWeakReference = null;
        this.db = null;
        this.isDestroyed = true;
    }

    private void processAdditionalDatas(UpdateListener listener, String error) {
        AdditionalData additionalData = null;
        if (this.db != null) {
            additionalData = this.db.getFirstAdditional();
        }
        if (additionalData != null) {
            this.db.removeAdditionalData(additionalData);
            additionalData.incrementRedirectCount();
            this.adRequest = RequestsFactory.getAdRequest(this.params, true, this.db, additionalData);
            this.adRequest.setExecuteListener(this.executeListener);
            Sender.addRequest(this.adRequest, this.context);
        } else if (this.db != null) {
            listener.onUpdate();
        } else {
            listener.onError(error);
        }
    }

    public AppwallSection getAppwallSection(String name) {
        if (this.db != null) {
            Section section = this.db.getSection(name);
            if (section instanceof AppwallSection) {
                return (AppwallSection) section;
            }
        }
        return null;
    }

    public ArrayList<AppwallSection> getAppwallSections() {
        if (this.db == null) {
            return null;
        }
        ArrayList<AppwallSection> arrayList = new ArrayList();
        Iterator i$ = this.db.getSections().iterator();
        while (i$.hasNext()) {
            Section section = (Section) i$.next();
            if (section instanceof AppwallSection) {
                arrayList.add((AppwallSection) section);
            }
        }
        return arrayList;
    }

    public FullscreenSection getFullscreenSection(String name) {
        if (this.db != null) {
            Section section = this.db.getSection(name);
            if (section instanceof FullscreenSection) {
                return (FullscreenSection) section;
            }
        }
        return null;
    }

    public ArrayList<FullscreenSection> getFullscreenSections() {
        if (this.db == null) {
            return null;
        }
        ArrayList<FullscreenSection> arrayList = new ArrayList();
        Iterator i$ = this.db.getSections().iterator();
        while (i$.hasNext()) {
            Section section = (Section) i$.next();
            if (section instanceof FullscreenSection) {
                arrayList.add((FullscreenSection) section);
            }
        }
        return arrayList;
    }

    public Map<String, String> getDeviceParams() {
        FingerprintDataProvider.getInstance().getDeviceParamsDataProvider().collectData(this.context);
        return FingerprintDataProvider.getInstance().getDeviceParamsDataProvider().getDeviceParams();
    }

    public String getData() {
        if (this.db == null || this.db.getRawData() == null) {
            return null;
        }
        return this.db.getRawData().toString();
    }

    public boolean hasData() {
        return this.db != null;
    }
}
