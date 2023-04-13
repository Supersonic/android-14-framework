package android.hardware.camera2;

import android.hardware.camera2.ICameraDeviceCallbacks;
import android.hardware.camera2.ICameraOfflineSession;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.hardware.camera2.utils.SubmitInfo;
import android.media.MediaMetrics;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.Surface;
/* loaded from: classes.dex */
public interface ICameraDeviceUser extends IInterface {
    public static final int AUDIO_RESTRICTION_NONE = 0;
    public static final int AUDIO_RESTRICTION_VIBRATION = 1;
    public static final int AUDIO_RESTRICTION_VIBRATION_SOUND = 3;
    public static final int CONSTRAINED_HIGH_SPEED_MODE = 1;
    public static final int NORMAL_MODE = 0;
    public static final int NO_IN_FLIGHT_REPEATING_FRAMES = -1;
    public static final int TEMPLATE_MANUAL = 6;
    public static final int TEMPLATE_PREVIEW = 1;
    public static final int TEMPLATE_RECORD = 3;
    public static final int TEMPLATE_STILL_CAPTURE = 2;
    public static final int TEMPLATE_VIDEO_SNAPSHOT = 4;
    public static final int TEMPLATE_ZERO_SHUTTER_LAG = 5;
    public static final int VENDOR_MODE_START = 32768;

    void beginConfigure() throws RemoteException;

    long cancelRequest(int i) throws RemoteException;

    CameraMetadataNative createDefaultRequest(int i) throws RemoteException;

    int createInputStream(int i, int i2, int i3, boolean z) throws RemoteException;

    int createStream(OutputConfiguration outputConfiguration) throws RemoteException;

    void deleteStream(int i) throws RemoteException;

    void disconnect() throws RemoteException;

    int[] endConfigure(int i, CameraMetadataNative cameraMetadataNative, long j) throws RemoteException;

    void finalizeOutputConfigurations(int i, OutputConfiguration outputConfiguration) throws RemoteException;

    long flush() throws RemoteException;

    CameraMetadataNative getCameraInfo() throws RemoteException;

    int getGlobalAudioRestriction() throws RemoteException;

    Surface getInputSurface() throws RemoteException;

    boolean isSessionConfigurationSupported(SessionConfiguration sessionConfiguration) throws RemoteException;

    void prepare(int i) throws RemoteException;

    void prepare2(int i, int i2) throws RemoteException;

    void setCameraAudioRestriction(int i) throws RemoteException;

    SubmitInfo submitRequest(CaptureRequest captureRequest, boolean z) throws RemoteException;

    SubmitInfo submitRequestList(CaptureRequest[] captureRequestArr, boolean z) throws RemoteException;

    ICameraOfflineSession switchToOffline(ICameraDeviceCallbacks iCameraDeviceCallbacks, int[] iArr) throws RemoteException;

    void tearDown(int i) throws RemoteException;

    void updateOutputConfiguration(int i, OutputConfiguration outputConfiguration) throws RemoteException;

