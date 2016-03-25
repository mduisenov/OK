package ru.ok.android.emoji;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import ru.ok.android.emoji.smiles.SmilesManager;
import ru.ok.android.emoji.stickers.StickerSetListener;
import ru.ok.android.emoji.ui.custom.PagerSlidingTabStripEmoji;
import ru.ok.android.emoji.utils.TabClickListenerImpl;
import ru.ok.android.proto.MessagesProto.Message;

public final class EmojiPanelView extends LinearLayout implements OnPageChangeListener, OnTouchListener, PanelView, StickerSetListener {
    private boolean backspacePerformed;
    private final EmojiViewController controller;
    private final View deleteView;
    private Handler handler;
    private final EmojiViewListener listener;
    private final ViewPager pager;
    private int prevSelectedPage;
    private final EmojiSectionPagerAdapter sectionsAdapter;
    private final PagerSlidingTabStripEmoji strip;

    public interface EmojiViewListener {
        boolean onBackspace();

        void onStickerPageSelected();
    }

    public void showStickersPage() {
        this.pager.setCurrentItem(this.sectionsAdapter.getStickersPageIndex(), false);
    }

    public EmojiPanelView(Context context, EmojiViewController controller, boolean stickersEnabled) {
        super(context);
        this.prevSelectedPage = -1;
        this.handler = new 1(this);
        setOrientation(1);
        setBackgroundColor(getResources().getColor(C0263R.color.emoji_panel_background));
        inflate(context, C0263R.layout.emoji_panel_view, this);
        this.pager = (ViewPager) findViewById(C0263R.id.pager);
        this.controller = controller;
        controller.addStickersSetsListener(this);
        this.sectionsAdapter = new EmojiSectionPagerAdapter(context, controller, stickersEnabled);
        this.pager.setAdapter(this.sectionsAdapter);
        if (controller.getRecents().getSmilesRecents().isEmpty()) {
            this.pager.setCurrentItem(1, false);
        }
        this.strip = (PagerSlidingTabStripEmoji) findViewById(C0263R.id.indicator);
        this.strip.setShouldExpand(false);
        this.strip.setOnPageChangeListener(this);
        this.strip.setViewPager(this.pager);
        this.strip.setTabClickListener(new TabClickListenerImpl("smile-section-clicked"));
        this.strip.setTabPaddingLeftRight(0);
        this.deleteView = findViewById(C0263R.id.delete);
        this.deleteView.setOnTouchListener(this);
        this.listener = controller;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case RECEIVED_VALUE:
                this.backspacePerformed = false;
                this.handler.sendEmptyMessageDelayed(0, 350);
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                this.handler.removeMessages(0);
                if (!this.backspacePerformed) {
                    callBackspace();
                    break;
                }
                break;
        }
        return false;
    }

    private void callBackspace() {
        if (this.listener.onBackspace()) {
            this.deleteView.performHapticFeedback(3);
        }
    }

    public void onPause() {
        this.controller.getRecents().onPause();
    }

    public void onShown() {
        if (!this.controller.getRecents().getSmilesRecents().isEmpty()) {
            this.pager.setCurrentItem(0, false);
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageSelected(int position) {
        if (!(this.prevSelectedPage == -1 || this.prevSelectedPage == position)) {
            this.sectionsAdapter.pageDeselected(this.prevSelectedPage);
            SmilesManager.smilesCallback.logEvent("smile-section-swiped", "position", String.valueOf(position));
        }
        this.prevSelectedPage = position;
        if (position == 2) {
            this.listener.onStickerPageSelected();
            this.sectionsAdapter.onStickersPageSelected();
        }
    }

    public void onPageScrollStateChanged(int state) {
    }

    public void onStickersSetChanged() {
        this.sectionsAdapter.updateStickers();
        this.strip.notifyDataSetChanged();
    }
}
