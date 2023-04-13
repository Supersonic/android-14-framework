package android.telephony.mbms;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public interface GroupCallCallback {
    public static final int SIGNAL_STRENGTH_UNAVAILABLE = -1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface GroupCallError {
    }

    default void onError(int errorCode, String message) {
    }

    default void onGroupCallStateChanged(int state, int reason) {
    }

    default void onBroadcastSignalStrengthUpdated(int signalStrength) {
    }
}
