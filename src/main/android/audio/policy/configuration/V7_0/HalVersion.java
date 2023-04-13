package android.audio.policy.configuration.V7_0;
/* loaded from: classes.dex */
public enum HalVersion {
    _2_0("2.0"),
    _3_0("3.0");
    
    private final String rawName;

    HalVersion(String rawName) {
        this.rawName = rawName;
    }

    public String getRawName() {
        return this.rawName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static HalVersion fromString(String rawString) {
        HalVersion[] values;
        for (HalVersion _f : values()) {
            if (_f.getRawName().equals(rawString)) {
                return _f;
            }
        }
        throw new IllegalArgumentException(rawString);
    }
}
