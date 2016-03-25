package ru.ok.java.api.utils;

public final class Constants {

    public static final class Api {
        public static String CID_VALUE;
        public static final String CLIENT_NAME;
        public static final String[] COOKIE_APPCAPS_DOMAIN_URLS;

        static {
            CID_VALUE = null;
            CLIENT_NAME = "android_8_" + getVersion();
            COOKIE_APPCAPS_DOMAIN_URLS = new String[]{".odnoklassniki.ru", ".ok.ru"};
        }

        private static String getVersion() {
            return stripUnsupportedChars(Configuration.getVersion());
        }

        private static String stripUnsupportedChars(String versionName) {
            if (versionName == null) {
                return null;
            }
            StringBuilder sb = null;
            for (int i = 0; i < versionName.length(); i++) {
                char c = versionName.charAt(i);
                if (Character.isDigit(c) || c == '.') {
                    if (sb != null) {
                        sb.append(c);
                    }
                } else if (sb == null) {
                    sb = new StringBuilder(versionName.length());
                    sb.append(versionName.substring(0, i));
                }
            }
            if (sb != null) {
                return sb.toString();
            }
            return versionName;
        }

        public static String m193k() {
            byte[] a = new byte[]{(byte) 66, (byte) 68, (byte) 58, (byte) 51, (byte) 71, (byte) 54, (byte) 51, (byte) 67, (byte) 74, (byte) 76, (byte) 60, (byte) 51, (byte) 73, (byte) 56, (byte) 76, (byte) 78, (byte) 39, (byte) 33, (byte) 42, (byte) 43, (byte) 85, (byte) 32, (byte) 47, (byte) 47};
            for (int i = 0; i < a.length; i++) {
                a[i] = (byte) (a[i] ^ ((byte) i));
            }
            return new String(a);
        }
    }
}
