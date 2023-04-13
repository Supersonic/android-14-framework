package com.android.server.location.injector;

import android.util.Log;
import com.android.server.location.LocationManagerService;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public abstract class ScreenInteractiveHelper {
    public final CopyOnWriteArrayList<ScreenInteractiveChangedListener> mListeners = new CopyOnWriteArrayList<>();

    /* loaded from: classes.dex */
    public interface ScreenInteractiveChangedListener {
        void onScreenInteractiveChanged(boolean z);
    }

    public abstract boolean isInteractive();

    public final void addListener(ScreenInteractiveChangedListener screenInteractiveChangedListener) {
        this.mListeners.add(screenInteractiveChangedListener);
    }

    public final void removeListener(ScreenInteractiveChangedListener screenInteractiveChangedListener) {
        this.mListeners.remove(screenInteractiveChangedListener);
    }

    public final void notifyScreenInteractiveChanged(boolean z) {
        if (LocationManagerService.f1147D) {
            Log.d("LocationManagerService", "screen interactive is now " + z);
        }
        Iterator<ScreenInteractiveChangedListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onScreenInteractiveChanged(z);
        }
    }
}
