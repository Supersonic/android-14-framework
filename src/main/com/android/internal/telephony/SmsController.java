package com.android.internal.telephony;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Binder;
import android.os.Bundle;
import android.os.TelephonyServiceManager;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyFrameworkInitializer;
import android.telephony.TelephonyManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class SmsController extends ISmsImplBase {
    private final Context mContext;

    /* JADX WARN: Multi-variable type inference failed */
    @VisibleForTesting
    public SmsController(Context context) {
        this.mContext = context;
        TelephonyServiceManager.ServiceRegisterer smsServiceRegisterer = TelephonyFrameworkInitializer.getTelephonyServiceManager().getSmsServiceRegisterer();
        if (smsServiceRegisterer.get() == null) {
            smsServiceRegisterer.register(this);
        }
    }

    private Phone getPhone(int i) {
        Phone phone = PhoneFactory.getPhone(SubscriptionManager.getPhoneId(i));
        return phone == null ? PhoneFactory.getDefaultPhone() : phone;
    }

    private SmsPermissions getSmsPermissions(int i) {
        Phone phone = getPhone(i);
        Context context = this.mContext;
        return new SmsPermissions(phone, context, (AppOpsManager) context.getSystemService("appops"));
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean updateMessageOnIccEfForSubscriber(int i, String str, int i2, int i3, byte[] bArr) {
        if (str == null) {
            str = getCallingPackage();
        }
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            return iccSmsInterfaceManager.updateMessageOnIccEf(str, i2, i3, bArr);
        }
        Rlog.e("SmsController", "updateMessageOnIccEfForSubscriber iccSmsIntMgr is null for Subscription: " + i);
        return false;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean copyMessageToIccEfForSubscriber(int i, String str, int i2, byte[] bArr, byte[] bArr2) {
        if (str == null) {
            str = getCallingPackage();
        }
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            return iccSmsInterfaceManager.copyMessageToIccEf(str, i2, bArr, bArr2);
        }
        Rlog.e("SmsController", "copyMessageToIccEfForSubscriber iccSmsIntMgr is null for Subscription: " + i);
        return false;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public List<SmsRawData> getAllMessagesFromIccEfForSubscriber(int i, String str) {
        if (str == null) {
            str = getCallingPackage();
        }
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            return iccSmsInterfaceManager.getAllMessagesFromIccEf(str);
        }
        Rlog.e("SmsController", "getAllMessagesFromIccEfForSubscriber iccSmsIntMgr is null for Subscription: " + i);
        return null;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @Deprecated
    public void sendDataForSubscriber(int i, String str, String str2, String str3, int i2, byte[] bArr, PendingIntent pendingIntent, PendingIntent pendingIntent2) {
        sendDataForSubscriber(i, str, null, str2, str3, i2, bArr, pendingIntent, pendingIntent2);
    }

    public void sendDataForSubscriber(int i, String str, String str2, String str3, String str4, int i2, byte[] bArr, PendingIntent pendingIntent, PendingIntent pendingIntent2) {
        String callingPackage = str == null ? getCallingPackage() : str;
        Rlog.d("SmsController", "sendDataForSubscriber caller=" + callingPackage);
        if (!TelephonyPermissions.checkSubscriptionAssociatedWithUser(this.mContext, i, Binder.getCallingUserHandle())) {
            if (TelephonyUtils.isUidForeground(this.mContext, Binder.getCallingUid())) {
                TelephonyUtils.showErrorIfSubscriptionAssociatedWithManagedProfile(this.mContext, i);
            }
            sendErrorInPendingIntent(pendingIntent, 33);
        } else if (isNumberBlockedByFDN(i, str3, callingPackage)) {
            sendErrorInPendingIntent(pendingIntent, 6);
        } else {
            IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
            if (iccSmsInterfaceManager != null) {
                iccSmsInterfaceManager.sendData(callingPackage, str2, str3, str4, i2, bArr, pendingIntent, pendingIntent2);
                return;
            }
            Rlog.e("SmsController", "sendDataForSubscriber iccSmsIntMgr is null for Subscription: " + i);
            sendErrorInPendingIntent(pendingIntent, 1);
        }
    }

    private void sendDataForSubscriberWithSelfPermissionsInternal(int i, String str, String str2, String str3, String str4, int i2, byte[] bArr, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z) {
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            iccSmsInterfaceManager.sendDataWithSelfPermissions(str, str2, str3, str4, i2, bArr, pendingIntent, pendingIntent2, z);
            return;
        }
        Rlog.e("SmsController", "sendText iccSmsIntMgr is null for Subscription: " + i);
        sendErrorInPendingIntent(pendingIntent, 1);
    }

    private String getCallingPackage() {
        return this.mContext.getPackageManager().getPackagesForUid(Binder.getCallingUid())[0];
    }

    public void sendTextForSubscriber(int i, String str, String str2, String str3, String str4, String str5, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z, long j) {
        sendTextForSubscriber(i, str, str2, str3, str4, str5, pendingIntent, pendingIntent2, z, j, false, false);
    }

    public void sendTextForSubscriber(int i, String str, String str2, String str3, String str4, String str5, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z, long j, boolean z2, boolean z3) {
        String callingPackage = str == null ? getCallingPackage() : str;
        Rlog.d("SmsController", "sendTextForSubscriber caller=" + callingPackage);
        if ((z2 || z3) && this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0) {
            throw new SecurityException("Requires MODIFY_PHONE_STATE permission.");
        }
        if (!getSmsPermissions(i).checkCallingCanSendText(z, callingPackage, str2, "Sending SMS message")) {
            sendErrorInPendingIntent(pendingIntent, 1);
        } else if (!TelephonyPermissions.checkSubscriptionAssociatedWithUser(this.mContext, i, Binder.getCallingUserHandle())) {
            if (TelephonyUtils.isUidForeground(this.mContext, Binder.getCallingUid())) {
                TelephonyUtils.showErrorIfSubscriptionAssociatedWithManagedProfile(this.mContext, i);
            }
            sendErrorInPendingIntent(pendingIntent, 33);
        } else if (!z2 && isNumberBlockedByFDN(i, str3, callingPackage)) {
            sendErrorInPendingIntent(pendingIntent, 6);
        } else {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                SubscriptionInfo subscriptionInfo = getSubscriptionInfo(i);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                if (isBluetoothSubscription(subscriptionInfo)) {
                    sendBluetoothText(subscriptionInfo, str3, str5, pendingIntent, pendingIntent2);
                } else {
                    sendIccText(i, callingPackage, str3, str4, str5, pendingIntent, pendingIntent2, z, j, z3);
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                throw th;
            }
        }
    }

    private boolean isBluetoothSubscription(SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo != null && subscriptionInfo.getSubscriptionType() == 1;
    }

    private void sendBluetoothText(SubscriptionInfo subscriptionInfo, String str, String str2, PendingIntent pendingIntent, PendingIntent pendingIntent2) {
        new BtSmsInterfaceManager().sendText(this.mContext, str, str2, pendingIntent, pendingIntent2, subscriptionInfo);
    }

    private void sendIccText(int i, String str, String str2, String str3, String str4, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z, long j, boolean z2) {
        Rlog.d("SmsController", "sendTextForSubscriber iccSmsIntMgr Subscription: " + i + " " + formatCrossStackMessageId(j));
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            iccSmsInterfaceManager.sendText(str, str2, str3, str4, pendingIntent, pendingIntent2, z, j, z2);
            return;
        }
        Rlog.e("SmsController", "sendTextForSubscriber iccSmsIntMgr is null for Subscription: " + i + " " + formatCrossStackMessageId(j));
        sendErrorInPendingIntent(pendingIntent, 1);
    }

    private void sendTextForSubscriberWithSelfPermissionsInternal(int i, String str, String str2, String str3, String str4, String str5, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z, boolean z2) {
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            iccSmsInterfaceManager.sendTextWithSelfPermissions(str, str2, str3, str4, str5, pendingIntent, pendingIntent2, z, z2);
            return;
        }
        Rlog.e("SmsController", "sendText iccSmsIntMgr is null for Subscription: " + i);
        sendErrorInPendingIntent(pendingIntent, 1);
    }

    public void sendTextForSubscriberWithOptions(int i, String str, String str2, String str3, String str4, String str5, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z, int i2, boolean z2, int i3) {
        String callingPackage = str == null ? getCallingPackage() : str;
        Rlog.d("SmsController", "sendTextForSubscriberWithOptions caller=" + callingPackage);
        if (!TelephonyPermissions.checkSubscriptionAssociatedWithUser(this.mContext, i, Binder.getCallingUserHandle())) {
            if (TelephonyUtils.isUidForeground(this.mContext, Binder.getCallingUid())) {
                TelephonyUtils.showErrorIfSubscriptionAssociatedWithManagedProfile(this.mContext, i);
            }
            sendErrorInPendingIntent(pendingIntent, 33);
        } else if (isNumberBlockedByFDN(i, str3, callingPackage)) {
            sendErrorInPendingIntent(pendingIntent, 6);
        } else {
            IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
            if (iccSmsInterfaceManager != null) {
                iccSmsInterfaceManager.sendTextWithOptions(callingPackage, str2, str3, str4, str5, pendingIntent, pendingIntent2, z, i2, z2, i3);
                return;
            }
            Rlog.e("SmsController", "sendTextWithOptions iccSmsIntMgr is null for Subscription: " + i);
            sendErrorInPendingIntent(pendingIntent, 1);
        }
    }

    public void sendMultipartTextForSubscriber(int i, String str, String str2, String str3, String str4, List<String> list, List<PendingIntent> list2, List<PendingIntent> list3, boolean z, long j) {
        String callingPackage = getCallingPackage() != null ? getCallingPackage() : str;
        Rlog.d("SmsController", "sendMultipartTextForSubscriber caller=" + callingPackage);
        if (!TelephonyPermissions.checkSubscriptionAssociatedWithUser(this.mContext, i, Binder.getCallingUserHandle())) {
            if (TelephonyUtils.isUidForeground(this.mContext, Binder.getCallingUid())) {
                TelephonyUtils.showErrorIfSubscriptionAssociatedWithManagedProfile(this.mContext, i);
            }
            sendErrorInPendingIntents(list2, 33);
        } else if (isNumberBlockedByFDN(i, str3, callingPackage)) {
            sendErrorInPendingIntents(list2, 6);
        } else {
            IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
            if (iccSmsInterfaceManager != null) {
                iccSmsInterfaceManager.sendMultipartText(callingPackage, str2, str3, str4, list, list2, list3, z, j);
                return;
            }
            Rlog.e("SmsController", "sendMultipartTextForSubscriber iccSmsIntMgr is null for Subscription: " + i + " " + formatCrossStackMessageId(j));
            sendErrorInPendingIntents(list2, 1);
        }
    }

    public void sendMultipartTextForSubscriberWithOptions(int i, String str, String str2, String str3, String str4, List<String> list, List<PendingIntent> list2, List<PendingIntent> list3, boolean z, int i2, boolean z2, int i3) {
        String callingPackage = str == null ? getCallingPackage() : str;
        Rlog.d("SmsController", "sendMultipartTextForSubscriberWithOptions caller=" + callingPackage);
        if (!TelephonyPermissions.checkSubscriptionAssociatedWithUser(this.mContext, i, Binder.getCallingUserHandle())) {
            if (TelephonyUtils.isUidForeground(this.mContext, Binder.getCallingUid())) {
                TelephonyUtils.showErrorIfSubscriptionAssociatedWithManagedProfile(this.mContext, i);
            }
            sendErrorInPendingIntents(list2, 33);
        } else if (isNumberBlockedByFDN(i, str3, callingPackage)) {
            sendErrorInPendingIntents(list2, 6);
        } else {
            IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
            if (iccSmsInterfaceManager != null) {
                iccSmsInterfaceManager.sendMultipartTextWithOptions(callingPackage, str2, str3, str4, list, list2, list3, z, i2, z2, i3, 0L);
                return;
            }
            Rlog.e("SmsController", "sendMultipartTextWithOptions iccSmsIntMgr is null for Subscription: " + i);
            sendErrorInPendingIntents(list2, 1);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean enableCellBroadcastForSubscriber(int i, int i2, int i3) {
        return enableCellBroadcastRangeForSubscriber(i, i2, i2, i3);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean enableCellBroadcastRangeForSubscriber(int i, int i2, int i3, int i4) {
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            return iccSmsInterfaceManager.enableCellBroadcastRange(i2, i3, i4);
        }
        Rlog.e("SmsController", "enableCellBroadcastRangeForSubscriber iccSmsIntMgr is null for Subscription: " + i);
        return false;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean disableCellBroadcastForSubscriber(int i, int i2, int i3) {
        return disableCellBroadcastRangeForSubscriber(i, i2, i2, i3);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean disableCellBroadcastRangeForSubscriber(int i, int i2, int i3, int i4) {
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            return iccSmsInterfaceManager.disableCellBroadcastRange(i2, i3, i4);
        }
        Rlog.e("SmsController", "disableCellBroadcastRangeForSubscriber iccSmsIntMgr is null for Subscription:" + i);
        return false;
    }

    public int getPremiumSmsPermission(String str) {
        return getPremiumSmsPermissionForSubscriber(getPreferredSmsSubscription(), str);
    }

    public int getPremiumSmsPermissionForSubscriber(int i, String str) {
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            return iccSmsInterfaceManager.getPremiumSmsPermission(str);
        }
        Rlog.e("SmsController", "getPremiumSmsPermissionForSubscriber iccSmsIntMgr is null");
        return 0;
    }

    public void setPremiumSmsPermission(String str, int i) {
        setPremiumSmsPermissionForSubscriber(getPreferredSmsSubscription(), str, i);
    }

    public void setPremiumSmsPermissionForSubscriber(int i, String str, int i2) {
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            iccSmsInterfaceManager.setPremiumSmsPermission(str, i2);
        } else {
            Rlog.e("SmsController", "setPremiumSmsPermissionForSubscriber iccSmsIntMgr is null");
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean isImsSmsSupportedForSubscriber(int i) {
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            return iccSmsInterfaceManager.isImsSmsSupported();
        }
        Rlog.e("SmsController", "isImsSmsSupportedForSubscriber iccSmsIntMgr is null");
        return false;
    }

    public boolean isSmsSimPickActivityNeeded(int i) {
        Context applicationContext = this.mContext.getApplicationContext();
        ActivityManager activityManager = (ActivityManager) applicationContext.getSystemService(ActivityManager.class);
        if (!(activityManager != null && activityManager.getUidImportance(Binder.getCallingUid()) == 100)) {
            Rlog.d("SmsController", "isSmsSimPickActivityNeeded: calling process not foreground. Suppressing activity.");
            return false;
        }
        TelephonyManager telephonyManager = (TelephonyManager) applicationContext.getSystemService("phone");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            List<SubscriptionInfo> activeSubscriptionInfoList = SubscriptionManager.from(applicationContext).getActiveSubscriptionInfoList();
            if (activeSubscriptionInfoList != null) {
                int size = activeSubscriptionInfoList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    SubscriptionInfo subscriptionInfo = activeSubscriptionInfoList.get(i2);
                    if (subscriptionInfo != null && subscriptionInfo.getSubscriptionId() == i) {
                        return false;
                    }
                }
                if (size > 1 && telephonyManager.getSimCount() > 1) {
                    return true;
                }
            }
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getImsSmsFormatForSubscriber(int i) {
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            return iccSmsInterfaceManager.getImsSmsFormat();
        }
        Rlog.e("SmsController", "getImsSmsFormatForSubscriber iccSmsIntMgr is null");
        return null;
    }

    public void injectSmsPduForSubscriber(int i, byte[] bArr, String str, PendingIntent pendingIntent) {
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            iccSmsInterfaceManager.injectSmsPdu(bArr, str, pendingIntent);
            return;
        }
        Rlog.e("SmsController", "injectSmsPduForSubscriber iccSmsIntMgr is null");
        sendErrorInPendingIntent(pendingIntent, 2);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getPreferredSmsSubscription() {
        int defaultSmsSubId;
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            defaultSmsSubId = SubscriptionManagerService.getInstance().getDefaultSmsSubId();
        } else {
            defaultSmsSubId = SubscriptionController.getInstance().getDefaultSmsSubId();
        }
        if (SubscriptionManager.isValidSubscriptionId(defaultSmsSubId)) {
            return defaultSmsSubId;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                int[] activeSubIdList = SubscriptionManagerService.getInstance().getActiveSubIdList(true);
                if (activeSubIdList.length == 1) {
                    return activeSubIdList[0];
                }
            } else {
                int[] activeSubIdList2 = SubscriptionController.getInstance().getActiveSubIdList(true);
                if (activeSubIdList2.length == 1) {
                    return activeSubIdList2[0];
                }
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return -1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isSMSPromptEnabled() {
        return PhoneFactory.isSMSPromptEnabled();
    }

    public void sendStoredText(int i, String str, String str2, Uri uri, String str3, PendingIntent pendingIntent, PendingIntent pendingIntent2) {
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (!getCallingPackage().equals(str)) {
            throw new SecurityException("sendStoredText: Package " + str + "does not belong to " + Binder.getCallingUid());
        }
        Rlog.d("SmsController", "sendStoredText caller=" + str);
        if (iccSmsInterfaceManager != null) {
            iccSmsInterfaceManager.sendStoredText(str, str2, uri, str3, pendingIntent, pendingIntent2);
            return;
        }
        Rlog.e("SmsController", "sendStoredText iccSmsIntMgr is null for subscription: " + i);
        sendErrorInPendingIntent(pendingIntent, 1);
    }

    public void sendStoredMultipartText(int i, String str, String str2, Uri uri, String str3, List<PendingIntent> list, List<PendingIntent> list2) {
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (!getCallingPackage().equals(str)) {
            throw new SecurityException("sendStoredMultipartText: Package " + str + " does not belong to " + Binder.getCallingUid());
        }
        Rlog.d("SmsController", "sendStoredMultipartText caller=" + str);
        if (iccSmsInterfaceManager != null) {
            iccSmsInterfaceManager.sendStoredMultipartText(str, str2, uri, str3, list, list2);
            return;
        }
        Rlog.e("SmsController", "sendStoredMultipartText iccSmsIntMgr is null for subscription: " + i);
        sendErrorInPendingIntents(list, 1);
    }

    public Bundle getCarrierConfigValuesForSubscriber(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return getMmsConfig(((CarrierConfigManager) this.mContext.getSystemService("carrier_config")).getConfigForSubId(i));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    private static Bundle getMmsConfig(BaseBundle baseBundle) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("enabledTransID", baseBundle.getBoolean("enabledTransID"));
        bundle.putBoolean("enabledMMS", baseBundle.getBoolean("enabledMMS"));
        bundle.putBoolean("enableGroupMms", baseBundle.getBoolean("enableGroupMms"));
        bundle.putBoolean("enabledNotifyWapMMSC", baseBundle.getBoolean("enabledNotifyWapMMSC"));
        bundle.putBoolean("aliasEnabled", baseBundle.getBoolean("aliasEnabled"));
        bundle.putBoolean("allowAttachAudio", baseBundle.getBoolean("allowAttachAudio"));
        bundle.putBoolean("enableMultipartSMS", baseBundle.getBoolean("enableMultipartSMS"));
        bundle.putBoolean("enableSMSDeliveryReports", baseBundle.getBoolean("enableSMSDeliveryReports"));
        bundle.putBoolean("supportMmsContentDisposition", baseBundle.getBoolean("supportMmsContentDisposition"));
        bundle.putBoolean("sendMultipartSmsAsSeparateMessages", baseBundle.getBoolean("sendMultipartSmsAsSeparateMessages"));
        bundle.putBoolean("enableMMSReadReports", baseBundle.getBoolean("enableMMSReadReports"));
        bundle.putBoolean("enableMMSDeliveryReports", baseBundle.getBoolean("enableMMSDeliveryReports"));
        bundle.putBoolean("mmsCloseConnection", baseBundle.getBoolean("mmsCloseConnection"));
        bundle.putInt("maxMessageSize", baseBundle.getInt("maxMessageSize"));
        bundle.putInt("maxImageWidth", baseBundle.getInt("maxImageWidth"));
        bundle.putInt("maxImageHeight", baseBundle.getInt("maxImageHeight"));
        bundle.putInt("recipientLimit", baseBundle.getInt("recipientLimit"));
        bundle.putInt("aliasMinChars", baseBundle.getInt("aliasMinChars"));
        bundle.putInt("aliasMaxChars", baseBundle.getInt("aliasMaxChars"));
        bundle.putInt("smsToMmsTextThreshold", baseBundle.getInt("smsToMmsTextThreshold"));
        bundle.putInt("smsToMmsTextLengthThreshold", baseBundle.getInt("smsToMmsTextLengthThreshold"));
        bundle.putInt("maxMessageTextSize", baseBundle.getInt("maxMessageTextSize"));
        bundle.putInt("maxSubjectLength", baseBundle.getInt("maxSubjectLength"));
        bundle.putInt("httpSocketTimeout", baseBundle.getInt("httpSocketTimeout"));
        bundle.putString("uaProfTagName", baseBundle.getString("uaProfTagName"));
        bundle.putString("userAgent", baseBundle.getString("userAgent"));
        bundle.putString("uaProfUrl", baseBundle.getString("uaProfUrl"));
        bundle.putString("httpParams", baseBundle.getString("httpParams"));
        bundle.putString("emailGatewayNumber", baseBundle.getString("emailGatewayNumber"));
        bundle.putString("naiSuffix", baseBundle.getString("naiSuffix"));
        bundle.putBoolean("config_cellBroadcastAppLinks", baseBundle.getBoolean("config_cellBroadcastAppLinks"));
        bundle.putBoolean("supportHttpCharsetHeader", baseBundle.getBoolean("supportHttpCharsetHeader"));
        return bundle;
    }

    public String createAppSpecificSmsTokenWithPackageInfo(int i, String str, String str2, PendingIntent pendingIntent) {
        if (str == null) {
            str = getCallingPackage();
        }
        return getPhone(i).getAppSmsManager().createAppSpecificSmsTokenWithPackageInfo(i, str, str2, pendingIntent);
    }

    public String createAppSpecificSmsToken(int i, String str, PendingIntent pendingIntent) {
        if (str == null) {
            str = getCallingPackage();
        }
        return getPhone(i).getAppSmsManager().createAppSpecificSmsToken(str, pendingIntent);
    }

    public void setStorageMonitorMemoryStatusOverride(int i, boolean z) {
        Phone phone = getPhone(i);
        if (phone == null) {
            Rlog.e("SmsController", "Phone Object is Null");
        } else if (phone.getContext().checkPermission("android.permission.MODIFY_PHONE_STATE", Binder.getCallingPid(), Binder.getCallingUid()) != 0) {
            throw new SecurityException("setStorageMonitorMemoryStatusOverride needs MODIFY_PHONE_STATE");
        } else {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                phone.mSmsStorageMonitor.sendMemoryStatusOverride(z);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public void clearStorageMonitorMemoryStatusOverride(int i) {
        Phone phone = getPhone(i);
        if (phone == null) {
            Rlog.e("SmsController", "Phone Object is Null");
        } else if (phone.getContext().checkPermission("android.permission.MODIFY_PHONE_STATE", Binder.getCallingPid(), Binder.getCallingUid()) != 0) {
            throw new SecurityException("clearStorageMonitorMemoryStatusOverride needs MODIFY_PHONE_STATE");
        } else {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                phone.mSmsStorageMonitor.clearMemoryStatusOverride();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public int checkSmsShortCodeDestination(int i, String str, String str2, String str3, String str4) {
        if (str == null) {
            str = getCallingPackage();
        }
        if (TelephonyPermissions.checkCallingOrSelfReadPhoneState(getPhone(i).getContext(), i, str, str2, "checkSmsShortCodeDestination")) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return getPhone(i).mSmsUsageMonitor.checkDestination(str3, str4);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return 0;
    }

    public void sendVisualVoicemailSmsForSubscriber(String str, String str2, int i, String str3, int i2, String str4, PendingIntent pendingIntent) {
        Rlog.d("SmsController", "sendVisualVoicemailSmsForSubscriber caller=" + str);
        if (getPhone(i).isInEcm()) {
            Rlog.d("SmsController", "sendVisualVoicemailSmsForSubscriber: Do not send non-emergency SMS in ECBM as it forces device to exit ECBM.");
        } else if (!TelephonyPermissions.checkSubscriptionAssociatedWithUser(this.mContext, i, Binder.getCallingUserHandle())) {
            if (TelephonyUtils.isUidForeground(this.mContext, Binder.getCallingUid())) {
                TelephonyUtils.showErrorIfSubscriptionAssociatedWithManagedProfile(this.mContext, i);
            }
            sendErrorInPendingIntent(pendingIntent, 33);
        } else if (i2 == 0) {
            sendTextForSubscriberWithSelfPermissionsInternal(i, str, str2, str3, null, str4, pendingIntent, null, false, true);
        } else {
            sendDataForSubscriberWithSelfPermissionsInternal(i, str, str2, str3, null, (short) i2, str4.getBytes(StandardCharsets.UTF_8), pendingIntent, null, true);
        }
    }

    public String getSmscAddressFromIccEfForSubscriber(int i, String str) {
        if (str == null) {
            str = getCallingPackage();
        }
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            return iccSmsInterfaceManager.getSmscAddressFromIccEf(str);
        }
        Rlog.e("SmsController", "getSmscAddressFromIccEfForSubscriber iccSmsIntMgr is null for Subscription: " + i);
        return null;
    }

    public boolean setSmscAddressOnIccEfForSubscriber(String str, int i, String str2) {
        if (str2 == null) {
            str2 = getCallingPackage();
        }
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            return iccSmsInterfaceManager.setSmscAddressOnIccEf(str2, str);
        }
        Rlog.e("SmsController", "setSmscAddressOnIccEfForSubscriber iccSmsIntMgr is null for Subscription: " + i);
        return false;
    }

    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (TelephonyUtils.checkDumpPermission(this.mContext, "SmsController", printWriter)) {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "    ");
            for (Phone phone : PhoneFactory.getPhones()) {
                int subId = phone.getSubId();
                indentingPrintWriter.println(String.format("SmsManager for subId = %d:", Integer.valueOf(subId)));
                indentingPrintWriter.increaseIndent();
                if (getIccSmsInterfaceManager(subId) != null) {
                    getIccSmsInterfaceManager(subId).dump(fileDescriptor, indentingPrintWriter, strArr);
                }
                indentingPrintWriter.decreaseIndent();
            }
            indentingPrintWriter.flush();
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void sendErrorInPendingIntent(PendingIntent pendingIntent, int i) {
        if (pendingIntent != null) {
            try {
                pendingIntent.send(i);
            } catch (PendingIntent.CanceledException unused) {
            }
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void sendErrorInPendingIntents(List<PendingIntent> list, int i) {
        if (list == null) {
            return;
        }
        for (PendingIntent pendingIntent : list) {
            sendErrorInPendingIntent(pendingIntent, i);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private IccSmsInterfaceManager getIccSmsInterfaceManager(int i) {
        return getPhone(i).getIccSmsInterfaceManager();
    }

    private SubscriptionInfo getSubscriptionInfo(int i) {
        return ((SubscriptionManager) this.mContext.getSystemService("telephony_subscription_service")).getActiveSubscriptionInfo(i);
    }

    public int getSmsCapacityOnIccForSubscriber(int i) {
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            return iccSmsInterfaceManager.getSmsCapacityOnIcc(getCallingPackage(), null);
        }
        Rlog.e("SmsController", "iccSmsIntMgr is null for  subId: " + i);
        return 0;
    }

    public boolean resetAllCellBroadcastRanges(int i) {
        IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
        if (iccSmsInterfaceManager != null) {
            iccSmsInterfaceManager.resetAllCellBroadcastRanges();
            return true;
        }
        Rlog.e("SmsController", "iccSmsIntMgr is null for  subId: " + i);
        return false;
    }

    public static String formatCrossStackMessageId(long j) {
        return "{x-message-id:" + j + "}";
    }

    @VisibleForTesting
    public boolean isNumberBlockedByFDN(int i, String str, String str2) {
        int phoneId = SubscriptionManager.getPhoneId(i);
        if (FdnUtils.isFdnEnabled(phoneId)) {
            TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
            if (telephonyManager.isEmergencyNumber(str)) {
                return false;
            }
            String upperCase = telephonyManager.getSimCountryIso().toUpperCase(Locale.ENGLISH);
            if (FdnUtils.isNumberBlockedByFDN(phoneId, str, upperCase)) {
                return true;
            }
            IccSmsInterfaceManager iccSmsInterfaceManager = getIccSmsInterfaceManager(i);
            if (iccSmsInterfaceManager != null) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    String smscAddressFromIccEf = iccSmsInterfaceManager.getSmscAddressFromIccEf(str2);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return FdnUtils.isNumberBlockedByFDN(phoneId, smscAddressFromIccEf, upperCase);
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    throw th;
                }
            }
            Rlog.e("SmsController", "getSmscAddressFromIccEfForSubscriber iccSmsIntMgr is null for Subscription: " + i);
            return true;
        }
        return false;
    }
}
