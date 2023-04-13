package com.android.server.job.controllers;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkPolicyManager;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.telephony.CellSignalStrength;
import android.telephony.SignalStrength;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Pools;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.AppSchedulingModuleThread;
import com.android.server.LocalServices;
import com.android.server.job.JobSchedulerService;
import com.android.server.net.NetworkPolicyManagerInternal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class ConnectivityController extends RestrictingController implements ConnectivityManager.OnNetworkActiveListener {
    public static final boolean DEBUG;
    @GuardedBy({"mLock"})
    public final ArrayMap<Network, NetworkCapabilities> mAvailableNetworks;
    public final ConnectivityManager mConnManager;
    public final SparseArray<UidDefaultNetworkCallback> mCurrentDefaultNetworkCallbacks;
    public final Pools.Pool<UidDefaultNetworkCallback> mDefaultNetworkCallbackPool;
    public final FlexibilityController mFlexibilityController;
    public final Handler mHandler;
    @GuardedBy({"mLock"})
    public long mLastAllJobUpdateTimeElapsed;
    @GuardedBy({"mLock"})
    public long mLastCallbackAdjustmentTimeElapsed;
    public final NetworkPolicyManagerInternal mNetPolicyManagerInternal;
    public final ConnectivityManager.NetworkCallback mNetworkCallback;
    @GuardedBy({"mLock"})
    public final SparseArray<ArraySet<JobStatus>> mRequestedWhitelistJobs;
    @GuardedBy({"mLock"})
    public final SparseArray<CellSignalStrengthCallback> mSignalStrengths;
    public final List<UidStats> mSortedStats;
    @GuardedBy({"mLock"})
    public final SparseArray<ArraySet<JobStatus>> mTrackedJobs;
    public final SparseArray<UidStats> mUidStats;
    public final Comparator<UidStats> mUidStatsComparator;

    static {
        DEBUG = JobSchedulerService.DEBUG || Log.isLoggable("JobScheduler.Connectivity", 3);
    }

    public ConnectivityController(JobSchedulerService jobSchedulerService, FlexibilityController flexibilityController) {
        super(jobSchedulerService);
        this.mTrackedJobs = new SparseArray<>();
        this.mRequestedWhitelistJobs = new SparseArray<>();
        this.mAvailableNetworks = new ArrayMap<>();
        this.mCurrentDefaultNetworkCallbacks = new SparseArray<>();
        this.mUidStatsComparator = new Comparator<UidStats>() { // from class: com.android.server.job.controllers.ConnectivityController.1
            public final int prioritizeExistenceOver(int i, int i2, int i3) {
                if (i2 <= i || i3 <= i) {
                    if (i2 > i || i3 > i) {
                        return i2 > i ? -1 : 1;
                    }
                    return 0;
                }
                return 0;
            }

            @Override // java.util.Comparator
            public int compare(UidStats uidStats, UidStats uidStats2) {
                int prioritizeExistenceOver = prioritizeExistenceOver(0, uidStats.runningJobs.size(), uidStats2.runningJobs.size());
                if (prioritizeExistenceOver != 0) {
                    return prioritizeExistenceOver;
                }
                int prioritizeExistenceOver2 = prioritizeExistenceOver(0, uidStats.numReadyWithConnectivity, uidStats2.numReadyWithConnectivity);
                if (prioritizeExistenceOver2 != 0) {
                    return prioritizeExistenceOver2;
                }
                int prioritizeExistenceOver3 = prioritizeExistenceOver(0, uidStats.numRequestedNetworkAvailable, uidStats2.numRequestedNetworkAvailable);
                if (prioritizeExistenceOver3 != 0) {
                    return prioritizeExistenceOver3;
                }
                int prioritizeExistenceOver4 = prioritizeExistenceOver(39, uidStats.baseBias, uidStats2.baseBias);
                if (prioritizeExistenceOver4 != 0) {
                    return prioritizeExistenceOver4;
                }
                int prioritizeExistenceOver5 = prioritizeExistenceOver(0, uidStats.numUIJs, uidStats2.numUIJs);
                if (prioritizeExistenceOver5 != 0) {
                    return prioritizeExistenceOver5;
                }
                int prioritizeExistenceOver6 = prioritizeExistenceOver(0, uidStats.numEJs, uidStats2.numEJs);
                if (prioritizeExistenceOver6 != 0) {
                    return prioritizeExistenceOver6;
                }
                int prioritizeExistenceOver7 = prioritizeExistenceOver(34, uidStats.baseBias, uidStats2.baseBias);
                if (prioritizeExistenceOver7 != 0) {
                    return prioritizeExistenceOver7;
                }
                long j = uidStats.earliestUIJEnqueueTime;
                long j2 = uidStats2.earliestUIJEnqueueTime;
                if (j < j2) {
                    return -1;
                }
                if (j > j2) {
                    return 1;
                }
                long j3 = uidStats.earliestEJEnqueueTime;
                long j4 = uidStats2.earliestEJEnqueueTime;
                if (j3 < j4) {
                    return -1;
                }
                if (j3 > j4) {
                    return 1;
                }
                int i = uidStats.baseBias;
                int i2 = uidStats2.baseBias;
                if (i != i2) {
                    return i2 - i;
                }
                long j5 = uidStats.earliestEnqueueTime;
                long j6 = uidStats2.earliestEnqueueTime;
                if (j5 < j6) {
                    return -1;
                }
                return j5 > j6 ? 1 : 0;
            }
        };
        this.mUidStats = new SparseArray<>();
        this.mDefaultNetworkCallbackPool = new Pools.SimplePool(125);
        this.mSortedStats = new ArrayList();
        this.mSignalStrengths = new SparseArray<>();
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.server.job.controllers.ConnectivityController.2
            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onAvailable(Network network) {
                if (ConnectivityController.DEBUG) {
                    Slog.v("JobScheduler.Connectivity", "onAvailable: " + network);
                }
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                if (ConnectivityController.DEBUG) {
                    Slog.v("JobScheduler.Connectivity", "onCapabilitiesChanged: " + network);
                }
                synchronized (ConnectivityController.this.mLock) {
                    NetworkCapabilities networkCapabilities2 = (NetworkCapabilities) ConnectivityController.this.mAvailableNetworks.put(network, networkCapabilities);
                    if (networkCapabilities2 != null) {
                        maybeUnregisterSignalStrengthCallbackLocked(networkCapabilities2);
                    }
                    maybeRegisterSignalStrengthCallbackLocked(networkCapabilities);
                    ConnectivityController.this.updateTrackedJobsLocked(-1, network);
                    ConnectivityController.this.postAdjustCallbacks();
                }
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network) {
                if (ConnectivityController.DEBUG) {
                    Slog.v("JobScheduler.Connectivity", "onLost: " + network);
                }
                synchronized (ConnectivityController.this.mLock) {
                    NetworkCapabilities networkCapabilities = (NetworkCapabilities) ConnectivityController.this.mAvailableNetworks.remove(network);
                    if (networkCapabilities != null) {
                        maybeUnregisterSignalStrengthCallbackLocked(networkCapabilities);
                    }
                    for (int i = 0; i < ConnectivityController.this.mCurrentDefaultNetworkCallbacks.size(); i++) {
                        UidDefaultNetworkCallback uidDefaultNetworkCallback = (UidDefaultNetworkCallback) ConnectivityController.this.mCurrentDefaultNetworkCallbacks.valueAt(i);
                        if (Objects.equals(uidDefaultNetworkCallback.mDefaultNetwork, network)) {
                            uidDefaultNetworkCallback.mDefaultNetwork = null;
                        }
                    }
                    ConnectivityController.this.updateTrackedJobsLocked(-1, network);
                    ConnectivityController.this.postAdjustCallbacks();
                }
            }

            @GuardedBy({"mLock"})
            public final void maybeRegisterSignalStrengthCallbackLocked(NetworkCapabilities networkCapabilities) {
                if (networkCapabilities.hasTransport(0)) {
                    TelephonyManager telephonyManager = (TelephonyManager) ConnectivityController.this.mContext.getSystemService(TelephonyManager.class);
                    for (Integer num : networkCapabilities.getSubscriptionIds()) {
                        int intValue = num.intValue();
                        if (ConnectivityController.this.mSignalStrengths.indexOfKey(intValue) < 0) {
                            TelephonyManager createForSubscriptionId = telephonyManager.createForSubscriptionId(intValue);
                            CellSignalStrengthCallback cellSignalStrengthCallback = new CellSignalStrengthCallback();
                            createForSubscriptionId.registerTelephonyCallback(AppSchedulingModuleThread.getExecutor(), cellSignalStrengthCallback);
                            ConnectivityController.this.mSignalStrengths.put(intValue, cellSignalStrengthCallback);
                            SignalStrength signalStrength = createForSubscriptionId.getSignalStrength();
                            if (signalStrength != null) {
                                cellSignalStrengthCallback.signalStrength = signalStrength.getLevel();
                            }
                        }
                    }
                }
            }

            @GuardedBy({"mLock"})
            public final void maybeUnregisterSignalStrengthCallbackLocked(NetworkCapabilities networkCapabilities) {
                if (networkCapabilities.hasTransport(0)) {
                    ArraySet arraySet = new ArraySet();
                    int size = ConnectivityController.this.mAvailableNetworks.size();
                    for (int i = 0; i < size; i++) {
                        NetworkCapabilities networkCapabilities2 = (NetworkCapabilities) ConnectivityController.this.mAvailableNetworks.valueAt(i);
                        if (networkCapabilities2.hasTransport(0)) {
                            arraySet.addAll(networkCapabilities2.getSubscriptionIds());
                        }
                    }
                    if (ConnectivityController.DEBUG) {
                        Slog.d("JobScheduler.Connectivity", "Active subscription IDs: " + arraySet);
                    }
                    TelephonyManager telephonyManager = (TelephonyManager) ConnectivityController.this.mContext.getSystemService(TelephonyManager.class);
                    for (Integer num : networkCapabilities.getSubscriptionIds()) {
                        int intValue = num.intValue();
                        if (!arraySet.contains(Integer.valueOf(intValue))) {
                            TelephonyManager createForSubscriptionId = telephonyManager.createForSubscriptionId(intValue);
                            CellSignalStrengthCallback cellSignalStrengthCallback = (CellSignalStrengthCallback) ConnectivityController.this.mSignalStrengths.removeReturnOld(intValue);
                            if (cellSignalStrengthCallback != null) {
                                createForSubscriptionId.unregisterTelephonyCallback(cellSignalStrengthCallback);
                            } else {
                                Slog.wtf("JobScheduler.Connectivity", "Callback for sub " + intValue + " didn't exist?!?!");
                            }
                        }
                    }
                }
            }
        };
        this.mNetworkCallback = networkCallback;
        this.mHandler = new CcHandler(this.mContext.getMainLooper());
        ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class);
        this.mConnManager = connectivityManager;
        this.mNetPolicyManagerInternal = (NetworkPolicyManagerInternal) LocalServices.getService(NetworkPolicyManagerInternal.class);
        this.mFlexibilityController = flexibilityController;
        connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().clearCapabilities().build(), networkCallback);
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void maybeStartTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        if (jobStatus.hasConnectivityConstraint()) {
            UidStats uidStats = getUidStats(jobStatus.getSourceUid(), jobStatus.getSourcePackageName(), false);
            if (wouldBeReadyWithConstraintLocked(jobStatus, 268435456)) {
                uidStats.numReadyWithConnectivity++;
            }
            ArraySet<JobStatus> arraySet = this.mTrackedJobs.get(jobStatus.getSourceUid());
            if (arraySet == null) {
                arraySet = new ArraySet<>();
                this.mTrackedJobs.put(jobStatus.getSourceUid(), arraySet);
            }
            arraySet.add(jobStatus);
            jobStatus.setTrackingController(2);
            updateConstraintsSatisfied(jobStatus);
        }
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void prepareForExecutionLocked(JobStatus jobStatus) {
        if (jobStatus.hasConnectivityConstraint()) {
            getUidStats(jobStatus.getSourceUid(), jobStatus.getSourcePackageName(), true).runningJobs.add(jobStatus);
        }
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void unprepareFromExecutionLocked(JobStatus jobStatus) {
        if (jobStatus.hasConnectivityConstraint()) {
            getUidStats(jobStatus.getSourceUid(), jobStatus.getSourcePackageName(), true).runningJobs.remove(jobStatus);
            postAdjustCallbacks();
        }
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void maybeStopTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        if (jobStatus.clearTrackingController(2)) {
            ArraySet<JobStatus> arraySet = this.mTrackedJobs.get(jobStatus.getSourceUid());
            if (arraySet != null) {
                arraySet.remove(jobStatus);
            }
            UidStats uidStats = getUidStats(jobStatus.getSourceUid(), jobStatus.getSourcePackageName(), true);
            uidStats.numReadyWithConnectivity--;
            uidStats.runningJobs.remove(jobStatus);
            maybeRevokeStandbyExceptionLocked(jobStatus);
            postAdjustCallbacks();
        }
    }

    @Override // com.android.server.job.controllers.RestrictingController
    public void startTrackingRestrictedJobLocked(JobStatus jobStatus) {
        if (jobStatus.hasConnectivityConstraint()) {
            updateConstraintsSatisfied(jobStatus);
        }
    }

    @Override // com.android.server.job.controllers.RestrictingController
    public void stopTrackingRestrictedJobLocked(JobStatus jobStatus) {
        if (jobStatus.hasConnectivityConstraint()) {
            updateConstraintsSatisfied(jobStatus);
        }
    }

    public final UidStats getUidStats(int i, String str, boolean z) {
        UidStats uidStats = this.mUidStats.get(i);
        if (uidStats == null) {
            if (z) {
                Slog.wtfStack("JobScheduler.Connectivity", "UidStats was null after job for " + str + " was registered");
            }
            UidStats uidStats2 = new UidStats(i);
            this.mUidStats.append(i, uidStats2);
            return uidStats2;
        }
        return uidStats;
    }

    public boolean isNetworkAvailable(JobStatus jobStatus) {
        synchronized (this.mLock) {
            for (int i = 0; i < this.mAvailableNetworks.size(); i++) {
                Network keyAt = this.mAvailableNetworks.keyAt(i);
                NetworkCapabilities valueAt = this.mAvailableNetworks.valueAt(i);
                boolean isSatisfied = isSatisfied(jobStatus, keyAt, valueAt, this.mConstants);
                if (DEBUG) {
                    Slog.v("JobScheduler.Connectivity", "isNetworkAvailable(" + jobStatus + ") with network " + keyAt + " and capabilities " + valueAt + ". Satisfied=" + isSatisfied);
                }
                if (isSatisfied) {
                    return true;
                }
            }
            return false;
        }
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public void requestStandbyExceptionLocked(JobStatus jobStatus) {
        int sourceUid = jobStatus.getSourceUid();
        boolean isStandbyExceptionRequestedLocked = isStandbyExceptionRequestedLocked(sourceUid);
        ArraySet<JobStatus> arraySet = this.mRequestedWhitelistJobs.get(sourceUid);
        if (arraySet == null) {
            arraySet = new ArraySet<>();
            this.mRequestedWhitelistJobs.put(sourceUid, arraySet);
        }
        if (!arraySet.add(jobStatus) || isStandbyExceptionRequestedLocked) {
            if (DEBUG) {
                Slog.i("JobScheduler.Connectivity", "requestStandbyExceptionLocked found exception already requested.");
                return;
            }
            return;
        }
        if (DEBUG) {
            Slog.i("JobScheduler.Connectivity", "Requesting standby exception for UID: " + sourceUid);
        }
        this.mNetPolicyManagerInternal.setAppIdleWhitelist(sourceUid, true);
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public boolean isStandbyExceptionRequestedLocked(int i) {
        ArraySet<JobStatus> arraySet = this.mRequestedWhitelistJobs.get(i);
        return arraySet != null && arraySet.size() > 0;
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void evaluateStateLocked(JobStatus jobStatus) {
        if (jobStatus.hasConnectivityConstraint()) {
            UidStats uidStats = getUidStats(jobStatus.getSourceUid(), jobStatus.getSourcePackageName(), true);
            if (jobStatus.shouldTreatAsExpeditedJob() || jobStatus.shouldTreatAsUserInitiatedJob()) {
                if (!jobStatus.isConstraintSatisfied(268435456)) {
                    updateConstraintsSatisfied(jobStatus);
                }
            } else if (((jobStatus.isRequestedExpeditedJob() && !jobStatus.shouldTreatAsExpeditedJob()) || (jobStatus.getJob().isUserInitiated() && !jobStatus.shouldTreatAsUserInitiatedJob())) && jobStatus.isConstraintSatisfied(268435456)) {
                updateConstraintsSatisfied(jobStatus);
            }
            if (wouldBeReadyWithConstraintLocked(jobStatus, 268435456) && isNetworkAvailable(jobStatus)) {
                if (DEBUG) {
                    Slog.i("JobScheduler.Connectivity", "evaluateStateLocked finds job " + jobStatus + " would be ready.");
                }
                uidStats.numReadyWithConnectivity++;
                requestStandbyExceptionLocked(jobStatus);
                return;
            }
            if (DEBUG) {
                Slog.i("JobScheduler.Connectivity", "evaluateStateLocked finds job " + jobStatus + " would not be ready.");
            }
            maybeRevokeStandbyExceptionLocked(jobStatus);
        }
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void reevaluateStateLocked(int i) {
        ArraySet<JobStatus> arraySet = this.mTrackedJobs.get(i);
        if (arraySet == null) {
            return;
        }
        for (int size = arraySet.size() - 1; size >= 0; size--) {
            evaluateStateLocked(arraySet.valueAt(size));
        }
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public void maybeRevokeStandbyExceptionLocked(JobStatus jobStatus) {
        int sourceUid = jobStatus.getSourceUid();
        if (isStandbyExceptionRequestedLocked(sourceUid)) {
            ArraySet<JobStatus> arraySet = this.mRequestedWhitelistJobs.get(sourceUid);
            if (arraySet == null) {
                Slog.wtf("JobScheduler.Connectivity", "maybeRevokeStandbyExceptionLocked found null jobs array even though a standby exception has been requested.");
            } else if (!arraySet.remove(jobStatus) || arraySet.size() > 0) {
                if (DEBUG) {
                    Slog.i("JobScheduler.Connectivity", "maybeRevokeStandbyExceptionLocked not revoking because there are still " + arraySet.size() + " jobs left.");
                }
            } else {
                revokeStandbyExceptionLocked(sourceUid);
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void revokeStandbyExceptionLocked(int i) {
        if (DEBUG) {
            Slog.i("JobScheduler.Connectivity", "Revoking standby exception for UID: " + i);
        }
        this.mNetPolicyManagerInternal.setAppIdleWhitelist(i, false);
        this.mRequestedWhitelistJobs.remove(i);
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void onAppRemovedLocked(String str, int i) {
        if (this.mService.getPackagesForUidLocked(i) == null) {
            this.mTrackedJobs.delete(i);
            unregisterDefaultNetworkCallbackLocked(i, JobSchedulerService.sElapsedRealtimeClock.millis());
            this.mSortedStats.remove((UidStats) this.mUidStats.removeReturnOld(i));
            registerPendingUidCallbacksLocked();
        }
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void onUserRemovedLocked(int i) {
        long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
        for (int size = this.mUidStats.size() - 1; size >= 0; size--) {
            UidStats valueAt = this.mUidStats.valueAt(size);
            if (UserHandle.getUserId(valueAt.uid) == i) {
                unregisterDefaultNetworkCallbackLocked(valueAt.uid, millis);
                this.mSortedStats.remove(valueAt);
                this.mUidStats.removeAt(size);
            }
        }
        postAdjustCallbacks();
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void onUidBiasChangedLocked(int i, int i2, int i3) {
        UidStats uidStats = this.mUidStats.get(i);
        if (uidStats == null || uidStats.baseBias == i3) {
            return;
        }
        uidStats.baseBias = i3;
        postAdjustCallbacks();
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void onBatteryStateChangedLocked() {
        this.mHandler.sendEmptyMessage(1);
    }

    public final boolean isUsable(NetworkCapabilities networkCapabilities) {
        return networkCapabilities != null && networkCapabilities.hasCapability(21);
    }

    public final boolean isInsane(JobStatus jobStatus, Network network, NetworkCapabilities networkCapabilities, JobSchedulerService.Constants constants) {
        long maxJobExecutionTimeMs = this.mService.getMaxJobExecutionTimeMs(jobStatus);
        long minimumNetworkChunkBytes = jobStatus.getMinimumNetworkChunkBytes();
        if (minimumNetworkChunkBytes != -1) {
            long linkDownstreamBandwidthKbps = networkCapabilities.getLinkDownstreamBandwidthKbps();
            if (linkDownstreamBandwidthKbps > 0) {
                long calculateTransferTimeMs = calculateTransferTimeMs(minimumNetworkChunkBytes, linkDownstreamBandwidthKbps);
                if (calculateTransferTimeMs > maxJobExecutionTimeMs) {
                    Slog.w("JobScheduler.Connectivity", "Minimum chunk " + minimumNetworkChunkBytes + " bytes over " + linkDownstreamBandwidthKbps + " kbps network would take " + calculateTransferTimeMs + "ms and job has " + maxJobExecutionTimeMs + "ms to run; that's insane!");
                    return true;
                }
            }
            long linkUpstreamBandwidthKbps = networkCapabilities.getLinkUpstreamBandwidthKbps();
            if (linkUpstreamBandwidthKbps > 0) {
                long calculateTransferTimeMs2 = calculateTransferTimeMs(minimumNetworkChunkBytes, linkUpstreamBandwidthKbps);
                if (calculateTransferTimeMs2 > maxJobExecutionTimeMs) {
                    Slog.w("JobScheduler.Connectivity", "Minimum chunk " + minimumNetworkChunkBytes + " bytes over " + linkUpstreamBandwidthKbps + " kbps network would take " + calculateTransferTimeMs2 + "ms and job has " + maxJobExecutionTimeMs + "ms to run; that's insane!");
                    return true;
                }
                return false;
            }
            return false;
        } else if (networkCapabilities.hasCapability(11) && this.mService.isBatteryCharging()) {
            return false;
        } else {
            long estimatedNetworkDownloadBytes = jobStatus.getEstimatedNetworkDownloadBytes();
            if (estimatedNetworkDownloadBytes != -1) {
                long linkDownstreamBandwidthKbps2 = networkCapabilities.getLinkDownstreamBandwidthKbps();
                if (linkDownstreamBandwidthKbps2 > 0) {
                    long calculateTransferTimeMs3 = calculateTransferTimeMs(estimatedNetworkDownloadBytes, linkDownstreamBandwidthKbps2);
                    if (calculateTransferTimeMs3 > maxJobExecutionTimeMs) {
                        Slog.w("JobScheduler.Connectivity", "Estimated " + estimatedNetworkDownloadBytes + " download bytes over " + linkDownstreamBandwidthKbps2 + " kbps network would take " + calculateTransferTimeMs3 + "ms and job has " + maxJobExecutionTimeMs + "ms to run; that's insane!");
                        return true;
                    }
                }
            }
            long estimatedNetworkUploadBytes = jobStatus.getEstimatedNetworkUploadBytes();
            if (estimatedNetworkUploadBytes != -1) {
                long linkUpstreamBandwidthKbps2 = networkCapabilities.getLinkUpstreamBandwidthKbps();
                if (linkUpstreamBandwidthKbps2 > 0) {
                    long calculateTransferTimeMs4 = calculateTransferTimeMs(estimatedNetworkUploadBytes, linkUpstreamBandwidthKbps2);
                    if (calculateTransferTimeMs4 > maxJobExecutionTimeMs) {
                        Slog.w("JobScheduler.Connectivity", "Estimated " + estimatedNetworkUploadBytes + " upload bytes over " + linkUpstreamBandwidthKbps2 + " kbps network would take " + calculateTransferTimeMs4 + "ms and job has " + maxJobExecutionTimeMs + "ms to run; that's insane!");
                        return true;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
    }

    public long getEstimatedTransferTimeMs(JobStatus jobStatus) {
        Network network;
        NetworkCapabilities networkCapabilities;
        long estimatedNetworkDownloadBytes = jobStatus.getEstimatedNetworkDownloadBytes();
        long estimatedNetworkUploadBytes = jobStatus.getEstimatedNetworkUploadBytes();
        if ((estimatedNetworkDownloadBytes == -1 && estimatedNetworkUploadBytes == -1) || (network = jobStatus.network) == null || (networkCapabilities = getNetworkCapabilities(network)) == null) {
            return -1L;
        }
        long calculateTransferTimeMs = calculateTransferTimeMs(estimatedNetworkDownloadBytes, networkCapabilities.getLinkDownstreamBandwidthKbps());
        long calculateTransferTimeMs2 = calculateTransferTimeMs(estimatedNetworkUploadBytes, networkCapabilities.getLinkUpstreamBandwidthKbps());
        return calculateTransferTimeMs == -1 ? calculateTransferTimeMs2 : calculateTransferTimeMs2 == -1 ? calculateTransferTimeMs : calculateTransferTimeMs + calculateTransferTimeMs2;
    }

    @VisibleForTesting
    public static long calculateTransferTimeMs(long j, long j2) {
        if (j == -1 || j2 <= 0) {
            return -1L;
        }
        return (j * 1000) / ((j2 * 1000) / 8);
    }

    public static boolean isCongestionDelayed(JobStatus jobStatus, Network network, NetworkCapabilities networkCapabilities, JobSchedulerService.Constants constants) {
        return !networkCapabilities.hasCapability(20) && jobStatus.getFractionRunTime() < constants.CONN_CONGESTION_DELAY_FRAC;
    }

    @GuardedBy({"mLock"})
    public final boolean isStrongEnough(JobStatus jobStatus, NetworkCapabilities networkCapabilities, JobSchedulerService.Constants constants) {
        int effectivePriority = jobStatus.getEffectivePriority();
        if (effectivePriority < 400 && constants.CONN_USE_CELL_SIGNAL_STRENGTH && networkCapabilities.hasTransport(0) && !networkCapabilities.hasTransport(4)) {
            int i = 0;
            for (Integer num : networkCapabilities.getSubscriptionIds()) {
                int intValue = num.intValue();
                CellSignalStrengthCallback cellSignalStrengthCallback = this.mSignalStrengths.get(intValue);
                if (cellSignalStrengthCallback != null) {
                    i = Math.max(i, cellSignalStrengthCallback.signalStrength);
                } else {
                    Slog.wtf("JobScheduler.Connectivity", "Subscription ID " + intValue + " doesn't have a registered callback");
                }
            }
            if (DEBUG) {
                Slog.d("JobScheduler.Connectivity", "Cell signal strength for job=" + i);
            }
            if (i <= 1) {
                if (effectivePriority > 300) {
                    return true;
                }
                if (effectivePriority < 300) {
                    return false;
                }
                return (this.mService.isBatteryCharging() && this.mService.isBatteryNotLow()) || jobStatus.getFractionRunTime() > constants.CONN_PREFETCH_RELAX_FRAC;
            } else if (i > 2 || effectivePriority >= 200) {
                return true;
            } else {
                if (this.mService.isBatteryCharging() && this.mService.isBatteryNotLow()) {
                    return true;
                }
                return getUidStats(jobStatus.getSourceUid(), jobStatus.getSourcePackageName(), true).runningJobs.contains(jobStatus);
            }
        }
        return true;
    }

    public static NetworkCapabilities.Builder copyCapabilities(NetworkRequest networkRequest) {
        NetworkCapabilities.Builder builder = new NetworkCapabilities.Builder();
        for (int i : networkRequest.getTransportTypes()) {
            builder.addTransportType(i);
        }
        for (int i2 : networkRequest.getCapabilities()) {
            builder.addCapability(i2);
        }
        return builder;
    }

    public static boolean isStrictSatisfied(JobStatus jobStatus, Network network, NetworkCapabilities networkCapabilities, JobSchedulerService.Constants constants) {
        if (jobStatus.getEffectiveStandbyBucket() == 5 && (!jobStatus.isConstraintSatisfied(16777216) || !jobStatus.isConstraintSatisfied(134217728))) {
            NetworkCapabilities.Builder copyCapabilities = copyCapabilities(jobStatus.getJob().getRequiredNetwork());
            copyCapabilities.addCapability(11);
            return copyCapabilities.build().satisfiedByNetworkCapabilities(networkCapabilities);
        }
        return jobStatus.getJob().getRequiredNetwork().canBeSatisfiedBy(networkCapabilities);
    }

    public final boolean isRelaxedSatisfied(JobStatus jobStatus, Network network, NetworkCapabilities networkCapabilities, JobSchedulerService.Constants constants) {
        if (!jobStatus.getJob().isPrefetch() || jobStatus.getStandbyBucket() == 5) {
            return false;
        }
        long estimatedNetworkDownloadBytes = jobStatus.getEstimatedNetworkDownloadBytes();
        if (estimatedNetworkDownloadBytes <= 0) {
            return false;
        }
        NetworkCapabilities.Builder copyCapabilities = copyCapabilities(jobStatus.getJob().getRequiredNetwork());
        copyCapabilities.removeCapability(11);
        if (!copyCapabilities.build().satisfiedByNetworkCapabilities(networkCapabilities) || jobStatus.getFractionRunTime() <= constants.CONN_PREFETCH_RELAX_FRAC) {
            return false;
        }
        long subscriptionOpportunisticQuota = this.mNetPolicyManagerInternal.getSubscriptionOpportunisticQuota(network, 1);
        long estimatedNetworkUploadBytes = jobStatus.getEstimatedNetworkUploadBytes();
        return subscriptionOpportunisticQuota >= estimatedNetworkDownloadBytes + (estimatedNetworkUploadBytes != -1 ? estimatedNetworkUploadBytes : 0L);
    }

    @VisibleForTesting
    public boolean isSatisfied(JobStatus jobStatus, Network network, NetworkCapabilities networkCapabilities, JobSchedulerService.Constants constants) {
        if (network == null || networkCapabilities == null || !isUsable(networkCapabilities) || isInsane(jobStatus, network, networkCapabilities, constants) || isCongestionDelayed(jobStatus, network, networkCapabilities, constants) || !isStrongEnough(jobStatus, networkCapabilities, constants)) {
            return false;
        }
        return isStrictSatisfied(jobStatus, network, networkCapabilities, constants) || isRelaxedSatisfied(jobStatus, network, networkCapabilities, constants);
    }

    @GuardedBy({"mLock"})
    public final void maybeRegisterDefaultNetworkCallbackLocked(JobStatus jobStatus) {
        if (this.mCurrentDefaultNetworkCallbacks.contains(jobStatus.getSourceUid())) {
            return;
        }
        UidStats uidStats = getUidStats(jobStatus.getSourceUid(), jobStatus.getSourcePackageName(), true);
        if (!this.mSortedStats.contains(uidStats)) {
            this.mSortedStats.add(uidStats);
        }
        if (this.mCurrentDefaultNetworkCallbacks.size() >= 125) {
            postAdjustCallbacks();
        } else {
            registerPendingUidCallbacksLocked();
        }
    }

    @GuardedBy({"mLock"})
    public final void registerPendingUidCallbacksLocked() {
        int size = this.mCurrentDefaultNetworkCallbacks.size();
        int size2 = this.mSortedStats.size();
        if (size2 < size) {
            Slog.wtf("JobScheduler.Connectivity", "There are more registered callbacks than sorted UIDs: " + size + " vs " + size2);
        }
        while (size < size2 && size < 125) {
            UidStats uidStats = this.mSortedStats.get(size);
            UidDefaultNetworkCallback uidDefaultNetworkCallback = (UidDefaultNetworkCallback) this.mDefaultNetworkCallbackPool.acquire();
            if (uidDefaultNetworkCallback == null) {
                uidDefaultNetworkCallback = new UidDefaultNetworkCallback();
            }
            uidDefaultNetworkCallback.setUid(uidStats.uid);
            this.mCurrentDefaultNetworkCallbacks.append(uidStats.uid, uidDefaultNetworkCallback);
            this.mConnManager.registerDefaultNetworkCallbackForUid(uidStats.uid, uidDefaultNetworkCallback, this.mHandler);
            size++;
        }
    }

    public final void postAdjustCallbacks() {
        postAdjustCallbacks(0L);
    }

    public final void postAdjustCallbacks(long j) {
        this.mHandler.sendEmptyMessageDelayed(0, j);
    }

    @GuardedBy({"mLock"})
    public final void maybeAdjustRegisteredCallbacksLocked() {
        this.mHandler.removeMessages(0);
        if (this.mUidStats.size() == this.mCurrentDefaultNetworkCallbacks.size()) {
            return;
        }
        long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
        if (millis - this.mLastCallbackAdjustmentTimeElapsed < 1000) {
            postAdjustCallbacks(1000L);
            return;
        }
        this.mLastCallbackAdjustmentTimeElapsed = millis;
        this.mSortedStats.clear();
        for (int i = 0; i < this.mUidStats.size(); i++) {
            UidStats valueAt = this.mUidStats.valueAt(i);
            ArraySet<JobStatus> arraySet = this.mTrackedJobs.get(valueAt.uid);
            if (arraySet == null || arraySet.size() == 0) {
                unregisterDefaultNetworkCallbackLocked(valueAt.uid, millis);
            } else {
                if (valueAt.lastUpdatedElapsed + 30000 < millis) {
                    valueAt.earliestEnqueueTime = Long.MAX_VALUE;
                    valueAt.earliestEJEnqueueTime = Long.MAX_VALUE;
                    valueAt.earliestUIJEnqueueTime = Long.MAX_VALUE;
                    valueAt.numReadyWithConnectivity = 0;
                    valueAt.numRequestedNetworkAvailable = 0;
                    valueAt.numRegular = 0;
                    valueAt.numEJs = 0;
                    valueAt.numUIJs = 0;
                    for (int i2 = 0; i2 < arraySet.size(); i2++) {
                        JobStatus valueAt2 = arraySet.valueAt(i2);
                        if (wouldBeReadyWithConstraintLocked(valueAt2, 268435456)) {
                            valueAt.numReadyWithConnectivity++;
                            if (isNetworkAvailable(valueAt2)) {
                                valueAt.numRequestedNetworkAvailable++;
                            }
                            valueAt.earliestEnqueueTime = Math.min(valueAt.earliestEnqueueTime, valueAt2.enqueueTime);
                            if (valueAt2.shouldTreatAsExpeditedJob() || valueAt2.startedAsExpeditedJob) {
                                valueAt.earliestEJEnqueueTime = Math.min(valueAt.earliestEJEnqueueTime, valueAt2.enqueueTime);
                            } else if (valueAt2.shouldTreatAsUserInitiatedJob()) {
                                valueAt.earliestUIJEnqueueTime = Math.min(valueAt.earliestUIJEnqueueTime, valueAt2.enqueueTime);
                            }
                        }
                        if (valueAt2.shouldTreatAsExpeditedJob() || valueAt2.startedAsExpeditedJob) {
                            valueAt.numEJs++;
                        } else if (valueAt2.shouldTreatAsUserInitiatedJob()) {
                            valueAt.numUIJs++;
                        } else {
                            valueAt.numRegular++;
                        }
                    }
                    valueAt.lastUpdatedElapsed = millis;
                }
                this.mSortedStats.add(valueAt);
            }
        }
        this.mSortedStats.sort(this.mUidStatsComparator);
        ArraySet<JobStatus> arraySet2 = new ArraySet<>();
        for (int size = this.mSortedStats.size() - 1; size >= 0; size--) {
            UidStats uidStats = this.mSortedStats.get(size);
            if (size >= 125) {
                if (unregisterDefaultNetworkCallbackLocked(uidStats.uid, millis)) {
                    arraySet2.addAll((ArraySet<? extends JobStatus>) this.mTrackedJobs.get(uidStats.uid));
                }
            } else if (this.mCurrentDefaultNetworkCallbacks.get(uidStats.uid) == null) {
                UidDefaultNetworkCallback uidDefaultNetworkCallback = (UidDefaultNetworkCallback) this.mDefaultNetworkCallbackPool.acquire();
                if (uidDefaultNetworkCallback == null) {
                    uidDefaultNetworkCallback = new UidDefaultNetworkCallback();
                }
                uidDefaultNetworkCallback.setUid(uidStats.uid);
                this.mCurrentDefaultNetworkCallbacks.append(uidStats.uid, uidDefaultNetworkCallback);
                this.mConnManager.registerDefaultNetworkCallbackForUid(uidStats.uid, uidDefaultNetworkCallback, this.mHandler);
            }
        }
        if (arraySet2.size() > 0) {
            this.mStateChangedListener.onControllerStateChanged(arraySet2);
        }
    }

    @GuardedBy({"mLock"})
    public final boolean unregisterDefaultNetworkCallbackLocked(int i, long j) {
        UidDefaultNetworkCallback uidDefaultNetworkCallback = this.mCurrentDefaultNetworkCallbacks.get(i);
        boolean z = false;
        if (uidDefaultNetworkCallback == null) {
            return false;
        }
        this.mCurrentDefaultNetworkCallbacks.remove(i);
        this.mConnManager.unregisterNetworkCallback(uidDefaultNetworkCallback);
        this.mDefaultNetworkCallbackPool.release(uidDefaultNetworkCallback);
        uidDefaultNetworkCallback.clear();
        ArraySet<JobStatus> arraySet = this.mTrackedJobs.get(i);
        if (arraySet != null) {
            for (int size = arraySet.size() - 1; size >= 0; size--) {
                z |= updateConstraintsSatisfied(arraySet.valueAt(size), j, null, null);
            }
        }
        return z;
    }

    public final NetworkCapabilities getNetworkCapabilities(Network network) {
        NetworkCapabilities networkCapabilities;
        if (network == null) {
            return null;
        }
        synchronized (this.mLock) {
            networkCapabilities = this.mAvailableNetworks.get(network);
        }
        return networkCapabilities;
    }

    @GuardedBy({"mLock"})
    public final Network getNetworkLocked(JobStatus jobStatus) {
        int i;
        UidDefaultNetworkCallback uidDefaultNetworkCallback = this.mCurrentDefaultNetworkCallbacks.get(jobStatus.getSourceUid());
        if (uidDefaultNetworkCallback == null) {
            return null;
        }
        if (this.mUidStats.get(jobStatus.getSourceUid()).baseBias >= 30 || (jobStatus.getFlags() & 1) != 0) {
            if (DEBUG) {
                Slog.d("JobScheduler.Connectivity", "Using FG bypass for " + jobStatus.getSourceUid());
            }
            i = -196616;
        } else {
            i = -8;
            if (jobStatus.shouldTreatAsUserInitiatedJob()) {
                if (DEBUG) {
                    Slog.d("JobScheduler.Connectivity", "Using UI bypass for " + jobStatus.getSourceUid());
                }
            } else if (jobStatus.shouldTreatAsExpeditedJob() || jobStatus.startedAsExpeditedJob) {
                if (DEBUG) {
                    Slog.d("JobScheduler.Connectivity", "Using EJ bypass for " + jobStatus.getSourceUid());
                }
            } else {
                if (DEBUG) {
                    Slog.d("JobScheduler.Connectivity", "Using BG bypass for " + jobStatus.getSourceUid());
                }
                i = -1;
            }
        }
        if ((uidDefaultNetworkCallback.mBlockedReasons & i) == 0) {
            return uidDefaultNetworkCallback.mDefaultNetwork;
        }
        return null;
    }

    public final boolean updateConstraintsSatisfied(JobStatus jobStatus) {
        long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
        if (this.mCurrentDefaultNetworkCallbacks.get(jobStatus.getSourceUid()) == null) {
            maybeRegisterDefaultNetworkCallbackLocked(jobStatus);
            return updateConstraintsSatisfied(jobStatus, millis, null, null);
        }
        Network networkLocked = getNetworkLocked(jobStatus);
        return updateConstraintsSatisfied(jobStatus, millis, networkLocked, getNetworkCapabilities(networkLocked));
    }

    public final boolean updateConstraintsSatisfied(JobStatus jobStatus, long j, Network network, NetworkCapabilities networkCapabilities) {
        boolean isSatisfied = isSatisfied(jobStatus, network, networkCapabilities, this.mConstants);
        boolean z = false;
        if (!isSatisfied && jobStatus.network != null && this.mService.isCurrentlyRunningLocked(jobStatus)) {
            Network network2 = jobStatus.network;
            if (isSatisfied(jobStatus, network2, getNetworkCapabilities(network2), this.mConstants)) {
                if (DEBUG) {
                    Slog.i("JobScheduler.Connectivity", "Not reassigning network for running job " + jobStatus);
                }
                return false;
            }
        }
        boolean connectivityConstraintSatisfied = jobStatus.setConnectivityConstraintSatisfied(j, isSatisfied);
        if (jobStatus.getPreferUnmetered()) {
            if (isSatisfied && networkCapabilities != null && networkCapabilities.hasCapability(11)) {
                z = true;
            }
            jobStatus.setHasAccessToUnmetered(z);
            jobStatus.setFlexibilityConstraintSatisfied(j, this.mFlexibilityController.isFlexibilitySatisfiedLocked(jobStatus));
        }
        if (!connectivityConstraintSatisfied && isSatisfied && jobStatus.network != null && this.mService.isCurrentlyRunningLocked(jobStatus)) {
            this.mStateChangedListener.onNetworkChanged(jobStatus, network);
        }
        jobStatus.network = network;
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("Connectivity ");
            sb.append(connectivityConstraintSatisfied ? "CHANGED" : "unchanged");
            sb.append(" for ");
            sb.append(jobStatus);
            sb.append(": usable=");
            sb.append(isUsable(networkCapabilities));
            sb.append(" satisfied=");
            sb.append(isSatisfied);
            Slog.i("JobScheduler.Connectivity", sb.toString());
        }
        return connectivityConstraintSatisfied;
    }

    @GuardedBy({"mLock"})
    public final void updateAllTrackedJobsLocked(boolean z) {
        if (z) {
            long millis = (this.mLastAllJobUpdateTimeElapsed + this.mConstants.CONN_UPDATE_ALL_JOBS_MIN_INTERVAL_MS) - JobSchedulerService.sElapsedRealtimeClock.millis();
            if (millis > 0) {
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, 1, 0), millis);
                return;
            }
        }
        this.mHandler.removeMessages(1);
        updateTrackedJobsLocked(-1, (Network) null);
        this.mLastAllJobUpdateTimeElapsed = JobSchedulerService.sElapsedRealtimeClock.millis();
    }

    @GuardedBy({"mLock"})
    public final void updateTrackedJobsLocked(int i, Network network) {
        ArraySet<JobStatus> arraySet;
        if (i == -1) {
            arraySet = new ArraySet<>();
            for (int size = this.mTrackedJobs.size() - 1; size >= 0; size--) {
                if (updateTrackedJobsLocked(this.mTrackedJobs.valueAt(size), network)) {
                    arraySet.addAll((ArraySet<? extends JobStatus>) this.mTrackedJobs.valueAt(size));
                }
            }
        } else {
            arraySet = updateTrackedJobsLocked(this.mTrackedJobs.get(i), network) ? this.mTrackedJobs.get(i) : null;
        }
        if (arraySet == null || arraySet.size() <= 0) {
            return;
        }
        this.mStateChangedListener.onControllerStateChanged(arraySet);
    }

    @GuardedBy({"mLock"})
    public final boolean updateTrackedJobsLocked(ArraySet<JobStatus> arraySet, Network network) {
        if (arraySet == null || arraySet.size() == 0 || this.mCurrentDefaultNetworkCallbacks.get(arraySet.valueAt(0).getSourceUid()) == null) {
            return false;
        }
        long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
        boolean z = false;
        for (int size = arraySet.size() - 1; size >= 0; size--) {
            JobStatus valueAt = arraySet.valueAt(size);
            Network networkLocked = getNetworkLocked(valueAt);
            NetworkCapabilities networkCapabilities = getNetworkCapabilities(networkLocked);
            if ((network == null || network.equals(networkLocked)) || !Objects.equals(valueAt.network, networkLocked)) {
                z |= updateConstraintsSatisfied(valueAt, millis, networkLocked, networkCapabilities);
            }
        }
        return z;
    }

    @Override // android.net.ConnectivityManager.OnNetworkActiveListener
    public void onNetworkActive() {
        synchronized (this.mLock) {
            for (int size = this.mTrackedJobs.size() - 1; size >= 0; size--) {
                ArraySet<JobStatus> valueAt = this.mTrackedJobs.valueAt(size);
                for (int size2 = valueAt.size() - 1; size2 >= 0; size2--) {
                    JobStatus valueAt2 = valueAt.valueAt(size2);
                    if (valueAt2.isReady()) {
                        if (DEBUG) {
                            Slog.d("JobScheduler.Connectivity", "Running " + valueAt2 + " due to network activity.");
                        }
                        this.mStateChangedListener.onRunJobNow(valueAt2);
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public class CcHandler extends Handler {
        public CcHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            synchronized (ConnectivityController.this.mLock) {
                int i = message.what;
                if (i != 0) {
                    boolean z = true;
                    if (i == 1) {
                        synchronized (ConnectivityController.this.mLock) {
                            if (message.arg1 != 1) {
                                z = false;
                            }
                            ConnectivityController.this.updateAllTrackedJobsLocked(z);
                        }
                    }
                } else {
                    synchronized (ConnectivityController.this.mLock) {
                        ConnectivityController.this.maybeAdjustRegisteredCallbacksLocked();
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public class UidDefaultNetworkCallback extends ConnectivityManager.NetworkCallback {
        public int mBlockedReasons;
        public Network mDefaultNetwork;
        public int mUid;

        public UidDefaultNetworkCallback() {
        }

        public final void setUid(int i) {
            this.mUid = i;
            this.mDefaultNetwork = null;
        }

        public final void clear() {
            this.mDefaultNetwork = null;
            this.mUid = -10000;
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onAvailable(Network network) {
            if (ConnectivityController.DEBUG) {
                Slog.v("JobScheduler.Connectivity", "default-onAvailable(" + this.mUid + "): " + network);
            }
        }

        public void onBlockedStatusChanged(Network network, int i) {
            if (ConnectivityController.DEBUG) {
                Slog.v("JobScheduler.Connectivity", "default-onBlockedStatusChanged(" + this.mUid + "): " + network + " -> " + i);
            }
            if (this.mUid == -10000) {
                return;
            }
            synchronized (ConnectivityController.this.mLock) {
                this.mDefaultNetwork = network;
                this.mBlockedReasons = i;
                ConnectivityController.this.updateTrackedJobsLocked(this.mUid, network);
            }
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLost(Network network) {
            if (ConnectivityController.DEBUG) {
                Slog.v("JobScheduler.Connectivity", "default-onLost(" + this.mUid + "): " + network);
            }
            if (this.mUid == -10000) {
                return;
            }
            synchronized (ConnectivityController.this.mLock) {
                if (Objects.equals(this.mDefaultNetwork, network)) {
                    this.mDefaultNetwork = null;
                    ConnectivityController.this.updateTrackedJobsLocked(this.mUid, network);
                    ConnectivityController.this.postAdjustCallbacks(1000L);
                }
            }
        }

        public final void dumpLocked(IndentingPrintWriter indentingPrintWriter) {
            indentingPrintWriter.print("UID: ");
            indentingPrintWriter.print(this.mUid);
            indentingPrintWriter.print("; ");
            if (this.mDefaultNetwork == null) {
                indentingPrintWriter.print("No network");
            } else {
                indentingPrintWriter.print("Network: ");
                indentingPrintWriter.print(this.mDefaultNetwork);
                indentingPrintWriter.print(" (blocked=");
                indentingPrintWriter.print(NetworkPolicyManager.blockedReasonsToString(this.mBlockedReasons));
                indentingPrintWriter.print(")");
            }
            indentingPrintWriter.println();
        }
    }

    /* loaded from: classes.dex */
    public static class UidStats {
        public int baseBias;
        public long earliestEJEnqueueTime;
        public long earliestEnqueueTime;
        public long earliestUIJEnqueueTime;
        public long lastUpdatedElapsed;
        public int numEJs;
        public int numReadyWithConnectivity;
        public int numRegular;
        public int numRequestedNetworkAvailable;
        public int numUIJs;
        public final ArraySet<JobStatus> runningJobs;
        public final int uid;

        public UidStats(int i) {
            this.runningJobs = new ArraySet<>();
            this.uid = i;
        }

        public final void dumpLocked(IndentingPrintWriter indentingPrintWriter, long j) {
            indentingPrintWriter.print("UidStats{");
            indentingPrintWriter.print("uid", Integer.valueOf(this.uid));
            indentingPrintWriter.print("pri", Integer.valueOf(this.baseBias));
            indentingPrintWriter.print("#run", Integer.valueOf(this.runningJobs.size()));
            indentingPrintWriter.print("#readyWithConn", Integer.valueOf(this.numReadyWithConnectivity));
            indentingPrintWriter.print("#netAvail", Integer.valueOf(this.numRequestedNetworkAvailable));
            indentingPrintWriter.print("#EJs", Integer.valueOf(this.numEJs));
            indentingPrintWriter.print("#reg", Integer.valueOf(this.numRegular));
            indentingPrintWriter.print("earliestEnqueue", Long.valueOf(this.earliestEnqueueTime));
            indentingPrintWriter.print("earliestEJEnqueue", Long.valueOf(this.earliestEJEnqueueTime));
            indentingPrintWriter.print("earliestUIJEnqueue", Long.valueOf(this.earliestUIJEnqueueTime));
            indentingPrintWriter.print("updated=");
            TimeUtils.formatDuration(this.lastUpdatedElapsed - j, indentingPrintWriter);
            indentingPrintWriter.println("}");
        }
    }

    /* loaded from: classes.dex */
    public class CellSignalStrengthCallback extends TelephonyCallback implements TelephonyCallback.SignalStrengthsListener {
        @GuardedBy({"mLock"})
        public int signalStrength;

        public CellSignalStrengthCallback() {
            this.signalStrength = 4;
        }

        @Override // android.telephony.TelephonyCallback.SignalStrengthsListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            synchronized (ConnectivityController.this.mLock) {
                int level = signalStrength.getLevel();
                if (ConnectivityController.DEBUG) {
                    Slog.d("JobScheduler.Connectivity", "Signal strength changing from " + this.signalStrength + " to " + level);
                    for (CellSignalStrength cellSignalStrength : signalStrength.getCellSignalStrengths()) {
                        Slog.d("JobScheduler.Connectivity", "CSS: " + cellSignalStrength.getLevel() + " " + cellSignalStrength);
                    }
                }
                if (this.signalStrength == level) {
                    return;
                }
                this.signalStrength = level;
                ConnectivityController.this.mHandler.obtainMessage(1, 1, 0).sendToTarget();
            }
        }
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void dumpControllerStateLocked(IndentingPrintWriter indentingPrintWriter, Predicate<JobStatus> predicate) {
        long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
        if (this.mRequestedWhitelistJobs.size() > 0) {
            indentingPrintWriter.print("Requested standby exceptions:");
            for (int i = 0; i < this.mRequestedWhitelistJobs.size(); i++) {
                indentingPrintWriter.print(" ");
                indentingPrintWriter.print(this.mRequestedWhitelistJobs.keyAt(i));
                indentingPrintWriter.print(" (");
                indentingPrintWriter.print(this.mRequestedWhitelistJobs.valueAt(i).size());
                indentingPrintWriter.print(" jobs)");
            }
            indentingPrintWriter.println();
        }
        if (this.mAvailableNetworks.size() > 0) {
            indentingPrintWriter.println("Available networks:");
            indentingPrintWriter.increaseIndent();
            for (int i2 = 0; i2 < this.mAvailableNetworks.size(); i2++) {
                indentingPrintWriter.print(this.mAvailableNetworks.keyAt(i2));
                indentingPrintWriter.print(": ");
                indentingPrintWriter.println(this.mAvailableNetworks.valueAt(i2));
            }
            indentingPrintWriter.decreaseIndent();
        } else {
            indentingPrintWriter.println("No available networks");
        }
        indentingPrintWriter.println();
        if (this.mSignalStrengths.size() > 0) {
            indentingPrintWriter.println("Subscription ID signal strengths:");
            indentingPrintWriter.increaseIndent();
            for (int i3 = 0; i3 < this.mSignalStrengths.size(); i3++) {
                indentingPrintWriter.print(this.mSignalStrengths.keyAt(i3));
                indentingPrintWriter.print(": ");
                indentingPrintWriter.println(this.mSignalStrengths.valueAt(i3).signalStrength);
            }
            indentingPrintWriter.decreaseIndent();
        } else {
            indentingPrintWriter.println("No cached signal strengths");
        }
        indentingPrintWriter.println();
        indentingPrintWriter.println("Current default network callbacks:");
        indentingPrintWriter.increaseIndent();
        for (int i4 = 0; i4 < this.mCurrentDefaultNetworkCallbacks.size(); i4++) {
            this.mCurrentDefaultNetworkCallbacks.valueAt(i4).dumpLocked(indentingPrintWriter);
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println();
        indentingPrintWriter.println("UID Pecking Order:");
        indentingPrintWriter.increaseIndent();
        for (int i5 = 0; i5 < this.mSortedStats.size(); i5++) {
            indentingPrintWriter.print(i5);
            indentingPrintWriter.print(": ");
            this.mSortedStats.get(i5).dumpLocked(indentingPrintWriter, millis);
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println();
        for (int i6 = 0; i6 < this.mTrackedJobs.size(); i6++) {
            ArraySet<JobStatus> valueAt = this.mTrackedJobs.valueAt(i6);
            for (int i7 = 0; i7 < valueAt.size(); i7++) {
                JobStatus valueAt2 = valueAt.valueAt(i7);
                if (predicate.test(valueAt2)) {
                    indentingPrintWriter.print("#");
                    valueAt2.printUniqueId(indentingPrintWriter);
                    indentingPrintWriter.print(" from ");
                    UserHandle.formatUid(indentingPrintWriter, valueAt2.getSourceUid());
                    indentingPrintWriter.print(": ");
                    indentingPrintWriter.print(valueAt2.getJob().getRequiredNetwork());
                    indentingPrintWriter.println();
                }
            }
        }
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void dumpControllerStateLocked(ProtoOutputStream protoOutputStream, long j, Predicate<JobStatus> predicate) {
        long start = protoOutputStream.start(j);
        long start2 = protoOutputStream.start(1146756268035L);
        for (int i = 0; i < this.mRequestedWhitelistJobs.size(); i++) {
            protoOutputStream.write(2220498092035L, this.mRequestedWhitelistJobs.keyAt(i));
        }
        for (int i2 = 0; i2 < this.mTrackedJobs.size(); i2++) {
            ArraySet<JobStatus> valueAt = this.mTrackedJobs.valueAt(i2);
            for (int i3 = 0; i3 < valueAt.size(); i3++) {
                JobStatus valueAt2 = valueAt.valueAt(i3);
                if (predicate.test(valueAt2)) {
                    long start3 = protoOutputStream.start(2246267895810L);
                    valueAt2.writeToShortProto(protoOutputStream, 1146756268033L);
                    protoOutputStream.write(1120986464258L, valueAt2.getSourceUid());
                    protoOutputStream.end(start3);
                }
            }
        }
        protoOutputStream.end(start2);
        protoOutputStream.end(start);
    }
}
