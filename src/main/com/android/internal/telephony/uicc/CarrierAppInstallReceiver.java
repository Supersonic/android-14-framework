package com.android.internal.telephony.uicc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
/* loaded from: classes.dex */
public class CarrierAppInstallReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {
            Log.d("CarrierAppInstall", "Received package install intent");
            String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
            if (TextUtils.isEmpty(schemeSpecificPart)) {
                Log.w("CarrierAppInstall", "Package is empty, ignoring");
                return;
            }
            InstallCarrierAppUtils.hideNotification(context, schemeSpecificPart);
            if (InstallCarrierAppUtils.isPackageInstallNotificationActive(context)) {
                return;
            }
            InstallCarrierAppUtils.unregisterPackageInstallReceiver(context);
        }
    }
}
