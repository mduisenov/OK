package ru.ok.model.search;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import ru.ok.model.GroupInfo;

public class SearchResultCommunity extends SearchResult {
    public static final Creator<SearchResultCommunity> CREATOR;
    private CommunityType communityType;
    private GroupInfo groupInfo;

    /* renamed from: ru.ok.model.search.SearchResultCommunity.1 */
    static class C15801 implements Creator<SearchResultCommunity> {
        C15801() {
        }

        public SearchResultCommunity createFromParcel(Parcel src) {
            SearchResultCommunity community = new SearchResultCommunity();
            community.readFromParcel(src);
            return community;
        }

        public SearchResultCommunity[] newArray(int count) {
            return new SearchResultCommunity[count];
        }
    }

    public enum CommunityType {
        UNKNOWN {
            public String getRemoteType() {
                return null;
            }
        },
        SCHOOL {
            public String getRemoteType() {
                return "searchType_school";
            }
        },
        UNIVERSITY {
            public String getRemoteType() {
                return "searchType_university";
            }
        },
        COLLEAGUE {
            public String getRemoteType() {
                return "searchType_colleage";
            }
        },
        ARMY {
            public String getRemoteType() {
                return "searchType_army";
            }
        },
        WORKPLACE {
            public String getRemoteType() {
                return "searchType_workplace";
            }
        },
        COMMUNITY {
            public String getRemoteType() {
                return "searchType_community";
            }
        };

        public abstract String getRemoteType();
    }

    public SearchResultCommunity() {
        this.communityType = CommunityType.UNKNOWN;
    }

    public GroupInfo getGroupInfo() {
        return this.groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public CommunityType getCommunityType() {
        return this.communityType;
    }

    public void setTypeMsg(String typeMsg) {
        super.setTypeMsg(typeMsg);
        CommunityType communityType = CommunityType.UNKNOWN;
        for (CommunityType type : CommunityType.values()) {
            if (TextUtils.equals(typeMsg, type.getRemoteType())) {
                communityType = type;
                break;
            }
        }
        this.communityType = communityType;
    }

    public SearchType getType() {
        return SearchType.COMMUNITY;
    }

    public final void readFromParcel(Parcel src) {
        super.readFromParcel(src);
        this.groupInfo = (GroupInfo) src.readParcelable(GroupInfo.class.getClassLoader());
        this.communityType = CommunityType.values()[src.readInt()];
    }

    public final void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.groupInfo, 0);
        dest.writeInt(this.communityType.ordinal());
    }

    static {
        CREATOR = new C15801();
    }
}
