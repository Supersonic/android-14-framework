package android.hardware.biometrics.face;

import android.hardware.biometrics.common.ICancellationSignal;
import android.hardware.biometrics.common.OperationContext;
import android.hardware.common.NativeHandle;
import android.hardware.keymaster.HardwareAuthToken;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface ISession extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$biometrics$face$ISession".replace('$', '.');

    /* loaded from: classes.dex */
    public static class Default implements ISession {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.hardware.biometrics.face.ISession
        public ICancellationSignal authenticate(long j) throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.face.ISession
        public ICancellationSignal authenticateWithContext(long j, OperationContext operationContext) throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.face.ISession
        public void close() throws RemoteException {
        }

        @Override // android.hardware.biometrics.face.ISession
        public ICancellationSignal detectInteraction() throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.face.ISession
        public ICancellationSignal detectInteractionWithContext(OperationContext operationContext) throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.face.ISession
        public ICancellationSignal enroll(HardwareAuthToken hardwareAuthToken, byte b, byte[] bArr, NativeHandle nativeHandle) throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.face.ISession
        public ICancellationSignal enrollWithContext(HardwareAuthToken hardwareAuthToken, byte b, byte[] bArr, NativeHandle nativeHandle, OperationContext operationContext) throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.face.ISession
        public void enumerateEnrollments() throws RemoteException {
        }

        @Override // android.hardware.biometrics.face.ISession
        public void generateChallenge() throws RemoteException {
        }

        @Override // android.hardware.biometrics.face.ISession
        public void getAuthenticatorId() throws RemoteException {
        }

        @Override // android.hardware.biometrics.face.ISession
        public void getFeatures() throws RemoteException {
        }

        @Override // android.hardware.biometrics.face.ISession
        public void invalidateAuthenticatorId() throws RemoteException {
        }

        @Override // android.hardware.biometrics.face.ISession
        public void removeEnrollments(int[] iArr) throws RemoteException {
        }

        @Override // android.hardware.biometrics.face.ISession
        public void resetLockout(HardwareAuthToken hardwareAuthToken) throws RemoteException {
        }

        @Override // android.hardware.biometrics.face.ISession
        public void revokeChallenge(long j) throws RemoteException {
        }

        @Override // android.hardware.biometrics.face.ISession
        public void setFeature(HardwareAuthToken hardwareAuthToken, byte b, boolean z) throws RemoteException {
        }
    }

    ICancellationSignal authenticate(long j) throws RemoteException;

    ICancellationSignal authenticateWithContext(long j, OperationContext operationContext) throws RemoteException;

    void close() throws RemoteException;

    ICancellationSignal detectInteraction() throws RemoteException;

    ICancellationSignal detectInteractionWithContext(OperationContext operationContext) throws RemoteException;

    ICancellationSignal enroll(HardwareAuthToken hardwareAuthToken, byte b, byte[] bArr, NativeHandle nativeHandle) throws RemoteException;

    ICancellationSignal enrollWithContext(HardwareAuthToken hardwareAuthToken, byte b, byte[] bArr, NativeHandle nativeHandle, OperationContext operationContext) throws RemoteException;

    void enumerateEnrollments() throws RemoteException;

    void generateChallenge() throws RemoteException;

    void getAuthenticatorId() throws RemoteException;

    EnrollmentStageConfig[] getEnrollmentConfig(byte b) throws RemoteException;

    void getFeatures() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void invalidateAuthenticatorId() throws RemoteException;

    void onContextChanged(OperationContext operationContext) throws RemoteException;

    void removeEnrollments(int[] iArr) throws RemoteException;

    void resetLockout(HardwareAuthToken hardwareAuthToken) throws RemoteException;

    void revokeChallenge(long j) throws RemoteException;

    void setFeature(HardwareAuthToken hardwareAuthToken, byte b, boolean z) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ISession {
        public static String getDefaultTransactionName(int i) {
            switch (i) {
                case 1:
                    return "generateChallenge";
                case 2:
                    return "revokeChallenge";
                case 3:
                    return "getEnrollmentConfig";
                case 4:
                    return "enroll";
                case 5:
                    return "authenticate";
                case 6:
                    return "detectInteraction";
                case 7:
                    return "enumerateEnrollments";
                case 8:
                    return "removeEnrollments";
                case 9:
                    return "getFeatures";
                case 10:
                    return "setFeature";
                case 11:
                    return "getAuthenticatorId";
                case 12:
                    return "invalidateAuthenticatorId";
                case 13:
                    return "resetLockout";
                case 14:
                    return "close";
                case 15:
                    return "authenticateWithContext";
                case 16:
                    return "enrollWithContext";
                case 17:
                    return "detectInteractionWithContext";
                case 18:
                    return "onContextChanged";
                default:
                    switch (i) {
                        case 16777214:
                            return "getInterfaceHash";
                        case 16777215:
                            return "getInterfaceVersion";
                        default:
                            return null;
                    }
            }
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public int getMaxTransactionId() {
            return 16777214;
        }

        public Stub() {
            markVintfStability();
            attachInterface(this, ISession.DESCRIPTOR);
        }

        public static ISession asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(ISession.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof ISession)) {
                return (ISession) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        public String getTransactionName(int i) {
            return getDefaultTransactionName(i);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = ISession.DESCRIPTOR;
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(str);
            }
            switch (i) {
                case 16777214:
                    parcel2.writeNoException();
                    parcel2.writeString(getInterfaceHash());
                    return true;
                case 16777215:
                    parcel2.writeNoException();
                    parcel2.writeInt(getInterfaceVersion());
                    return true;
                case 1598968902:
                    parcel2.writeString(str);
                    return true;
                default:
                    switch (i) {
                        case 1:
                            generateChallenge();
                            parcel2.writeNoException();
                            break;
                        case 2:
                            long readLong = parcel.readLong();
                            parcel.enforceNoDataAvail();
                            revokeChallenge(readLong);
                            parcel2.writeNoException();
                            break;
                        case 3:
                            byte readByte = parcel.readByte();
                            parcel.enforceNoDataAvail();
                            EnrollmentStageConfig[] enrollmentConfig = getEnrollmentConfig(readByte);
                            parcel2.writeNoException();
                            parcel2.writeTypedArray(enrollmentConfig, 1);
                            break;
                        case 4:
                            parcel.enforceNoDataAvail();
                            ICancellationSignal enroll = enroll((HardwareAuthToken) parcel.readTypedObject(HardwareAuthToken.CREATOR), parcel.readByte(), parcel.createByteArray(), (NativeHandle) parcel.readTypedObject(NativeHandle.CREATOR));
                            parcel2.writeNoException();
                            parcel2.writeStrongInterface(enroll);
                            break;
                        case 5:
                            long readLong2 = parcel.readLong();
                            parcel.enforceNoDataAvail();
                            ICancellationSignal authenticate = authenticate(readLong2);
                            parcel2.writeNoException();
                            parcel2.writeStrongInterface(authenticate);
                            break;
                        case 6:
                            ICancellationSignal detectInteraction = detectInteraction();
                            parcel2.writeNoException();
                            parcel2.writeStrongInterface(detectInteraction);
                            break;
                        case 7:
                            enumerateEnrollments();
                            parcel2.writeNoException();
                            break;
                        case 8:
                            int[] createIntArray = parcel.createIntArray();
                            parcel.enforceNoDataAvail();
                            removeEnrollments(createIntArray);
                            parcel2.writeNoException();
                            break;
                        case 9:
                            getFeatures();
                            parcel2.writeNoException();
                            break;
                        case 10:
                            byte readByte2 = parcel.readByte();
                            boolean readBoolean = parcel.readBoolean();
                            parcel.enforceNoDataAvail();
                            setFeature((HardwareAuthToken) parcel.readTypedObject(HardwareAuthToken.CREATOR), readByte2, readBoolean);
                            parcel2.writeNoException();
                            break;
                        case 11:
                            getAuthenticatorId();
                            parcel2.writeNoException();
                            break;
                        case 12:
                            invalidateAuthenticatorId();
                            parcel2.writeNoException();
                            break;
                        case 13:
                            parcel.enforceNoDataAvail();
                            resetLockout((HardwareAuthToken) parcel.readTypedObject(HardwareAuthToken.CREATOR));
                            parcel2.writeNoException();
                            break;
                        case 14:
                            close();
                            parcel2.writeNoException();
                            break;
                        case 15:
                            parcel.enforceNoDataAvail();
                            ICancellationSignal authenticateWithContext = authenticateWithContext(parcel.readLong(), (OperationContext) parcel.readTypedObject(OperationContext.CREATOR));
                            parcel2.writeNoException();
                            parcel2.writeStrongInterface(authenticateWithContext);
                            break;
                        case 16:
                            parcel.enforceNoDataAvail();
                            ICancellationSignal enrollWithContext = enrollWithContext((HardwareAuthToken) parcel.readTypedObject(HardwareAuthToken.CREATOR), parcel.readByte(), parcel.createByteArray(), (NativeHandle) parcel.readTypedObject(NativeHandle.CREATOR), (OperationContext) parcel.readTypedObject(OperationContext.CREATOR));
                            parcel2.writeNoException();
                            parcel2.writeStrongInterface(enrollWithContext);
                            break;
                        case 17:
                            parcel.enforceNoDataAvail();
                            ICancellationSignal detectInteractionWithContext = detectInteractionWithContext((OperationContext) parcel.readTypedObject(OperationContext.CREATOR));
                            parcel2.writeNoException();
                            parcel2.writeStrongInterface(detectInteractionWithContext);
                            break;
                        case 18:
                            parcel.enforceNoDataAvail();
                            onContextChanged((OperationContext) parcel.readTypedObject(OperationContext.CREATOR));
                            parcel2.writeNoException();
                            break;
                        default:
                            return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements ISession {
            public IBinder mRemote;
            public int mCachedVersion = -1;
            public String mCachedHash = "-1";

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.hardware.biometrics.face.ISession
            public void generateChallenge() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0)) {
                        throw new RemoteException("Method generateChallenge is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public void revokeChallenge(long j) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    obtain.writeLong(j);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0)) {
                        throw new RemoteException("Method revokeChallenge is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public ICancellationSignal enroll(HardwareAuthToken hardwareAuthToken, byte b, byte[] bArr, NativeHandle nativeHandle) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    obtain.writeTypedObject(hardwareAuthToken, 0);
                    obtain.writeByte(b);
                    obtain.writeByteArray(bArr);
                    obtain.writeTypedObject(nativeHandle, 0);
                    if (!this.mRemote.transact(4, obtain, obtain2, 0)) {
                        throw new RemoteException("Method enroll is unimplemented.");
                    }
                    obtain2.readException();
                    return ICancellationSignal.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public ICancellationSignal authenticate(long j) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    obtain.writeLong(j);
                    if (!this.mRemote.transact(5, obtain, obtain2, 0)) {
                        throw new RemoteException("Method authenticate is unimplemented.");
                    }
                    obtain2.readException();
                    return ICancellationSignal.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public ICancellationSignal detectInteraction() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    if (!this.mRemote.transact(6, obtain, obtain2, 0)) {
                        throw new RemoteException("Method detectInteraction is unimplemented.");
                    }
                    obtain2.readException();
                    return ICancellationSignal.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public void enumerateEnrollments() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    if (!this.mRemote.transact(7, obtain, obtain2, 0)) {
                        throw new RemoteException("Method enumerateEnrollments is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public void removeEnrollments(int[] iArr) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    obtain.writeIntArray(iArr);
                    if (!this.mRemote.transact(8, obtain, obtain2, 0)) {
                        throw new RemoteException("Method removeEnrollments is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public void getFeatures() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    if (!this.mRemote.transact(9, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getFeatures is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public void setFeature(HardwareAuthToken hardwareAuthToken, byte b, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    obtain.writeTypedObject(hardwareAuthToken, 0);
                    obtain.writeByte(b);
                    obtain.writeBoolean(z);
                    if (!this.mRemote.transact(10, obtain, obtain2, 0)) {
                        throw new RemoteException("Method setFeature is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public void getAuthenticatorId() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    if (!this.mRemote.transact(11, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getAuthenticatorId is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public void invalidateAuthenticatorId() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    if (!this.mRemote.transact(12, obtain, obtain2, 0)) {
                        throw new RemoteException("Method invalidateAuthenticatorId is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public void resetLockout(HardwareAuthToken hardwareAuthToken) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    obtain.writeTypedObject(hardwareAuthToken, 0);
                    if (!this.mRemote.transact(13, obtain, obtain2, 0)) {
                        throw new RemoteException("Method resetLockout is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public void close() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    if (!this.mRemote.transact(14, obtain, obtain2, 0)) {
                        throw new RemoteException("Method close is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public ICancellationSignal authenticateWithContext(long j, OperationContext operationContext) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    obtain.writeLong(j);
                    obtain.writeTypedObject(operationContext, 0);
                    if (!this.mRemote.transact(15, obtain, obtain2, 0)) {
                        throw new RemoteException("Method authenticateWithContext is unimplemented.");
                    }
                    obtain2.readException();
                    return ICancellationSignal.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public ICancellationSignal enrollWithContext(HardwareAuthToken hardwareAuthToken, byte b, byte[] bArr, NativeHandle nativeHandle, OperationContext operationContext) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    obtain.writeTypedObject(hardwareAuthToken, 0);
                    obtain.writeByte(b);
                    obtain.writeByteArray(bArr);
                    obtain.writeTypedObject(nativeHandle, 0);
                    obtain.writeTypedObject(operationContext, 0);
                    if (!this.mRemote.transact(16, obtain, obtain2, 0)) {
                        throw new RemoteException("Method enrollWithContext is unimplemented.");
                    }
                    obtain2.readException();
                    return ICancellationSignal.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.biometrics.face.ISession
            public ICancellationSignal detectInteractionWithContext(OperationContext operationContext) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(ISession.DESCRIPTOR);
                    obtain.writeTypedObject(operationContext, 0);
                    if (!this.mRemote.transact(17, obtain, obtain2, 0)) {
                        throw new RemoteException("Method detectInteractionWithContext is unimplemented.");
                    }
                    obtain2.readException();
                    return ICancellationSignal.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
