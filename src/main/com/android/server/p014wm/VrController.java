package com.android.server.p014wm;

import android.content.ComponentName;
import android.os.Process;
import android.service.vr.IPersistentVrStateCallbacks;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.util.proto.ProtoUtils;
import com.android.server.LocalServices;
import com.android.server.p006am.ActivityManagerService;
import com.android.server.p013vr.VrManagerInternal;
/* renamed from: com.android.server.wm.VrController */
/* loaded from: classes2.dex */
public final class VrController {
    public static int[] ORIG_ENUMS = {0, 1, 2};
    public static int[] PROTO_ENUMS = {0, 1, 2};
    public final Object mGlobalAmLock;
    public volatile int mVrState = 0;
    public int mVrRenderThreadTid = 0;
    public final IPersistentVrStateCallbacks mPersistentVrModeListener = new IPersistentVrStateCallbacks.Stub() { // from class: com.android.server.wm.VrController.1
        public void onPersistentVrStateChanged(boolean z) {
            synchronized (VrController.this.mGlobalAmLock) {
                if (z) {
                    VrController.this.setVrRenderThreadLocked(0, 3, true);
                    VrController.this.mVrState |= 2;
                } else {
                    VrController.this.setPersistentVrRenderThreadLocked(0, true);
                    VrController.this.mVrState &= -3;
                }
            }
        }
    };

    public VrController(Object obj) {
        this.mGlobalAmLock = obj;
    }

    public void onSystemReady() {
        VrManagerInternal vrManagerInternal = (VrManagerInternal) LocalServices.getService(VrManagerInternal.class);
        if (vrManagerInternal != null) {
            vrManagerInternal.addPersistentVrModeStateListener(this.mPersistentVrModeListener);
        }
    }

    public boolean isInterestingToSchedGroup() {
        return (this.mVrState & 3) != 0;
    }

    public void onTopProcChangedLocked(WindowProcessController windowProcessController) {
        int currentSchedulingGroup = windowProcessController.getCurrentSchedulingGroup();
        if (currentSchedulingGroup == 3) {
            setVrRenderThreadLocked(windowProcessController.mVrThreadTid, currentSchedulingGroup, true);
        } else if (windowProcessController.mVrThreadTid == this.mVrRenderThreadTid) {
            clearVrRenderThreadLocked(true);
        }
    }

    public boolean onVrModeChanged(ActivityRecord activityRecord) {
        ComponentName componentName;
        boolean z;
        int i;
        ComponentName componentName2;
        boolean changeVrModeLocked;
        int pid;
        VrManagerInternal vrManagerInternal = (VrManagerInternal) LocalServices.getService(VrManagerInternal.class);
        if (vrManagerInternal == null) {
            return false;
        }
        synchronized (this.mGlobalAmLock) {
            componentName = activityRecord.requestedVrComponent;
            z = componentName != null;
            i = activityRecord.mUserId;
            componentName2 = activityRecord.info.getComponentName();
            changeVrModeLocked = changeVrModeLocked(z, activityRecord.app);
            WindowProcessController windowProcessController = activityRecord.app;
            pid = windowProcessController != null ? windowProcessController.getPid() : -1;
        }
        vrManagerInternal.setVrMode(z, componentName, i, pid, componentName2);
        return changeVrModeLocked;
    }

    public void setVrThreadLocked(int i, int i2, WindowProcessController windowProcessController) {
        if (hasPersistentVrFlagSet()) {
            Slog.w("VrController", "VR thread cannot be set in persistent VR mode!");
        } else if (windowProcessController == null) {
            Slog.w("VrController", "Persistent VR thread not set, calling process doesn't exist!");
        } else {
            if (i != 0) {
                enforceThreadInProcess(i, i2);
            }
            if (!inVrMode()) {
                Slog.w("VrController", "VR thread cannot be set when not in VR mode!");
            } else {
                setVrRenderThreadLocked(i, windowProcessController.getCurrentSchedulingGroup(), false);
            }
            if (i <= 0) {
                i = 0;
            }
            windowProcessController.mVrThreadTid = i;
        }
    }

