package android.telephony;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.telephony.cdma.CdmaSmsCbProgramData;
import android.text.TextUtils;
import java.util.List;
/* loaded from: classes3.dex */
public interface ICellBroadcastService extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ICellBroadcastService";

    CharSequence getCellBroadcastAreaInfo(int i) throws RemoteException;

    void handleCdmaCellBroadcastSms(int i, byte[] bArr, int i2) throws RemoteException;

    void handleCdmaScpMessage(int i, List<CdmaSmsCbProgramData> list, String str, RemoteCallback remoteCallback) throws RemoteException;

    void handleGsmCellBroadcastSms(int i, byte[] bArr) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ICellBroadcastService {
        @Override // android.telephony.ICellBroadcastService
        public void handleGsmCellBroadcastSms(int slotId, byte[] message) throws RemoteException {
        }

        @Override // android.telephony.ICellBroadcastService
        public void handleCdmaCellBroadcastSms(int slotId, byte[] bearerData, int serviceCategory) throws RemoteException {
        }

        @Override // android.telephony.ICellBroadcastService
        public void handleCdmaScpMessage(int slotId, List<CdmaSmsCbProgramData> programData, String originatingAddress, RemoteCallback callback) throws RemoteException {
        }

        @Override // android.telephony.ICellBroadcastService
        public CharSequence getCellBroadcastAreaInfo(int slotIndex) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ICellBroadcastService {
        static final int TRANSACTION_getCellBroadcastAreaInfo = 4;
        static final int TRANSACTION_handleCdmaCellBroadcastSms = 2;
        static final int TRANSACTION_handleCdmaScpMessage = 3;
        static final int TRANSACTION_handleGsmCellBroadcastSms = 1;

        public Stub() {
            attachInterface(this, ICellBroadcastService.DESCRIPTOR);
        }

        public static ICellBroadcastService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICellBroadcastService.DESCRIPTOR);
            if (iin != null && (iin instanceof ICellBroadcastService)) {
                return (ICellBroadcastService) iin;
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
                    return "handleGsmCellBroadcastSms";
                case 2:
                    return "handleCdmaCellBroadcastSms";
                case 3:
                    return "handleCdmaScpMessage";
                case 4:
                    return "getCellBroadcastAreaInfo";
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
                data.enforceInterface(ICellBroadcastService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICellBroadcastService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            byte[] _arg1 = data.createByteArray();
                            data.enforceNoDataAvail();
                            handleGsmCellBroadcastSms(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            byte[] _arg12 = data.createByteArray();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            handleCdmaCellBroadcastSms(_arg02, _arg12, _arg2);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            List<CdmaSmsCbProgramData> _arg13 = data.createTypedArrayList(CdmaSmsCbProgramData.CREATOR);
                            String _arg22 = data.readString();
                            RemoteCallback _arg3 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            handleCdmaScpMessage(_arg03, _arg13, _arg22, _arg3);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            CharSequence _result = getCellBroadcastAreaInfo(_arg04);
                            reply.writeNoException();
                            if (_result != null) {
                                reply.writeInt(1);
                                TextUtils.writeToParcel(_result, reply, 1);
                                break;
                            } else {
                                reply.writeInt(0);
                                break;
                            }
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ICellBroadcastService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICellBroadcastService.DESCRIPTOR;
            }

            @Override // android.telephony.ICellBroadcastService
            public void handleGsmCellBroadcastSms(int slotId, byte[] message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICellBroadcastService.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeByteArray(message);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ICellBroadcastService
            public void handleCdmaCellBroadcastSms(int slotId, byte[] bearerData, int serviceCategory) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICellBroadcastService.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeByteArray(bearerData);
                    _data.writeInt(serviceCategory);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ICellBroadcastService
            public void handleCdmaScpMessage(int slotId, List<CdmaSmsCbProgramData> programData, String originatingAddress, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICellBroadcastService.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeTypedList(programData, 0);
                    _data.writeString(originatingAddress);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ICellBroadcastService
            public CharSequence getCellBroadcastAreaInfo(int slotIndex) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICellBroadcastService.DESCRIPTOR);
                    _data.writeInt(slotIndex);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    CharSequence _result = (CharSequence) _reply.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
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
