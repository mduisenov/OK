package ru.ok.android.ui.nativeRegistration;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.google.android.gms.common.ConnectionResult;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import ru.ok.android.bus.BusEvent;
import ru.ok.android.bus.GlobalBus;
import ru.ok.android.fresco.FrescoOdkl;
import ru.ok.android.fresco.FrescoOdkl.SideCrop;
import ru.ok.android.onelog.registration.RegistrationWorkflowLogHelper;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.ui.activity.BaseNoToolbarActivity;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.settings.Settings;
import ru.ok.onelog.builtin.Outcome;

public class FirstEnterActivity extends BaseNoToolbarActivity implements OnClickListener {
    private final int animationDuration;
    private int currentImage;
    SimpleDraweeView currentImageView;
    private View enterButton;
    private FrameLayout imageSwitcher;
    private final int imageSwitchingInterval;
    ArrayList<String> imageUrls;
    private View logo;
    private View needHelpButton;
    private NeedHelpDialog needHelpDialog;
    SimpleDraweeView nextImageView;
    private boolean permissionsAlreadyAsked;
    private View registerButton;
    private Timer timer;

    /* renamed from: ru.ok.android.ui.nativeRegistration.FirstEnterActivity.1 */
    class C10851 extends BaseControllerListener<ImageInfo> {
        final /* synthetic */ int val$nextImage;

        C10851(int i) {
            this.val$nextImage = i;
        }

        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            FirstEnterActivity.this.currentImage = this.val$nextImage;
            FirstEnterActivity.this.swapImages();
            FirstEnterActivity.this.startTimer();
        }

        public void onFailure(String id, Throwable throwable) {
            FirstEnterActivity.this.startTimer();
        }
    }

    class ImageSwitcherTask extends TimerTask {

        /* renamed from: ru.ok.android.ui.nativeRegistration.FirstEnterActivity.ImageSwitcherTask.1 */
        class C10861 implements Runnable {
            C10861() {
            }

            public void run() {
                FirstEnterActivity.this.showNextImage();
            }
        }

        ImageSwitcherTask() {
        }

        public void run() {
            FirstEnterActivity.this.runOnUiThread(new C10861());
        }
    }

    public FirstEnterActivity() {
        this.imageSwitchingInterval = 3000;
        this.animationDuration = ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED;
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CURRENT_IMAGE", this.currentImage);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case 2131624803:
                goToSettings();
            case 2131624819:
                goToLogin();
            case 2131624820:
                goToRegistration();
            case 2131624821:
                goToHelp();
            default:
        }
    }

    private void showNextImage() {
        cancelTimer();
        int nextImage = (this.currentImage + 1) % (this.imageUrls.size() + 1);
        if (nextImage < this.imageUrls.size()) {
            this.nextImageView.setController(((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setUri(Uri.parse((String) this.imageUrls.get(nextImage))).setControllerListener(new C10851(nextImage))).build());
            return;
        }
        this.currentImage = nextImage;
        this.nextImageView.setImageURI(null);
        swapImages();
        startTimer();
    }

    private void swapImages() {
        SimpleDraweeView buf = this.nextImageView;
        this.nextImageView = this.currentImageView;
        this.currentImageView = buf;
        this.currentImageView.setVisibility(0);
        this.nextImageView.setVisibility(4);
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        if (AuthorizationPreferences.getLoginScreenImageUrls() != null) {
            this.imageUrls = new ArrayList(AuthorizationPreferences.getLoginScreenImageUrls());
        }
        if (savedInstanceState != null) {
            this.currentImage = savedInstanceState.getInt("CURRENT_IMAGE");
        } else {
            if (this.imageUrls != null && this.imageUrls.size() > 0) {
                this.currentImage = this.imageUrls.size();
            }
            GlobalBus.send(2131623948, new BusEvent());
        }
        setContentView(2130903189);
        this.enterButton = findViewById(2131624819);
        this.registerButton = findViewById(2131624820);
        this.needHelpButton = findViewById(2131624821);
        this.logo = findViewById(2131624803);
        this.imageSwitcher = (FrameLayout) findViewById(2131624816);
        this.currentImageView = (SimpleDraweeView) findViewById(2131624817);
        this.currentImageView.setVisibility(0);
        FrescoOdkl.cropToSide(this.currentImageView, SideCrop.TOP_CENTER);
        this.nextImageView = (SimpleDraweeView) findViewById(2131624818);
        this.nextImageView.setVisibility(4);
        FrescoOdkl.cropToSide(this.nextImageView, SideCrop.TOP_CENTER);
        if (this.imageUrls == null || this.currentImage == this.imageUrls.size()) {
            this.currentImageView.setImageURI(null);
        } else {
            this.currentImageView.setImageURI(Uri.parse((String) this.imageUrls.get(this.currentImage)));
        }
        if (this.imageUrls != null && this.imageUrls.size() > 0) {
            LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.setDuration(1500);
            this.imageSwitcher.setLayoutTransition(layoutTransition);
        }
        initListeners();
    }

    private void initListeners() {
        this.enterButton.setOnClickListener(this);
        this.logo.setOnClickListener(this);
        this.registerButton.setOnClickListener(this);
        this.needHelpButton.setOnClickListener(this);
    }

    public void goToSettings() {
        if (false) {
            NavigationHelper.showSettings(this, true);
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(this, NativeLoginActivity.class);
        Intent intentFromIntent = NavigationHelper.getIntentFromIntent(getIntent());
        NavigationHelper.putIntentToIntent(intent, intentFromIntent);
        intent.putExtra("authorization", true);
        startActivity(intent);
        if (intentFromIntent != null && !"android.intent.action.MAIN".equals(intentFromIntent.getAction())) {
            finish();
        }
    }

    private void startRegistration() {
        NavigationHelper.goToRegistration(this);
        RegistrationWorkflowLogHelper.log(getClass(), Outcome.success);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            goToRegistration();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void goToRegistration() {
        if (AuthorizationPreferences.getNativeRegistrationEnabled()) {
            if (VERSION.SDK_INT >= 23) {
                String[] permissionsToRequest = AuthorizationPreferences.getInitNecessaryPermissions(getContext());
                if (!(this.permissionsAlreadyAsked || AuthorizationPreferences.getPermissionsRequestOnSeparateScreen() || permissionsToRequest.length <= 0)) {
                    this.permissionsAlreadyAsked = true;
                    ActivityCompat.requestPermissions(this, permissionsToRequest, 0);
                    return;
                }
            }
            startRegistration();
            return;
        }
        NavigationHelper.goToOldRegistration(this, 0);
    }

    private void goToHelp() {
        if (this.needHelpDialog == null) {
            this.needHelpDialog = new NeedHelpDialog();
        }
        if (!this.needHelpDialog.isAdded()) {
            this.needHelpDialog.show(getSupportFragmentManager(), null);
        }
    }

    private void cancelTimer() {
        this.timer.cancel();
        this.timer.purge();
    }

    protected void onResume() {
        super.onResume();
        if (Settings.hasLoginData(this)) {
            Intent intent = NavigationHelper.createIntentForOdklActivity(this);
            intent.setFlags(268468224);
            startActivity(intent);
            finish();
        } else if (this.imageUrls != null && this.imageUrls.size() > 0) {
            startTimer();
        }
    }

    private void startTimer() {
        this.timer = new Timer();
        this.timer.schedule(new ImageSwitcherTask(), 3000, 3000);
    }

    protected void onPause() {
        super.onPause();
        if (this.timer != null) {
            cancelTimer();
        }
    }
}
