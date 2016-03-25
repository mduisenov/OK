package ru.ok.android.fragments.web.hooks;

import ru.ok.android.fragments.web.hooks.HookPauseTrackProcessor.OnPauseMusicListener;

public final class HookPauseMusicProcessor extends HookPauseTrackProcessor {
    public HookPauseMusicProcessor(OnPauseMusicListener listener) {
        super(listener);
    }

    protected String getHookName() {
        return "/apphook/pauseMusic";
    }
}
