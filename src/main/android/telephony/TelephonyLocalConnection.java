package android.telephony;

import java.util.UUID;
/* loaded from: classes3.dex */
public class TelephonyLocalConnection {
    private static ConnectionImpl sInstance;

    /* loaded from: classes3.dex */
    public interface ConnectionImpl {
        String getCallComposerServerUrlForHandle(int i, UUID uuid);
    }

    public static String getCallComposerServerUrlForHandle(int subscriptionId, UUID uuid) {
        checkInstance();
        return sInstance.getCallComposerServerUrlForHandle(subscriptionId, uuid);
    }

    private static void checkInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Connection impl is null!");
        }
    }

    public static void setInstance(ConnectionImpl impl) {
        sInstance = impl;
    }
}
