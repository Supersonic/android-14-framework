package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IRcsConfigCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IRcsConfigCallback";

    void onAutoConfigurationErrorReceived(int i, String str) throws RemoteException;

    void onConfigurationChanged(byte[] bArr) throws RemoteException;

    void onConfigurationReset() throws RemoteException;

    void onPreProvisioningReceived(byte[] bArr) throws RemoteException;

    void onRemoved() throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IRcsConfigCallback {
        @Override // android.telephony.ims.aidl.IRcsConfigCallback
        public void onConfigurationChanged(byte[] config) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IRcsConfigCallback
        public void onAutoConfigurationErrorReceived(int errorCode, String errorString) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IRcsConfigCallback
        public void onConfigurationReset() throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IRcsConfigCallback
        public void onRemoved() throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IRcsConfigCallback
        public void onPreProvisioningReceived(byte[] config) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IRcsConfigCallback {
        static final int TRANSACTION_onAutoConfigurationErrorReceived = 2;
        static final int TRANSACTION_onConfigurationChanged = 1;
        static final int TRANSACTION_onConfigurationReset = 3;
        static final int TRANSACTION_onPreProvisioningReceived = 5;
        static final int TRANSACTION_onRemoved = 4;

        public Stub() {
            attachInterface(this, IRcsConfigCallback.DESCRIPTOR);
        }

        public static IRcsConfigCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IRcsConfigCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IRcsConfigCallback)) {
                return (IRcsConfigCallback) iin;
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
                    return "onConfigurationChanged";
                case 2:
                    return "onAutoConfigurationErrorReceived";
                case 3:
                    return "onConfigurationReset";
                case 4:
                    return "onRemoved";
                case 5:
                    return "onPreProvisioningReceived";
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
                data.enforceInterface(IRcsConfigCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IRcsConfigCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            byte[] _arg0 = data.createByteArray();
                            data.enforceNoDataAvail();
                            onConfigurationChanged(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            onAutoConfigurationErrorReceived(_arg02, _arg1);
                            break;
                        case 3:
                            onConfigurationReset();
                            break;
                        case 4:
                            onRemoved();
                            break;
                        case 5:
                            byte[] _arg03 = data.createByteArray();
                            data.enforceNoDataAvail();
                            onPreProvisioningReceived(_arg03);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IRcsConfigCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IRcsConfigCallback.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IRcsConfigCallback
            public void onConfigurationChanged(byte[] config) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRcsConfigCallback.DESCRIPTOR);
                    _data.writeByteArray(config);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IRcsConfigCallback
            public void onAutoConfigurationErrorReceived(int errorCode, String errorString) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRcsConfigCallback.DESCRIPTOR);
                    _data.writeInt(errorCode);
                    _data.writeString(errorString);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IRcsConfigCallback
            public void onConfigurationReset() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRcsConfigCallback.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IRcsConfigCallback
            public void onRemoved() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRcsConfigCallback.DESCRIPTOR);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IRcsConfigCallback
            public void onPreProvisioningReceived(byte[] config) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRcsConfigCallback.DESCRIPTOR);
                    _data.writeByteArray(config);
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
