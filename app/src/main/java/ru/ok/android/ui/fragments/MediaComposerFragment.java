package ru.ok.android.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.afollestad.materialdialogs.AlertDialogWrapper.Builder;
import com.google.android.gms.plus.PlusShare;
import com.google.android.libraries.cast.companionlibrary.C0158R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.bus.annotation.Subscribe;
import ru.ok.android.proto.MessagesProto;
import ru.ok.android.services.processors.settings.MediaComposerSettingsProcessor;
import ru.ok.android.statistics.mediacomposer.MediaComposerStats;
import ru.ok.android.ui.custom.BubbleTextIcon;
import ru.ok.android.ui.custom.mediacomposer.FragmentBridge;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerData;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerContentListener;
import ru.ok.android.ui.custom.mediacomposer.MediaComposerView.MediaComposerController;
import ru.ok.android.ui.custom.mediacomposer.MediaItem;
import ru.ok.android.ui.custom.mediacomposer.MediaTopicMessage;
import ru.ok.android.ui.custom.mediacomposer.MediaTopicSettingsValidator;
import ru.ok.android.ui.custom.mediacomposer.MediaTopicValidator;
import ru.ok.android.ui.custom.mediacomposer.OkApiMediaTopicValidator;
import ru.ok.android.ui.custom.mediacomposer.adapter.MediaItemAdapter;
import ru.ok.android.ui.custom.toolbar.ToolbarMenu;
import ru.ok.android.ui.custom.toolbar.ToolbarMenuItem;
import ru.ok.android.ui.custom.toolbar.ToolbarView;
import ru.ok.android.ui.custom.toolbar.ToolbarView.OnToolbarItemSelectedListener;
import ru.ok.android.ui.dialogs.AlertFragmentDialog;
import ru.ok.android.ui.dialogs.AlertFragmentDialog.OnAlertDismissListener;
import ru.ok.android.ui.places.PlacesActivity;
import ru.ok.android.ui.users.UsersSelectionParams;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.android.utils.localization.base.LocalizedFragment;
import ru.ok.android.utils.settings.ServicesSettingsHelper;
import ru.ok.java.api.request.mediatopic.MediaTopicType;
import ru.ok.model.places.Place;
import ru.ok.model.settings.MediaComposerSettings;

public class MediaComposerFragment extends LocalizedFragment implements OnClickListener, MediaComposerContentListener, OnToolbarItemSelectedListener, OnAlertDismissListener {
    private static final String[] allDialogFragmentTags;
    private MediaItemAdapter adapter;
    private AtomicBoolean canPostMediaTopic;
    private FragmentBridge fragmentBridge;
    protected String groupId;
    private boolean isNeedValidateMenu;
    protected boolean isToStatusChecked;
    private ToolbarMenuItem itemPlace;
    private MediaComposerFragmentListener listener;
    protected MediaComposerController mediaComposerController;
    private MediaComposerView mediaComposerView;
    protected MediaTopicType mediaTopicType;
    private final MediaTopicValidator mediaTopicValidator;
    protected int mode;
    @NonNull
    private MediaComposerSettings settings;
    private TextView textPlace;
    private final ToastHandler toastHandler;
    private ToolbarView toolbarView;
    private BubbleTextIcon withFriendsBubble;

    public interface MediaComposerFragmentListener {
        void onMediaComposerCompleted(MediaComposerData mediaComposerData);
    }

    public interface OnToStatusChangedListener {
        void onToStatusChanged(boolean z);
    }

    /* renamed from: ru.ok.android.ui.fragments.MediaComposerFragment.1 */
    class C07981 implements OnClickListener {
        final /* synthetic */ Menu val$menu;

        C07981(Menu menu) {
            this.val$menu = menu;
        }

        public void onClick(View v) {
            this.val$menu.performIdentifierAction(2131625475, 0);
        }
    }

    /* renamed from: ru.ok.android.ui.fragments.MediaComposerFragment.2 */
    class C07992 implements DialogInterface.OnClickListener {
        C07992() {
        }

