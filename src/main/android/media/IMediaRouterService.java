package android.media;

import android.media.IMediaRouter2;
import android.media.IMediaRouter2Manager;
import android.media.IMediaRouterClient;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface IMediaRouterService extends IInterface {
    void deselectRouteWithManager(IMediaRouter2Manager iMediaRouter2Manager, int i, String str, MediaRoute2Info mediaRoute2Info) throws RemoteException;

    void deselectRouteWithRouter2(IMediaRouter2 iMediaRouter2, String str, MediaRoute2Info mediaRoute2Info) throws RemoteException;

    List<RoutingSessionInfo> getRemoteSessions(IMediaRouter2Manager iMediaRouter2Manager) throws RemoteException;

    MediaRouterClientState getState(IMediaRouterClient iMediaRouterClient) throws RemoteException;

    List<MediaRoute2Info> getSystemRoutes() throws RemoteException;

    RoutingSessionInfo getSystemSessionInfo() throws RemoteException;

    RoutingSessionInfo getSystemSessionInfoForPackage(IMediaRouter2Manager iMediaRouter2Manager, String str) throws RemoteException;

    boolean isPlaybackActive(IMediaRouterClient iMediaRouterClient) throws RemoteException;

    void registerClientAsUser(IMediaRouterClient iMediaRouterClient, String str, int i) throws RemoteException;

    void registerClientGroupId(IMediaRouterClient iMediaRouterClient, String str) throws RemoteException;

    void registerManager(IMediaRouter2Manager iMediaRouter2Manager, String str) throws RemoteException;

    void registerRouter2(IMediaRouter2 iMediaRouter2, String str) throws RemoteException;

    void releaseSessionWithManager(IMediaRouter2Manager iMediaRouter2Manager, int i, String str) throws RemoteException;

    void releaseSessionWithRouter2(IMediaRouter2 iMediaRouter2, String str) throws RemoteException;

    void requestCreateSessionWithManager(IMediaRouter2Manager iMediaRouter2Manager, int i, RoutingSessionInfo routingSessionInfo, MediaRoute2Info mediaRoute2Info) throws RemoteException;

    void requestCreateSessionWithRouter2(IMediaRouter2 iMediaRouter2, int i, long j, RoutingSessionInfo routingSessionInfo, MediaRoute2Info mediaRoute2Info, Bundle bundle) throws RemoteException;

    void requestSetVolume(IMediaRouterClient iMediaRouterClient, String str, int i) throws RemoteException;

    void requestUpdateVolume(IMediaRouterClient iMediaRouterClient, String str, int i) throws RemoteException;

    void selectRouteWithManager(IMediaRouter2Manager iMediaRouter2Manager, int i, String str, MediaRoute2Info mediaRoute2Info) throws RemoteException;

    void selectRouteWithRouter2(IMediaRouter2 iMediaRouter2, String str, MediaRoute2Info mediaRoute2Info) throws RemoteException;

    void setBluetoothA2dpOn(IMediaRouterClient iMediaRouterClient, boolean z) throws RemoteException;

    void setDiscoveryRequest(IMediaRouterClient iMediaRouterClient, int i, boolean z) throws RemoteException;

    void setDiscoveryRequestWithRouter2(IMediaRouter2 iMediaRouter2, RouteDiscoveryPreference routeDiscoveryPreference) throws RemoteException;

    void setRouteListingPreference(IMediaRouter2 iMediaRouter2, RouteListingPreference routeListingPreference) throws RemoteException;

    void setRouteVolumeWithManager(IMediaRouter2Manager iMediaRouter2Manager, int i, MediaRoute2Info mediaRoute2Info, int i2) throws RemoteException;

    void setRouteVolumeWithRouter2(IMediaRouter2 iMediaRouter2, MediaRoute2Info mediaRoute2Info, int i) throws RemoteException;

    void setSelectedRoute(IMediaRouterClient iMediaRouterClient, String str, boolean z) throws RemoteException;

    void setSessionVolumeWithManager(IMediaRouter2Manager iMediaRouter2Manager, int i, String str, int i2) throws RemoteException;

    void setSessionVolumeWithRouter2(IMediaRouter2 iMediaRouter2, String str, int i) throws RemoteException;

    boolean showMediaOutputSwitcher(String str) throws RemoteException;

    void startScan(IMediaRouter2Manager iMediaRouter2Manager) throws RemoteException;

    void stopScan(IMediaRouter2Manager iMediaRouter2Manager) throws RemoteException;

    void transferToRouteWithManager(IMediaRouter2Manager iMediaRouter2Manager, int i, String str, MediaRoute2Info mediaRoute2Info) throws RemoteException;

    void transferToRouteWithRouter2(IMediaRouter2 iMediaRouter2, String str, MediaRoute2Info mediaRoute2Info) throws RemoteException;

    void unregisterClient(IMediaRouterClient iMediaRouterClient) throws RemoteException;

    void unregisterManager(IMediaRouter2Manager iMediaRouter2Manager) throws RemoteException;

    void unregisterRouter2(IMediaRouter2 iMediaRouter2) throws RemoteException;

    boolean verifyPackageExists(String str) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMediaRouterService {
        @Override // android.media.IMediaRouterService
        public void registerClientAsUser(IMediaRouterClient client, String packageName, int userId) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void unregisterClient(IMediaRouterClient client) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void registerClientGroupId(IMediaRouterClient client, String groupId) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public MediaRouterClientState getState(IMediaRouterClient client) throws RemoteException {
            return null;
        }

        @Override // android.media.IMediaRouterService
        public boolean isPlaybackActive(IMediaRouterClient client) throws RemoteException {
            return false;
        }

        @Override // android.media.IMediaRouterService
        public void setBluetoothA2dpOn(IMediaRouterClient client, boolean on) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void setDiscoveryRequest(IMediaRouterClient client, int routeTypes, boolean activeScan) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void setSelectedRoute(IMediaRouterClient client, String routeId, boolean explicit) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void requestSetVolume(IMediaRouterClient client, String routeId, int volume) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void requestUpdateVolume(IMediaRouterClient client, String routeId, int direction) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public boolean verifyPackageExists(String clientPackageName) throws RemoteException {
            return false;
        }

        @Override // android.media.IMediaRouterService
        public List<MediaRoute2Info> getSystemRoutes() throws RemoteException {
            return null;
        }

        @Override // android.media.IMediaRouterService
        public RoutingSessionInfo getSystemSessionInfo() throws RemoteException {
            return null;
        }

        @Override // android.media.IMediaRouterService
        public void registerRouter2(IMediaRouter2 router, String packageName) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void unregisterRouter2(IMediaRouter2 router) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void setDiscoveryRequestWithRouter2(IMediaRouter2 router, RouteDiscoveryPreference preference) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void setRouteListingPreference(IMediaRouter2 router, RouteListingPreference routeListingPreference) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void setRouteVolumeWithRouter2(IMediaRouter2 router, MediaRoute2Info route, int volume) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void requestCreateSessionWithRouter2(IMediaRouter2 router, int requestId, long managerRequestId, RoutingSessionInfo oldSession, MediaRoute2Info route, Bundle sessionHints) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void selectRouteWithRouter2(IMediaRouter2 router, String sessionId, MediaRoute2Info route) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void deselectRouteWithRouter2(IMediaRouter2 router, String sessionId, MediaRoute2Info route) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void transferToRouteWithRouter2(IMediaRouter2 router, String sessionId, MediaRoute2Info route) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void setSessionVolumeWithRouter2(IMediaRouter2 router, String sessionId, int volume) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void releaseSessionWithRouter2(IMediaRouter2 router, String sessionId) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public List<RoutingSessionInfo> getRemoteSessions(IMediaRouter2Manager manager) throws RemoteException {
            return null;
        }

        @Override // android.media.IMediaRouterService
        public RoutingSessionInfo getSystemSessionInfoForPackage(IMediaRouter2Manager manager, String packageName) throws RemoteException {
            return null;
        }

        @Override // android.media.IMediaRouterService
        public void registerManager(IMediaRouter2Manager manager, String packageName) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void unregisterManager(IMediaRouter2Manager manager) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void setRouteVolumeWithManager(IMediaRouter2Manager manager, int requestId, MediaRoute2Info route, int volume) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void startScan(IMediaRouter2Manager manager) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void stopScan(IMediaRouter2Manager manager) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void requestCreateSessionWithManager(IMediaRouter2Manager manager, int requestId, RoutingSessionInfo oldSession, MediaRoute2Info route) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void selectRouteWithManager(IMediaRouter2Manager manager, int requestId, String sessionId, MediaRoute2Info route) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void deselectRouteWithManager(IMediaRouter2Manager manager, int requestId, String sessionId, MediaRoute2Info route) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void transferToRouteWithManager(IMediaRouter2Manager manager, int requestId, String sessionId, MediaRoute2Info route) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void setSessionVolumeWithManager(IMediaRouter2Manager manager, int requestId, String sessionId, int volume) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public void releaseSessionWithManager(IMediaRouter2Manager manager, int requestId, String sessionId) throws RemoteException {
        }

        @Override // android.media.IMediaRouterService
        public boolean showMediaOutputSwitcher(String packageName) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMediaRouterService {
        public static final String DESCRIPTOR = "android.media.IMediaRouterService";
        static final int TRANSACTION_deselectRouteWithManager = 34;
        static final int TRANSACTION_deselectRouteWithRouter2 = 21;
        static final int TRANSACTION_getRemoteSessions = 25;
        static final int TRANSACTION_getState = 4;
        static final int TRANSACTION_getSystemRoutes = 12;
        static final int TRANSACTION_getSystemSessionInfo = 13;
        static final int TRANSACTION_getSystemSessionInfoForPackage = 26;
        static final int TRANSACTION_isPlaybackActive = 5;
        static final int TRANSACTION_registerClientAsUser = 1;
        static final int TRANSACTION_registerClientGroupId = 3;
        static final int TRANSACTION_registerManager = 27;
        static final int TRANSACTION_registerRouter2 = 14;
        static final int TRANSACTION_releaseSessionWithManager = 37;
        static final int TRANSACTION_releaseSessionWithRouter2 = 24;
        static final int TRANSACTION_requestCreateSessionWithManager = 32;
        static final int TRANSACTION_requestCreateSessionWithRouter2 = 19;
        static final int TRANSACTION_requestSetVolume = 9;
        static final int TRANSACTION_requestUpdateVolume = 10;
        static final int TRANSACTION_selectRouteWithManager = 33;
        static final int TRANSACTION_selectRouteWithRouter2 = 20;
        static final int TRANSACTION_setBluetoothA2dpOn = 6;
        static final int TRANSACTION_setDiscoveryRequest = 7;
        static final int TRANSACTION_setDiscoveryRequestWithRouter2 = 16;
        static final int TRANSACTION_setRouteListingPreference = 17;
        static final int TRANSACTION_setRouteVolumeWithManager = 29;
        static final int TRANSACTION_setRouteVolumeWithRouter2 = 18;
        static final int TRANSACTION_setSelectedRoute = 8;
        static final int TRANSACTION_setSessionVolumeWithManager = 36;
        static final int TRANSACTION_setSessionVolumeWithRouter2 = 23;
        static final int TRANSACTION_showMediaOutputSwitcher = 38;
        static final int TRANSACTION_startScan = 30;
        static final int TRANSACTION_stopScan = 31;
        static final int TRANSACTION_transferToRouteWithManager = 35;
        static final int TRANSACTION_transferToRouteWithRouter2 = 22;
        static final int TRANSACTION_unregisterClient = 2;
        static final int TRANSACTION_unregisterManager = 28;
        static final int TRANSACTION_unregisterRouter2 = 15;
        static final int TRANSACTION_verifyPackageExists = 11;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaRouterService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMediaRouterService)) {
                return (IMediaRouterService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "registerClientAsUser";
                case 2:
                    return "unregisterClient";
                case 3:
                    return "registerClientGroupId";
                case 4:
                    return "getState";
                case 5:
                    return "isPlaybackActive";
                case 6:
                    return "setBluetoothA2dpOn";
                case 7:
                    return "setDiscoveryRequest";
                case 8:
                    return "setSelectedRoute";
                case 9:
                    return "requestSetVolume";
                case 10:
                    return "requestUpdateVolume";
                case 11:
                    return "verifyPackageExists";
                case 12:
                    return "getSystemRoutes";
                case 13:
                    return "getSystemSessionInfo";
                case 14:
                    return "registerRouter2";
                case 15:
                    return "unregisterRouter2";
                case 16:
                    return "setDiscoveryRequestWithRouter2";
                case 17:
                    return "setRouteListingPreference";
                case 18:
                    return "setRouteVolumeWithRouter2";
                case 19:
                    return "requestCreateSessionWithRouter2";
                case 20:
                    return "selectRouteWithRouter2";
                case 21:
                    return "deselectRouteWithRouter2";
                case 22:
                    return "transferToRouteWithRouter2";
                case 23:
                    return "setSessionVolumeWithRouter2";
                case 24:
                    return "releaseSessionWithRouter2";
                case 25:
                    return "getRemoteSessions";
                case 26:
                    return "getSystemSessionInfoForPackage";
                case 27:
                    return "registerManager";
                case 28:
                    return "unregisterManager";
                case 29:
                    return "setRouteVolumeWithManager";
                case 30:
                    return "startScan";
                case 31:
                    return "stopScan";
                case 32:
                    return "requestCreateSessionWithManager";
                case 33:
                    return "selectRouteWithManager";
                case 34:
                    return "deselectRouteWithManager";
                case 35:
                    return "transferToRouteWithManager";
                case 36:
                    return "setSessionVolumeWithManager";
                case 37:
                    return "releaseSessionWithManager";
                case 38:
                    return "showMediaOutputSwitcher";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IMediaRouterClient _arg0 = IMediaRouterClient.Stub.asInterface(data.readStrongBinder());
                            String _arg1 = data.readString();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            registerClientAsUser(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 2:
                            IMediaRouterClient _arg02 = IMediaRouterClient.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterClient(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            IMediaRouterClient _arg03 = IMediaRouterClient.Stub.asInterface(data.readStrongBinder());
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            registerClientGroupId(_arg03, _arg12);
                            reply.writeNoException();
                            break;
                        case 4:
                            IMediaRouterClient _arg04 = IMediaRouterClient.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            MediaRouterClientState _result = getState(_arg04);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 5:
                            IMediaRouterClient _arg05 = IMediaRouterClient.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result2 = isPlaybackActive(_arg05);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 6:
                            IMediaRouterClient _arg06 = IMediaRouterClient.Stub.asInterface(data.readStrongBinder());
                            boolean _arg13 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setBluetoothA2dpOn(_arg06, _arg13);
                            reply.writeNoException();
                            break;
                        case 7:
                            IMediaRouterClient _arg07 = IMediaRouterClient.Stub.asInterface(data.readStrongBinder());
                            int _arg14 = data.readInt();
                            boolean _arg22 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setDiscoveryRequest(_arg07, _arg14, _arg22);
                            reply.writeNoException();
                            break;
                        case 8:
                            IMediaRouterClient _arg08 = IMediaRouterClient.Stub.asInterface(data.readStrongBinder());
                            String _arg15 = data.readString();
                            boolean _arg23 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSelectedRoute(_arg08, _arg15, _arg23);
                            reply.writeNoException();
                            break;
                        case 9:
                            IMediaRouterClient _arg09 = IMediaRouterClient.Stub.asInterface(data.readStrongBinder());
                            String _arg16 = data.readString();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            requestSetVolume(_arg09, _arg16, _arg24);
                            reply.writeNoException();
                            break;
                        case 10:
                            IMediaRouterClient _arg010 = IMediaRouterClient.Stub.asInterface(data.readStrongBinder());
                            String _arg17 = data.readString();
                            int _arg25 = data.readInt();
                            data.enforceNoDataAvail();
                            requestUpdateVolume(_arg010, _arg17, _arg25);
                            reply.writeNoException();
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result3 = verifyPackageExists(_arg011);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 12:
                            List<MediaRoute2Info> _result4 = getSystemRoutes();
                            reply.writeNoException();
                            reply.writeTypedList(_result4, 1);
                            break;
                        case 13:
                            RoutingSessionInfo _result5 = getSystemSessionInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 14:
                            IMediaRouter2 _arg012 = IMediaRouter2.Stub.asInterface(data.readStrongBinder());
                            String _arg18 = data.readString();
                            data.enforceNoDataAvail();
                            registerRouter2(_arg012, _arg18);
                            reply.writeNoException();
                            break;
                        case 15:
                            IMediaRouter2 _arg013 = IMediaRouter2.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterRouter2(_arg013);
                            reply.writeNoException();
                            break;
                        case 16:
                            IMediaRouter2 _arg014 = IMediaRouter2.Stub.asInterface(data.readStrongBinder());
                            RouteDiscoveryPreference _arg19 = (RouteDiscoveryPreference) data.readTypedObject(RouteDiscoveryPreference.CREATOR);
                            data.enforceNoDataAvail();
                            setDiscoveryRequestWithRouter2(_arg014, _arg19);
                            reply.writeNoException();
                            break;
                        case 17:
                            IMediaRouter2 _arg015 = IMediaRouter2.Stub.asInterface(data.readStrongBinder());
                            RouteListingPreference _arg110 = (RouteListingPreference) data.readTypedObject(RouteListingPreference.CREATOR);
                            data.enforceNoDataAvail();
                            setRouteListingPreference(_arg015, _arg110);
                            reply.writeNoException();
                            break;
                        case 18:
                            IMediaRouter2 _arg016 = IMediaRouter2.Stub.asInterface(data.readStrongBinder());
                            MediaRoute2Info _arg111 = (MediaRoute2Info) data.readTypedObject(MediaRoute2Info.CREATOR);
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            setRouteVolumeWithRouter2(_arg016, _arg111, _arg26);
                            reply.writeNoException();
                            break;
                        case 19:
                            IMediaRouter2 _arg017 = IMediaRouter2.Stub.asInterface(data.readStrongBinder());
                            int _arg112 = data.readInt();
                            long _arg27 = data.readLong();
                            RoutingSessionInfo _arg3 = (RoutingSessionInfo) data.readTypedObject(RoutingSessionInfo.CREATOR);
                            MediaRoute2Info _arg4 = (MediaRoute2Info) data.readTypedObject(MediaRoute2Info.CREATOR);
                            Bundle _arg5 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            requestCreateSessionWithRouter2(_arg017, _arg112, _arg27, _arg3, _arg4, _arg5);
                            reply.writeNoException();
                            break;
                        case 20:
                            IMediaRouter2 _arg018 = IMediaRouter2.Stub.asInterface(data.readStrongBinder());
                            String _arg113 = data.readString();
                            MediaRoute2Info _arg28 = (MediaRoute2Info) data.readTypedObject(MediaRoute2Info.CREATOR);
                            data.enforceNoDataAvail();
                            selectRouteWithRouter2(_arg018, _arg113, _arg28);
                            reply.writeNoException();
                            break;
                        case 21:
                            IMediaRouter2 _arg019 = IMediaRouter2.Stub.asInterface(data.readStrongBinder());
                            String _arg114 = data.readString();
                            MediaRoute2Info _arg29 = (MediaRoute2Info) data.readTypedObject(MediaRoute2Info.CREATOR);
                            data.enforceNoDataAvail();
                            deselectRouteWithRouter2(_arg019, _arg114, _arg29);
                            reply.writeNoException();
                            break;
                        case 22:
                            IMediaRouter2 _arg020 = IMediaRouter2.Stub.asInterface(data.readStrongBinder());
                            String _arg115 = data.readString();
                            MediaRoute2Info _arg210 = (MediaRoute2Info) data.readTypedObject(MediaRoute2Info.CREATOR);
                            data.enforceNoDataAvail();
                            transferToRouteWithRouter2(_arg020, _arg115, _arg210);
                            reply.writeNoException();
                            break;
                        case 23:
                            IMediaRouter2 _arg021 = IMediaRouter2.Stub.asInterface(data.readStrongBinder());
                            String _arg116 = data.readString();
                            int _arg211 = data.readInt();
                            data.enforceNoDataAvail();
                            setSessionVolumeWithRouter2(_arg021, _arg116, _arg211);
                            reply.writeNoException();
                            break;
                        case 24:
                            IMediaRouter2 _arg022 = IMediaRouter2.Stub.asInterface(data.readStrongBinder());
                            String _arg117 = data.readString();
                            data.enforceNoDataAvail();
                            releaseSessionWithRouter2(_arg022, _arg117);
                            reply.writeNoException();
                            break;
                        case 25:
                            IMediaRouter2Manager _arg023 = IMediaRouter2Manager.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            List<RoutingSessionInfo> _result6 = getRemoteSessions(_arg023);
                            reply.writeNoException();
                            reply.writeTypedList(_result6, 1);
                            break;
                        case 26:
                            IMediaRouter2Manager _arg024 = IMediaRouter2Manager.Stub.asInterface(data.readStrongBinder());
                            String _arg118 = data.readString();
                            data.enforceNoDataAvail();
                            RoutingSessionInfo _result7 = getSystemSessionInfoForPackage(_arg024, _arg118);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 27:
                            IMediaRouter2Manager _arg025 = IMediaRouter2Manager.Stub.asInterface(data.readStrongBinder());
                            String _arg119 = data.readString();
                            data.enforceNoDataAvail();
                            registerManager(_arg025, _arg119);
                            reply.writeNoException();
                            break;
                        case 28:
                            IMediaRouter2Manager _arg026 = IMediaRouter2Manager.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterManager(_arg026);
                            reply.writeNoException();
                            break;
                        case 29:
                            IMediaRouter2Manager _arg027 = IMediaRouter2Manager.Stub.asInterface(data.readStrongBinder());
                            int _arg120 = data.readInt();
                            MediaRoute2Info _arg212 = (MediaRoute2Info) data.readTypedObject(MediaRoute2Info.CREATOR);
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            setRouteVolumeWithManager(_arg027, _arg120, _arg212, _arg32);
                            reply.writeNoException();
                            break;
                        case 30:
                            IMediaRouter2Manager _arg028 = IMediaRouter2Manager.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startScan(_arg028);
                            reply.writeNoException();
                            break;
                        case 31:
                            IMediaRouter2Manager _arg029 = IMediaRouter2Manager.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            stopScan(_arg029);
                            reply.writeNoException();
                            break;
                        case 32:
                            IMediaRouter2Manager _arg030 = IMediaRouter2Manager.Stub.asInterface(data.readStrongBinder());
                            int _arg121 = data.readInt();
                            RoutingSessionInfo _arg213 = (RoutingSessionInfo) data.readTypedObject(RoutingSessionInfo.CREATOR);
                            MediaRoute2Info _arg33 = (MediaRoute2Info) data.readTypedObject(MediaRoute2Info.CREATOR);
                            data.enforceNoDataAvail();
                            requestCreateSessionWithManager(_arg030, _arg121, _arg213, _arg33);
                            reply.writeNoException();
                            break;
                        case 33:
                            IMediaRouter2Manager _arg031 = IMediaRouter2Manager.Stub.asInterface(data.readStrongBinder());
                            int _arg122 = data.readInt();
                            String _arg214 = data.readString();
                            MediaRoute2Info _arg34 = (MediaRoute2Info) data.readTypedObject(MediaRoute2Info.CREATOR);
                            data.enforceNoDataAvail();
                            selectRouteWithManager(_arg031, _arg122, _arg214, _arg34);
                            reply.writeNoException();
                            break;
                        case 34:
                            IMediaRouter2Manager _arg032 = IMediaRouter2Manager.Stub.asInterface(data.readStrongBinder());
                            int _arg123 = data.readInt();
                            String _arg215 = data.readString();
                            MediaRoute2Info _arg35 = (MediaRoute2Info) data.readTypedObject(MediaRoute2Info.CREATOR);
                            data.enforceNoDataAvail();
                            deselectRouteWithManager(_arg032, _arg123, _arg215, _arg35);
                            reply.writeNoException();
                            break;
                        case 35:
                            IMediaRouter2Manager _arg033 = IMediaRouter2Manager.Stub.asInterface(data.readStrongBinder());
                            int _arg124 = data.readInt();
                            String _arg216 = data.readString();
                            MediaRoute2Info _arg36 = (MediaRoute2Info) data.readTypedObject(MediaRoute2Info.CREATOR);
                            data.enforceNoDataAvail();
                            transferToRouteWithManager(_arg033, _arg124, _arg216, _arg36);
                            reply.writeNoException();
                            break;
                        case 36:
                            IMediaRouter2Manager _arg034 = IMediaRouter2Manager.Stub.asInterface(data.readStrongBinder());
                            int _arg125 = data.readInt();
                            String _arg217 = data.readString();
                            int _arg37 = data.readInt();
                            data.enforceNoDataAvail();
                            setSessionVolumeWithManager(_arg034, _arg125, _arg217, _arg37);
                            reply.writeNoException();
                            break;
                        case 37:
                            IMediaRouter2Manager _arg035 = IMediaRouter2Manager.Stub.asInterface(data.readStrongBinder());
                            int _arg126 = data.readInt();
                            String _arg218 = data.readString();
                            data.enforceNoDataAvail();
                            releaseSessionWithManager(_arg035, _arg126, _arg218);
                            reply.writeNoException();
                            break;
                        case 38:
                            String _arg036 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result8 = showMediaOutputSwitcher(_arg036);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IMediaRouterService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.media.IMediaRouterService
            public void registerClientAsUser(IMediaRouterClient client, String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void unregisterClient(IMediaRouterClient client) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void registerClientGroupId(IMediaRouterClient client, String groupId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeString(groupId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public MediaRouterClientState getState(IMediaRouterClient client) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    MediaRouterClientState _result = (MediaRouterClientState) _reply.readTypedObject(MediaRouterClientState.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public boolean isPlaybackActive(IMediaRouterClient client) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void setBluetoothA2dpOn(IMediaRouterClient client, boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeBoolean(on);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void setDiscoveryRequest(IMediaRouterClient client, int routeTypes, boolean activeScan) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeInt(routeTypes);
                    _data.writeBoolean(activeScan);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void setSelectedRoute(IMediaRouterClient client, String routeId, boolean explicit) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeString(routeId);
                    _data.writeBoolean(explicit);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void requestSetVolume(IMediaRouterClient client, String routeId, int volume) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeString(routeId);
                    _data.writeInt(volume);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void requestUpdateVolume(IMediaRouterClient client, String routeId, int direction) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeString(routeId);
                    _data.writeInt(direction);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public boolean verifyPackageExists(String clientPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(clientPackageName);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public List<MediaRoute2Info> getSystemRoutes() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    List<MediaRoute2Info> _result = _reply.createTypedArrayList(MediaRoute2Info.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public RoutingSessionInfo getSystemSessionInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    RoutingSessionInfo _result = (RoutingSessionInfo) _reply.readTypedObject(RoutingSessionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void registerRouter2(IMediaRouter2 router, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(router);
                    _data.writeString(packageName);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void unregisterRouter2(IMediaRouter2 router) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(router);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void setDiscoveryRequestWithRouter2(IMediaRouter2 router, RouteDiscoveryPreference preference) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(router);
                    _data.writeTypedObject(preference, 0);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void setRouteListingPreference(IMediaRouter2 router, RouteListingPreference routeListingPreference) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(router);
                    _data.writeTypedObject(routeListingPreference, 0);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void setRouteVolumeWithRouter2(IMediaRouter2 router, MediaRoute2Info route, int volume) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(router);
                    _data.writeTypedObject(route, 0);
                    _data.writeInt(volume);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void requestCreateSessionWithRouter2(IMediaRouter2 router, int requestId, long managerRequestId, RoutingSessionInfo oldSession, MediaRoute2Info route, Bundle sessionHints) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(router);
                    _data.writeInt(requestId);
                    _data.writeLong(managerRequestId);
                    _data.writeTypedObject(oldSession, 0);
                    _data.writeTypedObject(route, 0);
                    _data.writeTypedObject(sessionHints, 0);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void selectRouteWithRouter2(IMediaRouter2 router, String sessionId, MediaRoute2Info route) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(router);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(route, 0);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void deselectRouteWithRouter2(IMediaRouter2 router, String sessionId, MediaRoute2Info route) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(router);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(route, 0);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void transferToRouteWithRouter2(IMediaRouter2 router, String sessionId, MediaRoute2Info route) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(router);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(route, 0);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void setSessionVolumeWithRouter2(IMediaRouter2 router, String sessionId, int volume) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(router);
                    _data.writeString(sessionId);
                    _data.writeInt(volume);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void releaseSessionWithRouter2(IMediaRouter2 router, String sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(router);
                    _data.writeString(sessionId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public List<RoutingSessionInfo> getRemoteSessions(IMediaRouter2Manager manager) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(manager);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    List<RoutingSessionInfo> _result = _reply.createTypedArrayList(RoutingSessionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public RoutingSessionInfo getSystemSessionInfoForPackage(IMediaRouter2Manager manager, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(manager);
                    _data.writeString(packageName);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    RoutingSessionInfo _result = (RoutingSessionInfo) _reply.readTypedObject(RoutingSessionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void registerManager(IMediaRouter2Manager manager, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(manager);
                    _data.writeString(packageName);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void unregisterManager(IMediaRouter2Manager manager) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(manager);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void setRouteVolumeWithManager(IMediaRouter2Manager manager, int requestId, MediaRoute2Info route, int volume) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(manager);
                    _data.writeInt(requestId);
                    _data.writeTypedObject(route, 0);
                    _data.writeInt(volume);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void startScan(IMediaRouter2Manager manager) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(manager);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void stopScan(IMediaRouter2Manager manager) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(manager);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void requestCreateSessionWithManager(IMediaRouter2Manager manager, int requestId, RoutingSessionInfo oldSession, MediaRoute2Info route) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(manager);
                    _data.writeInt(requestId);
                    _data.writeTypedObject(oldSession, 0);
                    _data.writeTypedObject(route, 0);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void selectRouteWithManager(IMediaRouter2Manager manager, int requestId, String sessionId, MediaRoute2Info route) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(manager);
                    _data.writeInt(requestId);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(route, 0);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void deselectRouteWithManager(IMediaRouter2Manager manager, int requestId, String sessionId, MediaRoute2Info route) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(manager);
                    _data.writeInt(requestId);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(route, 0);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void transferToRouteWithManager(IMediaRouter2Manager manager, int requestId, String sessionId, MediaRoute2Info route) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(manager);
                    _data.writeInt(requestId);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(route, 0);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void setSessionVolumeWithManager(IMediaRouter2Manager manager, int requestId, String sessionId, int volume) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(manager);
                    _data.writeInt(requestId);
                    _data.writeString(sessionId);
                    _data.writeInt(volume);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public void releaseSessionWithManager(IMediaRouter2Manager manager, int requestId, String sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(manager);
                    _data.writeInt(requestId);
                    _data.writeString(sessionId);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRouterService
            public boolean showMediaOutputSwitcher(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 37;
        }
    }
}
