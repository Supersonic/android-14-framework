package com.android.server.location.injector;

import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.server.location.LocationManagerService;
import com.android.server.location.eventlog.LocationEventLog;
import java.io.FileDescriptor;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public abstract class UserInfoHelper {
    public final CopyOnWriteArrayList<UserListener> mListeners = new CopyOnWriteArrayList<>();

    /* loaded from: classes.dex */
    public interface UserListener {
        void onUserChanged(int i, int i2);
    }

    public abstract void dump(FileDescriptor fileDescriptor, IndentingPrintWriter indentingPrintWriter, String[] strArr);

    public abstract int getCurrentUserId();

    public abstract int[] getProfileIds(int i);

    public abstract int[] getRunningUserIds();

    public abstract boolean isCurrentUserId(int i);

    public abstract boolean isVisibleUserId(int i);

    public final void addListener(UserListener userListener) {
        this.mListeners.add(userListener);
    }

    public final void removeListener(UserListener userListener) {
        this.mListeners.remove(userListener);
    }

    public final void dispatchOnUserStarted(int i) {
        if (LocationManagerService.f1147D) {
            Log.d("LocationManagerService", "u" + i + " started");
        }
        Iterator<UserListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onUserChanged(i, 2);
        }
    }

    public final void dispatchOnUserStopped(int i) {
        if (LocationManagerService.f1147D) {
            Log.d("LocationManagerService", "u" + i + " stopped");
        }
        Iterator<UserListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onUserChanged(i, 3);
        }
    }

    public final void dispatchOnCurrentUserChanged(int i, int i2) {
        int[] profileIds = getProfileIds(i);
        int[] profileIds2 = getProfileIds(i2);
        if (LocationManagerService.f1147D) {
            Log.d("LocationManagerService", "current user changed from u" + Arrays.toString(profileIds) + " to u" + Arrays.toString(profileIds2));
        }
        LocationEventLog.EVENT_LOG.logUserSwitched(i, i2);
        Iterator<UserListener> it = this.mListeners.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            UserListener next = it.next();
            for (int i3 : profileIds) {
                next.onUserChanged(i3, 1);
            }
        }
        Iterator<UserListener> it2 = this.mListeners.iterator();
        while (it2.hasNext()) {
            UserListener next2 = it2.next();
            for (int i4 : profileIds2) {
                next2.onUserChanged(i4, 1);
            }
        }
    }

    public final void dispatchOnVisibleUserChanged(int i, boolean z) {
        if (LocationManagerService.f1147D) {
            StringBuilder sb = new StringBuilder();
            sb.append("visibility of u");
            sb.append(i);
            sb.append(" changed to ");
            sb.append(z ? "visible" : "invisible");
            Log.d("LocationManagerService", sb.toString());
        }
        LocationEventLog.EVENT_LOG.logUserVisibilityChanged(i, z);
        Iterator<UserListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onUserChanged(i, 4);
        }
    }
}
