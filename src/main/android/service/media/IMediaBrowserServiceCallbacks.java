package android.service.media;

import android.content.p001pm.ParceledListSlice;
import android.media.session.MediaSession;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IMediaBrowserServiceCallbacks extends IInterface {
    void onConnect(String str, MediaSession.Token token, Bundle bundle) throws RemoteException;

    void onConnectFailed() throws RemoteException;

    void onLoadChildren(String str, ParceledListSlice parceledListSlice, Bundle bundle) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IMediaBrowserServiceCallbacks {
        @Override // android.service.media.IMediaBrowserServiceCallbacks
        public void onConnect(String root, MediaSession.Token session, Bundle extras) throws RemoteException {
        }

        @Override // android.service.media.IMediaBrowserServiceCallbacks
        public void onConnectFailed() throws RemoteException {
        }

        @Override // android.service.media.IMediaBrowserServiceCallbacks
        public void onLoadChildren(String mediaId, ParceledListSlice list, Bundle options) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IMediaBrowserServiceCallbacks {
        public static final String DESCRIPTOR = "android.service.media.IMediaBrowserServiceCallbacks";
        static final int TRANSACTION_onConnect = 1;
        static final int TRANSACTION_onConnectFailed = 2;
        static final int TRANSACTION_onLoadChildren = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaBrowserServiceCallbacks asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMediaBrowserServiceCallbacks)) {
                return (IMediaBrowserServiceCallbacks) iin;
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
                    return "onConnect";
                case 2:
                    return "onConnectFailed";
                case 3:
                    return "onLoadChildren";
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
                            String _arg0 = data.readString();
                            MediaSession.Token _arg1 = (MediaSession.Token) data.readTypedObject(MediaSession.Token.CREATOR);
                            Bundle _arg2 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onConnect(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            onConnectFailed();
                            break;
                        case 3:
                            String _arg02 = data.readString();
                            ParceledListSlice _arg12 = (ParceledListSlice) data.readTypedObject(ParceledListSlice.CREATOR);
                            Bundle _arg22 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            onLoadChildren(_arg02, _arg12, _arg22);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IMediaBrowserServiceCallbacks {
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

            @Override // android.service.media.IMediaBrowserServiceCallbacks
            public void onConnect(String root, MediaSession.Token session, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(root);
                    _data.writeTypedObject(session, 0);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.media.IMediaBrowserServiceCallbacks
            public void onConnectFailed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.media.IMediaBrowserServiceCallbacks
            public void onLoadChildren(String mediaId, ParceledListSlice list, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(mediaId);
                    _data.writeTypedObject(list, 0);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
