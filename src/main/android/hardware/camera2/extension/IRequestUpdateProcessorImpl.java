package android.hardware.camera2.extension;

import android.hardware.camera2.impl.CameraMetadataNative;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.Surface;
/* loaded from: classes.dex */
public interface IRequestUpdateProcessorImpl extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.camera2.extension.IRequestUpdateProcessorImpl";

    void onImageFormatUpdate(int i) throws RemoteException;

    void onOutputSurface(Surface surface, int i) throws RemoteException;

    void onResolutionUpdate(Size size) throws RemoteException;

    CaptureStageImpl process(CameraMetadataNative cameraMetadataNative, int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IRequestUpdateProcessorImpl {
        @Override // android.hardware.camera2.extension.IRequestUpdateProcessorImpl
        public void onOutputSurface(Surface surface, int imageFormat) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IRequestUpdateProcessorImpl
        public void onResolutionUpdate(Size size) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IRequestUpdateProcessorImpl
        public void onImageFormatUpdate(int imageFormat) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IRequestUpdateProcessorImpl
        public CaptureStageImpl process(CameraMetadataNative result, int sequenceId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IRequestUpdateProcessorImpl {
        static final int TRANSACTION_onImageFormatUpdate = 3;
        static final int TRANSACTION_onOutputSurface = 1;
        static final int TRANSACTION_onResolutionUpdate = 2;
        static final int TRANSACTION_process = 4;

        public Stub() {
            attachInterface(this, IRequestUpdateProcessorImpl.DESCRIPTOR);
        }

        public static IRequestUpdateProcessorImpl asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IRequestUpdateProcessorImpl.DESCRIPTOR);
            if (iin != null && (iin instanceof IRequestUpdateProcessorImpl)) {
                return (IRequestUpdateProcessorImpl) iin;
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
                    return "onOutputSurface";
                case 2:
                    return "onResolutionUpdate";
                case 3:
                    return "onImageFormatUpdate";
                case 4:
                    return "process";
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
                data.enforceInterface(IRequestUpdateProcessorImpl.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IRequestUpdateProcessorImpl.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            Surface _arg0 = (Surface) data.readTypedObject(Surface.CREATOR);
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onOutputSurface(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            Size _arg02 = (Size) data.readTypedObject(Size.CREATOR);
                            data.enforceNoDataAvail();
                            onResolutionUpdate(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onImageFormatUpdate(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            CameraMetadataNative _arg04 = (CameraMetadataNative) data.readTypedObject(CameraMetadataNative.CREATOR);
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            CaptureStageImpl _result = process(_arg04, _arg12);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IRequestUpdateProcessorImpl {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IRequestUpdateProcessorImpl.DESCRIPTOR;
            }

            @Override // android.hardware.camera2.extension.IRequestUpdateProcessorImpl
            public void onOutputSurface(Surface surface, int imageFormat) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestUpdateProcessorImpl.DESCRIPTOR);
                    _data.writeTypedObject(surface, 0);
                    _data.writeInt(imageFormat);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IRequestUpdateProcessorImpl
            public void onResolutionUpdate(Size size) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestUpdateProcessorImpl.DESCRIPTOR);
                    _data.writeTypedObject(size, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IRequestUpdateProcessorImpl
            public void onImageFormatUpdate(int imageFormat) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestUpdateProcessorImpl.DESCRIPTOR);
                    _data.writeInt(imageFormat);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IRequestUpdateProcessorImpl
            public CaptureStageImpl process(CameraMetadataNative result, int sequenceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRequestUpdateProcessorImpl.DESCRIPTOR);
                    _data.writeTypedObject(result, 0);
                    _data.writeInt(sequenceId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    CaptureStageImpl _result = (CaptureStageImpl) _reply.readTypedObject(CaptureStageImpl.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
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
