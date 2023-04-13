package com.android.internal.telephony.emergency;

import android.os.AsyncResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.ServiceState;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.SomeArgs;
import com.android.telephony.Rlog;
import java.util.Locale;
/* loaded from: classes.dex */
public class RadioOnStateListener {
    private static int MAX_NUM_RETRIES = 5;
    public static final int MSG_RADIO_OFF_OR_NOT_AVAILABLE = 5;
    @VisibleForTesting
    public static final int MSG_RADIO_ON = 4;
    @VisibleForTesting
    public static final int MSG_SERVICE_STATE_CHANGED = 2;
    private static long TIME_BETWEEN_RETRIES_MILLIS = 5000;
    private Callback mCallback;
    private boolean mForEmergencyCall;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.internal.telephony.emergency.RadioOnStateListener.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                SomeArgs someArgs = (SomeArgs) message.obj;
                try {
                    boolean booleanValue = ((Boolean) someArgs.arg3).booleanValue();
                    boolean booleanValue2 = ((Boolean) someArgs.arg4).booleanValue();
                    RadioOnStateListener.this.startSequenceInternal((Phone) someArgs.arg1, (Callback) someArgs.arg2, booleanValue, booleanValue2);
                } finally {
                    someArgs.recycle();
                }
            } else if (i == 2) {
                RadioOnStateListener.this.onServiceStateChanged((ServiceState) ((AsyncResult) message.obj).result);
            } else if (i == 3) {
                RadioOnStateListener.this.onRetryTimeout();
            } else if (i == 4) {
                RadioOnStateListener.this.onRadioOn();
            } else if (i == 5) {
                RadioOnStateListener.this.registerForRadioOn();
            } else {
                Rlog.w("RadioOnStateListener", String.format(Locale.getDefault(), "handleMessage: unexpected message: %d.", Integer.valueOf(message.what)));
            }
        }
    };
    private int mNumRetriesSoFar;
    private Phone mPhone;
    private boolean mSelectedPhoneForEmergencyCall;

    /* loaded from: classes.dex */
    public interface Callback {
        boolean isOkToCall(Phone phone, int i);

        void onComplete(RadioOnStateListener radioOnStateListener, boolean z);
    }

    public void waitForRadioOn(Phone phone, Callback callback, boolean z, boolean z2) {
        Rlog.d("RadioOnStateListener", "waitForRadioOn: Phone " + phone.getPhoneId());
        if (this.mPhone != null) {
            return;
        }
        SomeArgs obtain = SomeArgs.obtain();
        obtain.arg1 = phone;
        obtain.arg2 = callback;
        obtain.arg3 = Boolean.valueOf(z);
        obtain.arg4 = Boolean.valueOf(z2);
        this.mHandler.obtainMessage(1, obtain).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSequenceInternal(Phone phone, Callback callback, boolean z, boolean z2) {
        Rlog.d("RadioOnStateListener", "startSequenceInternal: Phone " + phone.getPhoneId());
        cleanup();
        this.mPhone = phone;
        this.mCallback = callback;
        this.mForEmergencyCall = z;
        this.mSelectedPhoneForEmergencyCall = z2;
        registerForServiceStateChanged();
        registerForRadioOff();
        startRetryTimer();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onServiceStateChanged(ServiceState serviceState) {
        Phone phone = this.mPhone;
        if (phone == null) {
            return;
        }
        Rlog.d("RadioOnStateListener", String.format("onServiceStateChanged(), new state = %s, Phone = %s", serviceState, Integer.valueOf(phone.getPhoneId())));
        if (isOkToCall(serviceState.getState())) {
            Rlog.d("RadioOnStateListener", "onServiceStateChanged: ok to call!");
            onComplete(true);
            cleanup();
            return;
        }
        Rlog.d("RadioOnStateListener", "onServiceStateChanged: not ready to call yet, keep waiting.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRadioOn() {
        Phone phone = this.mPhone;
        if (phone == null) {
            return;
        }
        ServiceState serviceState = phone.getServiceState();
        Rlog.d("RadioOnStateListener", String.format("onRadioOn, state = %s, Phone = %s", serviceState, Integer.valueOf(this.mPhone.getPhoneId())));
        if (isOkToCall(serviceState.getState())) {
            onComplete(true);
            cleanup();
            return;
        }
        Rlog.d("RadioOnStateListener", "onRadioOn: not ready to call yet, keep waiting.");
    }

    private boolean isOkToCall(int i) {
        Callback callback = this.mCallback;
        if (callback == null) {
            return false;
        }
        return callback.isOkToCall(this.mPhone, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRetryTimeout() {
        Phone phone = this.mPhone;
        if (phone == null) {
            return;
        }
        int state = phone.getServiceState().getState();
        Rlog.d("RadioOnStateListener", String.format(Locale.getDefault(), "onRetryTimeout():  phone state = %s, service state = %d, retries = %d.", this.mPhone.getState(), Integer.valueOf(state), Integer.valueOf(this.mNumRetriesSoFar)));
        if (isOkToCall(state)) {
            Rlog.d("RadioOnStateListener", "onRetryTimeout: Radio is on. Cleaning up.");
            onComplete(true);
            cleanup();
            return;
        }
        this.mNumRetriesSoFar++;
        Rlog.d("RadioOnStateListener", "mNumRetriesSoFar is now " + this.mNumRetriesSoFar);
        if (this.mNumRetriesSoFar > MAX_NUM_RETRIES) {
            Rlog.w("RadioOnStateListener", "Hit MAX_NUM_RETRIES; giving up.");
            cleanup();
            return;
        }
        Rlog.d("RadioOnStateListener", "Trying (again) to turn on the radio.");
        this.mPhone.setRadioPower(true, this.mForEmergencyCall, this.mSelectedPhoneForEmergencyCall, false);
        startRetryTimer();
    }

    public void cleanup() {
        Rlog.d("RadioOnStateListener", "cleanup()");
        onComplete(false);
        unregisterForServiceStateChanged();
        unregisterForRadioOff();
        unregisterForRadioOn();
        cancelRetryTimer();
        this.mPhone = null;
        this.mNumRetriesSoFar = 0;
    }

    private void startRetryTimer() {
        cancelRetryTimer();
        this.mHandler.sendEmptyMessageDelayed(3, TIME_BETWEEN_RETRIES_MILLIS);
    }

    private void cancelRetryTimer() {
        this.mHandler.removeMessages(3);
    }

    private void registerForServiceStateChanged() {
        unregisterForServiceStateChanged();
        this.mPhone.registerForServiceStateChanged(this.mHandler, 2, null);
    }

    private void unregisterForServiceStateChanged() {
        Phone phone = this.mPhone;
        if (phone != null) {
            phone.unregisterForServiceStateChanged(this.mHandler);
        }
        this.mHandler.removeMessages(2);
    }

    private void registerForRadioOff() {
        this.mPhone.mCi.registerForOffOrNotAvailable(this.mHandler, 5, null);
    }

    private void unregisterForRadioOff() {
        Phone phone = this.mPhone;
        if (phone != null) {
            phone.mCi.unregisterForOffOrNotAvailable(this.mHandler);
        }
        this.mHandler.removeMessages(5);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerForRadioOn() {
        unregisterForRadioOff();
        this.mPhone.mCi.registerForOn(this.mHandler, 4, null);
    }

    private void unregisterForRadioOn() {
        Phone phone = this.mPhone;
        if (phone != null) {
            phone.mCi.unregisterForOn(this.mHandler);
        }
        this.mHandler.removeMessages(4);
    }

    private void onComplete(boolean z) {
        Callback callback = this.mCallback;
        if (callback != null) {
            this.mCallback = null;
            callback.onComplete(this, z);
        }
    }

    @VisibleForTesting
    public Handler getHandler() {
        return this.mHandler;
    }

    @VisibleForTesting
    public void setMaxNumRetries(int i) {
        MAX_NUM_RETRIES = i;
    }

    @VisibleForTesting
    public void setTimeBetweenRetriesMillis(long j) {
        TIME_BETWEEN_RETRIES_MILLIS = j;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !getClass().equals(obj.getClass())) {
            return false;
        }
        RadioOnStateListener radioOnStateListener = (RadioOnStateListener) obj;
        if (this.mNumRetriesSoFar != radioOnStateListener.mNumRetriesSoFar) {
            return false;
        }
        Callback callback = this.mCallback;
        if (callback == null ? radioOnStateListener.mCallback == null : callback.equals(radioOnStateListener.mCallback)) {
            Phone phone = this.mPhone;
            return phone != null ? phone.equals(radioOnStateListener.mPhone) : radioOnStateListener.mPhone == null;
        }
        return false;
    }

    public int hashCode() {
        int i = (217 + this.mNumRetriesSoFar) * 31;
        Callback callback = this.mCallback;
        int hashCode = (i + (callback == null ? 0 : callback.hashCode())) * 31;
        Phone phone = this.mPhone;
        return hashCode + (phone != null ? phone.hashCode() : 0);
    }
}
