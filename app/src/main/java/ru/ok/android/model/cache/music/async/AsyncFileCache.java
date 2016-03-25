package ru.ok.android.model.cache.music.async;

import java.io.InputStream;
import java.util.concurrent.Future;
import ru.ok.model.wmf.PlayTrackInfo;

public interface AsyncFileCache {

    public interface CacheDataCallBack {
        void onCacheDataFail(PlayTrackInfo playTrackInfo, InputStream inputStream);

        void onCacheDataSuccessful(PlayTrackInfo playTrackInfo, InputStream inputStream);
    }

    public interface ContainsKeyCallBack {
        void onGetKeyInCacheValue(String str, boolean z);
    }

    Future isKeyContains(String str, ContainsKeyCallBack containsKeyCallBack);
}
