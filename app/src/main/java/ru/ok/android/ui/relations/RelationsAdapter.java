package ru.ok.android.ui.relations;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.adapters.ScrollLoadBlocker;
import ru.ok.android.ui.custom.RecyclerItemClickListenerController.OnItemClickListener;
import ru.ok.android.ui.custom.cards.listcard.CardItem;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter;
import ru.ok.android.ui.custom.cards.listcard.CardListAdapter.AbsListItem;
import ru.ok.android.ui.custom.cards.listcard.CardViewHolder;
import ru.ok.android.ui.custom.cards.search.UserViewsHolder;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.android.utils.Utils;
import ru.ok.android.utils.filter.NameSplitter;
import ru.ok.android.utils.filter.TranslateNormalizer;
import ru.ok.android.utils.localization.base.LocalizedActivity;
import ru.ok.android.utils.settings.Settings;
import ru.ok.java.api.request.relatives.RelativesType;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;
import ru.ok.model.UserInfo.UserOnlineType;

public class RelationsAdapter extends CardListAdapter implements OnItemClickListener {
    private static int[] NAMES_RELATIVES_MEN;
    private static int[] NAMES_RELATIVES_WOMEN;
    private static final List<RelativesType> RELATIVES_TYPE_LIST;
    private final ScrollLoadBlocker imageLoadBlocker;
    private boolean isFilterEnabled;
    private String normalizedQuery;
    private RelativesType relativesType;
    private Map<RelativesType, Set<String>> setMap;
    private Map<String, Set<RelativesType>> setMapSubtype;

    static {
        RELATIVES_TYPE_LIST = Arrays.asList(new RelativesType[]{RelativesType.PARENT, RelativesType.CHILD, RelativesType.BROTHERSISTER, RelativesType.UNCLEAUNT, RelativesType.NEPHEW, RelativesType.GRANDPARENT, RelativesType.GRANDCHILD, RelativesType.CHILDINLAW, RelativesType.GODPARENT, RelativesType.GODCHILD, RelativesType.SPOUSE, RelativesType.PARENTINLAW});
        NAMES_RELATIVES_MEN = new int[]{2131166321, 2131165578, 2131165450, 2131166743, 2131166250, 2131165927, 2131165925, 2131165580, 2131165919, 2131165917, 2131166618};
        NAMES_RELATIVES_WOMEN = new int[]{2131166322, 2131165579, 2131165451, 2131166744, 2131166251, 2131165928, 2131165926, 2131165581, 2131165920, 2131165918, 2131166619};
    }

    public void setRelativesType(RelativesType relativesType) {
        if (relativesType == null) {
            relativesType = RelativesType.ALL;
        }
        this.relativesType = relativesType;
        filter();
    }

    public RelativesType getRelativesType() {
        return this.relativesType;
    }

    public void setFilterEnabled(boolean isFilterEnabled) {
        this.isFilterEnabled = isFilterEnabled;
    }

    public RelationsAdapter(LocalizedActivity context) {
        super(context);
        this.setMap = new HashMap();
        this.setMapSubtype = new HashMap();
        this.imageLoadBlocker = ScrollLoadBlocker.forIdleAndTouchIdle();
        this.relativesType = RelativesType.ALL;
        this.normalizedQuery = null;
        this.isFilterEnabled = true;
        this.itemClickListenerController.addItemClickListener(this);
    }

    public void setData(List<CardItem> dataCard, Map<RelativesType, Set<String>> setMap, Map<String, Set<RelativesType>> setMapSubtype) {
        this.setMap = setMap;
        this.setMapSubtype = setMapSubtype;
        super.setData(dataCard);
    }

    public boolean headerIsEnable() {
        return !TextUtils.isEmpty(this.normalizedQuery);
    }

    public void postProcessItem(CardViewHolder viewHolder, int typeItem, Object object) {
        if (typeItem == 0 && this.relativesType == RelativesType.RELATIVE && this.setMapSubtype != null) {
            UserViewsHolder userViewsHolder = (UserViewsHolder) viewHolder;
            Context context = viewHolder.itemView.getContext();
            StringBuilder builder = new StringBuilder();
            UserInfo userInfo = (UserInfo) object;
            Set<RelativesType> types = (Set) this.setMapSubtype.get(userInfo.uid);
            if (types != null) {
                for (RelativesType type : types) {
                    if (builder.length() > 0) {
                        builder.append(",");
                    }
                    int index = RELATIVES_TYPE_LIST.indexOf(type);
                    if (index != -1) {
                        UserGenderType genderType;
                        if (userInfo.genderType == UserGenderType.FEMALE) {
                            genderType = Settings.getCurrentUser(context).genderType;
                            if (type == RelativesType.PARENTINLAW && genderType == UserGenderType.MALE) {
                                builder.append(this.activity.getStringLocalized(2131166325));
                            } else if (type == RelativesType.PARENTINLAW && genderType == UserGenderType.FEMALE) {
                                builder.append(this.activity.getStringLocalized(2131166326));
                            } else {
                                builder.append(this.activity.getStringLocalized(NAMES_RELATIVES_WOMEN[index]));
                            }
                        } else {
                            genderType = Settings.getCurrentUser(context).genderType;
                            if (type == RelativesType.PARENTINLAW && genderType == UserGenderType.MALE) {
                                builder.append(this.activity.getStringLocalized(2131166323));
                            } else if (type == RelativesType.PARENTINLAW && genderType == UserGenderType.FEMALE) {
                                builder.append(this.activity.getStringLocalized(2131166324));
                            } else {
                                builder.append(this.activity.getStringLocalized(NAMES_RELATIVES_MEN[index]));
                            }
                        }
                    }
                }
            }
            userViewsHolder.getInfoView().setText(builder.toString());
        }
    }

    public boolean isNeed(AbsListItem item) {
        if (!this.isFilterEnabled) {
            return true;
        }
        Set<String> set = this.setMap == null ? null : (Set) this.setMap.get(this.relativesType);
        switch (item.getType()) {
            case RECEIVED_VALUE:
                UserInfo userInfo = item.object;
                if (this.relativesType == RelativesType.ONLINE && Utils.onlineStatus(userInfo) != UserOnlineType.OFFLINE) {
                    return true;
                }
                if (this.relativesType == RelativesType.ALL || (set != null && set.contains(userInfo.uid))) {
                    return userNameQueryCheck(userInfo.firstName, userInfo.lastName);
                }
                return false;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                if (this.relativesType == RelativesType.ALL && !headerIsEnable()) {
                    return true;
                }
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                if (headerIsEnable()) {
                    return true;
                }
                break;
            case Message.UUID_FIELD_NUMBER /*5*/:
                if (headerIsEnable() || this.setMap == null) {
                    return true;
                }
        }
        return false;
    }

    private boolean userNameQueryCheck(String firstName, String lastName) {
        if (TextUtils.isEmpty(this.normalizedQuery)) {
            return true;
        }
        for (String name : new String[]{firstName, lastName}) {
            String nameNormalized = TranslateNormalizer.normalizeText4Search(name);
            if (nameNormalized.startsWith(this.normalizedQuery)) {
                return true;
            }
            for (String token : NameSplitter.split(nameNormalized)) {
                if (token.startsWith(this.normalizedQuery)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onItemClick(View view, int i) {
        if (getItem(i) instanceof UserInfo) {
            NavigationHelper.showUserInfo((Activity) view.getContext(), ((UserInfo) getItem(i)).uid);
        } else if (getItemViewType(i) == 3) {
            NavigationHelper.showPymk((Activity) view.getContext());
        }
    }
}
