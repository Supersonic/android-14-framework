package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IImsSmsListener extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IImsSmsListener";

    void onMemoryAvailableResult(int i, int i2, int i3) throws RemoteException;

    void onSendSmsResult(int i, int i2, int i3, int i4, int i5) throws RemoteException;

    void onSmsReceived(int i, String str, byte[] bArr) throws RemoteException;

    void onSmsStatusReportReceived(int i, String str, byte[] bArr) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IImsSmsListener {
        @Override // android.telephony.ims.aidl.IImsSmsListener
        public void onSendSmsResult(int token, int messageRef, int status, int reason, int networkErrorCode) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsSmsListener
        public void onSmsStatusReportReceived(int token, String format, byte[] pdu) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsSmsListener
        public void onSmsReceived(int token, String format, byte[] pdu) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsSmsListener
        public void onMemoryAvailableResult(int token, int status, int networkErrorCode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IImsSmsListener {
        static final int TRANSACTION_onMemoryAvailableResult = 4;
        static final int TRANSACTION_onSendSmsResult = 1;
        static final int TRANSACTION_onSmsReceived = 3;
        static final int TRANSACTION_onSmsStatusReportReceived = 2;

        public Stub() {
            attachInterface(this, IImsSmsListener.DESCRIPTOR);
        }

        public static IImsSmsListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IImsSmsListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IImsSmsListener)) {
                return (IImsSmsListener) iin;
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
                    return "onSendSmsResult";
                case 2:
                    return "onSmsStatusReportReceived";
                case 3:
                    return "onSmsReceived";
                case 4:
                    return "onMemoryAvailableResult";
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
                data.enforceInterface(IImsSmsListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IImsSmsListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            onSendSmsResult(_arg0, _arg1, _arg2, _arg3, _arg4);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg12 = data.readString();
                            byte[] _arg22 = data.createByteArray();
                            data.enforceNoDataAvail();
                            onSmsStatusReportReceived(_arg02, _arg12, _arg22);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            String _arg13 = data.readString();
                            byte[] _arg23 = data.createByteArray();
                            data.enforceNoDataAvail();
                            onSmsReceived(_arg03, _arg13, _arg23);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg14 = data.readInt();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            onMemoryAvailableResult(_arg04, _arg14, _arg24);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IImsSmsListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IImsSmsListener.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IImsSmsListener
            public void onSendSmsResult(int token, int messageRef, int status, int reason, int networkErrorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsSmsListener.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeInt(messageRef);
                    _data.writeInt(status);
                    _data.writeInt(reason);
                    _data.writeInt(networkErrorCode);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsSmsListener
            public void onSmsStatusReportReceived(int token, String format, byte[] pdu) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsSmsListener.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeString(format);
                    _data.writeByteArray(pdu);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsSmsListener
            public void onSmsReceived(int token, String format, byte[] pdu) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsSmsListener.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeString(format);
                    _data.writeByteArray(pdu);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsSmsListener
            public void onMemoryAvailableResult(int token, int status, int networkErrorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsSmsListener.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeInt(status);
                    _data.writeInt(networkErrorCode);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
