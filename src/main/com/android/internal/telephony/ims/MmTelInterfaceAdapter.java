package com.android.internal.telephony.ims;

import android.app.PendingIntent;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.ims.ImsCallProfile;
import com.android.ims.internal.IImsCallSession;
import com.android.ims.internal.IImsConfig;
import com.android.ims.internal.IImsEcbm;
import com.android.ims.internal.IImsMMTelFeature;
import com.android.ims.internal.IImsMultiEndpoint;
import com.android.ims.internal.IImsRegistrationListener;
import com.android.ims.internal.IImsUt;
/* loaded from: classes.dex */
public class MmTelInterfaceAdapter {
    protected IBinder mBinder;
    protected int mSlotId;

    public MmTelInterfaceAdapter(int i, IBinder iBinder) {
        this.mBinder = iBinder;
        this.mSlotId = i;
    }

    public int startSession(PendingIntent pendingIntent, IImsRegistrationListener iImsRegistrationListener) throws RemoteException {
        return getInterface().startSession(pendingIntent, iImsRegistrationListener);
    }

    public void endSession(int i) throws RemoteException {
        getInterface().endSession(i);
    }

    public boolean isConnected(int i, int i2) throws RemoteException {
        return getInterface().isConnected(i, i2);
    }

    public boolean isOpened() throws RemoteException {
        return getInterface().isOpened();
    }

    public int getFeatureState() throws RemoteException {
        return getInterface().getFeatureStatus();
    }

    public void addRegistrationListener(IImsRegistrationListener iImsRegistrationListener) throws RemoteException {
        getInterface().addRegistrationListener(iImsRegistrationListener);
    }

    public void removeRegistrationListener(IImsRegistrationListener iImsRegistrationListener) throws RemoteException {
        getInterface().removeRegistrationListener(iImsRegistrationListener);
    }

    public ImsCallProfile createCallProfile(int i, int i2, int i3) throws RemoteException {
        return getInterface().createCallProfile(i, i2, i3);
    }

    public IImsCallSession createCallSession(int i, ImsCallProfile imsCallProfile) throws RemoteException {
        return getInterface().createCallSession(i, imsCallProfile);
    }

    public IImsCallSession getPendingCallSession(int i, String str) throws RemoteException {
        return getInterface().getPendingCallSession(i, str);
    }

    public IImsUt getUtInterface() throws RemoteException {
        return getInterface().getUtInterface();
    }

    public IImsConfig getConfigInterface() throws RemoteException {
        return getInterface().getConfigInterface();
    }

    public void turnOnIms() throws RemoteException {
        getInterface().turnOnIms();
    }

    public void turnOffIms() throws RemoteException {
        getInterface().turnOffIms();
    }

    public IImsEcbm getEcbmInterface() throws RemoteException {
        return getInterface().getEcbmInterface();
    }

    public void setUiTTYMode(int i, Message message) throws RemoteException {
        getInterface().setUiTTYMode(i, message);
    }

    public IImsMultiEndpoint getMultiEndpointInterface() throws RemoteException {
        return getInterface().getMultiEndpointInterface();
    }

    private IImsMMTelFeature getInterface() throws RemoteException {
        IImsMMTelFeature asInterface = IImsMMTelFeature.Stub.asInterface(this.mBinder);
        if (asInterface != null) {
            return asInterface;
        }
        throw new RemoteException("Binder not Available");
    }
}
