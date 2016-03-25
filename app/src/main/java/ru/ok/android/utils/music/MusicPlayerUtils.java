package ru.ok.android.utils.music;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import ru.ok.android.model.music.MusicInfoContainer;
import ru.ok.android.services.app.MusicService.InformationState;
import ru.ok.android.utils.bus.BusProtocol;
import ru.ok.android.utils.controls.music.MusicListType;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.wmf.PlayTrackInfo;

public class MusicPlayerUtils {

    /* renamed from: ru.ok.android.utils.music.MusicPlayerUtils.1 */
    static class C14741 implements Runnable {
        final /* synthetic */ View val$delegate;
        final /* synthetic */ int val$touchOffsetPixels;
        final /* synthetic */ View val$view;

        C14741(View view, int i, View view2) {
            this.val$delegate = view;
            this.val$touchOffsetPixels = i;
            this.val$view = view2;
        }

        public void run() {
            int[] location = new int[2];
            this.val$delegate.getLocationOnScreen(location);
            Rect rect = new Rect(location[0], location[1], location[0] + this.val$delegate.getWidth(), location[1] + this.val$delegate.getHeight());
            rect.inset(-this.val$touchOffsetPixels, -this.val$touchOffsetPixels);
            this.val$view.setTouchDelegate(new SeekBarTouchDelegate(rect, this.val$delegate));
        }
    }

    public static CharSequence buildLegalInfo(@NonNull Context context, PlayTrackInfo info) {
        if (info == null) {
            return null;
        }
        CharSequence sb = new StringBuilder();
        if (info.userId == null || info.userId.length() > 0) {
            sb.append(LocalizationManager.getString(context, 2131166240));
            sb.append(" ");
            sb.append(getId(info.userId));
            return sb;
        }
        sb.append(LocalizationManager.getString(context, 2131166232));
        return sb;
    }

    private static String getId(String id) {
        return "id " + id;
    }

    public static void setTouchDelegate(View view, View delegate, int touchOffsetPixels) {
        view.post(new C14741(delegate, touchOffsetPixels, view));
    }

    public static boolean isShowPlay(Bundle bundle, MusicListType currentType, String currentId) {
        InformationState playState = (InformationState) bundle.getSerializable(BusProtocol.PREF_MEDIA_PLAYER_STATE);
        MusicInfoContainer musicInfoContainer = (MusicInfoContainer) bundle.getParcelable(BusProtocol.PREF_MEDIA_PLAYER_STATE_MUSIC_INFO_CONTAINER);
        if (musicInfoContainer == null || playState != InformationState.PLAY || musicInfoContainer.track == null) {
            return false;
        }
        String playlistKey = bundle.getString("playlist_key");
        if (currentType == MusicListType.PLAYLIST || (playlistKey != null && playlistKey.equals(buildPlaylistKey(currentType, currentId)))) {
            return true;
        }
        return false;
    }

    public static String buildPlaylistKey(@NonNull MusicListType type, @Nullable String playlistId) {
        return type + "|" + playlistId;
    }
}
