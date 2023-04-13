package com.android.server.location.injector;

import android.location.util.identity.CallerIdentity;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public abstract class AppOpsHelper {
    public final CopyOnWriteArrayList<LocationAppOpListener> mListeners = new CopyOnWriteArrayList<>();

    /* loaded from: classes.dex */
    public interface LocationAppOpListener {
        void onAppOpsChanged(String str);
    }

    public abstract boolean checkOpNoThrow(int i, CallerIdentity callerIdentity);

    public abstract void finishOp(int i, CallerIdentity callerIdentity);

    public abstract boolean noteOp(int i, CallerIdentity callerIdentity);

    public abstract boolean noteOpNoThrow(int i, CallerIdentity callerIdentity);

    public abstract boolean startOpNoThrow(int i, CallerIdentity callerIdentity);

    public final void notifyAppOpChanged(String str) {
        Iterator<LocationAppOpListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onAppOpsChanged(str);
        }
    }

    public final void addListener(LocationAppOpListener locationAppOpListener) {
        this.mListeners.add(locationAppOpListener);
    }
}
