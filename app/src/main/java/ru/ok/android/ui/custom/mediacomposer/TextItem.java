package ru.ok.android.ui.custom.mediacomposer;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import ru.ok.model.mediatopics.MediaItemType;

public class TextItem extends MediaItem {
    public static final Creator<TextItem> CREATOR;
    private static final long serialVersionUID = 1;
    private String text;

    /* renamed from: ru.ok.android.ui.custom.mediacomposer.TextItem.1 */
    static class C06851 implements Creator<TextItem> {
        C06851() {
        }

        public TextItem createFromParcel(Parcel source) {
            return new TextItem(source);
        }

        public TextItem[] newArray(int size) {
            return new TextItem[size];
        }
    }

    TextItem() {
        super(MediaItemType.TEXT);
        this.text = "";
    }

    TextItem(Parcel source) {
        super(MediaItemType.TEXT, source);
        this.text = source.readString();
    }

    public boolean isEmpty() {
        return this.text == null || this.text.trim().length() == 0;
    }

    @Nullable
    public String getText() {
        return this.text;
    }

    public int getTextLength() {
        return this.text == null ? 0 : this.text.length();
    }

    public String getSampleText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MediaItem append(MediaItem other) {
        if (!(other instanceof TextItem)) {
            return null;
        }
        TextItem newItem = new TextItem();
        String myText = getText();
        int myLength = myText == null ? 0 : myText.length();
        String otherText = ((TextItem) other).getText();
        int otherLength = otherText == null ? 0 : otherText.length();
        StringBuilder sb = new StringBuilder((myLength + otherLength) + 1);
        if (myText != null) {
            sb.append(myText);
        }
        if (otherLength > 0) {
            sb.append('\n').append(otherText);
        }
        newItem.setText(sb.toString());
        return newItem;
    }

    public String toString() {
        return "TextItem[" + this.text + "]";
    }

    public static boolean equal(TextItem t1, TextItem t2) {
        if (t1 == null) {
            if (t2 == null) {
                return true;
            }
            return false;
        } else if (t2 != null) {
            return TextUtils.equals(t1.text, t2.text);
        } else {
            return false;
        }
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.text);
    }

    static {
        CREATOR = new C06851();
    }
}
