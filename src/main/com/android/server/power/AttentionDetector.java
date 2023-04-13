package com.android.server.power;

import android.app.ActivityManager;
import android.app.SynchronousUserSwitchObserver;
import android.attention.AttentionManagerInternal;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.LocalServices;
import com.android.server.clipboard.ClipboardService;
import com.android.server.p014wm.WindowManagerInternal;
import java.io.PrintWriter;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes2.dex */
public class AttentionDetector {
    @VisibleForTesting
    protected AttentionManagerInternal mAttentionManager;
    @VisibleForTesting
    AttentionCallbackInternalImpl mCallback;
    @VisibleForTesting
    protected ContentResolver mContentResolver;
    public Context mContext;
    @VisibleForTesting
    protected long mDefaultMaximumExtensionMillis;
    public long mEffectivePostDimTimeoutMillis;
    public boolean mIsSettingEnabled;
    public long mLastActedOnNextScreenDimming;
    public long mLastUserActivityTime;
    public final Object mLock;
    public long mMaximumExtensionMillis;
    public final Runnable mOnUserAttention;
    @VisibleForTesting
    protected long mPreDimCheckDurationMillis;
    public long mRequestedPostDimTimeoutMillis;
    @VisibleForTesting
    protected WindowManagerInternal mWindowManager;
    public AtomicLong mConsecutiveTimeoutExtendedCount = new AtomicLong(0);
    public final AtomicBoolean mRequested = new AtomicBoolean(false);
    @VisibleForTesting
    protected int mRequestId = 0;
    public int mWakefulness = 1;

    public AttentionDetector(Runnable runnable, Object obj) {
        this.mOnUserAttention = runnable;
        this.mLock = obj;
    }

    @VisibleForTesting
    public void updateEnabledFromSettings(Context context) {
        this.mIsSettingEnabled = Settings.Secure.getIntForUser(context.getContentResolver(), "adaptive_sleep", 0, -2) == 1;
    }

