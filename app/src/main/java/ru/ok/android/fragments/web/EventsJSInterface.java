package ru.ok.android.fragments.web;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import ru.ok.android.fragments.web.client.interceptor.hooks.AppHooksBridge;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.controls.events.EventsManager;
import ru.ok.android.utils.json.JsonUploadAlbumInfoParser;
import ru.ok.java.api.JsonHttpResult;
import ru.ok.java.api.exceptions.ResultParsingException;
import ru.ok.java.api.json.JsonGetHookEventParser;
import ru.ok.model.photo.PhotoAlbumInfo;

public class EventsJSInterface {
    public static String JS_HOOK_NAME;
    AppHooksBridge appHooksBridge;

    static {
        JS_HOOK_NAME = "hookjs";
    }

    public static JSFunction getJSFunction() {
        JSFunction function = new JSFunction(JS_HOOK_NAME, "sendToAndroid");
        function.addParam("hookAppData()");
        return function;
    }

    public EventsJSInterface(AppHooksBridge bridge) {
        this.appHooksBridge = bridge;
    }

    @JavascriptInterface
    public void hideLoading() {
        Logger.m172d("hook hide loading");
    }

    @JavascriptInterface
    public void onUploadPhotoCalled(String jsonAlbumInfo) {
        PhotoAlbumInfo album = null;
        if (!TextUtils.isEmpty(jsonAlbumInfo)) {
            album = JsonUploadAlbumInfoParser.parse(jsonAlbumInfo);
        }
        if (this.appHooksBridge != null) {
            this.appHooksBridge.uploadPhoto(album);
        }
    }

    @JavascriptInterface
    public void sendToAndroid(String text) {
        Logger.m172d("hook js:" + text);
        try {
            EventsManager.getInstance().setEvents(new JsonGetHookEventParser(new JsonHttpResult(0, text), System.currentTimeMillis()).parse(), true);
        } catch (ResultParsingException e) {
            Logger.m172d("hook js exception:" + e.getMessage());
        }
    }
}
