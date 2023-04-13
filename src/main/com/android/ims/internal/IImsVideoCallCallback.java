package com.android.ims.internal;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telecom.VideoProfile;
/* loaded from: classes4.dex */
public interface IImsVideoCallCallback extends IInterface {
    void changeCallDataUsage(long j) throws RemoteException;

    void changeCameraCapabilities(VideoProfile.CameraCapabilities cameraCapabilities) throws RemoteException;

    void changePeerDimensions(int i, int i2) throws RemoteException;

    void changeVideoQuality(int i) throws RemoteException;

    void handleCallSessionEvent(int i) throws RemoteException;

    void receiveSessionModifyRequest(VideoProfile videoProfile) throws RemoteException;

    void receiveSessionModifyResponse(int i, VideoProfile videoProfile, VideoProfile videoProfile2) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IImsVideoCallCallback {
        @Override // com.android.ims.internal.IImsVideoCallCallback
        public void receiveSessionModifyRequest(VideoProfile videoProfile) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallCallback
        public void receiveSessionModifyResponse(int status, VideoProfile requestedProfile, VideoProfile responseProfile) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallCallback
        public void handleCallSessionEvent(int event) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallCallback
        public void changePeerDimensions(int width, int height) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallCallback
        public void changeCallDataUsage(long dataUsage) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallCallback
        public void changeCameraCapabilities(VideoProfile.CameraCapabilities cameraCapabilities) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsVideoCallCallback
        public void changeVideoQuality(int videoQuality) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IImsVideoCallCallback {
        public static final String DESCRIPTOR = "com.android.ims.internal.IImsVideoCallCallback";
        static final int TRANSACTION_changeCallDataUsage = 5;
        static final int TRANSACTION_changeCameraCapabilities = 6;
        static final int TRANSACTION_changePeerDimensions = 4;
        static final int TRANSACTION_changeVideoQuality = 7;
        static final int TRANSACTION_handleCallSessionEvent = 3;
        static final int TRANSACTION_receiveSessionModifyRequest = 1;
        static final int TRANSACTION_receiveSessionModifyResponse = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IImsVideoCallCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IImsVideoCallCallback)) {
                return (IImsVideoCallCallback) iin;
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
                    return "receiveSessionModifyRequest";
                case 2:
                    return "receiveSessionModifyResponse";
                case 3:
                    return "handleCallSessionEvent";
                case 4:
                    return "changePeerDimensions";
                case 5:
                    return "changeCallDataUsage";
                case 6:
                    return "changeCameraCapabilities";
                case 7:
                    return "changeVideoQuality";
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
                            VideoProfile _arg0 = (VideoProfile) data.readTypedObject(VideoProfile.CREATOR);
                            data.enforceNoDataAvail();
                            receiveSessionModifyRequest(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            VideoProfile _arg1 = (VideoProfile) data.readTypedObject(VideoProfile.CREATOR);
                            VideoProfile _arg2 = (VideoProfile) data.readTypedObject(VideoProfile.CREATOR);
                            data.enforceNoDataAvail();
                            receiveSessionModifyResponse(_arg02, _arg1, _arg2);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            handleCallSessionEvent(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            changePeerDimensions(_arg04, _arg12);
                            break;
                        case 5:
                            long _arg05 = data.readLong();
                            data.enforceNoDataAvail();
                            changeCallDataUsage(_arg05);
                            break;
                        case 6:
                            VideoProfile.CameraCapabilities _arg06 = (VideoProfile.CameraCapabilities) data.readTypedObject(VideoProfile.CameraCapabilities.CREATOR);
                            data.enforceNoDataAvail();
                            changeCameraCapabilities(_arg06);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            changeVideoQuality(_arg07);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IImsVideoCallCallback {
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

            @Override // com.android.ims.internal.IImsVideoCallCallback
            public void receiveSessionModifyRequest(VideoProfile videoProfile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(videoProfile, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallCallback
            public void receiveSessionModifyResponse(int status, VideoProfile requestedProfile, VideoProfile responseProfile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    _data.writeTypedObject(requestedProfile, 0);
                    _data.writeTypedObject(responseProfile, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallCallback
            public void handleCallSessionEvent(int event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(event);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallCallback
            public void changePeerDimensions(int width, int height) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallCallback
            public void changeCallDataUsage(long dataUsage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(dataUsage);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallCallback
            public void changeCameraCapabilities(VideoProfile.CameraCapabilities cameraCapabilities) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(cameraCapabilities, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsVideoCallCallback
            public void changeVideoQuality(int videoQuality) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(videoQuality);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
