package com.android.server.content;

import android.accounts.Account;
import android.accounts.AccountManagerInternal;
import android.annotation.RequiresPermission;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.compat.CompatChanges;
import android.app.job.JobInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IContentService;
import android.content.ISyncStatusObserver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.PeriodicSync;
import android.content.SyncAdapterType;
import android.content.SyncInfo;
import android.content.SyncRequest;
import android.content.SyncStatusInfo;
import android.content.pm.ProviderInfo;
import android.database.IContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.FactoryTest;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.BinderDeathDispatcher;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.content.ContentService;
import com.android.server.content.SyncStorageEngine;
import com.android.server.p011pm.permission.LegacyPermissionManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class ContentService extends IContentService.Stub {
    public static final BinderDeathDispatcher<IContentObserver> sObserverDeathDispatcher = new BinderDeathDispatcher<>();
    @GuardedBy({"sObserverLeakDetectedUid"})
    public static final ArraySet<Integer> sObserverLeakDetectedUid = new ArraySet<>(0);
    public final AccountManagerInternal mAccountManagerInternal;
    public Context mContext;
    public boolean mFactoryTest;
    public final ObserverNode mRootNode = new ObserverNode("");
    public SyncManager mSyncManager = null;
    public final Object mSyncManagerLock = new Object();
    @GuardedBy({"mCache"})
    public final SparseArray<ArrayMap<String, ArrayMap<Pair<String, Uri>, Bundle>>> mCache = new SparseArray<>();
    public BroadcastReceiver mCacheReceiver = new BroadcastReceiver() { // from class: com.android.server.content.ContentService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            synchronized (ContentService.this.mCache) {
                if ("android.intent.action.LOCALE_CHANGED".equals(intent.getAction())) {
                    ContentService.this.mCache.clear();
                } else {
                    Uri data = intent.getData();
                    if (data != null) {
                        ContentService.this.invalidateCacheLocked(intent.getIntExtra("android.intent.extra.user_handle", -10000), data.getSchemeSpecificPart(), null);
                    }
                }
            }
        }
    };

    public static int normalizeSyncable(int i) {
        if (i > 0) {
            return 1;
        }
        return i == 0 ? 0 : -2;
    }

    public final int getProcStateForStatsd(int i) {
        switch (i) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 5;
            case 5:
                return 6;
            case 6:
                return 7;
            case 7:
                return 8;
            case 8:
                return 9;
            case 9:
                return 10;
            case 10:
                return 11;
            case 11:
                return 12;
            case 12:
                return 13;
            case 13:
                return 14;
            case 14:
            default:
                return 0;
            case 15:
                return 16;
            case 16:
                return 17;
            case 17:
                return 18;
            case 18:
                return 19;
            case 19:
                return 20;
        }
    }

    public final int getRestrictionLevelForStatsd(int i) {
        if (i != 10) {
            if (i != 20) {
                if (i != 30) {
                    if (i != 40) {
                        if (i != 50) {
                            return i != 60 ? 0 : 6;
                        }
                        return 5;
                    }
                    return 4;
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }

    /* loaded from: classes.dex */
    public static class Lifecycle extends SystemService {
        public ContentService mService;

        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v1, types: [com.android.server.content.ContentService, android.os.IBinder] */
        @Override // com.android.server.SystemService
        public void onStart() {
            ?? contentService = new ContentService(getContext(), FactoryTest.getMode() == 1);
            this.mService = contentService;
            publishBinderService("content", contentService);
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            this.mService.onBootPhase(i);
        }

        @Override // com.android.server.SystemService
        public void onUserStarting(SystemService.TargetUser targetUser) {
            this.mService.onStartUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserUnlocking(SystemService.TargetUser targetUser) {
            this.mService.onUnlockUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserStopping(SystemService.TargetUser targetUser) {
            this.mService.onStopUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserStopped(SystemService.TargetUser targetUser) {
            synchronized (this.mService.mCache) {
                this.mService.mCache.remove(targetUser.getUserIdentifier());
            }
        }
    }

    public final SyncManager getSyncManager() {
        SyncManager syncManager;
        synchronized (this.mSyncManagerLock) {
            if (this.mSyncManager == null) {
                this.mSyncManager = new SyncManager(this.mContext, this.mFactoryTest);
            }
            syncManager = this.mSyncManager;
        }
        return syncManager;
    }

    public void onStartUser(int i) {
        SyncManager syncManager = this.mSyncManager;
        if (syncManager != null) {
            syncManager.onStartUser(i);
        }
    }

    public void onUnlockUser(int i) {
        SyncManager syncManager = this.mSyncManager;
        if (syncManager != null) {
            syncManager.onUnlockUser(i);
        }
    }

    public void onStopUser(int i) {
        SyncManager syncManager = this.mSyncManager;
        if (syncManager != null) {
            syncManager.onStopUser(i);
        }
    }

    public synchronized void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        int i;
        if (DumpUtils.checkDumpAndUsageStatsPermission(this.mContext, "ContentService", printWriter)) {
            PrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            boolean contains = ArrayUtils.contains(strArr, "-a");
            long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
            SyncManager syncManager = this.mSyncManager;
            if (syncManager == null) {
                indentingPrintWriter.println("SyncManager not available yet");
            } else {
                syncManager.dump(fileDescriptor, indentingPrintWriter, contains);
            }
            indentingPrintWriter.println();
            indentingPrintWriter.println("Observer tree:");
            synchronized (this.mRootNode) {
                int[] iArr = new int[2];
                final SparseIntArray sparseIntArray = new SparseIntArray();
                this.mRootNode.dumpLocked(fileDescriptor, indentingPrintWriter, strArr, "", "  ", iArr, sparseIntArray);
                indentingPrintWriter.println();
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < sparseIntArray.size(); i2++) {
                    arrayList.add(Integer.valueOf(sparseIntArray.keyAt(i2)));
                }
                Collections.sort(arrayList, new Comparator<Integer>() { // from class: com.android.server.content.ContentService.2
                    @Override // java.util.Comparator
                    public int compare(Integer num, Integer num2) {
                        int i3 = sparseIntArray.get(num.intValue());
                        int i4 = sparseIntArray.get(num2.intValue());
                        if (i3 < i4) {
                            return 1;
                        }
                        return i3 > i4 ? -1 : 0;
                    }
                });
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    int intValue = ((Integer) arrayList.get(i3)).intValue();
                    indentingPrintWriter.print("  pid ");
                    indentingPrintWriter.print(intValue);
                    indentingPrintWriter.print(": ");
                    indentingPrintWriter.print(sparseIntArray.get(intValue));
                    indentingPrintWriter.println(" observers");
                }
                indentingPrintWriter.println();
                indentingPrintWriter.increaseIndent();
                indentingPrintWriter.print("Total number of nodes: ");
                indentingPrintWriter.println(iArr[0]);
                indentingPrintWriter.print("Total number of observers: ");
                indentingPrintWriter.println(iArr[1]);
                sObserverDeathDispatcher.dump(indentingPrintWriter);
                indentingPrintWriter.decreaseIndent();
            }
            ArraySet<Integer> arraySet = sObserverLeakDetectedUid;
            synchronized (arraySet) {
                indentingPrintWriter.println();
                indentingPrintWriter.print("Observer leaking UIDs: ");
                indentingPrintWriter.println(arraySet.toString());
            }
            synchronized (this.mCache) {
                indentingPrintWriter.println();
                indentingPrintWriter.println("Cached content:");
                indentingPrintWriter.increaseIndent();
                for (i = 0; i < this.mCache.size(); i++) {
                    indentingPrintWriter.println("User " + this.mCache.keyAt(i) + XmlUtils.STRING_ARRAY_SEPARATOR);
                    indentingPrintWriter.increaseIndent();
                    indentingPrintWriter.println(this.mCache.valueAt(i));
                    indentingPrintWriter.decreaseIndent();
                }
                indentingPrintWriter.decreaseIndent();
            }
            IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public ContentService(Context context, boolean z) {
        this.mContext = context;
        this.mFactoryTest = z;
        ((LegacyPermissionManagerInternal) LocalServices.getService(LegacyPermissionManagerInternal.class)).setSyncAdapterPackagesProvider(new LegacyPermissionManagerInternal.SyncAdapterPackagesProvider() { // from class: com.android.server.content.ContentService$$ExternalSyntheticLambda1
            @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal.SyncAdapterPackagesProvider
            public final String[] getPackages(String str, int i) {
                String[] lambda$new$0;
                lambda$new$0 = ContentService.this.lambda$new$0(str, i);
                return lambda$new$0;
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_DATA_CLEARED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiverAsUser(this.mCacheReceiver, UserHandle.ALL, intentFilter, null, null);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.LOCALE_CHANGED");
        this.mContext.registerReceiverAsUser(this.mCacheReceiver, UserHandle.ALL, intentFilter2, null, null);
        this.mAccountManagerInternal = (AccountManagerInternal) LocalServices.getService(AccountManagerInternal.class);
    }

    public void onBootPhase(int i) {
        if (i == 550) {
            getSyncManager();
        }
        SyncManager syncManager = this.mSyncManager;
        if (syncManager != null) {
            syncManager.onBootPhase(i);
        }
    }

    public void registerContentObserver(Uri uri, boolean z, IContentObserver iContentObserver, int i, int i2) {
        if (iContentObserver == null || uri == null) {
            throw new IllegalArgumentException("You must pass a valid uri and observer");
        }
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        int handleIncomingUser = handleIncomingUser(uri, callingPid, callingUid, 1, true, i);
        String checkContentProviderAccess = ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).checkContentProviderAccess(uri.getAuthority(), handleIncomingUser);
        if (checkContentProviderAccess != null) {
            if (i2 >= 26) {
                throw new SecurityException(checkContentProviderAccess);
            }
            if (!checkContentProviderAccess.startsWith("Failed to find provider")) {
                Log.w("ContentService", "Ignoring content changes for " + uri + " from " + callingUid + ": " + checkContentProviderAccess);
                return;
            }
        }
        synchronized (this.mRootNode) {
            ObserverNode observerNode = this.mRootNode;
            observerNode.addObserverLocked(uri, iContentObserver, z, observerNode, callingUid, callingPid, handleIncomingUser);
        }
    }

    public void unregisterContentObserver(IContentObserver iContentObserver) {
        if (iContentObserver == null) {
            throw new IllegalArgumentException("You must pass a valid observer");
        }
        synchronized (this.mRootNode) {
            this.mRootNode.removeObserverLocked(iContentObserver);
        }
    }

    public void notifyChange(Uri[] uriArr, IContentObserver iContentObserver, boolean z, int i, int i2, int i3, String str) {
        ArrayMap arrayMap;
        int i4;
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        int callingUserId = UserHandle.getCallingUserId();
        ObserverCollector observerCollector = new ObserverCollector();
        ArrayMap arrayMap2 = new ArrayMap();
        for (Uri uri : uriArr) {
            int handleIncomingUser = handleIncomingUser(uri, callingPid, callingUid, 2, true, i2);
            Pair create = Pair.create(uri.getAuthority(), Integer.valueOf(handleIncomingUser));
            if (!arrayMap2.containsKey(create)) {
                String checkContentProviderAccess = ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).checkContentProviderAccess(uri.getAuthority(), handleIncomingUser);
                if (checkContentProviderAccess != null) {
                    if (i3 >= 26) {
                        throw new SecurityException(checkContentProviderAccess);
                    }
                    if (!checkContentProviderAccess.startsWith("Failed to find provider")) {
                        Log.w("ContentService", "Ignoring notify for " + uri + " from " + callingUid + ": " + checkContentProviderAccess);
                        continue;
                    }
                }
                arrayMap2.put(create, getProviderPackageName(uri, handleIncomingUser));
            }
            synchronized (this.mRootNode) {
                this.mRootNode.collectObserversLocked(uri, ObserverNode.countUriSegments(uri), 0, iContentObserver, z, i, handleIncomingUser, observerCollector);
            }
            continue;
        }
        long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
        try {
            observerCollector.dispatch();
            SyncManager syncManager = getSyncManager();
            int i5 = 0;
            while (i5 < arrayMap2.size()) {
                String str2 = (String) ((Pair) arrayMap2.keyAt(i5)).first;
                int intValue = ((Integer) ((Pair) arrayMap2.keyAt(i5)).second).intValue();
                String str3 = (String) arrayMap2.valueAt(i5);
                if ((i & 1) != 0) {
                    arrayMap = arrayMap2;
                    i4 = callingUid;
                    syncManager.scheduleLocalSync(null, callingUserId, callingUid, str2, getSyncExemptionForCaller(callingUid), callingUid, callingPid, str);
                } else {
                    arrayMap = arrayMap2;
                    i4 = callingUid;
                }
                synchronized (this.mCache) {
                    for (Uri uri2 : uriArr) {
                        if (Objects.equals(uri2.getAuthority(), str2)) {
                            invalidateCacheLocked(intValue, str3, uri2);
                        }
                    }
                }
                i5++;
                arrayMap2 = arrayMap;
                callingUid = i4;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final int checkUriPermission(Uri uri, int i, int i2, int i3, int i4) {
        try {
            return ActivityManager.getService().checkUriPermission(uri, i, i2, i3, i4, (IBinder) null);
        } catch (RemoteException unused) {
            return -1;
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class ObserverCollector {
        public final ArrayMap<Key, List<Uri>> collected = new ArrayMap<>();

        /* loaded from: classes.dex */
        public static class Key {
            public final int flags;
            public final IContentObserver observer;
            public final boolean selfChange;
            public final int uid;
            public final int userId;

            public Key(IContentObserver iContentObserver, int i, boolean z, int i2, int i3) {
                this.observer = iContentObserver;
                this.uid = i;
                this.selfChange = z;
                this.flags = i2;
                this.userId = i3;
            }

            public boolean equals(Object obj) {
                if (obj instanceof Key) {
                    Key key = (Key) obj;
                    return Objects.equals(this.observer, key.observer) && this.uid == key.uid && this.selfChange == key.selfChange && this.flags == key.flags && this.userId == key.userId;
                }
                return false;
            }

            public int hashCode() {
                return Objects.hash(this.observer, Integer.valueOf(this.uid), Boolean.valueOf(this.selfChange), Integer.valueOf(this.flags), Integer.valueOf(this.userId));
            }
        }

        public void collect(IContentObserver iContentObserver, int i, boolean z, Uri uri, int i2, int i3) {
            Key key = new Key(iContentObserver, i, z, i2, i3);
            List<Uri> list = this.collected.get(key);
            if (list == null) {
                list = new ArrayList<>();
                this.collected.put(key, list);
            }
            list.add(uri);
        }

        public void dispatch() {
            for (int i = 0; i < this.collected.size(); i++) {
                final Key keyAt = this.collected.keyAt(i);
                final List<Uri> valueAt = this.collected.valueAt(i);
                Runnable runnable = new Runnable() { // from class: com.android.server.content.ContentService$ObserverCollector$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ContentService.ObserverCollector.lambda$dispatch$0(ContentService.ObserverCollector.Key.this, valueAt);
                    }
                };
                boolean z = (keyAt.flags & 32768) != 0;
                if (((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).getUidProcessState(keyAt.uid) <= 6 || z) {
                    runnable.run();
                } else {
                    BackgroundThread.getHandler().postDelayed(runnable, 10000L);
                }
            }
        }

        public static /* synthetic */ void lambda$dispatch$0(Key key, List list) {
            try {
                key.observer.onChangeEtc(key.selfChange, (Uri[]) list.toArray(new Uri[list.size()]), key.flags, key.userId);
            } catch (RemoteException unused) {
            }
        }
    }

    public void requestSync(Account account, String str, Bundle bundle, String str2) {
        Bundle.setDefusable(bundle, true);
        ContentResolver.validateSyncExtrasBundle(bundle);
        int callingUserId = UserHandle.getCallingUserId();
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        if (hasAccountAccess(true, account, callingUid) && hasAuthorityAccess(str, callingUid, callingUserId)) {
            validateExtras(callingUid, bundle);
            int syncExemptionAndCleanUpExtrasForCaller = getSyncExemptionAndCleanUpExtrasForCaller(callingUid, bundle);
            long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
            try {
                getSyncManager().scheduleSync(account, callingUserId, callingUid, str, bundle, -2, syncExemptionAndCleanUpExtrasForCaller, callingUid, callingPid, str2);
            } finally {
                IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public void sync(SyncRequest syncRequest, String str) {
        syncAsUser(syncRequest, UserHandle.getCallingUserId(), str);
    }

    public final long clampPeriod(long j) {
        long minPeriodMillis = JobInfo.getMinPeriodMillis() / 1000;
        if (j < minPeriodMillis) {
            Slog.w("ContentService", "Requested poll frequency of " + j + " seconds being rounded up to " + minPeriodMillis + "s.");
            return minPeriodMillis;
        }
        return j;
    }

    public void syncAsUser(SyncRequest syncRequest, int i, String str) {
        enforceCrossUserPermission(i, "no permission to request sync as user: " + i);
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        if (hasAccountAccess(true, syncRequest.getAccount(), callingUid) && hasAuthorityAccess(syncRequest.getProvider(), callingUid, i)) {
            Bundle bundle = syncRequest.getBundle();
            validateExtras(callingUid, bundle);
            int syncExemptionAndCleanUpExtrasForCaller = getSyncExemptionAndCleanUpExtrasForCaller(callingUid, bundle);
            long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
            try {
                long syncFlexTime = syncRequest.getSyncFlexTime();
                long syncRunTime = syncRequest.getSyncRunTime();
                if (syncRequest.isPeriodic()) {
                    this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
                    getSyncManager().updateOrAddPeriodicSync(new SyncStorageEngine.EndPoint(syncRequest.getAccount(), syncRequest.getProvider(), i), clampPeriod(syncRunTime), syncFlexTime, bundle);
                } else {
                    getSyncManager().scheduleSync(syncRequest.getAccount(), i, callingUid, syncRequest.getProvider(), bundle, -2, syncExemptionAndCleanUpExtrasForCaller, callingUid, callingPid, str);
                }
            } finally {
                IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public void cancelSync(Account account, String str, ComponentName componentName) {
        cancelSyncAsUser(account, str, componentName, UserHandle.getCallingUserId());
    }

    public void cancelSyncAsUser(Account account, String str, ComponentName componentName, int i) {
        if (str != null && str.length() == 0) {
            throw new IllegalArgumentException("Authority must be non-empty");
        }
        enforceCrossUserPermission(i, "no permission to modify the sync settings for user " + i);
        if (componentName != null) {
            Slog.e("ContentService", "cname not null.");
            return;
        }
        long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
        try {
            SyncStorageEngine.EndPoint endPoint = new SyncStorageEngine.EndPoint(account, str, i);
            getSyncManager().clearScheduledSyncOperations(endPoint);
            getSyncManager().cancelActiveSync(endPoint, null, "API");
        } finally {
            IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void cancelRequest(SyncRequest syncRequest) {
        if (syncRequest.isPeriodic()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
        }
        int callingUid = Binder.getCallingUid();
        Bundle bundle = new Bundle(syncRequest.getBundle());
        validateExtras(callingUid, bundle);
        int callingUserId = UserHandle.getCallingUserId();
        long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
        try {
            SyncStorageEngine.EndPoint endPoint = new SyncStorageEngine.EndPoint(syncRequest.getAccount(), syncRequest.getProvider(), callingUserId);
            if (syncRequest.isPeriodic()) {
                SyncManager syncManager = getSyncManager();
                syncManager.removePeriodicSync(endPoint, bundle, "cancelRequest() by uid=" + callingUid);
            }
            getSyncManager().cancelScheduledSyncOperation(endPoint, bundle);
            getSyncManager().cancelActiveSync(endPoint, bundle, "API");
        } finally {
            IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public SyncAdapterType[] getSyncAdapterTypes() {
        return getSyncAdapterTypesAsUser(UserHandle.getCallingUserId());
    }

    public SyncAdapterType[] getSyncAdapterTypesAsUser(int i) {
        enforceCrossUserPermission(i, "no permission to read sync settings for user " + i);
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
        try {
            return getSyncManager().getSyncAdapterTypes(callingUid, i);
        } finally {
            IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* renamed from: getSyncAdapterPackagesForAuthorityAsUser */
    public String[] lambda$new$0(String str, int i) {
        enforceCrossUserPermission(i, "no permission to read sync settings for user " + i);
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
        try {
            return getSyncManager().getSyncAdapterPackagesForAuthorityAsUser(str, callingUid, i);
        } finally {
            IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public String getSyncAdapterPackageAsUser(String str, String str2, int i) {
        enforceCrossUserPermission(i, "no permission to read sync settings for user " + i);
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
        try {
            return getSyncManager().getSyncAdapterPackageAsUser(str, str2, callingUid, i);
        } finally {
            IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean getSyncAutomatically(Account account, String str) {
        return getSyncAutomaticallyAsUser(account, str, UserHandle.getCallingUserId());
    }

    public boolean getSyncAutomaticallyAsUser(Account account, String str, int i) {
        enforceCrossUserPermission(i, "no permission to read the sync settings for user " + i);
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_SETTINGS", "no permission to read the sync settings");
        int callingUid = Binder.getCallingUid();
        if (hasAccountAccess(true, account, callingUid) && hasAuthorityAccess(str, callingUid, i)) {
            long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
            try {
                return getSyncManager().getSyncStorageEngine().getSyncAutomatically(account, i, str);
            } finally {
                IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return false;
    }

    public void setSyncAutomatically(Account account, String str, boolean z) {
        setSyncAutomaticallyAsUser(account, str, z, UserHandle.getCallingUserId());
    }

    public void setSyncAutomaticallyAsUser(Account account, String str, boolean z, int i) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Authority must be non-empty");
        }
        this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
        enforceCrossUserPermission(i, "no permission to modify the sync settings for user " + i);
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        if (hasAccountAccess(true, account, callingUid) && hasAuthorityAccess(str, callingUid, i)) {
            int syncExemptionForCaller = getSyncExemptionForCaller(callingUid);
            long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
            try {
                getSyncManager().getSyncStorageEngine().setSyncAutomatically(account, i, str, z, syncExemptionForCaller, callingUid, callingPid);
            } finally {
                IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public void addPeriodicSync(Account account, String str, Bundle bundle, long j) {
        Bundle.setDefusable(bundle, true);
        if (account == null) {
            throw new IllegalArgumentException("Account must not be null");
        }
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Authority must not be empty.");
        }
        this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        if (hasAccountAccess(true, account, callingUid) && hasAuthorityAccess(str, callingUid, callingUserId)) {
            validateExtras(callingUid, bundle);
            long clampPeriod = clampPeriod(j);
            long calculateDefaultFlexTime = SyncStorageEngine.calculateDefaultFlexTime(clampPeriod);
            long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
            try {
                getSyncManager().updateOrAddPeriodicSync(new SyncStorageEngine.EndPoint(account, str, callingUserId), clampPeriod, calculateDefaultFlexTime, bundle);
            } finally {
                IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public void removePeriodicSync(Account account, String str, Bundle bundle) {
        Bundle.setDefusable(bundle, true);
        if (account == null) {
            throw new IllegalArgumentException("Account must not be null");
        }
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Authority must not be empty");
        }
        this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        if (hasAccountAccess(true, account, callingUid) && hasAuthorityAccess(str, callingUid, callingUserId)) {
            validateExtras(callingUid, bundle);
            long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
            try {
                SyncManager syncManager = getSyncManager();
                SyncStorageEngine.EndPoint endPoint = new SyncStorageEngine.EndPoint(account, str, callingUserId);
                syncManager.removePeriodicSync(endPoint, bundle, "removePeriodicSync() by uid=" + callingUid);
            } finally {
                IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public List<PeriodicSync> getPeriodicSyncs(Account account, String str, ComponentName componentName) {
        if (account == null) {
            throw new IllegalArgumentException("Account must not be null");
        }
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Authority must not be empty");
        }
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_SETTINGS", "no permission to read the sync settings");
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        if (!hasAccountAccess(true, account, callingUid)) {
            return new ArrayList();
        }
        if (!hasAuthorityAccess(str, callingUid, callingUserId)) {
            return new ArrayList();
        }
        long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
        try {
            return getSyncManager().getPeriodicSyncs(new SyncStorageEngine.EndPoint(account, str, callingUserId));
        } finally {
            IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getIsSyncable(Account account, String str) {
        return getIsSyncableAsUser(account, str, UserHandle.getCallingUserId());
    }

    public int getIsSyncableAsUser(Account account, String str, int i) {
        enforceCrossUserPermission(i, "no permission to read the sync settings for user " + i);
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_SETTINGS", "no permission to read the sync settings");
        int callingUid = Binder.getCallingUid();
        if (hasAccountAccess(true, account, callingUid) && hasAuthorityAccess(str, callingUid, i)) {
            long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
            try {
                return getSyncManager().computeSyncable(account, i, str, false);
            } finally {
                IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return 0;
    }

    public void setIsSyncable(Account account, String str, int i) {
        setIsSyncableAsUser(account, str, i, UserHandle.getCallingUserId());
    }

    public void setIsSyncableAsUser(Account account, String str, int i, int i2) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Authority must not be empty");
        }
        enforceCrossUserPermission(i2, "no permission to set the sync settings for user " + i2);
        this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
        int normalizeSyncable = normalizeSyncable(i);
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        if (hasAccountAccess(true, account, callingUid) && hasAuthorityAccess(str, callingUid, i2)) {
            long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
            try {
                getSyncManager().getSyncStorageEngine().setIsSyncable(account, i2, str, normalizeSyncable, callingUid, callingPid);
            } finally {
                IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public boolean getMasterSyncAutomatically() {
        return getMasterSyncAutomaticallyAsUser(UserHandle.getCallingUserId());
    }

    public boolean getMasterSyncAutomaticallyAsUser(int i) {
        enforceCrossUserPermission(i, "no permission to read the sync settings for user " + i);
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_SETTINGS", "no permission to read the sync settings");
        long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
        try {
            return getSyncManager().getSyncStorageEngine().getMasterSyncAutomatically(i);
        } finally {
            IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setMasterSyncAutomatically(boolean z) {
        setMasterSyncAutomaticallyAsUser(z, UserHandle.getCallingUserId());
    }

    public void setMasterSyncAutomaticallyAsUser(boolean z, int i) {
        enforceCrossUserPermission(i, "no permission to set the sync status for user " + i);
        this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SYNC_SETTINGS", "no permission to write the sync settings");
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
        try {
            getSyncManager().getSyncStorageEngine().setMasterSyncAutomatically(z, i, getSyncExemptionForCaller(callingUid), callingUid, callingPid);
        } finally {
            IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isSyncActive(Account account, String str, ComponentName componentName) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_STATS", "no permission to read the sync stats");
        int callingUid = Binder.getCallingUid();
        int callingUserId = UserHandle.getCallingUserId();
        if (hasAccountAccess(true, account, callingUid) && hasAuthorityAccess(str, callingUid, callingUserId)) {
            long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
            try {
                return getSyncManager().getSyncStorageEngine().isSyncActive(new SyncStorageEngine.EndPoint(account, str, callingUserId));
            } finally {
                IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return false;
    }

    public List<SyncInfo> getCurrentSyncs() {
        return getCurrentSyncsAsUser(UserHandle.getCallingUserId());
    }

    public List<SyncInfo> getCurrentSyncsAsUser(final int i) {
        enforceCrossUserPermission(i, "no permission to read the sync settings for user " + i);
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_STATS", "no permission to read the sync stats");
        boolean z = this.mContext.checkCallingOrSelfPermission("android.permission.GET_ACCOUNTS") == 0;
        final int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
        try {
            List<SyncInfo> currentSyncsCopy = getSyncManager().getSyncStorageEngine().getCurrentSyncsCopy(i, z);
            IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            currentSyncsCopy.removeIf(new Predicate() { // from class: com.android.server.content.ContentService$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getCurrentSyncsAsUser$1;
                    lambda$getCurrentSyncsAsUser$1 = ContentService.this.lambda$getCurrentSyncsAsUser$1(callingUid, i, (SyncInfo) obj);
                    return lambda$getCurrentSyncsAsUser$1;
                }
            });
            return currentSyncsCopy;
        } catch (Throwable th) {
            IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getCurrentSyncsAsUser$1(int i, int i2, SyncInfo syncInfo) {
        return !hasAuthorityAccess(syncInfo.authority, i, i2);
    }

    public SyncStatusInfo getSyncStatus(Account account, String str, ComponentName componentName) {
        return getSyncStatusAsUser(account, str, componentName, UserHandle.getCallingUserId());
    }

    public SyncStatusInfo getSyncStatusAsUser(Account account, String str, ComponentName componentName, int i) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Authority must not be empty");
        }
        enforceCrossUserPermission(i, "no permission to read the sync stats for user " + i);
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_STATS", "no permission to read the sync stats");
        int callingUid = Binder.getCallingUid();
        if (hasAccountAccess(true, account, callingUid) && hasAuthorityAccess(str, callingUid, i)) {
            long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
            try {
                if (account != null && str != null) {
                    return getSyncManager().getSyncStorageEngine().getStatusByAuthority(new SyncStorageEngine.EndPoint(account, str, i));
                }
                throw new IllegalArgumentException("Must call sync status with valid authority");
            } finally {
                IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return null;
    }

    public boolean isSyncPending(Account account, String str, ComponentName componentName) {
        return isSyncPendingAsUser(account, str, componentName, UserHandle.getCallingUserId());
    }

    public boolean isSyncPendingAsUser(Account account, String str, ComponentName componentName, int i) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_SYNC_STATS", "no permission to read the sync stats");
        enforceCrossUserPermission(i, "no permission to retrieve the sync settings for user " + i);
        int callingUid = Binder.getCallingUid();
        if (hasAccountAccess(true, account, callingUid) && hasAuthorityAccess(str, callingUid, i)) {
            long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
            try {
                if (account != null && str != null) {
                    return getSyncManager().getSyncStorageEngine().isSyncPending(new SyncStorageEngine.EndPoint(account, str, i));
                }
                throw new IllegalArgumentException("Invalid authority specified");
            } finally {
                IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return false;
    }

    public void addStatusChangeListener(int i, ISyncStatusObserver iSyncStatusObserver) {
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
        if (iSyncStatusObserver != null) {
            try {
                getSyncManager().getSyncStorageEngine().addStatusChangeListener(i, callingUid, iSyncStatusObserver);
            } finally {
                IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public void removeStatusChangeListener(ISyncStatusObserver iSyncStatusObserver) {
        long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
        if (iSyncStatusObserver != null) {
            try {
                getSyncManager().getSyncStorageEngine().removeStatusChangeListener(iSyncStatusObserver);
            } finally {
                IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public final String getProviderPackageName(Uri uri, int i) {
        ProviderInfo resolveContentProviderAsUser = this.mContext.getPackageManager().resolveContentProviderAsUser(uri.getAuthority(), 0, i);
        if (resolveContentProviderAsUser != null) {
            return resolveContentProviderAsUser.packageName;
        }
        return null;
    }

    @GuardedBy({"mCache"})
    public final ArrayMap<Pair<String, Uri>, Bundle> findOrCreateCacheLocked(int i, String str) {
        ArrayMap<String, ArrayMap<Pair<String, Uri>, Bundle>> arrayMap = this.mCache.get(i);
        if (arrayMap == null) {
            arrayMap = new ArrayMap<>();
            this.mCache.put(i, arrayMap);
        }
        ArrayMap<Pair<String, Uri>, Bundle> arrayMap2 = arrayMap.get(str);
        if (arrayMap2 == null) {
            ArrayMap<Pair<String, Uri>, Bundle> arrayMap3 = new ArrayMap<>();
            arrayMap.put(str, arrayMap3);
            return arrayMap3;
        }
        return arrayMap2;
    }

    @GuardedBy({"mCache"})
    public final void invalidateCacheLocked(int i, String str, Uri uri) {
        ArrayMap<Pair<String, Uri>, Bundle> arrayMap;
        ArrayMap<String, ArrayMap<Pair<String, Uri>, Bundle>> arrayMap2 = this.mCache.get(i);
        if (arrayMap2 == null || (arrayMap = arrayMap2.get(str)) == null) {
            return;
        }
        if (uri != null) {
            int i2 = 0;
            while (i2 < arrayMap.size()) {
                Object obj = arrayMap.keyAt(i2).second;
                if (obj == null || !((Uri) obj).toString().startsWith(uri.toString())) {
                    i2++;
                } else {
                    arrayMap.removeAt(i2);
                }
            }
            return;
        }
        arrayMap.clear();
    }

    @RequiresPermission("android.permission.CACHE_CONTENT")
    public void putCache(String str, Uri uri, Bundle bundle, int i) {
        Bundle.setDefusable(bundle, true);
        enforceNonFullCrossUserPermission(i, "ContentService");
        this.mContext.enforceCallingOrSelfPermission("android.permission.CACHE_CONTENT", "ContentService");
        ((AppOpsManager) this.mContext.getSystemService(AppOpsManager.class)).checkPackage(Binder.getCallingUid(), str);
        String providerPackageName = getProviderPackageName(uri, i);
        Pair<String, Uri> create = Pair.create(str, uri);
        synchronized (this.mCache) {
            ArrayMap<Pair<String, Uri>, Bundle> findOrCreateCacheLocked = findOrCreateCacheLocked(i, providerPackageName);
            if (bundle != null) {
                findOrCreateCacheLocked.put(create, bundle);
            } else {
                findOrCreateCacheLocked.remove(create);
            }
        }
    }

    @RequiresPermission("android.permission.CACHE_CONTENT")
    public Bundle getCache(String str, Uri uri, int i) {
        Bundle bundle;
        enforceNonFullCrossUserPermission(i, "ContentService");
        this.mContext.enforceCallingOrSelfPermission("android.permission.CACHE_CONTENT", "ContentService");
        ((AppOpsManager) this.mContext.getSystemService(AppOpsManager.class)).checkPackage(Binder.getCallingUid(), str);
        String providerPackageName = getProviderPackageName(uri, i);
        Pair create = Pair.create(str, uri);
        synchronized (this.mCache) {
            bundle = findOrCreateCacheLocked(i, providerPackageName).get(create);
        }
        return bundle;
    }

    public final int handleIncomingUser(Uri uri, int i, int i2, int i3, boolean z, int i4) {
        if (i4 == -2) {
            i4 = ActivityManager.getCurrentUser();
        }
        if (i4 == -1) {
            Context context = this.mContext;
            context.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "No access to " + uri);
        } else if (i4 < 0) {
            throw new IllegalArgumentException("Invalid user: " + i4);
        } else if (i4 != UserHandle.getCallingUserId() && checkUriPermission(uri, i, i2, i3, i4) != 0) {
            boolean z2 = true;
            if (this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0 && (!z || this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS") != 0)) {
                z2 = false;
            }
            if (!z2) {
                String str = z ? "android.permission.INTERACT_ACROSS_USERS_FULL or android.permission.INTERACT_ACROSS_USERS" : "android.permission.INTERACT_ACROSS_USERS_FULL";
                throw new SecurityException("No access to " + uri + ": neither user " + i2 + " nor current process has " + str);
            }
        }
        return i4;
    }

    public final void enforceCrossUserPermission(int i, String str) {
        if (UserHandle.getCallingUserId() != i) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", str);
        }
    }

    public final void enforceNonFullCrossUserPermission(int i, String str) {
        if (UserHandle.getCallingUserId() == i || this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS") == 0) {
            return;
        }
        this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", str);
    }

    public final boolean hasAccountAccess(boolean z, Account account, int i) {
        if (account == null) {
            return true;
        }
        if (!z || CompatChanges.isChangeEnabled(201794303L, i)) {
            long clearCallingIdentity = IContentService.Stub.clearCallingIdentity();
            try {
                return this.mAccountManagerInternal.hasAccountAccess(account, i);
            } finally {
                IContentService.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return true;
    }

    public final boolean hasAuthorityAccess(String str, int i, int i2) {
        if (!TextUtils.isEmpty(str) && CompatChanges.isChangeEnabled(207133734L, i)) {
            return !ArrayUtils.isEmpty(lambda$new$0(str, i2));
        }
        return true;
    }

    public final void validateExtras(int i, Bundle bundle) {
        if (!bundle.containsKey("v_exemption") || i == 0 || i == 1000 || i == 2000) {
            return;
        }
        Log.w("ContentService", "Invalid extras specified. requestsync -f/-F needs to run on 'adb shell'");
        throw new SecurityException("Invalid extras specified.");
    }

    public final int getSyncExemptionForCaller(int i) {
        return getSyncExemptionAndCleanUpExtrasForCaller(i, null);
    }

    public final int getSyncExemptionAndCleanUpExtrasForCaller(int i, Bundle bundle) {
        if (bundle != null) {
            int i2 = bundle.getInt("v_exemption", -1);
            bundle.remove("v_exemption");
            if (i2 != -1) {
                return i2;
            }
        }
        ActivityManagerInternal activityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        if (activityManagerInternal == null) {
            return 0;
        }
        int uidProcessState = activityManagerInternal.getUidProcessState(i);
        boolean isUidActive = activityManagerInternal.isUidActive(i);
        if (uidProcessState <= 2 || uidProcessState == 3) {
            return 2;
        }
        if (uidProcessState <= 6 || isUidActive) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.SYNC_EXEMPTION_OCCURRED, i, getProcStateForStatsd(uidProcessState), isUidActive, getRestrictionLevelForStatsd(activityManagerInternal.getRestrictionLevel(i)));
            return 1;
        }
        return 0;
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static final class ObserverNode {
        public String mName;
        public ArrayList<ObserverNode> mChildren = new ArrayList<>();
        public ArrayList<ObserverEntry> mObservers = new ArrayList<>();

        /* loaded from: classes.dex */
        public class ObserverEntry implements IBinder.DeathRecipient {
            public final boolean notifyForDescendants;
            public final IContentObserver observer;
            public final Object observersLock;
            public final int pid;
            public final int uid;
            public final int userHandle;

            public ObserverEntry(IContentObserver iContentObserver, boolean z, Object obj, int i, int i2, int i3, Uri uri) {
                boolean contains;
                String str;
                this.observersLock = obj;
                this.observer = iContentObserver;
                this.uid = i;
                this.pid = i2;
                this.userHandle = i3;
                this.notifyForDescendants = z;
                int linkToDeath = ContentService.sObserverDeathDispatcher.linkToDeath(iContentObserver, this);
                if (linkToDeath == -1) {
                    binderDied();
                } else if (linkToDeath == 1000) {
                    synchronized (ContentService.sObserverLeakDetectedUid) {
                        contains = ContentService.sObserverLeakDetectedUid.contains(Integer.valueOf(i));
                        if (!contains) {
                            ContentService.sObserverLeakDetectedUid.add(Integer.valueOf(i));
                        }
                    }
                    if (contains) {
                        return;
                    }
                    try {
                        str = (String) ArrayUtils.firstOrNull(AppGlobals.getPackageManager().getPackagesForUid(i));
                    } catch (RemoteException unused) {
                        str = null;
                    }
                    Slog.wtf("ContentService", "Observer registered too many times. Leak? cpid=" + this.pid + " cuid=" + this.uid + " cpkg=" + str + " url=" + uri);
                }
            }

            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                synchronized (this.observersLock) {
                    ObserverNode.this.removeObserverLocked(this.observer);
                }
            }

            public void dumpLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, String str, String str2, SparseIntArray sparseIntArray) {
                int i = this.pid;
                sparseIntArray.put(i, sparseIntArray.get(i) + 1);
                printWriter.print(str2);
                printWriter.print(str);
                printWriter.print(": pid=");
                printWriter.print(this.pid);
                printWriter.print(" uid=");
                printWriter.print(this.uid);
                printWriter.print(" user=");
                printWriter.print(this.userHandle);
                printWriter.print(" target=");
                IContentObserver iContentObserver = this.observer;
                printWriter.println(Integer.toHexString(System.identityHashCode(iContentObserver != null ? iContentObserver.asBinder() : null)));
            }
        }

        public ObserverNode(String str) {
            this.mName = str;
        }

        public void dumpLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, String str, String str2, int[] iArr, SparseIntArray sparseIntArray) {
            String str3;
            if (this.mObservers.size() > 0) {
                str3 = "".equals(str) ? this.mName : str + "/" + this.mName;
                for (int i = 0; i < this.mObservers.size(); i++) {
                    iArr[1] = iArr[1] + 1;
                    this.mObservers.get(i).dumpLocked(fileDescriptor, printWriter, strArr, str3, str2, sparseIntArray);
                }
            } else {
                str3 = null;
            }
            if (this.mChildren.size() > 0) {
                if (str3 == null) {
                    str3 = "".equals(str) ? this.mName : str + "/" + this.mName;
                }
                String str4 = str3;
                for (int i2 = 0; i2 < this.mChildren.size(); i2++) {
                    iArr[0] = iArr[0] + 1;
                    this.mChildren.get(i2).dumpLocked(fileDescriptor, printWriter, strArr, str4, str2, iArr, sparseIntArray);
                }
            }
        }

        public static String getUriSegment(Uri uri, int i) {
            if (uri != null) {
                if (i == 0) {
                    return uri.getAuthority();
                }
                return uri.getPathSegments().get(i - 1);
            }
            return null;
        }

        public static int countUriSegments(Uri uri) {
            if (uri == null) {
                return 0;
            }
            return uri.getPathSegments().size() + 1;
        }

        public void addObserverLocked(Uri uri, IContentObserver iContentObserver, boolean z, Object obj, int i, int i2, int i3) {
            addObserverLocked(uri, 0, iContentObserver, z, obj, i, i2, i3);
        }

        public final void addObserverLocked(Uri uri, int i, IContentObserver iContentObserver, boolean z, Object obj, int i2, int i3, int i4) {
            if (i == countUriSegments(uri)) {
                this.mObservers.add(new ObserverEntry(iContentObserver, z, obj, i2, i3, i4, uri));
                return;
            }
            String uriSegment = getUriSegment(uri, i);
            if (uriSegment == null) {
                throw new IllegalArgumentException("Invalid Uri (" + uri + ") used for observer");
            }
            int size = this.mChildren.size();
            for (int i5 = 0; i5 < size; i5++) {
                ObserverNode observerNode = this.mChildren.get(i5);
                if (observerNode.mName.equals(uriSegment)) {
                    observerNode.addObserverLocked(uri, i + 1, iContentObserver, z, obj, i2, i3, i4);
                    return;
                }
            }
            ObserverNode observerNode2 = new ObserverNode(uriSegment);
            this.mChildren.add(observerNode2);
            observerNode2.addObserverLocked(uri, i + 1, iContentObserver, z, obj, i2, i3, i4);
        }

        public boolean removeObserverLocked(IContentObserver iContentObserver) {
            int size = this.mChildren.size();
            int i = 0;
            while (i < size) {
                if (this.mChildren.get(i).removeObserverLocked(iContentObserver)) {
                    this.mChildren.remove(i);
                    i--;
                    size--;
                }
                i++;
            }
            IBinder asBinder = iContentObserver.asBinder();
            int size2 = this.mObservers.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size2) {
                    break;
                }
                ObserverEntry observerEntry = this.mObservers.get(i2);
                if (observerEntry.observer.asBinder() == asBinder) {
                    this.mObservers.remove(i2);
                    ContentService.sObserverDeathDispatcher.unlinkToDeath(iContentObserver, observerEntry);
                    break;
                }
                i2++;
            }
            return this.mChildren.size() == 0 && this.mObservers.size() == 0;
        }

        public final void collectMyObserversLocked(Uri uri, boolean z, IContentObserver iContentObserver, boolean z2, int i, int i2, ObserverCollector observerCollector) {
            int size = this.mObservers.size();
            IBinder asBinder = iContentObserver == null ? null : iContentObserver.asBinder();
            for (int i3 = 0; i3 < size; i3++) {
                ObserverEntry observerEntry = this.mObservers.get(i3);
                boolean z3 = observerEntry.observer.asBinder() == asBinder;
                if ((!z3 || z2) && (i2 == -1 || observerEntry.userHandle == -1 || i2 == observerEntry.userHandle)) {
                    if (z) {
                        if ((i & 2) != 0 && observerEntry.notifyForDescendants) {
                        }
                        observerCollector.collect(observerEntry.observer, observerEntry.uid, z3, uri, i, i2);
                    } else {
                        if (!observerEntry.notifyForDescendants) {
                        }
                        observerCollector.collect(observerEntry.observer, observerEntry.uid, z3, uri, i, i2);
                    }
                }
            }
        }

        @VisibleForTesting
        public void collectObserversLocked(Uri uri, int i, IContentObserver iContentObserver, boolean z, int i2, int i3, ObserverCollector observerCollector) {
            collectObserversLocked(uri, countUriSegments(uri), i, iContentObserver, z, i2, i3, observerCollector);
        }

        /* JADX WARN: Removed duplicated region for block: B:11:0x0043  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void collectObserversLocked(Uri uri, int i, int i2, IContentObserver iContentObserver, boolean z, int i3, int i4, ObserverCollector observerCollector) {
            String uriSegment;
            int size;
            int i5;
            int i6 = i2;
            if (i6 >= i) {
                collectMyObserversLocked(uri, true, iContentObserver, z, i3, i4, observerCollector);
            } else if (i6 < i) {
                uriSegment = getUriSegment(uri, i6);
                collectMyObserversLocked(uri, false, iContentObserver, z, i3, i4, observerCollector);
                String str = uriSegment;
                size = this.mChildren.size();
                i5 = 0;
                while (i5 < size) {
                    ObserverNode observerNode = this.mChildren.get(i5);
                    if (str == null || observerNode.mName.equals(str)) {
                        observerNode.collectObserversLocked(uri, i, i6 + 1, iContentObserver, z, i3, i4, observerCollector);
                        if (str != null) {
                            return;
                        }
                    }
                    i5++;
                    i6 = i2;
                }
            }
            uriSegment = null;
            String str2 = uriSegment;
            size = this.mChildren.size();
            i5 = 0;
            while (i5 < size) {
            }
        }
    }

    public final void enforceShell(String str) {
        int callingUid = Binder.getCallingUid();
        if (callingUid == 2000 || callingUid == 0) {
            return;
        }
        throw new SecurityException("Non-shell user attempted to call " + str);
    }

    public void resetTodayStats() {
        enforceShell("resetTodayStats");
        if (this.mSyncManager != null) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mSyncManager.resetTodayStats();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public void onDbCorruption(String str, String str2, String str3) {
        Slog.e(str, str2);
        Slog.e(str, "at " + str3);
        Slog.wtf(str, str2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new ContentShellCommand(this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }
}