        public void onClick(DialogInterface d, int position) {
            if (position == 0) {
                MediaComposerFragment.this.startSelectPlace();
            } else {
                MediaComposerFragment.this.deletePlace();
            }
        }
    }

    class ToastHandler extends Handler {
        private long expectedToastEnd;
        private final SparseArray<Integer> internedInts;

        ToastHandler() {
            this.internedInts = new SparseArray();
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessagesProto.Message.TEXT_FIELD_NUMBER /*1*/:
                    Context context = MediaComposerFragment.this.getActivity();
                    if (context != null) {
                        int toastDuration = msg.arg1;
                        Toast toast = Toast.makeText(context, LocalizationManager.getString(context, msg.obj.intValue()), toastDuration);
                        if (MediaComposerFragment.this.toolbarView != null) {
                            int[] xy = new int[2];
                            MediaComposerFragment.this.toolbarView.getLocationOnScreen(xy);
                            toast.setGravity(48, 0, Math.max(xy[1] / 2, xy[1] - ((int) ((((double) MediaComposerFragment.this.getResources().getDisplayMetrics().density) * 1.5d) * 64.0d))));
                        }
                        toast.show();
                        long durationMs = toastDuration == 1 ? 3500 : 2000;
                        removeMessages(2);
                        sendEmptyMessageDelayed(2, durationMs);
                        this.expectedToastEnd = SystemClock.elapsedRealtime() + durationMs;
                    }
                default:
            }
        }

        void postShowToast(int toastTextResId, int toastDuration) {
            Integer internedToastTextResId = getInternedInt(toastTextResId);
            if (!hasMessages(1, internedToastTextResId)) {
                long delayMs;
                removeMessages(1);
                Message showToastMsg = Message.obtain(this, 1, toastDuration, 0, internedToastTextResId);
                if (hasMessages(2)) {
                    delayMs = Math.max(this.expectedToastEnd - SystemClock.elapsedRealtime(), 0);
                } else {
                    delayMs = 0;
                }
                sendMessageDelayed(showToastMsg, delayMs);
            }
        }

