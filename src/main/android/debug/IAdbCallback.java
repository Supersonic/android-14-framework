package android.debug;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IAdbCallback extends IInterface {
    public static final String DESCRIPTOR = "android.debug.IAdbCallback";

    void onDebuggingChanged(boolean z, byte b) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAdbCallback {
        @Override // android.debug.IAdbCallback
        public void onDebuggingChanged(boolean enabled, byte type) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAdbCallback {
        static final int TRANSACTION_onDebuggingChanged = 1;

        public Stub() {
            attachInterface(this, IAdbCallback.DESCRIPTOR);
        }

        public static IAdbCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAdbCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IAdbCallback)) {
                return (IAdbCallback) iin;
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
                    return "onDebuggingChanged";
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
                data.enforceInterface(IAdbCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAdbCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            byte _arg1 = data.readByte();
                            data.enforceNoDataAvail();
                            onDebuggingChanged(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IAdbCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAdbCallback.DESCRIPTOR;
            }

            @Override // android.debug.IAdbCallback
            public void onDebuggingChanged(boolean enabled, byte type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAdbCallback.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    _data.writeByte(type);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
