package ru.ok.android.ui.stream.suggestions;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.ui.fragments.messages.view.PymkMutualFriendsView;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.StringUtils;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.UserInfo;
import ru.ok.onelog.search.SearchSuggestionType;
import ru.ok.onelog.search.SearchSuggestionsUsageFactory;

public class PymkPreviewHolder extends ViewHolder {
    private Context context;
    private final TextView pymkTextView;
    private ArrayList<UserInfo> pymkUsers;
    private final PymkMutualFriendsView pymkView;
    private final TextView showMoreTextView;

    /* renamed from: ru.ok.android.ui.stream.suggestions.PymkPreviewHolder.1 */
    class C12571 implements OnClickListener {
        final /* synthetic */ Activity val$activity;

        C12571(Activity activity) {
            this.val$activity = activity;
        }

        public void onClick(View v) {
            NavigationHelper.showDetailedPymk(this.val$activity);
            OneLog.log(SearchSuggestionsUsageFactory.get(SearchSuggestionType.pymk));
        }
    }

    public static View createView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(2130903507, parent, false);
    }

    public PymkPreviewHolder(View view, ArrayList<UserInfo> pymkUsers, Activity activity) {
        super(view);
        this.pymkUsers = pymkUsers;
        this.context = activity.getApplicationContext();
        this.pymkView = (PymkMutualFriendsView) view.findViewById(2131625374);
        this.pymkView.setMaxAvatars(6);
        this.showMoreTextView = (TextView) view.findViewById(2131625376);
        this.showMoreTextView.setText(LocalizationManager.getString(this.context, 2131166416));
        OnClickListener viewClickListener = new C12571(activity);
        view.setOnClickListener(viewClickListener);
        this.showMoreTextView.setOnClickListener(viewClickListener);
        this.pymkTextView = (TextView) view.findViewById(2131625375);
        updatePymkUsers(pymkUsers);
    }

    public void updatePymkUsers(ArrayList<UserInfo> pymkUsers) {
        this.pymkUsers = pymkUsers;
        this.pymkView.setParticipants((List) pymkUsers, false);
        updatePymkText(this.pymkTextView);
    }

    private void updatePymkText(TextView textView) {
        if (this.pymkUsers != null && this.pymkUsers.size() != 0) {
            StringBuilder text = new StringBuilder();
            int count = this.pymkUsers.size();
            int minCount = Math.min(count, 2);
            text.append(LocalizationManager.getString(this.context, 2131166417));
            for (int i = 0; i < minCount; i++) {
                if (i > 0) {
                    text.append(", ");
                } else {
                    text.append(" ");
                }
                text.append(((UserInfo) this.pymkUsers.get(i)).getConcatName());
            }
            int othersCount = count - 2;
            if (othersCount > 0) {
                text.append(" ").append(LocalizationManager.getString(this.context, 2131165387)).append(" ");
                int peopleCountTextId = StringUtils.plural((long) othersCount, 2131165646, 2131165644, 2131165645);
                text.append(LocalizationManager.getString(this.context, peopleCountTextId, Integer.valueOf(othersCount)));
            }
            textView.setText(text.toString());
        }
    }
}
