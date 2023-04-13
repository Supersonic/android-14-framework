package com.android.internal.telecom;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.telecom.IDeviceIdleControllerAdapter;
/* loaded from: classes2.dex */
public interface IInternalServiceRetriever extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telecom.IInternalServiceRetriever";

    IDeviceIdleControllerAdapter getDeviceIdleController() throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IInternalServiceRetriever {
        @Override // com.android.internal.telecom.IInternalServiceRetriever
        public IDeviceIdleControllerAdapter getDeviceIdleController() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IInternalServiceRetriever {
        static final int TRANSACTION_getDeviceIdleController = 1;

        public Stub() {
            attachInterface(this, IInternalServiceRetriever.DESCRIPTOR);
        }

        public static IInternalServiceRetriever asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IInternalServiceRetriever.DESCRIPTOR);
            if (iin != null && (iin instanceof IInternalServiceRetriever)) {
                return (IInternalServiceRetriever) iin;
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
                    return "getDeviceIdleController";
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
                data.enforceInterface(IInternalServiceRetriever.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IInternalServiceRetriever.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IDeviceIdleControllerAdapter _result = getDeviceIdleController();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IInternalServiceRetriever {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IInternalServiceRetriever.DESCRIPTOR;
            }

            @Override // com.android.internal.telecom.IInternalServiceRetriever
            public IDeviceIdleControllerAdapter getDeviceIdleController() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IInternalServiceRetriever.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    IDeviceIdleControllerAdapter _result = IDeviceIdleControllerAdapter.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
