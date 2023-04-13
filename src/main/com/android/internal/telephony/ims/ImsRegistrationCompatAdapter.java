package com.android.internal.telephony.ims;

import android.net.Uri;
import android.os.RemoteException;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.stub.ImsRegistrationImplBase;
import android.util.ArrayMap;
import com.android.ims.internal.IImsRegistrationListener;
import java.util.Map;
/* loaded from: classes.dex */
public class ImsRegistrationCompatAdapter extends ImsRegistrationImplBase {
    private static final Map<Integer, Integer> RADIO_TECH_MAPPER;
    private final IImsRegistrationListener mListener = new IImsRegistrationListener.Stub() { // from class: com.android.internal.telephony.ims.ImsRegistrationCompatAdapter.1
        public void registrationFeatureCapabilityChanged(int i, int[] iArr, int[] iArr2) throws RemoteException {
        }

        public void registrationResumed() throws RemoteException {
        }

        public void registrationServiceCapabilityChanged(int i, int i2) throws RemoteException {
        }

        public void registrationSuspended() throws RemoteException {
        }

        public void voiceMessageCountUpdate(int i) throws RemoteException {
        }

        public void registrationConnected() throws RemoteException {
            ImsRegistrationCompatAdapter.this.onRegistered(-1);
        }

        public void registrationProgressing() throws RemoteException {
            ImsRegistrationCompatAdapter.this.onRegistering(-1);
        }

        public void registrationConnectedWithRadioTech(int i) throws RemoteException {
            ImsRegistrationCompatAdapter.this.onRegistered(((Integer) ImsRegistrationCompatAdapter.RADIO_TECH_MAPPER.getOrDefault(Integer.valueOf(i), -1)).intValue());
        }

        public void registrationProgressingWithRadioTech(int i) throws RemoteException {
            ImsRegistrationCompatAdapter.this.onRegistering(((Integer) ImsRegistrationCompatAdapter.RADIO_TECH_MAPPER.getOrDefault(Integer.valueOf(i), -1)).intValue());
        }

        public void registrationDisconnected(ImsReasonInfo imsReasonInfo) throws RemoteException {
            ImsRegistrationCompatAdapter.this.onDeregistered(imsReasonInfo);
        }

        public void registrationAssociatedUriChanged(Uri[] uriArr) throws RemoteException {
            ImsRegistrationCompatAdapter.this.onSubscriberAssociatedUriChanged(uriArr);
        }

        public void registrationChangeFailed(int i, ImsReasonInfo imsReasonInfo) throws RemoteException {
            ImsRegistrationCompatAdapter.this.onTechnologyChangeFailed(((Integer) ImsRegistrationCompatAdapter.RADIO_TECH_MAPPER.getOrDefault(Integer.valueOf(i), -1)).intValue(), imsReasonInfo);
        }
    };

    static {
        ArrayMap arrayMap = new ArrayMap(2);
        RADIO_TECH_MAPPER = arrayMap;
        arrayMap.put(20, 3);
        arrayMap.put(14, 0);
        arrayMap.put(18, 1);
    }

    public IImsRegistrationListener getRegistrationListener() {
        return this.mListener;
    }
}
