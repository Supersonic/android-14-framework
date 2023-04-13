package com.android.internal.telephony.uicc;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.util.NotificationChannelController;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
@VisibleForTesting
/* loaded from: classes.dex */
public class InstallCarrierAppUtils {
    private static CarrierAppInstallReceiver sCarrierAppInstallReceiver;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void showNotification(Context context, String str) {
        String string;
        Resources system = Resources.getSystem();
        String string2 = system.getString(17040475);
        String appNameFromPackageName = getAppNameFromPackageName(context, str);
        if (TextUtils.isEmpty(appNameFromPackageName)) {
            string = system.getString(17040473);
        } else {
            string = system.getString(17040474, appNameFromPackageName);
        }
        String string3 = system.getString(17040472);
        getNotificationManager(context).notify(str, 12, new Notification.Builder(context, NotificationChannelController.CHANNEL_ID_SIM).setContentTitle(string2).setContentText(string).setSmallIcon(17302868).addAction(new Notification.Action.Builder((Icon) null, string3, PendingIntent.getActivity(context, 0, getPlayStoreIntent(str), 201326592)).build()).setOngoing(Settings.Global.getInt(context.getContentResolver(), "install_carrier_app_notification_persistent", 1) == 1).setVisibility(-1).build());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void hideAllNotifications(Context context) {
        NotificationManager notificationManager = getNotificationManager(context);
        StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();
        if (activeNotifications == null) {
            return;
        }
        for (StatusBarNotification statusBarNotification : activeNotifications) {
            if (statusBarNotification.getId() == 12) {
                notificationManager.cancel(statusBarNotification.getTag(), statusBarNotification.getId());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void hideNotification(Context context, String str) {
        getNotificationManager(context).cancel(str, 12);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Intent getPlayStoreIntent(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("market://details?id=" + str));
        intent.addFlags(268435456);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void showNotificationIfNotInstalledDelayed(Context context, String str, long j) {
        ((AlarmManager) context.getSystemService("alarm")).set(3, SystemClock.elapsedRealtime() + j, PendingIntent.getBroadcast(context, 0, ShowInstallAppNotificationReceiver.get(context, str), 67108864));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void registerPackageInstallReceiver(Context context) {
        if (sCarrierAppInstallReceiver == null) {
            sCarrierAppInstallReceiver = new CarrierAppInstallReceiver();
            Context applicationContext = context.getApplicationContext();
            IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
            intentFilter.addDataScheme("package");
            applicationContext.registerReceiver(sCarrierAppInstallReceiver, intentFilter);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void unregisterPackageInstallReceiver(Context context) {
        if (sCarrierAppInstallReceiver == null) {
            return;
        }
        context.getApplicationContext().unregisterReceiver(sCarrierAppInstallReceiver);
        sCarrierAppInstallReceiver = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isPackageInstallNotificationActive(Context context) {
        for (StatusBarNotification statusBarNotification : getNotificationManager(context).getActiveNotifications()) {
            if (statusBarNotification.getId() == 12) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getAppNameFromPackageName(Context context, String str) {
        return getAppNameFromPackageName(str, Settings.Global.getString(context.getContentResolver(), "carrier_app_names"));
    }

    @VisibleForTesting
    public static String getAppNameFromPackageName(String str, String str2) {
        String lowerCase = str.toLowerCase(Locale.ROOT);
        if (TextUtils.isEmpty(str2)) {
            return null;
        }
        List<String> asList = Arrays.asList(str2.split("\\s*;\\s*"));
        if (asList.isEmpty()) {
            return null;
        }
        for (String str3 : asList) {
            String[] split = str3.split("\\s*:\\s*");
            if (split.length == 2 && split[0].equals(lowerCase)) {
                return split[1];
            }
        }
        return null;
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService("notification");
    }
}
