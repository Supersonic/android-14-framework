package android.audio.policy.configuration.V7_0;
/* loaded from: classes.dex */
public enum AudioSource {
    AUDIO_SOURCE_DEFAULT("AUDIO_SOURCE_DEFAULT"),
    AUDIO_SOURCE_MIC("AUDIO_SOURCE_MIC"),
    AUDIO_SOURCE_VOICE_UPLINK("AUDIO_SOURCE_VOICE_UPLINK"),
    AUDIO_SOURCE_VOICE_DOWNLINK("AUDIO_SOURCE_VOICE_DOWNLINK"),
    AUDIO_SOURCE_VOICE_CALL("AUDIO_SOURCE_VOICE_CALL"),
    AUDIO_SOURCE_CAMCORDER("AUDIO_SOURCE_CAMCORDER"),
    AUDIO_SOURCE_VOICE_RECOGNITION("AUDIO_SOURCE_VOICE_RECOGNITION"),
    AUDIO_SOURCE_VOICE_COMMUNICATION("AUDIO_SOURCE_VOICE_COMMUNICATION"),
    AUDIO_SOURCE_REMOTE_SUBMIX("AUDIO_SOURCE_REMOTE_SUBMIX"),
    AUDIO_SOURCE_UNPROCESSED("AUDIO_SOURCE_UNPROCESSED"),
    AUDIO_SOURCE_VOICE_PERFORMANCE("AUDIO_SOURCE_VOICE_PERFORMANCE"),
    AUDIO_SOURCE_ECHO_REFERENCE("AUDIO_SOURCE_ECHO_REFERENCE"),
    AUDIO_SOURCE_FM_TUNER("AUDIO_SOURCE_FM_TUNER"),
    AUDIO_SOURCE_HOTWORD("AUDIO_SOURCE_HOTWORD");
    
    private final String rawName;

    AudioSource(String rawName) {
        this.rawName = rawName;
    }

    public String getRawName() {
        return this.rawName;
    }

    static AudioSource fromString(String rawString) {
        AudioSource[] values;
        for (AudioSource _f : values()) {
            if (_f.getRawName().equals(rawString)) {
                return _f;
            }
        }
        throw new IllegalArgumentException(rawString);
    }
}
