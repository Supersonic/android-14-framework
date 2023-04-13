package android.telephony.data;

import android.net.LinkProperties;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.data.IDataServiceCallback;
import java.util.List;
/* loaded from: classes3.dex */
public interface IDataService extends IInterface {
    void cancelHandover(int i, int i2, IDataServiceCallback iDataServiceCallback) throws RemoteException;

    void createDataServiceProvider(int i) throws RemoteException;

    void deactivateDataCall(int i, int i2, int i3, IDataServiceCallback iDataServiceCallback) throws RemoteException;

    void registerForDataCallListChanged(int i, IDataServiceCallback iDataServiceCallback) throws RemoteException;

    void registerForUnthrottleApn(int i, IDataServiceCallback iDataServiceCallback) throws RemoteException;

    void removeDataServiceProvider(int i) throws RemoteException;

    void requestDataCallList(int i, IDataServiceCallback iDataServiceCallback) throws RemoteException;

    void setDataProfile(int i, List<DataProfile> list, boolean z, IDataServiceCallback iDataServiceCallback) throws RemoteException;

    void setInitialAttachApn(int i, DataProfile dataProfile, boolean z, IDataServiceCallback iDataServiceCallback) throws RemoteException;

    void setupDataCall(int i, int i2, DataProfile dataProfile, boolean z, boolean z2, int i3, LinkProperties linkProperties, int i4, NetworkSliceInfo networkSliceInfo, TrafficDescriptor trafficDescriptor, boolean z3, IDataServiceCallback iDataServiceCallback) throws RemoteException;

    void startHandover(int i, int i2, IDataServiceCallback iDataServiceCallback) throws RemoteException;

    void unregisterForDataCallListChanged(int i, IDataServiceCallback iDataServiceCallback) throws RemoteException;

