package android.audio.policy.configuration.V7_0;
/* loaded from: classes.dex */
public enum EngineSuffix {
    _default("default"),
    configurable("configurable");
    
    private final String rawName;

    EngineSuffix(String rawName) {
        this.rawName = rawName;
    }

    public String getRawName() {
        return this.rawName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static EngineSuffix fromString(String rawString) {
        EngineSuffix[] values;
        for (EngineSuffix _f : values()) {
            if (_f.getRawName().equals(rawString)) {
                return _f;
            }
        }
        throw new IllegalArgumentException(rawString);
    }
}
