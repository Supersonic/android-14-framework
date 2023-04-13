package com.android.server.trust;

import android.annotation.EnforcePermission;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.admin.DevicePolicyManager;
import android.app.trust.ITrustListener;
import android.app.trust.ITrustManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricSourceType;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.security.Authorization;
import android.service.trust.GrantTrustResult;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.Xml;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.R;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.util.DumpUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.companion.virtual.VirtualDeviceManagerInternal;
import com.android.server.trust.TrustManagerService;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class TrustManagerService extends SystemService {
    public static final boolean DEBUG;
    public static final boolean ENABLE_ACTIVE_UNLOCK_FLAG;
    public static final Intent TRUST_AGENT_INTENT;
    public final ArraySet<AgentInfo> mActiveAgents;
    public final ActivityManager mActivityManager;
    public final Object mAlarmLock;
    public AlarmManager mAlarmManager;
    public final TrustArchive mArchive;
    public final Context mContext;
    public int mCurrentUser;
    @GuardedBy({"mDeviceLockedForUser"})
    public final SparseBooleanArray mDeviceLockedForUser;
    public final Handler mHandler;
    public final SparseArray<TrustableTimeoutAlarmListener> mIdleTrustableTimeoutAlarmListenerForUser;
    public final LockPatternUtils mLockPatternUtils;
    public final PackageMonitor mPackageMonitor;
    public final Receiver mReceiver;
    public final IBinder mService;
    public final SettingsObserver mSettingsObserver;
    public final StrongAuthTracker mStrongAuthTracker;
    public boolean mTrustAgentsCanRun;
    public final ArrayList<ITrustListener> mTrustListeners;
    public final ArrayMap<Integer, TrustedTimeoutAlarmListener> mTrustTimeoutAlarmListenerForUser;
    @GuardedBy({"mTrustUsuallyManagedForUser"})
    public final SparseBooleanArray mTrustUsuallyManagedForUser;
    public final SparseArray<TrustableTimeoutAlarmListener> mTrustableTimeoutAlarmListenerForUser;
    @GuardedBy({"mUserIsTrusted"})
    public final SparseBooleanArray mUserIsTrusted;
    public final UserManager mUserManager;
    @GuardedBy({"mUserTrustState"})
    public final SparseArray<TrustState> mUserTrustState;
    @GuardedBy({"mUsersUnlockedByBiometric"})
    public final SparseBooleanArray mUsersUnlockedByBiometric;
    public VirtualDeviceManagerInternal mVirtualDeviceManager;

    /* loaded from: classes2.dex */
    public enum TimeoutType {
        TRUSTED,
        TRUSTABLE
    }

    /* loaded from: classes2.dex */
    public enum TrustState {
        UNTRUSTED,
        TRUSTABLE,
        TRUSTED
    }

    static {
        DEBUG = Build.IS_DEBUGGABLE && Log.isLoggable("TrustManagerService", 2);
        TRUST_AGENT_INTENT = new Intent("android.service.trust.TrustAgentService");
        ENABLE_ACTIVE_UNLOCK_FLAG = SystemProperties.getBoolean("fw.enable_active_unlock_flag", true);
    }

    public TrustManagerService(Context context) {
        super(context);
        this.mActiveAgents = new ArraySet<>();
        this.mTrustListeners = new ArrayList<>();
        this.mReceiver = new Receiver();
        this.mArchive = new TrustArchive();
        this.mUserIsTrusted = new SparseBooleanArray();
        this.mUserTrustState = new SparseArray<>();
        this.mDeviceLockedForUser = new SparseBooleanArray();
        this.mTrustUsuallyManagedForUser = new SparseBooleanArray();
        this.mUsersUnlockedByBiometric = new SparseBooleanArray();
        this.mTrustTimeoutAlarmListenerForUser = new ArrayMap<>();
        this.mTrustableTimeoutAlarmListenerForUser = new SparseArray<>();
        this.mIdleTrustableTimeoutAlarmListenerForUser = new SparseArray<>();
        this.mAlarmLock = new Object();
        this.mTrustAgentsCanRun = false;
        this.mCurrentUser = 0;
        this.mService = new trustITrustManager$StubC17001();
        Handler handler = new Handler() { // from class: com.android.server.trust.TrustManagerService.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                SparseBooleanArray clone;
                switch (message.what) {
                    case 1:
                        TrustManagerService.this.addListener((ITrustListener) message.obj);
                        return;
                    case 2:
                        TrustManagerService.this.removeListener((ITrustListener) message.obj);
                        return;
                    case 3:
                        TrustManagerService.this.dispatchUnlockAttempt(message.arg1 != 0, message.arg2);
                        return;
                    case 4:
                        TrustManagerService.this.refreshAgentList(-1);
                        TrustManagerService.this.refreshDeviceLockedForUser(-1);
                        return;
                    case 5:
                    default:
                        return;
                    case 6:
                        TrustManagerService.this.dispatchTrustableDowngrade();
                        TrustManagerService trustManagerService = TrustManagerService.this;
                        trustManagerService.refreshDeviceLockedForUser(trustManagerService.mCurrentUser);
                        return;
                    case 7:
                    case 8:
                    case 11:
                        TrustManagerService.this.refreshAgentList(message.arg1);
                        return;
                    case 9:
                        TrustManagerService.this.mCurrentUser = message.arg1;
                        TrustManagerService.this.mSettingsObserver.updateContentObserver();
                        TrustManagerService.this.refreshDeviceLockedForUser(-1);
                        return;
                    case 10:
                        synchronized (TrustManagerService.this.mTrustUsuallyManagedForUser) {
                            clone = TrustManagerService.this.mTrustUsuallyManagedForUser.clone();
                        }
                        for (int i = 0; i < clone.size(); i++) {
                            int keyAt = clone.keyAt(i);
                            boolean valueAt = clone.valueAt(i);
                            if (valueAt != TrustManagerService.this.mLockPatternUtils.isTrustUsuallyManaged(keyAt)) {
                                TrustManagerService.this.mLockPatternUtils.setTrustUsuallyManaged(valueAt, keyAt);
                            }
                        }
                        return;
                    case 12:
                        TrustManagerService.this.setDeviceLockedForUser(message.arg1, true);
                        return;
                    case 13:
                        TrustManagerService.this.dispatchUnlockLockout(message.arg1, message.arg2);
                        return;
                    case 14:
                        if (message.arg2 == 1) {
                            TrustManagerService.this.updateTrust(message.arg1, 0, true, null);
                        }
                        TrustManagerService.this.refreshDeviceLockedForUser(message.arg1, message.getData().getInt("except", -10000));
                        return;
                    case 15:
                        TrustManagerService.this.handleScheduleTrustTimeout(message.arg1 == 1, message.arg2 == 1 ? TimeoutType.TRUSTABLE : TimeoutType.TRUSTED);
                        return;
                    case 16:
                        TrustManagerService.this.dispatchUserRequestedUnlock(message.arg1, message.arg2 != 0);
                        return;
                    case 17:
                        TrustManagerService.this.refreshTrustableTimers(message.arg1);
                        return;
                    case 18:
                        TrustManagerService.this.dispatchUserMayRequestUnlock(message.arg1);
                        return;
                }
            }
        };
        this.mHandler = handler;
        this.mPackageMonitor = new PackageMonitor() { // from class: com.android.server.trust.TrustManagerService.3
            public void onSomePackagesChanged() {
                TrustManagerService.this.refreshAgentList(-1);
            }

            public void onPackageAdded(String str, int i) {
                TrustManagerService.this.checkNewAgentsForUser(UserHandle.getUserId(i));
            }

            public boolean onPackageChanged(String str, int i, String[] strArr) {
                TrustManagerService.this.checkNewAgentsForUser(UserHandle.getUserId(i));
                return true;
            }

            public void onPackageDisappeared(String str, int i) {
                TrustManagerService.this.removeAgentsOfPackage(str);
            }
        };
        this.mContext = context;
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mActivityManager = (ActivityManager) context.getSystemService("activity");
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mStrongAuthTracker = new StrongAuthTracker(context);
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
        this.mSettingsObserver = new SettingsObserver(handler);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("trust", this.mService);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (isSafeMode()) {
            return;
        }
        if (i == 500) {
            checkNewAgents();
            this.mPackageMonitor.register(this.mContext, this.mHandler.getLooper(), UserHandle.ALL, true);
            this.mReceiver.register(this.mContext);
            this.mLockPatternUtils.registerStrongAuthTracker(this.mStrongAuthTracker);
        } else if (i == 600) {
            this.mTrustAgentsCanRun = true;
            refreshAgentList(-1);
            refreshDeviceLockedForUser(-1);
        } else if (i == 1000) {
            maybeEnableFactoryTrustAgents(0);
        }
    }

    /* loaded from: classes2.dex */
    public final class SettingsObserver extends ContentObserver {
        public final Uri LOCK_SCREEN_WHEN_TRUST_LOST;
        public final Uri TRUST_AGENTS_EXTEND_UNLOCK;
        public final ContentResolver mContentResolver;
        public final boolean mIsAutomotive;
        public boolean mLockWhenTrustLost;
        public boolean mTrustAgentsNonrenewableTrust;

        public SettingsObserver(Handler handler) {
            super(handler);
            this.TRUST_AGENTS_EXTEND_UNLOCK = Settings.Secure.getUriFor("trust_agents_extend_unlock");
            this.LOCK_SCREEN_WHEN_TRUST_LOST = Settings.Secure.getUriFor("lock_screen_when_trust_lost");
            this.mIsAutomotive = TrustManagerService.this.getContext().getPackageManager().hasSystemFeature("android.hardware.type.automotive");
            this.mContentResolver = TrustManagerService.this.getContext().getContentResolver();
            updateContentObserver();
        }

        public void updateContentObserver() {
            this.mContentResolver.unregisterContentObserver(this);
            this.mContentResolver.registerContentObserver(this.TRUST_AGENTS_EXTEND_UNLOCK, false, this, TrustManagerService.this.mCurrentUser);
            this.mContentResolver.registerContentObserver(this.LOCK_SCREEN_WHEN_TRUST_LOST, false, this, TrustManagerService.this.mCurrentUser);
            onChange(true, this.TRUST_AGENTS_EXTEND_UNLOCK);
            onChange(true, this.LOCK_SCREEN_WHEN_TRUST_LOST);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (this.TRUST_AGENTS_EXTEND_UNLOCK.equals(uri)) {
                this.mTrustAgentsNonrenewableTrust = Settings.Secure.getIntForUser(this.mContentResolver, "trust_agents_extend_unlock", !this.mIsAutomotive ? 1 : 0, TrustManagerService.this.mCurrentUser) != 0;
            } else if (this.LOCK_SCREEN_WHEN_TRUST_LOST.equals(uri)) {
                this.mLockWhenTrustLost = Settings.Secure.getIntForUser(this.mContentResolver, "lock_screen_when_trust_lost", 0, TrustManagerService.this.mCurrentUser) != 0;
            }
        }

        public boolean getTrustAgentsNonrenewableTrust() {
            return this.mTrustAgentsNonrenewableTrust;
        }

        public boolean getLockWhenTrustLost() {
            return this.mLockWhenTrustLost;
        }
    }

    public final void maybeLockScreen(int i) {
        if (i == this.mCurrentUser && this.mSettingsObserver.getLockWhenTrustLost()) {
            if (DEBUG) {
                Slog.d("TrustManagerService", "Locking device because trust was lost");
            }
            try {
                WindowManagerGlobal.getWindowManagerService().lockNow((Bundle) null);
            } catch (RemoteException unused) {
                Slog.e("TrustManagerService", "Error locking screen when trust was lost");
            }
            TrustedTimeoutAlarmListener trustedTimeoutAlarmListener = this.mTrustTimeoutAlarmListenerForUser.get(Integer.valueOf(i));
            if (trustedTimeoutAlarmListener == null || !this.mSettingsObserver.getTrustAgentsNonrenewableTrust()) {
                return;
            }
            this.mAlarmManager.cancel(trustedTimeoutAlarmListener);
            trustedTimeoutAlarmListener.setQueued(false);
        }
    }

    public final void scheduleTrustTimeout(boolean z, boolean z2) {
        this.mHandler.obtainMessage(15, z ? 1 : 0, z2 ? 1 : 0).sendToTarget();
    }

    public final void handleScheduleTrustTimeout(boolean z, TimeoutType timeoutType) {
        int i = this.mCurrentUser;
        if (timeoutType == TimeoutType.TRUSTABLE) {
            handleScheduleTrustableTimeouts(i, z, false);
        } else {
            handleScheduleTrustedTimeout(i, z);
        }
    }

    public final void refreshTrustableTimers(int i) {
        handleScheduleTrustableTimeouts(i, true, true);
    }

    public final void handleScheduleTrustedTimeout(int i, boolean z) {
        TrustedTimeoutAlarmListener trustedTimeoutAlarmListener;
        long elapsedRealtime = SystemClock.elapsedRealtime() + BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS;
        TrustedTimeoutAlarmListener trustedTimeoutAlarmListener2 = this.mTrustTimeoutAlarmListenerForUser.get(Integer.valueOf(i));
        if (trustedTimeoutAlarmListener2 != null) {
            if (!z && trustedTimeoutAlarmListener2.isQueued()) {
                if (DEBUG) {
                    Slog.d("TrustManagerService", "Found existing trust timeout alarm. Skipping.");
                    return;
                }
                return;
            }
            this.mAlarmManager.cancel(trustedTimeoutAlarmListener2);
            trustedTimeoutAlarmListener = trustedTimeoutAlarmListener2;
        } else {
            TrustedTimeoutAlarmListener trustedTimeoutAlarmListener3 = new TrustedTimeoutAlarmListener(i);
            this.mTrustTimeoutAlarmListenerForUser.put(Integer.valueOf(i), trustedTimeoutAlarmListener3);
            trustedTimeoutAlarmListener = trustedTimeoutAlarmListener3;
        }
        if (DEBUG) {
            Slog.d("TrustManagerService", "\tSetting up trust timeout alarm");
        }
        trustedTimeoutAlarmListener.setQueued(true);
        this.mAlarmManager.setExact(2, elapsedRealtime, "TrustManagerService.trustTimeoutForUser", trustedTimeoutAlarmListener, this.mHandler);
    }

    public final void handleScheduleTrustableTimeouts(int i, boolean z, boolean z2) {
        setUpIdleTimeout(i, z);
        setUpHardTimeout(i, z2);
    }

    public final void setUpIdleTimeout(int i, boolean z) {
        TrustableTimeoutAlarmListener trustableTimeoutAlarmListener;
        long elapsedRealtime = SystemClock.elapsedRealtime() + 28800000;
        TrustableTimeoutAlarmListener trustableTimeoutAlarmListener2 = this.mIdleTrustableTimeoutAlarmListenerForUser.get(i);
        this.mContext.enforceCallingOrSelfPermission("android.permission.SCHEDULE_EXACT_ALARM", null);
        if (trustableTimeoutAlarmListener2 != null) {
            if (!z && trustableTimeoutAlarmListener2.isQueued()) {
                if (DEBUG) {
                    Slog.d("TrustManagerService", "Found existing trustable timeout alarm. Skipping.");
                    return;
                }
                return;
            }
            this.mAlarmManager.cancel(trustableTimeoutAlarmListener2);
            trustableTimeoutAlarmListener = trustableTimeoutAlarmListener2;
        } else {
            TrustableTimeoutAlarmListener trustableTimeoutAlarmListener3 = new TrustableTimeoutAlarmListener(i);
            this.mIdleTrustableTimeoutAlarmListenerForUser.put(i, trustableTimeoutAlarmListener3);
            trustableTimeoutAlarmListener = trustableTimeoutAlarmListener3;
        }
        if (DEBUG) {
            Slog.d("TrustManagerService", "\tSetting up trustable idle timeout alarm");
        }
        trustableTimeoutAlarmListener.setQueued(true);
        this.mAlarmManager.setExact(2, elapsedRealtime, "TrustManagerService.trustTimeoutForUser", trustableTimeoutAlarmListener, this.mHandler);
    }

    public final void setUpHardTimeout(int i, boolean z) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.SCHEDULE_EXACT_ALARM", null);
        TrustableTimeoutAlarmListener trustableTimeoutAlarmListener = this.mTrustableTimeoutAlarmListenerForUser.get(i);
        if (trustableTimeoutAlarmListener == null || !trustableTimeoutAlarmListener.isQueued() || z) {
            long elapsedRealtime = SystemClock.elapsedRealtime() + BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS;
            if (trustableTimeoutAlarmListener == null) {
                trustableTimeoutAlarmListener = new TrustableTimeoutAlarmListener(i);
                this.mTrustableTimeoutAlarmListenerForUser.put(i, trustableTimeoutAlarmListener);
            } else if (z) {
                this.mAlarmManager.cancel(trustableTimeoutAlarmListener);
            }
            TrustableTimeoutAlarmListener trustableTimeoutAlarmListener2 = trustableTimeoutAlarmListener;
            if (DEBUG) {
                Slog.d("TrustManagerService", "\tSetting up trustable hard timeout alarm");
            }
            trustableTimeoutAlarmListener2.setQueued(true);
            this.mAlarmManager.setExact(2, elapsedRealtime, "TrustManagerService.trustTimeoutForUser", trustableTimeoutAlarmListener2, this.mHandler);
        }
    }

    /* loaded from: classes2.dex */
    public static final class AgentInfo {
        public TrustAgentWrapper agent;
        public ComponentName component;
        public Drawable icon;
        public CharSequence label;
        public SettingsAttrs settings;
        public int userId;

        public AgentInfo() {
        }

        public boolean equals(Object obj) {
            if (obj instanceof AgentInfo) {
                AgentInfo agentInfo = (AgentInfo) obj;
                return this.component.equals(agentInfo.component) && this.userId == agentInfo.userId;
            }
            return false;
        }

        public int hashCode() {
            return (this.component.hashCode() * 31) + this.userId;
        }
    }

    public final void updateTrustAll() {
        for (UserInfo userInfo : this.mUserManager.getAliveUsers()) {
            updateTrust(userInfo.id, 0);
        }
    }

    public void updateTrust(int i, int i2) {
        updateTrust(i, i2, null);
    }

    public void updateTrust(int i, int i2, AndroidFuture<GrantTrustResult> androidFuture) {
        updateTrust(i, i2, false, androidFuture);
    }

    public final void updateTrust(int i, int i2, boolean z, AndroidFuture<GrantTrustResult> androidFuture) {
        if (ENABLE_ACTIVE_UNLOCK_FLAG) {
            updateTrustWithRenewableUnlock(i, i2, z, androidFuture);
        } else {
            updateTrustWithNonrenewableTrust(i, i2, z);
        }
    }

    public final void updateTrustWithNonrenewableTrust(int i, int i2, boolean z) {
        boolean z2;
        boolean aggregateIsTrustManaged = aggregateIsTrustManaged(i);
        dispatchOnTrustManagedChanged(aggregateIsTrustManaged, i);
        if (this.mStrongAuthTracker.isTrustAllowedForUser(i) && isTrustUsuallyManagedInternal(i) != aggregateIsTrustManaged) {
            updateTrustUsuallyManaged(i, aggregateIsTrustManaged);
        }
        boolean aggregateIsTrusted = aggregateIsTrusted(i);
        boolean z3 = true;
        try {
            z2 = WindowManagerGlobal.getWindowManagerService().isKeyguardLocked();
        } catch (RemoteException unused) {
            z2 = true;
        }
        synchronized (this.mUserIsTrusted) {
            if (this.mSettingsObserver.getTrustAgentsNonrenewableTrust()) {
                aggregateIsTrusted = aggregateIsTrusted && !(z2 && !z && (this.mUserIsTrusted.get(i) != aggregateIsTrusted)) && i == this.mCurrentUser;
                if (DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Extend unlock setting trusted as ");
                    sb.append(Boolean.toString(aggregateIsTrusted));
                    sb.append(" && ");
                    sb.append(Boolean.toString(!z2));
                    sb.append(" && ");
                    sb.append(Boolean.toString(i == this.mCurrentUser));
                    Slog.d("TrustManagerService", sb.toString());
                }
            }
            if (this.mUserIsTrusted.get(i) == aggregateIsTrusted) {
                z3 = false;
            }
            this.mUserIsTrusted.put(i, aggregateIsTrusted);
        }
        dispatchOnTrustChanged(aggregateIsTrusted, false, i, i2, getTrustGrantedMessages(i));
        if (z3) {
            refreshDeviceLockedForUser(i);
            if (!aggregateIsTrusted) {
                maybeLockScreen(i);
            } else {
                scheduleTrustTimeout(false, false);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x0076  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0078  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x009c  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x00b5  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x00b7  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x00cf  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void updateTrustWithRenewableUnlock(int i, int i2, boolean z, AndroidFuture<GrantTrustResult> androidFuture) {
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean aggregateIsTrustManaged = aggregateIsTrustManaged(i);
        dispatchOnTrustManagedChanged(aggregateIsTrustManaged, i);
        if (this.mStrongAuthTracker.isTrustAllowedForUser(i) && isTrustUsuallyManagedInternal(i) != aggregateIsTrustManaged) {
            updateTrustUsuallyManaged(i, aggregateIsTrustManaged);
        }
        boolean aggregateIsTrusted = aggregateIsTrusted(i);
        boolean aggregateIsTrustable = aggregateIsTrustable(i);
        try {
            z2 = !WindowManagerGlobal.getWindowManagerService().isKeyguardLocked();
        } catch (RemoteException unused) {
            z2 = false;
        }
        synchronized (this.mUserTrustState) {
            TrustState trustState = this.mUserTrustState.get(i);
            TrustState trustState2 = TrustState.TRUSTED;
            boolean z7 = trustState == trustState2;
            TrustState trustState3 = this.mUserTrustState.get(i);
            TrustState trustState4 = TrustState.TRUSTABLE;
            boolean z8 = trustState3 == trustState4;
            boolean hasSystemFeature = getContext().getPackageManager().hasSystemFeature("android.hardware.type.automotive");
            boolean z9 = z8 && (i2 & 4) != 0;
            if (!z2 && !z && !z9 && !hasSystemFeature) {
                z3 = false;
                boolean z10 = i != this.mCurrentUser;
                if (aggregateIsTrusted || !z7) {
                    if (!aggregateIsTrusted && z3 && z10) {
                        trustState4 = trustState2;
                    } else if (aggregateIsTrustable || ((!z7 && !z8) || !z10)) {
                        trustState4 = TrustState.UNTRUSTED;
                    }
                    this.mUserTrustState.put(i, trustState4);
                    z4 = DEBUG;
                    if (z4) {
                        Slog.d("TrustManagerService", "pendingTrustState: " + trustState4);
                    }
                    z5 = trustState4 != trustState2;
                    z6 = z2 && z5;
                    dispatchOnTrustChanged(z5, z6, i, i2, getTrustGrantedMessages(i));
                    if (z5 != z7) {
                        refreshDeviceLockedForUser(i);
                        if (!z5) {
                            maybeLockScreen(i);
                        } else {
                            boolean z11 = (i2 & 4) != 0;
                            scheduleTrustTimeout(z11, z11);
                        }
                    }
                    if (z6 || androidFuture == null) {
                    }
                    if (z4) {
                        Slog.d("TrustManagerService", "calling back with UNLOCKED_BY_GRANT");
                    }
                    androidFuture.complete(new GrantTrustResult(1));
                    return;
                }
                return;
            }
            z3 = true;
            if (i != this.mCurrentUser) {
            }
            if (aggregateIsTrusted) {
            }
            if (!aggregateIsTrusted) {
            }
            if (aggregateIsTrustable) {
            }
            trustState4 = TrustState.UNTRUSTED;
            this.mUserTrustState.put(i, trustState4);
            z4 = DEBUG;
            if (z4) {
            }
            if (trustState4 != trustState2) {
            }
            if (z2) {
            }
            dispatchOnTrustChanged(z5, z6, i, i2, getTrustGrantedMessages(i));
            if (z5 != z7) {
            }
            if (z6) {
            }
        }
    }

    public final void updateTrustUsuallyManaged(int i, boolean z) {
        synchronized (this.mTrustUsuallyManagedForUser) {
            this.mTrustUsuallyManagedForUser.put(i, z);
        }
        this.mHandler.removeMessages(10);
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(10), 120000L);
    }

    public long addEscrowToken(byte[] bArr, int i) {
        return this.mLockPatternUtils.addEscrowToken(bArr, i, new LockPatternUtils.EscrowTokenStateChangeCallback() { // from class: com.android.server.trust.TrustManagerService$$ExternalSyntheticLambda0
            public final void onEscrowTokenActivated(long j, int i2) {
                TrustManagerService.this.lambda$addEscrowToken$0(j, i2);
            }
        });
    }

    public boolean removeEscrowToken(long j, int i) {
        return this.mLockPatternUtils.removeEscrowToken(j, i);
    }

    public boolean isEscrowTokenActive(long j, int i) {
        return this.mLockPatternUtils.isEscrowTokenActive(j, i);
    }

    public void unlockUserWithToken(long j, byte[] bArr, int i) {
        this.mLockPatternUtils.unlockUserWithToken(j, bArr, i);
    }

    public void lockUser(int i) {
        this.mLockPatternUtils.requireStrongAuth(256, i);
        try {
            WindowManagerGlobal.getWindowManagerService().lockNow((Bundle) null);
        } catch (RemoteException unused) {
            Slog.e("TrustManagerService", "Error locking screen when called from trust agent");
        }
    }

    public void showKeyguardErrorMessage(CharSequence charSequence) {
        dispatchOnTrustError(charSequence);
    }

    public void refreshAgentList(int i) {
        List arrayList;
        Iterator<ResolveInfo> it;
        int strongAuthForUser;
        int i2 = i;
        if (DEBUG) {
            Slog.d("TrustManagerService", "refreshAgentList(" + i2 + ")");
        }
        if (this.mTrustAgentsCanRun) {
            if (i2 != -1 && i2 < 0) {
                Log.e("TrustManagerService", "refreshAgentList(userId=" + i2 + "): Invalid user handle, must be USER_ALL or a specific user.", new Throwable("here"));
                i2 = -1;
            }
            PackageManager packageManager = this.mContext.getPackageManager();
            if (i2 == -1) {
                arrayList = this.mUserManager.getAliveUsers();
            } else {
                arrayList = new ArrayList();
                arrayList.add(this.mUserManager.getUserInfo(i2));
            }
            LockPatternUtils lockPatternUtils = this.mLockPatternUtils;
            ArraySet arraySet = new ArraySet();
            arraySet.addAll((ArraySet) this.mActiveAgents);
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                UserInfo userInfo = (UserInfo) it2.next();
                if (userInfo != null && !userInfo.partial && userInfo.isEnabled() && !userInfo.guestToRemove) {
                    if (!userInfo.supportsSwitchToByUser()) {
                        if (DEBUG) {
                            Slog.d("TrustManagerService", "refreshAgentList: skipping user " + userInfo.id + ": switchToByUser=false");
                        }
                    } else if (!this.mActivityManager.isUserRunning(userInfo.id)) {
                        if (DEBUG) {
                            Slog.d("TrustManagerService", "refreshAgentList: skipping user " + userInfo.id + ": user not started");
                        }
                    } else if (!lockPatternUtils.isSecure(userInfo.id)) {
                        if (DEBUG) {
                            Slog.d("TrustManagerService", "refreshAgentList: skipping user " + userInfo.id + ": no secure credential");
                        }
                    } else {
                        DevicePolicyManager devicePolicyManager = lockPatternUtils.getDevicePolicyManager();
                        boolean z = (devicePolicyManager.getKeyguardDisabledFeatures(null, userInfo.id) & 16) != 0;
                        List enabledTrustAgents = lockPatternUtils.getEnabledTrustAgents(userInfo.id);
                        if (enabledTrustAgents.isEmpty()) {
                            if (DEBUG) {
                                Slog.d("TrustManagerService", "refreshAgentList: skipping user " + userInfo.id + ": no agents enabled by user");
                            }
                        } else {
                            Iterator<ResolveInfo> it3 = resolveAllowedTrustAgents(packageManager, userInfo.id).iterator();
                            while (it3.hasNext()) {
                                ResolveInfo next = it3.next();
                                ComponentName componentName = getComponentName(next);
                                Iterator it4 = it2;
                                if (!enabledTrustAgents.contains(componentName)) {
                                    if (DEBUG) {
                                        Slog.d("TrustManagerService", "refreshAgentList: skipping " + componentName.flattenToShortString() + " u" + userInfo.id + ": not enabled by user");
                                    }
                                    it2 = it4;
                                } else {
                                    LockPatternUtils lockPatternUtils2 = lockPatternUtils;
                                    if (z) {
                                        it = it3;
                                        List trustAgentConfiguration = devicePolicyManager.getTrustAgentConfiguration(null, componentName, userInfo.id);
                                        if (trustAgentConfiguration == null || trustAgentConfiguration.isEmpty()) {
                                            if (DEBUG) {
                                                Slog.d("TrustManagerService", "refreshAgentList: skipping " + componentName.flattenToShortString() + " u" + userInfo.id + ": not allowed by DPM");
                                            }
                                            lockPatternUtils = lockPatternUtils2;
                                            it2 = it4;
                                            it3 = it;
                                        }
                                    } else {
                                        it = it3;
                                    }
                                    AgentInfo agentInfo = new AgentInfo();
                                    agentInfo.component = componentName;
                                    agentInfo.userId = userInfo.id;
                                    if (!this.mActiveAgents.contains(agentInfo)) {
                                        agentInfo.label = next.loadLabel(packageManager);
                                        agentInfo.icon = next.loadIcon(packageManager);
                                        agentInfo.settings = getSettingsAttrs(packageManager, next);
                                    } else {
                                        agentInfo = this.mActiveAgents.valueAt(this.mActiveAgents.indexOf(agentInfo));
                                    }
                                    SettingsAttrs settingsAttrs = agentInfo.settings;
                                    boolean z2 = settingsAttrs != null && next.serviceInfo.directBootAware && settingsAttrs.canUnlockProfile;
                                    if (z2 && DEBUG) {
                                        Slog.d("TrustManagerService", "refreshAgentList: trustagent " + componentName + "of user " + userInfo.id + "can unlock user profile.");
                                    }
                                    if (!this.mUserManager.isUserUnlockingOrUnlocked(userInfo.id) && !z2) {
                                        if (DEBUG) {
                                            Slog.d("TrustManagerService", "refreshAgentList: skipping user " + userInfo.id + "'s trust agent " + componentName + ": FBE still locked and  the agent cannot unlock user profile.");
                                        }
                                    } else {
                                        if (!this.mStrongAuthTracker.canAgentsRunForUser(userInfo.id) && (strongAuthForUser = this.mStrongAuthTracker.getStrongAuthForUser(userInfo.id)) != 8) {
                                            if (strongAuthForUser != 1 || !z2) {
                                                if (DEBUG) {
                                                    Slog.d("TrustManagerService", "refreshAgentList: skipping user " + userInfo.id + ": prevented by StrongAuthTracker = 0x" + Integer.toHexString(this.mStrongAuthTracker.getStrongAuthForUser(userInfo.id)));
                                                }
                                            }
                                        }
                                        if (agentInfo.agent == null) {
                                            agentInfo.agent = new TrustAgentWrapper(this.mContext, this, new Intent().setComponent(componentName), userInfo.getUserHandle());
                                        }
                                        if (!this.mActiveAgents.contains(agentInfo)) {
                                            this.mActiveAgents.add(agentInfo);
                                        } else {
                                            arraySet.remove(agentInfo);
                                        }
                                        lockPatternUtils = lockPatternUtils2;
                                        it2 = it4;
                                        it3 = it;
                                    }
                                    lockPatternUtils = lockPatternUtils2;
                                    it2 = it4;
                                    it3 = it;
                                }
                            }
                        }
                    }
                }
            }
            boolean z3 = false;
            for (int i3 = 0; i3 < arraySet.size(); i3++) {
                AgentInfo agentInfo2 = (AgentInfo) arraySet.valueAt(i3);
                if (i2 == -1 || i2 == agentInfo2.userId) {
                    if (agentInfo2.agent.isManagingTrust()) {
                        z3 = true;
                    }
                    agentInfo2.agent.destroy();
                    this.mActiveAgents.remove(agentInfo2);
                }
            }
            if (z3) {
                if (i2 == -1) {
                    updateTrustAll();
                } else {
                    updateTrust(i2, 0);
                }
            }
        }
    }

    public boolean isDeviceLockedInner(int i) {
        boolean z;
        synchronized (this.mDeviceLockedForUser) {
            z = this.mDeviceLockedForUser.get(i, true);
        }
        return z;
    }

    public final void refreshDeviceLockedForUser(int i) {
        refreshDeviceLockedForUser(i, -10000);
    }

    public final void refreshDeviceLockedForUser(int i, int i2) {
        List list;
        boolean z;
        boolean z2;
        boolean z3;
        if (i != -1 && i < 0) {
            Log.e("TrustManagerService", "refreshDeviceLockedForUser(userId=" + i + "): Invalid user handle, must be USER_ALL or a specific user.", new Throwable("here"));
            i = -1;
        }
        if (i == -1) {
            list = this.mUserManager.getAliveUsers();
        } else {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.mUserManager.getUserInfo(i));
            list = arrayList;
        }
        IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
        for (int i3 = 0; i3 < list.size(); i3++) {
            UserInfo userInfo = (UserInfo) list.get(i3);
            if (userInfo != null && !userInfo.partial && userInfo.isEnabled() && !userInfo.guestToRemove) {
                int i4 = userInfo.id;
                boolean isSecure = this.mLockPatternUtils.isSecure(i4);
                if (!userInfo.supportsSwitchToByUser()) {
                    if (userInfo.isManagedProfile() && !isSecure) {
                        setDeviceLockedForUser(i4, false);
                    }
                } else {
                    boolean aggregateIsTrusted = aggregateIsTrusted(i4);
                    boolean z4 = true;
                    if (this.mCurrentUser == i4) {
                        synchronized (this.mUsersUnlockedByBiometric) {
                            z = this.mUsersUnlockedByBiometric.get(i4, false);
                        }
                        try {
                            z3 = windowManagerService.isKeyguardLocked();
                        } catch (RemoteException e) {
                            Log.w("TrustManagerService", "Unable to check keyguard lock state", e);
                            z3 = true;
                        }
                        z2 = i2 == i4;
                    } else {
                        z = false;
                        z2 = false;
                        z3 = true;
                    }
                    z4 = (!isSecure || !z3 || aggregateIsTrusted || z) ? false : false;
                    if (!z4 || !z2) {
                        setDeviceLockedForUser(i4, z4);
                    }
                }
            }
        }
    }

    public final void setDeviceLockedForUser(int i, boolean z) {
        int i2;
        boolean z2;
        int[] enabledProfileIds;
        synchronized (this.mDeviceLockedForUser) {
            z2 = isDeviceLockedInner(i) != z;
            this.mDeviceLockedForUser.put(i, z);
        }
        if (z2) {
            dispatchDeviceLocked(i, z);
            Authorization.onLockScreenEvent(z, i, (byte[]) null, getBiometricSids(i));
            for (int i3 : this.mUserManager.getEnabledProfileIds(i)) {
                if (this.mLockPatternUtils.isManagedProfileWithUnifiedChallenge(i3)) {
                    Authorization.onLockScreenEvent(z, i3, (byte[]) null, getBiometricSids(i3));
                }
            }
        }
    }

    public final void dispatchDeviceLocked(int i, boolean z) {
        for (int i2 = 0; i2 < this.mActiveAgents.size(); i2++) {
            AgentInfo valueAt = this.mActiveAgents.valueAt(i2);
            if (valueAt.userId == i) {
                if (z) {
                    valueAt.agent.onDeviceLocked();
                } else {
                    valueAt.agent.onDeviceUnlocked();
                }
            }
        }
    }

    /* renamed from: dispatchEscrowTokenActivatedLocked */
    public final void lambda$addEscrowToken$0(long j, int i) {
        for (int i2 = 0; i2 < this.mActiveAgents.size(); i2++) {
            AgentInfo valueAt = this.mActiveAgents.valueAt(i2);
            if (valueAt.userId == i) {
                valueAt.agent.onEscrowTokenActivated(j, i);
            }
        }
    }

    public void updateDevicePolicyFeatures() {
        boolean z = false;
        for (int i = 0; i < this.mActiveAgents.size(); i++) {
            AgentInfo valueAt = this.mActiveAgents.valueAt(i);
            if (valueAt.agent.isConnected()) {
                valueAt.agent.updateDevicePolicyFeatures();
                z = true;
            }
        }
        if (z) {
            this.mArchive.logDevicePolicyChanged();
        }
    }

    public final void removeAgentsOfPackage(String str) {
        boolean z = false;
        for (int size = this.mActiveAgents.size() - 1; size >= 0; size--) {
            AgentInfo valueAt = this.mActiveAgents.valueAt(size);
            if (str.equals(valueAt.component.getPackageName())) {
                Log.i("TrustManagerService", "Resetting agent " + valueAt.component.flattenToShortString());
                if (valueAt.agent.isManagingTrust()) {
                    z = true;
                }
                valueAt.agent.destroy();
                this.mActiveAgents.removeAt(size);
            }
        }
        if (z) {
            updateTrustAll();
        }
    }

    public void resetAgent(ComponentName componentName, int i) {
        boolean z = false;
        for (int size = this.mActiveAgents.size() - 1; size >= 0; size--) {
            AgentInfo valueAt = this.mActiveAgents.valueAt(size);
            if (componentName.equals(valueAt.component) && i == valueAt.userId) {
                Log.i("TrustManagerService", "Resetting agent " + valueAt.component.flattenToShortString());
                if (valueAt.agent.isManagingTrust()) {
                    z = true;
                }
                valueAt.agent.destroy();
                this.mActiveAgents.removeAt(size);
            }
        }
        if (z) {
            updateTrust(i, 0);
        }
        refreshAgentList(i);
    }

    /* JADX WARN: Code restructure failed: missing block: B:49:0x0087, code lost:
        if (r1 == null) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0089, code lost:
        r1.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0090, code lost:
        if (r1 == null) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x0096, code lost:
        if (r1 == null) goto L32;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final SettingsAttrs getSettingsAttrs(PackageManager packageManager, ResolveInfo resolveInfo) {
        ServiceInfo serviceInfo;
        XmlResourceParser xmlResourceParser;
        String str;
        XmlResourceParser xmlResourceParser2 = null;
        if (resolveInfo == null || (serviceInfo = resolveInfo.serviceInfo) == null || serviceInfo.metaData == null) {
            return null;
        }
        boolean z = false;
        try {
            xmlResourceParser = serviceInfo.loadXmlMetaData(packageManager, "android.service.trust.trustagent");
            try {
                try {
                } catch (Throwable th) {
                    th = th;
                    xmlResourceParser2 = xmlResourceParser;
                    if (xmlResourceParser2 != null) {
                        xmlResourceParser2.close();
                    }
                    throw th;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e = e;
                str = null;
            } catch (IOException e2) {
                e = e2;
                str = null;
            } catch (XmlPullParserException e3) {
                e = e3;
                str = null;
            }
        } catch (PackageManager.NameNotFoundException e4) {
            e = e4;
            xmlResourceParser = null;
            str = null;
        } catch (IOException e5) {
            e = e5;
            xmlResourceParser = null;
            str = null;
        } catch (XmlPullParserException e6) {
            e = e6;
            xmlResourceParser = null;
            str = null;
        } catch (Throwable th2) {
            th = th2;
        }
        if (xmlResourceParser == null) {
            Slog.w("TrustManagerService", "Can't find android.service.trust.trustagent meta-data");
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            return null;
        }
        Resources resourcesForApplication = packageManager.getResourcesForApplication(resolveInfo.serviceInfo.applicationInfo);
        AttributeSet asAttributeSet = Xml.asAttributeSet(xmlResourceParser);
        while (true) {
            int next = xmlResourceParser.next();
            if (next == 1 || next == 2) {
                break;
            }
        }
        if (!"trust-agent".equals(xmlResourceParser.getName())) {
            Slog.w("TrustManagerService", "Meta-data does not start with trust-agent tag");
            xmlResourceParser.close();
            return null;
        }
        TypedArray obtainAttributes = resourcesForApplication.obtainAttributes(asAttributeSet, R.styleable.TrustAgent);
        str = obtainAttributes.getString(2);
        try {
            z = asAttributeSet.getAttributeBooleanValue("http://schemas.android.com/apk/prv/res/android", "unlockProfile", false);
            obtainAttributes.recycle();
            xmlResourceParser.close();
            e = null;
        } catch (PackageManager.NameNotFoundException e7) {
            e = e7;
        } catch (IOException e8) {
            e = e8;
        } catch (XmlPullParserException e9) {
            e = e9;
        }
        if (e != null) {
            Slog.w("TrustManagerService", "Error parsing : " + resolveInfo.serviceInfo.packageName, e);
            return null;
        } else if (str == null) {
            return null;
        } else {
            if (str.indexOf(47) < 0) {
                str = resolveInfo.serviceInfo.packageName + "/" + str;
            }
            return new SettingsAttrs(ComponentName.unflattenFromString(str), z);
        }
    }

    public final ComponentName getComponentName(ResolveInfo resolveInfo) {
        if (resolveInfo == null || resolveInfo.serviceInfo == null) {
            return null;
        }
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        return new ComponentName(serviceInfo.packageName, serviceInfo.name);
    }

    public final void maybeEnableFactoryTrustAgents(int i) {
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "trust_agents_initialized", 0, i) != 0) {
            return;
        }
        List<ResolveInfo> resolveAllowedTrustAgents = resolveAllowedTrustAgents(this.mContext.getPackageManager(), i);
        ComponentName defaultFactoryTrustAgent = getDefaultFactoryTrustAgent(this.mContext);
        boolean z = defaultFactoryTrustAgent != null;
        ArraySet arraySet = new ArraySet();
        if (z) {
            arraySet.add(defaultFactoryTrustAgent);
            Log.i("TrustManagerService", "Enabling " + defaultFactoryTrustAgent + " because it is a default agent.");
        } else {
            for (ResolveInfo resolveInfo : resolveAllowedTrustAgents) {
                ComponentName componentName = getComponentName(resolveInfo);
                if (isSystemTrustAgent(resolveInfo)) {
                    arraySet.add(componentName);
                } else {
                    Log.i("TrustManagerService", "Leaving agent " + componentName + " disabled because package is not a system package.");
                }
            }
        }
        enableNewAgents(arraySet, i);
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "trust_agents_initialized", 1, i);
    }

    public final void checkNewAgents() {
        for (UserInfo userInfo : this.mUserManager.getAliveUsers()) {
            checkNewAgentsForUser(userInfo.id);
        }
    }

    public final void checkNewAgentsForUser(int i) {
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "known_trust_agents_initialized", 0, i) == 0) {
            initializeKnownAgents(i);
            return;
        }
        List knownTrustAgents = this.mLockPatternUtils.getKnownTrustAgents(i);
        List<ResolveInfo> resolveAllowedTrustAgents = resolveAllowedTrustAgents(this.mContext.getPackageManager(), i);
        ArraySet arraySet = new ArraySet(resolveAllowedTrustAgents.size());
        ArraySet arraySet2 = new ArraySet(resolveAllowedTrustAgents.size());
        for (ResolveInfo resolveInfo : resolveAllowedTrustAgents) {
            ComponentName componentName = getComponentName(resolveInfo);
            if (!knownTrustAgents.contains(componentName)) {
                arraySet.add(componentName);
                if (isSystemTrustAgent(resolveInfo)) {
                    arraySet2.add(componentName);
                }
            }
        }
        if (arraySet.isEmpty()) {
            return;
        }
        ArraySet arraySet3 = new ArraySet(knownTrustAgents);
        arraySet3.addAll(arraySet);
        this.mLockPatternUtils.setKnownTrustAgents(arraySet3, i);
        if (getDefaultFactoryTrustAgent(this.mContext) != null) {
            return;
        }
        enableNewAgents(arraySet2, i);
    }

    public final void enableNewAgents(Collection<ComponentName> collection, int i) {
        if (collection.isEmpty()) {
            return;
        }
        ArraySet arraySet = new ArraySet(collection);
        arraySet.addAll(this.mLockPatternUtils.getEnabledTrustAgents(i));
        this.mLockPatternUtils.setEnabledTrustAgents(arraySet, i);
    }

    public final void initializeKnownAgents(int i) {
        List<ResolveInfo> resolveAllowedTrustAgents = resolveAllowedTrustAgents(this.mContext.getPackageManager(), i);
        ArraySet arraySet = new ArraySet(resolveAllowedTrustAgents.size());
        for (ResolveInfo resolveInfo : resolveAllowedTrustAgents) {
            arraySet.add(getComponentName(resolveInfo));
        }
        this.mLockPatternUtils.setKnownTrustAgents(arraySet, i);
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "known_trust_agents_initialized", 1, i);
    }

    public static ComponentName getDefaultFactoryTrustAgent(Context context) {
        String string = context.getResources().getString(17039906);
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return ComponentName.unflattenFromString(string);
    }

    public final List<ResolveInfo> resolveAllowedTrustAgents(PackageManager packageManager, int i) {
        List<ResolveInfo> queryIntentServicesAsUser = packageManager.queryIntentServicesAsUser(TRUST_AGENT_INTENT, 786560, i);
        ArrayList arrayList = new ArrayList(queryIntentServicesAsUser.size());
        for (ResolveInfo resolveInfo : queryIntentServicesAsUser) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo != null && serviceInfo.applicationInfo != null) {
                if (packageManager.checkPermission("android.permission.PROVIDE_TRUST_AGENT", serviceInfo.packageName) != 0) {
                    ComponentName componentName = getComponentName(resolveInfo);
                    Log.w("TrustManagerService", "Skipping agent " + componentName + " because package does not have permission android.permission.PROVIDE_TRUST_AGENT.");
                } else {
                    arrayList.add(resolveInfo);
                }
            }
        }
        return arrayList;
    }

    public static boolean isSystemTrustAgent(ResolveInfo resolveInfo) {
        return (resolveInfo.serviceInfo.applicationInfo.flags & 1) != 0;
    }

    public final boolean aggregateIsTrusted(int i) {
        if (this.mStrongAuthTracker.isTrustAllowedForUser(i)) {
            for (int i2 = 0; i2 < this.mActiveAgents.size(); i2++) {
                AgentInfo valueAt = this.mActiveAgents.valueAt(i2);
                if (valueAt.userId == i && valueAt.agent.isTrusted()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public final boolean aggregateIsTrustable(int i) {
        if (this.mStrongAuthTracker.isTrustAllowedForUser(i)) {
            for (int i2 = 0; i2 < this.mActiveAgents.size(); i2++) {
                AgentInfo valueAt = this.mActiveAgents.valueAt(i2);
                if (valueAt.userId == i && valueAt.agent.isTrustable()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public final void dispatchTrustableDowngrade() {
        for (int i = 0; i < this.mActiveAgents.size(); i++) {
            AgentInfo valueAt = this.mActiveAgents.valueAt(i);
            if (valueAt.userId == this.mCurrentUser) {
                valueAt.agent.downgradeToTrustable();
            }
        }
    }

    public final List<String> getTrustGrantedMessages(int i) {
        if (!this.mStrongAuthTracker.isTrustAllowedForUser(i)) {
            return new ArrayList();
        }
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.mActiveAgents.size(); i2++) {
            AgentInfo valueAt = this.mActiveAgents.valueAt(i2);
            if (valueAt.userId == i && valueAt.agent.isTrusted() && valueAt.agent.shouldDisplayTrustGrantedMessage() && valueAt.agent.getMessage() != null) {
                arrayList.add(valueAt.agent.getMessage().toString());
            }
        }
        return arrayList;
    }

    public final boolean aggregateIsTrustManaged(int i) {
        if (this.mStrongAuthTracker.isTrustAllowedForUser(i)) {
            for (int i2 = 0; i2 < this.mActiveAgents.size(); i2++) {
                AgentInfo valueAt = this.mActiveAgents.valueAt(i2);
                if (valueAt.userId == i && valueAt.agent.isManagingTrust()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public final void dispatchUnlockAttempt(boolean z, int i) {
        if (z) {
            this.mStrongAuthTracker.allowTrustFromUnlock(i);
            updateTrust(i, 0, true, null);
            this.mHandler.obtainMessage(17, Integer.valueOf(i)).sendToTarget();
        }
        for (int i2 = 0; i2 < this.mActiveAgents.size(); i2++) {
            AgentInfo valueAt = this.mActiveAgents.valueAt(i2);
            if (valueAt.userId == i) {
                valueAt.agent.onUnlockAttempt(z);
            }
        }
    }

    public final void dispatchUserRequestedUnlock(int i, boolean z) {
        if (DEBUG) {
            Slog.d("TrustManagerService", "dispatchUserRequestedUnlock(user=" + i + ", dismissKeyguard=" + z + ")");
        }
        for (int i2 = 0; i2 < this.mActiveAgents.size(); i2++) {
            AgentInfo valueAt = this.mActiveAgents.valueAt(i2);
            if (valueAt.userId == i) {
                valueAt.agent.onUserRequestedUnlock(z);
            }
        }
    }

    public final void dispatchUserMayRequestUnlock(int i) {
        if (DEBUG) {
            Slog.d("TrustManagerService", "dispatchUserMayRequestUnlock(user=" + i + ")");
        }
        for (int i2 = 0; i2 < this.mActiveAgents.size(); i2++) {
            AgentInfo valueAt = this.mActiveAgents.valueAt(i2);
            if (valueAt.userId == i) {
                valueAt.agent.onUserMayRequestUnlock();
            }
        }
    }

    public final void dispatchUnlockLockout(int i, int i2) {
        for (int i3 = 0; i3 < this.mActiveAgents.size(); i3++) {
            AgentInfo valueAt = this.mActiveAgents.valueAt(i3);
            if (valueAt.userId == i2) {
                valueAt.agent.onUnlockLockout(i);
            }
        }
    }

    public final void addListener(ITrustListener iTrustListener) {
        for (int i = 0; i < this.mTrustListeners.size(); i++) {
            if (this.mTrustListeners.get(i).asBinder() == iTrustListener.asBinder()) {
                return;
            }
        }
        this.mTrustListeners.add(iTrustListener);
        updateTrustAll();
    }

    public final void removeListener(ITrustListener iTrustListener) {
        for (int i = 0; i < this.mTrustListeners.size(); i++) {
            if (this.mTrustListeners.get(i).asBinder() == iTrustListener.asBinder()) {
                this.mTrustListeners.remove(i);
                return;
            }
        }
    }

    public final void dispatchOnTrustChanged(boolean z, boolean z2, int i, int i2, List<String> list) {
        if (DEBUG) {
            Log.i("TrustManagerService", "onTrustChanged(" + z + ", " + z2 + ", " + i + ", 0x" + Integer.toHexString(i2) + ")");
        }
        int i3 = 0;
        if (!z) {
            i2 = 0;
        }
        while (i3 < this.mTrustListeners.size()) {
            try {
                this.mTrustListeners.get(i3).onTrustChanged(z, z2, i, i2, list);
            } catch (DeadObjectException unused) {
                Slog.d("TrustManagerService", "Removing dead TrustListener.");
                this.mTrustListeners.remove(i3);
                i3--;
            } catch (RemoteException e) {
                Slog.e("TrustManagerService", "Exception while notifying TrustListener.", e);
            }
            i3++;
        }
    }

    public final void dispatchOnTrustManagedChanged(boolean z, int i) {
        if (DEBUG) {
            Log.i("TrustManagerService", "onTrustManagedChanged(" + z + ", " + i + ")");
        }
        int i2 = 0;
        while (i2 < this.mTrustListeners.size()) {
            try {
                this.mTrustListeners.get(i2).onTrustManagedChanged(z, i);
            } catch (DeadObjectException unused) {
                Slog.d("TrustManagerService", "Removing dead TrustListener.");
                this.mTrustListeners.remove(i2);
                i2--;
            } catch (RemoteException e) {
                Slog.e("TrustManagerService", "Exception while notifying TrustListener.", e);
            }
            i2++;
        }
    }

    public final void dispatchOnTrustError(CharSequence charSequence) {
        if (DEBUG) {
            Log.i("TrustManagerService", "onTrustError(" + ((Object) charSequence) + ")");
        }
        int i = 0;
        while (i < this.mTrustListeners.size()) {
            try {
                this.mTrustListeners.get(i).onTrustError(charSequence);
            } catch (DeadObjectException unused) {
                Slog.d("TrustManagerService", "Removing dead TrustListener.");
                this.mTrustListeners.remove(i);
                i--;
            } catch (RemoteException e) {
                Slog.e("TrustManagerService", "Exception while notifying TrustListener.", e);
            }
            i++;
        }
    }

    public final long[] getBiometricSids(int i) {
        BiometricManager biometricManager = (BiometricManager) this.mContext.getSystemService(BiometricManager.class);
        if (biometricManager == null) {
            return null;
        }
        return biometricManager.getAuthenticatorIds(i);
    }

    @Override // com.android.server.SystemService
    public void onUserStarting(SystemService.TargetUser targetUser) {
        this.mHandler.obtainMessage(7, targetUser.getUserIdentifier(), 0, null).sendToTarget();
    }

    @Override // com.android.server.SystemService
    public void onUserStopped(SystemService.TargetUser targetUser) {
        this.mHandler.obtainMessage(8, targetUser.getUserIdentifier(), 0, null).sendToTarget();
    }

    @Override // com.android.server.SystemService
    public void onUserSwitching(SystemService.TargetUser targetUser, SystemService.TargetUser targetUser2) {
        this.mHandler.obtainMessage(9, targetUser2.getUserIdentifier(), 0, null).sendToTarget();
    }

    @Override // com.android.server.SystemService
    public void onUserUnlocking(SystemService.TargetUser targetUser) {
        this.mHandler.obtainMessage(11, targetUser.getUserIdentifier(), 0, null).sendToTarget();
    }

    @Override // com.android.server.SystemService
    public void onUserStopping(SystemService.TargetUser targetUser) {
        this.mHandler.obtainMessage(12, targetUser.getUserIdentifier(), 0, null).sendToTarget();
    }

    /* renamed from: com.android.server.trust.TrustManagerService$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public class trustITrustManager$StubC17001 extends ITrustManager.Stub {
        public static /* synthetic */ void lambda$reportKeyguardShowingChanged$0() {
        }

        public final String dumpBool(boolean z) {
            return z ? "1" : "0";
        }

        public trustITrustManager$StubC17001() {
        }

        public void reportUnlockAttempt(boolean z, int i) throws RemoteException {
            enforceReportPermission();
            TrustManagerService.this.mHandler.obtainMessage(3, z ? 1 : 0, i).sendToTarget();
        }

        public void reportUserRequestedUnlock(int i, boolean z) throws RemoteException {
            enforceReportPermission();
            TrustManagerService.this.mHandler.obtainMessage(16, i, z ? 1 : 0).sendToTarget();
        }

        public void reportUserMayRequestUnlock(int i) throws RemoteException {
            enforceReportPermission();
            TrustManagerService.this.mHandler.obtainMessage(18, Integer.valueOf(i)).sendToTarget();
        }

        public void reportUnlockLockout(int i, int i2) throws RemoteException {
            enforceReportPermission();
            TrustManagerService.this.mHandler.obtainMessage(13, i, i2).sendToTarget();
        }

        public void reportEnabledTrustAgentsChanged(int i) throws RemoteException {
            enforceReportPermission();
            TrustManagerService.this.mHandler.removeMessages(4);
            TrustManagerService.this.mHandler.sendEmptyMessage(4);
        }

        public void reportKeyguardShowingChanged() throws RemoteException {
            enforceReportPermission();
            TrustManagerService.this.mHandler.removeMessages(6);
            TrustManagerService.this.mHandler.sendEmptyMessage(6);
            TrustManagerService.this.mHandler.runWithScissors(new Runnable() { // from class: com.android.server.trust.TrustManagerService$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TrustManagerService.trustITrustManager$StubC17001.lambda$reportKeyguardShowingChanged$0();
                }
            }, 0L);
        }

        public void registerTrustListener(ITrustListener iTrustListener) throws RemoteException {
            enforceListenerPermission();
            TrustManagerService.this.mHandler.obtainMessage(1, iTrustListener).sendToTarget();
        }

        public void unregisterTrustListener(ITrustListener iTrustListener) throws RemoteException {
            enforceListenerPermission();
            TrustManagerService.this.mHandler.obtainMessage(2, iTrustListener).sendToTarget();
        }

        public final boolean isAppOrDisplayOnAnyVirtualDevice(int i, int i2) {
            if (UserHandle.isCore(i)) {
                return false;
            }
            if (TrustManagerService.this.mVirtualDeviceManager == null) {
                TrustManagerService.this.mVirtualDeviceManager = (VirtualDeviceManagerInternal) LocalServices.getService(VirtualDeviceManagerInternal.class);
                if (TrustManagerService.this.mVirtualDeviceManager == null) {
                    return false;
                }
            }
            if (i2 == -1) {
                if (TrustManagerService.this.mVirtualDeviceManager.isAppRunningOnAnyVirtualDevice(i)) {
                    return true;
                }
            } else if (i2 != 0 && TrustManagerService.this.mVirtualDeviceManager.isDisplayOwnedByAnyVirtualDevice(i2)) {
                return true;
            }
            return false;
        }

        public boolean isDeviceLocked(int i, int i2) throws RemoteException {
            int callingUid = ITrustManager.Stub.getCallingUid();
            if (isAppOrDisplayOnAnyVirtualDevice(callingUid, i2)) {
                return false;
            }
            int handleIncomingUser = ActivityManager.handleIncomingUser(ITrustManager.Stub.getCallingPid(), callingUid, i, false, true, "isDeviceLocked", null);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (!TrustManagerService.this.mLockPatternUtils.isSeparateProfileChallengeEnabled(handleIncomingUser)) {
                    handleIncomingUser = TrustManagerService.this.resolveProfileParent(handleIncomingUser);
                }
                return TrustManagerService.this.isDeviceLockedInner(handleIncomingUser);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isDeviceSecure(int i, int i2) throws RemoteException {
            int callingUid = ITrustManager.Stub.getCallingUid();
            if (isAppOrDisplayOnAnyVirtualDevice(callingUid, i2)) {
                return false;
            }
            int handleIncomingUser = ActivityManager.handleIncomingUser(ITrustManager.Stub.getCallingPid(), callingUid, i, false, true, "isDeviceSecure", null);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (!TrustManagerService.this.mLockPatternUtils.isSeparateProfileChallengeEnabled(handleIncomingUser)) {
                    handleIncomingUser = TrustManagerService.this.resolveProfileParent(handleIncomingUser);
                }
                return TrustManagerService.this.mLockPatternUtils.isSecure(handleIncomingUser);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public final void enforceReportPermission() {
            TrustManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE", "reporting trust events");
        }

        public final void enforceListenerPermission() {
            TrustManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.TRUST_LISTENER", "register trust listener");
        }

        public void dump(FileDescriptor fileDescriptor, final PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(TrustManagerService.this.mContext, "TrustManagerService", printWriter)) {
                if (TrustManagerService.this.isSafeMode()) {
                    printWriter.println("disabled because the system is in safe mode.");
                } else if (!TrustManagerService.this.mTrustAgentsCanRun) {
                    printWriter.println("disabled because the third-party apps can't run yet.");
                } else {
                    final List aliveUsers = TrustManagerService.this.mUserManager.getAliveUsers();
                    TrustManagerService.this.mHandler.runWithScissors(new Runnable() { // from class: com.android.server.trust.TrustManagerService.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            printWriter.println("Trust manager state:");
                            for (UserInfo userInfo : aliveUsers) {
                                trustITrustManager$StubC17001 trustitrustmanager_stubc17001 = trustITrustManager$StubC17001.this;
                                trustitrustmanager_stubc17001.dumpUser(printWriter, userInfo, userInfo.id == TrustManagerService.this.mCurrentUser);
                            }
                        }
                    }, 1500L);
                }
            }
        }

        public final void dumpUser(PrintWriter printWriter, UserInfo userInfo, boolean z) {
            printWriter.printf(" User \"%s\" (id=%d, flags=%#x)", userInfo.name, Integer.valueOf(userInfo.id), Integer.valueOf(userInfo.flags));
            if (!userInfo.supportsSwitchToByUser()) {
                printWriter.println("(managed profile)");
                printWriter.println("   disabled because switching to this user is not possible.");
                return;
            }
            if (z) {
                printWriter.print(" (current)");
            }
            printWriter.print(": trusted=" + dumpBool(TrustManagerService.this.aggregateIsTrusted(userInfo.id)));
            printWriter.print(", trustManaged=" + dumpBool(TrustManagerService.this.aggregateIsTrustManaged(userInfo.id)));
            printWriter.print(", deviceLocked=" + dumpBool(TrustManagerService.this.isDeviceLockedInner(userInfo.id)));
            printWriter.print(", strongAuthRequired=" + dumpHex(TrustManagerService.this.mStrongAuthTracker.getStrongAuthForUser(userInfo.id)));
            printWriter.println();
            printWriter.println("   Enabled agents:");
            ArraySet arraySet = new ArraySet();
            Iterator it = TrustManagerService.this.mActiveAgents.iterator();
            boolean z2 = false;
            while (true) {
                boolean z3 = z2;
                while (it.hasNext()) {
                    AgentInfo agentInfo = (AgentInfo) it.next();
                    if (agentInfo.userId == userInfo.id) {
                        boolean isTrusted = agentInfo.agent.isTrusted();
                        printWriter.print("    ");
                        printWriter.println(agentInfo.component.flattenToShortString());
                        printWriter.print("     bound=" + dumpBool(agentInfo.agent.isBound()));
                        printWriter.print(", connected=" + dumpBool(agentInfo.agent.isConnected()));
                        printWriter.print(", managingTrust=" + dumpBool(agentInfo.agent.isManagingTrust()));
                        printWriter.print(", trusted=" + dumpBool(isTrusted));
                        printWriter.println();
                        if (isTrusted) {
                            printWriter.println("      message=\"" + ((Object) agentInfo.agent.getMessage()) + "\"");
                        }
                        if (!agentInfo.agent.isConnected()) {
                            String formatDuration = TrustArchive.formatDuration(agentInfo.agent.getScheduledRestartUptimeMillis() - SystemClock.uptimeMillis());
                            printWriter.println("      restartScheduledAt=" + formatDuration);
                        }
                        if (!arraySet.add(TrustArchive.getSimpleName(agentInfo.component))) {
                            break;
                        }
                    }
                }
                printWriter.println("   Events:");
                TrustManagerService.this.mArchive.dump(printWriter, 50, userInfo.id, "    ", z3);
                printWriter.println();
                return;
                z2 = true;
            }
        }

        public final String dumpHex(int i) {
            return "0x" + Integer.toHexString(i);
        }

        public void setDeviceLockedForUser(int i, boolean z) {
            enforceReportPermission();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (TrustManagerService.this.mLockPatternUtils.isSeparateProfileChallengeEnabled(i) && TrustManagerService.this.mLockPatternUtils.isSecure(i)) {
                    synchronized (TrustManagerService.this.mDeviceLockedForUser) {
                        TrustManagerService.this.mDeviceLockedForUser.put(i, z);
                    }
                    Authorization.onLockScreenEvent(z, i, (byte[]) null, TrustManagerService.this.getBiometricSids(i));
                    if (z) {
                        try {
                            ActivityManager.getService().notifyLockedProfile(i);
                        } catch (RemoteException unused) {
                        }
                    }
                    Intent intent = new Intent("android.intent.action.DEVICE_LOCKED_CHANGED");
                    intent.addFlags(1073741824);
                    intent.putExtra("android.intent.extra.user_handle", i);
                    TrustManagerService.this.mContext.sendBroadcastAsUser(intent, UserHandle.SYSTEM, "android.permission.TRUST_LISTENER", null);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @EnforcePermission("android.permission.TRUST_LISTENER")
        public boolean isTrustUsuallyManaged(int i) {
            super.isTrustUsuallyManaged_enforcePermission();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return TrustManagerService.this.isTrustUsuallyManagedInternal(i);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void unlockedByBiometricForUser(int i, BiometricSourceType biometricSourceType) {
            enforceReportPermission();
            synchronized (TrustManagerService.this.mUsersUnlockedByBiometric) {
                TrustManagerService.this.mUsersUnlockedByBiometric.put(i, true);
            }
            TrustManagerService.this.mHandler.obtainMessage(14, i, TrustManagerService.this.mSettingsObserver.getTrustAgentsNonrenewableTrust() ? 1 : 0).sendToTarget();
            TrustManagerService.this.mHandler.obtainMessage(17, Integer.valueOf(i)).sendToTarget();
        }

        public void clearAllBiometricRecognized(BiometricSourceType biometricSourceType, int i) {
            enforceReportPermission();
            synchronized (TrustManagerService.this.mUsersUnlockedByBiometric) {
                TrustManagerService.this.mUsersUnlockedByBiometric.clear();
            }
            Message obtainMessage = TrustManagerService.this.mHandler.obtainMessage(14, -1, 0);
            if (i >= 0) {
                Bundle bundle = new Bundle();
                bundle.putInt("except", i);
                obtainMessage.setData(bundle);
            }
            obtainMessage.sendToTarget();
        }
    }

    public final boolean isTrustUsuallyManagedInternal(int i) {
        synchronized (this.mTrustUsuallyManagedForUser) {
            int indexOfKey = this.mTrustUsuallyManagedForUser.indexOfKey(i);
            if (indexOfKey >= 0) {
                return this.mTrustUsuallyManagedForUser.valueAt(indexOfKey);
            }
            boolean isTrustUsuallyManaged = this.mLockPatternUtils.isTrustUsuallyManaged(i);
            synchronized (this.mTrustUsuallyManagedForUser) {
                int indexOfKey2 = this.mTrustUsuallyManagedForUser.indexOfKey(i);
                if (indexOfKey2 >= 0) {
                    return this.mTrustUsuallyManagedForUser.valueAt(indexOfKey2);
                }
                this.mTrustUsuallyManagedForUser.put(i, isTrustUsuallyManaged);
                return isTrustUsuallyManaged;
            }
        }
    }

    public final int resolveProfileParent(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UserInfo profileParent = this.mUserManager.getProfileParent(i);
            return profileParent != null ? profileParent.getUserHandle().getIdentifier() : i;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* loaded from: classes2.dex */
    public static class SettingsAttrs {
        public boolean canUnlockProfile;
        public ComponentName componentName;

        public SettingsAttrs(ComponentName componentName, boolean z) {
            this.componentName = componentName;
            this.canUnlockProfile = z;
        }
    }

    /* loaded from: classes2.dex */
    public class Receiver extends BroadcastReceiver {
        public Receiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int userId;
            String action = intent.getAction();
            if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(action)) {
                TrustManagerService.this.refreshAgentList(getSendingUserId());
                TrustManagerService.this.updateDevicePolicyFeatures();
            } else if ("android.intent.action.USER_ADDED".equals(action) || "android.intent.action.USER_STARTED".equals(action)) {
                int userId2 = getUserId(intent);
                if (userId2 > 0) {
                    TrustManagerService.this.maybeEnableFactoryTrustAgents(userId2);
                }
            } else if (!"android.intent.action.USER_REMOVED".equals(action) || (userId = getUserId(intent)) <= 0) {
            } else {
                synchronized (TrustManagerService.this.mUserIsTrusted) {
                    TrustManagerService.this.mUserIsTrusted.delete(userId);
                }
                synchronized (TrustManagerService.this.mDeviceLockedForUser) {
                    TrustManagerService.this.mDeviceLockedForUser.delete(userId);
                }
                synchronized (TrustManagerService.this.mTrustUsuallyManagedForUser) {
                    TrustManagerService.this.mTrustUsuallyManagedForUser.delete(userId);
                }
                synchronized (TrustManagerService.this.mUsersUnlockedByBiometric) {
                    TrustManagerService.this.mUsersUnlockedByBiometric.delete(userId);
                }
                TrustManagerService.this.refreshAgentList(userId);
                TrustManagerService.this.refreshDeviceLockedForUser(userId);
            }
        }

        public final int getUserId(Intent intent) {
            int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -100);
            if (intExtra > 0) {
                return intExtra;
            }
            Log.w("TrustManagerService", "EXTRA_USER_HANDLE missing or invalid, value=" + intExtra);
            return -100;
        }

        public void register(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
            intentFilter.addAction("android.intent.action.USER_ADDED");
            intentFilter.addAction("android.intent.action.USER_REMOVED");
            intentFilter.addAction("android.intent.action.USER_STARTED");
            context.registerReceiverAsUser(this, UserHandle.ALL, intentFilter, null, null);
        }
    }

    /* loaded from: classes2.dex */
    public class StrongAuthTracker extends LockPatternUtils.StrongAuthTracker {
        public SparseBooleanArray mStartFromSuccessfulUnlock;

        public StrongAuthTracker(Context context) {
            super(context);
            this.mStartFromSuccessfulUnlock = new SparseBooleanArray();
        }

        public void onStrongAuthRequiredChanged(int i) {
            this.mStartFromSuccessfulUnlock.delete(i);
            if (TrustManagerService.DEBUG) {
                Log.i("TrustManagerService", "onStrongAuthRequiredChanged(" + i + ") -> trustAllowed=" + isTrustAllowedForUser(i) + " agentsCanRun=" + canAgentsRunForUser(i));
            }
            if (!isTrustAllowedForUser(i)) {
                cancelPendingAlarm((TrustTimeoutAlarmListener) TrustManagerService.this.mTrustTimeoutAlarmListenerForUser.get(Integer.valueOf(i)));
                cancelPendingAlarm((TrustTimeoutAlarmListener) TrustManagerService.this.mTrustableTimeoutAlarmListenerForUser.get(i));
                cancelPendingAlarm((TrustTimeoutAlarmListener) TrustManagerService.this.mIdleTrustableTimeoutAlarmListenerForUser.get(i));
            }
            TrustManagerService.this.refreshAgentList(i);
            TrustManagerService.this.updateTrust(i, 0);
        }

        public final void cancelPendingAlarm(TrustTimeoutAlarmListener trustTimeoutAlarmListener) {
            if (trustTimeoutAlarmListener == null || !trustTimeoutAlarmListener.isQueued()) {
                return;
            }
            trustTimeoutAlarmListener.setQueued(false);
            TrustManagerService.this.mAlarmManager.cancel(trustTimeoutAlarmListener);
        }

        public boolean canAgentsRunForUser(int i) {
            return this.mStartFromSuccessfulUnlock.get(i) || super.isTrustAllowedForUser(i);
        }

        public void allowTrustFromUnlock(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("userId must be a valid user: " + i);
            }
            boolean canAgentsRunForUser = canAgentsRunForUser(i);
            this.mStartFromSuccessfulUnlock.put(i, true);
            if (TrustManagerService.DEBUG) {
                Log.i("TrustManagerService", "allowTrustFromUnlock(" + i + ") -> trustAllowed=" + isTrustAllowedForUser(i) + " agentsCanRun=" + canAgentsRunForUser(i));
            }
            if (canAgentsRunForUser(i) != canAgentsRunForUser) {
                TrustManagerService.this.refreshAgentList(i);
            }
        }
    }

    /* loaded from: classes2.dex */
    public abstract class TrustTimeoutAlarmListener implements AlarmManager.OnAlarmListener {
        public boolean mIsQueued = false;
        public final int mUserId;

        public abstract void handleAlarm();

        public TrustTimeoutAlarmListener(int i) {
            this.mUserId = i;
        }

        @Override // android.app.AlarmManager.OnAlarmListener
        public void onAlarm() {
            this.mIsQueued = false;
            handleAlarm();
            if (TrustManagerService.this.mStrongAuthTracker.isTrustAllowedForUser(this.mUserId)) {
                if (TrustManagerService.DEBUG) {
                    Slog.d("TrustManagerService", "Revoking all trust because of trust timeout");
                }
                LockPatternUtils lockPatternUtils = TrustManagerService.this.mLockPatternUtils;
                StrongAuthTracker unused = TrustManagerService.this.mStrongAuthTracker;
                lockPatternUtils.requireStrongAuth(256, this.mUserId);
            }
            TrustManagerService.this.maybeLockScreen(this.mUserId);
        }

        public boolean isQueued() {
            return this.mIsQueued;
        }

        public void setQueued(boolean z) {
            this.mIsQueued = z;
        }
    }

    /* loaded from: classes2.dex */
    public class TrustedTimeoutAlarmListener extends TrustTimeoutAlarmListener {
        public TrustedTimeoutAlarmListener(int i) {
            super(i);
        }

        @Override // com.android.server.trust.TrustManagerService.TrustTimeoutAlarmListener
        public void handleAlarm() {
            if (TrustManagerService.ENABLE_ACTIVE_UNLOCK_FLAG) {
                TrustableTimeoutAlarmListener trustableTimeoutAlarmListener = (TrustableTimeoutAlarmListener) TrustManagerService.this.mTrustableTimeoutAlarmListenerForUser.get(this.mUserId);
                if (trustableTimeoutAlarmListener != null && trustableTimeoutAlarmListener.isQueued()) {
                    synchronized (TrustManagerService.this.mAlarmLock) {
                        disableNonrenewableTrustWhileRenewableTrustIsPresent();
                    }
                }
            }
        }

        public final void disableNonrenewableTrustWhileRenewableTrustIsPresent() {
            synchronized (TrustManagerService.this.mUserTrustState) {
                if (TrustManagerService.this.mUserTrustState.get(this.mUserId) == TrustState.TRUSTED) {
                    TrustManagerService.this.mUserTrustState.put(this.mUserId, TrustState.TRUSTABLE);
                    TrustManagerService.this.updateTrust(this.mUserId, 0);
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public class TrustableTimeoutAlarmListener extends TrustTimeoutAlarmListener {
        public TrustableTimeoutAlarmListener(int i) {
            super(i);
        }

        @Override // com.android.server.trust.TrustManagerService.TrustTimeoutAlarmListener
        public void handleAlarm() {
            if (TrustManagerService.ENABLE_ACTIVE_UNLOCK_FLAG) {
                cancelBothTrustableAlarms();
                TrustedTimeoutAlarmListener trustedTimeoutAlarmListener = (TrustedTimeoutAlarmListener) TrustManagerService.this.mTrustTimeoutAlarmListenerForUser.get(Integer.valueOf(this.mUserId));
                if (trustedTimeoutAlarmListener != null && trustedTimeoutAlarmListener.isQueued()) {
                    synchronized (TrustManagerService.this.mAlarmLock) {
                        disableRenewableTrustWhileNonrenewableTrustIsPresent();
                    }
                }
            }
        }

        public final void cancelBothTrustableAlarms() {
            TrustableTimeoutAlarmListener trustableTimeoutAlarmListener = (TrustableTimeoutAlarmListener) TrustManagerService.this.mIdleTrustableTimeoutAlarmListenerForUser.get(this.mUserId);
            TrustableTimeoutAlarmListener trustableTimeoutAlarmListener2 = (TrustableTimeoutAlarmListener) TrustManagerService.this.mTrustableTimeoutAlarmListenerForUser.get(this.mUserId);
            if (trustableTimeoutAlarmListener != null && trustableTimeoutAlarmListener.isQueued()) {
                trustableTimeoutAlarmListener.setQueued(false);
                TrustManagerService.this.mAlarmManager.cancel(trustableTimeoutAlarmListener);
            }
            if (trustableTimeoutAlarmListener2 == null || !trustableTimeoutAlarmListener2.isQueued()) {
                return;
            }
            trustableTimeoutAlarmListener2.setQueued(false);
            TrustManagerService.this.mAlarmManager.cancel(trustableTimeoutAlarmListener2);
        }

        public final void disableRenewableTrustWhileNonrenewableTrustIsPresent() {
            Iterator it = TrustManagerService.this.mActiveAgents.iterator();
            while (it.hasNext()) {
                ((AgentInfo) it.next()).agent.setUntrustable();
            }
            TrustManagerService.this.updateTrust(this.mUserId, 0);
        }
    }
}
