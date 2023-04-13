package android.hardware.biometrics.face.V1_0;

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
public interface IBiometricsFace extends IBase {
    @Override // android.hidl.base.V1_0.IBase
    IHwBinder asBinder();

    int authenticate(long j) throws RemoteException;

    int cancel() throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    int enroll(ArrayList<Byte> arrayList, int i, ArrayList<Integer> arrayList2) throws RemoteException;

    int enumerate() throws RemoteException;

    OptionalUint64 generateChallenge(int i) throws RemoteException;

    OptionalUint64 getAuthenticatorId() throws RemoteException;

    OptionalBool getFeature(int i, int i2) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    int remove(int i) throws RemoteException;

    int resetLockout(ArrayList<Byte> arrayList) throws RemoteException;

    int revokeChallenge() throws RemoteException;

    int setActiveUser(int i, String str) throws RemoteException;

    OptionalUint64 setCallback(IBiometricsFaceClientCallback iBiometricsFaceClientCallback) throws RemoteException;

    int setFeature(int i, boolean z, ArrayList<Byte> arrayList, int i2) throws RemoteException;

    int userActivity() throws RemoteException;

    static IBiometricsFace asInterface(IHwBinder iHwBinder) {
        if (iHwBinder == null) {
            return null;
        }
        IHwInterface queryLocalInterface = iHwBinder.queryLocalInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
        if (queryLocalInterface != null && (queryLocalInterface instanceof IBiometricsFace)) {
            return (IBiometricsFace) queryLocalInterface;
        }
        Proxy proxy = new Proxy(iHwBinder);
        try {
            Iterator<String> it = proxy.interfaceChain().iterator();
            while (it.hasNext()) {
                if (it.next().equals("android.hardware.biometrics.face@1.0::IBiometricsFace")) {
                    return proxy;
                }
            }
        } catch (RemoteException unused) {
        }
        return null;
    }

    @Deprecated
    static IBiometricsFace getService(String str) throws RemoteException {
        return asInterface(HwBinder.getService("android.hardware.biometrics.face@1.0::IBiometricsFace", str));
    }

    @Deprecated
    static IBiometricsFace getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes.dex */
    public static final class Proxy implements IBiometricsFace {
        public IHwBinder mRemote;

        public Proxy(IHwBinder iHwBinder) {
            Objects.requireNonNull(iHwBinder);
            this.mRemote = iHwBinder;
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace, android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException unused) {
                return "[class or subclass of android.hardware.biometrics.face@1.0::IBiometricsFace]@Proxy";
            }
        }

