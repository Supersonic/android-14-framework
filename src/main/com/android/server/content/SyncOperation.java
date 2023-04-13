package com.android.server.content;

import android.accounts.Account;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Slog;
import com.android.server.content.SyncStorageEngine;
import java.util.Iterator;
/* loaded from: classes.dex */
public class SyncOperation {
    public static String[] REASON_NAMES = {"DataSettingsChanged", "AccountsUpdated", "ServiceChanged", "Periodic", "IsSyncable", "AutoSync", "MasterSyncAuto", "UserStart"};
    public final boolean allowParallelSyncs;
    public long expectedRuntime;
    public final long flexMillis;
    public final boolean isPeriodic;
    public int jobId;
    public final String key;
    public volatile Bundle mImmutableExtras;
    public final String owningPackage;
    public final int owningUid;
    public final long periodMillis;
    public final int reason;
    public int retries;
    public boolean scheduleEjAsRegularJob;
    public final int sourcePeriodicId;
    public int syncExemptionFlag;
    public final int syncSource;
    public final SyncStorageEngine.EndPoint target;
    public String wakeLockName;

    public SyncOperation(Account account, int i, int i2, String str, int i3, int i4, String str2, Bundle bundle, boolean z, int i5) {
        this(new SyncStorageEngine.EndPoint(account, str2, i), i2, str, i3, i4, bundle, z, i5);
    }

    public SyncOperation(SyncStorageEngine.EndPoint endPoint, int i, String str, int i2, int i3, Bundle bundle, boolean z, int i4) {
        this(endPoint, i, str, i2, i3, bundle, z, false, -1, 0L, 0L, i4);
    }

    public SyncOperation(SyncOperation syncOperation, long j, long j2) {
        this(syncOperation.target, syncOperation.owningUid, syncOperation.owningPackage, syncOperation.reason, syncOperation.syncSource, syncOperation.mImmutableExtras, syncOperation.allowParallelSyncs, syncOperation.isPeriodic, syncOperation.sourcePeriodicId, j, j2, 0);
    }

    public SyncOperation(SyncStorageEngine.EndPoint endPoint, int i, String str, int i2, int i3, Bundle bundle, boolean z, boolean z2, int i4, long j, long j2, int i5) {
        this.target = endPoint;
        this.owningUid = i;
        this.owningPackage = str;
        this.reason = i2;
        this.syncSource = i3;
        this.mImmutableExtras = new Bundle(bundle);
        this.allowParallelSyncs = z;
        this.isPeriodic = z2;
        this.sourcePeriodicId = i4;
        this.periodMillis = j;
        this.flexMillis = j2;
        this.jobId = -1;
        this.key = toKey();
        this.syncExemptionFlag = i5;
    }

    public SyncOperation createOneTimeSyncOperation() {
        if (this.isPeriodic) {
            return new SyncOperation(this.target, this.owningUid, this.owningPackage, this.reason, this.syncSource, this.mImmutableExtras, this.allowParallelSyncs, false, this.jobId, this.periodMillis, this.flexMillis, 0);
        }
        return null;
    }

