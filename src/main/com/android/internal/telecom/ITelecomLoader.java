package com.android.internal.telecom;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.telecom.IInternalServiceRetriever;
import com.android.internal.telecom.ITelecomService;
/* loaded from: classes2.dex */
public interface ITelecomLoader extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telecom.ITelecomLoader";

    ITelecomService createTelecomService(IInternalServiceRetriever iInternalServiceRetriever) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ITelecomLoader {
        @Override // com.android.internal.telecom.ITelecomLoader
        public ITelecomService createTelecomService(IInternalServiceRetriever retriever) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITelecomLoader {
        static final int TRANSACTION_createTelecomService = 1;

        public Stub() {
            attachInterface(this, ITelecomLoader.DESCRIPTOR);
        }

        public static ITelecomLoader asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITelecomLoader.DESCRIPTOR);
            if (iin != null && (iin instanceof ITelecomLoader)) {
                return (ITelecomLoader) iin;
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
                    return "createTelecomService";
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
                data.enforceInterface(ITelecomLoader.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITelecomLoader.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IInternalServiceRetriever _arg0 = IInternalServiceRetriever.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            ITelecomService _result = createTelecomService(_arg0);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ITelecomLoader {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITelecomLoader.DESCRIPTOR;
            }

            @Override // com.android.internal.telecom.ITelecomLoader
            public ITelecomService createTelecomService(IInternalServiceRetriever retriever) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ITelecomLoader.DESCRIPTOR);
                    _data.writeStrongInterface(retriever);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ITelecomService _result = ITelecomService.Stub.asInterface(_reply.readStrongBinder());
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
