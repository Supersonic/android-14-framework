package android.net.networkstack;

import android.content.Context;
import android.net.INetworkStackConnector;
import android.net.NetworkStack;
import android.os.IBinder;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public class ModuleNetworkStackClient extends NetworkStackClientBase {
    private static final String TAG = "ModuleNetworkStackClient";
    private static ModuleNetworkStackClient sInstance;

    private ModuleNetworkStackClient() {
    }

    public static synchronized ModuleNetworkStackClient getInstance(Context context) {
        ModuleNetworkStackClient moduleNetworkStackClient;
        synchronized (ModuleNetworkStackClient.class) {
            if (sInstance == null) {
                ModuleNetworkStackClient moduleNetworkStackClient2 = new ModuleNetworkStackClient();
                sInstance = moduleNetworkStackClient2;
                moduleNetworkStackClient2.startPolling();
            }
            moduleNetworkStackClient = sInstance;
        }
        return moduleNetworkStackClient;
    }

    @VisibleForTesting
    public static synchronized void resetInstanceForTest() {
        synchronized (ModuleNetworkStackClient.class) {
            sInstance = null;
        }
    }

    private void startPolling() {
        IBinder service = NetworkStack.getService();
        if (service != null) {
            onNetworkStackConnected(INetworkStackConnector.Stub.asInterface(service));
        } else {
            new Thread(new PollingRunner()).start();
        }
    }

    /* loaded from: classes.dex */
    public class PollingRunner implements Runnable {
        public PollingRunner() {
        }

        @Override // java.lang.Runnable
        public void run() {
            while (true) {
                IBinder service = NetworkStack.getService();
                if (service == null) {
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        Log.e(ModuleNetworkStackClient.TAG, "Interrupted while waiting for NetworkStack connector", e);
                    }
                } else {
                    ModuleNetworkStackClient.this.onNetworkStackConnected(INetworkStackConnector.Stub.asInterface(service));
                    return;
                }
            }
        }
    }
}
