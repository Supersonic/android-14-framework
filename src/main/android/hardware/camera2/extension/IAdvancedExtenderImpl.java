package android.hardware.camera2.extension;

import android.hardware.camera2.extension.ISessionProcessorImpl;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IAdvancedExtenderImpl extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.camera2.extension.IAdvancedExtenderImpl";

    CameraMetadataNative getAvailableCaptureRequestKeys(String str) throws RemoteException;

    CameraMetadataNative getAvailableCaptureResultKeys(String str) throws RemoteException;

    LatencyRange getEstimatedCaptureLatencyRange(String str, Size size, int i) throws RemoteException;

    ISessionProcessorImpl getSessionProcessor() throws RemoteException;

    List<SizeList> getSupportedCaptureOutputResolutions(String str) throws RemoteException;

    List<SizeList> getSupportedPostviewResolutions(Size size) throws RemoteException;

    List<SizeList> getSupportedPreviewOutputResolutions(String str) throws RemoteException;

    void init(String str) throws RemoteException;

    boolean isCaptureProcessProgressAvailable() throws RemoteException;

    boolean isExtensionAvailable(String str) throws RemoteException;

    boolean isPostviewAvailable() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAdvancedExtenderImpl {
        @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
        public boolean isExtensionAvailable(String cameraId) throws RemoteException {
            return false;
        }

        @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
        public void init(String cameraId) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
        public LatencyRange getEstimatedCaptureLatencyRange(String cameraId, Size outputSize, int format) throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
        public List<SizeList> getSupportedPreviewOutputResolutions(String cameraId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
        public List<SizeList> getSupportedCaptureOutputResolutions(String cameraId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
        public List<SizeList> getSupportedPostviewResolutions(Size captureSize) throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
        public ISessionProcessorImpl getSessionProcessor() throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
        public CameraMetadataNative getAvailableCaptureRequestKeys(String cameraId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
        public CameraMetadataNative getAvailableCaptureResultKeys(String cameraId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
        public boolean isCaptureProcessProgressAvailable() throws RemoteException {
            return false;
        }

        @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
        public boolean isPostviewAvailable() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAdvancedExtenderImpl {
        static final int TRANSACTION_getAvailableCaptureRequestKeys = 8;
        static final int TRANSACTION_getAvailableCaptureResultKeys = 9;
        static final int TRANSACTION_getEstimatedCaptureLatencyRange = 3;
        static final int TRANSACTION_getSessionProcessor = 7;
        static final int TRANSACTION_getSupportedCaptureOutputResolutions = 5;
        static final int TRANSACTION_getSupportedPostviewResolutions = 6;
        static final int TRANSACTION_getSupportedPreviewOutputResolutions = 4;
        static final int TRANSACTION_init = 2;
        static final int TRANSACTION_isCaptureProcessProgressAvailable = 10;
        static final int TRANSACTION_isExtensionAvailable = 1;
        static final int TRANSACTION_isPostviewAvailable = 11;

        public Stub() {
            attachInterface(this, IAdvancedExtenderImpl.DESCRIPTOR);
        }

        public static IAdvancedExtenderImpl asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAdvancedExtenderImpl.DESCRIPTOR);
            if (iin != null && (iin instanceof IAdvancedExtenderImpl)) {
                return (IAdvancedExtenderImpl) iin;
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
                    return "isExtensionAvailable";
                case 2:
                    return "init";
                case 3:
                    return "getEstimatedCaptureLatencyRange";
                case 4:
                    return "getSupportedPreviewOutputResolutions";
                case 5:
                    return "getSupportedCaptureOutputResolutions";
                case 6:
                    return "getSupportedPostviewResolutions";
                case 7:
                    return "getSessionProcessor";
                case 8:
                    return "getAvailableCaptureRequestKeys";
                case 9:
                    return "getAvailableCaptureResultKeys";
                case 10:
                    return "isCaptureProcessProgressAvailable";
                case 11:
                    return "isPostviewAvailable";
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
                data.enforceInterface(IAdvancedExtenderImpl.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAdvancedExtenderImpl.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result = isExtensionAvailable(_arg0);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            init(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            Size _arg1 = (Size) data.readTypedObject(Size.CREATOR);
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            LatencyRange _result2 = getEstimatedCaptureLatencyRange(_arg03, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            List<SizeList> _result3 = getSupportedPreviewOutputResolutions(_arg04);
                            reply.writeNoException();
                            reply.writeTypedList(_result3, 1);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            data.enforceNoDataAvail();
                            List<SizeList> _result4 = getSupportedCaptureOutputResolutions(_arg05);
                            reply.writeNoException();
                            reply.writeTypedList(_result4, 1);
                            break;
                        case 6:
                            Size _arg06 = (Size) data.readTypedObject(Size.CREATOR);
                            data.enforceNoDataAvail();
                            List<SizeList> _result5 = getSupportedPostviewResolutions(_arg06);
                            reply.writeNoException();
                            reply.writeTypedList(_result5, 1);
                            break;
                        case 7:
                            ISessionProcessorImpl _result6 = getSessionProcessor();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result6);
                            break;
                        case 8:
                            String _arg07 = data.readString();
                            data.enforceNoDataAvail();
                            CameraMetadataNative _result7 = getAvailableCaptureRequestKeys(_arg07);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 9:
                            String _arg08 = data.readString();
                            data.enforceNoDataAvail();
                            CameraMetadataNative _result8 = getAvailableCaptureResultKeys(_arg08);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            break;
                        case 10:
                            boolean _result9 = isCaptureProcessProgressAvailable();
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 11:
                            boolean _result10 = isPostviewAvailable();
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IAdvancedExtenderImpl {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAdvancedExtenderImpl.DESCRIPTOR;
            }

            @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
            public boolean isExtensionAvailable(String cameraId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdvancedExtenderImpl.DESCRIPTOR);
                    _data.writeString(cameraId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
            public void init(String cameraId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdvancedExtenderImpl.DESCRIPTOR);
                    _data.writeString(cameraId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
            public LatencyRange getEstimatedCaptureLatencyRange(String cameraId, Size outputSize, int format) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdvancedExtenderImpl.DESCRIPTOR);
                    _data.writeString(cameraId);
                    _data.writeTypedObject(outputSize, 0);
                    _data.writeInt(format);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    LatencyRange _result = (LatencyRange) _reply.readTypedObject(LatencyRange.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
            public List<SizeList> getSupportedPreviewOutputResolutions(String cameraId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdvancedExtenderImpl.DESCRIPTOR);
                    _data.writeString(cameraId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    List<SizeList> _result = _reply.createTypedArrayList(SizeList.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
            public List<SizeList> getSupportedCaptureOutputResolutions(String cameraId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdvancedExtenderImpl.DESCRIPTOR);
                    _data.writeString(cameraId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    List<SizeList> _result = _reply.createTypedArrayList(SizeList.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
            public List<SizeList> getSupportedPostviewResolutions(Size captureSize) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdvancedExtenderImpl.DESCRIPTOR);
                    _data.writeTypedObject(captureSize, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    List<SizeList> _result = _reply.createTypedArrayList(SizeList.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
            public ISessionProcessorImpl getSessionProcessor() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdvancedExtenderImpl.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    ISessionProcessorImpl _result = ISessionProcessorImpl.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
            public CameraMetadataNative getAvailableCaptureRequestKeys(String cameraId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdvancedExtenderImpl.DESCRIPTOR);
                    _data.writeString(cameraId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    CameraMetadataNative _result = (CameraMetadataNative) _reply.readTypedObject(CameraMetadataNative.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
            public CameraMetadataNative getAvailableCaptureResultKeys(String cameraId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdvancedExtenderImpl.DESCRIPTOR);
                    _data.writeString(cameraId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    CameraMetadataNative _result = (CameraMetadataNative) _reply.readTypedObject(CameraMetadataNative.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
            public boolean isCaptureProcessProgressAvailable() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdvancedExtenderImpl.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IAdvancedExtenderImpl
            public boolean isPostviewAvailable() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdvancedExtenderImpl.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 10;
        }
    }
}
