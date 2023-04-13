package android.hardware;

import android.hardware.ICamera;
import android.hardware.ICameraClient;
import android.hardware.ICameraServiceListener;
import android.hardware.camera2.ICameraDeviceCallbacks;
import android.hardware.camera2.ICameraDeviceUser;
import android.hardware.camera2.ICameraInjectionCallback;
import android.hardware.camera2.ICameraInjectionSession;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.params.VendorTagDescriptor;
import android.hardware.camera2.params.VendorTagDescriptorCache;
import android.hardware.camera2.utils.CameraIdAndSessionConfiguration;
import android.hardware.camera2.utils.ConcurrentCameraIdCombination;
import android.media.MediaMetrics;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ICameraService extends IInterface {
    public static final int API_VERSION_1 = 1;
    public static final int API_VERSION_2 = 2;
    public static final int CAMERA_TYPE_ALL = 1;
    public static final int CAMERA_TYPE_BACKWARD_COMPATIBLE = 0;
    public static final int DEVICE_STATE_BACK_COVERED = 1;
    public static final int DEVICE_STATE_FOLDED = 4;
    public static final int DEVICE_STATE_FRONT_COVERED = 2;
    public static final int DEVICE_STATE_LAST_FRAMEWORK_BIT = Integer.MIN_VALUE;
    public static final int DEVICE_STATE_NORMAL = 0;
    public static final int ERROR_ALREADY_EXISTS = 2;
    public static final int ERROR_CAMERA_IN_USE = 7;
    public static final int ERROR_DEPRECATED_HAL = 9;
    public static final int ERROR_DISABLED = 6;
    public static final int ERROR_DISCONNECTED = 4;
    public static final int ERROR_ILLEGAL_ARGUMENT = 3;
    public static final int ERROR_INVALID_OPERATION = 10;
    public static final int ERROR_MAX_CAMERAS_IN_USE = 8;
    public static final int ERROR_PERMISSION_DENIED = 1;
    public static final int ERROR_TIMED_OUT = 5;
    public static final int EVENT_NONE = 0;
    public static final int EVENT_USB_DEVICE_ATTACHED = 2;
    public static final int EVENT_USB_DEVICE_DETACHED = 3;
    public static final int EVENT_USER_SWITCHED = 1;
    public static final int USE_CALLING_PID = -1;
    public static final int USE_CALLING_UID = -1;

    CameraStatus[] addListener(ICameraServiceListener iCameraServiceListener) throws RemoteException;

    ICamera connect(ICameraClient iCameraClient, int i, String str, int i2, int i3, int i4, boolean z) throws RemoteException;

    ICameraDeviceUser connectDevice(ICameraDeviceCallbacks iCameraDeviceCallbacks, String str, String str2, String str3, int i, int i2, int i3, boolean z) throws RemoteException;

    CameraMetadataNative getCameraCharacteristics(String str, int i, boolean z) throws RemoteException;

    CameraInfo getCameraInfo(int i, boolean z) throws RemoteException;

    VendorTagDescriptorCache getCameraVendorTagCache() throws RemoteException;

    VendorTagDescriptor getCameraVendorTagDescriptor() throws RemoteException;

    ConcurrentCameraIdCombination[] getConcurrentCameraIds() throws RemoteException;

    String getLegacyParameters(int i) throws RemoteException;

    int getNumberOfCameras(int i) throws RemoteException;

    int getTorchStrengthLevel(String str) throws RemoteException;

    ICameraInjectionSession injectCamera(String str, String str2, String str3, ICameraInjectionCallback iCameraInjectionCallback) throws RemoteException;

    boolean isConcurrentSessionConfigurationSupported(CameraIdAndSessionConfiguration[] cameraIdAndSessionConfigurationArr, int i) throws RemoteException;

    boolean isHiddenPhysicalCamera(String str) throws RemoteException;

    void notifyDeviceStateChange(long j) throws RemoteException;

    void notifyDisplayConfigurationChange() throws RemoteException;

    void notifySystemEvent(int i, int[] iArr) throws RemoteException;

    void removeListener(ICameraServiceListener iCameraServiceListener) throws RemoteException;

    void setTorchMode(String str, boolean z, IBinder iBinder) throws RemoteException;

    boolean supportsCameraApi(String str, int i) throws RemoteException;

    void turnOnTorchWithStrengthLevel(String str, int i, IBinder iBinder) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ICameraService {
        @Override // android.hardware.ICameraService
        public int getNumberOfCameras(int type) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.ICameraService
        public CameraInfo getCameraInfo(int cameraId, boolean overrideToPortrait) throws RemoteException {
            return null;
        }

        @Override // android.hardware.ICameraService
        public ICamera connect(ICameraClient client, int cameraId, String opPackageName, int clientUid, int clientPid, int targetSdkVersion, boolean overrideToPortrait) throws RemoteException {
            return null;
        }

        @Override // android.hardware.ICameraService
        public ICameraDeviceUser connectDevice(ICameraDeviceCallbacks callbacks, String cameraId, String opPackageName, String featureId, int clientUid, int oomScoreOffset, int targetSdkVersion, boolean overrideToPortrait) throws RemoteException {
            return null;
        }

        @Override // android.hardware.ICameraService
        public CameraStatus[] addListener(ICameraServiceListener listener) throws RemoteException {
            return null;
        }

        @Override // android.hardware.ICameraService
        public ConcurrentCameraIdCombination[] getConcurrentCameraIds() throws RemoteException {
            return null;
        }

        @Override // android.hardware.ICameraService
        public boolean isConcurrentSessionConfigurationSupported(CameraIdAndSessionConfiguration[] sessions, int targetSdkVersion) throws RemoteException {
            return false;
        }

        @Override // android.hardware.ICameraService
        public void removeListener(ICameraServiceListener listener) throws RemoteException {
        }

        @Override // android.hardware.ICameraService
        public CameraMetadataNative getCameraCharacteristics(String cameraId, int targetSdkVersion, boolean overrideToPortrait) throws RemoteException {
            return null;
        }

        @Override // android.hardware.ICameraService
        public VendorTagDescriptor getCameraVendorTagDescriptor() throws RemoteException {
            return null;
        }

        @Override // android.hardware.ICameraService
        public VendorTagDescriptorCache getCameraVendorTagCache() throws RemoteException {
            return null;
        }

        @Override // android.hardware.ICameraService
        public String getLegacyParameters(int cameraId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.ICameraService
        public boolean supportsCameraApi(String cameraId, int apiVersion) throws RemoteException {
            return false;
        }

        @Override // android.hardware.ICameraService
        public boolean isHiddenPhysicalCamera(String cameraId) throws RemoteException {
            return false;
        }

        @Override // android.hardware.ICameraService
        public ICameraInjectionSession injectCamera(String packageName, String internalCamId, String externalCamId, ICameraInjectionCallback CameraInjectionCallback) throws RemoteException {
            return null;
        }

        @Override // android.hardware.ICameraService
        public void setTorchMode(String cameraId, boolean enabled, IBinder clientBinder) throws RemoteException {
        }

        @Override // android.hardware.ICameraService
        public void turnOnTorchWithStrengthLevel(String cameraId, int strengthLevel, IBinder clientBinder) throws RemoteException {
        }

        @Override // android.hardware.ICameraService
        public int getTorchStrengthLevel(String cameraId) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.ICameraService
        public void notifySystemEvent(int eventId, int[] args) throws RemoteException {
        }

        @Override // android.hardware.ICameraService
        public void notifyDisplayConfigurationChange() throws RemoteException {
        }

        @Override // android.hardware.ICameraService
        public void notifyDeviceStateChange(long newState) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ICameraService {
        public static final String DESCRIPTOR = "android.hardware.ICameraService";
        static final int TRANSACTION_addListener = 5;
        static final int TRANSACTION_connect = 3;
        static final int TRANSACTION_connectDevice = 4;
        static final int TRANSACTION_getCameraCharacteristics = 9;
        static final int TRANSACTION_getCameraInfo = 2;
        static final int TRANSACTION_getCameraVendorTagCache = 11;
        static final int TRANSACTION_getCameraVendorTagDescriptor = 10;
        static final int TRANSACTION_getConcurrentCameraIds = 6;
        static final int TRANSACTION_getLegacyParameters = 12;
        static final int TRANSACTION_getNumberOfCameras = 1;
        static final int TRANSACTION_getTorchStrengthLevel = 18;
        static final int TRANSACTION_injectCamera = 15;
        static final int TRANSACTION_isConcurrentSessionConfigurationSupported = 7;
        static final int TRANSACTION_isHiddenPhysicalCamera = 14;
        static final int TRANSACTION_notifyDeviceStateChange = 21;
        static final int TRANSACTION_notifyDisplayConfigurationChange = 20;
        static final int TRANSACTION_notifySystemEvent = 19;
        static final int TRANSACTION_removeListener = 8;
        static final int TRANSACTION_setTorchMode = 16;
        static final int TRANSACTION_supportsCameraApi = 13;
        static final int TRANSACTION_turnOnTorchWithStrengthLevel = 17;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICameraService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ICameraService)) {
                return (ICameraService) iin;
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
                    return "getNumberOfCameras";
                case 2:
                    return "getCameraInfo";
                case 3:
                    return MediaMetrics.Value.CONNECT;
                case 4:
                    return "connectDevice";
                case 5:
                    return "addListener";
                case 6:
                    return "getConcurrentCameraIds";
                case 7:
                    return "isConcurrentSessionConfigurationSupported";
                case 8:
                    return "removeListener";
                case 9:
                    return "getCameraCharacteristics";
                case 10:
                    return "getCameraVendorTagDescriptor";
                case 11:
                    return "getCameraVendorTagCache";
                case 12:
                    return "getLegacyParameters";
                case 13:
                    return "supportsCameraApi";
                case 14:
                    return "isHiddenPhysicalCamera";
                case 15:
                    return "injectCamera";
                case 16:
                    return "setTorchMode";
                case 17:
                    return "turnOnTorchWithStrengthLevel";
                case 18:
                    return "getTorchStrengthLevel";
                case 19:
                    return "notifySystemEvent";
                case 20:
                    return "notifyDisplayConfigurationChange";
                case 21:
                    return "notifyDeviceStateChange";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result = getNumberOfCameras(_arg0);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            CameraInfo _result2 = getCameraInfo(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            ICameraClient _arg03 = ICameraClient.Stub.asInterface(data.readStrongBinder());
                            int _arg12 = data.readInt();
                            String _arg2 = data.readString();
                            int _arg3 = data.readInt();
                            int _arg4 = data.readInt();
                            int _arg5 = data.readInt();
                            boolean _arg6 = data.readBoolean();
                            data.enforceNoDataAvail();
                            ICamera _result3 = connect(_arg03, _arg12, _arg2, _arg3, _arg4, _arg5, _arg6);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        case 4:
                            ICameraDeviceCallbacks _arg04 = ICameraDeviceCallbacks.Stub.asInterface(data.readStrongBinder());
                            String _arg13 = data.readString();
                            String _arg22 = data.readString();
                            String _arg32 = data.readString();
                            int _arg42 = data.readInt();
                            int _arg52 = data.readInt();
                            int _arg62 = data.readInt();
                            boolean _arg7 = data.readBoolean();
                            data.enforceNoDataAvail();
                            ICameraDeviceUser _result4 = connectDevice(_arg04, _arg13, _arg22, _arg32, _arg42, _arg52, _arg62, _arg7);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result4);
                            break;
                        case 5:
                            ICameraServiceListener _arg05 = ICameraServiceListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            CameraStatus[] _result5 = addListener(_arg05);
                            reply.writeNoException();
                            reply.writeTypedArray(_result5, 1);
                            break;
                        case 6:
                            ConcurrentCameraIdCombination[] _result6 = getConcurrentCameraIds();
                            reply.writeNoException();
                            reply.writeTypedArray(_result6, 1);
                            break;
                        case 7:
                            CameraIdAndSessionConfiguration[] _arg06 = (CameraIdAndSessionConfiguration[]) data.createTypedArray(CameraIdAndSessionConfiguration.CREATOR);
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result7 = isConcurrentSessionConfigurationSupported(_arg06, _arg14);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 8:
                            ICameraServiceListener _arg07 = ICameraServiceListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeListener(_arg07);
                            reply.writeNoException();
                            break;
                        case 9:
                            String _arg08 = data.readString();
                            int _arg15 = data.readInt();
                            boolean _arg23 = data.readBoolean();
                            data.enforceNoDataAvail();
                            CameraMetadataNative _result8 = getCameraCharacteristics(_arg08, _arg15, _arg23);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            break;
                        case 10:
                            VendorTagDescriptor _result9 = getCameraVendorTagDescriptor();
                            reply.writeNoException();
                            reply.writeTypedObject(_result9, 1);
                            break;
                        case 11:
                            VendorTagDescriptorCache _result10 = getCameraVendorTagCache();
                            reply.writeNoException();
                            reply.writeTypedObject(_result10, 1);
                            break;
                        case 12:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result11 = getLegacyParameters(_arg09);
                            reply.writeNoException();
                            reply.writeString(_result11);
                            break;
                        case 13:
                            String _arg010 = data.readString();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result12 = supportsCameraApi(_arg010, _arg16);
                            reply.writeNoException();
                            reply.writeBoolean(_result12);
                            break;
                        case 14:
                            String _arg011 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result13 = isHiddenPhysicalCamera(_arg011);
                            reply.writeNoException();
                            reply.writeBoolean(_result13);
                            break;
                        case 15:
                            String _arg012 = data.readString();
                            String _arg17 = data.readString();
                            String _arg24 = data.readString();
                            ICameraInjectionCallback _arg33 = ICameraInjectionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            ICameraInjectionSession _result14 = injectCamera(_arg012, _arg17, _arg24, _arg33);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result14);
                            break;
                        case 16:
                            String _arg013 = data.readString();
                            boolean _arg18 = data.readBoolean();
                            IBinder _arg25 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            setTorchMode(_arg013, _arg18, _arg25);
                            reply.writeNoException();
                            break;
                        case 17:
                            String _arg014 = data.readString();
                            int _arg19 = data.readInt();
                            IBinder _arg26 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            turnOnTorchWithStrengthLevel(_arg014, _arg19, _arg26);
                            reply.writeNoException();
                            break;
                        case 18:
                            String _arg015 = data.readString();
                            data.enforceNoDataAvail();
                            int _result15 = getTorchStrengthLevel(_arg015);
                            reply.writeNoException();
                            reply.writeInt(_result15);
                            break;
                        case 19:
                            int _arg016 = data.readInt();
                            int[] _arg110 = data.createIntArray();
                            data.enforceNoDataAvail();
                            notifySystemEvent(_arg016, _arg110);
                            break;
                        case 20:
                            notifyDisplayConfigurationChange();
                            break;
                        case 21:
                            long _arg017 = data.readLong();
                            data.enforceNoDataAvail();
                            notifyDeviceStateChange(_arg017);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements ICameraService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.hardware.ICameraService
            public int getNumberOfCameras(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public CameraInfo getCameraInfo(int cameraId, boolean overrideToPortrait) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cameraId);
                    _data.writeBoolean(overrideToPortrait);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    CameraInfo _result = (CameraInfo) _reply.readTypedObject(CameraInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public ICamera connect(ICameraClient client, int cameraId, String opPackageName, int clientUid, int clientPid, int targetSdkVersion, boolean overrideToPortrait) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(client);
                    _data.writeInt(cameraId);
                    _data.writeString(opPackageName);
                    _data.writeInt(clientUid);
                    _data.writeInt(clientPid);
                    _data.writeInt(targetSdkVersion);
                    _data.writeBoolean(overrideToPortrait);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ICamera _result = ICamera.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public ICameraDeviceUser connectDevice(ICameraDeviceCallbacks callbacks, String cameraId, String opPackageName, String featureId, int clientUid, int oomScoreOffset, int targetSdkVersion, boolean overrideToPortrait) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callbacks);
                    _data.writeString(cameraId);
                    _data.writeString(opPackageName);
                    _data.writeString(featureId);
                    _data.writeInt(clientUid);
                    _data.writeInt(oomScoreOffset);
                    _data.writeInt(targetSdkVersion);
                    _data.writeBoolean(overrideToPortrait);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    ICameraDeviceUser _result = ICameraDeviceUser.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public CameraStatus[] addListener(ICameraServiceListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    CameraStatus[] _result = (CameraStatus[]) _reply.createTypedArray(CameraStatus.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public ConcurrentCameraIdCombination[] getConcurrentCameraIds() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    ConcurrentCameraIdCombination[] _result = (ConcurrentCameraIdCombination[]) _reply.createTypedArray(ConcurrentCameraIdCombination.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public boolean isConcurrentSessionConfigurationSupported(CameraIdAndSessionConfiguration[] sessions, int targetSdkVersion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(sessions, 0);
                    _data.writeInt(targetSdkVersion);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public void removeListener(ICameraServiceListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public CameraMetadataNative getCameraCharacteristics(String cameraId, int targetSdkVersion, boolean overrideToPortrait) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cameraId);
                    _data.writeInt(targetSdkVersion);
                    _data.writeBoolean(overrideToPortrait);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    CameraMetadataNative _result = (CameraMetadataNative) _reply.readTypedObject(CameraMetadataNative.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public VendorTagDescriptor getCameraVendorTagDescriptor() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    VendorTagDescriptor _result = (VendorTagDescriptor) _reply.readTypedObject(VendorTagDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public VendorTagDescriptorCache getCameraVendorTagCache() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    VendorTagDescriptorCache _result = (VendorTagDescriptorCache) _reply.readTypedObject(VendorTagDescriptorCache.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public String getLegacyParameters(int cameraId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cameraId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public boolean supportsCameraApi(String cameraId, int apiVersion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cameraId);
                    _data.writeInt(apiVersion);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public boolean isHiddenPhysicalCamera(String cameraId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cameraId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public ICameraInjectionSession injectCamera(String packageName, String internalCamId, String externalCamId, ICameraInjectionCallback CameraInjectionCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(internalCamId);
                    _data.writeString(externalCamId);
                    _data.writeStrongInterface(CameraInjectionCallback);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    ICameraInjectionSession _result = ICameraInjectionSession.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public void setTorchMode(String cameraId, boolean enabled, IBinder clientBinder) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cameraId);
                    _data.writeBoolean(enabled);
                    _data.writeStrongBinder(clientBinder);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public void turnOnTorchWithStrengthLevel(String cameraId, int strengthLevel, IBinder clientBinder) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cameraId);
                    _data.writeInt(strengthLevel);
                    _data.writeStrongBinder(clientBinder);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public int getTorchStrengthLevel(String cameraId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cameraId);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public void notifySystemEvent(int eventId, int[] args) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(eventId);
                    _data.writeIntArray(args);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public void notifyDisplayConfigurationChange() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.ICameraService
            public void notifyDeviceStateChange(long newState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(newState);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 20;
        }
    }
}
