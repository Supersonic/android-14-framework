package android.view.displayhash;

import android.p008os.RemoteException;
import android.util.ArraySet;
import android.util.Log;
import android.view.WindowManagerGlobal;
import java.util.Collections;
import java.util.Set;
/* loaded from: classes4.dex */
public final class DisplayHashManager {
    private static final String TAG = "DisplayHashManager";
    private static Set<String> sSupportedHashAlgorithms;
    private final Object mSupportedHashingAlgorithmLock = new Object();

    public Set<String> getSupportedHashAlgorithms() {
        synchronized (this.mSupportedHashingAlgorithmLock) {
            Set<String> set = sSupportedHashAlgorithms;
            if (set != null) {
                return set;
            }
            try {
                String[] supportedAlgorithms = WindowManagerGlobal.getWindowManagerService().getSupportedDisplayHashAlgorithms();
                if (supportedAlgorithms == null) {
                    return Collections.emptySet();
                }
                ArraySet arraySet = new ArraySet(supportedAlgorithms);
                sSupportedHashAlgorithms = arraySet;
                return arraySet;
            } catch (RemoteException e) {
                Log.m109e(TAG, "Failed to send request getSupportedHashingAlgorithms", e);
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public VerifiedDisplayHash verifyDisplayHash(DisplayHash displayHash) {
        try {
            return WindowManagerGlobal.getWindowManagerService().verifyDisplayHash(displayHash);
        } catch (RemoteException e) {
            Log.m109e(TAG, "Failed to send request verifyImpressionToken", e);
            throw e.rethrowFromSystemServer();
        }
    }

    public void setDisplayHashThrottlingEnabled(boolean enable) {
        try {
            WindowManagerGlobal.getWindowManagerService().setDisplayHashThrottlingEnabled(enable);
        } catch (RemoteException e) {
        }
    }
}
