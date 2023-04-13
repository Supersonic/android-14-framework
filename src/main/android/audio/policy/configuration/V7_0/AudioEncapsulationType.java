package android.audio.policy.configuration.V7_0;
/* loaded from: classes.dex */
public enum AudioEncapsulationType {
    AUDIO_ENCAPSULATION_TYPE_NONE("AUDIO_ENCAPSULATION_TYPE_NONE"),
    AUDIO_ENCAPSULATION_TYPE_IEC61937("AUDIO_ENCAPSULATION_TYPE_IEC61937");
    
    private final String rawName;

    AudioEncapsulationType(String rawName) {
        this.rawName = rawName;
    }

    public String getRawName() {
        return this.rawName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AudioEncapsulationType fromString(String rawString) {
        AudioEncapsulationType[] values;
        for (AudioEncapsulationType _f : values()) {
            if (_f.getRawName().equals(rawString)) {
                return _f;
            }
        }
        throw new IllegalArgumentException(rawString);
    }
}
