package com.android.ims.internal;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.ims.ImsConfigListener;
/* loaded from: classes4.dex */
public interface IImsConfig extends IInterface {
    void getFeatureValue(int i, int i2, ImsConfigListener imsConfigListener) throws RemoteException;

    String getProvisionedStringValue(int i) throws RemoteException;

    int getProvisionedValue(int i) throws RemoteException;

    void getVideoQuality(ImsConfigListener imsConfigListener) throws RemoteException;

    boolean getVolteProvisioned() throws RemoteException;

    void setFeatureValue(int i, int i2, int i3, ImsConfigListener imsConfigListener) throws RemoteException;

    int setProvisionedStringValue(int i, String str) throws RemoteException;

    int setProvisionedValue(int i, int i2) throws RemoteException;

    void setVideoQuality(int i, ImsConfigListener imsConfigListener) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IImsConfig {
        @Override // com.android.ims.internal.IImsConfig
        public int getProvisionedValue(int item) throws RemoteException {
            return 0;
        }

        @Override // com.android.ims.internal.IImsConfig
        public String getProvisionedStringValue(int item) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.IImsConfig
        public int setProvisionedValue(int item, int value) throws RemoteException {
            return 0;
        }

        @Override // com.android.ims.internal.IImsConfig
        public int setProvisionedStringValue(int item, String value) throws RemoteException {
            return 0;
        }

        @Override // com.android.ims.internal.IImsConfig
        public void getFeatureValue(int feature, int network, ImsConfigListener listener) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsConfig
        public void setFeatureValue(int feature, int network, int value, ImsConfigListener listener) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsConfig
        public boolean getVolteProvisioned() throws RemoteException {
            return false;
        }

        @Override // com.android.ims.internal.IImsConfig
        public void getVideoQuality(ImsConfigListener listener) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsConfig
        public void setVideoQuality(int quality, ImsConfigListener listener) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IImsConfig {
        public static final String DESCRIPTOR = "com.android.ims.internal.IImsConfig";
        static final int TRANSACTION_getFeatureValue = 5;
        static final int TRANSACTION_getProvisionedStringValue = 2;
        static final int TRANSACTION_getProvisionedValue = 1;
        static final int TRANSACTION_getVideoQuality = 8;
        static final int TRANSACTION_getVolteProvisioned = 7;
        static final int TRANSACTION_setFeatureValue = 6;
        static final int TRANSACTION_setProvisionedStringValue = 4;
        static final int TRANSACTION_setProvisionedValue = 3;
        static final int TRANSACTION_setVideoQuality = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IImsConfig asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IImsConfig)) {
                return (IImsConfig) iin;
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
                    return "getProvisionedValue";
                case 2:
                    return "getProvisionedStringValue";
                case 3:
                    return "setProvisionedValue";
                case 4:
                    return "setProvisionedStringValue";
                case 5:
                    return "getFeatureValue";
                case 6:
                    return "setFeatureValue";
                case 7:
                    return "getVolteProvisioned";
                case 8:
                    return "getVideoQuality";
                case 9:
                    return "setVideoQuality";
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
                            int _result = getProvisionedValue(_arg0);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result2 = getProvisionedStringValue(_arg02);
                            reply.writeNoException();
                            reply.writeString(_result2);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result3 = setProvisionedValue(_arg03, _arg1);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            int _result4 = setProvisionedStringValue(_arg04, _arg12);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            int _arg13 = data.readInt();
                            ImsConfigListener _arg2 = ImsConfigListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getFeatureValue(_arg05, _arg13, _arg2);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            int _arg14 = data.readInt();
                            int _arg22 = data.readInt();
                            ImsConfigListener _arg3 = ImsConfigListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setFeatureValue(_arg06, _arg14, _arg22, _arg3);
                            break;
                        case 7:
                            boolean _result5 = getVolteProvisioned();
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 8:
                            ImsConfigListener _arg07 = ImsConfigListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getVideoQuality(_arg07);
                            break;
                        case 9:
                            int _arg08 = data.readInt();
                            ImsConfigListener _arg15 = ImsConfigListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setVideoQuality(_arg08, _arg15);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IImsConfig {
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

            @Override // com.android.ims.internal.IImsConfig
            public int getProvisionedValue(int item) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(item);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsConfig
            public String getProvisionedStringValue(int item) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(item);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsConfig
            public int setProvisionedValue(int item, int value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(item);
                    _data.writeInt(value);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsConfig
            public int setProvisionedStringValue(int item, String value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(item);
                    _data.writeString(value);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsConfig
            public void getFeatureValue(int feature, int network, ImsConfigListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(feature);
                    _data.writeInt(network);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsConfig
            public void setFeatureValue(int feature, int network, int value, ImsConfigListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(feature);
                    _data.writeInt(network);
                    _data.writeInt(value);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsConfig
            public boolean getVolteProvisioned() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsConfig
            public void getVideoQuality(ImsConfigListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsConfig
            public void setVideoQuality(int quality, ImsConfigListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(quality);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
