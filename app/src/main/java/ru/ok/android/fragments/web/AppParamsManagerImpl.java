package ru.ok.android.fragments.web;

import android.text.TextUtils;
import ru.ok.android.fragments.web.client.interceptor.appparams.AppParamsInterceptor.AppParamsManager;
import ru.ok.android.fragments.web.client.interceptor.appparams.AppParamsInterceptor.WebAppParam;

public class AppParamsManagerImpl implements AppParamsManager {
    private static AppParamsManager instance;
    private StringBuilder appParamsBuilder;

    private AppParamsManagerImpl() {
        this.appParamsBuilder = new StringBuilder();
        this.appParamsBuilder = new StringBuilder("");
    }

    public static AppParamsManager getInstance() {
        if (instance == null) {
            instance = new AppParamsManagerImpl();
        }
        return instance;
    }

    public void pushAppParam(WebAppParam appParams) {
        if (!isContains(appParams)) {
            this.appParamsBuilder.append(appParams.getValue());
        }
    }

    public boolean isContains(WebAppParam param) {
        for (char ch : this.appParamsBuilder.toString().toCharArray()) {
            if (ch == param.getValue()) {
                return true;
            }
        }
        return false;
    }

    public String peekAppParams() {
        return this.appParamsBuilder.toString();
    }

    public String popAppParams() {
        String value = peekAppParams();
        clear();
        return value;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(this.appParamsBuilder);
    }

    public void clear() {
        this.appParamsBuilder = new StringBuilder();
    }
}
