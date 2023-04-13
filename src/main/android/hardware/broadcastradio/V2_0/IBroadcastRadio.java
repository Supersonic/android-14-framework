package android.hardware.broadcastradio.V2_0;

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
public interface IBroadcastRadio extends IBase {

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface getAmFmRegionConfigCallback {
        void onValues(int i, AmFmRegionConfig amFmRegionConfig);
    }

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface getDabRegionConfigCallback {
        void onValues(int i, ArrayList<DabTableEntry> arrayList);
    }

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface openSessionCallback {
        void onValues(int i, ITunerSession iTunerSession);
    }

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface registerAnnouncementListenerCallback {
        void onValues(int i, ICloseHandle iCloseHandle);
    }

    void getAmFmRegionConfig(boolean z, getAmFmRegionConfigCallback getamfmregionconfigcallback) throws RemoteException;

    void getDabRegionConfig(getDabRegionConfigCallback getdabregionconfigcallback) throws RemoteException;

    ArrayList<Byte> getImage(int i) throws RemoteException;

    Properties getProperties() throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    void openSession(ITunerCallback iTunerCallback, openSessionCallback opensessioncallback) throws RemoteException;

    void registerAnnouncementListener(ArrayList<Byte> arrayList, IAnnouncementListener iAnnouncementListener, registerAnnouncementListenerCallback registerannouncementlistenercallback) throws RemoteException;

    static IBroadcastRadio asInterface(IHwBinder iHwBinder) {
        if (iHwBinder == null) {
            return null;
        }
        IHwInterface queryLocalInterface = iHwBinder.queryLocalInterface("android.hardware.broadcastradio@2.0::IBroadcastRadio");
        if (queryLocalInterface != null && (queryLocalInterface instanceof IBroadcastRadio)) {
            return (IBroadcastRadio) queryLocalInterface;
        }
        Proxy proxy = new Proxy(iHwBinder);
        try {
            Iterator<String> it = proxy.interfaceChain().iterator();
            while (it.hasNext()) {
                if (it.next().equals("android.hardware.broadcastradio@2.0::IBroadcastRadio")) {
                    return proxy;
                }
            }
        } catch (RemoteException unused) {
        }
        return null;
    }

    @Deprecated
    static IBroadcastRadio getService(String str) throws RemoteException {
        return asInterface(HwBinder.getService("android.hardware.broadcastradio@2.0::IBroadcastRadio", str));
    }

