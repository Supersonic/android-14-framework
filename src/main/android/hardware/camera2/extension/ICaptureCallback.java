package android.hardware.camera2.extension;

import android.hardware.camera2.impl.CameraMetadataNative;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ICaptureCallback extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.camera2.extension.ICaptureCallback";

    void onCaptureCompleted(long j, int i, CameraMetadataNative cameraMetadataNative) throws RemoteException;

    void onCaptureFailed(int i) throws RemoteException;

    void onCaptureProcessProgressed(int i) throws RemoteException;

    void onCaptureProcessStarted(int i) throws RemoteException;

    void onCaptureSequenceAborted(int i) throws RemoteException;

    void onCaptureSequenceCompleted(int i) throws RemoteException;

    void onCaptureStarted(int i, long j) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ICaptureCallback {
        @Override // android.hardware.camera2.extension.ICaptureCallback
        public void onCaptureStarted(int captureSequenceId, long timestamp) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.ICaptureCallback
        public void onCaptureProcessStarted(int captureSequenceId) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.ICaptureCallback
        public void onCaptureFailed(int captureSequenceId) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.ICaptureCallback
        public void onCaptureSequenceCompleted(int captureSequenceId) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.ICaptureCallback
        public void onCaptureSequenceAborted(int captureSequenceId) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.ICaptureCallback
        public void onCaptureCompleted(long shutterTimestamp, int requestId, CameraMetadataNative results) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.ICaptureCallback
        public void onCaptureProcessProgressed(int progress) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ICaptureCallback {
        static final int TRANSACTION_onCaptureCompleted = 6;
        static final int TRANSACTION_onCaptureFailed = 3;
        static final int TRANSACTION_onCaptureProcessProgressed = 7;
        static final int TRANSACTION_onCaptureProcessStarted = 2;
        static final int TRANSACTION_onCaptureSequenceAborted = 5;
        static final int TRANSACTION_onCaptureSequenceCompleted = 4;
        static final int TRANSACTION_onCaptureStarted = 1;

        public Stub() {
            attachInterface(this, ICaptureCallback.DESCRIPTOR);
        }

        public static ICaptureCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICaptureCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ICaptureCallback)) {
                return (ICaptureCallback) iin;
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
                    return "onCaptureProcessStarted";
                case 3:
                    return "onCaptureFailed";
                case 4:
                    return "onCaptureSequenceCompleted";
                case 5:
                    return "onCaptureSequenceAborted";
                case 6:
                    return "onCaptureCompleted";
                case 7:
                    return "onCaptureProcessProgressed";
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
                data.enforceInterface(ICaptureCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICaptureCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            long _arg1 = data.readLong();
                            data.enforceNoDataAvail();
                            onCaptureStarted(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onCaptureProcessStarted(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onCaptureFailed(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            onCaptureSequenceCompleted(_arg04);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            onCaptureSequenceAborted(_arg05);
                            reply.writeNoException();
                            break;
                        case 6:
                            long _arg06 = data.readLong();
                            int _arg12 = data.readInt();
                            CameraMetadataNative _arg2 = (CameraMetadataNative) data.readTypedObject(CameraMetadataNative.CREATOR);
                            data.enforceNoDataAvail();
                            onCaptureCompleted(_arg06, _arg12, _arg2);
                            reply.writeNoException();
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            onCaptureProcessProgressed(_arg07);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements ICaptureCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICaptureCallback.DESCRIPTOR;
            }

            @Override // android.hardware.camera2.extension.ICaptureCallback
            public void onCaptureStarted(int captureSequenceId, long timestamp) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICaptureCallback.DESCRIPTOR);
                    _data.writeInt(captureSequenceId);
                    _data.writeLong(timestamp);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ICaptureCallback
            public void onCaptureProcessStarted(int captureSequenceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICaptureCallback.DESCRIPTOR);
                    _data.writeInt(captureSequenceId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ICaptureCallback
            public void onCaptureFailed(int captureSequenceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICaptureCallback.DESCRIPTOR);
                    _data.writeInt(captureSequenceId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ICaptureCallback
            public void onCaptureSequenceCompleted(int captureSequenceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICaptureCallback.DESCRIPTOR);
                    _data.writeInt(captureSequenceId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ICaptureCallback
            public void onCaptureSequenceAborted(int captureSequenceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICaptureCallback.DESCRIPTOR);
                    _data.writeInt(captureSequenceId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ICaptureCallback
            public void onCaptureCompleted(long shutterTimestamp, int requestId, CameraMetadataNative results) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICaptureCallback.DESCRIPTOR);
                    _data.writeLong(shutterTimestamp);
                    _data.writeInt(requestId);
                    _data.writeTypedObject(results, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ICaptureCallback
            public void onCaptureProcessProgressed(int progress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICaptureCallback.DESCRIPTOR);
                    _data.writeInt(progress);
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
