package android.hardware.biometrics.fingerprint;

import android.hardware.biometrics.common.ICancellationSignal;
import android.hardware.biometrics.common.OperationContext;
import android.hardware.keymaster.HardwareAuthToken;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ISession extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$biometrics$fingerprint$ISession".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 3;

    ICancellationSignal authenticate(long j) throws RemoteException;

    ICancellationSignal authenticateWithContext(long j, OperationContext operationContext) throws RemoteException;

    void close() throws RemoteException;

    ICancellationSignal detectInteraction() throws RemoteException;

    ICancellationSignal detectInteractionWithContext(OperationContext operationContext) throws RemoteException;

    ICancellationSignal enroll(HardwareAuthToken hardwareAuthToken) throws RemoteException;

    ICancellationSignal enrollWithContext(HardwareAuthToken hardwareAuthToken, OperationContext operationContext) throws RemoteException;

    void enumerateEnrollments() throws RemoteException;

    void generateChallenge() throws RemoteException;

    void getAuthenticatorId() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void invalidateAuthenticatorId() throws RemoteException;

    void onContextChanged(OperationContext operationContext) throws RemoteException;

    void onPointerCancelWithContext(PointerContext pointerContext) throws RemoteException;

    @Deprecated
    void onPointerDown(int i, int i2, int i3, float f, float f2) throws RemoteException;

    void onPointerDownWithContext(PointerContext pointerContext) throws RemoteException;

    @Deprecated
    void onPointerUp(int i) throws RemoteException;

    void onPointerUpWithContext(PointerContext pointerContext) throws RemoteException;

    void onUiReady() throws RemoteException;

    void removeEnrollments(int[] iArr) throws RemoteException;

    void resetLockout(HardwareAuthToken hardwareAuthToken) throws RemoteException;

    void revokeChallenge(long j) throws RemoteException;

    void setIgnoreDisplayTouches(boolean z) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ISession {
        @Override // android.hardware.biometrics.fingerprint.ISession
        public void generateChallenge() throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void revokeChallenge(long challenge) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public ICancellationSignal enroll(HardwareAuthToken hat) throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public ICancellationSignal authenticate(long operationId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public ICancellationSignal detectInteraction() throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void enumerateEnrollments() throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void removeEnrollments(int[] enrollmentIds) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void getAuthenticatorId() throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void invalidateAuthenticatorId() throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void resetLockout(HardwareAuthToken hat) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void close() throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void onPointerDown(int pointerId, int x, int y, float minor, float major) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void onPointerUp(int pointerId) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void onUiReady() throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public ICancellationSignal authenticateWithContext(long operationId, OperationContext context) throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public ICancellationSignal enrollWithContext(HardwareAuthToken hat, OperationContext context) throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public ICancellationSignal detectInteractionWithContext(OperationContext context) throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void onPointerDownWithContext(PointerContext context) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void onPointerUpWithContext(PointerContext context) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void onContextChanged(OperationContext context) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void onPointerCancelWithContext(PointerContext context) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public void setIgnoreDisplayTouches(boolean shouldIgnore) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.biometrics.fingerprint.ISession
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ISession {
        static final int TRANSACTION_authenticate = 4;
        static final int TRANSACTION_authenticateWithContext = 15;
        static final int TRANSACTION_close = 11;
        static final int TRANSACTION_detectInteraction = 5;
        static final int TRANSACTION_detectInteractionWithContext = 17;
        static final int TRANSACTION_enroll = 3;
        static final int TRANSACTION_enrollWithContext = 16;
        static final int TRANSACTION_enumerateEnrollments = 6;
        static final int TRANSACTION_generateChallenge = 1;
        static final int TRANSACTION_getAuthenticatorId = 8;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_invalidateAuthenticatorId = 9;
        static final int TRANSACTION_onContextChanged = 20;
        static final int TRANSACTION_onPointerCancelWithContext = 21;
        static final int TRANSACTION_onPointerDown = 12;
        static final int TRANSACTION_onPointerDownWithContext = 18;
        static final int TRANSACTION_onPointerUp = 13;
        static final int TRANSACTION_onPointerUpWithContext = 19;
        static final int TRANSACTION_onUiReady = 14;
        static final int TRANSACTION_removeEnrollments = 7;
        static final int TRANSACTION_resetLockout = 10;
        static final int TRANSACTION_revokeChallenge = 2;
        static final int TRANSACTION_setIgnoreDisplayTouches = 22;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static ISession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISession)) {
                return (ISession) iin;
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
                    return "generateChallenge";
                case 2:
                    return "revokeChallenge";
                case 3:
                    return "enroll";
                case 4:
                    return "authenticate";
                case 5:
                    return "detectInteraction";
                case 6:
                    return "enumerateEnrollments";
                case 7:
                    return "removeEnrollments";
                case 8:
                    return "getAuthenticatorId";
                case 9:
                    return "invalidateAuthenticatorId";
                case 10:
                    return "resetLockout";
                case 11:
                    return "close";
                case 12:
                    return "onPointerDown";
                case 13:
                    return "onPointerUp";
                case 14:
                    return "onUiReady";
                case 15:
                    return "authenticateWithContext";
                case 16:
                    return "enrollWithContext";
                case 17:
                    return "detectInteractionWithContext";
                case 18:
                    return "onPointerDownWithContext";
                case 19:
                    return "onPointerUpWithContext";
                case 20:
                    return "onContextChanged";
                case 21:
                    return "onPointerCancelWithContext";
                case 22:
                    return "setIgnoreDisplayTouches";
                case 16777214:
                    return "getInterfaceHash";
                case 16777215:
                    return "getInterfaceVersion";
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
            String descriptor = DESCRIPTOR;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(descriptor);
            }
            switch (code) {
                case 16777214:
                    reply.writeNoException();
                    reply.writeString(getInterfaceHash());
                    return true;
                case 16777215:
                    reply.writeNoException();
                    reply.writeInt(getInterfaceVersion());
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            generateChallenge();
                            reply.writeNoException();
                            break;
                        case 2:
                            long _arg0 = data.readLong();
                            data.enforceNoDataAvail();
                            revokeChallenge(_arg0);
                            reply.writeNoException();
                            break;
                        case 3:
                            HardwareAuthToken _arg02 = (HardwareAuthToken) data.readTypedObject(HardwareAuthToken.CREATOR);
                            data.enforceNoDataAvail();
                            ICancellationSignal _result = enroll(_arg02);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 4:
                            long _arg03 = data.readLong();
                            data.enforceNoDataAvail();
                            ICancellationSignal _result2 = authenticate(_arg03);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 5:
                            ICancellationSignal _result3 = detectInteraction();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        case 6:
                            enumerateEnrollments();
                            reply.writeNoException();
                            break;
                        case 7:
                            int[] _arg04 = data.createIntArray();
                            data.enforceNoDataAvail();
                            removeEnrollments(_arg04);
                            reply.writeNoException();
                            break;
                        case 8:
                            getAuthenticatorId();
                            reply.writeNoException();
                            break;
                        case 9:
                            invalidateAuthenticatorId();
                            reply.writeNoException();
                            break;
                        case 10:
                            HardwareAuthToken _arg05 = (HardwareAuthToken) data.readTypedObject(HardwareAuthToken.CREATOR);
                            data.enforceNoDataAvail();
                            resetLockout(_arg05);
                            reply.writeNoException();
                            break;
                        case 11:
                            close();
                            reply.writeNoException();
                            break;
                        case 12:
                            int _arg06 = data.readInt();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            float _arg3 = data.readFloat();
                            float _arg4 = data.readFloat();
                            data.enforceNoDataAvail();
                            onPointerDown(_arg06, _arg1, _arg2, _arg3, _arg4);
                            reply.writeNoException();
                            break;
                        case 13:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            onPointerUp(_arg07);
                            reply.writeNoException();
                            break;
                        case 14:
                            onUiReady();
                            reply.writeNoException();
                            break;
                        case 15:
                            long _arg08 = data.readLong();
                            OperationContext _arg12 = (OperationContext) data.readTypedObject(OperationContext.CREATOR);
                            data.enforceNoDataAvail();
                            ICancellationSignal _result4 = authenticateWithContext(_arg08, _arg12);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result4);
                            break;
                        case 16:
                            HardwareAuthToken _arg09 = (HardwareAuthToken) data.readTypedObject(HardwareAuthToken.CREATOR);
                            OperationContext _arg13 = (OperationContext) data.readTypedObject(OperationContext.CREATOR);
                            data.enforceNoDataAvail();
                            ICancellationSignal _result5 = enrollWithContext(_arg09, _arg13);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result5);
                            break;
                        case 17:
                            OperationContext _arg010 = (OperationContext) data.readTypedObject(OperationContext.CREATOR);
                            data.enforceNoDataAvail();
                            ICancellationSignal _result6 = detectInteractionWithContext(_arg010);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result6);
                            break;
                        case 18:
                            PointerContext _arg011 = (PointerContext) data.readTypedObject(PointerContext.CREATOR);
                            data.enforceNoDataAvail();
                            onPointerDownWithContext(_arg011);
                            reply.writeNoException();
                            break;
                        case 19:
                            PointerContext _arg012 = (PointerContext) data.readTypedObject(PointerContext.CREATOR);
                            data.enforceNoDataAvail();
                            onPointerUpWithContext(_arg012);
                            reply.writeNoException();
                            break;
                        case 20:
                            OperationContext _arg013 = (OperationContext) data.readTypedObject(OperationContext.CREATOR);
                            data.enforceNoDataAvail();
                            onContextChanged(_arg013);
                            reply.writeNoException();
                            break;
                        case 21:
                            PointerContext _arg014 = (PointerContext) data.readTypedObject(PointerContext.CREATOR);
                            data.enforceNoDataAvail();
                            onPointerCancelWithContext(_arg014);
                            reply.writeNoException();
                            break;
                        case 22:
                            boolean _arg015 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setIgnoreDisplayTouches(_arg015);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ISession {
            private IBinder mRemote;
            private int mCachedVersion = -1;
            private String mCachedHash = "-1";

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void generateChallenge() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method generateChallenge is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void revokeChallenge(long challenge) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(challenge);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method revokeChallenge is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public ICancellationSignal enroll(HardwareAuthToken hat) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(hat, 0);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method enroll is unimplemented.");
                    }
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public ICancellationSignal authenticate(long operationId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(operationId);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method authenticate is unimplemented.");
                    }
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public ICancellationSignal detectInteraction() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method detectInteraction is unimplemented.");
                    }
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void enumerateEnrollments() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method enumerateEnrollments is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void removeEnrollments(int[] enrollmentIds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeIntArray(enrollmentIds);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method removeEnrollments is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void getAuthenticatorId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getAuthenticatorId is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void invalidateAuthenticatorId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method invalidateAuthenticatorId is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void resetLockout(HardwareAuthToken hat) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(hat, 0);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method resetLockout is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method close is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void onPointerDown(int pointerId, int x, int y, float minor, float major) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(pointerId);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    _data.writeFloat(minor);
                    _data.writeFloat(major);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onPointerDown is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void onPointerUp(int pointerId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(pointerId);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onPointerUp is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void onUiReady() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onUiReady is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public ICancellationSignal authenticateWithContext(long operationId, OperationContext context) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(operationId);
                    _data.writeTypedObject(context, 0);
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method authenticateWithContext is unimplemented.");
                    }
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public ICancellationSignal enrollWithContext(HardwareAuthToken hat, OperationContext context) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(hat, 0);
                    _data.writeTypedObject(context, 0);
                    boolean _status = this.mRemote.transact(16, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method enrollWithContext is unimplemented.");
                    }
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public ICancellationSignal detectInteractionWithContext(OperationContext context) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(context, 0);
                    boolean _status = this.mRemote.transact(17, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method detectInteractionWithContext is unimplemented.");
                    }
                    _reply.readException();
                    ICancellationSignal _result = ICancellationSignal.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void onPointerDownWithContext(PointerContext context) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(context, 0);
                    boolean _status = this.mRemote.transact(18, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onPointerDownWithContext is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void onPointerUpWithContext(PointerContext context) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(context, 0);
                    boolean _status = this.mRemote.transact(19, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onPointerUpWithContext is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void onContextChanged(OperationContext context) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(context, 0);
                    boolean _status = this.mRemote.transact(20, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onContextChanged is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void onPointerCancelWithContext(PointerContext context) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(context, 0);
                    boolean _status = this.mRemote.transact(21, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onPointerCancelWithContext is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public void setIgnoreDisplayTouches(boolean shouldIgnore) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeBoolean(shouldIgnore);
                    boolean _status = this.mRemote.transact(22, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setIgnoreDisplayTouches is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    try {
                        data.writeInterfaceToken(DESCRIPTOR);
                        this.mRemote.transact(16777215, data, reply, 0);
                        reply.readException();
                        this.mCachedVersion = reply.readInt();
                    } finally {
                        reply.recycle();
                        data.recycle();
                    }
                }
                return this.mCachedVersion;
            }

            @Override // android.hardware.biometrics.fingerprint.ISession
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(16777214, data, reply, 0);
                    reply.readException();
                    this.mCachedHash = reply.readString();
                    reply.recycle();
                    data.recycle();
                }
                return this.mCachedHash;
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 16777214;
        }
    }
}
