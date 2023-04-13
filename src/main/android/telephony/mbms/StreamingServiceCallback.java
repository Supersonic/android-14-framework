package android.telephony.mbms;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public class StreamingServiceCallback {
    public static final int SIGNAL_STRENGTH_UNAVAILABLE = -1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    private @interface StreamingServiceError {
    }

    public void onError(int errorCode, String message) {
    }

    public void onStreamStateUpdated(int state, int reason) {
    }

    public void onMediaDescriptionUpdated() {
    }

    public void onBroadcastSignalStrengthUpdated(int signalStrength) {
    }

    public void onStreamMethodUpdated(int methodType) {
    }
}
