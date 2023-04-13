package com.android.internal.notification;

import android.app.INotificationManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.app.admin.DevicePolicyResources;
import android.content.Context;
import android.content.p001pm.ParceledListSlice;
import android.media.AudioAttributes;
import android.net.VpnManager;
import android.p008os.RemoteException;
import com.android.internal.C4057R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes4.dex */
public class SystemNotificationChannels {
    @Deprecated
    public static String VIRTUAL_KEYBOARD = "VIRTUAL_KEYBOARD";
    public static String PHYSICAL_KEYBOARD = "PHYSICAL_KEYBOARD";
    public static String SECURITY = "SECURITY";
    public static String CAR_MODE = "CAR_MODE";
    public static String ACCOUNT = "ACCOUNT";
    public static String DEVELOPER = "DEVELOPER";
    public static String DEVELOPER_IMPORTANT = "DEVELOPER_IMPORTANT";
    public static String UPDATES = "UPDATES";
    public static String NETWORK_STATUS = "NETWORK_STATUS";
    public static String NETWORK_ALERTS = "NETWORK_ALERTS";
    public static String NETWORK_AVAILABLE = "NETWORK_AVAILABLE";
    public static String VPN = VpnManager.NOTIFICATION_CHANNEL_VPN;
    @Deprecated
    public static String DEVICE_ADMIN_DEPRECATED = "DEVICE_ADMIN";
    public static String DEVICE_ADMIN = "DEVICE_ADMIN_ALERTS";
    public static String ALERTS = "ALERTS";
    public static String RETAIL_MODE = "RETAIL_MODE";
    public static String USB = "USB";
    public static String FOREGROUND_SERVICE = "FOREGROUND_SERVICE";
    public static String HEAVY_WEIGHT_APP = "HEAVY_WEIGHT_APP";
    @Deprecated
    public static String SYSTEM_CHANGES_DEPRECATED = "SYSTEM_CHANGES";
    public static String SYSTEM_CHANGES = "SYSTEM_CHANGES_ALERTS";
    public static String DO_NOT_DISTURB = "DO_NOT_DISTURB";
    public static String ACCESSIBILITY_MAGNIFICATION = "ACCESSIBILITY_MAGNIFICATION";
    public static String ACCESSIBILITY_SECURITY_POLICY = "ACCESSIBILITY_SECURITY_POLICY";
    public static String ABUSIVE_BACKGROUND_APPS = "ABUSIVE_BACKGROUND_APPS";

