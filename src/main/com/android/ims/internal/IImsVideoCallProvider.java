package com.android.ims.internal;

import android.net.Uri;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telecom.VideoProfile;
import android.view.Surface;
import com.android.ims.internal.IImsVideoCallCallback;
/* loaded from: classes4.dex */
public interface IImsVideoCallProvider extends IInterface {
    void requestCallDataUsage() throws RemoteException;

    void requestCameraCapabilities() throws RemoteException;

    void sendSessionModifyRequest(VideoProfile videoProfile, VideoProfile videoProfile2) throws RemoteException;

    void sendSessionModifyResponse(VideoProfile videoProfile) throws RemoteException;

    void setCallback(IImsVideoCallCallback iImsVideoCallCallback) throws RemoteException;

    void setCamera(String str, int i) throws RemoteException;

    void setDeviceOrientation(int i) throws RemoteException;

    void setDisplaySurface(Surface surface) throws RemoteException;

    void setPauseImage(Uri uri) throws RemoteException;

    void setPreviewSurface(Surface surface) throws RemoteException;

    void setZoom(float f) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IImsVideoCallProvider {
        @Override // com.android.ims.internal.IImsVideoCallProvider
        public void setCallback(IImsVideoCallCallback callback) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallProvider
        public void setCamera(String cameraId, int uid) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallProvider
        public void setPreviewSurface(Surface surface) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallProvider
        public void setDisplaySurface(Surface surface) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallProvider
        public void setDeviceOrientation(int rotation) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallProvider
        public void setZoom(float value) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallProvider
        public void sendSessionModifyRequest(VideoProfile fromProfile, VideoProfile toProfile) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallProvider
        public void sendSessionModifyResponse(VideoProfile responseProfile) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallProvider
        public void requestCameraCapabilities() throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallProvider
        public void requestCallDataUsage() throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallProvider
        public void setPauseImage(Uri uri) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IImsVideoCallProvider {
        public static final String DESCRIPTOR = "com.android.ims.internal.IImsVideoCallProvider";
        static final int TRANSACTION_requestCallDataUsage = 10;
        static final int TRANSACTION_requestCameraCapabilities = 9;
        static final int TRANSACTION_sendSessionModifyRequest = 7;
        static final int TRANSACTION_sendSessionModifyResponse = 8;
        static final int TRANSACTION_setCallback = 1;
        static final int TRANSACTION_setCamera = 2;
        static final int TRANSACTION_setDeviceOrientation = 5;
        static final int TRANSACTION_setDisplaySurface = 4;
        static final int TRANSACTION_setPauseImage = 11;
        static final int TRANSACTION_setPreviewSurface = 3;
        static final int TRANSACTION_setZoom = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IImsVideoCallProvider asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IImsVideoCallProvider)) {
                return (IImsVideoCallProvider) iin;
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
                    return "setCallback";
                case 2:
                    return "setCamera";
                case 3:
                    return "setPreviewSurface";
                case 4:
                    return "setDisplaySurface";
                case 5:
                    return "setDeviceOrientation";
                case 6:
                    return "setZoom";
                case 7:
                    return "sendSessionModifyRequest";
                case 8:
                    return "sendSessionModifyResponse";
                case 9:
                    return "requestCameraCapabilities";
                case 10:
                    return "requestCallDataUsage";
                case 11:
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
                            IImsVideoCallCallback _arg0 = IImsVideoCallCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setCallback(_arg0);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            setCamera(_arg02, _arg1);
                            break;
                        case 3:
                            Surface _arg03 = (Surface) data.readTypedObject(Surface.CREATOR);
                            data.enforceNoDataAvail();
                            setPreviewSurface(_arg03);
                            break;
                        case 4:
                            Surface _arg04 = (Surface) data.readTypedObject(Surface.CREATOR);
                            data.enforceNoDataAvail();
                            setDisplaySurface(_arg04);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            setDeviceOrientation(_arg05);
                            break;
                        case 6:
                            float _arg06 = data.readFloat();
                            data.enforceNoDataAvail();
                            setZoom(_arg06);
                            break;
                        case 7:
                            VideoProfile _arg07 = (VideoProfile) data.readTypedObject(VideoProfile.CREATOR);
                            VideoProfile _arg12 = (VideoProfile) data.readTypedObject(VideoProfile.CREATOR);
                            data.enforceNoDataAvail();
                            sendSessionModifyRequest(_arg07, _arg12);
                            break;
                        case 8:
                            VideoProfile _arg08 = (VideoProfile) data.readTypedObject(VideoProfile.CREATOR);
                            data.enforceNoDataAvail();
                            sendSessionModifyResponse(_arg08);
                            break;
                        case 9:
                            requestCameraCapabilities();
                            break;
                        case 10:
                            requestCallDataUsage();
                            break;
                        case 11:
                            Uri _arg09 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            setPauseImage(_arg09);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IImsVideoCallProvider {
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

            @Override // com.android.ims.internal.IImsVideoCallProvider
            public void setCallback(IImsVideoCallCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallProvider
            public void setCamera(String cameraId, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cameraId);
                    _data.writeInt(uid);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallProvider
            public void setPreviewSurface(Surface surface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(surface, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallProvider
            public void setDisplaySurface(Surface surface) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(surface, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallProvider
            public void setDeviceOrientation(int rotation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(rotation);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallProvider
            public void setZoom(float value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(value);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallProvider
            public void sendSessionModifyRequest(VideoProfile fromProfile, VideoProfile toProfile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(fromProfile, 0);
                    _data.writeTypedObject(toProfile, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallProvider
            public void sendSessionModifyResponse(VideoProfile responseProfile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(responseProfile, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallProvider
            public void requestCameraCapabilities() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallProvider
            public void requestCallDataUsage() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallProvider
            public void setPauseImage(Uri uri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(uri, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
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
