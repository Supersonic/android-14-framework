package android.telephony.mbms.vendor;

import android.content.ContentResolver;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.mbms.IGroupCallCallback;
import android.telephony.mbms.IMbmsGroupCallSessionCallback;
import java.util.List;
/* loaded from: classes3.dex */
public interface IMbmsGroupCallService extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.mbms.vendor.IMbmsGroupCallService";

    void dispose(int i) throws RemoteException;

    int initialize(IMbmsGroupCallSessionCallback iMbmsGroupCallSessionCallback, int i) throws RemoteException;

    int startGroupCall(int i, long j, List list, List list2, IGroupCallCallback iGroupCallCallback) throws RemoteException;

    void stopGroupCall(int i, long j) throws RemoteException;

    void updateGroupCall(int i, long j, List list, List list2) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IMbmsGroupCallService {
        @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
        public int initialize(IMbmsGroupCallSessionCallback callback, int subId) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
        public void stopGroupCall(int subId, long tmgi) throws RemoteException {
        }

        @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
        public void updateGroupCall(int subscriptionId, long tmgi, List saiList, List frequencyList) throws RemoteException {
        }

        @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
        public int startGroupCall(int subscriptionId, long tmgi, List saiList, List frequencyList, IGroupCallCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
        public void dispose(int subId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IMbmsGroupCallService {
        static final int TRANSACTION_dispose = 5;
        static final int TRANSACTION_initialize = 1;
        static final int TRANSACTION_startGroupCall = 4;
        static final int TRANSACTION_stopGroupCall = 2;
        static final int TRANSACTION_updateGroupCall = 3;

        public Stub() {
            attachInterface(this, IMbmsGroupCallService.DESCRIPTOR);
        }

        public static IMbmsGroupCallService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IMbmsGroupCallService.DESCRIPTOR);
            if (iin != null && (iin instanceof IMbmsGroupCallService)) {
                return (IMbmsGroupCallService) iin;
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
                    return ContentResolver.SYNC_EXTRAS_INITIALIZE;
                case 2:
                    return "stopGroupCall";
                case 3:
                    return "updateGroupCall";
                case 4:
                    return "startGroupCall";
                case 5:
                    return "dispose";
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
                data.enforceInterface(IMbmsGroupCallService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IMbmsGroupCallService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IMbmsGroupCallSessionCallback _arg0 = IMbmsGroupCallSessionCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result = initialize(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            long _arg12 = data.readLong();
                            data.enforceNoDataAvail();
                            stopGroupCall(_arg02, _arg12);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            long _arg13 = data.readLong();
                            ClassLoader cl = getClass().getClassLoader();
                            List _arg2 = data.readArrayList(cl);
                            List _arg3 = data.readArrayList(cl);
                            data.enforceNoDataAvail();
                            updateGroupCall(_arg03, _arg13, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            long _arg14 = data.readLong();
                            ClassLoader cl2 = getClass().getClassLoader();
                            List _arg22 = data.readArrayList(cl2);
                            List _arg32 = data.readArrayList(cl2);
                            IGroupCallCallback _arg4 = IGroupCallCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result2 = startGroupCall(_arg04, _arg14, _arg22, _arg32, _arg4);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            dispose(_arg05);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IMbmsGroupCallService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMbmsGroupCallService.DESCRIPTOR;
            }

            @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
            public int initialize(IMbmsGroupCallSessionCallback callback, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMbmsGroupCallService.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(subId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
            public void stopGroupCall(int subId, long tmgi) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMbmsGroupCallService.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeLong(tmgi);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
            public void updateGroupCall(int subscriptionId, long tmgi, List saiList, List frequencyList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMbmsGroupCallService.DESCRIPTOR);
                    _data.writeInt(subscriptionId);
                    _data.writeLong(tmgi);
                    _data.writeList(saiList);
                    _data.writeList(frequencyList);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
            public int startGroupCall(int subscriptionId, long tmgi, List saiList, List frequencyList, IGroupCallCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMbmsGroupCallService.DESCRIPTOR);
                    _data.writeInt(subscriptionId);
                    _data.writeLong(tmgi);
                    _data.writeList(saiList);
                    _data.writeList(frequencyList);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.mbms.vendor.IMbmsGroupCallService
            public void dispose(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IMbmsGroupCallService.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
