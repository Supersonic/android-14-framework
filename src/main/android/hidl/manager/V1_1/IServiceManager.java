package android.hidl.manager.V1_1;

import android.hidl.base.V1_0.DebugInfo;
import android.hidl.base.V1_0.IBase;
import android.hidl.manager.V1_0.IServiceManager;
import android.hidl.manager.V1_0.IServiceNotification;
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
public interface IServiceManager extends android.hidl.manager.V1_0.IServiceManager {
    public static final String kInterfaceName = "android.hidl.manager@1.1::IServiceManager";

    @Override // android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
    IHwBinder asBinder();

    @Override // android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    @Override // android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    @Override // android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    @Override // android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    @Override // android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    @Override // android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    @Override // android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    @Override // android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    boolean unregisterForNotifications(String str, String str2, IServiceNotification iServiceNotification) throws RemoteException;

    static IServiceManager asInterface(IHwBinder iHwBinder) {
        if (iHwBinder == null) {
            return null;
        }
        IHwInterface queryLocalInterface = iHwBinder.queryLocalInterface(kInterfaceName);
        if (queryLocalInterface != null && (queryLocalInterface instanceof IServiceManager)) {
            return (IServiceManager) queryLocalInterface;
        }
        Proxy proxy = new Proxy(iHwBinder);
        try {
            Iterator<String> it = proxy.interfaceChain().iterator();
            while (it.hasNext()) {
                if (it.next().equals(kInterfaceName)) {
                    return proxy;
                }
            }
        } catch (RemoteException unused) {
        }
        return null;
    }

    static IServiceManager castFrom(IHwInterface iHwInterface) {
        if (iHwInterface == null) {
            return null;
        }
        return asInterface(iHwInterface.asBinder());
    }