    public void systemReady(final Context context) {
        this.mContext = context;
        updateEnabledFromSettings(context);
        this.mContentResolver = context.getContentResolver();
        this.mAttentionManager = (AttentionManagerInternal) LocalServices.getService(AttentionManagerInternal.class);
        this.mWindowManager = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
        this.mDefaultMaximumExtensionMillis = context.getResources().getInteger(17694737);
        try {
            ActivityManager.getService().registerUserSwitchObserver(new UserSwitchObserver(), "AttentionDetector");
        } catch (RemoteException unused) {
        }
        context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("adaptive_sleep"), false, new ContentObserver(new Handler(context.getMainLooper())) { // from class: com.android.server.power.AttentionDetector.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                AttentionDetector.this.updateEnabledFromSettings(context);
            }
        }, -1);
        readValuesFromDeviceConfig();
        DeviceConfig.addOnPropertiesChangedListener("attention_manager_service", context.getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.power.AttentionDetector$$ExternalSyntheticLambda0
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                AttentionDetector.this.lambda$systemReady$0(properties);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$systemReady$0(DeviceConfig.Properties properties) {
        onDeviceConfigChange(properties.getKeyset());
    }

    public long updateUserActivity(long j, long j2) {
        if (j == this.mLastActedOnNextScreenDimming || !this.mIsSettingEnabled || !isAttentionServiceSupported() || this.mWindowManager.isKeyguardShowingAndNotOccluded()) {
            return j;
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        long j3 = j - this.mPreDimCheckDurationMillis;
        long j4 = this.mLastUserActivityTime + this.mMaximumExtensionMillis;
        if (uptimeMillis < j3) {
            return j3;
        }
        if (j4 < j3) {
            return j;
        }
        if (this.mRequested.get()) {
            return j3;
        }
        this.mRequested.set(true);
        this.mRequestId++;
        this.mLastActedOnNextScreenDimming = j;
        this.mCallback = new AttentionCallbackInternalImpl(this.mRequestId);
        this.mEffectivePostDimTimeoutMillis = Math.min(this.mRequestedPostDimTimeoutMillis, j2);
        Slog.v("AttentionDetector", "Checking user attention, ID: " + this.mRequestId);
        if (!this.mAttentionManager.checkAttention(this.mPreDimCheckDurationMillis + this.mEffectivePostDimTimeoutMillis, this.mCallback)) {
            this.mRequested.set(false);
        }
        return j3;
    }

    public int onUserActivity(long j, int i) {
        if (i == 0 || i == 1 || i == 2 || i == 3) {
            cancelCurrentRequestIfAny();
            this.mLastUserActivityTime = j;
            resetConsecutiveExtensionCount();
            return 1;
        } else if (i != 4) {
            return -1;
        } else {
            this.mConsecutiveTimeoutExtendedCount.incrementAndGet();
            return 0;
        }
    }

    public void onWakefulnessChangeStarted(int i) {
        this.mWakefulness = i;
        if (i != 1) {
            cancelCurrentRequestIfAny();
            resetConsecutiveExtensionCount();
        }
    }

    public final void cancelCurrentRequestIfAny() {
        if (this.mRequested.get()) {
            this.mAttentionManager.cancelAttentionCheck(this.mCallback);
            this.mRequested.set(false);
        }
    }

    public final void resetConsecutiveExtensionCount() {
        long andSet = this.mConsecutiveTimeoutExtendedCount.getAndSet(0L);
        if (andSet > 0) {
            FrameworkStatsLog.write(168, andSet);
        }
    }

    @VisibleForTesting
    public boolean isAttentionServiceSupported() {
        AttentionManagerInternal attentionManagerInternal = this.mAttentionManager;
        return attentionManagerInternal != null && attentionManagerInternal.isAttentionServiceSupported();
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("AttentionDetector:");
        printWriter.println(" mIsSettingEnabled=" + this.mIsSettingEnabled);
        printWriter.println(" mMaxExtensionMillis=" + this.mMaximumExtensionMillis);
        printWriter.println(" mPreDimCheckDurationMillis=" + this.mPreDimCheckDurationMillis);
        printWriter.println(" mEffectivePostDimTimeout=" + this.mEffectivePostDimTimeoutMillis);
        printWriter.println(" mLastUserActivityTime(excludingAttention)=" + this.mLastUserActivityTime);
        printWriter.println(" mAttentionServiceSupported=" + isAttentionServiceSupported());
        printWriter.println(" mRequested=" + this.mRequested);
    }

    @VisibleForTesting
    public long getPreDimCheckDurationMillis() {
        long j = DeviceConfig.getLong("attention_manager_service", "pre_dim_check_duration_millis", 2000L);
        if (j < 0 || j > 13000) {
            Slog.w("AttentionDetector", "Bad flag value supplied for: pre_dim_check_duration_millis");
            return 2000L;
        }
        return j;
    }

    @VisibleForTesting
    public long getPostDimCheckDurationMillis() {
        long j = DeviceConfig.getLong("attention_manager_service", "post_dim_check_duration_millis", 0L);
        if (j < 0 || j > 10000) {
            Slog.w("AttentionDetector", "Bad flag value supplied for: post_dim_check_duration_millis");
            return 0L;
        }
        return j;
    }

    @VisibleForTesting
    public long getMaxExtensionMillis() {
        long j = DeviceConfig.getLong("attention_manager_service", "max_extension_millis", this.mDefaultMaximumExtensionMillis);
        if (j < 0 || j > ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS) {
            Slog.w("AttentionDetector", "Bad flag value supplied for: max_extension_millis");
            return this.mDefaultMaximumExtensionMillis;
        }
        return j;
    }

    public final void onDeviceConfigChange(Set<String> set) {
        for (String str : set) {
            str.hashCode();
            char c = 65535;
            switch (str.hashCode()) {
                case -2018189628:
                    if (str.equals("post_dim_check_duration_millis")) {
                        c = 0;
                        break;
                    }
                    break;
                case -511526975:
                    if (str.equals("max_extension_millis")) {
                        c = 1;
                        break;
                    }
                    break;
                case 417901319:
                    if (str.equals("pre_dim_check_duration_millis")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                case 2:
                    readValuesFromDeviceConfig();
                    return;
                default:
                    Slog.i("AttentionDetector", "Ignoring change on " + str);
            }
        }
    }

    public final void readValuesFromDeviceConfig() {
        this.mMaximumExtensionMillis = getMaxExtensionMillis();
        this.mPreDimCheckDurationMillis = getPreDimCheckDurationMillis();
        this.mRequestedPostDimTimeoutMillis = getPostDimCheckDurationMillis();
        Slog.i("AttentionDetector", "readValuesFromDeviceConfig():\nmMaximumExtensionMillis=" + this.mMaximumExtensionMillis + "\nmPreDimCheckDurationMillis=" + this.mPreDimCheckDurationMillis + "\nmRequestedPostDimTimeoutMillis=" + this.mRequestedPostDimTimeoutMillis);
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public final class AttentionCallbackInternalImpl extends AttentionManagerInternal.AttentionCallbackInternal {
        public final int mId;

        public AttentionCallbackInternalImpl(int i) {
            this.mId = i;
        }

        public void onSuccess(int i, long j) {
            Slog.v("AttentionDetector", "onSuccess: " + i + ", ID: " + this.mId);
            int i2 = this.mId;
            AttentionDetector attentionDetector = AttentionDetector.this;
            if (i2 == attentionDetector.mRequestId && attentionDetector.mRequested.getAndSet(false)) {
                synchronized (AttentionDetector.this.mLock) {
                    if (AttentionDetector.this.mWakefulness != 1) {
                        return;
                    }
                    if (i == 1) {
                        AttentionDetector.this.mOnUserAttention.run();
                    } else {
                        AttentionDetector.this.resetConsecutiveExtensionCount();
                    }
                }
            }
        }

        public void onFailure(int i) {
            Slog.i("AttentionDetector", "Failed to check attention: " + i + ", ID: " + this.mId);
            AttentionDetector.this.mRequested.set(false);
        }
    }

    /* loaded from: classes2.dex */
    public final class UserSwitchObserver extends SynchronousUserSwitchObserver {
        public UserSwitchObserver() {
        }

        public void onUserSwitching(int i) throws RemoteException {
            AttentionDetector attentionDetector = AttentionDetector.this;
            attentionDetector.updateEnabledFromSettings(attentionDetector.mContext);
        }
    }
}