    public void setPersistentVrThreadLocked(int i, int i2, WindowProcessController windowProcessController) {
        if (!hasPersistentVrFlagSet()) {
            Slog.w("VrController", "Persistent VR thread may only be set in persistent VR mode!");
        } else if (windowProcessController == null) {
            Slog.w("VrController", "Persistent VR thread not set, calling process doesn't exist!");
        } else {
            if (i != 0) {
                enforceThreadInProcess(i, i2);
            }
            setPersistentVrRenderThreadLocked(i, false);
        }
    }

    public boolean shouldDisableNonVrUiLocked() {
        return this.mVrState != 0;
    }

    public final boolean changeVrModeLocked(boolean z, WindowProcessController windowProcessController) {
        int i = this.mVrState;
        if (z) {
            this.mVrState |= 1;
        } else {
            this.mVrState &= -2;
        }
        boolean z2 = i != this.mVrState;
        if (z2) {
            if (windowProcessController != null) {
                int i2 = windowProcessController.mVrThreadTid;
                if (i2 > 0) {
                    setVrRenderThreadLocked(i2, windowProcessController.getCurrentSchedulingGroup(), false);
                }
            } else {
                clearVrRenderThreadLocked(false);
            }
        }
        return z2;
    }

    public final int updateVrRenderThreadLocked(int i, boolean z) {
        int i2 = this.mVrRenderThreadTid;
        if (i2 == i) {
            return i2;
        }
        if (i2 > 0) {
            ActivityManagerService.scheduleAsRegularPriority(i2, z);
            this.mVrRenderThreadTid = 0;
        }
        if (i > 0) {
            this.mVrRenderThreadTid = i;
            ActivityManagerService.scheduleAsFifoPriority(i, z);
        }
        return this.mVrRenderThreadTid;
    }

    public final int setPersistentVrRenderThreadLocked(int i, boolean z) {
        if (!hasPersistentVrFlagSet()) {
            if (!z) {
                Slog.w("VrController", "Failed to set persistent VR thread, system not in persistent VR mode.");
            }
            return this.mVrRenderThreadTid;
        }
        return updateVrRenderThreadLocked(i, z);
    }

    public final int setVrRenderThreadLocked(int i, int i2, boolean z) {
        boolean inVrMode = inVrMode();
        boolean hasPersistentVrFlagSet = hasPersistentVrFlagSet();
        if (!inVrMode || hasPersistentVrFlagSet || i2 != 3) {
            if (!z) {
                String str = !inVrMode ? "system not in VR mode." : hasPersistentVrFlagSet ? "system in persistent VR mode." : "caller is not the current top application.";
                Slog.w("VrController", "Failed to set VR thread, " + str);
            }
            return this.mVrRenderThreadTid;
        }
        return updateVrRenderThreadLocked(i, z);
    }

    public final void clearVrRenderThreadLocked(boolean z) {
        updateVrRenderThreadLocked(0, z);
    }

    public final void enforceThreadInProcess(int i, int i2) {
        if (!Process.isThreadInProcess(i2, i)) {
            throw new IllegalArgumentException("VR thread does not belong to process");
        }
    }

    public final boolean inVrMode() {
        return (this.mVrState & 1) != 0;
    }

    public final boolean hasPersistentVrFlagSet() {
        return (this.mVrState & 2) != 0;
    }

    public String toString() {
        return String.format("[VrState=0x%x,VrRenderThreadTid=%d]", Integer.valueOf(this.mVrState), Integer.valueOf(this.mVrRenderThreadTid));
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        ProtoUtils.writeBitWiseFlagsToProtoEnum(protoOutputStream, 2259152797697L, this.mVrState, ORIG_ENUMS, PROTO_ENUMS);
        protoOutputStream.write(1120986464258L, this.mVrRenderThreadTid);
        protoOutputStream.end(start);
    }
}
