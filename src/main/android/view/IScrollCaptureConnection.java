package android.view;

import android.graphics.Rect;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.IScrollCaptureCallbacks;
/* loaded from: classes4.dex */
public interface IScrollCaptureConnection extends IInterface {
    public static final String DESCRIPTOR = "android.view.IScrollCaptureConnection";

    void close() throws RemoteException;

    ICancellationSignal endCapture() throws RemoteException;

    ICancellationSignal requestImage(Rect rect) throws RemoteException;

    ICancellationSignal startCapture(Surface surface, IScrollCaptureCallbacks iScrollCaptureCallbacks) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IScrollCaptureConnection {
        @Override // android.view.IScrollCaptureConnection
        public ICancellationSignal startCapture(Surface surface, IScrollCaptureCallbacks callbacks) throws RemoteException {
            return null;
        }

        @Override // android.view.IScrollCaptureConnection
        public ICancellationSignal requestImage(Rect captureArea) throws RemoteException {
            return null;
        }

        @Override // android.view.IScrollCaptureConnection
        public ICancellationSignal endCapture() throws RemoteException {
            return null;
        }

        @Override // android.view.IScrollCaptureConnection
        public void close() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IScrollCaptureConnection {
        static final int TRANSACTION_close = 4;
        static final int TRANSACTION_endCapture = 3;
        static final int TRANSACTION_requestImage = 2;
        static final int TRANSACTION_startCapture = 1;

        public Stub() {
            attachInterface(this, IScrollCaptureConnection.DESCRIPTOR);
        }

        public static IScrollCaptureConnection asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IScrollCaptureConnection.DESCRIPTOR);
            if (iin != null && (iin instanceof IScrollCaptureConnection)) {
                return (IScrollCaptureConnection) iin;
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
                    return "startCapture";
                case 2:
                    return "requestImage";
                case 3:
                    return "endCapture";
                case 4:
                    return "close";
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
                data.enforceInterface(IScrollCaptureConnection.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IScrollCaptureConnection.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            Surface _arg0 = (Surface) data.readTypedObject(Surface.CREATOR);
                            IScrollCaptureCallbacks _arg1 = IScrollCaptureCallbacks.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            ICancellationSignal _result = startCapture(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 2:
                            Rect _arg02 = (Rect) data.readTypedObject(Rect.CREATOR);
                            data.enforceNoDataAvail();
                            ICancellationSignal _result2 = requestImage(_arg02);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 3:
                            ICancellationSignal _result3 = endCapture();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        case 4:
                            close();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IScrollCaptureConnection {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IScrollCaptureConnection.DESCRIPTOR;
            }

            @Override // android.view.IScrollCaptureConnection
            public ICancellationSignal startCapture(Surface surface, IScrollCaptureCallbacks callbacks) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IScrollCaptureConnection.DESCRIPTOR);
                    _data.writeTypedObject(surface, 0);
                    _data.writeStrongInterface(callbacks);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IScrollCaptureConnection
            public ICancellationSignal requestImage(Rect captureArea) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IScrollCaptureConnection.DESCRIPTOR);
                    _data.writeTypedObject(captureArea, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IScrollCaptureConnection
            public ICancellationSignal endCapture() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IScrollCaptureConnection.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IScrollCaptureConnection
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IScrollCaptureConnection.DESCRIPTOR);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