        public final boolean equals(Object obj) {
            return HidlSupport.interfacesEqual(this, obj);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace
        public OptionalUint64 setCallback(IBiometricsFaceClientCallback iBiometricsFaceClientCallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.face@1.0::IBiometricsFace");
            hwParcel.writeStrongBinder(iBiometricsFaceClientCallback == null ? null : iBiometricsFaceClientCallback.asBinder());
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(1, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                OptionalUint64 optionalUint64 = new OptionalUint64();
                optionalUint64.readFromParcel(hwParcel2);
                return optionalUint64;
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace
        public int setActiveUser(int i, String str) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.face@1.0::IBiometricsFace");
            hwParcel.writeInt32(i);
            hwParcel.writeString(str);
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

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace
        public OptionalUint64 generateChallenge(int i) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.face@1.0::IBiometricsFace");
            hwParcel.writeInt32(i);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(3, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                OptionalUint64 optionalUint64 = new OptionalUint64();
                optionalUint64.readFromParcel(hwParcel2);
                return optionalUint64;
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace
        public int enroll(ArrayList<Byte> arrayList, int i, ArrayList<Integer> arrayList2) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.face@1.0::IBiometricsFace");
            hwParcel.writeInt8Vector(arrayList);
            hwParcel.writeInt32(i);
            hwParcel.writeInt32Vector(arrayList2);
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

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace
        public int revokeChallenge() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.face@1.0::IBiometricsFace");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(5, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt32();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace
        public int setFeature(int i, boolean z, ArrayList<Byte> arrayList, int i2) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.face@1.0::IBiometricsFace");
            hwParcel.writeInt32(i);
            hwParcel.writeBool(z);
            hwParcel.writeInt8Vector(arrayList);
            hwParcel.writeInt32(i2);
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

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace
        public OptionalBool getFeature(int i, int i2) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.face@1.0::IBiometricsFace");
            hwParcel.writeInt32(i);
            hwParcel.writeInt32(i2);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(7, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                OptionalBool optionalBool = new OptionalBool();
                optionalBool.readFromParcel(hwParcel2);
                return optionalBool;
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace
        public OptionalUint64 getAuthenticatorId() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.face@1.0::IBiometricsFace");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(8, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                OptionalUint64 optionalUint64 = new OptionalUint64();
                optionalUint64.readFromParcel(hwParcel2);
                return optionalUint64;
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace
        public int cancel() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.face@1.0::IBiometricsFace");
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

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace
        public int enumerate() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.face@1.0::IBiometricsFace");
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

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace
        public int remove(int i) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.face@1.0::IBiometricsFace");
            hwParcel.writeInt32(i);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(11, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt32();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace
        public int authenticate(long j) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.face@1.0::IBiometricsFace");
            hwParcel.writeInt64(j);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(12, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt32();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace
        public int resetLockout(ArrayList<Byte> arrayList) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.biometrics.face@1.0::IBiometricsFace");
            hwParcel.writeInt8Vector(arrayList);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(14, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt32();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace, android.hidl.base.V1_0.IBase
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

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace, android.hidl.base.V1_0.IBase
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
    public static abstract class Stub extends HwBinder implements IBiometricsFace {
        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace, android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace, android.hidl.base.V1_0.IBase
        public void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) {
        }

        @Override // android.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return "android.hardware.biometrics.face@1.0::IBiometricsFace";
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

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFace, android.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList("android.hardware.biometrics.face@1.0::IBiometricsFace", IBase.kInterfaceName));
        }

        @Override // android.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{-31, -113, -13, 24, -13, -4, 67, -37, 55, -11, 84, 105, 109, -60, -27, 81, -85, -71, -79, 25, -67, -27, 57, 80, -9, 62, 40, -50, 51, -87, 122, 64}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
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
            if ("android.hardware.biometrics.face@1.0::IBiometricsFace".equals(str)) {
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
                    hwParcel.enforceInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
                    OptionalUint64 callback = setCallback(IBiometricsFaceClientCallback.asInterface(hwParcel.readStrongBinder()));
                    hwParcel2.writeStatus(0);
                    callback.writeToParcel(hwParcel2);
                    hwParcel2.send();
                    return;
                case 2:
                    hwParcel.enforceInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
                    int activeUser = setActiveUser(hwParcel.readInt32(), hwParcel.readString());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(activeUser);
                    hwParcel2.send();
                    return;
                case 3:
                    hwParcel.enforceInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
                    OptionalUint64 generateChallenge = generateChallenge(hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    generateChallenge.writeToParcel(hwParcel2);
                    hwParcel2.send();
                    return;
                case 4:
                    hwParcel.enforceInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
                    int enroll = enroll(hwParcel.readInt8Vector(), hwParcel.readInt32(), hwParcel.readInt32Vector());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(enroll);
                    hwParcel2.send();
                    return;
                case 5:
                    hwParcel.enforceInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
                    int revokeChallenge = revokeChallenge();
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(revokeChallenge);
                    hwParcel2.send();
                    return;
                case 6:
                    hwParcel.enforceInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
                    int feature = setFeature(hwParcel.readInt32(), hwParcel.readBool(), hwParcel.readInt8Vector(), hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(feature);
                    hwParcel2.send();
                    return;
                case 7:
                    hwParcel.enforceInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
                    OptionalBool feature2 = getFeature(hwParcel.readInt32(), hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    feature2.writeToParcel(hwParcel2);
                    hwParcel2.send();
                    return;
                case 8:
                    hwParcel.enforceInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
                    OptionalUint64 authenticatorId = getAuthenticatorId();
                    hwParcel2.writeStatus(0);
                    authenticatorId.writeToParcel(hwParcel2);
                    hwParcel2.send();
                    return;
                case 9:
                    hwParcel.enforceInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
                    int cancel = cancel();
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(cancel);
                    hwParcel2.send();
                    return;
                case 10:
                    hwParcel.enforceInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
                    int enumerate = enumerate();
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(enumerate);
                    hwParcel2.send();
                    return;
                case 11:
                    hwParcel.enforceInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
                    int remove = remove(hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(remove);
                    hwParcel2.send();
                    return;
                case 12:
                    hwParcel.enforceInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
                    int authenticate = authenticate(hwParcel.readInt64());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(authenticate);
                    hwParcel2.send();
                    return;
                case 13:
                    hwParcel.enforceInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
                    int userActivity = userActivity();
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(userActivity);
                    hwParcel2.send();
                    return;
                case 14:
                    hwParcel.enforceInterface("android.hardware.biometrics.face@1.0::IBiometricsFace");
                    int resetLockout = resetLockout(hwParcel.readInt8Vector());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(resetLockout);
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
