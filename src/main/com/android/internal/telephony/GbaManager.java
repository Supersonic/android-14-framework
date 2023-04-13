package com.android.internal.telephony;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.IBootstrapAuthenticationCallback;
import android.telephony.SubscriptionManager;
import android.telephony.gba.GbaAuthRequest;
import android.telephony.gba.IGbaService;
import android.text.TextUtils;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.metrics.RcsStats;
import com.android.telephony.Rlog;
import java.util.concurrent.ConcurrentLinkedQueue;
/* loaded from: classes.dex */
public class GbaManager {
    @VisibleForTesting
    public static final int MAX_RETRY = 5;
    @VisibleForTesting
    public static final int REQUEST_TIMEOUT_MS = 5000;
    @VisibleForTesting
    public static final int RETRY_TIME_MS = 3000;
    private final Context mContext;
    private GbaDeathRecipient mDeathRecipient;
    private Handler mHandler;
    private IGbaService mIGbaService;
    private final String mLogTag;
    private final RcsStats mRcsStats;
    private int mReleaseTime;
    private GbaServiceConnection mServiceConnection;
    private String mServicePackageName;
    private final int mSubId;
    private String mTargetBindingPackageName;
    private static final boolean DBG = Build.IS_DEBUGGABLE;
    private static final SparseArray<GbaManager> sGbaManagers = new SparseArray<>();
    private int mRetryTimes = 0;
    private final ConcurrentLinkedQueue<GbaAuthRequest> mRequestQueue = new ConcurrentLinkedQueue<>();
    private final SparseArray<IBootstrapAuthenticationCallback> mCallbacks = new SparseArray<>();
    private final IBootstrapAuthenticationCallback mServiceCallback = new IBootstrapAuthenticationCallback.Stub() { // from class: com.android.internal.telephony.GbaManager.1
        public void onKeysAvailable(int i, byte[] bArr, String str) {
            IBootstrapAuthenticationCallback iBootstrapAuthenticationCallback;
            GbaManager gbaManager = GbaManager.this;
            gbaManager.logv("onKeysAvailable: " + Integer.toHexString(i) + ", id: " + str);
            synchronized (GbaManager.this.mCallbacks) {
                iBootstrapAuthenticationCallback = (IBootstrapAuthenticationCallback) GbaManager.this.mCallbacks.get(i);
            }
            if (iBootstrapAuthenticationCallback != null) {
                try {
                    iBootstrapAuthenticationCallback.onKeysAvailable(i, bArr, str);
                    GbaManager.this.mRcsStats.onGbaSuccessEvent(GbaManager.this.mSubId);
                } catch (RemoteException e) {
                    GbaManager gbaManager2 = GbaManager.this;
                    gbaManager2.logd("RemoteException " + e);
                }
                synchronized (GbaManager.this.mCallbacks) {
                    GbaManager.this.mCallbacks.remove(i);
                    if (GbaManager.this.mCallbacks.size() == 0) {
                        GbaManager.this.releaseServiceAsNeeded(0);
                    }
                }
            }
        }

        public void onAuthenticationFailure(int i, int i2) {
            IBootstrapAuthenticationCallback iBootstrapAuthenticationCallback;
            GbaManager gbaManager = GbaManager.this;
            gbaManager.logd("onAuthenticationFailure: " + Integer.toHexString(i) + " for: " + i2);
            synchronized (GbaManager.this.mCallbacks) {
                iBootstrapAuthenticationCallback = (IBootstrapAuthenticationCallback) GbaManager.this.mCallbacks.get(i);
            }
            if (iBootstrapAuthenticationCallback != null) {
                try {
                    iBootstrapAuthenticationCallback.onAuthenticationFailure(i, i2);
                    GbaManager.this.mRcsStats.onGbaFailureEvent(GbaManager.this.mSubId, i2);
                } catch (RemoteException e) {
                    GbaManager gbaManager2 = GbaManager.this;
                    gbaManager2.logd("RemoteException " + e);
                }
                synchronized (GbaManager.this.mCallbacks) {
                    GbaManager.this.mCallbacks.remove(i);
                    if (GbaManager.this.mCallbacks.size() == 0) {
                        GbaManager.this.releaseServiceAsNeeded(0);
                    }
                }
            }
        }
    };

