package com.android.server.p009os;

import android.os.Binder;
import android.os.IBinder;
import android.os.ISchedulingPolicyService;
import android.os.Process;
import android.util.Log;
import com.android.server.SystemServerInitThreadPool;
/* renamed from: com.android.server.os.SchedulingPolicyService */
/* loaded from: classes2.dex */
public class SchedulingPolicyService extends ISchedulingPolicyService.Stub {
    public static final String[] MEDIA_PROCESS_NAMES = {"media.swcodec"};
    public IBinder mClient;
    public final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() { // from class: com.android.server.os.SchedulingPolicyService.1
        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            SchedulingPolicyService.this.requestCpusetBoost(false, null);
        }
    };
    public int mBoostedPid = -1;

    public SchedulingPolicyService() {
        SystemServerInitThreadPool.submit(new Runnable() { // from class: com.android.server.os.SchedulingPolicyService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SchedulingPolicyService.this.lambda$new$0();
            }
        }, "SchedulingPolicyService.<init>");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        int[] pidsForCommands;
        synchronized (this.mDeathRecipient) {
            if (this.mBoostedPid == -1 && (pidsForCommands = Process.getPidsForCommands(MEDIA_PROCESS_NAMES)) != null && pidsForCommands.length == 1) {
                int i = pidsForCommands[0];
                this.mBoostedPid = i;
                disableCpusetBoost(i);
            }
        }
    }

    public int requestPriority(int i, int i2, int i3, boolean z) {
        if (isPermitted() && i3 >= 1 && i3 <= 3 && Process.getThreadGroupLeader(i2) == i) {
            if (Binder.getCallingUid() == 1041 && !z && Process.getUidForPid(i2) != 1041) {
                return -1;
            }
            if (Binder.getCallingUid() != 1002) {
                try {
                    Process.setThreadGroup(i2, !z ? 4 : 6);
                } catch (RuntimeException e) {
                    Log.e("SchedulingPolicyService", "Failed setThreadGroup: " + e);
                    return -1;
                }
            }
            try {
                Process.setThreadScheduler(i2, 1073741825, i3);
                return 0;
            } catch (RuntimeException e2) {
                Log.e("SchedulingPolicyService", "Failed setThreadScheduler: " + e2);
            }
        }
        return -1;
    }

    public int requestCpusetBoost(boolean z, IBinder iBinder) {
        if (Binder.getCallingPid() == Process.myPid() || Binder.getCallingUid() == 1013) {
            int[] pidsForCommands = Process.getPidsForCommands(MEDIA_PROCESS_NAMES);
            if (pidsForCommands == null || pidsForCommands.length != 1) {
                Log.e("SchedulingPolicyService", "requestCpusetBoost: can't find media.codec process");
                return -1;
            }
            synchronized (this.mDeathRecipient) {
                if (z) {
                    return enableCpusetBoost(pidsForCommands[0], iBinder);
                }
                return disableCpusetBoost(pidsForCommands[0]);
            }
        }
        return -1;
    }

    public final int enableCpusetBoost(int i, IBinder iBinder) {
        if (this.mBoostedPid == i) {
            return 0;
        }
        this.mBoostedPid = -1;
        IBinder iBinder2 = this.mClient;
        if (iBinder2 != null) {
            try {
                iBinder2.unlinkToDeath(this.mDeathRecipient, 0);
            } catch (Exception unused) {
            } catch (Throwable th) {
                this.mClient = null;
                throw th;
            }
            this.mClient = null;
        }
        try {
            iBinder.linkToDeath(this.mDeathRecipient, 0);
            Log.i("SchedulingPolicyService", "Moving " + i + " to group 5");
            Process.setProcessGroup(i, 5);
            this.mBoostedPid = i;
            this.mClient = iBinder;
            return 0;
        } catch (Exception e) {
            Log.e("SchedulingPolicyService", "Failed enableCpusetBoost: " + e);
            try {
                iBinder.unlinkToDeath(this.mDeathRecipient, 0);
            } catch (Exception unused2) {
            }
            return -1;
        }
    }

    public final int disableCpusetBoost(int i) {
        int i2 = this.mBoostedPid;
        this.mBoostedPid = -1;
        IBinder iBinder = this.mClient;
        if (iBinder != null) {
            try {
                iBinder.unlinkToDeath(this.mDeathRecipient, 0);
            } catch (Exception unused) {
            } catch (Throwable th) {
                this.mClient = null;
                throw th;
            }
            this.mClient = null;
        }
        if (i2 == i) {
            try {
                Log.i("SchedulingPolicyService", "Moving " + i + " back to group default");
                Process.setProcessGroup(i, -1);
            } catch (Exception unused2) {
                Log.w("SchedulingPolicyService", "Couldn't move pid " + i + " back to group default");
            }
        }
        return 0;
    }

    public final boolean isPermitted() {
        int callingUid;
        return Binder.getCallingPid() == Process.myPid() || (callingUid = Binder.getCallingUid()) == 1002 || callingUid == 1041 || callingUid == 1047;
    }
}
