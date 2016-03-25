package ru.ok.android.emoji.smiles;

import android.content.Context;
import android.widget.TextView;

public final class SmilesManager {
    private static volatile SmilesManager instance;
    public static SmilesCallback smilesCallback;
    public static boolean useWeakReferences;
    private final SmileTextProcessor smileTextProcessor;
    private final SmilesCallbackHelper smilesCallbackHelper;

    static {
        useWeakReferences = true;
    }

    public static void setSmilesCallback(SmilesCallback smilesCallback) {
        smilesCallback = smilesCallback;
    }

    private SmilesManager() {
        this.smilesCallbackHelper = new SmilesCallbackHelper();
        this.smileTextProcessor = new SmileTextProcessor();
    }

    public static SmilesManager getInstance() {
        if (instance == null) {
            synchronized (SmilesManager.class) {
                if (instance == null) {
                    synchronized (SmilesManager.class) {
                        instance = new SmilesManager();
                    }
                }
            }
        }
        return instance;
    }

    public CharSequence processPaymentSmiles(Context context, CharSequence message) {
        return this.smileTextProcessor.getSpannedText(context, message);
    }

    public void setSmilesCallBack(TextView textView, CharSequence spannedText) {
        this.smilesCallbackHelper.processSmiles(textView, spannedText);
    }
}
