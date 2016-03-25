package ru.ok.android.ui.users.fragments.profiles;

import ru.ok.android.services.processors.base.CommandProcessor.ErrorType;

public interface ProfileLoadCallBack {

    public static final class ProfileAccessInfo {
        public final boolean isBlocked;
        public final boolean isDisabled;
        public final boolean isFriendOrMember;
        public final boolean isPrivate;

        public ProfileAccessInfo(boolean isBlocked, boolean isPrivate, boolean isFriendOrMember, boolean isDisabled) {
            this.isBlocked = isBlocked;
            this.isPrivate = isPrivate;
            this.isFriendOrMember = isFriendOrMember;
            this.isDisabled = isDisabled;
        }

        public ProfileAccessInfo(boolean isBlocked, boolean isPrivate, boolean isFriendOrMember) {
            this(isBlocked, isPrivate, isFriendOrMember, false);
        }

        public boolean canAccess() {
            return (this.isDisabled || this.isBlocked || (this.isPrivate && !this.isFriendOrMember)) ? false : true;
        }

        public String toString() {
            return "ProfileAccessInfo[isBlocked=" + this.isBlocked + " isPrivate=" + this.isPrivate + " isFriendOrMember=" + this.isFriendOrMember + "]";
        }

        public boolean equals(Object o) {
            if (o == null || !(o instanceof ProfileAccessInfo)) {
                return false;
            }
            ProfileAccessInfo other = (ProfileAccessInfo) o;
            if (this.isBlocked == other.isBlocked && this.isPrivate == other.isPrivate && this.isFriendOrMember == other.isFriendOrMember && this.isDisabled == other.isDisabled) {
                return true;
            }
            return false;
        }
    }

    public enum ProfileType {
        USER,
        GROUP
    }

    void onProfileInfoLoad(ProfileType profileType, ProfileAccessInfo profileAccessInfo);

    void onProfileInfoLoadError(ProfileType profileType, ErrorType errorType);

    void onProfileRefresh(ProfileType profileType, ProfileAccessInfo profileAccessInfo);
}
