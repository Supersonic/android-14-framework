package com.android.server.job.controllers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArrayMap;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.server.job.JobSchedulerService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class ComponentController extends StateController {
    public static final boolean DEBUG;
    public final BroadcastReceiver mBroadcastReceiver;
    public final ComponentStateUpdateFunctor mComponentStateUpdateFunctor;
    public final SparseArrayMap<ComponentName, String> mServiceProcessCache;

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void dumpControllerStateLocked(ProtoOutputStream protoOutputStream, long j, Predicate<JobStatus> predicate) {
    }

    @Override // com.android.server.job.controllers.StateController
    public void maybeStopTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
    }

    static {
        DEBUG = JobSchedulerService.DEBUG || Log.isLoggable("JobScheduler.Component", 3);
    }

    public ComponentController(JobSchedulerService jobSchedulerService) {
        super(jobSchedulerService);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.job.controllers.ComponentController.1
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                char c;
                String schemeSpecificPart;
                String action = intent.getAction();
                if (action == null) {
                    Slog.wtf("JobScheduler.Component", "Intent action was null");
                    return;
                }
                switch (action.hashCode()) {
                    case -742246786:
                        if (action.equals("android.intent.action.USER_STOPPED")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 172491798:
                        if (action.equals("android.intent.action.PACKAGE_CHANGED")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 833559602:
                        if (action.equals("android.intent.action.USER_UNLOCKED")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1544582882:
                        if (action.equals("android.intent.action.PACKAGE_ADDED")) {
                            c = 3;
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
                    case 2:
                        ComponentController.this.updateComponentStateForUser(intent.getIntExtra("android.intent.extra.user_handle", 0));
                        return;
                    case 1:
                        Uri data = intent.getData();
                        schemeSpecificPart = data != null ? data.getSchemeSpecificPart() : null;
                        String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.changed_component_name_list");
                        if (schemeSpecificPart == null || stringArrayExtra == null || stringArrayExtra.length <= 0) {
                            return;
                        }
                        ComponentController.this.updateComponentStateForPackage(UserHandle.getUserId(intent.getIntExtra("android.intent.extra.UID", -1)), schemeSpecificPart);
                        return;
                    case 3:
                        if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                            Uri data2 = intent.getData();
                            schemeSpecificPart = data2 != null ? data2.getSchemeSpecificPart() : null;
                            if (schemeSpecificPart != null) {
                                ComponentController.this.updateComponentStateForPackage(UserHandle.getUserId(intent.getIntExtra("android.intent.extra.UID", -1)), schemeSpecificPart);
                                return;
                            }
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
        this.mBroadcastReceiver = broadcastReceiver;
        this.mServiceProcessCache = new SparseArrayMap<>();
        this.mComponentStateUpdateFunctor = new ComponentStateUpdateFunctor();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiverAsUser(broadcastReceiver, UserHandle.ALL, intentFilter, null, null);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.USER_UNLOCKED");
        intentFilter2.addAction("android.intent.action.USER_STOPPED");
        this.mContext.registerReceiverAsUser(broadcastReceiver, UserHandle.ALL, intentFilter2, null, null);
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void maybeStartTrackingJobLocked(JobStatus jobStatus, JobStatus jobStatus2) {
        updateComponentEnabledStateLocked(jobStatus);
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void onAppRemovedLocked(String str, int i) {
        clearComponentsForPackageLocked(UserHandle.getUserId(i), str);
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void onUserRemovedLocked(int i) {
        this.mServiceProcessCache.delete(i);
    }

    @GuardedBy({"mLock"})
    public final String getServiceProcessLocked(JobStatus jobStatus) {
        ServiceInfo serviceInfo;
        ComponentName serviceComponent = jobStatus.getServiceComponent();
        int userId = jobStatus.getUserId();
        if (this.mServiceProcessCache.contains(userId, serviceComponent)) {
            return (String) this.mServiceProcessCache.get(userId, serviceComponent);
        }
        try {
            serviceInfo = this.mContext.createContextAsUser(UserHandle.of(userId), 0).getPackageManager().getServiceInfo(serviceComponent, 268435456);
        } catch (PackageManager.NameNotFoundException unused) {
            if (this.mService.areUsersStartedLocked(jobStatus)) {
                Slog.e("JobScheduler.Component", "Job exists for non-existent package: " + serviceComponent.getPackageName());
            }
            serviceInfo = null;
        }
        String str = serviceInfo != null ? serviceInfo.processName : null;
        this.mServiceProcessCache.add(userId, serviceComponent, str);
        return str;
    }

    @GuardedBy({"mLock"})
    public final boolean updateComponentEnabledStateLocked(JobStatus jobStatus) {
        String serviceProcessLocked = getServiceProcessLocked(jobStatus);
        if (DEBUG && serviceProcessLocked == null) {
            Slog.v("JobScheduler.Component", jobStatus.toShortString() + " component not present");
        }
        String str = jobStatus.serviceProcessName;
        jobStatus.serviceProcessName = serviceProcessLocked;
        return !Objects.equals(str, serviceProcessLocked);
    }

    @GuardedBy({"mLock"})
    public final void clearComponentsForPackageLocked(int i, String str) {
        int indexOfKey = this.mServiceProcessCache.indexOfKey(i);
        for (int numElementsForKey = this.mServiceProcessCache.numElementsForKey(i) - 1; numElementsForKey >= 0; numElementsForKey--) {
            ComponentName componentName = (ComponentName) this.mServiceProcessCache.keyAt(indexOfKey, numElementsForKey);
            if (componentName.getPackageName().equals(str)) {
                this.mServiceProcessCache.delete(i, componentName);
            }
        }
    }

    public final void updateComponentStateForPackage(final int i, final String str) {
        synchronized (this.mLock) {
            clearComponentsForPackageLocked(i, str);
            updateComponentStatesLocked(new Predicate() { // from class: com.android.server.job.controllers.ComponentController$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateComponentStateForPackage$0;
                    lambda$updateComponentStateForPackage$0 = ComponentController.lambda$updateComponentStateForPackage$0(i, str, (JobStatus) obj);
                    return lambda$updateComponentStateForPackage$0;
                }
            });
        }
    }

    public static /* synthetic */ boolean lambda$updateComponentStateForPackage$0(int i, String str, JobStatus jobStatus) {
        return jobStatus.getUserId() == i && jobStatus.getServiceComponent().getPackageName().equals(str);
    }

    public final void updateComponentStateForUser(final int i) {
        synchronized (this.mLock) {
            this.mServiceProcessCache.delete(i);
            updateComponentStatesLocked(new Predicate() { // from class: com.android.server.job.controllers.ComponentController$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateComponentStateForUser$1;
                    lambda$updateComponentStateForUser$1 = ComponentController.lambda$updateComponentStateForUser$1(i, (JobStatus) obj);
                    return lambda$updateComponentStateForUser$1;
                }
            });
        }
    }

    public static /* synthetic */ boolean lambda$updateComponentStateForUser$1(int i, JobStatus jobStatus) {
        return jobStatus.getUserId() == i;
    }

    @GuardedBy({"mLock"})
    public final void updateComponentStatesLocked(Predicate<JobStatus> predicate) {
        this.mComponentStateUpdateFunctor.reset();
        this.mService.getJobStore().forEachJob(predicate, this.mComponentStateUpdateFunctor);
        if (this.mComponentStateUpdateFunctor.mChangedJobs.size() > 0) {
            this.mStateChangedListener.onControllerStateChanged(this.mComponentStateUpdateFunctor.mChangedJobs);
        }
    }

    /* loaded from: classes.dex */
    public final class ComponentStateUpdateFunctor implements Consumer<JobStatus> {
        @GuardedBy({"mLock"})
        public final ArraySet<JobStatus> mChangedJobs = new ArraySet<>();

        public ComponentStateUpdateFunctor() {
        }

        @Override // java.util.function.Consumer
        @GuardedBy({"mLock"})
        public void accept(JobStatus jobStatus) {
            if (ComponentController.this.updateComponentEnabledStateLocked(jobStatus)) {
                this.mChangedJobs.add(jobStatus);
            }
        }

        @GuardedBy({"mLock"})
        public final void reset() {
            this.mChangedJobs.clear();
        }
    }

    @Override // com.android.server.job.controllers.StateController
    @GuardedBy({"mLock"})
    public void dumpControllerStateLocked(IndentingPrintWriter indentingPrintWriter, Predicate<JobStatus> predicate) {
        for (int i = 0; i < this.mServiceProcessCache.numMaps(); i++) {
            int keyAt = this.mServiceProcessCache.keyAt(i);
            for (int i2 = 0; i2 < this.mServiceProcessCache.numElementsForKey(keyAt); i2++) {
                indentingPrintWriter.print(keyAt);
                indentingPrintWriter.print(PackageManagerShellCommandDataLoader.STDIN_PATH);
                indentingPrintWriter.print((ComponentName) this.mServiceProcessCache.keyAt(i, i2));
                indentingPrintWriter.print(": ");
                indentingPrintWriter.print((String) this.mServiceProcessCache.valueAt(i, i2));
                indentingPrintWriter.println();
            }
        }
    }
}
