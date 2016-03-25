package ru.ok.android.ui.utils;

import android.text.TextUtils;

public final class RowPositionUtils {
    public static RowPosition determineRowPosition(String currentAuthorId, String currentSection, long currentDate, String customTag, String prevAuthorId, String prevSection, long prevDate, String prevCustomTag, String nextAuthorId, String nextSection, long nextDate, String nextCustomTag) {
        boolean prevSame = TextUtils.equals(prevAuthorId, currentAuthorId) && TextUtils.equals(prevSection, currentSection) && TextUtils.equals(customTag, prevCustomTag) && !(TextUtils.isEmpty(prevAuthorId) && TextUtils.isEmpty(currentAuthorId));
        boolean nextSame = TextUtils.equals(nextAuthorId, currentAuthorId) && TextUtils.equals(nextSection, currentSection) && TextUtils.equals(customTag, nextCustomTag) && !(TextUtils.isEmpty(nextAuthorId) && TextUtils.isEmpty(currentAuthorId));
        boolean prevCommentLongDeltaTime = currentDate - prevDate > 60000;
        boolean nextCommentLongDeltaTime = nextDate - currentDate > 60000 || nextDate == 0;
        if (!prevSame && !nextSame) {
            return RowPosition.SINGLE_FIRST_DATE;
        }
        if (prevSame || !nextSame) {
            if (!prevSame || nextSame) {
                if (nextCommentLongDeltaTime && prevCommentLongDeltaTime) {
                    return RowPosition.SINGLE_DATE_AVATAR;
                }
                if (nextCommentLongDeltaTime) {
                    return RowPosition.SINGLE_DATE;
                }
                if (prevCommentLongDeltaTime) {
                    return RowPosition.SINGLE_FIRST;
                }
                return RowPosition.SINGLE;
            } else if (prevCommentLongDeltaTime) {
                return RowPosition.SINGLE_DATE_AVATAR;
            } else {
                return RowPosition.SINGLE_DATE;
            }
        } else if (nextCommentLongDeltaTime) {
            return RowPosition.SINGLE_FIRST_DATE;
        } else {
            return RowPosition.SINGLE_FIRST;
        }
    }
}
