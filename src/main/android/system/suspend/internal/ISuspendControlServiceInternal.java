package android.system.suspend.internal;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface ISuspendControlServiceInternal extends IInterface {
    public static final String DESCRIPTOR = "android$system$suspend$internal$ISuspendControlServiceInternal".replace('$', '.');

    boolean enableAutosuspend(IBinder iBinder) throws RemoteException;

    boolean forceSuspend() throws RemoteException;

    SuspendInfo getSuspendStats() throws RemoteException;

    WakeLockInfo[] getWakeLockStats() throws RemoteException;

    WakeupInfo[] getWakeupStats() throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISuspendControlServiceInternal {
        @Override // android.system.suspend.internal.ISuspendControlServiceInternal
        public boolean enableAutosuspend(IBinder token) throws RemoteException {
            return false;
        }

        @Override // android.system.suspend.internal.ISuspendControlServiceInternal
        public boolean forceSuspend() throws RemoteException {
            return false;
        }

        @Override // android.system.suspend.internal.ISuspendControlServiceInternal
        public WakeLockInfo[] getWakeLockStats() throws RemoteException {
            return null;
        }

        @Override // android.system.suspend.internal.ISuspendControlServiceInternal
        public WakeupInfo[] getWakeupStats() throws RemoteException {
            return null;
        }

        @Override // android.system.suspend.internal.ISuspendControlServiceInternal
        public SuspendInfo getSuspendStats() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISuspendControlServiceInternal {
        static final int TRANSACTION_enableAutosuspend = 1;
        static final int TRANSACTION_forceSuspend = 2;
        static final int TRANSACTION_getSuspendStats = 5;
        static final int TRANSACTION_getWakeLockStats = 3;
        static final int TRANSACTION_getWakeupStats = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISuspendControlServiceInternal asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISuspendControlServiceInternal)) {
                return (ISuspendControlServiceInternal) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(descriptor);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IBinder _arg0 = data.readStrongBinder();
                            boolean enableAutosuspend = enableAutosuspend(_arg0);
                            reply.writeNoException();
                            reply.writeInt(enableAutosuspend ? 1 : 0);
                            break;
                        case 2:
                            boolean forceSuspend = forceSuspend();
                            reply.writeNoException();
                            reply.writeInt(forceSuspend ? 1 : 0);
                            break;
                        case 3:
                            WakeLockInfo[] _result = getWakeLockStats();
                            reply.writeNoException();
                            reply.writeTypedArray(_result, 1);
                            break;
                        case 4:
                            WakeupInfo[] _result2 = getWakeupStats();
                            reply.writeNoException();
                            reply.writeTypedArray(_result2, 1);
                            break;
                        case 5:
                            SuspendInfo _result3 = getSuspendStats();
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISuspendControlServiceInternal {
            private IBinder mRemote;

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

            @Override // android.system.suspend.internal.ISuspendControlServiceInternal
            public boolean enableAutosuspend(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _status = _reply.readInt() != 0;
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.system.suspend.internal.ISuspendControlServiceInternal
            public boolean forceSuspend() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _status = _reply.readInt() != 0;
                    return _status;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.system.suspend.internal.ISuspendControlServiceInternal
            public WakeLockInfo[] getWakeLockStats() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    WakeLockInfo[] _result = (WakeLockInfo[]) _reply.createTypedArray(WakeLockInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.system.suspend.internal.ISuspendControlServiceInternal
            public WakeupInfo[] getWakeupStats() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    WakeupInfo[] _result = (WakeupInfo[]) _reply.createTypedArray(WakeupInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.system.suspend.internal.ISuspendControlServiceInternal
            public SuspendInfo getSuspendStats() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    SuspendInfo _result = (SuspendInfo) _reply.readTypedObject(SuspendInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
