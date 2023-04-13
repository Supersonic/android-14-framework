package com.android.ims;

import android.os.RemoteException;
import android.telephony.ims.ImsExternalCallState;
import com.android.ims.internal.IImsExternalCallStateListener;
import com.android.ims.internal.IImsMultiEndpoint;
import com.android.telephony.Rlog;
import java.util.List;
/* loaded from: classes.dex */
public class ImsMultiEndpoint {
    private static final boolean DBG = true;
    private static final String TAG = "ImsMultiEndpoint";
    private final IImsMultiEndpoint mImsMultiendpoint;

    /* loaded from: classes.dex */
    private class ImsExternalCallStateListenerProxy extends IImsExternalCallStateListener.Stub {
        private ImsExternalCallStateListener mListener;

        public ImsExternalCallStateListenerProxy(ImsExternalCallStateListener listener) {
            this.mListener = listener;
        }

        public void onImsExternalCallStateUpdate(List<ImsExternalCallState> externalCallState) {
            Rlog.d(ImsMultiEndpoint.TAG, "onImsExternalCallStateUpdate");
            ImsExternalCallStateListener imsExternalCallStateListener = this.mListener;
            if (imsExternalCallStateListener != null) {
                imsExternalCallStateListener.onImsExternalCallStateUpdate(externalCallState);
            }
        }
    }

    public ImsMultiEndpoint(IImsMultiEndpoint iImsMultiEndpoint) {
        Rlog.d(TAG, "ImsMultiEndpoint created");
        this.mImsMultiendpoint = iImsMultiEndpoint;
    }

    public void setExternalCallStateListener(ImsExternalCallStateListener externalCallStateListener) throws RemoteException {
        Rlog.d(TAG, "setExternalCallStateListener");
        this.mImsMultiendpoint.setListener(externalCallStateListener != null ? new ImsExternalCallStateListenerProxy(externalCallStateListener) : null);
    }

    public boolean isBinderAlive() {
        return this.mImsMultiendpoint.asBinder().isBinderAlive();
    }
}
