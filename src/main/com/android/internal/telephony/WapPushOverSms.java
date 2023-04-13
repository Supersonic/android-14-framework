package com.android.internal.telephony;

import android.app.BroadcastOptions;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerWhitelistManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import com.android.internal.telephony.IWapPushManager;
import com.android.internal.telephony.InboundSmsHandler;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import com.google.android.mms.pdu.GenericPdu;
import com.google.android.mms.pdu.NotificationInd;
import com.google.android.mms.pdu.PduParser;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes.dex */
public class WapPushOverSms implements ServiceConnection {
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final Context mContext;
    PowerWhitelistManager mPowerWhitelistManager;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private volatile IWapPushManager mWapPushManager;
    private String mWapPushManagerPackage;

    private void bindWapPushManagerService(Context context) {
        Intent intent = new Intent(IWapPushManager.class.getName());
        ComponentName resolveSystemService = resolveSystemService(context.getPackageManager(), intent);
        intent.setComponent(resolveSystemService);
        if (resolveSystemService == null || !context.bindService(intent, this, 1)) {
            Rlog.e("WAP PUSH", "bindService() for wappush manager failed");
            return;
        }
        synchronized (this) {
            this.mWapPushManagerPackage = resolveSystemService.getPackageName();
        }
    }

    private static ComponentName resolveSystemService(PackageManager packageManager, Intent intent) {
        List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(intent, 1048576);
        ComponentName componentName = null;
        if (queryIntentServices == null) {
            return null;
        }
        int i = 0;
        while (i < queryIntentServices.size()) {
            ServiceInfo serviceInfo = queryIntentServices.get(i).serviceInfo;
            ComponentName componentName2 = new ComponentName(serviceInfo.applicationInfo.packageName, serviceInfo.name);
            if (componentName != null) {
                throw new IllegalStateException("Multiple system services handle " + intent + ": " + componentName + ", " + componentName2);
            }
            i++;
            componentName = componentName2;
        }
        return componentName;
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.mWapPushManager = IWapPushManager.Stub.asInterface(iBinder);
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        this.mWapPushManager = null;
    }

    public WapPushOverSms(Context context) {
        this.mContext = context;
        this.mPowerWhitelistManager = (PowerWhitelistManager) context.getSystemService(PowerWhitelistManager.class);
        UserManager userManager = (UserManager) context.getSystemService("user");
        bindWapPushManagerService(context);
    }

    public void dispose() {
        if (this.mWapPushManager != null) {
            this.mContext.unbindService(this);
        } else {
            Rlog.e("WAP PUSH", "dispose: not bound to a wappush manager");
        }
    }

