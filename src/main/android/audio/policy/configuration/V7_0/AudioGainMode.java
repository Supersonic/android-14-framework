package android.audio.policy.configuration.V7_0;
/* loaded from: classes.dex */
public enum AudioGainMode {
    AUDIO_GAIN_MODE_JOINT("AUDIO_GAIN_MODE_JOINT"),
    AUDIO_GAIN_MODE_CHANNELS("AUDIO_GAIN_MODE_CHANNELS"),
    AUDIO_GAIN_MODE_RAMP("AUDIO_GAIN_MODE_RAMP");
    
    private final String rawName;

    AudioGainMode(String rawName) {
        this.rawName = rawName;
    }

    public String getRawName() {
        return this.rawName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AudioGainMode fromString(String rawString) {
        AudioGainMode[] values;
        for (AudioGainMode _f : values()) {
            if (_f.getRawName().equals(rawString)) {
                return _f;
            }
        }
        throw new IllegalArgumentException(rawString);
    }
}
