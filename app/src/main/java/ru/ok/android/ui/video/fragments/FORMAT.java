package ru.ok.android.ui.video.fragments;

public enum FORMAT {
    FORMAT_144(144, 1),
    FORMAT_240(240, 5),
    FORMAT_360(360, 7),
    FORMAT_480(480, 6),
    FORMAT_720(720, 4),
    FORMAT_1080(1080, 3),
    FORMAT_1440(1440, 2),
    FORMAT_2160(2160, 0);
    
    private int noWifiPrior;
    private int size;

    private FORMAT(int size, int noWifiPrior) {
        this.size = size;
        this.noWifiPrior = noWifiPrior;
    }

    public int getSize() {
        return this.size;
    }

    public int getNoWifiPrior() {
        return this.noWifiPrior;
    }
}
