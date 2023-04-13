package com.android.internal.telephony;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import com.android.internal.telephony.ims.ImsResolver;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class CarrierSmsUtils {
    protected static final String TAG = "CarrierSmsUtils";
    protected static final boolean VDBG = false;

    public static String getImsRcsPackageForIntent(Context context, Phone phone, Intent intent) {
        String imsRcsPackage = getImsRcsPackage(phone);
        if (imsRcsPackage == null) {
            return null;
        }
        for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentServices(intent, 0)) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo == null) {
                String str = TAG;
                Rlog.e(str, "Can't get service information from " + resolveInfo);
            } else if (imsRcsPackage.equals(serviceInfo.packageName)) {
                return imsRcsPackage;
            }
        }
        return null;
    }

    private static String getImsRcsPackage(Phone phone) {
        ImsResolver imsResolver = ImsResolver.getInstance();
        if (imsResolver == null) {
            Rlog.i(TAG, "getImsRcsPackage: Device does not support IMS - skipping");
            return null;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return imsResolver.getConfiguredImsServicePackageName(phone.getPhoneId(), 2);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    private CarrierSmsUtils() {
    }
}
