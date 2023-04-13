package com.android.internal.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.p008os.Bundle;
import android.p008os.IPowerManager;
import android.p008os.PowerManager;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.Slog;
/* loaded from: classes4.dex */
public class ShutdownActivity extends Activity {
    private static final String TAG = "ShutdownActivity";
    private boolean mConfirm;
    private boolean mReboot;
    private boolean mUserRequested;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        final String reason;
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.mReboot = Intent.ACTION_REBOOT.equals(intent.getAction());
        this.mConfirm = intent.getBooleanExtra(Intent.EXTRA_KEY_CONFIRM, false);
        boolean booleanExtra = intent.getBooleanExtra(Intent.EXTRA_USER_REQUESTED_SHUTDOWN, false);
        this.mUserRequested = booleanExtra;
        if (booleanExtra) {
            reason = PowerManager.SHUTDOWN_USER_REQUESTED;
        } else {
            reason = intent.getStringExtra(Intent.EXTRA_REASON);
        }
        Slog.m94i(TAG, "onCreate(): confirm=" + this.mConfirm);
        Thread thr = new Thread(TAG) { // from class: com.android.internal.app.ShutdownActivity.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager.getService(Context.POWER_SERVICE));
                try {
                    if (ShutdownActivity.this.mReboot) {
                        pm.reboot(ShutdownActivity.this.mConfirm, null, false);
                    } else {
                        pm.shutdown(ShutdownActivity.this.mConfirm, reason, false);
                    }
                } catch (RemoteException e) {
                }
            }
        };
        thr.start();
        finish();
        try {
            thr.join();
        } catch (InterruptedException e) {
        }
    }
}