    void unregisterForUnthrottleApn(int i, IDataServiceCallback iDataServiceCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IDataService {
        @Override // android.telephony.data.IDataService
        public void createDataServiceProvider(int slotId) throws RemoteException {
        }

        @Override // android.telephony.data.IDataService
        public void removeDataServiceProvider(int slotId) throws RemoteException {
        }

        @Override // android.telephony.data.IDataService
        public void setupDataCall(int slotId, int accessNetwork, DataProfile dataProfile, boolean isRoaming, boolean allowRoaming, int reason, LinkProperties linkProperties, int pduSessionId, NetworkSliceInfo sliceInfo, TrafficDescriptor trafficDescriptor, boolean matchAllRuleAllowed, IDataServiceCallback callback) throws RemoteException {
        }

        @Override // android.telephony.data.IDataService
        public void deactivateDataCall(int slotId, int cid, int reason, IDataServiceCallback callback) throws RemoteException {
        }

        @Override // android.telephony.data.IDataService
        public void setInitialAttachApn(int slotId, DataProfile dataProfile, boolean isRoaming, IDataServiceCallback callback) throws RemoteException {
        }

        @Override // android.telephony.data.IDataService
        public void setDataProfile(int slotId, List<DataProfile> dps, boolean isRoaming, IDataServiceCallback callback) throws RemoteException {
        }

        @Override // android.telephony.data.IDataService
        public void requestDataCallList(int slotId, IDataServiceCallback callback) throws RemoteException {
        }

        @Override // android.telephony.data.IDataService
        public void registerForDataCallListChanged(int slotId, IDataServiceCallback callback) throws RemoteException {
        }

        @Override // android.telephony.data.IDataService
        public void unregisterForDataCallListChanged(int slotId, IDataServiceCallback callback) throws RemoteException {
        }

        @Override // android.telephony.data.IDataService
        public void startHandover(int slotId, int cid, IDataServiceCallback callback) throws RemoteException {
        }

        @Override // android.telephony.data.IDataService
        public void cancelHandover(int slotId, int cid, IDataServiceCallback callback) throws RemoteException {
        }

        @Override // android.telephony.data.IDataService
        public void registerForUnthrottleApn(int slotIndex, IDataServiceCallback callback) throws RemoteException {
        }

        @Override // android.telephony.data.IDataService
        public void unregisterForUnthrottleApn(int slotIndex, IDataServiceCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IDataService {
        public static final String DESCRIPTOR = "android.telephony.data.IDataService";
        static final int TRANSACTION_cancelHandover = 11;
        static final int TRANSACTION_createDataServiceProvider = 1;
        static final int TRANSACTION_deactivateDataCall = 4;
        static final int TRANSACTION_registerForDataCallListChanged = 8;
        static final int TRANSACTION_registerForUnthrottleApn = 12;
        static final int TRANSACTION_removeDataServiceProvider = 2;
        static final int TRANSACTION_requestDataCallList = 7;
        static final int TRANSACTION_setDataProfile = 6;
        static final int TRANSACTION_setInitialAttachApn = 5;
        static final int TRANSACTION_setupDataCall = 3;
        static final int TRANSACTION_startHandover = 10;
        static final int TRANSACTION_unregisterForDataCallListChanged = 9;
        static final int TRANSACTION_unregisterForUnthrottleApn = 13;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDataService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IDataService)) {
                return (IDataService) iin;
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
                    return "createDataServiceProvider";
                case 2:
                    return "removeDataServiceProvider";
                case 3:
                    return "setupDataCall";
                case 4:
                    return "deactivateDataCall";
                case 5:
                    return "setInitialAttachApn";
                case 6:
                    return "setDataProfile";
                case 7:
                    return "requestDataCallList";
                case 8:
                    return "registerForDataCallListChanged";
                case 9:
                    return "unregisterForDataCallListChanged";
                case 10:
                    return "startHandover";
                case 11:
                    return "cancelHandover";
                case 12:
                    return "registerForUnthrottleApn";
                case 13:
                    return "unregisterForUnthrottleApn";
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
                            data.enforceNoDataAvail();
                            createDataServiceProvider(_arg0);
                            return true;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            removeDataServiceProvider(_arg02);
                            return true;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg1 = data.readInt();
                            DataProfile _arg2 = (DataProfile) data.readTypedObject(DataProfile.CREATOR);
                            boolean _arg3 = data.readBoolean();
                            boolean _arg4 = data.readBoolean();
                            int _arg5 = data.readInt();
                            LinkProperties _arg6 = (LinkProperties) data.readTypedObject(LinkProperties.CREATOR);
                            int _arg7 = data.readInt();
                            NetworkSliceInfo _arg8 = (NetworkSliceInfo) data.readTypedObject(NetworkSliceInfo.CREATOR);
                            TrafficDescriptor _arg9 = (TrafficDescriptor) data.readTypedObject(TrafficDescriptor.CREATOR);
                            boolean _arg10 = data.readBoolean();
                            IDataServiceCallback _arg11 = IDataServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setupDataCall(_arg03, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8, _arg9, _arg10, _arg11);
                            return true;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg12 = data.readInt();
                            int _arg22 = data.readInt();
                            IDataServiceCallback _arg32 = IDataServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            deactivateDataCall(_arg04, _arg12, _arg22, _arg32);
                            return true;
                        case 5:
                            int _arg05 = data.readInt();
                            DataProfile _arg13 = (DataProfile) data.readTypedObject(DataProfile.CREATOR);
                            boolean _arg23 = data.readBoolean();
                            IDataServiceCallback _arg33 = IDataServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setInitialAttachApn(_arg05, _arg13, _arg23, _arg33);
                            return true;
                        case 6:
                            int _arg06 = data.readInt();
                            List<DataProfile> _arg14 = data.createTypedArrayList(DataProfile.CREATOR);
                            boolean _arg24 = data.readBoolean();
                            IDataServiceCallback _arg34 = IDataServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setDataProfile(_arg06, _arg14, _arg24, _arg34);
                            return true;
                        case 7:
                            int _arg07 = data.readInt();
                            IDataServiceCallback _arg15 = IDataServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            requestDataCallList(_arg07, _arg15);
                            return true;
                        case 8:
                            int _arg08 = data.readInt();
                            IDataServiceCallback _arg16 = IDataServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerForDataCallListChanged(_arg08, _arg16);
                            return true;
                        case 9:
                            int _arg09 = data.readInt();
                            IDataServiceCallback _arg17 = IDataServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterForDataCallListChanged(_arg09, _arg17);
                            return true;
                        case 10:
                            int _arg010 = data.readInt();
                            int _arg18 = data.readInt();
                            IDataServiceCallback _arg25 = IDataServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startHandover(_arg010, _arg18, _arg25);
                            return true;
                        case 11:
                            int _arg011 = data.readInt();
                            int _arg19 = data.readInt();
                            IDataServiceCallback _arg26 = IDataServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            cancelHandover(_arg011, _arg19, _arg26);
                            return true;
                        case 12:
                            int _arg012 = data.readInt();
                            IDataServiceCallback _arg110 = IDataServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerForUnthrottleApn(_arg012, _arg110);
                            return true;
                        case 13:
                            int _arg013 = data.readInt();
                            IDataServiceCallback _arg111 = IDataServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterForUnthrottleApn(_arg013, _arg111);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IDataService {
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

            @Override // android.telephony.data.IDataService
            public void createDataServiceProvider(int slotId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataService
            public void removeDataServiceProvider(int slotId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataService
            public void setupDataCall(int slotId, int accessNetwork, DataProfile dataProfile, boolean isRoaming, boolean allowRoaming, int reason, LinkProperties linkProperties, int pduSessionId, NetworkSliceInfo sliceInfo, TrafficDescriptor trafficDescriptor, boolean matchAllRuleAllowed, IDataServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(accessNetwork);
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    _data.writeTypedObject(dataProfile, 0);
                    try {
                        _data.writeBoolean(isRoaming);
                        try {
                            _data.writeBoolean(allowRoaming);
                            try {
                                _data.writeInt(reason);
                            } catch (Throwable th2) {
                                th = th2;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeTypedObject(linkProperties, 0);
                        try {
                            _data.writeInt(pduSessionId);
                            try {
                                _data.writeTypedObject(sliceInfo, 0);
                                try {
                                    _data.writeTypedObject(trafficDescriptor, 0);
                                } catch (Throwable th5) {
                                    th = th5;
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th6) {
                                th = th6;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeBoolean(matchAllRuleAllowed);
                            try {
                                _data.writeStrongInterface(callback);
                                try {
                                    this.mRemote.transact(3, _data, null, 1);
                                    _data.recycle();
                                } catch (Throwable th8) {
                                    th = th8;
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th9) {
                                th = th9;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.telephony.data.IDataService
            public void deactivateDataCall(int slotId, int cid, int reason, IDataServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(cid);
                    _data.writeInt(reason);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataService
            public void setInitialAttachApn(int slotId, DataProfile dataProfile, boolean isRoaming, IDataServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeTypedObject(dataProfile, 0);
                    _data.writeBoolean(isRoaming);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataService
            public void setDataProfile(int slotId, List<DataProfile> dps, boolean isRoaming, IDataServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeTypedList(dps, 0);
                    _data.writeBoolean(isRoaming);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataService
            public void requestDataCallList(int slotId, IDataServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataService
            public void registerForDataCallListChanged(int slotId, IDataServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataService
            public void unregisterForDataCallListChanged(int slotId, IDataServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataService
            public void startHandover(int slotId, int cid, IDataServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(cid);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataService
            public void cancelHandover(int slotId, int cid, IDataServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(cid);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataService
            public void registerForUnthrottleApn(int slotIndex, IDataServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotIndex);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.data.IDataService
            public void unregisterForUnthrottleApn(int slotIndex, IDataServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotIndex);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 12;
        }
    }
}
