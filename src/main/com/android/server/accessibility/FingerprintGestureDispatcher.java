package com.android.server.accessibility;

import android.content.res.Resources;
import android.hardware.fingerprint.IFingerprintClientActiveCallback;
import android.hardware.fingerprint.IFingerprintService;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.util.FrameworkStatsLog;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class FingerprintGestureDispatcher extends IFingerprintClientActiveCallback.Stub implements Handler.Callback {
    public final IFingerprintService mFingerprintService;
    public final boolean mHardwareSupportsGestures;
    public final Object mLock;
    public boolean mRegisteredReadOnlyExceptInHandler;
    public final List<FingerprintGestureClient> mCapturingClients = new ArrayList(0);
    public final Handler mHandler = new Handler(this);

    /* loaded from: classes.dex */
    public interface FingerprintGestureClient {
        boolean isCapturingFingerprintGestures();

        void onFingerprintGesture(int i);

        void onFingerprintGestureDetectionActiveChanged(boolean z);
    }

    public FingerprintGestureDispatcher(IFingerprintService iFingerprintService, Resources resources, Object obj) {
        this.mFingerprintService = iFingerprintService;
        this.mHardwareSupportsGestures = resources.getBoolean(17891685);
        this.mLock = obj;
    }

    public void updateClientList(List<? extends FingerprintGestureClient> list) {
        if (this.mHardwareSupportsGestures) {
            synchronized (this.mLock) {
                this.mCapturingClients.clear();
                for (int i = 0; i < list.size(); i++) {
                    FingerprintGestureClient fingerprintGestureClient = list.get(i);
                    if (fingerprintGestureClient.isCapturingFingerprintGestures()) {
                        this.mCapturingClients.add(fingerprintGestureClient);
                    }
                }
                if (this.mCapturingClients.isEmpty()) {
                    if (this.mRegisteredReadOnlyExceptInHandler) {
                        this.mHandler.obtainMessage(2).sendToTarget();
                    }
                } else if (!this.mRegisteredReadOnlyExceptInHandler) {
                    this.mHandler.obtainMessage(1).sendToTarget();
                }
            }
        }
    }

    public void onClientActiveChanged(boolean z) {
        if (this.mHardwareSupportsGestures) {
            synchronized (this.mLock) {
                for (int i = 0; i < this.mCapturingClients.size(); i++) {
                    this.mCapturingClients.get(i).onFingerprintGestureDetectionActiveChanged(!z);
                }
            }
        }
    }

    public boolean isFingerprintGestureDetectionAvailable() {
        if (this.mHardwareSupportsGestures) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return !this.mFingerprintService.isClientActive();
            } catch (RemoteException unused) {
                return false;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return false;
    }

    public boolean onFingerprintGesture(int i) {
        int i2;
        synchronized (this.mLock) {
            if (this.mCapturingClients.isEmpty()) {
                return false;
            }
            switch (i) {
                case FrameworkStatsLog.TV_CAS_SESSION_OPEN_STATUS /* 280 */:
                    i2 = 4;
                    break;
                case FrameworkStatsLog.ASSISTANT_INVOCATION_REPORTED /* 281 */:
                    i2 = 8;
                    break;
                case FrameworkStatsLog.DISPLAY_WAKE_REPORTED /* 282 */:
                    i2 = 2;
                    break;
                case 283:
                    i2 = 1;
                    break;
                default:
                    return false;
            }
            ArrayList arrayList = new ArrayList(this.mCapturingClients);
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                ((FingerprintGestureClient) arrayList.get(i3)).onFingerprintGesture(i2);
            }
            return true;
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        long clearCallingIdentity;
        int i = message.what;
        if (i == 1) {
            clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                    this.mFingerprintService.addClientActiveCallback(this);
                    this.mRegisteredReadOnlyExceptInHandler = true;
                } catch (RemoteException unused) {
                    Slog.e("FingerprintGestureDispatcher", "Failed to register for fingerprint activity callbacks");
                }
                return false;
            } finally {
            }
        } else if (i == 2) {
            clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                    this.mFingerprintService.removeClientActiveCallback(this);
                } finally {
                }
            } catch (RemoteException unused2) {
                Slog.e("FingerprintGestureDispatcher", "Failed to unregister for fingerprint activity callbacks");
            }
            this.mRegisteredReadOnlyExceptInHandler = false;
            return true;
        } else {
            Slog.e("FingerprintGestureDispatcher", "Unknown message: " + message.what);
            return false;
        }
    }
}