    void waitUntilIdle() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ICameraDeviceUser {
        @Override // android.hardware.camera2.ICameraDeviceUser
        public void disconnect() throws RemoteException {
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public SubmitInfo submitRequest(CaptureRequest request, boolean streaming) throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public SubmitInfo submitRequestList(CaptureRequest[] requestList, boolean streaming) throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public long cancelRequest(int requestId) throws RemoteException {
            return 0L;
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public void beginConfigure() throws RemoteException {
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public int[] endConfigure(int operatingMode, CameraMetadataNative sessionParams, long startTimeMs) throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public boolean isSessionConfigurationSupported(SessionConfiguration sessionConfiguration) throws RemoteException {
            return false;
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public void deleteStream(int streamId) throws RemoteException {
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public int createStream(OutputConfiguration outputConfiguration) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public int createInputStream(int width, int height, int format, boolean isMultiResolution) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public Surface getInputSurface() throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public CameraMetadataNative createDefaultRequest(int templateId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public CameraMetadataNative getCameraInfo() throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public void waitUntilIdle() throws RemoteException {
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public long flush() throws RemoteException {
            return 0L;
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public void prepare(int streamId) throws RemoteException {
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public void tearDown(int streamId) throws RemoteException {
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public void prepare2(int maxCount, int streamId) throws RemoteException {
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public void updateOutputConfiguration(int streamId, OutputConfiguration outputConfiguration) throws RemoteException {
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public void finalizeOutputConfigurations(int streamId, OutputConfiguration outputConfiguration) throws RemoteException {
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public void setCameraAudioRestriction(int mode) throws RemoteException {
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public int getGlobalAudioRestriction() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.camera2.ICameraDeviceUser
        public ICameraOfflineSession switchToOffline(ICameraDeviceCallbacks callbacks, int[] offlineOutputIds) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ICameraDeviceUser {
        public static final String DESCRIPTOR = "android.hardware.camera2.ICameraDeviceUser";
        static final int TRANSACTION_beginConfigure = 5;
        static final int TRANSACTION_cancelRequest = 4;
        static final int TRANSACTION_createDefaultRequest = 12;
        static final int TRANSACTION_createInputStream = 10;
        static final int TRANSACTION_createStream = 9;
        static final int TRANSACTION_deleteStream = 8;
        static final int TRANSACTION_disconnect = 1;
        static final int TRANSACTION_endConfigure = 6;
        static final int TRANSACTION_finalizeOutputConfigurations = 20;
        static final int TRANSACTION_flush = 15;
        static final int TRANSACTION_getCameraInfo = 13;
        static final int TRANSACTION_getGlobalAudioRestriction = 22;
        static final int TRANSACTION_getInputSurface = 11;
        static final int TRANSACTION_isSessionConfigurationSupported = 7;
        static final int TRANSACTION_prepare = 16;
        static final int TRANSACTION_prepare2 = 18;
        static final int TRANSACTION_setCameraAudioRestriction = 21;
        static final int TRANSACTION_submitRequest = 2;
        static final int TRANSACTION_submitRequestList = 3;
        static final int TRANSACTION_switchToOffline = 23;
        static final int TRANSACTION_tearDown = 17;
        static final int TRANSACTION_updateOutputConfiguration = 19;
        static final int TRANSACTION_waitUntilIdle = 14;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ICameraDeviceUser asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ICameraDeviceUser)) {
                return (ICameraDeviceUser) iin;
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
                    return MediaMetrics.Value.DISCONNECT;
                case 2:
                    return "submitRequest";
                case 3:
                    return "submitRequestList";
                case 4:
                    return "cancelRequest";
                case 5:
                    return "beginConfigure";
                case 6:
                    return "endConfigure";
                case 7:
                    return "isSessionConfigurationSupported";
                case 8:
                    return "deleteStream";
                case 9:
                    return "createStream";
                case 10:
                    return "createInputStream";
                case 11:
                    return "getInputSurface";
                case 12:
                    return "createDefaultRequest";
                case 13:
                    return "getCameraInfo";
                case 14:
                    return "waitUntilIdle";
                case 15:
                    return "flush";
                case 16:
                    return "prepare";
                case 17:
                    return "tearDown";
                case 18:
                    return "prepare2";
                case 19:
                    return "updateOutputConfiguration";
                case 20:
                    return "finalizeOutputConfigurations";
                case 21:
                    return "setCameraAudioRestriction";
                case 22:
                    return "getGlobalAudioRestriction";
                case 23:
                    return "switchToOffline";
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
                            disconnect();
                            reply.writeNoException();
                            break;
                        case 2:
                            CaptureRequest _arg0 = (CaptureRequest) data.readTypedObject(CaptureRequest.CREATOR);
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            SubmitInfo _result = submitRequest(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 3:
                            CaptureRequest[] _arg02 = (CaptureRequest[]) data.createTypedArray(CaptureRequest.CREATOR);
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            SubmitInfo _result2 = submitRequestList(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            long _result3 = cancelRequest(_arg03);
                            reply.writeNoException();
                            reply.writeLong(_result3);
                            break;
                        case 5:
                            beginConfigure();
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg04 = data.readInt();
                            CameraMetadataNative _arg13 = (CameraMetadataNative) data.readTypedObject(CameraMetadataNative.CREATOR);
                            long _arg2 = data.readLong();
                            data.enforceNoDataAvail();
                            int[] _result4 = endConfigure(_arg04, _arg13, _arg2);
                            reply.writeNoException();
                            reply.writeIntArray(_result4);
                            break;
                        case 7:
                            SessionConfiguration _arg05 = (SessionConfiguration) data.readTypedObject(SessionConfiguration.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result5 = isSessionConfigurationSupported(_arg05);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 8:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            deleteStream(_arg06);
                            reply.writeNoException();
                            break;
                        case 9:
                            OutputConfiguration _arg07 = (OutputConfiguration) data.readTypedObject(OutputConfiguration.CREATOR);
                            data.enforceNoDataAvail();
                            int _result6 = createStream(_arg07);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 10:
                            int _arg08 = data.readInt();
                            int _arg14 = data.readInt();
                            int _arg22 = data.readInt();
                            boolean _arg3 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result7 = createInputStream(_arg08, _arg14, _arg22, _arg3);
                            reply.writeNoException();
                            reply.writeInt(_result7);
                            break;
                        case 11:
                            Surface _result8 = getInputSurface();
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            break;
                        case 12:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            CameraMetadataNative _result9 = createDefaultRequest(_arg09);
                            reply.writeNoException();
                            reply.writeTypedObject(_result9, 1);
                            break;
                        case 13:
                            CameraMetadataNative _result10 = getCameraInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result10, 1);
                            break;
                        case 14:
                            waitUntilIdle();
                            reply.writeNoException();
                            break;
                        case 15:
                            long _result11 = flush();
                            reply.writeNoException();
                            reply.writeLong(_result11);
                            break;
                        case 16:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            prepare(_arg010);
                            reply.writeNoException();
                            break;
                        case 17:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            tearDown(_arg011);
                            reply.writeNoException();
                            break;
                        case 18:
                            int _arg012 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            prepare2(_arg012, _arg15);
                            reply.writeNoException();
                            break;
                        case 19:
                            int _arg013 = data.readInt();
                            OutputConfiguration _arg16 = (OutputConfiguration) data.readTypedObject(OutputConfiguration.CREATOR);
                            data.enforceNoDataAvail();
                            updateOutputConfiguration(_arg013, _arg16);
                            reply.writeNoException();
                            break;
                        case 20:
                            int _arg014 = data.readInt();
                            OutputConfiguration _arg17 = (OutputConfiguration) data.readTypedObject(OutputConfiguration.CREATOR);
                            data.enforceNoDataAvail();
                            finalizeOutputConfigurations(_arg014, _arg17);
                            reply.writeNoException();
                            break;
                        case 21:
                            int _arg015 = data.readInt();
                            data.enforceNoDataAvail();
                            setCameraAudioRestriction(_arg015);
                            reply.writeNoException();
                            break;
                        case 22:
                            int _result12 = getGlobalAudioRestriction();
                            reply.writeNoException();
                            reply.writeInt(_result12);
                            break;
                        case 23:
                            ICameraDeviceCallbacks _arg016 = ICameraDeviceCallbacks.Stub.asInterface(data.readStrongBinder());
                            int[] _arg18 = data.createIntArray();
                            data.enforceNoDataAvail();
                            ICameraOfflineSession _result13 = switchToOffline(_arg016, _arg18);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result13);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ICameraDeviceUser {
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

            @Override // android.hardware.camera2.ICameraDeviceUser
            public void disconnect() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public SubmitInfo submitRequest(CaptureRequest request, boolean streaming) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeBoolean(streaming);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    SubmitInfo _result = (SubmitInfo) _reply.readTypedObject(SubmitInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public SubmitInfo submitRequestList(CaptureRequest[] requestList, boolean streaming) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(requestList, 0);
                    _data.writeBoolean(streaming);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    SubmitInfo _result = (SubmitInfo) _reply.readTypedObject(SubmitInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public long cancelRequest(int requestId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(requestId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public void beginConfigure() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public int[] endConfigure(int operatingMode, CameraMetadataNative sessionParams, long startTimeMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(operatingMode);
                    _data.writeTypedObject(sessionParams, 0);
                    _data.writeLong(startTimeMs);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public boolean isSessionConfigurationSupported(SessionConfiguration sessionConfiguration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sessionConfiguration, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public void deleteStream(int streamId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public int createStream(OutputConfiguration outputConfiguration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(outputConfiguration, 0);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public int createInputStream(int width, int height, int format, boolean isMultiResolution) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    _data.writeInt(format);
                    _data.writeBoolean(isMultiResolution);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public Surface getInputSurface() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    Surface _result = (Surface) _reply.readTypedObject(Surface.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public CameraMetadataNative createDefaultRequest(int templateId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(templateId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    CameraMetadataNative _result = (CameraMetadataNative) _reply.readTypedObject(CameraMetadataNative.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public CameraMetadataNative getCameraInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    CameraMetadataNative _result = (CameraMetadataNative) _reply.readTypedObject(CameraMetadataNative.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public void waitUntilIdle() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public long flush() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public void prepare(int streamId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamId);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public void tearDown(int streamId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamId);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public void prepare2(int maxCount, int streamId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(maxCount);
                    _data.writeInt(streamId);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public void updateOutputConfiguration(int streamId, OutputConfiguration outputConfiguration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamId);
                    _data.writeTypedObject(outputConfiguration, 0);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public void finalizeOutputConfigurations(int streamId, OutputConfiguration outputConfiguration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamId);
                    _data.writeTypedObject(outputConfiguration, 0);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public void setCameraAudioRestriction(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public int getGlobalAudioRestriction() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.ICameraDeviceUser
            public ICameraOfflineSession switchToOffline(ICameraDeviceCallbacks callbacks, int[] offlineOutputIds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callbacks);
                    _data.writeIntArray(offlineOutputIds);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    ICameraOfflineSession _result = ICameraOfflineSession.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 22;
        }
    }
}
