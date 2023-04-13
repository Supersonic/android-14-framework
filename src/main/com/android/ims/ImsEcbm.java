package com.android.ims;

import android.os.RemoteException;
import com.android.ims.internal.IImsEcbm;
import com.android.ims.internal.IImsEcbmListener;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class ImsEcbm {
    private static final boolean DBG = true;
    private static final String TAG = "ImsEcbm";
    private final IImsEcbm miEcbm;

    public ImsEcbm(IImsEcbm iEcbm) {
        Rlog.d(TAG, "ImsEcbm created");
        this.miEcbm = iEcbm;
    }

    public void setEcbmStateListener(ImsEcbmStateListener ecbmListener) throws RemoteException {
        this.miEcbm.setListener(ecbmListener != null ? new ImsEcbmListenerProxy(ecbmListener) : null);
    }

    public void exitEmergencyCallbackMode() throws ImsException {
        try {
            this.miEcbm.exitEmergencyCallbackMode();
        } catch (RemoteException e) {
            throw new ImsException("exitEmergencyCallbackMode()", e, 106);
        }
    }

    public boolean isBinderAlive() {
        return this.miEcbm.asBinder().isBinderAlive();
    }

    /* loaded from: classes.dex */
    private static class ImsEcbmListenerProxy extends IImsEcbmListener.Stub {
        private final ImsEcbmStateListener mListener;

        public ImsEcbmListenerProxy(ImsEcbmStateListener listener) {
            this.mListener = listener;
        }

        public void enteredECBM() {
            Rlog.d(ImsEcbm.TAG, "enteredECBM ::");
            ImsEcbmStateListener imsEcbmStateListener = this.mListener;
            if (imsEcbmStateListener != null) {
                imsEcbmStateListener.onECBMEntered();
            }
        }

        public void exitedECBM() {
            Rlog.d(ImsEcbm.TAG, "exitedECBM ::");
            ImsEcbmStateListener imsEcbmStateListener = this.mListener;
            if (imsEcbmStateListener != null) {
                imsEcbmStateListener.onECBMExited();
            }
        }
    }
}
