package android.hardware.camera2.extension;

import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.extension.ICaptureCallback;
import android.hardware.camera2.extension.IRequestProcessorImpl;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ISessionProcessorImpl extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.camera2.extension.ISessionProcessorImpl";

    void deInitSession() throws RemoteException;

    LatencyPair getRealtimeCaptureLatency() throws RemoteException;

    CameraSessionConfig initSession(String str, OutputSurface outputSurface, OutputSurface outputSurface2, OutputSurface outputSurface3) throws RemoteException;

    void onCaptureSessionEnd() throws RemoteException;

    void onCaptureSessionStart(IRequestProcessorImpl iRequestProcessorImpl) throws RemoteException;

    void setParameters(CaptureRequest captureRequest) throws RemoteException;

    int startCapture(ICaptureCallback iCaptureCallback, boolean z) throws RemoteException;

    int startRepeating(ICaptureCallback iCaptureCallback) throws RemoteException;

    int startTrigger(CaptureRequest captureRequest, ICaptureCallback iCaptureCallback) throws RemoteException;

    void stopRepeating() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ISessionProcessorImpl {
        @Override // android.hardware.camera2.extension.ISessionProcessorImpl
        public CameraSessionConfig initSession(String cameraId, OutputSurface previewSurface, OutputSurface imageCaptureSurface, OutputSurface postviewSurface) throws RemoteException {
            return null;
        }

        @Override // android.hardware.camera2.extension.ISessionProcessorImpl
        public void deInitSession() throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.ISessionProcessorImpl
        public void onCaptureSessionStart(IRequestProcessorImpl requestProcessor) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.ISessionProcessorImpl
        public void onCaptureSessionEnd() throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.ISessionProcessorImpl
        public int startRepeating(ICaptureCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.camera2.extension.ISessionProcessorImpl
        public void stopRepeating() throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.ISessionProcessorImpl
        public int startCapture(ICaptureCallback callback, boolean isPostviewRequested) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.camera2.extension.ISessionProcessorImpl
        public void setParameters(CaptureRequest captureRequest) throws RemoteException {
        }

        @Override // android.hardware.camera2.extension.ISessionProcessorImpl
        public int startTrigger(CaptureRequest captureRequest, ICaptureCallback callback) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.camera2.extension.ISessionProcessorImpl
        public LatencyPair getRealtimeCaptureLatency() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ISessionProcessorImpl {
        static final int TRANSACTION_deInitSession = 2;
        static final int TRANSACTION_getRealtimeCaptureLatency = 10;
        static final int TRANSACTION_initSession = 1;
        static final int TRANSACTION_onCaptureSessionEnd = 4;
        static final int TRANSACTION_onCaptureSessionStart = 3;
        static final int TRANSACTION_setParameters = 8;
        static final int TRANSACTION_startCapture = 7;
        static final int TRANSACTION_startRepeating = 5;
        static final int TRANSACTION_startTrigger = 9;
        static final int TRANSACTION_stopRepeating = 6;

        public Stub() {
            attachInterface(this, ISessionProcessorImpl.DESCRIPTOR);
        }

        public static ISessionProcessorImpl asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISessionProcessorImpl.DESCRIPTOR);
            if (iin != null && (iin instanceof ISessionProcessorImpl)) {
                return (ISessionProcessorImpl) iin;
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
                    return "initSession";
                case 2:
                    return "deInitSession";
                case 3:
                    return "onCaptureSessionStart";
                case 4:
                    return "onCaptureSessionEnd";
                case 5:
                    return "startRepeating";
                case 6:
                    return "stopRepeating";
                case 7:
                    return "startCapture";
                case 8:
                    return "setParameters";
                case 9:
                    return "startTrigger";
                case 10:
                    return "getRealtimeCaptureLatency";
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
                data.enforceInterface(ISessionProcessorImpl.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISessionProcessorImpl.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            OutputSurface _arg1 = (OutputSurface) data.readTypedObject(OutputSurface.CREATOR);
                            OutputSurface _arg2 = (OutputSurface) data.readTypedObject(OutputSurface.CREATOR);
                            OutputSurface _arg3 = (OutputSurface) data.readTypedObject(OutputSurface.CREATOR);
                            data.enforceNoDataAvail();
                            CameraSessionConfig _result = initSession(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            deInitSession();
                            reply.writeNoException();
                            break;
                        case 3:
                            IRequestProcessorImpl _arg02 = IRequestProcessorImpl.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onCaptureSessionStart(_arg02);
                            reply.writeNoException();
                            break;
                        case 4:
                            onCaptureSessionEnd();
                            reply.writeNoException();
                            break;
                        case 5:
                            ICaptureCallback _arg03 = ICaptureCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result2 = startRepeating(_arg03);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 6:
                            stopRepeating();
                            reply.writeNoException();
                            break;
                        case 7:
                            ICaptureCallback _arg04 = ICaptureCallback.Stub.asInterface(data.readStrongBinder());
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result3 = startCapture(_arg04, _arg12);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 8:
                            CaptureRequest _arg05 = (CaptureRequest) data.readTypedObject(CaptureRequest.CREATOR);
                            data.enforceNoDataAvail();
                            setParameters(_arg05);
                            reply.writeNoException();
                            break;
                        case 9:
                            CaptureRequest _arg06 = (CaptureRequest) data.readTypedObject(CaptureRequest.CREATOR);
                            ICaptureCallback _arg13 = ICaptureCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result4 = startTrigger(_arg06, _arg13);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 10:
                            LatencyPair _result5 = getRealtimeCaptureLatency();
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ISessionProcessorImpl {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISessionProcessorImpl.DESCRIPTOR;
            }

            @Override // android.hardware.camera2.extension.ISessionProcessorImpl
            public CameraSessionConfig initSession(String cameraId, OutputSurface previewSurface, OutputSurface imageCaptureSurface, OutputSurface postviewSurface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISessionProcessorImpl.DESCRIPTOR);
                    _data.writeString(cameraId);
                    _data.writeTypedObject(previewSurface, 0);
                    _data.writeTypedObject(imageCaptureSurface, 0);
                    _data.writeTypedObject(postviewSurface, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    CameraSessionConfig _result = (CameraSessionConfig) _reply.readTypedObject(CameraSessionConfig.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ISessionProcessorImpl
            public void deInitSession() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISessionProcessorImpl.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ISessionProcessorImpl
            public void onCaptureSessionStart(IRequestProcessorImpl requestProcessor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISessionProcessorImpl.DESCRIPTOR);
                    _data.writeStrongInterface(requestProcessor);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ISessionProcessorImpl
            public void onCaptureSessionEnd() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISessionProcessorImpl.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ISessionProcessorImpl
            public int startRepeating(ICaptureCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISessionProcessorImpl.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ISessionProcessorImpl
            public void stopRepeating() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISessionProcessorImpl.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ISessionProcessorImpl
            public int startCapture(ICaptureCallback callback, boolean isPostviewRequested) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISessionProcessorImpl.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeBoolean(isPostviewRequested);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ISessionProcessorImpl
            public void setParameters(CaptureRequest captureRequest) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISessionProcessorImpl.DESCRIPTOR);
                    _data.writeTypedObject(captureRequest, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ISessionProcessorImpl
            public int startTrigger(CaptureRequest captureRequest, ICaptureCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISessionProcessorImpl.DESCRIPTOR);
                    _data.writeTypedObject(captureRequest, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.camera2.extension.ISessionProcessorImpl
            public LatencyPair getRealtimeCaptureLatency() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISessionProcessorImpl.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    LatencyPair _result = (LatencyPair) _reply.readTypedObject(LatencyPair.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