    public PersistableBundle toJobInfoExtras() {
        PersistableBundle persistableBundle = new PersistableBundle();
        PersistableBundle persistableBundle2 = new PersistableBundle();
        Bundle bundle = this.mImmutableExtras;
        for (String str : bundle.keySet()) {
            Object obj = bundle.get(str);
            if (obj instanceof Account) {
                Account account = (Account) obj;
                PersistableBundle persistableBundle3 = new PersistableBundle();
                persistableBundle3.putString("accountName", account.name);
                persistableBundle3.putString("accountType", account.type);
                persistableBundle.putPersistableBundle("ACCOUNT:" + str, persistableBundle3);
            } else if (obj instanceof Long) {
                persistableBundle2.putLong(str, ((Long) obj).longValue());
            } else if (obj instanceof Integer) {
                persistableBundle2.putInt(str, ((Integer) obj).intValue());
            } else if (obj instanceof Boolean) {
                persistableBundle2.putBoolean(str, ((Boolean) obj).booleanValue());
            } else if (obj instanceof Float) {
                persistableBundle2.putDouble(str, ((Float) obj).floatValue());
            } else if (obj instanceof Double) {
                persistableBundle2.putDouble(str, ((Double) obj).doubleValue());
            } else if (obj instanceof String) {
                persistableBundle2.putString(str, (String) obj);
            } else if (obj == null) {
                persistableBundle2.putString(str, null);
            } else {
                Slog.e("SyncManager", "Unknown extra type.");
            }
        }
        persistableBundle.putPersistableBundle("syncExtras", persistableBundle2);
        persistableBundle.putBoolean("SyncManagerJob", true);
        persistableBundle.putString("provider", this.target.provider);
        persistableBundle.putString("accountName", this.target.account.name);
        persistableBundle.putString("accountType", this.target.account.type);
        persistableBundle.putInt("userId", this.target.userId);
        persistableBundle.putInt("owningUid", this.owningUid);
        persistableBundle.putString("owningPackage", this.owningPackage);
        persistableBundle.putInt("reason", this.reason);
        persistableBundle.putInt("source", this.syncSource);
        persistableBundle.putBoolean("allowParallelSyncs", this.allowParallelSyncs);
        persistableBundle.putInt("jobId", this.jobId);
        persistableBundle.putBoolean("isPeriodic", this.isPeriodic);
        persistableBundle.putInt("sourcePeriodicId", this.sourcePeriodicId);
        persistableBundle.putLong("periodMillis", this.periodMillis);
        persistableBundle.putLong("flexMillis", this.flexMillis);
        persistableBundle.putLong("expectedRuntime", this.expectedRuntime);
        persistableBundle.putInt("retries", this.retries);
        persistableBundle.putInt("syncExemptionFlag", this.syncExemptionFlag);
        persistableBundle.putBoolean("ejDowngradedToRegular", this.scheduleEjAsRegularJob);
        return persistableBundle;
    }

    public static SyncOperation maybeCreateFromJobExtras(PersistableBundle persistableBundle) {
        Iterator<String> it;
        if (persistableBundle != null && persistableBundle.getBoolean("SyncManagerJob", false)) {
            String string = persistableBundle.getString("accountName");
            String string2 = persistableBundle.getString("accountType");
            String string3 = persistableBundle.getString("provider");
            int i = persistableBundle.getInt("userId", Integer.MAX_VALUE);
            int i2 = persistableBundle.getInt("owningUid");
            String string4 = persistableBundle.getString("owningPackage");
            int i3 = persistableBundle.getInt("reason", Integer.MAX_VALUE);
            int i4 = persistableBundle.getInt("source", Integer.MAX_VALUE);
            boolean z = persistableBundle.getBoolean("allowParallelSyncs", false);
            boolean z2 = persistableBundle.getBoolean("isPeriodic", false);
            int i5 = persistableBundle.getInt("sourcePeriodicId", -1);
            long j = persistableBundle.getLong("periodMillis");
            long j2 = persistableBundle.getLong("flexMillis");
            int i6 = persistableBundle.getInt("syncExemptionFlag", 0);
            Bundle bundle = new Bundle();
            PersistableBundle persistableBundle2 = persistableBundle.getPersistableBundle("syncExtras");
            if (persistableBundle2 != null) {
                bundle.putAll(persistableBundle2);
            }
            Iterator<String> it2 = persistableBundle.keySet().iterator();
            while (it2.hasNext()) {
                String next = it2.next();
                if (next == null || !next.startsWith("ACCOUNT:")) {
                    it = it2;
                } else {
                    String substring = next.substring(8);
                    PersistableBundle persistableBundle3 = persistableBundle.getPersistableBundle(next);
                    it = it2;
                    bundle.putParcelable(substring, new Account(persistableBundle3.getString("accountName"), persistableBundle3.getString("accountType")));
                }
                it2 = it;
            }
            SyncOperation syncOperation = new SyncOperation(new SyncStorageEngine.EndPoint(new Account(string, string2), string3, i), i2, string4, i3, i4, bundle, z, z2, i5, j, j2, i6);
            syncOperation.jobId = persistableBundle.getInt("jobId");
            syncOperation.expectedRuntime = persistableBundle.getLong("expectedRuntime");
            syncOperation.retries = persistableBundle.getInt("retries");
            syncOperation.scheduleEjAsRegularJob = persistableBundle.getBoolean("ejDowngradedToRegular");
            return syncOperation;
        }
        return null;
    }

