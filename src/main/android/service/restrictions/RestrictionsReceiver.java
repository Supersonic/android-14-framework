package android.service.restrictions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.RestrictionsManager;
import android.p008os.PersistableBundle;
/* loaded from: classes3.dex */
public abstract class RestrictionsReceiver extends BroadcastReceiver {
    private static final String TAG = "RestrictionsReceiver";

    public abstract void onRequestPermission(Context context, String str, String str2, String str3, PersistableBundle persistableBundle);

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (RestrictionsManager.ACTION_REQUEST_PERMISSION.equals(action)) {
            String packageName = intent.getStringExtra(RestrictionsManager.EXTRA_PACKAGE_NAME);
            String requestType = intent.getStringExtra(RestrictionsManager.EXTRA_REQUEST_TYPE);
            String requestId = intent.getStringExtra(RestrictionsManager.EXTRA_REQUEST_ID);
            PersistableBundle request = (PersistableBundle) intent.getParcelableExtra(RestrictionsManager.EXTRA_REQUEST_BUNDLE, PersistableBundle.class);
            onRequestPermission(context, packageName, requestType, requestId, request);
        }
    }
}