    private DecodedResult decodeWapPdu(byte[] bArr, InboundSmsHandler inboundSmsHandler) {
        int i;
        int i2;
        int phoneId;
        int i3;
        WspTypeDecoder makeWspTypeDecoder;
        byte[] bArr2;
        GenericPdu genericPdu;
        DecodedResult decodedResult = new DecodedResult();
        try {
            i = bArr[0] & 255;
            i2 = bArr[1] & 255;
            phoneId = inboundSmsHandler.getPhone().getPhoneId();
            if (i2 == 6 || i2 == 7) {
                i3 = 2;
            } else {
                int integer = this.mContext.getResources().getInteger(17694981);
                if (integer != -1) {
                    int i4 = integer + 1;
                    i = bArr[integer] & 255;
                    i3 = i4 + 1;
                    i2 = bArr[i4] & 255;
                    if (i2 != 6 && i2 != 7) {
                        decodedResult.statusCode = 1;
                        return decodedResult;
                    }
                } else {
                    decodedResult.statusCode = 1;
                    return decodedResult;
                }
            }
            makeWspTypeDecoder = TelephonyComponentFactory.getInstance().inject(WspTypeDecoder.class.getName()).makeWspTypeDecoder(bArr);
        } catch (ArrayIndexOutOfBoundsException e) {
            Rlog.e("WAP PUSH", "ignoring dispatchWapPdu() array index exception: " + e);
            decodedResult.statusCode = 2;
        }
        if (!makeWspTypeDecoder.decodeUintvarInteger(i3)) {
            decodedResult.statusCode = 2;
            return decodedResult;
        }
        int i5 = i2;
        int value32 = (int) makeWspTypeDecoder.getValue32();
        int decodedDataLength = i3 + makeWspTypeDecoder.getDecodedDataLength();
        if (!makeWspTypeDecoder.decodeContentType(decodedDataLength)) {
            decodedResult.statusCode = 2;
            return decodedResult;
        }
        String valueString = makeWspTypeDecoder.getValueString();
        long value322 = makeWspTypeDecoder.getValue32();
        int decodedDataLength2 = decodedDataLength + makeWspTypeDecoder.getDecodedDataLength();
        byte[] bArr3 = new byte[value32];
        System.arraycopy(bArr, decodedDataLength, bArr3, 0, value32);
        if (valueString == null || !valueString.equals(WspTypeDecoder.CONTENT_TYPE_B_PUSH_CO)) {
            int i6 = decodedDataLength + value32;
            int length = bArr.length - i6;
            bArr2 = new byte[length];
            System.arraycopy(bArr, i6, bArr2, 0, length);
        } else {
            bArr2 = bArr;
        }
        int subscriptionId = SubscriptionManager.getSubscriptionId(phoneId);
        if (!SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
            subscriptionId = SmsManager.getDefaultSmsSubscriptionId();
        }
        int i7 = subscriptionId;
        try {
            genericPdu = new PduParser(bArr2, shouldParseContentDisposition(i7)).parse();
        } catch (Exception e2) {
            Rlog.e("WAP PUSH", "Unable to parse PDU: " + e2.toString());
            genericPdu = null;
        }
        if (genericPdu != null && genericPdu.getMessageType() == 130) {
            NotificationInd notificationInd = (NotificationInd) genericPdu;
            if (notificationInd.getFrom() != null && BlockChecker.isBlocked(this.mContext, notificationInd.getFrom().getString(), null)) {
                decodedResult.statusCode = 1;
                return decodedResult;
            }
        }
        if (makeWspTypeDecoder.seekXWapApplicationId(decodedDataLength2, (value32 + decodedDataLength2) - 1)) {
            makeWspTypeDecoder.decodeXWapApplicationId((int) makeWspTypeDecoder.getValue32());
            String valueString2 = makeWspTypeDecoder.getValueString();
            if (valueString2 == null) {
                valueString2 = Integer.toString((int) makeWspTypeDecoder.getValue32());
            }
            decodedResult.wapAppId = valueString2;
            decodedResult.contentType = valueString == null ? Long.toString(value322) : valueString;
        }
        decodedResult.subId = i7;
        decodedResult.phoneId = phoneId;
        decodedResult.parsedPdu = genericPdu;
        decodedResult.mimeType = valueString;
        decodedResult.transactionId = i;
        decodedResult.pduType = i5;
        decodedResult.header = bArr3;
        decodedResult.intentData = bArr2;
        decodedResult.contentTypeParameters = makeWspTypeDecoder.getContentParameters();
        decodedResult.statusCode = -1;
        return decodedResult;
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x007b A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int dispatchWapPdu(byte[] bArr, InboundSmsHandler.SmsBroadcastReceiver smsBroadcastReceiver, InboundSmsHandler inboundSmsHandler, String str, int i, long j) {
        boolean z;
        Bundle bundle;
        DecodedResult decodeWapPdu = decodeWapPdu(bArr, inboundSmsHandler);
        int i2 = decodeWapPdu.statusCode;
        if (i2 != -1) {
            return i2;
        }
        if (decodeWapPdu.wapAppId != null) {
            try {
                IWapPushManager iWapPushManager = this.mWapPushManager;
                if (iWapPushManager != null) {
                    synchronized (this) {
                        this.mPowerWhitelistManager.whitelistAppTemporarilyForEvent(this.mWapPushManagerPackage, 2, 315, "mms-mgr");
                    }
                    Intent intent = new Intent();
                    intent.putExtra("transactionId", decodeWapPdu.transactionId);
                    intent.putExtra("pduType", decodeWapPdu.pduType);
                    intent.putExtra("header", decodeWapPdu.header);
                    intent.putExtra("data", decodeWapPdu.intentData);
                    intent.putExtra("contentTypeParameters", decodeWapPdu.contentTypeParameters);
                    SubscriptionManager.putPhoneIdAndSubIdExtra(intent, decodeWapPdu.phoneId);
                    if (!TextUtils.isEmpty(str)) {
                        intent.putExtra("address", str);
                    }
                    int processMessage = iWapPushManager.processMessage(decodeWapPdu.wapAppId, decodeWapPdu.contentType, intent);
                    if ((processMessage & 1) > 0 && (processMessage & 32768) == 0) {
                        z = false;
                        if (!z) {
                            return 1;
                        }
                    }
                }
                z = true;
                if (!z) {
                }
            } catch (RemoteException unused) {
            }
        }
        if (decodeWapPdu.mimeType == null) {
            return 2;
        }
        Intent intent2 = new Intent("android.provider.Telephony.WAP_PUSH_DELIVER");
        intent2.setType(decodeWapPdu.mimeType);
        intent2.putExtra("transactionId", decodeWapPdu.transactionId);
        intent2.putExtra("pduType", decodeWapPdu.pduType);
        intent2.putExtra("header", decodeWapPdu.header);
        intent2.putExtra("data", decodeWapPdu.intentData);
        intent2.putExtra("contentTypeParameters", decodeWapPdu.contentTypeParameters);
        if (!TextUtils.isEmpty(str)) {
            intent2.putExtra("address", str);
        }
        if (j != 0) {
            intent2.putExtra("messageId", j);
        }
        UserHandle subscriptionUserHandle = TelephonyUtils.getSubscriptionUserHandle(this.mContext, i);
        ComponentName defaultMmsApplicationAsUser = SmsApplication.getDefaultMmsApplicationAsUser(this.mContext, true, subscriptionUserHandle);
        if (defaultMmsApplicationAsUser != null) {
            intent2.setComponent(defaultMmsApplicationAsUser);
            long whitelistAppTemporarilyForEvent = this.mPowerWhitelistManager.whitelistAppTemporarilyForEvent(defaultMmsApplicationAsUser.getPackageName(), 2, 315, "mms-app");
            BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
            makeBasic.setTemporaryAppAllowlist(whitelistAppTemporarilyForEvent, 0, 315, PhoneConfigurationManager.SSSS);
            bundle = makeBasic.toBundle();
        } else {
            bundle = null;
        }
        Bundle bundle2 = bundle;
        if (subscriptionUserHandle == null) {
            subscriptionUserHandle = UserHandle.SYSTEM;
        }
        inboundSmsHandler.dispatchIntent(intent2, getPermissionForType(decodeWapPdu.mimeType), getAppOpsStringPermissionForIntent(decodeWapPdu.mimeType), bundle2, smsBroadcastReceiver, subscriptionUserHandle, i);
        return -1;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean isWapPushForMms(byte[] bArr, InboundSmsHandler inboundSmsHandler) {
        DecodedResult decodeWapPdu = decodeWapPdu(bArr, inboundSmsHandler);
        return decodeWapPdu.statusCode == -1 && WspTypeDecoder.CONTENT_TYPE_B_MMS.equals(decodeWapPdu.mimeType);
    }

    private static boolean shouldParseContentDisposition(int i) {
        return SmsManager.getSmsManagerForSubscriptionId(i).getCarrierConfigValues().getBoolean("supportMmsContentDisposition", true);
    }

    public static String getPermissionForType(String str) {
        return WspTypeDecoder.CONTENT_TYPE_B_MMS.equals(str) ? "android.permission.RECEIVE_MMS" : "android.permission.RECEIVE_WAP_PUSH";
    }

    public static String getAppOpsStringPermissionForIntent(String str) {
        return WspTypeDecoder.CONTENT_TYPE_B_MMS.equals(str) ? "android:receive_mms" : "android:receive_wap_push";
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class DecodedResult {
        String contentType;
        HashMap<String, String> contentTypeParameters;
        byte[] header;
        byte[] intentData;
        String mimeType;
        GenericPdu parsedPdu;
        int pduType;
        int phoneId;
        int statusCode;
        int subId;
        int transactionId;
        String wapAppId;

        private DecodedResult() {
        }
    }
}