    public static void createAll(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(NotificationManager.class);
        List<NotificationChannel> channelsList = new ArrayList<>();
        NotificationChannel physicalKeyboardChannel = new NotificationChannel(PHYSICAL_KEYBOARD, context.getString(C4057R.string.notification_channel_physical_keyboard), 2);
        physicalKeyboardChannel.setBlockable(true);
        channelsList.add(physicalKeyboardChannel);
        NotificationChannel security = new NotificationChannel(SECURITY, context.getString(C4057R.string.notification_channel_security), 2);
        channelsList.add(security);
        NotificationChannel car = new NotificationChannel(CAR_MODE, context.getString(C4057R.string.notification_channel_car_mode), 2);
        car.setBlockable(true);
        channelsList.add(car);
        channelsList.add(newAccountChannel(context));
        NotificationChannel developer = new NotificationChannel(DEVELOPER, context.getString(C4057R.string.notification_channel_developer), 2);
        developer.setBlockable(true);
        channelsList.add(developer);
        NotificationChannel developerImportant = new NotificationChannel(DEVELOPER_IMPORTANT, context.getString(C4057R.string.notification_channel_developer_important), 4);
        developer.setBlockable(true);
        channelsList.add(developerImportant);
        NotificationChannel updates = new NotificationChannel(UPDATES, context.getString(C4057R.string.notification_channel_updates), 2);
        channelsList.add(updates);
        NotificationChannel network = new NotificationChannel(NETWORK_STATUS, context.getString(C4057R.string.notification_channel_network_status), 2);
        network.setBlockable(true);
        channelsList.add(network);
        NotificationChannel networkAlertsChannel = new NotificationChannel(NETWORK_ALERTS, context.getString(C4057R.string.notification_channel_network_alerts), 4);
        networkAlertsChannel.setBlockable(true);
        channelsList.add(networkAlertsChannel);
        NotificationChannel networkAvailable = new NotificationChannel(NETWORK_AVAILABLE, context.getString(C4057R.string.notification_channel_network_available), 2);
        networkAvailable.setBlockable(true);
        channelsList.add(networkAvailable);
        NotificationChannel vpn = new NotificationChannel(VPN, context.getString(C4057R.string.notification_channel_vpn), 2);
        channelsList.add(vpn);
        NotificationChannel deviceAdmin = new NotificationChannel(DEVICE_ADMIN, getDeviceAdminNotificationChannelName(context), 4);
        channelsList.add(deviceAdmin);
        NotificationChannel alertsChannel = new NotificationChannel(ALERTS, context.getString(C4057R.string.notification_channel_alerts), 3);
        channelsList.add(alertsChannel);
        NotificationChannel retail = new NotificationChannel(RETAIL_MODE, context.getString(C4057R.string.notification_channel_retail_mode), 2);
        channelsList.add(retail);
        NotificationChannel usb = new NotificationChannel(USB, context.getString(C4057R.string.notification_channel_usb), 1);
        channelsList.add(usb);
        NotificationChannel foregroundChannel = new NotificationChannel(FOREGROUND_SERVICE, context.getString(C4057R.string.notification_channel_foreground_service), 2);
        foregroundChannel.setBlockable(true);
        channelsList.add(foregroundChannel);
        NotificationChannel heavyWeightChannel = new NotificationChannel(HEAVY_WEIGHT_APP, context.getString(C4057R.string.notification_channel_heavy_weight_app), 3);
        heavyWeightChannel.setShowBadge(false);
        heavyWeightChannel.setSound(null, new AudioAttributes.Builder().setContentType(4).setUsage(10).build());
        channelsList.add(heavyWeightChannel);
        NotificationChannel systemChanges = new NotificationChannel(SYSTEM_CHANGES, context.getString(C4057R.string.notification_channel_system_changes), 3);
        systemChanges.setSound(null, new AudioAttributes.Builder().setContentType(4).setUsage(5).build());
        channelsList.add(systemChanges);
        NotificationChannel dndChanges = new NotificationChannel(DO_NOT_DISTURB, context.getString(C4057R.string.notification_channel_do_not_disturb), 2);
        channelsList.add(dndChanges);
        NotificationChannel newFeaturePrompt = new NotificationChannel(ACCESSIBILITY_MAGNIFICATION, context.getString(C4057R.string.notification_channel_accessibility_magnification), 4);
        newFeaturePrompt.setBlockable(true);
        channelsList.add(newFeaturePrompt);
        NotificationChannel accessibilitySecurityPolicyChannel = new NotificationChannel(ACCESSIBILITY_SECURITY_POLICY, context.getString(C4057R.string.notification_channel_accessibility_security_policy), 2);
        channelsList.add(accessibilitySecurityPolicyChannel);
        NotificationChannel abusiveBackgroundAppsChannel = new NotificationChannel(ABUSIVE_BACKGROUND_APPS, context.getString(C4057R.string.notification_channel_abusive_bg_apps), 2);
        channelsList.add(abusiveBackgroundAppsChannel);
        nm.createNotificationChannels(channelsList);
    }

    private static String getDeviceAdminNotificationChannelName(final Context context) {
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        return dpm.getResources().getString(DevicePolicyResources.Strings.Core.NOTIFICATION_CHANNEL_DEVICE_ADMIN, new Supplier() { // from class: com.android.internal.notification.SystemNotificationChannels$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String string;
                string = Context.this.getString(C4057R.string.notification_channel_device_admin);
                return string;
            }
        });
    }

    public static void removeDeprecated(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(NotificationManager.class);
        nm.deleteNotificationChannel(VIRTUAL_KEYBOARD);
        nm.deleteNotificationChannel(DEVICE_ADMIN_DEPRECATED);
        nm.deleteNotificationChannel(SYSTEM_CHANGES_DEPRECATED);
    }

    public static void createAccountChannelForPackage(String pkg, int uid, Context context) {
        INotificationManager iNotificationManager = NotificationManager.getService();
        try {
            iNotificationManager.createNotificationChannelsForPackage(pkg, uid, new ParceledListSlice(Arrays.asList(newAccountChannel(context))));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private static NotificationChannel newAccountChannel(Context context) {
        return new NotificationChannel(ACCOUNT, context.getString(C4057R.string.notification_channel_account), 2);
    }

    private SystemNotificationChannels() {
    }
}
