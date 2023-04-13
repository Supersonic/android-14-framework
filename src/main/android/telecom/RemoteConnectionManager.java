package android.telecom;

import android.content.ComponentName;
import android.p008os.RemoteException;
import com.android.internal.telecom.IConnectionService;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes3.dex */
public class RemoteConnectionManager {
    private final ConnectionService mOurConnectionServiceImpl;
    private final Map<ComponentName, RemoteConnectionService> mRemoteConnectionServices = new HashMap();

    public RemoteConnectionManager(ConnectionService ourConnectionServiceImpl) {
        this.mOurConnectionServiceImpl = ourConnectionServiceImpl;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addConnectionService(ComponentName componentName, IConnectionService outgoingConnectionServiceRpc) {
        if (!this.mRemoteConnectionServices.containsKey(componentName)) {
            try {
                RemoteConnectionService remoteConnectionService = new RemoteConnectionService(outgoingConnectionServiceRpc, this.mOurConnectionServiceImpl);
                this.mRemoteConnectionServices.put(componentName, remoteConnectionService);
            } catch (RemoteException e) {
                Log.m131w(this, "error when addConnectionService of %s: %s", componentName, e.toString());
            }
        }
    }

    public RemoteConnection createRemoteConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request, boolean isIncoming) {
        PhoneAccountHandle accountHandle = request.getAccountHandle();
        if (accountHandle == null) {
            throw new IllegalArgumentException("accountHandle must be specified.");
        }
        ComponentName componentName = request.getAccountHandle().getComponentName();
        if (!this.mRemoteConnectionServices.containsKey(componentName)) {
            throw new UnsupportedOperationException("accountHandle not supported: " + componentName);
        }
        RemoteConnectionService remoteService = this.mRemoteConnectionServices.get(componentName);
        if (remoteService != null) {
            return remoteService.createRemoteConnection(connectionManagerPhoneAccount, request, isIncoming);
        }
        return null;
    }

    public RemoteConference createRemoteConference(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request, boolean isIncoming) {
        PhoneAccountHandle accountHandle = request.getAccountHandle();
        if (accountHandle == null) {
            throw new IllegalArgumentException("accountHandle must be specified.");
        }
        ComponentName componentName = request.getAccountHandle().getComponentName();
        if (!this.mRemoteConnectionServices.containsKey(componentName)) {
            throw new UnsupportedOperationException("accountHandle not supported: " + componentName);
        }
        RemoteConnectionService remoteService = this.mRemoteConnectionServices.get(componentName);
        if (remoteService != null) {
            return remoteService.createRemoteConference(connectionManagerPhoneAccount, request, isIncoming);
        }
        return null;
    }

    public void conferenceRemoteConnections(RemoteConnection a, RemoteConnection b) {
        if (a.getConnectionService() == b.getConnectionService()) {
            try {
                a.getConnectionService().conference(a.getId(), b.getId(), null);
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        Log.m131w(this, "Request to conference incompatible remote connections (%s,%s) (%s,%s)", a.getConnectionService(), a.getId(), b.getConnectionService(), b.getId());
    }
}
