package ru.ok.android.utils;

public final class Bithacks {
    private static final int[] MultiplyDeBruijnBitPosition;

    public static int ceilPow2(int x) {
        int p = 1 << highestBitSet(x);
        if (p < x) {
            return p << 1;
        }
        return p;
    }

    public static int highestBitSet(int x) {
        x |= x >>> 1;
        x |= x >>> 2;
        x |= x >>> 4;
        x |= x >>> 8;
        return MultiplyDeBruijnBitPosition[(130329821 * (x | (x >>> 16))) >>> 27];
    }

    static {
        MultiplyDeBruijnBitPosition = new int[]{0, 9, 1, 10, 13, 21, 2, 29, 11, 14, 16, 18, 22, 25, 3, 30, 8, 12, 20, 28, 15, 17, 24, 7, 19, 27, 23, 6, 26, 5, 4, 31};
    }
}
