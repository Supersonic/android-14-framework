package android.hardware.camera2.extension;

import android.hardware.camera2.extension.IPreviewImageProcessorImpl;
import android.hardware.camera2.extension.IRequestUpdateProcessorImpl;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IPreviewExtenderImpl extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.camera2.extension.IPreviewExtenderImpl";
    public static final int PROCESSOR_TYPE_IMAGE_PROCESSOR = 1;
    public static final int PROCESSOR_TYPE_NONE = 2;
    public static final int PROCESSOR_TYPE_REQUEST_UPDATE_ONLY = 0;

    CaptureStageImpl getCaptureStage() throws RemoteException;

    IPreviewImageProcessorImpl getPreviewImageProcessor() throws RemoteException;

    int getProcessorType() throws RemoteException;

    IRequestUpdateProcessorImpl getRequestUpdateProcessor() throws RemoteException;

    int getSessionType() throws RemoteException;

    List<SizeList> getSupportedResolutions() throws RemoteException;

    void init(String str, CameraMetadataNative cameraMetadataNative) throws RemoteException;

    boolean isExtensionAvailable(String str, CameraMetadataNative cameraMetadataNative) throws RemoteException;

    void onDeInit() throws RemoteException;

    CaptureStageImpl onDisableSession() throws RemoteException;

    CaptureStageImpl onEnableSession() throws RemoteException;

    void onInit(String str, CameraMetadataNative cameraMetadataNative) throws RemoteException;

    CaptureStageImpl onPresetSession() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IPreviewExtenderImpl {
        @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
        public void onInit(String cameraId, CameraMetadataNative cameraCharacteristics) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
        public void onDeInit() throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
        public CaptureStageImpl onPresetSession() throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
        public CaptureStageImpl onEnableSession() throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
        public CaptureStageImpl onDisableSession() throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
        public void init(String cameraId, CameraMetadataNative chars) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
        public boolean isExtensionAvailable(String cameraId, CameraMetadataNative chars) throws RemoteException {
            return false;
        }

        @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
        public CaptureStageImpl getCaptureStage() throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
        public int getSessionType() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
        public int getProcessorType() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
        public IPreviewImageProcessorImpl getPreviewImageProcessor() throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
        public IRequestUpdateProcessorImpl getRequestUpdateProcessor() throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
        public List<SizeList> getSupportedResolutions() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IPreviewExtenderImpl {
        static final int TRANSACTION_getCaptureStage = 8;
        static final int TRANSACTION_getPreviewImageProcessor = 11;
        static final int TRANSACTION_getProcessorType = 10;
        static final int TRANSACTION_getRequestUpdateProcessor = 12;
        static final int TRANSACTION_getSessionType = 9;
        static final int TRANSACTION_getSupportedResolutions = 13;
        static final int TRANSACTION_init = 6;
        static final int TRANSACTION_isExtensionAvailable = 7;
        static final int TRANSACTION_onDeInit = 2;
        static final int TRANSACTION_onDisableSession = 5;
        static final int TRANSACTION_onEnableSession = 4;
        static final int TRANSACTION_onInit = 1;
        static final int TRANSACTION_onPresetSession = 3;

        public Stub() {
            attachInterface(this, IPreviewExtenderImpl.DESCRIPTOR);
        }

        public static IPreviewExtenderImpl asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IPreviewExtenderImpl.DESCRIPTOR);
            if (iin != null && (iin instanceof IPreviewExtenderImpl)) {
                return (IPreviewExtenderImpl) iin;
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
                    return "onInit";
                case 2:
                    return "onDeInit";
                case 3:
                    return "onPresetSession";
                case 4:
                    return "onEnableSession";
                case 5:
                    return "onDisableSession";
                case 6:
                    return "init";
                case 7:
                    return "isExtensionAvailable";
                case 8:
                    return "getCaptureStage";
                case 9:
                    return "getSessionType";
                case 10:
                    return "getProcessorType";
                case 11:
                    return "getPreviewImageProcessor";
                case 12:
                    return "getRequestUpdateProcessor";
                case 13:
                    return "getSupportedResolutions";
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
                data.enforceInterface(IPreviewExtenderImpl.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IPreviewExtenderImpl.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            CameraMetadataNative _arg1 = (CameraMetadataNative) data.readTypedObject(CameraMetadataNative.CREATOR);
                            data.enforceNoDataAvail();
                            onInit(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            onDeInit();
                            reply.writeNoException();
                            break;
                        case 3:
                            CaptureStageImpl _result = onPresetSession();
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 4:
                            CaptureStageImpl _result2 = onEnableSession();
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 5:
                            CaptureStageImpl _result3 = onDisableSession();
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 6:
                            String _arg02 = data.readString();
                            CameraMetadataNative _arg12 = (CameraMetadataNative) data.readTypedObject(CameraMetadataNative.CREATOR);
                            data.enforceNoDataAvail();
                            init(_arg02, _arg12);
                            reply.writeNoException();
                            break;
                        case 7:
                            String _arg03 = data.readString();
                            CameraMetadataNative _arg13 = (CameraMetadataNative) data.readTypedObject(CameraMetadataNative.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result4 = isExtensionAvailable(_arg03, _arg13);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 8:
                            CaptureStageImpl _result5 = getCaptureStage();
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 9:
                            int _result6 = getSessionType();
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 10:
                            int _result7 = getProcessorType();
                            reply.writeNoException();
                            reply.writeInt(_result7);
                            break;
                        case 11:
                            IPreviewImageProcessorImpl _result8 = getPreviewImageProcessor();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result8);
                            break;
                        case 12:
                            IRequestUpdateProcessorImpl _result9 = getRequestUpdateProcessor();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result9);
                            break;
                        case 13:
                            List<SizeList> _result10 = getSupportedResolutions();
                            reply.writeNoException();
                            reply.writeTypedList(_result10, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IPreviewExtenderImpl {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPreviewExtenderImpl.DESCRIPTOR;
            }

            @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
            public void onInit(String cameraId, CameraMetadataNative cameraCharacteristics) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPreviewExtenderImpl.DESCRIPTOR);
                    _data.writeString(cameraId);
                    _data.writeTypedObject(cameraCharacteristics, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
            public void onDeInit() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPreviewExtenderImpl.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
            public CaptureStageImpl onPresetSession() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPreviewExtenderImpl.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    CaptureStageImpl _result = (CaptureStageImpl) _reply.readTypedObject(CaptureStageImpl.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
            public CaptureStageImpl onEnableSession() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPreviewExtenderImpl.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    CaptureStageImpl _result = (CaptureStageImpl) _reply.readTypedObject(CaptureStageImpl.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
            public CaptureStageImpl onDisableSession() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPreviewExtenderImpl.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    CaptureStageImpl _result = (CaptureStageImpl) _reply.readTypedObject(CaptureStageImpl.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
            public void init(String cameraId, CameraMetadataNative chars) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPreviewExtenderImpl.DESCRIPTOR);
                    _data.writeString(cameraId);
                    _data.writeTypedObject(chars, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
            public boolean isExtensionAvailable(String cameraId, CameraMetadataNative chars) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPreviewExtenderImpl.DESCRIPTOR);
                    _data.writeString(cameraId);
                    _data.writeTypedObject(chars, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
            public CaptureStageImpl getCaptureStage() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPreviewExtenderImpl.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    CaptureStageImpl _result = (CaptureStageImpl) _reply.readTypedObject(CaptureStageImpl.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
            public int getSessionType() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPreviewExtenderImpl.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
            public int getProcessorType() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPreviewExtenderImpl.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
            public IPreviewImageProcessorImpl getPreviewImageProcessor() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPreviewExtenderImpl.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    IPreviewImageProcessorImpl _result = IPreviewImageProcessorImpl.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
            public IRequestUpdateProcessorImpl getRequestUpdateProcessor() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPreviewExtenderImpl.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    IRequestUpdateProcessorImpl _result = IRequestUpdateProcessorImpl.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.IPreviewExtenderImpl
            public List<SizeList> getSupportedResolutions() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPreviewExtenderImpl.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    List<SizeList> _result = _reply.createTypedArrayList(SizeList.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 12;
        }
    }
}
