package com.android.internal.telephony.uicc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/* loaded from: classes.dex */
public class ShowInstallAppNotificationReceiver extends BroadcastReceiver {
    public static Intent get(Context context, String str) {
        Intent intent = new Intent(context, ShowInstallAppNotificationReceiver.class);
        intent.putExtra("package_name", str);
        return intent;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String stringExtra = intent.getStringExtra("package_name");
        if (UiccProfile.isPackageBundled(context, stringExtra)) {
            return;
        }
        InstallCarrierAppUtils.showNotification(context, stringExtra);
        InstallCarrierAppUtils.registerPackageInstallReceiver(context);
    }
}
