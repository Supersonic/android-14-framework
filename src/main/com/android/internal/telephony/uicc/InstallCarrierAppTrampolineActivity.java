package com.android.internal.telephony.uicc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class InstallCarrierAppTrampolineActivity extends Activity {
    private String mPackageName;

    public static Intent get(Context context, String str) {
        Intent intent = new Intent(context, InstallCarrierAppTrampolineActivity.class);
        intent.putExtra("package_name", str);
        return intent;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent != null) {
            this.mPackageName = intent.getStringExtra("package_name");
        }
        if (bundle == null) {
            long j = Settings.Global.getLong(getContentResolver(), "install_carrier_app_notification_sleep_millis", TimeUnit.HOURS.toMillis(24L));
            Log.d("CarrierAppInstall", "Sleeping carrier app install notification for : " + j + " millis");
            InstallCarrierAppUtils.showNotificationIfNotInstalledDelayed(this, this.mPackageName, j);
        }
        Intent intent2 = new Intent();
        intent2.setComponent(ComponentName.unflattenFromString(Resources.getSystem().getString(17039848)));
        String appNameFromPackageName = InstallCarrierAppUtils.getAppNameFromPackageName(this, this.mPackageName);
        if (!TextUtils.isEmpty(appNameFromPackageName)) {
            intent2.putExtra("carrier_name", appNameFromPackageName);
        }
        if (intent2.resolveActivity(getPackageManager()) == null) {
            Log.d("CarrierAppInstall", "Could not resolve activity for installing the carrier app");
            finishNoAnimation();
            return;
        }
        startActivityForResult(intent2, 1);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1) {
            if (i2 == 2) {
                startActivity(InstallCarrierAppUtils.getPlayStoreIntent(this.mPackageName));
            }
            finishNoAnimation();
        }
    }

    private void finishNoAnimation() {
        finish();
        overridePendingTransition(0, 0);
    }
}