    static IServiceManager getService(String str, boolean z) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, str, z));
    }

    static IServiceManager getService(boolean z) throws RemoteException {
        return getService("default", z);
    }

    @Deprecated
    static IServiceManager getService(String str) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, str));
    }

    @Deprecated
    static IServiceManager getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes.dex */
    public static final class Proxy implements IServiceManager {
        private IHwBinder mRemote;

        public Proxy(IHwBinder iHwBinder) {
            Objects.requireNonNull(iHwBinder);
            this.mRemote = iHwBinder;
        }

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException unused) {
                return "[class or subclass of android.hidl.manager@1.1::IServiceManager]@Proxy";
            }
        }

        public final boolean equals(Object obj) {
            return HidlSupport.interfacesEqual(this, obj);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hidl.manager.V1_0.IServiceManager
        public IBase get(String str, String str2) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
            hwParcel.writeString(str);
            hwParcel.writeString(str2);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(1, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return IBase.asInterface(hwParcel2.readStrongBinder());
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.manager.V1_0.IServiceManager
        public boolean add(String str, IBase iBase) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
            hwParcel.writeString(str);
            hwParcel.writeStrongBinder(iBase == null ? null : iBase.asBinder());
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(2, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readBool();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.manager.V1_0.IServiceManager
        public byte getTransport(String str, String str2) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
            hwParcel.writeString(str);
            hwParcel.writeString(str2);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(3, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readInt8();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.manager.V1_0.IServiceManager
        public ArrayList<String> list() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(4, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readStringVector();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.manager.V1_0.IServiceManager
        public ArrayList<String> listByInterface(String str) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
            hwParcel.writeString(str);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(5, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readStringVector();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.manager.V1_0.IServiceManager
        public boolean registerForNotifications(String str, String str2, IServiceNotification iServiceNotification) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
            hwParcel.writeString(str);
            hwParcel.writeString(str2);
            hwParcel.writeStrongBinder(iServiceNotification == null ? null : iServiceNotification.asBinder());
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(6, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readBool();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.manager.V1_0.IServiceManager
        public ArrayList<IServiceManager.InstanceDebugInfo> debugDump() throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(7, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return IServiceManager.InstanceDebugInfo.readVectorFromParcel(hwParcel2);
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.manager.V1_0.IServiceManager
        public void registerPassthroughClient(String str, String str2) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
            hwParcel.writeString(str);
            hwParcel.writeString(str2);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(8, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.manager.V1_1.IServiceManager
        public boolean unregisterForNotifications(String str, String str2, IServiceNotification iServiceNotification) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken(IServiceManager.kInterfaceName);
            hwParcel.writeString(str);
            hwParcel.writeString(str2);
            hwParcel.writeStrongBinder(iServiceNotification == null ? null : iServiceNotification.asBinder());
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(9, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                return hwParcel2.readBool();
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
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

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
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

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
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

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
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

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
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

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException {
            return this.mRemote.linkToDeath(deathRecipient, j);
        }

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
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

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
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

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
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

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(deathRecipient);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends HwBinder implements IServiceManager {
        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
        public void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) {
        }

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IServiceManager.kInterfaceName;
        }

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) {
            return true;
        }

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) {
            return true;
        }

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IServiceManager.kInterfaceName, android.hidl.manager.V1_0.IServiceManager.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{11, -108, -36, -121, 111, 116, -98, -46, 74, -104, -10, 28, 65, -44, 106, -41, 90, 39, 81, 17, 99, -15, -106, -118, 8, 66, 19, -93, 60, 104, 78, -10}, new byte[]{-123, 57, 79, -118, 13, 21, -25, -5, 46, -28, 92, 82, -47, -5, -117, -113, -45, -63, 60, 51, 62, 99, -57, -116, 74, -95, -1, -122, -124, 12, -10, -36}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo debugInfo = new DebugInfo();
            debugInfo.pid = HidlSupport.getPidIfSharable();
            debugInfo.ptr = 0L;
            debugInfo.arch = 0;
            return debugInfo;
        }

        @Override // android.hidl.manager.V1_1.IServiceManager, android.hidl.manager.V1_0.IServiceManager, android.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        public IHwInterface queryLocalInterface(String str) {
            if (IServiceManager.kInterfaceName.equals(str)) {
                return this;
            }
            return null;
        }

        public void registerAsService(String str) throws RemoteException {
            registerService(str);
        }

        public String toString() {
            return interfaceDescriptor() + "@Stub";
        }

        public void onTransact(int i, HwParcel hwParcel, HwParcel hwParcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    hwParcel.enforceInterface(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
                    IBase iBase = get(hwParcel.readString(), hwParcel.readString());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeStrongBinder(iBase == null ? null : iBase.asBinder());
                    hwParcel2.send();
                    return;
                case 2:
                    hwParcel.enforceInterface(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
                    boolean add = add(hwParcel.readString(), IBase.asInterface(hwParcel.readStrongBinder()));
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeBool(add);
                    hwParcel2.send();
                    return;
                case 3:
                    hwParcel.enforceInterface(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
                    byte transport = getTransport(hwParcel.readString(), hwParcel.readString());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt8(transport);
                    hwParcel2.send();
                    return;
                case 4:
                    hwParcel.enforceInterface(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
                    ArrayList<String> list = list();
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeStringVector(list);
                    hwParcel2.send();
                    return;
                case 5:
                    hwParcel.enforceInterface(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
                    ArrayList<String> listByInterface = listByInterface(hwParcel.readString());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeStringVector(listByInterface);
                    hwParcel2.send();
                    return;
                case 6:
                    hwParcel.enforceInterface(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
                    boolean registerForNotifications = registerForNotifications(hwParcel.readString(), hwParcel.readString(), IServiceNotification.asInterface(hwParcel.readStrongBinder()));
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeBool(registerForNotifications);
                    hwParcel2.send();
                    return;
                case 7:
                    hwParcel.enforceInterface(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
                    ArrayList<IServiceManager.InstanceDebugInfo> debugDump = debugDump();
                    hwParcel2.writeStatus(0);
                    IServiceManager.InstanceDebugInfo.writeVectorToParcel(hwParcel2, debugDump);
                    hwParcel2.send();
                    return;
                case 8:
                    hwParcel.enforceInterface(android.hidl.manager.V1_0.IServiceManager.kInterfaceName);
                    registerPassthroughClient(hwParcel.readString(), hwParcel.readString());
                    hwParcel2.writeStatus(0);
                    hwParcel2.send();
                    return;
                case 9:
                    hwParcel.enforceInterface(IServiceManager.kInterfaceName);
                    boolean unregisterForNotifications = unregisterForNotifications(hwParcel.readString(), hwParcel.readString(), IServiceNotification.asInterface(hwParcel.readStrongBinder()));
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeBool(unregisterForNotifications);
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
