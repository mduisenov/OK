package ru.ok.android.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import io.github.eterverda.sntp.SNTP;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.mail.android.mytarget.core.async.Sender;
import ru.ok.android.app.OdnoklassnikiApplication;
import ru.ok.android.app.helper.ServiceHelper;
import ru.ok.android.http.client.methods.HttpUriRequest;
import ru.ok.android.model.cache.ImageViewManager;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.android.ui.custom.imageview.UrlImageView;
import ru.ok.android.ui.custom.online.OnlineDrawable;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.java.api.ServiceStateHolder;
import ru.ok.model.UserInfo;
import ru.ok.model.UserInfo.UserOnlineType;
import ru.ok.model.stream.banner.PromoLink;

public class Utils {
    private static final Pair<Integer, Integer> MOBILE_PAIR;
    private static final Pair<Integer, Integer> MOBILE_PAIR_MESSAGES;
    private static final Pair<Integer, Integer> WEB_PAIR;
    private static final Pair<Integer, Integer> WEB_PAIR_MESSAGES;
    private static final Pattern bracesPattern;
    private static volatile Pair<Pattern, String>[] protectedUrlParamPattern;

    /* renamed from: ru.ok.android.utils.Utils.1 */
    static /* synthetic */ class C14271 {
        static final /* synthetic */ int[] $SwitchMap$ru$ok$model$UserInfo$UserOnlineType;

        static {
            $SwitchMap$ru$ok$model$UserInfo$UserOnlineType = new int[UserOnlineType.values().length];
            try {
                $SwitchMap$ru$ok$model$UserInfo$UserOnlineType[UserOnlineType.WEB.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ru$ok$model$UserInfo$UserOnlineType[UserOnlineType.MOBILE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    static {
        bracesPattern = Pattern.compile("\\{([^}]*)\\}");
        WEB_PAIR = new Pair(Integer.valueOf(2130838020), Integer.valueOf(2130838021));
        MOBILE_PAIR = new Pair(Integer.valueOf(2130838096), Integer.valueOf(2130838097));
        WEB_PAIR_MESSAGES = new Pair(Integer.valueOf(2130838022), Integer.valueOf(2130838023));
        MOBILE_PAIR_MESSAGES = new Pair(Integer.valueOf(2130838098), Integer.valueOf(2130838099));
    }

    public static void setViewBackgroundWithoutResettingPadding(View v, int backgroundResId) {
        if (v != null) {
            int paddingBottom = v.getPaddingBottom();
            int paddingLeft = v.getPaddingLeft();
            int paddingRight = v.getPaddingRight();
            int paddingTop = v.getPaddingTop();
            v.setBackgroundResource(backgroundResId);
            v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
    }

    public static void sendPixels(PromoLink promoLink, int type, Context activity) {
        if (activity != null && promoLink != null) {
            ArrayList<String> pixels = promoLink.statPixels.getStatPixels(type);
            if (pixels != null) {
                Iterator i$ = pixels.iterator();
                while (i$.hasNext()) {
                    Sender.addStat((String) i$.next(), activity);
                }
            }
        }
    }

    public static Pair<Integer, Integer> getUserOnlineDrawableResIdPair(UserOnlineType onlineType) {
        switch (C14271.$SwitchMap$ru$ok$model$UserInfo$UserOnlineType[onlineType.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return WEB_PAIR;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return MOBILE_PAIR;
            default:
                return new Pair(Integer.valueOf(0), Integer.valueOf(0));
        }
    }

    public static Pair<Integer, Integer> getUserOnlineDrawable4MessagesResIdPair(UserOnlineType onlineType) {
        switch (C14271.$SwitchMap$ru$ok$model$UserInfo$UserOnlineType[onlineType.ordinal()]) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return WEB_PAIR_MESSAGES;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return MOBILE_PAIR_MESSAGES;
            default:
                return new Pair(Integer.valueOf(0), Integer.valueOf(0));
        }
    }

    public static int getUserOnlineDrawableResId(UserOnlineType onlineType) {
        return ((Integer) getUserOnlineDrawableResIdPair(onlineType).first).intValue();
    }

    public static void updateOnlineView(View view, UserOnlineType onlineType) {
        if (onlineType == null) {
            ViewUtil.gone(view);
            return;
        }
        updateViewWithPair(view, getUserOnlineDrawableResIdPair(onlineType));
    }

    public static void updateOnlineViewForMessages(View view, UserOnlineType onlineType) {
        if (onlineType == null) {
            ViewUtil.gone(view);
            return;
        }
        updateViewWithPair(view, getUserOnlineDrawable4MessagesResIdPair(onlineType));
    }

    private static void updateViewWithPair(View view, Pair<Integer, Integer> drawableResourceIds) {
        boolean z;
        if (!(drawableResourceIds == null || ((Integer) drawableResourceIds.first).intValue() == 0)) {
            OnlineDrawable onlineDrawable = new OnlineDrawable(view.getContext().getResources().getDrawable(((Integer) drawableResourceIds.first).intValue()));
            view.setBackgroundDrawable(new LayerDrawable(new Drawable[]{resources.getDrawable(((Integer) drawableResourceIds.second).intValue()), onlineDrawable}));
        }
        if (drawableResourceIds == null || ((Integer) drawableResourceIds.first).intValue() == 0) {
            z = false;
        } else {
            z = true;
        }
        ViewUtil.setVisibility(view, z);
    }

    public static boolean equalsUri(Uri data1, Uri data2) {
        if (data1 == null) {
            return data2 == null;
        } else {
            return data1.equals(data2);
        }
    }

    public static final boolean equalBundles(Bundle b1, Bundle b2) {
        boolean empty1;
        boolean empty2;
        if (b1 == null || b1.isEmpty()) {
            empty1 = true;
        } else {
            empty1 = false;
        }
        if (b2 == null || b2.isEmpty()) {
            empty2 = true;
        } else {
            empty2 = false;
        }
        if (empty1 || empty2) {
            boolean z;
            if (empty1 == empty2) {
                z = true;
            } else {
                z = false;
            }
            return z;
        }
        Set<String> keys1 = b1.keySet();
        Set<String> keys2 = b2.keySet();
        if (keys1.size() != keys2.size()) {
            return false;
        }
        for (String key : keys1) {
            if (!keys2.contains(key)) {
                return false;
            }
            Object val1 = b1.get(key);
            Object val2 = b2.get(key);
            if (!(val1 == null && (val2 instanceof CharSequence) && ((CharSequence) val2).length() == 0) && (!(val2 == null && (val1 instanceof CharSequence) && ((CharSequence) val1).length() == 0) && ((val1 == null && val2 != null) || !(val1 == null || val1.equals(val2))))) {
                if (!(val1 instanceof Bundle) || !(val2 instanceof Bundle) || !equalBundles((Bundle) val1, (Bundle) val2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean userCanCall(UserInfo userInfo) {
        if (userInfo == null) {
            return false;
        }
        ServiceStateHolder holder = JsonSessionTransportProvider.getInstance().getStateHolder();
        if (TextUtils.equals(userInfo.uid, holder == null ? null : holder.getUserId()) || !userInfo.getAvailableCall()) {
            return false;
        }
        return true;
    }

    @NonNull
    public static UserOnlineType onlineStatus(UserInfo userInfo) {
        if (userInfo == null) {
            return UserOnlineType.OFFLINE;
        }
        if (userInfo.lastOnline <= 0 || SNTP.safeCurrentTimeMillisFromCache() - userInfo.lastOnline <= 1200000) {
            return userInfo.online;
        }
        return UserOnlineType.OFFLINE;
    }

    public static boolean canSendVideoMailTo(UserInfo userInfo) {
        if (userInfo == null) {
            return false;
        }
        if (TextUtils.equals(userInfo.uid, OdnoklassnikiApplication.getCurrentUser().getId()) || !userInfo.getAvailableVMail()) {
            return false;
        }
        return true;
    }

    public static <T> void shuffle(T[] array) {
        Random rnd = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            T a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    public static ServiceHelper getServiceHelper() {
        return ((OdnoklassnikiApplication) OdnoklassnikiApplication.getContext().getApplicationContext()).getServiceHelper();
    }

    public static float dipToPixels(float dipValue) {
        return dipToPixels(OdnoklassnikiApplication.getContext(), dipValue);
    }

    public static float dipToPixels(Context context, float dipValue) {
        return TypedValue.applyDimension(1, dipValue, context.getResources().getDisplayMetrics());
    }

    public static CharSequence removeTextBetweenBraces(String str) {
        Matcher matcher = Pattern.compile("\\{([^}]*)\\}").matcher(str);
        SpannableStringBuilder sb = new SpannableStringBuilder();
        int previousFinish = 0;
        while (matcher.find()) {
            int startOpening = matcher.start();
            int endOpening = matcher.end();
            sb.append(str.substring(previousFinish, startOpening));
            previousFinish = endOpening;
            if (!matcher.find()) {
                break;
            }
            int startClosing = matcher.start();
            previousFinish = matcher.end();
            String str2 = str.substring(startOpening + 1, endOpening - 1).split(":")[0];
        }
        if (previousFinish != str.length()) {
            sb.append(str.substring(previousFinish, str.length()));
        }
        return sb;
    }

    public static CharSequence processTextBetweenBraces(Context context, String str) {
        Matcher matcher = bracesPattern.matcher(str);
        SpannableStringBuilder sb = new SpannableStringBuilder();
        int previousFinish = 0;
        while (matcher.find()) {
            int startOpening = matcher.start();
            int endOpening = matcher.end();
            sb.append(str.substring(previousFinish, startOpening));
            previousFinish = endOpening;
            if (!matcher.find()) {
                break;
            }
            int startClosing = matcher.start();
            previousFinish = matcher.end();
            String type = str.substring(startOpening + 1, endOpening - 1).split(":")[0];
            String substring = str.substring(endOpening, startClosing);
            sb.append(substring);
            sb.setSpan(new ForegroundColorSpan(context.getResources().getColor(2131493072)), sb.length() - substring.length(), sb.length(), 33);
        }
        if (previousFinish != str.length()) {
            sb.append(str.substring(previousFinish, str.length()));
        }
        return sb;
    }

    public static void setTextViewTextWithVisibility(TextView textView, CharSequence text) {
        setTextViewTextWithVisibilityState(textView, text, 8);
    }

    public static void setTextViewTextWithVisibilityState(TextView textView, CharSequence text, int noVisibleValue) {
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(noVisibleValue);
            return;
        }
        textView.setVisibility(0);
        textView.setText(text);
    }

    public static void setImageViewUrlWithVisibility(UrlImageView imageView, String url, int defaultResourceId) {
        int visibility;
        if (!TextUtils.isEmpty(url)) {
            visibility = 0;
            ImageViewManager.getInstance().displayImage(url, imageView, null);
        } else if (defaultResourceId != 0) {
            visibility = 0;
            imageView.setImageResource(defaultResourceId);
        } else {
            visibility = 8;
        }
        imageView.setVisibility(visibility);
    }

    public static void scrollToPosition(ListView listView, int position) {
        listView.setSelection(position);
    }

    public static void formatLikeBlock(Context context, int likesCount, boolean isLiked, boolean likeAllowed, boolean unlikeAllowed, TextView likes, TextView count) {
        formatLikeBlock(context, likesCount, isLiked, likeAllowed, unlikeAllowed, likes, count, 2130838091);
    }

    public static void formatLikeBlock(Context context, int likesCount, boolean isLiked, boolean likeAllowed, boolean unlikeAllowed, TextView likes, TextView count, int iconId) {
        int likeDrawableResource;
        if (likesCount > 0 || isLiked) {
            String likeText;
            count.setVisibility(0);
            if (isLiked && likesCount > 1) {
                likeText = LocalizationManager.getString(context, 2131166884, Integer.valueOf(likesCount - 1));
            } else if (isLiked) {
                likeText = LocalizationManager.getString(context, 2131166888);
            } else {
                likeText = String.valueOf(likesCount);
            }
            count.setText(likeText);
            count.setTextColor(context.getResources().getColor(isLiked ? 2131492887 : 2131493015));
            count.setEnabled(likesCount > (isLiked ? 1 : 0));
            if (isLiked) {
                iconId = 2130838092;
            }
            count.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0);
            likeDrawableResource = isLiked ? 2130838004 : 2130838003;
        } else {
            count.setVisibility(8);
            likeDrawableResource = iconId;
        }
        likes.setCompoundDrawablesWithIntrinsicBounds(likeDrawableResource, 0, 0, 0);
        likes.setCompoundDrawablePadding((int) dipToPixels(likeDrawableResource == 2130838091 ? 4.0f : 2.0f));
        int i = ((!likeAllowed || isLiked) && !(unlikeAllowed && isLiked)) ? 8 : 0;
        likes.setVisibility(i);
        likes.setTextColor(context.getResources().getColor(isLiked ? 2131492887 : 2131493015));
        int likesPaddingLeft = (likesCount > 0 || isLiked) ? 0 : (int) dipToPixels(8.0f);
        likes.setPadding(likesPaddingLeft, likes.getPaddingTop(), likes.getPaddingRight(), likes.getPaddingBottom());
    }

    public static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, from.length() + index, to);
            index = builder.indexOf(from, index + to.length());
        }
    }

    public static String toString(HttpUriRequest method) {
        if (method == null) {
            return "null";
        }
        String uriStr = null;
        try {
            URI uri = method.getURI();
            uriStr = uri == null ? null : uri.toString();
        } catch (Exception e) {
        }
        if (uriStr != null) {
            for (Pair<Pattern, String> pattern : getPasswordUrlParamPattern()) {
                uriStr = ((Pattern) pattern.first).matcher(uriStr).replaceAll((String) pattern.second);
            }
        }
        return method.getMethod() + " " + uriStr;
    }

    private static Pair<Pattern, String>[] getPasswordUrlParamPattern() {
        if (protectedUrlParamPattern == null) {
            synchronized (Utils.class) {
                if (protectedUrlParamPattern == null) {
                    protectedUrlParamPattern = new Pair[]{new Pair(Pattern.compile("password=([^&]+)"), "password=XXX"), new Pair(Pattern.compile("phone=([^&]+)"), "phone=XXX")};
                }
            }
        }
        return protectedUrlParamPattern;
    }

    public static <T> T[] copyOfRange(T[] original, int start, int end) {
        int originalLength = original.length;
        if (start > end) {
            throw new IllegalArgumentException();
        } else if (start < 0 || start > originalLength) {
            throw new ArrayIndexOutOfBoundsException();
        } else {
            int resultLength = end - start;
            Object[] result = (Object[]) ((Object[]) Array.newInstance(original.getClass().getComponentType(), resultLength));
            System.arraycopy(original, start, result, 0, Math.min(resultLength, originalLength - start));
            return result;
        }
    }

    public static void addToClipBoard(Context context, CharSequence label, CharSequence text) {
        ((ClipboardManager) context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText(label, text));
    }

    public static int getVersionCode(Context context) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Throwable e) {
            Logger.m178e(e);
            return i;
        }
    }

    public static int getHashcode(List<String> strings) {
        if (strings == null || strings.size() == 0) {
            return 0;
        }
        int hash = 0;
        for (CharSequence string : strings) {
            for (int i = 0; i < string.length(); i++) {
                hash = (hash * 31) + string.charAt(i);
            }
        }
        return hash;
    }

    public static View findDirectChildById(ViewGroup viewGroup, int id) {
        int size = viewGroup.getChildCount();
        for (int i = 0; i < size; i++) {
            View child = viewGroup.getChildAt(i);
            if (child.getId() == id) {
                return child;
            }
        }
        return null;
    }

    public static String getAgeAndLocationText(Context context, UserInfo userInfo) {
        StringBuilder ageAndLocation = new StringBuilder();
        if (userInfo.age != -1) {
            ageAndLocation.append(LocalizationManager.getString(context, StringUtils.plural((long) userInfo.age, 2131165364, 2131165365, 2131165366), Integer.valueOf(userInfo.age)));
        }
        if (userInfo.location != null) {
            if (!(userInfo.location.city == null || userInfo.location.city.isEmpty())) {
                if (ageAndLocation.length() > 0) {
                    ageAndLocation.append(", ");
                }
                ageAndLocation.append(userInfo.location.city);
            }
            if (!(userInfo.location.country == null || userInfo.location.country.isEmpty())) {
                if (ageAndLocation.length() > 0) {
                    ageAndLocation.append(", ");
                }
                ageAndLocation.append(userInfo.location.country);
            }
        }
        return ageAndLocation.toString();
    }
}
