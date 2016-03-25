package ru.ok.model.stream.banner;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.ok.android.C0206R;
import ru.ok.android.proto.ConversationProto.Conversation;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.utils.Logger;

public class StatPixelHolderImpl implements Parcelable, StatPixelHolder {
    public static final Creator<StatPixelHolderImpl> CREATOR;
    @Nullable
    ArrayList<String>[] pixels;

    /* renamed from: ru.ok.model.stream.banner.StatPixelHolderImpl.1 */
    static class C16071 implements Creator<StatPixelHolderImpl> {
        C16071() {
        }

        public StatPixelHolderImpl createFromParcel(Parcel source) {
            return new StatPixelHolderImpl(source);
        }

        public StatPixelHolderImpl[] newArray(int size) {
            return new StatPixelHolderImpl[size];
        }
    }

    public <T extends StatPixelHolder> T copyTo(T outHolder) {
        if (this.pixels != null) {
            for (int type = 0; type < this.pixels.length; type++) {
                if (this.pixels[type] != null) {
                    outHolder.addStatPixels(type, this.pixels[type]);
                }
            }
        }
        return outHolder;
    }

    public void addStatPixel(int type, String url) {
        if (type < 0 || type >= 29) {
            Logger.m185w("Invalid statistics type: %d", Integer.valueOf(type));
            return;
        }
        if (this.pixels == null) {
            this.pixels = new ArrayList[29];
        }
        ArrayList<String> urls = this.pixels[type];
        if (urls == null) {
            urls = new ArrayList();
            this.pixels[type] = urls;
        }
        urls.add(url);
    }

    public void addStatPixels(int type, Collection<String> statUrls) {
        if (type < 0 || type >= 29) {
            Logger.m185w("Invalid statistics type: %d", Integer.valueOf(type));
        } else if (statUrls != null && !statUrls.isEmpty()) {
            if (this.pixels == null) {
                this.pixels = new ArrayList[29];
            }
            ArrayList<String> urls = this.pixels[type];
            if (urls == null) {
                urls = new ArrayList();
                this.pixels[type] = urls;
            }
            urls.addAll(statUrls);
        }
    }

    @Nullable
    public ArrayList<String> getStatPixels(int type) {
        if (type < 0 || type >= 29) {
            Logger.m185w("Invalid statistics type: %d", Integer.valueOf(type));
            return null;
        } else if (this.pixels != null) {
            return this.pixels[type];
        } else {
            return null;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StatPixelHolderImpl[").append('\n');
        for (int type = 0; type < 29; type++) {
            ArrayList<String> urls = getStatPixels(type);
            if (urls != null) {
                sb.append("  ").append(pixelTypeToString(type)).append(": ").append(urls).append('\n');
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pixels == null ? 0 : 1);
        if (this.pixels != null) {
            dest.writeInt(size);
            for (List writeList : this.pixels) {
                dest.writeList(writeList);
            }
        }
    }

    StatPixelHolderImpl(Parcel src) {
        if (src.readInt() != 0) {
            ClassLoader cl = StatPixelHolderImpl.class.getClassLoader();
            int size = src.readInt();
            this.pixels = new ArrayList[size];
            for (int i = 0; i < size; i++) {
                this.pixels[i] = src.readArrayList(cl);
            }
        }
    }

    static {
        CREATOR = new C16071();
    }

    private static String pixelTypeToString(int type) {
        switch (type) {
            case RECEIVED_VALUE:
                return "shown";
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return "shownOnScroll";
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return "onClick";
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return "authorClick";
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return "like";
            case Message.UUID_FIELD_NUMBER /*5*/:
                return "linkExt";
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return "playMusic";
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                return "vote";
            case Message.TASKID_FIELD_NUMBER /*8*/:
                return "comment";
            case Message.UPDATETIME_FIELD_NUMBER /*9*/:
                return "join";
            case Message.FAILUREREASON_FIELD_NUMBER /*10*/:
                return "playVideo";
            case Message.EDITINFO_FIELD_NUMBER /*11*/:
                return "playheadReachedValue3";
            case Message.REPLYSTICKERS_FIELD_NUMBER /*12*/:
                return "playheadReachedValue25";
            case Conversation.OWNERID_FIELD_NUMBER /*13*/:
                return "playheadReachedValue50";
            case C0206R.styleable.Toolbar_titleMarginEnd /*14*/:
                return "playheadReachedValue75";
            case C0206R.styleable.Toolbar_titleMarginTop /*15*/:
                return "playheadReachedValue95";
            case C0206R.styleable.Toolbar_titleMarginBottom /*16*/:
                return "playheadReachedValue100";
            case C0206R.styleable.Toolbar_maxButtonHeight /*17*/:
                return "playbackCompleted";
            case C0206R.styleable.Toolbar_collapseIcon /*18*/:
                return "volumeOff";
            case C0206R.styleable.Toolbar_collapseContentDescription /*19*/:
                return "volumeOn";
            case C0206R.styleable.Toolbar_navigationIcon /*20*/:
                return "playbackPaused";
            case C0206R.styleable.Toolbar_navigationContentDescription /*21*/:
                return "playbackResumed";
            case C0206R.styleable.Toolbar_logoDescription /*22*/:
                return "fullscreenOn";
            case C0206R.styleable.Toolbar_titleTextColor /*23*/:
                return "fullscreenOff";
            case C0206R.styleable.Toolbar_subtitleTextColor /*24*/:
                return "playVideoVolumeOn";
            case C0206R.styleable.Theme_actionMenuTextAppearance /*25*/:
                return "playVideoVolumeOff";
            case C0206R.styleable.Theme_actionMenuTextColor /*26*/:
                return "onDeeplinkClick";
            case C0206R.styleable.Theme_actionModeStyle /*27*/:
                return "openApp";
            case C0206R.styleable.Theme_actionModeCloseButtonStyle /*28*/:
                return "reshare";
            default:
                return "unknown(" + type + ")";
        }
    }
}
