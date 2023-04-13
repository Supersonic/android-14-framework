package com.android.ims.internal;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.ims.ImsFeatureContainer;
/* loaded from: classes4.dex */
public interface IImsServiceFeatureCallback extends IInterface {
    void imsFeatureCreated(ImsFeatureContainer imsFeatureContainer, int i) throws RemoteException;

    void imsFeatureRemoved(int i) throws RemoteException;

    void imsStatusChanged(int i, int i2) throws RemoteException;

    void updateCapabilities(long j) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IImsServiceFeatureCallback {
        @Override // com.android.ims.internal.IImsServiceFeatureCallback
        public void imsFeatureCreated(ImsFeatureContainer feature, int subId) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsServiceFeatureCallback
        public void imsFeatureRemoved(int reason) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsServiceFeatureCallback
        public void imsStatusChanged(int status, int subId) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsServiceFeatureCallback
        public void updateCapabilities(long capabilities) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IImsServiceFeatureCallback {
        public static final String DESCRIPTOR = "com.android.ims.internal.IImsServiceFeatureCallback";
        static final int TRANSACTION_imsFeatureCreated = 1;
        static final int TRANSACTION_imsFeatureRemoved = 2;
        static final int TRANSACTION_imsStatusChanged = 3;
        static final int TRANSACTION_updateCapabilities = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IImsServiceFeatureCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IImsServiceFeatureCallback)) {
                return (IImsServiceFeatureCallback) iin;
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
                    return "imsFeatureCreated";
                case 2:
                    return "imsFeatureRemoved";
                case 3:
                    return "imsStatusChanged";
                case 4:
                    return "updateCapabilities";
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
                            ImsFeatureContainer _arg0 = (ImsFeatureContainer) data.readTypedObject(ImsFeatureContainer.CREATOR);
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            imsFeatureCreated(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            imsFeatureRemoved(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            imsStatusChanged(_arg03, _arg12);
                            break;
                        case 4:
                            long _arg04 = data.readLong();
                            data.enforceNoDataAvail();
                            updateCapabilities(_arg04);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IImsServiceFeatureCallback {
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

            @Override // com.android.ims.internal.IImsServiceFeatureCallback
            public void imsFeatureCreated(ImsFeatureContainer feature, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(feature, 0);
                    _data.writeInt(subId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsServiceFeatureCallback
            public void imsFeatureRemoved(int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(reason);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsServiceFeatureCallback
            public void imsStatusChanged(int status, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    _data.writeInt(subId);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsServiceFeatureCallback
            public void updateCapabilities(long capabilities) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(capabilities);
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
