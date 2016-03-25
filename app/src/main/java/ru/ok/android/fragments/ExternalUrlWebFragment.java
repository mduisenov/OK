package ru.ok.android.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import ru.ok.android.emoji.C0263R;
import ru.ok.android.fragments.web.WebBaseFragment.DefaultWebViewClient;
import ru.ok.android.fragments.web.WebFragment;
import ru.ok.android.widget.menuitems.SlidingMenuHelper.Type;

public class ExternalUrlWebFragment extends WebFragment implements OnClickListener {
    private MenuItem refreshItem;
    private View rotateView;

    class ExternalWebViewClient extends DefaultWebViewClient {
        public ExternalWebViewClient(Context context) {
            super(context);
        }

        public boolean isExternalUrl(String url) {
            return false;
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.refreshProvider.setRefreshEnabled(false);
    }

    public static Bundle newArguments(String url) {
        Bundle args = new Bundle();
        args.putString("URL", url);
        return args;
    }

    public static Bundle newArguments(String url, Type type) {
        Bundle args = newArguments(url);
        if (type != null) {
            args.putString("TYPE", type.name());
        }
        return args;
    }

    public Type getType() {
        String typeName = getArguments().getString("TYPE");
        if (TextUtils.isEmpty(typeName)) {
            return null;
        }
        return Type.valueOf(typeName);
    }

    public String getStartUrl() {
        return getArguments().getString("URL");
    }

    public DefaultWebViewClient createWebViewClient() {
        return new ExternalWebViewClient(getContext());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.refreshItem = menu.findItem(2131625510);
        if (this.refreshItem != null) {
            MenuItemCompat.setShowAsAction(this.refreshItem, 2);
            View actionView = LayoutInflater.from(getActivity()).inflate(2130903317, null);
            actionView.setOnClickListener(this);
            MenuItemCompat.setActionView(this.refreshItem, actionView);
            this.rotateView = actionView.findViewById(C0263R.id.image);
        }
    }

    public void showLoadDialog() {
        super.showLoadDialog();
        if (this.rotateView != null) {
            ObjectAnimator animator = (ObjectAnimator) this.rotateView.getTag();
            if (animator == null) {
                animator = ObjectAnimator.ofFloat(this.rotateView, "rotation", new float[]{0.0f, 360.0f});
                animator.setRepeatCount(-1);
                animator.setDuration(1000);
                this.rotateView.setTag(animator);
            }
            animator.cancel();
            animator.start();
        }
    }

    public void refreshCompleted() {
        super.refreshCompleted();
        if (this.rotateView != null) {
            ObjectAnimator animator = (ObjectAnimator) this.rotateView.getTag();
            if (animator != null) {
                animator.cancel();
            }
        }
    }

    public void onClick(View v) {
        performRefresh();
    }
}