    public boolean isConflict(SyncOperation syncOperation) {
        SyncStorageEngine.EndPoint endPoint = syncOperation.target;
        if (this.target.account.type.equals(endPoint.account.type) && this.target.provider.equals(endPoint.provider)) {
            SyncStorageEngine.EndPoint endPoint2 = this.target;
            if (endPoint2.userId == endPoint.userId && (!this.allowParallelSyncs || endPoint2.account.name.equals(endPoint.account.name))) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesPeriodicOperation(SyncOperation syncOperation) {
        return this.target.matchesSpec(syncOperation.target) && SyncManager.syncExtrasEquals(this.mImmutableExtras, syncOperation.mImmutableExtras, true) && this.periodMillis == syncOperation.periodMillis && this.flexMillis == syncOperation.flexMillis;
    }

    public boolean isDerivedFromFailedPeriodicSync() {
        return this.sourcePeriodicId != -1;
    }

    public int getJobBias() {
        if (isInitialization()) {
            return 20;
        }
        return isExpedited() ? 10 : 0;
    }

    public final String toKey() {
        Bundle bundle = this.mImmutableExtras;
        StringBuilder sb = new StringBuilder();
        sb.append("provider: ");
        sb.append(this.target.provider);
        sb.append(" account {name=" + this.target.account.name + ", user=" + this.target.userId + ", type=" + this.target.account.type + "}");
        sb.append(" isPeriodic: ");
        sb.append(this.isPeriodic);
        sb.append(" period: ");
        sb.append(this.periodMillis);
        sb.append(" flex: ");
        sb.append(this.flexMillis);
        sb.append(" extras: ");
        extrasToStringBuilder(bundle, sb);
        return sb.toString();
    }

    public String toString() {
        return dump(null, true, null, false);
    }

    public String toSafeString() {
        return dump(null, true, null, true);
    }

    public String dump(PackageManager packageManager, boolean z, SyncAdapterStateFetcher syncAdapterStateFetcher, boolean z2) {
        Bundle bundle = this.mImmutableExtras;
        StringBuilder sb = new StringBuilder();
        sb.append("JobId=");
        sb.append(this.jobId);
        sb.append(" ");
        sb.append(z2 ? "***" : this.target.account.name);
        sb.append("/");
        sb.append(this.target.account.type);
        sb.append(" u");
        sb.append(this.target.userId);
        sb.append(" [");
        sb.append(this.target.provider);
        sb.append("] ");
        sb.append(SyncStorageEngine.SOURCES[this.syncSource]);
        if (this.expectedRuntime != 0) {
            sb.append(" ExpectedIn=");
            SyncManager.formatDurationHMS(sb, this.expectedRuntime - SystemClock.elapsedRealtime());
        }
        if (bundle.getBoolean("expedited", false)) {
            sb.append(" EXPEDITED");
        }
        if (bundle.getBoolean("schedule_as_expedited_job", false)) {
            sb.append(" EXPEDITED-JOB");
            if (this.scheduleEjAsRegularJob) {
                sb.append("(scheduled-as-regular)");
            }
        }
        int i = this.syncExemptionFlag;
        if (i != 0) {
            if (i == 1) {
                sb.append(" STANDBY-EXEMPTED");
            } else if (i == 2) {
                sb.append(" STANDBY-EXEMPTED(TOP)");
            } else {
                sb.append(" ExemptionFlag=" + this.syncExemptionFlag);
            }
        }
        sb.append(" Reason=");
        sb.append(reasonToString(packageManager, this.reason));
        if (this.isPeriodic) {
            sb.append(" (period=");
            SyncManager.formatDurationHMS(sb, this.periodMillis);
            sb.append(" flex=");
            SyncManager.formatDurationHMS(sb, this.flexMillis);
            sb.append(")");
        }
        if (this.retries > 0) {
            sb.append(" Retries=");
            sb.append(this.retries);
        }
        if (!z) {
            sb.append(" Owner={");
            UserHandle.formatUid(sb, this.owningUid);
            sb.append(" ");
            sb.append(this.owningPackage);
            if (syncAdapterStateFetcher != null) {
                sb.append(" [");
                sb.append(syncAdapterStateFetcher.getStandbyBucket(UserHandle.getUserId(this.owningUid), this.owningPackage));
                sb.append("]");
                if (syncAdapterStateFetcher.isAppActive(this.owningUid)) {
                    sb.append(" [ACTIVE]");
                }
            }
            sb.append("}");
            if (!bundle.keySet().isEmpty()) {
                sb.append(" ");
                extrasToStringBuilder(bundle, sb);
            }
        }
        return sb.toString();
    }

    public static String reasonToString(PackageManager packageManager, int i) {
        if (i < 0) {
            int i2 = (-i) - 1;
            String[] strArr = REASON_NAMES;
            if (i2 >= strArr.length) {
                return String.valueOf(i);
            }
            return strArr[i2];
        } else if (packageManager != null) {
            String[] packagesForUid = packageManager.getPackagesForUid(i);
            if (packagesForUid != null && packagesForUid.length == 1) {
                return packagesForUid[0];
            }
            String nameForUid = packageManager.getNameForUid(i);
            return nameForUid != null ? nameForUid : String.valueOf(i);
        } else {
            return String.valueOf(i);
        }
    }

    public boolean isInitialization() {
        return this.mImmutableExtras.getBoolean("initialize", false);
    }

    public boolean isExpedited() {
        return this.mImmutableExtras.getBoolean("expedited", false);
    }

    public boolean isUpload() {
        return this.mImmutableExtras.getBoolean("upload", false);
    }

    public void enableTwoWaySync() {
        removeExtra("upload");
    }

    public boolean hasIgnoreBackoff() {
        return this.mImmutableExtras.getBoolean("ignore_backoff", false);
    }

    public void enableBackoff() {
        removeExtra("ignore_backoff");
    }

    public boolean hasDoNotRetry() {
        return this.mImmutableExtras.getBoolean("do_not_retry", false);
    }

    public boolean isNotAllowedOnMetered() {
        return this.mImmutableExtras.getBoolean("allow_metered", false);
    }

    public boolean isIgnoreSettings() {
        return this.mImmutableExtras.getBoolean("ignore_settings", false);
    }

    public boolean hasRequireCharging() {
        return this.mImmutableExtras.getBoolean("require_charging", false);
    }

    public boolean isScheduledAsExpeditedJob() {
        return this.mImmutableExtras.getBoolean("schedule_as_expedited_job", false);
    }

    public boolean isAppStandbyExempted() {
        return this.syncExemptionFlag != 0;
    }

    public boolean areExtrasEqual(Bundle bundle, boolean z) {
        return SyncManager.syncExtrasEquals(this.mImmutableExtras, bundle, z);
    }

    public static void extrasToStringBuilder(Bundle bundle, StringBuilder sb) {
        if (bundle == null) {
            sb.append("null");
            return;
        }
        sb.append("[");
        for (String str : bundle.keySet()) {
            sb.append(str);
            sb.append("=");
            sb.append(bundle.get(str));
            sb.append(" ");
        }
        sb.append("]");
    }

    public static String extrasToString(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        extrasToStringBuilder(bundle, sb);
        return sb.toString();
    }

    public String wakeLockName() {
        String str = this.wakeLockName;
        if (str != null) {
            return str;
        }
        String str2 = this.target.provider + "/" + this.target.account.type + "/" + this.target.account.name;
        this.wakeLockName = str2;
        return str2;
    }

    public Object[] toEventLog(int i) {
        Integer valueOf = Integer.valueOf(i);
        Integer valueOf2 = Integer.valueOf(this.syncSource);
        SyncStorageEngine.EndPoint endPoint = this.target;
        return new Object[]{endPoint.provider, valueOf, valueOf2, Integer.valueOf(endPoint.account.name.hashCode())};
    }

    public final void removeExtra(String str) {
        Bundle bundle = this.mImmutableExtras;
        if (bundle.containsKey(str)) {
            Bundle bundle2 = new Bundle(bundle);
            bundle2.remove(str);
            this.mImmutableExtras = bundle2;
        }
    }

    public Bundle getClonedExtras() {
        return new Bundle(this.mImmutableExtras);
    }

    public String getExtrasAsString() {
        return extrasToString(this.mImmutableExtras);
    }
}
