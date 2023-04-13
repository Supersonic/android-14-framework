package android.hardware.camera2.extension;

import android.hardware.camera2.impl.CameraMetadataNative;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IProcessResultImpl extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.camera2.extension.IProcessResultImpl";

    void onCaptureCompleted(long j, CameraMetadataNative cameraMetadataNative) throws RemoteException;

    void onCaptureProcessProgressed(int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IProcessResultImpl {
        @Override // android.hardware.camera2.extension.IProcessResultImpl
        public void onCaptureCompleted(long shutterTimestamp, CameraMetadataNative results) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IProcessResultImpl
        public void onCaptureProcessProgressed(int progress) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IProcessResultImpl {
        static final int TRANSACTION_onCaptureCompleted = 1;
        static final int TRANSACTION_onCaptureProcessProgressed = 2;

        public Stub() {
            attachInterface(this, IProcessResultImpl.DESCRIPTOR);
        }

        public static IProcessResultImpl asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IProcessResultImpl.DESCRIPTOR);
            if (iin != null && (iin instanceof IProcessResultImpl)) {
                return (IProcessResultImpl) iin;
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
                    return "onCaptureCompleted";
                case 2:
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
                data.enforceInterface(IProcessResultImpl.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IProcessResultImpl.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            long _arg0 = data.readLong();
                            CameraMetadataNative _arg1 = (CameraMetadataNative) data.readTypedObject(CameraMetadataNative.CREATOR);
                            data.enforceNoDataAvail();
                            onCaptureCompleted(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onCaptureProcessProgressed(_arg02);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IProcessResultImpl {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IProcessResultImpl.DESCRIPTOR;
            }

            @Override // android.hardware.camera2.extension.IProcessResultImpl
            public void onCaptureCompleted(long shutterTimestamp, CameraMetadataNative results) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IProcessResultImpl.DESCRIPTOR);
                    _data.writeLong(shutterTimestamp);
                    _data.writeTypedObject(results, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IProcessResultImpl
            public void onCaptureProcessProgressed(int progress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IProcessResultImpl.DESCRIPTOR);
                    _data.writeInt(progress);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
