package android.media;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IStreamAliasingDispatcher extends IInterface {
    public static final String DESCRIPTOR = "android.media.IStreamAliasingDispatcher";

    void dispatchStreamAliasingChanged() throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IStreamAliasingDispatcher {
        @Override // android.media.IStreamAliasingDispatcher
        public void dispatchStreamAliasingChanged() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IStreamAliasingDispatcher {
        static final int TRANSACTION_dispatchStreamAliasingChanged = 1;

        public Stub() {
            attachInterface(this, IStreamAliasingDispatcher.DESCRIPTOR);
        }

        public static IStreamAliasingDispatcher asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IStreamAliasingDispatcher.DESCRIPTOR);
            if (iin != null && (iin instanceof IStreamAliasingDispatcher)) {
                return (IStreamAliasingDispatcher) iin;
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
                    return "dispatchStreamAliasingChanged";
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
                data.enforceInterface(IStreamAliasingDispatcher.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IStreamAliasingDispatcher.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            dispatchStreamAliasingChanged();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IStreamAliasingDispatcher {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IStreamAliasingDispatcher.DESCRIPTOR;
            }

            @Override // android.media.IStreamAliasingDispatcher
            public void dispatchStreamAliasingChanged() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IStreamAliasingDispatcher.DESCRIPTOR);
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
