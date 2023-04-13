package android.hardware;

import android.hardware.ISensorPrivacyListener;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ISensorPrivacyManager extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.ISensorPrivacyManager";

    void addSensorPrivacyListener(ISensorPrivacyListener iSensorPrivacyListener) throws RemoteException;

    void addToggleSensorPrivacyListener(ISensorPrivacyListener iSensorPrivacyListener) throws RemoteException;

    boolean isCombinedToggleSensorPrivacyEnabled(int i) throws RemoteException;

    boolean isSensorPrivacyEnabled() throws RemoteException;

    boolean isToggleSensorPrivacyEnabled(int i, int i2) throws RemoteException;

    void removeSensorPrivacyListener(ISensorPrivacyListener iSensorPrivacyListener) throws RemoteException;

    void removeToggleSensorPrivacyListener(ISensorPrivacyListener iSensorPrivacyListener) throws RemoteException;

    boolean requiresAuthentication() throws RemoteException;

    void setSensorPrivacy(boolean z) throws RemoteException;

    void setToggleSensorPrivacy(int i, int i2, int i3, boolean z) throws RemoteException;

    void setToggleSensorPrivacyForProfileGroup(int i, int i2, int i3, boolean z) throws RemoteException;

    void showSensorUseDialog(int i) throws RemoteException;

    boolean supportsSensorToggle(int i, int i2) throws RemoteException;

    void suppressToggleSensorPrivacyReminders(int i, int i2, IBinder iBinder, boolean z) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ISensorPrivacyManager {
        @Override // android.hardware.ISensorPrivacyManager
        public boolean supportsSensorToggle(int toggleType, int sensor) throws RemoteException {
            return false;
        }

        @Override // android.hardware.ISensorPrivacyManager
        public void addSensorPrivacyListener(ISensorPrivacyListener listener) throws RemoteException {
        }

        @Override // android.hardware.ISensorPrivacyManager
        public void addToggleSensorPrivacyListener(ISensorPrivacyListener listener) throws RemoteException {
        }

        @Override // android.hardware.ISensorPrivacyManager
        public void removeSensorPrivacyListener(ISensorPrivacyListener listener) throws RemoteException {
        }

        @Override // android.hardware.ISensorPrivacyManager
        public void removeToggleSensorPrivacyListener(ISensorPrivacyListener listener) throws RemoteException {
        }

        @Override // android.hardware.ISensorPrivacyManager
        public boolean isSensorPrivacyEnabled() throws RemoteException {
            return false;
        }

        @Override // android.hardware.ISensorPrivacyManager
        public boolean isCombinedToggleSensorPrivacyEnabled(int sensor) throws RemoteException {
            return false;
        }

        @Override // android.hardware.ISensorPrivacyManager
        public boolean isToggleSensorPrivacyEnabled(int toggleType, int sensor) throws RemoteException {
            return false;
        }

        @Override // android.hardware.ISensorPrivacyManager
        public void setSensorPrivacy(boolean enable) throws RemoteException {
        }

        @Override // android.hardware.ISensorPrivacyManager
        public void setToggleSensorPrivacy(int userId, int source, int sensor, boolean enable) throws RemoteException {
        }

        @Override // android.hardware.ISensorPrivacyManager
        public void setToggleSensorPrivacyForProfileGroup(int userId, int source, int sensor, boolean enable) throws RemoteException {
        }

        @Override // android.hardware.ISensorPrivacyManager
        public void suppressToggleSensorPrivacyReminders(int userId, int sensor, IBinder token, boolean suppress) throws RemoteException {
        }

        @Override // android.hardware.ISensorPrivacyManager
        public boolean requiresAuthentication() throws RemoteException {
            return false;
        }

        @Override // android.hardware.ISensorPrivacyManager
        public void showSensorUseDialog(int sensor) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ISensorPrivacyManager {
        static final int TRANSACTION_addSensorPrivacyListener = 2;
        static final int TRANSACTION_addToggleSensorPrivacyListener = 3;
        static final int TRANSACTION_isCombinedToggleSensorPrivacyEnabled = 7;
        static final int TRANSACTION_isSensorPrivacyEnabled = 6;
        static final int TRANSACTION_isToggleSensorPrivacyEnabled = 8;
        static final int TRANSACTION_removeSensorPrivacyListener = 4;
        static final int TRANSACTION_removeToggleSensorPrivacyListener = 5;
        static final int TRANSACTION_requiresAuthentication = 13;
        static final int TRANSACTION_setSensorPrivacy = 9;
        static final int TRANSACTION_setToggleSensorPrivacy = 10;
        static final int TRANSACTION_setToggleSensorPrivacyForProfileGroup = 11;
        static final int TRANSACTION_showSensorUseDialog = 14;
        static final int TRANSACTION_supportsSensorToggle = 1;
        static final int TRANSACTION_suppressToggleSensorPrivacyReminders = 12;

        public Stub() {
            attachInterface(this, ISensorPrivacyManager.DESCRIPTOR);
        }

        public static ISensorPrivacyManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISensorPrivacyManager.DESCRIPTOR);
            if (iin != null && (iin instanceof ISensorPrivacyManager)) {
                return (ISensorPrivacyManager) iin;
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
                    return "supportsSensorToggle";
                case 2:
                    return "addSensorPrivacyListener";
                case 3:
                    return "addToggleSensorPrivacyListener";
                case 4:
                    return "removeSensorPrivacyListener";
                case 5:
                    return "removeToggleSensorPrivacyListener";
                case 6:
                    return "isSensorPrivacyEnabled";
                case 7:
                    return "isCombinedToggleSensorPrivacyEnabled";
                case 8:
                    return "isToggleSensorPrivacyEnabled";
                case 9:
                    return "setSensorPrivacy";
                case 10:
                    return "setToggleSensorPrivacy";
                case 11:
                    return "setToggleSensorPrivacyForProfileGroup";
                case 12:
                    return "suppressToggleSensorPrivacyReminders";
                case 13:
                    return "requiresAuthentication";
                case 14:
                    return "showSensorUseDialog";
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
                data.enforceInterface(ISensorPrivacyManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISensorPrivacyManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result = supportsSensorToggle(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            ISensorPrivacyListener _arg02 = ISensorPrivacyListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addSensorPrivacyListener(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            ISensorPrivacyListener _arg03 = ISensorPrivacyListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addToggleSensorPrivacyListener(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            ISensorPrivacyListener _arg04 = ISensorPrivacyListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeSensorPrivacyListener(_arg04);
                            reply.writeNoException();
                            break;
                        case 5:
                            ISensorPrivacyListener _arg05 = ISensorPrivacyListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeToggleSensorPrivacyListener(_arg05);
                            reply.writeNoException();
                            break;
                        case 6:
                            boolean _result2 = isSensorPrivacyEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 7:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result3 = isCombinedToggleSensorPrivacyEnabled(_arg06);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 8:
                            int _arg07 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result4 = isToggleSensorPrivacyEnabled(_arg07, _arg12);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 9:
                            boolean _arg08 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSensorPrivacy(_arg08);
                            reply.writeNoException();
                            break;
                        case 10:
                            int _arg09 = data.readInt();
                            int _arg13 = data.readInt();
                            int _arg2 = data.readInt();
                            boolean _arg3 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setToggleSensorPrivacy(_arg09, _arg13, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 11:
                            int _arg010 = data.readInt();
                            int _arg14 = data.readInt();
                            int _arg22 = data.readInt();
                            boolean _arg32 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setToggleSensorPrivacyForProfileGroup(_arg010, _arg14, _arg22, _arg32);
                            reply.writeNoException();
                            break;
                        case 12:
                            int _arg011 = data.readInt();
                            int _arg15 = data.readInt();
                            IBinder _arg23 = data.readStrongBinder();
                            boolean _arg33 = data.readBoolean();
                            data.enforceNoDataAvail();
                            suppressToggleSensorPrivacyReminders(_arg011, _arg15, _arg23, _arg33);
                            reply.writeNoException();
                            break;
                        case 13:
                            boolean _result5 = requiresAuthentication();
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 14:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            showSensorUseDialog(_arg012);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ISensorPrivacyManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISensorPrivacyManager.DESCRIPTOR;
            }

            @Override // android.hardware.ISensorPrivacyManager
            public boolean supportsSensorToggle(int toggleType, int sensor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISensorPrivacyManager.DESCRIPTOR);
                    _data.writeInt(toggleType);
                    _data.writeInt(sensor);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ISensorPrivacyManager
            public void addSensorPrivacyListener(ISensorPrivacyListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISensorPrivacyManager.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ISensorPrivacyManager
            public void addToggleSensorPrivacyListener(ISensorPrivacyListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISensorPrivacyManager.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ISensorPrivacyManager
            public void removeSensorPrivacyListener(ISensorPrivacyListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISensorPrivacyManager.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ISensorPrivacyManager
            public void removeToggleSensorPrivacyListener(ISensorPrivacyListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISensorPrivacyManager.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ISensorPrivacyManager
            public boolean isSensorPrivacyEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISensorPrivacyManager.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ISensorPrivacyManager
            public boolean isCombinedToggleSensorPrivacyEnabled(int sensor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISensorPrivacyManager.DESCRIPTOR);
                    _data.writeInt(sensor);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ISensorPrivacyManager
            public boolean isToggleSensorPrivacyEnabled(int toggleType, int sensor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISensorPrivacyManager.DESCRIPTOR);
                    _data.writeInt(toggleType);
                    _data.writeInt(sensor);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ISensorPrivacyManager
            public void setSensorPrivacy(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISensorPrivacyManager.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ISensorPrivacyManager
            public void setToggleSensorPrivacy(int userId, int source, int sensor, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISensorPrivacyManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeInt(source);
                    _data.writeInt(sensor);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ISensorPrivacyManager
            public void setToggleSensorPrivacyForProfileGroup(int userId, int source, int sensor, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISensorPrivacyManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeInt(source);
                    _data.writeInt(sensor);
                    _data.writeBoolean(enable);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ISensorPrivacyManager
            public void suppressToggleSensorPrivacyReminders(int userId, int sensor, IBinder token, boolean suppress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISensorPrivacyManager.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeInt(sensor);
                    _data.writeStrongBinder(token);
                    _data.writeBoolean(suppress);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ISensorPrivacyManager
            public boolean requiresAuthentication() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISensorPrivacyManager.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.ISensorPrivacyManager
            public void showSensorUseDialog(int sensor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISensorPrivacyManager.DESCRIPTOR);
                    _data.writeInt(sensor);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 13;
        }
    }
}
