package android.service.media;

import android.media.MediaMetrics;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.service.media.IMediaBrowserServiceCallbacks;
/* loaded from: classes3.dex */
public interface IMediaBrowserService extends IInterface {
    void addSubscription(String str, IBinder iBinder, Bundle bundle, IMediaBrowserServiceCallbacks iMediaBrowserServiceCallbacks) throws RemoteException;

    void addSubscriptionDeprecated(String str, IMediaBrowserServiceCallbacks iMediaBrowserServiceCallbacks) throws RemoteException;

    void connect(String str, Bundle bundle, IMediaBrowserServiceCallbacks iMediaBrowserServiceCallbacks) throws RemoteException;

    void disconnect(IMediaBrowserServiceCallbacks iMediaBrowserServiceCallbacks) throws RemoteException;

    void getMediaItem(String str, ResultReceiver resultReceiver, IMediaBrowserServiceCallbacks iMediaBrowserServiceCallbacks) throws RemoteException;

    void removeSubscription(String str, IBinder iBinder, IMediaBrowserServiceCallbacks iMediaBrowserServiceCallbacks) throws RemoteException;

    void removeSubscriptionDeprecated(String str, IMediaBrowserServiceCallbacks iMediaBrowserServiceCallbacks) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IMediaBrowserService {
        @Override // android.service.media.IMediaBrowserService
        public void connect(String pkg, Bundle rootHints, IMediaBrowserServiceCallbacks callbacks) throws RemoteException {
        }

        @Override // android.service.media.IMediaBrowserService
        public void disconnect(IMediaBrowserServiceCallbacks callbacks) throws RemoteException {
        }

        @Override // android.service.media.IMediaBrowserService
        public void addSubscriptionDeprecated(String uri, IMediaBrowserServiceCallbacks callbacks) throws RemoteException {
        }

        @Override // android.service.media.IMediaBrowserService
        public void removeSubscriptionDeprecated(String uri, IMediaBrowserServiceCallbacks callbacks) throws RemoteException {
        }

        @Override // android.service.media.IMediaBrowserService
        public void getMediaItem(String uri, ResultReceiver cb, IMediaBrowserServiceCallbacks callbacks) throws RemoteException {
        }

        @Override // android.service.media.IMediaBrowserService
        public void addSubscription(String uri, IBinder token, Bundle options, IMediaBrowserServiceCallbacks callbacks) throws RemoteException {
        }

        @Override // android.service.media.IMediaBrowserService
        public void removeSubscription(String uri, IBinder token, IMediaBrowserServiceCallbacks callbacks) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IMediaBrowserService {
        public static final String DESCRIPTOR = "android.service.media.IMediaBrowserService";
        static final int TRANSACTION_addSubscription = 6;
        static final int TRANSACTION_addSubscriptionDeprecated = 3;
        static final int TRANSACTION_connect = 1;
        static final int TRANSACTION_disconnect = 2;
        static final int TRANSACTION_getMediaItem = 5;
        static final int TRANSACTION_removeSubscription = 7;
        static final int TRANSACTION_removeSubscriptionDeprecated = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaBrowserService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMediaBrowserService)) {
                return (IMediaBrowserService) iin;
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
                    return MediaMetrics.Value.CONNECT;
                case 2:
                    return MediaMetrics.Value.DISCONNECT;
                case 3:
                    return "addSubscriptionDeprecated";
                case 4:
                    return "removeSubscriptionDeprecated";
                case 5:
                    return "getMediaItem";
                case 6:
                    return "addSubscription";
                case 7:
                    return "removeSubscription";
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
                            Bundle _arg1 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            IMediaBrowserServiceCallbacks _arg2 = IMediaBrowserServiceCallbacks.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            connect(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            IMediaBrowserServiceCallbacks _arg02 = IMediaBrowserServiceCallbacks.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            disconnect(_arg02);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            IMediaBrowserServiceCallbacks _arg12 = IMediaBrowserServiceCallbacks.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addSubscriptionDeprecated(_arg03, _arg12);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            IMediaBrowserServiceCallbacks _arg13 = IMediaBrowserServiceCallbacks.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeSubscriptionDeprecated(_arg04, _arg13);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            ResultReceiver _arg14 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            IMediaBrowserServiceCallbacks _arg22 = IMediaBrowserServiceCallbacks.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getMediaItem(_arg05, _arg14, _arg22);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            IBinder _arg15 = data.readStrongBinder();
                            Bundle _arg23 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            IMediaBrowserServiceCallbacks _arg3 = IMediaBrowserServiceCallbacks.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addSubscription(_arg06, _arg15, _arg23, _arg3);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            IBinder _arg16 = data.readStrongBinder();
                            IMediaBrowserServiceCallbacks _arg24 = IMediaBrowserServiceCallbacks.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeSubscription(_arg07, _arg16, _arg24);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IMediaBrowserService {
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

            @Override // android.service.media.IMediaBrowserService
            public void connect(String pkg, Bundle rootHints, IMediaBrowserServiceCallbacks callbacks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    _data.writeTypedObject(rootHints, 0);
                    _data.writeStrongInterface(callbacks);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.media.IMediaBrowserService
            public void disconnect(IMediaBrowserServiceCallbacks callbacks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callbacks);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.media.IMediaBrowserService
            public void addSubscriptionDeprecated(String uri, IMediaBrowserServiceCallbacks callbacks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uri);
                    _data.writeStrongInterface(callbacks);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.media.IMediaBrowserService
            public void removeSubscriptionDeprecated(String uri, IMediaBrowserServiceCallbacks callbacks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uri);
                    _data.writeStrongInterface(callbacks);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.media.IMediaBrowserService
            public void getMediaItem(String uri, ResultReceiver cb, IMediaBrowserServiceCallbacks callbacks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uri);
                    _data.writeTypedObject(cb, 0);
                    _data.writeStrongInterface(callbacks);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.media.IMediaBrowserService
            public void addSubscription(String uri, IBinder token, Bundle options, IMediaBrowserServiceCallbacks callbacks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uri);
                    _data.writeStrongBinder(token);
                    _data.writeTypedObject(options, 0);
                    _data.writeStrongInterface(callbacks);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.media.IMediaBrowserService
            public void removeSubscription(String uri, IBinder token, IMediaBrowserServiceCallbacks callbacks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uri);
                    _data.writeStrongBinder(token);
                    _data.writeStrongInterface(callbacks);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
