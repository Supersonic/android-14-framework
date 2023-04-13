package com.android.server.devicestate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public class DeviceStateNotificationController extends BroadcastReceiver {
    @VisibleForTesting
    static final String CHANNEL_ID = "DeviceStateManager";
    @VisibleForTesting
    static final String INTENT_ACTION_CANCEL_STATE = "com.android.server.devicestate.INTENT_ACTION_CANCEL_STATE";
    @VisibleForTesting
    static final int NOTIFICATION_ID = 1;
    @VisibleForTesting
    static final String NOTIFICATION_TAG = "DeviceStateManager";
    public final Runnable mCancelStateRunnable;
    public final Context mContext;
    public final Handler mHandler;
    public final SparseArray<NotificationInfo> mNotificationInfos;
    public final NotificationManager mNotificationManager;
    public final PackageManager mPackageManager;

    public DeviceStateNotificationController(Context context, Handler handler, Runnable runnable) {
        this(context, handler, runnable, getNotificationInfos(context), context.getPackageManager(), (NotificationManager) context.getSystemService(NotificationManager.class));
    }

    @VisibleForTesting
    public DeviceStateNotificationController(Context context, Handler handler, Runnable runnable, SparseArray<NotificationInfo> sparseArray, PackageManager packageManager, NotificationManager notificationManager) {
        this.mContext = context;
        this.mHandler = handler;
        this.mCancelStateRunnable = runnable;
        this.mNotificationInfos = sparseArray;
        this.mPackageManager = packageManager;
        this.mNotificationManager = notificationManager;
        context.registerReceiver(this, new IntentFilter(INTENT_ACTION_CANCEL_STATE), "android.permission.CONTROL_DEVICE_STATE", handler, 4);
    }

    public void showStateActiveNotificationIfNeeded(int i, int i2) {
        NotificationInfo notificationInfo = this.mNotificationInfos.get(i);
        if (notificationInfo == null || !notificationInfo.hasActiveNotification()) {
            return;
        }
        String applicationLabel = getApplicationLabel(i2);
        if (applicationLabel != null) {
            showNotification(notificationInfo.name, notificationInfo.activeNotificationTitle, String.format(notificationInfo.activeNotificationContent, applicationLabel), true, 17302445);
            return;
        }
        Slog.e("DeviceStateNotificationController", "Cannot determine the requesting app name when showing state active notification. uid=" + i2 + ", state=" + i);
    }

    public void showThermalCriticalNotificationIfNeeded(int i) {
        NotificationInfo notificationInfo = this.mNotificationInfos.get(i);
        if (notificationInfo == null || !notificationInfo.hasThermalCriticalNotification()) {
            return;
        }
        showNotification(notificationInfo.name, notificationInfo.thermalCriticalNotificationTitle, notificationInfo.thermalCriticalNotificationContent, false, 17302891);
    }

    public void cancelNotification(int i) {
        if (this.mNotificationInfos.contains(i)) {
            this.mNotificationManager.cancel("DeviceStateManager", 1);
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !INTENT_ACTION_CANCEL_STATE.equals(intent.getAction())) {
            return;
        }
        this.mCancelStateRunnable.run();
    }

    public final void showNotification(String str, String str2, String str3, boolean z, int i) {
        NotificationChannel notificationChannel = new NotificationChannel("DeviceStateManager", str, 4);
        Notification.Builder category = new Notification.Builder(this.mContext, "DeviceStateManager").setSmallIcon(i).setContentTitle(str2).setContentText(str3).setSubText(str).setLocalOnly(true).setOngoing(z).setCategory("sys");
        if (z) {
            category.addAction(new Notification.Action.Builder((Icon) null, this.mContext.getString(17040128), PendingIntent.getBroadcast(this.mContext, 0, new Intent(INTENT_ACTION_CANCEL_STATE).setPackage(this.mContext.getPackageName()), 67108864)).build());
        }
        this.mNotificationManager.createNotificationChannel(notificationChannel);
        this.mNotificationManager.notify("DeviceStateManager", 1, category.build());
    }

    public static SparseArray<NotificationInfo> getNotificationInfos(Context context) {
        SparseArray<NotificationInfo> sparseArray = new SparseArray<>();
        int[] intArray = context.getResources().getIntArray(17236171);
        String[] stringArray = context.getResources().getStringArray(17236170);
        String[] stringArray2 = context.getResources().getStringArray(17236169);
        String[] stringArray3 = context.getResources().getStringArray(17236168);
        String[] stringArray4 = context.getResources().getStringArray(17236173);
        String[] stringArray5 = context.getResources().getStringArray(17236172);
        if (intArray.length != stringArray.length || intArray.length != stringArray2.length || intArray.length != stringArray3.length || intArray.length != stringArray4.length || intArray.length != stringArray5.length) {
            throw new IllegalStateException("The length of state identifiers and notification texts must match!");
        }
        for (int i = 0; i < intArray.length; i++) {
            int i2 = intArray[i];
            if (i2 != -1) {
                sparseArray.put(i2, new NotificationInfo(stringArray[i], stringArray2[i], stringArray3[i], stringArray4[i], stringArray5[i]));
            }
        }
        return sparseArray;
    }

    public final String getApplicationLabel(int i) {
        try {
            return this.mPackageManager.getApplicationInfo(this.mPackageManager.getNameForUid(i), PackageManager.ApplicationInfoFlags.of(0L)).loadLabel(this.mPackageManager).toString();
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class NotificationInfo {
        public final String activeNotificationContent;
        public final String activeNotificationTitle;
        public final String name;
        public final String thermalCriticalNotificationContent;
        public final String thermalCriticalNotificationTitle;

        public NotificationInfo(String str, String str2, String str3, String str4, String str5) {
            this.name = str;
            this.activeNotificationTitle = str2;
            this.activeNotificationContent = str3;
            this.thermalCriticalNotificationTitle = str4;
            this.thermalCriticalNotificationContent = str5;
        }

        public boolean hasActiveNotification() {
            String str = this.activeNotificationTitle;
            return str != null && str.length() > 0;
        }

        public boolean hasThermalCriticalNotification() {
            String str = this.thermalCriticalNotificationTitle;
            return str != null && str.length() > 0;
        }
    }
}
