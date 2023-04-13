package android.hardware.soundtrigger.V2_1;

import android.hardware.soundtrigger.V2_0.ISoundTriggerHwCallback;
import android.hardware.soundtrigger.V2_0.PhraseRecognitionExtra;
import android.hidl.base.V1_0.DebugInfo;
import android.hidl.base.V1_0.IBase;
import android.os.HidlMemory;
import android.os.HidlSupport;
import android.os.HwBinder;
import android.os.HwBlob;
import android.os.HwParcel;
import android.os.IHwBinder;
import android.os.IHwInterface;
import android.os.NativeHandle;
import android.os.RemoteException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes.dex */
public interface ISoundTriggerHwCallback extends android.hardware.soundtrigger.V2_0.ISoundTriggerHwCallback {
    @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHwCallback, android.hidl.base.V1_0.IBase
    IHwBinder asBinder();

    @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHwCallback, android.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    void phraseRecognitionCallback_2_1(PhraseRecognitionEvent phraseRecognitionEvent, int i) throws RemoteException;

    void recognitionCallback_2_1(RecognitionEvent recognitionEvent, int i) throws RemoteException;

    void soundModelCallback_2_1(ModelEvent modelEvent, int i) throws RemoteException;

    static ISoundTriggerHwCallback asInterface(IHwBinder iHwBinder) {
        if (iHwBinder == null) {
            return null;
        }
        IHwInterface queryLocalInterface = iHwBinder.queryLocalInterface("android.hardware.soundtrigger@2.1::ISoundTriggerHwCallback");
        if (queryLocalInterface != null && (queryLocalInterface instanceof ISoundTriggerHwCallback)) {
            return (ISoundTriggerHwCallback) queryLocalInterface;
        }
        Proxy proxy = new Proxy(iHwBinder);
        try {
            Iterator<String> it = proxy.interfaceChain().iterator();
            while (it.hasNext()) {
                if (it.next().equals("android.hardware.soundtrigger@2.1::ISoundTriggerHwCallback")) {
                    return proxy;
                }
            }
        } catch (RemoteException unused) {
        }
        return null;
    }

    /* loaded from: classes.dex */
    public static final class RecognitionEvent {
        public ISoundTriggerHwCallback.RecognitionEvent header = new ISoundTriggerHwCallback.RecognitionEvent();
        public HidlMemory data = null;

        public final String toString() {
            return "{.header = " + this.header + ", .data = " + this.data + "}";
        }

        public final void readFromParcel(HwParcel hwParcel) {
            readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(160L), 0L);
        }

        public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
            this.header.readEmbeddedFromParcel(hwParcel, hwBlob, 0 + j);
            long j2 = j + 120;
            try {
                this.data = hwParcel.readEmbeddedHidlMemory(hwBlob.getFieldHandle(j2), hwBlob.handle(), j2).dup();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class PhraseRecognitionEvent {
        public RecognitionEvent common = new RecognitionEvent();
        public ArrayList<PhraseRecognitionExtra> phraseExtras = new ArrayList<>();

        public final String toString() {
            return "{.common = " + this.common + ", .phraseExtras = " + this.phraseExtras + "}";
        }

        public final void readFromParcel(HwParcel hwParcel) {
            readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(176L), 0L);
        }

        public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
            this.common.readEmbeddedFromParcel(hwParcel, hwBlob, j + 0);
            long j2 = j + 160;
            int int32 = hwBlob.getInt32(8 + j2);
            HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 32, hwBlob.handle(), j2 + 0, true);
            this.phraseExtras.clear();
            for (int i = 0; i < int32; i++) {
                PhraseRecognitionExtra phraseRecognitionExtra = new PhraseRecognitionExtra();
                phraseRecognitionExtra.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 32);
                this.phraseExtras.add(phraseRecognitionExtra);
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class ModelEvent {
        public ISoundTriggerHwCallback.ModelEvent header = new ISoundTriggerHwCallback.ModelEvent();
        public HidlMemory data = null;

        public final String toString() {
            return "{.header = " + this.header + ", .data = " + this.data + "}";
        }

        public final void readFromParcel(HwParcel hwParcel) {
            readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(64L), 0L);
        }

