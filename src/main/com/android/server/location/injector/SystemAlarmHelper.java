package com.android.server.location.injector;

import android.app.AlarmManager;
import android.content.Context;
import android.os.SystemClock;
import android.os.WorkSource;
import com.android.server.FgThread;
import java.util.Objects;
/* loaded from: classes.dex */
public class SystemAlarmHelper extends AlarmHelper {
    public final Context mContext;

    public SystemAlarmHelper(Context context) {
        this.mContext = context;
    }

    @Override // com.android.server.location.injector.AlarmHelper
    public void setDelayedAlarmInternal(long j, AlarmManager.OnAlarmListener onAlarmListener, WorkSource workSource) {
        AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService(AlarmManager.class);
        Objects.requireNonNull(alarmManager);
        alarmManager.set(2, SystemClock.elapsedRealtime() + j, 0L, 0L, onAlarmListener, FgThread.getHandler(), workSource);
    }

    @Override // com.android.server.location.injector.AlarmHelper
    public void cancel(AlarmManager.OnAlarmListener onAlarmListener) {
        AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService(AlarmManager.class);
        Objects.requireNonNull(alarmManager);
        alarmManager.cancel(onAlarmListener);
    }
}
