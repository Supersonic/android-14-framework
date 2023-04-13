package android.audio.policy.configuration.V7_0;

import android.app.slice.Slice;
/* loaded from: classes.dex */
public enum Role {
    sink("sink"),
    source(Slice.SUBTYPE_SOURCE);
    
    private final String rawName;

    Role(String rawName) {
        this.rawName = rawName;
    }

    public String getRawName() {
        return this.rawName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Role fromString(String rawString) {
        Role[] values;
        for (Role _f : values()) {
            if (_f.getRawName().equals(rawString)) {
                return _f;
            }
        }
        throw new IllegalArgumentException(rawString);
    }
}
