package com.android.internal.telephony;

import android.app.AppOpsManager;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.net.Uri;
import android.os.Binder;
import android.os.RemoteException;
import android.os.TelephonyServiceManager;
import android.telephony.ImsiEncryptionInfo;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyFrameworkInitializer;
import android.text.TextUtils;
import android.util.EventLog;
import com.android.internal.telephony.IPhoneSubInfo;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.uicc.IsimRecords;
import com.android.internal.telephony.uicc.SIMRecords;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.uicc.UiccPort;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class PhoneSubInfoController extends IPhoneSubInfo.Stub {
    private AppOpsManager mAppOps;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final Context mContext;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface CallPhoneMethodHelper<T> {
        T callMethod(Phone phone);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface PermissionCheckHelper {
        boolean checkPermission(Context context, int i, String str, String str2, String str3);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public PhoneSubInfoController(Context context) {
        TelephonyServiceManager.ServiceRegisterer phoneSubServiceRegisterer = TelephonyFrameworkInitializer.getTelephonyServiceManager().getPhoneSubServiceRegisterer();
        if (phoneSubServiceRegisterer.get() == null) {
            phoneSubServiceRegisterer.register(this);
        }
        this.mAppOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mContext = context;
    }

    @Deprecated
    public String getDeviceId(String str) {
        return getDeviceIdWithFeature(str, null);
    }

    public String getDeviceIdWithFeature(String str, String str2) {
        return getDeviceIdForPhone(SubscriptionManager.getPhoneId(getDefaultSubscription()), str, str2);
    }

    public String getDeviceIdForPhone(int i, String str, String str2) {
        enforceCallingPackageUidMatched(str);
        return (String) callPhoneMethodForPhoneIdWithReadDeviceIdentifiersCheck(i, str, str2, "getDeviceId", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda18
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String deviceId;
                deviceId = phone.getDeviceId();
                return deviceId;
            }
        });
    }

    public String getNaiForSubscriber(int i, String str, String str2) {
        return (String) callPhoneMethodForSubIdWithReadSubscriberIdentifiersCheck(i, str, str2, "getNai", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda22
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String nai;
                nai = phone.getNai();
                return nai;
            }
        });
    }

    public String getImeiForSubscriber(int i, String str, String str2) {
        return (String) callPhoneMethodForSubIdWithReadDeviceIdentifiersCheck(i, str, str2, "getImei", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda14
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String imei;
                imei = phone.getImei();
                return imei;
            }
        });
    }

    public ImsiEncryptionInfo getCarrierInfoForImsiEncryption(int i, final int i2, String str) {
        return (ImsiEncryptionInfo) callPhoneMethodForSubIdWithPrivilegedCheck(i, "getCarrierInfoForImsiEncryption", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda20
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                ImsiEncryptionInfo carrierInfoForImsiEncryption;
                carrierInfoForImsiEncryption = phone.getCarrierInfoForImsiEncryption(i2, true);
                return carrierInfoForImsiEncryption;
            }
        });
    }

    public void setCarrierInfoForImsiEncryption(int i, String str, final ImsiEncryptionInfo imsiEncryptionInfo) {
        callPhoneMethodForSubIdWithModifyCheck(i, str, "setCarrierInfoForImsiEncryption", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda11
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                Object carrierInfoForImsiEncryption;
                carrierInfoForImsiEncryption = phone.setCarrierInfoForImsiEncryption(imsiEncryptionInfo);
                return carrierInfoForImsiEncryption;
            }
        });
    }

    public void resetCarrierKeysForImsiEncryption(int i, String str) {
        callPhoneMethodForSubIdWithModifyCheck(i, str, "resetCarrierKeysForImsiEncryption", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda9
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                Object resetCarrierKeysForImsiEncryption;
                resetCarrierKeysForImsiEncryption = phone.resetCarrierKeysForImsiEncryption();
                return resetCarrierKeysForImsiEncryption;
            }
        });
    }

    public String getDeviceSvn(String str, String str2) {
        return getDeviceSvnUsingSubId(getDefaultSubscription(), str, str2);
    }

    public String getDeviceSvnUsingSubId(int i, String str, String str2) {
        return (String) callPhoneMethodForSubIdWithReadCheck(i, str, str2, "getDeviceSvn", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda16
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String deviceSvn;
                deviceSvn = phone.getDeviceSvn();
                return deviceSvn;
            }
        });
    }

    @Deprecated
    public String getSubscriberId(String str) {
        return getSubscriberIdWithFeature(str, null);
    }

    public String getSubscriberIdWithFeature(String str, String str2) {
        return getSubscriberIdForSubscriber(getDefaultSubscription(), str, str2);
    }

    public String getSubscriberIdForSubscriber(int i, String str, String str2) {
        boolean isActiveSubId;
        this.mAppOps.checkPackage(Binder.getCallingUid(), str);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                isActiveSubId = SubscriptionManagerService.getInstance().isActiveSubId(i, str, str2);
            } else {
                isActiveSubId = SubscriptionController.getInstance().isActiveSubId(i, str, str2);
            }
            if (isActiveSubId) {
                return (String) callPhoneMethodForSubIdWithReadSubscriberIdentifiersCheck(i, str, str2, "getSubscriberIdForSubscriber", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda15
                    @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
                    public final Object callMethod(Phone phone) {
                        String subscriberId;
                        subscriberId = phone.getSubscriberId();
                        return subscriberId;
                    }
                });
            }
            if (TelephonyPermissions.checkCallingOrSelfReadSubscriberIdentifiers(this.mContext, i, str, str2, "getSubscriberIdForSubscriber")) {
                clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                        SubscriptionInfoInternal subscriptionInfoInternal = SubscriptionManagerService.getInstance().getSubscriptionInfoInternal(i);
                        if (subscriptionInfoInternal == null || TextUtils.isEmpty(subscriptionInfoInternal.getImsi())) {
                            return null;
                        }
                        return subscriptionInfoInternal.getImsi();
                    }
                    return SubscriptionController.getInstance().getImsiPrivileged(i);
                } finally {
                }
            }
            return null;
        } finally {
        }
    }

    @Deprecated
    public String getIccSerialNumber(String str) {
        return getIccSerialNumberWithFeature(str, null);
    }

    public String getIccSerialNumberWithFeature(String str, String str2) {
        return getIccSerialNumberForSubscriber(getDefaultSubscription(), str, str2);
    }

    public String getIccSerialNumberForSubscriber(int i, String str, String str2) {
        return (String) callPhoneMethodForSubIdWithReadSubscriberIdentifiersCheck(i, str, str2, "getIccSerialNumber", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda5
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String iccSerialNumber;
                iccSerialNumber = phone.getIccSerialNumber();
                return iccSerialNumber;
            }
        });
    }

    public String getLine1Number(String str, String str2) {
        return getLine1NumberForSubscriber(getDefaultSubscription(), str, str2);
    }

    public String getLine1NumberForSubscriber(int i, String str, String str2) {
        return (String) callPhoneMethodForSubIdWithReadPhoneNumberCheck(i, str, str2, "getLine1Number", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda0
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String line1Number;
                line1Number = phone.getLine1Number();
                return line1Number;
            }
        });
    }

    public String getLine1AlphaTag(String str, String str2) {
        return getLine1AlphaTagForSubscriber(getDefaultSubscription(), str, str2);
    }

    public String getLine1AlphaTagForSubscriber(int i, String str, String str2) {
        return (String) callPhoneMethodForSubIdWithReadCheck(i, str, str2, "getLine1AlphaTag", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda6
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String line1AlphaTag;
                line1AlphaTag = phone.getLine1AlphaTag();
                return line1AlphaTag;
            }
        });
    }

    public String getMsisdn(String str, String str2) {
        return getMsisdnForSubscriber(getDefaultSubscription(), str, str2);
    }

    public String getMsisdnForSubscriber(int i, String str, String str2) {
        return (String) callPhoneMethodForSubIdWithReadPhoneNumberCheck(i, str, str2, "getMsisdn", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda10
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String msisdn;
                msisdn = phone.getMsisdn();
                return msisdn;
            }
        });
    }

    public String getVoiceMailNumber(String str, String str2) {
        return getVoiceMailNumberForSubscriber(getDefaultSubscription(), str, str2);
    }

    public String getVoiceMailNumberForSubscriber(int i, String str, String str2) {
        return (String) callPhoneMethodForSubIdWithReadCheck(i, str, str2, "getVoiceMailNumber", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda12
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String lambda$getVoiceMailNumberForSubscriber$12;
                lambda$getVoiceMailNumberForSubscriber$12 = PhoneSubInfoController.this.lambda$getVoiceMailNumberForSubscriber$12(phone);
                return lambda$getVoiceMailNumberForSubscriber$12;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getVoiceMailNumberForSubscriber$12(Phone phone) {
        return PhoneNumberUtils.extractNetworkPortion(phone.getVoiceMailNumber());
    }

    public String getVoiceMailAlphaTag(String str, String str2) {
        return getVoiceMailAlphaTagForSubscriber(getDefaultSubscription(), str, str2);
    }

    public String getVoiceMailAlphaTagForSubscriber(int i, String str, String str2) {
        return (String) callPhoneMethodForSubIdWithReadCheck(i, str, str2, "getVoiceMailAlphaTag", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda17
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String voiceMailAlphaTag;
                voiceMailAlphaTag = phone.getVoiceMailAlphaTag();
                return voiceMailAlphaTag;
            }
        });
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private Phone getPhone(int i) {
        int phoneId = SubscriptionManager.getPhoneId(i);
        if (SubscriptionManager.isValidPhoneId(phoneId)) {
            return PhoneFactory.getPhone(phoneId);
        }
        return null;
    }

    private void enforceCallingPackageUidMatched(String str) {
        try {
            this.mAppOps.checkPackage(Binder.getCallingUid(), str);
        } catch (SecurityException e) {
            EventLog.writeEvent(1397638484, "188677422", Integer.valueOf(Binder.getCallingUid()));
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean enforceIccSimChallengeResponsePermission(Context context, int i, String str, String str2, String str3) {
        if (TelephonyPermissions.checkCallingOrSelfUseIccAuthWithDeviceIdentifier(context, str, str2, str3)) {
            return true;
        }
        enforcePrivilegedPermissionOrCarrierPrivilege(i, str3);
        return true;
    }

    private void enforcePrivilegedPermissionOrCarrierPrivilege(int i, String str) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE") == 0) {
            return;
        }
        TelephonyPermissions.enforceCallingOrSelfCarrierPrivilege(this.mContext, i, str);
    }

    private void enforceModifyPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE", "Requires MODIFY_PHONE_STATE");
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int getDefaultSubscription() {
        return PhoneFactory.getDefaultSubscription();
    }

    public String getIsimImpi(int i) {
        return (String) callPhoneMethodForSubIdWithPrivilegedCheck(i, "getIsimImpi", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda8
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String lambda$getIsimImpi$14;
                lambda$getIsimImpi$14 = PhoneSubInfoController.lambda$getIsimImpi$14(phone);
                return lambda$getIsimImpi$14;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$getIsimImpi$14(Phone phone) {
        IsimRecords isimRecords = phone.getIsimRecords();
        if (isimRecords != null) {
            return isimRecords.getIsimImpi();
        }
        return null;
    }

    public String getImsPrivateUserIdentity(int i, String str, String str2) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            throw new IllegalArgumentException("Invalid SubscriptionID  = " + i);
        } else if (!TelephonyPermissions.checkCallingOrSelfUseIccAuthWithDeviceIdentifier(this.mContext, str, str2, "getImsPrivateUserIdentity")) {
            throw new SecurityException("No permissions to the caller");
        } else {
            IsimRecords isimRecords = getPhone(i).getIsimRecords();
            if (isimRecords != null) {
                return isimRecords.getIsimImpi();
            }
            throw new IllegalStateException("ISIM is not loaded");
        }
    }

    public String getIsimDomain(int i) {
        return (String) callPhoneMethodForSubIdWithPrivilegedCheck(i, "getIsimDomain", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda13
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String lambda$getIsimDomain$15;
                lambda$getIsimDomain$15 = PhoneSubInfoController.lambda$getIsimDomain$15(phone);
                return lambda$getIsimDomain$15;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$getIsimDomain$15(Phone phone) {
        IsimRecords isimRecords = phone.getIsimRecords();
        if (isimRecords != null) {
            return isimRecords.getIsimDomain();
        }
        return null;
    }

    public String[] getIsimImpu(int i) {
        return (String[]) callPhoneMethodForSubIdWithPrivilegedCheck(i, "getIsimImpu", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda7
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String[] lambda$getIsimImpu$16;
                lambda$getIsimImpu$16 = PhoneSubInfoController.lambda$getIsimImpu$16(phone);
                return lambda$getIsimImpu$16;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String[] lambda$getIsimImpu$16(Phone phone) {
        IsimRecords isimRecords = phone.getIsimRecords();
        if (isimRecords != null) {
            return isimRecords.getIsimImpu();
        }
        return null;
    }

    public List<Uri> getImsPublicUserIdentities(int i, String str, String str2) {
        if (TelephonyPermissions.checkCallingOrSelfReadPrivilegedPhoneStatePermissionOrReadPhoneNumber(this.mContext, i, str, str2, "getImsPublicUserIdentities")) {
            IsimRecords isimRecords = getPhone(i).getIsimRecords();
            if (isimRecords != null) {
                String[] isimImpu = isimRecords.getIsimImpu();
                ArrayList arrayList = new ArrayList();
                for (String str3 : isimImpu) {
                    if (str3 != null && str3.trim().length() > 0) {
                        arrayList.add(Uri.parse(str3));
                    }
                }
                return arrayList;
            }
            throw new IllegalStateException("ISIM is not loaded");
        }
        throw new IllegalArgumentException("Invalid SubscriptionID  = " + i);
    }

    public String getIsimIst(int i) throws RemoteException {
        return (String) callPhoneMethodForSubIdWithPrivilegedCheck(i, "getIsimIst", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda4
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String lambda$getIsimIst$17;
                lambda$getIsimIst$17 = PhoneSubInfoController.lambda$getIsimIst$17(phone);
                return lambda$getIsimIst$17;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$getIsimIst$17(Phone phone) {
        IsimRecords isimRecords = phone.getIsimRecords();
        if (isimRecords != null) {
            return isimRecords.getIsimIst();
        }
        return null;
    }

    public String[] getIsimPcscf(int i) throws RemoteException {
        return (String[]) callPhoneMethodForSubIdWithPrivilegedCheck(i, "getIsimPcscf", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda21
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String[] lambda$getIsimPcscf$18;
                lambda$getIsimPcscf$18 = PhoneSubInfoController.lambda$getIsimPcscf$18(phone);
                return lambda$getIsimPcscf$18;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String[] lambda$getIsimPcscf$18(Phone phone) {
        IsimRecords isimRecords = phone.getIsimRecords();
        if (isimRecords != null) {
            return isimRecords.getIsimPcscf();
        }
        return null;
    }

    public String getSimServiceTable(int i, final int i2) throws RemoteException {
        return (String) callPhoneMethodForSubIdWithPrivilegedCheck(i, "getSimServiceTable", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda1
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String lambda$getSimServiceTable$19;
                lambda$getSimServiceTable$19 = PhoneSubInfoController.this.lambda$getSimServiceTable$19(i2, phone);
                return lambda$getSimServiceTable$19;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getSimServiceTable$19(int i, Phone phone) {
        UiccPort uiccPort = phone.getUiccPort();
        if (uiccPort == null || uiccPort.getUiccProfile() == null) {
            loge("getSimServiceTable(): uiccPort or uiccProfile is null");
            return null;
        }
        UiccCardApplication applicationByType = uiccPort.getUiccProfile().getApplicationByType(i);
        if (applicationByType == null) {
            loge("getSimServiceTable(): no app with specified apptype=" + i);
            return null;
        }
        return ((SIMRecords) applicationByType.getIccRecords()).getSimServiceTable();
    }

    public String getIccSimChallengeResponse(int i, final int i2, final int i3, final String str, String str2, String str3) throws RemoteException {
        return (String) callPhoneMethodWithPermissionCheck(i, str2, str3, "getIccSimChallengeResponse", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda2
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String lambda$getIccSimChallengeResponse$20;
                lambda$getIccSimChallengeResponse$20 = PhoneSubInfoController.this.lambda$getIccSimChallengeResponse$20(i2, i3, str, phone);
                return lambda$getIccSimChallengeResponse$20;
            }
        }, new PermissionCheckHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda3
            @Override // com.android.internal.telephony.PhoneSubInfoController.PermissionCheckHelper
            public final boolean checkPermission(Context context, int i4, String str4, String str5, String str6) {
                boolean enforceIccSimChallengeResponsePermission;
                enforceIccSimChallengeResponsePermission = PhoneSubInfoController.this.enforceIccSimChallengeResponsePermission(context, i4, str4, str5, str6);
                return enforceIccSimChallengeResponsePermission;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getIccSimChallengeResponse$20(int i, int i2, String str, Phone phone) {
        UiccPort uiccPort = phone.getUiccPort();
        if (uiccPort == null) {
            loge("getIccSimChallengeResponse() uiccPort is null");
            return null;
        }
        UiccCardApplication applicationByType = uiccPort.getApplicationByType(i);
        if (applicationByType == null) {
            loge("getIccSimChallengeResponse() no app with specified type -- " + i);
            return null;
        }
        loge("getIccSimChallengeResponse() found app " + applicationByType.getAid() + " specified type -- " + i);
        if (i2 != 128 && i2 != 129 && i2 != 132 && i2 != 133) {
            loge("getIccSimChallengeResponse() unsupported authType: " + i2);
            return null;
        }
        return applicationByType.getIccRecords().getIccSimChallengeResponse(i2, str);
    }

    public String getGroupIdLevel1ForSubscriber(int i, String str, String str2) {
        return (String) callPhoneMethodForSubIdWithReadCheck(i, str, str2, "getGroupIdLevel1", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda19
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                String groupIdLevel1;
                groupIdLevel1 = phone.getGroupIdLevel1();
                return groupIdLevel1;
            }
        });
    }

    private <T> T callPhoneMethodWithPermissionCheck(int i, String str, String str2, String str3, CallPhoneMethodHelper<T> callPhoneMethodHelper, PermissionCheckHelper permissionCheckHelper) {
        if (permissionCheckHelper.checkPermission(this.mContext, i, str, str2, str3)) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                Phone phone = getPhone(i);
                if (phone != null) {
                    return callPhoneMethodHelper.callMethod(phone);
                }
                return null;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return null;
    }

    private <T> T callPhoneMethodForSubIdWithReadCheck(int i, String str, String str2, String str3, CallPhoneMethodHelper<T> callPhoneMethodHelper) {
        return (T) callPhoneMethodWithPermissionCheck(i, str, str2, str3, callPhoneMethodHelper, new PermissionCheckHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda29
            @Override // com.android.internal.telephony.PhoneSubInfoController.PermissionCheckHelper
            public final boolean checkPermission(Context context, int i2, String str4, String str5, String str6) {
                boolean checkCallingOrSelfReadPhoneState;
                checkCallingOrSelfReadPhoneState = TelephonyPermissions.checkCallingOrSelfReadPhoneState(context, i2, str4, str5, str6);
                return checkCallingOrSelfReadPhoneState;
            }
        });
    }

    private <T> T callPhoneMethodForSubIdWithReadDeviceIdentifiersCheck(int i, String str, String str2, String str3, CallPhoneMethodHelper<T> callPhoneMethodHelper) {
        return (T) callPhoneMethodWithPermissionCheck(i, str, str2, str3, callPhoneMethodHelper, new PermissionCheckHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda27
            @Override // com.android.internal.telephony.PhoneSubInfoController.PermissionCheckHelper
            public final boolean checkPermission(Context context, int i2, String str4, String str5, String str6) {
                boolean checkCallingOrSelfReadDeviceIdentifiers;
                checkCallingOrSelfReadDeviceIdentifiers = TelephonyPermissions.checkCallingOrSelfReadDeviceIdentifiers(context, i2, str4, str5, str6);
                return checkCallingOrSelfReadDeviceIdentifiers;
            }
        });
    }

    private <T> T callPhoneMethodForSubIdWithReadSubscriberIdentifiersCheck(int i, String str, String str2, String str3, CallPhoneMethodHelper<T> callPhoneMethodHelper) {
        return (T) callPhoneMethodWithPermissionCheck(i, str, str2, str3, callPhoneMethodHelper, new PermissionCheckHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda25
            @Override // com.android.internal.telephony.PhoneSubInfoController.PermissionCheckHelper
            public final boolean checkPermission(Context context, int i2, String str4, String str5, String str6) {
                boolean checkCallingOrSelfReadSubscriberIdentifiers;
                checkCallingOrSelfReadSubscriberIdentifiers = TelephonyPermissions.checkCallingOrSelfReadSubscriberIdentifiers(context, i2, str4, str5, str6);
                return checkCallingOrSelfReadSubscriberIdentifiers;
            }
        });
    }

    private <T> T callPhoneMethodForSubIdWithPrivilegedCheck(int i, final String str, CallPhoneMethodHelper<T> callPhoneMethodHelper) {
        return (T) callPhoneMethodWithPermissionCheck(i, null, null, str, callPhoneMethodHelper, new PermissionCheckHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda24
            @Override // com.android.internal.telephony.PhoneSubInfoController.PermissionCheckHelper
            public final boolean checkPermission(Context context, int i2, String str2, String str3, String str4) {
                boolean lambda$callPhoneMethodForSubIdWithPrivilegedCheck$25;
                lambda$callPhoneMethodForSubIdWithPrivilegedCheck$25 = PhoneSubInfoController.this.lambda$callPhoneMethodForSubIdWithPrivilegedCheck$25(str, context, i2, str2, str3, str4);
                return lambda$callPhoneMethodForSubIdWithPrivilegedCheck$25;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$callPhoneMethodForSubIdWithPrivilegedCheck$25(String str, Context context, int i, String str2, String str3, String str4) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", str);
        return true;
    }

    private <T> T callPhoneMethodForSubIdWithModifyCheck(int i, String str, String str2, CallPhoneMethodHelper<T> callPhoneMethodHelper) {
        return (T) callPhoneMethodWithPermissionCheck(i, null, null, str2, callPhoneMethodHelper, new PermissionCheckHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda26
            @Override // com.android.internal.telephony.PhoneSubInfoController.PermissionCheckHelper
            public final boolean checkPermission(Context context, int i2, String str3, String str4, String str5) {
                boolean lambda$callPhoneMethodForSubIdWithModifyCheck$26;
                lambda$callPhoneMethodForSubIdWithModifyCheck$26 = PhoneSubInfoController.this.lambda$callPhoneMethodForSubIdWithModifyCheck$26(context, i2, str3, str4, str5);
                return lambda$callPhoneMethodForSubIdWithModifyCheck$26;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$callPhoneMethodForSubIdWithModifyCheck$26(Context context, int i, String str, String str2, String str3) {
        enforceModifyPermission();
        return true;
    }

    private <T> T callPhoneMethodForSubIdWithReadPhoneNumberCheck(int i, String str, String str2, String str3, CallPhoneMethodHelper<T> callPhoneMethodHelper) {
        return (T) callPhoneMethodWithPermissionCheck(i, str, str2, str3, callPhoneMethodHelper, new PermissionCheckHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda28
            @Override // com.android.internal.telephony.PhoneSubInfoController.PermissionCheckHelper
            public final boolean checkPermission(Context context, int i2, String str4, String str5, String str6) {
                boolean checkCallingOrSelfReadPhoneNumber;
                checkCallingOrSelfReadPhoneNumber = TelephonyPermissions.checkCallingOrSelfReadPhoneNumber(context, i2, str4, str5, str6);
                return checkCallingOrSelfReadPhoneNumber;
            }
        });
    }

    private <T> T callPhoneMethodForPhoneIdWithReadDeviceIdentifiersCheck(int i, String str, String str2, String str3, CallPhoneMethodHelper<T> callPhoneMethodHelper) {
        if (!SubscriptionManager.isValidPhoneId(i)) {
            i = 0;
        }
        Phone phone = PhoneFactory.getPhone(i);
        if (phone != null && TelephonyPermissions.checkCallingOrSelfReadDeviceIdentifiers(this.mContext, phone.getSubId(), str, str2, str3)) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return callPhoneMethodHelper.callMethod(phone);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return null;
    }

    public Uri getSmscIdentity(int i, final int i2) throws RemoteException {
        Uri uri = (Uri) callPhoneMethodForSubIdWithPrivilegedCheck(i, "getSmscIdentity", new CallPhoneMethodHelper() { // from class: com.android.internal.telephony.PhoneSubInfoController$$ExternalSyntheticLambda23
            @Override // com.android.internal.telephony.PhoneSubInfoController.CallPhoneMethodHelper
            public final Object callMethod(Phone phone) {
                Uri lambda$getSmscIdentity$28;
                lambda$getSmscIdentity$28 = PhoneSubInfoController.lambda$getSmscIdentity$28(i2, phone);
                return lambda$getSmscIdentity$28;
            }
        });
        if (uri != null) {
            return uri;
        }
        throw new IllegalStateException("Telephony service error");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Uri lambda$getSmscIdentity$28(int i, Phone phone) {
        try {
            UiccCardApplication applicationByType = phone.getUiccPort().getUiccProfile().getApplicationByType(i);
            String smscIdentity = applicationByType != null ? applicationByType.getIccRecords().getSmscIdentity() : null;
            if (TextUtils.isEmpty(smscIdentity)) {
                return Uri.EMPTY;
            }
            return Uri.parse(smscIdentity);
        } catch (NullPointerException e) {
            Rlog.e("PhoneSubInfoController", "getSmscIdentity(): Exception = " + e);
            return null;
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void loge(String str) {
        Rlog.e("PhoneSubInfoController", str);
    }
}