    /* loaded from: classes.dex */
    public static final class Proxy implements IBroadcastRadio {
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
                return "[class or subclass of android.hardware.broadcastradio@2.0::IBroadcastRadio]@Proxy";
            }
        }

        public final boolean equals(Object obj) {
            return HidlSupport.interfacesEqual(this, obj);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio
        public Properties getProperties() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.broadcastradio@2.0::IBroadcastRadio");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(1, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                Properties properties = new Properties();
                properties.readFromParcel(hwParcel2);
                return properties;
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio
        public void getAmFmRegionConfig(boolean z, getAmFmRegionConfigCallback getamfmregionconfigcallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.broadcastradio@2.0::IBroadcastRadio");
            hwParcel.writeBool(z);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(2, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                int readInt32 = hwParcel2.readInt32();
                AmFmRegionConfig amFmRegionConfig = new AmFmRegionConfig();
                amFmRegionConfig.readFromParcel(hwParcel2);
                getamfmregionconfigcallback.onValues(readInt32, amFmRegionConfig);
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio
        public void getDabRegionConfig(getDabRegionConfigCallback getdabregionconfigcallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.broadcastradio@2.0::IBroadcastRadio");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(3, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                getdabregionconfigcallback.onValues(hwParcel2.readInt32(), DabTableEntry.readVectorFromParcel(hwParcel2));
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio
        public void openSession(ITunerCallback iTunerCallback, openSessionCallback opensessioncallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.broadcastradio@2.0::IBroadcastRadio");
            hwParcel.writeStrongBinder(iTunerCallback == null ? null : iTunerCallback.asBinder());
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(4, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                opensessioncallback.onValues(hwParcel2.readInt32(), ITunerSession.asInterface(hwParcel2.readStrongBinder()));
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio
        public ArrayList<Byte> getImage(int i) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.broadcastradio@2.0::IBroadcastRadio");
            hwParcel.writeInt32(i);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(5, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt8Vector();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio
        public void registerAnnouncementListener(ArrayList<Byte> arrayList, IAnnouncementListener iAnnouncementListener, registerAnnouncementListenerCallback registerannouncementlistenercallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.broadcastradio@2.0::IBroadcastRadio");
            hwParcel.writeInt8Vector(arrayList);
            hwParcel.writeStrongBinder(iAnnouncementListener == null ? null : iAnnouncementListener.asBinder());
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(6, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                registerannouncementlistenercallback.onValues(hwParcel2.readInt32(), ICloseHandle.asInterface(hwParcel2.readStrongBinder()));
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio, android.hidl.base.V1_0.IBase
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

        @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio, android.hidl.base.V1_0.IBase
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
    public static abstract class Stub extends HwBinder implements IBroadcastRadio {
        @Override // android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hidl.base.V1_0.IBase
        public void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) {
        }

        @Override // android.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return "android.hardware.broadcastradio@2.0::IBroadcastRadio";
        }

        @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio, android.hidl.base.V1_0.IBase
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

        @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio, android.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList("android.hardware.broadcastradio@2.0::IBroadcastRadio", IBase.kInterfaceName));
        }

        @Override // android.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{68, 1, 124, 66, -26, -12, -40, -53, 48, -16, 126, -79, -38, 4, 84, 10, -104, 115, 106, 51, 106, -62, -116, 126, 14, -46, -26, -98, 21, -119, -8, -47}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
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
            if ("android.hardware.broadcastradio@2.0::IBroadcastRadio".equals(str)) {
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
                    hwParcel.enforceInterface("android.hardware.broadcastradio@2.0::IBroadcastRadio");
                    Properties properties = getProperties();
                    hwParcel2.writeStatus(0);
                    properties.writeToParcel(hwParcel2);
                    hwParcel2.send();
                    return;
                case 2:
                    hwParcel.enforceInterface("android.hardware.broadcastradio@2.0::IBroadcastRadio");
                    getAmFmRegionConfig(hwParcel.readBool(), new getAmFmRegionConfigCallback() { // from class: android.hardware.broadcastradio.V2_0.IBroadcastRadio.Stub.1
                        @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio.getAmFmRegionConfigCallback
                        public void onValues(int i3, AmFmRegionConfig amFmRegionConfig) {
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeInt32(i3);
                            amFmRegionConfig.writeToParcel(hwParcel2);
                            hwParcel2.send();
                        }
                    });
                    return;
                case 3:
                    hwParcel.enforceInterface("android.hardware.broadcastradio@2.0::IBroadcastRadio");
                    getDabRegionConfig(new getDabRegionConfigCallback() { // from class: android.hardware.broadcastradio.V2_0.IBroadcastRadio.Stub.2
                        @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio.getDabRegionConfigCallback
                        public void onValues(int i3, ArrayList<DabTableEntry> arrayList) {
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeInt32(i3);
                            DabTableEntry.writeVectorToParcel(hwParcel2, arrayList);
                            hwParcel2.send();
                        }
                    });
                    return;
                case 4:
                    hwParcel.enforceInterface("android.hardware.broadcastradio@2.0::IBroadcastRadio");
                    openSession(ITunerCallback.asInterface(hwParcel.readStrongBinder()), new openSessionCallback() { // from class: android.hardware.broadcastradio.V2_0.IBroadcastRadio.Stub.3
                        @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio.openSessionCallback
                        public void onValues(int i3, ITunerSession iTunerSession) {
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeInt32(i3);
                            hwParcel2.writeStrongBinder(iTunerSession == null ? null : iTunerSession.asBinder());
                            hwParcel2.send();
                        }
                    });
                    return;
                case 5:
                    hwParcel.enforceInterface("android.hardware.broadcastradio@2.0::IBroadcastRadio");
                    ArrayList<Byte> image = getImage(hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt8Vector(image);
                    hwParcel2.send();
                    return;
                case 6:
                    hwParcel.enforceInterface("android.hardware.broadcastradio@2.0::IBroadcastRadio");
                    registerAnnouncementListener(hwParcel.readInt8Vector(), IAnnouncementListener.asInterface(hwParcel.readStrongBinder()), new registerAnnouncementListenerCallback() { // from class: android.hardware.broadcastradio.V2_0.IBroadcastRadio.Stub.4
                        @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio.registerAnnouncementListenerCallback
                        public void onValues(int i3, ICloseHandle iCloseHandle) {
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeInt32(i3);
                            hwParcel2.writeStrongBinder(iCloseHandle == null ? null : iCloseHandle.asBinder());
                            hwParcel2.send();
                        }
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
