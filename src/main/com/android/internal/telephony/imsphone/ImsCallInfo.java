package com.android.internal.telephony.imsphone;

import android.telephony.ServiceState;
import com.android.internal.telephony.Call;
/* loaded from: classes.dex */
public class ImsCallInfo {
    private final int mIndex;
    private ImsPhoneConnection mConnection = null;
    private Call.State mState = Call.State.IDLE;
    private boolean mIsHeldByRemote = false;

    public ImsCallInfo(int i) {
        this.mIndex = i;
    }

    public void reset() {
        this.mConnection = null;
        this.mState = Call.State.IDLE;
        this.mIsHeldByRemote = false;
    }

    public void update(ImsPhoneConnection imsPhoneConnection) {
        this.mConnection = imsPhoneConnection;
        this.mState = imsPhoneConnection.getState();
    }

    public boolean update(ImsPhoneConnection imsPhoneConnection, boolean z, boolean z2) {
        Call.State state = imsPhoneConnection.getState();
        boolean z3 = this.mState != state;
        this.mState = state;
        if (z && !this.mIsHeldByRemote) {
            this.mIsHeldByRemote = true;
            return true;
        } else if (z2 && this.mIsHeldByRemote) {
            this.mIsHeldByRemote = false;
            return true;
        } else {
            return z3;
        }
    }

    public void onDisconnect() {
        this.mState = Call.State.DISCONNECTED;
    }

    public int getIndex() {
        return this.mIndex;
    }

    public Call.State getCallState() {
        return this.mState;
    }

    public boolean isHeldByRemote() {
        return this.mIsHeldByRemote;
    }

    public boolean isIncoming() {
        return this.mConnection.isIncoming();
    }

    public boolean isEmergencyCall() {
        return this.mConnection.isEmergencyCall();
    }

    public int getCallRadioTech() {
        return ServiceState.rilRadioTechnologyToAccessNetworkType(this.mConnection.getCallRadioTech());
    }

    public String toString() {
        return "[ id=" + this.mIndex + ", state=" + this.mState + ", isMT=" + isIncoming() + ", heldByRemote=" + this.mIsHeldByRemote + " ]";
    }
}
