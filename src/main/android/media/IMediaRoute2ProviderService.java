package android.media;

import android.media.IMediaRoute2ProviderServiceCallback;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IMediaRoute2ProviderService extends IInterface {
    public static final String DESCRIPTOR = "android.media.IMediaRoute2ProviderService";

    void deselectRoute(long j, String str, String str2) throws RemoteException;

    void releaseSession(long j, String str) throws RemoteException;

    void requestCreateSession(long j, String str, String str2, Bundle bundle) throws RemoteException;

    void selectRoute(long j, String str, String str2) throws RemoteException;

    void setCallback(IMediaRoute2ProviderServiceCallback iMediaRoute2ProviderServiceCallback) throws RemoteException;

    void setRouteVolume(long j, String str, int i) throws RemoteException;

    void setSessionVolume(long j, String str, int i) throws RemoteException;

    void transferToRoute(long j, String str, String str2) throws RemoteException;

    void updateDiscoveryPreference(RouteDiscoveryPreference routeDiscoveryPreference) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMediaRoute2ProviderService {
        @Override // android.media.IMediaRoute2ProviderService
        public void setCallback(IMediaRoute2ProviderServiceCallback callback) throws RemoteException {
        }

        @Override // android.media.IMediaRoute2ProviderService
        public void updateDiscoveryPreference(RouteDiscoveryPreference discoveryPreference) throws RemoteException {
        }

        @Override // android.media.IMediaRoute2ProviderService
        public void setRouteVolume(long requestId, String routeId, int volume) throws RemoteException {
        }

        @Override // android.media.IMediaRoute2ProviderService
        public void requestCreateSession(long requestId, String packageName, String routeId, Bundle sessionHints) throws RemoteException {
        }

        @Override // android.media.IMediaRoute2ProviderService
        public void selectRoute(long requestId, String sessionId, String routeId) throws RemoteException {
        }

        @Override // android.media.IMediaRoute2ProviderService
        public void deselectRoute(long requestId, String sessionId, String routeId) throws RemoteException {
        }

        @Override // android.media.IMediaRoute2ProviderService
        public void transferToRoute(long requestId, String sessionId, String routeId) throws RemoteException {
        }

        @Override // android.media.IMediaRoute2ProviderService
        public void setSessionVolume(long requestId, String sessionId, int volume) throws RemoteException {
        }

        @Override // android.media.IMediaRoute2ProviderService
        public void releaseSession(long requestId, String sessionId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMediaRoute2ProviderService {
        static final int TRANSACTION_deselectRoute = 6;
        static final int TRANSACTION_releaseSession = 9;
        static final int TRANSACTION_requestCreateSession = 4;
        static final int TRANSACTION_selectRoute = 5;
        static final int TRANSACTION_setCallback = 1;
        static final int TRANSACTION_setRouteVolume = 3;
        static final int TRANSACTION_setSessionVolume = 8;
        static final int TRANSACTION_transferToRoute = 7;
        static final int TRANSACTION_updateDiscoveryPreference = 2;

        public Stub() {
            attachInterface(this, IMediaRoute2ProviderService.DESCRIPTOR);
        }

        public static IMediaRoute2ProviderService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IMediaRoute2ProviderService.DESCRIPTOR);
            if (iin != null && (iin instanceof IMediaRoute2ProviderService)) {
                return (IMediaRoute2ProviderService) iin;
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
                    return "setCallback";
                case 2:
                    return "updateDiscoveryPreference";
                case 3:
                    return "setRouteVolume";
                case 4:
                    return "requestCreateSession";
                case 5:
                    return "selectRoute";
                case 6:
                    return "deselectRoute";
                case 7:
                    return "transferToRoute";
                case 8:
                    return "setSessionVolume";
                case 9:
                    return "releaseSession";
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
                data.enforceInterface(IMediaRoute2ProviderService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IMediaRoute2ProviderService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IMediaRoute2ProviderServiceCallback _arg0 = IMediaRoute2ProviderServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setCallback(_arg0);
                            break;
                        case 2:
                            RouteDiscoveryPreference _arg02 = (RouteDiscoveryPreference) data.readTypedObject(RouteDiscoveryPreference.CREATOR);
                            data.enforceNoDataAvail();
                            updateDiscoveryPreference(_arg02);
                            break;
                        case 3:
                            long _arg03 = data.readLong();
                            String _arg1 = data.readString();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            setRouteVolume(_arg03, _arg1, _arg2);
                            break;
                        case 4:
                            long _arg04 = data.readLong();
                            String _arg12 = data.readString();
                            String _arg22 = data.readString();
                            Bundle _arg3 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            requestCreateSession(_arg04, _arg12, _arg22, _arg3);
                            break;
                        case 5:
                            long _arg05 = data.readLong();
                            String _arg13 = data.readString();
                            String _arg23 = data.readString();
                            data.enforceNoDataAvail();
                            selectRoute(_arg05, _arg13, _arg23);
                            break;
                        case 6:
                            long _arg06 = data.readLong();
                            String _arg14 = data.readString();
                            String _arg24 = data.readString();
                            data.enforceNoDataAvail();
                            deselectRoute(_arg06, _arg14, _arg24);
                            break;
                        case 7:
                            long _arg07 = data.readLong();
                            String _arg15 = data.readString();
                            String _arg25 = data.readString();
                            data.enforceNoDataAvail();
                            transferToRoute(_arg07, _arg15, _arg25);
                            break;
                        case 8:
                            long _arg08 = data.readLong();
                            String _arg16 = data.readString();
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            setSessionVolume(_arg08, _arg16, _arg26);
                            break;
                        case 9:
                            long _arg09 = data.readLong();
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            releaseSession(_arg09, _arg17);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IMediaRoute2ProviderService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMediaRoute2ProviderService.DESCRIPTOR;
            }

            @Override // android.media.IMediaRoute2ProviderService
            public void setCallback(IMediaRoute2ProviderServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMediaRoute2ProviderService.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRoute2ProviderService
            public void updateDiscoveryPreference(RouteDiscoveryPreference discoveryPreference) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMediaRoute2ProviderService.DESCRIPTOR);
                    _data.writeTypedObject(discoveryPreference, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRoute2ProviderService
            public void setRouteVolume(long requestId, String routeId, int volume) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMediaRoute2ProviderService.DESCRIPTOR);
                    _data.writeLong(requestId);
                    _data.writeString(routeId);
                    _data.writeInt(volume);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRoute2ProviderService
            public void requestCreateSession(long requestId, String packageName, String routeId, Bundle sessionHints) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMediaRoute2ProviderService.DESCRIPTOR);
                    _data.writeLong(requestId);
                    _data.writeString(packageName);
                    _data.writeString(routeId);
                    _data.writeTypedObject(sessionHints, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRoute2ProviderService
            public void selectRoute(long requestId, String sessionId, String routeId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMediaRoute2ProviderService.DESCRIPTOR);
                    _data.writeLong(requestId);
                    _data.writeString(sessionId);
                    _data.writeString(routeId);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRoute2ProviderService
            public void deselectRoute(long requestId, String sessionId, String routeId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMediaRoute2ProviderService.DESCRIPTOR);
                    _data.writeLong(requestId);
                    _data.writeString(sessionId);
                    _data.writeString(routeId);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRoute2ProviderService
            public void transferToRoute(long requestId, String sessionId, String routeId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMediaRoute2ProviderService.DESCRIPTOR);
                    _data.writeLong(requestId);
                    _data.writeString(sessionId);
                    _data.writeString(routeId);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRoute2ProviderService
            public void setSessionVolume(long requestId, String sessionId, int volume) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMediaRoute2ProviderService.DESCRIPTOR);
                    _data.writeLong(requestId);
                    _data.writeString(sessionId);
                    _data.writeInt(volume);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRoute2ProviderService
            public void releaseSession(long requestId, String sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMediaRoute2ProviderService.DESCRIPTOR);
                    _data.writeLong(requestId);
                    _data.writeString(sessionId);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
