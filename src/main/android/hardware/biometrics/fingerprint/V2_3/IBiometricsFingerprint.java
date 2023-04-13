package android.hardware.biometrics.fingerprint.V2_3;

import android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprintClientCallback;
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
public interface IBiometricsFingerprint extends android.hardware.biometrics.fingerprint.V2_2.IBiometricsFingerprint {
    @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint, android.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    boolean isUdfps(int i) throws RemoteException;

    void onFingerDown(int i, int i2, float f, float f2) throws RemoteException;

    void onFingerUp() throws RemoteException;

    static IBiometricsFingerprint asInterface(IHwBinder iHwBinder) {
        if (iHwBinder == null) {
            return null;
        }
        IHwInterface queryLocalInterface = iHwBinder.queryLocalInterface("android.hardware.biometrics.fingerprint@2.3::IBiometricsFingerprint");
        if (queryLocalInterface != null && (queryLocalInterface instanceof IBiometricsFingerprint)) {
            return (IBiometricsFingerprint) queryLocalInterface;
        }
        Proxy proxy = new Proxy(iHwBinder);
        try {
            Iterator<String> it = proxy.interfaceChain().iterator();
            while (it.hasNext()) {
                if (it.next().equals("android.hardware.biometrics.fingerprint@2.3::IBiometricsFingerprint")) {
                    return proxy;
                }
            }
        } catch (RemoteException unused) {
        }
        return null;
    }

    static IBiometricsFingerprint castFrom(IHwInterface iHwInterface) {
        if (iHwInterface == null) {
            return null;
        }
        return asInterface(iHwInterface.asBinder());
    }

    /* loaded from: classes.dex */
    public static final class Proxy implements IBiometricsFingerprint {
        public IHwBinder mRemote;

        public Proxy(IHwBinder iHwBinder) {
            Objects.requireNonNull(iHwBinder);
            this.mRemote = iHwBinder;
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint, android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException unused) {
                return "[class or subclass of android.hardware.biometrics.fingerprint@2.3::IBiometricsFingerprint]@Proxy";
            }
        }

