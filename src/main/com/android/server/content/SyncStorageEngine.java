package com.android.server.content;

import android.accounts.Account;
import android.accounts.AccountAndUser;
import android.accounts.AccountManager;
import android.app.backup.BackupManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ISyncStatusObserver;
import android.content.PeriodicSync;
import android.content.SyncInfo;
import android.content.SyncRequest;
import android.content.SyncStatusInfo;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.EventLog;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IntPair;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.LocalServices;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.content.SyncManager;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class SyncStorageEngine {
    @VisibleForTesting
    static final long MILLIS_IN_4WEEKS = 2419200000L;
    public static final String[] SOURCES = {"OTHER", "LOCAL", "POLL", "USER", "PERIODIC", "FEED"};
    public static PeriodicSyncAddedListener mPeriodicSyncAddedListener;
    public static HashMap<String, String> sAuthorityRenames;
    public static volatile SyncStorageEngine sSyncStorageEngine;
    public final AtomicFile mAccountInfoFile;
    public final HashMap<AccountAndUser, AccountInfo> mAccounts;
    @VisibleForTesting
    final SparseArray<AuthorityInfo> mAuthorities;
    public OnAuthorityRemovedListener mAuthorityRemovedListener;
    public final Calendar mCal;
    public final RemoteCallbackList<ISyncStatusObserver> mChangeListeners;
    public final Context mContext;
    public final SparseArray<ArrayList<SyncInfo>> mCurrentSyncs;
    @VisibleForTesting
    final DayStats[] mDayStats;
    public boolean mDefaultMasterSyncAutomatically;
    public boolean mGrantSyncAdaptersAccountAccess;
    public final MyHandler mHandler;
    public volatile boolean mIsClockValid;
    public volatile boolean mIsJobAttributionFixed;
    public volatile boolean mIsJobNamespaceMigrated;
    public final SyncLogger mLogger;
    public SparseArray<Boolean> mMasterSyncAutomatically;
    public int mNextAuthorityId;
    public int mNextHistoryId;
    public final PackageManagerInternal mPackageManagerInternal;
    public final ArrayMap<ComponentName, SparseArray<AuthorityInfo>> mServices;
    public final AtomicFile mStatisticsFile;
    public final AtomicFile mStatusFile;
    public File mSyncDir;
    public final ArrayList<SyncHistoryItem> mSyncHistory;
    public int mSyncRandomOffset;
    public OnSyncRequestListener mSyncRequestListener;
    @VisibleForTesting
    final SparseArray<SyncStatusInfo> mSyncStatus;
    public int mYear;
    public int mYearInDays;

    /* loaded from: classes.dex */
    public interface OnAuthorityRemovedListener {
        void onAuthorityRemoved(EndPoint endPoint);
    }

    /* loaded from: classes.dex */
    public interface OnSyncRequestListener {
        void onSyncRequest(EndPoint endPoint, int i, Bundle bundle, int i2, int i3, int i4);
    }

    /* loaded from: classes.dex */
    public interface PeriodicSyncAddedListener {
        void onPeriodicSyncAdded(EndPoint endPoint, Bundle bundle, long j, long j2);
    }

    /* loaded from: classes.dex */
    public static class SyncHistoryItem {
        public int authorityId;
        public long downstreamActivity;
        public long elapsedTime;
        public int event;
        public long eventTime;
        public Bundle extras;
        public int historyId;
        public boolean initialization;
        public String mesg;
        public int reason;
        public int source;
        public int syncExemptionFlag;
        public long upstreamActivity;
    }

    public static long calculateDefaultFlexTime(long j) {
        if (j < 5) {
            return 0L;
        }
        if (j < 86400) {
            return (long) (j * 0.04d);
        }
        return 3456L;
    }

    static {
        HashMap<String, String> hashMap = new HashMap<>();
        sAuthorityRenames = hashMap;
        hashMap.put("contacts", "com.android.contacts");
        sAuthorityRenames.put("calendar", "com.android.calendar");
        sSyncStorageEngine = null;
    }

    /* loaded from: classes.dex */
    public static class AccountInfo {
        public final AccountAndUser accountAndUser;
        public final HashMap<String, AuthorityInfo> authorities = new HashMap<>();

        public AccountInfo(AccountAndUser accountAndUser) {
            this.accountAndUser = accountAndUser;
        }
    }

    /* loaded from: classes.dex */
    public static class EndPoint {
        public static final EndPoint USER_ALL_PROVIDER_ALL_ACCOUNTS_ALL = new EndPoint(null, null, -1);
        public final Account account;
        public final String provider;
        public final int userId;

        public EndPoint(Account account, String str, int i) {
            this.account = account;
            this.provider = str;
            this.userId = i;
        }

        public boolean matchesSpec(EndPoint endPoint) {
            int i = this.userId;
            int i2 = endPoint.userId;
            if (i == i2 || i == -1 || i2 == -1) {
                Account account = endPoint.account;
                boolean equals = account == null ? true : this.account.equals(account);
                String str = endPoint.provider;
                return equals && (str == null ? true : this.provider.equals(str));
            }
            return false;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            Account account = this.account;
            sb.append(account == null ? "ALL ACCS" : account.name);
            sb.append("/");
            String str = this.provider;
            if (str == null) {
                str = "ALL PDRS";
            }
            sb.append(str);
            sb.append(":u" + this.userId);
            return sb.toString();
        }

        public String toSafeString() {
            StringBuilder sb = new StringBuilder();
            Account account = this.account;
            sb.append(account == null ? "ALL ACCS" : SyncLogger.logSafe(account));
            sb.append("/");
            String str = this.provider;
            if (str == null) {
                str = "ALL PDRS";
            }
            sb.append(str);
            sb.append(":u" + this.userId);
            return sb.toString();
        }
    }

    /* loaded from: classes.dex */
    public static class AuthorityInfo {
        public long backoffDelay;
        public long backoffTime;
        public long delayUntil;
        public boolean enabled;
        public final int ident;
        public final ArrayList<PeriodicSync> periodicSyncs;
        public int syncable;
        public final EndPoint target;

        public AuthorityInfo(AuthorityInfo authorityInfo) {
            this.target = authorityInfo.target;
            this.ident = authorityInfo.ident;
            this.enabled = authorityInfo.enabled;
            this.syncable = authorityInfo.syncable;
            this.backoffTime = authorityInfo.backoffTime;
            this.backoffDelay = authorityInfo.backoffDelay;
            this.delayUntil = authorityInfo.delayUntil;
            this.periodicSyncs = new ArrayList<>();
            Iterator<PeriodicSync> it = authorityInfo.periodicSyncs.iterator();
            while (it.hasNext()) {
                this.periodicSyncs.add(new PeriodicSync(it.next()));
            }
        }

        public AuthorityInfo(EndPoint endPoint, int i) {
            this.target = endPoint;
            this.ident = i;
            this.enabled = false;
            this.periodicSyncs = new ArrayList<>();
            defaultInitialisation();
        }

        public final void defaultInitialisation() {
            this.syncable = -1;
            this.backoffTime = -1L;
            this.backoffDelay = -1L;
            if (SyncStorageEngine.mPeriodicSyncAddedListener != null) {
                SyncStorageEngine.mPeriodicSyncAddedListener.onPeriodicSyncAdded(this.target, new Bundle(), 86400L, SyncStorageEngine.calculateDefaultFlexTime(86400L));
            }
        }

        public String toString() {
            return this.target + ", enabled=" + this.enabled + ", syncable=" + this.syncable + ", backoff=" + this.backoffTime + ", delay=" + this.delayUntil;
        }
    }

    /* loaded from: classes.dex */
    public static class DayStats {
        public final int day;
        public int failureCount;
        public long failureTime;
        public int successCount;
        public long successTime;

        public DayStats(int i) {
            this.day = i;
        }
    }

    /* loaded from: classes.dex */
    public static class AccountAuthorityValidator {
        public final AccountManager mAccountManager;
        public final PackageManager mPackageManager;
        public final SparseArray<Account[]> mAccountsCache = new SparseArray<>();
        public final SparseArray<ArrayMap<String, Boolean>> mProvidersPerUserCache = new SparseArray<>();

        public AccountAuthorityValidator(Context context) {
            this.mAccountManager = (AccountManager) context.getSystemService(AccountManager.class);
            this.mPackageManager = context.getPackageManager();
        }

        public boolean isAccountValid(Account account, int i) {
            Account[] accountArr = this.mAccountsCache.get(i);
            if (accountArr == null) {
                accountArr = this.mAccountManager.getAccountsAsUser(i);
                this.mAccountsCache.put(i, accountArr);
            }
            return ArrayUtils.contains(accountArr, account);
        }

        public boolean isAuthorityValid(String str, int i) {
            ArrayMap<String, Boolean> arrayMap = this.mProvidersPerUserCache.get(i);
            if (arrayMap == null) {
                arrayMap = new ArrayMap<>();
                this.mProvidersPerUserCache.put(i, arrayMap);
            }
            if (!arrayMap.containsKey(str)) {
                arrayMap.put(str, Boolean.valueOf(this.mPackageManager.resolveContentProviderAsUser(str, 786432, i) != null));
            }
            return arrayMap.get(str).booleanValue();
        }
    }

    public SyncStorageEngine(Context context, File file, Looper looper) {
        SparseArray<AuthorityInfo> sparseArray = new SparseArray<>();
        this.mAuthorities = sparseArray;
        this.mAccounts = new HashMap<>();
        this.mCurrentSyncs = new SparseArray<>();
        this.mSyncStatus = new SparseArray<>();
        this.mSyncHistory = new ArrayList<>();
        this.mChangeListeners = new RemoteCallbackList<>();
        this.mServices = new ArrayMap<>();
        this.mNextAuthorityId = 0;
        this.mDayStats = new DayStats[28];
        this.mNextHistoryId = 0;
        this.mMasterSyncAutomatically = new SparseArray<>();
        this.mHandler = new MyHandler(looper);
        this.mContext = context;
        sSyncStorageEngine = this;
        SyncLogger syncLogger = SyncLogger.getInstance();
        this.mLogger = syncLogger;
        this.mCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
        this.mDefaultMasterSyncAutomatically = context.getResources().getBoolean(17891843);
        this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        File file2 = new File(new File(file, "system"), "sync");
        this.mSyncDir = file2;
        file2.mkdirs();
        maybeDeleteLegacyPendingInfoLocked(this.mSyncDir);
        this.mAccountInfoFile = new AtomicFile(new File(this.mSyncDir, "accounts.xml"), "sync-accounts");
        this.mStatusFile = new AtomicFile(new File(this.mSyncDir, "status"), "sync-status");
        this.mStatisticsFile = new AtomicFile(new File(this.mSyncDir, "stats"), "sync-stats");
        readAccountInfoLocked();
        readStatusLocked();
        readStatisticsLocked();
        if (syncLogger.enabled()) {
            int size = sparseArray.size();
            syncLogger.log("Loaded ", Integer.valueOf(size), " items");
            for (int i = 0; i < size; i++) {
                this.mLogger.log(this.mAuthorities.valueAt(i));
            }
        }
    }

    public static void init(Context context, Looper looper) {
        if (sSyncStorageEngine != null) {
            return;
        }
        sSyncStorageEngine = new SyncStorageEngine(context, Environment.getDataDirectory(), looper);
    }

    public static SyncStorageEngine getSingleton() {
        if (sSyncStorageEngine == null) {
            throw new IllegalStateException("not initialized");
        }
        return sSyncStorageEngine;
    }

    public void setOnSyncRequestListener(OnSyncRequestListener onSyncRequestListener) {
        if (this.mSyncRequestListener == null) {
            this.mSyncRequestListener = onSyncRequestListener;
        }
    }

    public void setOnAuthorityRemovedListener(OnAuthorityRemovedListener onAuthorityRemovedListener) {
        if (this.mAuthorityRemovedListener == null) {
            this.mAuthorityRemovedListener = onAuthorityRemovedListener;
        }
    }

    public void setPeriodicSyncAddedListener(PeriodicSyncAddedListener periodicSyncAddedListener) {
        if (mPeriodicSyncAddedListener == null) {
            mPeriodicSyncAddedListener = periodicSyncAddedListener;
        }
    }

    /* loaded from: classes.dex */
    public class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                synchronized (SyncStorageEngine.this.mAuthorities) {
                    SyncStorageEngine.this.writeStatusLocked();
                }
            } else if (i == 2) {
                synchronized (SyncStorageEngine.this.mAuthorities) {
                    SyncStorageEngine.this.writeStatisticsLocked();
                }
            }
        }
    }

    public void addStatusChangeListener(int i, int i2, ISyncStatusObserver iSyncStatusObserver) {
        synchronized (this.mAuthorities) {
            this.mChangeListeners.register(iSyncStatusObserver, Long.valueOf(IntPair.of(i2, i)));
        }
    }

    public void removeStatusChangeListener(ISyncStatusObserver iSyncStatusObserver) {
        synchronized (this.mAuthorities) {
            this.mChangeListeners.unregister(iSyncStatusObserver);
        }
    }

    public void reportChange(int i, EndPoint endPoint) {
        String str;
        Account account = endPoint.account;
        reportChange(i, (account == null || (str = endPoint.provider) == null) ? null : ContentResolver.getSyncAdapterPackageAsUser(account.type, str, endPoint.userId), endPoint.userId);
    }

    public void reportChange(int i, String str, int i2) {
        ArrayList arrayList;
        synchronized (this.mAuthorities) {
            int beginBroadcast = this.mChangeListeners.beginBroadcast();
            arrayList = null;
            while (beginBroadcast > 0) {
                beginBroadcast--;
                long longValue = ((Long) this.mChangeListeners.getBroadcastCookie(beginBroadcast)).longValue();
                int first = IntPair.first(longValue);
                int userId = UserHandle.getUserId(first);
                if ((IntPair.second(longValue) & i) != 0 && i2 == userId && (str == null || !this.mPackageManagerInternal.filterAppAccess(str, first, i2))) {
                    if (arrayList == null) {
                        arrayList = new ArrayList(beginBroadcast);
                    }
                    arrayList.add(this.mChangeListeners.getBroadcastItem(beginBroadcast));
                }
            }
            this.mChangeListeners.finishBroadcast();
        }
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "reportChange " + i + " to: " + arrayList);
        }
        if (arrayList != null) {
            int size = arrayList.size();
            while (size > 0) {
                size--;
                try {
                    ((ISyncStatusObserver) arrayList.get(size)).onStatusChanged(i);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    public boolean getSyncAutomatically(Account account, int i, String str) {
        synchronized (this.mAuthorities) {
            boolean z = true;
            if (account != null) {
                AuthorityInfo authorityLocked = getAuthorityLocked(new EndPoint(account, str, i), "getSyncAutomatically");
                if (authorityLocked == null || !authorityLocked.enabled) {
                    z = false;
                }
                return z;
            }
            int size = this.mAuthorities.size();
            while (size > 0) {
                size--;
                AuthorityInfo valueAt = this.mAuthorities.valueAt(size);
                if (valueAt.target.matchesSpec(new EndPoint(account, str, i)) && valueAt.enabled) {
                    return true;
                }
            }
            return false;
        }
    }

    public void setSyncAutomatically(Account account, int i, String str, boolean z, int i2, int i3, int i4) {
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.d("SyncManager", "setSyncAutomatically:  provider " + str + ", user " + i + " -> " + z);
        }
        this.mLogger.log("Set sync auto account=", account, " user=", Integer.valueOf(i), " authority=", str, " value=", Boolean.toString(z), " cuid=", Integer.valueOf(i3), " cpid=", Integer.valueOf(i4));
        synchronized (this.mAuthorities) {
            AuthorityInfo orCreateAuthorityLocked = getOrCreateAuthorityLocked(new EndPoint(account, str, i), -1, false);
            if (orCreateAuthorityLocked.enabled == z) {
                if (Log.isLoggable("SyncManager", 2)) {
                    Slog.d("SyncManager", "setSyncAutomatically: already set to " + z + ", doing nothing");
                }
                return;
            }
            if (z && orCreateAuthorityLocked.syncable == 2) {
                orCreateAuthorityLocked.syncable = -1;
            }
            orCreateAuthorityLocked.enabled = z;
            writeAccountInfoLocked();
            if (z) {
                requestSync(account, i, -6, str, new Bundle(), i2, i3, i4);
            }
            reportChange(1, orCreateAuthorityLocked.target);
            queueBackup();
        }
    }

    public int getIsSyncable(Account account, int i, String str) {
        synchronized (this.mAuthorities) {
            if (account != null) {
                AuthorityInfo authorityLocked = getAuthorityLocked(new EndPoint(account, str, i), "get authority syncable");
                if (authorityLocked == null) {
                    return -1;
                }
                return authorityLocked.syncable;
            }
            int size = this.mAuthorities.size();
            while (size > 0) {
                size--;
                AuthorityInfo valueAt = this.mAuthorities.valueAt(size);
                EndPoint endPoint = valueAt.target;
                if (endPoint != null && endPoint.provider.equals(str)) {
                    return valueAt.syncable;
                }
            }
            return -1;
        }
    }

    public void setIsSyncable(Account account, int i, String str, int i2, int i3, int i4) {
        setSyncableStateForEndPoint(new EndPoint(account, str, i), i2, i3, i4);
    }

    public final void setSyncableStateForEndPoint(EndPoint endPoint, int i, int i2, int i3) {
        this.mLogger.log("Set syncable ", endPoint, " value=", Integer.toString(i), " cuid=", Integer.valueOf(i2), " cpid=", Integer.valueOf(i3));
        synchronized (this.mAuthorities) {
            AuthorityInfo orCreateAuthorityLocked = getOrCreateAuthorityLocked(endPoint, -1, false);
            if (i < -1) {
                i = -1;
            }
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.d("SyncManager", "setIsSyncable: " + orCreateAuthorityLocked.toString() + " -> " + i);
            }
            if (orCreateAuthorityLocked.syncable == i) {
                if (Log.isLoggable("SyncManager", 2)) {
                    Slog.d("SyncManager", "setIsSyncable: already set to " + i + ", doing nothing");
                }
                return;
            }
            orCreateAuthorityLocked.syncable = i;
            writeAccountInfoLocked();
            if (i == 1) {
                requestSync(orCreateAuthorityLocked, -5, new Bundle(), 0, i2, i3);
            }
            reportChange(1, endPoint);
        }
    }

    public void setJobNamespaceMigrated(boolean z) {
        if (this.mIsJobNamespaceMigrated == z) {
            return;
        }
        this.mIsJobNamespaceMigrated = z;
        this.mHandler.sendEmptyMessageDelayed(1, 600000L);
    }

    public boolean isJobNamespaceMigrated() {
        return this.mIsJobNamespaceMigrated;
    }

    public void setJobAttributionFixed(boolean z) {
        if (this.mIsJobAttributionFixed == z) {
            return;
        }
        this.mIsJobAttributionFixed = z;
        this.mHandler.sendEmptyMessageDelayed(1, 600000L);
    }

    public boolean isJobAttributionFixed() {
        return this.mIsJobAttributionFixed;
    }

    public Pair<Long, Long> getBackoff(EndPoint endPoint) {
        synchronized (this.mAuthorities) {
            AuthorityInfo authorityLocked = getAuthorityLocked(endPoint, "getBackoff");
            if (authorityLocked != null) {
                return Pair.create(Long.valueOf(authorityLocked.backoffTime), Long.valueOf(authorityLocked.backoffDelay));
            }
            return null;
        }
    }

    public void setBackoff(EndPoint endPoint, long j, long j2) {
        boolean backoffLocked;
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "setBackoff: " + endPoint + " -> nextSyncTime " + j + ", nextDelay " + j2);
        }
        synchronized (this.mAuthorities) {
            Account account = endPoint.account;
            if (account != null && endPoint.provider != null) {
                AuthorityInfo orCreateAuthorityLocked = getOrCreateAuthorityLocked(endPoint, -1, true);
                if (orCreateAuthorityLocked.backoffTime == j && orCreateAuthorityLocked.backoffDelay == j2) {
                    backoffLocked = false;
                } else {
                    orCreateAuthorityLocked.backoffTime = j;
                    orCreateAuthorityLocked.backoffDelay = j2;
                    backoffLocked = true;
                }
            }
            backoffLocked = setBackoffLocked(account, endPoint.userId, endPoint.provider, j, j2);
        }
        if (backoffLocked) {
            reportChange(1, endPoint);
        }
    }

    public final boolean setBackoffLocked(Account account, int i, String str, long j, long j2) {
        boolean z = false;
        for (AccountInfo accountInfo : this.mAccounts.values()) {
            if (account == null || account.equals(accountInfo.accountAndUser.account) || i == accountInfo.accountAndUser.userId) {
                for (AuthorityInfo authorityInfo : accountInfo.authorities.values()) {
                    if (str == null || str.equals(authorityInfo.target.provider)) {
                        if (authorityInfo.backoffTime != j || authorityInfo.backoffDelay != j2) {
                            authorityInfo.backoffTime = j;
                            authorityInfo.backoffDelay = j2;
                            z = true;
                        }
                    }
                }
            }
        }
        return z;
    }

    public void clearAllBackoffsLocked() {
        ArraySet arraySet = new ArraySet();
        synchronized (this.mAuthorities) {
            for (AccountInfo accountInfo : this.mAccounts.values()) {
                for (AuthorityInfo authorityInfo : accountInfo.authorities.values()) {
                    if (authorityInfo.backoffTime != -1 || authorityInfo.backoffDelay != -1) {
                        if (Log.isLoggable("SyncManager", 2)) {
                            Slog.v("SyncManager", "clearAllBackoffsLocked: authority:" + authorityInfo.target + " account:" + accountInfo.accountAndUser.account.name + " user:" + accountInfo.accountAndUser.userId + " backoffTime was: " + authorityInfo.backoffTime + " backoffDelay was: " + authorityInfo.backoffDelay);
                        }
                        authorityInfo.backoffTime = -1L;
                        authorityInfo.backoffDelay = -1L;
                        arraySet.add(Integer.valueOf(accountInfo.accountAndUser.userId));
                    }
                }
            }
        }
        for (int size = arraySet.size() - 1; size > 0; size--) {
            reportChange(1, null, ((Integer) arraySet.valueAt(size)).intValue());
        }
    }

    public long getDelayUntilTime(EndPoint endPoint) {
        synchronized (this.mAuthorities) {
            AuthorityInfo authorityLocked = getAuthorityLocked(endPoint, "getDelayUntil");
            if (authorityLocked == null) {
                return 0L;
            }
            return authorityLocked.delayUntil;
        }
    }

    public void setDelayUntilTime(EndPoint endPoint, long j) {
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "setDelayUntil: " + endPoint + " -> delayUntil " + j);
        }
        synchronized (this.mAuthorities) {
            AuthorityInfo orCreateAuthorityLocked = getOrCreateAuthorityLocked(endPoint, -1, true);
            if (orCreateAuthorityLocked.delayUntil == j) {
                return;
            }
            orCreateAuthorityLocked.delayUntil = j;
            reportChange(1, endPoint);
        }
    }

    public void setMasterSyncAutomatically(boolean z, int i, int i2, int i3, int i4) {
        this.mLogger.log("Set master enabled=", Boolean.valueOf(z), " user=", Integer.valueOf(i), " cuid=", Integer.valueOf(i3), " cpid=", Integer.valueOf(i4));
        synchronized (this.mAuthorities) {
            Boolean bool = this.mMasterSyncAutomatically.get(i);
            if (bool == null || !bool.equals(Boolean.valueOf(z))) {
                this.mMasterSyncAutomatically.put(i, Boolean.valueOf(z));
                writeAccountInfoLocked();
                if (z) {
                    requestSync(null, i, -7, null, new Bundle(), i2, i3, i4);
                }
                reportChange(1, null, i);
                this.mContext.sendBroadcast(ContentResolver.ACTION_SYNC_CONN_STATUS_CHANGED);
                queueBackup();
            }
        }
    }

    public boolean getMasterSyncAutomatically(int i) {
        boolean booleanValue;
        synchronized (this.mAuthorities) {
            Boolean bool = this.mMasterSyncAutomatically.get(i);
            booleanValue = bool == null ? this.mDefaultMasterSyncAutomatically : bool.booleanValue();
        }
        return booleanValue;
    }

    public int getAuthorityCount() {
        int size;
        synchronized (this.mAuthorities) {
            size = this.mAuthorities.size();
        }
        return size;
    }

    public AuthorityInfo getAuthority(int i) {
        AuthorityInfo authorityInfo;
        synchronized (this.mAuthorities) {
            authorityInfo = this.mAuthorities.get(i);
        }
        return authorityInfo;
    }

    public boolean isSyncActive(EndPoint endPoint) {
        synchronized (this.mAuthorities) {
            for (SyncInfo syncInfo : getCurrentSyncs(endPoint.userId)) {
                AuthorityInfo authority = getAuthority(syncInfo.authorityId);
                if (authority != null && authority.target.matchesSpec(endPoint)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void markPending(EndPoint endPoint, boolean z) {
        synchronized (this.mAuthorities) {
            AuthorityInfo orCreateAuthorityLocked = getOrCreateAuthorityLocked(endPoint, -1, true);
            if (orCreateAuthorityLocked == null) {
                return;
            }
            getOrCreateSyncStatusLocked(orCreateAuthorityLocked.ident).pending = z;
            reportChange(2, endPoint);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x0083, code lost:
        if (r9 > 0) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0085, code lost:
        if (r9 <= 0) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0087, code lost:
        r9 = r9 - 1;
        r10 = r1.keyAt(r9);
        r2 = (com.android.server.content.SyncStorageEngine.AuthorityInfo) r1.valueAt(r9);
        r3 = r8.mAuthorityRemovedListener;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0095, code lost:
        if (r3 == null) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0097, code lost:
        r3.onAuthorityRemoved(r2.target);
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x009c, code lost:
        r8.mAuthorities.remove(r10);
        r2 = r8.mSyncStatus.size();
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00a7, code lost:
        if (r2 <= 0) goto L49;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00a9, code lost:
        r2 = r2 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00b1, code lost:
        if (r8.mSyncStatus.keyAt(r2) != r10) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00b3, code lost:
        r3 = r8.mSyncStatus;
        r3.remove(r3.keyAt(r2));
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00bd, code lost:
        r2 = r8.mSyncHistory.size();
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00c3, code lost:
        if (r2 <= 0) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00c5, code lost:
        r2 = r2 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00d1, code lost:
        if (r8.mSyncHistory.get(r2).authorityId != r10) goto L55;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00d3, code lost:
        r8.mSyncHistory.remove(r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00d9, code lost:
        writeAccountInfoLocked();
        writeStatusLocked();
        writeStatisticsLocked();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void removeStaleAccounts(Account[] accountArr, int i) {
        synchronized (this.mAuthorities) {
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "Updating for new accounts...");
            }
            SparseArray sparseArray = new SparseArray();
            Iterator<AccountInfo> it = this.mAccounts.values().iterator();
            while (it.hasNext()) {
                AccountInfo next = it.next();
                AccountAndUser accountAndUser = next.accountAndUser;
                if (accountAndUser.userId == i && (accountArr == null || !ArrayUtils.contains(accountArr, accountAndUser.account))) {
                    if (Log.isLoggable("SyncManager", 2)) {
                        Slog.v("SyncManager", "Account removed: " + next.accountAndUser);
                    }
                    for (AuthorityInfo authorityInfo : next.authorities.values()) {
                        sparseArray.put(authorityInfo.ident, authorityInfo);
                    }
                    it.remove();
                }
            }
            int size = sparseArray.size();
        }
    }

    public SyncInfo addActiveSync(SyncManager.ActiveSyncContext activeSyncContext) {
        SyncInfo syncInfo;
        synchronized (this.mAuthorities) {
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "setActiveSync: account= auth=" + activeSyncContext.mSyncOperation.target + " src=" + activeSyncContext.mSyncOperation.syncSource + " extras=" + activeSyncContext.mSyncOperation.getExtrasAsString());
            }
            AuthorityInfo orCreateAuthorityLocked = getOrCreateAuthorityLocked(activeSyncContext.mSyncOperation.target, -1, true);
            int i = orCreateAuthorityLocked.ident;
            EndPoint endPoint = orCreateAuthorityLocked.target;
            syncInfo = new SyncInfo(i, endPoint.account, endPoint.provider, activeSyncContext.mStartTime);
            getCurrentSyncs(orCreateAuthorityLocked.target.userId).add(syncInfo);
        }
        reportActiveChange(activeSyncContext.mSyncOperation.target);
        return syncInfo;
    }

    public void removeActiveSync(SyncInfo syncInfo, int i) {
        synchronized (this.mAuthorities) {
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "removeActiveSync: account=" + syncInfo.account + " user=" + i + " auth=" + syncInfo.authority);
            }
            getCurrentSyncs(i).remove(syncInfo);
        }
        reportActiveChange(new EndPoint(syncInfo.account, syncInfo.authority, i));
    }

    public void reportActiveChange(EndPoint endPoint) {
        reportChange(4, endPoint);
    }

    public long insertStartSyncEvent(SyncOperation syncOperation, long j) {
        ArrayList<SyncHistoryItem> arrayList;
        synchronized (this.mAuthorities) {
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "insertStartSyncEvent: " + syncOperation);
            }
            AuthorityInfo authorityLocked = getAuthorityLocked(syncOperation.target, "insertStartSyncEvent");
            if (authorityLocked == null) {
                return -1L;
            }
            SyncHistoryItem syncHistoryItem = new SyncHistoryItem();
            syncHistoryItem.initialization = syncOperation.isInitialization();
            syncHistoryItem.authorityId = authorityLocked.ident;
            int i = this.mNextHistoryId;
            int i2 = i + 1;
            this.mNextHistoryId = i2;
            syncHistoryItem.historyId = i;
            if (i2 < 0) {
                this.mNextHistoryId = 0;
            }
            syncHistoryItem.eventTime = j;
            syncHistoryItem.source = syncOperation.syncSource;
            syncHistoryItem.reason = syncOperation.reason;
            syncHistoryItem.extras = syncOperation.getClonedExtras();
            syncHistoryItem.event = 0;
            syncHistoryItem.syncExemptionFlag = syncOperation.syncExemptionFlag;
            this.mSyncHistory.add(0, syncHistoryItem);
            while (this.mSyncHistory.size() > 100) {
                this.mSyncHistory.remove(arrayList.size() - 1);
            }
            long j2 = syncHistoryItem.historyId;
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "returning historyId " + j2);
            }
            reportChange(8, syncOperation.owningPackage, syncOperation.target.userId);
            return j2;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:43:0x0122 A[Catch: all -> 0x022a, TryCatch #0 {, blocks: (B:4:0x000b, B:6:0x0014, B:7:0x002b, B:9:0x0033, B:14:0x0048, B:15:0x005f, B:17:0x0061, B:34:0x00e9, B:36:0x00f3, B:41:0x0110, B:43:0x0122, B:45:0x0128, B:48:0x012f, B:56:0x017b, B:58:0x01bd, B:62:0x01c9, B:63:0x01cd, B:64:0x01d4, B:65:0x01d9, B:67:0x01ec, B:72:0x0206, B:76:0x021f, B:73:0x020a, B:75:0x0213, B:68:0x01f0, B:70:0x01f8, B:49:0x013f, B:51:0x0147, B:54:0x014e, B:55:0x016c, B:37:0x00fb, B:39:0x00ff, B:28:0x00a8, B:29:0x00b3, B:30:0x00be, B:31:0x00c9, B:32:0x00d4, B:33:0x00df), top: B:82:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x013f A[Catch: all -> 0x022a, TryCatch #0 {, blocks: (B:4:0x000b, B:6:0x0014, B:7:0x002b, B:9:0x0033, B:14:0x0048, B:15:0x005f, B:17:0x0061, B:34:0x00e9, B:36:0x00f3, B:41:0x0110, B:43:0x0122, B:45:0x0128, B:48:0x012f, B:56:0x017b, B:58:0x01bd, B:62:0x01c9, B:63:0x01cd, B:64:0x01d4, B:65:0x01d9, B:67:0x01ec, B:72:0x0206, B:76:0x021f, B:73:0x020a, B:75:0x0213, B:68:0x01f0, B:70:0x01f8, B:49:0x013f, B:51:0x0147, B:54:0x014e, B:55:0x016c, B:37:0x00fb, B:39:0x00ff, B:28:0x00a8, B:29:0x00b3, B:30:0x00be, B:31:0x00c9, B:32:0x00d4, B:33:0x00df), top: B:82:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x01bd A[Catch: all -> 0x022a, TryCatch #0 {, blocks: (B:4:0x000b, B:6:0x0014, B:7:0x002b, B:9:0x0033, B:14:0x0048, B:15:0x005f, B:17:0x0061, B:34:0x00e9, B:36:0x00f3, B:41:0x0110, B:43:0x0122, B:45:0x0128, B:48:0x012f, B:56:0x017b, B:58:0x01bd, B:62:0x01c9, B:63:0x01cd, B:64:0x01d4, B:65:0x01d9, B:67:0x01ec, B:72:0x0206, B:76:0x021f, B:73:0x020a, B:75:0x0213, B:68:0x01f0, B:70:0x01f8, B:49:0x013f, B:51:0x0147, B:54:0x014e, B:55:0x016c, B:37:0x00fb, B:39:0x00ff, B:28:0x00a8, B:29:0x00b3, B:30:0x00be, B:31:0x00c9, B:32:0x00d4, B:33:0x00df), top: B:82:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:67:0x01ec A[Catch: all -> 0x022a, TryCatch #0 {, blocks: (B:4:0x000b, B:6:0x0014, B:7:0x002b, B:9:0x0033, B:14:0x0048, B:15:0x005f, B:17:0x0061, B:34:0x00e9, B:36:0x00f3, B:41:0x0110, B:43:0x0122, B:45:0x0128, B:48:0x012f, B:56:0x017b, B:58:0x01bd, B:62:0x01c9, B:63:0x01cd, B:64:0x01d4, B:65:0x01d9, B:67:0x01ec, B:72:0x0206, B:76:0x021f, B:73:0x020a, B:75:0x0213, B:68:0x01f0, B:70:0x01f8, B:49:0x013f, B:51:0x0147, B:54:0x014e, B:55:0x016c, B:37:0x00fb, B:39:0x00ff, B:28:0x00a8, B:29:0x00b3, B:30:0x00be, B:31:0x00c9, B:32:0x00d4, B:33:0x00df), top: B:82:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:68:0x01f0 A[Catch: all -> 0x022a, TryCatch #0 {, blocks: (B:4:0x000b, B:6:0x0014, B:7:0x002b, B:9:0x0033, B:14:0x0048, B:15:0x005f, B:17:0x0061, B:34:0x00e9, B:36:0x00f3, B:41:0x0110, B:43:0x0122, B:45:0x0128, B:48:0x012f, B:56:0x017b, B:58:0x01bd, B:62:0x01c9, B:63:0x01cd, B:64:0x01d4, B:65:0x01d9, B:67:0x01ec, B:72:0x0206, B:76:0x021f, B:73:0x020a, B:75:0x0213, B:68:0x01f0, B:70:0x01f8, B:49:0x013f, B:51:0x0147, B:54:0x014e, B:55:0x016c, B:37:0x00fb, B:39:0x00ff, B:28:0x00a8, B:29:0x00b3, B:30:0x00be, B:31:0x00c9, B:32:0x00d4, B:33:0x00df), top: B:82:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0206 A[Catch: all -> 0x022a, TryCatch #0 {, blocks: (B:4:0x000b, B:6:0x0014, B:7:0x002b, B:9:0x0033, B:14:0x0048, B:15:0x005f, B:17:0x0061, B:34:0x00e9, B:36:0x00f3, B:41:0x0110, B:43:0x0122, B:45:0x0128, B:48:0x012f, B:56:0x017b, B:58:0x01bd, B:62:0x01c9, B:63:0x01cd, B:64:0x01d4, B:65:0x01d9, B:67:0x01ec, B:72:0x0206, B:76:0x021f, B:73:0x020a, B:75:0x0213, B:68:0x01f0, B:70:0x01f8, B:49:0x013f, B:51:0x0147, B:54:0x014e, B:55:0x016c, B:37:0x00fb, B:39:0x00ff, B:28:0x00a8, B:29:0x00b3, B:30:0x00be, B:31:0x00c9, B:32:0x00d4, B:33:0x00df), top: B:82:0x000b }] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x020a A[Catch: all -> 0x022a, TryCatch #0 {, blocks: (B:4:0x000b, B:6:0x0014, B:7:0x002b, B:9:0x0033, B:14:0x0048, B:15:0x005f, B:17:0x0061, B:34:0x00e9, B:36:0x00f3, B:41:0x0110, B:43:0x0122, B:45:0x0128, B:48:0x012f, B:56:0x017b, B:58:0x01bd, B:62:0x01c9, B:63:0x01cd, B:64:0x01d4, B:65:0x01d9, B:67:0x01ec, B:72:0x0206, B:76:0x021f, B:73:0x020a, B:75:0x0213, B:68:0x01f0, B:70:0x01f8, B:49:0x013f, B:51:0x0147, B:54:0x014e, B:55:0x016c, B:37:0x00fb, B:39:0x00ff, B:28:0x00a8, B:29:0x00b3, B:30:0x00be, B:31:0x00c9, B:32:0x00d4, B:33:0x00df), top: B:82:0x000b }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void stopSyncEvent(long j, long j2, String str, long j3, long j4, String str2, int i) {
        SyncHistoryItem syncHistoryItem;
        boolean z;
        synchronized (this.mAuthorities) {
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "stopSyncEvent: historyId=" + j);
            }
            int size = this.mSyncHistory.size();
            while (true) {
                if (size <= 0) {
                    syncHistoryItem = null;
                    break;
                }
                size--;
                syncHistoryItem = this.mSyncHistory.get(size);
                if (syncHistoryItem.historyId == j) {
                    break;
                }
            }
            if (syncHistoryItem == null) {
                Slog.w("SyncManager", "stopSyncEvent: no history for id " + j);
                return;
            }
            syncHistoryItem.elapsedTime = j2;
            syncHistoryItem.event = 1;
            syncHistoryItem.mesg = str;
            syncHistoryItem.downstreamActivity = j3;
            syncHistoryItem.upstreamActivity = j4;
            SyncStatusInfo orCreateSyncStatusLocked = getOrCreateSyncStatusLocked(syncHistoryItem.authorityId);
            orCreateSyncStatusLocked.maybeResetTodayStats(isClockValid(), false);
            SyncStatusInfo.Stats stats = orCreateSyncStatusLocked.totalStats;
            stats.numSyncs++;
            SyncStatusInfo.Stats stats2 = orCreateSyncStatusLocked.todayStats;
            stats2.numSyncs++;
            stats.totalElapsedTime += j2;
            stats2.totalElapsedTime += j2;
            int i2 = syncHistoryItem.source;
            if (i2 == 0) {
                stats.numSourceOther++;
                stats2.numSourceOther++;
            } else if (i2 == 1) {
                stats.numSourceLocal++;
                stats2.numSourceLocal++;
            } else if (i2 == 2) {
                stats.numSourcePoll++;
                stats2.numSourcePoll++;
            } else if (i2 == 3) {
                stats.numSourceUser++;
                stats2.numSourceUser++;
            } else if (i2 == 4) {
                stats.numSourcePeriodic++;
                stats2.numSourcePeriodic++;
            } else if (i2 == 5) {
                stats.numSourceFeed++;
                stats2.numSourceFeed++;
            }
            int currentDayLocked = getCurrentDayLocked();
            DayStats[] dayStatsArr = this.mDayStats;
            DayStats dayStats = dayStatsArr[0];
            if (dayStats == null) {
                dayStatsArr[0] = new DayStats(currentDayLocked);
            } else if (currentDayLocked != dayStats.day) {
                System.arraycopy(dayStatsArr, 0, dayStatsArr, 1, dayStatsArr.length - 1);
                this.mDayStats[0] = new DayStats(currentDayLocked);
                z = true;
                DayStats dayStats2 = this.mDayStats[0];
                long j5 = syncHistoryItem.eventTime + j2;
                if (!"success".equals(str)) {
                    if (orCreateSyncStatusLocked.lastSuccessTime == 0 || orCreateSyncStatusLocked.lastFailureTime != 0) {
                        r11 = true;
                    }
                    orCreateSyncStatusLocked.setLastSuccess(syncHistoryItem.source, j5);
                    dayStats2.successCount++;
                    dayStats2.successTime += j2;
                } else if (!"canceled".equals(str)) {
                    r11 = orCreateSyncStatusLocked.lastFailureTime == 0;
                    orCreateSyncStatusLocked.totalStats.numFailures++;
                    orCreateSyncStatusLocked.todayStats.numFailures++;
                    orCreateSyncStatusLocked.setLastFailure(syncHistoryItem.source, j5, str);
                    dayStats2.failureCount++;
                    dayStats2.failureTime += j2;
                } else {
                    orCreateSyncStatusLocked.totalStats.numCancels++;
                    orCreateSyncStatusLocked.todayStats.numCancels++;
                    r11 = true;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("" + str + " Source=" + SOURCES[syncHistoryItem.source] + " Elapsed=");
                SyncManager.formatDurationHMS(sb, j2);
                sb.append(" Reason=");
                sb.append(SyncOperation.reasonToString(null, syncHistoryItem.reason));
                if (syncHistoryItem.syncExemptionFlag != 0) {
                    sb.append(" Exemption=");
                    int i3 = syncHistoryItem.syncExemptionFlag;
                    if (i3 == 1) {
                        sb.append("fg");
                    } else if (i3 == 2) {
                        sb.append("top");
                    } else {
                        sb.append(i3);
                    }
                }
                sb.append(" Extras=");
                SyncOperation.extrasToStringBuilder(syncHistoryItem.extras, sb);
                orCreateSyncStatusLocked.addEvent(sb.toString());
                if (!r11) {
                    writeStatusLocked();
                } else if (!this.mHandler.hasMessages(1)) {
                    MyHandler myHandler = this.mHandler;
                    myHandler.sendMessageDelayed(myHandler.obtainMessage(1), 600000L);
                }
                if (!z) {
                    writeStatisticsLocked();
                } else if (!this.mHandler.hasMessages(2)) {
                    MyHandler myHandler2 = this.mHandler;
                    myHandler2.sendMessageDelayed(myHandler2.obtainMessage(2), 1800000L);
                }
                reportChange(8, str2, i);
            }
            z = false;
            DayStats dayStats22 = this.mDayStats[0];
            long j52 = syncHistoryItem.eventTime + j2;
            if (!"success".equals(str)) {
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("" + str + " Source=" + SOURCES[syncHistoryItem.source] + " Elapsed=");
            SyncManager.formatDurationHMS(sb2, j2);
            sb2.append(" Reason=");
            sb2.append(SyncOperation.reasonToString(null, syncHistoryItem.reason));
            if (syncHistoryItem.syncExemptionFlag != 0) {
            }
            sb2.append(" Extras=");
            SyncOperation.extrasToStringBuilder(syncHistoryItem.extras, sb2);
            orCreateSyncStatusLocked.addEvent(sb2.toString());
            if (!r11) {
            }
            if (!z) {
            }
            reportChange(8, str2, i);
        }
    }

    public final List<SyncInfo> getCurrentSyncs(int i) {
        List<SyncInfo> currentSyncsLocked;
        synchronized (this.mAuthorities) {
            currentSyncsLocked = getCurrentSyncsLocked(i);
        }
        return currentSyncsLocked;
    }

    public List<SyncInfo> getCurrentSyncsCopy(int i, boolean z) {
        ArrayList arrayList;
        SyncInfo syncInfo;
        synchronized (this.mAuthorities) {
            List<SyncInfo> currentSyncsLocked = getCurrentSyncsLocked(i);
            arrayList = new ArrayList();
            for (SyncInfo syncInfo2 : currentSyncsLocked) {
                if (!z) {
                    syncInfo = SyncInfo.createAccountRedacted(syncInfo2.authorityId, syncInfo2.authority, syncInfo2.startTime);
                } else {
                    syncInfo = new SyncInfo(syncInfo2);
                }
                arrayList.add(syncInfo);
            }
        }
        return arrayList;
    }

    public final List<SyncInfo> getCurrentSyncsLocked(int i) {
        ArrayList<SyncInfo> arrayList = this.mCurrentSyncs.get(i);
        if (arrayList == null) {
            ArrayList<SyncInfo> arrayList2 = new ArrayList<>();
            this.mCurrentSyncs.put(i, arrayList2);
            return arrayList2;
        }
        return arrayList;
    }

    public Pair<AuthorityInfo, SyncStatusInfo> getCopyOfAuthorityWithSyncStatus(EndPoint endPoint) {
        Pair<AuthorityInfo, SyncStatusInfo> createCopyPairOfAuthorityWithSyncStatusLocked;
        synchronized (this.mAuthorities) {
            createCopyPairOfAuthorityWithSyncStatusLocked = createCopyPairOfAuthorityWithSyncStatusLocked(getOrCreateAuthorityLocked(endPoint, -1, true));
        }
        return createCopyPairOfAuthorityWithSyncStatusLocked;
    }

    public SyncStatusInfo getStatusByAuthority(EndPoint endPoint) {
        if (endPoint.account == null || endPoint.provider == null) {
            return null;
        }
        synchronized (this.mAuthorities) {
            int size = this.mSyncStatus.size();
            for (int i = 0; i < size; i++) {
                SyncStatusInfo valueAt = this.mSyncStatus.valueAt(i);
                AuthorityInfo authorityInfo = this.mAuthorities.get(valueAt.authorityId);
                if (authorityInfo != null && authorityInfo.target.matchesSpec(endPoint)) {
                    return valueAt;
                }
            }
            return null;
        }
    }

    public boolean isSyncPending(EndPoint endPoint) {
        synchronized (this.mAuthorities) {
            int size = this.mSyncStatus.size();
            for (int i = 0; i < size; i++) {
                SyncStatusInfo valueAt = this.mSyncStatus.valueAt(i);
                AuthorityInfo authorityInfo = this.mAuthorities.get(valueAt.authorityId);
                if (authorityInfo != null && authorityInfo.target.matchesSpec(endPoint) && valueAt.pending) {
                    return true;
                }
            }
            return false;
        }
    }

    public ArrayList<SyncHistoryItem> getSyncHistory() {
        ArrayList<SyncHistoryItem> arrayList;
        synchronized (this.mAuthorities) {
            int size = this.mSyncHistory.size();
            arrayList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                arrayList.add(this.mSyncHistory.get(i));
            }
        }
        return arrayList;
    }

    public DayStats[] getDayStatistics() {
        DayStats[] dayStatsArr;
        synchronized (this.mAuthorities) {
            DayStats[] dayStatsArr2 = this.mDayStats;
            int length = dayStatsArr2.length;
            dayStatsArr = new DayStats[length];
            System.arraycopy(dayStatsArr2, 0, dayStatsArr, 0, length);
        }
        return dayStatsArr;
    }

    public final Pair<AuthorityInfo, SyncStatusInfo> createCopyPairOfAuthorityWithSyncStatusLocked(AuthorityInfo authorityInfo) {
        return Pair.create(new AuthorityInfo(authorityInfo), new SyncStatusInfo(getOrCreateSyncStatusLocked(authorityInfo.ident)));
    }

    public final int getCurrentDayLocked() {
        this.mCal.setTimeInMillis(System.currentTimeMillis());
        int i = this.mCal.get(6);
        if (this.mYear != this.mCal.get(1)) {
            this.mYear = this.mCal.get(1);
            this.mCal.clear();
            this.mCal.set(1, this.mYear);
            this.mYearInDays = (int) (this.mCal.getTimeInMillis() / BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS);
        }
        return i + this.mYearInDays;
    }

    public final AuthorityInfo getAuthorityLocked(EndPoint endPoint, String str) {
        AccountAndUser accountAndUser = new AccountAndUser(endPoint.account, endPoint.userId);
        AccountInfo accountInfo = this.mAccounts.get(accountAndUser);
        if (accountInfo == null) {
            if (str != null && Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", str + ": unknown account " + accountAndUser);
            }
            return null;
        }
        AuthorityInfo authorityInfo = accountInfo.authorities.get(endPoint.provider);
        if (authorityInfo == null) {
            if (str != null && Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", str + ": unknown provider " + endPoint.provider);
            }
            return null;
        }
        return authorityInfo;
    }

    public final AuthorityInfo getOrCreateAuthorityLocked(EndPoint endPoint, int i, boolean z) {
        AccountAndUser accountAndUser = new AccountAndUser(endPoint.account, endPoint.userId);
        AccountInfo accountInfo = this.mAccounts.get(accountAndUser);
        if (accountInfo == null) {
            accountInfo = new AccountInfo(accountAndUser);
            this.mAccounts.put(accountAndUser, accountInfo);
        }
        AuthorityInfo authorityInfo = accountInfo.authorities.get(endPoint.provider);
        if (authorityInfo == null) {
            AuthorityInfo createAuthorityLocked = createAuthorityLocked(endPoint, i, z);
            accountInfo.authorities.put(endPoint.provider, createAuthorityLocked);
            return createAuthorityLocked;
        }
        return authorityInfo;
    }

    public final AuthorityInfo createAuthorityLocked(EndPoint endPoint, int i, boolean z) {
        if (i < 0) {
            i = this.mNextAuthorityId;
            this.mNextAuthorityId = i + 1;
            z = true;
        }
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "created a new AuthorityInfo for " + endPoint);
        }
        AuthorityInfo authorityInfo = new AuthorityInfo(endPoint, i);
        this.mAuthorities.put(i, authorityInfo);
        if (z) {
            writeAccountInfoLocked();
        }
        return authorityInfo;
    }

    public void removeAuthority(EndPoint endPoint) {
        synchronized (this.mAuthorities) {
            removeAuthorityLocked(endPoint.account, endPoint.userId, endPoint.provider, true);
        }
    }

    public final void removeAuthorityLocked(Account account, int i, String str, boolean z) {
        AuthorityInfo remove;
        AccountInfo accountInfo = this.mAccounts.get(new AccountAndUser(account, i));
        if (accountInfo == null || (remove = accountInfo.authorities.remove(str)) == null) {
            return;
        }
        OnAuthorityRemovedListener onAuthorityRemovedListener = this.mAuthorityRemovedListener;
        if (onAuthorityRemovedListener != null) {
            onAuthorityRemovedListener.onAuthorityRemoved(remove.target);
        }
        this.mAuthorities.remove(remove.ident);
        if (z) {
            writeAccountInfoLocked();
        }
    }

    public final SyncStatusInfo getOrCreateSyncStatusLocked(int i) {
        SyncStatusInfo syncStatusInfo = this.mSyncStatus.get(i);
        if (syncStatusInfo == null) {
            SyncStatusInfo syncStatusInfo2 = new SyncStatusInfo(i);
            this.mSyncStatus.put(i, syncStatusInfo2);
            return syncStatusInfo2;
        }
        return syncStatusInfo;
    }

    public void writeAllState() {
        synchronized (this.mAuthorities) {
            writeStatusLocked();
            writeStatisticsLocked();
        }
    }

    public boolean shouldGrantSyncAdaptersAccountAccess() {
        return this.mGrantSyncAdaptersAccountAccess;
    }

    /* JADX WARN: Removed duplicated region for block: B:130:0x01c8 A[Catch: all -> 0x01f8, TRY_ENTER, TryCatch #14 {all -> 0x01f8, blocks: (B:130:0x01c8, B:131:0x01cc, B:138:0x01e5), top: B:162:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:131:0x01cc A[Catch: all -> 0x01f8, TRY_LEAVE, TryCatch #14 {all -> 0x01f8, blocks: (B:130:0x01c8, B:131:0x01cc, B:138:0x01e5), top: B:162:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:154:0x01f4 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:158:0x0205 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:165:0x01db A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:181:0x016d A[EDGE_INSN: B:181:0x016d->B:96:0x016d ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:184:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:186:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:97:0x0170 A[LOOP:1: B:59:0x00e3->B:97:0x0170, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void readAccountInfoLocked() {
        int i;
        int i2;
        int i3;
        FileInputStream fileInputStream;
        int i4;
        FileInputStream fileInputStream2;
        FileInputStream fileInputStream3;
        int i5;
        int i6;
        FileInputStream fileInputStream4 = null;
        try {
            try {
                FileInputStream openRead = this.mAccountInfoFile.openRead();
                int i7 = 2;
                try {
                    if (Log.isLoggable("SyncManagerFile", 2)) {
                        try {
                            Slog.v("SyncManagerFile", "Reading " + this.mAccountInfoFile.getBaseFile());
                        } catch (IOException e) {
                            e = e;
                            fileInputStream2 = openRead;
                            i4 = -1;
                            if (fileInputStream2 != null) {
                            }
                            this.mNextAuthorityId = Math.max(i4 + 1, this.mNextAuthorityId);
                            if (fileInputStream2 == null) {
                            }
                        } catch (XmlPullParserException e2) {
                            e = e2;
                            fileInputStream = openRead;
                            i3 = -1;
                            Slog.w("SyncManager", "Error reading accounts", e);
                            this.mNextAuthorityId = Math.max(i3 + 1, this.mNextAuthorityId);
                            if (fileInputStream == null) {
                            }
                        } catch (Throwable th) {
                            th = th;
                            i = 1;
                            fileInputStream4 = openRead;
                            i2 = -1;
                            this.mNextAuthorityId = Math.max(i2 + i, this.mNextAuthorityId);
                            if (fileInputStream4 != null) {
                            }
                            throw th;
                        }
                    }
                    TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(openRead);
                    int eventType = resolvePullParser.getEventType();
                    while (eventType != 2 && eventType != 1) {
                        eventType = resolvePullParser.next();
                    }
                    if (eventType == 1) {
                        Slog.i("SyncManager", "No initial accounts");
                        this.mNextAuthorityId = Math.max(0, this.mNextAuthorityId);
                        if (openRead != null) {
                            try {
                                openRead.close();
                                return;
                            } catch (IOException unused) {
                                return;
                            }
                        }
                        return;
                    }
                    if ("accounts".equals(resolvePullParser.getName())) {
                        boolean attributeBoolean = resolvePullParser.getAttributeBoolean((String) null, "listen-for-tickles", true);
                        int attributeInt = resolvePullParser.getAttributeInt((String) null, "version", 0);
                        if (attributeInt < 3) {
                            this.mGrantSyncAdaptersAccountAccess = true;
                        }
                        this.mNextAuthorityId = Math.max(this.mNextAuthorityId, resolvePullParser.getAttributeInt((String) null, "nextAuthorityId", 0));
                        int attributeInt2 = resolvePullParser.getAttributeInt((String) null, "offsetInSeconds", 0);
                        this.mSyncRandomOffset = attributeInt2;
                        if (attributeInt2 == 0) {
                            try {
                                fileInputStream3 = openRead;
                                try {
                                    this.mSyncRandomOffset = new Random(System.currentTimeMillis()).nextInt(86400);
                                } catch (IOException e3) {
                                    e = e3;
                                    fileInputStream2 = fileInputStream3;
                                    i4 = -1;
                                    if (fileInputStream2 != null) {
                                    }
                                    this.mNextAuthorityId = Math.max(i4 + 1, this.mNextAuthorityId);
                                    if (fileInputStream2 == null) {
                                    }
                                } catch (XmlPullParserException e4) {
                                    e = e4;
                                    fileInputStream = fileInputStream3;
                                    i3 = -1;
                                    Slog.w("SyncManager", "Error reading accounts", e);
                                    this.mNextAuthorityId = Math.max(i3 + 1, this.mNextAuthorityId);
                                    if (fileInputStream == null) {
                                    }
                                } catch (Throwable th2) {
                                    th = th2;
                                    i = 1;
                                    fileInputStream4 = fileInputStream3;
                                    i2 = -1;
                                    this.mNextAuthorityId = Math.max(i2 + i, this.mNextAuthorityId);
                                    if (fileInputStream4 != null) {
                                    }
                                    throw th;
                                }
                            } catch (IOException e5) {
                                e = e5;
                                fileInputStream3 = openRead;
                            } catch (XmlPullParserException e6) {
                                e = e6;
                                fileInputStream3 = openRead;
                            } catch (Throwable th3) {
                                th = th3;
                                fileInputStream3 = openRead;
                            }
                        } else {
                            fileInputStream3 = openRead;
                        }
                        try {
                            this.mMasterSyncAutomatically.put(0, Boolean.valueOf(attributeBoolean));
                            int next = resolvePullParser.next();
                            AccountAuthorityValidator accountAuthorityValidator = new AccountAuthorityValidator(this.mContext);
                            int i8 = -1;
                            PeriodicSync periodicSync = null;
                            AuthorityInfo authorityInfo = null;
                            while (true) {
                                if (next == i7) {
                                    try {
                                        try {
                                            String name = resolvePullParser.getName();
                                            if (resolvePullParser.getDepth() == i7) {
                                                if ("authority".equals(name)) {
                                                    authorityInfo = parseAuthority(resolvePullParser, attributeInt, accountAuthorityValidator);
                                                    if (authorityInfo != null) {
                                                        int i9 = authorityInfo.ident;
                                                        if (i9 > i8) {
                                                            i8 = i9;
                                                            periodicSync = null;
                                                        }
                                                    } else {
                                                        Object[] objArr = new Object[3];
                                                        objArr[0] = "26513719";
                                                        try {
                                                            objArr[1] = -1;
                                                            objArr[i7] = "Malformed authority";
                                                            EventLog.writeEvent(1397638484, objArr);
                                                        } catch (Throwable th4) {
                                                            th = th4;
                                                            i2 = i8;
                                                            i = 1;
                                                            fileInputStream4 = fileInputStream3;
                                                            this.mNextAuthorityId = Math.max(i2 + i, this.mNextAuthorityId);
                                                            if (fileInputStream4 != null) {
                                                                try {
                                                                    fileInputStream4.close();
                                                                } catch (IOException unused2) {
                                                                }
                                                            }
                                                            throw th;
                                                        }
                                                    }
                                                    periodicSync = null;
                                                } else if ("listenForTickles".equals(name)) {
                                                    parseListenForTickles(resolvePullParser);
                                                }
                                            } else if (resolvePullParser.getDepth() == 3) {
                                                if ("periodicSync".equals(name) && authorityInfo != null) {
                                                    periodicSync = parsePeriodicSync(resolvePullParser, authorityInfo);
                                                }
                                            } else if (resolvePullParser.getDepth() == 4 && periodicSync != null && "extra".equals(name)) {
                                                parseExtra(resolvePullParser, periodicSync.extras);
                                            }
                                            next = resolvePullParser.next();
                                            if (next != 1) {
                                                break;
                                            }
                                            i7 = 2;
                                        } catch (Throwable th5) {
                                            th = th5;
                                            i2 = i8;
                                            fileInputStream4 = fileInputStream3;
                                            i = 1;
                                            this.mNextAuthorityId = Math.max(i2 + i, this.mNextAuthorityId);
                                            if (fileInputStream4 != null) {
                                            }
                                            throw th;
                                        }
                                    } catch (IOException e7) {
                                        e = e7;
                                        i4 = i8;
                                        fileInputStream2 = fileInputStream3;
                                        if (fileInputStream2 != null) {
                                            Slog.i("SyncManager", "No initial accounts");
                                        } else {
                                            Slog.w("SyncManager", "Error reading accounts", e);
                                        }
                                        this.mNextAuthorityId = Math.max(i4 + 1, this.mNextAuthorityId);
                                        if (fileInputStream2 == null) {
                                            try {
                                                fileInputStream2.close();
                                                return;
                                            } catch (IOException unused3) {
                                                return;
                                            }
                                        }
                                        return;
                                    } catch (XmlPullParserException e8) {
                                        e = e8;
                                        i3 = i8;
                                        fileInputStream = fileInputStream3;
                                        Slog.w("SyncManager", "Error reading accounts", e);
                                        this.mNextAuthorityId = Math.max(i3 + 1, this.mNextAuthorityId);
                                        if (fileInputStream == null) {
                                            try {
                                                fileInputStream.close();
                                                return;
                                            } catch (IOException unused4) {
                                                return;
                                            }
                                        }
                                        return;
                                    }
                                }
                                next = resolvePullParser.next();
                                if (next != 1) {
                                }
                            }
                            i5 = 1;
                            i6 = i8;
                        } catch (IOException e9) {
                            e = e9;
                            fileInputStream2 = fileInputStream3;
                            i4 = -1;
                            if (fileInputStream2 != null) {
                            }
                            this.mNextAuthorityId = Math.max(i4 + 1, this.mNextAuthorityId);
                            if (fileInputStream2 == null) {
                            }
                        } catch (XmlPullParserException e10) {
                            e = e10;
                            fileInputStream = fileInputStream3;
                            i3 = -1;
                            Slog.w("SyncManager", "Error reading accounts", e);
                            this.mNextAuthorityId = Math.max(i3 + 1, this.mNextAuthorityId);
                            if (fileInputStream == null) {
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            i = 1;
                            fileInputStream4 = fileInputStream3;
                            i2 = -1;
                            this.mNextAuthorityId = Math.max(i2 + i, this.mNextAuthorityId);
                            if (fileInputStream4 != null) {
                            }
                            throw th;
                        }
                    } else {
                        fileInputStream3 = openRead;
                        i5 = 1;
                        i6 = -1;
                    }
                    this.mNextAuthorityId = Math.max(i6 + i5, this.mNextAuthorityId);
                    if (fileInputStream3 != null) {
                        try {
                            fileInputStream3.close();
                        } catch (IOException unused5) {
                        }
                    }
                    maybeMigrateSettingsForRenamedAuthorities();
                } catch (IOException e11) {
                    e = e11;
                    fileInputStream3 = openRead;
                } catch (XmlPullParserException e12) {
                    e = e12;
                    fileInputStream3 = openRead;
                } catch (Throwable th7) {
                    th = th7;
                    fileInputStream3 = openRead;
                }
            } catch (Throwable th8) {
                th = th8;
            }
        } catch (IOException e13) {
            e = e13;
            i4 = -1;
            fileInputStream2 = null;
        } catch (XmlPullParserException e14) {
            e = e14;
            i3 = -1;
            fileInputStream = null;
        } catch (Throwable th9) {
            th = th9;
            i = 1;
            i2 = -1;
            fileInputStream4 = null;
        }
    }

    public final void maybeDeleteLegacyPendingInfoLocked(File file) {
        File file2 = new File(file, "pending.bin");
        if (file2.exists()) {
            file2.delete();
        }
    }

    public final boolean maybeMigrateSettingsForRenamedAuthorities() {
        ArrayList arrayList = new ArrayList();
        int size = this.mAuthorities.size();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            AuthorityInfo valueAt = this.mAuthorities.valueAt(i);
            String str = sAuthorityRenames.get(valueAt.target.provider);
            if (str != null) {
                arrayList.add(valueAt);
                if (valueAt.enabled) {
                    EndPoint endPoint = valueAt.target;
                    EndPoint endPoint2 = new EndPoint(endPoint.account, str, endPoint.userId);
                    if (getAuthorityLocked(endPoint2, "cleanup") == null) {
                        getOrCreateAuthorityLocked(endPoint2, -1, false).enabled = true;
                        z = true;
                    }
                }
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            EndPoint endPoint3 = ((AuthorityInfo) it.next()).target;
            removeAuthorityLocked(endPoint3.account, endPoint3.userId, endPoint3.provider, false);
            z = true;
        }
        return z;
    }

    public final void parseListenForTickles(TypedXmlPullParser typedXmlPullParser) {
        try {
            typedXmlPullParser.getAttributeInt((String) null, "user");
        } catch (XmlPullParserException e) {
            Slog.e("SyncManager", "error parsing the user for listen-for-tickles", e);
        }
        this.mMasterSyncAutomatically.put(0, Boolean.valueOf(typedXmlPullParser.getAttributeBoolean((String) null, "enabled", true)));
    }

    public final AuthorityInfo parseAuthority(TypedXmlPullParser typedXmlPullParser, int i, AccountAuthorityValidator accountAuthorityValidator) throws XmlPullParserException {
        int i2;
        int i3;
        String str;
        int parseInt;
        AuthorityInfo authorityInfo = null;
        try {
            i2 = typedXmlPullParser.getAttributeInt((String) null, "id");
        } catch (XmlPullParserException e) {
            Slog.e("SyncManager", "error parsing the id of the authority", e);
            i2 = -1;
        }
        if (i2 >= 0) {
            String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "authority");
            boolean attributeBoolean = typedXmlPullParser.getAttributeBoolean((String) null, "enabled", true);
            String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "syncable");
            String attributeValue3 = typedXmlPullParser.getAttributeValue((String) null, "account");
            String attributeValue4 = typedXmlPullParser.getAttributeValue((String) null, "type");
            int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "user", 0);
            String attributeValue5 = typedXmlPullParser.getAttributeValue((String) null, "package");
            String attributeValue6 = typedXmlPullParser.getAttributeValue((String) null, "class");
            if (attributeValue4 == null && attributeValue5 == null) {
                attributeValue2 = String.valueOf(-1);
                attributeValue4 = "com.google";
            }
            authorityInfo = this.mAuthorities.get(i2);
            if (Log.isLoggable("SyncManagerFile", 2)) {
                str = "SyncManager";
                StringBuilder sb = new StringBuilder();
                i3 = i2;
                sb.append("Adding authority: account=");
                sb.append(attributeValue3);
                sb.append(" accountType=");
                sb.append(attributeValue4);
                sb.append(" auth=");
                sb.append(attributeValue);
                sb.append(" package=");
                sb.append(attributeValue5);
                sb.append(" class=");
                sb.append(attributeValue6);
                sb.append(" user=");
                sb.append(attributeInt);
                sb.append(" enabled=");
                sb.append(attributeBoolean);
                sb.append(" syncable=");
                sb.append(attributeValue2);
                Slog.v("SyncManagerFile", sb.toString());
            } else {
                i3 = i2;
                str = "SyncManager";
            }
            if (authorityInfo == null) {
                if (Log.isLoggable("SyncManagerFile", 2)) {
                    Slog.v("SyncManagerFile", "Creating authority entry");
                }
                if (attributeValue3 != null && attributeValue != null) {
                    EndPoint endPoint = new EndPoint(new Account(attributeValue3, attributeValue4), attributeValue, attributeInt);
                    if (accountAuthorityValidator.isAccountValid(endPoint.account, attributeInt) && accountAuthorityValidator.isAuthorityValid(attributeValue, attributeInt)) {
                        AuthorityInfo orCreateAuthorityLocked = getOrCreateAuthorityLocked(endPoint, i3, false);
                        if (i > 0) {
                            orCreateAuthorityLocked.periodicSyncs.clear();
                        }
                        authorityInfo = orCreateAuthorityLocked;
                    } else {
                        EventLog.writeEvent(1397638484, "35028827", -1, "account:" + endPoint.account + " provider:" + attributeValue + " user:" + attributeInt);
                    }
                }
            }
            if (authorityInfo != null) {
                authorityInfo.enabled = attributeBoolean;
                if (attributeValue2 == null) {
                    parseInt = -1;
                } else {
                    try {
                        parseInt = Integer.parseInt(attributeValue2);
                    } catch (NumberFormatException unused) {
                        if ("unknown".equals(attributeValue2)) {
                            authorityInfo.syncable = -1;
                        } else {
                            authorityInfo.syncable = Boolean.parseBoolean(attributeValue2) ? 1 : 0;
                        }
                    }
                }
                authorityInfo.syncable = parseInt;
            } else {
                Slog.w(str, "Failure adding authority: auth=" + attributeValue + " enabled=" + attributeBoolean + " syncable=" + attributeValue2);
            }
        }
        return authorityInfo;
    }

    public final PeriodicSync parsePeriodicSync(TypedXmlPullParser typedXmlPullParser, AuthorityInfo authorityInfo) {
        long j;
        Bundle bundle = new Bundle();
        try {
            long attributeLong = typedXmlPullParser.getAttributeLong((String) null, "period");
            try {
                j = typedXmlPullParser.getAttributeLong((String) null, "flex");
            } catch (XmlPullParserException e) {
                long calculateDefaultFlexTime = calculateDefaultFlexTime(attributeLong);
                Slog.e("SyncManager", "Error formatting value parsed for periodic sync flex, using default: " + calculateDefaultFlexTime, e);
                j = calculateDefaultFlexTime;
            }
            EndPoint endPoint = authorityInfo.target;
            PeriodicSync periodicSync = new PeriodicSync(endPoint.account, endPoint.provider, bundle, attributeLong, j);
            authorityInfo.periodicSyncs.add(periodicSync);
            return periodicSync;
        } catch (XmlPullParserException e2) {
            Slog.e("SyncManager", "error parsing the period of a periodic sync", e2);
            return null;
        }
    }

    public final void parseExtra(TypedXmlPullParser typedXmlPullParser, Bundle bundle) {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
        String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "type");
        try {
            if ("long".equals(attributeValue2)) {
                bundle.putLong(attributeValue, typedXmlPullParser.getAttributeLong((String) null, "value1"));
            } else if ("integer".equals(attributeValue2)) {
                bundle.putInt(attributeValue, typedXmlPullParser.getAttributeInt((String) null, "value1"));
            } else if ("double".equals(attributeValue2)) {
                bundle.putDouble(attributeValue, typedXmlPullParser.getAttributeDouble((String) null, "value1"));
            } else if ("float".equals(attributeValue2)) {
                bundle.putFloat(attributeValue, typedXmlPullParser.getAttributeFloat((String) null, "value1"));
            } else if ("boolean".equals(attributeValue2)) {
                bundle.putBoolean(attributeValue, typedXmlPullParser.getAttributeBoolean((String) null, "value1"));
            } else if ("string".equals(attributeValue2)) {
                bundle.putString(attributeValue, typedXmlPullParser.getAttributeValue((String) null, "value1"));
            } else if ("account".equals(attributeValue2)) {
                bundle.putParcelable(attributeValue, new Account(typedXmlPullParser.getAttributeValue((String) null, "value1"), typedXmlPullParser.getAttributeValue((String) null, "value2")));
            }
        } catch (XmlPullParserException e) {
            Slog.e("SyncManager", "error parsing bundle value", e);
        }
    }

    public final void writeAccountInfoLocked() {
        FileOutputStream startWrite;
        if (Log.isLoggable("SyncManagerFile", 2)) {
            Slog.v("SyncManagerFile", "Writing new " + this.mAccountInfoFile.getBaseFile());
        }
        FileOutputStream fileOutputStream = null;
        try {
            startWrite = this.mAccountInfoFile.startWrite();
        } catch (IOException e) {
            e = e;
        }
        try {
            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
            resolveSerializer.startDocument((String) null, Boolean.TRUE);
            resolveSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            resolveSerializer.startTag((String) null, "accounts");
            resolveSerializer.attributeInt((String) null, "version", 3);
            resolveSerializer.attributeInt((String) null, "nextAuthorityId", this.mNextAuthorityId);
            resolveSerializer.attributeInt((String) null, "offsetInSeconds", this.mSyncRandomOffset);
            int size = this.mMasterSyncAutomatically.size();
            for (int i = 0; i < size; i++) {
                int keyAt = this.mMasterSyncAutomatically.keyAt(i);
                resolveSerializer.startTag((String) null, "listenForTickles");
                resolveSerializer.attributeInt((String) null, "user", keyAt);
                resolveSerializer.attributeBoolean((String) null, "enabled", this.mMasterSyncAutomatically.valueAt(i).booleanValue());
                resolveSerializer.endTag((String) null, "listenForTickles");
            }
            int size2 = this.mAuthorities.size();
            for (int i2 = 0; i2 < size2; i2++) {
                AuthorityInfo valueAt = this.mAuthorities.valueAt(i2);
                EndPoint endPoint = valueAt.target;
                resolveSerializer.startTag((String) null, "authority");
                resolveSerializer.attributeInt((String) null, "id", valueAt.ident);
                resolveSerializer.attributeInt((String) null, "user", endPoint.userId);
                resolveSerializer.attributeBoolean((String) null, "enabled", valueAt.enabled);
                resolveSerializer.attribute((String) null, "account", endPoint.account.name);
                resolveSerializer.attribute((String) null, "type", endPoint.account.type);
                resolveSerializer.attribute((String) null, "authority", endPoint.provider);
                resolveSerializer.attributeInt((String) null, "syncable", valueAt.syncable);
                resolveSerializer.endTag((String) null, "authority");
            }
            resolveSerializer.endTag((String) null, "accounts");
            resolveSerializer.endDocument();
            this.mAccountInfoFile.finishWrite(startWrite);
        } catch (IOException e2) {
            e = e2;
            fileOutputStream = startWrite;
            Slog.w("SyncManager", "Error writing accounts", e);
            if (fileOutputStream != null) {
                this.mAccountInfoFile.failWrite(fileOutputStream);
            }
        }
    }

    public final void readStatusParcelLocked(File file) {
        Parcel obtain;
        try {
            byte[] readFully = new AtomicFile(file).readFully();
            obtain = Parcel.obtain();
            obtain.unmarshall(readFully, 0, readFully.length);
            obtain.setDataPosition(0);
        } catch (IOException unused) {
            Slog.i("SyncManager", "No initial status");
            return;
        }
        while (true) {
            int readInt = obtain.readInt();
            if (readInt != 0) {
                if (readInt == 100) {
                    try {
                        SyncStatusInfo syncStatusInfo = new SyncStatusInfo(obtain);
                        if (this.mAuthorities.indexOfKey(syncStatusInfo.authorityId) >= 0) {
                            syncStatusInfo.pending = false;
                            this.mSyncStatus.put(syncStatusInfo.authorityId, syncStatusInfo);
                        }
                    } catch (Exception e) {
                        Slog.e("SyncManager", "Unable to parse some sync status.", e);
                    }
                } else {
                    Slog.w("SyncManager", "Unknown status token: " + readInt);
                    return;
                }
                Slog.i("SyncManager", "No initial status");
                return;
            }
            return;
        }
    }

    public final void upgradeStatusIfNeededLocked() {
        File file = new File(this.mSyncDir, "status.bin");
        if (file.exists() && !this.mStatusFile.exists()) {
            readStatusParcelLocked(file);
            writeStatusLocked();
        }
        if (file.exists() && this.mStatusFile.exists()) {
            file.delete();
        }
    }

    @VisibleForTesting
    public void readStatusLocked() {
        upgradeStatusIfNeededLocked();
        if (this.mStatusFile.exists()) {
            try {
                FileInputStream openRead = this.mStatusFile.openRead();
                readStatusInfoLocked(openRead);
                if (openRead != null) {
                    openRead.close();
                }
            } catch (Exception e) {
                Slog.e("SyncManager", "Unable to read status info file.", e);
            }
        }
    }

    public final void readStatusInfoLocked(InputStream inputStream) throws IOException {
        ProtoInputStream protoInputStream = new ProtoInputStream(inputStream);
        while (true) {
            int nextField = protoInputStream.nextField();
            if (nextField == -1) {
                return;
            }
            if (nextField == 1) {
                long start = protoInputStream.start(2246267895809L);
                SyncStatusInfo readSyncStatusInfoLocked = readSyncStatusInfoLocked(protoInputStream);
                protoInputStream.end(start);
                if (this.mAuthorities.indexOfKey(readSyncStatusInfoLocked.authorityId) >= 0) {
                    readSyncStatusInfoLocked.pending = false;
                    this.mSyncStatus.put(readSyncStatusInfoLocked.authorityId, readSyncStatusInfoLocked);
                }
            } else if (nextField == 2) {
                this.mIsJobNamespaceMigrated = protoInputStream.readBoolean(1133871366146L);
            } else if (nextField == 3) {
                this.mIsJobAttributionFixed = protoInputStream.readBoolean(1133871366147L);
            }
        }
    }

    public final SyncStatusInfo readSyncStatusInfoLocked(ProtoInputStream protoInputStream) throws IOException {
        SyncStatusInfo syncStatusInfo;
        int i = 0;
        if (protoInputStream.nextField(1120986464258L)) {
            syncStatusInfo = new SyncStatusInfo(protoInputStream.readInt(1120986464258L));
        } else {
            syncStatusInfo = new SyncStatusInfo(0);
        }
        ArrayList arrayList = new ArrayList();
        int i2 = 0;
        while (true) {
            int nextField = protoInputStream.nextField();
            if (nextField != -1) {
                switch (nextField) {
                    case 2:
                        Slog.w("SyncManager", "Failed to read the authority id via fast-path; some data might not have been read.");
                        syncStatusInfo = new SyncStatusInfo(protoInputStream.readInt(1120986464258L), syncStatusInfo);
                        break;
                    case 3:
                        syncStatusInfo.lastSuccessTime = protoInputStream.readLong(1112396529667L);
                        break;
                    case 4:
                        syncStatusInfo.lastSuccessSource = protoInputStream.readInt(1120986464260L);
                        break;
                    case 5:
                        syncStatusInfo.lastFailureTime = protoInputStream.readLong(1112396529669L);
                        break;
                    case 6:
                        syncStatusInfo.lastFailureSource = protoInputStream.readInt(1120986464262L);
                        break;
                    case 7:
                        syncStatusInfo.lastFailureMesg = protoInputStream.readString(1138166333447L);
                        break;
                    case 8:
                        syncStatusInfo.initialFailureTime = protoInputStream.readLong(1112396529672L);
                        break;
                    case 9:
                        syncStatusInfo.pending = protoInputStream.readBoolean(1133871366153L);
                        break;
                    case 10:
                        syncStatusInfo.initialize = protoInputStream.readBoolean(1133871366154L);
                        break;
                    case 11:
                        syncStatusInfo.addPeriodicSyncTime(protoInputStream.readLong(2211908157451L));
                        break;
                    case 12:
                        long start = protoInputStream.start(2246267895820L);
                        Pair<Long, String> parseLastEventInfoLocked = parseLastEventInfoLocked(protoInputStream);
                        if (parseLastEventInfoLocked != null) {
                            arrayList.add(parseLastEventInfoLocked);
                        }
                        protoInputStream.end(start);
                        break;
                    case 13:
                        syncStatusInfo.lastTodayResetTime = protoInputStream.readLong(1112396529677L);
                        break;
                    case 14:
                        long start2 = protoInputStream.start(1146756268046L);
                        readSyncStatusStatsLocked(protoInputStream, syncStatusInfo.totalStats);
                        protoInputStream.end(start2);
                        break;
                    case 15:
                        long start3 = protoInputStream.start(1146756268047L);
                        readSyncStatusStatsLocked(protoInputStream, syncStatusInfo.todayStats);
                        protoInputStream.end(start3);
                        break;
                    case 16:
                        long start4 = protoInputStream.start(1146756268048L);
                        readSyncStatusStatsLocked(protoInputStream, syncStatusInfo.yesterdayStats);
                        protoInputStream.end(start4);
                        break;
                    case 17:
                        long readLong = protoInputStream.readLong(2211908157457L);
                        long[] jArr = syncStatusInfo.perSourceLastSuccessTimes;
                        if (i == jArr.length) {
                            Slog.w("SyncManager", "Attempted to read more per source last success times than expected; data might be corrupted.");
                            break;
                        } else {
                            jArr[i] = readLong;
                            i++;
                            break;
                        }
                    case 18:
                        long readLong2 = protoInputStream.readLong(2211908157458L);
                        long[] jArr2 = syncStatusInfo.perSourceLastFailureTimes;
                        if (i2 == jArr2.length) {
                            Slog.w("SyncManager", "Attempted to read more per source last failure times than expected; data might be corrupted.");
                            break;
                        } else {
                            jArr2[i2] = readLong2;
                            i2++;
                            break;
                        }
                }
            } else {
                syncStatusInfo.populateLastEventsInformation(arrayList);
                return syncStatusInfo;
            }
        }
    }

    public final Pair<Long, String> parseLastEventInfoLocked(ProtoInputStream protoInputStream) throws IOException {
        long j = 0;
        String str = null;
        while (true) {
            int nextField = protoInputStream.nextField();
            if (nextField == -1) {
                break;
            } else if (nextField == 1) {
                j = protoInputStream.readLong(1112396529665L);
            } else if (nextField == 2) {
                str = protoInputStream.readString(1138166333442L);
            }
        }
        if (str == null) {
            return null;
        }
        return new Pair<>(Long.valueOf(j), str);
    }

    public final void readSyncStatusStatsLocked(ProtoInputStream protoInputStream, SyncStatusInfo.Stats stats) throws IOException {
        while (true) {
            switch (protoInputStream.nextField()) {
                case -1:
                    return;
                case 1:
                    stats.totalElapsedTime = protoInputStream.readLong(1112396529665L);
                    break;
                case 2:
                    stats.numSyncs = protoInputStream.readInt(1120986464258L);
                    break;
                case 3:
                    stats.numFailures = protoInputStream.readInt(1120986464259L);
                    break;
                case 4:
                    stats.numCancels = protoInputStream.readInt(1120986464260L);
                    break;
                case 5:
                    stats.numSourceOther = protoInputStream.readInt(1120986464261L);
                    break;
                case 6:
                    stats.numSourceLocal = protoInputStream.readInt(1120986464262L);
                    break;
                case 7:
                    stats.numSourcePoll = protoInputStream.readInt(1120986464263L);
                    break;
                case 8:
                    stats.numSourceUser = protoInputStream.readInt(1120986464264L);
                    break;
                case 9:
                    stats.numSourcePeriodic = protoInputStream.readInt(1120986464265L);
                    break;
                case 10:
                    stats.numSourceFeed = protoInputStream.readInt(1120986464266L);
                    break;
            }
        }
    }

    @VisibleForTesting
    public void writeStatusLocked() {
        FileOutputStream fileOutputStream;
        Throwable th;
        Throwable e;
        if (Log.isLoggable("SyncManagerFile", 2)) {
            Slog.v("SyncManagerFile", "Writing new " + this.mStatusFile.getBaseFile());
        }
        this.mHandler.removeMessages(1);
        try {
            fileOutputStream = this.mStatusFile.startWrite();
            try {
                try {
                    writeStatusInfoLocked(fileOutputStream);
                    this.mStatusFile.finishWrite(fileOutputStream);
                    this.mStatusFile.failWrite(null);
                } catch (IOException | IllegalArgumentException e2) {
                    e = e2;
                    Slog.e("SyncManager", "Unable to write sync status to proto.", e);
                    this.mStatusFile.failWrite(fileOutputStream);
                }
            } catch (Throwable th2) {
                th = th2;
                this.mStatusFile.failWrite(fileOutputStream);
                throw th;
            }
        } catch (IOException | IllegalArgumentException e3) {
            fileOutputStream = null;
            e = e3;
        } catch (Throwable th3) {
            fileOutputStream = null;
            th = th3;
            this.mStatusFile.failWrite(fileOutputStream);
            throw th;
        }
    }

    public final void writeStatusInfoLocked(OutputStream outputStream) {
        ProtoOutputStream protoOutputStream = new ProtoOutputStream(outputStream);
        int size = this.mSyncStatus.size();
        for (int i = 0; i < size; i++) {
            SyncStatusInfo valueAt = this.mSyncStatus.valueAt(i);
            long start = protoOutputStream.start(2246267895809L);
            protoOutputStream.write(1120986464258L, valueAt.authorityId);
            protoOutputStream.write(1112396529667L, valueAt.lastSuccessTime);
            protoOutputStream.write(1120986464260L, valueAt.lastSuccessSource);
            protoOutputStream.write(1112396529669L, valueAt.lastFailureTime);
            protoOutputStream.write(1120986464262L, valueAt.lastFailureSource);
            protoOutputStream.write(1138166333447L, valueAt.lastFailureMesg);
            protoOutputStream.write(1112396529672L, valueAt.initialFailureTime);
            protoOutputStream.write(1133871366153L, valueAt.pending);
            protoOutputStream.write(1133871366154L, valueAt.initialize);
            int periodicSyncTimesSize = valueAt.getPeriodicSyncTimesSize();
            for (int i2 = 0; i2 < periodicSyncTimesSize; i2++) {
                protoOutputStream.write(2211908157451L, valueAt.getPeriodicSyncTime(i2));
            }
            int eventCount = valueAt.getEventCount();
            for (int i3 = 0; i3 < eventCount; i3++) {
                long start2 = protoOutputStream.start(2246267895820L);
                protoOutputStream.write(1112396529665L, valueAt.getEventTime(i3));
                protoOutputStream.write(1138166333442L, valueAt.getEvent(i3));
                protoOutputStream.end(start2);
            }
            protoOutputStream.write(1112396529677L, valueAt.lastTodayResetTime);
            long start3 = protoOutputStream.start(1146756268046L);
            writeStatusStatsLocked(protoOutputStream, valueAt.totalStats);
            protoOutputStream.end(start3);
            long start4 = protoOutputStream.start(1146756268047L);
            writeStatusStatsLocked(protoOutputStream, valueAt.todayStats);
            protoOutputStream.end(start4);
            long start5 = protoOutputStream.start(1146756268048L);
            writeStatusStatsLocked(protoOutputStream, valueAt.yesterdayStats);
            protoOutputStream.end(start5);
            int length = valueAt.perSourceLastSuccessTimes.length;
            for (int i4 = 0; i4 < length; i4++) {
                protoOutputStream.write(2211908157457L, valueAt.perSourceLastSuccessTimes[i4]);
            }
            int length2 = valueAt.perSourceLastFailureTimes.length;
            for (int i5 = 0; i5 < length2; i5++) {
                protoOutputStream.write(2211908157458L, valueAt.perSourceLastFailureTimes[i5]);
            }
            protoOutputStream.end(start);
        }
        protoOutputStream.write(1133871366146L, this.mIsJobNamespaceMigrated);
        protoOutputStream.write(1133871366147L, this.mIsJobAttributionFixed);
        protoOutputStream.flush();
    }

    public final void writeStatusStatsLocked(ProtoOutputStream protoOutputStream, SyncStatusInfo.Stats stats) {
        protoOutputStream.write(1112396529665L, stats.totalElapsedTime);
        protoOutputStream.write(1120986464258L, stats.numSyncs);
        protoOutputStream.write(1120986464259L, stats.numFailures);
        protoOutputStream.write(1120986464260L, stats.numCancels);
        protoOutputStream.write(1120986464261L, stats.numSourceOther);
        protoOutputStream.write(1120986464262L, stats.numSourceLocal);
        protoOutputStream.write(1120986464263L, stats.numSourcePoll);
        protoOutputStream.write(1120986464264L, stats.numSourceUser);
        protoOutputStream.write(1120986464265L, stats.numSourcePeriodic);
        protoOutputStream.write(1120986464266L, stats.numSourceFeed);
    }

    public final void requestSync(AuthorityInfo authorityInfo, int i, Bundle bundle, int i2, int i3, int i4) {
        OnSyncRequestListener onSyncRequestListener;
        if (Process.myUid() == 1000 && (onSyncRequestListener = this.mSyncRequestListener) != null) {
            onSyncRequestListener.onSyncRequest(authorityInfo.target, i, bundle, i2, i3, i4);
            return;
        }
        SyncRequest.Builder extras = new SyncRequest.Builder().syncOnce().setExtras(bundle);
        EndPoint endPoint = authorityInfo.target;
        extras.setSyncAdapter(endPoint.account, endPoint.provider);
        ContentResolver.requestSync(extras.build());
    }

    public final void requestSync(Account account, int i, int i2, String str, Bundle bundle, int i3, int i4, int i5) {
        OnSyncRequestListener onSyncRequestListener;
        if (Process.myUid() == 1000 && (onSyncRequestListener = this.mSyncRequestListener) != null) {
            onSyncRequestListener.onSyncRequest(new EndPoint(account, str, i), i2, bundle, i3, i4, i5);
        } else {
            ContentResolver.requestSync(account, str, bundle);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0026, code lost:
        android.util.Slog.w("SyncManager", "Unknown stats token: " + r7);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void readStatsParcelLocked(File file) {
        Parcel obtain = Parcel.obtain();
        try {
            try {
                byte[] readFully = new AtomicFile(file).readFully();
                int i = 0;
                obtain.unmarshall(readFully, 0, readFully.length);
                obtain.setDataPosition(0);
                while (true) {
                    int readInt = obtain.readInt();
                    if (readInt == 0) {
                        break;
                    }
                    if (readInt != 101 && readInt != 100) {
                        break;
                    }
                    int readInt2 = obtain.readInt();
                    if (readInt == 100) {
                        readInt2 = (readInt2 - 2009) + 14245;
                    }
                    DayStats dayStats = new DayStats(readInt2);
                    dayStats.successCount = obtain.readInt();
                    dayStats.successTime = obtain.readLong();
                    dayStats.failureCount = obtain.readInt();
                    dayStats.failureTime = obtain.readLong();
                    DayStats[] dayStatsArr = this.mDayStats;
                    if (i < dayStatsArr.length) {
                        dayStatsArr[i] = dayStats;
                        i++;
                    }
                }
            } catch (IOException unused) {
                Slog.i("SyncManager", "No initial statistics");
            }
        } finally {
            obtain.recycle();
        }
    }

    public final void upgradeStatisticsIfNeededLocked() {
        File file = new File(this.mSyncDir, "stats.bin");
        if (file.exists() && !this.mStatisticsFile.exists()) {
            readStatsParcelLocked(file);
            writeStatisticsLocked();
        }
        if (file.exists() && this.mStatisticsFile.exists()) {
            file.delete();
        }
    }

    public final void readStatisticsLocked() {
        upgradeStatisticsIfNeededLocked();
        if (this.mStatisticsFile.exists()) {
            try {
                FileInputStream openRead = this.mStatisticsFile.openRead();
                readDayStatsLocked(openRead);
                if (openRead != null) {
                    openRead.close();
                }
            } catch (Exception e) {
                Slog.e("SyncManager", "Unable to read day stats file.", e);
            }
        }
    }

    public final void readDayStatsLocked(InputStream inputStream) throws IOException {
        ProtoInputStream protoInputStream = new ProtoInputStream(inputStream);
        int i = 0;
        while (true) {
            int nextField = protoInputStream.nextField();
            if (nextField == -1) {
                return;
            }
            if (nextField == 1) {
                long start = protoInputStream.start(2246267895809L);
                DayStats readIndividualDayStatsLocked = readIndividualDayStatsLocked(protoInputStream);
                protoInputStream.end(start);
                DayStats[] dayStatsArr = this.mDayStats;
                dayStatsArr[i] = readIndividualDayStatsLocked;
                i++;
                if (i == dayStatsArr.length) {
                    return;
                }
            }
        }
    }

    public final DayStats readIndividualDayStatsLocked(ProtoInputStream protoInputStream) throws IOException {
        DayStats dayStats;
        if (protoInputStream.nextField(1120986464257L)) {
            dayStats = new DayStats(protoInputStream.readInt(1120986464257L));
        } else {
            dayStats = new DayStats(0);
        }
        while (true) {
            int nextField = protoInputStream.nextField();
            if (nextField == -1) {
                return dayStats;
            }
            if (nextField == 1) {
                Slog.w("SyncManager", "Failed to read the day via fast-path; some data might not have been read.");
                DayStats dayStats2 = new DayStats(protoInputStream.readInt(1120986464257L));
                dayStats2.successCount = dayStats.successCount;
                dayStats2.successTime = dayStats.successTime;
                dayStats2.failureCount = dayStats.failureCount;
                dayStats2.failureTime = dayStats.failureTime;
                dayStats = dayStats2;
            } else if (nextField == 2) {
                dayStats.successCount = protoInputStream.readInt(1120986464258L);
            } else if (nextField == 3) {
                dayStats.successTime = protoInputStream.readLong(1112396529667L);
            } else if (nextField == 4) {
                dayStats.failureCount = protoInputStream.readInt(1120986464260L);
            } else if (nextField == 5) {
                dayStats.failureTime = protoInputStream.readLong(1112396529669L);
            }
        }
    }

    @VisibleForTesting
    public void writeStatisticsLocked() {
        FileOutputStream fileOutputStream;
        Throwable th;
        Throwable e;
        if (Log.isLoggable("SyncManagerFile", 2)) {
            Slog.v("SyncManager", "Writing new " + this.mStatisticsFile.getBaseFile());
        }
        this.mHandler.removeMessages(2);
        try {
            fileOutputStream = this.mStatisticsFile.startWrite();
            try {
                try {
                    writeDayStatsLocked(fileOutputStream);
                    this.mStatisticsFile.finishWrite(fileOutputStream);
                    this.mStatisticsFile.failWrite(null);
                } catch (IOException | IllegalArgumentException e2) {
                    e = e2;
                    Slog.e("SyncManager", "Unable to write day stats to proto.", e);
                    this.mStatisticsFile.failWrite(fileOutputStream);
                }
            } catch (Throwable th2) {
                th = th2;
                this.mStatisticsFile.failWrite(fileOutputStream);
                throw th;
            }
        } catch (IOException | IllegalArgumentException e3) {
            fileOutputStream = null;
            e = e3;
        } catch (Throwable th3) {
            fileOutputStream = null;
            th = th3;
            this.mStatisticsFile.failWrite(fileOutputStream);
            throw th;
        }
    }

    public final void writeDayStatsLocked(OutputStream outputStream) throws IOException, IllegalArgumentException {
        ProtoOutputStream protoOutputStream = new ProtoOutputStream(outputStream);
        int length = this.mDayStats.length;
        for (int i = 0; i < length; i++) {
            DayStats dayStats = this.mDayStats[i];
            if (dayStats == null) {
                break;
            }
            long start = protoOutputStream.start(2246267895809L);
            protoOutputStream.write(1120986464257L, dayStats.day);
            protoOutputStream.write(1120986464258L, dayStats.successCount);
            protoOutputStream.write(1112396529667L, dayStats.successTime);
            protoOutputStream.write(1120986464260L, dayStats.failureCount);
            protoOutputStream.write(1112396529669L, dayStats.failureTime);
            protoOutputStream.end(start);
        }
        protoOutputStream.flush();
    }

    public void queueBackup() {
        BackupManager.dataChanged(PackageManagerShellCommandDataLoader.PACKAGE);
    }

    public void setClockValid() {
        if (this.mIsClockValid) {
            return;
        }
        this.mIsClockValid = true;
        Slog.w("SyncManager", "Clock is valid now.");
    }

    public boolean isClockValid() {
        return this.mIsClockValid;
    }

    public void resetTodayStats(boolean z) {
        if (z) {
            Log.w("SyncManager", "Force resetting today stats.");
        }
        synchronized (this.mAuthorities) {
            int size = this.mSyncStatus.size();
            for (int i = 0; i < size; i++) {
                this.mSyncStatus.valueAt(i).maybeResetTodayStats(isClockValid(), z);
            }
            writeStatusLocked();
        }
    }
}
