package ru.ok.android.onelog.registration;

import java.util.EnumSet;
import java.util.Iterator;

public class ProfileErrorBuilder {
    private EnumSet<ProfileError> actualErrors;

    private enum ProfileError {
        N,
        S,
        BD,
        CT,
        PW,
        PWi
    }

    public ProfileErrorBuilder() {
        this.actualErrors = EnumSet.noneOf(ProfileError.class);
    }

    private void addOrRemoveError(ProfileError profileError, boolean add) {
        if (add) {
            this.actualErrors.add(profileError);
        } else {
            this.actualErrors.remove(profileError);
        }
    }

    public void setFirstNameEmpty(boolean isFirstNameEmpty) {
        addOrRemoveError(ProfileError.N, isFirstNameEmpty);
    }

    public void setLastNameEmpty(boolean isLastNameEmpty) {
        addOrRemoveError(ProfileError.S, isLastNameEmpty);
    }

    public void setBirthdayEmpty(boolean isBirthdayEmpty) {
        addOrRemoveError(ProfileError.BD, isBirthdayEmpty);
    }

    public void setPasswordEmpty(boolean isPasswordEmpty) {
        addOrRemoveError(ProfileError.PW, isPasswordEmpty);
        if (isPasswordEmpty) {
            addOrRemoveError(ProfileError.PWi, false);
        }
    }

    public void setPasswordInvalid(boolean isPasswordInvalid) {
        addOrRemoveError(ProfileError.PWi, isPasswordInvalid);
        if (isPasswordInvalid) {
            addOrRemoveError(ProfileError.PW, false);
        }
    }

    public boolean hasError() {
        return !this.actualErrors.isEmpty();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator i$ = this.actualErrors.iterator();
        while (i$.hasNext()) {
            ProfileError profileError = (ProfileError) i$.next();
            if (stringBuilder.length() > 0) {
                stringBuilder.append('_');
            }
            stringBuilder.append(profileError);
        }
        return stringBuilder.toString();
    }
}
