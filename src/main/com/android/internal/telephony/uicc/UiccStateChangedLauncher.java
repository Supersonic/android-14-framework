package com.android.internal.telephony.uicc;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.uicc.IccCardStatus;
/* loaded from: classes.dex */
public class UiccStateChangedLauncher extends Handler {
    private static final String TAG = UiccStateChangedLauncher.class.getName();
    private static String sDeviceProvisioningPackage = null;
    private Context mContext;
    private boolean[] mIsRestricted = null;
    private UiccController mUiccController;

    public UiccStateChangedLauncher(Context context, UiccController uiccController) {
        String string = context.getResources().getString(17039914);
        sDeviceProvisioningPackage = string;
        if (string == null || string.isEmpty()) {
            return;
        }
        this.mContext = context;
        this.mUiccController = uiccController;
        uiccController.registerForIccChanged(this, 1, null);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        boolean z;
        if (message.what == 1) {
            if (this.mIsRestricted == null) {
                this.mIsRestricted = new boolean[TelephonyManager.getDefault().getPhoneCount()];
                z = true;
            } else {
                z = false;
            }
            for (int i = 0; i < this.mIsRestricted.length; i++) {
                UiccCard uiccCardForPhone = this.mUiccController.getUiccCardForPhone(i);
                boolean z2 = uiccCardForPhone == null || uiccCardForPhone.getCardState() != IccCardStatus.CardState.CARDSTATE_RESTRICTED;
                boolean[] zArr = this.mIsRestricted;
                boolean z3 = zArr[i];
                if (z2 != z3) {
                    zArr[i] = !z3;
                    z = true;
                }
            }
            if (z) {
                notifyStateChanged();
                return;
            }
            return;
        }
        throw new RuntimeException("unexpected event not handled");
    }

    private void notifyStateChanged() {
        Intent intent = new Intent("android.intent.action.SIM_STATE_CHANGED");
        intent.setPackage(sDeviceProvisioningPackage);
        try {
            this.mContext.sendBroadcast(intent);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}
