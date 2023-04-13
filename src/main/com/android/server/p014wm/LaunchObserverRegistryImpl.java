package com.android.server.p014wm;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.util.ArrayList;
import java.util.function.BiConsumer;
/* renamed from: com.android.server.wm.LaunchObserverRegistryImpl */
/* loaded from: classes2.dex */
public class LaunchObserverRegistryImpl extends ActivityMetricsLaunchObserver implements ActivityMetricsLaunchObserverRegistry {
    public final Handler mHandler;
    public final ArrayList<ActivityMetricsLaunchObserver> mList = new ArrayList<>();

    public LaunchObserverRegistryImpl(Looper looper) {
        this.mHandler = new Handler(looper);
    }

    @Override // com.android.server.p014wm.ActivityMetricsLaunchObserverRegistry
    public void registerLaunchObserver(ActivityMetricsLaunchObserver activityMetricsLaunchObserver) {
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.wm.LaunchObserverRegistryImpl$$ExternalSyntheticLambda6
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((LaunchObserverRegistryImpl) obj).handleRegisterLaunchObserver((ActivityMetricsLaunchObserver) obj2);
            }
        }, this, activityMetricsLaunchObserver));
    }

    @Override // com.android.server.p014wm.ActivityMetricsLaunchObserver
    public void onIntentStarted(Intent intent, long j) {
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.wm.LaunchObserverRegistryImpl$$ExternalSyntheticLambda2
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((LaunchObserverRegistryImpl) obj).handleOnIntentStarted((Intent) obj2, ((Long) obj3).longValue());
            }
        }, this, intent, Long.valueOf(j)));
    }

    @Override // com.android.server.p014wm.ActivityMetricsLaunchObserver
    public void onIntentFailed(long j) {
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.wm.LaunchObserverRegistryImpl$$ExternalSyntheticLambda4
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((LaunchObserverRegistryImpl) obj).handleOnIntentFailed(((Long) obj2).longValue());
            }
        }, this, Long.valueOf(j)));
    }

    @Override // com.android.server.p014wm.ActivityMetricsLaunchObserver
    public void onActivityLaunched(long j, ComponentName componentName, int i) {
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: com.android.server.wm.LaunchObserverRegistryImpl$$ExternalSyntheticLambda5
            public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                ((LaunchObserverRegistryImpl) obj).handleOnActivityLaunched(((Long) obj2).longValue(), (ComponentName) obj3, ((Integer) obj4).intValue());
            }
        }, this, Long.valueOf(j), componentName, Integer.valueOf(i)));
    }

    @Override // com.android.server.p014wm.ActivityMetricsLaunchObserver
    public void onActivityLaunchCancelled(long j) {
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.wm.LaunchObserverRegistryImpl$$ExternalSyntheticLambda1
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((LaunchObserverRegistryImpl) obj).handleOnActivityLaunchCancelled(((Long) obj2).longValue());
            }
        }, this, Long.valueOf(j)));
    }

    @Override // com.android.server.p014wm.ActivityMetricsLaunchObserver
    public void onActivityLaunchFinished(long j, ComponentName componentName, long j2) {
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: com.android.server.wm.LaunchObserverRegistryImpl$$ExternalSyntheticLambda0
            public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                ((LaunchObserverRegistryImpl) obj).handleOnActivityLaunchFinished(((Long) obj2).longValue(), (ComponentName) obj3, ((Long) obj4).longValue());
            }
        }, this, Long.valueOf(j), componentName, Long.valueOf(j2)));
    }

    @Override // com.android.server.p014wm.ActivityMetricsLaunchObserver
    public void onReportFullyDrawn(long j, long j2) {
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.wm.LaunchObserverRegistryImpl$$ExternalSyntheticLambda3
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((LaunchObserverRegistryImpl) obj).handleOnReportFullyDrawn(((Long) obj2).longValue(), ((Long) obj3).longValue());
            }
        }, this, Long.valueOf(j), Long.valueOf(j2)));
    }

    public final void handleRegisterLaunchObserver(ActivityMetricsLaunchObserver activityMetricsLaunchObserver) {
        this.mList.add(activityMetricsLaunchObserver);
    }

    public final void handleOnIntentStarted(Intent intent, long j) {
        for (int i = 0; i < this.mList.size(); i++) {
            this.mList.get(i).onIntentStarted(intent, j);
        }
    }

    public final void handleOnIntentFailed(long j) {
        for (int i = 0; i < this.mList.size(); i++) {
            this.mList.get(i).onIntentFailed(j);
        }
    }

    public final void handleOnActivityLaunched(long j, ComponentName componentName, int i) {
        for (int i2 = 0; i2 < this.mList.size(); i2++) {
            this.mList.get(i2).onActivityLaunched(j, componentName, i);
        }
    }

    public final void handleOnActivityLaunchCancelled(long j) {
        for (int i = 0; i < this.mList.size(); i++) {
            this.mList.get(i).onActivityLaunchCancelled(j);
        }
    }

    public final void handleOnActivityLaunchFinished(long j, ComponentName componentName, long j2) {
        for (int i = 0; i < this.mList.size(); i++) {
            this.mList.get(i).onActivityLaunchFinished(j, componentName, j2);
        }
    }

    public final void handleOnReportFullyDrawn(long j, long j2) {
        for (int i = 0; i < this.mList.size(); i++) {
            this.mList.get(i).onReportFullyDrawn(j, j2);
        }
    }
}
