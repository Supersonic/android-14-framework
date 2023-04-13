package com.android.server.notification;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telecom.TelecomManager;
import com.android.internal.util.NotificationMessagingUtil;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.util.Comparator;
import java.util.Objects;
/* loaded from: classes2.dex */
public class NotificationComparator implements Comparator<NotificationRecord> {
    public final Context mContext;
    public String mDefaultPhoneApp;
    public final NotificationMessagingUtil mMessagingUtil;
    public final BroadcastReceiver mPhoneAppBroadcastReceiver;

    public NotificationComparator(Context context) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.notification.NotificationComparator.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                NotificationComparator.this.mDefaultPhoneApp = intent.getStringExtra("android.telecom.extra.CHANGE_DEFAULT_DIALER_PACKAGE_NAME");
            }
        };
        this.mPhoneAppBroadcastReceiver = broadcastReceiver;
        this.mContext = context;
        context.registerReceiver(broadcastReceiver, new IntentFilter("android.telecom.action.DEFAULT_DIALER_CHANGED"));
        this.mMessagingUtil = new NotificationMessagingUtil(context);
    }

    @Override // java.util.Comparator
    public int compare(NotificationRecord notificationRecord, NotificationRecord notificationRecord2) {
        int compare;
        int importance = notificationRecord.getImportance();
        int importance2 = notificationRecord2.getImportance();
        boolean z = importance >= 3;
        boolean z2 = importance2 >= 3;
        if (z != z2) {
            compare = Boolean.compare(z, z2);
        } else if (notificationRecord.getRankingScore() != notificationRecord2.getRankingScore()) {
            compare = Float.compare(notificationRecord.getRankingScore(), notificationRecord2.getRankingScore());
        } else {
            boolean isImportantColorized = isImportantColorized(notificationRecord);
            boolean isImportantColorized2 = isImportantColorized(notificationRecord2);
            if (isImportantColorized != isImportantColorized2) {
                compare = Boolean.compare(isImportantColorized, isImportantColorized2);
            } else {
                boolean isImportantOngoing = isImportantOngoing(notificationRecord);
                boolean isImportantOngoing2 = isImportantOngoing(notificationRecord2);
                if (isImportantOngoing != isImportantOngoing2) {
                    compare = Boolean.compare(isImportantOngoing, isImportantOngoing2);
                } else {
                    boolean isImportantMessaging = isImportantMessaging(notificationRecord);
                    boolean isImportantMessaging2 = isImportantMessaging(notificationRecord2);
                    if (isImportantMessaging != isImportantMessaging2) {
                        compare = Boolean.compare(isImportantMessaging, isImportantMessaging2);
                    } else {
                        boolean isImportantPeople = isImportantPeople(notificationRecord);
                        boolean isImportantPeople2 = isImportantPeople(notificationRecord2);
                        int compare2 = Float.compare(notificationRecord.getContactAffinity(), notificationRecord2.getContactAffinity());
                        if (isImportantPeople && isImportantPeople2) {
                            if (compare2 != 0) {
                                return compare2 * (-1);
                            }
                        } else if (isImportantPeople != isImportantPeople2) {
                            compare = Boolean.compare(isImportantPeople, isImportantPeople2);
                        }
                        boolean isSystemMax = isSystemMax(notificationRecord);
                        boolean isSystemMax2 = isSystemMax(notificationRecord2);
                        if (isSystemMax != isSystemMax2) {
                            compare = Boolean.compare(isSystemMax, isSystemMax2);
                        } else if (importance != importance2) {
                            compare = Integer.compare(importance, importance2);
                        } else if (compare2 != 0) {
                            return compare2 * (-1);
                        } else {
                            int packagePriority = notificationRecord.getPackagePriority();
                            int packagePriority2 = notificationRecord2.getPackagePriority();
                            if (packagePriority != packagePriority2) {
                                compare = Integer.compare(packagePriority, packagePriority2);
                            } else {
                                int i = notificationRecord.getSbn().getNotification().priority;
                                int i2 = notificationRecord2.getSbn().getNotification().priority;
                                if (i != i2) {
                                    compare = Integer.compare(i, i2);
                                } else {
                                    boolean isInterruptive = notificationRecord.isInterruptive();
                                    boolean isInterruptive2 = notificationRecord2.isInterruptive();
                                    if (isInterruptive != isInterruptive2) {
                                        compare = Boolean.compare(isInterruptive, isInterruptive2);
                                    } else {
                                        compare = Long.compare(notificationRecord.getRankingTimeMs(), notificationRecord2.getRankingTimeMs());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return compare * (-1);
    }

    public final boolean isImportantColorized(NotificationRecord notificationRecord) {
        if (notificationRecord.getImportance() < 2) {
            return false;
        }
        return notificationRecord.getNotification().isColorized();
    }

    public final boolean isImportantOngoing(NotificationRecord notificationRecord) {
        if (notificationRecord.getImportance() < 2) {
            return false;
        }
        if (isCallStyle(notificationRecord)) {
            return true;
        }
        if (isOngoing(notificationRecord)) {
            return isCallCategory(notificationRecord) || isMediaNotification(notificationRecord);
        }
        return false;
    }

    public boolean isImportantPeople(NotificationRecord notificationRecord) {
        return notificationRecord.getImportance() >= 2 && notificationRecord.getContactAffinity() > 0.0f;
    }

    public boolean isImportantMessaging(NotificationRecord notificationRecord) {
        return this.mMessagingUtil.isImportantMessaging(notificationRecord.getSbn(), notificationRecord.getImportance());
    }

    public boolean isSystemMax(NotificationRecord notificationRecord) {
        if (notificationRecord.getImportance() < 4) {
            return false;
        }
        String packageName = notificationRecord.getSbn().getPackageName();
        return PackageManagerShellCommandDataLoader.PACKAGE.equals(packageName) || "com.android.systemui".equals(packageName);
    }

    public final boolean isOngoing(NotificationRecord notificationRecord) {
        return (notificationRecord.getNotification().flags & 64) != 0;
    }

    public final boolean isMediaNotification(NotificationRecord notificationRecord) {
        return notificationRecord.getNotification().isMediaNotification();
    }

    public final boolean isCallCategory(NotificationRecord notificationRecord) {
        return notificationRecord.isCategory("call") && isDefaultPhoneApp(notificationRecord.getSbn().getPackageName());
    }

    public final boolean isCallStyle(NotificationRecord notificationRecord) {
        return notificationRecord.getNotification().isStyle(Notification.CallStyle.class);
    }

    public final boolean isDefaultPhoneApp(String str) {
        if (this.mDefaultPhoneApp == null) {
            TelecomManager telecomManager = (TelecomManager) this.mContext.getSystemService("telecom");
            this.mDefaultPhoneApp = telecomManager != null ? telecomManager.getDefaultDialerPackage() : null;
        }
        return Objects.equals(str, this.mDefaultPhoneApp);
    }
}
