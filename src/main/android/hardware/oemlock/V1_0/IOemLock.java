package android.hardware.oemlock.V1_0;

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
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes.dex */
public interface IOemLock extends IBase {

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface getNameCallback {
        void onValues(int i, String str);
    }

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface isOemUnlockAllowedByCarrierCallback {
        void onValues(int i, boolean z);
    }

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface isOemUnlockAllowedByDeviceCallback {
        void onValues(int i, boolean z);
    }

    void getName(getNameCallback getnamecallback) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    void isOemUnlockAllowedByCarrier(isOemUnlockAllowedByCarrierCallback isoemunlockallowedbycarriercallback) throws RemoteException;

    void isOemUnlockAllowedByDevice(isOemUnlockAllowedByDeviceCallback isoemunlockallowedbydevicecallback) throws RemoteException;

    int setOemUnlockAllowedByCarrier(boolean z, ArrayList<Byte> arrayList) throws RemoteException;

    int setOemUnlockAllowedByDevice(boolean z) throws RemoteException;

    static IOemLock asInterface(IHwBinder iHwBinder) {
        if (iHwBinder == null) {
            return null;
        }
        IHwInterface queryLocalInterface = iHwBinder.queryLocalInterface("android.hardware.oemlock@1.0::IOemLock");
        if (queryLocalInterface != null && (queryLocalInterface instanceof IOemLock)) {
            return (IOemLock) queryLocalInterface;
        }
        Proxy proxy = new Proxy(iHwBinder);
        try {
            Iterator<String> it = proxy.interfaceChain().iterator();
            while (it.hasNext()) {
                if (it.next().equals("android.hardware.oemlock@1.0::IOemLock")) {
                    return proxy;
                }
            }
        } catch (RemoteException unused) {
        }
        return null;
    }

    static IOemLock getService(String str, boolean z) throws RemoteException {
        return asInterface(HwBinder.getService("android.hardware.oemlock@1.0::IOemLock", str, z));
    }

    static IOemLock getService(boolean z) throws RemoteException {
        return getService("default", z);
    }

    /* loaded from: classes.dex */
    public static final class Proxy implements IOemLock {
        public IHwBinder mRemote;

        public Proxy(IHwBinder iHwBinder) {
            Objects.requireNonNull(iHwBinder);
            this.mRemote = iHwBinder;
        }

