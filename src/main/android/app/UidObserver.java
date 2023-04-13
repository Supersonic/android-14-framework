package android.app;

import android.app.IUidObserver;
/* loaded from: classes.dex */
public class UidObserver extends IUidObserver.Stub {
    @Override // android.app.IUidObserver
    public void onUidActive(int uid) {
    }

    @Override // android.app.IUidObserver
    public void onUidCachedChanged(int uid, boolean cached) {
    }

    @Override // android.app.IUidObserver
    public void onUidGone(int uid, boolean disabled) {
    }

    @Override // android.app.IUidObserver
    public void onUidIdle(int uid, boolean disabled) {
    }

    @Override // android.app.IUidObserver
    public void onUidProcAdjChanged(int uid) {
    }

    @Override // android.app.IUidObserver
    public void onUidStateChanged(int uid, int procState, long procStateSeq, int capability) {
    }
}
