package com.android.server.location.injector;

import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.server.location.LocationManagerService;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public abstract class PackageResetHelper {
    public final CopyOnWriteArrayList<Responder> mResponders = new CopyOnWriteArrayList<>();

    /* loaded from: classes.dex */
    public interface Responder {
        boolean isResetableForPackage(String str);

        void onPackageReset(String str);
    }

    @GuardedBy({"this"})
    public abstract void onRegister();

    @GuardedBy({"this"})
    public abstract void onUnregister();

    public synchronized void register(Responder responder) {
        boolean isEmpty = this.mResponders.isEmpty();
        this.mResponders.add(responder);
        if (isEmpty) {
            onRegister();
        }
    }

    public synchronized void unregister(Responder responder) {
        this.mResponders.remove(responder);
        if (this.mResponders.isEmpty()) {
            onUnregister();
        }
    }

    public final void notifyPackageReset(String str) {
        if (LocationManagerService.f1147D) {
            Log.d("LocationManagerService", "package " + str + " reset");
        }
        Iterator<Responder> it = this.mResponders.iterator();
        while (it.hasNext()) {
            it.next().onPackageReset(str);
        }
    }

    public final boolean queryResetableForPackage(String str) {
        Iterator<Responder> it = this.mResponders.iterator();
        while (it.hasNext()) {
            if (it.next().isResetableForPackage(str)) {
                return true;
            }
        }
        return false;
    }
}
