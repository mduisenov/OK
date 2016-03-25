package ru.ok.android.ui.tabbar.manager;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.coordinator.behaviors.TabbarBehavior;
import ru.ok.android.ui.tabbar.OdklTabbar;
import ru.ok.android.utils.DeviceUtils;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.MathUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.events.DiscussionOdklEvent;
import ru.ok.model.events.OdnkEvent;
import ru.ok.model.events.OdnkEvent.EventType;

public class TabbarManager implements FullTabbarManager {
    private View contentView;
    private Activity context;
    private boolean isNeedShowTabbar;
    private boolean isShowAbove;
    private ViewGroup rootLayout;
    private OdklTabbar tabbarView;
    private ViewStub tabbarViewStub;

    /* renamed from: ru.ok.android.ui.tabbar.manager.TabbarManager.1 */
    static /* synthetic */ class C12851 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$events$OdnkEvent$EventType;

        static {
            $SwitchMap$ru$ok$model$events$OdnkEvent$EventType = new int[EventType.values().length];
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.EVENTS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.MARKS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.GUESTS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.DISCUSSIONS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.MESSAGES.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.ACTIVITIES.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$ru$ok$model$events$OdnkEvent$EventType[EventType.LOCALE.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public interface LocateTabbar {
        void locate(ViewGroup viewGroup, View view);
    }

    public static class SimpleLocateTabbar implements LocateTabbar {
        public void locate(ViewGroup rootLayout, View viewstab) {
            if (rootLayout instanceof RelativeLayout) {
                LayoutParams params = new LayoutParams(viewstab.getLayoutParams());
                params.addRule(12);
                viewstab.setLayoutParams(params);
            } else if (rootLayout instanceof FrameLayout) {
                FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(viewstab.getLayoutParams());
                params2.gravity = 80;
                viewstab.setLayoutParams(params2);
            } else if (rootLayout instanceof CoordinatorLayout) {
                CoordinatorLayout.LayoutParams params3 = new CoordinatorLayout.LayoutParams(viewstab.getLayoutParams());
                params3.gravity = 80;
                params3.setBehavior(new TabbarBehavior(rootLayout.getContext()));
                viewstab.setLayoutParams(params3);
            } else {
                throw new IllegalStateException("This class support rootLayout extends RelativeLayout or FrameLayout ");
            }
            rootLayout.addView(viewstab);
        }
    }

    public TabbarManager(Activity activity, ViewGroup rootView, View contentView, LocateTabbar locate) {
        this.isNeedShowTabbar = true;
        this.isShowAbove = false;
        this.rootLayout = rootView;
        this.contentView = contentView;
        this.context = activity;
        this.tabbarViewStub = (ViewStub) this.context.getLayoutInflater().inflate(2130903534, rootView, false).findViewById(2131624436);
        locate.locate(this.rootLayout, this.tabbarViewStub);
    }

    public TabbarManager(Activity activity, ViewGroup rootView, View contentView) {
        this(activity, rootView, contentView, new SimpleLocateTabbar());
    }

    public OdklTabbar getTabbarView() {
        return this.tabbarView;
    }

    public void showAboveTabbar() {
        if (!this.isShowAbove && this.tabbarView != null && this.contentView != null && (this.contentView.getLayoutParams() instanceof MarginLayoutParams)) {
            ((MarginLayoutParams) this.contentView.getLayoutParams()).bottomMargin = this.context.getResources().getDimensionPixelSize(2131231193);
            this.contentView.requestLayout();
            this.isShowAbove = true;
            clearScroll();
        }
    }

    public int getScrollTabbar() {
        return this.tabbarView == null ? 0 : (int) this.tabbarView.getTranslationY();
    }

    public void setScrollTabbar(float scroll) {
        if (this.isShowAbove) {
            scroll = 0.0f;
        }
        if (this.tabbarView != null) {
            this.tabbarView.setTranslationY(MathUtils.clamp(scroll, 0.0f, (float) this.tabbarView.getHeight()));
        }
    }

    public void showTabbar(boolean isAnimate) {
        if (DeviceUtils.isShowTabbar() && this.isNeedShowTabbar) {
            clearScroll();
            if (!isTabbarVisible()) {
                if (this.tabbarView == null) {
                    inflateStubTabbarView();
                }
                if (this.tabbarView != null) {
                    this.tabbarView.clearAnimation();
                    if (isAnimate) {
                        this.tabbarView.animateShow(null);
                    } else {
                        setVisibilityTabbar(0);
                    }
                }
            }
        }
    }

    public void setNeedShowTabbar(boolean needShowTabbar) {
        this.isNeedShowTabbar = needShowTabbar;
    }

    public void onResume() {
        updateTabbarPosition();
        if (getTabbarView() != null) {
            getTabbarView().onResume();
        }
    }

    public void onPause() {
        if (getTabbarView() != null) {
            getTabbarView().onPause();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("key_is_need_show_tabbar", this.isNeedShowTabbar);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        this.isNeedShowTabbar = savedInstanceState.getBoolean("key_is_need_show_tabbar", true);
    }

    public void onGetNewEvents(ArrayList<OdnkEvent> returnList) {
        int marksCount = 0;
        int guestCount = 0;
        boolean hasGuestOrMarksOrEvents = false;
        OdklTabbar tabbar = getTabbarView();
        Iterator i$ = returnList.iterator();
        while (i$.hasNext()) {
            OdnkEvent event = (OdnkEvent) i$.next();
            switch (C12851.$SwitchMap$ru$ok$model$events$OdnkEvent$EventType[event.type.ordinal()]) {
                case Message.TEXT_FIELD_NUMBER /*1*/:
                    hasGuestOrMarksOrEvents = true;
                    break;
                case Message.AUTHORID_FIELD_NUMBER /*2*/:
                    hasGuestOrMarksOrEvents = true;
                    marksCount = event.getValueInt();
                    break;
                case Message.TYPE_FIELD_NUMBER /*3*/:
                    hasGuestOrMarksOrEvents = true;
                    guestCount = event.getValueInt();
                    break;
                case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                    if (tabbar != null && (event instanceof DiscussionOdklEvent)) {
                        int discussionsCount = event.getValueInt();
                        tabbar.processDiscussionsEvents(((DiscussionOdklEvent) event).getIntValueReply(), ((DiscussionOdklEvent) event).getIntValueLike(), discussionsCount);
                        break;
                    }
                case Message.UUID_FIELD_NUMBER /*5*/:
                    if (tabbar == null) {
                        break;
                    }
                    tabbar.updateConversationsCounter(event.getValueInt());
                    break;
                case Message.REPLYTO_FIELD_NUMBER /*6*/:
                    if (tabbar == null) {
                        break;
                    }
                    tabbar.processFeedEvent(event.getValueInt());
                    break;
                case Message.ATTACHES_FIELD_NUMBER /*7*/:
                    LocalizationManager.from(this.context).setLocaleTo(event.value);
                    break;
                default:
                    break;
            }
        }
        if (tabbar != null && hasGuestOrMarksOrEvents) {
            tabbar.processMenuAction(marksCount, guestCount, 0);
        }
    }

    private void updateTabbarPosition() {
        if (this.tabbarView != null) {
            this.tabbarView.init();
            this.rootLayout.requestLayout();
        }
    }

    private void clearScroll() {
        setScrollTabbar(0.0f);
    }

    private void inflateStubTabbarView() {
        if (this.tabbarViewStub != null) {
            this.tabbarView = (OdklTabbar) this.tabbarViewStub.inflate();
            this.tabbarView.setVisibility(0);
            Application app = this.context.getApplication();
            if (app instanceof OdnoklassnikiApplication) {
                try {
                    this.tabbarView.setFont(((OdnoklassnikiApplication) app).getFontFromAssets("fonts/DroidSans.ttf"));
                } catch (FileNotFoundException e) {
                    Logger.m185w("Failed to load font", e);
                }
            }
            this.tabbarViewStub = null;
            updateTabbarPosition();
        }
    }

    private void setVisibilityTabbar(int visibility) {
        if (visibility == 0) {
            clearScroll();
        }
        this.tabbarView.setVisibility(visibility);
    }

    private boolean isTabbarVisible() {
        return this.tabbarView != null && this.tabbarView.getVisibility() == 0;
    }
}
