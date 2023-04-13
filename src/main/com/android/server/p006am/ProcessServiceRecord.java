package com.android.server.p006am;

import android.os.IBinder;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.internal.annotations.GuardedBy;
import com.android.server.p014wm.WindowProcessController;
import java.io.PrintWriter;
import java.util.ArrayList;
/* renamed from: com.android.server.am.ProcessServiceRecord */
/* loaded from: classes.dex */
public final class ProcessServiceRecord {
    public boolean mAllowlistManager;
    public final ProcessRecord mApp;
    public int mConnectionGroup;
    public int mConnectionImportance;
    public ServiceRecord mConnectionService;
    public boolean mExecServicesFg;
    public int mFgServiceTypes;
    public boolean mHasAboveClient;
    public boolean mHasClientActivities;
    public boolean mHasForegroundServices;
    public boolean mHasTopStartedAlmostPerceptibleServices;
    public boolean mHasTypeNoneFgs;
    public long mLastTopStartedAlmostPerceptibleBindRequestUptimeMs;
    public int mRepFgServiceTypes;
    public boolean mRepHasForegroundServices;
    public final ActivityManagerService mService;
    public boolean mTreatLikeActivity;
    public final ArraySet<ServiceRecord> mServices = new ArraySet<>();
    public final ArraySet<ServiceRecord> mExecutingServices = new ArraySet<>();
    public final ArraySet<ConnectionRecord> mConnections = new ArraySet<>();
    public ArraySet<Integer> mBoundClientUids = new ArraySet<>();

    public ProcessServiceRecord(ProcessRecord processRecord) {
        this.mApp = processRecord;
        this.mService = processRecord.mService;
    }

    public void setHasClientActivities(boolean z) {
        this.mHasClientActivities = z;
        this.mApp.getWindowProcessController().setHasClientActivities(z);
    }

    public boolean hasClientActivities() {
        return this.mHasClientActivities;
    }

    public void setHasForegroundServices(boolean z, int i, boolean z2) {
        this.mHasForegroundServices = z;
        this.mFgServiceTypes = i;
        this.mHasTypeNoneFgs = z2;
        this.mApp.getWindowProcessController().setHasForegroundServices(z);
        if (z) {
            this.mApp.mProfile.addHostingComponentType(256);
        } else {
            this.mApp.mProfile.clearHostingComponentType(256);
        }
    }

    public boolean hasForegroundServices() {
        return this.mHasForegroundServices;
    }

    public void setHasReportedForegroundServices(boolean z) {
        this.mRepHasForegroundServices = z;
    }

    public boolean hasReportedForegroundServices() {
        return this.mRepHasForegroundServices;
    }

    public final int getForegroundServiceTypes() {
        if (this.mHasForegroundServices) {
            return this.mFgServiceTypes;
        }
        return 0;
    }

    public boolean areForegroundServiceTypesSame(int i, boolean z) {
        return (getForegroundServiceTypes() & i) == i && this.mHasTypeNoneFgs == z;
    }

    public boolean containsAnyForegroundServiceTypes(int i) {
        return (getForegroundServiceTypes() & i) != 0;
    }

    public boolean hasNonShortForegroundServices() {
        if (this.mHasForegroundServices) {
            return this.mHasTypeNoneFgs || this.mFgServiceTypes != 2048;
        }
        return false;
    }

