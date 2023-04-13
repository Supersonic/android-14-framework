package com.android.server.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.service.notification.Condition;
import android.service.notification.IConditionProvider;
import android.service.notification.ZenModeConfig;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Slog;
import com.android.server.notification.NotificationManagerService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public class CountdownConditionProvider extends SystemConditionProviderService {
    public boolean mConnected;
    public boolean mIsAlarm;
    public long mTime;
    public static final boolean DEBUG = Log.isLoggable("ConditionProviders", 3);
    public static final ComponentName COMPONENT = new ComponentName(PackageManagerShellCommandDataLoader.PACKAGE, CountdownConditionProvider.class.getName());
    public static final String ACTION = CountdownConditionProvider.class.getName();
    public final Context mContext = this;
    public final Receiver mReceiver = new Receiver();

    @Override // com.android.server.notification.SystemConditionProviderService
    public void onBootComplete() {
    }

    @Override // android.service.notification.ConditionProviderService
    public void onUnsubscribe(Uri uri) {
    }

    public CountdownConditionProvider() {
        if (DEBUG) {
            Slog.d("ConditionProviders.CCP", "new CountdownConditionProvider()");
        }
    }

    @Override // com.android.server.notification.SystemConditionProviderService
    public ComponentName getComponent() {
        return COMPONENT;
    }

    @Override // com.android.server.notification.SystemConditionProviderService
    public boolean isValidConditionId(Uri uri) {
        return ZenModeConfig.isValidCountdownConditionId(uri);
    }

    @Override // com.android.server.notification.SystemConditionProviderService
    public void attachBase(Context context) {
        attachBaseContext(context);
    }

    @Override // com.android.server.notification.SystemConditionProviderService
    public IConditionProvider asInterface() {
        return onBind(null);
    }

    @Override // com.android.server.notification.SystemConditionProviderService
    public void dump(PrintWriter printWriter, NotificationManagerService.DumpFilter dumpFilter) {
        printWriter.println("    CountdownConditionProvider:");
        printWriter.print("      mConnected=");
        printWriter.println(this.mConnected);
        printWriter.print("      mTime=");
        printWriter.println(this.mTime);
    }

    @Override // android.service.notification.ConditionProviderService
    public void onConnected() {
        if (DEBUG) {
            Slog.d("ConditionProviders.CCP", "onConnected");
        }
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter(ACTION), 2);
        this.mConnected = true;
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        if (DEBUG) {
            Slog.d("ConditionProviders.CCP", "onDestroy");
        }
        if (this.mConnected) {
            this.mContext.unregisterReceiver(this.mReceiver);
        }
        this.mConnected = false;
    }

    @Override // android.service.notification.ConditionProviderService
    public void onSubscribe(Uri uri) {
        boolean z = DEBUG;
        if (z) {
            Slog.d("ConditionProviders.CCP", "onSubscribe " + uri);
        }
        this.mTime = ZenModeConfig.tryParseCountdownConditionId(uri);
        this.mIsAlarm = ZenModeConfig.isValidCountdownToAlarmConditionId(uri);
        AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        PendingIntent pendingIntent = getPendingIntent(uri);
        alarmManager.cancel(pendingIntent);
        if (this.mTime > 0) {
            long currentTimeMillis = System.currentTimeMillis();
            CharSequence relativeTimeSpanString = DateUtils.getRelativeTimeSpanString(this.mTime, currentTimeMillis, 60000L);
            long j = this.mTime;
            if (j <= currentTimeMillis) {
                notifyCondition(newCondition(j, this.mIsAlarm, 0));
            } else {
                alarmManager.setExact(0, j, pendingIntent);
            }
            if (z) {
                Object[] objArr = new Object[6];
                long j2 = this.mTime;
                objArr[0] = j2 <= currentTimeMillis ? "Not scheduling" : "Scheduling";
                objArr[1] = ACTION;
                objArr[2] = SystemConditionProviderService.m47ts(j2);
                objArr[3] = Long.valueOf(this.mTime - currentTimeMillis);
                objArr[4] = relativeTimeSpanString;
                objArr[5] = SystemConditionProviderService.m47ts(currentTimeMillis);
                Slog.d("ConditionProviders.CCP", String.format("%s %s for %s, %s in the future (%s), now=%s", objArr));
            }
        }
    }

    public PendingIntent getPendingIntent(Uri uri) {
        return PendingIntent.getBroadcast(this.mContext, 100, new Intent(ACTION).setPackage(PackageManagerShellCommandDataLoader.PACKAGE).putExtra("condition_id", uri).setFlags(1073741824), 201326592);
    }

    /* loaded from: classes2.dex */
    public final class Receiver extends BroadcastReceiver {
        public Receiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (CountdownConditionProvider.ACTION.equals(intent.getAction())) {
                Uri uri = (Uri) intent.getParcelableExtra("condition_id", Uri.class);
                boolean isValidCountdownToAlarmConditionId = ZenModeConfig.isValidCountdownToAlarmConditionId(uri);
                long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(uri);
                if (CountdownConditionProvider.DEBUG) {
                    Slog.d("ConditionProviders.CCP", "Countdown condition fired: " + uri);
                }
                if (tryParseCountdownConditionId > 0) {
                    CountdownConditionProvider.this.notifyCondition(CountdownConditionProvider.newCondition(tryParseCountdownConditionId, isValidCountdownToAlarmConditionId, 0));
                }
            }
        }
    }

    public static final Condition newCondition(long j, boolean z, int i) {
        return new Condition(ZenModeConfig.toCountdownConditionId(j, z), "", "", "", 0, i, 1);
    }

    public static String tryParseDescription(Uri uri) {
        long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(uri);
        if (tryParseCountdownConditionId == 0) {
            return null;
        }
        long currentTimeMillis = System.currentTimeMillis();
        return String.format("Scheduled for %s, %s in the future (%s), now=%s", SystemConditionProviderService.m47ts(tryParseCountdownConditionId), Long.valueOf(tryParseCountdownConditionId - currentTimeMillis), DateUtils.getRelativeTimeSpanString(tryParseCountdownConditionId, currentTimeMillis, 60000L), SystemConditionProviderService.m47ts(currentTimeMillis));
    }
}
