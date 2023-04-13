package android.nfc;

import android.nfc.INfcControllerAlwaysOnListener;
import android.nfc.NfcAdapter;
import android.p008os.Binder;
import android.p008os.RemoteException;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
/* loaded from: classes2.dex */
public class NfcControllerAlwaysOnListener extends INfcControllerAlwaysOnListener.Stub {
    private static final String TAG = NfcControllerAlwaysOnListener.class.getSimpleName();
    private final INfcAdapter mAdapter;
    private final Map<NfcAdapter.ControllerAlwaysOnListener, Executor> mListenerMap = new HashMap();
    private boolean mCurrentState = false;
    private boolean mIsRegistered = false;

    public NfcControllerAlwaysOnListener(INfcAdapter adapter) {
        this.mAdapter = adapter;
    }

    public void register(Executor executor, NfcAdapter.ControllerAlwaysOnListener listener) {
        try {
            if (!this.mAdapter.isControllerAlwaysOnSupported()) {
                return;
            }
            synchronized (this) {
                if (this.mListenerMap.containsKey(listener)) {
                    return;
                }
                this.mListenerMap.put(listener, executor);
                if (!this.mIsRegistered) {
                    try {
                        this.mAdapter.registerControllerAlwaysOnListener(this);
                        this.mIsRegistered = true;
                    } catch (RemoteException e) {
                        Log.m104w(TAG, "Failed to register");
                    }
                }
            }
        } catch (RemoteException e2) {
            Log.m104w(TAG, "Failed to register");
        }
    }

    public void unregister(NfcAdapter.ControllerAlwaysOnListener listener) {
        try {
            if (!this.mAdapter.isControllerAlwaysOnSupported()) {
                return;
            }
            synchronized (this) {
                if (this.mListenerMap.containsKey(listener)) {
                    this.mListenerMap.remove(listener);
                    if (this.mListenerMap.isEmpty() && this.mIsRegistered) {
                        try {
                            this.mAdapter.unregisterControllerAlwaysOnListener(this);
                        } catch (RemoteException e) {
                            Log.m104w(TAG, "Failed to unregister");
                        }
                        this.mIsRegistered = false;
                    }
                }
            }
        } catch (RemoteException e2) {
            Log.m104w(TAG, "Failed to unregister");
        }
    }

    private void sendCurrentState(final NfcAdapter.ControllerAlwaysOnListener listener) {
        synchronized (this) {
            Executor executor = this.mListenerMap.get(listener);
            long identity = Binder.clearCallingIdentity();
            executor.execute(new Runnable() { // from class: android.nfc.NfcControllerAlwaysOnListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    NfcControllerAlwaysOnListener.this.lambda$sendCurrentState$0(listener);
                }
            });
            Binder.restoreCallingIdentity(identity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendCurrentState$0(NfcAdapter.ControllerAlwaysOnListener listener) {
        listener.onControllerAlwaysOnChanged(this.mCurrentState);
    }

    @Override // android.nfc.INfcControllerAlwaysOnListener
    public void onControllerAlwaysOnChanged(boolean isEnabled) {
        synchronized (this) {
            this.mCurrentState = isEnabled;
            for (NfcAdapter.ControllerAlwaysOnListener cb : this.mListenerMap.keySet()) {
                sendCurrentState(cb);
            }
        }
    }
}
