package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Call;
import java.util.Iterator;
/* loaded from: classes.dex */
public class GsmCdmaCall extends Call {
    GsmCdmaCallTracker mOwner;

    public GsmCdmaCall(GsmCdmaCallTracker gsmCdmaCallTracker) {
        this.mOwner = gsmCdmaCallTracker;
    }

    @Override // com.android.internal.telephony.Call
    public Phone getPhone() {
        return this.mOwner.getPhone();
    }

    @Override // com.android.internal.telephony.Call
    public boolean isMultiparty() {
        return getConnectionsCount() > 1;
    }

    @Override // com.android.internal.telephony.Call
    public void hangup() throws CallStateException {
        this.mOwner.hangup(this);
    }

    @Override // com.android.internal.telephony.Call
    public void hangup(int i) throws CallStateException {
        this.mOwner.hangup(this);
    }

    public String toString() {
        return this.mState.toString();
    }

    public void attach(Connection connection, DriverCall driverCall) {
        addConnection(connection);
        this.mState = Call.stateFromDCState(driverCall.state);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void attachFake(Connection connection, Call.State state) {
        addConnection(connection);
        this.mState = state;
    }

    public boolean connectionDisconnected(GsmCdmaConnection gsmCdmaConnection) {
        boolean z;
        if (this.mState != Call.State.DISCONNECTED) {
            Iterator<Connection> it = getConnections().iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = true;
                    break;
                } else if (it.next().getState() != Call.State.DISCONNECTED) {
                    z = false;
                    break;
                }
            }
            if (z) {
                this.mState = Call.State.DISCONNECTED;
                return true;
            }
        }
        return false;
    }

    public void detach(GsmCdmaConnection gsmCdmaConnection) {
        removeConnection(gsmCdmaConnection);
        if (getConnectionsCount() == 0) {
            this.mState = Call.State.IDLE;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean update(GsmCdmaConnection gsmCdmaConnection, DriverCall driverCall) {
        Call.State stateFromDCState = Call.stateFromDCState(driverCall.state);
        if (stateFromDCState != this.mState) {
            this.mState = stateFromDCState;
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFull() {
        return getConnectionsCount() == this.mOwner.getMaxConnectionsPerCall();
    }

    @VisibleForTesting
    public void onHangupLocal() {
        if (this.mState.isAlive()) {
            Iterator<Connection> it = getConnections().iterator();
            while (it.hasNext()) {
                ((GsmCdmaConnection) it.next()).onHangupLocal();
            }
            this.mState = Call.State.DISCONNECTING;
        }
    }
}
