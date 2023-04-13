package android.hardware.health.V2_1;

import android.hardware.health.V2_0.IHealth;
import android.hidl.base.V1_0.DebugInfo;
import android.hidl.base.V1_0.IBase;
import android.os.HidlSupport;
import android.os.HwBinder;
import android.os.HwBlob;
import android.os.HwParcel;
import android.os.IHwBinder;
import android.os.IHwInterface;
import android.os.NativeHandle;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes.dex */
public interface IHealth extends android.hardware.health.V2_0.IHealth {

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface getHealthConfigCallback {
    }

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface getHealthInfo_2_1Callback {
    }

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface shouldKeepScreenOnCallback {
    }

    void getHealthConfig(getHealthConfigCallback gethealthconfigcallback) throws RemoteException;

    void getHealthInfo_2_1(getHealthInfo_2_1Callback gethealthinfo_2_1callback) throws RemoteException;

    void shouldKeepScreenOn(shouldKeepScreenOnCallback shouldkeepscreenoncallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static final class Proxy implements IHealth {
        public IHwBinder mRemote;

        @Override // android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException unused) {
                return "[class or subclass of android.hardware.health@2.1::IHealth]@Proxy";
            }
        }

        public final boolean equals(Object obj) {
            return HidlSupport.interfacesEqual(this, obj);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.health.V2_0.IHealth
        public int registerCallback(android.hardware.health.V2_0.IHealthInfoCallback iHealthInfoCallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.health@2.0::IHealth");
            hwParcel.writeStrongBinder(iHealthInfoCallback == null ? null : iHealthInfoCallback.asBinder());
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(1, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt32();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.health.V2_0.IHealth
        public int unregisterCallback(android.hardware.health.V2_0.IHealthInfoCallback iHealthInfoCallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.health@2.0::IHealth");
            hwParcel.writeStrongBinder(iHealthInfoCallback == null ? null : iHealthInfoCallback.asBinder());
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(2, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt32();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.health.V2_0.IHealth
        public int update() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.health@2.0::IHealth");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(3, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt32();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.health.V2_0.IHealth
        public void getChargeCounter(IHealth.getChargeCounterCallback getchargecountercallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.health@2.0::IHealth");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(4, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                getchargecountercallback.onValues(hwParcel2.readInt32(), hwParcel2.readInt32());
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.health.V2_0.IHealth
        public void getCurrentNow(IHealth.getCurrentNowCallback getcurrentnowcallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.health@2.0::IHealth");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(5, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                getcurrentnowcallback.onValues(hwParcel2.readInt32(), hwParcel2.readInt32());
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.health.V2_0.IHealth
        public void getCurrentAverage(IHealth.getCurrentAverageCallback getcurrentaveragecallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.health@2.0::IHealth");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(6, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                getcurrentaveragecallback.onValues(hwParcel2.readInt32(), hwParcel2.readInt32());
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.health.V2_0.IHealth
        public void getCapacity(IHealth.getCapacityCallback getcapacitycallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.health@2.0::IHealth");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(7, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                getcapacitycallback.onValues(hwParcel2.readInt32(), hwParcel2.readInt32());
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.health.V2_0.IHealth
        public void getEnergyCounter(IHealth.getEnergyCounterCallback getenergycountercallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.health@2.0::IHealth");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(8, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                getenergycountercallback.onValues(hwParcel2.readInt32(), hwParcel2.readInt64());
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.health.V2_0.IHealth
        public void getChargeStatus(IHealth.getChargeStatusCallback getchargestatuscallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.health@2.0::IHealth");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(9, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                getchargestatuscallback.onValues(hwParcel2.readInt32(), hwParcel2.readInt32());
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.health.V2_0.IHealth
        public void getHealthInfo(IHealth.getHealthInfoCallback gethealthinfocallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.health@2.0::IHealth");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(12, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                int readInt32 = hwParcel2.readInt32();
                android.hardware.health.V2_0.HealthInfo healthInfo = new android.hardware.health.V2_0.HealthInfo();
                healthInfo.readFromParcel(hwParcel2);
                gethealthinfocallback.onValues(readInt32, healthInfo);
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.health.V2_0.IHealth, android.hidl.base.V1_0.IBase
        public ArrayList<String> interfaceChain() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(256067662, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readStringVector();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.base.V1_0.IBase
        public void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(IBase.kInterfaceName);
            hwParcel.writeNativeHandle(nativeHandle);
            hwParcel.writeStringVector(arrayList);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(256131655, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.base.V1_0.IBase
        public String interfaceDescriptor() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(256136003, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readString();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.base.V1_0.IBase
        public ArrayList<byte[]> getHashChain() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(256398152, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                ArrayList<byte[]> arrayList = new ArrayList<>();
                HwBlob readBuffer = hwParcel2.readBuffer(16L);
                int int32 = readBuffer.getInt32(8L);
                HwBlob readEmbeddedBuffer = hwParcel2.readEmbeddedBuffer(int32 * 32, readBuffer.handle(), 0L, true);
                arrayList.clear();
                for (int i = 0; i < int32; i++) {
                    byte[] bArr = new byte[32];
                    readEmbeddedBuffer.copyToInt8Array(i * 32, bArr, 32);
                    arrayList.add(bArr);
                }
                return arrayList;
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.base.V1_0.IBase
        public void setHALInstrumentation() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(256462420, hwParcel, hwParcel2, 1);
                hwParcel.releaseTemporaryStorage();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException {
            return this.mRemote.linkToDeath(deathRecipient, j);
        }

        @Override // android.hidl.base.V1_0.IBase
        public void ping() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(256921159, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.base.V1_0.IBase
        public DebugInfo getDebugInfo() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(257049926, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                DebugInfo debugInfo = new DebugInfo();
                debugInfo.readFromParcel(hwParcel2);
                return debugInfo;
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.base.V1_0.IBase
        public void notifySyspropsChanged() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(257120595, hwParcel, hwParcel2, 1);
                hwParcel.releaseTemporaryStorage();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(deathRecipient);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends HwBinder implements IHealth {
        @Override // android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hidl.base.V1_0.IBase
        public void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) {
        }

        @Override // android.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return "android.hardware.health@2.1::IHealth";
        }

        @Override // android.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) {
            return true;
        }

        @Override // android.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) {
            return true;
        }

        @Override // android.hardware.health.V2_0.IHealth, android.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList("android.hardware.health@2.1::IHealth", "android.hardware.health@2.0::IHealth", IBase.kInterfaceName));
        }

        @Override // android.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{-50, -115, -66, 118, -21, -98, -23, 75, 70, -17, -104, -9, 37, -66, -103, 46, 118, 10, 87, 81, 7, 61, 79, 73, 18, 72, 64, 38, 84, 19, 113, -13}, new byte[]{103, 86, -126, -35, 48, 7, Byte.MIN_VALUE, 92, -104, 94, -86, -20, -111, 97, 42, -68, -120, -12, -62, 91, 52, 49, -5, -124, 7, 11, 117, -124, -95, -89, 65, -5}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo debugInfo = new DebugInfo();
            debugInfo.pid = HidlSupport.getPidIfSharable();
            debugInfo.ptr = 0L;
            debugInfo.arch = 0;
            return debugInfo;
        }

        @Override // android.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        public IHwInterface queryLocalInterface(String str) {
            if ("android.hardware.health@2.1::IHealth".equals(str)) {
                return this;
            }
            return null;
        }

        public String toString() {
            return interfaceDescriptor() + "@Stub";
        }

        public void onTransact(int i, HwParcel hwParcel, final HwParcel hwParcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    hwParcel.enforceInterface("android.hardware.health@2.0::IHealth");
                    int registerCallback = registerCallback(android.hardware.health.V2_0.IHealthInfoCallback.asInterface(hwParcel.readStrongBinder()));
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(registerCallback);
                    hwParcel2.send();
                    return;
                case 2:
                    hwParcel.enforceInterface("android.hardware.health@2.0::IHealth");
                    int unregisterCallback = unregisterCallback(android.hardware.health.V2_0.IHealthInfoCallback.asInterface(hwParcel.readStrongBinder()));
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(unregisterCallback);
                    hwParcel2.send();
                    return;
                case 3:
                    hwParcel.enforceInterface("android.hardware.health@2.0::IHealth");
                    int update = update();
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(update);
                    hwParcel2.send();
                    return;
                case 4:
                    hwParcel.enforceInterface("android.hardware.health@2.0::IHealth");
                    getChargeCounter(new IHealth.getChargeCounterCallback() { // from class: android.hardware.health.V2_1.IHealth.Stub.1
                        @Override // android.hardware.health.V2_0.IHealth.getChargeCounterCallback
                        public void onValues(int i3, int i4) {
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeInt32(i3);
                            hwParcel2.writeInt32(i4);
                            hwParcel2.send();
                        }
                    });
                    return;
                case 5:
                    hwParcel.enforceInterface("android.hardware.health@2.0::IHealth");
                    getCurrentNow(new IHealth.getCurrentNowCallback() { // from class: android.hardware.health.V2_1.IHealth.Stub.2
                        @Override // android.hardware.health.V2_0.IHealth.getCurrentNowCallback
                        public void onValues(int i3, int i4) {
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeInt32(i3);
                            hwParcel2.writeInt32(i4);
                            hwParcel2.send();
                        }
                    });
                    return;
                case 6:
                    hwParcel.enforceInterface("android.hardware.health@2.0::IHealth");
                    getCurrentAverage(new IHealth.getCurrentAverageCallback() { // from class: android.hardware.health.V2_1.IHealth.Stub.3
                        @Override // android.hardware.health.V2_0.IHealth.getCurrentAverageCallback
                        public void onValues(int i3, int i4) {
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeInt32(i3);
                            hwParcel2.writeInt32(i4);
                            hwParcel2.send();
                        }
                    });
                    return;
                case 7:
                    hwParcel.enforceInterface("android.hardware.health@2.0::IHealth");
                    getCapacity(new IHealth.getCapacityCallback() { // from class: android.hardware.health.V2_1.IHealth.Stub.4
                        @Override // android.hardware.health.V2_0.IHealth.getCapacityCallback
                        public void onValues(int i3, int i4) {
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeInt32(i3);
                            hwParcel2.writeInt32(i4);
                            hwParcel2.send();
                        }
                    });
                    return;
                case 8:
                    hwParcel.enforceInterface("android.hardware.health@2.0::IHealth");
                    getEnergyCounter(new IHealth.getEnergyCounterCallback() { // from class: android.hardware.health.V2_1.IHealth.Stub.5
                        @Override // android.hardware.health.V2_0.IHealth.getEnergyCounterCallback
                        public void onValues(int i3, long j) {
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeInt32(i3);
                            hwParcel2.writeInt64(j);
                            hwParcel2.send();
                        }
                    });
                    return;
                case 9:
                    hwParcel.enforceInterface("android.hardware.health@2.0::IHealth");
                    getChargeStatus(new IHealth.getChargeStatusCallback() { // from class: android.hardware.health.V2_1.IHealth.Stub.6
                        @Override // android.hardware.health.V2_0.IHealth.getChargeStatusCallback
                        public void onValues(int i3, int i4) {
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeInt32(i3);
                            hwParcel2.writeInt32(i4);
                            hwParcel2.send();
                        }
                    });
                    return;
                case 10:
                    hwParcel.enforceInterface("android.hardware.health@2.0::IHealth");
                    getStorageInfo(new IHealth.getStorageInfoCallback() { // from class: android.hardware.health.V2_1.IHealth.Stub.7
                    });
                    return;
                case 11:
                    hwParcel.enforceInterface("android.hardware.health@2.0::IHealth");
                    getDiskStats(new IHealth.getDiskStatsCallback() { // from class: android.hardware.health.V2_1.IHealth.Stub.8
                    });
                    return;
                case 12:
                    hwParcel.enforceInterface("android.hardware.health@2.0::IHealth");
                    getHealthInfo(new IHealth.getHealthInfoCallback() { // from class: android.hardware.health.V2_1.IHealth.Stub.9
                        @Override // android.hardware.health.V2_0.IHealth.getHealthInfoCallback
                        public void onValues(int i3, android.hardware.health.V2_0.HealthInfo healthInfo) {
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeInt32(i3);
                            healthInfo.writeToParcel(hwParcel2);
                            hwParcel2.send();
                        }
                    });
                    return;
                case 13:
                    hwParcel.enforceInterface("android.hardware.health@2.1::IHealth");
                    getHealthConfig(new getHealthConfigCallback() { // from class: android.hardware.health.V2_1.IHealth.Stub.10
                    });
                    return;
                case 14:
                    hwParcel.enforceInterface("android.hardware.health@2.1::IHealth");
                    getHealthInfo_2_1(new getHealthInfo_2_1Callback() { // from class: android.hardware.health.V2_1.IHealth.Stub.11
                    });
                    return;
                case 15:
                    hwParcel.enforceInterface("android.hardware.health@2.1::IHealth");
                    shouldKeepScreenOn(new shouldKeepScreenOnCallback() { // from class: android.hardware.health.V2_1.IHealth.Stub.12
                    });
                    return;
                default:
                    switch (i) {
                        case 256067662:
                            hwParcel.enforceInterface(IBase.kInterfaceName);
                            ArrayList<String> interfaceChain = interfaceChain();
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeStringVector(interfaceChain);
                            hwParcel2.send();
                            return;
                        case 256131655:
                            hwParcel.enforceInterface(IBase.kInterfaceName);
                            debug(hwParcel.readNativeHandle(), hwParcel.readStringVector());
                            hwParcel2.writeStatus(0);
                            hwParcel2.send();
                            return;
                        case 256136003:
                            hwParcel.enforceInterface(IBase.kInterfaceName);
                            String interfaceDescriptor = interfaceDescriptor();
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeString(interfaceDescriptor);
                            hwParcel2.send();
                            return;
                        case 256398152:
                            hwParcel.enforceInterface(IBase.kInterfaceName);
                            ArrayList<byte[]> hashChain = getHashChain();
                            hwParcel2.writeStatus(0);
                            HwBlob hwBlob = new HwBlob(16);
                            int size = hashChain.size();
                            hwBlob.putInt32(8L, size);
                            hwBlob.putBool(12L, false);
                            HwBlob hwBlob2 = new HwBlob(size * 32);
                            for (int i3 = 0; i3 < size; i3++) {
                                long j = i3 * 32;
                                byte[] bArr = hashChain.get(i3);
                                if (bArr == null || bArr.length != 32) {
                                    throw new IllegalArgumentException("Array element is not of the expected length");
                                }
                                hwBlob2.putInt8Array(j, bArr);
                            }
                            hwBlob.putBlob(0L, hwBlob2);
                            hwParcel2.writeBuffer(hwBlob);
                            hwParcel2.send();
                            return;
                        case 256462420:
                            hwParcel.enforceInterface(IBase.kInterfaceName);
                            setHALInstrumentation();
                            return;
                        case 256921159:
                            hwParcel.enforceInterface(IBase.kInterfaceName);
                            ping();
                            hwParcel2.writeStatus(0);
                            hwParcel2.send();
                            return;
                        case 257049926:
                            hwParcel.enforceInterface(IBase.kInterfaceName);
                            DebugInfo debugInfo = getDebugInfo();
                            hwParcel2.writeStatus(0);
                            debugInfo.writeToParcel(hwParcel2);
                            hwParcel2.send();
                            return;
                        case 257120595:
                            hwParcel.enforceInterface(IBase.kInterfaceName);
                            notifySyspropsChanged();
                            return;
                        default:
                            return;
                    }
            }
        }
    }
}
