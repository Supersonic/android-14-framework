package android.hardware.camera2.extension;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IImageProcessorImpl extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.camera2.extension.IImageProcessorImpl";

    void onNextImageAvailable(OutputConfigId outputConfigId, ParcelImage parcelImage, String str) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IImageProcessorImpl {
        @Override // android.hardware.camera2.extension.IImageProcessorImpl
        public void onNextImageAvailable(OutputConfigId outputConfigId, ParcelImage image, String physicalCameraId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IImageProcessorImpl {
        static final int TRANSACTION_onNextImageAvailable = 1;

        public Stub() {
            attachInterface(this, IImageProcessorImpl.DESCRIPTOR);
        }

        public static IImageProcessorImpl asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IImageProcessorImpl.DESCRIPTOR);
            if (iin != null && (iin instanceof IImageProcessorImpl)) {
                return (IImageProcessorImpl) iin;
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
                    return "onNextImageAvailable";
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
                data.enforceInterface(IImageProcessorImpl.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IImageProcessorImpl.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            OutputConfigId _arg0 = (OutputConfigId) data.readTypedObject(OutputConfigId.CREATOR);
                            ParcelImage _arg1 = (ParcelImage) data.readTypedObject(ParcelImage.CREATOR);
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            onNextImageAvailable(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IImageProcessorImpl {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IImageProcessorImpl.DESCRIPTOR;
            }

            @Override // android.hardware.camera2.extension.IImageProcessorImpl
            public void onNextImageAvailable(OutputConfigId outputConfigId, ParcelImage image, String physicalCameraId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImageProcessorImpl.DESCRIPTOR);
                    _data.writeTypedObject(outputConfigId, 0);
                    _data.writeTypedObject(image, 0);
                    _data.writeString(physicalCameraId);
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
