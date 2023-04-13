package com.android.internal.telephony;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface ICarrierConfigLoader extends IInterface {
    @Deprecated
    PersistableBundle getConfigForSubId(int i, String str) throws RemoteException;

    PersistableBundle getConfigForSubIdWithFeature(int i, String str, String str2) throws RemoteException;

    PersistableBundle getConfigSubsetForSubIdWithFeature(int i, String str, String str2, String[] strArr) throws RemoteException;

    String getDefaultCarrierServicePackageName() throws RemoteException;

    void notifyConfigChangedForSubId(int i) throws RemoteException;

    void overrideConfig(int i, PersistableBundle persistableBundle, boolean z) throws RemoteException;

    void updateConfigForPhoneId(int i, String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ICarrierConfigLoader {
        @Override // com.android.internal.telephony.ICarrierConfigLoader
        public PersistableBundle getConfigForSubId(int subId, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ICarrierConfigLoader
        public PersistableBundle getConfigForSubIdWithFeature(int subId, String callingPackage, String callingFeatureId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ICarrierConfigLoader
        public void overrideConfig(int subId, PersistableBundle overrides, boolean persistent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ICarrierConfigLoader
        public void notifyConfigChangedForSubId(int subId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ICarrierConfigLoader
        public void updateConfigForPhoneId(int phoneId, String simState) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ICarrierConfigLoader
        public String getDefaultCarrierServicePackageName() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ICarrierConfigLoader
        public PersistableBundle getConfigSubsetForSubIdWithFeature(int subId, String callingPackage, String callingFeatureId, String[] carrierConfigs) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ICarrierConfigLoader {
        public static final String DESCRIPTOR = "com.android.internal.telephony.ICarrierConfigLoader";
        static final int TRANSACTION_getConfigForSubId = 1;
        static final int TRANSACTION_getConfigForSubIdWithFeature = 2;
        static final int TRANSACTION_getConfigSubsetForSubIdWithFeature = 7;
        static final int TRANSACTION_getDefaultCarrierServicePackageName = 6;
        static final int TRANSACTION_notifyConfigChangedForSubId = 4;
        static final int TRANSACTION_overrideConfig = 3;
        static final int TRANSACTION_updateConfigForPhoneId = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICarrierConfigLoader asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ICarrierConfigLoader)) {
                return (ICarrierConfigLoader) iin;
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
                    return "getConfigForSubId";
                case 2:
                    return "getConfigForSubIdWithFeature";
                case 3:
                    return "overrideConfig";
                case 4:
                    return "notifyConfigChangedForSubId";
                case 5:
                    return "updateConfigForPhoneId";
                case 6:
                    return "getDefaultCarrierServicePackageName";
                case 7:
                    return "getConfigSubsetForSubIdWithFeature";
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
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            PersistableBundle _result = getConfigForSubId(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg12 = data.readString();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            PersistableBundle _result2 = getConfigForSubIdWithFeature(_arg02, _arg12, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            PersistableBundle _arg13 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
                            boolean _arg22 = data.readBoolean();
                            data.enforceNoDataAvail();
                            overrideConfig(_arg03, _arg13, _arg22);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyConfigChangedForSubId(_arg04);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            updateConfigForPhoneId(_arg05, _arg14);
                            reply.writeNoException();
                            break;
                        case 6:
                            String _result3 = getDefaultCarrierServicePackageName();
                            reply.writeNoException();
                            reply.writeString(_result3);
                            break;
                        case 7:
                            int _arg06 = data.readInt();
                            String _arg15 = data.readString();
                            String _arg23 = data.readString();
                            String[] _arg3 = data.createStringArray();
                            data.enforceNoDataAvail();
                            PersistableBundle _result4 = getConfigSubsetForSubIdWithFeature(_arg06, _arg15, _arg23, _arg3);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements ICarrierConfigLoader {
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

            @Override // com.android.internal.telephony.ICarrierConfigLoader
            public PersistableBundle getConfigForSubId(int subId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    PersistableBundle _result = (PersistableBundle) _reply.readTypedObject(PersistableBundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ICarrierConfigLoader
            public PersistableBundle getConfigForSubIdWithFeature(int subId, String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    PersistableBundle _result = (PersistableBundle) _reply.readTypedObject(PersistableBundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ICarrierConfigLoader
            public void overrideConfig(int subId, PersistableBundle overrides, boolean persistent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeTypedObject(overrides, 0);
                    _data.writeBoolean(persistent);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ICarrierConfigLoader
            public void notifyConfigChangedForSubId(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ICarrierConfigLoader
            public void updateConfigForPhoneId(int phoneId, String simState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(phoneId);
                    _data.writeString(simState);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ICarrierConfigLoader
            public String getDefaultCarrierServicePackageName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ICarrierConfigLoader
            public PersistableBundle getConfigSubsetForSubIdWithFeature(int subId, String callingPackage, String callingFeatureId, String[] carrierConfigs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    _data.writeStringArray(carrierConfigs);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    PersistableBundle _result = (PersistableBundle) _reply.readTypedObject(PersistableBundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
