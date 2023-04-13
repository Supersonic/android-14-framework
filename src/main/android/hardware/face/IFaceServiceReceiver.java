package android.hardware.face;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IFaceServiceReceiver extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.face.IFaceServiceReceiver";

    void onAcquired(int i, int i2) throws RemoteException;

    void onAuthenticationFailed() throws RemoteException;

    void onAuthenticationFrame(FaceAuthenticationFrame faceAuthenticationFrame) throws RemoteException;

    void onAuthenticationSucceeded(Face face, int i, boolean z) throws RemoteException;

    void onChallengeGenerated(int i, int i2, long j) throws RemoteException;

    void onEnrollResult(Face face, int i) throws RemoteException;

    void onEnrollmentFrame(FaceEnrollFrame faceEnrollFrame) throws RemoteException;

    void onError(int i, int i2) throws RemoteException;

    void onFaceDetected(int i, int i2, boolean z) throws RemoteException;

    void onFeatureGet(boolean z, int[] iArr, boolean[] zArr) throws RemoteException;

    void onFeatureSet(boolean z, int i) throws RemoteException;

    void onRemoved(Face face, int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IFaceServiceReceiver {
        @Override // android.hardware.face.IFaceServiceReceiver
        public void onEnrollResult(Face face, int remaining) throws RemoteException {
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onAcquired(int acquiredInfo, int vendorCode) throws RemoteException {
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onAuthenticationSucceeded(Face face, int userId, boolean isStrongBiometric) throws RemoteException {
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onFaceDetected(int sensorId, int userId, boolean isStrongBiometric) throws RemoteException {
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onAuthenticationFailed() throws RemoteException {
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onError(int error, int vendorCode) throws RemoteException {
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onRemoved(Face face, int remaining) throws RemoteException {
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onFeatureSet(boolean success, int feature) throws RemoteException {
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onFeatureGet(boolean success, int[] features, boolean[] featureState) throws RemoteException {
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onChallengeGenerated(int sensorId, int userId, long challenge) throws RemoteException {
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onAuthenticationFrame(FaceAuthenticationFrame frame) throws RemoteException {
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onEnrollmentFrame(FaceEnrollFrame frame) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IFaceServiceReceiver {
        static final int TRANSACTION_onAcquired = 2;
        static final int TRANSACTION_onAuthenticationFailed = 5;
        static final int TRANSACTION_onAuthenticationFrame = 11;
        static final int TRANSACTION_onAuthenticationSucceeded = 3;
        static final int TRANSACTION_onChallengeGenerated = 10;
        static final int TRANSACTION_onEnrollResult = 1;
        static final int TRANSACTION_onEnrollmentFrame = 12;
        static final int TRANSACTION_onError = 6;
        static final int TRANSACTION_onFaceDetected = 4;
        static final int TRANSACTION_onFeatureGet = 9;
        static final int TRANSACTION_onFeatureSet = 8;
        static final int TRANSACTION_onRemoved = 7;

        public Stub() {
            attachInterface(this, IFaceServiceReceiver.DESCRIPTOR);
        }

        public static IFaceServiceReceiver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IFaceServiceReceiver.DESCRIPTOR);
            if (iin != null && (iin instanceof IFaceServiceReceiver)) {
                return (IFaceServiceReceiver) iin;
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
                    return "onEnrollResult";
                case 2:
                    return "onAcquired";
                case 3:
                    return "onAuthenticationSucceeded";
                case 4:
                    return "onFaceDetected";
                case 5:
                    return "onAuthenticationFailed";
                case 6:
                    return "onError";
                case 7:
                    return "onRemoved";
                case 8:
                    return "onFeatureSet";
                case 9:
                    return "onFeatureGet";
                case 10:
                    return "onChallengeGenerated";
                case 11:
                    return "onAuthenticationFrame";
                case 12:
                    return "onEnrollmentFrame";
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
                data.enforceInterface(IFaceServiceReceiver.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IFaceServiceReceiver.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            Face _arg0 = (Face) data.readTypedObject(Face.CREATOR);
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onEnrollResult(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onAcquired(_arg02, _arg12);
                            break;
                        case 3:
                            Face _arg03 = (Face) data.readTypedObject(Face.CREATOR);
                            int _arg13 = data.readInt();
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onAuthenticationSucceeded(_arg03, _arg13, _arg2);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg14 = data.readInt();
                            boolean _arg22 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onFaceDetected(_arg04, _arg14, _arg22);
                            break;
                        case 5:
                            onAuthenticationFailed();
                            break;
                        case 6:
                            int _arg05 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            onError(_arg05, _arg15);
                            break;
                        case 7:
                            Face _arg06 = (Face) data.readTypedObject(Face.CREATOR);
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            onRemoved(_arg06, _arg16);
                            break;
                        case 8:
                            boolean _arg07 = data.readBoolean();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            onFeatureSet(_arg07, _arg17);
                            break;
                        case 9:
                            boolean _arg08 = data.readBoolean();
                            int[] _arg18 = data.createIntArray();
                            boolean[] _arg23 = data.createBooleanArray();
                            data.enforceNoDataAvail();
                            onFeatureGet(_arg08, _arg18, _arg23);
                            break;
                        case 10:
                            int _arg09 = data.readInt();
                            int _arg19 = data.readInt();
                            long _arg24 = data.readLong();
                            data.enforceNoDataAvail();
                            onChallengeGenerated(_arg09, _arg19, _arg24);
                            break;
                        case 11:
                            FaceAuthenticationFrame _arg010 = (FaceAuthenticationFrame) data.readTypedObject(FaceAuthenticationFrame.CREATOR);
                            data.enforceNoDataAvail();
                            onAuthenticationFrame(_arg010);
                            break;
                        case 12:
                            FaceEnrollFrame _arg011 = (FaceEnrollFrame) data.readTypedObject(FaceEnrollFrame.CREATOR);
                            data.enforceNoDataAvail();
                            onEnrollmentFrame(_arg011);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IFaceServiceReceiver {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IFaceServiceReceiver.DESCRIPTOR;
            }

            @Override // android.hardware.face.IFaceServiceReceiver
            public void onEnrollResult(Face face, int remaining) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFaceServiceReceiver.DESCRIPTOR);
                    _data.writeTypedObject(face, 0);
                    _data.writeInt(remaining);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.face.IFaceServiceReceiver
            public void onAcquired(int acquiredInfo, int vendorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFaceServiceReceiver.DESCRIPTOR);
                    _data.writeInt(acquiredInfo);
                    _data.writeInt(vendorCode);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.face.IFaceServiceReceiver
            public void onAuthenticationSucceeded(Face face, int userId, boolean isStrongBiometric) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFaceServiceReceiver.DESCRIPTOR);
                    _data.writeTypedObject(face, 0);
                    _data.writeInt(userId);
                    _data.writeBoolean(isStrongBiometric);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.face.IFaceServiceReceiver
            public void onFaceDetected(int sensorId, int userId, boolean isStrongBiometric) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFaceServiceReceiver.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeInt(userId);
                    _data.writeBoolean(isStrongBiometric);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.face.IFaceServiceReceiver
            public void onAuthenticationFailed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFaceServiceReceiver.DESCRIPTOR);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.face.IFaceServiceReceiver
            public void onError(int error, int vendorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFaceServiceReceiver.DESCRIPTOR);
                    _data.writeInt(error);
                    _data.writeInt(vendorCode);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.face.IFaceServiceReceiver
            public void onRemoved(Face face, int remaining) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFaceServiceReceiver.DESCRIPTOR);
                    _data.writeTypedObject(face, 0);
                    _data.writeInt(remaining);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.face.IFaceServiceReceiver
            public void onFeatureSet(boolean success, int feature) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFaceServiceReceiver.DESCRIPTOR);
                    _data.writeBoolean(success);
                    _data.writeInt(feature);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.face.IFaceServiceReceiver
            public void onFeatureGet(boolean success, int[] features, boolean[] featureState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFaceServiceReceiver.DESCRIPTOR);
                    _data.writeBoolean(success);
                    _data.writeIntArray(features);
                    _data.writeBooleanArray(featureState);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.face.IFaceServiceReceiver
            public void onChallengeGenerated(int sensorId, int userId, long challenge) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFaceServiceReceiver.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeInt(userId);
                    _data.writeLong(challenge);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.face.IFaceServiceReceiver
            public void onAuthenticationFrame(FaceAuthenticationFrame frame) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFaceServiceReceiver.DESCRIPTOR);
                    _data.writeTypedObject(frame, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.face.IFaceServiceReceiver
            public void onEnrollmentFrame(FaceEnrollFrame frame) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IFaceServiceReceiver.DESCRIPTOR);
                    _data.writeTypedObject(frame, 0);
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
