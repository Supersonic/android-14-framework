package com.android.internal.infra;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IAndroidFuture extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.infra.IAndroidFuture";

    void complete(AndroidFuture androidFuture) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IAndroidFuture {
        @Override // com.android.internal.infra.IAndroidFuture
        public void complete(AndroidFuture resultContainer) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IAndroidFuture {
        static final int TRANSACTION_complete = 1;

        public Stub() {
            attachInterface(this, IAndroidFuture.DESCRIPTOR);
        }

        public static IAndroidFuture asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAndroidFuture.DESCRIPTOR);
            if (iin != null && (iin instanceof IAndroidFuture)) {
                return (IAndroidFuture) iin;
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
                    return "complete";
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
                data.enforceInterface(IAndroidFuture.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAndroidFuture.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            AndroidFuture _arg0 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            complete(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IAndroidFuture {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAndroidFuture.DESCRIPTOR;
            }

            @Override // com.android.internal.infra.IAndroidFuture
            public void complete(AndroidFuture resultContainer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAndroidFuture.DESCRIPTOR);
                    _data.writeTypedObject(resultContainer, 0);
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