        private Integer getInternedInt(int i) {
            Integer internedI = (Integer) this.internedInts.get(i);
            if (internedI != null) {
                return internedI;
            }
            internedI = Integer.valueOf(i);
            this.internedInts.put(i, internedI);
            return internedI;
        }
    }

    public MediaComposerFragment() {
        this.mediaTopicValidator = new OkApiMediaTopicValidator();
        this.canPostMediaTopic = null;
        this.mode = 1;
        this.isToStatusChecked = true;
        this.toastHandler = new ToastHandler();
        this.isNeedValidateMenu = false;
        this.settings = new MediaComposerSettings();
    }

    protected static Bundle createArgs(MediaComposerData mediaComposerData, Bundle extras) {
        Bundle args = new Bundle();
        args.putParcelable("media_composer_data", mediaComposerData);
        if (extras != null) {
            args.putAll(extras);
        }
        return args;
    }

    public void setListener(MediaComposerFragmentListener listener) {
        this.listener = listener;
    }

    protected void notifyMediaComposerCompleted(MediaComposerData data) {
        if (this.listener != null) {
            this.listener.onMediaComposerCompleted(data);
        }
    }

    public void replaceMediaTopicMessage(MediaTopicMessage message) {
        this.mediaComposerController.reset(message);
    }

    public void setMode(int mode) {
        if ((mode == 1 || mode == 2) && this.mode != mode) {
            this.mode = mode;
        }
        updateMode();
    }

    protected void updateMode() {
        if (this.mediaComposerView != null) {
            this.mediaComposerView.setEditable(this.mode == 1);
        }
        View fragmentView = getView();
        if (fragmentView == null) {
            return;
        }
        if (this.mode == 1) {
            showToolbar(fragmentView);
        } else {
            hideToolbar(fragmentView);
        }
    }

    public int getMode() {
        return this.mode;
    }

    public boolean isMediaTopicEmpty() {
        return this.mediaComposerController == null || this.mediaComposerController.isEmpty();
    }

    public boolean isToStatusChecked() {
        return this.isToStatusChecked;
    }

    public void onResume() {
        super.onResume();
        if (this.mediaComposerView != null) {
            this.mediaComposerView.onResume();
        }
        FragmentActivity activity = getActivity();
        if (this.isNeedValidateMenu && activity != null) {
            activity.supportInvalidateOptionsMenu();
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.mediaComposerView != null) {
            this.mediaComposerView.onDestroy();
        }
    }

    protected int getLayoutId() {
        return 2130903292;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.m172d("savedInstanceState=" + savedInstanceState);
        View rootView = inflater.inflate(getLayoutId(), container, false);
        this.mediaComposerView = (MediaComposerView) rootView.findViewById(2131625045);
        this.mediaComposerController = this.mediaComposerView.getController();
        this.mediaComposerView.setStatMode(getStatsMode());
        this.textPlace = (TextView) rootView.findViewById(2131625044);
        this.textPlace.setOnClickListener(this);
        this.mediaComposerController.setSettings(this.settings);
        this.mediaComposerController.setMediaTopicType(this.mediaTopicType);
        this.adapter = new MediaItemAdapter(getActivity(), this.mediaComposerView.getStyleParams(), this.fragmentBridge, this.mediaComposerController, this.mediaTopicType);
        this.mediaComposerView.setAdapter(this.adapter);
        this.mediaComposerView.setMediaComposerContentListener(this);
        String blankTextHint = getArguments().getString("blank_text_hint");
        if (blankTextHint != null) {
            this.mediaComposerView.setBlankTextHint(blankTextHint);
        }
        return rootView;
    }

    public void onCreate(Bundle savedInstanceState) {
        MediaComposerData mediaComposerData;
        this.fragmentBridge = new FragmentBridge(this);
        if (savedInstanceState != null) {
            mediaComposerData = (MediaComposerData) savedInstanceState.getParcelable("media_composer_data");
        } else {
            mediaComposerData = (MediaComposerData) getArguments().getParcelable("media_composer_data");
        }
        this.mediaTopicType = mediaComposerData.mediaTopicType;
        this.groupId = mediaComposerData.groupId;
        GlobalBus.register(this);
        if (savedInstanceState == null) {
            GlobalBus.send(2131624022, new BusEvent());
        }
        this.settings = MediaComposerSettings.fromSharedPreferences(ServicesSettingsHelper.getPreferences(getActivity()));
        MediaComposerSettingsProcessor.updateWithTestPreferences(getContext(), this.settings);
        super.onCreate(savedInstanceState);
    }

    public void onDestroy() {
        GlobalBus.unregister(this);
        super.onDestroy();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        MediaComposerData mediaComposerData;
        int mode;
        Bundle args = getArguments();
        if (savedInstanceState != null) {
            mediaComposerData = (MediaComposerData) savedInstanceState.getParcelable("media_composer_data");
            mode = savedInstanceState.getInt("mode");
        } else {
            mediaComposerData = (MediaComposerData) args.getParcelable("media_composer_data");
            mode = this.mode;
        }
        if (!(mediaComposerData == null || mediaComposerData.mediaTopicMessage == null)) {
            this.mediaComposerController.reset(mediaComposerData.mediaTopicMessage);
            this.isToStatusChecked = mediaComposerData.toStatus;
        }
        setMode(mode);
        if (this.mediaComposerController.getItemsCount() == 0) {
            this.mediaComposerController.addAfterEnd(MediaItem.emptyText());
        }
        setHasOptionsMenu(true);
    }

    private void showToolbar(View fragmentRootView) {
        View toolbar = fragmentRootView.findViewById(C0158R.id.toolbar);
        if (toolbar instanceof ViewStub) {
            toolbar = ((ViewStub) toolbar).inflate();
        }
        toolbar.setVisibility(0);
        ToolbarView toolbarView = (ToolbarView) toolbar;
        toolbarView.setMenu(this.mediaTopicType == MediaTopicType.USER ? 2131689501 : 2131689500);
        this.toolbarView = toolbarView;
        toolbarView.setListener(this);
        ToolbarMenu toolbarMenu = toolbarView.getMenu();
        ToolbarMenuItem toStatusItem = toolbarMenu.findItem(2131624350);
        if (toStatusItem != null) {
            toStatusItem.setChecked(this.isToStatusChecked);
            ToggleButton button = (ToggleButton) MenuItemCompat.getActionView(toStatusItem).findViewById(2131625055);
            String text = getStringLocalized(2131166086);
            button.setTextOn(text);
            button.setTextOff(text);
            button.setText(text);
        }
        ToolbarMenuItem withFriendsItem = toolbarMenu.findItem(2131625478);
        if (withFriendsItem != null) {
            View withFriendsView = MenuItemCompat.getActionView(withFriendsItem);
            if (withFriendsView != null) {
                this.withFriendsBubble = (BubbleTextIcon) withFriendsView.findViewById(2131624497);
            }
        }
        this.itemPlace = toolbarMenu.findItem(2131625439);
        updateWithFriendsCounter();
        updateWithPlace();
    }

    private void hideToolbar(View fragmentView) {
        View toolbar = fragmentView.findViewById(C0158R.id.toolbar);
        if (toolbar != null) {
            toolbar.setVisibility(8);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int resId;
        if (this.mode == 2) {
            resId = 2131689502;
        } else {
            resId = 2131689499;
        }
        if (inflateMenuLocalized(resId, menu)) {
            MenuItem sendItem = menu.findItem(2131625475);
            if (sendItem != null) {
                Button button = (Button) MenuItemCompat.getActionView(sendItem).findViewById(2131624595);
                String sendActionLabel = getArguments().getString("send_action_label");
                if (sendActionLabel != null) {
                    button.setText(sendActionLabel);
                }
                button.setOnClickListener(new C07981(menu));
            }
        }
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem post = menu.findItem(2131625475);
        if (post != null) {
            boolean isEnabled = this.mediaTopicValidator.canPost(this.mediaComposerController.getMediaTopicMessageUnsafe(), this.mediaTopicType, this.isToStatusChecked);
            post.setEnabled(isEnabled);
            ((Button) MenuItemCompat.getActionView(post).findViewById(2131624595)).setEnabled(isEnabled);
        }
    }

    protected void updateWithFriendsCounter() {
        if (this.withFriendsBubble != null && this.mediaComposerController != null) {
            ArrayList<String> withFriendsUids = this.mediaComposerController.getWithFriendsUids();
            int withFriendsCount = withFriendsUids == null ? 0 : withFriendsUids.size();
            this.withFriendsBubble.setBubbleText(Integer.toString(withFriendsCount));
            if (withFriendsCount > 0) {
                this.withFriendsBubble.showBubble();
            } else {
                this.withFriendsBubble.hideBubble();
            }
        }
    }

    protected void updateWithPlace() {
        Place place = this.mediaComposerController.getWithPlace();
        if (place != null) {
            if (this.itemPlace != null) {
                ((ImageView) MenuItemCompat.getActionView(this.itemPlace)).setImageResource(2130838260);
            }
            this.textPlace.setText(" " + place.name);
            this.textPlace.setVisibility(0);
            return;
        }
        if (this.itemPlace != null) {
            ((ImageView) MenuItemCompat.getActionView(this.itemPlace)).setImageResource(2130838131);
        }
        this.textPlace.setVisibility(8);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onOptionsItemSelected(android.view.MenuItem r9) {
        /*
        r8 = this;
        r5 = 1;
        r3 = new android.os.Bundle;
        r3.<init>();
        r6 = r9.getItemId();
        switch(r6) {
            case 2131624350: goto L_0x0069;
            case 2131625439: goto L_0x009e;
            case 2131625442: goto L_0x0041;
            case 2131625475: goto L_0x0065;
            case 2131625476: goto L_0x0012;
            case 2131625477: goto L_0x0053;
            case 2131625478: goto L_0x0084;
            default: goto L_0x000d;
        };
    L_0x000d:
        r5 = super.onOptionsItemSelected(r9);
    L_0x0011:
        return r5;
    L_0x0012:
        r4 = ru.ok.model.mediatopics.MediaItemType.PHOTO_BLOCK;
        r6 = r8.mediaComposerController;
        r6 = r6.getStats();
        r6 = r6.photoCount;
        r7 = r8.getStatsMode();
        ru.ok.android.statistics.mediacomposer.MediaComposerStats.toolbarPhoto(r6, r7);
    L_0x0023:
        r6 = r8.mediaComposerController;
        r6 = r6.getMaxAllowedBlockCount();
        if (r6 == 0) goto L_0x003b;
    L_0x002b:
        r6 = r8.mediaComposerController;
        r6 = r6.getBlockCount();
        r6 = r6 + 1;
        r7 = r8.mediaComposerController;
        r7 = r7.getMaxAllowedBlockCount();
        if (r6 >= r7) goto L_0x00b6;
    L_0x003b:
        r6 = r8.adapter;
        r6.startMediaAdd(r4, r3);
        goto L_0x0011;
    L_0x0041:
        r4 = ru.ok.model.mediatopics.MediaItemType.MUSIC;
        r6 = r8.mediaComposerController;
        r6 = r6.getStats();
        r6 = r6.trackCount;
        r7 = r8.getStatsMode();
        ru.ok.android.statistics.mediacomposer.MediaComposerStats.toolbarMusic(r6, r7);
        goto L_0x0023;
    L_0x0053:
        r4 = ru.ok.model.mediatopics.MediaItemType.POLL;
        r6 = r8.mediaComposerController;
        r6 = r6.getStats();
        r6 = r6.pollCount;
        r7 = r8.getStatsMode();
        ru.ok.android.statistics.mediacomposer.MediaComposerStats.toolbarPoll(r6, r7);
        goto L_0x0023;
    L_0x0065:
        r8.onPostSelected();
        goto L_0x0011;
    L_0x0069:
        r6 = r9.isChecked();
        r8.isToStatusChecked = r6;
        r6 = r8.isToStatusChecked;
        r8.showToStatusPopup(r6);
        r6 = r8.getStatsMode();
        ru.ok.android.statistics.mediacomposer.MediaComposerStats.toolbarStatus(r6);
        r8.onMediaComposerContentChanged();
        r6 = r8.isToStatusChecked;
        r8.notifyActivityOnStatusChanged(r6);
        goto L_0x0011;
    L_0x0084:
        r8.startSelectFriends();
        r6 = r8.mediaComposerController;
        r2 = r6.getWithFriendsUids();
        if (r2 != 0) goto L_0x0099;
    L_0x008f:
        r1 = 0;
    L_0x0090:
        r6 = r8.getStatsMode();
        ru.ok.android.statistics.mediacomposer.MediaComposerStats.toolbarCheckFriends(r1, r6);
        goto L_0x0011;
    L_0x0099:
        r1 = r2.size();
        goto L_0x0090;
    L_0x009e:
        r5 = r8.mediaComposerController;
        r5 = r5.getWithPlace();
        if (r5 != 0) goto L_0x00b2;
    L_0x00a6:
        r8.startSelectPlace();
    L_0x00a9:
        r5 = r8.getStatsMode();
        ru.ok.android.statistics.mediacomposer.MediaComposerStats.toolbarCheckPlace(r5);
        goto L_0x000d;
    L_0x00b2:
        r8.showSelectPlaceDialog();
        goto L_0x00a9;
    L_0x00b6:
        r6 = r8.mediaTopicType;
        r7 = "block_count";
        ru.ok.android.statistics.mediacomposer.MediaComposerStats.hitLimit(r6, r7);
        r0 = r8.getActivity();
        if (r0 == 0) goto L_0x0011;
    L_0x00c4:
        r6 = 2131166102; // 0x7f070396 float:1.794644E38 double:1.0529359566E-314;
        r6 = ru.ok.android.utils.localization.LocalizationManager.getString(r0, r6);
        r8.showAlertDialog(r6);
        goto L_0x0011;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.fragments.MediaComposerFragment.onOptionsItemSelected(android.view.MenuItem):boolean");
    }

    protected void onPostSelected() {
        if (checkLimits()) {
            complete();
        }
    }

    private boolean checkLimits() {
        int alertTextId = MediaTopicSettingsValidator.checkIsValid(this.mediaComposerController.getMediaTopicMessage(), this.mediaTopicType, this.settings);
        if (alertTextId == 0) {
            return true;
        }
        showLimitsAlert(alertTextId);
        return false;
    }

    private void showLimitsAlert(@StringRes int textResId) {
        Context activity = getActivity();
        if (activity != null) {
            showAlertDialog(LocalizationManager.getString(activity, textResId));
        }
    }

    private void showSelectPlaceDialog() {
        Builder adb = new Builder(getContext());
        adb.setItems(new CharSequence[]{LocalizationManager.getString(getContext(), 2131166365), LocalizationManager.getString(getContext(), 2131166366)}, new C07992());
        adb.setTitle(getStringLocalized(2131166369));
        adb.show();
    }

    public void onClick(View v) {
        showSelectPlaceDialog();
    }

    protected FragmentTransaction hideDialogs() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (String fragmentTag : allDialogFragmentTags) {
            Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
            if (fragment != null) {
                fragmentTransaction.remove(fragment);
            }
        }
        return fragmentTransaction;
    }

    void showAlertDialog(String message) {
        FragmentTransaction fragmentTransaction = hideDialogs();
        AlertFragmentDialog dialog = AlertFragmentDialog.newInstance(null, message, 20);
        dialog.setTargetFragment(this, 20);
        dialog.show(fragmentTransaction, "alert");
    }

    public void onToolbarItemSelected(MenuItem item) {
        onOptionsItemSelected(item);
    }

    public void onToolbarSubmenuOpened(MenuItem item) {
    }

    protected void complete() {
        MediaComposerData data;
        if (this.mediaTopicType == MediaTopicType.USER) {
            data = MediaComposerData.user(this.mediaComposerController.getMediaTopicMessage(), this.isToStatusChecked);
        } else if (this.mediaTopicType == MediaTopicType.GROUP_THEME) {
            data = MediaComposerData.group(this.groupId, this.mediaComposerController.getMediaTopicMessage());
        } else if (this.mediaTopicType == MediaTopicType.GROUP_SUGGESTED) {
            data = MediaComposerData.groupSuggested(this.groupId, this.mediaComposerController.getMediaTopicMessage());
        } else {
            data = null;
        }
        if (data != null) {
            MediaComposerStats.actionbarPost(data.mediaTopicMessage, data.toStatus, getStatsMode());
            notifyMediaComposerCompleted(data);
        }
    }

    private void startSelectFriends() {
        Intent selectFriends = new Intent();
        selectFriends.setClassName(getActivity(), "ru.ok.android.ui.users.activity.CheckFriendsFilteredActivity");
        UsersSelectionParams selectionParams = new UsersSelectionParams(this.mediaComposerController.getWithFriendsUids(), Integer.MAX_VALUE);
        selectFriends.putExtra(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, 2131166506);
        selectFriends.putExtra("selection_params", selectionParams);
        selectFriends.putExtra("select_target", 1);
        startActivityForResult(selectFriends, 6);
    }

    private void startSelectPlace() {
        Place place = this.mediaComposerController.getWithPlace();
        Intent selectPlace = new Intent(getActivity(), PlacesActivity.class);
        selectPlace.putExtra("place_input", place);
        startActivityForResult(selectPlace, 7);
    }

    private void deletePlace() {
        this.mediaComposerController.clearWithPlace();
        updateWithPlace();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.m173d("onActivityResult: requestCode=%d resultCode=%d data=%s", Integer.valueOf(requestCode), Integer.valueOf(resultCode), "" + data);
        switch (requestCode) {
            case MessagesProto.Message.REPLYTO_FIELD_NUMBER /*6*/:
                onSelectFriendsResult(resultCode, data);
            case MessagesProto.Message.ATTACHES_FIELD_NUMBER /*7*/:
                onSelectPlaceResult(resultCode, data);
            default:
                if (this.fragmentBridge.willHandleRequestCodeFromResult(requestCode)) {
                    this.fragmentBridge.onFragmentActivityResult(requestCode, resultCode, data);
                }
        }
    }

    protected void onSelectPlaceResult(int resultCode, Intent data) {
        if (resultCode == -1 && data != null && data.hasExtra("place_result")) {
            this.mediaComposerController.setWithPlace((Place) data.getParcelableExtra("place_result"));
            updateWithPlace();
        }
    }

    protected void onSelectFriendsResult(int resultCode, Intent data) {
        String str = "resultCode=%s data=%s";
        Object[] objArr = new Object[2];
        objArr[0] = resultCode == -1 ? "OK" : "NOT_OK";
        objArr[1] = data;
        Logger.m173d(str, objArr);
        if (resultCode == -1 && data != null && data.hasExtra("selected_ids")) {
            Collection selectedUids = data.getStringArrayListExtra("selected_ids");
            MediaComposerStats.checkFriends(this.mediaComposerController.getWithFriendsUids(), selectedUids, getStatsMode());
            this.mediaComposerController.setWithFriends(selectedUids);
            updateWithFriendsCounter();
            return;
        }
        MediaComposerStats.checkFriends(0, false, getStatsMode());
    }

    public void onMediaComposerContentChanged() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            boolean canPost = this.mediaTopicValidator.canPost(this.mediaComposerController.getMediaTopicMessage(), this.mediaTopicType, this.isToStatusChecked);
            if (this.canPostMediaTopic == null || this.canPostMediaTopic.get() != canPost) {
                if (isResumed()) {
                    activity.supportInvalidateOptionsMenu();
                } else {
                    this.isNeedValidateMenu = true;
                }
            }
            if (this.canPostMediaTopic == null) {
                this.canPostMediaTopic = new AtomicBoolean();
            }
            this.canPostMediaTopic.set(canPost);
        }
    }

    private void notifyActivityOnStatusChanged(boolean toStatusNewValue) {
        FragmentActivity activity = getActivity();
        if (activity instanceof OnToStatusChangedListener) {
            ((OnToStatusChangedListener) activity).onToStatusChanged(toStatusNewValue);
        }
    }

    private void showToStatusPopup(boolean isToStatus) {
        this.toastHandler.postShowToast(isToStatus ? 2131166181 : 2131166180, 0);
    }

    public void onSaveInstanceState(Bundle outState) {
        MediaComposerData data;
        super.onSaveInstanceState(outState);
        outState.putInt("mode", this.mode);
        if (this.mediaTopicType == MediaTopicType.USER) {
            data = MediaComposerData.user(this.mediaComposerController.getMediaTopicMessage(), this.isToStatusChecked);
        } else if (this.mediaTopicType == MediaTopicType.GROUP_THEME) {
            data = MediaComposerData.group(this.groupId, this.mediaComposerController.getMediaTopicMessage());
        } else if (this.mediaTopicType == MediaTopicType.GROUP_SUGGESTED) {
            data = MediaComposerData.groupSuggested(this.groupId, this.mediaComposerController.getMediaTopicMessage());
        } else {
            data = null;
        }
        outState.putParcelable("media_composer_data", data);
    }

    @Subscribe(on = 2131623946, to = 2131624196)
    public void onFetchedMediaComposerSettings(BusEvent event) {
        if (event.resultCode == -1) {
            MediaComposerSettings newSettings = (MediaComposerSettings) event.bundleOutput.getParcelable("result");
            Logger.m173d("newSettings=%s", newSettings);
            Context context = getContext();
            if (newSettings != null && context != null) {
                MediaComposerSettingsProcessor.updateWithTestPreferences(context, newSettings);
                this.settings = newSettings;
                if (this.mediaComposerController != null) {
                    this.mediaComposerController.setSettings(newSettings);
                    return;
                }
                return;
            }
            return;
        }
        Logger.m184w("Failed to fetch settings");
    }

    public void onAlertDismiss(int requestCode) {
        Logger.m172d("");
    }

    protected String getStatsMode() {
        return this instanceof MediaTopicEditorFragment ? "new" : "edit";
    }

    static {
        allDialogFragmentTags = new String[]{"alert"};
    }
}
