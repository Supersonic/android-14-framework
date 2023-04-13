package android.hardware.camera2.extension;

import android.hardware.camera2.extension.IImageProcessorImpl;
import android.hardware.camera2.extension.IRequestCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IRequestProcessorImpl extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.camera2.extension.IRequestProcessorImpl";

    void abortCaptures() throws RemoteException;

    void setImageProcessor(OutputConfigId outputConfigId, IImageProcessorImpl iImageProcessorImpl) throws RemoteException;

    int setRepeating(Request request, IRequestCallback iRequestCallback) throws RemoteException;

    void stopRepeating() throws RemoteException;

    int submit(Request request, IRequestCallback iRequestCallback) throws RemoteException;

    int submitBurst(List<Request> list, IRequestCallback iRequestCallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IRequestProcessorImpl {
        @Override // android.hardware.camera2.extension.IRequestProcessorImpl
        public void setImageProcessor(OutputConfigId outputConfigId, IImageProcessorImpl imageProcessor) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IRequestProcessorImpl
        public int submit(Request request, IRequestCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.camera2.extension.IRequestProcessorImpl
        public int submitBurst(List<Request> requests, IRequestCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.camera2.extension.IRequestProcessorImpl
        public int setRepeating(Request request, IRequestCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.camera2.extension.IRequestProcessorImpl
        public void abortCaptures() throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IRequestProcessorImpl
        public void stopRepeating() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IRequestProcessorImpl {
        static final int TRANSACTION_abortCaptures = 5;
        static final int TRANSACTION_setImageProcessor = 1;
        static final int TRANSACTION_setRepeating = 4;
        static final int TRANSACTION_stopRepeating = 6;
        static final int TRANSACTION_submit = 2;
        static final int TRANSACTION_submitBurst = 3;

        public Stub() {
            attachInterface(this, IRequestProcessorImpl.DESCRIPTOR);
        }

        public static IRequestProcessorImpl asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IRequestProcessorImpl.DESCRIPTOR);
            if (iin != null && (iin instanceof IRequestProcessorImpl)) {
                return (IRequestProcessorImpl) iin;
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
                    return "setImageProcessor";
                case 2:
                    return "submit";
                case 3:
                    return "submitBurst";
                case 4:
                    return "setRepeating";
                case 5:
                    return "abortCaptures";
                case 6:
                    return "stopRepeating";
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
                data.enforceInterface(IRequestProcessorImpl.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IRequestProcessorImpl.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            OutputConfigId _arg0 = (OutputConfigId) data.readTypedObject(OutputConfigId.CREATOR);
                            IImageProcessorImpl _arg1 = IImageProcessorImpl.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setImageProcessor(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            Request _arg02 = (Request) data.readTypedObject(Request.CREATOR);
                            IRequestCallback _arg12 = IRequestCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result = submit(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 3:
                            List<Request> _arg03 = data.createTypedArrayList(Request.CREATOR);
                            IRequestCallback _arg13 = IRequestCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result2 = submitBurst(_arg03, _arg13);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 4:
                            Request _arg04 = (Request) data.readTypedObject(Request.CREATOR);
                            IRequestCallback _arg14 = IRequestCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result3 = setRepeating(_arg04, _arg14);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 5:
                            abortCaptures();
                            reply.writeNoException();
                            break;
                        case 6:
                            stopRepeating();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IRequestProcessorImpl {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IRequestProcessorImpl.DESCRIPTOR;
            }

            @Override // android.hardware.camera2.extension.IRequestProcessorImpl
            public void setImageProcessor(OutputConfigId outputConfigId, IImageProcessorImpl imageProcessor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestProcessorImpl.DESCRIPTOR);
                    _data.writeTypedObject(outputConfigId, 0);
                    _data.writeStrongInterface(imageProcessor);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IRequestProcessorImpl
            public int submit(Request request, IRequestCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestProcessorImpl.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IRequestProcessorImpl
            public int submitBurst(List<Request> requests, IRequestCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestProcessorImpl.DESCRIPTOR);
                    _data.writeTypedList(requests, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IRequestProcessorImpl
            public int setRepeating(Request request, IRequestCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestProcessorImpl.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IRequestProcessorImpl
            public void abortCaptures() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestProcessorImpl.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IRequestProcessorImpl
            public void stopRepeating() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestProcessorImpl.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 5;
        }
    }
}
