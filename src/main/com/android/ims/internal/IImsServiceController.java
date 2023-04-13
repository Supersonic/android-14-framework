package com.android.ims.internal;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.ims.internal.IImsFeatureStatusCallback;
import com.android.ims.internal.IImsMMTelFeature;
import com.android.ims.internal.IImsRcsFeature;
/* loaded from: classes4.dex */
public interface IImsServiceController extends IInterface {
    void addFeatureStatusCallback(int i, int i2, IImsFeatureStatusCallback iImsFeatureStatusCallback) throws RemoteException;

    IImsMMTelFeature createEmergencyMMTelFeature(int i) throws RemoteException;

    IImsMMTelFeature createMMTelFeature(int i) throws RemoteException;

    IImsRcsFeature createRcsFeature(int i) throws RemoteException;

    void removeFeatureStatusCallback(int i, int i2, IImsFeatureStatusCallback iImsFeatureStatusCallback) throws RemoteException;

    void removeImsFeature(int i, int i2) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IImsServiceController {
        @Override // com.android.ims.internal.IImsServiceController
        public IImsMMTelFeature createEmergencyMMTelFeature(int slotId) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.IImsServiceController
        public IImsMMTelFeature createMMTelFeature(int slotId) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.IImsServiceController
        public IImsRcsFeature createRcsFeature(int slotId) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.IImsServiceController
        public void removeImsFeature(int slotId, int featureType) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsServiceController
        public void addFeatureStatusCallback(int slotId, int featureType, IImsFeatureStatusCallback c) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsServiceController
        public void removeFeatureStatusCallback(int slotId, int featureType, IImsFeatureStatusCallback c) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IImsServiceController {
        public static final String DESCRIPTOR = "com.android.ims.internal.IImsServiceController";
        static final int TRANSACTION_addFeatureStatusCallback = 5;
        static final int TRANSACTION_createEmergencyMMTelFeature = 1;
        static final int TRANSACTION_createMMTelFeature = 2;
        static final int TRANSACTION_createRcsFeature = 3;
        static final int TRANSACTION_removeFeatureStatusCallback = 6;
        static final int TRANSACTION_removeImsFeature = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IImsServiceController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IImsServiceController)) {
                return (IImsServiceController) iin;
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
                    return "createEmergencyMMTelFeature";
                case 2:
                    return "createMMTelFeature";
                case 3:
                    return "createRcsFeature";
                case 4:
                    return "removeImsFeature";
                case 5:
                    return "addFeatureStatusCallback";
                case 6:
                    return "removeFeatureStatusCallback";
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
                            IImsMMTelFeature _result = createEmergencyMMTelFeature(_arg0);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            IImsMMTelFeature _result2 = createMMTelFeature(_arg02);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            IImsRcsFeature _result3 = createRcsFeature(_arg03);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            removeImsFeature(_arg04, _arg1);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            int _arg12 = data.readInt();
                            IImsFeatureStatusCallback _arg2 = IImsFeatureStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addFeatureStatusCallback(_arg05, _arg12, _arg2);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            int _arg13 = data.readInt();
                            IImsFeatureStatusCallback _arg22 = IImsFeatureStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeFeatureStatusCallback(_arg06, _arg13, _arg22);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IImsServiceController {
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

            @Override // com.android.ims.internal.IImsServiceController
            public IImsMMTelFeature createEmergencyMMTelFeature(int slotId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    IImsMMTelFeature _result = IImsMMTelFeature.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsServiceController
            public IImsMMTelFeature createMMTelFeature(int slotId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    IImsMMTelFeature _result = IImsMMTelFeature.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsServiceController
            public IImsRcsFeature createRcsFeature(int slotId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    IImsRcsFeature _result = IImsRcsFeature.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsServiceController
            public void removeImsFeature(int slotId, int featureType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(featureType);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsServiceController
            public void addFeatureStatusCallback(int slotId, int featureType, IImsFeatureStatusCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(featureType);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsServiceController
            public void removeFeatureStatusCallback(int slotId, int featureType, IImsFeatureStatusCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(featureType);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
