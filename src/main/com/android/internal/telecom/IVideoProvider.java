package com.android.internal.telecom;

import android.net.Uri;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telecom.VideoProfile;
import android.view.Surface;
/* loaded from: classes2.dex */
public interface IVideoProvider extends IInterface {
    void addVideoCallback(IBinder iBinder) throws RemoteException;

    void removeVideoCallback(IBinder iBinder) throws RemoteException;

    void requestCallDataUsage() throws RemoteException;

    void requestCameraCapabilities() throws RemoteException;

    void sendSessionModifyRequest(VideoProfile videoProfile, VideoProfile videoProfile2) throws RemoteException;

    void sendSessionModifyResponse(VideoProfile videoProfile) throws RemoteException;

    void setCamera(String str, String str2, int i) throws RemoteException;

    void setDeviceOrientation(int i) throws RemoteException;

    void setDisplaySurface(Surface surface) throws RemoteException;

    void setPauseImage(Uri uri) throws RemoteException;

    void setPreviewSurface(Surface surface) throws RemoteException;

    void setZoom(float f) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IVideoProvider {
        @Override // com.android.internal.telecom.IVideoProvider
        public void addVideoCallback(IBinder videoCallbackBinder) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IVideoProvider
        public void removeVideoCallback(IBinder videoCallbackBinder) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IVideoProvider
        public void setCamera(String cameraId, String mCallingPackageName, int targetSdkVersion) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IVideoProvider
        public void setPreviewSurface(Surface surface) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IVideoProvider
        public void setDisplaySurface(Surface surface) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IVideoProvider
        public void setDeviceOrientation(int rotation) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IVideoProvider
        public void setZoom(float value) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IVideoProvider
        public void sendSessionModifyRequest(VideoProfile fromProfile, VideoProfile toProfile) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IVideoProvider
        public void sendSessionModifyResponse(VideoProfile responseProfile) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IVideoProvider
        public void requestCameraCapabilities() throws RemoteException {
        }

        @Override // com.android.internal.telecom.IVideoProvider
        public void requestCallDataUsage() throws RemoteException {
        }

        @Override // com.android.internal.telecom.IVideoProvider
        public void setPauseImage(Uri uri) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IVideoProvider {
        public static final String DESCRIPTOR = "com.android.internal.telecom.IVideoProvider";
        static final int TRANSACTION_addVideoCallback = 1;
        static final int TRANSACTION_removeVideoCallback = 2;
        static final int TRANSACTION_requestCallDataUsage = 11;
        static final int TRANSACTION_requestCameraCapabilities = 10;
        static final int TRANSACTION_sendSessionModifyRequest = 8;
        static final int TRANSACTION_sendSessionModifyResponse = 9;
        static final int TRANSACTION_setCamera = 3;
        static final int TRANSACTION_setDeviceOrientation = 6;
        static final int TRANSACTION_setDisplaySurface = 5;
        static final int TRANSACTION_setPauseImage = 12;
        static final int TRANSACTION_setPreviewSurface = 4;
        static final int TRANSACTION_setZoom = 7;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVideoProvider asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IVideoProvider)) {
                return (IVideoProvider) iin;
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
                    return "addVideoCallback";
                case 2:
                    return "removeVideoCallback";
                case 3:
                    return "setCamera";
                case 4:
                    return "setPreviewSurface";
                case 5:
                    return "setDisplaySurface";
                case 6:
                    return "setDeviceOrientation";
                case 7:
                    return "setZoom";
                case 8:
                    return "sendSessionModifyRequest";
                case 9:
                    return "sendSessionModifyResponse";
                case 10:
                    return "requestCameraCapabilities";
                case 11:
                    return "requestCallDataUsage";
                case 12:
                    return "setPauseImage";
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
                            IBinder _arg0 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            addVideoCallback(_arg0);
                            break;
                        case 2:
                            IBinder _arg02 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            removeVideoCallback(_arg02);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            String _arg1 = data.readString();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            setCamera(_arg03, _arg1, _arg2);
                            break;
                        case 4:
                            Surface _arg04 = (Surface) data.readTypedObject(Surface.CREATOR);
                            data.enforceNoDataAvail();
                            setPreviewSurface(_arg04);
                            break;
                        case 5:
                            Surface _arg05 = (Surface) data.readTypedObject(Surface.CREATOR);
                            data.enforceNoDataAvail();
                            setDisplaySurface(_arg05);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            setDeviceOrientation(_arg06);
                            break;
                        case 7:
                            float _arg07 = data.readFloat();
                            data.enforceNoDataAvail();
                            setZoom(_arg07);
                            break;
                        case 8:
                            VideoProfile _arg08 = (VideoProfile) data.readTypedObject(VideoProfile.CREATOR);
                            VideoProfile _arg12 = (VideoProfile) data.readTypedObject(VideoProfile.CREATOR);
                            data.enforceNoDataAvail();
                            sendSessionModifyRequest(_arg08, _arg12);
                            break;
                        case 9:
                            VideoProfile _arg09 = (VideoProfile) data.readTypedObject(VideoProfile.CREATOR);
                            data.enforceNoDataAvail();
                            sendSessionModifyResponse(_arg09);
                            break;
                        case 10:
                            requestCameraCapabilities();
                            break;
                        case 11:
                            requestCallDataUsage();
                            break;
                        case 12:
                            Uri _arg010 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            setPauseImage(_arg010);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IVideoProvider {
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

            @Override // com.android.internal.telecom.IVideoProvider
            public void addVideoCallback(IBinder videoCallbackBinder) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(videoCallbackBinder);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IVideoProvider
            public void removeVideoCallback(IBinder videoCallbackBinder) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(videoCallbackBinder);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IVideoProvider
            public void setCamera(String cameraId, String mCallingPackageName, int targetSdkVersion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cameraId);
                    _data.writeString(mCallingPackageName);
                    _data.writeInt(targetSdkVersion);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IVideoProvider
            public void setPreviewSurface(Surface surface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(surface, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IVideoProvider
            public void setDisplaySurface(Surface surface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(surface, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IVideoProvider
            public void setDeviceOrientation(int rotation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(rotation);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IVideoProvider
            public void setZoom(float value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(value);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IVideoProvider
            public void sendSessionModifyRequest(VideoProfile fromProfile, VideoProfile toProfile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(fromProfile, 0);
                    _data.writeTypedObject(toProfile, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IVideoProvider
            public void sendSessionModifyResponse(VideoProfile responseProfile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(responseProfile, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IVideoProvider
            public void requestCameraCapabilities() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IVideoProvider
            public void requestCallDataUsage() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IVideoProvider
            public void setPauseImage(Uri uri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(uri, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 11;
        }
    }
}
