package ru.ok.android.billing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import ru.mail.libverify.C0176R;
import ru.ok.android.ui.custom.toasts.TimeToast;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.BillingBuyItem;

public class BillingDialogFragment extends DialogFragment implements OnClickListener, OnPurchased {
    private OnDismissListener onDismissListener;
    private View viewProgress;

    public class BillingDialog extends AlertDialog {
        private int message;
        private int title;

        public BillingDialog(Context context) {
            super(context);
            this.message = 0;
            this.title = 0;
        }

        public void setMessage(int res) {
            this.message = res;
        }

        public void setTitle(int res) {
            this.title = res;
        }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LocalizationManager localizationManager = LocalizationManager.from(getContext());
            View view = LocalizationManager.inflate(getContext(), 2130903115, null, false);
            setContentView(view);
            BillingDialogFragment.this.viewProgress = findViewById(2131624654);
            findViewById(2131624653).setOnClickListener(BillingDialogFragment.this);
            if (this.title != 0) {
                ((TextView) view.findViewById(C0176R.id.title)).setText(localizationManager.getString(this.title));
            }
            if (this.message != 0) {
                ((TextView) view.findViewById(2131624538)).setText(localizationManager.getString(this.message));
            }
            for (int id : new int[]{2131624647, 2131624648, 2131624649, 2131624650}) {
                findViewById(id).setOnClickListener(BillingDialogFragment.this);
            }
            ((TextView) findViewById(2131624651)).setText(Html.fromHtml("<font color=#ffffff>100 OK</font><font color=#999999> + 5 OK</font>"));
            BillingDialogFragment.this.updateGui();
        }
    }

    public BillingDialogFragment() {
        this.viewProgress = null;
    }

    public void updateGui() {
        BillingHelper billingHelper = getBillingHelper();
        if (billingHelper == null || !billingHelper.isWork(billingHelper)) {
            setVisibilityToView(this.viewProgress, 4);
        } else {
            setVisibilityToView(this.viewProgress, 0);
        }
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (this.onDismissListener != null) {
            this.onDismissListener.onDismiss(dialog);
        }
    }

    public static void showBillingFragment(BillingActivity activity) {
        BillingDialogFragment fragment = new BillingDialogFragment();
        fragment.setOnDismissListener(activity);
        fragment.show(activity.getSupportFragmentManager(), null);
    }

    private static void setVisibilityToView(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BillingDialog billingDialog = new BillingDialog(getActivity());
        billingDialog.setTitle(2131165443);
        billingDialog.setMessage(2131165440);
        return billingDialog;
    }

    public void onPurchased(BillingBuyItem billingBuyItem) {
        Context activity = getActivity();
        if (activity != null) {
            TimeToast.show(activity, 2131165442, 1);
        }
    }

    public BillingHelper getBillingHelper() {
        BillingActivity activity = (BillingActivity) getActivity();
        return activity == null ? null : activity.getBillingHelper();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case 2131624647:
                BillingHelper.initializePaid(BillingBuyItem.PACKET_20, this);
                break;
            case 2131624648:
                BillingHelper.initializePaid(BillingBuyItem.PACKET_40, this);
                break;
            case 2131624649:
                BillingHelper.initializePaid(BillingBuyItem.PACKET_80, this);
                break;
            case 2131624650:
                BillingHelper.initializePaid(BillingBuyItem.PACKET_100, this);
                break;
            case 2131624653:
                Dialog dialog = getDialog();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    break;
                }
        }
        updateGui();
    }
}
