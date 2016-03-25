package ru.ok.android.ui.stream.list;

public class EntitySpanStyle {
    public final boolean fakeBoldText;
    public final boolean underlineUser;
    public final int userTextColor;

    public EntitySpanStyle(int userTextColor, boolean underlineUser, boolean fakeBoldText) {
        this.userTextColor = userTextColor;
        this.underlineUser = underlineUser;
        this.fakeBoldText = fakeBoldText;
    }
}