    /* loaded from: classes.dex */
    private final class GbaManagerHandler extends Handler {
        GbaManagerHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            GbaManager gbaManager = GbaManager.this;
            gbaManager.logv("handle msg:" + message.what);
            switch (message.what) {
                case 1:
                    GbaManager gbaManager2 = GbaManager.this;
                    int i = gbaManager2.mRetryTimes;
                    gbaManager2.mRetryTimes = i + 1;
                    if (i < 5) {
                        GbaManager.this.rebindService(false);
                        return;
                    }
                    GbaManager.this.loge("Too many retries, stop now!");
                    sendEmptyMessage(3);
                    return;
                case 2:
                    if (GbaManager.this.mRequestQueue.isEmpty()) {
                        GbaManager.this.clearCallbacksAndNotifyFailure();
                        GbaManager.this.unbindService();
                        return;
                    }
                    return;
                case 3:
                case 4:
                    GbaManager.this.mRetryTimes = 0;
                    GbaManager.this.processRequests();
                    return;
                case 5:
                    GbaManager.this.mRetryTimes = 0;
                    if (GbaManager.this.isServiceConnetable() || GbaManager.this.isServiceConnected()) {
                        GbaManager.this.rebindService(true);
                        return;
                    }
                    return;
                case 6:
                    if (GbaManager.this.isServiceConnected()) {
                        GbaManager.this.processRequests();
                        return;
                    } else if (GbaManager.this.mHandler.hasMessages(1)) {
                        return;
                    } else {
                        GbaManager.this.mHandler.sendEmptyMessage(1);
                        return;
                    }
                default:
                    GbaManager gbaManager3 = GbaManager.this;
                    gbaManager3.loge("Unhandled event " + message.what);
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class GbaDeathRecipient implements IBinder.DeathRecipient {
        private IBinder mBinder;
        private final ComponentName mComponentName;

        GbaDeathRecipient(ComponentName componentName) {
            this.mComponentName = componentName;
        }

        public void linkToDeath(IBinder iBinder) throws RemoteException {
            if (iBinder != null) {
                this.mBinder = iBinder;
                iBinder.linkToDeath(this, 0);
            }
        }

        public synchronized void unlinkToDeath() {
            IBinder iBinder = this.mBinder;
            if (iBinder != null) {
                iBinder.unlinkToDeath(this, 0);
                this.mBinder = null;
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            GbaManager gbaManager = GbaManager.this;
            gbaManager.logd("GbaService(" + this.mComponentName + ") has died.");
            unlinkToDeath();
            GbaManager.this.retryBind();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class GbaServiceConnection implements ServiceConnection {
        private GbaServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            GbaManager gbaManager = GbaManager.this;
            gbaManager.logd("service " + componentName + " for Gba is connected.");
            GbaManager.this.mIGbaService = IGbaService.Stub.asInterface(iBinder);
            GbaManager gbaManager2 = GbaManager.this;
            gbaManager2.mDeathRecipient = new GbaDeathRecipient(componentName);
            try {
                GbaManager.this.mDeathRecipient.linkToDeath(iBinder);
            } catch (RemoteException e) {
                GbaManager.this.mDeathRecipient.binderDied();
                GbaManager gbaManager3 = GbaManager.this;
                gbaManager3.logd("RemoteException " + e);
            }
            GbaManager.this.mHandler.sendEmptyMessage(4);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            GbaManager gbaManager = GbaManager.this;
            gbaManager.logd("service " + componentName + " is now disconnected.");
            GbaManager.this.mTargetBindingPackageName = null;
        }
    }

    @VisibleForTesting
    public GbaManager(Context context, int i, String str, int i2, RcsStats rcsStats) {
        this.mContext = context;
        this.mSubId = i;
        String str2 = "GbaManager[" + i + "]";
        this.mLogTag = str2;
        this.mServicePackageName = str;
        this.mReleaseTime = i2;
        HandlerThread handlerThread = new HandlerThread(str2);
        handlerThread.start();
        GbaManagerHandler gbaManagerHandler = new GbaManagerHandler(handlerThread.getLooper());
        this.mHandler = gbaManagerHandler;
        if (this.mReleaseTime < 0) {
            gbaManagerHandler.sendEmptyMessage(1);
        }
        this.mRcsStats = rcsStats;
    }

    public static GbaManager make(Context context, int i, String str, int i2) {
        GbaManager gbaManager = new GbaManager(context, i, str, i2, RcsStats.getInstance());
        SparseArray<GbaManager> sparseArray = sGbaManagers;
        synchronized (sparseArray) {
            sparseArray.put(i, gbaManager);
        }
        return gbaManager;
    }

    public static GbaManager getInstance(int i) {
        GbaManager gbaManager;
        SparseArray<GbaManager> sparseArray = sGbaManagers;
        synchronized (sparseArray) {
            gbaManager = sparseArray.get(i);
        }
        return gbaManager;
    }

    public void bootstrapAuthenticationRequest(GbaAuthRequest gbaAuthRequest) {
        logv("bootstrapAuthenticationRequest: " + gbaAuthRequest);
        if (TextUtils.isEmpty(getServicePackage())) {
            logd("do not support!");
            try {
                gbaAuthRequest.getCallback().onAuthenticationFailure(gbaAuthRequest.getToken(), 1);
                return;
            } catch (RemoteException e) {
                loge("exception to call service: " + e);
                return;
            }
        }
        this.mRequestQueue.offer(gbaAuthRequest);
        if (this.mHandler.hasMessages(6)) {
            return;
        }
        this.mHandler.sendEmptyMessage(6);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processRequests() {
        if (isServiceConnected()) {
            while (!this.mRequestQueue.isEmpty()) {
                try {
                    GbaAuthRequest gbaAuthRequest = new GbaAuthRequest(this.mRequestQueue.peek());
                    synchronized (this.mCallbacks) {
                        this.mCallbacks.put(gbaAuthRequest.getToken(), gbaAuthRequest.getCallback());
                    }
                    gbaAuthRequest.setCallback(this.mServiceCallback);
                    this.mIGbaService.authenticationRequest(gbaAuthRequest);
                    this.mRequestQueue.poll();
                } catch (RemoteException e) {
                    this.mDeathRecipient.binderDied();
                    logd("RemoteException " + e);
                }
            }
        } else {
            while (!this.mRequestQueue.isEmpty()) {
                GbaAuthRequest poll = this.mRequestQueue.poll();
                try {
                    poll.getCallback().onAuthenticationFailure(poll.getToken(), 1);
                } catch (RemoteException e2) {
                    logd("RemoteException " + e2);
                }
            }
        }
        releaseServiceAsNeeded(REQUEST_TIMEOUT_MS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseServiceAsNeeded(int i) {
        int releaseTime = getReleaseTime();
        if (releaseTime < 0) {
            return;
        }
        if (releaseTime > i) {
            i = releaseTime;
        }
        if (this.mHandler.hasMessages(2)) {
            this.mHandler.removeMessages(2);
        }
        this.mHandler.sendEmptyMessageDelayed(2, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearCallbacksAndNotifyFailure() {
        synchronized (this.mCallbacks) {
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                try {
                    this.mCallbacks.valueAt(i).onAuthenticationFailure(this.mCallbacks.keyAt(i), 0);
                } catch (RemoteException e) {
                    logd("RemoteException " + e);
                }
            }
            this.mCallbacks.clear();
        }
    }

    @VisibleForTesting
    public boolean isServiceConnected() {
        boolean z;
        synchronized (this) {
            IGbaService iGbaService = this.mIGbaService;
            z = iGbaService != null && iGbaService.asBinder().isBinderAlive() && TextUtils.equals(this.mServicePackageName, this.mTargetBindingPackageName);
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isServiceConnetable() {
        boolean z;
        synchronized (this) {
            z = this.mTargetBindingPackageName != null || (this.mReleaseTime < 0 && !TextUtils.isEmpty(this.mServicePackageName));
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unbindService() {
        GbaDeathRecipient gbaDeathRecipient = this.mDeathRecipient;
        if (gbaDeathRecipient != null) {
            gbaDeathRecipient.unlinkToDeath();
        }
        if (this.mServiceConnection != null) {
            logv("unbind service.");
            this.mContext.unbindService(this.mServiceConnection);
        }
        this.mDeathRecipient = null;
        this.mIGbaService = null;
        this.mServiceConnection = null;
        this.mTargetBindingPackageName = null;
    }

    private void bindService() {
        if (this.mContext == null || !SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            loge("Can't bind service with invalid sub Id.");
            return;
        }
        String servicePackage = getServicePackage();
        if (TextUtils.isEmpty(servicePackage)) {
            loge("Can't find the binding package");
            return;
        }
        Intent intent = new Intent("android.telephony.gba.GbaService");
        intent.setPackage(servicePackage);
        try {
            logv("Trying to bind " + servicePackage);
            GbaServiceConnection gbaServiceConnection = new GbaServiceConnection();
            this.mServiceConnection = gbaServiceConnection;
            if (!this.mContext.bindService(intent, gbaServiceConnection, 67108865)) {
                logd("Cannot bind to the service.");
                retryBind();
                return;
            }
            this.mTargetBindingPackageName = servicePackage;
        } catch (SecurityException e) {
            loge("bindService failed " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void retryBind() {
        if (this.mHandler.hasMessages(1)) {
            logv("wait for pending retry.");
            return;
        }
        logv("starting retry:" + this.mRetryTimes);
        this.mHandler.sendEmptyMessageDelayed(1, 3000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void rebindService(boolean z) {
        if (!z && isServiceConnected()) {
            logv("Service " + getServicePackage() + " already bound or being bound.");
            return;
        }
        unbindService();
        bindService();
    }

    public boolean overrideServicePackage(String str) {
        synchronized (this) {
            if (TextUtils.equals(this.mServicePackageName, str)) {
                return false;
            }
            logv("Service package name is changed from " + this.mServicePackageName + " to " + str);
            this.mServicePackageName = str;
            if (!this.mHandler.hasMessages(5)) {
                this.mHandler.sendEmptyMessage(5);
            }
            return true;
        }
    }

    public String getServicePackage() {
        String str;
        synchronized (this) {
            str = this.mServicePackageName;
        }
        return str;
    }

    public boolean overrideReleaseTime(int i) {
        synchronized (this) {
            if (this.mReleaseTime != i) {
                logv("Service release time is changed from " + this.mReleaseTime + " to " + i);
                this.mReleaseTime = i;
                if (!this.mHandler.hasMessages(5)) {
                    this.mHandler.sendEmptyMessage(5);
                }
                return true;
            }
            return false;
        }
    }

    public int getReleaseTime() {
        int i;
        synchronized (this) {
            i = this.mReleaseTime;
        }
        return i;
    }

    @VisibleForTesting
    public Handler getHandler() {
        return this.mHandler;
    }

    @VisibleForTesting
    public void destroy() {
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler.getLooper().quit();
        this.mRequestQueue.clear();
        this.mCallbacks.clear();
        unbindService();
        sGbaManagers.remove(this.mSubId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logv(String str) {
        if (DBG) {
            Rlog.d(this.mLogTag, str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logd(String str) {
        Rlog.d(this.mLogTag, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String str) {
        Rlog.e(this.mLogTag, str);
    }
}
