package android.audio.policy.configuration.V7_0;
/* loaded from: classes.dex */
public enum MixType {
    mix("mix"),
    mux("mux");
    
    private final String rawName;

    MixType(String rawName) {
        this.rawName = rawName;
    }

    public String getRawName() {
        return this.rawName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MixType fromString(String rawString) {
        MixType[] values;
        for (MixType _f : values()) {
            if (_f.getRawName().equals(rawString)) {
                return _f;
            }
        }
        throw new IllegalArgumentException(rawString);
    }
}