        public final boolean equals(Object obj) {
            return HidlSupport.interfacesEqual(this, obj);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
        public long setNotify(IBiometricsFingerprintClientCallback iBiometricsFingerprintClientCallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
            hwParcel.writeStrongBinder(iBiometricsFingerprintClientCallback == null ? null : iBiometricsFingerprintClientCallback.asBinder());
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(1, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt64();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
        public long preEnroll() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(2, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt64();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
        public int enroll(byte[] bArr, int i, int i2) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
            HwBlob hwBlob = new HwBlob(69);
            if (bArr == null || bArr.length != 69) {
                throw new IllegalArgumentException("Array element is not of the expected length");
            }
            hwBlob.putInt8Array(0L, bArr);
            hwParcel.writeBuffer(hwBlob);
            hwParcel.writeInt32(i);
            hwParcel.writeInt32(i2);
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

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
        public int postEnroll() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
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

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
        public long getAuthenticatorId() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(5, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt64();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
        public int cancel() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(6, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt32();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
        public int enumerate() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(7, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt32();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
        public int remove(int i, int i2) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
            hwParcel.writeInt32(i);
            hwParcel.writeInt32(i2);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(8, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt32();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
        public int setActiveGroup(int i, String str) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
            hwParcel.writeInt32(i);
            hwParcel.writeString(str);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(9, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt32();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint
        public int authenticate(long j, int i) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
            hwParcel.writeInt64(j);
            hwParcel.writeInt32(i);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(10, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt32();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.fingerprint.V2_3.IBiometricsFingerprint
        public void onFingerDown(int i, int i2, float f, float f2) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.fingerprint@2.3::IBiometricsFingerprint");
            hwParcel.writeInt32(i);
            hwParcel.writeInt32(i2);
            hwParcel.writeFloat(f);
            hwParcel.writeFloat(f2);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(12, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.fingerprint.V2_3.IBiometricsFingerprint
        public void onFingerUp() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.fingerprint@2.3::IBiometricsFingerprint");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(13, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.fingerprint.V2_3.IBiometricsFingerprint, android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint, android.hidl.base.V1_0.IBase
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
    public static abstract class Stub extends HwBinder implements IBiometricsFingerprint {
        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint, android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hidl.base.V1_0.IBase
        public void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) {
        }

        @Override // android.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return "android.hardware.biometrics.fingerprint@2.3::IBiometricsFingerprint";
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

        @Override // android.hardware.biometrics.fingerprint.V2_3.IBiometricsFingerprint, android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint, android.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList("android.hardware.biometrics.fingerprint@2.3::IBiometricsFingerprint", "android.hardware.biometrics.fingerprint@2.2::IBiometricsFingerprint", "android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint", IBase.kInterfaceName));
        }

        @Override // android.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{122, 120, -23, -106, 59, -20, 11, 7, 30, 125, 70, -110, -116, 97, 0, -30, 23, 66, 112, -119, 45, 63, 21, -95, -22, -83, 7, 73, -105, -83, -14, 121}, new byte[]{20, 15, -113, 98, 16, 12, -49, -100, -46, -126, -82, 54, -123, -96, -12, -17, 10, -97, -105, 29, 119, -33, -68, 115, 80, -52, -76, -32, 76, -14, -107, -20}, new byte[]{31, -67, -63, -8, 82, -8, -67, 46, 74, 108, 92, -77, 10, -62, -73, -122, 104, -55, -115, -50, 17, -118, 97, 118, 45, 64, 52, -82, -123, -97, 67, -40}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
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
            if ("android.hardware.biometrics.fingerprint@2.3::IBiometricsFingerprint".equals(str)) {
                return this;
            }
            return null;
        }

        public String toString() {
            return interfaceDescriptor() + "@Stub";
        }

        public void onTransact(int i, HwParcel hwParcel, HwParcel hwParcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    hwParcel.enforceInterface("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
                    long notify = setNotify(IBiometricsFingerprintClientCallback.asInterface(hwParcel.readStrongBinder()));
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt64(notify);
                    hwParcel2.send();
                    return;
                case 2:
                    hwParcel.enforceInterface("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
                    long preEnroll = preEnroll();
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt64(preEnroll);
                    hwParcel2.send();
                    return;
                case 3:
                    hwParcel.enforceInterface("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
                    byte[] bArr = new byte[69];
                    hwParcel.readBuffer(69L).copyToInt8Array(0L, bArr, 69);
                    int enroll = enroll(bArr, hwParcel.readInt32(), hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(enroll);
                    hwParcel2.send();
                    return;
                case 4:
                    hwParcel.enforceInterface("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
                    int postEnroll = postEnroll();
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(postEnroll);
                    hwParcel2.send();
                    return;
                case 5:
                    hwParcel.enforceInterface("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
                    long authenticatorId = getAuthenticatorId();
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt64(authenticatorId);
                    hwParcel2.send();
                    return;
                case 6:
                    hwParcel.enforceInterface("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
                    int cancel = cancel();
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(cancel);
                    hwParcel2.send();
                    return;
                case 7:
                    hwParcel.enforceInterface("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
                    int enumerate = enumerate();
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(enumerate);
                    hwParcel2.send();
                    return;
                case 8:
                    hwParcel.enforceInterface("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
                    int remove = remove(hwParcel.readInt32(), hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(remove);
                    hwParcel2.send();
                    return;
                case 9:
                    hwParcel.enforceInterface("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
                    int activeGroup = setActiveGroup(hwParcel.readInt32(), hwParcel.readString());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(activeGroup);
                    hwParcel2.send();
                    return;
                case 10:
                    hwParcel.enforceInterface("android.hardware.biometrics.fingerprint@2.1::IBiometricsFingerprint");
                    int authenticate = authenticate(hwParcel.readInt64(), hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(authenticate);
                    hwParcel2.send();
                    return;
                case 11:
                    hwParcel.enforceInterface("android.hardware.biometrics.fingerprint@2.3::IBiometricsFingerprint");
                    boolean isUdfps = isUdfps(hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeBool(isUdfps);
                    hwParcel2.send();
                    return;
                case 12:
                    hwParcel.enforceInterface("android.hardware.biometrics.fingerprint@2.3::IBiometricsFingerprint");
                    onFingerDown(hwParcel.readInt32(), hwParcel.readInt32(), hwParcel.readFloat(), hwParcel.readFloat());
                    hwParcel2.writeStatus(0);
                    hwParcel2.send();
                    return;
                case 13:
                    hwParcel.enforceInterface("android.hardware.biometrics.fingerprint@2.3::IBiometricsFingerprint");
                    onFingerUp();
                    hwParcel2.writeStatus(0);
                    hwParcel2.send();
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
                                byte[] bArr2 = hashChain.get(i3);
                                if (bArr2 == null || bArr2.length != 32) {
                                    throw new IllegalArgumentException("Array element is not of the expected length");
                                }
                                hwBlob2.putInt8Array(j, bArr2);
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
