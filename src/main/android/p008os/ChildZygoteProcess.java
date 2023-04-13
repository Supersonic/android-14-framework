package android.p008os;

import android.net.LocalSocketAddress;
/* renamed from: android.os.ChildZygoteProcess */
/* loaded from: classes3.dex */
public class ChildZygoteProcess extends ZygoteProcess {
    private final int mPid;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChildZygoteProcess(LocalSocketAddress socketAddress, int pid) {
        super(socketAddress, null);
        this.mPid = pid;
    }

    public int getPid() {
        return this.mPid;
    }
}
