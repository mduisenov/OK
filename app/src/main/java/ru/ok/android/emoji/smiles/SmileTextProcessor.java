package ru.ok.android.emoji.smiles;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.ok.android.emoji.utils.DrawablesCache;

public final class SmileTextProcessor {
    private static final String TAG;
    private static final Pattern imagePattern;

    static {
        TAG = SmileTextProcessor.class.getSimpleName();
        imagePattern = Pattern.compile("#u([0-9a-f]{2,16})(#\\d+:\\d+)?s#");
    }

    public static CharSequence trimSmileSizes(CharSequence text) {
        return TextUtils.isEmpty(text) ? text : imagePattern.matcher(text).replaceAll("#u$1s#");
    }

    public CharSequence getSpannedText(Context context, CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        Matcher paymentMatcher = imagePattern.matcher(text);
        SpannableStringBuilder sb = null;
        while (paymentMatcher.find()) {
            String smileCode = paymentMatcher.group(1);
            String size = paymentMatcher.group(2);
            if (smileCode != null) {
                if (sb == null) {
                    sb = SpannableStringBuilder.valueOf(text);
                }
                appendSmile(context, sb, smileCode, size, paymentMatcher.start(), paymentMatcher.end());
            }
        }
        if (sb == null) {
            CharSequence sb2 = text;
        }
        return sb;
    }

    private void appendSmile(Context context, SpannableStringBuilder sb, String code, String size, int start, int end) {
        int width = 0;
        int height = 0;
        if (size != null) {
            String[] chunks = size.split(":");
            if (chunks != null && chunks.length == 2) {
                String widthStr = chunks[0];
                if (!TextUtils.isEmpty(widthStr)) {
                    widthStr = widthStr.substring(1);
                }
                String heightStr = chunks[1];
                try {
                    width = Integer.parseInt(widthStr);
                    height = Integer.parseInt(heightStr);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Failed to parse payed smile size", e);
                }
            }
        }
        sb.setSpan(new ImageSpan(getWebDrawable(context, paymentSmileUrl(code), scaleSize(context, width), scaleSize(context, height))), start, end, 33);
    }

    @NonNull
    public static String paymentSmileUrl(String code) {
        return "http://dsm.odnoklassniki.ru/getImage?smileId=" + code;
    }

    public WebImageDrawable getWebDrawable(Context context, String url, int width, int height) {
        String key = url + "/" + width;
        Drawable res = DrawablesCache.get(key);
        if (res instanceof WebImageDrawable) {
            return (WebImageDrawable) res;
        }
        WebImageDrawable result = new WebImageDrawable(context, url, width, height);
        DrawablesCache.put(key, result);
        return result;
    }

    private static int scaleSize(Context context, int size) {
        return (size != 0 ? (int) (((float) size) / 32.0f) : 1) * SmileUtils.getPayedSmileSize(context);
    }

    public static String buildStickerText(String code, int width, int height) {
        if (width == 0 || height == 0) {
            height = NotificationCompat.FLAG_HIGH_PRIORITY;
            width = NotificationCompat.FLAG_HIGH_PRIORITY;
        }
        return String.format("#u%s#%d:%ds#", new Object[]{code, Integer.valueOf(width), Integer.valueOf(height)});
    }

    public static boolean isSticker(String message) {
        int length = message == null ? 0 : message.length();
        if (length <= 0) {
            return false;
        }
        Matcher matcher = imagePattern.matcher(message);
        if (matcher.find() && matcher.group().length() == length) {
            return true;
        }
        return false;
    }
}
