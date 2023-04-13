package android.media.p007tv.interactive;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.media.tv.interactive.ITvInteractiveAppManagerCallback */
/* loaded from: classes2.dex */
public interface ITvInteractiveAppManagerCallback extends IInterface {
    public static final String DESCRIPTOR = "android.media.tv.interactive.ITvInteractiveAppManagerCallback";

    void onInteractiveAppServiceAdded(String str) throws RemoteException;

    void onInteractiveAppServiceRemoved(String str) throws RemoteException;

    void onInteractiveAppServiceUpdated(String str) throws RemoteException;

    void onStateChanged(String str, int i, int i2, int i3) throws RemoteException;

    void onTvInteractiveAppServiceInfoUpdated(TvInteractiveAppServiceInfo tvInteractiveAppServiceInfo) throws RemoteException;

    /* renamed from: android.media.tv.interactive.ITvInteractiveAppManagerCallback$Default */
    /* loaded from: classes2.dex */
    public static class Default implements ITvInteractiveAppManagerCallback {
        @Override // android.media.p007tv.interactive.ITvInteractiveAppManagerCallback
        public void onInteractiveAppServiceAdded(String iAppServiceId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManagerCallback
        public void onInteractiveAppServiceRemoved(String iAppServiceId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManagerCallback
        public void onInteractiveAppServiceUpdated(String iAppServiceId) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManagerCallback
        public void onTvInteractiveAppServiceInfoUpdated(TvInteractiveAppServiceInfo tvIAppInfo) throws RemoteException {
        }

        @Override // android.media.p007tv.interactive.ITvInteractiveAppManagerCallback
        public void onStateChanged(String iAppServiceId, int type, int state, int err) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.media.tv.interactive.ITvInteractiveAppManagerCallback$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITvInteractiveAppManagerCallback {
        static final int TRANSACTION_onInteractiveAppServiceAdded = 1;
        static final int TRANSACTION_onInteractiveAppServiceRemoved = 2;
        static final int TRANSACTION_onInteractiveAppServiceUpdated = 3;
        static final int TRANSACTION_onStateChanged = 5;
        static final int TRANSACTION_onTvInteractiveAppServiceInfoUpdated = 4;

        public Stub() {
            attachInterface(this, ITvInteractiveAppManagerCallback.DESCRIPTOR);
        }

        public static ITvInteractiveAppManagerCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITvInteractiveAppManagerCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ITvInteractiveAppManagerCallback)) {
                return (ITvInteractiveAppManagerCallback) iin;
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
                    return "onInteractiveAppServiceAdded";
                case 2:
                    return "onInteractiveAppServiceRemoved";
                case 3:
                    return "onInteractiveAppServiceUpdated";
                case 4:
                    return "onTvInteractiveAppServiceInfoUpdated";
                case 5:
                    return "onStateChanged";
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
                data.enforceInterface(ITvInteractiveAppManagerCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITvInteractiveAppManagerCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            onInteractiveAppServiceAdded(_arg0);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            onInteractiveAppServiceRemoved(_arg02);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            onInteractiveAppServiceUpdated(_arg03);
                            break;
                        case 4:
                            TvInteractiveAppServiceInfo _arg04 = (TvInteractiveAppServiceInfo) data.readTypedObject(TvInteractiveAppServiceInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onTvInteractiveAppServiceInfoUpdated(_arg04);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            onStateChanged(_arg05, _arg1, _arg2, _arg3);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.media.tv.interactive.ITvInteractiveAppManagerCallback$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements ITvInteractiveAppManagerCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITvInteractiveAppManagerCallback.DESCRIPTOR;
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManagerCallback
            public void onInteractiveAppServiceAdded(String iAppServiceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManagerCallback.DESCRIPTOR);
                    _data.writeString(iAppServiceId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManagerCallback
            public void onInteractiveAppServiceRemoved(String iAppServiceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManagerCallback.DESCRIPTOR);
                    _data.writeString(iAppServiceId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManagerCallback
            public void onInteractiveAppServiceUpdated(String iAppServiceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManagerCallback.DESCRIPTOR);
                    _data.writeString(iAppServiceId);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManagerCallback
            public void onTvInteractiveAppServiceInfoUpdated(TvInteractiveAppServiceInfo tvIAppInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManagerCallback.DESCRIPTOR);
                    _data.writeTypedObject(tvIAppInfo, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.interactive.ITvInteractiveAppManagerCallback
            public void onStateChanged(String iAppServiceId, int type, int state, int err) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITvInteractiveAppManagerCallback.DESCRIPTOR);
                    _data.writeString(iAppServiceId);
                    _data.writeInt(type);
                    _data.writeInt(state);
                    _data.writeInt(err);
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