        public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
            this.header.readEmbeddedFromParcel(hwParcel, hwBlob, 0 + j);
            long j2 = j + 24;
            try {
                this.data = hwParcel.readEmbeddedHidlMemory(hwBlob.getFieldHandle(j2), hwBlob.handle(), j2).dup();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class Proxy implements ISoundTriggerHwCallback {
        public IHwBinder mRemote;

        public Proxy(IHwBinder iHwBinder) {
            Objects.requireNonNull(iHwBinder);
            this.mRemote = iHwBinder;
        }

        @Override // android.hardware.soundtrigger.V2_1.ISoundTriggerHwCallback, android.hardware.soundtrigger.V2_0.ISoundTriggerHwCallback, android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException unused) {
                return "[class or subclass of android.hardware.soundtrigger@2.1::ISoundTriggerHwCallback]@Proxy";
            }
        }

        public final boolean equals(Object obj) {
            return HidlSupport.interfacesEqual(this, obj);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.soundtrigger.V2_1.ISoundTriggerHwCallback, android.hardware.soundtrigger.V2_0.ISoundTriggerHwCallback, android.hidl.base.V1_0.IBase
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
    public static abstract class Stub extends HwBinder implements ISoundTriggerHwCallback {
        @Override // android.hardware.soundtrigger.V2_1.ISoundTriggerHwCallback, android.hardware.soundtrigger.V2_0.ISoundTriggerHwCallback, android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hidl.base.V1_0.IBase
        public void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) {
        }

        @Override // android.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return "android.hardware.soundtrigger@2.1::ISoundTriggerHwCallback";
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

        @Override // android.hardware.soundtrigger.V2_1.ISoundTriggerHwCallback, android.hardware.soundtrigger.V2_0.ISoundTriggerHwCallback, android.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList("android.hardware.soundtrigger@2.1::ISoundTriggerHwCallback", "android.hardware.soundtrigger@2.0::ISoundTriggerHwCallback", IBase.kInterfaceName));
        }

        @Override // android.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{-24, -56, 108, 105, -60, 56, -38, -115, 21, 73, -123, 108, 27, -77, -30, -47, -72, -38, 82, 114, 47, -126, 53, -1, 73, -93, 15, 44, -50, -111, 116, 44}, new byte[]{26, 110, 43, -46, -119, -14, 41, 49, -59, 38, -78, 25, 22, -111, 15, 29, 76, 67, 107, 122, -53, -107, 86, -28, 36, 61, -28, -50, -114, 108, -62, -28}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
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
            if ("android.hardware.soundtrigger@2.1::ISoundTriggerHwCallback".equals(str)) {
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
                    hwParcel.enforceInterface("android.hardware.soundtrigger@2.0::ISoundTriggerHwCallback");
                    ISoundTriggerHwCallback.RecognitionEvent recognitionEvent = new ISoundTriggerHwCallback.RecognitionEvent();
                    recognitionEvent.readFromParcel(hwParcel);
                    recognitionCallback(recognitionEvent, hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.send();
                    return;
                case 2:
                    hwParcel.enforceInterface("android.hardware.soundtrigger@2.0::ISoundTriggerHwCallback");
                    ISoundTriggerHwCallback.PhraseRecognitionEvent phraseRecognitionEvent = new ISoundTriggerHwCallback.PhraseRecognitionEvent();
                    phraseRecognitionEvent.readFromParcel(hwParcel);
                    phraseRecognitionCallback(phraseRecognitionEvent, hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.send();
                    return;
                case 3:
                    hwParcel.enforceInterface("android.hardware.soundtrigger@2.0::ISoundTriggerHwCallback");
                    ISoundTriggerHwCallback.ModelEvent modelEvent = new ISoundTriggerHwCallback.ModelEvent();
                    modelEvent.readFromParcel(hwParcel);
                    soundModelCallback(modelEvent, hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.send();
                    return;
                case 4:
                    hwParcel.enforceInterface("android.hardware.soundtrigger@2.1::ISoundTriggerHwCallback");
                    RecognitionEvent recognitionEvent2 = new RecognitionEvent();
                    recognitionEvent2.readFromParcel(hwParcel);
                    recognitionCallback_2_1(recognitionEvent2, hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.send();
                    return;
                case 5:
                    hwParcel.enforceInterface("android.hardware.soundtrigger@2.1::ISoundTriggerHwCallback");
                    PhraseRecognitionEvent phraseRecognitionEvent2 = new PhraseRecognitionEvent();
                    phraseRecognitionEvent2.readFromParcel(hwParcel);
                    phraseRecognitionCallback_2_1(phraseRecognitionEvent2, hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.send();
                    return;
                case 6:
                    hwParcel.enforceInterface("android.hardware.soundtrigger@2.1::ISoundTriggerHwCallback");
                    ModelEvent modelEvent2 = new ModelEvent();
                    modelEvent2.readFromParcel(hwParcel);
                    soundModelCallback_2_1(modelEvent2, hwParcel.readInt32());
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
