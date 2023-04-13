package android.service.selectiontoolbar;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface ISelectionToolbarRenderServiceCallback extends IInterface {
    public static final String DESCRIPTOR = "android.service.selectiontoolbar.ISelectionToolbarRenderServiceCallback";

    void transferTouch(IBinder iBinder, IBinder iBinder2) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISelectionToolbarRenderServiceCallback {
        @Override // android.service.selectiontoolbar.ISelectionToolbarRenderServiceCallback
        public void transferTouch(IBinder source, IBinder target) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISelectionToolbarRenderServiceCallback {
        static final int TRANSACTION_transferTouch = 1;

        public Stub() {
            attachInterface(this, ISelectionToolbarRenderServiceCallback.DESCRIPTOR);
        }

        public static ISelectionToolbarRenderServiceCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISelectionToolbarRenderServiceCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISelectionToolbarRenderServiceCallback)) {
                return (ISelectionToolbarRenderServiceCallback) iin;
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
                    return "transferTouch";
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
                data.enforceInterface(ISelectionToolbarRenderServiceCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISelectionToolbarRenderServiceCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IBinder _arg0 = data.readStrongBinder();
                            IBinder _arg1 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            transferTouch(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISelectionToolbarRenderServiceCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISelectionToolbarRenderServiceCallback.DESCRIPTOR;
            }

            @Override // android.service.selectiontoolbar.ISelectionToolbarRenderServiceCallback
            public void transferTouch(IBinder source, IBinder target) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISelectionToolbarRenderServiceCallback.DESCRIPTOR);
                    _data.writeStrongBinder(source);
                    _data.writeStrongBinder(target);
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
