package android.media.p007tv;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* renamed from: android.media.tv.ITvInputManagerCallback */
/* loaded from: classes2.dex */
public interface ITvInputManagerCallback extends IInterface {
    void onCurrentTunedInfosUpdated(List<TunedInfo> list) throws RemoteException;

    void onInputAdded(String str) throws RemoteException;

    void onInputRemoved(String str) throws RemoteException;

    void onInputStateChanged(String str, int i) throws RemoteException;

    void onInputUpdated(String str) throws RemoteException;

    void onTvInputInfoUpdated(TvInputInfo tvInputInfo) throws RemoteException;

    /* renamed from: android.media.tv.ITvInputManagerCallback$Default */
    /* loaded from: classes2.dex */
    public static class Default implements ITvInputManagerCallback {
        @Override // android.media.p007tv.ITvInputManagerCallback
        public void onInputAdded(String inputId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManagerCallback
        public void onInputRemoved(String inputId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManagerCallback
        public void onInputUpdated(String inputId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManagerCallback
        public void onInputStateChanged(String inputId, int state) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManagerCallback
        public void onTvInputInfoUpdated(TvInputInfo TvInputInfo) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputManagerCallback
        public void onCurrentTunedInfosUpdated(List<TunedInfo> currentTunedInfos) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.media.tv.ITvInputManagerCallback$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITvInputManagerCallback {
        public static final String DESCRIPTOR = "android.media.tv.ITvInputManagerCallback";
        static final int TRANSACTION_onCurrentTunedInfosUpdated = 6;
        static final int TRANSACTION_onInputAdded = 1;
        static final int TRANSACTION_onInputRemoved = 2;
        static final int TRANSACTION_onInputStateChanged = 4;
        static final int TRANSACTION_onInputUpdated = 3;
        static final int TRANSACTION_onTvInputInfoUpdated = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITvInputManagerCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITvInputManagerCallback)) {
                return (ITvInputManagerCallback) iin;
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
                    return "onInputAdded";
                case 2:
                    return "onInputRemoved";
                case 3:
                    return "onInputUpdated";
                case 4:
                    return "onInputStateChanged";
                case 5:
                    return "onTvInputInfoUpdated";
                case 6:
                    return "onCurrentTunedInfosUpdated";
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
                            data.enforceNoDataAvail();
                            onInputAdded(_arg0);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            onInputRemoved(_arg02);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            onInputUpdated(_arg03);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onInputStateChanged(_arg04, _arg1);
                            break;
                        case 5:
                            TvInputInfo _arg05 = (TvInputInfo) data.readTypedObject(TvInputInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onTvInputInfoUpdated(_arg05);
                            break;
                        case 6:
                            List<TunedInfo> _arg06 = data.createTypedArrayList(TunedInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onCurrentTunedInfosUpdated(_arg06);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.media.tv.ITvInputManagerCallback$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements ITvInputManagerCallback {
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

            @Override // android.media.p007tv.ITvInputManagerCallback
            public void onInputAdded(String inputId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManagerCallback
            public void onInputRemoved(String inputId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManagerCallback
            public void onInputUpdated(String inputId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputId);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManagerCallback
            public void onInputStateChanged(String inputId, int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputId);
                    _data.writeInt(state);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManagerCallback
            public void onTvInputInfoUpdated(TvInputInfo TvInputInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(TvInputInfo, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputManagerCallback
            public void onCurrentTunedInfosUpdated(List<TunedInfo> currentTunedInfos) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(currentTunedInfos, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 5;
        }
    }
}
