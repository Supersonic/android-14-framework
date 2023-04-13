package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import android.util.Log;
import com.android.ims.internal.IImsUtListener;
@SystemApi
/* loaded from: classes3.dex */
public class ImsUtListener {
    @Deprecated
    public static final String BUNDLE_KEY_CLIR = "queryClir";
    @Deprecated
    public static final String BUNDLE_KEY_SSINFO = "imsSsInfo";
    private static final String LOG_TAG = "ImsUtListener";
    private IImsUtListener mServiceInterface;

    public void onUtConfigurationUpdated(int id) {
        try {
            this.mServiceInterface.utConfigurationUpdated(null, id);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "utConfigurationUpdated: remote exception");
        }
    }

    public void onUtConfigurationUpdateFailed(int id, ImsReasonInfo error) {
        try {
            this.mServiceInterface.utConfigurationUpdateFailed(null, id, error);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "utConfigurationUpdateFailed: remote exception");
        }
    }

    @Deprecated
    public void onUtConfigurationQueried(int id, Bundle configuration) {
        try {
            this.mServiceInterface.utConfigurationQueried(null, id, configuration);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "utConfigurationQueried: remote exception");
        }
    }

    public void onLineIdentificationSupplementaryServiceResponse(int id, ImsSsInfo configuration) {
        try {
            this.mServiceInterface.lineIdentificationSupplementaryServiceResponse(id, configuration);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void onUtConfigurationQueryFailed(int id, ImsReasonInfo error) {
        try {
            this.mServiceInterface.utConfigurationQueryFailed(null, id, error);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "utConfigurationQueryFailed: remote exception");
        }
    }

    public void onUtConfigurationCallBarringQueried(int id, ImsSsInfo[] cbInfo) {
        try {
            this.mServiceInterface.utConfigurationCallBarringQueried(null, id, cbInfo);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "utConfigurationCallBarringQueried: remote exception");
        }
    }

    public void onUtConfigurationCallForwardQueried(int id, ImsCallForwardInfo[] cfInfo) {
        try {
            this.mServiceInterface.utConfigurationCallForwardQueried(null, id, cfInfo);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "utConfigurationCallForwardQueried: remote exception");
        }
    }

    public void onUtConfigurationCallWaitingQueried(int id, ImsSsInfo[] cwInfo) {
        try {
            this.mServiceInterface.utConfigurationCallWaitingQueried(null, id, cwInfo);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "utConfigurationCallWaitingQueried: remote exception");
        }
    }

    public void onSupplementaryServiceIndication(ImsSsData ssData) {
        try {
            this.mServiceInterface.onSupplementaryServiceIndication(ssData);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "onSupplementaryServiceIndication: remote exception");
        }
    }

    public ImsUtListener(IImsUtListener serviceInterface) {
        this.mServiceInterface = serviceInterface;
    }

    public IImsUtListener getListenerInterface() {
        return this.mServiceInterface;
    }
}
