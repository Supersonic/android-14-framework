package android.telephony.mbms;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
/* loaded from: classes3.dex */
public interface MbmsGroupCallSessionCallback {

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface GroupCallError {
    }

    default void onError(int errorCode, String message) {
    }

    default void onAvailableSaisUpdated(List<Integer> currentSais, List<List<Integer>> availableSais) {
    }

    default void onServiceInterfaceAvailable(String interfaceName, int index) {
    }

    default void onMiddlewareReady() {
    }
}
