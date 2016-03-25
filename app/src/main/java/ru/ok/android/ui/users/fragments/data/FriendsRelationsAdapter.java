package ru.ok.android.ui.users.fragments.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import java.util.List;
import java.util.Map;
import ru.ok.android.ui.adapters.spinner.BaseNavigationSpinnerAdapter;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.request.relatives.RelativesType;

public final class FriendsRelationsAdapter extends BaseNavigationSpinnerAdapter {
    static Map<RelativesType, Integer> RELATION_TEXT_RES_ID;
    private final LocalizationManager lm;
    private List<RelationItem> relations;

    public static class RelationItem {
        public final int count;
        @NonNull
        public final RelativesType type;

        public RelationItem(RelativesType type, int count) {
            this.type = type;
            this.count = count;
        }
    }

    static {
        RELATION_TEXT_RES_ID = new ArrayMap();
        RELATION_TEXT_RES_ID.put(RelativesType.ALL, Integer.valueOf(2131166446));
        RELATION_TEXT_RES_ID.put(RelativesType.LOVE, Integer.valueOf(2131166452));
        RELATION_TEXT_RES_ID.put(RelativesType.COLLEGUE, Integer.valueOf(2131166449));
        RELATION_TEXT_RES_ID.put(RelativesType.CLOSEFRIEND, Integer.valueOf(2131166448));
        RELATION_TEXT_RES_ID.put(RelativesType.CLASSMATE, Integer.valueOf(2131166447));
        RELATION_TEXT_RES_ID.put(RelativesType.CURSEMATE, Integer.valueOf(2131166451));
        RELATION_TEXT_RES_ID.put(RelativesType.COMPANIONINARMS, Integer.valueOf(2131166450));
        RELATION_TEXT_RES_ID.put(RelativesType.RELATIVE, Integer.valueOf(2131166453));
    }

    public FriendsRelationsAdapter(Context context) {
        super(context);
        this.lm = LocalizationManager.from(context);
    }

    protected String getItemText(int position) {
        if (this.lm == null) {
            return null;
        }
        Integer textResId = (Integer) RELATION_TEXT_RES_ID.get(getItem(position).type);
        if (textResId != null) {
            return this.lm.getString(textResId.intValue());
        }
        return null;
    }

    protected String getCountText(int position) {
        RelationItem relationItem = (RelationItem) this.relations.get(position);
        int count = relationItem.count;
        if (count != 0 || relationItem.type == RelativesType.ONLINE) {
            return String.valueOf(count);
        }
        return "";
    }

    public int getCount() {
        return this.relations != null ? this.relations.size() : 0;
    }

    public RelationItem getItem(int position) {
        return (RelationItem) this.relations.get(position);
    }

    public long getItemId(int position) {
        return (long) ((RelationItem) this.relations.get(position)).type.hashCode();
    }

    public void updateRelations(List<RelationItem> relations) {
        this.relations = relations;
        notifyDataSetChanged();
    }
}
