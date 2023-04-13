package android.app;

import android.annotation.SystemApi;
import android.app.IAlarmListener;
import android.content.Context;
import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.p008os.WorkSource;
import android.text.TextUtils;
import android.util.Log;
import android.util.proto.ProtoOutputStream;
import com.android.i18n.timezone.ZoneInfoDb;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class AlarmManager {
    public static final String ACTION_NEXT_ALARM_CLOCK_CHANGED = "android.app.action.NEXT_ALARM_CLOCK_CHANGED";
    public static final String ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED = "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED";
    public static final int ELAPSED_REALTIME = 3;
    public static final int ELAPSED_REALTIME_WAKEUP = 2;
    public static final long ENABLE_USE_EXACT_ALARM = 218533173;
    public static final long ENFORCE_MINIMUM_WINDOW_ON_INEXACT_ALARMS = 185199076;
    public static final long EXACT_LISTENER_ALARMS_DROPPED_ON_CACHED = 265195908;
    public static final int FLAG_ALLOW_WHILE_IDLE = 4;
    public static final int FLAG_ALLOW_WHILE_IDLE_COMPAT = 32;
    public static final int FLAG_ALLOW_WHILE_IDLE_UNRESTRICTED = 8;
    public static final int FLAG_IDLE_UNTIL = 16;
    public static final int FLAG_PRIORITIZE = 64;
    public static final int FLAG_STANDALONE = 1;
    public static final int FLAG_WAKE_FROM_IDLE = 2;
    private static final String GENERATED_TAG_PREFIX = "$android.alarm.generated";
    public static final long INTERVAL_DAY = 86400000;
    public static final long INTERVAL_FIFTEEN_MINUTES = 900000;
    public static final long INTERVAL_HALF_DAY = 43200000;
    public static final long INTERVAL_HALF_HOUR = 1800000;
    public static final long INTERVAL_HOUR = 3600000;
    public static final long REQUIRE_EXACT_ALARM_PERMISSION = 171306433;
    public static final int RTC = 1;
    public static final int RTC_WAKEUP = 0;
    public static final long SCHEDULE_EXACT_ALARM_DENIED_BY_DEFAULT = 226439802;
    public static final long SCHEDULE_EXACT_ALARM_DOES_NOT_ELEVATE_BUCKET = 262645982;
    private static final String TAG = "AlarmManager";
    public static final long WINDOW_EXACT = 0;
    public static final long WINDOW_HEURISTIC = -1;
    private static WeakHashMap<OnAlarmListener, WeakReference<ListenerWrapper>> sWrappers;
    private final boolean mAlwaysExact;
    private final Context mContext;
    private final Handler mMainThreadHandler;
    private final String mPackageName;
    private final IAlarmManager mService;
    private final int mTargetSdkVersion;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface AlarmType {
    }

    /* loaded from: classes.dex */
    public interface OnAlarmListener {
        void onAlarm();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class ListenerWrapper extends IAlarmListener.Stub implements Runnable {
        IAlarmCompleteListener mCompletion;
        Executor mExecutor;
        final OnAlarmListener mListener;

        public ListenerWrapper(OnAlarmListener listener) {
            this.mListener = listener;
        }

        void setExecutor(Executor e) {
            this.mExecutor = e;
        }

        public void cancel() {
            try {
                AlarmManager.this.mService.remove(null, this);
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }

        @Override // android.app.IAlarmListener
        public void doAlarm(IAlarmCompleteListener alarmManager) {
            this.mCompletion = alarmManager;
            this.mExecutor.execute(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.mListener.onAlarm();
                try {
                    this.mCompletion.alarmComplete(this);
                } catch (Exception e) {
                    Log.m109e(AlarmManager.TAG, "Unable to report completion to Alarm Manager!", e);
                }
            } catch (Throwable th) {
                try {
                    this.mCompletion.alarmComplete(this);
                } catch (Exception e2) {
                    Log.m109e(AlarmManager.TAG, "Unable to report completion to Alarm Manager!", e2);
                }
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlarmManager(IAlarmManager service, Context ctx) {
        this.mService = service;
        this.mContext = ctx;
        this.mPackageName = ctx.getPackageName();
        int i = ctx.getApplicationInfo().targetSdkVersion;
        this.mTargetSdkVersion = i;
        this.mAlwaysExact = i < 19;
        this.mMainThreadHandler = new Handler(ctx.getMainLooper());
    }

    private long legacyExactLength() {
        return this.mAlwaysExact ? 0L : -1L;
    }

    public void set(int type, long triggerAtMillis, PendingIntent operation) {
        setImpl(type, triggerAtMillis, legacyExactLength(), 0L, 0, operation, (OnAlarmListener) null, (String) null, (Handler) null, (WorkSource) null, (AlarmClockInfo) null);
    }

    public void set(int type, long triggerAtMillis, String tag, OnAlarmListener listener, Handler targetHandler) {
        setImpl(type, triggerAtMillis, legacyExactLength(), 0L, 0, (PendingIntent) null, listener, tag, targetHandler, (WorkSource) null, (AlarmClockInfo) null);
    }

    public void setRepeating(int type, long triggerAtMillis, long intervalMillis, PendingIntent operation) {
        setImpl(type, triggerAtMillis, legacyExactLength(), intervalMillis, 0, operation, (OnAlarmListener) null, (String) null, (Handler) null, (WorkSource) null, (AlarmClockInfo) null);
    }

    public void setWindow(int type, long windowStartMillis, long windowLengthMillis, PendingIntent operation) {
        setImpl(type, windowStartMillis, windowLengthMillis, 0L, 0, operation, (OnAlarmListener) null, (String) null, (Handler) null, (WorkSource) null, (AlarmClockInfo) null);
    }

    public void setWindow(int type, long windowStartMillis, long windowLengthMillis, String tag, OnAlarmListener listener, Handler targetHandler) {
        setImpl(type, windowStartMillis, windowLengthMillis, 0L, 0, (PendingIntent) null, listener, tag, targetHandler, (WorkSource) null, (AlarmClockInfo) null);
    }

    public void setWindow(int type, long windowStartMillis, long windowLengthMillis, String tag, Executor executor, OnAlarmListener listener) {
        setImpl(type, windowStartMillis, windowLengthMillis, 0L, 0, (PendingIntent) null, listener, tag, executor, (WorkSource) null, (AlarmClockInfo) null);
    }

    @SystemApi
    public void setWindow(int type, long windowStartMillis, long windowLengthMillis, String tag, Executor executor, WorkSource workSource, OnAlarmListener listener) {
        setImpl(type, windowStartMillis, windowLengthMillis, 0L, 0, (PendingIntent) null, listener, tag, executor, workSource, (AlarmClockInfo) null);
    }

    @SystemApi
    public void setPrioritized(int type, long windowStartMillis, long windowLengthMillis, String tag, Executor executor, OnAlarmListener listener) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(listener);
        setImpl(type, windowStartMillis, windowLengthMillis, 0L, 64, (PendingIntent) null, listener, tag, executor, (WorkSource) null, (AlarmClockInfo) null);
    }

    public void setExact(int type, long triggerAtMillis, PendingIntent operation) {
        setImpl(type, triggerAtMillis, 0L, 0L, 0, operation, (OnAlarmListener) null, (String) null, (Handler) null, (WorkSource) null, (AlarmClockInfo) null);
    }

    public void setExact(int type, long triggerAtMillis, String tag, OnAlarmListener listener, Handler targetHandler) {
        setImpl(type, triggerAtMillis, 0L, 0L, 0, (PendingIntent) null, listener, tag, targetHandler, (WorkSource) null, (AlarmClockInfo) null);
    }

    public void setIdleUntil(int type, long triggerAtMillis, String tag, OnAlarmListener listener, Handler targetHandler) {
        setImpl(type, triggerAtMillis, 0L, 0L, 16, (PendingIntent) null, listener, tag, targetHandler, (WorkSource) null, (AlarmClockInfo) null);
    }

    public void setAlarmClock(AlarmClockInfo info, PendingIntent operation) {
        setImpl(0, info.getTriggerTime(), 0L, 0L, 0, operation, (OnAlarmListener) null, (String) null, (Handler) null, (WorkSource) null, info);
    }

    @SystemApi
    public void set(int type, long triggerAtMillis, long windowMillis, long intervalMillis, PendingIntent operation, WorkSource workSource) {
        setImpl(type, triggerAtMillis, windowMillis, intervalMillis, 0, operation, (OnAlarmListener) null, (String) null, (Handler) null, workSource, (AlarmClockInfo) null);
    }

    public void set(int type, long triggerAtMillis, long windowMillis, long intervalMillis, String tag, OnAlarmListener listener, Handler targetHandler, WorkSource workSource) {
        setImpl(type, triggerAtMillis, windowMillis, intervalMillis, 0, (PendingIntent) null, listener, tag, targetHandler, workSource, (AlarmClockInfo) null);
    }

    private static String makeTag(long triggerMillis, WorkSource ws) {
        StringBuilder tagBuilder = new StringBuilder(GENERATED_TAG_PREFIX);
        tagBuilder.append(":");
        int attributionUid = (ws == null || ws.isEmpty()) ? Process.myUid() : ws.getAttributionUid();
        tagBuilder.append(UserHandle.formatUid(attributionUid));
        tagBuilder.append(":");
        tagBuilder.append(triggerMillis);
        return tagBuilder.toString();
    }

    @SystemApi
    @Deprecated
    public void set(int type, long triggerAtMillis, long windowMillis, long intervalMillis, OnAlarmListener listener, Handler targetHandler, WorkSource workSource) {
        setImpl(type, triggerAtMillis, windowMillis, intervalMillis, 0, (PendingIntent) null, listener, makeTag(triggerAtMillis, workSource), targetHandler, workSource, (AlarmClockInfo) null);
    }

    @SystemApi
    public void setExact(int type, long triggerAtMillis, String tag, Executor executor, WorkSource workSource, OnAlarmListener listener) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(workSource);
        Objects.requireNonNull(listener);
        setImpl(type, triggerAtMillis, 0L, 0L, 0, (PendingIntent) null, listener, tag, executor, workSource, (AlarmClockInfo) null);
    }

    private void setImpl(int type, long triggerAtMillis, long windowMillis, long intervalMillis, int flags, PendingIntent operation, OnAlarmListener listener, String listenerTag, Handler targetHandler, WorkSource workSource, AlarmClockInfo alarmClock) {
        Handler handlerToUse = targetHandler != null ? targetHandler : this.mMainThreadHandler;
        setImpl(type, triggerAtMillis, windowMillis, intervalMillis, flags, operation, listener, listenerTag, new HandlerExecutor(handlerToUse), workSource, alarmClock);
    }

    private void setImpl(int type, long triggerAtMillis, long windowMillis, long intervalMillis, int flags, PendingIntent operation, OnAlarmListener listener, String listenerTag, Executor targetExecutor, WorkSource workSource, AlarmClockInfo alarmClock) {
        long triggerAtMillis2;
        ListenerWrapper recipientWrapper;
        if (triggerAtMillis >= 0) {
            triggerAtMillis2 = triggerAtMillis;
        } else {
            triggerAtMillis2 = 0;
        }
        ListenerWrapper recipientWrapper2 = null;
        if (listener == null) {
            recipientWrapper = null;
        } else {
            synchronized (AlarmManager.class) {
                try {
                    if (sWrappers == null) {
                        sWrappers = new WeakHashMap<>();
                    }
                    WeakReference<ListenerWrapper> weakRef = sWrappers.get(listener);
                    if (weakRef != null) {
                        recipientWrapper2 = weakRef.get();
                    }
                    if (recipientWrapper2 == null) {
                        recipientWrapper2 = new ListenerWrapper(listener);
                        sWrappers.put(listener, new WeakReference<>(recipientWrapper2));
                    }
                } catch (Throwable th) {
                    th = th;
                    while (true) {
                        try {
                            break;
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    }
                    throw th;
                }
            }
            recipientWrapper2.setExecutor(targetExecutor);
            recipientWrapper = recipientWrapper2;
        }
        try {
            this.mService.set(this.mPackageName, type, triggerAtMillis2, windowMillis, intervalMillis, flags, operation, recipientWrapper, listenerTag, workSource, alarmClock);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setInexactRepeating(int type, long triggerAtMillis, long intervalMillis, PendingIntent operation) {
        setImpl(type, triggerAtMillis, -1L, intervalMillis, 0, operation, (OnAlarmListener) null, (String) null, (Handler) null, (WorkSource) null, (AlarmClockInfo) null);
    }

    public void setAndAllowWhileIdle(int type, long triggerAtMillis, PendingIntent operation) {
        setImpl(type, triggerAtMillis, -1L, 0L, 4, operation, (OnAlarmListener) null, (String) null, (Handler) null, (WorkSource) null, (AlarmClockInfo) null);
    }

    public void setExactAndAllowWhileIdle(int type, long triggerAtMillis, PendingIntent operation) {
        setImpl(type, triggerAtMillis, 0L, 0L, 4, operation, (OnAlarmListener) null, (String) null, (Handler) null, (WorkSource) null, (AlarmClockInfo) null);
    }

    @SystemApi
    public void setExactAndAllowWhileIdle(int type, long triggerAtMillis, String tag, Executor executor, WorkSource workSource, OnAlarmListener listener) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(listener);
        setImpl(type, triggerAtMillis, 0L, 0L, 4, (PendingIntent) null, listener, tag, executor, workSource, (AlarmClockInfo) null);
    }

    public void cancel(PendingIntent operation) {
        if (operation == null) {
            if (this.mTargetSdkVersion >= 24) {
                throw new NullPointerException("cancel() called with a null PendingIntent");
            }
            Log.m110e(TAG, "cancel() called with a null PendingIntent");
            return;
        }
        try {
            this.mService.remove(operation, null);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void cancel(OnAlarmListener listener) {
        WeakReference<ListenerWrapper> weakRef;
        if (listener == null) {
            throw new NullPointerException("cancel() called with a null OnAlarmListener");
        }
        ListenerWrapper wrapper = null;
        synchronized (AlarmManager.class) {
            WeakHashMap<OnAlarmListener, WeakReference<ListenerWrapper>> weakHashMap = sWrappers;
            if (weakHashMap != null && (weakRef = weakHashMap.get(listener)) != null) {
                wrapper = weakRef.get();
            }
        }
        if (wrapper == null) {
            Log.m104w(TAG, "Unrecognized alarm listener " + listener);
        } else {
            wrapper.cancel();
        }
    }

    public void cancelAll() {
        try {
            this.mService.removeAll(this.mContext.getOpPackageName());
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setTime(long millis) {
        try {
            this.mService.setTime(millis);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setTimeZone(String timeZone) {
        if (TextUtils.isEmpty(timeZone)) {
            return;
        }
        if (this.mTargetSdkVersion >= 23) {
            boolean hasTimeZone = ZoneInfoDb.getInstance().hasTimeZone(timeZone);
            if (!hasTimeZone) {
                throw new IllegalArgumentException("Timezone: " + timeZone + " is not an Olson ID");
            }
        }
        try {
            this.mService.setTimeZone(timeZone);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public long getNextWakeFromIdleTime() {
        try {
            return this.mService.getNextWakeFromIdleTime();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public boolean canScheduleExactAlarms() {
        try {
            return this.mService.canScheduleExactAlarms(this.mContext.getOpPackageName());
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public boolean hasScheduleExactAlarm(String packageName, int userId) {
        try {
            return this.mService.hasScheduleExactAlarm(packageName, userId);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    public AlarmClockInfo getNextAlarmClock() {
        return getNextAlarmClock(this.mContext.getUserId());
    }

    public AlarmClockInfo getNextAlarmClock(int userId) {
        try {
            return this.mService.getNextAlarmClock(userId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    /* loaded from: classes.dex */
    public static final class AlarmClockInfo implements Parcelable {
        public static final Parcelable.Creator<AlarmClockInfo> CREATOR = new Parcelable.Creator<AlarmClockInfo>() { // from class: android.app.AlarmManager.AlarmClockInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public AlarmClockInfo createFromParcel(Parcel in) {
                return new AlarmClockInfo(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public AlarmClockInfo[] newArray(int size) {
                return new AlarmClockInfo[size];
            }
        };
        private final PendingIntent mShowIntent;
        private final long mTriggerTime;

        public AlarmClockInfo(long triggerTime, PendingIntent showIntent) {
            this.mTriggerTime = triggerTime;
            this.mShowIntent = showIntent;
        }

        AlarmClockInfo(Parcel in) {
            this.mTriggerTime = in.readLong();
            this.mShowIntent = (PendingIntent) in.readParcelable(PendingIntent.class.getClassLoader());
        }

        public long getTriggerTime() {
            return this.mTriggerTime;
        }

        public PendingIntent getShowIntent() {
            return this.mShowIntent;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.mTriggerTime);
            dest.writeParcelable(this.mShowIntent, flags);
        }

        public void dumpDebug(ProtoOutputStream proto, long fieldId) {
            long token = proto.start(fieldId);
            proto.write(1112396529665L, this.mTriggerTime);
            PendingIntent pendingIntent = this.mShowIntent;
            if (pendingIntent != null) {
                pendingIntent.dumpDebug(proto, 1146756268034L);
            }
            proto.end(token);
        }
    }
}
