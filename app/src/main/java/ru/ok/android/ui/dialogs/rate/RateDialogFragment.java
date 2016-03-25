package ru.ok.android.ui.dialogs.rate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import ru.mail.libverify.C0176R;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.onelog.rate.RateDialogCancelFactory;
import ru.ok.onelog.rate.RateDialogNegativeFactory;
import ru.ok.onelog.rate.RateDialogPositiveFactory;

public class RateDialogFragment extends DialogFragment implements OnClickListener {
    private static WeakReference<DialogFragment> dialogFragment;
    private String nameFicha;
    private int resNeg;
    private int resNeu;
    private int resPos;
    private int resText;
    private int resTitle;

    public static class RateDialogImpl extends AlertDialog implements View.OnClickListener {
        private int message;
        private OnClickListener onClickListenerNeg;
        private OnClickListener onClickListenerNeu;
        private OnClickListener onClickListenerPos;
        private int resNeg;
        private int resNeu;
        private int resPos;
        private int title;

        public void setMessage(int res) {
            this.message = res;
        }

        public void setTitle(int res) {
            this.title = res;
        }

        public void setPositiveButton(int resText, OnClickListener onClickListenerPos) {
            this.onClickListenerPos = onClickListenerPos;
            this.resPos = resText;
        }

        public void setNegativeButton(int resText, OnClickListener onClickListenerNeg) {
            this.onClickListenerNeg = onClickListenerNeg;
            this.resNeg = resText;
        }

        public void setNeutralButton(int resText, OnClickListener onClickListenerNeu) {
            this.onClickListenerNeu = onClickListenerNeu;
            this.resNeu = resText;
        }

        public RateDialogImpl(Context context) {
            super(context);
            this.onClickListenerPos = null;
            this.onClickListenerNeg = null;
            this.onClickListenerNeu = null;
            this.resPos = 0;
            this.resNeg = 0;
            this.resNeu = 0;
            this.message = 0;
            this.title = 0;
        }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LocalizationManager localizationManager = LocalizationManager.from(getContext());
            View view = LocalizationManager.inflate(getContext(), 2130903413, null, false);
            setContentView(view);
            View viewPos = view.findViewById(2131625273);
            View viewNeg = view.findViewById(2131625275);
            viewPos.setOnClickListener(this);
            viewNeg.setOnClickListener(this);
            if (this.resNeu != 0) {
                TextView textNeu = (TextView) view.findViewById(2131624653);
                textNeu.setText(localizationManager.getString(this.resNeu));
                textNeu.setOnClickListener(this);
            }
            if (this.resNeg != 0) {
                ((TextView) view.findViewById(2131625276)).setText(localizationManager.getString(this.resNeg));
            }
            if (this.resPos != 0) {
                ((TextView) view.findViewById(2131625274)).setText(localizationManager.getString(this.resPos));
            }
            if (this.title != 0) {
                ((TextView) view.findViewById(C0176R.id.title)).setText(localizationManager.getString(this.title));
            }
            if (this.message != 0) {
                ((TextView) view.findViewById(2131624538)).setText(localizationManager.getString(this.message));
            }
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case 2131624653:
                    if (this.onClickListenerNeu != null) {
                        this.onClickListenerNeu.onClick(this, -3);
                    }
                case 2131625273:
                    if (this.onClickListenerPos != null) {
                        this.onClickListenerPos.onClick(this, -1);
                    }
                case 2131625275:
                    if (this.onClickListenerNeg != null) {
                        this.onClickListenerNeg.onClick(this, -2);
                    }
                default:
            }
        }
    }

    public RateDialogFragment() {
        this.resPos = 0;
        this.resNeg = 0;
        this.resNeu = 0;
        this.resTitle = 0;
        this.resText = 0;
    }

    static {
        dialogFragment = null;
    }

    public static RateDialogFragment newInstance(String nameFicha, int resPos, int resNeg, int resNeu, int resTitle, int resText) {
        RateDialogFragment fragment = new RateDialogFragment();
        fragment.nameFicha = nameFicha;
        fragment.resPos = resPos;
        fragment.resNeg = resNeg;
        fragment.resNeu = resNeu;
        fragment.resTitle = resTitle;
        fragment.resText = resText;
        return fragment;
    }

    public void show(FragmentManager manager, String tag) {
        if ((dialogFragment == null ? null : (DialogFragment) dialogFragment.get()) == null) {
            dialogFragment = new WeakReference(this);
            try {
                super.show(manager, tag);
            } catch (Throwable e) {
                Logger.m178e(e);
            }
        }
    }

    public void onDismiss(DialogInterface dialog) {
        dialogFragment = null;
        super.onDismiss(dialog);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        RateDialogImpl rateDialog = new RateDialogImpl(getActivity());
        rateDialog.setTitle(this.resTitle);
        rateDialog.setOnCancelListener(this);
        if (this.resPos != 0) {
            rateDialog.setPositiveButton(this.resPos, this);
        }
        if (this.resNeg != 0) {
            rateDialog.setNegativeButton(this.resNeg, this);
        }
        if (this.resNeu != 0) {
            rateDialog.setNeutralButton(this.resNeu, this);
        }
        rateDialog.setMessage(this.resText);
        return rateDialog;
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        OneLog.log(RateDialogCancelFactory.get(this.nameFicha));
    }

    public void onClick(DialogInterface dialog, int which) {
        Activity activity = getActivity();
        if (activity != null) {
            RateDialog.setHasBeenShownDialog(activity, this.nameFicha);
            switch (which) {
                case PagerAdapter.POSITION_NONE /*-2*/:
                    OneLog.log(RateDialogNegativeFactory.get(this.nameFicha));
                    NavigationHelper.showFeedbackPage(activity, false);
                    break;
                case RecyclerView.NO_POSITION /*-1*/:
                    OneLog.log(RateDialogPositiveFactory.get(this.nameFicha));
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse("market://details?id=" + activity.getPackageName()));
                    activity.startActivity(intent);
                    break;
            }
            dialog.dismiss();
        }
    }
}
