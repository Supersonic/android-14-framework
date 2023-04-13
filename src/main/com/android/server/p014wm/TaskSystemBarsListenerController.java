package com.android.server.p014wm;

import com.android.internal.os.BackgroundThread;
import com.android.server.p014wm.WindowManagerInternal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Executor;
/* renamed from: com.android.server.wm.TaskSystemBarsListenerController */
/* loaded from: classes2.dex */
public final class TaskSystemBarsListenerController {
    public final HashSet<WindowManagerInternal.TaskSystemBarsListener> mListeners = new HashSet<>();
    public final Executor mBackgroundExecutor = BackgroundThread.getExecutor();

    public void registerListener(WindowManagerInternal.TaskSystemBarsListener taskSystemBarsListener) {
        this.mListeners.add(taskSystemBarsListener);
    }

    public void unregisterListener(WindowManagerInternal.TaskSystemBarsListener taskSystemBarsListener) {
        this.mListeners.remove(taskSystemBarsListener);
    }

    public void dispatchTransientSystemBarVisibilityChanged(final int i, final boolean z, final boolean z2) {
        final HashSet hashSet = new HashSet(this.mListeners);
        this.mBackgroundExecutor.execute(new Runnable() { // from class: com.android.server.wm.TaskSystemBarsListenerController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                TaskSystemBarsListenerController.lambda$dispatchTransientSystemBarVisibilityChanged$0(hashSet, i, z, z2);
            }
        });
    }

    public static /* synthetic */ void lambda$dispatchTransientSystemBarVisibilityChanged$0(HashSet hashSet, int i, boolean z, boolean z2) {
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            ((WindowManagerInternal.TaskSystemBarsListener) it.next()).onTransientSystemBarsVisibilityChanged(i, z, z2);
        }
    }
}