    public boolean areAllShortForegroundServicesProcstateTimedOut(long j) {
        if (this.mHasForegroundServices && !hasNonShortForegroundServices()) {
            for (int size = this.mServices.size() - 1; size >= 0; size--) {
                ServiceRecord valueAt = this.mServices.valueAt(size);
                if (valueAt.isShortFgs() && valueAt.hasShortFgsInfo() && valueAt.getShortFgsInfo().getProcStateDemoteTime() >= j) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void setReportedForegroundServiceTypes(int i) {
        this.mRepFgServiceTypes = i;
    }

    public int getNumForegroundServices() {
        int size = this.mServices.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            if (this.mServices.valueAt(i2).isForeground) {
                i++;
            }
        }
        return i;
    }

    public void updateHasTopStartedAlmostPerceptibleServices() {
        this.mHasTopStartedAlmostPerceptibleServices = false;
        this.mLastTopStartedAlmostPerceptibleBindRequestUptimeMs = 0L;
        for (int size = this.mServices.size() - 1; size >= 0; size--) {
            ServiceRecord valueAt = this.mServices.valueAt(size);
            this.mLastTopStartedAlmostPerceptibleBindRequestUptimeMs = Math.max(this.mLastTopStartedAlmostPerceptibleBindRequestUptimeMs, valueAt.lastTopAlmostPerceptibleBindRequestUptimeMs);
            if (!this.mHasTopStartedAlmostPerceptibleServices && isAlmostPerceptible(valueAt)) {
                this.mHasTopStartedAlmostPerceptibleServices = true;
            }
        }
    }

    public final boolean isAlmostPerceptible(ServiceRecord serviceRecord) {
        if (serviceRecord.lastTopAlmostPerceptibleBindRequestUptimeMs <= 0) {
            return false;
        }
        ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = serviceRecord.getConnections();
        for (int size = connections.size() - 1; size >= 0; size--) {
            ArrayList<ConnectionRecord> valueAt = connections.valueAt(size);
            for (int size2 = valueAt.size() - 1; size2 >= 0; size2--) {
                if (valueAt.get(size2).hasFlag(65536)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasTopStartedAlmostPerceptibleServices() {
        return this.mHasTopStartedAlmostPerceptibleServices || (this.mLastTopStartedAlmostPerceptibleBindRequestUptimeMs > 0 && SystemClock.uptimeMillis() - this.mLastTopStartedAlmostPerceptibleBindRequestUptimeMs < this.mService.mConstants.mServiceBindAlmostPerceptibleTimeoutMs);
    }

    public void setConnectionService(ServiceRecord serviceRecord) {
        this.mConnectionService = serviceRecord;
    }

    public int getConnectionGroup() {
        return this.mConnectionGroup;
    }

    public void setConnectionGroup(int i) {
        this.mConnectionGroup = i;
    }

    public int getConnectionImportance() {
        return this.mConnectionImportance;
    }

    public void setConnectionImportance(int i) {
        this.mConnectionImportance = i;
    }

    public void updateHasAboveClientLocked() {
        this.mHasAboveClient = false;
        for (int size = this.mConnections.size() - 1; size >= 0; size--) {
            if (this.mConnections.valueAt(size).hasFlag(8)) {
                this.mHasAboveClient = true;
                return;
            }
        }
    }

    public void setHasAboveClient(boolean z) {
        this.mHasAboveClient = z;
    }

    public boolean hasAboveClient() {
        return this.mHasAboveClient;
    }

    public int modifyRawOomAdj(int i) {
        if (!this.mHasAboveClient || i < 0) {
            return i;
        }
        int i2 = 100;
        if (i >= 100) {
            i2 = 200;
            if (i >= 200) {
                i2 = 250;
                if (i >= 250) {
                    i2 = 900;
                    if (i >= 900) {
                        return i < 999 ? i + 1 : i;
                    }
                }
            }
        }
        return i2;
    }

    public boolean isTreatedLikeActivity() {
        return this.mTreatLikeActivity;
    }

    public void setTreatLikeActivity(boolean z) {
        this.mTreatLikeActivity = z;
    }

    public boolean shouldExecServicesFg() {
        return this.mExecServicesFg;
    }

    public void setExecServicesFg(boolean z) {
        this.mExecServicesFg = z;
    }

    public boolean startService(ServiceRecord serviceRecord) {
        if (serviceRecord == null) {
            return false;
        }
        boolean add = this.mServices.add(serviceRecord);
        if (add && serviceRecord.serviceInfo != null) {
            this.mApp.getWindowProcessController().onServiceStarted(serviceRecord.serviceInfo);
            updateHostingComonentTypeForBindingsLocked();
        }
        long j = serviceRecord.lastTopAlmostPerceptibleBindRequestUptimeMs;
        if (j > 0) {
            this.mLastTopStartedAlmostPerceptibleBindRequestUptimeMs = Math.max(this.mLastTopStartedAlmostPerceptibleBindRequestUptimeMs, j);
            if (!this.mHasTopStartedAlmostPerceptibleServices) {
                this.mHasTopStartedAlmostPerceptibleServices = isAlmostPerceptible(serviceRecord);
            }
        }
        return add;
    }

    public boolean stopService(ServiceRecord serviceRecord) {
        boolean remove = this.mServices.remove(serviceRecord);
        if (serviceRecord.lastTopAlmostPerceptibleBindRequestUptimeMs > 0) {
            updateHasTopStartedAlmostPerceptibleServices();
        }
        if (remove) {
            updateHostingComonentTypeForBindingsLocked();
        }
        return remove;
    }

    public void stopAllServices() {
        this.mServices.clear();
        updateHasTopStartedAlmostPerceptibleServices();
    }

    public int numberOfRunningServices() {
        return this.mServices.size();
    }

    public ServiceRecord getRunningServiceAt(int i) {
        return this.mServices.valueAt(i);
    }

    public void startExecutingService(ServiceRecord serviceRecord) {
        this.mExecutingServices.add(serviceRecord);
    }

    public void stopExecutingService(ServiceRecord serviceRecord) {
        this.mExecutingServices.remove(serviceRecord);
    }

    public void stopAllExecutingServices() {
        this.mExecutingServices.clear();
    }

    public ServiceRecord getExecutingServiceAt(int i) {
        return this.mExecutingServices.valueAt(i);
    }

    public int numberOfExecutingServices() {
        return this.mExecutingServices.size();
    }

    public void addConnection(ConnectionRecord connectionRecord) {
        this.mConnections.add(connectionRecord);
    }

    public void removeConnection(ConnectionRecord connectionRecord) {
        this.mConnections.remove(connectionRecord);
    }

    public void removeAllConnections() {
        this.mConnections.clear();
    }

    public ConnectionRecord getConnectionAt(int i) {
        return this.mConnections.valueAt(i);
    }

    public int numberOfConnections() {
        return this.mConnections.size();
    }

    public void addBoundClientUid(int i, String str, long j) {
        this.mBoundClientUids.add(Integer.valueOf(i));
        this.mApp.getWindowProcessController().addBoundClientUid(i, str, j);
    }

    public void updateBoundClientUids() {
        clearBoundClientUids();
        if (this.mServices.isEmpty()) {
            return;
        }
        ArraySet<Integer> arraySet = new ArraySet<>();
        int size = this.mServices.size();
        WindowProcessController windowProcessController = this.mApp.getWindowProcessController();
        for (int i = 0; i < size; i++) {
            ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = this.mServices.valueAt(i).getConnections();
            int size2 = connections.size();
            for (int i2 = 0; i2 < size2; i2++) {
                ArrayList<ConnectionRecord> valueAt = connections.valueAt(i2);
                for (int i3 = 0; i3 < valueAt.size(); i3++) {
                    ConnectionRecord connectionRecord = valueAt.get(i3);
                    arraySet.add(Integer.valueOf(connectionRecord.clientUid));
                    windowProcessController.addBoundClientUid(connectionRecord.clientUid, connectionRecord.clientPackageName, connectionRecord.getFlags());
                }
            }
        }
        this.mBoundClientUids = arraySet;
    }

    public void addBoundClientUidsOfNewService(ServiceRecord serviceRecord) {
        if (serviceRecord == null) {
            return;
        }
        ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = serviceRecord.getConnections();
        for (int size = connections.size() - 1; size >= 0; size--) {
            ArrayList<ConnectionRecord> valueAt = connections.valueAt(size);
            for (int i = 0; i < valueAt.size(); i++) {
                ConnectionRecord connectionRecord = valueAt.get(i);
                this.mBoundClientUids.add(Integer.valueOf(connectionRecord.clientUid));
                this.mApp.getWindowProcessController().addBoundClientUid(connectionRecord.clientUid, connectionRecord.clientPackageName, connectionRecord.getFlags());
            }
        }
    }

    public void clearBoundClientUids() {
        this.mBoundClientUids.clear();
        this.mApp.getWindowProcessController().clearBoundClientUids();
    }

    @GuardedBy({"mService"})
    public void updateHostingComonentTypeForBindingsLocked() {
        boolean z = true;
        int numberOfRunningServices = numberOfRunningServices() - 1;
        while (true) {
            if (numberOfRunningServices >= 0) {
                ServiceRecord runningServiceAt = getRunningServiceAt(numberOfRunningServices);
                if (runningServiceAt != null && !runningServiceAt.getConnections().isEmpty()) {
                    break;
                }
                numberOfRunningServices--;
            } else {
                z = false;
                break;
            }
        }
        if (z) {
            this.mApp.mProfile.addHostingComponentType(512);
        } else {
            this.mApp.mProfile.clearHostingComponentType(512);
        }
    }

    @GuardedBy({"mService"})
    public boolean incServiceCrashCountLocked(long j) {
        boolean z = false;
        boolean z2 = this.mApp.mState.getCurProcState() == 5;
        for (int numberOfRunningServices = numberOfRunningServices() - 1; numberOfRunningServices >= 0; numberOfRunningServices--) {
            ServiceRecord runningServiceAt = getRunningServiceAt(numberOfRunningServices);
            if (j > runningServiceAt.restartTime + ActivityManagerConstants.MIN_CRASH_INTERVAL) {
                runningServiceAt.crashCount = 1;
            } else {
                runningServiceAt.crashCount++;
            }
            if (runningServiceAt.crashCount < this.mService.mConstants.BOUND_SERVICE_MAX_CRASH_RETRY && (runningServiceAt.isForeground || z2)) {
                z = true;
            }
        }
        return z;
    }

    @GuardedBy({"mService"})
    public void onCleanupApplicationRecordLocked() {
        this.mTreatLikeActivity = false;
        this.mHasAboveClient = false;
        setHasClientActivities(false);
    }

    public void dump(PrintWriter printWriter, String str, long j) {
        if (this.mHasForegroundServices || this.mApp.mState.getForcingToImportant() != null) {
            printWriter.print(str);
            printWriter.print("mHasForegroundServices=");
            printWriter.print(this.mHasForegroundServices);
            printWriter.print(" forcingToImportant=");
            printWriter.println(this.mApp.mState.getForcingToImportant());
        }
        if (this.mHasTopStartedAlmostPerceptibleServices || this.mLastTopStartedAlmostPerceptibleBindRequestUptimeMs > 0) {
            printWriter.print(str);
            printWriter.print("mHasTopStartedAlmostPerceptibleServices=");
            printWriter.print(this.mHasTopStartedAlmostPerceptibleServices);
            printWriter.print(" mLastTopStartedAlmostPerceptibleBindRequestUptimeMs=");
            printWriter.println(this.mLastTopStartedAlmostPerceptibleBindRequestUptimeMs);
        }
        if (this.mHasClientActivities || this.mHasAboveClient || this.mTreatLikeActivity) {
            printWriter.print(str);
            printWriter.print("hasClientActivities=");
            printWriter.print(this.mHasClientActivities);
            printWriter.print(" hasAboveClient=");
            printWriter.print(this.mHasAboveClient);
            printWriter.print(" treatLikeActivity=");
            printWriter.println(this.mTreatLikeActivity);
        }
        if (this.mConnectionService != null || this.mConnectionGroup != 0) {
            printWriter.print(str);
            printWriter.print("connectionGroup=");
            printWriter.print(this.mConnectionGroup);
            printWriter.print(" Importance=");
            printWriter.print(this.mConnectionImportance);
            printWriter.print(" Service=");
            printWriter.println(this.mConnectionService);
        }
        if (this.mAllowlistManager) {
            printWriter.print(str);
            printWriter.print("allowlistManager=");
            printWriter.println(this.mAllowlistManager);
        }
        if (this.mServices.size() > 0) {
            printWriter.print(str);
            printWriter.println("Services:");
            int size = this.mServices.size();
            for (int i = 0; i < size; i++) {
                printWriter.print(str);
                printWriter.print("  - ");
                printWriter.println(this.mServices.valueAt(i));
            }
        }
        if (this.mExecutingServices.size() > 0) {
            printWriter.print(str);
            printWriter.print("Executing Services (fg=");
            printWriter.print(this.mExecServicesFg);
            printWriter.println(")");
            int size2 = this.mExecutingServices.size();
            for (int i2 = 0; i2 < size2; i2++) {
                printWriter.print(str);
                printWriter.print("  - ");
                printWriter.println(this.mExecutingServices.valueAt(i2));
            }
        }
        if (this.mConnections.size() > 0) {
            printWriter.print(str);
            printWriter.println("mConnections:");
            int size3 = this.mConnections.size();
            for (int i3 = 0; i3 < size3; i3++) {
                printWriter.print(str);
                printWriter.print("  - ");
                printWriter.println(this.mConnections.valueAt(i3));
            }
        }
    }
}
