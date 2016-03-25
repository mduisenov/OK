package ru.mail.android.mytarget.core.models.banners;

import ru.mail.android.mytarget.nativeads.banners.NativeAppwallBanner;
import ru.mail.android.mytarget.nativeads.models.ImageData;

public class AppwallBanner extends AbstractBanner implements NativeAppwallBanner {
    private ImageData bubbleIcon;
    private String bubbleId;
    private int coins;
    private ImageData coinsIcon;
    private int coinsIconBgColor;
    private int coinsIconTextColor;
    private String description;
    private ImageData gotoAppIcon;
    private boolean hasNotification;
    private ImageData icon;
    private boolean isBanner;
    private boolean isItemHighlight;
    private boolean isMain;
    private boolean isRequireCategoryHighlight;
    private boolean isRequireWifi;
    private boolean isSubItem;
    private ImageData itemHighlightIcon;
    private ImageData labelIcon;
    private String labelType;
    private int mrgsId;
    private String paidType;
    private float rating;
    private String status;
    private ImageData statusIcon;
    private String title;
    private int votes;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public ImageData getIcon() {
        return this.icon;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBubbleId() {
        return this.bubbleId;
    }

    public void setBubbleId(String bubbleId) {
        this.bubbleId = bubbleId;
    }

    public String getLabelType() {
        return this.labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaidType() {
        return this.paidType;
    }

    public void setPaidType(String paidType) {
        this.paidType = paidType;
    }

    public int getMrgsId() {
        return this.mrgsId;
    }

    public void setMrgsId(int mrgsId) {
        this.mrgsId = mrgsId;
    }

    public int getVotes() {
        return this.votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public float getRating() {
        return this.rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isHasNotification() {
        return this.hasNotification;
    }

    public void setHasNotification(boolean hasNotification) {
        this.hasNotification = hasNotification;
    }

    public boolean isMain() {
        return this.isMain;
    }

    public void setMain(boolean main) {
        this.isMain = main;
    }

    public boolean isRequireCategoryHighlight() {
        return this.isRequireCategoryHighlight;
    }

    public void setRequireCategoryHighlight(boolean requireCategoryHighlight) {
        this.isRequireCategoryHighlight = requireCategoryHighlight;
    }

    public boolean isItemHighlight() {
        return this.isItemHighlight;
    }

    public void setItemHighlight(boolean itemHighlight) {
        this.isItemHighlight = itemHighlight;
    }

    public boolean isBanner() {
        return this.isBanner;
    }

    public void setBanner(boolean banner) {
        this.isBanner = banner;
    }

    public boolean isRequireWifi() {
        return this.isRequireWifi;
    }

    public void setRequireWifi(boolean requireWifi) {
        this.isRequireWifi = requireWifi;
    }

    public boolean isSubItem() {
        return this.isSubItem;
    }

    public void setSubItem(boolean subItem) {
        this.isSubItem = subItem;
    }

    public int getCoins() {
        return this.coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setStatusIcon(ImageData statusIcon) {
        this.statusIcon = statusIcon;
    }

    public void setIcon(ImageData icon) {
        this.icon = icon;
    }

    public void setCoinsIcon(ImageData coinsIcon) {
        this.coinsIcon = coinsIcon;
    }

    public void setLabelIcon(ImageData labelIcon) {
        this.labelIcon = labelIcon;
    }

    public void setGotoAppIcon(ImageData gotoAppIcon) {
        this.gotoAppIcon = gotoAppIcon;
    }

    public void setBubbleIcon(ImageData bubbleIcon) {
        this.bubbleIcon = bubbleIcon;
    }

    public ImageData getCoinsIcon() {
        return this.coinsIcon;
    }

    public ImageData getLabelIcon() {
        return this.labelIcon;
    }

    public ImageData getGotoAppIcon() {
        return this.gotoAppIcon;
    }

    public ImageData getStatusIcon() {
        return this.statusIcon;
    }

    public ImageData getBubbleIcon() {
        return this.bubbleIcon;
    }

    public int getCoinsIconBgColor() {
        return this.coinsIconBgColor;
    }

    public void setCoinsIconBgColor(int coinsIconBgColor) {
        this.coinsIconBgColor = coinsIconBgColor;
    }

    public int getCoinsIconTextColor() {
        return this.coinsIconTextColor;
    }

    public void setCoinsIconTextColor(int coinsIconTextColor) {
        this.coinsIconTextColor = coinsIconTextColor;
    }

    public ImageData getItemHighlightIcon() {
        return this.itemHighlightIcon;
    }

    public void setItemHighlightIcon(ImageData itemHighlightIcon) {
        this.itemHighlightIcon = itemHighlightIcon;
    }

    public AppwallBanner(String id, String type) {
        super(id, type);
        this.coinsIconBgColor = -552418;
        this.coinsIconTextColor = -1;
    }

    public String toString() {
        return "AppwallBanner {title='" + this.title + '\'' + ", description='" + this.description + '\'' + '}';
    }
}
