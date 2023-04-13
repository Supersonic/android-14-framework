package android.p008os;

import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.ProcessInfo;
import android.util.Log;
import com.android.internal.p028os.Zygote;
import dalvik.system.VMRuntime;
/* renamed from: android.os.AppZygote */
/* loaded from: classes3.dex */
public class AppZygote {
    private static final String LOG_TAG = "AppZygote";
    private final ApplicationInfo mAppInfo;
    private final Object mLock = new Object();
    private final ProcessInfo mProcessInfo;
    private ChildZygoteProcess mZygote;
    private final int mZygoteUid;
    private final int mZygoteUidGidMax;
    private final int mZygoteUidGidMin;

    public AppZygote(ApplicationInfo appInfo, ProcessInfo processInfo, int zygoteUid, int uidGidMin, int uidGidMax) {
        this.mAppInfo = appInfo;
        this.mProcessInfo = processInfo;
        this.mZygoteUid = zygoteUid;
        this.mZygoteUidGidMin = uidGidMin;
        this.mZygoteUidGidMax = uidGidMax;
    }

    public ChildZygoteProcess getProcess() {
        synchronized (this.mLock) {
            ChildZygoteProcess childZygoteProcess = this.mZygote;
            if (childZygoteProcess != null) {
                return childZygoteProcess;
            }
            connectToZygoteIfNeededLocked();
            return this.mZygote;
        }
    }

    public void stopZygote() {
        synchronized (this.mLock) {
            stopZygoteLocked();
        }
    }

    public ApplicationInfo getAppInfo() {
        return this.mAppInfo;
    }

    private void stopZygoteLocked() {
        ChildZygoteProcess childZygoteProcess = this.mZygote;
        if (childZygoteProcess != null) {
            childZygoteProcess.close();
            Process.killProcessGroup(this.mZygoteUid, this.mZygote.getPid());
            this.mZygote = null;
        }
    }

    private void connectToZygoteIfNeededLocked() {
        String abi = this.mAppInfo.primaryCpuAbi != null ? this.mAppInfo.primaryCpuAbi : Build.SUPPORTED_ABIS[0];
        try {
            int runtimeFlags = Zygote.getMemorySafetyRuntimeFlagsForSecondaryZygote(this.mAppInfo, this.mProcessInfo);
            int i = this.mZygoteUid;
            ChildZygoteProcess startChildZygote = Process.ZYGOTE_PROCESS.startChildZygote("com.android.internal.os.AppZygoteInit", this.mAppInfo.processName + "_zygote", i, i, null, runtimeFlags, "app_zygote", abi, abi, VMRuntime.getInstructionSet(abi), this.mZygoteUidGidMin, this.mZygoteUidGidMax);
            this.mZygote = startChildZygote;
            ZygoteProcess.waitForConnectionToZygote(startChildZygote.getPrimarySocketAddress());
            Log.m108i(LOG_TAG, "Starting application preload.");
            this.mZygote.preloadApp(this.mAppInfo, abi);
            Log.m108i(LOG_TAG, "Application preload done.");
        } catch (Exception e) {
            Log.m109e(LOG_TAG, "Error connecting to app zygote", e);
            stopZygoteLocked();
        }
    }
}
