package android.app;

import android.Manifest;
import android.annotation.SystemApi;
import android.app.INotificationManager;
import android.app.Notification;
import android.app.Person;
import android.content.ComponentName;
import android.content.Context;
import android.content.PermissionChecker;
import android.content.p001pm.ParceledListSlice;
import android.graphics.drawable.Icon;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.StrictMode;
import android.p008os.UserHandle;
import android.service.notification.Condition;
import android.service.notification.StatusBarNotification;
import android.service.notification.ZenModeConfig;
import android.util.Log;
import android.util.proto.ProtoOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes.dex */
public class NotificationManager {
    public static final String ACTION_APP_BLOCK_STATE_CHANGED = "android.app.action.APP_BLOCK_STATE_CHANGED";
    public static final String ACTION_AUTOMATIC_ZEN_RULE = "android.app.action.AUTOMATIC_ZEN_RULE";
    public static final String ACTION_AUTOMATIC_ZEN_RULE_STATUS_CHANGED = "android.app.action.AUTOMATIC_ZEN_RULE_STATUS_CHANGED";
    @SystemApi
    public static final String ACTION_CLOSE_NOTIFICATION_HANDLER_PANEL = "android.app.action.CLOSE_NOTIFICATION_HANDLER_PANEL";
    public static final String ACTION_EFFECTS_SUPPRESSOR_CHANGED = "android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED";
    public static final String ACTION_INTERRUPTION_FILTER_CHANGED = "android.app.action.INTERRUPTION_FILTER_CHANGED";
    public static final String ACTION_INTERRUPTION_FILTER_CHANGED_INTERNAL = "android.app.action.INTERRUPTION_FILTER_CHANGED_INTERNAL";
    public static final String ACTION_NOTIFICATION_CHANNEL_BLOCK_STATE_CHANGED = "android.app.action.NOTIFICATION_CHANNEL_BLOCK_STATE_CHANGED";
    public static final String ACTION_NOTIFICATION_CHANNEL_GROUP_BLOCK_STATE_CHANGED = "android.app.action.NOTIFICATION_CHANNEL_GROUP_BLOCK_STATE_CHANGED";
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final String ACTION_NOTIFICATION_LISTENER_ENABLED_CHANGED = "android.app.action.NOTIFICATION_LISTENER_ENABLED_CHANGED";
    public static final String ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED = "android.app.action.NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED";
    public static final String ACTION_NOTIFICATION_POLICY_CHANGED = "android.app.action.NOTIFICATION_POLICY_CHANGED";
    @SystemApi
    public static final String ACTION_OPEN_NOTIFICATION_HANDLER_PANEL = "android.app.action.OPEN_NOTIFICATION_HANDLER_PANEL";
    @SystemApi
    public static final String ACTION_TOGGLE_NOTIFICATION_HANDLER_PANEL = "android.app.action.TOGGLE_NOTIFICATION_HANDLER_PANEL";
    public static final int AUTOMATIC_RULE_STATUS_DISABLED = 2;
    public static final int AUTOMATIC_RULE_STATUS_ENABLED = 1;
    public static final int AUTOMATIC_RULE_STATUS_REMOVED = 3;
    public static final int AUTOMATIC_RULE_STATUS_UNKNOWN = -1;
    public static final int BUBBLE_PREFERENCE_ALL = 1;
    public static final int BUBBLE_PREFERENCE_NONE = 0;
    public static final int BUBBLE_PREFERENCE_SELECTED = 2;
    public static final String EXTRA_AUTOMATIC_RULE_ID = "android.app.extra.AUTOMATIC_RULE_ID";
    public static final String EXTRA_AUTOMATIC_ZEN_RULE_ID = "android.app.extra.AUTOMATIC_ZEN_RULE_ID";
    public static final String EXTRA_AUTOMATIC_ZEN_RULE_STATUS = "android.app.extra.AUTOMATIC_ZEN_RULE_STATUS";
    public static final String EXTRA_BLOCKED_STATE = "android.app.extra.BLOCKED_STATE";
    public static final String EXTRA_NOTIFICATION_CHANNEL_GROUP_ID = "android.app.extra.NOTIFICATION_CHANNEL_GROUP_ID";
    public static final String EXTRA_NOTIFICATION_CHANNEL_ID = "android.app.extra.NOTIFICATION_CHANNEL_ID";
    public static final int IMPORTANCE_DEFAULT = 3;
    public static final int IMPORTANCE_HIGH = 4;
    public static final int IMPORTANCE_LOW = 2;
    public static final int IMPORTANCE_MAX = 5;
    public static final int IMPORTANCE_MIN = 1;
    public static final int IMPORTANCE_NONE = 0;
    public static final int IMPORTANCE_UNSPECIFIED = -1000;
    public static final int INTERRUPTION_FILTER_ALARMS = 4;
    public static final int INTERRUPTION_FILTER_ALL = 1;
    public static final int INTERRUPTION_FILTER_NONE = 3;
    public static final int INTERRUPTION_FILTER_PRIORITY = 2;
    public static final int INTERRUPTION_FILTER_UNKNOWN = 0;
    public static final String META_DATA_AUTOMATIC_RULE_TYPE = "android.service.zen.automatic.ruleType";
    public static final String META_DATA_RULE_INSTANCE_LIMIT = "android.service.zen.automatic.ruleInstanceLimit";
    public static final int VISIBILITY_NO_OVERRIDE = -1000;
    private static INotificationManager sService;
    private Context mContext;
    private static String TAG = "NotificationManager";
    private static boolean localLOGV = false;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface AutomaticZenRuleStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface BubblePreference {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Importance {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface InterruptionFilter {
    }

    public static INotificationManager getService() {
        INotificationManager iNotificationManager = sService;
        if (iNotificationManager != null) {
            return iNotificationManager;
        }
        IBinder b = ServiceManager.getService("notification");
        INotificationManager asInterface = INotificationManager.Stub.asInterface(b);
        sService = asInterface;
        return asInterface;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NotificationManager(Context context, Handler handler) {
        this.mContext = context;
    }

    public static NotificationManager from(Context context) {
        return (NotificationManager) context.getSystemService("notification");
    }

    public void notify(int id, Notification notification) {
        notify(null, id, notification);
    }

    public void notify(String tag, int id, Notification notification) {
        notifyAsUser(tag, id, notification, this.mContext.getUser());
    }

    public void notifyAsPackage(String targetPackage, String tag, int id, Notification notification) {
        INotificationManager service = getService();
        String sender = this.mContext.getPackageName();
        try {
            if (localLOGV) {
                Log.m106v(TAG, sender + ": notify(" + id + ", " + notification + NavigationBarInflaterView.KEY_CODE_END);
            }
            service.enqueueNotificationWithTag(targetPackage, sender, tag, id, fixNotification(notification), this.mContext.getUser().getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void notifyAsUser(String tag, int id, Notification notification, UserHandle user) {
        INotificationManager service = getService();
        String pkg = this.mContext.getPackageName();
        try {
            if (localLOGV) {
                Log.m106v(TAG, pkg + ": notify(" + id + ", " + notification + NavigationBarInflaterView.KEY_CODE_END);
            }
            service.enqueueNotificationWithTag(pkg, this.mContext.getOpPackageName(), tag, id, fixNotification(notification), user.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private Notification fixNotification(Notification notification) {
        String pkg = this.mContext.getPackageName();
        Notification.addFieldsFromContext(this.mContext, notification);
        if (notification.sound != null) {
            notification.sound = notification.sound.getCanonicalUri();
            if (StrictMode.vmFileUriExposureEnabled()) {
                notification.sound.checkFileUriExposed("Notification.sound");
            }
        }
        fixLegacySmallIcon(notification, pkg);
        if (this.mContext.getApplicationInfo().targetSdkVersion > 22 && notification.getSmallIcon() == null) {
            throw new IllegalArgumentException("Invalid notification (no valid small icon): " + notification);
        }
        notification.reduceImageSizes(this.mContext);
        return Notification.Builder.maybeCloneStrippedForDelivery(notification);
    }

    private void fixLegacySmallIcon(Notification n, String pkg) {
        if (n.getSmallIcon() == null && n.icon != 0) {
            n.setSmallIcon(Icon.createWithResource(pkg, n.icon));
        }
    }

    public void cancel(int id) {
        cancel(null, id);
    }

    public void cancel(String tag, int id) {
        cancelAsUser(tag, id, this.mContext.getUser());
    }

    public void cancelAsPackage(String targetPackage, String tag, int id) {
        INotificationManager service = getService();
        try {
            service.cancelNotificationWithTag(targetPackage, this.mContext.getOpPackageName(), tag, id, this.mContext.getUser().getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void cancelAsUser(String tag, int id, UserHandle user) {
        INotificationManager service = getService();
        String pkg = this.mContext.getPackageName();
        if (localLOGV) {
            Log.m106v(TAG, pkg + ": cancel(" + id + NavigationBarInflaterView.KEY_CODE_END);
        }
        try {
            service.cancelNotificationWithTag(pkg, this.mContext.getOpPackageName(), tag, id, user.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void cancelAll() {
        INotificationManager service = getService();
        String pkg = this.mContext.getPackageName();
        if (localLOGV) {
            Log.m106v(TAG, pkg + ": cancelAll()");
        }
        try {
            service.cancelAllNotifications(pkg, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setNotificationDelegate(String delegate) {
        INotificationManager service = getService();
        String pkg = this.mContext.getPackageName();
        if (localLOGV) {
            Log.m106v(TAG, pkg + ": cancelAll()");
        }
        try {
            service.setNotificationDelegate(pkg, delegate);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String getNotificationDelegate() {
        INotificationManager service = getService();
        String pkg = this.mContext.getPackageName();
        try {
            return service.getNotificationDelegate(pkg);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean canNotifyAsPackage(String pkg) {
        INotificationManager service = getService();
        try {
            return service.canNotifyAsPackage(this.mContext.getPackageName(), pkg, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean canUseFullScreenIntent() {
        Context context = this.mContext;
        int result = PermissionChecker.checkPermissionForPreflight(context, Manifest.C0000permission.USE_FULL_SCREEN_INTENT, context.getAttributionSource());
        switch (result) {
            case 0:
                return true;
            case 1:
            case 2:
                return false;
            default:
                if (localLOGV) {
                    Log.m106v(TAG, "Unknown PermissionChecker result: " + result);
                }
                return false;
        }
    }

    public void createNotificationChannelGroup(NotificationChannelGroup group) {
        createNotificationChannelGroups(Arrays.asList(group));
    }

    public void createNotificationChannelGroups(List<NotificationChannelGroup> groups) {
        INotificationManager service = getService();
        try {
            service.createNotificationChannelGroups(this.mContext.getPackageName(), new ParceledListSlice(groups));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void createNotificationChannel(NotificationChannel channel) {
        createNotificationChannels(Arrays.asList(channel));
    }

    public void createNotificationChannels(List<NotificationChannel> channels) {
        INotificationManager service = getService();
        try {
            service.createNotificationChannels(this.mContext.getPackageName(), new ParceledListSlice(channels));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public NotificationChannel getNotificationChannel(String channelId) {
        INotificationManager service = getService();
        try {
            return service.getNotificationChannel(this.mContext.getOpPackageName(), this.mContext.getUserId(), this.mContext.getPackageName(), channelId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public NotificationChannel getNotificationChannel(String channelId, String conversationId) {
        INotificationManager service = getService();
        try {
            return service.getConversationNotificationChannel(this.mContext.getOpPackageName(), this.mContext.getUserId(), this.mContext.getPackageName(), channelId, true, conversationId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<NotificationChannel> getNotificationChannels() {
        INotificationManager service = getService();
        try {
            return service.getNotificationChannels(this.mContext.getOpPackageName(), this.mContext.getPackageName(), this.mContext.getUserId()).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void deleteNotificationChannel(String channelId) {
        INotificationManager service = getService();
        try {
            service.deleteNotificationChannel(this.mContext.getPackageName(), channelId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public NotificationChannelGroup getNotificationChannelGroup(String channelGroupId) {
        INotificationManager service = getService();
        try {
            return service.getNotificationChannelGroup(this.mContext.getPackageName(), channelGroupId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<NotificationChannelGroup> getNotificationChannelGroups() {
        INotificationManager service = getService();
        try {
            ParceledListSlice<NotificationChannelGroup> parceledList = service.getNotificationChannelGroups(this.mContext.getPackageName());
            if (parceledList != null) {
                return parceledList.getList();
            }
            return new ArrayList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void deleteNotificationChannelGroup(String groupId) {
        INotificationManager service = getService();
        try {
            service.deleteNotificationChannelGroup(this.mContext.getPackageName(), groupId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void updateNotificationChannel(String pkg, int uid, NotificationChannel channel) {
        INotificationManager service = getService();
        try {
            service.updateNotificationChannelForPackage(pkg, uid, channel);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ComponentName getEffectsSuppressor() {
        INotificationManager service = getService();
        try {
            return service.getEffectsSuppressor();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean matchesCallFilter(Bundle extras) {
        INotificationManager service = getService();
        try {
            return service.matchesCallFilter(extras);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void cleanUpCallersAfter(long timeThreshold) {
        INotificationManager service = getService();
        try {
            service.cleanUpCallersAfter(timeThreshold);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isSystemConditionProviderEnabled(String path) {
        INotificationManager service = getService();
        try {
            return service.isSystemConditionProviderEnabled(path);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setZenMode(int mode, Uri conditionId, String reason) {
        INotificationManager service = getService();
        try {
            service.setZenMode(mode, conditionId, reason);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getZenMode() {
        INotificationManager service = getService();
        try {
            return service.getZenMode();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ZenModeConfig getZenModeConfig() {
        INotificationManager service = getService();
        try {
            return service.getZenModeConfig();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Policy getConsolidatedNotificationPolicy() {
        INotificationManager service = getService();
        try {
            return service.getConsolidatedNotificationPolicy();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getRuleInstanceCount(ComponentName owner) {
        INotificationManager service = getService();
        try {
            return service.getRuleInstanceCount(owner);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Map<String, AutomaticZenRule> getAutomaticZenRules() {
        INotificationManager service = getService();
        try {
            List<ZenModeConfig.ZenRule> rules = service.getZenRules();
            Map<String, AutomaticZenRule> ruleMap = new HashMap<>();
            for (ZenModeConfig.ZenRule rule : rules) {
                AutomaticZenRule azr = new AutomaticZenRule(rule.name, rule.component, rule.configurationActivity, rule.conditionId, rule.zenPolicy, zenModeToInterruptionFilter(rule.zenMode), rule.enabled, rule.creationTime);
                azr.setPackageName(rule.pkg);
                ruleMap.put(rule.f426id, azr);
            }
            return ruleMap;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public AutomaticZenRule getAutomaticZenRule(String id) {
        INotificationManager service = getService();
        try {
            return service.getAutomaticZenRule(id);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String addAutomaticZenRule(AutomaticZenRule automaticZenRule) {
        INotificationManager service = getService();
        try {
            return service.addAutomaticZenRule(automaticZenRule, this.mContext.getPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean updateAutomaticZenRule(String id, AutomaticZenRule automaticZenRule) {
        INotificationManager service = getService();
        try {
            return service.updateAutomaticZenRule(id, automaticZenRule);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setAutomaticZenRuleState(String id, Condition condition) {
        INotificationManager service = getService();
        try {
            service.setAutomaticZenRuleState(id, condition);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean removeAutomaticZenRule(String id) {
        INotificationManager service = getService();
        try {
            return service.removeAutomaticZenRule(id);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean removeAutomaticZenRules(String packageName) {
        INotificationManager service = getService();
        try {
            return service.removeAutomaticZenRules(packageName);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getImportance() {
        INotificationManager service = getService();
        try {
            return service.getPackageImportance(this.mContext.getPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean areNotificationsEnabled() {
        INotificationManager service = getService();
        try {
            return service.areNotificationsEnabled(this.mContext.getPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public boolean areBubblesAllowed() {
        INotificationManager service = getService();
        try {
            return service.areBubblesAllowed(this.mContext.getPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean areBubblesEnabled() {
        INotificationManager service = getService();
        try {
            return service.areBubblesEnabled(this.mContext.getUser());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getBubblePreference() {
        INotificationManager service = getService();
        try {
            return service.getBubblePreferenceForPackage(this.mContext.getPackageName(), Binder.getCallingUid());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void silenceNotificationSound() {
        INotificationManager service = getService();
        try {
            service.silenceNotificationSound();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean areNotificationsPaused() {
        INotificationManager service = getService();
        try {
            return service.isPackagePaused(this.mContext.getPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isNotificationPolicyAccessGranted() {
        INotificationManager service = getService();
        try {
            return service.isNotificationPolicyAccessGranted(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isNotificationListenerAccessGranted(ComponentName listener) {
        INotificationManager service = getService();
        try {
            return service.isNotificationListenerAccessGranted(listener);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isNotificationAssistantAccessGranted(ComponentName assistant) {
        INotificationManager service = getService();
        try {
            return service.isNotificationAssistantAccessGranted(assistant);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean shouldHideSilentStatusBarIcons() {
        INotificationManager service = getService();
        try {
            return service.shouldHideSilentStatusIcons(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public List<String> getAllowedAssistantAdjustments() {
        INotificationManager service = getService();
        try {
            return service.getAllowedAssistantAdjustments(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isNotificationPolicyAccessGrantedForPackage(String pkg) {
        INotificationManager service = getService();
        try {
            return service.isNotificationPolicyAccessGrantedForPackage(pkg);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<String> getEnabledNotificationListenerPackages() {
        INotificationManager service = getService();
        try {
            return service.getEnabledNotificationListenerPackages();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Policy getNotificationPolicy() {
        INotificationManager service = getService();
        try {
            return service.getNotificationPolicy(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setNotificationPolicy(Policy policy) {
        checkRequired("policy", policy);
        INotificationManager service = getService();
        try {
            service.setNotificationPolicy(this.mContext.getOpPackageName(), policy);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setNotificationPolicyAccessGranted(String pkg, boolean granted) {
        INotificationManager service = getService();
        try {
            service.setNotificationPolicyAccessGranted(pkg, granted);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setNotificationListenerAccessGranted(ComponentName listener, boolean granted) {
        setNotificationListenerAccessGranted(listener, granted, true);
    }

    @SystemApi
    public void setNotificationListenerAccessGranted(ComponentName listener, boolean granted, boolean userSet) {
        INotificationManager service = getService();
        try {
            service.setNotificationListenerAccessGranted(listener, granted, userSet);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setNotificationListenerAccessGrantedForUser(ComponentName listener, int userId, boolean granted) {
        INotificationManager service = getService();
        try {
            service.setNotificationListenerAccessGrantedForUser(listener, userId, granted, true);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setNotificationAssistantAccessGranted(ComponentName assistant, boolean granted) {
        INotificationManager service = getService();
        try {
            service.setNotificationAssistantAccessGranted(assistant, granted);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public List<ComponentName> getEnabledNotificationListeners() {
        return getEnabledNotificationListeners(this.mContext.getUserId());
    }

    public List<ComponentName> getEnabledNotificationListeners(int userId) {
        INotificationManager service = getService();
        try {
            return service.getEnabledNotificationListeners(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public ComponentName getAllowedNotificationAssistant() {
        INotificationManager service = getService();
        try {
            return service.getAllowedNotificationAssistant();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public boolean hasEnabledNotificationListener(String packageName, UserHandle userHandle) {
        INotificationManager service = getService();
        try {
            return service.hasEnabledNotificationListener(packageName, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private static void checkRequired(String name, Object value) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    public void setToastRateLimitingEnabled(boolean enable) {
        INotificationManager service = getService();
        try {
            service.setToastRateLimitingEnabled(enable);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* loaded from: classes.dex */
    public static class Policy implements Parcelable {
        public static final int CONVERSATION_SENDERS_ANYONE = 1;
        public static final int CONVERSATION_SENDERS_IMPORTANT = 2;
        public static final int CONVERSATION_SENDERS_NONE = 3;
        public static final int CONVERSATION_SENDERS_UNSET = -1;
        public static final int PRIORITY_CATEGORY_ALARMS = 32;
        public static final int PRIORITY_CATEGORY_CALLS = 8;
        public static final int PRIORITY_CATEGORY_CONVERSATIONS = 256;
        public static final int PRIORITY_CATEGORY_EVENTS = 2;
        public static final int PRIORITY_CATEGORY_MEDIA = 64;
        public static final int PRIORITY_CATEGORY_MESSAGES = 4;
        public static final int PRIORITY_CATEGORY_REMINDERS = 1;
        public static final int PRIORITY_CATEGORY_REPEAT_CALLERS = 16;
        public static final int PRIORITY_CATEGORY_SYSTEM = 128;
        public static final int PRIORITY_SENDERS_ANY = 0;
        public static final int PRIORITY_SENDERS_CONTACTS = 1;
        public static final int PRIORITY_SENDERS_STARRED = 2;
        public static final int STATE_CHANNELS_BYPASSING_DND = 1;
        public static final int STATE_UNSET = -1;
        public static final int SUPPRESSED_EFFECTS_UNSET = -1;
        public static final int SUPPRESSED_EFFECT_AMBIENT = 128;
        public static final int SUPPRESSED_EFFECT_BADGE = 64;
        public static final int SUPPRESSED_EFFECT_FULL_SCREEN_INTENT = 4;
        public static final int SUPPRESSED_EFFECT_LIGHTS = 8;
        public static final int SUPPRESSED_EFFECT_NOTIFICATION_LIST = 256;
        public static final int SUPPRESSED_EFFECT_PEEK = 16;
        @Deprecated
        public static final int SUPPRESSED_EFFECT_SCREEN_OFF = 1;
        @Deprecated
        public static final int SUPPRESSED_EFFECT_SCREEN_ON = 2;
        public static final int SUPPRESSED_EFFECT_STATUS_BAR = 32;
        public final int priorityCallSenders;
        public final int priorityCategories;
        public final int priorityConversationSenders;
        public final int priorityMessageSenders;
        public final int state;
        public final int suppressedVisualEffects;
        public static final int[] ALL_PRIORITY_CATEGORIES = {32, 64, 128, 1, 2, 4, 8, 16, 256};
        private static final int[] ALL_SUPPRESSED_EFFECTS = {1, 2, 4, 8, 16, 32, 64, 128, 256};
        public static final Parcelable.Creator<Policy> CREATOR = new Parcelable.Creator<Policy>() { // from class: android.app.NotificationManager.Policy.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Policy createFromParcel(Parcel in) {
                return new Policy(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Policy[] newArray(int size) {
                return new Policy[size];
            }
        };

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface ConversationSenders {
        }

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface PrioritySenders {
        }

        public Policy(int priorityCategories, int priorityCallSenders, int priorityMessageSenders) {
            this(priorityCategories, priorityCallSenders, priorityMessageSenders, -1, -1, -1);
        }

        public Policy(int priorityCategories, int priorityCallSenders, int priorityMessageSenders, int suppressedVisualEffects) {
            this(priorityCategories, priorityCallSenders, priorityMessageSenders, suppressedVisualEffects, -1, -1);
        }

        public Policy(int priorityCategories, int priorityCallSenders, int priorityMessageSenders, int suppressedVisualEffects, int priorityConversationSenders) {
            this(priorityCategories, priorityCallSenders, priorityMessageSenders, suppressedVisualEffects, -1, priorityConversationSenders);
        }

        public Policy(int priorityCategories, int priorityCallSenders, int priorityMessageSenders, int suppressedVisualEffects, int state, int priorityConversationSenders) {
            this.priorityCategories = priorityCategories;
            this.priorityCallSenders = priorityCallSenders;
            this.priorityMessageSenders = priorityMessageSenders;
            this.suppressedVisualEffects = suppressedVisualEffects;
            this.state = state;
            this.priorityConversationSenders = priorityConversationSenders;
        }

        public Policy(Parcel source) {
            this(source.readInt(), source.readInt(), source.readInt(), source.readInt(), source.readInt(), source.readInt());
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.priorityCategories);
            dest.writeInt(this.priorityCallSenders);
            dest.writeInt(this.priorityMessageSenders);
            dest.writeInt(this.suppressedVisualEffects);
            dest.writeInt(this.state);
            dest.writeInt(this.priorityConversationSenders);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.priorityCategories), Integer.valueOf(this.priorityCallSenders), Integer.valueOf(this.priorityMessageSenders), Integer.valueOf(this.suppressedVisualEffects), Integer.valueOf(this.state), Integer.valueOf(this.priorityConversationSenders));
        }

        public boolean equals(Object o) {
            if (o instanceof Policy) {
                if (o == this) {
                    return true;
                }
                Policy other = (Policy) o;
                return other.priorityCategories == this.priorityCategories && other.priorityCallSenders == this.priorityCallSenders && other.priorityMessageSenders == this.priorityMessageSenders && suppressedVisualEffectsEqual(this.suppressedVisualEffects, other.suppressedVisualEffects) && other.state == this.state && other.priorityConversationSenders == this.priorityConversationSenders;
            }
            return false;
        }

        private boolean suppressedVisualEffectsEqual(int suppressedEffects, int otherSuppressedVisualEffects) {
            if (suppressedEffects == otherSuppressedVisualEffects) {
                return true;
            }
            if ((suppressedEffects & 2) != 0) {
                suppressedEffects |= 16;
            }
            if ((suppressedEffects & 1) != 0) {
                suppressedEffects = suppressedEffects | 4 | 8 | 128;
            }
            if ((otherSuppressedVisualEffects & 2) != 0) {
                otherSuppressedVisualEffects |= 16;
            }
            if ((otherSuppressedVisualEffects & 1) != 0) {
                otherSuppressedVisualEffects = otherSuppressedVisualEffects | 4 | 8 | 128;
            }
            if ((suppressedEffects & 2) != (otherSuppressedVisualEffects & 2)) {
                int currSuppressedEffects = (suppressedEffects & 2) != 0 ? otherSuppressedVisualEffects : suppressedEffects;
                if ((currSuppressedEffects & 16) == 0) {
                    return false;
                }
            }
            int currSuppressedEffects2 = suppressedEffects & 1;
            if (currSuppressedEffects2 != (otherSuppressedVisualEffects & 1)) {
                int currSuppressedEffects3 = (suppressedEffects & 1) != 0 ? otherSuppressedVisualEffects : suppressedEffects;
                if ((currSuppressedEffects3 & 4) == 0 || (currSuppressedEffects3 & 8) == 0 || (currSuppressedEffects3 & 128) == 0) {
                    return false;
                }
            }
            int currSuppressedEffects4 = suppressedEffects & (-3);
            int thisWithoutOldEffects = currSuppressedEffects4 & (-2);
            int otherWithoutOldEffects = otherSuppressedVisualEffects & (-3) & (-2);
            return thisWithoutOldEffects == otherWithoutOldEffects;
        }

        public String toString() {
            String str;
            StringBuilder append = new StringBuilder().append("NotificationManager.Policy[priorityCategories=").append(priorityCategoriesToString(this.priorityCategories)).append(",priorityCallSenders=").append(prioritySendersToString(this.priorityCallSenders)).append(",priorityMessageSenders=").append(prioritySendersToString(this.priorityMessageSenders)).append(",priorityConvSenders=").append(conversationSendersToString(this.priorityConversationSenders)).append(",suppressedVisualEffects=").append(suppressedEffectsToString(this.suppressedVisualEffects)).append(",areChannelsBypassingDnd=");
            int i = this.state;
            if (i == -1) {
                str = "unset";
            } else if ((i & 1) != 0) {
                str = "true";
            } else {
                str = "false";
            }
            return append.append(str).append(NavigationBarInflaterView.SIZE_MOD_END).toString();
        }

        public void dumpDebug(ProtoOutputStream proto, long fieldId) {
            long pToken = proto.start(fieldId);
            bitwiseToProtoEnum(proto, 2259152797697L, this.priorityCategories);
            proto.write(1159641169922L, this.priorityCallSenders);
            proto.write(1159641169923L, this.priorityMessageSenders);
            bitwiseToProtoEnum(proto, 2259152797700L, this.suppressedVisualEffects);
            proto.end(pToken);
        }

        private static void bitwiseToProtoEnum(ProtoOutputStream proto, long fieldId, int data) {
            int i = 1;
            while (data > 0) {
                if ((data & 1) == 1) {
                    proto.write(fieldId, i);
                }
                i++;
                data >>>= 1;
            }
        }

        public static int getAllSuppressedVisualEffects() {
            int effects = 0;
            int i = 0;
            while (true) {
                int[] iArr = ALL_SUPPRESSED_EFFECTS;
                if (i < iArr.length) {
                    effects |= iArr[i];
                    i++;
                } else {
                    return effects;
                }
            }
        }

        public static boolean areAllVisualEffectsSuppressed(int effects) {
            int i = 0;
            while (true) {
                int[] iArr = ALL_SUPPRESSED_EFFECTS;
                if (i < iArr.length) {
                    int effect = iArr[i];
                    if ((effects & effect) != 0) {
                        i++;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            }
        }

        private static int toggleEffects(int currentEffects, int[] effects, boolean suppress) {
            for (int effect : effects) {
                if (suppress) {
                    currentEffects |= effect;
                } else {
                    currentEffects &= ~effect;
                }
            }
            return currentEffects;
        }

        public static String suppressedEffectsToString(int effects) {
            if (effects <= 0) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (true) {
                int[] iArr = ALL_SUPPRESSED_EFFECTS;
                if (i >= iArr.length) {
                    break;
                }
                int effect = iArr[i];
                if ((effects & effect) != 0) {
                    if (sb.length() > 0) {
                        sb.append(',');
                    }
                    sb.append(effectToString(effect));
                }
                effects &= ~effect;
                i++;
            }
            if (effects != 0) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append("UNKNOWN_").append(effects);
            }
            return sb.toString();
        }

        public static String priorityCategoriesToString(int priorityCategories) {
            if (priorityCategories == 0) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (true) {
                int[] iArr = ALL_PRIORITY_CATEGORIES;
                if (i >= iArr.length) {
                    break;
                }
                int priorityCategory = iArr[i];
                if ((priorityCategories & priorityCategory) != 0) {
                    if (sb.length() > 0) {
                        sb.append(',');
                    }
                    sb.append(priorityCategoryToString(priorityCategory));
                }
                priorityCategories &= ~priorityCategory;
                i++;
            }
            if (priorityCategories != 0) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append("PRIORITY_CATEGORY_UNKNOWN_").append(priorityCategories);
            }
            return sb.toString();
        }

        private static String effectToString(int effect) {
            switch (effect) {
                case -1:
                    return "SUPPRESSED_EFFECTS_UNSET";
                case 1:
                    return "SUPPRESSED_EFFECT_SCREEN_OFF";
                case 2:
                    return "SUPPRESSED_EFFECT_SCREEN_ON";
                case 4:
                    return "SUPPRESSED_EFFECT_FULL_SCREEN_INTENT";
                case 8:
                    return "SUPPRESSED_EFFECT_LIGHTS";
                case 16:
                    return "SUPPRESSED_EFFECT_PEEK";
                case 32:
                    return "SUPPRESSED_EFFECT_STATUS_BAR";
                case 64:
                    return "SUPPRESSED_EFFECT_BADGE";
                case 128:
                    return "SUPPRESSED_EFFECT_AMBIENT";
                case 256:
                    return "SUPPRESSED_EFFECT_NOTIFICATION_LIST";
                default:
                    return "UNKNOWN_" + effect;
            }
        }

        private static String priorityCategoryToString(int priorityCategory) {
            switch (priorityCategory) {
                case 1:
                    return "PRIORITY_CATEGORY_REMINDERS";
                case 2:
                    return "PRIORITY_CATEGORY_EVENTS";
                case 4:
                    return "PRIORITY_CATEGORY_MESSAGES";
                case 8:
                    return "PRIORITY_CATEGORY_CALLS";
                case 16:
                    return "PRIORITY_CATEGORY_REPEAT_CALLERS";
                case 32:
                    return "PRIORITY_CATEGORY_ALARMS";
                case 64:
                    return "PRIORITY_CATEGORY_MEDIA";
                case 128:
                    return "PRIORITY_CATEGORY_SYSTEM";
                case 256:
                    return "PRIORITY_CATEGORY_CONVERSATIONS";
                default:
                    return "PRIORITY_CATEGORY_UNKNOWN_" + priorityCategory;
            }
        }

        public static String prioritySendersToString(int prioritySenders) {
            switch (prioritySenders) {
                case 0:
                    return "PRIORITY_SENDERS_ANY";
                case 1:
                    return "PRIORITY_SENDERS_CONTACTS";
                case 2:
                    return "PRIORITY_SENDERS_STARRED";
                default:
                    return "PRIORITY_SENDERS_UNKNOWN_" + prioritySenders;
            }
        }

        public static String conversationSendersToString(int priorityConversationSenders) {
            switch (priorityConversationSenders) {
                case -1:
                    return "unset";
                case 0:
                default:
                    return "invalidConversationType{" + priorityConversationSenders + "}";
                case 1:
                    return "anyone";
                case 2:
                    return "important";
                case 3:
                    return "none";
            }
        }

        public boolean allowAlarms() {
            return (this.priorityCategories & 32) != 0;
        }

        public boolean allowMedia() {
            return (this.priorityCategories & 64) != 0;
        }

        public boolean allowSystem() {
            return (this.priorityCategories & 128) != 0;
        }

        public boolean allowRepeatCallers() {
            return (this.priorityCategories & 16) != 0;
        }

        public boolean allowCalls() {
            return (this.priorityCategories & 8) != 0;
        }

        public boolean allowConversations() {
            return (this.priorityCategories & 256) != 0;
        }

        public boolean allowMessages() {
            return (this.priorityCategories & 4) != 0;
        }

        public boolean allowEvents() {
            return (this.priorityCategories & 2) != 0;
        }

        public boolean allowReminders() {
            return (this.priorityCategories & 1) != 0;
        }

        public int allowCallsFrom() {
            return this.priorityCallSenders;
        }

        public int allowMessagesFrom() {
            return this.priorityMessageSenders;
        }

        public int allowConversationsFrom() {
            return this.priorityConversationSenders;
        }

        public boolean showFullScreenIntents() {
            return (this.suppressedVisualEffects & 4) == 0;
        }

        public boolean showLights() {
            return (this.suppressedVisualEffects & 8) == 0;
        }

        public boolean showPeeking() {
            return (this.suppressedVisualEffects & 16) == 0;
        }

        public boolean showStatusBarIcons() {
            return (this.suppressedVisualEffects & 32) == 0;
        }

        public boolean showAmbient() {
            return (this.suppressedVisualEffects & 128) == 0;
        }

        public boolean showBadges() {
            return (this.suppressedVisualEffects & 64) == 0;
        }

        public boolean showInNotificationList() {
            return (this.suppressedVisualEffects & 256) == 0;
        }

        public Policy copy() {
            Parcel parcel = Parcel.obtain();
            try {
                writeToParcel(parcel, 0);
                parcel.setDataPosition(0);
                return new Policy(parcel);
            } finally {
                parcel.recycle();
            }
        }
    }

    public StatusBarNotification[] getActiveNotifications() {
        INotificationManager service = getService();
        String pkg = this.mContext.getPackageName();
        try {
            ParceledListSlice<StatusBarNotification> parceledList = service.getAppActiveNotifications(pkg, this.mContext.getUserId());
            if (parceledList != null) {
                List<StatusBarNotification> list = parceledList.getList();
                return (StatusBarNotification[]) list.toArray(new StatusBarNotification[list.size()]);
            }
            return new StatusBarNotification[0];
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public final int getCurrentInterruptionFilter() {
        INotificationManager service = getService();
        try {
            return zenModeToInterruptionFilter(service.getZenMode());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public final void setInterruptionFilter(int interruptionFilter) {
        INotificationManager service = getService();
        try {
            service.setInterruptionFilter(this.mContext.getOpPackageName(), interruptionFilter);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean matchesCallFilter(Uri uri) {
        Bundle extras = new Bundle();
        ArrayList<Person> pList = new ArrayList<>();
        pList.add(new Person.Builder().setUri(uri.toString()).build());
        extras.putParcelableArrayList(Notification.EXTRA_PEOPLE_LIST, pList);
        return matchesCallFilter(extras);
    }

    public static int zenModeToInterruptionFilter(int zen) {
        switch (zen) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            default:
                return 0;
        }
    }

    public static int zenModeFromInterruptionFilter(int interruptionFilter, int defValue) {
        switch (interruptionFilter) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            default:
                return defValue;
        }
    }
}
