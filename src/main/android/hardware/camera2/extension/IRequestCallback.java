package android.hardware.camera2.extension;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IRequestCallback extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.camera2.extension.IRequestCallback";

    void onCaptureBufferLost(int i, long j, int i2) throws RemoteException;

    void onCaptureCompleted(int i, ParcelTotalCaptureResult parcelTotalCaptureResult) throws RemoteException;

    void onCaptureFailed(int i, CaptureFailure captureFailure) throws RemoteException;

    void onCaptureProgressed(int i, ParcelCaptureResult parcelCaptureResult) throws RemoteException;

    void onCaptureSequenceAborted(int i) throws RemoteException;

    void onCaptureSequenceCompleted(int i, long j) throws RemoteException;

    void onCaptureStarted(int i, long j, long j2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IRequestCallback {
        @Override // android.hardware.camera2.extension.IRequestCallback
        public void onCaptureStarted(int requestId, long frameNumber, long timestamp) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IRequestCallback
        public void onCaptureProgressed(int requestId, ParcelCaptureResult partialResult) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IRequestCallback
        public void onCaptureCompleted(int requestId, ParcelTotalCaptureResult totalCaptureResult) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IRequestCallback
        public void onCaptureFailed(int requestId, CaptureFailure captureFailure) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IRequestCallback
        public void onCaptureBufferLost(int requestId, long frameNumber, int outputStreamId) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IRequestCallback
        public void onCaptureSequenceCompleted(int sequenceId, long frameNumber) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IRequestCallback
        public void onCaptureSequenceAborted(int sequenceId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IRequestCallback {
        static final int TRANSACTION_onCaptureBufferLost = 5;
        static final int TRANSACTION_onCaptureCompleted = 3;
        static final int TRANSACTION_onCaptureFailed = 4;
        static final int TRANSACTION_onCaptureProgressed = 2;
        static final int TRANSACTION_onCaptureSequenceAborted = 7;
        static final int TRANSACTION_onCaptureSequenceCompleted = 6;
        static final int TRANSACTION_onCaptureStarted = 1;

        public Stub() {
            attachInterface(this, IRequestCallback.DESCRIPTOR);
        }

        public static IRequestCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IRequestCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IRequestCallback)) {
                return (IRequestCallback) iin;
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
                    return "onCaptureProgressed";
                case 3:
                    return "onCaptureCompleted";
                case 4:
                    return "onCaptureFailed";
                case 5:
                    return "onCaptureBufferLost";
                case 6:
                    return "onCaptureSequenceCompleted";
                case 7:
                    return "onCaptureSequenceAborted";
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
                data.enforceInterface(IRequestCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IRequestCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            long _arg1 = data.readLong();
                            long _arg2 = data.readLong();
                            data.enforceNoDataAvail();
                            onCaptureStarted(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            ParcelCaptureResult _arg12 = (ParcelCaptureResult) data.readTypedObject(ParcelCaptureResult.CREATOR);
                            data.enforceNoDataAvail();
                            onCaptureProgressed(_arg02, _arg12);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            ParcelTotalCaptureResult _arg13 = (ParcelTotalCaptureResult) data.readTypedObject(ParcelTotalCaptureResult.CREATOR);
                            data.enforceNoDataAvail();
                            onCaptureCompleted(_arg03, _arg13);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            CaptureFailure _arg14 = (CaptureFailure) data.readTypedObject(CaptureFailure.CREATOR);
                            data.enforceNoDataAvail();
                            onCaptureFailed(_arg04, _arg14);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            long _arg15 = data.readLong();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            onCaptureBufferLost(_arg05, _arg15, _arg22);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            long _arg16 = data.readLong();
                            data.enforceNoDataAvail();
                            onCaptureSequenceCompleted(_arg06, _arg16);
                            reply.writeNoException();
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            onCaptureSequenceAborted(_arg07);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IRequestCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IRequestCallback.DESCRIPTOR;
            }

            @Override // android.hardware.camera2.extension.IRequestCallback
            public void onCaptureStarted(int requestId, long frameNumber, long timestamp) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestCallback.DESCRIPTOR);
                    _data.writeInt(requestId);
                    _data.writeLong(frameNumber);
                    _data.writeLong(timestamp);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IRequestCallback
            public void onCaptureProgressed(int requestId, ParcelCaptureResult partialResult) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestCallback.DESCRIPTOR);
                    _data.writeInt(requestId);
                    _data.writeTypedObject(partialResult, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IRequestCallback
            public void onCaptureCompleted(int requestId, ParcelTotalCaptureResult totalCaptureResult) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestCallback.DESCRIPTOR);
                    _data.writeInt(requestId);
                    _data.writeTypedObject(totalCaptureResult, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IRequestCallback
            public void onCaptureFailed(int requestId, CaptureFailure captureFailure) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestCallback.DESCRIPTOR);
                    _data.writeInt(requestId);
                    _data.writeTypedObject(captureFailure, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IRequestCallback
            public void onCaptureBufferLost(int requestId, long frameNumber, int outputStreamId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestCallback.DESCRIPTOR);
                    _data.writeInt(requestId);
                    _data.writeLong(frameNumber);
                    _data.writeInt(outputStreamId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IRequestCallback
            public void onCaptureSequenceCompleted(int sequenceId, long frameNumber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestCallback.DESCRIPTOR);
                    _data.writeInt(sequenceId);
                    _data.writeLong(frameNumber);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IRequestCallback
            public void onCaptureSequenceAborted(int sequenceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestCallback.DESCRIPTOR);
                    _data.writeInt(sequenceId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
