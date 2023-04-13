package android.media.p007tv;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.media.tv.ITvInputHardwareCallback */
/* loaded from: classes2.dex */
public interface ITvInputHardwareCallback extends IInterface {
    void onReleased() throws RemoteException;

    void onStreamConfigChanged(TvStreamConfig[] tvStreamConfigArr) throws RemoteException;

    /* renamed from: android.media.tv.ITvInputHardwareCallback$Default */
    /* loaded from: classes2.dex */
    public static class Default implements ITvInputHardwareCallback {
        @Override // android.media.p007tv.ITvInputHardwareCallback
        public void onReleased() throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputHardwareCallback
        public void onStreamConfigChanged(TvStreamConfig[] configs) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.media.tv.ITvInputHardwareCallback$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITvInputHardwareCallback {
        public static final String DESCRIPTOR = "android.media.tv.ITvInputHardwareCallback";
        static final int TRANSACTION_onReleased = 1;
        static final int TRANSACTION_onStreamConfigChanged = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITvInputHardwareCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITvInputHardwareCallback)) {
                return (ITvInputHardwareCallback) iin;
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
                    return "onReleased";
                case 2:
                    return "onStreamConfigChanged";
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
                            onReleased();
                            break;
                        case 2:
                            TvStreamConfig[] _arg0 = (TvStreamConfig[]) data.createTypedArray(TvStreamConfig.CREATOR);
                            data.enforceNoDataAvail();
                            onStreamConfigChanged(_arg0);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.media.tv.ITvInputHardwareCallback$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements ITvInputHardwareCallback {
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

            @Override // android.media.p007tv.ITvInputHardwareCallback
            public void onReleased() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputHardwareCallback
            public void onStreamConfigChanged(TvStreamConfig[] configs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(configs, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
