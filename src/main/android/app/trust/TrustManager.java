package android.app.trust;

import android.app.trust.ITrustListener;
import android.app.trust.ITrustManager;
import android.hardware.biometrics.BiometricSourceType;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.util.ArrayMap;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class TrustManager {
    private static final String DATA_FLAGS = "initiatedByUser";
    private static final String DATA_GRANTED_MESSAGES = "grantedMessages";
    private static final String DATA_MESSAGE = "message";
    private static final String DATA_NEWLY_UNLOCKED = "newlyUnlocked";
    private static final int MSG_TRUST_CHANGED = 1;
    private static final int MSG_TRUST_ERROR = 3;
    private static final int MSG_TRUST_MANAGED_CHANGED = 2;
    private static final String TAG = "TrustManager";
    private final ITrustManager mService;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: android.app.trust.TrustManager.2
        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bundle data = msg.peekData();
                    int flags = data != null ? data.getInt(TrustManager.DATA_FLAGS) : 0;
                    boolean enabled = msg.arg1 != 0;
                    int newlyUnlockedInt = data != null ? data.getInt(TrustManager.DATA_NEWLY_UNLOCKED) : 0;
                    boolean newlyUnlocked = newlyUnlockedInt != 0;
                    ((TrustListener) msg.obj).onTrustChanged(enabled, newlyUnlocked, msg.arg2, flags, msg.getData().getStringArrayList(TrustManager.DATA_GRANTED_MESSAGES));
                    return;
                case 2:
                    ((TrustListener) msg.obj).onTrustManagedChanged(msg.arg1 != 0, msg.arg2);
                    return;
                case 3:
                    CharSequence message = msg.peekData().getCharSequence("message");
                    ((TrustListener) msg.obj).onTrustError(message);
                    return;
                default:
                    return;
            }
        }
    };
    private final ArrayMap<TrustListener, ITrustListener> mTrustListeners = new ArrayMap<>();

    /* loaded from: classes.dex */
    public interface TrustListener {
        void onTrustChanged(boolean z, boolean z2, int i, int i2, List<String> list);

        void onTrustError(CharSequence charSequence);

        void onTrustManagedChanged(boolean z, int i);
    }

    public TrustManager(IBinder b) {
        this.mService = ITrustManager.Stub.asInterface(b);
    }

    public void setDeviceLockedForUser(int userId, boolean locked) {
        try {
            this.mService.setDeviceLockedForUser(userId, locked);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportUnlockAttempt(boolean successful, int userId) {
        try {
            this.mService.reportUnlockAttempt(successful, userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportUserRequestedUnlock(int userId, boolean dismissKeyguard) {
        try {
            this.mService.reportUserRequestedUnlock(userId, dismissKeyguard);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportUserMayRequestUnlock(int userId) {
        try {
            this.mService.reportUserMayRequestUnlock(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportUnlockLockout(int timeoutMs, int userId) {
        try {
            this.mService.reportUnlockLockout(timeoutMs, userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportEnabledTrustAgentsChanged(int userId) {
        try {
            this.mService.reportEnabledTrustAgentsChanged(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportKeyguardShowingChanged() {
        try {
            this.mService.reportKeyguardShowingChanged();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void registerTrustListener(final TrustListener trustListener) {
        try {
            ITrustListener.Stub iTrustListener = new ITrustListener.Stub() { // from class: android.app.trust.TrustManager.1
                @Override // android.app.trust.ITrustListener
                public void onTrustChanged(boolean enabled, boolean newlyUnlocked, int userId, int flags, List<String> trustGrantedMessages) {
                    Message m = TrustManager.this.mHandler.obtainMessage(1, enabled ? 1 : 0, userId, trustListener);
                    if (flags != 0) {
                        m.getData().putInt(TrustManager.DATA_FLAGS, flags);
                    }
                    m.getData().putInt(TrustManager.DATA_NEWLY_UNLOCKED, newlyUnlocked ? 1 : 0);
                    m.getData().putCharSequenceArrayList(TrustManager.DATA_GRANTED_MESSAGES, (ArrayList) trustGrantedMessages);
                    m.sendToTarget();
                }

                @Override // android.app.trust.ITrustListener
                public void onTrustManagedChanged(boolean managed, int userId) {
                    TrustManager.this.mHandler.obtainMessage(2, managed ? 1 : 0, userId, trustListener).sendToTarget();
                }

                @Override // android.app.trust.ITrustListener
                public void onTrustError(CharSequence message) {
                    Message m = TrustManager.this.mHandler.obtainMessage(3, trustListener);
                    m.getData().putCharSequence("message", message);
                    m.sendToTarget();
                }
            };
            this.mService.registerTrustListener(iTrustListener);
            this.mTrustListeners.put(trustListener, iTrustListener);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void unregisterTrustListener(TrustListener trustListener) {
        ITrustListener iTrustListener = this.mTrustListeners.remove(trustListener);
        if (iTrustListener != null) {
            try {
                this.mService.unregisterTrustListener(iTrustListener);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public boolean isTrustUsuallyManaged(int userId) {
        try {
            return this.mService.isTrustUsuallyManaged(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void unlockedByBiometricForUser(int userId, BiometricSourceType source) {
        try {
            this.mService.unlockedByBiometricForUser(userId, source);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void clearAllBiometricRecognized(BiometricSourceType source, int unlockedUser) {
        try {
            this.mService.clearAllBiometricRecognized(source, unlockedUser);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
