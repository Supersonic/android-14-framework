package android.window;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IBackAnimationFinishedCallback extends IInterface {
    public static final String DESCRIPTOR = "android.window.IBackAnimationFinishedCallback";

    void onAnimationFinished(boolean z) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IBackAnimationFinishedCallback {
        @Override // android.window.IBackAnimationFinishedCallback
        public void onAnimationFinished(boolean triggerBack) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IBackAnimationFinishedCallback {
        static final int TRANSACTION_onAnimationFinished = 1;

        public Stub() {
            attachInterface(this, IBackAnimationFinishedCallback.DESCRIPTOR);
        }

        public static IBackAnimationFinishedCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IBackAnimationFinishedCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IBackAnimationFinishedCallback)) {
                return (IBackAnimationFinishedCallback) iin;
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
                    return "onAnimationFinished";
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
                data.enforceInterface(IBackAnimationFinishedCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IBackAnimationFinishedCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onAnimationFinished(_arg0);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IBackAnimationFinishedCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBackAnimationFinishedCallback.DESCRIPTOR;
            }

            @Override // android.window.IBackAnimationFinishedCallback
            public void onAnimationFinished(boolean triggerBack) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBackAnimationFinishedCallback.DESCRIPTOR);
                    _data.writeBoolean(triggerBack);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
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
