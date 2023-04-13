package android.service.carrier;

import android.net.Uri;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.carrier.ICarrierMessagingCallback;
import java.util.List;
/* loaded from: classes3.dex */
public interface ICarrierMessagingService extends IInterface {
    void downloadMms(Uri uri, int i, Uri uri2, ICarrierMessagingCallback iCarrierMessagingCallback) throws RemoteException;

    void filterSms(MessagePdu messagePdu, String str, int i, int i2, ICarrierMessagingCallback iCarrierMessagingCallback) throws RemoteException;

    void sendDataSms(byte[] bArr, int i, String str, int i2, int i3, ICarrierMessagingCallback iCarrierMessagingCallback) throws RemoteException;

    void sendMms(Uri uri, int i, Uri uri2, ICarrierMessagingCallback iCarrierMessagingCallback) throws RemoteException;

    void sendMultipartTextSms(List<String> list, int i, String str, int i2, ICarrierMessagingCallback iCarrierMessagingCallback) throws RemoteException;

    void sendTextSms(String str, int i, String str2, int i2, ICarrierMessagingCallback iCarrierMessagingCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ICarrierMessagingService {
        @Override // android.service.carrier.ICarrierMessagingService
        public void filterSms(MessagePdu pdu, String format, int destPort, int subId, ICarrierMessagingCallback callback) throws RemoteException {
        }

        @Override // android.service.carrier.ICarrierMessagingService
        public void sendTextSms(String text, int subId, String destAddress, int sendSmsFlag, ICarrierMessagingCallback callback) throws RemoteException {
        }

        @Override // android.service.carrier.ICarrierMessagingService
        public void sendDataSms(byte[] data, int subId, String destAddress, int destPort, int sendSmsFlag, ICarrierMessagingCallback callback) throws RemoteException {
        }

        @Override // android.service.carrier.ICarrierMessagingService
        public void sendMultipartTextSms(List<String> parts, int subId, String destAddress, int sendSmsFlag, ICarrierMessagingCallback callback) throws RemoteException {
        }

        @Override // android.service.carrier.ICarrierMessagingService
        public void sendMms(Uri pduUri, int subId, Uri location, ICarrierMessagingCallback callback) throws RemoteException {
        }

        @Override // android.service.carrier.ICarrierMessagingService
        public void downloadMms(Uri pduUri, int subId, Uri location, ICarrierMessagingCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ICarrierMessagingService {
        public static final String DESCRIPTOR = "android.service.carrier.ICarrierMessagingService";
        static final int TRANSACTION_downloadMms = 6;
        static final int TRANSACTION_filterSms = 1;
        static final int TRANSACTION_sendDataSms = 3;
        static final int TRANSACTION_sendMms = 5;
        static final int TRANSACTION_sendMultipartTextSms = 4;
        static final int TRANSACTION_sendTextSms = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICarrierMessagingService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ICarrierMessagingService)) {
                return (ICarrierMessagingService) iin;
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
                    return "filterSms";
                case 2:
                    return "sendTextSms";
                case 3:
                    return "sendDataSms";
                case 4:
                    return "sendMultipartTextSms";
                case 5:
                    return "sendMms";
                case 6:
                    return "downloadMms";
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
                            MessagePdu _arg0 = (MessagePdu) data.readTypedObject(MessagePdu.CREATOR);
                            String _arg1 = data.readString();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            ICarrierMessagingCallback _arg4 = ICarrierMessagingCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            filterSms(_arg0, _arg1, _arg2, _arg3, _arg4);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            int _arg12 = data.readInt();
                            String _arg22 = data.readString();
                            int _arg32 = data.readInt();
                            ICarrierMessagingCallback _arg42 = ICarrierMessagingCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            sendTextSms(_arg02, _arg12, _arg22, _arg32, _arg42);
                            break;
                        case 3:
                            byte[] _arg03 = data.createByteArray();
                            int _arg13 = data.readInt();
                            String _arg23 = data.readString();
                            int _arg33 = data.readInt();
                            int _arg43 = data.readInt();
                            ICarrierMessagingCallback _arg5 = ICarrierMessagingCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            sendDataSms(_arg03, _arg13, _arg23, _arg33, _arg43, _arg5);
                            break;
                        case 4:
                            List<String> _arg04 = data.createStringArrayList();
                            int _arg14 = data.readInt();
                            String _arg24 = data.readString();
                            int _arg34 = data.readInt();
                            ICarrierMessagingCallback _arg44 = ICarrierMessagingCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            sendMultipartTextSms(_arg04, _arg14, _arg24, _arg34, _arg44);
                            break;
                        case 5:
                            Uri _arg05 = (Uri) data.readTypedObject(Uri.CREATOR);
                            int _arg15 = data.readInt();
                            Uri _arg25 = (Uri) data.readTypedObject(Uri.CREATOR);
                            ICarrierMessagingCallback _arg35 = ICarrierMessagingCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            sendMms(_arg05, _arg15, _arg25, _arg35);
                            break;
                        case 6:
                            Uri _arg06 = (Uri) data.readTypedObject(Uri.CREATOR);
                            int _arg16 = data.readInt();
                            Uri _arg26 = (Uri) data.readTypedObject(Uri.CREATOR);
                            ICarrierMessagingCallback _arg36 = ICarrierMessagingCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            downloadMms(_arg06, _arg16, _arg26, _arg36);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ICarrierMessagingService {
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

            @Override // android.service.carrier.ICarrierMessagingService
            public void filterSms(MessagePdu pdu, String format, int destPort, int subId, ICarrierMessagingCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(pdu, 0);
                    _data.writeString(format);
                    _data.writeInt(destPort);
                    _data.writeInt(subId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.carrier.ICarrierMessagingService
            public void sendTextSms(String text, int subId, String destAddress, int sendSmsFlag, ICarrierMessagingCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(text);
                    _data.writeInt(subId);
                    _data.writeString(destAddress);
                    _data.writeInt(sendSmsFlag);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.carrier.ICarrierMessagingService
            public void sendDataSms(byte[] data, int subId, String destAddress, int destPort, int sendSmsFlag, ICarrierMessagingCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(data);
                    _data.writeInt(subId);
                    _data.writeString(destAddress);
                    _data.writeInt(destPort);
                    _data.writeInt(sendSmsFlag);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.carrier.ICarrierMessagingService
            public void sendMultipartTextSms(List<String> parts, int subId, String destAddress, int sendSmsFlag, ICarrierMessagingCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(parts);
                    _data.writeInt(subId);
                    _data.writeString(destAddress);
                    _data.writeInt(sendSmsFlag);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.carrier.ICarrierMessagingService
            public void sendMms(Uri pduUri, int subId, Uri location, ICarrierMessagingCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(pduUri, 0);
                    _data.writeInt(subId);
                    _data.writeTypedObject(location, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.carrier.ICarrierMessagingService
            public void downloadMms(Uri pduUri, int subId, Uri location, ICarrierMessagingCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(pduUri, 0);
                    _data.writeInt(subId);
                    _data.writeTypedObject(location, 0);
                    _data.writeStrongInterface(callback);
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
