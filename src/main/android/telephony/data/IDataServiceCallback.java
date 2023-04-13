package android.telephony.data;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes3.dex */
public interface IDataServiceCallback extends IInterface {
    void onApnUnthrottled(String str) throws RemoteException;

    void onDataCallListChanged(List<DataCallResponse> list) throws RemoteException;

    void onDataProfileUnthrottled(DataProfile dataProfile) throws RemoteException;

    void onDeactivateDataCallComplete(int i) throws RemoteException;

    void onHandoverCancelled(int i) throws RemoteException;

    void onHandoverStarted(int i) throws RemoteException;

    void onRequestDataCallListComplete(int i, List<DataCallResponse> list) throws RemoteException;

    void onSetDataProfileComplete(int i) throws RemoteException;

    void onSetInitialAttachApnComplete(int i) throws RemoteException;

    void onSetupDataCallComplete(int i, DataCallResponse dataCallResponse) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IDataServiceCallback {
        @Override // android.telephony.data.IDataServiceCallback
        public void onSetupDataCallComplete(int result, DataCallResponse dataCallResponse) throws RemoteException {
        }

        @Override // android.telephony.data.IDataServiceCallback
        public void onDeactivateDataCallComplete(int result) throws RemoteException {
        }

        @Override // android.telephony.data.IDataServiceCallback
        public void onSetInitialAttachApnComplete(int result) throws RemoteException {
        }

        @Override // android.telephony.data.IDataServiceCallback
        public void onSetDataProfileComplete(int result) throws RemoteException {
        }

        @Override // android.telephony.data.IDataServiceCallback
        public void onRequestDataCallListComplete(int result, List<DataCallResponse> dataCallList) throws RemoteException {
        }

        @Override // android.telephony.data.IDataServiceCallback
        public void onDataCallListChanged(List<DataCallResponse> dataCallList) throws RemoteException {
        }

        @Override // android.telephony.data.IDataServiceCallback
        public void onHandoverStarted(int result) throws RemoteException {
        }

        @Override // android.telephony.data.IDataServiceCallback
        public void onHandoverCancelled(int result) throws RemoteException {
        }

        @Override // android.telephony.data.IDataServiceCallback
        public void onApnUnthrottled(String apn) throws RemoteException {
        }

        @Override // android.telephony.data.IDataServiceCallback
        public void onDataProfileUnthrottled(DataProfile dp) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IDataServiceCallback {
        public static final String DESCRIPTOR = "android.telephony.data.IDataServiceCallback";
        static final int TRANSACTION_onApnUnthrottled = 9;
        static final int TRANSACTION_onDataCallListChanged = 6;
        static final int TRANSACTION_onDataProfileUnthrottled = 10;
        static final int TRANSACTION_onDeactivateDataCallComplete = 2;
        static final int TRANSACTION_onHandoverCancelled = 8;
        static final int TRANSACTION_onHandoverStarted = 7;
        static final int TRANSACTION_onRequestDataCallListComplete = 5;
        static final int TRANSACTION_onSetDataProfileComplete = 4;
        static final int TRANSACTION_onSetInitialAttachApnComplete = 3;
        static final int TRANSACTION_onSetupDataCallComplete = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDataServiceCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IDataServiceCallback)) {
                return (IDataServiceCallback) iin;
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
                    return "onSetupDataCallComplete";
                case 2:
                    return "onDeactivateDataCallComplete";
                case 3:
                    return "onSetInitialAttachApnComplete";
                case 4:
                    return "onSetDataProfileComplete";
                case 5:
                    return "onRequestDataCallListComplete";
                case 6:
                    return "onDataCallListChanged";
                case 7:
                    return "onHandoverStarted";
                case 8:
                    return "onHandoverCancelled";
                case 9:
                    return "onApnUnthrottled";
                case 10:
                    return "onDataProfileUnthrottled";
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
                            int _arg0 = data.readInt();
                            DataCallResponse _arg1 = (DataCallResponse) data.readTypedObject(DataCallResponse.CREATOR);
                            data.enforceNoDataAvail();
                            onSetupDataCallComplete(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onDeactivateDataCallComplete(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onSetInitialAttachApnComplete(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            onSetDataProfileComplete(_arg04);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            List<DataCallResponse> _arg12 = data.createTypedArrayList(DataCallResponse.CREATOR);
                            data.enforceNoDataAvail();
                            onRequestDataCallListComplete(_arg05, _arg12);
                            break;
                        case 6:
                            List<DataCallResponse> _arg06 = data.createTypedArrayList(DataCallResponse.CREATOR);
                            data.enforceNoDataAvail();
                            onDataCallListChanged(_arg06);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            onHandoverStarted(_arg07);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            onHandoverCancelled(_arg08);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            onApnUnthrottled(_arg09);
                            break;
                        case 10:
                            DataProfile _arg010 = (DataProfile) data.readTypedObject(DataProfile.CREATOR);
                            data.enforceNoDataAvail();
                            onDataProfileUnthrottled(_arg010);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IDataServiceCallback {
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

            @Override // android.telephony.data.IDataServiceCallback
            public void onSetupDataCallComplete(int result, DataCallResponse dataCallResponse) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(result);
                    _data.writeTypedObject(dataCallResponse, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataServiceCallback
            public void onDeactivateDataCallComplete(int result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(result);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataServiceCallback
            public void onSetInitialAttachApnComplete(int result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(result);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataServiceCallback
            public void onSetDataProfileComplete(int result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(result);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataServiceCallback
            public void onRequestDataCallListComplete(int result, List<DataCallResponse> dataCallList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(result);
                    _data.writeTypedList(dataCallList, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataServiceCallback
            public void onDataCallListChanged(List<DataCallResponse> dataCallList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(dataCallList, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataServiceCallback
            public void onHandoverStarted(int result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(result);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataServiceCallback
            public void onHandoverCancelled(int result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(result);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataServiceCallback
            public void onApnUnthrottled(String apn) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(apn);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataServiceCallback
            public void onDataProfileUnthrottled(DataProfile dp) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(dp, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
