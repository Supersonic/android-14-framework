package com.android.internal.telephony.imsphone;

import android.net.Uri;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.ImsRegistrationAttributes;
import android.telephony.ims.RegistrationManager;
import android.telephony.ims.aidl.IImsRegistrationCallback;
import android.util.Log;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class ImsRegistrationCallbackHelper {
    private final RegistrationManager.RegistrationCallback mImsRegistrationCallback;
    private ImsRegistrationUpdate mImsRegistrationUpdate;
    private int mRegistrationState = 0;
    private final Object mLock = new Object();

    /* loaded from: classes.dex */
    public interface ImsRegistrationUpdate {
        void handleImsRegistered(ImsRegistrationAttributes imsRegistrationAttributes);

        void handleImsRegistering(int i);

        void handleImsSubscriberAssociatedUriChanged(Uri[] uriArr);

        void handleImsUnregistered(ImsReasonInfo imsReasonInfo, int i, int i2);
    }

    public ImsRegistrationCallbackHelper(ImsRegistrationUpdate imsRegistrationUpdate, Executor executor) {
        RegistrationManager.RegistrationCallback registrationCallback = new RegistrationManager.RegistrationCallback() { // from class: com.android.internal.telephony.imsphone.ImsRegistrationCallbackHelper.1
            @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
            public void onRegistered(ImsRegistrationAttributes imsRegistrationAttributes) {
                ImsRegistrationCallbackHelper.this.updateRegistrationState(2);
                ImsRegistrationCallbackHelper.this.mImsRegistrationUpdate.handleImsRegistered(imsRegistrationAttributes);
            }

            @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
            public void onRegistering(int i) {
                ImsRegistrationCallbackHelper.this.updateRegistrationState(1);
                ImsRegistrationCallbackHelper.this.mImsRegistrationUpdate.handleImsRegistering(i);
            }

            @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
            public void onUnregistered(ImsReasonInfo imsReasonInfo) {
                onUnregistered(imsReasonInfo, 0, -1);
            }

            public void onUnregistered(ImsReasonInfo imsReasonInfo, int i, int i2) {
                ImsRegistrationCallbackHelper.this.updateRegistrationState(0);
                ImsRegistrationCallbackHelper.this.mImsRegistrationUpdate.handleImsUnregistered(imsReasonInfo, i, i2);
            }

            public void onSubscriberAssociatedUriChanged(Uri[] uriArr) {
                ImsRegistrationCallbackHelper.this.mImsRegistrationUpdate.handleImsSubscriberAssociatedUriChanged(uriArr);
            }
        };
        this.mImsRegistrationCallback = registrationCallback;
        registrationCallback.setExecutor(executor);
        this.mImsRegistrationUpdate = imsRegistrationUpdate;
    }

    public void reset() {
        Log.d("ImsRegCallbackHelper", "reset");
        updateRegistrationState(0);
    }

    public synchronized void updateRegistrationState(int i) {
        synchronized (this.mLock) {
            Log.d("ImsRegCallbackHelper", "updateRegistrationState: registration state from " + RegistrationManager.registrationStateToString(this.mRegistrationState) + " to " + RegistrationManager.registrationStateToString(i));
            this.mRegistrationState = i;
        }
    }

    public int getImsRegistrationState() {
        int i;
        synchronized (this.mLock) {
            i = this.mRegistrationState;
        }
        return i;
    }

    public boolean isImsRegistered() {
        return getImsRegistrationState() == 2;
    }

    public RegistrationManager.RegistrationCallback getCallback() {
        return this.mImsRegistrationCallback;
    }

    public IImsRegistrationCallback getCallbackBinder() {
        return this.mImsRegistrationCallback.getBinder();
    }
}
