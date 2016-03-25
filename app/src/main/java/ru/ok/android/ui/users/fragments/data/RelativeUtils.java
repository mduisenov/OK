package ru.ok.android.ui.users.fragments.data;

import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import java.util.Comparator;
import java.util.Map;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.ui.users.fragments.data.FriendsRelationsAdapter.RelationItem;
import ru.ok.java.api.request.relatives.RelativesType;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserGenderType;

public final class RelativeUtils {
    private static final int[] PARENTINLAW_FOR_FEMALE;
    private static final int[] PARENTINLAW_FOR_MALE;
    static final Comparator<? super RelationItem> RELATIONS_COMPARATOR;
    public static final Map<RelativesType, Integer> RELATIONS_ORDER;
    private static final Map<RelativesType, int[]> RELATIVES_TYPE_LIST;

    /* renamed from: ru.ok.android.ui.users.fragments.data.RelativeUtils.1 */
    static class C13221 implements Comparator<RelationItem> {
        C13221() {
        }

        public int compare(RelationItem a, RelationItem b) {
            return ((Integer) RelativeUtils.RELATIONS_ORDER.get(a.type)).compareTo((Integer) RelativeUtils.RELATIONS_ORDER.get(b.type));
        }
    }

    static {
        RELATIONS_COMPARATOR = new C13221();
        PARENTINLAW_FOR_MALE = new int[]{2131166323, 2131166324};
        PARENTINLAW_FOR_FEMALE = new int[]{2131166325, 2131166326};
        RELATIVES_TYPE_LIST = new ArrayMap();
        RELATIVES_TYPE_LIST.put(RelativesType.PARENT, new int[]{2131166321, 2131166322});
        RELATIVES_TYPE_LIST.put(RelativesType.CHILD, new int[]{2131165578, 2131165579});
        RELATIVES_TYPE_LIST.put(RelativesType.BROTHERSISTER, new int[]{2131165450, 2131165451});
        RELATIVES_TYPE_LIST.put(RelativesType.UNCLEAUNT, new int[]{2131166743, 2131166744});
        RELATIVES_TYPE_LIST.put(RelativesType.NEPHEW, new int[]{2131166250, 2131166251});
        RELATIVES_TYPE_LIST.put(RelativesType.GRANDPARENT, new int[]{2131165927, 2131165928});
        RELATIVES_TYPE_LIST.put(RelativesType.GRANDCHILD, new int[]{2131165925, 2131165926});
        RELATIVES_TYPE_LIST.put(RelativesType.CHILDINLAW, new int[]{2131165580, 2131165581});
        RELATIVES_TYPE_LIST.put(RelativesType.GODPARENT, new int[]{2131165919, 2131165920});
        RELATIVES_TYPE_LIST.put(RelativesType.GODCHILD, new int[]{2131165917, 2131165918});
        RELATIVES_TYPE_LIST.put(RelativesType.SPOUSE, new int[]{2131166618, 2131166619});
        RELATIONS_ORDER = new ArrayMap();
        RELATIONS_ORDER.put(RelativesType.ALL, Integer.valueOf(0));
        RELATIONS_ORDER.put(RelativesType.LOVE, Integer.valueOf(1));
        RELATIONS_ORDER.put(RelativesType.COLLEGUE, Integer.valueOf(2));
        RELATIONS_ORDER.put(RelativesType.CLOSEFRIEND, Integer.valueOf(3));
        RELATIONS_ORDER.put(RelativesType.CLASSMATE, Integer.valueOf(4));
        RELATIONS_ORDER.put(RelativesType.CURSEMATE, Integer.valueOf(5));
        RELATIONS_ORDER.put(RelativesType.COMPANIONINARMS, Integer.valueOf(6));
        RELATIONS_ORDER.put(RelativesType.RELATIVE, Integer.valueOf(7));
    }

    public static int getRelativeTextResourceId(@Nullable RelativesType relativesType, @Nullable UserInfo user) {
        if (relativesType == null || user == null) {
            return 0;
        }
        int arrayIndex;
        if (user.genderType == UserGenderType.FEMALE) {
            arrayIndex = 1;
        } else {
            arrayIndex = 0;
        }
        if (relativesType == RelativesType.PARENTINLAW) {
            return (OdnoklassnikiApplication.getCurrentUser().genderType == UserGenderType.FEMALE ? PARENTINLAW_FOR_FEMALE : PARENTINLAW_FOR_MALE)[arrayIndex];
        }
        int[] ids = (int[]) RELATIVES_TYPE_LIST.get(relativesType);
        if (ids != null) {
            return ids[arrayIndex];
        }
        return 0;
    }
}
