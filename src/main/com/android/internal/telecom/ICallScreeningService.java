package com.android.internal.telecom;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telecom.ParcelableCall;
import com.android.internal.telecom.ICallScreeningAdapter;
/* loaded from: classes2.dex */
public interface ICallScreeningService extends IInterface {
    void screenCall(ICallScreeningAdapter iCallScreeningAdapter, ParcelableCall parcelableCall) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ICallScreeningService {
        @Override // com.android.internal.telecom.ICallScreeningService
        public void screenCall(ICallScreeningAdapter adapter, ParcelableCall call) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ICallScreeningService {
        public static final String DESCRIPTOR = "com.android.internal.telecom.ICallScreeningService";
        static final int TRANSACTION_screenCall = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICallScreeningService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ICallScreeningService)) {
                return (ICallScreeningService) iin;
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
                    return "screenCall";
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
                            ICallScreeningAdapter _arg0 = ICallScreeningAdapter.Stub.asInterface(data.readStrongBinder());
                            ParcelableCall _arg1 = (ParcelableCall) data.readTypedObject(ParcelableCall.CREATOR);
                            data.enforceNoDataAvail();
                            screenCall(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ICallScreeningService {
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

            @Override // com.android.internal.telecom.ICallScreeningService
            public void screenCall(ICallScreeningAdapter adapter, ParcelableCall call) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(adapter);
                    _data.writeTypedObject(call, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
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
