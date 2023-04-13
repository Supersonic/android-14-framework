package android.companion.virtual.sensor;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.SharedMemory;
/* loaded from: classes.dex */
public interface IVirtualSensorCallback extends IInterface {
    public static final String DESCRIPTOR = "android.companion.virtual.sensor.IVirtualSensorCallback";

    void onConfigurationChanged(VirtualSensor virtualSensor, boolean z, int i, int i2) throws RemoteException;

    void onDirectChannelConfigured(int i, VirtualSensor virtualSensor, int i2, int i3) throws RemoteException;

    void onDirectChannelCreated(int i, SharedMemory sharedMemory) throws RemoteException;

    void onDirectChannelDestroyed(int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IVirtualSensorCallback {
        @Override // android.companion.virtual.sensor.IVirtualSensorCallback
        public void onConfigurationChanged(VirtualSensor sensor, boolean enabled, int samplingPeriodMicros, int batchReportLatencyMicros) throws RemoteException {
        }

        @Override // android.companion.virtual.sensor.IVirtualSensorCallback
        public void onDirectChannelCreated(int channelHandle, SharedMemory sharedMemory) throws RemoteException {
        }

        @Override // android.companion.virtual.sensor.IVirtualSensorCallback
        public void onDirectChannelDestroyed(int channelHandle) throws RemoteException {
        }

        @Override // android.companion.virtual.sensor.IVirtualSensorCallback
        public void onDirectChannelConfigured(int channelHandle, VirtualSensor sensor, int rateLevel, int reportToken) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IVirtualSensorCallback {
        static final int TRANSACTION_onConfigurationChanged = 1;
        static final int TRANSACTION_onDirectChannelConfigured = 4;
        static final int TRANSACTION_onDirectChannelCreated = 2;
        static final int TRANSACTION_onDirectChannelDestroyed = 3;

        public Stub() {
            attachInterface(this, IVirtualSensorCallback.DESCRIPTOR);
        }

        public static IVirtualSensorCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IVirtualSensorCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IVirtualSensorCallback)) {
                return (IVirtualSensorCallback) iin;
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
                    return "onConfigurationChanged";
                case 2:
                    return "onDirectChannelCreated";
                case 3:
                    return "onDirectChannelDestroyed";
                case 4:
                    return "onDirectChannelConfigured";
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
                data.enforceInterface(IVirtualSensorCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IVirtualSensorCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            VirtualSensor _arg0 = (VirtualSensor) data.readTypedObject(VirtualSensor.CREATOR);
                            boolean _arg1 = data.readBoolean();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            onConfigurationChanged(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            SharedMemory _arg12 = (SharedMemory) data.readTypedObject(SharedMemory.CREATOR);
                            data.enforceNoDataAvail();
                            onDirectChannelCreated(_arg02, _arg12);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onDirectChannelDestroyed(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            VirtualSensor _arg13 = (VirtualSensor) data.readTypedObject(VirtualSensor.CREATOR);
                            int _arg22 = data.readInt();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            onDirectChannelConfigured(_arg04, _arg13, _arg22, _arg32);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IVirtualSensorCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IVirtualSensorCallback.DESCRIPTOR;
            }

            @Override // android.companion.virtual.sensor.IVirtualSensorCallback
            public void onConfigurationChanged(VirtualSensor sensor, boolean enabled, int samplingPeriodMicros, int batchReportLatencyMicros) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IVirtualSensorCallback.DESCRIPTOR);
                    _data.writeTypedObject(sensor, 0);
                    _data.writeBoolean(enabled);
                    _data.writeInt(samplingPeriodMicros);
                    _data.writeInt(batchReportLatencyMicros);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.sensor.IVirtualSensorCallback
            public void onDirectChannelCreated(int channelHandle, SharedMemory sharedMemory) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IVirtualSensorCallback.DESCRIPTOR);
                    _data.writeInt(channelHandle);
                    _data.writeTypedObject(sharedMemory, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.sensor.IVirtualSensorCallback
            public void onDirectChannelDestroyed(int channelHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IVirtualSensorCallback.DESCRIPTOR);
                    _data.writeInt(channelHandle);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.sensor.IVirtualSensorCallback
            public void onDirectChannelConfigured(int channelHandle, VirtualSensor sensor, int rateLevel, int reportToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IVirtualSensorCallback.DESCRIPTOR);
                    _data.writeInt(channelHandle);
                    _data.writeTypedObject(sensor, 0);
                    _data.writeInt(rateLevel);
                    _data.writeInt(reportToken);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
