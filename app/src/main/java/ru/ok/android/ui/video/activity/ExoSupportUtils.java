package ru.ok.android.ui.video.activity;

import ru.ok.android.utils.DeviceUtils;

public final class ExoSupportUtils {
    private static final String[] noSupportCpuNames;
    private static Boolean useExoCacheValue;

    static {
        useExoCacheValue = null;
        noSupportCpuNames = new String[]{"MT65", "MT8317", "MT8125", "MT8389", "MT8121", "MT8135", "MT8117", "MT8173", "MT6795", "MT8382", "MT8377"};
    }

    public static boolean isUsesExoForCpuModel() {
        if (useExoCacheValue == null) {
            useExoCacheValue = Boolean.valueOf(isUsesExoForCpuModel(DeviceUtils.getCPUModel()));
        }
        return useExoCacheValue.booleanValue();
    }

    private static boolean isUsesExoForCpuModel(String cpuModel) {
        for (String cpuName : noSupportCpuNames) {
            if (cpuModel.startsWith(cpuName)) {
                return false;
            }
        }
        return true;
    }
}
