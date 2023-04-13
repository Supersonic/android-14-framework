package android.hardware.fingerprint;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.ThreadedRenderer;
/* loaded from: classes.dex */
public interface IUdfpsOverlay extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.fingerprint.IUdfpsOverlay";

    void hide(int i) throws RemoteException;

    void show(long j, int i, int i2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IUdfpsOverlay {
        @Override // android.hardware.fingerprint.IUdfpsOverlay
        public void show(long requestId, int sensorId, int reason) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IUdfpsOverlay
        public void hide(int sensorId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IUdfpsOverlay {
        static final int TRANSACTION_hide = 2;
        static final int TRANSACTION_show = 1;

        public Stub() {
            attachInterface(this, IUdfpsOverlay.DESCRIPTOR);
        }

        public static IUdfpsOverlay asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IUdfpsOverlay.DESCRIPTOR);
            if (iin != null && (iin instanceof IUdfpsOverlay)) {
                return (IUdfpsOverlay) iin;
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
                    return ThreadedRenderer.OVERDRAW_PROPERTY_SHOW;
                case 2:
                    return "hide";
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
                data.enforceInterface(IUdfpsOverlay.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IUdfpsOverlay.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            long _arg0 = data.readLong();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            show(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            hide(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IUdfpsOverlay {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IUdfpsOverlay.DESCRIPTOR;
            }

            @Override // android.hardware.fingerprint.IUdfpsOverlay
            public void show(long requestId, int sensorId, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IUdfpsOverlay.DESCRIPTOR);
                    _data.writeLong(requestId);
                    _data.writeInt(sensorId);
                    _data.writeInt(reason);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IUdfpsOverlay
            public void hide(int sensorId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IUdfpsOverlay.DESCRIPTOR);
                    _data.writeInt(sensorId);
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
