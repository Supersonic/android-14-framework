package android.location;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IGpsGeofenceHardware extends IInterface {
    boolean addCircularHardwareGeofence(int i, double d, double d2, double d3, int i2, int i3, int i4, int i5) throws RemoteException;

    boolean isHardwareGeofenceSupported() throws RemoteException;

    boolean pauseHardwareGeofence(int i) throws RemoteException;

    boolean removeHardwareGeofence(int i) throws RemoteException;

    boolean resumeHardwareGeofence(int i, int i2) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IGpsGeofenceHardware {
        @Override // android.location.IGpsGeofenceHardware
        public boolean isHardwareGeofenceSupported() throws RemoteException {
            return false;
        }

        @Override // android.location.IGpsGeofenceHardware
        public boolean addCircularHardwareGeofence(int geofenceId, double latitude, double longitude, double radius, int lastTransition, int monitorTransition, int notificationResponsiveness, int unknownTimer) throws RemoteException {
            return false;
        }

        @Override // android.location.IGpsGeofenceHardware
        public boolean removeHardwareGeofence(int geofenceId) throws RemoteException {
            return false;
        }

        @Override // android.location.IGpsGeofenceHardware
        public boolean pauseHardwareGeofence(int geofenceId) throws RemoteException {
            return false;
        }

        @Override // android.location.IGpsGeofenceHardware
        public boolean resumeHardwareGeofence(int geofenceId, int monitorTransition) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IGpsGeofenceHardware {
        public static final String DESCRIPTOR = "android.location.IGpsGeofenceHardware";
        static final int TRANSACTION_addCircularHardwareGeofence = 2;
        static final int TRANSACTION_isHardwareGeofenceSupported = 1;
        static final int TRANSACTION_pauseHardwareGeofence = 4;
        static final int TRANSACTION_removeHardwareGeofence = 3;
        static final int TRANSACTION_resumeHardwareGeofence = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IGpsGeofenceHardware asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IGpsGeofenceHardware)) {
                return (IGpsGeofenceHardware) iin;
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
                    return "isHardwareGeofenceSupported";
                case 2:
                    return "addCircularHardwareGeofence";
                case 3:
                    return "removeHardwareGeofence";
                case 4:
                    return "pauseHardwareGeofence";
                case 5:
                    return "resumeHardwareGeofence";
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
                            boolean _result = isHardwareGeofenceSupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            return true;
                        case 2:
                            int _arg0 = data.readInt();
                            double _arg1 = data.readDouble();
                            double _arg2 = data.readDouble();
                            double _arg3 = data.readDouble();
                            int _arg4 = data.readInt();
                            int _arg5 = data.readInt();
                            int _arg6 = data.readInt();
                            int _arg7 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result2 = addCircularHardwareGeofence(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            return true;
                        case 3:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result3 = removeHardwareGeofence(_arg02);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            return true;
                        case 4:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result4 = pauseHardwareGeofence(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            return true;
                        case 5:
                            int _arg04 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result5 = resumeHardwareGeofence(_arg04, _arg12);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IGpsGeofenceHardware {
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

            @Override // android.location.IGpsGeofenceHardware
            public boolean isHardwareGeofenceSupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.IGpsGeofenceHardware
            public boolean addCircularHardwareGeofence(int geofenceId, double latitude, double longitude, double radius, int lastTransition, int monitorTransition, int notificationResponsiveness, int unknownTimer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(geofenceId);
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    _data.writeDouble(latitude);
                    try {
                        _data.writeDouble(longitude);
                        try {
                            _data.writeDouble(radius);
                            try {
                                _data.writeInt(lastTransition);
                            } catch (Throwable th2) {
                                th = th2;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(monitorTransition);
                        try {
                            _data.writeInt(notificationResponsiveness);
                            try {
                                _data.writeInt(unknownTimer);
                                try {
                                    this.mRemote.transact(2, _data, _reply, 0);
                                    _reply.readException();
                                    boolean _result = _reply.readBoolean();
                                    _reply.recycle();
                                    _data.recycle();
                                    return _result;
                                } catch (Throwable th5) {
                                    th = th5;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th6) {
                                th = th6;
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th8) {
                        th = th8;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th9) {
                    th = th9;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.location.IGpsGeofenceHardware
            public boolean removeHardwareGeofence(int geofenceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(geofenceId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.IGpsGeofenceHardware
            public boolean pauseHardwareGeofence(int geofenceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(geofenceId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.location.IGpsGeofenceHardware
            public boolean resumeHardwareGeofence(int geofenceId, int monitorTransition) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(geofenceId);
                    _data.writeInt(monitorTransition);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }
}
