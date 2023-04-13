package android.app;

import android.content.Intent;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IUnsafeIntentStrictModeCallback extends IInterface {
    public static final String DESCRIPTOR = "android.app.IUnsafeIntentStrictModeCallback";

    void onImplicitIntentMatchedInternalComponent(Intent intent) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IUnsafeIntentStrictModeCallback {
        @Override // android.app.IUnsafeIntentStrictModeCallback
        public void onImplicitIntentMatchedInternalComponent(Intent intent) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IUnsafeIntentStrictModeCallback {
        static final int TRANSACTION_onImplicitIntentMatchedInternalComponent = 1;

        public Stub() {
            attachInterface(this, IUnsafeIntentStrictModeCallback.DESCRIPTOR);
        }

        public static IUnsafeIntentStrictModeCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IUnsafeIntentStrictModeCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IUnsafeIntentStrictModeCallback)) {
                return (IUnsafeIntentStrictModeCallback) iin;
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
                    return "onImplicitIntentMatchedInternalComponent";
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
                data.enforceInterface(IUnsafeIntentStrictModeCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IUnsafeIntentStrictModeCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            Intent _arg0 = (Intent) data.readTypedObject(Intent.CREATOR);
                            data.enforceNoDataAvail();
                            onImplicitIntentMatchedInternalComponent(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IUnsafeIntentStrictModeCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IUnsafeIntentStrictModeCallback.DESCRIPTOR;
            }

            @Override // android.app.IUnsafeIntentStrictModeCallback
            public void onImplicitIntentMatchedInternalComponent(Intent intent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IUnsafeIntentStrictModeCallback.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
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
