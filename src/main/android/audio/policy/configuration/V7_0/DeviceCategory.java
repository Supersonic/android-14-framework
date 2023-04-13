package android.audio.policy.configuration.V7_0;
/* loaded from: classes.dex */
public enum DeviceCategory {
    DEVICE_CATEGORY_HEADSET("DEVICE_CATEGORY_HEADSET"),
    DEVICE_CATEGORY_SPEAKER("DEVICE_CATEGORY_SPEAKER"),
    DEVICE_CATEGORY_EARPIECE("DEVICE_CATEGORY_EARPIECE"),
    DEVICE_CATEGORY_EXT_MEDIA("DEVICE_CATEGORY_EXT_MEDIA"),
    DEVICE_CATEGORY_HEARING_AID("DEVICE_CATEGORY_HEARING_AID");
    
    private final String rawName;

    DeviceCategory(String rawName) {
        this.rawName = rawName;
    }

    public String getRawName() {
        return this.rawName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DeviceCategory fromString(String rawString) {
        DeviceCategory[] values;
        for (DeviceCategory _f : values()) {
            if (_f.getRawName().equals(rawString)) {
                return _f;
            }
        }
        throw new IllegalArgumentException(rawString);
    }
}
