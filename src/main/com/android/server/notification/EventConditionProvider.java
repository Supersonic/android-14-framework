package com.android.server.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.notification.Condition;
import android.service.notification.IConditionProvider;
import android.service.notification.ZenModeConfig;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.notification.CalendarTracker;
import com.android.server.notification.NotificationManagerService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes2.dex */
public class EventConditionProvider extends SystemConditionProviderService {
    public static final String ACTION_EVALUATE;
    public static final String SIMPLE_NAME;
    public boolean mBootComplete;
    public boolean mConnected;
    public long mNextAlarmTime;
    public boolean mRegistered;
    public final HandlerThread mThread;
    public final Handler mWorker;
    public static final boolean DEBUG = Log.isLoggable("ConditionProviders", 3);
    public static final ComponentName COMPONENT = new ComponentName(PackageManagerShellCommandDataLoader.PACKAGE, EventConditionProvider.class.getName());
    public final Context mContext = this;
    public final ArraySet<Uri> mSubscriptions = new ArraySet<>();
    public final SparseArray<CalendarTracker> mTrackers = new SparseArray<>();
    public final CalendarTracker.Callback mTrackerCallback = new CalendarTracker.Callback() { // from class: com.android.server.notification.EventConditionProvider.2
        @Override // com.android.server.notification.CalendarTracker.Callback
        public void onChanged() {
            if (EventConditionProvider.DEBUG) {
                Slog.d("ConditionProviders.ECP", "mTrackerCallback.onChanged");
            }
            EventConditionProvider.this.mWorker.removeCallbacks(EventConditionProvider.this.mEvaluateSubscriptionsW);
            EventConditionProvider.this.mWorker.postDelayed(EventConditionProvider.this.mEvaluateSubscriptionsW, 2000L);
        }
    };
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.server.notification.EventConditionProvider.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (EventConditionProvider.DEBUG) {
                Slog.d("ConditionProviders.ECP", "onReceive " + intent.getAction());
            }
            EventConditionProvider.this.evaluateSubscriptions();
        }
    };
    public final Runnable mEvaluateSubscriptionsW = new Runnable() { // from class: com.android.server.notification.EventConditionProvider.4
        @Override // java.lang.Runnable
        public void run() {
            EventConditionProvider.this.evaluateSubscriptionsW();
        }
    };

    static {
        String simpleName = EventConditionProvider.class.getSimpleName();
        SIMPLE_NAME = simpleName;
        ACTION_EVALUATE = simpleName + ".EVALUATE";
    }

    public EventConditionProvider() {
        if (DEBUG) {
            Slog.d("ConditionProviders.ECP", "new " + SIMPLE_NAME + "()");
        }
        HandlerThread handlerThread = new HandlerThread("ConditionProviders.ECP", 10);
        this.mThread = handlerThread;
        handlerThread.start();
        this.mWorker = new Handler(handlerThread.getLooper());
    }

    @Override // com.android.server.notification.SystemConditionProviderService
    public ComponentName getComponent() {
        return COMPONENT;
    }

    @Override // com.android.server.notification.SystemConditionProviderService
    public boolean isValidConditionId(Uri uri) {
        return ZenModeConfig.isValidEventConditionId(uri);
    }

    @Override // com.android.server.notification.SystemConditionProviderService
    public void dump(PrintWriter printWriter, NotificationManagerService.DumpFilter dumpFilter) {
        printWriter.print("    ");
        printWriter.print(SIMPLE_NAME);
        printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
        printWriter.print("      mConnected=");
        printWriter.println(this.mConnected);
        printWriter.print("      mRegistered=");
        printWriter.println(this.mRegistered);
        printWriter.print("      mBootComplete=");
        printWriter.println(this.mBootComplete);
        SystemConditionProviderService.dumpUpcomingTime(printWriter, "mNextAlarmTime", this.mNextAlarmTime, System.currentTimeMillis());
        synchronized (this.mSubscriptions) {
            printWriter.println("      mSubscriptions=");
            Iterator<Uri> it = this.mSubscriptions.iterator();
            while (it.hasNext()) {
                printWriter.print("        ");
                printWriter.println(it.next());
            }
        }
        printWriter.println("      mTrackers=");
        for (int i = 0; i < this.mTrackers.size(); i++) {
            printWriter.print("        user=");
            printWriter.println(this.mTrackers.keyAt(i));
            this.mTrackers.valueAt(i).dump("          ", printWriter);
        }
    }

    @Override // com.android.server.notification.SystemConditionProviderService
    public void onBootComplete() {
        if (DEBUG) {
            Slog.d("ConditionProviders.ECP", "onBootComplete");
        }
        if (this.mBootComplete) {
            return;
        }
        this.mBootComplete = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.notification.EventConditionProvider.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                EventConditionProvider.this.reloadTrackers();
            }
        }, intentFilter);
        reloadTrackers();
    }

    @Override // android.service.notification.ConditionProviderService
    public void onConnected() {
        if (DEBUG) {
            Slog.d("ConditionProviders.ECP", "onConnected");
        }
        this.mConnected = true;
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        if (DEBUG) {
            Slog.d("ConditionProviders.ECP", "onDestroy");
        }
        this.mConnected = false;
    }

    @Override // android.service.notification.ConditionProviderService
    public void onSubscribe(Uri uri) {
        if (DEBUG) {
            Slog.d("ConditionProviders.ECP", "onSubscribe " + uri);
        }
        if (!ZenModeConfig.isValidEventConditionId(uri)) {
            notifyCondition(createCondition(uri, 0));
            return;
        }
        synchronized (this.mSubscriptions) {
            if (this.mSubscriptions.add(uri)) {
                evaluateSubscriptions();
            }
        }
    }

    @Override // android.service.notification.ConditionProviderService
    public void onUnsubscribe(Uri uri) {
        if (DEBUG) {
            Slog.d("ConditionProviders.ECP", "onUnsubscribe " + uri);
        }
        synchronized (this.mSubscriptions) {
            if (this.mSubscriptions.remove(uri)) {
                evaluateSubscriptions();
            }
        }
    }

    @Override // com.android.server.notification.SystemConditionProviderService
    public void attachBase(Context context) {
        attachBaseContext(context);
    }

    @Override // com.android.server.notification.SystemConditionProviderService
    public IConditionProvider asInterface() {
        return onBind(null);
    }

    public final void reloadTrackers() {
        if (DEBUG) {
            Slog.d("ConditionProviders.ECP", "reloadTrackers");
        }
        for (int i = 0; i < this.mTrackers.size(); i++) {
            this.mTrackers.valueAt(i).setCallback(null);
        }
        this.mTrackers.clear();
        for (UserHandle userHandle : UserManager.get(this.mContext).getUserProfiles()) {
            Context contextForUser = userHandle.isSystem() ? this.mContext : getContextForUser(this.mContext, userHandle);
            if (contextForUser == null) {
                Slog.w("ConditionProviders.ECP", "Unable to create context for user " + userHandle.getIdentifier());
            } else {
                this.mTrackers.put(userHandle.getIdentifier(), new CalendarTracker(this.mContext, contextForUser));
            }
        }
        evaluateSubscriptions();
    }

    public final void evaluateSubscriptions() {
        if (this.mWorker.hasCallbacks(this.mEvaluateSubscriptionsW)) {
            return;
        }
        this.mWorker.post(this.mEvaluateSubscriptionsW);
    }

    public final void evaluateSubscriptionsW() {
        Iterator<Uri> it;
        CalendarTracker.CheckEventResult checkEventResult;
        Iterator<Uri> it2;
        boolean z = DEBUG;
        if (z) {
            Slog.d("ConditionProviders.ECP", "evaluateSubscriptions");
        }
        if (!this.mBootComplete) {
            if (z) {
                Slog.d("ConditionProviders.ECP", "Skipping evaluate before boot complete");
                return;
            }
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        ArrayList<Condition> arrayList = new ArrayList();
        synchronized (this.mSubscriptions) {
            int i = 0;
            for (int i2 = 0; i2 < this.mTrackers.size(); i2++) {
                this.mTrackers.valueAt(i2).setCallback(this.mSubscriptions.isEmpty() ? null : this.mTrackerCallback);
            }
            setRegistered(!this.mSubscriptions.isEmpty());
            Iterator<Uri> it3 = this.mSubscriptions.iterator();
            long j = 0;
            while (it3.hasNext()) {
                Uri next = it3.next();
                ZenModeConfig.EventInfo tryParseEventConditionId = ZenModeConfig.tryParseEventConditionId(next);
                if (tryParseEventConditionId == null) {
                    arrayList.add(createCondition(next, i));
                    it = it3;
                } else {
                    if (tryParseEventConditionId.calName == null) {
                        int i3 = i;
                        checkEventResult = null;
                        while (i3 < this.mTrackers.size()) {
                            CalendarTracker.CheckEventResult checkEvent = this.mTrackers.valueAt(i3).checkEvent(tryParseEventConditionId, currentTimeMillis);
                            if (checkEventResult == null) {
                                it2 = it3;
                                checkEventResult = checkEvent;
                            } else {
                                checkEventResult.inEvent |= checkEvent.inEvent;
                                it2 = it3;
                                checkEventResult.recheckAt = Math.min(checkEventResult.recheckAt, checkEvent.recheckAt);
                            }
                            i3++;
                            it3 = it2;
                        }
                        it = it3;
                    } else {
                        it = it3;
                        int resolveUserId = ZenModeConfig.EventInfo.resolveUserId(tryParseEventConditionId.userId);
                        CalendarTracker calendarTracker = this.mTrackers.get(resolveUserId);
                        if (calendarTracker == null) {
                            Slog.w("ConditionProviders.ECP", "No calendar tracker found for user " + resolveUserId);
                            arrayList.add(createCondition(next, 0));
                        } else {
                            checkEventResult = calendarTracker.checkEvent(tryParseEventConditionId, currentTimeMillis);
                        }
                    }
                    long j2 = checkEventResult.recheckAt;
                    if (j2 != 0 && (j == 0 || j2 < j)) {
                        j = j2;
                    }
                    if (!checkEventResult.inEvent) {
                        i = 0;
                        arrayList.add(createCondition(next, 0));
                    } else {
                        i = 0;
                        arrayList.add(createCondition(next, 1));
                    }
                    it3 = it;
                }
                it3 = it;
                i = 0;
            }
            rescheduleAlarm(currentTimeMillis, j);
        }
        for (Condition condition : arrayList) {
            if (condition != null) {
                notifyCondition(condition);
            }
        }
        if (DEBUG) {
            Slog.d("ConditionProviders.ECP", "evaluateSubscriptions took " + (System.currentTimeMillis() - currentTimeMillis));
        }
    }

    public final void rescheduleAlarm(long j, long j2) {
        this.mNextAlarmTime = j2;
        AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        PendingIntent pendingIntent = getPendingIntent(j2);
        alarmManager.cancel(pendingIntent);
        int i = (j2 > 0L ? 1 : (j2 == 0L ? 0 : -1));
        if (i == 0 || j2 < j) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Not scheduling evaluate: ");
                sb.append(i == 0 ? "no time specified" : "specified time in the past");
                Slog.d("ConditionProviders.ECP", sb.toString());
                return;
            }
            return;
        }
        if (DEBUG) {
            Slog.d("ConditionProviders.ECP", String.format("Scheduling evaluate for %s, in %s, now=%s", SystemConditionProviderService.m47ts(j2), SystemConditionProviderService.formatDuration(j2 - j), SystemConditionProviderService.m47ts(j)));
        }
        alarmManager.setExact(0, j2, pendingIntent);
    }

    public PendingIntent getPendingIntent(long j) {
        return PendingIntent.getBroadcast(this.mContext, 1, new Intent(ACTION_EVALUATE).addFlags(268435456).setPackage(PackageManagerShellCommandDataLoader.PACKAGE).putExtra("time", j), 201326592);
    }

    public final Condition createCondition(Uri uri, int i) {
        return new Condition(uri, "...", "...", "...", 0, i, 2);
    }

    public final void setRegistered(boolean z) {
        if (this.mRegistered == z) {
            return;
        }
        if (DEBUG) {
            Slog.d("ConditionProviders.ECP", "setRegistered " + z);
        }
        this.mRegistered = z;
        if (z) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.TIME_SET");
            intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
            intentFilter.addAction(ACTION_EVALUATE);
            registerReceiver(this.mReceiver, intentFilter, 2);
            return;
        }
        unregisterReceiver(this.mReceiver);
    }

    public static Context getContextForUser(Context context, UserHandle userHandle) {
        try {
            return context.createPackageContextAsUser(context.getPackageName(), 0, userHandle);
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }
}
