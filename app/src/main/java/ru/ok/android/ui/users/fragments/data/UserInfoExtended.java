package ru.ok.android.ui.users.fragments.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.util.Comparator;
import ru.ok.android.utils.filter.NameSplitter;
import ru.ok.android.utils.filter.TranslateNormalizer;
import ru.ok.model.UserInfo;

public final class UserInfoExtended {
    public static final Comparator<UserInfoExtended> COMPARATOR;
    private static final String[] ZERO_ARRAY;
    @NonNull
    public final String firstLetter;
    @Nullable
    private String firstNameSearch;
    @Nullable
    private String[] firstNameTokensSearch;
    @Nullable
    private String lastNameSearch;
    @Nullable
    private String[] lastNameTokensSearch;
    @NonNull
    public final String nameSorting;
    @NonNull
    public final UserInfo user;

    /* renamed from: ru.ok.android.ui.users.fragments.data.UserInfoExtended.1 */
    static class C13231 implements Comparator<UserInfoExtended> {
        C13231() {
        }

        public int compare(UserInfoExtended a, UserInfoExtended b) {
            return a.nameSorting.compareTo(b.nameSorting);
        }
    }

    static {
        COMPARATOR = new C13231();
        ZERO_ARRAY = new String[0];
    }

    public UserInfoExtended(@NonNull UserInfo user) {
        boolean useFakeFirstLetter = false;
        this.user = user;
        String name = TranslateNormalizer.normalizeText4Sorting(user.getAnyName());
        String letter = name.length() > 0 ? name.substring(0, 1).toUpperCase() : null;
        if (letter == null || !Character.isLetter(letter.charAt(0))) {
            useFakeFirstLetter = true;
        }
        if (useFakeFirstLetter) {
            letter = "#";
        }
        this.firstLetter = letter;
        if (useFakeFirstLetter) {
            name = "\udbff\udfff" + name;
        }
        this.nameSorting = name;
        this.firstNameSearch = TranslateNormalizer.normalizeText4Search(user.firstName);
        this.lastNameSearch = TranslateNormalizer.normalizeText4Search(user.lastName);
    }

    private static String[] splitName(String name) {
        String[] tokens = NameSplitter.split(name);
        if (tokens.length == 1 && tokens[0].length() == name.length()) {
            return ZERO_ARRAY;
        }
        return tokens;
    }

    public boolean isUserPassQuery(String query) {
        if (TextUtils.isEmpty(query)) {
            return true;
        }
        String firstName = this.user.firstName;
        if (firstName != null) {
            if (this.firstNameSearch == null) {
                this.firstNameSearch = TranslateNormalizer.normalizeText4Search(firstName);
            }
            if (this.firstNameSearch.startsWith(query)) {
                return true;
            }
        }
        String lastName = this.user.lastName;
        if (lastName != null) {
            if (this.lastNameSearch == null) {
                this.lastNameSearch = TranslateNormalizer.normalizeText4Search(lastName);
            }
            if (this.lastNameSearch.startsWith(query)) {
                return true;
            }
        }
        if (firstName != null) {
            if (this.firstNameTokensSearch == null) {
                this.firstNameTokensSearch = splitName(this.firstNameSearch);
            }
            for (String token : this.firstNameTokensSearch) {
                if (token.startsWith(query)) {
                    return true;
                }
            }
        }
        if (lastName != null) {
            if (this.lastNameTokensSearch == null) {
                this.lastNameTokensSearch = splitName(this.lastNameSearch);
            }
            for (String token2 : this.lastNameTokensSearch) {
                if (token2.startsWith(query)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean equals(Object o) {
        return (o instanceof UserInfoExtended) && ((UserInfoExtended) o).user.equals(this.user);
    }

    public int hashCode() {
        return this.user.hashCode();
    }
}
