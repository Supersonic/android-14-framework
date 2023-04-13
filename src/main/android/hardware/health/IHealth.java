package android.hardware.health;

import android.hardware.health.IHealthInfoCallback;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IHealth extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$health$IHealth".replace('$', '.');

    /* loaded from: classes.dex */
    public static class Default implements IHealth {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.hardware.health.IHealth
        public BatteryHealthData getBatteryHealthData() throws RemoteException {
            return null;
        }

        @Override // android.hardware.health.IHealth
        public int getCapacity() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.health.IHealth
        public int getChargeCounterUah() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.health.IHealth
        public int getChargeStatus() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.health.IHealth
        public int getChargingPolicy() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.health.IHealth
        public int getCurrentAverageMicroamps() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.health.IHealth
        public int getCurrentNowMicroamps() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.health.IHealth
        public long getEnergyCounterNwh() throws RemoteException {
            return 0L;
        }

        @Override // android.hardware.health.IHealth
        public HealthInfo getHealthInfo() throws RemoteException {
            return null;
        }

        @Override // android.hardware.health.IHealth
        public void registerCallback(IHealthInfoCallback iHealthInfoCallback) throws RemoteException {
        }

        @Override // android.hardware.health.IHealth
        public void unregisterCallback(IHealthInfoCallback iHealthInfoCallback) throws RemoteException {
        }

        @Override // android.hardware.health.IHealth
        public void update() throws RemoteException {
        }
    }

    BatteryHealthData getBatteryHealthData() throws RemoteException;

    int getCapacity() throws RemoteException;

    int getChargeCounterUah() throws RemoteException;

    int getChargeStatus() throws RemoteException;

    int getChargingPolicy() throws RemoteException;

    int getCurrentAverageMicroamps() throws RemoteException;

    int getCurrentNowMicroamps() throws RemoteException;

    DiskStats[] getDiskStats() throws RemoteException;

    long getEnergyCounterNwh() throws RemoteException;

    HealthInfo getHealthInfo() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    StorageInfo[] getStorageInfo() throws RemoteException;

    void registerCallback(IHealthInfoCallback iHealthInfoCallback) throws RemoteException;

    void setChargingPolicy(int i) throws RemoteException;

    void unregisterCallback(IHealthInfoCallback iHealthInfoCallback) throws RemoteException;

    void update() throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IHealth {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            markVintfStability();
            attachInterface(this, IHealth.DESCRIPTOR);
        }

        public static IHealth asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IHealth.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IHealth)) {
                return (IHealth) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = IHealth.DESCRIPTOR;
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
                            IHealthInfoCallback asInterface = IHealthInfoCallback.Stub.asInterface(parcel.readStrongBinder());
                            parcel.enforceNoDataAvail();
                            registerCallback(asInterface);
                            parcel2.writeNoException();
                            break;
                        case 2:
                            IHealthInfoCallback asInterface2 = IHealthInfoCallback.Stub.asInterface(parcel.readStrongBinder());
                            parcel.enforceNoDataAvail();
                            unregisterCallback(asInterface2);
                            parcel2.writeNoException();
                            break;
                        case 3:
                            update();
                            parcel2.writeNoException();
                            break;
                        case 4:
                            int chargeCounterUah = getChargeCounterUah();
                            parcel2.writeNoException();
                            parcel2.writeInt(chargeCounterUah);
                            break;
                        case 5:
                            int currentNowMicroamps = getCurrentNowMicroamps();
                            parcel2.writeNoException();
                            parcel2.writeInt(currentNowMicroamps);
                            break;
                        case 6:
                            int currentAverageMicroamps = getCurrentAverageMicroamps();
                            parcel2.writeNoException();
                            parcel2.writeInt(currentAverageMicroamps);
                            break;
                        case 7:
                            int capacity = getCapacity();
                            parcel2.writeNoException();
                            parcel2.writeInt(capacity);
                            break;
                        case 8:
                            long energyCounterNwh = getEnergyCounterNwh();
                            parcel2.writeNoException();
                            parcel2.writeLong(energyCounterNwh);
                            break;
                        case 9:
                            int chargeStatus = getChargeStatus();
                            parcel2.writeNoException();
                            parcel2.writeInt(chargeStatus);
                            break;
                        case 10:
                            StorageInfo[] storageInfo = getStorageInfo();
                            parcel2.writeNoException();
                            parcel2.writeTypedArray(storageInfo, 1);
                            break;
                        case 11:
                            DiskStats[] diskStats = getDiskStats();
                            parcel2.writeNoException();
                            parcel2.writeTypedArray(diskStats, 1);
                            break;
                        case 12:
                            HealthInfo healthInfo = getHealthInfo();
                            parcel2.writeNoException();
                            parcel2.writeTypedObject(healthInfo, 1);
                            break;
                        case 13:
                            int readInt = parcel.readInt();
                            parcel.enforceNoDataAvail();
                            setChargingPolicy(readInt);
                            parcel2.writeNoException();
                            break;
                        case 14:
                            int chargingPolicy = getChargingPolicy();
                            parcel2.writeNoException();
                            parcel2.writeInt(chargingPolicy);
                            break;
                        case 15:
                            BatteryHealthData batteryHealthData = getBatteryHealthData();
                            parcel2.writeNoException();
                            parcel2.writeTypedObject(batteryHealthData, 1);
                            break;
                        default:
                            return super.onTransact(i, parcel, parcel2, i2);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        public static class Proxy implements IHealth {
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

            @Override // android.hardware.health.IHealth
            public void registerCallback(IHealthInfoCallback iHealthInfoCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHealth.DESCRIPTOR);
                    obtain.writeStrongInterface(iHealthInfoCallback);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0)) {
                        throw new RemoteException("Method registerCallback is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.health.IHealth
            public void unregisterCallback(IHealthInfoCallback iHealthInfoCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHealth.DESCRIPTOR);
                    obtain.writeStrongInterface(iHealthInfoCallback);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0)) {
                        throw new RemoteException("Method unregisterCallback is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.health.IHealth
            public void update() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHealth.DESCRIPTOR);
                    if (!this.mRemote.transact(3, obtain, obtain2, 0)) {
                        throw new RemoteException("Method update is unimplemented.");
                    }
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.health.IHealth
            public int getChargeCounterUah() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHealth.DESCRIPTOR);
                    if (!this.mRemote.transact(4, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getChargeCounterUah is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.health.IHealth
            public int getCurrentNowMicroamps() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHealth.DESCRIPTOR);
                    if (!this.mRemote.transact(5, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getCurrentNowMicroamps is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.health.IHealth
            public int getCurrentAverageMicroamps() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHealth.DESCRIPTOR);
                    if (!this.mRemote.transact(6, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getCurrentAverageMicroamps is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.health.IHealth
            public int getCapacity() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHealth.DESCRIPTOR);
                    if (!this.mRemote.transact(7, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getCapacity is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.health.IHealth
            public long getEnergyCounterNwh() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHealth.DESCRIPTOR);
                    if (!this.mRemote.transact(8, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getEnergyCounterNwh is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.health.IHealth
            public int getChargeStatus() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHealth.DESCRIPTOR);
                    if (!this.mRemote.transact(9, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getChargeStatus is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.health.IHealth
            public HealthInfo getHealthInfo() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHealth.DESCRIPTOR);
                    if (!this.mRemote.transact(12, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getHealthInfo is unimplemented.");
                    }
                    obtain2.readException();
                    return (HealthInfo) obtain2.readTypedObject(HealthInfo.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.health.IHealth
            public int getChargingPolicy() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHealth.DESCRIPTOR);
                    if (!this.mRemote.transact(14, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getChargingPolicy is unimplemented.");
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // android.hardware.health.IHealth
            public BatteryHealthData getBatteryHealthData() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IHealth.DESCRIPTOR);
                    if (!this.mRemote.transact(15, obtain, obtain2, 0)) {
                        throw new RemoteException("Method getBatteryHealthData is unimplemented.");
                    }
                    obtain2.readException();
                    return (BatteryHealthData) obtain2.readTypedObject(BatteryHealthData.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
