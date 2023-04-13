package android.net.wifi.nl80211;

import android.net.wifi.nl80211.IApInterfaceEventCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IApInterface extends IInterface {
    public static final String DESCRIPTOR = "android.net.wifi.nl80211.IApInterface";
    public static final int ENCRYPTION_TYPE_NONE = 0;
    public static final int ENCRYPTION_TYPE_WPA = 1;
    public static final int ENCRYPTION_TYPE_WPA2 = 2;

    String getInterfaceName() throws RemoteException;

    boolean registerCallback(IApInterfaceEventCallback iApInterfaceEventCallback) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IApInterface {
        @Override // android.net.wifi.nl80211.IApInterface
        public boolean registerCallback(IApInterfaceEventCallback callback) throws RemoteException {
            return false;
        }

        @Override // android.net.wifi.nl80211.IApInterface
        public String getInterfaceName() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IApInterface {
        static final int TRANSACTION_getInterfaceName = 2;
        static final int TRANSACTION_registerCallback = 1;

        public Stub() {
            attachInterface(this, IApInterface.DESCRIPTOR);
        }

        public static IApInterface asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IApInterface.DESCRIPTOR);
            if (iin != null && (iin instanceof IApInterface)) {
                return (IApInterface) iin;
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
                    return "registerCallback";
                case 2:
                    return "getInterfaceName";
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
                data.enforceInterface(IApInterface.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IApInterface.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IApInterfaceEventCallback _arg0 = IApInterfaceEventCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result = registerCallback(_arg0);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            String _result2 = getInterfaceName();
                            reply.writeNoException();
                            reply.writeString(_result2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IApInterface {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IApInterface.DESCRIPTOR;
            }

            @Override // android.net.wifi.nl80211.IApInterface
            public boolean registerCallback(IApInterfaceEventCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IApInterface.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IApInterface
            public String getInterfaceName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IApInterface.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
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
