package ru.ok.android.services.processors.banners;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.util.List;
import ru.ok.android.fragments.web.WebExternalUrlManager;
import ru.ok.android.fragments.web.client.WebClientUtils;
import ru.ok.android.fragments.web.hooks.WebLinksProcessor;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkGroupProcessor;
import ru.ok.android.fragments.web.hooks.hooklinks.ShortLinkGroupThemeProcessor;
import ru.ok.android.proto.MessagesProto.Message;
import ru.ok.android.ui.fragments.messages.MessageBaseFragment.Page;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.NavigationHelper;
import ru.ok.model.GroupDiscussion;
import ru.ok.model.stream.banner.Banner;
import ru.ok.model.stream.banner.PromoLink;

public final class BannerLinksUtils {
    public static PromoLink getLastPromoLinkByType(List<PromoLink> promoLinks, int promoLinkType, long now) {
        if (promoLinks == null) {
            return null;
        }
        PromoLink promoLink = null;
        for (PromoLink pl : promoLinks) {
            if (pl != null && pl.type == promoLinkType && pl.banner != null && pl.fetchedTime + 600000 > now) {
                promoLink = pl;
            }
        }
        return promoLink;
    }

    public static int processBannerClick(Banner banner, Activity activity, WebLinksProcessor webLinksProcessor) {
        if (banner == null) {
            return -1;
        }
        if (banner.actionType == 2) {
            return processBannerDeeplink(banner, activity);
        }
        if (TextUtils.isEmpty(banner.clickUrl)) {
            return -1;
        }
        switch (banner.actionType) {
            case Message.TEXT_FIELD_NUMBER /*1*/:
                navigateExternalUrl(activity, banner.clickUrl);
                return 2;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                String groupId = ShortLinkGroupProcessor.extractGroupId(Uri.parse(banner.clickUrl), true);
                if (groupId != null) {
                    navigateInternalGroup(activity, groupId);
                    return 2;
                }
                navigateInternal(webLinksProcessor, banner.clickUrl);
                return 2;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                navigateInternal(webLinksProcessor, banner.clickUrl);
                return 2;
            case Message.UUID_FIELD_NUMBER /*5*/:
                GroupDiscussion discussion = ShortLinkGroupThemeProcessor.extractGroupDiscussion(Uri.parse(banner.clickUrl), true);
                if (discussion != null) {
                    navigateInternalGroupTheme(activity, discussion);
                    return 2;
                }
                navigateInternal(webLinksProcessor, banner.clickUrl);
                return 2;
            default:
                Logger.m185w("Unsupported banner action type: %d", Integer.valueOf(banner.actionType));
                return -1;
        }
    }

    private static void navigateGooglePlay(Activity activity, String url) {
        new WebExternalUrlManager(activity).preProcessUrl(url);
    }

    public static void navigateExternalUrl(Activity activity, String url) {
        Logger.m173d("url=%s", url);
        try {
            activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
        } catch (Exception e) {
            Logger.m180e(e, "Failed to navigate to url: %s", url);
        }
    }

    public static void navigateInternalGroup(Activity activity, String groupId) {
        NavigationHelper.showGroupInfo(activity, groupId);
    }

    public static void navigateInternalGroupTheme(Activity activity, GroupDiscussion discussion) {
        NavigationHelper.showDiscussionCommentsFragment(activity, discussion, Page.INFO, null);
    }

    public static void navigateInternal(WebLinksProcessor webLinksProcessor, String url) {
        Logger.m173d("url=%s", url);
        if (WebClientUtils.isOkHost(Uri.parse(url))) {
            webLinksProcessor.processUrl(url);
        } else {
            webLinksProcessor.processUrlWithoutGoTo(url);
        }
    }

    private static int processBannerDeeplink(@NonNull Banner banner, @NonNull Activity activity) {
        if (!TextUtils.isEmpty(banner.deepLink) && openDeeplink(banner.deepLink, activity)) {
            return 26;
        }
        Logger.m173d("Deeplink not opened, fallback to click URL: %s", banner.clickUrl);
        if (TextUtils.isEmpty(banner.clickUrl)) {
            return -1;
        }
        navigateGooglePlay(activity, banner.clickUrl);
        return 2;
    }

    private static boolean openDeeplink(@NonNull String deeplink, @NonNull Activity activity) {
        PackageManager pm = activity.getPackageManager();
        if (pm == null) {
            Logger.m184w("null package manager");
            return false;
        }
        Intent openDeeplink = new Intent("android.intent.action.VIEW", Uri.parse(deeplink));
        try {
            Logger.m173d("Resolving deeplink: %s", deeplink);
            ComponentName componentName = openDeeplink.resolveActivity(pm);
            if (componentName != null) {
                Logger.m173d("Resolved component: %s", componentName);
                openDeeplink.setComponent(componentName);
                activity.startActivity(openDeeplink);
                return true;
            }
            Logger.m173d("Not found activity for deeplink: %s", deeplink);
            return false;
        } catch (Exception e) {
            Logger.m180e(e, "Failed to resolve/start activity for deeplink: %s", deeplink);
            return false;
        }
    }
}
