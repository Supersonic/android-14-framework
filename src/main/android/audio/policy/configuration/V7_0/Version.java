package android.audio.policy.configuration.V7_0;
/* loaded from: classes.dex */
public enum Version {
    _7_0("7.0");
    
    private final String rawName;

    Version(String rawName) {
        this.rawName = rawName;
    }

    public String getRawName() {
        return this.rawName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Version fromString(String rawString) {
        Version[] values;
        for (Version _f : values()) {
            if (_f.getRawName().equals(rawString)) {
                return _f;
            }
        }
        throw new IllegalArgumentException(rawString);
    }
}
