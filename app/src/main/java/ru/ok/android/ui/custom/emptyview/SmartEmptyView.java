package ru.ok.android.ui.custom.emptyview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import ru.ok.android.C0206R;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.NetUtils;
import ru.ok.android.utils.ThreadUtil;
import ru.ok.android.utils.localization.LocalizationManager;

public final class SmartEmptyView extends LinearLayout implements OnClickListener {
    private int emptyText;
    private int errorText;
    private OnRepeatClickListener listener;
    protected LocalState localState;
    private int noInternetText;
    private ProgressBar progressBar;
    private int progressText;
    private Button repeat;
    private Handler stateUpdateHandler;
    private TextView text;
    protected WebState webState;

    public interface OnRepeatClickListener {
        void onRetryClick(SmartEmptyView smartEmptyView);
    }

    /* renamed from: ru.ok.android.ui.custom.emptyview.SmartEmptyView.1 */
    class C06501 implements Runnable {
        final /* synthetic */ int val$fMessage;
        final /* synthetic */ WebState val$finalLocalWebState;

        C06501(WebState webState, int i) {
            this.val$finalLocalWebState = webState;
            this.val$fMessage = i;
        }

        public void run() {
            SmartEmptyView.this.progressBar.setVisibility(this.val$finalLocalWebState.progressBarVisibility);
            SmartEmptyView.this.repeat.setVisibility(this.val$finalLocalWebState.repeatVisibility);
            String msgText = this.val$fMessage == 0 ? null : LocalizationManager.getString(SmartEmptyView.this.getContext(), this.val$fMessage);
            if (TextUtils.isEmpty(msgText)) {
                SmartEmptyView.this.text.setVisibility(8);
                return;
            }
            SmartEmptyView.this.text.setText(msgText);
            SmartEmptyView.this.text.setVisibility(0);
        }
    }

    /* renamed from: ru.ok.android.ui.custom.emptyview.SmartEmptyView.2 */
    static /* synthetic */ class C06512 {
        static final /* synthetic */ int[] f93xdf80bfa7;

        static {
            f93xdf80bfa7 = new int[WebState.values().length];
            try {
                f93xdf80bfa7[WebState.EMPTY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f93xdf80bfa7[WebState.HAS_DATA.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f93xdf80bfa7[WebState.PROGRESS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f93xdf80bfa7[WebState.ERROR.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f93xdf80bfa7[WebState.NO_INTERNET.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f93xdf80bfa7[WebState.NO_INTERNET_DONT_WAIT_CONNECTION.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    public enum LocalState {
        PROGRESS,
        EMPTY,
        HAS_DATA
    }

    public enum WebState {
        EMPTY(8, 8),
        HAS_DATA(0, 8),
        PROGRESS(0, 8),
        ERROR(8, 8),
        NO_INTERNET(8, 0),
        NO_INTERNET_DONT_WAIT_CONNECTION(8, 0);
        
        private final int progressBarVisibility;
        private final int repeatVisibility;

        private WebState(int progressBarVisibility, int repeatVisibility) {
            this.progressBarVisibility = progressBarVisibility;
            this.repeatVisibility = repeatVisibility;
        }
    }

    public SmartEmptyView(Context context) {
        super(context);
        this.stateUpdateHandler = new Handler(Looper.getMainLooper());
        init(context);
        initDef();
        setGravity(17);
        setClickable(false);
        setWebState(WebState.PROGRESS);
    }

    public SmartEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.stateUpdateHandler = new Handler(Looper.getMainLooper());
        init(context);
        TypedArray a = context.obtainStyledAttributes(attrs, C0206R.styleable.SmartEmptyView);
        initValues(a);
        a.recycle();
        this.webState = WebState.PROGRESS;
        this.localState = LocalState.EMPTY;
        updateVisibility();
    }

    protected void initValues(TypedArray a) {
        this.emptyText = a.getResourceId(2, 2131165668);
        this.errorText = a.getResourceId(0, 2131165669);
        this.progressText = a.getResourceId(1, 2131165670);
        this.noInternetText = a.getResourceId(3, 2131165984);
    }

    private void initDef() {
        this.emptyText = 2131165668;
        this.errorText = 2131165669;
        this.progressText = 2131165670;
        this.noInternetText = 2131165984;
    }

    public void setErrorText(int errorText) {
        this.errorText = errorText;
    }

    public void setNoInternetErrorText(int errorText) {
        this.noInternetText = errorText;
    }

    private void init(Context context) {
        LocalizationManager.from(context);
        LocalizationManager.inflate(context, 2130903279, (ViewGroup) this, true);
        this.text = (TextView) findViewById(C0263R.id.text);
        this.progressBar = (ProgressBar) findViewById(2131624548);
        this.repeat = (Button) findViewById(2131624865);
        this.repeat.setOnClickListener(this);
        setOrientation(1);
    }

    public void setEmptyText(int res) {
        this.emptyText = res;
        setWebState(this.webState);
    }

    public void setLocalState(LocalState localState) {
        this.localState = localState;
        updateVisibility();
    }

    public void setWebState(WebState webState) {
        this.webState = preprocessWebState(webState);
        updateVisibility();
    }

    private void updateVisibility() {
        int message;
        WebState localWebState = this.webState;
        if (this.localState == LocalState.PROGRESS) {
            localWebState = WebState.PROGRESS;
        }
        switch (C06512.f93xdf80bfa7[localWebState.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                message = this.emptyText;
                break;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
            case Message.TYPE_FIELD_NUMBER /*3*/:
                message = this.progressText;
                break;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                message = this.errorText;
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                message = this.noInternetText;
                break;
            default:
                throw new IllegalArgumentException("Don't know " + localWebState);
        }
        Runnable runnable = new C06501(localWebState, message);
        if (localWebState == WebState.NO_INTERNET || localWebState == WebState.NO_INTERNET_DONT_WAIT_CONNECTION) {
            this.stateUpdateHandler.postDelayed(runnable, 500);
        } else {
            ThreadUtil.executeOnMain(runnable);
        }
    }

    private WebState preprocessWebState(WebState state) {
        return (state != WebState.ERROR || NetUtils.isConnectionAvailable(getContext(), false)) ? state : WebState.NO_INTERNET;
    }

    public WebState getWebState() {
        return this.webState;
    }

    public void setOnRepeatClickListener(OnRepeatClickListener listener) {
        this.listener = listener;
    }

    public void onClick(View v) {
        onRetryClicked();
    }

    public void onRetryClicked() {
        if (this.listener != null) {
            this.listener.onRetryClick(this);
        }
    }
}
