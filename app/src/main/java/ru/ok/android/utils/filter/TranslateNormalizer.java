package ru.ok.android.utils.filter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import one.search.analyzers.ASCIIFoldingFilter;
import one.search.analyzers.phonetic.AbstractMetaphone;
import one.search.analyzers.phonetic.NewTranslitMetaphone2;
import ru.ok.android.utils.Utils;

public final class TranslateNormalizer {
    private static final Pair[] elemArray;
    private static final AbstractMetaphone translitMetaphone;

    static {
        elemArray = new Pair[]{new Pair("\u0415", "YE"), new Pair("\u0415", "JE"), new Pair("\u042f", "YA"), new Pair("\u042f", "JA"), new Pair("\u042e", "YU"), new Pair("\u042e", "JU"), new Pair("\u0401", "YO"), new Pair("\u0401", "JO"), new Pair("\u0428", "SH"), new Pair("\u0427", "CH"), new Pair("\u0416", "ZH"), new Pair("\u0429", "SCH"), new Pair("\u0410", "A"), new Pair("\u0411", "B"), new Pair("\u0426", "C"), new Pair("\u0414", "D"), new Pair("\u0415", "E"), new Pair("\u0424", "F"), new Pair("\u0413", "G"), new Pair("\u0425", "H"), new Pair("\u0418", "I"), new Pair("\u0419", "J"), new Pair("\u041a", "K"), new Pair("\u041b", "L"), new Pair("\u041c", "M"), new Pair("\u041d", "N"), new Pair("\u041e", "O"), new Pair("\u041f", "P"), new Pair("\u041a", "Q"), new Pair("\u0420", "R"), new Pair("\u0421", "S"), new Pair("\u0422", "T"), new Pair("\u0423", "U"), new Pair("\u0412", "V"), new Pair("\u0412", "W"), new Pair("\u0425", "X"), new Pair("\u0419", "Y"), new Pair("\u0417", "Z"), new Pair("\u0415", "\u0401")};
        translitMetaphone = new NewTranslitMetaphone2(true);
    }

    @NonNull
    public static String normalizeText4Search(@Nullable String text) {
        if (text == null) {
            return "";
        }
        return translitMetaphone.encode(ASCIIFoldingFilter.foldToASCII(text).toUpperCase());
    }

    public static String normalizeText4Sorting(@Nullable String text) {
        if (text == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(text.toUpperCase());
        for (Pair<String, String> elem : elemArray) {
            Utils.replaceAll(builder, (String) elem.second, (String) elem.first);
        }
        return builder.toString();
    }
}
