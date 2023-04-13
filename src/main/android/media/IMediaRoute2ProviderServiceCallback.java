package android.media;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface IMediaRoute2ProviderServiceCallback extends IInterface {
    public static final String DESCRIPTOR = "android.media.IMediaRoute2ProviderServiceCallback";

    void notifyProviderUpdated(MediaRoute2ProviderInfo mediaRoute2ProviderInfo) throws RemoteException;

    void notifyRequestFailed(long j, int i) throws RemoteException;

    void notifySessionCreated(long j, RoutingSessionInfo routingSessionInfo) throws RemoteException;

    void notifySessionReleased(RoutingSessionInfo routingSessionInfo) throws RemoteException;

    void notifySessionsUpdated(List<RoutingSessionInfo> list) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMediaRoute2ProviderServiceCallback {
        @Override // android.media.IMediaRoute2ProviderServiceCallback
        public void notifyProviderUpdated(MediaRoute2ProviderInfo providerInfo) throws RemoteException {
        }

        @Override // android.media.IMediaRoute2ProviderServiceCallback
        public void notifySessionCreated(long requestId, RoutingSessionInfo sessionInfo) throws RemoteException {
        }

        @Override // android.media.IMediaRoute2ProviderServiceCallback
        public void notifySessionsUpdated(List<RoutingSessionInfo> sessionInfo) throws RemoteException {
        }

        @Override // android.media.IMediaRoute2ProviderServiceCallback
        public void notifySessionReleased(RoutingSessionInfo sessionInfo) throws RemoteException {
        }

        @Override // android.media.IMediaRoute2ProviderServiceCallback
        public void notifyRequestFailed(long requestId, int reason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMediaRoute2ProviderServiceCallback {
        static final int TRANSACTION_notifyProviderUpdated = 1;
        static final int TRANSACTION_notifyRequestFailed = 5;
        static final int TRANSACTION_notifySessionCreated = 2;
        static final int TRANSACTION_notifySessionReleased = 4;
        static final int TRANSACTION_notifySessionsUpdated = 3;

        public Stub() {
            attachInterface(this, IMediaRoute2ProviderServiceCallback.DESCRIPTOR);
        }

        public static IMediaRoute2ProviderServiceCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IMediaRoute2ProviderServiceCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IMediaRoute2ProviderServiceCallback)) {
                return (IMediaRoute2ProviderServiceCallback) iin;
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
                    return "notifyProviderUpdated";
                case 2:
                    return "notifySessionCreated";
                case 3:
                    return "notifySessionsUpdated";
                case 4:
                    return "notifySessionReleased";
                case 5:
                    return "notifyRequestFailed";
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
                data.enforceInterface(IMediaRoute2ProviderServiceCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IMediaRoute2ProviderServiceCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            MediaRoute2ProviderInfo _arg0 = (MediaRoute2ProviderInfo) data.readTypedObject(MediaRoute2ProviderInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifyProviderUpdated(_arg0);
                            break;
                        case 2:
                            long _arg02 = data.readLong();
                            RoutingSessionInfo _arg1 = (RoutingSessionInfo) data.readTypedObject(RoutingSessionInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifySessionCreated(_arg02, _arg1);
                            break;
                        case 3:
                            List<RoutingSessionInfo> _arg03 = data.createTypedArrayList(RoutingSessionInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifySessionsUpdated(_arg03);
                            break;
                        case 4:
                            RoutingSessionInfo _arg04 = (RoutingSessionInfo) data.readTypedObject(RoutingSessionInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifySessionReleased(_arg04);
                            break;
                        case 5:
                            long _arg05 = data.readLong();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyRequestFailed(_arg05, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IMediaRoute2ProviderServiceCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMediaRoute2ProviderServiceCallback.DESCRIPTOR;
            }

            @Override // android.media.IMediaRoute2ProviderServiceCallback
            public void notifyProviderUpdated(MediaRoute2ProviderInfo providerInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMediaRoute2ProviderServiceCallback.DESCRIPTOR);
                    _data.writeTypedObject(providerInfo, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRoute2ProviderServiceCallback
            public void notifySessionCreated(long requestId, RoutingSessionInfo sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMediaRoute2ProviderServiceCallback.DESCRIPTOR);
                    _data.writeLong(requestId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRoute2ProviderServiceCallback
            public void notifySessionsUpdated(List<RoutingSessionInfo> sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMediaRoute2ProviderServiceCallback.DESCRIPTOR);
                    _data.writeTypedList(sessionInfo, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRoute2ProviderServiceCallback
            public void notifySessionReleased(RoutingSessionInfo sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMediaRoute2ProviderServiceCallback.DESCRIPTOR);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IMediaRoute2ProviderServiceCallback
            public void notifyRequestFailed(long requestId, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IMediaRoute2ProviderServiceCallback.DESCRIPTOR);
                    _data.writeLong(requestId);
                    _data.writeInt(reason);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }
}
