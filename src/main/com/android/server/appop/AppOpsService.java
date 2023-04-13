package com.android.server.appop;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.AppOpsManagerInternal;
import android.app.AsyncNotedAppOp;
import android.app.RuntimeAppOpAccessMessage;
import android.app.SyncNotedAppOp;
import android.app.admin.DevicePolicyManagerInternal;
import android.content.AttributionSource;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.IBinder;
import android.os.PackageTagsList;
import android.os.Process;
import android.os.RemoteCallback;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.storage.StorageManagerInternal;
import android.permission.PermissionManager;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.KeyValueListParser;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.Immutable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IAppOpsActiveCallback;
import com.android.internal.app.IAppOpsAsyncNotedCallback;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsNotedCallback;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IAppOpsStartedCallback;
import com.android.internal.app.MessageSamplingConfig;
import com.android.internal.compat.IPlatformCompat;
import com.android.internal.os.Clock;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.internal.util.function.DodecConsumer;
import com.android.internal.util.function.HeptFunction;
import com.android.internal.util.function.HexFunction;
import com.android.internal.util.function.NonaConsumer;
import com.android.internal.util.function.OctConsumer;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.QuadFunction;
import com.android.internal.util.function.QuintConsumer;
import com.android.internal.util.function.QuintFunction;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.UndecConsumer;
import com.android.internal.util.function.UndecFunction;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.LocalManagerRegistry;
import com.android.server.LocalServices;
import com.android.server.LockGuard;
import com.android.server.SystemServerInitThreadPool;
import com.android.server.appop.AppOpsService;
import com.android.server.appop.AppOpsUidStateTracker;
import com.android.server.appop.AttributedOp;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageState;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.component.ParsedAttribution;
import com.android.server.policy.AppOpsPolicy;
import dalvik.annotation.optimization.NeverCompile;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AppOpsService extends IAppOpsService.Stub {
    public static final int[] OPS_RESTRICTED_ON_SUSPEND = {28, 27, 26, 3};
    public final DevicePolicyManagerInternal dpmi;
    @GuardedBy({"this"})
    public int mAcceptableLeftDistance;
    public final ArrayMap<IBinder, SparseArray<ActiveCallback>> mActiveWatchers;
    public ActivityManagerInternal mActivityManagerInternal;
    @VisibleForTesting
    AppOpsCheckingServiceInterface mAppOpsCheckingService;
    public final AppOpsManagerInternalImpl mAppOpsManagerInternal;
    @VisibleForTesting
    AppOpsRestrictions mAppOpsRestrictions;
    @GuardedBy({"this"})
    public final ArrayMap<Pair<String, Integer>, RemoteCallbackList<IAppOpsAsyncNotedCallback>> mAsyncOpWatchers;
    public final AudioRestrictionManager mAudioRestrictionManager;
    public volatile CheckOpsDelegateDispatcher mCheckOpsDelegateDispatcher;
    @GuardedBy({"this"})
    public RuntimeAppOpAccessMessage mCollectedRuntimePermissionMessage;
    @VisibleForTesting
    final Constants mConstants;
    public final Context mContext;
    public boolean mFastWriteScheduled;
    public final Handler mHandler;
    public volatile HistoricalRegistry mHistoricalRegistry;
    @GuardedBy({"this"})
    public final AttributedOp.InProgressStartOpEventPool mInProgressStartOpEventPool;
    @GuardedBy({"this"})
    public float mMessagesCollectedCount;
    public final ArrayMap<IBinder, ModeCallback> mModeWatchers;
    public final ArraySet<Object> mNoteOpCallerStacktraces = new ArraySet<>();
    public final File mNoteOpCallerStacktracesFile;
    public final ArrayMap<IBinder, SparseArray<NotedCallback>> mNotedWatchers;
    public BroadcastReceiver mOnPackageUpdatedReceiver;
    @GuardedBy({"this"})
    public final AttributedOp.OpEventProxyInfoPool mOpEventProxyInfoPool;
    public final ArrayMap<IBinder, ClientGlobalRestrictionState> mOpGlobalRestrictions;
    public final ArrayMap<IBinder, ClientUserRestrictionState> mOpUserRestrictions;
    public PackageManagerInternal mPackageManagerInternal;
    public final IPlatformCompat mPlatformCompat;
    public SparseIntArray mProfileOwners;
    @GuardedBy({"this"})
    public ArraySet<String> mRarelyUsedPackages;
    public final AtomicFile mRecentAccessesFile;
    @GuardedBy({"this"})
    public int mSampledAppOpCode;
    @GuardedBy({"this"})
    public String mSampledPackage;
    @GuardedBy({"this"})
    public int mSamplingStrategy;
    public final ArrayMap<IBinder, SparseArray<StartedCallback>> mStartedWatchers;
    public final AtomicFile mStorageFile;
    public final SparseArray<int[]> mSwitchedOps;
    public AppOpsUidStateTracker mUidStateTracker;
    @GuardedBy({"this"})
    @VisibleForTesting
    final SparseArray<UidState> mUidStates;
    @GuardedBy({"this"})
    public final ArrayMap<Pair<String, Integer>, ArrayList<AsyncNotedAppOp>> mUnforwardedAsyncNotedOps;
    public UserManagerInternal mUserManagerInternal;
    public final Runnable mWriteRunner;
    public boolean mWriteScheduled;

    /* renamed from: -$$Nest$mcheckAudioOperationImpl */
    public static /* bridge */ /* synthetic */ int m1607$$Nest$mcheckAudioOperationImpl(AppOpsService appOpsService, int i, int i2, int i3, String str) {
        return appOpsService.checkAudioOperationImpl(i, i2, i3, str);
    }

    /* renamed from: -$$Nest$mfinishOperationImpl */
    public static /* bridge */ /* synthetic */ void m1609$$Nest$mfinishOperationImpl(AppOpsService appOpsService, IBinder iBinder, int i, int i2, String str, String str2) {
        appOpsService.finishOperationImpl(iBinder, i, i2, str, str2);
    }

    /* renamed from: -$$Nest$mnoteOperationImpl */
    public static /* bridge */ /* synthetic */ SyncNotedAppOp m1618$$Nest$mnoteOperationImpl(AppOpsService appOpsService, int i, int i2, String str, String str2, boolean z, String str3, boolean z2) {
        return appOpsService.noteOperationImpl(i, i2, str, str2, z, str3, z2);
    }

    public void collectNoteOpCallsForValidation(String str, int i, String str2, long j) {
    }

    public final boolean shouldStartForMode(int i, boolean z) {
        return i == 0 || (i == 3 && z);
    }

    @GuardedBy({"this"})
    public AppOpsUidStateTracker getUidStateTracker() {
        if (this.mUidStateTracker == null) {
            AppOpsUidStateTrackerImpl appOpsUidStateTrackerImpl = new AppOpsUidStateTrackerImpl((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class), this.mHandler, new Executor() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda10
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    AppOpsService.this.lambda$getUidStateTracker$0(runnable);
                }
            }, Clock.SYSTEM_CLOCK, this.mConstants);
            this.mUidStateTracker = appOpsUidStateTrackerImpl;
            appOpsUidStateTrackerImpl.addUidStateChangedCallback(new HandlerExecutor(this.mHandler), new AppOpsUidStateTracker.UidStateChangedCallback() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda11
                @Override // com.android.server.appop.AppOpsUidStateTracker.UidStateChangedCallback
                public final void onUidStateChanged(int i, int i2, boolean z) {
                    AppOpsService.this.onUidStateChanged(i, i2, z);
                }
            });
        }
        return this.mUidStateTracker;
    }

    public /* synthetic */ void lambda$getUidStateTracker$0(Runnable runnable) {
        synchronized (this) {
            runnable.run();
        }
    }

    /* loaded from: classes.dex */
    public final class Constants extends ContentObserver {
        public long BG_STATE_SETTLE_TIME;
        public long FG_SERVICE_STATE_SETTLE_TIME;
        public long TOP_STATE_SETTLE_TIME;
        public final KeyValueListParser mParser;
        public ContentResolver mResolver;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public Constants(Handler handler) {
            super(handler);
            AppOpsService.this = r1;
            this.mParser = new KeyValueListParser(',');
            updateConstants();
        }

        public void startMonitoring(ContentResolver contentResolver) {
            this.mResolver = contentResolver;
            contentResolver.registerContentObserver(Settings.Global.getUriFor("app_ops_constants"), false, this);
            updateConstants();
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            updateConstants();
        }

        public final void updateConstants() {
            ContentResolver contentResolver = this.mResolver;
            String string = contentResolver != null ? Settings.Global.getString(contentResolver, "app_ops_constants") : "";
            synchronized (AppOpsService.this) {
                try {
                    this.mParser.setString(string);
                } catch (IllegalArgumentException e) {
                    Slog.e("AppOps", "Bad app ops settings", e);
                }
                this.TOP_STATE_SETTLE_TIME = this.mParser.getDurationMillis("top_state_settle_time", 5000L);
                this.FG_SERVICE_STATE_SETTLE_TIME = this.mParser.getDurationMillis("fg_service_state_settle_time", 5000L);
                this.BG_STATE_SETTLE_TIME = this.mParser.getDurationMillis("bg_state_settle_time", 1000L);
            }
        }

        public void dump(PrintWriter printWriter) {
            printWriter.println("  Settings:");
            printWriter.print("    ");
            printWriter.print("top_state_settle_time");
            printWriter.print("=");
            TimeUtils.formatDuration(this.TOP_STATE_SETTLE_TIME, printWriter);
            printWriter.println();
            printWriter.print("    ");
            printWriter.print("fg_service_state_settle_time");
            printWriter.print("=");
            TimeUtils.formatDuration(this.FG_SERVICE_STATE_SETTLE_TIME, printWriter);
            printWriter.println();
            printWriter.print("    ");
            printWriter.print("bg_state_settle_time");
            printWriter.print("=");
            TimeUtils.formatDuration(this.BG_STATE_SETTLE_TIME, printWriter);
            printWriter.println();
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public final class UidState {
        public SparseBooleanArray foregroundOps;
        public boolean hasForegroundWatchers;
        public final ArrayMap<String, Ops> pkgOps = new ArrayMap<>();
        public final int uid;

        public UidState(int i) {
            AppOpsService.this = r1;
            this.uid = i;
        }

        public void clear() {
            AppOpsService.this.mAppOpsCheckingService.removeUid(this.uid);
            for (int i = 0; i < this.pkgOps.size(); i++) {
                AppOpsService.this.mAppOpsCheckingService.removePackage(this.pkgOps.keyAt(i), UserHandle.getUserId(this.uid));
            }
        }

        public SparseIntArray getNonDefaultUidModes() {
            return AppOpsService.this.mAppOpsCheckingService.getNonDefaultUidModes(this.uid);
        }

        public int getUidMode(int i) {
            return AppOpsService.this.mAppOpsCheckingService.getUidMode(this.uid, i);
        }

        public boolean setUidMode(int i, int i2) {
            return AppOpsService.this.mAppOpsCheckingService.setUidMode(this.uid, i, i2);
        }

        public int evalMode(int i, int i2) {
            return AppOpsService.this.getUidStateTracker().evalMode(this.uid, i, i2);
        }

        public void evalForegroundOps() {
            this.foregroundOps = null;
            this.foregroundOps = AppOpsService.this.mAppOpsCheckingService.evalForegroundUidOps(this.uid, null);
            for (int size = this.pkgOps.size() - 1; size >= 0; size--) {
                this.foregroundOps = AppOpsService.this.mAppOpsCheckingService.evalForegroundPackageOps(this.pkgOps.valueAt(size).packageName, this.foregroundOps, UserHandle.getUserId(this.uid));
            }
            this.hasForegroundWatchers = false;
            if (this.foregroundOps != null) {
                for (int i = 0; i < this.foregroundOps.size(); i++) {
                    if (this.foregroundOps.valueAt(i)) {
                        this.hasForegroundWatchers = true;
                        return;
                    }
                }
            }
        }

        public int getState() {
            return AppOpsService.this.getUidStateTracker().getUidState(this.uid);
        }

        public void dump(PrintWriter printWriter, long j) {
            AppOpsService.this.getUidStateTracker().dumpUidState(printWriter, this.uid, j);
        }
    }

    /* loaded from: classes.dex */
    public static final class Ops extends SparseArray<C0464Op> {
        public AppOpsManager.RestrictionBypass bypass;
        public final String packageName;
        public final UidState uidState;
        public final ArraySet<String> knownAttributionTags = new ArraySet<>();
        public final ArraySet<String> validAttributionTags = new ArraySet<>();

        public Ops(String str, UidState uidState) {
            this.packageName = str;
            this.uidState = uidState;
        }
    }

    /* loaded from: classes.dex */
    public static final class PackageVerificationResult {
        public final AppOpsManager.RestrictionBypass bypass;
        public final boolean isAttributionTagValid;

        public PackageVerificationResult(AppOpsManager.RestrictionBypass restrictionBypass, boolean z) {
            this.bypass = restrictionBypass;
            this.isAttributionTagValid = z;
        }
    }

    /* renamed from: com.android.server.appop.AppOpsService$Op */
    /* loaded from: classes.dex */
    public final class C0464Op {
        public final ArrayMap<String, AttributedOp> mAttributions = new ArrayMap<>(1);

        /* renamed from: op */
        public int f1125op;
        public final String packageName;
        public int uid;
        public final UidState uidState;

        public C0464Op(UidState uidState, String str, int i, int i2) {
            AppOpsService.this = r2;
            this.f1125op = i;
            this.uid = i2;
            this.uidState = uidState;
            this.packageName = str;
        }

        public int getMode() {
            return AppOpsService.this.mAppOpsCheckingService.getPackageMode(this.packageName, this.f1125op, UserHandle.getUserId(this.uid));
        }

        public void setMode(int i) {
            AppOpsService.this.mAppOpsCheckingService.setPackageMode(this.packageName, this.f1125op, i, UserHandle.getUserId(this.uid));
        }

        public void removeAttributionsWithNoTime() {
            for (int size = this.mAttributions.size() - 1; size >= 0; size--) {
                if (!this.mAttributions.valueAt(size).hasAnyTime()) {
                    this.mAttributions.removeAt(size);
                }
            }
        }

        public final AttributedOp getOrCreateAttribution(C0464Op c0464Op, String str) {
            AttributedOp attributedOp = this.mAttributions.get(str);
            if (attributedOp == null) {
                AttributedOp attributedOp2 = new AttributedOp(AppOpsService.this, str, c0464Op);
                this.mAttributions.put(str, attributedOp2);
                return attributedOp2;
            }
            return attributedOp;
        }

        public AppOpsManager.OpEntry createEntryLocked() {
            int size = this.mAttributions.size();
            ArrayMap arrayMap = new ArrayMap(size);
            for (int i = 0; i < size; i++) {
                arrayMap.put(this.mAttributions.keyAt(i), this.mAttributions.valueAt(i).createAttributedOpEntryLocked());
            }
            return new AppOpsManager.OpEntry(this.f1125op, getMode(), arrayMap);
        }

        public AppOpsManager.OpEntry createSingleAttributionEntryLocked(String str) {
            int size = this.mAttributions.size();
            ArrayMap arrayMap = new ArrayMap(1);
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                } else if (Objects.equals(this.mAttributions.keyAt(i), str)) {
                    arrayMap.put(this.mAttributions.keyAt(i), this.mAttributions.valueAt(i).createAttributedOpEntryLocked());
                    break;
                } else {
                    i++;
                }
            }
            return new AppOpsManager.OpEntry(this.f1125op, getMode(), arrayMap);
        }

        public boolean isRunning() {
            int size = this.mAttributions.size();
            for (int i = 0; i < size; i++) {
                if (this.mAttributions.valueAt(i).isRunning()) {
                    return true;
                }
            }
            return false;
        }
    }

    /* loaded from: classes.dex */
    public final class ModeCallback extends OnOpModeChangedListener implements IBinder.DeathRecipient {
        public final IAppOpsCallback mCallback;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ModeCallback(IAppOpsCallback iAppOpsCallback, int i, int i2, int i3, int i4, int i5) {
            super(i, i2, i3, i4, i5);
            AppOpsService.this = r7;
            this.mCallback = iAppOpsCallback;
            try {
                iAppOpsCallback.asBinder().linkToDeath(this, 0);
            } catch (RemoteException unused) {
            }
        }

        @Override // com.android.server.appop.OnOpModeChangedListener
        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("ModeCallback{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" watchinguid=");
            UserHandle.formatUid(sb, getWatchingUid());
            sb.append(" flags=0x");
            sb.append(Integer.toHexString(getFlags()));
            int watchedOpCode = getWatchedOpCode();
            if (watchedOpCode == -2) {
                sb.append(" op=(all)");
            } else if (watchedOpCode != -1) {
                sb.append(" op=");
                sb.append(AppOpsManager.opToName(getWatchedOpCode()));
            }
            sb.append(" from uid=");
            UserHandle.formatUid(sb, getCallingUid());
            sb.append(" pid=");
            sb.append(getCallingPid());
            sb.append('}');
            return sb.toString();
        }

        public void unlinkToDeath() {
            this.mCallback.asBinder().unlinkToDeath(this, 0);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            AppOpsService.this.stopWatchingMode(this.mCallback);
        }

        @Override // com.android.server.appop.OnOpModeChangedListener
        public void onOpModeChanged(int i, int i2, String str) throws RemoteException {
            this.mCallback.opChanged(i, i2, str);
        }
    }

    /* loaded from: classes.dex */
    public final class ActiveCallback implements IBinder.DeathRecipient {
        public final IAppOpsActiveCallback mCallback;
        public final int mCallingPid;
        public final int mCallingUid;
        public final int mWatchingUid;

        public ActiveCallback(IAppOpsActiveCallback iAppOpsActiveCallback, int i, int i2, int i3) {
            AppOpsService.this = r1;
            this.mCallback = iAppOpsActiveCallback;
            this.mWatchingUid = i;
            this.mCallingUid = i2;
            this.mCallingPid = i3;
            try {
                iAppOpsActiveCallback.asBinder().linkToDeath(this, 0);
            } catch (RemoteException unused) {
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("ActiveCallback{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" watchinguid=");
            UserHandle.formatUid(sb, this.mWatchingUid);
            sb.append(" from uid=");
            UserHandle.formatUid(sb, this.mCallingUid);
            sb.append(" pid=");
            sb.append(this.mCallingPid);
            sb.append('}');
            return sb.toString();
        }

        public void destroy() {
            this.mCallback.asBinder().unlinkToDeath(this, 0);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            AppOpsService.this.stopWatchingActive(this.mCallback);
        }
    }

    /* loaded from: classes.dex */
    public final class StartedCallback implements IBinder.DeathRecipient {
        public final IAppOpsStartedCallback mCallback;
        public final int mCallingPid;
        public final int mCallingUid;
        public final int mWatchingUid;

        public StartedCallback(IAppOpsStartedCallback iAppOpsStartedCallback, int i, int i2, int i3) {
            AppOpsService.this = r1;
            this.mCallback = iAppOpsStartedCallback;
            this.mWatchingUid = i;
            this.mCallingUid = i2;
            this.mCallingPid = i3;
            try {
                iAppOpsStartedCallback.asBinder().linkToDeath(this, 0);
            } catch (RemoteException unused) {
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("StartedCallback{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" watchinguid=");
            UserHandle.formatUid(sb, this.mWatchingUid);
            sb.append(" from uid=");
            UserHandle.formatUid(sb, this.mCallingUid);
            sb.append(" pid=");
            sb.append(this.mCallingPid);
            sb.append('}');
            return sb.toString();
        }

        public void destroy() {
            this.mCallback.asBinder().unlinkToDeath(this, 0);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            AppOpsService.this.stopWatchingStarted(this.mCallback);
        }
    }

    /* loaded from: classes.dex */
    public final class NotedCallback implements IBinder.DeathRecipient {
        public final IAppOpsNotedCallback mCallback;
        public final int mCallingPid;
        public final int mCallingUid;
        public final int mWatchingUid;

        public NotedCallback(IAppOpsNotedCallback iAppOpsNotedCallback, int i, int i2, int i3) {
            AppOpsService.this = r1;
            this.mCallback = iAppOpsNotedCallback;
            this.mWatchingUid = i;
            this.mCallingUid = i2;
            this.mCallingPid = i3;
            try {
                iAppOpsNotedCallback.asBinder().linkToDeath(this, 0);
            } catch (RemoteException unused) {
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("NotedCallback{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" watchinguid=");
            UserHandle.formatUid(sb, this.mWatchingUid);
            sb.append(" from uid=");
            UserHandle.formatUid(sb, this.mCallingUid);
            sb.append(" pid=");
            sb.append(this.mCallingPid);
            sb.append('}');
            return sb.toString();
        }

        public void destroy() {
            this.mCallback.asBinder().unlinkToDeath(this, 0);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            AppOpsService.this.stopWatchingNoted(this.mCallback);
        }
    }

    public static void onClientDeath(AttributedOp attributedOp, IBinder iBinder) {
        attributedOp.onClientDeath(iBinder);
    }

    @VisibleForTesting
    public AppOpsService(File file, File file2, Handler handler, Context context) {
        AttributedOp.OpEventProxyInfoPool opEventProxyInfoPool = new AttributedOp.OpEventProxyInfoPool(3);
        this.mOpEventProxyInfoPool = opEventProxyInfoPool;
        this.mInProgressStartOpEventPool = new AttributedOp.InProgressStartOpEventPool(opEventProxyInfoPool, 3);
        this.mAppOpsManagerInternal = new AppOpsManagerInternalImpl();
        this.dpmi = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
        this.mPlatformCompat = IPlatformCompat.Stub.asInterface(ServiceManager.getService("platform_compat"));
        this.mAsyncOpWatchers = new ArrayMap<>();
        this.mUnforwardedAsyncNotedOps = new ArrayMap<>();
        this.mWriteRunner = new Runnable() { // from class: com.android.server.appop.AppOpsService.1
            {
                AppOpsService.this = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                synchronized (AppOpsService.this) {
                    AppOpsService appOpsService = AppOpsService.this;
                    appOpsService.mWriteScheduled = false;
                    appOpsService.mFastWriteScheduled = false;
                    new AsyncTask<Void, Void, Void>() { // from class: com.android.server.appop.AppOpsService.1.1
                        {
                            RunnableC04541.this = this;
                        }

                        @Override // android.os.AsyncTask
                        public Void doInBackground(Void... voidArr) {
                            AppOpsService.this.writeRecentAccesses();
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                }
            }
        };
        this.mUidStates = new SparseArray<>();
        this.mHistoricalRegistry = new HistoricalRegistry(this);
        this.mOpUserRestrictions = new ArrayMap<>();
        this.mOpGlobalRestrictions = new ArrayMap<>();
        this.mCheckOpsDelegateDispatcher = new CheckOpsDelegateDispatcher(null, null);
        this.mSwitchedOps = new SparseArray<>();
        this.mSampledPackage = null;
        this.mSampledAppOpCode = -1;
        this.mAcceptableLeftDistance = 0;
        this.mRarelyUsedPackages = new ArraySet<>();
        this.mModeWatchers = new ArrayMap<>();
        this.mActiveWatchers = new ArrayMap<>();
        this.mStartedWatchers = new ArrayMap<>();
        this.mNotedWatchers = new ArrayMap<>();
        this.mAudioRestrictionManager = new AudioRestrictionManager();
        this.mOnPackageUpdatedReceiver = new BroadcastReceiver() { // from class: com.android.server.appop.AppOpsService.2
            {
                AppOpsService.this = this;
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                AndroidPackage androidPackage;
                String action = intent.getAction();
                String encodedSchemeSpecificPart = intent.getData().getEncodedSchemeSpecificPart();
                int intExtra = intent.getIntExtra("android.intent.extra.UID", -1);
                if (!action.equals("android.intent.action.PACKAGE_REPLACED") || (androidPackage = AppOpsService.this.getPackageManagerInternal().getPackage(encodedSchemeSpecificPart)) == null) {
                    return;
                }
                ArrayMap arrayMap = new ArrayMap();
                ArraySet arraySet = new ArraySet();
                arraySet.add(null);
                if (androidPackage.getAttributions() != null) {
                    int size = androidPackage.getAttributions().size();
                    for (int i = 0; i < size; i++) {
                        ParsedAttribution parsedAttribution = androidPackage.getAttributions().get(i);
                        arraySet.add(parsedAttribution.getTag());
                        int size2 = parsedAttribution.getInheritFrom().size();
                        for (int i2 = 0; i2 < size2; i2++) {
                            arrayMap.put(parsedAttribution.getInheritFrom().get(i2), parsedAttribution.getTag());
                        }
                    }
                }
                synchronized (AppOpsService.this) {
                    UidState uidState = AppOpsService.this.mUidStates.get(intExtra);
                    if (uidState == null) {
                        return;
                    }
                    Ops ops = uidState.pkgOps.get(encodedSchemeSpecificPart);
                    if (ops == null) {
                        return;
                    }
                    ops.bypass = null;
                    ops.knownAttributionTags.clear();
                    int size3 = ops.size();
                    for (int i3 = 0; i3 < size3; i3++) {
                        C0464Op valueAt = ops.valueAt(i3);
                        for (int size4 = valueAt.mAttributions.size() - 1; size4 >= 0; size4--) {
                            String keyAt = valueAt.mAttributions.keyAt(size4);
                            if (!arraySet.contains(keyAt)) {
                                valueAt.getOrCreateAttribution(valueAt, (String) arrayMap.get(keyAt)).add(valueAt.mAttributions.valueAt(size4));
                                valueAt.mAttributions.removeAt(size4);
                                AppOpsService.this.scheduleFastWriteLocked();
                            }
                        }
                    }
                }
            }
        };
        this.mContext = context;
        for (int i = 0; i < 134; i++) {
            int opToSwitch = AppOpsManager.opToSwitch(i);
            SparseArray<int[]> sparseArray = this.mSwitchedOps;
            sparseArray.put(opToSwitch, ArrayUtils.appendInt(sparseArray.get(opToSwitch), i));
        }
        AppOpsCheckingServiceTracingDecorator appOpsCheckingServiceTracingDecorator = new AppOpsCheckingServiceTracingDecorator(new AppOpsCheckingServiceImpl(file2, this, handler, context, this.mSwitchedOps));
        this.mAppOpsCheckingService = appOpsCheckingServiceTracingDecorator;
        this.mAppOpsRestrictions = new AppOpsRestrictionsImpl(context, handler, appOpsCheckingServiceTracingDecorator);
        LockGuard.installLock(this, 0);
        this.mStorageFile = new AtomicFile(file2, "appops_legacy");
        this.mRecentAccessesFile = new AtomicFile(file, "appops_accesses");
        this.mNoteOpCallerStacktracesFile = null;
        this.mHandler = handler;
        this.mConstants = new Constants(handler);
        readRecentAccesses();
        this.mAppOpsCheckingService.readState();
    }

    public void publish() {
        ServiceManager.addService("appops", asBinder());
        LocalServices.addService(AppOpsManagerInternal.class, this.mAppOpsManagerInternal);
        LocalManagerRegistry.addManager(AppOpsManagerLocal.class, new AppOpsManagerLocalImpl());
    }

    public void systemReady() {
        this.mAppOpsCheckingService.systemReady();
        initializeUidStates();
        this.mConstants.startMonitoring(this.mContext.getContentResolver());
        this.mHistoricalRegistry.systemReady(this.mContext.getContentResolver());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiverAsUser(this.mOnPackageUpdatedReceiver, UserHandle.ALL, intentFilter, null, null);
        synchronized (this) {
            for (int size = this.mUidStates.size() - 1; size >= 0; size--) {
                final int keyAt = this.mUidStates.keyAt(size);
                UidState valueAt = this.mUidStates.valueAt(size);
                String[] packagesForUid = getPackagesForUid(valueAt.uid);
                if (ArrayUtils.isEmpty(packagesForUid)) {
                    valueAt.clear();
                    this.mUidStates.removeAt(size);
                    scheduleFastWriteLocked();
                } else {
                    ArrayMap<String, Ops> arrayMap = valueAt.pkgOps;
                    int size2 = arrayMap.size();
                    for (int i = 0; i < size2; i++) {
                        final String keyAt2 = arrayMap.keyAt(i);
                        final String str = !ArrayUtils.contains(packagesForUid, keyAt2) ? "android.intent.action.PACKAGE_REMOVED" : "android.intent.action.PACKAGE_REPLACED";
                        SystemServerInitThreadPool.submit(new Runnable() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda15
                            @Override // java.lang.Runnable
                            public final void run() {
                                AppOpsService.this.lambda$systemReady$1(str, keyAt2, keyAt);
                            }
                        }, "Update app-ops uidState in case package " + keyAt2 + " changed");
                    }
                }
            }
        }
        getUserManagerInternal().addUserLifecycleListener(new UserManagerInternal.UserLifecycleListener() { // from class: com.android.server.appop.AppOpsService.3
            {
                AppOpsService.this = this;
            }

            @Override // com.android.server.p011pm.UserManagerInternal.UserLifecycleListener
            public void onUserCreated(UserInfo userInfo, Object obj) {
                AppOpsService.this.initializeUserUidStates(userInfo.id);
            }
        });
        getPackageManagerInternal().getPackageList(new PackageManagerInternal.PackageListObserver() { // from class: com.android.server.appop.AppOpsService.4
            {
                AppOpsService.this = this;
            }

            @Override // android.content.p000pm.PackageManagerInternal.PackageListObserver
            public void onPackageAdded(String str2, int i2) {
                boolean isSamplingTarget = AppOpsService.this.isSamplingTarget(AppOpsService.this.getPackageManagerInternal().getPackageInfo(str2, 4096L, Process.myUid(), AppOpsService.this.mContext.getUserId()));
                int[] userIds = AppOpsService.this.getUserManagerInternal().getUserIds();
                synchronized (AppOpsService.this) {
                    if (isSamplingTarget) {
                        try {
                            AppOpsService.this.mRarelyUsedPackages.add(str2);
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                    for (int i3 : userIds) {
                        UidState uidStateLocked = AppOpsService.this.getUidStateLocked(UserHandle.getUid(i3, i2), true);
                        if (!uidStateLocked.pkgOps.containsKey(str2)) {
                            uidStateLocked.pkgOps.put(str2, new Ops(str2, uidStateLocked));
                        }
                    }
                }
            }

            @Override // android.content.p000pm.PackageManagerInternal.PackageListObserver
            public void onPackageRemoved(String str2, int i2) {
                int[] userIds = AppOpsService.this.getUserManagerInternal().getUserIds();
                synchronized (AppOpsService.this) {
                    for (int i3 : userIds) {
                        AppOpsService.this.packageRemovedLocked(UserHandle.getUid(i3, i2), str2);
                    }
                }
            }
        });
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.PACKAGES_UNSUSPENDED");
        intentFilter2.addAction("android.intent.action.PACKAGES_SUSPENDED");
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.server.appop.AppOpsService.5
            {
                AppOpsService.this = this;
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                int[] iArr;
                int[] intArrayExtra = intent.getIntArrayExtra("android.intent.extra.changed_uid_list");
                String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                for (int i2 : AppOpsService.OPS_RESTRICTED_ON_SUSPEND) {
                    synchronized (AppOpsService.this) {
                        ArraySet<OnOpModeChangedListener> opModeChangedListeners = AppOpsService.this.mAppOpsCheckingService.getOpModeChangedListeners(i2);
                        if (opModeChangedListeners != null) {
                            for (int i3 = 0; i3 < intArrayExtra.length; i3++) {
                                AppOpsService.this.notifyOpChanged(opModeChangedListeners, i2, intArrayExtra[i3], stringArrayExtra[i3]);
                            }
                        }
                    }
                }
            }
        }, UserHandle.ALL, intentFilter2, null, null);
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.appop.AppOpsService.6
            {
                AppOpsService.this = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                AppOpsService.this.initializeRarelyUsedPackagesList(new ArraySet(AppOpsService.this.getPackageListAndResample()));
            }
        }, BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
        getPackageManagerInternal().setExternalSourcesPolicy(new PackageManagerInternal.ExternalSourcesPolicy() { // from class: com.android.server.appop.AppOpsService.7
            {
                AppOpsService.this = this;
            }

            @Override // android.content.p000pm.PackageManagerInternal.ExternalSourcesPolicy
            public int getPackageTrustedToInstallApps(String str2, int i2) {
                int checkOperation = AppOpsService.this.checkOperation(66, i2, str2);
                if (checkOperation != 0) {
                    return checkOperation != 2 ? 2 : 1;
                }
                return 0;
            }
        });
        this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
    }

    public /* synthetic */ void lambda$systemReady$1(String str, String str2, int i) {
        this.mOnPackageUpdatedReceiver.onReceive(this.mContext, new Intent(str).setData(Uri.fromParts("package", str2, null)).putExtra("android.intent.extra.UID", i));
    }

    public final void initializeUidStates() {
        int[] userIds = getUserManagerInternal().getUserIds();
        synchronized (this) {
            for (int i : userIds) {
                initializeUserUidStatesLocked(i);
            }
        }
    }

    public final void initializeUserUidStates(int i) {
        synchronized (this) {
            initializeUserUidStatesLocked(i);
        }
    }

    public final void initializeUserUidStatesLocked(int i) {
        ArrayMap<String, ? extends PackageStateInternal> packageStates = getPackageManagerInternal().getPackageStates();
        for (int i2 = 0; i2 < packageStates.size(); i2++) {
            int uid = UserHandle.getUid(i, packageStates.valueAt(i2).getAppId());
            UidState uidStateLocked = getUidStateLocked(uid, true);
            String keyAt = packageStates.keyAt(i2);
            Ops ops = new Ops(keyAt, uidStateLocked);
            uidStateLocked.pkgOps.put(keyAt, ops);
            SparseIntArray nonDefaultPackageModes = this.mAppOpsCheckingService.getNonDefaultPackageModes(keyAt, i);
            for (int i3 = 0; i3 < nonDefaultPackageModes.size(); i3++) {
                int i4 = nonDefaultPackageModes.get(i3);
                ops.put(i4, new C0464Op(uidStateLocked, keyAt, i4, uid));
            }
            uidStateLocked.evalForegroundOps();
        }
    }

    public void setAppOpsPolicy(AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate) {
        CheckOpsDelegateDispatcher checkOpsDelegateDispatcher = this.mCheckOpsDelegateDispatcher;
        this.mCheckOpsDelegateDispatcher = new CheckOpsDelegateDispatcher(checkOpsDelegate, checkOpsDelegateDispatcher != null ? checkOpsDelegateDispatcher.mCheckOpsDelegate : null);
    }

    @VisibleForTesting
    public void packageRemoved(int i, String str) {
        synchronized (this) {
            packageRemovedLocked(i, str);
        }
    }

    @GuardedBy({"this"})
    public final void packageRemovedLocked(int i, String str) {
        UidState uidState = this.mUidStates.get(i);
        if (uidState == null) {
            return;
        }
        Ops remove = uidState.pkgOps.remove(str);
        this.mAppOpsCheckingService.removePackage(str, UserHandle.getUserId(i));
        if (remove != null) {
            scheduleFastWriteLocked();
            int size = remove.size();
            for (int i2 = 0; i2 < size; i2++) {
                C0464Op valueAt = remove.valueAt(i2);
                int size2 = valueAt.mAttributions.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    AttributedOp valueAt2 = valueAt.mAttributions.valueAt(i3);
                    while (valueAt2.isRunning()) {
                        valueAt2.finished(valueAt2.mInProgressEvents.keyAt(0));
                    }
                    while (valueAt2.isPaused()) {
                        valueAt2.finished(valueAt2.mPausedInProgressEvents.keyAt(0));
                    }
                }
            }
        }
        this.mHandler.post(PooledLambda.obtainRunnable(new TriConsumer() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda5
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((HistoricalRegistry) obj).clearHistory(((Integer) obj2).intValue(), (String) obj3);
            }
        }, this.mHistoricalRegistry, Integer.valueOf(i), str));
    }

    public void uidRemoved(int i) {
        synchronized (this) {
            if (this.mUidStates.indexOfKey(i) >= 0) {
                this.mUidStates.get(i).clear();
                this.mUidStates.remove(i);
                scheduleFastWriteLocked();
            }
        }
    }

    public final void onUidStateChanged(int i, int i2, boolean z) {
        ArraySet<OnOpModeChangedListener> opModeChangedListeners;
        synchronized (this) {
            UidState uidStateLocked = getUidStateLocked(i, true);
            if (uidStateLocked != null && z && uidStateLocked.hasForegroundWatchers) {
                for (int size = uidStateLocked.foregroundOps.size() - 1; size >= 0; size--) {
                    if (uidStateLocked.foregroundOps.valueAt(size)) {
                        int keyAt = uidStateLocked.foregroundOps.keyAt(size);
                        if (uidStateLocked.getUidMode(keyAt) != AppOpsManager.opToDefaultMode(keyAt) && uidStateLocked.getUidMode(keyAt) == 4) {
                            this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuintConsumer() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda9
                                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                                    ((AppOpsService) obj).notifyOpChangedForAllPkgsInUid(((Integer) obj2).intValue(), ((Integer) obj3).intValue(), ((Boolean) obj4).booleanValue(), (IAppOpsCallback) obj5);
                                }
                            }, this, Integer.valueOf(keyAt), Integer.valueOf(uidStateLocked.uid), Boolean.TRUE, (Object) null));
                        } else if (!uidStateLocked.pkgOps.isEmpty() && (opModeChangedListeners = this.mAppOpsCheckingService.getOpModeChangedListeners(keyAt)) != null) {
                            for (int size2 = opModeChangedListeners.size() - 1; size2 >= 0; size2--) {
                                OnOpModeChangedListener valueAt = opModeChangedListeners.valueAt(size2);
                                if ((valueAt.getFlags() & 1) != 0 && valueAt.isWatchingUid(uidStateLocked.uid)) {
                                    for (int size3 = uidStateLocked.pkgOps.size() - 1; size3 >= 0; size3--) {
                                        C0464Op c0464Op = uidStateLocked.pkgOps.valueAt(size3).get(keyAt);
                                        if (c0464Op != null && c0464Op.getMode() == 4) {
                                            this.mHandler.sendMessage(PooledLambda.obtainMessage(new AppOpsService$$ExternalSyntheticLambda4(), this, opModeChangedListeners.valueAt(size2), Integer.valueOf(keyAt), Integer.valueOf(uidStateLocked.uid), uidStateLocked.pkgOps.keyAt(size3)));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (uidStateLocked != null) {
                int size4 = uidStateLocked.pkgOps.size();
                for (int i3 = 0; i3 < size4; i3++) {
                    Ops valueAt2 = uidStateLocked.pkgOps.valueAt(i3);
                    int size5 = valueAt2.size();
                    for (int i4 = 0; i4 < size5; i4++) {
                        C0464Op valueAt3 = valueAt2.valueAt(i4);
                        int size6 = valueAt3.mAttributions.size();
                        for (int i5 = 0; i5 < size6; i5++) {
                            valueAt3.mAttributions.valueAt(i5).onUidStateChanged(i2);
                        }
                    }
                }
            }
        }
    }

    public void updateUidProcState(int i, int i2, int i3) {
        synchronized (this) {
            getUidStateTracker().updateUidProcState(i, i2, i3);
            if (!this.mUidStates.contains(i)) {
                this.mUidStates.put(i, new UidState(i));
                onUidStateChanged(i, AppOpsUidStateTracker.processStateToUidState(i2), false);
            }
        }
    }

    public void shutdown() {
        boolean z;
        Slog.w("AppOps", "Writing app ops before shutdown...");
        synchronized (this) {
            z = false;
            if (this.mWriteScheduled) {
                this.mWriteScheduled = false;
                this.mFastWriteScheduled = false;
                this.mHandler.removeCallbacks(this.mWriteRunner);
                z = true;
            }
        }
        if (z) {
            writeRecentAccesses();
        }
        this.mAppOpsCheckingService.shutdown();
        this.mHistoricalRegistry.shutdown();
    }

    public final ArrayList<AppOpsManager.OpEntry> collectOps(Ops ops, int[] iArr) {
        int i = 0;
        if (iArr == null) {
            ArrayList<AppOpsManager.OpEntry> arrayList = new ArrayList<>();
            while (i < ops.size()) {
                arrayList.add(getOpEntryForResult(ops.valueAt(i)));
                i++;
            }
            return arrayList;
        }
        ArrayList<AppOpsManager.OpEntry> arrayList2 = null;
        while (i < iArr.length) {
            C0464Op c0464Op = ops.get(iArr[i]);
            if (c0464Op != null) {
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList<>();
                }
                arrayList2.add(getOpEntryForResult(c0464Op));
            }
            i++;
        }
        return arrayList2;
    }

    public final ArrayList<AppOpsManager.OpEntry> collectUidOps(UidState uidState, int[] iArr) {
        int size;
        SparseIntArray nonDefaultUidModes = uidState.getNonDefaultUidModes();
        ArrayList<AppOpsManager.OpEntry> arrayList = null;
        if (nonDefaultUidModes == null || (size = nonDefaultUidModes.size()) == 0) {
            return null;
        }
        int i = 0;
        if (iArr == null) {
            arrayList = new ArrayList<>();
            while (i < size) {
                int keyAt = nonDefaultUidModes.keyAt(i);
                arrayList.add(new AppOpsManager.OpEntry(keyAt, nonDefaultUidModes.get(keyAt), Collections.emptyMap()));
                i++;
            }
        } else {
            while (i < iArr.length) {
                int i2 = iArr[i];
                if (nonDefaultUidModes.indexOfKey(i2) >= 0) {
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    arrayList.add(new AppOpsManager.OpEntry(i2, nonDefaultUidModes.get(i2), Collections.emptyMap()));
                }
                i++;
            }
        }
        return arrayList;
    }

    public static AppOpsManager.OpEntry getOpEntryForResult(C0464Op c0464Op) {
        return c0464Op.createEntryLocked();
    }

    public List<AppOpsManager.PackageOps> getPackagesForOps(int[] iArr) {
        int callingUid = Binder.getCallingUid();
        ArrayList arrayList = null;
        boolean z = this.mContext.checkPermission("android.permission.GET_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), null) == 0;
        synchronized (this) {
            int size = this.mUidStates.size();
            for (int i = 0; i < size; i++) {
                UidState valueAt = this.mUidStates.valueAt(i);
                if (!valueAt.pkgOps.isEmpty()) {
                    ArrayMap<String, Ops> arrayMap = valueAt.pkgOps;
                    int size2 = arrayMap.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        Ops valueAt2 = arrayMap.valueAt(i2);
                        ArrayList<AppOpsManager.OpEntry> collectOps = collectOps(valueAt2, iArr);
                        if (collectOps != null) {
                            if (arrayList == null) {
                                arrayList = new ArrayList();
                            }
                            AppOpsManager.PackageOps packageOps = new AppOpsManager.PackageOps(valueAt2.packageName, valueAt2.uidState.uid, collectOps);
                            if (z || callingUid == valueAt2.uidState.uid) {
                                arrayList.add(packageOps);
                            }
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    public List<AppOpsManager.PackageOps> getOpsForPackage(int i, String str, int[] iArr) {
        enforceGetAppOpsStatsPermissionIfNeeded(i, str);
        String resolvePackageName = AppOpsManager.resolvePackageName(i, str);
        if (resolvePackageName == null) {
            return Collections.emptyList();
        }
        synchronized (this) {
            Ops opsLocked = getOpsLocked(i, resolvePackageName, null, false, null, false);
            if (opsLocked == null) {
                return null;
            }
            ArrayList<AppOpsManager.OpEntry> collectOps = collectOps(opsLocked, iArr);
            if (collectOps == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(new AppOpsManager.PackageOps(opsLocked.packageName, opsLocked.uidState.uid, collectOps));
            return arrayList;
        }
    }

    public final void enforceGetAppOpsStatsPermissionIfNeeded(int i, String str) {
        int callingUid = Binder.getCallingUid();
        if (callingUid == Process.myPid()) {
            return;
        }
        if (i == callingUid && str != null && checkPackage(i, str) == 0) {
            return;
        }
        this.mContext.enforcePermission("android.permission.GET_APP_OPS_STATS", Binder.getCallingPid(), callingUid, null);
    }

    public final void ensureHistoricalOpRequestIsValid(int i, String str, String str2, List<String> list, int i2, long j, long j2, int i3) {
        if ((i2 & 1) != 0) {
            Preconditions.checkArgument(i != -1);
        } else {
            Preconditions.checkArgument(i == -1);
        }
        if ((i2 & 2) != 0) {
            Objects.requireNonNull(str);
        } else {
            Preconditions.checkArgument(str == null);
        }
        if ((i2 & 4) == 0) {
            Preconditions.checkArgument(str2 == null);
        }
        if ((i2 & 8) != 0) {
            Objects.requireNonNull(list);
        } else {
            Preconditions.checkArgument(list == null);
        }
        Preconditions.checkFlagsArgument(i2, 15);
        Preconditions.checkArgumentNonnegative(j);
        Preconditions.checkArgument(j2 > j);
        Preconditions.checkFlagsArgument(i3, 31);
    }

    /* JADX WARN: Removed duplicated region for block: B:57:0x0041  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x00b7  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x00c5  */
    /* JADX WARN: Removed duplicated region for block: B:86:0x00ca  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x00d1  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x00d4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void getHistoricalOps(int i, String str, String str2, List<String> list, int i2, int i3, long j, long j2, int i4, final RemoteCallback remoteCallback) {
        boolean z;
        PackageManager packageManager = this.mContext.getPackageManager();
        ensureHistoricalOpRequestIsValid(i, str, str2, list, i3, j, j2, i4);
        Objects.requireNonNull(remoteCallback, "callback cannot be null");
        ActivityManagerInternal activityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        if ((i3 & 1) != 0 && i == Binder.getCallingUid()) {
            z = true;
            if (!z) {
                boolean z2 = activityManagerInternal.getInstrumentationSourceUid(Binder.getCallingUid()) != -1;
                boolean z3 = Binder.getCallingPid() == Process.myPid();
                try {
                    boolean z4 = packageManager.getPackageUidAsUser(this.mContext.getPackageManager().getPermissionControllerPackageName(), 0, UserHandle.getUserId(Binder.getCallingUid())) == Binder.getCallingUid();
                    boolean z5 = this.mContext.checkPermission("android.permission.GET_HISTORICAL_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid()) == 0;
                    if (!z3 && !z2 && !z4 && !z5) {
                        this.mHandler.post(new Runnable() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda2
                            @Override // java.lang.Runnable
                            public final void run() {
                                AppOpsService.lambda$getHistoricalOps$2(remoteCallback);
                            }
                        });
                        return;
                    }
                    this.mContext.enforcePermission("android.permission.GET_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), "getHistoricalOps");
                } catch (PackageManager.NameNotFoundException unused) {
                    return;
                }
            }
            String[] strArr = list == null ? (String[]) list.toArray(new String[list.size()]) : null;
            Set indicatorExemptedPackages = (i2 & 4) == 0 ? PermissionManager.getIndicatorExemptedPackages(this.mContext) : null;
            this.mHandler.post(PooledLambda.obtainRunnable(new DodecConsumer() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda3
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12) {
                    ((HistoricalRegistry) obj).getHistoricalOps(((Integer) obj2).intValue(), (String) obj3, (String) obj4, (String[]) obj5, ((Integer) obj6).intValue(), ((Integer) obj7).intValue(), ((Long) obj8).longValue(), ((Long) obj9).longValue(), ((Integer) obj10).intValue(), (String[]) obj11, (RemoteCallback) obj12);
                }
            }, this.mHistoricalRegistry, Integer.valueOf(i), str, str2, strArr, Integer.valueOf(i2), Integer.valueOf(i3), Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(i4), indicatorExemptedPackages != null ? (String[]) indicatorExemptedPackages.toArray(new String[indicatorExemptedPackages.size()]) : null, remoteCallback).recycleOnUse());
        }
        z = false;
        if (!z) {
        }
        if (list == null) {
        }
        if ((i2 & 4) == 0) {
        }
        this.mHandler.post(PooledLambda.obtainRunnable(new DodecConsumer() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda3
            public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12) {
                ((HistoricalRegistry) obj).getHistoricalOps(((Integer) obj2).intValue(), (String) obj3, (String) obj4, (String[]) obj5, ((Integer) obj6).intValue(), ((Integer) obj7).intValue(), ((Long) obj8).longValue(), ((Long) obj9).longValue(), ((Integer) obj10).intValue(), (String[]) obj11, (RemoteCallback) obj12);
            }
        }, this.mHistoricalRegistry, Integer.valueOf(i), str, str2, strArr, Integer.valueOf(i2), Integer.valueOf(i3), Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(i4), indicatorExemptedPackages != null ? (String[]) indicatorExemptedPackages.toArray(new String[indicatorExemptedPackages.size()]) : null, remoteCallback).recycleOnUse());
    }

    public static /* synthetic */ void lambda$getHistoricalOps$2(RemoteCallback remoteCallback) {
        remoteCallback.sendResult(new Bundle());
    }

    public void getHistoricalOpsFromDiskRaw(int i, String str, String str2, List<String> list, int i2, int i3, long j, long j2, int i4, RemoteCallback remoteCallback) {
        ensureHistoricalOpRequestIsValid(i, str, str2, list, i3, j, j2, i4);
        Objects.requireNonNull(remoteCallback, "callback cannot be null");
        this.mContext.enforcePermission("android.permission.MANAGE_APPOPS", Binder.getCallingPid(), Binder.getCallingUid(), "getHistoricalOps");
        String[] strArr = list != null ? (String[]) list.toArray(new String[list.size()]) : null;
        Set indicatorExemptedPackages = (i2 & 4) != 0 ? PermissionManager.getIndicatorExemptedPackages(this.mContext) : null;
        this.mHandler.post(PooledLambda.obtainRunnable(new DodecConsumer() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda6
            public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11, Object obj12) {
                ((HistoricalRegistry) obj).getHistoricalOpsFromDiskRaw(((Integer) obj2).intValue(), (String) obj3, (String) obj4, (String[]) obj5, ((Integer) obj6).intValue(), ((Integer) obj7).intValue(), ((Long) obj8).longValue(), ((Long) obj9).longValue(), ((Integer) obj10).intValue(), (String[]) obj11, (RemoteCallback) obj12);
            }
        }, this.mHistoricalRegistry, Integer.valueOf(i), str, str2, strArr, Integer.valueOf(i2), Integer.valueOf(i3), Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(i4), indicatorExemptedPackages != null ? (String[]) indicatorExemptedPackages.toArray(new String[indicatorExemptedPackages.size()]) : null, remoteCallback).recycleOnUse());
    }

    public void reloadNonHistoricalState() {
        this.mContext.enforcePermission("android.permission.MANAGE_APPOPS", Binder.getCallingPid(), Binder.getCallingUid(), "reloadNonHistoricalState");
        this.mAppOpsCheckingService.writeState();
        this.mAppOpsCheckingService.readState();
    }

    @VisibleForTesting
    public void readState() {
        this.mAppOpsCheckingService.readState();
    }

    public List<AppOpsManager.PackageOps> getUidOps(int i, int[] iArr) {
        this.mContext.enforcePermission("android.permission.GET_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), null);
        synchronized (this) {
            UidState uidStateLocked = getUidStateLocked(i, false);
            if (uidStateLocked == null) {
                return null;
            }
            ArrayList<AppOpsManager.OpEntry> collectUidOps = collectUidOps(uidStateLocked, iArr);
            if (collectUidOps == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(new AppOpsManager.PackageOps((String) null, uidStateLocked.uid, collectUidOps));
            return arrayList;
        }
    }

    public final void pruneOpLocked(C0464Op c0464Op, int i, String str) {
        Ops opsLocked;
        UidState uidState;
        ArrayMap<String, Ops> arrayMap;
        c0464Op.removeAttributionsWithNoTime();
        if (!c0464Op.mAttributions.isEmpty() || (opsLocked = getOpsLocked(i, str, null, false, null, false)) == null) {
            return;
        }
        opsLocked.remove(c0464Op.f1125op);
        c0464Op.setMode(AppOpsManager.opToDefaultMode(c0464Op.f1125op));
        if (opsLocked.size() > 0 || (arrayMap = (uidState = opsLocked.uidState).pkgOps) == null) {
            return;
        }
        arrayMap.remove(opsLocked.packageName);
        this.mAppOpsCheckingService.removePackage(opsLocked.packageName, UserHandle.getUserId(uidState.uid));
    }

    public final void enforceManageAppOpsModes(int i, int i2, int i3) {
        if (i == Process.myPid()) {
            return;
        }
        int userId = UserHandle.getUserId(i2);
        synchronized (this) {
            SparseIntArray sparseIntArray = this.mProfileOwners;
            if (sparseIntArray == null || sparseIntArray.get(userId, -1) != i2 || i3 < 0 || userId != UserHandle.getUserId(i3)) {
                this.mContext.enforcePermission("android.permission.MANAGE_APP_OPS_MODES", Binder.getCallingPid(), Binder.getCallingUid(), null);
            }
        }
    }

    public void setUidMode(int i, int i2, int i3) {
        setUidMode(i, i2, i3, null);
    }

    public final void setUidMode(int i, int i2, int i3, IAppOpsCallback iAppOpsCallback) {
        enforceManageAppOpsModes(Binder.getCallingPid(), Binder.getCallingUid(), i2);
        verifyIncomingOp(i);
        int opToSwitch = AppOpsManager.opToSwitch(i);
        if (iAppOpsCallback == null) {
            updatePermissionRevokedCompat(i2, opToSwitch, i3);
        }
        synchronized (this) {
            int opToDefaultMode = AppOpsManager.opToDefaultMode(opToSwitch);
            UidState uidStateLocked = getUidStateLocked(i2, false);
            if (uidStateLocked == null) {
                if (i3 == opToDefaultMode) {
                    return;
                }
                uidStateLocked = new UidState(i2);
                this.mUidStates.put(i2, uidStateLocked);
            }
            int uidMode = uidStateLocked.getUidMode(opToSwitch) != AppOpsManager.opToDefaultMode(opToSwitch) ? uidStateLocked.getUidMode(opToSwitch) : 3;
            if (uidStateLocked.setUidMode(opToSwitch, i3)) {
                uidStateLocked.evalForegroundOps();
                if (i3 != 2 && i3 != uidMode) {
                    boolean z = true;
                    if (i3 != 1) {
                        z = false;
                    }
                    updateStartedOpModeForUidLocked(opToSwitch, z, i2);
                }
                notifyOpChangedForAllPkgsInUid(opToSwitch, i2, false, iAppOpsCallback);
                notifyOpChangedSync(opToSwitch, i2, null, i3, uidMode);
            }
        }
    }

    public final void notifyOpChangedForAllPkgsInUid(int i, int i2, boolean z, IAppOpsCallback iAppOpsCallback) {
        this.mAppOpsCheckingService.notifyOpChangedForAllPkgsInUid(i, i2, z, iAppOpsCallback != null ? this.mModeWatchers.get(iAppOpsCallback.asBinder()) : null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:128:0x00f3, code lost:
        if (r29 != 4) goto L43;
     */
    /* JADX WARN: Code restructure failed: missing block: B:132:0x0104, code lost:
        if (r29 != 0) goto L43;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void updatePermissionRevokedCompat(int i, int i2, int i3) {
        int i4;
        String str;
        int[] iArr;
        int i5;
        PermissionInfo permissionInfo;
        String str2;
        String str3;
        String str4;
        long clearCallingIdentity;
        String str5;
        PackageManager packageManager = this.mContext.getPackageManager();
        if (packageManager == null) {
            return;
        }
        String[] packagesForUid = packageManager.getPackagesForUid(i);
        if (ArrayUtils.isEmpty(packagesForUid)) {
            return;
        }
        int i6 = 0;
        String str6 = packagesForUid[0];
        int[] iArr2 = this.mSwitchedOps.get(i2);
        int length = iArr2.length;
        int i7 = 0;
        while (i7 < length) {
            String opToPermission = AppOpsManager.opToPermission(iArr2[i7]);
            if (opToPermission != null && packageManager.checkPermission(opToPermission, str6) == 0) {
                try {
                    permissionInfo = packageManager.getPermissionInfo(opToPermission, i6);
                } catch (PackageManager.NameNotFoundException e) {
                    i4 = i7;
                    str = str6;
                    iArr = iArr2;
                    i5 = length;
                    e.printStackTrace();
                }
                if (permissionInfo.isRuntime()) {
                    boolean z = true;
                    int i8 = getPackageManagerInternal().getUidTargetSdkVersion(i) >= 23 ? 1 : i6;
                    UserHandle userHandleForUid = UserHandle.getUserHandleForUid(i);
                    String str7 = permissionInfo.backgroundPermission;
                    if (str7 != null) {
                        if (packageManager.checkPermission(str7, str6) == 0) {
                            boolean z2 = i3 != 0;
                            if (!z2 || i8 == 0) {
                                str5 = ", switchCode=";
                                i4 = i7;
                            } else {
                                i4 = i7;
                                StringBuilder sb = new StringBuilder();
                                sb.append("setUidMode() called with a mode inconsistent with runtime permission state, this is discouraged and you should revoke the runtime permission instead: uid=");
                                sb.append(i);
                                sb.append(", switchCode=");
                                sb.append(i2);
                                sb.append(", mode=");
                                sb.append(i3);
                                sb.append(", permission=");
                                str5 = ", switchCode=";
                                sb.append(permissionInfo.backgroundPermission);
                                Slog.w("AppOps", sb.toString());
                            }
                            clearCallingIdentity = Binder.clearCallingIdentity();
                            try {
                                iArr = iArr2;
                                str2 = str5;
                                i5 = length;
                                str3 = ", mode=";
                                str = str6;
                                str4 = ", permission=";
                                packageManager.updatePermissionFlags(permissionInfo.backgroundPermission, str6, 8, z2 ? 8 : 0, userHandleForUid);
                                Binder.restoreCallingIdentity(clearCallingIdentity);
                            } finally {
                            }
                        } else {
                            i4 = i7;
                            str = str6;
                            iArr = iArr2;
                            i5 = length;
                            str2 = ", switchCode=";
                            str3 = ", mode=";
                            str4 = ", permission=";
                        }
                        if (i3 != 0) {
                        }
                        z = false;
                    } else {
                        i4 = i7;
                        str = str6;
                        iArr = iArr2;
                        i5 = length;
                        str2 = ", switchCode=";
                        str3 = ", mode=";
                        str4 = ", permission=";
                    }
                    if (z && i8 != 0) {
                        Slog.w("AppOps", "setUidMode() called with a mode inconsistent with runtime permission state, this is discouraged and you should revoke the runtime permission instead: uid=" + i + str2 + i2 + str3 + i3 + str4 + opToPermission);
                    }
                    clearCallingIdentity = Binder.clearCallingIdentity();
                    try {
                        packageManager.updatePermissionFlags(opToPermission, str, 8, z ? 8 : 0, userHandleForUid);
                        i7 = i4 + 1;
                        length = i5;
                        str6 = str;
                        iArr2 = iArr;
                        i6 = 0;
                    } finally {
                    }
                }
            }
            i4 = i7;
            str = str6;
            iArr = iArr2;
            i5 = length;
            i7 = i4 + 1;
            length = i5;
            str6 = str;
            iArr2 = iArr;
            i6 = 0;
        }
    }

    public final void notifyOpChangedSync(int i, int i2, String str, int i3, int i4) {
        StorageManagerInternal storageManagerInternal = (StorageManagerInternal) LocalServices.getService(StorageManagerInternal.class);
        if (storageManagerInternal != null) {
            storageManagerInternal.onAppOpsChanged(i, i2, str, i3, i4);
        }
    }

    public void setMode(int i, int i2, String str, int i3) {
        setMode(i, i2, str, i3, null);
    }

    public void setMode(int i, int i2, String str, int i3, IAppOpsCallback iAppOpsCallback) {
        int i4;
        ArraySet arraySet;
        enforceManageAppOpsModes(Binder.getCallingPid(), Binder.getCallingUid(), i2);
        verifyIncomingOp(i);
        if (isIncomingPackageValid(str, UserHandle.getUserId(i2))) {
            int opToSwitch = AppOpsManager.opToSwitch(i);
            ArraySet arraySet2 = null;
            try {
                PackageVerificationResult verifyAndGetBypass = verifyAndGetBypass(i2, str, null);
                synchronized (this) {
                    UidState uidStateLocked = getUidStateLocked(i2, false);
                    C0464Op opLocked = getOpLocked(opToSwitch, i2, str, null, false, verifyAndGetBypass.bypass, true);
                    if (opLocked == null || opLocked.getMode() == i3) {
                        i4 = 3;
                    } else {
                        int mode = opLocked.getMode();
                        opLocked.setMode(i3);
                        if (uidStateLocked != null) {
                            uidStateLocked.evalForegroundOps();
                        }
                        ArraySet<OnOpModeChangedListener> opModeChangedListeners = this.mAppOpsCheckingService.getOpModeChangedListeners(opToSwitch);
                        if (opModeChangedListeners != null) {
                            arraySet2 = new ArraySet();
                            arraySet2.addAll((ArraySet) opModeChangedListeners);
                        }
                        ArraySet<OnOpModeChangedListener> packageModeChangedListeners = this.mAppOpsCheckingService.getPackageModeChangedListeners(str);
                        if (packageModeChangedListeners != null) {
                            if (arraySet2 == null) {
                                arraySet2 = new ArraySet();
                            }
                            arraySet2.addAll((ArraySet) packageModeChangedListeners);
                        }
                        if (arraySet2 != null && iAppOpsCallback != null) {
                            arraySet2.remove(this.mModeWatchers.get(iAppOpsCallback.asBinder()));
                        }
                        if (i3 == AppOpsManager.opToDefaultMode(opLocked.f1125op)) {
                            pruneOpLocked(opLocked, i2, str);
                        }
                        scheduleFastWriteLocked();
                        if (i3 != 2) {
                            updateStartedOpModeForUidLocked(opToSwitch, i3 == 1, i2);
                        }
                        i4 = mode;
                    }
                    arraySet = arraySet2;
                }
                if (arraySet != null) {
                    this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuintConsumer() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda12
                        public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                            ((AppOpsService) obj).notifyOpChanged((ArraySet) obj2, ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), (String) obj5);
                        }
                    }, this, arraySet, Integer.valueOf(opToSwitch), Integer.valueOf(i2), str));
                }
                notifyOpChangedSync(opToSwitch, i2, str, i3, i4);
            } catch (SecurityException e) {
                if (Process.isIsolated(i2)) {
                    Slog.e("AppOps", "Cannot setMode: isolated process");
                } else {
                    Slog.e("AppOps", "Cannot setMode", e);
                }
            }
        }
    }

    public final void notifyOpChanged(ArraySet<OnOpModeChangedListener> arraySet, int i, int i2, String str) {
        for (int i3 = 0; i3 < arraySet.size(); i3++) {
            notifyOpChanged(arraySet.valueAt(i3), i, i2, str);
        }
    }

    public final void notifyOpChanged(OnOpModeChangedListener onOpModeChangedListener, int i, int i2, String str) {
        this.mAppOpsCheckingService.notifyOpChanged(onOpModeChangedListener, i, i2, str);
    }

    public static ArrayList<ChangeRec> addChange(ArrayList<ChangeRec> arrayList, int i, int i2, String str, int i3) {
        boolean z = false;
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        } else {
            int size = arrayList.size();
            int i4 = 0;
            while (true) {
                if (i4 >= size) {
                    break;
                }
                ChangeRec changeRec = arrayList.get(i4);
                if (changeRec.f1124op == i && changeRec.pkg.equals(str)) {
                    z = true;
                    break;
                }
                i4++;
            }
        }
        if (!z) {
            arrayList.add(new ChangeRec(i, i2, str, i3));
        }
        return arrayList;
    }

    public static HashMap<OnOpModeChangedListener, ArrayList<ChangeRec>> addCallbacks(HashMap<OnOpModeChangedListener, ArrayList<ChangeRec>> hashMap, int i, int i2, String str, int i3, ArraySet<OnOpModeChangedListener> arraySet) {
        if (arraySet == null) {
            return hashMap;
        }
        if (hashMap == null) {
            hashMap = new HashMap<>();
        }
        int size = arraySet.size();
        for (int i4 = 0; i4 < size; i4++) {
            OnOpModeChangedListener valueAt = arraySet.valueAt(i4);
            ArrayList<ChangeRec> arrayList = hashMap.get(valueAt);
            ArrayList<ChangeRec> addChange = addChange(arrayList, i, i2, str, i3);
            if (addChange != arrayList) {
                hashMap.put(valueAt, addChange);
            }
        }
        return hashMap;
    }

    /* loaded from: classes.dex */
    public static final class ChangeRec {

        /* renamed from: op */
        public final int f1124op;
        public final String pkg;
        public final int previous_mode;
        public final int uid;

        public ChangeRec(int i, int i2, String str, int i3) {
            this.f1124op = i;
            this.uid = i2;
            this.pkg = str;
            this.previous_mode = i3;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:190:0x0032 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void resetAllModes(int i, String str) {
        int packageUid;
        ArrayList<ChangeRec> arrayList;
        HashMap<OnOpModeChangedListener, ArrayList<ChangeRec>> hashMap;
        int i2;
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        int handleIncomingUser = ActivityManager.handleIncomingUser(callingPid, callingUid, i, true, true, "resetAllModes", null);
        int i3 = -1;
        if (str != null) {
            try {
                packageUid = AppGlobals.getPackageManager().getPackageUid(str, 8192L, handleIncomingUser);
            } catch (RemoteException unused) {
            }
            enforceManageAppOpsModes(callingPid, callingUid, packageUid);
            ArrayList<ChangeRec> arrayList2 = new ArrayList<>();
            synchronized (this) {
                int i4 = 1;
                int size = this.mUidStates.size() - 1;
                boolean z = false;
                arrayList = arrayList2;
                hashMap = null;
                while (size >= 0) {
                    UidState valueAt = this.mUidStates.valueAt(size);
                    SparseIntArray nonDefaultUidModes = valueAt.getNonDefaultUidModes();
                    if (nonDefaultUidModes != null && (valueAt.uid == packageUid || packageUid == i3)) {
                        int size2 = nonDefaultUidModes.size() - i4;
                        while (size2 >= 0) {
                            int keyAt = nonDefaultUidModes.keyAt(size2);
                            if (AppOpsManager.opAllowsReset(keyAt)) {
                                int valueAt2 = nonDefaultUidModes.valueAt(size2);
                                valueAt.setUidMode(keyAt, AppOpsManager.opToDefaultMode(keyAt));
                                String[] packagesForUid = getPackagesForUid(valueAt.uid);
                                int length = packagesForUid.length;
                                int i5 = 0;
                                while (i5 < length) {
                                    int i6 = packageUid;
                                    String str2 = packagesForUid[i5];
                                    int i7 = length;
                                    String[] strArr = packagesForUid;
                                    int i8 = valueAt2;
                                    HashMap<OnOpModeChangedListener, ArrayList<ChangeRec>> hashMap2 = hashMap;
                                    int i9 = keyAt;
                                    HashMap<OnOpModeChangedListener, ArrayList<ChangeRec>> addCallbacks = addCallbacks(addCallbacks(hashMap2, keyAt, valueAt.uid, str2, i8, this.mAppOpsCheckingService.getOpModeChangedListeners(keyAt)), i9, valueAt.uid, str2, i8, this.mAppOpsCheckingService.getPackageModeChangedListeners(str2));
                                    arrayList = addChange(arrayList, i9, valueAt.uid, str2, i8);
                                    i5++;
                                    keyAt = i9;
                                    hashMap = addCallbacks;
                                    valueAt2 = i8;
                                    packageUid = i6;
                                    length = i7;
                                    packagesForUid = strArr;
                                }
                            }
                            size2--;
                            packageUid = packageUid;
                        }
                    }
                    int i10 = packageUid;
                    if (!valueAt.pkgOps.isEmpty() && (handleIncomingUser == -1 || handleIncomingUser == UserHandle.getUserId(valueAt.uid))) {
                        Iterator<Map.Entry<String, Ops>> it = valueAt.pkgOps.entrySet().iterator();
                        boolean z2 = false;
                        while (it.hasNext()) {
                            Map.Entry<String, Ops> next = it.next();
                            String key = next.getKey();
                            if (str == null || str.equals(key)) {
                                Ops value = next.getValue();
                                HashMap<OnOpModeChangedListener, ArrayList<ChangeRec>> hashMap3 = hashMap;
                                for (int size3 = value.size() - 1; size3 >= 0; size3--) {
                                    C0464Op valueAt3 = value.valueAt(size3);
                                    if (shouldDeferResetOpToDpm(valueAt3.f1125op)) {
                                        deferResetOpToDpm(valueAt3.f1125op, str, handleIncomingUser);
                                    } else if (AppOpsManager.opAllowsReset(valueAt3.f1125op) && valueAt3.getMode() != AppOpsManager.opToDefaultMode(valueAt3.f1125op)) {
                                        int mode = valueAt3.getMode();
                                        valueAt3.setMode(AppOpsManager.opToDefaultMode(valueAt3.f1125op));
                                        int i11 = valueAt3.uidState.uid;
                                        int i12 = valueAt3.f1125op;
                                        hashMap3 = addCallbacks(addCallbacks(hashMap3, i12, i11, key, mode, this.mAppOpsCheckingService.getOpModeChangedListeners(i12)), valueAt3.f1125op, i11, key, mode, this.mAppOpsCheckingService.getPackageModeChangedListeners(key));
                                        arrayList = addChange(arrayList, valueAt3.f1125op, i11, key, mode);
                                        valueAt3.removeAttributionsWithNoTime();
                                        if (valueAt3.mAttributions.isEmpty()) {
                                            value.removeAt(size3);
                                        }
                                        z2 = true;
                                        z = true;
                                    }
                                }
                                if (value.size() == 0) {
                                    it.remove();
                                    this.mAppOpsCheckingService.removePackage(key, UserHandle.getUserId(valueAt.uid));
                                }
                                hashMap = hashMap3;
                            }
                        }
                        i2 = 1;
                        if (z2) {
                            valueAt.evalForegroundOps();
                        }
                        size--;
                        i4 = i2;
                        packageUid = i10;
                        i3 = -1;
                    }
                    i2 = 1;
                    size--;
                    i4 = i2;
                    packageUid = i10;
                    i3 = -1;
                }
                if (z) {
                    scheduleFastWriteLocked();
                }
            }
            if (hashMap != null) {
                for (Map.Entry<OnOpModeChangedListener, ArrayList<ChangeRec>> entry : hashMap.entrySet()) {
                    OnOpModeChangedListener key2 = entry.getKey();
                    ArrayList<ChangeRec> value2 = entry.getValue();
                    for (int i13 = 0; i13 < value2.size(); i13++) {
                        ChangeRec changeRec = value2.get(i13);
                        this.mHandler.sendMessage(PooledLambda.obtainMessage(new AppOpsService$$ExternalSyntheticLambda4(), this, key2, Integer.valueOf(changeRec.f1124op), Integer.valueOf(changeRec.uid), changeRec.pkg));
                    }
                }
            }
            int size4 = arrayList.size();
            for (int i14 = 0; i14 < size4; i14++) {
                ChangeRec changeRec2 = arrayList.get(i14);
                int i15 = changeRec2.f1124op;
                notifyOpChangedSync(i15, changeRec2.uid, changeRec2.pkg, AppOpsManager.opToDefaultMode(i15), changeRec2.previous_mode);
            }
            return;
        }
        packageUid = -1;
        enforceManageAppOpsModes(callingPid, callingUid, packageUid);
        ArrayList<ChangeRec> arrayList22 = new ArrayList<>();
        synchronized (this) {
        }
    }

    public final boolean shouldDeferResetOpToDpm(int i) {
        DevicePolicyManagerInternal devicePolicyManagerInternal = this.dpmi;
        return devicePolicyManagerInternal != null && devicePolicyManagerInternal.supportsResetOp(i);
    }

    public final void deferResetOpToDpm(int i, String str, int i2) {
        this.dpmi.resetOp(i, str, i2);
    }

    public final void evalAllForegroundOpsLocked() {
        for (int size = this.mUidStates.size() - 1; size >= 0; size--) {
            UidState valueAt = this.mUidStates.valueAt(size);
            if (valueAt.foregroundOps != null) {
                valueAt.evalForegroundOps();
            }
        }
    }

    public void startWatchingMode(int i, String str, IAppOpsCallback iAppOpsCallback) {
        startWatchingModeWithFlags(i, str, 0, iAppOpsCallback);
    }

    public void startWatchingModeWithFlags(int i, String str, int i2, IAppOpsCallback iAppOpsCallback) {
        int opToSwitch;
        int i3;
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        Preconditions.checkArgumentInRange(i, -1, 133, "Invalid op code: " + i);
        if (iAppOpsCallback == null) {
            return;
        }
        boolean z = (str == null || filterAppAccessUnlocked(str, UserHandle.getUserId(callingUid))) ? false : true;
        synchronized (this) {
            if (i != -1) {
                try {
                    opToSwitch = AppOpsManager.opToSwitch(i);
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                opToSwitch = i;
            }
            if ((i2 & 2) == 0) {
                if (i == -1) {
                    i = -2;
                }
                i3 = i;
            } else {
                i3 = opToSwitch;
            }
            ModeCallback modeCallback = this.mModeWatchers.get(iAppOpsCallback.asBinder());
            if (modeCallback == null) {
                modeCallback = new ModeCallback(iAppOpsCallback, -1, i2, i3, callingUid, callingPid);
                this.mModeWatchers.put(iAppOpsCallback.asBinder(), modeCallback);
            }
            if (opToSwitch != -1) {
                this.mAppOpsCheckingService.startWatchingOpModeChanged(modeCallback, opToSwitch);
            }
            if (z) {
                this.mAppOpsCheckingService.startWatchingPackageModeChanged(modeCallback, str);
            }
            evalAllForegroundOpsLocked();
        }
    }

    public void stopWatchingMode(IAppOpsCallback iAppOpsCallback) {
        if (iAppOpsCallback == null) {
            return;
        }
        synchronized (this) {
            ModeCallback remove = this.mModeWatchers.remove(iAppOpsCallback.asBinder());
            if (remove != null) {
                remove.unlinkToDeath();
                this.mAppOpsCheckingService.removeListener(remove);
            }
            evalAllForegroundOpsLocked();
        }
    }

    public AppOpsManagerInternal.CheckOpsDelegate getAppOpsServiceDelegate() {
        AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate;
        synchronized (this) {
            CheckOpsDelegateDispatcher checkOpsDelegateDispatcher = this.mCheckOpsDelegateDispatcher;
            checkOpsDelegate = checkOpsDelegateDispatcher != null ? checkOpsDelegateDispatcher.getCheckOpsDelegate() : null;
        }
        return checkOpsDelegate;
    }

    public void setAppOpsServiceDelegate(AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate) {
        synchronized (this) {
            CheckOpsDelegateDispatcher checkOpsDelegateDispatcher = this.mCheckOpsDelegateDispatcher;
            this.mCheckOpsDelegateDispatcher = new CheckOpsDelegateDispatcher(checkOpsDelegateDispatcher != null ? checkOpsDelegateDispatcher.mPolicy : null, checkOpsDelegate);
        }
    }

    public int checkOperationRaw(int i, int i2, String str, String str2) {
        return this.mCheckOpsDelegateDispatcher.checkOperation(i, i2, str, str2, true);
    }

    public int checkOperation(int i, int i2, String str) {
        return this.mCheckOpsDelegateDispatcher.checkOperation(i, i2, str, null, false);
    }

    public final int checkOperationImpl(int i, int i2, String str, String str2, boolean z) {
        verifyIncomingOp(i);
        if (!isIncomingPackageValid(str, UserHandle.getUserId(i2))) {
            return AppOpsManager.opToDefaultMode(i);
        }
        String resolvePackageName = AppOpsManager.resolvePackageName(i2, str);
        if (resolvePackageName == null) {
            return 1;
        }
        return checkOperationUnchecked(i, i2, resolvePackageName, str2, z);
    }

    public final int checkOperationUnchecked(int i, int i2, String str, String str2, boolean z) {
        try {
            PackageVerificationResult verifyAndGetBypass = verifyAndGetBypass(i2, str, null);
            if (isOpRestrictedDueToSuspend(i, str, i2)) {
                return 1;
            }
            synchronized (this) {
                if (isOpRestrictedLocked(i2, i, str, str2, verifyAndGetBypass.bypass, true)) {
                    return 1;
                }
                int opToSwitch = AppOpsManager.opToSwitch(i);
                UidState uidStateLocked = getUidStateLocked(i2, false);
                if (uidStateLocked != null && uidStateLocked.getUidMode(opToSwitch) != AppOpsManager.opToDefaultMode(opToSwitch)) {
                    int uidMode = uidStateLocked.getUidMode(opToSwitch);
                    if (!z) {
                        uidMode = uidStateLocked.evalMode(opToSwitch, uidMode);
                    }
                    return uidMode;
                }
                C0464Op opLocked = getOpLocked(opToSwitch, i2, str, null, false, verifyAndGetBypass.bypass, false);
                if (opLocked == null) {
                    return AppOpsManager.opToDefaultMode(opToSwitch);
                }
                return z ? opLocked.getMode() : opLocked.uidState.evalMode(opLocked.f1125op, opLocked.getMode());
            }
        } catch (SecurityException e) {
            if (Process.isIsolated(i2)) {
                Slog.e("AppOps", "Cannot checkOperation: isolated process");
            } else {
                Slog.e("AppOps", "Cannot checkOperation", e);
            }
            return AppOpsManager.opToDefaultMode(i);
        }
    }

    public int checkAudioOperation(int i, int i2, int i3, String str) {
        return this.mCheckOpsDelegateDispatcher.checkAudioOperation(i, i2, i3, str);
    }

    public final int checkAudioOperationImpl(int i, int i2, int i3, String str) {
        int checkAudioOperation = this.mAudioRestrictionManager.checkAudioOperation(i, i2, i3, str);
        return checkAudioOperation != 0 ? checkAudioOperation : checkOperation(i, i3, str);
    }

    public void setAudioRestriction(int i, int i2, int i3, int i4, String[] strArr) {
        enforceManageAppOpsModes(Binder.getCallingPid(), Binder.getCallingUid(), i3);
        verifyIncomingUid(i3);
        verifyIncomingOp(i);
        this.mAudioRestrictionManager.setZenModeAudioRestriction(i, i2, i3, i4, strArr);
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new AppOpsService$$ExternalSyntheticLambda1(), this, Integer.valueOf(i), -2));
    }

    public void setCameraAudioRestriction(int i) {
        enforceManageAppOpsModes(Binder.getCallingPid(), Binder.getCallingUid(), -1);
        this.mAudioRestrictionManager.setCameraAudioRestriction(i);
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new AppOpsService$$ExternalSyntheticLambda1(), this, 28, -2));
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new AppOpsService$$ExternalSyntheticLambda1(), this, 3, -2));
    }

    public int checkPackage(int i, String str) {
        Objects.requireNonNull(str);
        try {
            verifyAndGetBypass(i, str, null, null, true);
            if (resolveUid(str) != i) {
                if (isPackageExisted(str)) {
                    if (!filterAppAccessUnlocked(str, UserHandle.getUserId(i))) {
                        return 0;
                    }
                }
                return 2;
            }
            return 0;
        } catch (SecurityException unused) {
            return 2;
        }
    }

    public final boolean isPackageExisted(String str) {
        return getPackageManagerInternal().getPackageStateInternal(str) != null;
    }

    public final boolean filterAppAccessUnlocked(String str, int i) {
        return ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).filterAppAccess(str, Binder.getCallingUid(), i);
    }

    public SyncNotedAppOp noteProxyOperation(int i, AttributionSource attributionSource, boolean z, String str, boolean z2, boolean z3) {
        return this.mCheckOpsDelegateDispatcher.noteProxyOperation(i, attributionSource, z, str, z2, z3);
    }

    public final SyncNotedAppOp noteProxyOperationImpl(int i, AttributionSource attributionSource, boolean z, String str, boolean z2, boolean z3) {
        String str2;
        String str3;
        int i2;
        int uid = attributionSource.getUid();
        String packageName = attributionSource.getPackageName();
        String attributionTag = attributionSource.getAttributionTag();
        int nextUid = attributionSource.getNextUid();
        String nextPackageName = attributionSource.getNextPackageName();
        String nextAttributionTag = attributionSource.getNextAttributionTag();
        verifyIncomingProxyUid(attributionSource);
        verifyIncomingOp(i);
        if (!isIncomingPackageValid(nextPackageName, UserHandle.getUserId(nextUid)) || !isIncomingPackageValid(packageName, UserHandle.getUserId(uid))) {
            return new SyncNotedAppOp(2, i, nextAttributionTag, nextPackageName);
        }
        boolean z4 = z3 && isCallerAndAttributionTrusted(attributionSource);
        String resolvePackageName = AppOpsManager.resolvePackageName(uid, packageName);
        if (resolvePackageName == null) {
            return new SyncNotedAppOp(1, i, nextAttributionTag, nextPackageName);
        }
        boolean z5 = this.mContext.checkPermission("android.permission.UPDATE_APP_OPS_STATS", -1, uid) == 0 || (Binder.getCallingUid() == nextUid);
        if (z4) {
            str2 = nextAttributionTag;
            str3 = nextPackageName;
            i2 = nextUid;
        } else {
            int i3 = z5 ? 2 : 4;
            i2 = nextUid;
            SyncNotedAppOp noteOperationUnchecked = noteOperationUnchecked(i, uid, resolvePackageName, attributionTag, -1, null, null, i3, !z5, "proxy " + str, z2);
            if (noteOperationUnchecked.getOpMode() != 0) {
                return new SyncNotedAppOp(noteOperationUnchecked.getOpMode(), i, nextAttributionTag, nextPackageName);
            }
            str2 = nextAttributionTag;
            str3 = nextPackageName;
        }
        String resolvePackageName2 = AppOpsManager.resolvePackageName(i2, str3);
        if (resolvePackageName2 == null) {
            return new SyncNotedAppOp(1, i, str2, str3);
        }
        return noteOperationUnchecked(i, i2, resolvePackageName2, str2, uid, resolvePackageName, attributionTag, z5 ? 8 : 16, z, str, z2);
    }

    public SyncNotedAppOp noteOperation(int i, int i2, String str, String str2, boolean z, String str3, boolean z2) {
        return this.mCheckOpsDelegateDispatcher.noteOperation(i, i2, str, str2, z, str3, z2);
    }

    public final SyncNotedAppOp noteOperationImpl(int i, int i2, String str, String str2, boolean z, String str3, boolean z2) {
        verifyIncomingUid(i2);
        verifyIncomingOp(i);
        if (!isIncomingPackageValid(str, UserHandle.getUserId(i2))) {
            return new SyncNotedAppOp(2, i, str2, str);
        }
        String resolvePackageName = AppOpsManager.resolvePackageName(i2, str);
        if (resolvePackageName == null) {
            return new SyncNotedAppOp(1, i, str2, str);
        }
        return noteOperationUnchecked(i, i2, resolvePackageName, str2, -1, null, null, 1, z, str3, z2);
    }

    public final SyncNotedAppOp noteOperationUnchecked(int i, int i2, String str, String str2, int i3, String str3, String str4, int i4, boolean z, String str5, boolean z2) {
        AttributedOp attributedOp;
        String str6 = str2;
        try {
            PackageVerificationResult verifyAndGetBypass = verifyAndGetBypass(i2, str, str6, str3);
            if (!verifyAndGetBypass.isAttributionTagValid) {
                str6 = null;
            }
            String str7 = str6;
            synchronized (this) {
                Ops opsLocked = getOpsLocked(i2, str, str7, verifyAndGetBypass.isAttributionTagValid, verifyAndGetBypass.bypass, true);
                if (opsLocked == null) {
                    scheduleOpNotedIfNeededLocked(i, i2, str, str7, i4, 1);
                    return new SyncNotedAppOp(2, i, str7, str);
                }
                C0464Op opLocked = getOpLocked(opsLocked, i, i2, true);
                AttributedOp orCreateAttribution = opLocked.getOrCreateAttribution(opLocked, str7);
                if (orCreateAttribution.isRunning()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Noting op not finished: uid ");
                    sb.append(i2);
                    sb.append(" pkg ");
                    sb.append(str);
                    sb.append(" code ");
                    sb.append(i);
                    sb.append(" startTime of in progress event=");
                    attributedOp = orCreateAttribution;
                    sb.append(orCreateAttribution.mInProgressEvents.valueAt(0).getStartTime());
                    Slog.w("AppOps", sb.toString());
                } else {
                    attributedOp = orCreateAttribution;
                }
                int opToSwitch = AppOpsManager.opToSwitch(i);
                UidState uidState = opsLocked.uidState;
                AttributedOp attributedOp2 = attributedOp;
                if (isOpRestrictedLocked(i2, i, str, str7, verifyAndGetBypass.bypass, false)) {
                    attributedOp2.rejected(uidState.getState(), i4);
                    scheduleOpNotedIfNeededLocked(i, i2, str, str7, i4, 1);
                    return new SyncNotedAppOp(1, i, str7, str);
                }
                if (uidState.getUidMode(opToSwitch) != AppOpsManager.opToDefaultMode(opToSwitch)) {
                    int evalMode = uidState.evalMode(i, uidState.getUidMode(opToSwitch));
                    if (evalMode != 0) {
                        attributedOp2.rejected(uidState.getState(), i4);
                        scheduleOpNotedIfNeededLocked(i, i2, str, str7, i4, evalMode);
                        return new SyncNotedAppOp(evalMode, i, str7, str);
                    }
                } else {
                    C0464Op opLocked2 = opToSwitch != i ? getOpLocked(opsLocked, opToSwitch, i2, true) : opLocked;
                    int evalMode2 = opLocked2.uidState.evalMode(opLocked2.f1125op, opLocked2.getMode());
                    if (evalMode2 != 0) {
                        attributedOp2.rejected(uidState.getState(), i4);
                        scheduleOpNotedIfNeededLocked(i, i2, str, str7, i4, evalMode2);
                        return new SyncNotedAppOp(evalMode2, i, str7, str);
                    }
                }
                scheduleOpNotedIfNeededLocked(i, i2, str, str7, i4, 0);
                attributedOp2.accessed(i3, str3, str4, uidState.getState(), i4);
                if (z) {
                    collectAsyncNotedOp(i2, str, i, str7, i4, str5, z2);
                }
                return new SyncNotedAppOp(0, i, str7, str);
            }
        } catch (SecurityException e) {
            if (Process.isIsolated(i2)) {
                Slog.e("AppOps", "Cannot noteOperation: isolated process");
            } else {
                Slog.e("AppOps", "Cannot noteOperation", e);
            }
            return new SyncNotedAppOp(2, i, str6, str);
        }
    }

    public void startWatchingActive(int[] iArr, IAppOpsActiveCallback iAppOpsActiveCallback) {
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        int i = this.mContext.checkCallingOrSelfPermission("android.permission.WATCH_APPOPS") != 0 ? callingUid : -1;
        if (iArr != null) {
            Preconditions.checkArrayElementsInRange(iArr, 0, 133, "Invalid op code in: " + Arrays.toString(iArr));
        }
        if (iAppOpsActiveCallback == null) {
            return;
        }
        synchronized (this) {
            SparseArray<ActiveCallback> sparseArray = this.mActiveWatchers.get(iAppOpsActiveCallback.asBinder());
            if (sparseArray == null) {
                sparseArray = new SparseArray<>();
                this.mActiveWatchers.put(iAppOpsActiveCallback.asBinder(), sparseArray);
            }
            SparseArray<ActiveCallback> sparseArray2 = sparseArray;
            ActiveCallback activeCallback = new ActiveCallback(iAppOpsActiveCallback, i, callingUid, callingPid);
            for (int i2 : iArr) {
                sparseArray2.put(i2, activeCallback);
            }
        }
    }

    public void stopWatchingActive(IAppOpsActiveCallback iAppOpsActiveCallback) {
        if (iAppOpsActiveCallback == null) {
            return;
        }
        synchronized (this) {
            SparseArray<ActiveCallback> remove = this.mActiveWatchers.remove(iAppOpsActiveCallback.asBinder());
            if (remove == null) {
                return;
            }
            int size = remove.size();
            for (int i = 0; i < size; i++) {
                remove.valueAt(i).destroy();
            }
        }
    }

    public void startWatchingStarted(int[] iArr, IAppOpsStartedCallback iAppOpsStartedCallback) {
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        int i = this.mContext.checkCallingOrSelfPermission("android.permission.WATCH_APPOPS") != 0 ? callingUid : -1;
        Preconditions.checkArgument(!ArrayUtils.isEmpty(iArr), "Ops cannot be null or empty");
        Preconditions.checkArrayElementsInRange(iArr, 0, 133, "Invalid op code in: " + Arrays.toString(iArr));
        Objects.requireNonNull(iAppOpsStartedCallback, "Callback cannot be null");
        synchronized (this) {
            SparseArray<StartedCallback> sparseArray = this.mStartedWatchers.get(iAppOpsStartedCallback.asBinder());
            if (sparseArray == null) {
                sparseArray = new SparseArray<>();
                this.mStartedWatchers.put(iAppOpsStartedCallback.asBinder(), sparseArray);
            }
            SparseArray<StartedCallback> sparseArray2 = sparseArray;
            StartedCallback startedCallback = new StartedCallback(iAppOpsStartedCallback, i, callingUid, callingPid);
            for (int i2 : iArr) {
                sparseArray2.put(i2, startedCallback);
            }
        }
    }

    public void stopWatchingStarted(IAppOpsStartedCallback iAppOpsStartedCallback) {
        Objects.requireNonNull(iAppOpsStartedCallback, "Callback cannot be null");
        synchronized (this) {
            SparseArray<StartedCallback> remove = this.mStartedWatchers.remove(iAppOpsStartedCallback.asBinder());
            if (remove == null) {
                return;
            }
            int size = remove.size();
            for (int i = 0; i < size; i++) {
                remove.valueAt(i).destroy();
            }
        }
    }

    public void startWatchingNoted(int[] iArr, IAppOpsNotedCallback iAppOpsNotedCallback) {
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        int i = this.mContext.checkCallingOrSelfPermission("android.permission.WATCH_APPOPS") != 0 ? callingUid : -1;
        Preconditions.checkArgument(!ArrayUtils.isEmpty(iArr), "Ops cannot be null or empty");
        Preconditions.checkArrayElementsInRange(iArr, 0, 133, "Invalid op code in: " + Arrays.toString(iArr));
        Objects.requireNonNull(iAppOpsNotedCallback, "Callback cannot be null");
        synchronized (this) {
            SparseArray<NotedCallback> sparseArray = this.mNotedWatchers.get(iAppOpsNotedCallback.asBinder());
            if (sparseArray == null) {
                sparseArray = new SparseArray<>();
                this.mNotedWatchers.put(iAppOpsNotedCallback.asBinder(), sparseArray);
            }
            SparseArray<NotedCallback> sparseArray2 = sparseArray;
            NotedCallback notedCallback = new NotedCallback(iAppOpsNotedCallback, i, callingUid, callingPid);
            for (int i2 : iArr) {
                sparseArray2.put(i2, notedCallback);
            }
        }
    }

    public void stopWatchingNoted(IAppOpsNotedCallback iAppOpsNotedCallback) {
        Objects.requireNonNull(iAppOpsNotedCallback, "Callback cannot be null");
        synchronized (this) {
            SparseArray<NotedCallback> remove = this.mNotedWatchers.remove(iAppOpsNotedCallback.asBinder());
            if (remove == null) {
                return;
            }
            int size = remove.size();
            for (int i = 0; i < size; i++) {
                remove.valueAt(i).destroy();
            }
        }
    }

    public final void collectAsyncNotedOp(final int i, final String str, final int i2, final String str2, int i3, String str3, boolean z) {
        AsyncNotedAppOp asyncNotedAppOp;
        RemoteCallbackList<IAppOpsAsyncNotedCallback> remoteCallbackList;
        Pair<String, Integer> pair;
        Objects.requireNonNull(str3);
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this) {
                Pair<String, Integer> asyncNotedOpsKey = getAsyncNotedOpsKey(str, i);
                RemoteCallbackList<IAppOpsAsyncNotedCallback> remoteCallbackList2 = this.mAsyncOpWatchers.get(asyncNotedOpsKey);
                AsyncNotedAppOp asyncNotedAppOp2 = new AsyncNotedAppOp(i2, callingUid, str2, str3, System.currentTimeMillis());
                final boolean[] zArr = {false};
                if ((i3 & 9) == 0 || !z) {
                    asyncNotedAppOp = asyncNotedAppOp2;
                    remoteCallbackList = remoteCallbackList2;
                    pair = asyncNotedOpsKey;
                } else {
                    asyncNotedAppOp = asyncNotedAppOp2;
                    remoteCallbackList = remoteCallbackList2;
                    pair = asyncNotedOpsKey;
                    reportRuntimeAppOpAccessMessageAsyncLocked(i, str, i2, str2, str3);
                }
                if (remoteCallbackList != null) {
                    final AsyncNotedAppOp asyncNotedAppOp3 = asyncNotedAppOp;
                    remoteCallbackList.broadcast(new Consumer() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda14
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            AppOpsService.lambda$collectAsyncNotedOp$3(asyncNotedAppOp3, zArr, i2, str, i, str2, (IAppOpsAsyncNotedCallback) obj);
                        }
                    });
                }
                if (!zArr[0]) {
                    ArrayList<AsyncNotedAppOp> arrayList = this.mUnforwardedAsyncNotedOps.get(pair);
                    if (arrayList == null) {
                        arrayList = new ArrayList<>(1);
                        this.mUnforwardedAsyncNotedOps.put(pair, arrayList);
                    }
                    arrayList.add(asyncNotedAppOp);
                    if (arrayList.size() > 10) {
                        arrayList.remove(0);
                    }
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ void lambda$collectAsyncNotedOp$3(AsyncNotedAppOp asyncNotedAppOp, boolean[] zArr, int i, String str, int i2, String str2, IAppOpsAsyncNotedCallback iAppOpsAsyncNotedCallback) {
        try {
            iAppOpsAsyncNotedCallback.opNoted(asyncNotedAppOp);
            zArr[0] = true;
        } catch (RemoteException e) {
            Slog.e("AppOps", "Could not forward noteOp of " + i + " to " + str + "/" + i2 + "(" + str2 + ")", e);
        }
    }

    public final Pair<String, Integer> getAsyncNotedOpsKey(String str, int i) {
        return new Pair<>(str, Integer.valueOf(i));
    }

    public void startWatchingAsyncNoted(String str, IAppOpsAsyncNotedCallback iAppOpsAsyncNotedCallback) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(iAppOpsAsyncNotedCallback);
        int callingUid = Binder.getCallingUid();
        final Pair<String, Integer> asyncNotedOpsKey = getAsyncNotedOpsKey(str, callingUid);
        verifyAndGetBypass(callingUid, str, null);
        synchronized (this) {
            RemoteCallbackList<IAppOpsAsyncNotedCallback> remoteCallbackList = this.mAsyncOpWatchers.get(asyncNotedOpsKey);
            if (remoteCallbackList == null) {
                remoteCallbackList = new RemoteCallbackList<IAppOpsAsyncNotedCallback>() { // from class: com.android.server.appop.AppOpsService.8
                    {
                        AppOpsService.this = this;
                    }

                    @Override // android.os.RemoteCallbackList
                    public void onCallbackDied(IAppOpsAsyncNotedCallback iAppOpsAsyncNotedCallback2) {
                        synchronized (AppOpsService.this) {
                            if (getRegisteredCallbackCount() == 0) {
                                AppOpsService.this.mAsyncOpWatchers.remove(asyncNotedOpsKey);
                            }
                        }
                    }
                };
                this.mAsyncOpWatchers.put(asyncNotedOpsKey, remoteCallbackList);
            }
            remoteCallbackList.register(iAppOpsAsyncNotedCallback);
        }
    }

    public void stopWatchingAsyncNoted(String str, IAppOpsAsyncNotedCallback iAppOpsAsyncNotedCallback) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(iAppOpsAsyncNotedCallback);
        int callingUid = Binder.getCallingUid();
        Pair<String, Integer> asyncNotedOpsKey = getAsyncNotedOpsKey(str, callingUid);
        verifyAndGetBypass(callingUid, str, null);
        synchronized (this) {
            RemoteCallbackList<IAppOpsAsyncNotedCallback> remoteCallbackList = this.mAsyncOpWatchers.get(asyncNotedOpsKey);
            if (remoteCallbackList != null) {
                remoteCallbackList.unregister(iAppOpsAsyncNotedCallback);
                if (remoteCallbackList.getRegisteredCallbackCount() == 0) {
                    this.mAsyncOpWatchers.remove(asyncNotedOpsKey);
                }
            }
        }
    }

    public List<AsyncNotedAppOp> extractAsyncOps(String str) {
        ArrayList<AsyncNotedAppOp> remove;
        Objects.requireNonNull(str);
        int callingUid = Binder.getCallingUid();
        verifyAndGetBypass(callingUid, str, null);
        synchronized (this) {
            remove = this.mUnforwardedAsyncNotedOps.remove(getAsyncNotedOpsKey(str, callingUid));
        }
        return remove;
    }

    public SyncNotedAppOp startOperation(IBinder iBinder, int i, int i2, String str, String str2, boolean z, boolean z2, String str3, boolean z3, int i3, int i4) {
        return this.mCheckOpsDelegateDispatcher.startOperation(iBinder, i, i2, str, str2, z, z2, str3, z3, i3, i4);
    }

    public final SyncNotedAppOp startOperationImpl(IBinder iBinder, int i, int i2, String str, String str2, boolean z, boolean z2, String str3, boolean z3, int i3, int i4) {
        int checkOperation;
        verifyIncomingUid(i2);
        verifyIncomingOp(i);
        if (!isIncomingPackageValid(str, UserHandle.getUserId(i2))) {
            return new SyncNotedAppOp(2, i, str2, str);
        }
        if (AppOpsManager.resolvePackageName(i2, str) == null) {
            return new SyncNotedAppOp(1, i, str2, str);
        }
        if ((i == 102 || i == 120) && (checkOperation = checkOperation(27, i2, str)) != 0) {
            return new SyncNotedAppOp(checkOperation, i, str2, str);
        }
        return startOperationUnchecked(iBinder, i, i2, str, str2, -1, null, null, 1, z, z2, str3, z3, i3, i4, false);
    }

    public SyncNotedAppOp startProxyOperation(IBinder iBinder, int i, AttributionSource attributionSource, boolean z, boolean z2, String str, boolean z3, boolean z4, int i2, int i3, int i4) {
        return this.mCheckOpsDelegateDispatcher.startProxyOperation(iBinder, i, attributionSource, z, z2, str, z3, z4, i2, i3, i4);
    }

    public final SyncNotedAppOp startProxyOperationImpl(IBinder iBinder, int i, AttributionSource attributionSource, boolean z, boolean z2, String str, boolean z3, boolean z4, int i2, int i3, int i4) {
        String str2;
        int i5;
        int i6;
        int uid = attributionSource.getUid();
        String packageName = attributionSource.getPackageName();
        String attributionTag = attributionSource.getAttributionTag();
        int nextUid = attributionSource.getNextUid();
        String nextPackageName = attributionSource.getNextPackageName();
        String nextAttributionTag = attributionSource.getNextAttributionTag();
        verifyIncomingProxyUid(attributionSource);
        verifyIncomingOp(i);
        if (!isIncomingPackageValid(packageName, UserHandle.getUserId(uid)) || !isIncomingPackageValid(nextPackageName, UserHandle.getUserId(nextUid))) {
            return new SyncNotedAppOp(2, i, nextAttributionTag, nextPackageName);
        }
        boolean isCallerAndAttributionTrusted = isCallerAndAttributionTrusted(attributionSource);
        boolean z5 = isCallerAndAttributionTrusted && z4;
        String resolvePackageName = AppOpsManager.resolvePackageName(uid, packageName);
        if (resolvePackageName == null) {
            return new SyncNotedAppOp(1, i, nextAttributionTag, nextPackageName);
        }
        boolean z6 = this.mContext.checkPermission("android.permission.UPDATE_APP_OPS_STATS", -1, uid) == 0 || (Binder.getCallingUid() == nextUid) || (isCallerAndAttributionTrusted && i4 != -1 && ((i2 & 8) != 0 || (i3 & 8) != 0));
        String resolvePackageName2 = AppOpsManager.resolvePackageName(nextUid, nextPackageName);
        if (resolvePackageName2 == null) {
            return new SyncNotedAppOp(1, i, nextAttributionTag, nextPackageName);
        }
        int i7 = z6 ? 8 : 16;
        if (z5) {
            str2 = nextAttributionTag;
            i5 = nextUid;
            i6 = uid;
        } else {
            str2 = nextAttributionTag;
            i5 = nextUid;
            i6 = uid;
            SyncNotedAppOp startOperationUnchecked = startOperationUnchecked(iBinder, i, nextUid, resolvePackageName2, nextAttributionTag, uid, resolvePackageName, attributionTag, i7, z, z2, str, z3, i3, i4, true);
            if (!shouldStartForMode(startOperationUnchecked.getOpMode(), z)) {
                return startOperationUnchecked;
            }
            int i8 = z6 ? 2 : 4;
            SyncNotedAppOp startOperationUnchecked2 = startOperationUnchecked(iBinder, i, i6, resolvePackageName, attributionTag, -1, null, null, i8, z, !z6, "proxy " + str, z3, i2, i4, false);
            if (!shouldStartForMode(startOperationUnchecked2.getOpMode(), z)) {
                return startOperationUnchecked2;
            }
        }
        return startOperationUnchecked(iBinder, i, i5, resolvePackageName2, str2, i6, resolvePackageName, attributionTag, i7, z, z2, str, z3, i3, i4, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r15v6, types: [int] */
    /* JADX WARN: Type inference failed for: r15v7 */
    /* JADX WARN: Type inference failed for: r15v8 */
    public final SyncNotedAppOp startOperationUnchecked(IBinder iBinder, int i, int i2, String str, String str2, int i3, String str3, String str4, int i4, boolean z, boolean z2, String str5, boolean z3, int i5, int i6, boolean z4) {
        AttributedOp attributedOp;
        int i7;
        C0464Op c0464Op;
        String str6;
        String str7;
        int i8;
        int i9;
        boolean z5;
        int i10;
        String str8;
        ?? r15;
        int i11;
        String str9 = str2;
        try {
            PackageVerificationResult verifyAndGetBypass = verifyAndGetBypass(i2, str, str9, str3);
            if (!verifyAndGetBypass.isAttributionTagValid) {
                str9 = null;
            }
            String str10 = str9;
            synchronized (this) {
                Ops opsLocked = getOpsLocked(i2, str, str10, verifyAndGetBypass.isAttributionTagValid, verifyAndGetBypass.bypass, true);
                if (opsLocked == null) {
                    if (!z4) {
                        scheduleOpStartedIfNeededLocked(i, i2, str, str10, i4, 1, 0, i5, i6);
                    }
                    return new SyncNotedAppOp(2, i, str10, str);
                }
                C0464Op opLocked = getOpLocked(opsLocked, i, i2, true);
                AttributedOp orCreateAttribution = opLocked.getOrCreateAttribution(opLocked, str10);
                UidState uidState = opsLocked.uidState;
                boolean isOpRestrictedLocked = isOpRestrictedLocked(i2, i, str, str10, verifyAndGetBypass.bypass, false);
                int opToSwitch = AppOpsManager.opToSwitch(i);
                if (uidState.getUidMode(opToSwitch) != AppOpsManager.opToDefaultMode(opToSwitch)) {
                    int evalMode = uidState.evalMode(i, uidState.getUidMode(opToSwitch));
                    if (!shouldStartForMode(evalMode, z)) {
                        if (z4) {
                            i11 = evalMode;
                        } else {
                            orCreateAttribution.rejected(uidState.getState(), i4);
                            i11 = evalMode;
                            scheduleOpStartedIfNeededLocked(i, i2, str, str10, i4, evalMode, 0, i5, i6);
                        }
                        return new SyncNotedAppOp(i11, i, str10, str);
                    }
                    attributedOp = orCreateAttribution;
                    str6 = str10;
                    i7 = 1;
                } else {
                    attributedOp = orCreateAttribution;
                    if (opToSwitch != i) {
                        i7 = 1;
                        c0464Op = getOpLocked(opsLocked, opToSwitch, i2, true);
                    } else {
                        i7 = 1;
                        c0464Op = opLocked;
                    }
                    int evalMode2 = c0464Op.uidState.evalMode(c0464Op.f1125op, c0464Op.getMode());
                    if (evalMode2 != 0 && (!z || evalMode2 != 3)) {
                        if (z4) {
                            str7 = str10;
                            i8 = evalMode2;
                        } else {
                            attributedOp.rejected(uidState.getState(), i4);
                            str7 = str10;
                            i8 = evalMode2;
                            scheduleOpStartedIfNeededLocked(i, i2, str, str10, i4, evalMode2, 0, i5, i6);
                        }
                        return new SyncNotedAppOp(i8, i, str7, str);
                    }
                    str6 = str10;
                }
                if (z4) {
                    r15 = isOpRestrictedLocked;
                    str8 = str6;
                } else {
                    try {
                        if (isOpRestrictedLocked) {
                            i9 = i7;
                            z5 = isOpRestrictedLocked;
                            attributedOp.createPaused(iBinder, i3, str3, str4, uidState.getState(), i4, i5, i6);
                            i10 = 0;
                        } else {
                            i9 = i7;
                            z5 = isOpRestrictedLocked;
                            attributedOp.started(iBinder, i3, str3, str4, uidState.getState(), i4, i5, i6);
                            i10 = i9;
                        }
                        str8 = str6;
                        scheduleOpStartedIfNeededLocked(i, i2, str, str6, i4, z5 ? i9 : 0, i10, i5, i6);
                        r15 = z5;
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (z2 && !z4 && r15 == 0) {
                    collectAsyncNotedOp(i2, str, i, str8, 1, str5, z3);
                }
                return new SyncNotedAppOp(r15, i, str8, str);
            }
        } catch (SecurityException e2) {
            if (Process.isIsolated(i2)) {
                Slog.e("AppOps", "Cannot startOperation: isolated process");
            } else {
                Slog.e("AppOps", "Cannot startOperation", e2);
            }
            return new SyncNotedAppOp(2, i, str9, str);
        }
    }

    public void finishOperation(IBinder iBinder, int i, int i2, String str, String str2) {
        this.mCheckOpsDelegateDispatcher.finishOperation(iBinder, i, i2, str, str2);
    }

    public final void finishOperationImpl(IBinder iBinder, int i, int i2, String str, String str2) {
        String resolvePackageName;
        verifyIncomingUid(i2);
        verifyIncomingOp(i);
        if (isIncomingPackageValid(str, UserHandle.getUserId(i2)) && (resolvePackageName = AppOpsManager.resolvePackageName(i2, str)) != null) {
            finishOperationUnchecked(iBinder, i, i2, resolvePackageName, str2);
        }
    }

    public void finishProxyOperation(IBinder iBinder, int i, AttributionSource attributionSource, boolean z) {
        this.mCheckOpsDelegateDispatcher.finishProxyOperation(iBinder, i, attributionSource, z);
    }

    public final Void finishProxyOperationImpl(IBinder iBinder, int i, AttributionSource attributionSource, boolean z) {
        String resolvePackageName;
        int uid = attributionSource.getUid();
        String packageName = attributionSource.getPackageName();
        String attributionTag = attributionSource.getAttributionTag();
        int nextUid = attributionSource.getNextUid();
        String nextPackageName = attributionSource.getNextPackageName();
        String nextAttributionTag = attributionSource.getNextAttributionTag();
        boolean z2 = z && isCallerAndAttributionTrusted(attributionSource);
        verifyIncomingProxyUid(attributionSource);
        verifyIncomingOp(i);
        if (isIncomingPackageValid(packageName, UserHandle.getUserId(uid)) && isIncomingPackageValid(nextPackageName, UserHandle.getUserId(nextUid)) && (resolvePackageName = AppOpsManager.resolvePackageName(uid, packageName)) != null) {
            if (!z2) {
                finishOperationUnchecked(iBinder, i, uid, resolvePackageName, attributionTag);
            }
            String resolvePackageName2 = AppOpsManager.resolvePackageName(nextUid, nextPackageName);
            if (resolvePackageName2 == null) {
                return null;
            }
            finishOperationUnchecked(iBinder, i, nextUid, resolvePackageName2, nextAttributionTag);
            return null;
        }
        return null;
    }

    public final void finishOperationUnchecked(IBinder iBinder, int i, int i2, String str, String str2) {
        try {
            PackageVerificationResult verifyAndGetBypass = verifyAndGetBypass(i2, str, str2);
            if (!verifyAndGetBypass.isAttributionTagValid) {
                str2 = null;
            }
            synchronized (this) {
                C0464Op opLocked = getOpLocked(i, i2, str, str2, verifyAndGetBypass.isAttributionTagValid, verifyAndGetBypass.bypass, true);
                if (opLocked == null) {
                    Slog.e("AppOps", "Operation not found: uid=" + i2 + " pkg=" + str + "(" + str2 + ") op=" + AppOpsManager.opToName(i));
                    return;
                }
                AttributedOp attributedOp = opLocked.mAttributions.get(str2);
                if (attributedOp == null) {
                    Slog.e("AppOps", "Attribution not found: uid=" + i2 + " pkg=" + str + "(" + str2 + ") op=" + AppOpsManager.opToName(i));
                    return;
                }
                if (!attributedOp.isRunning() && !attributedOp.isPaused()) {
                    Slog.e("AppOps", "Operation not started: uid=" + i2 + " pkg=" + str + "(" + str2 + ") op=" + AppOpsManager.opToName(i));
                }
                attributedOp.finished(iBinder);
            }
        } catch (SecurityException e) {
            if (Process.isIsolated(i2)) {
                Slog.e("AppOps", "Cannot finishOperation: isolated process");
            } else {
                Slog.e("AppOps", "Cannot finishOperation", e);
            }
        }
    }

    public void scheduleOpActiveChangedIfNeededLocked(int i, int i2, String str, String str2, boolean z, int i3, int i4) {
        int i5;
        int size = this.mActiveWatchers.size();
        ArraySet arraySet = null;
        for (int i6 = 0; i6 < size; i6++) {
            ActiveCallback activeCallback = this.mActiveWatchers.valueAt(i6).get(i);
            if (activeCallback != null && ((i5 = activeCallback.mWatchingUid) < 0 || i5 == i2)) {
                if (arraySet == null) {
                    arraySet = new ArraySet();
                }
                arraySet.add(activeCallback);
            }
        }
        if (arraySet == null) {
            return;
        }
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new NonaConsumer() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda0
            public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9) {
                ((AppOpsService) obj).notifyOpActiveChanged((ArraySet) obj2, ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), (String) obj5, (String) obj6, ((Boolean) obj7).booleanValue(), ((Integer) obj8).intValue(), ((Integer) obj9).intValue());
            }
        }, this, arraySet, Integer.valueOf(i), Integer.valueOf(i2), str, str2, Boolean.valueOf(z), Integer.valueOf(i3), Integer.valueOf(i4)));
    }

    public final void notifyOpActiveChanged(ArraySet<ActiveCallback> arraySet, int i, int i2, String str, String str2, boolean z, int i3, int i4) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int size = arraySet.size();
            for (int i5 = 0; i5 < size; i5++) {
                ActiveCallback valueAt = arraySet.valueAt(i5);
                try {
                    try {
                        if (!shouldIgnoreCallback(i, valueAt.mCallingPid, valueAt.mCallingUid)) {
                            valueAt.mCallback.opActiveChanged(i, i2, str, str2, z, i3, i4);
                        }
                    } catch (RemoteException unused) {
                    }
                } catch (RemoteException unused2) {
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void scheduleOpStartedIfNeededLocked(int i, int i2, String str, String str2, int i3, int i4, int i5, int i6, int i7) {
        int i8;
        int size = this.mStartedWatchers.size();
        ArraySet arraySet = null;
        for (int i9 = 0; i9 < size; i9++) {
            StartedCallback startedCallback = this.mStartedWatchers.valueAt(i9).get(i);
            if (startedCallback != null && ((i8 = startedCallback.mWatchingUid) < 0 || i8 == i2)) {
                if (arraySet == null) {
                    arraySet = new ArraySet();
                }
                arraySet.add(startedCallback);
            }
        }
        if (arraySet == null) {
            return;
        }
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new UndecConsumer() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda7
            public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11) {
                ((AppOpsService) obj).notifyOpStarted((ArraySet) obj2, ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), (String) obj5, (String) obj6, ((Integer) obj7).intValue(), ((Integer) obj8).intValue(), ((Integer) obj9).intValue(), ((Integer) obj10).intValue(), ((Integer) obj11).intValue());
            }
        }, this, arraySet, Integer.valueOf(i), Integer.valueOf(i2), str, str2, Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i7)));
    }

    public final void notifyOpStarted(ArraySet<StartedCallback> arraySet, int i, int i2, String str, String str2, int i3, int i4, int i5, int i6, int i7) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int size = arraySet.size();
            for (int i8 = 0; i8 < size; i8++) {
                StartedCallback valueAt = arraySet.valueAt(i8);
                try {
                    try {
                        if (!shouldIgnoreCallback(i, valueAt.mCallingPid, valueAt.mCallingUid)) {
                            valueAt.mCallback.opStarted(i, i2, str, str2, i3, i4, i5, i6, i7);
                        }
                    } catch (RemoteException unused) {
                    }
                } catch (RemoteException unused2) {
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void scheduleOpNotedIfNeededLocked(int i, int i2, String str, String str2, int i3, int i4) {
        int i5;
        int size = this.mNotedWatchers.size();
        ArraySet arraySet = null;
        for (int i6 = 0; i6 < size; i6++) {
            NotedCallback notedCallback = this.mNotedWatchers.valueAt(i6).get(i);
            if (notedCallback != null && ((i5 = notedCallback.mWatchingUid) < 0 || i5 == i2)) {
                if (arraySet == null) {
                    arraySet = new ArraySet();
                }
                arraySet.add(notedCallback);
            }
        }
        if (arraySet == null) {
            return;
        }
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new OctConsumer() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda16
            public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8) {
                ((AppOpsService) obj).notifyOpChecked((ArraySet) obj2, ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), (String) obj5, (String) obj6, ((Integer) obj7).intValue(), ((Integer) obj8).intValue());
            }
        }, this, arraySet, Integer.valueOf(i), Integer.valueOf(i2), str, str2, Integer.valueOf(i3), Integer.valueOf(i4)));
    }

    public final void notifyOpChecked(ArraySet<NotedCallback> arraySet, int i, int i2, String str, String str2, int i3, int i4) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int size = arraySet.size();
            for (int i5 = 0; i5 < size; i5++) {
                NotedCallback valueAt = arraySet.valueAt(i5);
                try {
                    try {
                        if (!shouldIgnoreCallback(i, valueAt.mCallingPid, valueAt.mCallingUid)) {
                            valueAt.mCallback.opNoted(i, i2, str, str2, i3, i4);
                        }
                    } catch (RemoteException unused) {
                    }
                } catch (RemoteException unused2) {
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int permissionToOpCode(String str) {
        if (str == null) {
            return -1;
        }
        return AppOpsManager.permissionToOpCode(str);
    }

    public boolean shouldCollectNotes(int i) {
        Preconditions.checkArgumentInRange(i, 0, 133, "opCode");
        if (AppOpsManager.shouldForceCollectNoteForOp(i)) {
            return true;
        }
        String opToPermission = AppOpsManager.opToPermission(i);
        if (opToPermission == null) {
            return false;
        }
        try {
            PermissionInfo permissionInfo = this.mContext.getPackageManager().getPermissionInfo(opToPermission, 0);
            return permissionInfo.getProtection() == 1 || (permissionInfo.getProtectionFlags() & 64) != 0;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public final void verifyIncomingProxyUid(AttributionSource attributionSource) {
        if (attributionSource.getUid() == Binder.getCallingUid() || Binder.getCallingPid() == Process.myPid() || attributionSource.isTrusted(this.mContext)) {
            return;
        }
        this.mContext.enforcePermission("android.permission.UPDATE_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), null);
    }

    public final void verifyIncomingUid(int i) {
        if (i == Binder.getCallingUid() || Binder.getCallingPid() == Process.myPid()) {
            return;
        }
        this.mContext.enforcePermission("android.permission.UPDATE_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), null);
    }

    public final boolean shouldIgnoreCallback(int i, int i2, int i3) {
        return AppOpsManager.opRestrictsRead(i) && this.mContext.checkPermission("android.permission.MANAGE_APPOPS", i2, i3) != 0;
    }

    public final void verifyIncomingOp(int i) {
        if (i >= 0 && i < 134) {
            if (AppOpsManager.opRestrictsRead(i)) {
                this.mContext.enforcePermission("android.permission.MANAGE_APPOPS", Binder.getCallingPid(), Binder.getCallingUid(), "verifyIncomingOp");
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Bad operation #" + i);
    }

    public final boolean isIncomingPackageValid(String str, int i) {
        int callingUid = Binder.getCallingUid();
        if (str != null && !isSpecialPackage(callingUid, str)) {
            if (!isPackageExisted(str)) {
                return false;
            }
            if (getPackageManagerInternal().filterAppAccess(str, callingUid, i)) {
                Slog.w("AppOps", str + " not found from " + callingUid);
                return false;
            }
        }
        return true;
    }

    public final boolean isSpecialPackage(int i, String str) {
        return i == 1000 || resolveUid(AppOpsManager.resolvePackageName(i, str)) != -1;
    }

    public final boolean isCallerAndAttributionTrusted(AttributionSource attributionSource) {
        return (attributionSource.getUid() != Binder.getCallingUid() && attributionSource.isTrusted(this.mContext)) || this.mContext.checkPermission("android.permission.UPDATE_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), null) == 0;
    }

    public final UidState getUidStateLocked(int i, boolean z) {
        UidState uidState = this.mUidStates.get(i);
        if (uidState == null) {
            if (z) {
                UidState uidState2 = new UidState(i);
                this.mUidStates.put(i, uidState2);
                return uidState2;
            }
            return null;
        }
        return uidState;
    }

    public final void updateAppWidgetVisibility(SparseArray<String> sparseArray, boolean z) {
        synchronized (this) {
            getUidStateTracker().updateAppWidgetVisibility(sparseArray, z);
        }
    }

    public final PackageManagerInternal getPackageManagerInternal() {
        if (this.mPackageManagerInternal == null) {
            this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        }
        PackageManagerInternal packageManagerInternal = this.mPackageManagerInternal;
        if (packageManagerInternal != null) {
            return packageManagerInternal;
        }
        throw new IllegalStateException("PackageManagerInternal not loaded");
    }

    public final UserManagerInternal getUserManagerInternal() {
        if (this.mUserManagerInternal == null) {
            this.mUserManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        }
        UserManagerInternal userManagerInternal = this.mUserManagerInternal;
        if (userManagerInternal != null) {
            return userManagerInternal;
        }
        throw new IllegalStateException("UserManagerInternal not loaded");
    }

    public final AppOpsManager.RestrictionBypass getBypassforPackage(PackageState packageState) {
        return new AppOpsManager.RestrictionBypass(packageState.getAppId() == 1000, packageState.isPrivileged(), this.mContext.checkPermission("android.permission.EXEMPT_FROM_AUDIO_RECORD_RESTRICTIONS", -1, packageState.getAppId()) == 0);
    }

    public final PackageVerificationResult verifyAndGetBypass(int i, String str, String str2) {
        return verifyAndGetBypass(i, str, str2, null);
    }

    public final PackageVerificationResult verifyAndGetBypass(int i, String str, String str2, String str3) {
        return verifyAndGetBypass(i, str, str2, str3, false);
    }

    /* JADX WARN: Can't wrap try/catch for region: R(13:(1:48)|49|(1:(1:74)(1:75))(1:52)|53|54|55|56|57|(2:59|(4:61|62|63|64))|68|62|63|64) */
    /* JADX WARN: Removed duplicated region for block: B:180:0x01b1  */
    /* JADX WARN: Removed duplicated region for block: B:184:0x0202  */
    /* JADX WARN: Removed duplicated region for block: B:202:0x003e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final PackageVerificationResult verifyAndGetBypass(int i, String str, String str2, String str3, boolean z) {
        PackageManager packageManager;
        String sdkSandboxPackageName;
        int packageUidAsUser;
        int i2;
        boolean z2;
        AppOpsManager.RestrictionBypass restrictionBypass;
        String str4;
        boolean z3;
        boolean z4;
        Ops ops;
        AppOpsManager.RestrictionBypass restrictionBypass2;
        if (i == 0) {
            return new PackageVerificationResult(null, true);
        }
        if (Process.isSdkSandboxUid(i)) {
            try {
                packageManager = this.mContext.getPackageManager();
                sdkSandboxPackageName = packageManager.getSdkSandboxPackageName();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (Objects.equals(str, sdkSandboxPackageName)) {
                packageUidAsUser = packageManager.getPackageUidAsUser(sdkSandboxPackageName, PackageManager.PackageInfoFlags.of(0L), UserHandle.getUserId(i));
                synchronized (this) {
                    UidState uidState = this.mUidStates.get(packageUidAsUser);
                    if (uidState != null && !uidState.pkgOps.isEmpty() && (ops = uidState.pkgOps.get(str)) != null && ((str2 == null || ops.knownAttributionTags.contains(str2)) && (restrictionBypass2 = ops.bypass) != null)) {
                        return new PackageVerificationResult(restrictionBypass2, ops.validAttributionTags.contains(str2));
                    }
                    int callingUid = Binder.getCallingUid();
                    int resolveUid = Objects.equals(str, "com.android.shell") ? 2000 : resolveUid(str);
                    if (resolveUid != -1) {
                        if (resolveUid != UserHandle.getAppId(packageUidAsUser)) {
                            if (!z) {
                                Slog.e("AppOps", "Bad call made by uid " + callingUid + ". Package \"" + str + "\" does not belong to uid " + packageUidAsUser + ".");
                            }
                            throw new SecurityException("Specified package \"" + str + "\" under uid " + UserHandle.getAppId(packageUidAsUser) + " but it is not");
                        }
                        return new PackageVerificationResult(AppOpsManager.RestrictionBypass.UNRESTRICTED, true);
                    }
                    int userId = UserHandle.getUserId(packageUidAsUser);
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    try {
                        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
                        PackageStateInternal packageStateInternal = packageManagerInternal.getPackageStateInternal(str);
                        AndroidPackage androidPackage = packageStateInternal == null ? null : packageStateInternal.getAndroidPackage();
                        if (androidPackage != null) {
                            z2 = isAttributionInPackage(androidPackage, str2);
                            i2 = UserHandle.getUid(userId, packageStateInternal.getAppId());
                            restrictionBypass = getBypassforPackage(packageStateInternal);
                        } else {
                            i2 = resolveUid;
                            z2 = false;
                            restrictionBypass = null;
                        }
                        if (!z2) {
                            boolean isAttributionInPackage = isAttributionInPackage(str3 != null ? packageManagerInternal.getPackage(str3) : null, str2);
                            if (androidPackage != null && isAttributionInPackage) {
                                str4 = "attributionTag " + str2 + " declared in manifest of the proxy package " + str3 + ", this is not advised";
                            } else if (androidPackage != null) {
                                str4 = "attributionTag " + str2 + " not declared in manifest of " + str;
                            } else {
                                str4 = "package " + str + " not found, can't check for attributionTag " + str2;
                            }
                            try {
                                z3 = isAttributionInPackage;
                            } catch (RemoteException unused) {
                                z3 = isAttributionInPackage;
                            }
                            try {
                                if (this.mPlatformCompat.isChangeEnabledByPackageName(151105954L, str, userId)) {
                                    if (this.mPlatformCompat.isChangeEnabledByUid(151105954L, callingUid)) {
                                        z4 = z3;
                                        Slog.e("AppOps", str4);
                                        z2 = z4;
                                    }
                                }
                                z4 = true;
                                Slog.e("AppOps", str4);
                                z2 = z4;
                            } catch (RemoteException unused2) {
                                z2 = z3;
                                if (i2 == packageUidAsUser) {
                                }
                            }
                        }
                        if (i2 == packageUidAsUser) {
                            if (!z) {
                                Slog.e("AppOps", "Bad call made by uid " + callingUid + ". Package \"" + str + "\" does not belong to uid " + packageUidAsUser + ".");
                            }
                            throw new SecurityException("Specified package \"" + str + "\" under uid " + packageUidAsUser + " but it is not");
                        }
                        return new PackageVerificationResult(restrictionBypass, z2);
                    } finally {
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                    }
                }
            }
        }
        packageUidAsUser = i;
        synchronized (this) {
        }
    }

    public final boolean isAttributionInPackage(AndroidPackage androidPackage, String str) {
        if (androidPackage == null) {
            return false;
        }
        if (str == null) {
            return true;
        }
        if (androidPackage.getAttributions() != null) {
            int size = androidPackage.getAttributions().size();
            for (int i = 0; i < size; i++) {
                if (androidPackage.getAttributions().get(i).getTag().equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public final Ops getOpsLocked(int i, String str, String str2, boolean z, AppOpsManager.RestrictionBypass restrictionBypass, boolean z2) {
        UidState uidStateLocked = getUidStateLocked(i, z2);
        if (uidStateLocked == null) {
            return null;
        }
        Ops ops = uidStateLocked.pkgOps.get(str);
        if (ops == null) {
            if (!z2) {
                return null;
            }
            ops = new Ops(str, uidStateLocked);
            uidStateLocked.pkgOps.put(str, ops);
        }
        if (z2) {
            if (restrictionBypass != null) {
                ops.bypass = restrictionBypass;
            }
            if (str2 != null) {
                ops.knownAttributionTags.add(str2);
                if (z) {
                    ops.validAttributionTags.add(str2);
                } else {
                    ops.validAttributionTags.remove(str2);
                }
            }
        }
        return ops;
    }

    public final void scheduleWriteLocked() {
        if (this.mWriteScheduled) {
            return;
        }
        this.mWriteScheduled = true;
        this.mHandler.postDelayed(this.mWriteRunner, 1800000L);
    }

    public final void scheduleFastWriteLocked() {
        if (this.mFastWriteScheduled) {
            return;
        }
        this.mWriteScheduled = true;
        this.mFastWriteScheduled = true;
        this.mHandler.removeCallbacks(this.mWriteRunner);
        this.mHandler.postDelayed(this.mWriteRunner, 10000L);
    }

    public final C0464Op getOpLocked(int i, int i2, String str, String str2, boolean z, AppOpsManager.RestrictionBypass restrictionBypass, boolean z2) {
        Ops opsLocked = getOpsLocked(i2, str, str2, z, restrictionBypass, z2);
        if (opsLocked == null) {
            return null;
        }
        return getOpLocked(opsLocked, i, i2, z2);
    }

    public final C0464Op getOpLocked(Ops ops, int i, int i2, boolean z) {
        C0464Op c0464Op = ops.get(i);
        if (c0464Op == null) {
            if (!z) {
                return null;
            }
            c0464Op = new C0464Op(ops.uidState, ops.packageName, i, i2);
            ops.put(i, c0464Op);
        }
        if (z) {
            scheduleWriteLocked();
        }
        return c0464Op;
    }

    public final boolean isOpRestrictedDueToSuspend(int i, String str, int i2) {
        if (ArrayUtils.contains(OPS_RESTRICTED_ON_SUSPEND, i)) {
            return ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).isPackageSuspended(str, UserHandle.getUserId(i2));
        }
        return false;
    }

    public final boolean isOpRestrictedLocked(int i, int i2, String str, String str2, AppOpsManager.RestrictionBypass restrictionBypass, boolean z) {
        int size = this.mOpGlobalRestrictions.size();
        for (int i3 = 0; i3 < size; i3++) {
            if (this.mOpGlobalRestrictions.valueAt(i3).hasRestriction(i2)) {
                return true;
            }
        }
        int userId = UserHandle.getUserId(i);
        int size2 = this.mOpUserRestrictions.size();
        for (int i4 = 0; i4 < size2; i4++) {
            if (this.mOpUserRestrictions.valueAt(i4).hasRestriction(i2, str, str2, userId, z)) {
                AppOpsManager.RestrictionBypass opAllowSystemBypassRestriction = AppOpsManager.opAllowSystemBypassRestriction(i2);
                if (opAllowSystemBypassRestriction != null) {
                    synchronized (this) {
                        if (opAllowSystemBypassRestriction.isSystemUid && restrictionBypass != null && restrictionBypass.isSystemUid) {
                            return false;
                        }
                        if (opAllowSystemBypassRestriction.isPrivileged && restrictionBypass != null && restrictionBypass.isPrivileged) {
                            return false;
                        }
                        if (opAllowSystemBypassRestriction.isRecordAudioRestrictionExcept && restrictionBypass != null && restrictionBypass.isRecordAudioRestrictionExcept) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public final void readRecentAccesses() {
        if (!this.mRecentAccessesFile.exists()) {
            readRecentAccesses(this.mStorageFile);
        } else {
            readRecentAccesses(this.mRecentAccessesFile);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:130:0x0076, code lost:
        r0.close();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void readRecentAccesses(AtomicFile atomicFile) {
        TypedXmlPullParser resolvePullParser;
        int next;
        synchronized (atomicFile) {
            synchronized (this) {
                try {
                    FileInputStream openRead = atomicFile.openRead();
                    this.mUidStates.clear();
                    this.mAppOpsCheckingService.clearAllModes();
                    try {
                        try {
                            try {
                                try {
                                    try {
                                        resolvePullParser = Xml.resolvePullParser(openRead);
                                        while (true) {
                                            next = resolvePullParser.next();
                                            if (next == 2 || next == 1) {
                                                break;
                                            }
                                        }
                                    } catch (IllegalStateException e) {
                                        Slog.w("AppOps", "Failed parsing " + e);
                                        this.mUidStates.clear();
                                        this.mAppOpsCheckingService.clearAllModes();
                                    }
                                } catch (NullPointerException e2) {
                                    Slog.w("AppOps", "Failed parsing " + e2);
                                    this.mUidStates.clear();
                                    this.mAppOpsCheckingService.clearAllModes();
                                }
                            } catch (NumberFormatException e3) {
                                Slog.w("AppOps", "Failed parsing " + e3);
                                this.mUidStates.clear();
                                this.mAppOpsCheckingService.clearAllModes();
                            }
                        } catch (IndexOutOfBoundsException e4) {
                            Slog.w("AppOps", "Failed parsing " + e4);
                            this.mUidStates.clear();
                            this.mAppOpsCheckingService.clearAllModes();
                        }
                    } catch (IOException e5) {
                        Slog.w("AppOps", "Failed parsing " + e5);
                        this.mUidStates.clear();
                        this.mAppOpsCheckingService.clearAllModes();
                    } catch (XmlPullParserException e6) {
                        Slog.w("AppOps", "Failed parsing " + e6);
                        this.mUidStates.clear();
                        this.mAppOpsCheckingService.clearAllModes();
                    }
                    if (next != 2) {
                        throw new IllegalStateException("no start tag found");
                    }
                    int depth = resolvePullParser.getDepth();
                    while (true) {
                        int next2 = resolvePullParser.next();
                        if (next2 != 1 && (next2 != 3 || resolvePullParser.getDepth() > depth)) {
                            if (next2 != 3 && next2 != 4) {
                                String name = resolvePullParser.getName();
                                if (name.equals("pkg")) {
                                    readPackage(resolvePullParser);
                                } else if (name.equals("uid")) {
                                    XmlUtils.skipCurrentTag(resolvePullParser);
                                } else {
                                    Slog.w("AppOps", "Unknown element under <app-ops>: " + resolvePullParser.getName());
                                    XmlUtils.skipCurrentTag(resolvePullParser);
                                }
                            }
                        }
                        try {
                            break;
                        } catch (IOException unused) {
                            return;
                        }
                    }
                } catch (FileNotFoundException unused2) {
                    Slog.i("AppOps", "No existing app ops " + atomicFile.getBaseFile() + "; starting empty");
                }
            }
        }
    }

    public final void readPackage(TypedXmlPullParser typedXmlPullParser) throws NumberFormatException, XmlPullParserException, IOException {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "n");
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("uid")) {
                    readUid(typedXmlPullParser, attributeValue);
                } else {
                    Slog.w("AppOps", "Unknown element under <pkg>: " + typedXmlPullParser.getName());
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
    }

    public final void readUid(TypedXmlPullParser typedXmlPullParser, String str) throws NumberFormatException, XmlPullParserException, IOException {
        UidState uidStateLocked = getUidStateLocked(typedXmlPullParser.getAttributeInt((String) null, "n"), true);
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("op")) {
                    readOp(typedXmlPullParser, uidStateLocked, str);
                } else {
                    Slog.w("AppOps", "Unknown element under <pkg>: " + typedXmlPullParser.getName());
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
    }

    public final void readAttributionOp(TypedXmlPullParser typedXmlPullParser, C0464Op c0464Op, String str) throws NumberFormatException, IOException, XmlPullParserException {
        long j;
        AttributedOp orCreateAttribution = c0464Op.getOrCreateAttribution(c0464Op, str);
        long attributeLong = typedXmlPullParser.getAttributeLong((String) null, "n");
        int extractUidStateFromKey = AppOpsManager.extractUidStateFromKey(attributeLong);
        int extractFlagsFromKey = AppOpsManager.extractFlagsFromKey(attributeLong);
        long attributeLong2 = typedXmlPullParser.getAttributeLong((String) null, "t", 0L);
        long attributeLong3 = typedXmlPullParser.getAttributeLong((String) null, "r", 0L);
        long attributeLong4 = typedXmlPullParser.getAttributeLong((String) null, "d", -1L);
        String readStringAttribute = XmlUtils.readStringAttribute(typedXmlPullParser, "pp");
        int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "pu", -1);
        String readStringAttribute2 = XmlUtils.readStringAttribute(typedXmlPullParser, "pc");
        if (attributeLong2 > 0) {
            orCreateAttribution.accessed(attributeLong2, attributeLong4, attributeInt, readStringAttribute, readStringAttribute2, extractUidStateFromKey, extractFlagsFromKey);
            j = attributeLong3;
        } else {
            j = attributeLong3;
        }
        if (j > 0) {
            orCreateAttribution.rejected(j, extractUidStateFromKey, extractFlagsFromKey);
        }
    }

    public final void readOp(TypedXmlPullParser typedXmlPullParser, UidState uidState, String str) throws NumberFormatException, XmlPullParserException, IOException {
        C0464Op c0464Op = new C0464Op(uidState, str, typedXmlPullParser.getAttributeInt((String) null, "n"), uidState.uid);
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("st")) {
                    readAttributionOp(typedXmlPullParser, c0464Op, XmlUtils.readStringAttribute(typedXmlPullParser, "id"));
                } else {
                    Slog.w("AppOps", "Unknown element under <op>: " + typedXmlPullParser.getName());
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
        Ops ops = uidState.pkgOps.get(str);
        if (ops == null) {
            ops = new Ops(str, uidState);
            uidState.pkgOps.put(str, ops);
        }
        ops.put(c0464Op.f1125op, c0464Op);
    }

    @VisibleForTesting
    public void writeRecentAccesses() {
        AtomicFile atomicFile;
        FileOutputStream fileOutputStream;
        FileOutputStream fileOutputStream2;
        int i;
        AppOpsManager.AttributedOpEntry attributedOpEntry;
        ArraySet arraySet;
        String str;
        String str2;
        String str3;
        AppOpsService appOpsService = this;
        AtomicFile atomicFile2 = appOpsService.mRecentAccessesFile;
        synchronized (atomicFile2) {
            try {
                try {
                    try {
                        FileOutputStream startWrite = appOpsService.mRecentAccessesFile.startWrite();
                        String str4 = null;
                        List<AppOpsManager.PackageOps> packagesForOps = appOpsService.getPackagesForOps(null);
                        try {
                            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
                            resolveSerializer.startDocument((String) null, Boolean.TRUE);
                            resolveSerializer.startTag((String) null, "app-ops");
                            resolveSerializer.attributeInt((String) null, "v", 1);
                            if (packagesForOps != null) {
                                String str5 = null;
                                int i2 = 0;
                                while (i2 < packagesForOps.size()) {
                                    try {
                                        AppOpsManager.PackageOps packageOps = packagesForOps.get(i2);
                                        if (!Objects.equals(packageOps.getPackageName(), str5)) {
                                            if (str5 != null) {
                                                resolveSerializer.endTag(str4, "pkg");
                                            }
                                            str5 = packageOps.getPackageName();
                                            if (str5 != null) {
                                                resolveSerializer.startTag(str4, "pkg");
                                                resolveSerializer.attribute(str4, "n", str5);
                                            }
                                        }
                                        resolveSerializer.startTag(str4, "uid");
                                        resolveSerializer.attributeInt(str4, "n", packageOps.getUid());
                                        List ops = packageOps.getOps();
                                        int i3 = 0;
                                        while (i3 < ops.size()) {
                                            AppOpsManager.OpEntry opEntry = (AppOpsManager.OpEntry) ops.get(i3);
                                            resolveSerializer.startTag(str4, "op");
                                            resolveSerializer.attributeInt(str4, "n", opEntry.getOp());
                                            if (opEntry.getMode() != AppOpsManager.opToDefaultMode(opEntry.getOp())) {
                                                resolveSerializer.attributeInt(str4, "m", opEntry.getMode());
                                            }
                                            Iterator it = opEntry.getAttributedOpEntries().keySet().iterator();
                                            while (it.hasNext()) {
                                                String str6 = (String) it.next();
                                                AppOpsManager.AttributedOpEntry attributedOpEntry2 = (AppOpsManager.AttributedOpEntry) opEntry.getAttributedOpEntries().get(str6);
                                                ArraySet collectKeys = attributedOpEntry2.collectKeys();
                                                int size = collectKeys.size();
                                                int i4 = 0;
                                                while (i4 < size) {
                                                    String str7 = str5;
                                                    List list = ops;
                                                    long longValue = ((Long) collectKeys.valueAt(i4)).longValue();
                                                    List<AppOpsManager.PackageOps> list2 = packagesForOps;
                                                    int extractUidStateFromKey = AppOpsManager.extractUidStateFromKey(longValue);
                                                    int i5 = size;
                                                    int extractFlagsFromKey = AppOpsManager.extractFlagsFromKey(longValue);
                                                    AppOpsManager.OpEntry opEntry2 = opEntry;
                                                    Iterator it2 = it;
                                                    long lastAccessTime = attributedOpEntry2.getLastAccessTime(extractUidStateFromKey, extractUidStateFromKey, extractFlagsFromKey);
                                                    atomicFile = atomicFile2;
                                                    try {
                                                        long lastRejectTime = attributedOpEntry2.getLastRejectTime(extractUidStateFromKey, extractUidStateFromKey, extractFlagsFromKey);
                                                        int i6 = i2;
                                                        long lastDuration = attributedOpEntry2.getLastDuration(extractUidStateFromKey, extractUidStateFromKey, extractFlagsFromKey);
                                                        AppOpsManager.OpEventProxyInfo lastProxyInfo = attributedOpEntry2.getLastProxyInfo(extractUidStateFromKey, extractUidStateFromKey, extractFlagsFromKey);
                                                        int i7 = (lastAccessTime > 0L ? 1 : (lastAccessTime == 0L ? 0 : -1));
                                                        if (i7 > 0 || lastRejectTime > 0 || lastDuration > 0 || lastProxyInfo != null) {
                                                            if (lastProxyInfo != null) {
                                                                str = lastProxyInfo.getPackageName();
                                                                String attributionTag = lastProxyInfo.getAttributionTag();
                                                                i = lastProxyInfo.getUid();
                                                                arraySet = collectKeys;
                                                                attributedOpEntry = attributedOpEntry2;
                                                                str2 = attributionTag;
                                                            } else {
                                                                i = -1;
                                                                attributedOpEntry = attributedOpEntry2;
                                                                arraySet = collectKeys;
                                                                str = null;
                                                                str2 = null;
                                                            }
                                                            fileOutputStream2 = startWrite;
                                                            try {
                                                                resolveSerializer.startTag((String) null, "st");
                                                                if (str6 != null) {
                                                                    resolveSerializer.attribute((String) null, "id", str6);
                                                                }
                                                                String str8 = str2;
                                                                str3 = str6;
                                                                resolveSerializer.attributeLong((String) null, "n", longValue);
                                                                if (i7 > 0) {
                                                                    resolveSerializer.attributeLong((String) null, "t", lastAccessTime);
                                                                }
                                                                if (lastRejectTime > 0) {
                                                                    resolveSerializer.attributeLong((String) null, "r", lastRejectTime);
                                                                }
                                                                if (lastDuration > 0) {
                                                                    resolveSerializer.attributeLong((String) null, "d", lastDuration);
                                                                }
                                                                if (str != null) {
                                                                    resolveSerializer.attribute((String) null, "pp", str);
                                                                }
                                                                if (str8 != null) {
                                                                    resolveSerializer.attribute((String) null, "pc", str8);
                                                                }
                                                                if (i >= 0) {
                                                                    resolveSerializer.attributeInt((String) null, "pu", i);
                                                                }
                                                                resolveSerializer.endTag((String) null, "st");
                                                            } catch (IOException e) {
                                                                e = e;
                                                                appOpsService = this;
                                                                fileOutputStream = fileOutputStream2;
                                                                Slog.w("AppOps", "Failed to write state, restoring backup.", e);
                                                                appOpsService.mRecentAccessesFile.failWrite(fileOutputStream);
                                                                appOpsService.mHistoricalRegistry.writeAndClearDiscreteHistory();
                                                            }
                                                        } else {
                                                            fileOutputStream2 = startWrite;
                                                            str3 = str6;
                                                            attributedOpEntry = attributedOpEntry2;
                                                            arraySet = collectKeys;
                                                        }
                                                        i4++;
                                                        packagesForOps = list2;
                                                        str5 = str7;
                                                        ops = list;
                                                        size = i5;
                                                        opEntry = opEntry2;
                                                        it = it2;
                                                        atomicFile2 = atomicFile;
                                                        i2 = i6;
                                                        str6 = str3;
                                                        attributedOpEntry2 = attributedOpEntry;
                                                        collectKeys = arraySet;
                                                        startWrite = fileOutputStream2;
                                                    } catch (IOException e2) {
                                                        e = e2;
                                                        appOpsService = this;
                                                        fileOutputStream = startWrite;
                                                        Slog.w("AppOps", "Failed to write state, restoring backup.", e);
                                                        appOpsService.mRecentAccessesFile.failWrite(fileOutputStream);
                                                        appOpsService.mHistoricalRegistry.writeAndClearDiscreteHistory();
                                                    }
                                                }
                                            }
                                            resolveSerializer.endTag((String) null, "op");
                                            i3++;
                                            packagesForOps = packagesForOps;
                                            str5 = str5;
                                            ops = ops;
                                            atomicFile2 = atomicFile2;
                                            i2 = i2;
                                            startWrite = startWrite;
                                            str4 = null;
                                        }
                                        resolveSerializer.endTag((String) null, "uid");
                                        i2++;
                                        appOpsService = this;
                                        packagesForOps = packagesForOps;
                                        str5 = str5;
                                        atomicFile2 = atomicFile2;
                                        startWrite = startWrite;
                                        str4 = null;
                                    } catch (IOException e3) {
                                        e = e3;
                                        atomicFile = atomicFile2;
                                    }
                                }
                                atomicFile = atomicFile2;
                                fileOutputStream2 = startWrite;
                                if (str5 != null) {
                                    resolveSerializer.endTag((String) null, "pkg");
                                }
                            } else {
                                atomicFile = atomicFile2;
                                fileOutputStream2 = startWrite;
                            }
                            resolveSerializer.endTag((String) null, "app-ops");
                            resolveSerializer.endDocument();
                            appOpsService = this;
                        } catch (IOException e4) {
                            e = e4;
                            atomicFile = atomicFile2;
                        }
                        try {
                            fileOutputStream = fileOutputStream2;
                        } catch (IOException e5) {
                            e = e5;
                            fileOutputStream = fileOutputStream2;
                            Slog.w("AppOps", "Failed to write state, restoring backup.", e);
                            appOpsService.mRecentAccessesFile.failWrite(fileOutputStream);
                            appOpsService.mHistoricalRegistry.writeAndClearDiscreteHistory();
                        }
                        try {
                            appOpsService.mRecentAccessesFile.finishWrite(fileOutputStream);
                        } catch (IOException e6) {
                            e = e6;
                            Slog.w("AppOps", "Failed to write state, restoring backup.", e);
                            appOpsService.mRecentAccessesFile.failWrite(fileOutputStream);
                            appOpsService.mHistoricalRegistry.writeAndClearDiscreteHistory();
                        }
                        appOpsService.mHistoricalRegistry.writeAndClearDiscreteHistory();
                    } catch (Throwable th) {
                        th = th;
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    AtomicFile atomicFile3 = atomicFile2;
                    throw th;
                }
            } catch (IOException e7) {
                Slog.w("AppOps", "Failed to write state: " + e7);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class Shell extends ShellCommand {
        public static final Binder sBinder = new Binder();
        public String attributionTag;
        public final IAppOpsService mInterface;
        public final AppOpsService mInternal;
        public int mode;
        public String modeStr;
        public int nonpackageUid;

        /* renamed from: op */
        public int f1126op;
        public String opStr;
        public String packageName;
        public int packageUid;
        public boolean targetsUid;
        public int userId = 0;
        public IBinder mToken = AppOpsManager.getClientId();

        public Shell(IAppOpsService iAppOpsService, AppOpsService appOpsService) {
            this.mInterface = iAppOpsService;
            this.mInternal = appOpsService;
        }

        public int onCommand(String str) {
            return AppOpsService.onShellCommand(this, str);
        }

        public void onHelp() {
            AppOpsService.dumpCommandHelp(getOutPrintWriter());
        }

        public static int strOpToOp(String str, PrintWriter printWriter) {
            try {
                try {
                    try {
                        return AppOpsManager.strOpToOp(str);
                    } catch (IllegalArgumentException unused) {
                        return Integer.parseInt(str);
                    }
                } catch (IllegalArgumentException e) {
                    printWriter.println("Error: " + e.getMessage());
                    return -1;
                }
            } catch (NumberFormatException unused2) {
                return AppOpsManager.strDebugOpToOp(str);
            }
        }

        public static int strModeToMode(String str, PrintWriter printWriter) {
            for (int length = AppOpsManager.MODE_NAMES.length - 1; length >= 0; length--) {
                if (AppOpsManager.MODE_NAMES[length].equals(str)) {
                    return length;
                }
            }
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException unused) {
                printWriter.println("Error: Mode " + str + " is not valid");
                return -1;
            }
        }

        public int parseUserOpMode(int i, PrintWriter printWriter) throws RemoteException {
            this.userId = -2;
            this.opStr = null;
            this.modeStr = null;
            while (true) {
                String nextArg = getNextArg();
                if (nextArg == null) {
                    break;
                } else if ("--user".equals(nextArg)) {
                    this.userId = UserHandle.parseUserArg(getNextArgRequired());
                } else if (this.opStr == null) {
                    this.opStr = nextArg;
                } else if (this.modeStr == null) {
                    this.modeStr = nextArg;
                    break;
                }
            }
            String str = this.opStr;
            if (str == null) {
                printWriter.println("Error: Operation not specified.");
                return -1;
            }
            int strOpToOp = strOpToOp(str, printWriter);
            this.f1126op = strOpToOp;
            if (strOpToOp < 0) {
                return -1;
            }
            String str2 = this.modeStr;
            if (str2 != null) {
                int strModeToMode = strModeToMode(str2, printWriter);
                this.mode = strModeToMode;
                return strModeToMode < 0 ? -1 : 0;
            }
            this.mode = i;
            return 0;
        }

        /* JADX WARN: Code restructure failed: missing block: B:169:0x00ca, code lost:
            if (r10 >= r9.packageName.length()) goto L83;
         */
        /* JADX WARN: Code restructure failed: missing block: B:171:0x00d2, code lost:
            r2 = java.lang.Integer.parseInt(r9.packageName.substring(1, r10));
            r7 = r9.packageName.charAt(r10);
            r10 = r10 + 1;
            r3 = r10;
         */
        /* JADX WARN: Code restructure failed: missing block: B:173:0x00e4, code lost:
            if (r3 >= r9.packageName.length()) goto L79;
         */
        /* JADX WARN: Code restructure failed: missing block: B:175:0x00ec, code lost:
            if (r9.packageName.charAt(r3) < '0') goto L78;
         */
        /* JADX WARN: Code restructure failed: missing block: B:177:0x00f4, code lost:
            if (r9.packageName.charAt(r3) > '9') goto L70;
         */
        /* JADX WARN: Code restructure failed: missing block: B:178:0x00f6, code lost:
            r3 = r3 + 1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:179:0x00f9, code lost:
            if (r3 <= r10) goto L83;
         */
        /* JADX WARN: Code restructure failed: missing block: B:180:0x00fb, code lost:
            r10 = java.lang.Integer.parseInt(r9.packageName.substring(r10, r3));
         */
        /* JADX WARN: Code restructure failed: missing block: B:181:0x0107, code lost:
            if (r7 != 'a') goto L75;
         */
        /* JADX WARN: Code restructure failed: missing block: B:182:0x0109, code lost:
            r9.nonpackageUid = android.os.UserHandle.getUid(r2, r10 + com.android.internal.util.FrameworkStatsLog.WIFI_BYTES_TRANSFER);
         */
        /* JADX WARN: Code restructure failed: missing block: B:184:0x0114, code lost:
            if (r7 != 's') goto L83;
         */
        /* JADX WARN: Code restructure failed: missing block: B:185:0x0116, code lost:
            r9.nonpackageUid = android.os.UserHandle.getUid(r2, r10);
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public int parseUserPackageOp(boolean z, PrintWriter printWriter) throws RemoteException {
            this.userId = -2;
            this.packageName = null;
            this.opStr = null;
            while (true) {
                String nextArg = getNextArg();
                if (nextArg == null) {
                    break;
                } else if ("--user".equals(nextArg)) {
                    this.userId = UserHandle.parseUserArg(getNextArgRequired());
                } else if ("--uid".equals(nextArg)) {
                    this.targetsUid = true;
                } else if ("--attribution".equals(nextArg)) {
                    this.attributionTag = getNextArgRequired();
                } else if (this.packageName == null) {
                    this.packageName = nextArg;
                } else if (this.opStr == null) {
                    this.opStr = nextArg;
                    break;
                }
            }
            if (this.packageName == null) {
                printWriter.println("Error: Package name not specified.");
                return -1;
            }
            String str = this.opStr;
            if (str == null && z) {
                printWriter.println("Error: Operation not specified.");
                return -1;
            }
            if (str != null) {
                int strOpToOp = strOpToOp(str, printWriter);
                this.f1126op = strOpToOp;
                if (strOpToOp < 0) {
                    return -1;
                }
            } else {
                this.f1126op = -1;
            }
            if (this.userId == -2) {
                this.userId = ActivityManager.getCurrentUser();
            }
            this.nonpackageUid = -1;
            try {
                this.nonpackageUid = Integer.parseInt(this.packageName);
            } catch (NumberFormatException unused) {
            }
            if (this.nonpackageUid == -1 && this.packageName.length() > 1 && this.packageName.charAt(0) == 'u' && this.packageName.indexOf(46) < 0) {
                int i = 1;
                while (i < this.packageName.length() && this.packageName.charAt(i) >= '0' && this.packageName.charAt(i) <= '9') {
                    i++;
                }
            }
            if (this.nonpackageUid != -1) {
                this.packageName = null;
            } else {
                int resolveUid = AppOpsService.resolveUid(this.packageName);
                this.packageUid = resolveUid;
                if (resolveUid < 0) {
                    this.packageUid = AppGlobals.getPackageManager().getPackageUid(this.packageName, 8192L, this.userId);
                }
                if (this.packageUid < 0) {
                    printWriter.println("Error: No UID for " + this.packageName + " in user " + this.userId);
                    return -1;
                }
            }
            return 0;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new Shell(this, this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public static void dumpCommandHelp(PrintWriter printWriter) {
        printWriter.println("AppOps service (appops) commands:");
        printWriter.println("  help");
        printWriter.println("    Print this help text.");
        printWriter.println("  start [--user <USER_ID>] [--attribution <ATTRIBUTION_TAG>] <PACKAGE | UID> <OP> ");
        printWriter.println("    Starts a given operation for a particular application.");
        printWriter.println("  stop [--user <USER_ID>] [--attribution <ATTRIBUTION_TAG>] <PACKAGE | UID> <OP> ");
        printWriter.println("    Stops a given operation for a particular application.");
        printWriter.println("  set [--user <USER_ID>] <[--uid] PACKAGE | UID> <OP> <MODE>");
        printWriter.println("    Set the mode for a particular application and operation.");
        printWriter.println("  get [--user <USER_ID>] [--attribution <ATTRIBUTION_TAG>] <PACKAGE | UID> [<OP>]");
        printWriter.println("    Return the mode for a particular application and optional operation.");
        printWriter.println("  query-op [--user <USER_ID>] <OP> [<MODE>]");
        printWriter.println("    Print all packages that currently have the given op in the given mode.");
        printWriter.println("  reset [--user <USER_ID>] [<PACKAGE>]");
        printWriter.println("    Reset the given application or all applications to default modes.");
        printWriter.println("  write-settings");
        printWriter.println("    Immediately write pending changes to storage.");
        printWriter.println("  read-settings");
        printWriter.println("    Read the last written settings, replacing current state in RAM.");
        printWriter.println("  options:");
        printWriter.println("    <PACKAGE> an Android package name or its UID if prefixed by --uid");
        printWriter.println("    <OP>      an AppOps operation.");
        printWriter.println("    <MODE>    one of allow, ignore, deny, or default");
        printWriter.println("    <USER_ID> the user id under which the package is installed. If --user is");
        printWriter.println("              not specified, the current user is assumed.");
    }

    public static int onShellCommand(Shell shell, String str) {
        char c;
        String str2;
        List list;
        Object[] objArr;
        if (str == null) {
            return shell.handleDefaultCommands(str);
        }
        PrintWriter outPrintWriter = shell.getOutPrintWriter();
        PrintWriter errPrintWriter = shell.getErrPrintWriter();
        try {
            int i = 0;
            switch (str.hashCode()) {
                case -1703718319:
                    if (str.equals("write-settings")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case -1166702330:
                    if (str.equals("query-op")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 102230:
                    if (str.equals("get")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 113762:
                    if (str.equals("set")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 3540994:
                    if (str.equals("stop")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                case 108404047:
                    if (str.equals("reset")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 109757538:
                    if (str.equals("start")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 2085703290:
                    if (str.equals("read-settings")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            String str3 = null;
            switch (c) {
                case 0:
                    int parseUserPackageOp = shell.parseUserPackageOp(true, errPrintWriter);
                    if (parseUserPackageOp < 0) {
                        return parseUserPackageOp;
                    }
                    String nextArg = shell.getNextArg();
                    if (nextArg == null) {
                        errPrintWriter.println("Error: Mode not specified.");
                        return -1;
                    }
                    int strModeToMode = Shell.strModeToMode(nextArg, errPrintWriter);
                    if (strModeToMode < 0) {
                        return -1;
                    }
                    boolean z = shell.targetsUid;
                    if (!z && (str2 = shell.packageName) != null) {
                        shell.mInterface.setMode(shell.f1126op, shell.packageUid, str2, strModeToMode);
                    } else if (z && shell.packageName != null) {
                        try {
                            shell.mInterface.setUidMode(shell.f1126op, shell.mInternal.mContext.getPackageManager().getPackageUidAsUser(shell.packageName, shell.userId), strModeToMode);
                        } catch (PackageManager.NameNotFoundException unused) {
                            return -1;
                        }
                    } else {
                        shell.mInterface.setUidMode(shell.f1126op, shell.nonpackageUid, strModeToMode);
                    }
                    return 0;
                case 1:
                    int parseUserPackageOp2 = shell.parseUserPackageOp(false, errPrintWriter);
                    if (parseUserPackageOp2 < 0) {
                        return parseUserPackageOp2;
                    }
                    List arrayList = new ArrayList();
                    if (shell.packageName != null) {
                        IAppOpsService iAppOpsService = shell.mInterface;
                        int i2 = shell.packageUid;
                        int i3 = shell.f1126op;
                        List uidOps = iAppOpsService.getUidOps(i2, i3 != -1 ? new int[]{i3} : null);
                        if (uidOps != null) {
                            arrayList.addAll(uidOps);
                        }
                        IAppOpsService iAppOpsService2 = shell.mInterface;
                        int i4 = shell.packageUid;
                        String str4 = shell.packageName;
                        int i5 = shell.f1126op;
                        List opsForPackage = iAppOpsService2.getOpsForPackage(i4, str4, i5 != -1 ? new int[]{i5} : null);
                        if (opsForPackage != null) {
                            arrayList.addAll(opsForPackage);
                        }
                    } else {
                        IAppOpsService iAppOpsService3 = shell.mInterface;
                        int i6 = shell.nonpackageUid;
                        int i7 = shell.f1126op;
                        arrayList = iAppOpsService3.getUidOps(i6, i7 != -1 ? new int[]{i7} : null);
                    }
                    if (arrayList != null && arrayList.size() > 0) {
                        long currentTimeMillis = System.currentTimeMillis();
                        int i8 = 0;
                        while (i8 < arrayList.size()) {
                            AppOpsManager.PackageOps packageOps = (AppOpsManager.PackageOps) arrayList.get(i8);
                            if (packageOps.getPackageName() == null) {
                                outPrintWriter.print("Uid mode: ");
                            }
                            int i9 = i;
                            for (List ops = packageOps.getOps(); i9 < ops.size(); ops = list) {
                                AppOpsManager.OpEntry opEntry = (AppOpsManager.OpEntry) ops.get(i9);
                                outPrintWriter.print(AppOpsManager.opToName(opEntry.getOp()));
                                outPrintWriter.print(": ");
                                outPrintWriter.print(AppOpsManager.modeToName(opEntry.getMode()));
                                if (shell.attributionTag == null) {
                                    if (opEntry.getLastAccessTime(31) != -1) {
                                        outPrintWriter.print("; time=");
                                        list = ops;
                                        TimeUtils.formatDuration(currentTimeMillis - opEntry.getLastAccessTime(31), outPrintWriter);
                                        outPrintWriter.print(" ago");
                                    } else {
                                        list = ops;
                                    }
                                    if (opEntry.getLastRejectTime(31) != -1) {
                                        outPrintWriter.print("; rejectTime=");
                                        TimeUtils.formatDuration(currentTimeMillis - opEntry.getLastRejectTime(31), outPrintWriter);
                                        outPrintWriter.print(" ago");
                                    }
                                    if (opEntry.isRunning()) {
                                        outPrintWriter.print(" (running)");
                                    } else if (opEntry.getLastDuration(31) != -1) {
                                        outPrintWriter.print("; duration=");
                                        TimeUtils.formatDuration(opEntry.getLastDuration(31), outPrintWriter);
                                    }
                                } else {
                                    list = ops;
                                    AppOpsManager.AttributedOpEntry attributedOpEntry = (AppOpsManager.AttributedOpEntry) opEntry.getAttributedOpEntries().get(shell.attributionTag);
                                    if (attributedOpEntry != null) {
                                        if (attributedOpEntry.getLastAccessTime(31) != -1) {
                                            outPrintWriter.print("; time=");
                                            TimeUtils.formatDuration(currentTimeMillis - attributedOpEntry.getLastAccessTime(31), outPrintWriter);
                                            outPrintWriter.print(" ago");
                                        }
                                        if (attributedOpEntry.getLastRejectTime(31) != -1) {
                                            outPrintWriter.print("; rejectTime=");
                                            TimeUtils.formatDuration(currentTimeMillis - attributedOpEntry.getLastRejectTime(31), outPrintWriter);
                                            outPrintWriter.print(" ago");
                                        }
                                        if (attributedOpEntry.isRunning()) {
                                            outPrintWriter.print(" (running)");
                                        } else if (attributedOpEntry.getLastDuration(31) != -1) {
                                            outPrintWriter.print("; duration=");
                                            TimeUtils.formatDuration(attributedOpEntry.getLastDuration(31), outPrintWriter);
                                        }
                                    }
                                }
                                outPrintWriter.println();
                                i9++;
                            }
                            i8++;
                            i = 0;
                        }
                        return i;
                    }
                    outPrintWriter.println("No operations.");
                    int i10 = shell.f1126op;
                    if (i10 <= -1 || i10 >= 134) {
                        return 0;
                    }
                    outPrintWriter.println("Default mode: " + AppOpsManager.modeToName(AppOpsManager.opToDefaultMode(shell.f1126op)));
                    return 0;
                case 2:
                    int parseUserOpMode = shell.parseUserOpMode(1, errPrintWriter);
                    if (parseUserOpMode < 0) {
                        return parseUserOpMode;
                    }
                    List packagesForOps = shell.mInterface.getPackagesForOps(new int[]{shell.f1126op});
                    if (packagesForOps != null && packagesForOps.size() > 0) {
                        for (int i11 = 0; i11 < packagesForOps.size(); i11++) {
                            AppOpsManager.PackageOps packageOps2 = (AppOpsManager.PackageOps) packagesForOps.get(i11);
                            List ops2 = ((AppOpsManager.PackageOps) packagesForOps.get(i11)).getOps();
                            int i12 = 0;
                            while (true) {
                                if (i12 < ops2.size()) {
                                    AppOpsManager.OpEntry opEntry2 = (AppOpsManager.OpEntry) ops2.get(i12);
                                    if (opEntry2.getOp() == shell.f1126op && opEntry2.getMode() == shell.mode) {
                                        objArr = 1;
                                    } else {
                                        i12++;
                                    }
                                } else {
                                    objArr = null;
                                }
                            }
                            if (objArr != null) {
                                outPrintWriter.println(packageOps2.getPackageName());
                            }
                        }
                        return 0;
                    }
                    outPrintWriter.println("No operations.");
                    return 0;
                case 3:
                    int i13 = -2;
                    while (true) {
                        String nextArg2 = shell.getNextArg();
                        if (nextArg2 != null) {
                            if ("--user".equals(nextArg2)) {
                                i13 = UserHandle.parseUserArg(shell.getNextArgRequired());
                            } else if (str3 != null) {
                                errPrintWriter.println("Error: Unsupported argument: " + nextArg2);
                                return -1;
                            } else {
                                str3 = nextArg2;
                            }
                        } else {
                            if (i13 == -2) {
                                i13 = ActivityManager.getCurrentUser();
                            }
                            shell.mInterface.resetAllModes(i13, str3);
                            outPrintWriter.print("Reset all modes for: ");
                            if (i13 == -1) {
                                outPrintWriter.print("all users");
                            } else {
                                outPrintWriter.print("user ");
                                outPrintWriter.print(i13);
                            }
                            outPrintWriter.print(", ");
                            if (str3 == null) {
                                outPrintWriter.println("all packages");
                            } else {
                                outPrintWriter.print("package ");
                                outPrintWriter.println(str3);
                            }
                            return 0;
                        }
                    }
                case 4:
                    shell.mInternal.enforceManageAppOpsModes(Binder.getCallingPid(), Binder.getCallingUid(), -1);
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    synchronized (shell.mInternal) {
                        AppOpsService appOpsService = shell.mInternal;
                        appOpsService.mHandler.removeCallbacks(appOpsService.mWriteRunner);
                    }
                    shell.mInternal.writeRecentAccesses();
                    shell.mInternal.mAppOpsCheckingService.writeState();
                    outPrintWriter.println("Current settings written.");
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return 0;
                case 5:
                    shell.mInternal.enforceManageAppOpsModes(Binder.getCallingPid(), Binder.getCallingUid(), -1);
                    long clearCallingIdentity2 = Binder.clearCallingIdentity();
                    shell.mInternal.readRecentAccesses();
                    shell.mInternal.mAppOpsCheckingService.readState();
                    outPrintWriter.println("Last settings read.");
                    Binder.restoreCallingIdentity(clearCallingIdentity2);
                    return 0;
                case 6:
                    int parseUserPackageOp3 = shell.parseUserPackageOp(true, errPrintWriter);
                    if (parseUserPackageOp3 < 0) {
                        return parseUserPackageOp3;
                    }
                    String str5 = shell.packageName;
                    if (str5 != null) {
                        shell.mInterface.startOperation(shell.mToken, shell.f1126op, shell.packageUid, str5, shell.attributionTag, true, true, "appops start shell command", true, 1, -1);
                        return 0;
                    }
                    return -1;
                case 7:
                    int parseUserPackageOp4 = shell.parseUserPackageOp(true, errPrintWriter);
                    if (parseUserPackageOp4 < 0) {
                        return parseUserPackageOp4;
                    }
                    String str6 = shell.packageName;
                    if (str6 != null) {
                        shell.mInterface.finishOperation(shell.mToken, shell.f1126op, shell.packageUid, str6, shell.attributionTag);
                        return 0;
                    }
                    return -1;
                default:
                    return shell.handleDefaultCommands(str);
            }
        } catch (RemoteException e) {
            outPrintWriter.println("Remote exception: " + e);
            return -1;
        }
    }

    public final void dumpHelp(PrintWriter printWriter) {
        printWriter.println("AppOps service (appops) dump options:");
        printWriter.println("  -h");
        printWriter.println("    Print this help text.");
        printWriter.println("  --op [OP]");
        printWriter.println("    Limit output to data associated with the given app op code.");
        printWriter.println("  --mode [MODE]");
        printWriter.println("    Limit output to data associated with the given app op mode.");
        printWriter.println("  --package [PACKAGE]");
        printWriter.println("    Limit output to data associated with the given package name.");
        printWriter.println("  --attributionTag [attributionTag]");
        printWriter.println("    Limit output to data associated with the given attribution tag.");
        printWriter.println("  --include-discrete [n]");
        printWriter.println("    Include discrete ops limited to n per dimension. Use zero for no limit.");
        printWriter.println("  --watchers");
        printWriter.println("    Only output the watcher sections.");
        printWriter.println("  --history");
        printWriter.println("    Only output history.");
        printWriter.println("  --uid-state-changes");
        printWriter.println("    Include logs about uid state changes.");
    }

    public final void dumpStatesLocked(PrintWriter printWriter, String str, int i, long j, C0464Op c0464Op, long j2, SimpleDateFormat simpleDateFormat, Date date, String str2) {
        int i2;
        int size = c0464Op.mAttributions.size();
        for (i2 = 0; i2 < size; i2 = i2 + 1) {
            i2 = ((i & 4) == 0 || Objects.equals(c0464Op.mAttributions.keyAt(i2), str)) ? 0 : i2 + 1;
            printWriter.print(str2 + c0464Op.mAttributions.keyAt(i2) + "=[\n");
            dumpStatesLocked(printWriter, j, c0464Op, c0464Op.mAttributions.keyAt(i2), j2, simpleDateFormat, date, str2 + "  ");
            printWriter.print(str2 + "]\n");
        }
    }

    public final void dumpStatesLocked(PrintWriter printWriter, long j, C0464Op c0464Op, String str, long j2, SimpleDateFormat simpleDateFormat, Date date, String str2) {
        AttributedOp.InProgressStartOpEvent valueAt;
        String str3;
        int i;
        AppOpsManager.AttributedOpEntry attributedOpEntry;
        String str4;
        long j3;
        String str5;
        String str6;
        String str7;
        String str8;
        String str9 = str2;
        AppOpsManager.AttributedOpEntry attributedOpEntry2 = (AppOpsManager.AttributedOpEntry) c0464Op.createSingleAttributionEntryLocked(str).getAttributedOpEntries().get(str);
        ArraySet collectKeys = attributedOpEntry2.collectKeys();
        int size = collectKeys.size();
        int i2 = 0;
        while (i2 < size) {
            long longValue = ((Long) collectKeys.valueAt(i2)).longValue();
            int extractUidStateFromKey = AppOpsManager.extractUidStateFromKey(longValue);
            int extractFlagsFromKey = AppOpsManager.extractFlagsFromKey(longValue);
            int i3 = i2;
            long lastAccessTime = attributedOpEntry2.getLastAccessTime(extractUidStateFromKey, extractUidStateFromKey, extractFlagsFromKey);
            long lastRejectTime = attributedOpEntry2.getLastRejectTime(extractUidStateFromKey, extractUidStateFromKey, extractFlagsFromKey);
            ArraySet arraySet = collectKeys;
            int i4 = size;
            long lastDuration = attributedOpEntry2.getLastDuration(extractUidStateFromKey, extractUidStateFromKey, extractFlagsFromKey);
            AppOpsManager.OpEventProxyInfo lastProxyInfo = attributedOpEntry2.getLastProxyInfo(extractUidStateFromKey, extractUidStateFromKey, extractFlagsFromKey);
            if (lastProxyInfo != null) {
                str3 = lastProxyInfo.getPackageName();
                String attributionTag = lastProxyInfo.getAttributionTag();
                i = lastProxyInfo.getUid();
                j3 = 0;
                attributedOpEntry = attributedOpEntry2;
                str4 = attributionTag;
            } else {
                str3 = null;
                i = -1;
                attributedOpEntry = attributedOpEntry2;
                str4 = null;
                j3 = 0;
            }
            String str10 = str4;
            if (lastAccessTime > j3) {
                printWriter.print(str9);
                printWriter.print("Access: ");
                printWriter.print(AppOpsManager.keyToString(longValue));
                printWriter.print(" ");
                date.setTime(lastAccessTime);
                printWriter.print(simpleDateFormat.format(date));
                printWriter.print(" (");
                TimeUtils.formatDuration(lastAccessTime - j2, printWriter);
                printWriter.print(")");
                if (lastDuration > 0) {
                    printWriter.print(" duration=");
                    TimeUtils.formatDuration(lastDuration, printWriter);
                }
                if (i >= 0) {
                    printWriter.print(" proxy[");
                    printWriter.print("uid=");
                    printWriter.print(i);
                    printWriter.print(", pkg=");
                    printWriter.print(str3);
                    str7 = ", attributionTag=";
                    printWriter.print(str7);
                    str6 = str10;
                    printWriter.print(str6);
                    str5 = "]";
                    printWriter.print(str5);
                } else {
                    str5 = "]";
                    str6 = str10;
                    str7 = ", attributionTag=";
                }
                printWriter.println();
            } else {
                str5 = "]";
                str6 = str10;
                str7 = ", attributionTag=";
            }
            if (lastRejectTime > 0) {
                str8 = str2;
                printWriter.print(str8);
                printWriter.print("Reject: ");
                printWriter.print(AppOpsManager.keyToString(longValue));
                date.setTime(lastRejectTime);
                printWriter.print(simpleDateFormat.format(date));
                printWriter.print(" (");
                TimeUtils.formatDuration(lastRejectTime - j2, printWriter);
                printWriter.print(")");
                if (i >= 0) {
                    printWriter.print(" proxy[");
                    printWriter.print("uid=");
                    printWriter.print(i);
                    printWriter.print(", pkg=");
                    printWriter.print(str3);
                    printWriter.print(str7);
                    printWriter.print(str6);
                    printWriter.print(str5);
                }
                printWriter.println();
            } else {
                str8 = str2;
            }
            i2 = i3 + 1;
            str9 = str8;
            collectKeys = arraySet;
            size = i4;
            attributedOpEntry2 = attributedOpEntry;
        }
        AttributedOp attributedOp = c0464Op.mAttributions.get(str);
        if (attributedOp.isRunning()) {
            int size2 = attributedOp.mInProgressEvents.size();
            long j4 = Long.MAX_VALUE;
            long j5 = 0;
            for (int i5 = 0; i5 < size2; i5++) {
                j4 = Math.min(j4, attributedOp.mInProgressEvents.valueAt(i5).getStartElapsedTime());
                j5 = Math.max(j5, valueAt.mNumUnfinishedStarts);
            }
            printWriter.print(str9 + "Running start at: ");
            TimeUtils.formatDuration(j - j4, printWriter);
            printWriter.println();
            if (j5 > 1) {
                printWriter.print(str9 + "startNesting=");
                printWriter.println(j5);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:753:0x04de, code lost:
        if (r2 == null) goto L380;
     */
    /* JADX WARN: Code restructure failed: missing block: B:755:0x04e4, code lost:
        if (r2.indexOfKey(r10) < 0) goto L380;
     */
    /* JADX WARN: Code restructure failed: missing block: B:757:0x04e7, code lost:
        r3 = false;
     */
    /* JADX WARN: Removed duplicated region for block: B:766:0x04fa  */
    /* JADX WARN: Removed duplicated region for block: B:767:0x04fd  */
    /* JADX WARN: Removed duplicated region for block: B:781:0x0525  */
    /* JADX WARN: Removed duplicated region for block: B:812:0x0587  */
    /* JADX WARN: Removed duplicated region for block: B:820:0x059e  */
    /* JADX WARN: Removed duplicated region for block: B:831:0x05cd  */
    /* JADX WARN: Removed duplicated region for block: B:843:0x060e A[Catch: all -> 0x07fd, TryCatch #3 {, blocks: (B:593:0x01d5, B:596:0x01de, B:597:0x01e3, B:600:0x01f5, B:604:0x01fd, B:605:0x0203, B:607:0x020b, B:608:0x022d, B:610:0x0232, B:612:0x023c, B:617:0x024b, B:619:0x0253, B:621:0x025d, B:628:0x0292, B:625:0x026a, B:627:0x0272, B:630:0x0296, B:634:0x02a4, B:636:0x02ac, B:659:0x0333, B:639:0x02bc, B:641:0x02c7, B:645:0x02d0, B:649:0x02db, B:650:0x02e1, B:653:0x030a, B:654:0x030d, B:656:0x031c, B:657:0x0321, B:658:0x0326, B:661:0x033b, B:664:0x0345, B:666:0x034f, B:690:0x03de, B:669:0x035e, B:671:0x0367, B:675:0x0370, B:679:0x037d, B:680:0x0383, B:683:0x03ac, B:685:0x03b6, B:687:0x03c5, B:688:0x03ca, B:689:0x03cf, B:692:0x03e5, B:696:0x03f1, B:698:0x03f9, B:723:0x0489, B:701:0x0409, B:703:0x0412, B:707:0x041b, B:711:0x0428, B:712:0x042e, B:715:0x0457, B:717:0x045f, B:719:0x046e, B:721:0x0476, B:722:0x047a, B:725:0x048e, B:731:0x049e, B:739:0x04af, B:741:0x04b3, B:743:0x04bb, B:823:0x05a4, B:828:0x05bf, B:829:0x05c5, B:832:0x05cf, B:840:0x05ff, B:835:0x05d8, B:839:0x05fc, B:841:0x0602, B:843:0x060e, B:845:0x0615, B:853:0x063f, B:852:0x0627, B:891:0x075c, B:857:0x064d, B:859:0x0653, B:861:0x065c, B:887:0x0733, B:865:0x0668, B:867:0x066e, B:886:0x0723, B:872:0x067e, B:876:0x0696, B:878:0x06aa, B:880:0x06cc, B:882:0x06e0, B:884:0x06e9, B:883:0x06e5, B:885:0x06f5, B:754:0x04e0, B:760:0x04ec, B:772:0x0508, B:774:0x0510, B:777:0x0518, B:813:0x058d, B:816:0x0595, B:787:0x0532, B:789:0x053a, B:792:0x0544, B:799:0x0552, B:801:0x055a, B:804:0x0568, B:807:0x0571, B:810:0x057b, B:894:0x0774, B:901:0x0781, B:904:0x078e, B:906:0x0799, B:908:0x07a3, B:909:0x07af, B:913:0x07c7, B:912:0x07b8), top: B:930:0x01d5 }] */
    /* JADX WARN: Removed duplicated region for block: B:856:0x064c  */
    @NeverCompile
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        boolean z;
        String str;
        String str2;
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        SimpleDateFormat simpleDateFormat;
        int i6;
        String str3;
        int i7;
        boolean z8;
        long j;
        SimpleDateFormat simpleDateFormat2;
        int i8;
        String str4;
        int i9;
        boolean z9;
        boolean z10;
        boolean z11;
        boolean z12;
        boolean z13;
        SparseBooleanArray sparseBooleanArray;
        int i10;
        boolean z14;
        long j2;
        int i11;
        Ops ops;
        ArrayMap<String, Ops> arrayMap;
        int i12;
        String str5;
        int i13;
        int i14;
        boolean z15;
        int i15;
        int i16;
        boolean z16;
        int i17;
        String str6;
        int i18;
        if (DumpUtils.checkDumpAndUsageStatsPermission(this.mContext, "AppOps", printWriter)) {
            String str7 = null;
            if (strArr != null) {
                int i19 = -1;
                int i20 = -1;
                int i21 = 10;
                int i22 = 0;
                boolean z17 = false;
                boolean z18 = false;
                int i23 = 0;
                boolean z19 = false;
                boolean z20 = false;
                boolean z21 = false;
                String str8 = null;
                int i24 = -1;
                while (i22 < strArr.length) {
                    String str9 = strArr[i22];
                    if ("-h".equals(str9)) {
                        dumpHelp(printWriter);
                        return;
                    }
                    if ("-a".equals(str9)) {
                        i17 = 1;
                        z21 = true;
                    } else {
                        if ("--op".equals(str9)) {
                            i22++;
                            if (i22 >= strArr.length) {
                                printWriter.println("No argument for --op option");
                                return;
                            }
                            i20 = Shell.strOpToOp(strArr[i22], printWriter);
                            i23 |= 8;
                            if (i20 < 0) {
                                return;
                            }
                        } else if ("--package".equals(str9)) {
                            i22++;
                            if (i22 >= strArr.length) {
                                printWriter.println("No argument for --package option");
                                return;
                            }
                            str7 = strArr[i22];
                            int i25 = i23 | 2;
                            try {
                                str6 = str8;
                                i18 = i24;
                                try {
                                    i19 = AppGlobals.getPackageManager().getPackageUid(str7, 12591104L, 0);
                                } catch (RemoteException unused) {
                                }
                            } catch (RemoteException unused2) {
                                str6 = str8;
                                i18 = i24;
                            }
                            if (i19 < 0) {
                                printWriter.println("Unknown package: " + str7);
                                return;
                            }
                            i23 = i25 | 1;
                            i19 = UserHandle.getAppId(i19);
                            i17 = 1;
                            str8 = str6;
                            i24 = i18;
                        } else {
                            String str10 = str8;
                            int i26 = i24;
                            if ("--attributionTag".equals(str9)) {
                                i22++;
                                if (i22 >= strArr.length) {
                                    printWriter.println("No argument for --attributionTag option");
                                    return;
                                }
                                str8 = strArr[i22];
                                i23 |= 4;
                                i24 = i26;
                            } else if ("--mode".equals(str9)) {
                                i22++;
                                if (i22 >= strArr.length) {
                                    printWriter.println("No argument for --mode option");
                                    return;
                                }
                                int strModeToMode = Shell.strModeToMode(strArr[i22], printWriter);
                                if (strModeToMode < 0) {
                                    return;
                                }
                                i24 = strModeToMode;
                                str8 = str10;
                            } else if ("--watchers".equals(str9)) {
                                str8 = str10;
                                i24 = i26;
                                z18 = true;
                            } else if ("--include-discrete".equals(str9)) {
                                i22++;
                                if (i22 >= strArr.length) {
                                    printWriter.println("No argument for --include-discrete option");
                                    return;
                                }
                                try {
                                    i21 = Integer.valueOf(strArr[i22]).intValue();
                                    str8 = str10;
                                    i24 = i26;
                                    z19 = true;
                                } catch (NumberFormatException unused3) {
                                    printWriter.println("Wrong parameter: " + strArr[i22]);
                                    return;
                                }
                            } else if ("--history".equals(str9)) {
                                str8 = str10;
                                i24 = i26;
                                z17 = true;
                            } else if (str9.length() > 0 && str9.charAt(0) == '-') {
                                printWriter.println("Unknown option: " + str9);
                                return;
                            } else if (!"--uid-state-changes".equals(str9)) {
                                printWriter.println("Unknown command: " + str9);
                                return;
                            } else {
                                str8 = str10;
                                i24 = i26;
                                i17 = 1;
                                z20 = true;
                            }
                        }
                        i17 = 1;
                    }
                    i22 += i17;
                }
                str2 = str8;
                z = true;
                i4 = i21;
                z4 = z19;
                z5 = z20;
                z6 = z21;
                i3 = i24;
                i2 = i19;
                z2 = z17;
                z3 = z18;
                i5 = i23;
                str = str7;
                i = i20;
            } else {
                z = true;
                str = null;
                str2 = null;
                i = -1;
                i2 = -1;
                i3 = -1;
                i4 = 10;
                i5 = 0;
                z2 = false;
                z3 = false;
                z4 = false;
                z5 = false;
                z6 = false;
            }
            SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date date = new Date();
            synchronized (this) {
                printWriter.println("Current AppOps Service state:");
                if (!z2 && !z3) {
                    this.mConstants.dump(printWriter);
                }
                printWriter.println();
                long currentTimeMillis = System.currentTimeMillis();
                long elapsedRealtime = SystemClock.elapsedRealtime();
                SystemClock.uptimeMillis();
                if (i5 == 0 && i3 < 0 && this.mProfileOwners != null && !z3 && !z2) {
                    printWriter.println("  Profile owners:");
                    for (int i27 = 0; i27 < this.mProfileOwners.size(); i27++) {
                        printWriter.print("    User #");
                        printWriter.print(this.mProfileOwners.keyAt(i27));
                        printWriter.print(": ");
                        UserHandle.formatUid(printWriter, this.mProfileOwners.valueAt(i27));
                        printWriter.println();
                    }
                    printWriter.println();
                }
                boolean dumpListeners = !z2 ? this.mAppOpsCheckingService.dumpListeners(i, i2, str, printWriter) | false : false;
                if (this.mModeWatchers.size() <= 0 || i >= 0 || z2) {
                    z7 = dumpListeners;
                } else {
                    z7 = dumpListeners;
                    boolean z22 = false;
                    for (int i28 = 0; i28 < this.mModeWatchers.size(); i28++) {
                        ModeCallback valueAt = this.mModeWatchers.valueAt(i28);
                        if (str == null || i2 == UserHandle.getAppId(valueAt.getWatchingUid())) {
                            if (z22) {
                                z16 = z22;
                            } else {
                                printWriter.println("  All op mode watchers:");
                                z16 = z;
                            }
                            printWriter.print("    ");
                            printWriter.print(Integer.toHexString(System.identityHashCode(this.mModeWatchers.keyAt(i28))));
                            printWriter.print(": ");
                            printWriter.println(valueAt);
                            z22 = z16;
                            z7 = z;
                        }
                    }
                }
                char c = ' ';
                if (this.mActiveWatchers.size() > 0 && i3 < 0) {
                    int i29 = 0;
                    boolean z23 = false;
                    while (i29 < this.mActiveWatchers.size()) {
                        SparseArray<ActiveCallback> valueAt2 = this.mActiveWatchers.valueAt(i29);
                        if (valueAt2.size() > 0) {
                            ActiveCallback valueAt3 = valueAt2.valueAt(0);
                            if ((i < 0 || valueAt2.indexOfKey(i) >= 0) && (str == null || i2 == UserHandle.getAppId(valueAt3.mWatchingUid))) {
                                if (!z23) {
                                    printWriter.println("  All op active watchers:");
                                    z23 = true;
                                }
                                printWriter.print("    ");
                                printWriter.print(Integer.toHexString(System.identityHashCode(this.mActiveWatchers.keyAt(i29))));
                                printWriter.println(" ->");
                                printWriter.print("        [");
                                int size = valueAt2.size();
                                int i30 = 0;
                                while (i30 < size) {
                                    if (i30 > 0) {
                                        printWriter.print(c);
                                    }
                                    printWriter.print(AppOpsManager.opToName(valueAt2.keyAt(i30)));
                                    if (i30 < size - 1) {
                                        printWriter.print(',');
                                    }
                                    i30++;
                                    c = ' ';
                                }
                                printWriter.println("]");
                                printWriter.print("        ");
                                printWriter.println(valueAt3);
                            }
                        }
                        i29++;
                        c = ' ';
                    }
                    z7 = true;
                }
                if (this.mStartedWatchers.size() > 0 && i3 < 0) {
                    int size2 = this.mStartedWatchers.size();
                    int i31 = 0;
                    boolean z24 = false;
                    while (i31 < size2) {
                        SparseArray<StartedCallback> valueAt4 = this.mStartedWatchers.valueAt(i31);
                        if (valueAt4.size() > 0) {
                            StartedCallback valueAt5 = valueAt4.valueAt(0);
                            if ((i < 0 || valueAt4.indexOfKey(i) >= 0) && (str == null || i2 == UserHandle.getAppId(valueAt5.mWatchingUid))) {
                                if (!z24) {
                                    printWriter.println("  All op started watchers:");
                                    z24 = true;
                                }
                                printWriter.print("    ");
                                printWriter.print(Integer.toHexString(System.identityHashCode(this.mStartedWatchers.keyAt(i31))));
                                printWriter.println(" ->");
                                printWriter.print("        [");
                                int size3 = valueAt4.size();
                                int i32 = 0;
                                while (i32 < size3) {
                                    if (i32 > 0) {
                                        i16 = size2;
                                        printWriter.print(' ');
                                    } else {
                                        i16 = size2;
                                    }
                                    printWriter.print(AppOpsManager.opToName(valueAt4.keyAt(i32)));
                                    if (i32 < size3 - 1) {
                                        printWriter.print(',');
                                    }
                                    i32++;
                                    size2 = i16;
                                }
                                i15 = size2;
                                printWriter.println("]");
                                printWriter.print("        ");
                                printWriter.println(valueAt5);
                                i31++;
                                size2 = i15;
                            }
                        }
                        i15 = size2;
                        i31++;
                        size2 = i15;
                    }
                    z7 = true;
                }
                if (this.mNotedWatchers.size() > 0 && i3 < 0) {
                    boolean z25 = false;
                    for (int i33 = 0; i33 < this.mNotedWatchers.size(); i33++) {
                        SparseArray<NotedCallback> valueAt6 = this.mNotedWatchers.valueAt(i33);
                        if (valueAt6.size() > 0) {
                            NotedCallback valueAt7 = valueAt6.valueAt(0);
                            if ((i < 0 || valueAt6.indexOfKey(i) >= 0) && (str == null || i2 == UserHandle.getAppId(valueAt7.mWatchingUid))) {
                                if (!z25) {
                                    printWriter.println("  All op noted watchers:");
                                    z25 = true;
                                }
                                printWriter.print("    ");
                                printWriter.print(Integer.toHexString(System.identityHashCode(this.mNotedWatchers.keyAt(i33))));
                                printWriter.println(" ->");
                                printWriter.print("        [");
                                int size4 = valueAt6.size();
                                for (int i34 = 0; i34 < size4; i34++) {
                                    if (i34 > 0) {
                                        printWriter.print(' ');
                                    }
                                    printWriter.print(AppOpsManager.opToName(valueAt6.keyAt(i34)));
                                    if (i34 < size4 - 1) {
                                        printWriter.print(',');
                                    }
                                }
                                printWriter.println("]");
                                printWriter.print("        ");
                                printWriter.println(valueAt7);
                            }
                        }
                    }
                    z7 = true;
                }
                if (this.mAudioRestrictionManager.hasActiveRestrictions() && i < 0 && str != null && i3 < 0 && !z3) {
                    if (!this.mAudioRestrictionManager.dump(printWriter) && !z7) {
                        z15 = false;
                        z7 = z15;
                    }
                    z15 = true;
                    z7 = z15;
                }
                if (z7) {
                    printWriter.println();
                }
                int i35 = 0;
                while (i35 < this.mUidStates.size()) {
                    UidState valueAt8 = this.mUidStates.valueAt(i35);
                    SparseIntArray nonDefaultUidModes = valueAt8.getNonDefaultUidModes();
                    ArrayMap<String, Ops> arrayMap2 = valueAt8.pkgOps;
                    if (!z3 && !z2) {
                        if (i < 0 && str == null && i3 < 0) {
                            simpleDateFormat2 = simpleDateFormat3;
                            i9 = i2;
                            printWriter.print("  Uid ");
                            UserHandle.formatUid(printWriter, valueAt8.uid);
                            printWriter.println(com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
                            valueAt8.dump(printWriter, elapsedRealtime);
                            if (valueAt8.foregroundOps != null && (i3 < 0 || i3 == 4)) {
                                printWriter.println("    foregroundOps:");
                                for (i14 = 0; i14 < valueAt8.foregroundOps.size(); i14++) {
                                    if (i < 0 || i == valueAt8.foregroundOps.keyAt(i14)) {
                                        printWriter.print("      ");
                                        printWriter.print(AppOpsManager.opToName(valueAt8.foregroundOps.keyAt(i14)));
                                        printWriter.print(": ");
                                        printWriter.println(valueAt8.foregroundOps.valueAt(i14) ? "WATCHER" : "SILENT");
                                    }
                                }
                                printWriter.print("    hasForegroundWatchers=");
                                printWriter.println(valueAt8.hasForegroundWatchers);
                            }
                            if (nonDefaultUidModes != null) {
                                int size5 = nonDefaultUidModes.size();
                                for (int i36 = 0; i36 < size5; i36++) {
                                    int keyAt = nonDefaultUidModes.keyAt(i36);
                                    int valueAt9 = nonDefaultUidModes.valueAt(i36);
                                    if ((i < 0 || i == keyAt) && (i3 < 0 || i3 == valueAt9)) {
                                        printWriter.print("      ");
                                        printWriter.print(AppOpsManager.opToName(keyAt));
                                        printWriter.print(": mode=");
                                        printWriter.println(AppOpsManager.modeToName(valueAt9));
                                    }
                                }
                            }
                            if (arrayMap2 != null) {
                                int i37 = 0;
                                while (i37 < arrayMap2.size()) {
                                    Ops valueAt10 = arrayMap2.valueAt(i37);
                                    if (str == null || str.equals(valueAt10.packageName)) {
                                        boolean z26 = false;
                                        int i38 = 0;
                                        while (i38 < valueAt10.size()) {
                                            C0464Op valueAt11 = valueAt10.valueAt(i38);
                                            int i39 = valueAt11.f1125op;
                                            if ((i < 0 || i == i39) && (i3 < 0 || i3 == valueAt11.getMode())) {
                                                if (z26) {
                                                    z14 = z26;
                                                } else {
                                                    printWriter.print("    Package ");
                                                    printWriter.print(valueAt10.packageName);
                                                    printWriter.println(com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
                                                    z14 = true;
                                                }
                                                printWriter.print("      ");
                                                printWriter.print(AppOpsManager.opToName(i39));
                                                printWriter.print(" (");
                                                printWriter.print(AppOpsManager.modeToName(valueAt11.getMode()));
                                                int opToSwitch = AppOpsManager.opToSwitch(i39);
                                                if (opToSwitch != i39) {
                                                    printWriter.print(" / switch ");
                                                    printWriter.print(AppOpsManager.opToName(opToSwitch));
                                                    C0464Op c0464Op = valueAt10.get(opToSwitch);
                                                    int opToDefaultMode = c0464Op == null ? AppOpsManager.opToDefaultMode(opToSwitch) : c0464Op.getMode();
                                                    printWriter.print("=");
                                                    printWriter.print(AppOpsManager.modeToName(opToDefaultMode));
                                                }
                                                printWriter.println("): ");
                                                j2 = elapsedRealtime;
                                                i11 = i38;
                                                ops = valueAt10;
                                                arrayMap = arrayMap2;
                                                i12 = i;
                                                str5 = str;
                                                i13 = i37;
                                                dumpStatesLocked(printWriter, str2, i5, elapsedRealtime, valueAt11, currentTimeMillis, simpleDateFormat2, date, "        ");
                                                z26 = z14;
                                                i38 = i11 + 1;
                                                arrayMap2 = arrayMap;
                                                elapsedRealtime = j2;
                                                valueAt10 = ops;
                                                i = i12;
                                                str = str5;
                                                i37 = i13;
                                            }
                                            j2 = elapsedRealtime;
                                            i11 = i38;
                                            ops = valueAt10;
                                            arrayMap = arrayMap2;
                                            i12 = i;
                                            str5 = str;
                                            i13 = i37;
                                            i38 = i11 + 1;
                                            arrayMap2 = arrayMap;
                                            elapsedRealtime = j2;
                                            valueAt10 = ops;
                                            i = i12;
                                            str = str5;
                                            i37 = i13;
                                        }
                                    }
                                    i37++;
                                    arrayMap2 = arrayMap2;
                                    elapsedRealtime = elapsedRealtime;
                                    i = i;
                                    str = str;
                                }
                            }
                            j = elapsedRealtime;
                            i8 = i;
                            str4 = str;
                            z7 = true;
                            i35++;
                            simpleDateFormat3 = simpleDateFormat2;
                            i2 = i9;
                            elapsedRealtime = j;
                            i = i8;
                            str = str4;
                        }
                        boolean z27 = true;
                        if (str != null && i2 != this.mUidStates.keyAt(i35)) {
                            z9 = false;
                            z10 = i3 >= 0;
                            if (z10 && nonDefaultUidModes != null) {
                                z11 = z27;
                                int i40 = 0;
                                while (!z10) {
                                    z12 = z9;
                                    if (i40 >= nonDefaultUidModes.size()) {
                                        break;
                                    }
                                    if (nonDefaultUidModes.valueAt(i40) == i3) {
                                        z10 = true;
                                    }
                                    i40++;
                                    z9 = z12;
                                }
                            } else {
                                z11 = z27;
                            }
                            z12 = z9;
                            if (arrayMap2 == null) {
                                z13 = z11;
                                int i41 = 0;
                                while (true) {
                                    if (z13 && z12 && z10) {
                                        simpleDateFormat2 = simpleDateFormat3;
                                        break;
                                    }
                                    simpleDateFormat2 = simpleDateFormat3;
                                    if (i41 >= arrayMap2.size()) {
                                        break;
                                    }
                                    Ops valueAt12 = arrayMap2.valueAt(i41);
                                    if (!z13 && valueAt12 != null && valueAt12.indexOfKey(i) >= 0) {
                                        z13 = true;
                                    }
                                    boolean z28 = z13;
                                    if (!z10) {
                                        int i42 = 0;
                                        while (!z10) {
                                            i10 = i2;
                                            if (i42 >= valueAt12.size()) {
                                                break;
                                            }
                                            if (valueAt12.valueAt(i42).getMode() == i3) {
                                                z10 = true;
                                            }
                                            i42++;
                                            i2 = i10;
                                        }
                                    }
                                    i10 = i2;
                                    if (!z12 && str.equals(valueAt12.packageName)) {
                                        z12 = true;
                                    }
                                    i41++;
                                    z13 = z28;
                                    simpleDateFormat3 = simpleDateFormat2;
                                    i2 = i10;
                                }
                                i9 = i2;
                            } else {
                                simpleDateFormat2 = simpleDateFormat3;
                                i9 = i2;
                                z13 = z11;
                            }
                            boolean z29 = z12;
                            sparseBooleanArray = valueAt8.foregroundOps;
                            if (sparseBooleanArray != null && !z13 && sparseBooleanArray.indexOfKey(i) > 0) {
                                z13 = true;
                            }
                            if (z13) {
                                if (z29) {
                                    if (z10) {
                                    }
                                    printWriter.print("  Uid ");
                                    UserHandle.formatUid(printWriter, valueAt8.uid);
                                    printWriter.println(com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
                                    valueAt8.dump(printWriter, elapsedRealtime);
                                    if (valueAt8.foregroundOps != null) {
                                        printWriter.println("    foregroundOps:");
                                        while (i14 < valueAt8.foregroundOps.size()) {
                                        }
                                        printWriter.print("    hasForegroundWatchers=");
                                        printWriter.println(valueAt8.hasForegroundWatchers);
                                    }
                                    if (nonDefaultUidModes != null) {
                                    }
                                    if (arrayMap2 != null) {
                                    }
                                    j = elapsedRealtime;
                                    i8 = i;
                                    str4 = str;
                                    z7 = true;
                                    i35++;
                                    simpleDateFormat3 = simpleDateFormat2;
                                    i2 = i9;
                                    elapsedRealtime = j;
                                    i = i8;
                                    str = str4;
                                }
                            }
                            j = elapsedRealtime;
                            i8 = i;
                            str4 = str;
                            i35++;
                            simpleDateFormat3 = simpleDateFormat2;
                            i2 = i9;
                            elapsedRealtime = j;
                            i = i8;
                            str = str4;
                        }
                        z9 = true;
                        if (i3 >= 0) {
                        }
                        if (z10) {
                        }
                        z11 = z27;
                        z12 = z9;
                        if (arrayMap2 == null) {
                        }
                        boolean z292 = z12;
                        sparseBooleanArray = valueAt8.foregroundOps;
                        if (sparseBooleanArray != null) {
                            z13 = true;
                        }
                        if (z13) {
                        }
                        j = elapsedRealtime;
                        i8 = i;
                        str4 = str;
                        i35++;
                        simpleDateFormat3 = simpleDateFormat2;
                        i2 = i9;
                        elapsedRealtime = j;
                        i = i8;
                        str = str4;
                    }
                    j = elapsedRealtime;
                    simpleDateFormat2 = simpleDateFormat3;
                    i8 = i;
                    str4 = str;
                    i9 = i2;
                    i35++;
                    simpleDateFormat3 = simpleDateFormat2;
                    i2 = i9;
                    elapsedRealtime = j;
                    i = i8;
                    str = str4;
                }
                simpleDateFormat = simpleDateFormat3;
                i6 = i;
                str3 = str;
                i7 = i2;
                if (z7) {
                    printWriter.println();
                }
                if (i3 < 0 && !z3 && !z2) {
                    z8 = false;
                    this.mAppOpsRestrictions.dumpRestrictions(printWriter, i6, str3, z8);
                    if (!z2 && !z3) {
                        printWriter.println();
                        if (this.mCheckOpsDelegateDispatcher.mPolicy == null && (this.mCheckOpsDelegateDispatcher.mPolicy instanceof AppOpsPolicy)) {
                            ((AppOpsPolicy) this.mCheckOpsDelegateDispatcher.mPolicy).dumpTags(printWriter);
                        } else {
                            printWriter.println("  AppOps policy not set.");
                        }
                    }
                    if (!z6 || z5) {
                        printWriter.println();
                        printWriter.println("Uid State Changes Event Log:");
                        getUidStateTracker().dumpEvents(printWriter);
                    }
                }
                z8 = true;
                this.mAppOpsRestrictions.dumpRestrictions(printWriter, i6, str3, z8);
                if (!z2) {
                    printWriter.println();
                    if (this.mCheckOpsDelegateDispatcher.mPolicy == null) {
                    }
                    printWriter.println("  AppOps policy not set.");
                }
                if (!z6) {
                }
                printWriter.println();
                printWriter.println("Uid State Changes Event Log:");
                getUidStateTracker().dumpEvents(printWriter);
            }
            if (z2 && !z3) {
                this.mHistoricalRegistry.dump("  ", printWriter, i7, str3, str2, i6, i5);
            }
            if (z4) {
                printWriter.println("Discrete accesses: ");
                this.mHistoricalRegistry.dumpDiscreteData(printWriter, i7, str3, str2, i5, i6, simpleDateFormat, date, "  ", i4);
            }
        }
    }

    public void setUserRestrictions(Bundle bundle, IBinder iBinder, int i) {
        checkSystemUid("setUserRestrictions");
        Objects.requireNonNull(bundle);
        Objects.requireNonNull(iBinder);
        for (int i2 = 0; i2 < 134; i2++) {
            String opToRestriction = AppOpsManager.opToRestriction(i2);
            if (opToRestriction != null) {
                setUserRestrictionNoCheck(i2, bundle.getBoolean(opToRestriction, false), iBinder, i, null);
            }
        }
    }

    public void setUserRestriction(int i, boolean z, IBinder iBinder, int i2, PackageTagsList packageTagsList) {
        if (Binder.getCallingPid() != Process.myPid()) {
            this.mContext.enforcePermission("android.permission.MANAGE_APP_OPS_RESTRICTIONS", Binder.getCallingPid(), Binder.getCallingUid(), null);
        }
        if (i2 != UserHandle.getCallingUserId() && this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0 && this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS") != 0) {
            throw new SecurityException("Need INTERACT_ACROSS_USERS_FULL or INTERACT_ACROSS_USERS to interact cross user ");
        }
        verifyIncomingOp(i);
        Objects.requireNonNull(iBinder);
        setUserRestrictionNoCheck(i, z, iBinder, i2, packageTagsList);
    }

    public final void setUserRestrictionNoCheck(int i, boolean z, IBinder iBinder, int i2, PackageTagsList packageTagsList) {
        synchronized (this) {
            ClientUserRestrictionState clientUserRestrictionState = this.mOpUserRestrictions.get(iBinder);
            if (clientUserRestrictionState == null) {
                try {
                    clientUserRestrictionState = new ClientUserRestrictionState(iBinder);
                    this.mOpUserRestrictions.put(iBinder, clientUserRestrictionState);
                } catch (RemoteException unused) {
                    return;
                }
            }
            if (clientUserRestrictionState.setRestriction(i, z, packageTagsList, i2)) {
                this.mHandler.sendMessage(PooledLambda.obtainMessage(new AppOpsService$$ExternalSyntheticLambda1(), this, Integer.valueOf(i), -2));
                this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda8
                    public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                        ((AppOpsService) obj).updateStartedOpModeForUser(((Integer) obj2).intValue(), ((Boolean) obj3).booleanValue(), ((Integer) obj4).intValue());
                    }
                }, this, Integer.valueOf(i), Boolean.valueOf(z), Integer.valueOf(i2)));
            }
            if (clientUserRestrictionState.isDefault()) {
                this.mOpUserRestrictions.remove(iBinder);
                clientUserRestrictionState.destroy();
            }
        }
    }

    public final void updateStartedOpModeForUser(int i, boolean z, int i2) {
        synchronized (this) {
            int size = this.mUidStates.size();
            for (int i3 = 0; i3 < size; i3++) {
                int keyAt = this.mUidStates.keyAt(i3);
                if (i2 == -1 || UserHandle.getUserId(keyAt) == i2) {
                    updateStartedOpModeForUidLocked(i, z, keyAt);
                }
            }
        }
    }

    public final void updateStartedOpModeForUidLocked(int i, boolean z, int i2) {
        UidState uidState = this.mUidStates.get(i2);
        if (uidState == null) {
            return;
        }
        int size = uidState.pkgOps.size();
        for (int i3 = 0; i3 < size; i3++) {
            Ops valueAt = uidState.pkgOps.valueAt(i3);
            C0464Op c0464Op = valueAt != null ? valueAt.get(i) : null;
            if (c0464Op != null && (c0464Op.getMode() == 0 || c0464Op.getMode() == 4)) {
                int size2 = c0464Op.mAttributions.size();
                for (int i4 = 0; i4 < size2; i4++) {
                    AttributedOp valueAt2 = c0464Op.mAttributions.valueAt(i4);
                    if (z && valueAt2.isRunning()) {
                        valueAt2.pause();
                    } else if (valueAt2.isPaused()) {
                        valueAt2.resume();
                    }
                }
            }
        }
    }

    public final void notifyWatchersOfChange(int i, int i2) {
        synchronized (this) {
            ArraySet<OnOpModeChangedListener> opModeChangedListeners = this.mAppOpsCheckingService.getOpModeChangedListeners(i);
            if (opModeChangedListeners == null) {
                return;
            }
            notifyOpChanged(opModeChangedListeners, i, i2, (String) null);
        }
    }

    public void removeUser(int i) throws RemoteException {
        checkSystemUid("removeUser");
        synchronized (this) {
            for (int size = this.mOpUserRestrictions.size() - 1; size >= 0; size--) {
                this.mOpUserRestrictions.valueAt(size).removeUser(i);
            }
            removeUidsForUserLocked(i);
        }
    }

    public boolean isOperationActive(int i, int i2, String str) {
        String resolvePackageName;
        if (Binder.getCallingUid() == i2 || this.mContext.checkCallingOrSelfPermission("android.permission.WATCH_APPOPS") == 0) {
            verifyIncomingOp(i);
            if (isIncomingPackageValid(str, UserHandle.getUserId(i2)) && (resolvePackageName = AppOpsManager.resolvePackageName(i2, str)) != null) {
                synchronized (this) {
                    Ops opsLocked = getOpsLocked(i2, resolvePackageName, null, false, null, false);
                    if (opsLocked == null) {
                        return false;
                    }
                    C0464Op c0464Op = opsLocked.get(i);
                    if (c0464Op == null) {
                        return false;
                    }
                    return c0464Op.isRunning();
                }
            }
            return false;
        }
        return false;
    }

    public boolean isProxying(int i, String str, String str2, int i2, String str3) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(str3);
        long callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            List<AppOpsManager.PackageOps> opsForPackage = getOpsForPackage(i2, str3, new int[]{i});
            boolean z = false;
            if (opsForPackage != null && !opsForPackage.isEmpty()) {
                List ops = opsForPackage.get(0).getOps();
                if (ops.isEmpty()) {
                    return false;
                }
                AppOpsManager.OpEntry opEntry = (AppOpsManager.OpEntry) ops.get(0);
                if (opEntry.isRunning()) {
                    AppOpsManager.OpEventProxyInfo lastProxyInfo = opEntry.getLastProxyInfo(24);
                    if (lastProxyInfo != null && callingUid == lastProxyInfo.getUid() && str.equals(lastProxyInfo.getPackageName())) {
                        if (Objects.equals(str2, lastProxyInfo.getAttributionTag())) {
                            z = true;
                        }
                    }
                    return z;
                }
                return false;
            }
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void resetPackageOpsNoHistory(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_APPOPS", "resetPackageOpsNoHistory");
        synchronized (this) {
            int packageUid = this.mPackageManagerInternal.getPackageUid(str, 0L, UserHandle.getCallingUserId());
            if (packageUid == -1) {
                return;
            }
            UidState uidState = this.mUidStates.get(packageUid);
            if (uidState == null) {
                return;
            }
            Ops remove = uidState.pkgOps.remove(str);
            this.mAppOpsCheckingService.removePackage(str, UserHandle.getUserId(packageUid));
            if (remove != null) {
                scheduleFastWriteLocked();
            }
        }
    }

    public void setHistoryParameters(int i, long j, int i2) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_APPOPS", "setHistoryParameters");
        this.mHistoricalRegistry.setHistoryParameters(i, j, i2);
    }

    public void offsetHistory(long j) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_APPOPS", "offsetHistory");
        this.mHistoricalRegistry.offsetHistory(j);
        this.mHistoricalRegistry.offsetDiscreteHistory(j);
    }

    public void addHistoricalOps(AppOpsManager.HistoricalOps historicalOps) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_APPOPS", "addHistoricalOps");
        this.mHistoricalRegistry.addHistoricalOps(historicalOps);
    }

    public void resetHistoryParameters() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_APPOPS", "resetHistoryParameters");
        this.mHistoricalRegistry.resetHistoryParameters();
    }

    public void clearHistory() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_APPOPS", "clearHistory");
        this.mHistoricalRegistry.clearAllHistory();
    }

    public void rebootHistory(long j) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_APPOPS", "rebootHistory");
        int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
        Preconditions.checkArgument(i >= 0);
        this.mHistoricalRegistry.shutdown();
        if (i > 0) {
            SystemClock.sleep(j);
        }
        this.mHistoricalRegistry = new HistoricalRegistry(this.mHistoricalRegistry);
        this.mHistoricalRegistry.systemReady(this.mContext.getContentResolver());
        this.mHistoricalRegistry.persistPendingHistory();
    }

    public MessageSamplingConfig reportRuntimeAppOpAccessMessageAndGetConfig(String str, SyncNotedAppOp syncNotedAppOp, String str2) {
        int callingUid = Binder.getCallingUid();
        Objects.requireNonNull(str);
        synchronized (this) {
            switchPackageIfBootTimeOrRarelyUsedLocked(str);
            if (!str.equals(this.mSampledPackage)) {
                return new MessageSamplingConfig(-1, 0, Instant.now().plus(1L, (TemporalUnit) ChronoUnit.HOURS).toEpochMilli());
            }
            Objects.requireNonNull(syncNotedAppOp);
            Objects.requireNonNull(str2);
            reportRuntimeAppOpAccessMessageInternalLocked(callingUid, str, AppOpsManager.strOpToOp(syncNotedAppOp.getOp()), syncNotedAppOp.getAttributionTag(), str2);
            return new MessageSamplingConfig(this.mSampledAppOpCode, this.mAcceptableLeftDistance, Instant.now().plus(1L, (TemporalUnit) ChronoUnit.HOURS).toEpochMilli());
        }
    }

    public final void reportRuntimeAppOpAccessMessageAsyncLocked(int i, String str, int i2, String str2, String str3) {
        switchPackageIfBootTimeOrRarelyUsedLocked(str);
        if (Objects.equals(this.mSampledPackage, str)) {
            reportRuntimeAppOpAccessMessageInternalLocked(i, str, i2, str2, str3);
        }
    }

    public final void reportRuntimeAppOpAccessMessageInternalLocked(int i, String str, int i2, String str2, String str3) {
        int leftCircularDistance = AppOpsManager.leftCircularDistance(i2, this.mSampledAppOpCode, FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_TIME_ZONE);
        int i3 = this.mAcceptableLeftDistance;
        if (i3 >= leftCircularDistance || this.mSamplingStrategy == 4) {
            if (i3 > leftCircularDistance && this.mSamplingStrategy != 4) {
                this.mAcceptableLeftDistance = leftCircularDistance;
                this.mMessagesCollectedCount = 0.0f;
            }
            this.mMessagesCollectedCount += 1.0f;
            if (ThreadLocalRandom.current().nextFloat() <= 1.0f / this.mMessagesCollectedCount) {
                this.mCollectedRuntimePermissionMessage = new RuntimeAppOpAccessMessage(i, i2, str, str2, str3, this.mSamplingStrategy);
            }
        }
    }

    public RuntimeAppOpAccessMessage collectRuntimeAppOpAccessMessage() {
        RuntimeAppOpAccessMessage runtimeAppOpAccessMessage;
        boolean z = ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).getInstrumentationSourceUid(Binder.getCallingUid()) != -1;
        if ((Binder.getCallingPid() == Process.myPid()) || z) {
            this.mContext.enforcePermission("android.permission.GET_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), null);
            synchronized (this) {
                runtimeAppOpAccessMessage = this.mCollectedRuntimePermissionMessage;
                this.mCollectedRuntimePermissionMessage = null;
            }
            this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.appop.AppOpsService$$ExternalSyntheticLambda13
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AppOpsService) obj).getPackageListAndResample();
                }
            }, this));
            return runtimeAppOpAccessMessage;
        }
        return null;
    }

    public final void switchPackageIfBootTimeOrRarelyUsedLocked(String str) {
        if (this.mSampledPackage == null) {
            if (ThreadLocalRandom.current().nextFloat() < 0.5f) {
                this.mSamplingStrategy = 3;
                resampleAppOpForPackageLocked(str, true);
            }
        } else if (this.mRarelyUsedPackages.contains(str)) {
            this.mRarelyUsedPackages.remove(str);
            if (ThreadLocalRandom.current().nextFloat() < 0.5f) {
                this.mSamplingStrategy = 2;
                resampleAppOpForPackageLocked(str, true);
            }
        }
    }

    public final List<String> getPackageListAndResample() {
        List<String> packageNamesForSampling = getPackageNamesForSampling();
        synchronized (this) {
            resamplePackageAndAppOpLocked(packageNamesForSampling);
        }
        return packageNamesForSampling;
    }

    public final void resamplePackageAndAppOpLocked(List<String> list) {
        if (list.isEmpty()) {
            return;
        }
        if (ThreadLocalRandom.current().nextFloat() < 0.5f) {
            this.mSamplingStrategy = 1;
            resampleAppOpForPackageLocked(list.get(ThreadLocalRandom.current().nextInt(list.size())), true);
            return;
        }
        this.mSamplingStrategy = 4;
        resampleAppOpForPackageLocked(list.get(ThreadLocalRandom.current().nextInt(list.size())), false);
    }

    public final void resampleAppOpForPackageLocked(String str, boolean z) {
        this.mMessagesCollectedCount = 0.0f;
        this.mSampledAppOpCode = z ? ThreadLocalRandom.current().nextInt(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_TIME_ZONE) : -1;
        this.mAcceptableLeftDistance = 133;
        this.mSampledPackage = str;
    }

    public final void initializeRarelyUsedPackagesList(final ArraySet<String> arraySet) {
        ((AppOpsManager) this.mContext.getSystemService(AppOpsManager.class)).getHistoricalOps(new AppOpsManager.HistoricalOpsRequest.Builder(Math.max(Instant.now().minus(7L, (TemporalUnit) ChronoUnit.DAYS).toEpochMilli(), 0L), Long.MAX_VALUE).setOpNames(getRuntimeAppOpsList()).setFlags(9).build(), AsyncTask.THREAD_POOL_EXECUTOR, new Consumer<AppOpsManager.HistoricalOps>() { // from class: com.android.server.appop.AppOpsService.9
            {
                AppOpsService.this = this;
            }

            @Override // java.util.function.Consumer
            public void accept(AppOpsManager.HistoricalOps historicalOps) {
                int uidCount = historicalOps.getUidCount();
                for (int i = 0; i < uidCount; i++) {
                    AppOpsManager.HistoricalUidOps uidOpsAt = historicalOps.getUidOpsAt(i);
                    int packageCount = uidOpsAt.getPackageCount();
                    for (int i2 = 0; i2 < packageCount; i2++) {
                        String packageName = uidOpsAt.getPackageOpsAt(i2).getPackageName();
                        if (arraySet.contains(packageName) && uidOpsAt.getPackageOpsAt(i2).getOpCount() != 0) {
                            arraySet.remove(packageName);
                        }
                    }
                }
                synchronized (this) {
                    int size = AppOpsService.this.mRarelyUsedPackages.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        arraySet.add((String) AppOpsService.this.mRarelyUsedPackages.valueAt(i3));
                    }
                    AppOpsService.this.mRarelyUsedPackages = arraySet;
                }
            }
        });
    }

    public final List<String> getRuntimeAppOpsList() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 134; i++) {
            if (shouldCollectNotes(i)) {
                arrayList.add(AppOpsManager.opToPublicName(i));
            }
        }
        return arrayList;
    }

    public final List<String> getPackageNamesForSampling() {
        ArrayList arrayList = new ArrayList();
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        for (String str : packageManagerInternal.getPackageList().getPackageNames()) {
            PackageInfo packageInfo = packageManagerInternal.getPackageInfo(str, 4096L, Process.myUid(), this.mContext.getUserId());
            if (isSamplingTarget(packageInfo)) {
                arrayList.add(packageInfo.packageName);
            }
        }
        return arrayList;
    }

    public final boolean isSamplingTarget(PackageInfo packageInfo) {
        String[] strArr;
        if (packageInfo == null || (strArr = packageInfo.requestedPermissions) == null) {
            return false;
        }
        for (String str : strArr) {
            if (this.mContext.getPackageManager().getPermissionInfo(str, 0).getProtection() == 1) {
                return true;
            }
        }
        return false;
    }

    @GuardedBy({"this"})
    public final void removeUidsForUserLocked(int i) {
        for (int size = this.mUidStates.size() - 1; size >= 0; size--) {
            if (UserHandle.getUserId(this.mUidStates.keyAt(size)) == i) {
                this.mUidStates.valueAt(size).clear();
                this.mUidStates.removeAt(size);
            }
        }
    }

    public final void checkSystemUid(String str) {
        if (Binder.getCallingUid() == 1000) {
            return;
        }
        throw new SecurityException(str + " must by called by the system");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int resolveUid(String str) {
        char c;
        if (str == null) {
            return -1;
        }
        switch (str.hashCode()) {
            case -1336564963:
                if (str.equals("dumpstate")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -31178072:
                if (str.equals("cameraserver")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 3506402:
                if (str.equals("root")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 103772132:
                if (str.equals("media")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 109403696:
                if (str.equals("shell")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1344606873:
                if (str.equals("audioserver")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 4:
                return 2000;
            case 1:
                return 1047;
            case 2:
                return 0;
            case 3:
                return 1013;
            case 5:
                return 1041;
            default:
                return -1;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0012  */
    /* JADX WARN: Removed duplicated region for block: B:27:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static String[] getPackagesForUid(int i) {
        String[] packagesForUid;
        if (AppGlobals.getPackageManager() != null) {
            try {
                packagesForUid = AppGlobals.getPackageManager().getPackagesForUid(i);
            } catch (RemoteException unused) {
            }
            return packagesForUid != null ? EmptyArray.STRING : packagesForUid;
        }
        packagesForUid = null;
        if (packagesForUid != null) {
        }
    }

    /* loaded from: classes.dex */
    public final class ClientUserRestrictionState implements IBinder.DeathRecipient {
        public final IBinder token;

        public ClientUserRestrictionState(IBinder iBinder) throws RemoteException {
            AppOpsService.this = r1;
            iBinder.linkToDeath(this, 0);
            this.token = iBinder;
        }

        public boolean setRestriction(int i, boolean z, PackageTagsList packageTagsList, int i2) {
            return AppOpsService.this.mAppOpsRestrictions.setUserRestriction(this.token, i2, i, z, packageTagsList);
        }

        public boolean hasRestriction(int i, String str, String str2, int i2, boolean z) {
            return AppOpsService.this.mAppOpsRestrictions.getUserRestriction(this.token, i2, i, str, str2, z);
        }

        public void removeUser(int i) {
            AppOpsService.this.mAppOpsRestrictions.clearUserRestrictions(this.token, Integer.valueOf(i));
        }

        public boolean isDefault() {
            return !AppOpsService.this.mAppOpsRestrictions.hasUserRestrictions(this.token);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (AppOpsService.this) {
                AppOpsService.this.mAppOpsRestrictions.clearUserRestrictions(this.token);
                AppOpsService.this.mOpUserRestrictions.remove(this.token);
                destroy();
            }
        }

        public void destroy() {
            this.token.unlinkToDeath(this, 0);
        }
    }

    /* loaded from: classes.dex */
    public final class ClientGlobalRestrictionState implements IBinder.DeathRecipient {
        public final IBinder mToken;

        public ClientGlobalRestrictionState(IBinder iBinder) throws RemoteException {
            AppOpsService.this = r1;
            iBinder.linkToDeath(this, 0);
            this.mToken = iBinder;
        }

        public boolean setRestriction(int i, boolean z) {
            return AppOpsService.this.mAppOpsRestrictions.setGlobalRestriction(this.mToken, i, z);
        }

        public boolean hasRestriction(int i) {
            return AppOpsService.this.mAppOpsRestrictions.getGlobalRestriction(this.mToken, i);
        }

        public boolean isDefault() {
            return !AppOpsService.this.mAppOpsRestrictions.hasGlobalRestrictions(this.mToken);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            AppOpsService.this.mAppOpsRestrictions.clearGlobalRestrictions(this.mToken);
            AppOpsService.this.mOpGlobalRestrictions.remove(this.mToken);
            destroy();
        }

        public void destroy() {
            this.mToken.unlinkToDeath(this, 0);
        }
    }

    /* loaded from: classes.dex */
    public final class AppOpsManagerLocalImpl implements AppOpsManagerLocal {
        public AppOpsManagerLocalImpl() {
            AppOpsService.this = r1;
        }

        @Override // com.android.server.appop.AppOpsManagerLocal
        public boolean isUidInForeground(int i) {
            boolean isUidInForeground;
            synchronized (AppOpsService.this) {
                isUidInForeground = AppOpsService.this.mUidStateTracker.isUidInForeground(i);
            }
            return isUidInForeground;
        }
    }

    /* loaded from: classes.dex */
    public final class AppOpsManagerInternalImpl extends AppOpsManagerInternal {
        public AppOpsManagerInternalImpl() {
            AppOpsService.this = r1;
        }

        public void setDeviceAndProfileOwners(SparseIntArray sparseIntArray) {
            synchronized (AppOpsService.this) {
                AppOpsService.this.mProfileOwners = sparseIntArray;
            }
        }

        public void updateAppWidgetVisibility(SparseArray<String> sparseArray, boolean z) {
            AppOpsService.this.updateAppWidgetVisibility(sparseArray, z);
        }

        public void setUidModeFromPermissionPolicy(int i, int i2, int i3, IAppOpsCallback iAppOpsCallback) {
            AppOpsService.this.setUidMode(i, i2, i3, iAppOpsCallback);
        }

        public void setModeFromPermissionPolicy(int i, int i2, String str, int i3, IAppOpsCallback iAppOpsCallback) {
            AppOpsService.this.setMode(i, i2, str, i3, iAppOpsCallback);
        }

        public void setGlobalRestriction(int i, boolean z, IBinder iBinder) {
            if (Binder.getCallingPid() != Process.myPid()) {
                throw new SecurityException("Only the system can set global restrictions");
            }
            synchronized (AppOpsService.this) {
                ClientGlobalRestrictionState clientGlobalRestrictionState = (ClientGlobalRestrictionState) AppOpsService.this.mOpGlobalRestrictions.get(iBinder);
                if (clientGlobalRestrictionState == null) {
                    try {
                        clientGlobalRestrictionState = new ClientGlobalRestrictionState(iBinder);
                        AppOpsService.this.mOpGlobalRestrictions.put(iBinder, clientGlobalRestrictionState);
                    } catch (RemoteException unused) {
                        return;
                    }
                }
                if (clientGlobalRestrictionState.setRestriction(i, z)) {
                    AppOpsService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.appop.AppOpsService$AppOpsManagerInternalImpl$$ExternalSyntheticLambda0
                        public final void accept(Object obj, Object obj2, Object obj3) {
                            ((AppOpsService) obj).notifyWatchersOfChange(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
                        }
                    }, AppOpsService.this, Integer.valueOf(i), -2));
                    AppOpsService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: com.android.server.appop.AppOpsService$AppOpsManagerInternalImpl$$ExternalSyntheticLambda1
                        public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                            ((AppOpsService) obj).updateStartedOpModeForUser(((Integer) obj2).intValue(), ((Boolean) obj3).booleanValue(), ((Integer) obj4).intValue());
                        }
                    }, AppOpsService.this, Integer.valueOf(i), Boolean.valueOf(z), -1));
                }
                if (clientGlobalRestrictionState.isDefault()) {
                    AppOpsService.this.mOpGlobalRestrictions.remove(iBinder);
                    clientGlobalRestrictionState.destroy();
                }
            }
        }

        public int getOpRestrictionCount(int i, UserHandle userHandle, String str, String str2) {
            int i2;
            synchronized (AppOpsService.this) {
                int size = AppOpsService.this.mOpUserRestrictions.size();
                i2 = 0;
                for (int i3 = 0; i3 < size; i3++) {
                    if (((ClientUserRestrictionState) AppOpsService.this.mOpUserRestrictions.valueAt(i3)).hasRestriction(i, str, str2, userHandle.getIdentifier(), false)) {
                        i2++;
                    }
                }
                int size2 = AppOpsService.this.mOpGlobalRestrictions.size();
                for (int i4 = 0; i4 < size2; i4++) {
                    if (((ClientGlobalRestrictionState) AppOpsService.this.mOpGlobalRestrictions.valueAt(i4)).hasRestriction(i)) {
                        i2++;
                    }
                }
            }
            return i2;
        }
    }

    @Immutable
    /* loaded from: classes.dex */
    public final class CheckOpsDelegateDispatcher {
        public final AppOpsManagerInternal.CheckOpsDelegate mCheckOpsDelegate;
        public final AppOpsManagerInternal.CheckOpsDelegate mPolicy;

        public CheckOpsDelegateDispatcher(AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate, AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate2) {
            AppOpsService.this = r1;
            this.mPolicy = checkOpsDelegate;
            this.mCheckOpsDelegate = checkOpsDelegate2;
        }

        public AppOpsManagerInternal.CheckOpsDelegate getCheckOpsDelegate() {
            return this.mCheckOpsDelegate;
        }

        public int checkOperation(int i, int i2, String str, String str2, boolean z) {
            AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate = this.mPolicy;
            if (checkOpsDelegate != null) {
                if (this.mCheckOpsDelegate != null) {
                    return checkOpsDelegate.checkOperation(i, i2, str, str2, z, new QuintFunction() { // from class: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda8
                        public final Object apply(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                            int checkDelegateOperationImpl;
                            checkDelegateOperationImpl = AppOpsService.CheckOpsDelegateDispatcher.this.checkDelegateOperationImpl(((Integer) obj).intValue(), ((Integer) obj2).intValue(), (String) obj3, (String) obj4, ((Boolean) obj5).booleanValue());
                            return Integer.valueOf(checkDelegateOperationImpl);
                        }
                    });
                }
                return checkOpsDelegate.checkOperation(i, i2, str, str2, z, new C0482x34f2e5ce(AppOpsService.this));
            } else if (this.mCheckOpsDelegate != null) {
                return checkDelegateOperationImpl(i, i2, str, str2, z);
            } else {
                return AppOpsService.this.checkOperationImpl(i, i2, str, str2, z);
            }
        }

        public final int checkDelegateOperationImpl(int i, int i2, String str, String str2, boolean z) {
            return this.mCheckOpsDelegate.checkOperation(i, i2, str, str2, z, new C0482x34f2e5ce(AppOpsService.this));
        }

        public int checkAudioOperation(int i, int i2, int i3, String str) {
            AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate = this.mPolicy;
            if (checkOpsDelegate != null) {
                if (this.mCheckOpsDelegate != null) {
                    return checkOpsDelegate.checkAudioOperation(i, i2, i3, str, new QuadFunction() { // from class: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda14
                        public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
                            int checkDelegateAudioOperationImpl;
                            checkDelegateAudioOperationImpl = AppOpsService.CheckOpsDelegateDispatcher.this.checkDelegateAudioOperationImpl(((Integer) obj).intValue(), ((Integer) obj2).intValue(), ((Integer) obj3).intValue(), (String) obj4);
                            return Integer.valueOf(checkDelegateAudioOperationImpl);
                        }
                    });
                }
                return checkOpsDelegate.checkAudioOperation(i, i2, i3, str, new C0474x6969d32f(AppOpsService.this));
            } else if (this.mCheckOpsDelegate != null) {
                return checkDelegateAudioOperationImpl(i, i2, i3, str);
            } else {
                return AppOpsService.this.checkAudioOperationImpl(i, i2, i3, str);
            }
        }

        public final int checkDelegateAudioOperationImpl(int i, int i2, int i3, String str) {
            return this.mCheckOpsDelegate.checkAudioOperation(i, i2, i3, str, new C0474x6969d32f(AppOpsService.this));
        }

        public SyncNotedAppOp noteOperation(int i, int i2, String str, String str2, boolean z, String str3, boolean z2) {
            AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate = this.mPolicy;
            if (checkOpsDelegate != null) {
                if (this.mCheckOpsDelegate != null) {
                    return checkOpsDelegate.noteOperation(i, i2, str, str2, z, str3, z2, new HeptFunction() { // from class: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda2
                        public final Object apply(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
                            SyncNotedAppOp noteDelegateOperationImpl;
                            noteDelegateOperationImpl = AppOpsService.CheckOpsDelegateDispatcher.this.noteDelegateOperationImpl(((Integer) obj).intValue(), ((Integer) obj2).intValue(), (String) obj3, (String) obj4, ((Boolean) obj5).booleanValue(), (String) obj6, ((Boolean) obj7).booleanValue());
                            return noteDelegateOperationImpl;
                        }
                    });
                }
                return checkOpsDelegate.noteOperation(i, i2, str, str2, z, str3, z2, new C0476x34f2e5c8(AppOpsService.this));
            } else if (this.mCheckOpsDelegate != null) {
                return noteDelegateOperationImpl(i, i2, str, str2, z, str3, z2);
            } else {
                return AppOpsService.this.noteOperationImpl(i, i2, str, str2, z, str3, z2);
            }
        }

        public final SyncNotedAppOp noteDelegateOperationImpl(int i, int i2, String str, String str2, boolean z, String str3, boolean z2) {
            return this.mCheckOpsDelegate.noteOperation(i, i2, str, str2, z, str3, z2, new C0476x34f2e5c8(AppOpsService.this));
        }

        public SyncNotedAppOp noteProxyOperation(int i, AttributionSource attributionSource, boolean z, String str, boolean z2, boolean z3) {
            AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate = this.mPolicy;
            if (checkOpsDelegate != null) {
                if (this.mCheckOpsDelegate != null) {
                    return checkOpsDelegate.noteProxyOperation(i, attributionSource, z, str, z2, z3, new HexFunction() { // from class: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda4
                        public final Object apply(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
                            SyncNotedAppOp noteDelegateProxyOperationImpl;
                            noteDelegateProxyOperationImpl = AppOpsService.CheckOpsDelegateDispatcher.this.noteDelegateProxyOperationImpl(((Integer) obj).intValue(), (AttributionSource) obj2, ((Boolean) obj3).booleanValue(), (String) obj4, ((Boolean) obj5).booleanValue(), ((Boolean) obj6).booleanValue());
                            return noteDelegateProxyOperationImpl;
                        }
                    });
                }
                return checkOpsDelegate.noteProxyOperation(i, attributionSource, z, str, z2, z3, new C0478x34f2e5ca(AppOpsService.this));
            } else if (this.mCheckOpsDelegate != null) {
                return noteDelegateProxyOperationImpl(i, attributionSource, z, str, z2, z3);
            } else {
                return AppOpsService.this.noteProxyOperationImpl(i, attributionSource, z, str, z2, z3);
            }
        }

        public final SyncNotedAppOp noteDelegateProxyOperationImpl(int i, AttributionSource attributionSource, boolean z, String str, boolean z2, boolean z3) {
            return this.mCheckOpsDelegate.noteProxyOperation(i, attributionSource, z, str, z2, z3, new C0478x34f2e5ca(AppOpsService.this));
        }

        public SyncNotedAppOp startOperation(IBinder iBinder, int i, int i2, String str, String str2, boolean z, boolean z2, String str3, boolean z3, int i3, int i4) {
            AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate = this.mPolicy;
            if (checkOpsDelegate != null) {
                if (this.mCheckOpsDelegate != null) {
                    return checkOpsDelegate.startOperation(iBinder, i, i2, str, str2, z, z2, str3, z3, i3, i4, new UndecFunction() { // from class: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda10
                        public final Object apply(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11) {
                            SyncNotedAppOp startDelegateOperationImpl;
                            startDelegateOperationImpl = AppOpsService.CheckOpsDelegateDispatcher.this.startDelegateOperationImpl((IBinder) obj, ((Integer) obj2).intValue(), ((Integer) obj3).intValue(), (String) obj4, (String) obj5, ((Boolean) obj6).booleanValue(), ((Boolean) obj7).booleanValue(), (String) obj8, ((Boolean) obj9).booleanValue(), ((Integer) obj10).intValue(), ((Integer) obj11).intValue());
                            return startDelegateOperationImpl;
                        }
                    });
                }
                return checkOpsDelegate.startOperation(iBinder, i, i2, str, str2, z, z2, str3, z3, i3, i4, new C0470x6969d32b(AppOpsService.this));
            } else if (this.mCheckOpsDelegate != null) {
                return startDelegateOperationImpl(iBinder, i, i2, str, str2, z, z2, str3, z3, i3, i4);
            } else {
                return AppOpsService.this.startOperationImpl(iBinder, i, i2, str, str2, z, z2, str3, z3, i3, i4);
            }
        }

        public final SyncNotedAppOp startDelegateOperationImpl(IBinder iBinder, int i, int i2, String str, String str2, boolean z, boolean z2, String str3, boolean z3, int i3, int i4) {
            return this.mCheckOpsDelegate.startOperation(iBinder, i, i2, str, str2, z, z2, str3, z3, i3, i4, new C0470x6969d32b(AppOpsService.this));
        }

        public SyncNotedAppOp startProxyOperation(IBinder iBinder, int i, AttributionSource attributionSource, boolean z, boolean z2, String str, boolean z3, boolean z4, int i2, int i3, int i4) {
            AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate = this.mPolicy;
            if (checkOpsDelegate != null) {
                if (this.mCheckOpsDelegate != null) {
                    return checkOpsDelegate.startProxyOperation(iBinder, i, attributionSource, z, z2, str, z3, z4, i2, i3, i4, new UndecFunction() { // from class: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda0
                        public final Object apply(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10, Object obj11) {
                            SyncNotedAppOp startDelegateProxyOperationImpl;
                            startDelegateProxyOperationImpl = AppOpsService.CheckOpsDelegateDispatcher.this.startDelegateProxyOperationImpl((IBinder) obj, ((Integer) obj2).intValue(), (AttributionSource) obj3, ((Boolean) obj4).booleanValue(), ((Boolean) obj5).booleanValue(), (String) obj6, ((Boolean) obj7).booleanValue(), ((Boolean) obj8).booleanValue(), ((Integer) obj9).intValue(), ((Integer) obj10).intValue(), ((Integer) obj11).intValue());
                            return startDelegateProxyOperationImpl;
                        }
                    });
                }
                return checkOpsDelegate.startProxyOperation(iBinder, i, attributionSource, z, z2, str, z3, z4, i2, i3, i4, new C0468x34f2e5c6(AppOpsService.this));
            } else if (this.mCheckOpsDelegate != null) {
                return startDelegateProxyOperationImpl(iBinder, i, attributionSource, z, z2, str, z3, z4, i2, i3, i4);
            } else {
                return AppOpsService.this.startProxyOperationImpl(iBinder, i, attributionSource, z, z2, str, z3, z4, i2, i3, i4);
            }
        }

        public final SyncNotedAppOp startDelegateProxyOperationImpl(IBinder iBinder, int i, AttributionSource attributionSource, boolean z, boolean z2, String str, boolean z3, boolean z4, int i2, int i3, int i4) {
            return this.mCheckOpsDelegate.startProxyOperation(iBinder, i, attributionSource, z, z2, str, z3, z4, i2, i3, i4, new C0468x34f2e5c6(AppOpsService.this));
        }

        public void finishOperation(IBinder iBinder, int i, int i2, String str, String str2) {
            AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate = this.mPolicy;
            if (checkOpsDelegate != null) {
                if (this.mCheckOpsDelegate != null) {
                    checkOpsDelegate.finishOperation(iBinder, i, i2, str, str2, new QuintConsumer() { // from class: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda12
                        public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                            AppOpsService.CheckOpsDelegateDispatcher.this.finishDelegateOperationImpl((IBinder) obj, ((Integer) obj2).intValue(), ((Integer) obj3).intValue(), (String) obj4, (String) obj5);
                        }
                    });
                } else {
                    checkOpsDelegate.finishOperation(iBinder, i, i2, str, str2, new C0472x6969d32d(AppOpsService.this));
                }
            } else if (this.mCheckOpsDelegate != null) {
                finishDelegateOperationImpl(iBinder, i, i2, str, str2);
            } else {
                AppOpsService.this.finishOperationImpl(iBinder, i, i2, str, str2);
            }
        }

        public final void finishDelegateOperationImpl(IBinder iBinder, int i, int i2, String str, String str2) {
            this.mCheckOpsDelegate.finishOperation(iBinder, i, i2, str, str2, new C0472x6969d32d(AppOpsService.this));
        }

        public void finishProxyOperation(IBinder iBinder, int i, AttributionSource attributionSource, boolean z) {
            AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate = this.mPolicy;
            if (checkOpsDelegate != null) {
                if (this.mCheckOpsDelegate != null) {
                    checkOpsDelegate.finishProxyOperation(iBinder, i, attributionSource, z, new QuadFunction() { // from class: com.android.server.appop.AppOpsService$CheckOpsDelegateDispatcher$$ExternalSyntheticLambda6
                        public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
                            Void finishDelegateProxyOperationImpl;
                            finishDelegateProxyOperationImpl = AppOpsService.CheckOpsDelegateDispatcher.this.finishDelegateProxyOperationImpl((IBinder) obj, ((Integer) obj2).intValue(), (AttributionSource) obj3, ((Boolean) obj4).booleanValue());
                            return finishDelegateProxyOperationImpl;
                        }
                    });
                } else {
                    checkOpsDelegate.finishProxyOperation(iBinder, i, attributionSource, z, new C0480x34f2e5cc(AppOpsService.this));
                }
            } else if (this.mCheckOpsDelegate != null) {
                finishDelegateProxyOperationImpl(iBinder, i, attributionSource, z);
            } else {
                AppOpsService.this.finishProxyOperationImpl(iBinder, i, attributionSource, z);
            }
        }

        public final Void finishDelegateProxyOperationImpl(IBinder iBinder, int i, AttributionSource attributionSource, boolean z) {
            this.mCheckOpsDelegate.finishProxyOperation(iBinder, i, attributionSource, z, new C0480x34f2e5cc(AppOpsService.this));
            return null;
        }
    }
}
