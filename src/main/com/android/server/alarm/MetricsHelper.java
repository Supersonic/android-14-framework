package com.android.server.alarm;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.StatsManager;
import android.content.Context;
import android.os.SystemClock;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.FrameworkStatsLog;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class MetricsHelper {
    public final Context mContext;
    public final Object mLock;

    public static int reasonToStatsReason(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            return i != 5 ? 0 : 6;
                        }
                        return 5;
                    }
                    return 4;
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }

    public MetricsHelper(Context context, Object obj) {
        this.mContext = context;
        this.mLock = obj;
    }

    public void registerPuller(final Supplier<AlarmStore> supplier) {
        ((StatsManager) this.mContext.getSystemService(StatsManager.class)).setPullAtomCallback((int) FrameworkStatsLog.PENDING_ALARM_INFO, (StatsManager.PullAtomMetadata) null, BackgroundThread.getExecutor(), new StatsManager.StatsPullAtomCallback() { // from class: com.android.server.alarm.MetricsHelper$$ExternalSyntheticLambda0
            public final int onPullAtom(int i, List list) {
                int lambda$registerPuller$12;
                lambda$registerPuller$12 = MetricsHelper.this.lambda$registerPuller$12(supplier, i, list);
                return lambda$registerPuller$12;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$registerPuller$12(Supplier supplier, int i, List list) {
        if (i != 10106) {
            throw new UnsupportedOperationException("Unknown tag" + i);
        }
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        synchronized (this.mLock) {
            AlarmStore alarmStore = (AlarmStore) supplier.get();
            list.add(FrameworkStatsLog.buildStatsEvent(i, alarmStore.size(), alarmStore.getCount(new Predicate() { // from class: com.android.server.alarm.MetricsHelper$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$registerPuller$0;
                    lambda$registerPuller$0 = MetricsHelper.lambda$registerPuller$0((Alarm) obj);
                    return lambda$registerPuller$0;
                }
            }), alarmStore.getCount(new Predicate() { // from class: com.android.server.alarm.MetricsHelper$$ExternalSyntheticLambda4
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean z;
                    z = ((Alarm) obj).wakeup;
                    return z;
                }
            }), alarmStore.getCount(new Predicate() { // from class: com.android.server.alarm.MetricsHelper$$ExternalSyntheticLambda5
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$registerPuller$2;
                    lambda$registerPuller$2 = MetricsHelper.lambda$registerPuller$2((Alarm) obj);
                    return lambda$registerPuller$2;
                }
            }), alarmStore.getCount(new Predicate() { // from class: com.android.server.alarm.MetricsHelper$$ExternalSyntheticLambda6
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$registerPuller$3;
                    lambda$registerPuller$3 = MetricsHelper.lambda$registerPuller$3((Alarm) obj);
                    return lambda$registerPuller$3;
                }
            }), alarmStore.getCount(new Predicate() { // from class: com.android.server.alarm.MetricsHelper$$ExternalSyntheticLambda7
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$registerPuller$4;
                    lambda$registerPuller$4 = MetricsHelper.lambda$registerPuller$4((Alarm) obj);
                    return lambda$registerPuller$4;
                }
            }), alarmStore.getCount(new Predicate() { // from class: com.android.server.alarm.MetricsHelper$$ExternalSyntheticLambda8
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$registerPuller$5;
                    lambda$registerPuller$5 = MetricsHelper.lambda$registerPuller$5((Alarm) obj);
                    return lambda$registerPuller$5;
                }
            }), alarmStore.getCount(new Predicate() { // from class: com.android.server.alarm.MetricsHelper$$ExternalSyntheticLambda9
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$registerPuller$6;
                    lambda$registerPuller$6 = MetricsHelper.lambda$registerPuller$6((Alarm) obj);
                    return lambda$registerPuller$6;
                }
            }), alarmStore.getCount(new Predicate() { // from class: com.android.server.alarm.MetricsHelper$$ExternalSyntheticLambda10
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$registerPuller$7;
                    lambda$registerPuller$7 = MetricsHelper.lambda$registerPuller$7((Alarm) obj);
                    return lambda$registerPuller$7;
                }
            }), alarmStore.getCount(new Predicate() { // from class: com.android.server.alarm.MetricsHelper$$ExternalSyntheticLambda11
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$registerPuller$8;
                    lambda$registerPuller$8 = MetricsHelper.lambda$registerPuller$8(elapsedRealtime, (Alarm) obj);
                    return lambda$registerPuller$8;
                }
            }), alarmStore.getCount(new Predicate() { // from class: com.android.server.alarm.MetricsHelper$$ExternalSyntheticLambda12
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$registerPuller$9;
                    lambda$registerPuller$9 = MetricsHelper.lambda$registerPuller$9((Alarm) obj);
                    return lambda$registerPuller$9;
                }
            }), alarmStore.getCount(new Predicate() { // from class: com.android.server.alarm.MetricsHelper$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$registerPuller$10;
                    lambda$registerPuller$10 = MetricsHelper.lambda$registerPuller$10((Alarm) obj);
                    return lambda$registerPuller$10;
                }
            }), alarmStore.getCount(new Predicate() { // from class: com.android.server.alarm.MetricsHelper$$ExternalSyntheticLambda3
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$registerPuller$11;
                    lambda$registerPuller$11 = MetricsHelper.lambda$registerPuller$11((Alarm) obj);
                    return lambda$registerPuller$11;
                }
            })));
        }
        return 0;
    }

    public static /* synthetic */ boolean lambda$registerPuller$0(Alarm alarm) {
        return alarm.windowLength == 0;
    }

    public static /* synthetic */ boolean lambda$registerPuller$2(Alarm alarm) {
        return (alarm.flags & 4) != 0;
    }

    public static /* synthetic */ boolean lambda$registerPuller$3(Alarm alarm) {
        return (alarm.flags & 64) != 0;
    }

    public static /* synthetic */ boolean lambda$registerPuller$4(Alarm alarm) {
        PendingIntent pendingIntent = alarm.operation;
        return pendingIntent != null && pendingIntent.isForegroundService();
    }

    public static /* synthetic */ boolean lambda$registerPuller$5(Alarm alarm) {
        PendingIntent pendingIntent = alarm.operation;
        return pendingIntent != null && pendingIntent.isActivity();
    }

    public static /* synthetic */ boolean lambda$registerPuller$6(Alarm alarm) {
        PendingIntent pendingIntent = alarm.operation;
        return pendingIntent != null && pendingIntent.isService();
    }

    public static /* synthetic */ boolean lambda$registerPuller$7(Alarm alarm) {
        return alarm.listener != null;
    }

    public static /* synthetic */ boolean lambda$registerPuller$8(long j, Alarm alarm) {
        return alarm.getRequestedElapsed() > j + 31536000000L;
    }

    public static /* synthetic */ boolean lambda$registerPuller$9(Alarm alarm) {
        return alarm.repeatInterval != 0;
    }

    public static /* synthetic */ boolean lambda$registerPuller$10(Alarm alarm) {
        return alarm.alarmClock != null;
    }

    public static /* synthetic */ boolean lambda$registerPuller$11(Alarm alarm) {
        return AlarmManagerService.isRtc(alarm.type);
    }

    public static void pushAlarmScheduled(Alarm alarm, int i) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.ALARM_SCHEDULED, alarm.uid, alarm.windowLength == 0, alarm.wakeup, (alarm.flags & 4) != 0, alarm.alarmClock != null, alarm.repeatInterval != 0, reasonToStatsReason(alarm.mExactAllowReason), AlarmManagerService.isRtc(alarm.type), ActivityManager.processStateAmToProto(i));
    }

    public static void pushAlarmBatchDelivered(int i, int i2, int[] iArr, int[] iArr2, int[] iArr3) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.ALARM_BATCH_DELIVERED, i, i2, iArr, iArr2, iArr3);
    }
}