        @Override // android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException unused) {
                return "[class or subclass of android.hardware.oemlock@1.0::IOemLock]@Proxy";
            }
        }

        public final boolean equals(Object obj) {
            return HidlSupport.interfacesEqual(this, obj);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.oemlock.V1_0.IOemLock
        public void getName(getNameCallback getnamecallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.oemlock@1.0::IOemLock");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(1, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                getnamecallback.onValues(hwParcel2.readInt32(), hwParcel2.readString());
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.oemlock.V1_0.IOemLock
        public int setOemUnlockAllowedByCarrier(boolean z, ArrayList<Byte> arrayList) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.oemlock@1.0::IOemLock");
            hwParcel.writeBool(z);
            hwParcel.writeInt8Vector(arrayList);
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

        @Override // android.hardware.oemlock.V1_0.IOemLock
        public void isOemUnlockAllowedByCarrier(isOemUnlockAllowedByCarrierCallback isoemunlockallowedbycarriercallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.oemlock@1.0::IOemLock");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(3, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                isoemunlockallowedbycarriercallback.onValues(hwParcel2.readInt32(), hwParcel2.readBool());
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.oemlock.V1_0.IOemLock
        public int setOemUnlockAllowedByDevice(boolean z) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.oemlock@1.0::IOemLock");
            hwParcel.writeBool(z);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(4, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt32();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.oemlock.V1_0.IOemLock
        public void isOemUnlockAllowedByDevice(isOemUnlockAllowedByDeviceCallback isoemunlockallowedbydevicecallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.oemlock@1.0::IOemLock");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(5, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                isoemunlockallowedbydevicecallback.onValues(hwParcel2.readInt32(), hwParcel2.readBool());
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.oemlock.V1_0.IOemLock, android.hidl.base.V1_0.IBase
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
    public static abstract class Stub extends HwBinder implements IOemLock {
        @Override // android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hidl.base.V1_0.IBase
        public void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) {
        }

        @Override // android.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return "android.hardware.oemlock@1.0::IOemLock";
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

        @Override // android.hardware.oemlock.V1_0.IOemLock, android.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList("android.hardware.oemlock@1.0::IOemLock", IBase.kInterfaceName));
        }

        @Override // android.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{75, 101, 118, 54, 99, -87, 74, 57, 32, 19, 64, 17, 105, 31, -113, -69, 66, -52, -73, -73, 121, 85, -119, -17, -35, -64, 73, -87, 16, 96, 71, -42}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
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
            if ("android.hardware.oemlock@1.0::IOemLock".equals(str)) {
                return this;
            }
            return null;
        }

        public String toString() {
            return interfaceDescriptor() + "@Stub";
        }

        public void onTransact(int i, HwParcel hwParcel, final HwParcel hwParcel2, int i2) throws RemoteException {
            if (i == 1) {
                hwParcel.enforceInterface("android.hardware.oemlock@1.0::IOemLock");
                getName(new getNameCallback() { // from class: android.hardware.oemlock.V1_0.IOemLock.Stub.1
                    @Override // android.hardware.oemlock.V1_0.IOemLock.getNameCallback
                    public void onValues(int i3, String str) {
                        hwParcel2.writeStatus(0);
                        hwParcel2.writeInt32(i3);
                        hwParcel2.writeString(str);
                        hwParcel2.send();
                    }
                });
                return;
            }
            if (i == 2) {
                hwParcel.enforceInterface("android.hardware.oemlock@1.0::IOemLock");
                int oemUnlockAllowedByCarrier = setOemUnlockAllowedByCarrier(hwParcel.readBool(), hwParcel.readInt8Vector());
                hwParcel2.writeStatus(0);
                hwParcel2.writeInt32(oemUnlockAllowedByCarrier);
                hwParcel2.send();
            } else if (i == 3) {
                hwParcel.enforceInterface("android.hardware.oemlock@1.0::IOemLock");
                isOemUnlockAllowedByCarrier(new isOemUnlockAllowedByCarrierCallback() { // from class: android.hardware.oemlock.V1_0.IOemLock.Stub.2
                    @Override // android.hardware.oemlock.V1_0.IOemLock.isOemUnlockAllowedByCarrierCallback
                    public void onValues(int i3, boolean z) {
                        hwParcel2.writeStatus(0);
                        hwParcel2.writeInt32(i3);
                        hwParcel2.writeBool(z);
                        hwParcel2.send();
                    }
                });
            } else if (i == 4) {
                hwParcel.enforceInterface("android.hardware.oemlock@1.0::IOemLock");
                int oemUnlockAllowedByDevice = setOemUnlockAllowedByDevice(hwParcel.readBool());
                hwParcel2.writeStatus(0);
                hwParcel2.writeInt32(oemUnlockAllowedByDevice);
                hwParcel2.send();
            } else if (i == 5) {
                hwParcel.enforceInterface("android.hardware.oemlock@1.0::IOemLock");
                isOemUnlockAllowedByDevice(new isOemUnlockAllowedByDeviceCallback() { // from class: android.hardware.oemlock.V1_0.IOemLock.Stub.3
                    @Override // android.hardware.oemlock.V1_0.IOemLock.isOemUnlockAllowedByDeviceCallback
                    public void onValues(int i3, boolean z) {
                        hwParcel2.writeStatus(0);
                        hwParcel2.writeInt32(i3);
                        hwParcel2.writeBool(z);
                        hwParcel2.send();
                    }
                });
            } else {
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