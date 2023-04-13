package com.android.server.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.accessibility.util.AccessibilityStatsLogUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.util.ImageUtils;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.accessibility.PolicyWarningUIController;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class PolicyWarningUIController {
    @VisibleForTesting
    protected static final String ACTION_A11Y_SETTINGS;
    @VisibleForTesting
    protected static final String ACTION_DISMISS_NOTIFICATION;
    @VisibleForTesting
    protected static final String ACTION_SEND_NOTIFICATION;
    public final AlarmManager mAlarmManager;
    public final Context mContext;
    public final ArraySet<ComponentName> mEnabledA11yServices = new ArraySet<>();
    public final Handler mMainHandler;
    public final NotificationController mNotificationController;

    static {
        String simpleName = PolicyWarningUIController.class.getSimpleName();
        ACTION_SEND_NOTIFICATION = simpleName + ".ACTION_SEND_NOTIFICATION";
        ACTION_A11Y_SETTINGS = simpleName + ".ACTION_A11Y_SETTINGS";
        ACTION_DISMISS_NOTIFICATION = simpleName + ".ACTION_DISMISS_NOTIFICATION";
    }

    public PolicyWarningUIController(Handler handler, Context context, NotificationController notificationController) {
        this.mMainHandler = handler;
        this.mContext = context;
        this.mNotificationController = notificationController;
        this.mAlarmManager = (AlarmManager) context.getSystemService(AlarmManager.class);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SEND_NOTIFICATION);
        intentFilter.addAction(ACTION_A11Y_SETTINGS);
        intentFilter.addAction(ACTION_DISMISS_NOTIFICATION);
        context.registerReceiver(notificationController, intentFilter, "android.permission.MANAGE_ACCESSIBILITY", handler, 2);
    }

    public void onSwitchUser(int i, Set<ComponentName> set) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.PolicyWarningUIController$$ExternalSyntheticLambda4
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PolicyWarningUIController.this.onSwitchUserInternal(((Integer) obj).intValue(), (Set) obj2);
            }
        }, Integer.valueOf(i), set));
    }

    public final void onSwitchUserInternal(int i, Set<ComponentName> set) {
        this.mEnabledA11yServices.clear();
        this.mEnabledA11yServices.addAll(set);
        this.mNotificationController.onSwitchUser(i);
    }

    public void onEnabledServicesChanged(int i, Set<ComponentName> set) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.PolicyWarningUIController$$ExternalSyntheticLambda3
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PolicyWarningUIController.this.onEnabledServicesChangedInternal(((Integer) obj).intValue(), (Set) obj2);
            }
        }, Integer.valueOf(i), set));
    }

    public void onEnabledServicesChangedInternal(int i, Set<ComponentName> set) {
        ArraySet arraySet = new ArraySet((ArraySet) this.mEnabledA11yServices);
        arraySet.removeAll(set);
        this.mEnabledA11yServices.clear();
        this.mEnabledA11yServices.addAll(set);
        Handler handler = this.mMainHandler;
        final NotificationController notificationController = this.mNotificationController;
        Objects.requireNonNull(notificationController);
        handler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.PolicyWarningUIController$$ExternalSyntheticLambda5
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PolicyWarningUIController.NotificationController.this.onServicesDisabled(((Integer) obj).intValue(), (ArraySet) obj2);
            }
        }, Integer.valueOf(i), arraySet));
    }

    public void onNonA11yCategoryServiceBound(int i, ComponentName componentName) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.PolicyWarningUIController$$ExternalSyntheticLambda1
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PolicyWarningUIController.this.setAlarm(((Integer) obj).intValue(), (ComponentName) obj2);
            }
        }, Integer.valueOf(i), componentName));
    }

    public void onNonA11yCategoryServiceUnbound(int i, ComponentName componentName) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.PolicyWarningUIController$$ExternalSyntheticLambda0
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                PolicyWarningUIController.this.cancelAlarm(((Integer) obj).intValue(), (ComponentName) obj2);
            }
        }, Integer.valueOf(i), componentName));
    }

    public final void setAlarm(int i, ComponentName componentName) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(10, 24);
        this.mAlarmManager.set(0, calendar.getTimeInMillis(), createPendingIntent(this.mContext, i, ACTION_SEND_NOTIFICATION, componentName));
    }

    public final void cancelAlarm(int i, ComponentName componentName) {
        this.mAlarmManager.cancel(createPendingIntent(this.mContext, i, ACTION_SEND_NOTIFICATION, componentName));
    }

    public static PendingIntent createPendingIntent(Context context, int i, String str, ComponentName componentName) {
        return PendingIntent.getBroadcast(context, 0, createIntent(context, i, str, componentName), 67108864);
    }

    public static Intent createIntent(Context context, int i, String str, ComponentName componentName) {
        Intent intent = new Intent(str);
        intent.setPackage(context.getPackageName()).setIdentifier(componentName.flattenToShortString()).putExtra("android.intent.extra.COMPONENT_NAME", componentName).putExtra("android.intent.extra.USER_ID", i).putExtra("android.intent.extra.TIME", SystemClock.elapsedRealtime());
        return intent;
    }

    public void enableSendingNonA11yToolNotification(boolean z) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.accessibility.PolicyWarningUIController$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PolicyWarningUIController.this.enableSendingNonA11yToolNotificationInternal(((Boolean) obj).booleanValue());
            }
        }, Boolean.valueOf(z)));
    }

    public final void enableSendingNonA11yToolNotificationInternal(boolean z) {
        this.mNotificationController.setSendingNotification(z);
    }

    /* loaded from: classes.dex */
    public static class NotificationController extends BroadcastReceiver {
        public final Context mContext;
        public int mCurrentUserId;
        public final NotificationManager mNotificationManager;
        public boolean mSendNotification;
        public final ArraySet<ComponentName> mNotifiedA11yServices = new ArraySet<>();
        public final List<ComponentName> mSentA11yServiceNotification = new ArrayList();

        public NotificationController(Context context) {
            this.mContext = context;
            this.mNotificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            ComponentName componentName = (ComponentName) intent.getParcelableExtra("android.intent.extra.COMPONENT_NAME", ComponentName.class);
            if (TextUtils.isEmpty(action) || componentName == null) {
                return;
            }
            long longExtra = intent.getLongExtra("android.intent.extra.TIME", 0L);
            long elapsedRealtime = longExtra > 0 ? SystemClock.elapsedRealtime() - longExtra : 0L;
            int intExtra = intent.getIntExtra("android.intent.extra.USER_ID", 0);
            if (PolicyWarningUIController.ACTION_SEND_NOTIFICATION.equals(action)) {
                if (trySendNotification(intExtra, componentName)) {
                    AccessibilityStatsLogUtils.logNonA11yToolServiceWarningReported(componentName.getPackageName(), AccessibilityStatsLogUtils.ACCESSIBILITY_PRIVACY_WARNING_STATUS_SHOWN, elapsedRealtime);
                }
            } else if (PolicyWarningUIController.ACTION_A11Y_SETTINGS.equals(action)) {
                if (tryLaunchSettings(intExtra, componentName)) {
                    AccessibilityStatsLogUtils.logNonA11yToolServiceWarningReported(componentName.getPackageName(), AccessibilityStatsLogUtils.ACCESSIBILITY_PRIVACY_WARNING_STATUS_CLICKED, elapsedRealtime);
                }
                this.mNotificationManager.cancel(componentName.flattenToShortString(), 1005);
                this.mSentA11yServiceNotification.remove(componentName);
                onNotificationCanceled(intExtra, componentName);
            } else if (PolicyWarningUIController.ACTION_DISMISS_NOTIFICATION.equals(action)) {
                this.mSentA11yServiceNotification.remove(componentName);
                onNotificationCanceled(intExtra, componentName);
            }
        }

        public void onSwitchUser(int i) {
            cancelSentNotifications();
            this.mNotifiedA11yServices.clear();
            this.mCurrentUserId = i;
            this.mNotifiedA11yServices.addAll((ArraySet<? extends ComponentName>) readNotifiedServiceList(i));
        }

        public void onServicesDisabled(int i, ArraySet<ComponentName> arraySet) {
            if (this.mNotifiedA11yServices.removeAll((ArraySet<? extends ComponentName>) arraySet)) {
                writeNotifiedServiceList(i, this.mNotifiedA11yServices);
            }
        }

        public final boolean trySendNotification(int i, ComponentName componentName) {
            if (i == this.mCurrentUserId && this.mSendNotification) {
                List<AccessibilityServiceInfo> enabledServiceInfos = getEnabledServiceInfos();
                int i2 = 0;
                while (true) {
                    if (i2 >= enabledServiceInfos.size()) {
                        break;
                    }
                    AccessibilityServiceInfo accessibilityServiceInfo = enabledServiceInfos.get(i2);
                    if (!componentName.flattenToShortString().equals(accessibilityServiceInfo.getComponentName().flattenToShortString())) {
                        i2++;
                    } else if (!accessibilityServiceInfo.isAccessibilityTool() && !this.mNotifiedA11yServices.contains(componentName)) {
                        CharSequence loadLabel = accessibilityServiceInfo.getResolveInfo().serviceInfo.loadLabel(this.mContext.getPackageManager());
                        Drawable loadIcon = accessibilityServiceInfo.getResolveInfo().loadIcon(this.mContext.getPackageManager());
                        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(17104896);
                        sendNotification(i, componentName, loadLabel, ImageUtils.buildScaledBitmap(loadIcon, dimensionPixelSize, dimensionPixelSize));
                        return true;
                    }
                }
                return false;
            }
            return false;
        }

        public final boolean tryLaunchSettings(int i, ComponentName componentName) {
            if (i != this.mCurrentUserId) {
                return false;
            }
            Intent intent = new Intent("android.settings.ACCESSIBILITY_DETAILS_SETTINGS");
            intent.addFlags(268468224);
            intent.putExtra("android.intent.extra.COMPONENT_NAME", componentName.flattenToShortString());
            intent.putExtra("start_time_to_log_a11y_tool", SystemClock.elapsedRealtime());
            this.mContext.startActivityAsUser(intent, ActivityOptions.makeBasic().setLaunchDisplayId(this.mContext.getDisplayId()).toBundle(), UserHandle.of(i));
            ((StatusBarManager) this.mContext.getSystemService(StatusBarManager.class)).collapsePanels();
            return true;
        }

        public void onNotificationCanceled(int i, ComponentName componentName) {
            if (i == this.mCurrentUserId && this.mNotifiedA11yServices.add(componentName)) {
                writeNotifiedServiceList(i, this.mNotifiedA11yServices);
            }
        }

        public final void sendNotification(int i, ComponentName componentName, CharSequence charSequence, Bitmap bitmap) {
            Notification.Builder builder = new Notification.Builder(this.mContext, SystemNotificationChannels.ACCESSIBILITY_SECURITY_POLICY);
            builder.setSmallIcon(17302299).setContentTitle(this.mContext.getString(17041713)).setContentText(this.mContext.getString(17041712, charSequence)).setStyle(new Notification.BigTextStyle().bigText(this.mContext.getString(17041712, charSequence))).setTicker(this.mContext.getString(17041713)).setOnlyAlertOnce(true).setDeleteIntent(PolicyWarningUIController.createPendingIntent(this.mContext, i, PolicyWarningUIController.ACTION_DISMISS_NOTIFICATION, componentName)).setContentIntent(PolicyWarningUIController.createPendingIntent(this.mContext, i, PolicyWarningUIController.ACTION_A11Y_SETTINGS, componentName));
            if (bitmap != null) {
                builder.setLargeIcon(bitmap);
            }
            this.mNotificationManager.notify(componentName.flattenToShortString(), 1005, builder.build());
            this.mSentA11yServiceNotification.add(componentName);
        }

        public final ArraySet<ComponentName> readNotifiedServiceList(int i) {
            String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "notified_non_accessibility_category_services", i);
            if (TextUtils.isEmpty(stringForUser)) {
                return new ArraySet<>();
            }
            TextUtils.SimpleStringSplitter<String> simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
            simpleStringSplitter.setString(stringForUser);
            ArraySet<ComponentName> arraySet = new ArraySet<>();
            for (String str : simpleStringSplitter) {
                ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
                if (unflattenFromString != null) {
                    arraySet.add(unflattenFromString);
                }
            }
            return arraySet;
        }

        public final void writeNotifiedServiceList(int i, ArraySet<ComponentName> arraySet) {
            StringBuilder sb = new StringBuilder();
            for (int i2 = 0; i2 < arraySet.size(); i2++) {
                if (i2 > 0) {
                    sb.append(':');
                }
                sb.append(arraySet.valueAt(i2).flattenToShortString());
            }
            Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "notified_non_accessibility_category_services", sb.toString(), i);
        }

        @VisibleForTesting
        public List<AccessibilityServiceInfo> getEnabledServiceInfos() {
            return AccessibilityManager.getInstance(this.mContext).getEnabledAccessibilityServiceList(-1);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$cancelSentNotifications$0(ComponentName componentName) {
            this.mNotificationManager.cancel(componentName.flattenToShortString(), 1005);
        }

        public final void cancelSentNotifications() {
            this.mSentA11yServiceNotification.forEach(new Consumer() { // from class: com.android.server.accessibility.PolicyWarningUIController$NotificationController$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    PolicyWarningUIController.NotificationController.this.lambda$cancelSentNotifications$0((ComponentName) obj);
                }
            });
            this.mSentA11yServiceNotification.clear();
        }

        public void setSendingNotification(boolean z) {
            this.mSendNotification = z;
        }
    }
}
