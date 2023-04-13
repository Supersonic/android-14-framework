package android.view;

import android.graphics.Rect;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IScrollCaptureCallbacks extends IInterface {
    public static final String DESCRIPTOR = "android.view.IScrollCaptureCallbacks";

    void onCaptureEnded() throws RemoteException;

    void onCaptureStarted() throws RemoteException;

    void onImageRequestCompleted(int i, Rect rect) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IScrollCaptureCallbacks {
        @Override // android.view.IScrollCaptureCallbacks
        public void onCaptureStarted() throws RemoteException {
        }

        @Override // android.view.IScrollCaptureCallbacks
        public void onImageRequestCompleted(int flags, Rect capturedArea) throws RemoteException {
        }

        @Override // android.view.IScrollCaptureCallbacks
        public void onCaptureEnded() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IScrollCaptureCallbacks {
        static final int TRANSACTION_onCaptureEnded = 3;
        static final int TRANSACTION_onCaptureStarted = 1;
        static final int TRANSACTION_onImageRequestCompleted = 2;

        public Stub() {
            attachInterface(this, IScrollCaptureCallbacks.DESCRIPTOR);
        }

        public static IScrollCaptureCallbacks asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IScrollCaptureCallbacks.DESCRIPTOR);
            if (iin != null && (iin instanceof IScrollCaptureCallbacks)) {
                return (IScrollCaptureCallbacks) iin;
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
                    return "onCaptureStarted";
                case 2:
                    return "onImageRequestCompleted";
                case 3:
                    return "onCaptureEnded";
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
                data.enforceInterface(IScrollCaptureCallbacks.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IScrollCaptureCallbacks.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            onCaptureStarted();
                            break;
                        case 2:
                            int _arg0 = data.readInt();
                            Rect _arg1 = (Rect) data.readTypedObject(Rect.CREATOR);
                            data.enforceNoDataAvail();
                            onImageRequestCompleted(_arg0, _arg1);
                            break;
                        case 3:
                            onCaptureEnded();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IScrollCaptureCallbacks {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IScrollCaptureCallbacks.DESCRIPTOR;
            }

            @Override // android.view.IScrollCaptureCallbacks
            public void onCaptureStarted() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IScrollCaptureCallbacks.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IScrollCaptureCallbacks
            public void onImageRequestCompleted(int flags, Rect capturedArea) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IScrollCaptureCallbacks.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeTypedObject(capturedArea, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IScrollCaptureCallbacks
            public void onCaptureEnded() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IScrollCaptureCallbacks.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
