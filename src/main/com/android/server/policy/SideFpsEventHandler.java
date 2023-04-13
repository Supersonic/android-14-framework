package com.android.server.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.biometrics.BiometricStateListener;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.hardware.fingerprint.IFingerprintAuthenticatorsRegisteredCallback;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.policy.SideFpsEventHandler;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes2.dex */
public class SideFpsEventHandler implements View.OnClickListener {
    public int mBiometricState;
    public final Context mContext;
    public SideFpsToast mDialog;
    public DialogProvider mDialogProvider;
    public final int mDismissDialogTimeout;
    public FingerprintManager mFingerprintManager;
    public final Handler mHandler;
    public long mLastPowerPressTime;
    public final PowerManager mPowerManager;
    public final AtomicBoolean mSideFpsEventHandlerReady;
    public final Runnable mTurnOffDialog;

    /* loaded from: classes2.dex */
    public interface DialogProvider {
        SideFpsToast provideDialog(Context context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        dismissDialog("mTurnOffDialog");
    }

    public SideFpsEventHandler(Context context, Handler handler, PowerManager powerManager) {
        this(context, handler, powerManager, new DialogProvider() { // from class: com.android.server.policy.SideFpsEventHandler$$ExternalSyntheticLambda0
            @Override // com.android.server.policy.SideFpsEventHandler.DialogProvider
            public final SideFpsToast provideDialog(Context context2) {
                SideFpsToast lambda$new$1;
                lambda$new$1 = SideFpsEventHandler.lambda$new$1(context2);
                return lambda$new$1;
            }
        });
    }

    public static /* synthetic */ SideFpsToast lambda$new$1(Context context) {
        SideFpsToast sideFpsToast = new SideFpsToast(context);
        sideFpsToast.getWindow().setType(2017);
        sideFpsToast.requestWindowFeature(1);
        return sideFpsToast;
    }

    @VisibleForTesting
    public SideFpsEventHandler(Context context, Handler handler, PowerManager powerManager, DialogProvider dialogProvider) {
        this.mTurnOffDialog = new Runnable() { // from class: com.android.server.policy.SideFpsEventHandler$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SideFpsEventHandler.this.lambda$new$0();
            }
        };
        this.mContext = context;
        this.mHandler = handler;
        this.mPowerManager = powerManager;
        this.mBiometricState = 0;
        this.mSideFpsEventHandlerReady = new AtomicBoolean(false);
        this.mDialogProvider = dialogProvider;
        context.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.policy.SideFpsEventHandler.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (SideFpsEventHandler.this.mDialog != null) {
                    SideFpsEventHandler.this.mDialog.dismiss();
                    SideFpsEventHandler.this.mDialog = null;
                }
            }
        }, new IntentFilter("android.intent.action.SCREEN_OFF"));
        this.mDismissDialogTimeout = context.getResources().getInteger(17694960);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        goToSleep(this.mLastPowerPressTime);
    }

    public void notifyPowerPressed() {
        Log.i("SideFpsEventHandler", "notifyPowerPressed");
        if (this.mFingerprintManager == null && this.mSideFpsEventHandlerReady.get()) {
            this.mFingerprintManager = (FingerprintManager) this.mContext.getSystemService(FingerprintManager.class);
        }
        FingerprintManager fingerprintManager = this.mFingerprintManager;
        if (fingerprintManager == null) {
            return;
        }
        fingerprintManager.onPowerPressed();
    }

    public boolean shouldConsumeSinglePress(final long j) {
        if (this.mSideFpsEventHandlerReady.get()) {
            int i = this.mBiometricState;
            if (i != 1) {
                return i == 3;
            }
            this.mHandler.post(new Runnable() { // from class: com.android.server.policy.SideFpsEventHandler$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    SideFpsEventHandler.this.lambda$shouldConsumeSinglePress$2(j);
                }
            });
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$shouldConsumeSinglePress$2(long j) {
        if (this.mHandler.hasCallbacks(this.mTurnOffDialog)) {
            Log.v("SideFpsEventHandler", "Detected a tap to turn off dialog, ignoring");
            this.mHandler.removeCallbacks(this.mTurnOffDialog);
        }
        showDialog(j, "Enroll Power Press");
        this.mHandler.postDelayed(this.mTurnOffDialog, this.mDismissDialogTimeout);
    }

    public final void goToSleep(long j) {
        this.mPowerManager.goToSleep(j, 4, 0);
    }

    public void onFingerprintSensorReady() {
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.fingerprint")) {
            final FingerprintManager fingerprintManager = (FingerprintManager) this.mContext.getSystemService(FingerprintManager.class);
            fingerprintManager.addAuthenticatorsRegisteredCallback(new IFingerprintAuthenticatorsRegisteredCallback.Stub() { // from class: com.android.server.policy.SideFpsEventHandler.2
                public void onAllAuthenticatorsRegistered(List<FingerprintSensorPropertiesInternal> list) {
                    if (fingerprintManager.isPowerbuttonFps()) {
                        fingerprintManager.registerBiometricStateListener(new C14541());
                        SideFpsEventHandler.this.mSideFpsEventHandlerReady.set(true);
                    }
                }

                /* renamed from: com.android.server.policy.SideFpsEventHandler$2$1 */
                /* loaded from: classes2.dex */
                public class C14541 extends BiometricStateListener {
                    public Runnable mStateRunnable = null;

                    public C14541() {
                    }

                    public void onStateChanged(final int i) {
                        Log.d("SideFpsEventHandler", "onStateChanged : " + i);
                        if (this.mStateRunnable != null) {
                            SideFpsEventHandler.this.mHandler.removeCallbacks(this.mStateRunnable);
                            this.mStateRunnable = null;
                        }
                        if (i == 0) {
                            this.mStateRunnable = new Runnable() { // from class: com.android.server.policy.SideFpsEventHandler$2$1$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    SideFpsEventHandler.C14532.C14541.this.lambda$onStateChanged$0(i);
                                }
                            };
                            SideFpsEventHandler.this.mHandler.postDelayed(this.mStateRunnable, 500L);
                            SideFpsEventHandler.this.dismissDialog("STATE_IDLE");
                            return;
                        }
                        SideFpsEventHandler.this.mBiometricState = i;
                    }

                    /* JADX INFO: Access modifiers changed from: private */
                    public /* synthetic */ void lambda$onStateChanged$0(int i) {
                        SideFpsEventHandler.this.mBiometricState = i;
                    }

                    public void onBiometricAction(int i) {
                        Log.d("SideFpsEventHandler", "onBiometricAction " + i);
                    }
                }
            });
        }
    }

    public final void dismissDialog(String str) {
        Log.d("SideFpsEventHandler", "Dismissing dialog with reason: " + str);
        SideFpsToast sideFpsToast = this.mDialog;
        if (sideFpsToast == null || !sideFpsToast.isShowing()) {
            return;
        }
        this.mDialog.dismiss();
    }

    public final void showDialog(long j, String str) {
        Log.d("SideFpsEventHandler", "Showing dialog with reason: " + str);
        SideFpsToast sideFpsToast = this.mDialog;
        if (sideFpsToast != null && sideFpsToast.isShowing()) {
            Log.d("SideFpsEventHandler", "Ignoring show dialog");
            return;
        }
        SideFpsToast provideDialog = this.mDialogProvider.provideDialog(this.mContext);
        this.mDialog = provideDialog;
        this.mLastPowerPressTime = j;
        provideDialog.show();
        this.mDialog.setOnClickListener(this);
    }
}
