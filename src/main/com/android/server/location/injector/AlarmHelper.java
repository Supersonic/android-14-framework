package com.android.server.location.injector;

import android.app.AlarmManager;
import android.os.WorkSource;
import com.android.internal.util.Preconditions;
/* loaded from: classes.dex */
public abstract class AlarmHelper {
    public abstract void cancel(AlarmManager.OnAlarmListener onAlarmListener);

    public abstract void setDelayedAlarmInternal(long j, AlarmManager.OnAlarmListener onAlarmListener, WorkSource workSource);

    public final void setDelayedAlarm(long j, AlarmManager.OnAlarmListener onAlarmListener, WorkSource workSource) {
        Preconditions.checkArgument(j > 0);
        setDelayedAlarmInternal(j, onAlarmListener, workSource);
    }
}
