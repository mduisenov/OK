package ru.ok.android.ui.nativeRegistration;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.onelog.registration.RequestPermissionButton;
import ru.ok.onelog.registration.RequestPermissionButtonClickEventFactory;

public class ChooseRegistrationFragment extends BaseFragment implements OnClickListener, OnTouchListener {
    private boolean buttonClicked;
    private View handContainer;
    private float handOffset;
    private ImageView handShadow;

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCommunicationInterface((CommunicationInterface) getActivity());
        this.handOffset = getResources().getDimension(2131231023);
        View view = View.inflate(getContext(), 2130903127, null);
        this.handContainer = view.findViewById(2131624686);
        this.handShadow = (ImageView) view.findViewById(2131624687);
        View animatedButton = view.findViewById(2131624685);
        animatedButton.setOnTouchListener(this);
        animatedButton.setOnClickListener(this);
        view.findViewById(2131624690).setOnClickListener(this);
        view.findViewById(2131624691).setOnClickListener(this);
        return view;
    }

    protected void hideSpinner() {
    }

    protected void showSpinner() {
    }

    protected String getLogin() {
        return null;
    }

    public void onClick(View v) {
        RequestPermissionButton requestPermissionButton;
        switch (v.getId()) {
            case 2131624685:
            case 2131624690:
                if (!this.buttonClicked) {
                    if (v.getId() == 2131624685) {
                        requestPermissionButton = RequestPermissionButton.btn_allow;
                    } else {
                        requestPermissionButton = RequestPermissionButton.btn_continue;
                    }
                    this.buttonClicked = true;
                    String[] permissionsToRequest = AuthorizationPreferences.getNecessaryPermissions(getContext());
                    if (permissionsToRequest.length <= 0) {
                        this.communicationInterface.goToRegistration();
                        break;
                    } else {
                        requestPermissions(permissionsToRequest, 1);
                        break;
                    }
                }
                return;
            default:
                this.communicationInterface.goToRegistration();
                requestPermissionButton = RequestPermissionButton.btn_skip;
                break;
        }
        OneLog.log(RequestPermissionButtonClickEventFactory.get(requestPermissionButton));
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            this.buttonClicked = false;
            this.communicationInterface.goToRegistration();
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean onTouch(View v, MotionEvent event) {
        Animation animation = null;
        switch (event.getAction()) {
            case RECEIVED_VALUE:
                animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, this.handOffset);
                this.handShadow.animate().setDuration(200).alpha(0.0f).start();
                break;
            case Message.TEXT_FIELD_NUMBER /*1*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
            case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
            case Message.REPLYSTICKERS_FIELD_NUMBER /*12*/:
                animation = new TranslateAnimation(0.0f, 0.0f, this.handOffset, 0.0f);
                this.handShadow.animate().setDuration(200).alpha(1.0f).start();
                break;
        }
        if (animation != null) {
            animation.setDuration(200);
            animation.setFillAfter(true);
            this.handContainer.startAnimation(animation);
        }
        return false;
    }
}
