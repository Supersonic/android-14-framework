package android.hardware.location;

import android.Manifest;
import android.app.ActivityThread;
import android.content.AttributionSource;
import android.hardware.location.IGeofenceHardwareCallback;
import android.hardware.location.IGeofenceHardwareMonitorCallback;
import android.location.IFusedGeofenceHardware;
import android.location.IGpsGeofenceHardware;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IGeofenceHardware extends IInterface {
    boolean addCircularFence(int i, GeofenceHardwareRequestParcelable geofenceHardwareRequestParcelable, IGeofenceHardwareCallback iGeofenceHardwareCallback) throws RemoteException;

    int[] getMonitoringTypes() throws RemoteException;

    int getStatusOfMonitoringType(int i) throws RemoteException;

    boolean pauseGeofence(int i, int i2) throws RemoteException;

    boolean registerForMonitorStateChangeCallback(int i, IGeofenceHardwareMonitorCallback iGeofenceHardwareMonitorCallback) throws RemoteException;

    boolean removeGeofence(int i, int i2) throws RemoteException;

    boolean resumeGeofence(int i, int i2, int i3) throws RemoteException;

    void setFusedGeofenceHardware(IFusedGeofenceHardware iFusedGeofenceHardware) throws RemoteException;

    void setGpsGeofenceHardware(IGpsGeofenceHardware iGpsGeofenceHardware) throws RemoteException;

    boolean unregisterForMonitorStateChangeCallback(int i, IGeofenceHardwareMonitorCallback iGeofenceHardwareMonitorCallback) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IGeofenceHardware {
        @Override // android.hardware.location.IGeofenceHardware
        public void setGpsGeofenceHardware(IGpsGeofenceHardware service) throws RemoteException {
        }

        @Override // android.hardware.location.IGeofenceHardware
        public void setFusedGeofenceHardware(IFusedGeofenceHardware service) throws RemoteException {
        }

        @Override // android.hardware.location.IGeofenceHardware
        public int[] getMonitoringTypes() throws RemoteException {
            return null;
        }

        @Override // android.hardware.location.IGeofenceHardware
        public int getStatusOfMonitoringType(int monitoringType) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean addCircularFence(int monitoringType, GeofenceHardwareRequestParcelable request, IGeofenceHardwareCallback callback) throws RemoteException {
            return false;
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean removeGeofence(int id, int monitoringType) throws RemoteException {
            return false;
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean pauseGeofence(int id, int monitoringType) throws RemoteException {
            return false;
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean resumeGeofence(int id, int monitoringType, int monitorTransitions) throws RemoteException {
            return false;
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean registerForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) throws RemoteException {
            return false;
        }

        @Override // android.hardware.location.IGeofenceHardware
        public boolean unregisterForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IGeofenceHardware {
        public static final String DESCRIPTOR = "android.hardware.location.IGeofenceHardware";
        static final int TRANSACTION_addCircularFence = 5;
        static final int TRANSACTION_getMonitoringTypes = 3;
        static final int TRANSACTION_getStatusOfMonitoringType = 4;
        static final int TRANSACTION_pauseGeofence = 7;
        static final int TRANSACTION_registerForMonitorStateChangeCallback = 9;
        static final int TRANSACTION_removeGeofence = 6;
        static final int TRANSACTION_resumeGeofence = 8;
        static final int TRANSACTION_setFusedGeofenceHardware = 2;
        static final int TRANSACTION_setGpsGeofenceHardware = 1;
        static final int TRANSACTION_unregisterForMonitorStateChangeCallback = 10;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static IGeofenceHardware asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IGeofenceHardware)) {
                return (IGeofenceHardware) iin;
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
                    return "setGpsGeofenceHardware";
                case 2:
                    return "setFusedGeofenceHardware";
                case 3:
                    return "getMonitoringTypes";
                case 4:
                    return "getStatusOfMonitoringType";
                case 5:
                    return "addCircularFence";
                case 6:
                    return "removeGeofence";
                case 7:
                    return "pauseGeofence";
                case 8:
                    return "resumeGeofence";
                case 9:
                    return "registerForMonitorStateChangeCallback";
                case 10:
                    return "unregisterForMonitorStateChangeCallback";
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
                            IGpsGeofenceHardware _arg0 = IGpsGeofenceHardware.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setGpsGeofenceHardware(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            IFusedGeofenceHardware _arg02 = IFusedGeofenceHardware.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setFusedGeofenceHardware(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            int[] _result = getMonitoringTypes();
                            reply.writeNoException();
                            reply.writeIntArray(_result);
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result2 = getStatusOfMonitoringType(_arg03);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 5:
                            int _arg04 = data.readInt();
                            GeofenceHardwareRequestParcelable _arg1 = (GeofenceHardwareRequestParcelable) data.readTypedObject(GeofenceHardwareRequestParcelable.CREATOR);
                            IGeofenceHardwareCallback _arg2 = IGeofenceHardwareCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result3 = addCircularFence(_arg04, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 6:
                            int _arg05 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result4 = removeGeofence(_arg05, _arg12);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 7:
                            int _arg06 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result5 = pauseGeofence(_arg06, _arg13);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 8:
                            int _arg07 = data.readInt();
                            int _arg14 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result6 = resumeGeofence(_arg07, _arg14, _arg22);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 9:
                            int _arg08 = data.readInt();
                            IGeofenceHardwareMonitorCallback _arg15 = IGeofenceHardwareMonitorCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result7 = registerForMonitorStateChangeCallback(_arg08, _arg15);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 10:
                            int _arg09 = data.readInt();
                            IGeofenceHardwareMonitorCallback _arg16 = IGeofenceHardwareMonitorCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result8 = unregisterForMonitorStateChangeCallback(_arg09, _arg16);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IGeofenceHardware {
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

            @Override // android.hardware.location.IGeofenceHardware
            public void setGpsGeofenceHardware(IGpsGeofenceHardware service) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(service);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public void setFusedGeofenceHardware(IFusedGeofenceHardware service) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(service);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public int[] getMonitoringTypes() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public int getStatusOfMonitoringType(int monitoringType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(monitoringType);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public boolean addCircularFence(int monitoringType, GeofenceHardwareRequestParcelable request, IGeofenceHardwareCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(monitoringType);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public boolean removeGeofence(int id, int monitoringType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(monitoringType);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public boolean pauseGeofence(int id, int monitoringType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(monitoringType);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public boolean resumeGeofence(int id, int monitoringType, int monitorTransitions) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(monitoringType);
                    _data.writeInt(monitorTransitions);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public boolean registerForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(monitoringType);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public boolean unregisterForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(monitoringType);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void getMonitoringTypes_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.LOCATION_HARDWARE, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void getStatusOfMonitoringType_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.LOCATION_HARDWARE, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void addCircularFence_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.LOCATION_HARDWARE, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void removeGeofence_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.LOCATION_HARDWARE, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void pauseGeofence_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.LOCATION_HARDWARE, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void resumeGeofence_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.LOCATION_HARDWARE, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void registerForMonitorStateChangeCallback_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.LOCATION_HARDWARE, source);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void unregisterForMonitorStateChangeCallback_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.LOCATION_HARDWARE, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
