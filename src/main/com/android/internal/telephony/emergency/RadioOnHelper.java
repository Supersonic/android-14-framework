package com.android.internal.telephony.emergency;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.CallWaitingController;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.emergency.RadioOnStateListener;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class RadioOnHelper implements RadioOnStateListener.Callback {
    private RadioOnStateListener.Callback mCallback;
    private final Context mContext;
    private List<RadioOnStateListener> mInProgressListeners = new ArrayList(2);
    private boolean mIsRadioOnCallingEnabled;
    private List<RadioOnStateListener> mListeners;

    public RadioOnHelper(Context context) {
        this.mContext = context;
    }

    private void setupListeners() {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList(2);
        }
        int activeModemCount = TelephonyManager.from(this.mContext).getActiveModemCount();
        while (this.mListeners.size() < activeModemCount) {
            this.mListeners.add(new RadioOnStateListener());
        }
        while (this.mListeners.size() > activeModemCount) {
            List<RadioOnStateListener> list = this.mListeners;
            list.get(list.size() - 1).cleanup();
            List<RadioOnStateListener> list2 = this.mListeners;
            list2.remove(list2.size() - 1);
        }
    }

    public void triggerRadioOnAndListen(RadioOnStateListener.Callback callback, boolean z, Phone phone, boolean z2) {
        setupListeners();
        this.mCallback = callback;
        this.mInProgressListeners.clear();
        this.mIsRadioOnCallingEnabled = false;
        for (int i = 0; i < TelephonyManager.from(this.mContext).getActiveModemCount(); i++) {
            Phone phone2 = PhoneFactory.getPhone(i);
            if (phone2 != null) {
                this.mInProgressListeners.add(this.mListeners.get(i));
                this.mListeners.get(i).waitForRadioOn(phone2, this, z, z && phone2 == phone);
            }
        }
        powerOnRadio(z, phone, z2);
    }

    private void powerOnRadio(boolean z, Phone phone, boolean z2) {
        Phone[] phones = PhoneFactory.getPhones();
        int length = phones.length;
        for (int i = 0; i < length; i++) {
            Phone phone2 = phones[i];
            Rlog.d("RadioOnStateListener", "powerOnRadio, enabling Radio");
            if (z2) {
                phone2.setRadioPowerOnForTestEmergencyCall(phone2 == phone);
            } else {
                phone2.setRadioPower(true, z, phone2 == phone, false);
            }
        }
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) > 0) {
            Rlog.d("RadioOnStateListener", "==> Turning off airplane mode for emergency call.");
            Settings.Global.putInt(this.mContext.getContentResolver(), "airplane_mode_on", 0);
            Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
            intent.putExtra(CallWaitingController.KEY_STATE, false);
            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        }
    }

    @Override // com.android.internal.telephony.emergency.RadioOnStateListener.Callback
    public void onComplete(RadioOnStateListener radioOnStateListener, boolean z) {
        this.mIsRadioOnCallingEnabled = z | this.mIsRadioOnCallingEnabled;
        this.mInProgressListeners.remove(radioOnStateListener);
        if (this.mCallback == null || !this.mInProgressListeners.isEmpty()) {
            return;
        }
        this.mCallback.onComplete(null, this.mIsRadioOnCallingEnabled);
    }

    @Override // com.android.internal.telephony.emergency.RadioOnStateListener.Callback
    public boolean isOkToCall(Phone phone, int i) {
        RadioOnStateListener.Callback callback = this.mCallback;
        if (callback == null) {
            return false;
        }
        return callback.isOkToCall(phone, i);
    }
}
