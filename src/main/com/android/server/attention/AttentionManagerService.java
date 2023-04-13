package com.android.server.attention;

import android.app.ActivityThread;
import android.attention.AttentionManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.hardware.SensorPrivacyManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.service.attention.IAttentionCallback;
import android.service.attention.IAttentionService;
import android.service.attention.IProximityUpdateCallback;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.SystemService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class AttentionManagerService extends SystemService {
    @VisibleForTesting
    protected static final int ATTENTION_CACHE_BUFFER_SIZE = 5;
    @VisibleForTesting
    static final long DEFAULT_STALE_AFTER_MILLIS = 1000;
    @VisibleForTesting
    static final String KEY_SERVICE_ENABLED = "service_enabled";
    @VisibleForTesting
    static final String KEY_STALE_AFTER_MILLIS = "stale_after_millis";
    public static String sTestAttentionServicePackage;
    @GuardedBy({"mLock"})
    public AttentionCheckCacheBuffer mAttentionCheckCacheBuffer;
    public AttentionHandler mAttentionHandler;
    @GuardedBy({"mLock"})
    public boolean mBinding;
    @VisibleForTesting
    ComponentName mComponentName;
    public final AttentionServiceConnection mConnection;
    public final Context mContext;
    @GuardedBy({"mLock"})
    @VisibleForTesting
    AttentionCheck mCurrentAttentionCheck;
    @GuardedBy({"mLock"})
    @VisibleForTesting
    ProximityUpdate mCurrentProximityUpdate;
    @VisibleForTesting
    boolean mIsServiceEnabled;
    public final Object mLock;
    public final PowerManager mPowerManager;
    public final SensorPrivacyManager mPrivacyManager;
    @GuardedBy({"mLock"})
    @VisibleForTesting
    protected IAttentionService mService;
    public CountDownLatch mServiceBindingLatch;
    @VisibleForTesting
    long mStaleAfterMillis;

    public AttentionManagerService(Context context) {
        this(context, (PowerManager) context.getSystemService("power"), new Object(), null);
        this.mAttentionHandler = new AttentionHandler();
    }

    @VisibleForTesting
    public AttentionManagerService(Context context, PowerManager powerManager, Object obj, AttentionHandler attentionHandler) {
        super(context);
        this.mConnection = new AttentionServiceConnection();
        Objects.requireNonNull(context);
        this.mContext = context;
        this.mPowerManager = powerManager;
        this.mLock = obj;
        this.mAttentionHandler = attentionHandler;
        this.mPrivacyManager = SensorPrivacyManager.getInstance(context);
        this.mServiceBindingLatch = new CountDownLatch(1);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 500) {
            this.mContext.registerReceiver(new ScreenStateReceiver(), new IntentFilter("android.intent.action.SCREEN_OFF"));
            readValuesFromDeviceConfig();
            DeviceConfig.addOnPropertiesChangedListener("attention_manager_service", ActivityThread.currentApplication().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.attention.AttentionManagerService$$ExternalSyntheticLambda0
                public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                    AttentionManagerService.this.lambda$onBootPhase$0(properties);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBootPhase$0(DeviceConfig.Properties properties) {
        onDeviceConfigChange(properties.getKeyset());
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("attention", new BinderService());
        publishLocalService(AttentionManagerInternal.class, new LocalService());
    }

    public static boolean isServiceConfigured(Context context) {
        return !TextUtils.isEmpty(getServiceConfigPackage(context));
    }

    @VisibleForTesting
    public boolean isServiceAvailable() {
        if (this.mComponentName == null) {
            this.mComponentName = resolveAttentionService(this.mContext);
        }
        return this.mComponentName != null;
    }

    public final boolean getIsServiceEnabled() {
        return DeviceConfig.getBoolean("attention_manager_service", KEY_SERVICE_ENABLED, true);
    }

    @VisibleForTesting
    public long getStaleAfterMillis() {
        long j = DeviceConfig.getLong("attention_manager_service", KEY_STALE_AFTER_MILLIS, (long) DEFAULT_STALE_AFTER_MILLIS);
        if (j < 0 || j > 10000) {
            Slog.w("AttentionManagerService", "Bad flag value supplied for: stale_after_millis");
            return DEFAULT_STALE_AFTER_MILLIS;
        }
        return j;
    }

    public final void onDeviceConfigChange(Set<String> set) {
        for (String str : set) {
            str.hashCode();
            if (str.equals(KEY_STALE_AFTER_MILLIS) || str.equals(KEY_SERVICE_ENABLED)) {
                readValuesFromDeviceConfig();
                return;
            }
            Slog.i("AttentionManagerService", "Ignoring change on " + str);
        }
    }

    public final void readValuesFromDeviceConfig() {
        this.mIsServiceEnabled = getIsServiceEnabled();
        this.mStaleAfterMillis = getStaleAfterMillis();
        Slog.i("AttentionManagerService", "readValuesFromDeviceConfig():\nmIsServiceEnabled=" + this.mIsServiceEnabled + "\nmStaleAfterMillis=" + this.mStaleAfterMillis);
    }

    @VisibleForTesting
    public boolean checkAttention(long j, AttentionManagerInternal.AttentionCallbackInternal attentionCallbackInternal) {
        Objects.requireNonNull(attentionCallbackInternal);
        if (!this.mIsServiceEnabled) {
            Slog.w("AttentionManagerService", "Trying to call checkAttention() on an unsupported device.");
            return false;
        } else if (!isServiceAvailable()) {
            Slog.w("AttentionManagerService", "Service is not available at this moment.");
            return false;
        } else if (this.mPrivacyManager.isSensorPrivacyEnabled(2)) {
            Slog.w("AttentionManagerService", "Camera is locked by a toggle.");
            return false;
        } else if (!this.mPowerManager.isInteractive() || this.mPowerManager.isPowerSaveMode()) {
            return false;
        } else {
            synchronized (this.mLock) {
                freeIfInactiveLocked();
                bindLocked();
            }
            long uptimeMillis = SystemClock.uptimeMillis();
            awaitServiceBinding(Math.min((long) DEFAULT_STALE_AFTER_MILLIS, j));
            synchronized (this.mLock) {
                AttentionCheckCacheBuffer attentionCheckCacheBuffer = this.mAttentionCheckCacheBuffer;
                AttentionCheckCache last = attentionCheckCacheBuffer == null ? null : attentionCheckCacheBuffer.getLast();
                if (last != null && uptimeMillis < last.mLastComputed + this.mStaleAfterMillis) {
                    attentionCallbackInternal.onSuccess(last.mResult, last.mTimestamp);
                    return true;
                }
                AttentionCheck attentionCheck = this.mCurrentAttentionCheck;
                if (attentionCheck == null || (attentionCheck.mIsDispatched && this.mCurrentAttentionCheck.mIsFulfilled)) {
                    this.mCurrentAttentionCheck = new AttentionCheck(attentionCallbackInternal, this);
                    if (this.mService != null) {
                        try {
                            cancelAfterTimeoutLocked(j);
                            this.mService.checkAttention(this.mCurrentAttentionCheck.mIAttentionCallback);
                            this.mCurrentAttentionCheck.mIsDispatched = true;
                        } catch (RemoteException unused) {
                            Slog.e("AttentionManagerService", "Cannot call into the AttentionService");
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        }
    }

    @VisibleForTesting
    public void cancelAttentionCheck(AttentionManagerInternal.AttentionCallbackInternal attentionCallbackInternal) {
        synchronized (this.mLock) {
            if (!this.mCurrentAttentionCheck.mCallbackInternal.equals(attentionCallbackInternal)) {
                Slog.w("AttentionManagerService", "Cannot cancel a non-current request");
            } else {
                cancel();
            }
        }
    }

    @VisibleForTesting
    public boolean onStartProximityUpdates(AttentionManagerInternal.ProximityUpdateCallbackInternal proximityUpdateCallbackInternal) {
        Objects.requireNonNull(proximityUpdateCallbackInternal);
        if (!this.mIsServiceEnabled) {
            Slog.w("AttentionManagerService", "Trying to call onProximityUpdate() on an unsupported device.");
            return false;
        } else if (!isServiceAvailable()) {
            Slog.w("AttentionManagerService", "Service is not available at this moment.");
            return false;
        } else if (!this.mPowerManager.isInteractive()) {
            Slog.w("AttentionManagerService", "Proximity Service is unavailable during screen off at this moment.");
            return false;
        } else {
            synchronized (this.mLock) {
                freeIfInactiveLocked();
                bindLocked();
            }
            awaitServiceBinding(DEFAULT_STALE_AFTER_MILLIS);
            synchronized (this.mLock) {
                ProximityUpdate proximityUpdate = this.mCurrentProximityUpdate;
                if (proximityUpdate != null && proximityUpdate.mStartedUpdates) {
                    if (this.mCurrentProximityUpdate.mCallbackInternal == proximityUpdateCallbackInternal) {
                        Slog.w("AttentionManagerService", "Provided callback is already registered. Skipping.");
                        return true;
                    }
                    Slog.w("AttentionManagerService", "New proximity update cannot be processed because there is already an ongoing update");
                    return false;
                }
                ProximityUpdate proximityUpdate2 = new ProximityUpdate(proximityUpdateCallbackInternal);
                this.mCurrentProximityUpdate = proximityUpdate2;
                return proximityUpdate2.startUpdates();
            }
        }
    }

    @VisibleForTesting
    public void onStopProximityUpdates(AttentionManagerInternal.ProximityUpdateCallbackInternal proximityUpdateCallbackInternal) {
        synchronized (this.mLock) {
            ProximityUpdate proximityUpdate = this.mCurrentProximityUpdate;
            if (proximityUpdate != null && proximityUpdate.mCallbackInternal.equals(proximityUpdateCallbackInternal) && this.mCurrentProximityUpdate.mStartedUpdates) {
                this.mCurrentProximityUpdate.cancelUpdates();
                this.mCurrentProximityUpdate = null;
                return;
            }
            Slog.w("AttentionManagerService", "Cannot stop a non-current callback");
        }
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public void freeIfInactiveLocked() {
        this.mAttentionHandler.removeMessages(1);
        this.mAttentionHandler.sendEmptyMessageDelayed(1, 60000L);
    }

    @GuardedBy({"mLock"})
    public final void cancelAfterTimeoutLocked(long j) {
        this.mAttentionHandler.sendEmptyMessageDelayed(2, j);
    }

    public static String getServiceConfigPackage(Context context) {
        return context.getPackageManager().getAttentionServicePackageName();
    }

    public final void awaitServiceBinding(long j) {
        try {
            this.mServiceBindingLatch.await(j, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Slog.e("AttentionManagerService", "Interrupted while waiting to bind Attention Service.", e);
        }
    }

    public static ComponentName resolveAttentionService(Context context) {
        int i;
        String str;
        ServiceInfo serviceInfo;
        String serviceConfigPackage = getServiceConfigPackage(context);
        if (!TextUtils.isEmpty(sTestAttentionServicePackage)) {
            str = sTestAttentionServicePackage;
            i = 128;
        } else {
            if (!TextUtils.isEmpty(serviceConfigPackage)) {
                i = 1048576;
                str = serviceConfigPackage;
            }
            return null;
        }
        ResolveInfo resolveService = context.getPackageManager().resolveService(new Intent("android.service.attention.AttentionService").setPackage(str), i);
        if (resolveService == null || (serviceInfo = resolveService.serviceInfo) == null) {
            Slog.wtf("AttentionManagerService", String.format("Service %s not found in package %s", "android.service.attention.AttentionService", serviceConfigPackage));
            return null;
        } else if ("android.permission.BIND_ATTENTION_SERVICE".equals(serviceInfo.permission)) {
            return serviceInfo.getComponentName();
        } else {
            Slog.e("AttentionManagerService", String.format("Service %s should require %s permission. Found %s permission", serviceInfo.getComponentName(), "android.permission.BIND_ATTENTION_SERVICE", serviceInfo.permission));
            return null;
        }
    }

    public final void dumpInternal(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("Attention Manager Service (dumpsys attention) state:\n");
        indentingPrintWriter.println("isServiceEnabled=" + this.mIsServiceEnabled);
        indentingPrintWriter.println("mStaleAfterMillis=" + this.mStaleAfterMillis);
        indentingPrintWriter.println("AttentionServicePackageName=" + getServiceConfigPackage(this.mContext));
        indentingPrintWriter.println("Resolved component:");
        if (this.mComponentName != null) {
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("Component=" + this.mComponentName.getPackageName());
            indentingPrintWriter.println("Class=" + this.mComponentName.getClassName());
            indentingPrintWriter.decreaseIndent();
        }
        synchronized (this.mLock) {
            indentingPrintWriter.println("binding=" + this.mBinding);
            indentingPrintWriter.println("current attention check:");
            AttentionCheck attentionCheck = this.mCurrentAttentionCheck;
            if (attentionCheck != null) {
                attentionCheck.dump(indentingPrintWriter);
            }
            AttentionCheckCacheBuffer attentionCheckCacheBuffer = this.mAttentionCheckCacheBuffer;
            if (attentionCheckCacheBuffer != null) {
                attentionCheckCacheBuffer.dump(indentingPrintWriter);
            }
            ProximityUpdate proximityUpdate = this.mCurrentProximityUpdate;
            if (proximityUpdate != null) {
                proximityUpdate.dump(indentingPrintWriter);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class LocalService extends AttentionManagerInternal {
        public LocalService() {
        }

        public boolean isAttentionServiceSupported() {
            return AttentionManagerService.this.mIsServiceEnabled;
        }

        public boolean checkAttention(long j, AttentionManagerInternal.AttentionCallbackInternal attentionCallbackInternal) {
            return AttentionManagerService.this.checkAttention(j, attentionCallbackInternal);
        }

        public void cancelAttentionCheck(AttentionManagerInternal.AttentionCallbackInternal attentionCallbackInternal) {
            AttentionManagerService.this.cancelAttentionCheck(attentionCallbackInternal);
        }

        public boolean onStartProximityUpdates(AttentionManagerInternal.ProximityUpdateCallbackInternal proximityUpdateCallbackInternal) {
            return AttentionManagerService.this.onStartProximityUpdates(proximityUpdateCallbackInternal);
        }

        public void onStopProximityUpdates(AttentionManagerInternal.ProximityUpdateCallbackInternal proximityUpdateCallbackInternal) {
            AttentionManagerService.this.onStopProximityUpdates(proximityUpdateCallbackInternal);
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static final class AttentionCheckCacheBuffer {
        public final AttentionCheckCache[] mQueue = new AttentionCheckCache[5];
        public int mStartIndex = 0;
        public int mSize = 0;

        public AttentionCheckCache getLast() {
            int i = this.mStartIndex;
            int i2 = this.mSize;
            int i3 = ((i + i2) - 1) % 5;
            if (i2 == 0) {
                return null;
            }
            return this.mQueue[i3];
        }

        public void add(AttentionCheckCache attentionCheckCache) {
            int i = this.mStartIndex;
            int i2 = this.mSize;
            this.mQueue[(i + i2) % 5] = attentionCheckCache;
            if (i2 == 5) {
                this.mStartIndex = i + 1;
            } else {
                this.mSize = i2 + 1;
            }
        }

        public AttentionCheckCache get(int i) {
            if (i >= this.mSize) {
                return null;
            }
            return this.mQueue[(this.mStartIndex + i) % 5];
        }

        public final void dump(IndentingPrintWriter indentingPrintWriter) {
            indentingPrintWriter.println("attention check cache:");
            for (int i = 0; i < this.mSize; i++) {
                AttentionCheckCache attentionCheckCache = get(i);
                if (attentionCheckCache != null) {
                    indentingPrintWriter.increaseIndent();
                    indentingPrintWriter.println("timestamp=" + attentionCheckCache.mTimestamp);
                    indentingPrintWriter.println("result=" + attentionCheckCache.mResult);
                    indentingPrintWriter.decreaseIndent();
                }
            }
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static final class AttentionCheckCache {
        public final long mLastComputed;
        public final int mResult;
        public final long mTimestamp;

        public AttentionCheckCache(long j, int i, long j2) {
            this.mLastComputed = j;
            this.mResult = i;
            this.mTimestamp = j2;
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static final class AttentionCheck {
        public final AttentionManagerInternal.AttentionCallbackInternal mCallbackInternal;
        public final IAttentionCallback mIAttentionCallback;
        public boolean mIsDispatched;
        public boolean mIsFulfilled;

        public AttentionCheck(final AttentionManagerInternal.AttentionCallbackInternal attentionCallbackInternal, final AttentionManagerService attentionManagerService) {
            this.mCallbackInternal = attentionCallbackInternal;
            this.mIAttentionCallback = new IAttentionCallback.Stub() { // from class: com.android.server.attention.AttentionManagerService.AttentionCheck.1
                public void onSuccess(int i, long j) {
                    if (AttentionCheck.this.mIsFulfilled) {
                        return;
                    }
                    AttentionCheck.this.mIsFulfilled = true;
                    attentionCallbackInternal.onSuccess(i, j);
                    logStats(i);
                    attentionManagerService.appendResultToAttentionCacheBuffer(new AttentionCheckCache(SystemClock.uptimeMillis(), i, j));
                }

                public void onFailure(int i) {
                    if (AttentionCheck.this.mIsFulfilled) {
                        return;
                    }
                    AttentionCheck.this.mIsFulfilled = true;
                    attentionCallbackInternal.onFailure(i);
                    logStats(i);
                }

                public final void logStats(int i) {
                    FrameworkStatsLog.write(143, i);
                }
            };
        }

        public void cancelInternal() {
            this.mIsFulfilled = true;
            this.mCallbackInternal.onFailure(3);
        }

        public void dump(IndentingPrintWriter indentingPrintWriter) {
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("is dispatched=" + this.mIsDispatched);
            indentingPrintWriter.println("is fulfilled:=" + this.mIsFulfilled);
            indentingPrintWriter.decreaseIndent();
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public final class ProximityUpdate {
        public final AttentionManagerInternal.ProximityUpdateCallbackInternal mCallbackInternal;
        public final IProximityUpdateCallback mIProximityUpdateCallback;
        public boolean mStartedUpdates;

        public ProximityUpdate(AttentionManagerInternal.ProximityUpdateCallbackInternal proximityUpdateCallbackInternal) {
            this.mCallbackInternal = proximityUpdateCallbackInternal;
            this.mIProximityUpdateCallback = new IProximityUpdateCallback.Stub() { // from class: com.android.server.attention.AttentionManagerService.ProximityUpdate.1
                public void onProximityUpdate(double d) {
                    ProximityUpdate.this.mCallbackInternal.onProximityUpdate(d);
                    synchronized (AttentionManagerService.this.mLock) {
                        AttentionManagerService.this.freeIfInactiveLocked();
                    }
                }
            };
        }

        public boolean startUpdates() {
            synchronized (AttentionManagerService.this.mLock) {
                if (this.mStartedUpdates) {
                    Slog.w("AttentionManagerService", "Already registered to a proximity service.");
                    return false;
                }
                IAttentionService iAttentionService = AttentionManagerService.this.mService;
                if (iAttentionService == null) {
                    Slog.w("AttentionManagerService", "There is no service bound. Proximity update request rejected.");
                    return false;
                }
                try {
                    iAttentionService.onStartProximityUpdates(this.mIProximityUpdateCallback);
                    this.mStartedUpdates = true;
                    return true;
                } catch (RemoteException e) {
                    Slog.e("AttentionManagerService", "Cannot call into the AttentionService", e);
                    return false;
                }
            }
        }

        public void cancelUpdates() {
            synchronized (AttentionManagerService.this.mLock) {
                if (this.mStartedUpdates) {
                    IAttentionService iAttentionService = AttentionManagerService.this.mService;
                    if (iAttentionService == null) {
                        this.mStartedUpdates = false;
                        return;
                    }
                    try {
                        iAttentionService.onStopProximityUpdates();
                        this.mStartedUpdates = false;
                    } catch (RemoteException e) {
                        Slog.e("AttentionManagerService", "Cannot call into the AttentionService", e);
                    }
                }
            }
        }

        public void dump(IndentingPrintWriter indentingPrintWriter) {
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("is StartedUpdates=" + this.mStartedUpdates);
            indentingPrintWriter.decreaseIndent();
        }
    }

    public final void appendResultToAttentionCacheBuffer(AttentionCheckCache attentionCheckCache) {
        synchronized (this.mLock) {
            if (this.mAttentionCheckCacheBuffer == null) {
                this.mAttentionCheckCacheBuffer = new AttentionCheckCacheBuffer();
            }
            this.mAttentionCheckCacheBuffer.add(attentionCheckCache);
        }
    }

    /* loaded from: classes.dex */
    public class AttentionServiceConnection implements ServiceConnection {
        public AttentionServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            init(IAttentionService.Stub.asInterface(iBinder));
            AttentionManagerService.this.mServiceBindingLatch.countDown();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            cleanupService();
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            cleanupService();
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            cleanupService();
        }

        public void cleanupService() {
            init(null);
            AttentionManagerService.this.mServiceBindingLatch = new CountDownLatch(1);
        }

        public final void init(IAttentionService iAttentionService) {
            synchronized (AttentionManagerService.this.mLock) {
                AttentionManagerService attentionManagerService = AttentionManagerService.this;
                attentionManagerService.mService = iAttentionService;
                attentionManagerService.mBinding = false;
                AttentionManagerService.this.handlePendingCallbackLocked();
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void handlePendingCallbackLocked() {
        AttentionCheck attentionCheck = this.mCurrentAttentionCheck;
        if (attentionCheck != null && !attentionCheck.mIsDispatched) {
            IAttentionService iAttentionService = this.mService;
            if (iAttentionService != null) {
                try {
                    iAttentionService.checkAttention(this.mCurrentAttentionCheck.mIAttentionCallback);
                    this.mCurrentAttentionCheck.mIsDispatched = true;
                } catch (RemoteException unused) {
                    Slog.e("AttentionManagerService", "Cannot call into the AttentionService");
                }
            } else {
                this.mCurrentAttentionCheck.mCallbackInternal.onFailure(2);
            }
        }
        ProximityUpdate proximityUpdate = this.mCurrentProximityUpdate;
        if (proximityUpdate == null || !proximityUpdate.mStartedUpdates) {
            return;
        }
        IAttentionService iAttentionService2 = this.mService;
        if (iAttentionService2 != null) {
            try {
                iAttentionService2.onStartProximityUpdates(this.mCurrentProximityUpdate.mIProximityUpdateCallback);
                return;
            } catch (RemoteException e) {
                Slog.e("AttentionManagerService", "Cannot call into the AttentionService", e);
                return;
            }
        }
        this.mCurrentProximityUpdate.cancelUpdates();
        this.mCurrentProximityUpdate = null;
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class AttentionHandler extends Handler {
        public AttentionHandler() {
            super(Looper.myLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                synchronized (AttentionManagerService.this.mLock) {
                    AttentionManagerService.this.cancelAndUnbindLocked();
                }
            } else if (i != 2) {
            } else {
                synchronized (AttentionManagerService.this.mLock) {
                    AttentionManagerService.this.cancel();
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public void cancel() {
        if (this.mCurrentAttentionCheck.mIsFulfilled) {
            return;
        }
        IAttentionService iAttentionService = this.mService;
        if (iAttentionService == null) {
            this.mCurrentAttentionCheck.cancelInternal();
            return;
        }
        try {
            iAttentionService.cancelAttentionCheck(this.mCurrentAttentionCheck.mIAttentionCallback);
        } catch (RemoteException unused) {
            Slog.e("AttentionManagerService", "Unable to cancel attention check");
            this.mCurrentAttentionCheck.cancelInternal();
        }
    }

    @GuardedBy({"mLock"})
    public final void cancelAndUnbindLocked() {
        synchronized (this.mLock) {
            if (this.mCurrentAttentionCheck != null) {
                cancel();
            }
            ProximityUpdate proximityUpdate = this.mCurrentProximityUpdate;
            if (proximityUpdate != null) {
                proximityUpdate.cancelUpdates();
            }
            if (this.mService == null) {
                return;
            }
            this.mAttentionHandler.post(new Runnable() { // from class: com.android.server.attention.AttentionManagerService$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AttentionManagerService.this.lambda$cancelAndUnbindLocked$1();
                }
            });
            this.mConnection.cleanupService();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelAndUnbindLocked$1() {
        this.mContext.unbindService(this.mConnection);
    }

    @GuardedBy({"mLock"})
    public final void bindLocked() {
        if (this.mBinding || this.mService != null) {
            return;
        }
        this.mBinding = true;
        this.mAttentionHandler.post(new Runnable() { // from class: com.android.server.attention.AttentionManagerService$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                AttentionManagerService.this.lambda$bindLocked$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$bindLocked$2() {
        this.mContext.bindServiceAsUser(new Intent("android.service.attention.AttentionService").setComponent(this.mComponentName), this.mConnection, 67112961, UserHandle.CURRENT);
    }

    /* loaded from: classes.dex */
    public final class ScreenStateReceiver extends BroadcastReceiver {
        public ScreenStateReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                synchronized (AttentionManagerService.this.mLock) {
                    AttentionManagerService.this.cancelAndUnbindLocked();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public final class AttentionManagerServiceShellCommand extends ShellCommand {
        public final TestableAttentionCallbackInternal mTestableAttentionCallback;
        public final TestableProximityUpdateCallbackInternal mTestableProximityUpdateCallback;

        /* loaded from: classes.dex */
        public class TestableAttentionCallbackInternal extends AttentionManagerInternal.AttentionCallbackInternal {
            public int mLastCallbackCode = -1;

            public TestableAttentionCallbackInternal() {
            }

            public void onSuccess(int i, long j) {
                this.mLastCallbackCode = i;
            }

            public void onFailure(int i) {
                this.mLastCallbackCode = i;
            }

            public void reset() {
                this.mLastCallbackCode = -1;
            }

            public int getLastCallbackCode() {
                return this.mLastCallbackCode;
            }
        }

        public AttentionManagerServiceShellCommand() {
            this.mTestableAttentionCallback = new TestableAttentionCallbackInternal();
            this.mTestableProximityUpdateCallback = new TestableProximityUpdateCallbackInternal();
        }

        /* loaded from: classes.dex */
        public class TestableProximityUpdateCallbackInternal implements AttentionManagerInternal.ProximityUpdateCallbackInternal {
            public double mLastCallbackCode = -1.0d;

            public TestableProximityUpdateCallbackInternal() {
            }

            public void onProximityUpdate(double d) {
                this.mLastCallbackCode = d;
            }

            public void reset() {
                this.mLastCallbackCode = -1.0d;
            }

            public double getLastCallbackCode() {
                return this.mLastCallbackCode;
            }
        }

        public int onCommand(String str) {
            boolean z;
            if (str == null) {
                return handleDefaultCommands(str);
            }
            PrintWriter errPrintWriter = getErrPrintWriter();
            try {
                boolean z2 = false;
                switch (str.hashCode()) {
                    case -1208709968:
                        if (str.equals("getLastTestCallbackCode")) {
                            z = true;
                            break;
                        }
                        z = true;
                        break;
                    case -1002424240:
                        if (str.equals("getAttentionServiceComponent")) {
                            z = false;
                            break;
                        }
                        z = true;
                        break;
                    case -415045819:
                        if (str.equals("setTestableAttentionService")) {
                            z = true;
                            break;
                        }
                        z = true;
                        break;
                    case 3045982:
                        if (str.equals("call")) {
                            z = true;
                            break;
                        }
                        z = true;
                        break;
                    case 1048378748:
                        if (str.equals("getLastTestProximityUpdateCallbackCode")) {
                            z = true;
                            break;
                        }
                        z = true;
                        break;
                    case 1193447472:
                        if (str.equals("clearTestableAttentionService")) {
                            z = true;
                            break;
                        }
                        z = true;
                        break;
                    default:
                        z = true;
                        break;
                }
                if (z) {
                    if (!z) {
                        if (!z) {
                            if (!z) {
                                if (!z) {
                                    if (z) {
                                        return cmdGetLastTestProximityUpdateCallbackCode();
                                    }
                                    return handleDefaultCommands(str);
                                }
                                return cmdGetLastTestCallbackCode();
                            }
                            return cmdClearTestableAttentionService();
                        }
                        return cmdSetTestableAttentionService(getNextArgRequired());
                    }
                    String nextArgRequired = getNextArgRequired();
                    switch (nextArgRequired.hashCode()) {
                        case -1571871954:
                            if (nextArgRequired.equals("onStartProximityUpdates")) {
                                z2 = true;
                                break;
                            }
                            z2 = true;
                            break;
                        case 685821932:
                            if (nextArgRequired.equals("onStopProximityUpdates")) {
                                z2 = true;
                                break;
                            }
                            z2 = true;
                            break;
                        case 763077136:
                            if (nextArgRequired.equals("cancelCheckAttention")) {
                                z2 = true;
                                break;
                            }
                            z2 = true;
                            break;
                        case 1485997302:
                            if (nextArgRequired.equals("checkAttention")) {
                                break;
                            }
                            z2 = true;
                            break;
                        default:
                            z2 = true;
                            break;
                    }
                    if (z2) {
                        if (!z2) {
                            if (!z2) {
                                if (z2) {
                                    return cmdCallOnStopProximityUpdates();
                                }
                                throw new IllegalArgumentException("Invalid argument");
                            }
                            return cmdCallOnStartProximityUpdates();
                        }
                        return cmdCallCancelAttention();
                    }
                    return cmdCallCheckAttention();
                }
                return cmdResolveAttentionServiceComponent();
            } catch (IllegalArgumentException e) {
                errPrintWriter.println("Error: " + e.getMessage());
                return -1;
            }
        }

        public final int cmdSetTestableAttentionService(String str) {
            PrintWriter outPrintWriter = getOutPrintWriter();
            if (TextUtils.isEmpty(str)) {
                outPrintWriter.println("false");
                return 0;
            }
            AttentionManagerService.sTestAttentionServicePackage = str;
            resetStates();
            outPrintWriter.println(AttentionManagerService.this.mComponentName != null ? "true" : "false");
            return 0;
        }

        public final int cmdClearTestableAttentionService() {
            AttentionManagerService.sTestAttentionServicePackage = "";
            this.mTestableAttentionCallback.reset();
            this.mTestableProximityUpdateCallback.reset();
            resetStates();
            return 0;
        }

        public final int cmdCallCheckAttention() {
            getOutPrintWriter().println(AttentionManagerService.this.checkAttention(2000L, this.mTestableAttentionCallback) ? "true" : "false");
            return 0;
        }

        public final int cmdCallCancelAttention() {
            PrintWriter outPrintWriter = getOutPrintWriter();
            AttentionManagerService.this.cancelAttentionCheck(this.mTestableAttentionCallback);
            outPrintWriter.println("true");
            return 0;
        }

        public final int cmdCallOnStartProximityUpdates() {
            getOutPrintWriter().println(AttentionManagerService.this.onStartProximityUpdates(this.mTestableProximityUpdateCallback) ? "true" : "false");
            return 0;
        }

        public final int cmdCallOnStopProximityUpdates() {
            PrintWriter outPrintWriter = getOutPrintWriter();
            AttentionManagerService.this.onStopProximityUpdates(this.mTestableProximityUpdateCallback);
            outPrintWriter.println("true");
            return 0;
        }

        public final int cmdResolveAttentionServiceComponent() {
            PrintWriter outPrintWriter = getOutPrintWriter();
            ComponentName resolveAttentionService = AttentionManagerService.resolveAttentionService(AttentionManagerService.this.mContext);
            outPrintWriter.println(resolveAttentionService != null ? resolveAttentionService.flattenToShortString() : "");
            return 0;
        }

        public final int cmdGetLastTestCallbackCode() {
            getOutPrintWriter().println(this.mTestableAttentionCallback.getLastCallbackCode());
            return 0;
        }

        public final int cmdGetLastTestProximityUpdateCallbackCode() {
            getOutPrintWriter().println(this.mTestableProximityUpdateCallback.getLastCallbackCode());
            return 0;
        }

        public final void resetStates() {
            synchronized (AttentionManagerService.this.mLock) {
                AttentionManagerService attentionManagerService = AttentionManagerService.this;
                attentionManagerService.mCurrentProximityUpdate = null;
                attentionManagerService.cancelAndUnbindLocked();
            }
            AttentionManagerService attentionManagerService2 = AttentionManagerService.this;
            attentionManagerService2.mComponentName = AttentionManagerService.resolveAttentionService(attentionManagerService2.mContext);
        }

        public void onHelp() {
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("Attention commands: ");
            outPrintWriter.println("  setTestableAttentionService <service_package>: Bind to a custom implementation of attention service");
            outPrintWriter.println("  ---<service_package>:");
            outPrintWriter.println("       := Package containing the Attention Service implementation to bind to");
            outPrintWriter.println("  ---returns:");
            outPrintWriter.println("       := true, if was bound successfully");
            outPrintWriter.println("       := false, if was not bound successfully");
            outPrintWriter.println("  clearTestableAttentionService: Undo custom bindings. Revert to previous behavior");
            outPrintWriter.println("  getAttentionServiceComponent: Get the current service component string");
            outPrintWriter.println("  ---returns:");
            outPrintWriter.println("       := If valid, the component string (in shorten form) for the currently bound service.");
            outPrintWriter.println("       := else, empty string");
            outPrintWriter.println("  call checkAttention: Calls check attention");
            outPrintWriter.println("  ---returns:");
            outPrintWriter.println("       := true, if the call was successfully dispatched to the service implementation. (to see the result, call getLastTestCallbackCode)");
            outPrintWriter.println("       := false, otherwise");
            outPrintWriter.println("  call cancelCheckAttention: Cancels check attention");
            outPrintWriter.println("  call onStartProximityUpdates: Calls onStartProximityUpdates");
            outPrintWriter.println("  ---returns:");
            outPrintWriter.println("       := true, if the request was successfully dispatched to the service implementation. (to see the result, call getLastTestProximityUpdateCallbackCode)");
            outPrintWriter.println("       := false, otherwise");
            outPrintWriter.println("  call onStopProximityUpdates: Cancels proximity updates");
            outPrintWriter.println("  getLastTestCallbackCode");
            outPrintWriter.println("  ---returns:");
            outPrintWriter.println("       := An integer, representing the last callback code received from the bounded implementation. If none, it will return -1");
            outPrintWriter.println("  getLastTestProximityUpdateCallbackCode");
            outPrintWriter.println("  ---returns:");
            outPrintWriter.println("       := A double, representing the last proximity value received from the bounded implementation. If none, it will return -1.0");
        }
    }

    /* loaded from: classes.dex */
    public final class BinderService extends Binder {
        public AttentionManagerServiceShellCommand mAttentionManagerServiceShellCommand;

        public BinderService() {
            this.mAttentionManagerServiceShellCommand = new AttentionManagerServiceShellCommand();
        }

        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
            this.mAttentionManagerServiceShellCommand.exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }

        @Override // android.os.Binder
        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(AttentionManagerService.this.mContext, "AttentionManagerService", printWriter)) {
                AttentionManagerService.this.dumpInternal(new IndentingPrintWriter(printWriter, "  "));
            }
        }
    }
}
