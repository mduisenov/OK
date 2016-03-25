package ru.ok.android.ui.messaging.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;
import com.afollestad.materialdialogs.C0047R;
import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;
import ru.ok.android.services.processors.stickers.StickersManager;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.activity.BaseActivity;
import ru.ok.android.ui.fragments.base.BaseFragment;
import ru.ok.android.ui.messaging.fragments.StickersPaymentFragment;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.java.api.json.stickers.StickersAvailabilityParser;
import ru.ok.java.api.request.stickers.StickersStatusRequest;

public final class PayStickersActivity extends BaseActivity {
    private StickersPaymentFragment paymentFragment;
    private ProgressFragment progressFragment;

    public static final class ProgressFragment extends BaseFragment {
        private AsyncTask<Void, Void, Long> checkStatusAsyncTask;
        private Runnable pendingResult;

        private class CheckStatusAsyncTask extends AsyncTask<Void, Void, Long> {
            private final boolean firstTime;

            /* renamed from: ru.ok.android.ui.messaging.activity.PayStickersActivity.ProgressFragment.CheckStatusAsyncTask.1 */
            class C10411 implements Runnable {
                final /* synthetic */ String val$message;

                C10411(String str) {
                    this.val$message = str;
                }

                public void run() {
                    Activity activity = ProgressFragment.this.getActivity();
                    if (activity != null) {
                        Toast.makeText(activity, this.val$message, 1).show();
                    }
                }
            }

            /* renamed from: ru.ok.android.ui.messaging.activity.PayStickersActivity.ProgressFragment.CheckStatusAsyncTask.2 */
            class C10422 implements Runnable {
                final /* synthetic */ Long val$deltaMs;

                C10422(Long l) {
                    this.val$deltaMs = l;
                }

                public void run() {
                    CheckStatusAsyncTask.this.deliverResult(this.val$deltaMs);
                }
            }

            private CheckStatusAsyncTask(boolean firstTime) {
                this.firstTime = firstTime;
            }

            protected Long doInBackground(Void... params) {
                try {
                    return Long.valueOf(StickersAvailabilityParser.parse(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new StickersStatusRequest())));
                } catch (Throwable e) {
                    String message;
                    Logger.m178e(e);
                    ErrorType errorType = ErrorType.fromException(e);
                    if (errorType == null || errorType == ErrorType.GENERAL) {
                        message = ProgressFragment.this.getStringLocalized(2131166626);
                    } else {
                        message = ProgressFragment.this.getStringLocalized(2131166627, ProgressFragment.this.getStringLocalized(errorType.getDefaultErrorMessage()));
                    }
                    ThreadUtil.executeOnMain(new C10411(message));
                    return null;
                }
            }

            protected void onPostExecute(Long deltaMs) {
                ProgressFragment.this.checkStatusAsyncTask = null;
                if (ProgressFragment.this.getActivity() == null) {
                    ProgressFragment.this.pendingResult = new C10422(deltaMs);
                } else if (!isCancelled()) {
                    deliverResult(deltaMs);
                }
            }

            private void deliverResult(Long deltaMs) {
                PayStickersActivity payStickersActivity = (PayStickersActivity) ProgressFragment.this.getActivity();
                if (deltaMs == null) {
                    payStickersActivity.onAvailabilityFailed();
                } else {
                    payStickersActivity.onAvailabilityFetched(deltaMs.longValue(), this.firstTime);
                }
            }
        }

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setMessage(getStringLocalized(2131165577));
            return dialog;
        }

        protected int getLayoutId() {
            return 0;
        }

        public void startChecking(boolean firstTime) {
            if (this.checkStatusAsyncTask == null) {
                this.checkStatusAsyncTask = new CheckStatusAsyncTask(firstTime, null);
                this.checkStatusAsyncTask.execute(new Void[]{(Void) null});
            }
        }

        public void processPendingResult() {
            if (this.pendingResult != null) {
                this.pendingResult.run();
                this.pendingResult = null;
            }
        }

        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            ((PayStickersActivity) getActivity()).onCancel();
        }

        public void onDestroy() {
            super.onDestroy();
            if (this.checkStatusAsyncTask != null) {
                this.checkStatusAsyncTask.cancel(true);
            }
        }
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        setContentView(2130903083);
        FragmentManager fm = getSupportFragmentManager();
        this.progressFragment = (ProgressFragment) fm.findFragmentByTag("progress-dialog");
        this.paymentFragment = (StickersPaymentFragment) fm.findFragmentByTag("payment-web");
        if (this.paymentFragment == null) {
            if (this.progressFragment == null) {
                this.progressFragment = new ProgressFragment();
                this.progressFragment.show(fm, "progress-dialog");
                this.progressFragment.startChecking(true);
                this.toolbar.setVisibility(8);
            } else {
                this.progressFragment.processPendingResult();
            }
        }
        setResult(0);
    }

    private void onAvailabilityFetched(long deltaMs, boolean firstTime) {
        Logger.m173d("Fetched delta: %s", Long.valueOf(deltaMs));
        StickersManager.updatePaymentEndDate(this, deltaMs);
        if (StickersManager.isServicePaid(this)) {
            setResult(-1);
            finish();
        } else if (firstTime) {
            FragmentTransaction remove = getSupportFragmentManager().beginTransaction().remove(this.progressFragment);
            Fragment stickersPaymentFragment = new StickersPaymentFragment();
            this.paymentFragment = stickersPaymentFragment;
            remove.add(C0047R.id.content, stickersPaymentFragment, "payment-web").commit();
            this.toolbar.setVisibility(0);
        } else {
            Toast.makeText(this, getStringLocalized(2131166628), 0).show();
            finish();
        }
    }

    private void onAvailabilityFailed() {
        Logger.m172d("");
        finish();
    }

    public void onCancel() {
        Logger.m172d("");
        finish();
    }

    public void onPaymentDone() {
        Logger.m172d("");
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        tr.remove(this.paymentFragment);
        this.paymentFragment = null;
        this.toolbar.setVisibility(8);
        if (this.progressFragment == null) {
            this.progressFragment = new ProgressFragment();
        }
        this.progressFragment.show(tr, "progress-dialog");
        this.progressFragment.startChecking(false);
    }

    public void onPaymentCancelled() {
        finish();
    }
}
