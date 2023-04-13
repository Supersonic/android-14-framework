package com.android.internal.app;

import android.app.Activity;
import android.app.IUiModeManager;
import android.content.Context;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.Log;
/* loaded from: classes4.dex */
public class DisableCarModeActivity extends Activity {
    private static final String TAG = "DisableCarModeActivity";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            IUiModeManager uiModeManager = IUiModeManager.Stub.asInterface(ServiceManager.getService(Context.UI_MODE_SERVICE));
            uiModeManager.disableCarModeByCallingPackage(3, getOpPackageName());
        } catch (RemoteException e) {
            Log.m109e(TAG, "Failed to disable car mode", e);
        }
        finish();
    }
}
