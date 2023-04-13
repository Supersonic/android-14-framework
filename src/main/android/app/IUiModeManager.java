package android.app;

import android.app.IOnProjectionStateChangedListener;
import android.app.IUiModeManagerCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IUiModeManager extends IInterface {
    void addCallback(IUiModeManagerCallback iUiModeManagerCallback) throws RemoteException;

    void addOnProjectionStateChangedListener(IOnProjectionStateChangedListener iOnProjectionStateChangedListener, int i) throws RemoteException;

    void disableCarMode(int i) throws RemoteException;

    void disableCarModeByCallingPackage(int i, String str) throws RemoteException;

    void enableCarMode(int i, int i2, String str) throws RemoteException;

    int getActiveProjectionTypes() throws RemoteException;

    float getContrast() throws RemoteException;

    int getCurrentModeType() throws RemoteException;

    long getCustomNightModeEnd() throws RemoteException;

    long getCustomNightModeStart() throws RemoteException;

    int getNightMode() throws RemoteException;

    int getNightModeCustomType() throws RemoteException;

    List<String> getProjectingPackages(int i) throws RemoteException;

    boolean isNightModeLocked() throws RemoteException;

    boolean isUiModeLocked() throws RemoteException;

    boolean releaseProjection(int i, String str) throws RemoteException;

    void removeOnProjectionStateChangedListener(IOnProjectionStateChangedListener iOnProjectionStateChangedListener) throws RemoteException;

    boolean requestProjection(IBinder iBinder, int i, String str) throws RemoteException;

    void setApplicationNightMode(int i) throws RemoteException;

    void setCustomNightModeEnd(long j) throws RemoteException;

    void setCustomNightModeStart(long j) throws RemoteException;

    void setNightMode(int i) throws RemoteException;

    boolean setNightModeActivated(boolean z) throws RemoteException;

    boolean setNightModeActivatedForCustomMode(int i, boolean z) throws RemoteException;

    void setNightModeCustomType(int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IUiModeManager {
        @Override // android.app.IUiModeManager
        public void addCallback(IUiModeManagerCallback callback) throws RemoteException {
        }

        @Override // android.app.IUiModeManager
        public void enableCarMode(int flags, int priority, String callingPackage) throws RemoteException {
        }

        @Override // android.app.IUiModeManager
        public void disableCarMode(int flags) throws RemoteException {
        }

        @Override // android.app.IUiModeManager
        public void disableCarModeByCallingPackage(int flags, String callingPackage) throws RemoteException {
        }

        @Override // android.app.IUiModeManager
        public int getCurrentModeType() throws RemoteException {
            return 0;
        }

        @Override // android.app.IUiModeManager
        public void setNightMode(int mode) throws RemoteException {
        }

        @Override // android.app.IUiModeManager
        public int getNightMode() throws RemoteException {
            return 0;
        }

        @Override // android.app.IUiModeManager
        public void setNightModeCustomType(int nightModeCustomType) throws RemoteException {
        }

        @Override // android.app.IUiModeManager
        public int getNightModeCustomType() throws RemoteException {
            return 0;
        }

        @Override // android.app.IUiModeManager
        public void setApplicationNightMode(int mode) throws RemoteException {
        }

        @Override // android.app.IUiModeManager
        public boolean isUiModeLocked() throws RemoteException {
            return false;
        }

        @Override // android.app.IUiModeManager
        public boolean isNightModeLocked() throws RemoteException {
            return false;
        }

        @Override // android.app.IUiModeManager
        public boolean setNightModeActivatedForCustomMode(int nightModeCustom, boolean active) throws RemoteException {
            return false;
        }

        @Override // android.app.IUiModeManager
        public boolean setNightModeActivated(boolean active) throws RemoteException {
            return false;
        }

        @Override // android.app.IUiModeManager
        public long getCustomNightModeStart() throws RemoteException {
            return 0L;
        }

        @Override // android.app.IUiModeManager
        public void setCustomNightModeStart(long time) throws RemoteException {
        }

        @Override // android.app.IUiModeManager
        public long getCustomNightModeEnd() throws RemoteException {
            return 0L;
        }

        @Override // android.app.IUiModeManager
        public void setCustomNightModeEnd(long time) throws RemoteException {
        }

        @Override // android.app.IUiModeManager
        public boolean requestProjection(IBinder binder, int projectionType, String callingPackage) throws RemoteException {
            return false;
        }

        @Override // android.app.IUiModeManager
        public boolean releaseProjection(int projectionType, String callingPackage) throws RemoteException {
            return false;
        }

        @Override // android.app.IUiModeManager
        public void addOnProjectionStateChangedListener(IOnProjectionStateChangedListener listener, int projectionType) throws RemoteException {
        }

        @Override // android.app.IUiModeManager
        public void removeOnProjectionStateChangedListener(IOnProjectionStateChangedListener listener) throws RemoteException {
        }

        @Override // android.app.IUiModeManager
        public List<String> getProjectingPackages(int projectionType) throws RemoteException {
            return null;
        }

        @Override // android.app.IUiModeManager
        public int getActiveProjectionTypes() throws RemoteException {
            return 0;
        }

        @Override // android.app.IUiModeManager
        public float getContrast() throws RemoteException {
            return 0.0f;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IUiModeManager {
        public static final String DESCRIPTOR = "android.app.IUiModeManager";
        static final int TRANSACTION_addCallback = 1;
        static final int TRANSACTION_addOnProjectionStateChangedListener = 21;
        static final int TRANSACTION_disableCarMode = 3;
        static final int TRANSACTION_disableCarModeByCallingPackage = 4;
        static final int TRANSACTION_enableCarMode = 2;
        static final int TRANSACTION_getActiveProjectionTypes = 24;
        static final int TRANSACTION_getContrast = 25;
        static final int TRANSACTION_getCurrentModeType = 5;
        static final int TRANSACTION_getCustomNightModeEnd = 17;
        static final int TRANSACTION_getCustomNightModeStart = 15;
        static final int TRANSACTION_getNightMode = 7;
        static final int TRANSACTION_getNightModeCustomType = 9;
        static final int TRANSACTION_getProjectingPackages = 23;
        static final int TRANSACTION_isNightModeLocked = 12;
        static final int TRANSACTION_isUiModeLocked = 11;
        static final int TRANSACTION_releaseProjection = 20;
        static final int TRANSACTION_removeOnProjectionStateChangedListener = 22;
        static final int TRANSACTION_requestProjection = 19;
        static final int TRANSACTION_setApplicationNightMode = 10;
        static final int TRANSACTION_setCustomNightModeEnd = 18;
        static final int TRANSACTION_setCustomNightModeStart = 16;
        static final int TRANSACTION_setNightMode = 6;
        static final int TRANSACTION_setNightModeActivated = 14;
        static final int TRANSACTION_setNightModeActivatedForCustomMode = 13;
        static final int TRANSACTION_setNightModeCustomType = 8;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IUiModeManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IUiModeManager)) {
                return (IUiModeManager) iin;
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
                    return "addCallback";
                case 2:
                    return "enableCarMode";
                case 3:
                    return "disableCarMode";
                case 4:
                    return "disableCarModeByCallingPackage";
                case 5:
                    return "getCurrentModeType";
                case 6:
                    return "setNightMode";
                case 7:
                    return "getNightMode";
                case 8:
                    return "setNightModeCustomType";
                case 9:
                    return "getNightModeCustomType";
                case 10:
                    return "setApplicationNightMode";
                case 11:
                    return "isUiModeLocked";
                case 12:
                    return "isNightModeLocked";
                case 13:
                    return "setNightModeActivatedForCustomMode";
                case 14:
                    return "setNightModeActivated";
                case 15:
                    return "getCustomNightModeStart";
                case 16:
                    return "setCustomNightModeStart";
                case 17:
                    return "getCustomNightModeEnd";
                case 18:
                    return "setCustomNightModeEnd";
                case 19:
                    return "requestProjection";
                case 20:
                    return "releaseProjection";
                case 21:
                    return "addOnProjectionStateChangedListener";
                case 22:
                    return "removeOnProjectionStateChangedListener";
                case 23:
                    return "getProjectingPackages";
                case 24:
                    return "getActiveProjectionTypes";
                case 25:
                    return "getContrast";
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
                            IUiModeManagerCallback _arg0 = IUiModeManagerCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addCallback(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg1 = data.readInt();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            enableCarMode(_arg02, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            disableCarMode(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            disableCarModeByCallingPackage(_arg04, _arg12);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _result = getCurrentModeType();
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 6:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            setNightMode(_arg05);
                            reply.writeNoException();
                            break;
                        case 7:
                            int _result2 = getNightMode();
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 8:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            setNightModeCustomType(_arg06);
                            reply.writeNoException();
                            break;
                        case 9:
                            int _result3 = getNightModeCustomType();
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 10:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            setApplicationNightMode(_arg07);
                            reply.writeNoException();
                            break;
                        case 11:
                            boolean _result4 = isUiModeLocked();
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 12:
                            boolean _result5 = isNightModeLocked();
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 13:
                            int _arg08 = data.readInt();
                            boolean _arg13 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result6 = setNightModeActivatedForCustomMode(_arg08, _arg13);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 14:
                            boolean _arg09 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result7 = setNightModeActivated(_arg09);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 15:
                            long _result8 = getCustomNightModeStart();
                            reply.writeNoException();
                            reply.writeLong(_result8);
                            break;
                        case 16:
                            long _arg010 = data.readLong();
                            data.enforceNoDataAvail();
                            setCustomNightModeStart(_arg010);
                            reply.writeNoException();
                            break;
                        case 17:
                            long _result9 = getCustomNightModeEnd();
                            reply.writeNoException();
                            reply.writeLong(_result9);
                            break;
                        case 18:
                            long _arg011 = data.readLong();
                            data.enforceNoDataAvail();
                            setCustomNightModeEnd(_arg011);
                            reply.writeNoException();
                            break;
                        case 19:
                            IBinder _arg012 = data.readStrongBinder();
                            int _arg14 = data.readInt();
                            String _arg22 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result10 = requestProjection(_arg012, _arg14, _arg22);
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            break;
                        case 20:
                            int _arg013 = data.readInt();
                            String _arg15 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result11 = releaseProjection(_arg013, _arg15);
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            break;
                        case 21:
                            IOnProjectionStateChangedListener _arg014 = IOnProjectionStateChangedListener.Stub.asInterface(data.readStrongBinder());
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            addOnProjectionStateChangedListener(_arg014, _arg16);
                            reply.writeNoException();
                            break;
                        case 22:
                            IOnProjectionStateChangedListener _arg015 = IOnProjectionStateChangedListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeOnProjectionStateChangedListener(_arg015);
                            reply.writeNoException();
                            break;
                        case 23:
                            int _arg016 = data.readInt();
                            data.enforceNoDataAvail();
                            List<String> _result12 = getProjectingPackages(_arg016);
                            reply.writeNoException();
                            reply.writeStringList(_result12);
                            break;
                        case 24:
                            int _result13 = getActiveProjectionTypes();
                            reply.writeNoException();
                            reply.writeInt(_result13);
                            break;
                        case 25:
                            float _result14 = getContrast();
                            reply.writeNoException();
                            reply.writeFloat(_result14);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IUiModeManager {
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

            @Override // android.app.IUiModeManager
            public void addCallback(IUiModeManagerCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public void enableCarMode(int flags, int priority, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeInt(priority);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public void disableCarMode(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public void disableCarModeByCallingPackage(int flags, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public int getCurrentModeType() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public void setNightMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public int getNightMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public void setNightModeCustomType(int nightModeCustomType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nightModeCustomType);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public int getNightModeCustomType() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public void setApplicationNightMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public boolean isUiModeLocked() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public boolean isNightModeLocked() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public boolean setNightModeActivatedForCustomMode(int nightModeCustom, boolean active) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nightModeCustom);
                    _data.writeBoolean(active);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public boolean setNightModeActivated(boolean active) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(active);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public long getCustomNightModeStart() throws RemoteException {
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

            @Override // android.app.IUiModeManager
            public void setCustomNightModeStart(long time) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(time);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public long getCustomNightModeEnd() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public void setCustomNightModeEnd(long time) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(time);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public boolean requestProjection(IBinder binder, int projectionType, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(binder);
                    _data.writeInt(projectionType);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public boolean releaseProjection(int projectionType, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(projectionType);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public void addOnProjectionStateChangedListener(IOnProjectionStateChangedListener listener, int projectionType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(projectionType);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public void removeOnProjectionStateChangedListener(IOnProjectionStateChangedListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public List<String> getProjectingPackages(int projectionType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(projectionType);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public int getActiveProjectionTypes() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.IUiModeManager
            public float getContrast() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 24;
        }
    }
}
