package com.android.server.location.injector;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public abstract class AppForegroundHelper {
    public final CopyOnWriteArrayList<AppForegroundListener> mListeners = new CopyOnWriteArrayList<>();

    /* loaded from: classes.dex */
    public interface AppForegroundListener {
        void onAppForegroundChanged(int i, boolean z);
    }

    public static boolean isForeground(int i) {
        return i <= 125;
    }

    public abstract boolean isAppForeground(int i);

    public final void addListener(AppForegroundListener appForegroundListener) {
        this.mListeners.add(appForegroundListener);
    }

    public final void removeListener(AppForegroundListener appForegroundListener) {
        this.mListeners.remove(appForegroundListener);
    }

    public final void notifyAppForeground(int i, boolean z) {
        Iterator<AppForegroundListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onAppForegroundChanged(i, z);
        }
    }
}
