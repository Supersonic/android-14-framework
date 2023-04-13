package android.hardware.soundtrigger.V2_0;

import android.hardware.audio.common.V2_0.AudioDevice;
import android.hardware.audio.common.V2_0.Uuid;
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
public interface ISoundTriggerHw extends IBase {

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface getPropertiesCallback {
        void onValues(int i, Properties properties);
    }

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface loadPhraseSoundModelCallback {
        void onValues(int i, int i2);
    }

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface loadSoundModelCallback {
        void onValues(int i, int i2);
    }

    @Override // android.hidl.base.V1_0.IBase
    IHwBinder asBinder();

    @Override // android.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    void getProperties(getPropertiesCallback getpropertiescallback) throws RemoteException;

    @Override // android.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    void loadPhraseSoundModel(PhraseSoundModel phraseSoundModel, ISoundTriggerHwCallback iSoundTriggerHwCallback, int i, loadPhraseSoundModelCallback loadphrasesoundmodelcallback) throws RemoteException;

    void loadSoundModel(SoundModel soundModel, ISoundTriggerHwCallback iSoundTriggerHwCallback, int i, loadSoundModelCallback loadsoundmodelcallback) throws RemoteException;

    int startRecognition(int i, RecognitionConfig recognitionConfig, ISoundTriggerHwCallback iSoundTriggerHwCallback, int i2) throws RemoteException;

    int stopAllRecognitions() throws RemoteException;

    int stopRecognition(int i) throws RemoteException;

    int unloadSoundModel(int i) throws RemoteException;

    static ISoundTriggerHw asInterface(IHwBinder iHwBinder) {
        if (iHwBinder == null) {
            return null;
        }
        IHwInterface queryLocalInterface = iHwBinder.queryLocalInterface("android.hardware.soundtrigger@2.0::ISoundTriggerHw");
        if (queryLocalInterface != null && (queryLocalInterface instanceof ISoundTriggerHw)) {
            return (ISoundTriggerHw) queryLocalInterface;
        }
        Proxy proxy = new Proxy(iHwBinder);
        try {
            Iterator<String> it = proxy.interfaceChain().iterator();
            while (it.hasNext()) {
                if (it.next().equals("android.hardware.soundtrigger@2.0::ISoundTriggerHw")) {
                    return proxy;
                }
            }
        } catch (RemoteException unused) {
        }
        return null;
    }

    static ISoundTriggerHw getService(String str, boolean z) throws RemoteException {
        return asInterface(HwBinder.getService("android.hardware.soundtrigger@2.0::ISoundTriggerHw", str, z));
    }

    static ISoundTriggerHw getService(boolean z) throws RemoteException {
        return getService("default", z);
    }

    /* loaded from: classes.dex */
    public static final class Properties {
        public String implementor = new String();
        public String description = new String();
        public int version = 0;
        public Uuid uuid = new Uuid();
        public int maxSoundModels = 0;
        public int maxKeyPhrases = 0;
        public int maxUsers = 0;
        public int recognitionModes = 0;
        public boolean captureTransition = false;
        public int maxBufferMs = 0;
        public boolean concurrentCapture = false;
        public boolean triggerInEvent = false;
        public int powerConsumptionMw = 0;

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && obj.getClass() == Properties.class) {
                Properties properties = (Properties) obj;
                return HidlSupport.deepEquals(this.implementor, properties.implementor) && HidlSupport.deepEquals(this.description, properties.description) && this.version == properties.version && HidlSupport.deepEquals(this.uuid, properties.uuid) && this.maxSoundModels == properties.maxSoundModels && this.maxKeyPhrases == properties.maxKeyPhrases && this.maxUsers == properties.maxUsers && this.recognitionModes == properties.recognitionModes && this.captureTransition == properties.captureTransition && this.maxBufferMs == properties.maxBufferMs && this.concurrentCapture == properties.concurrentCapture && this.triggerInEvent == properties.triggerInEvent && this.powerConsumptionMw == properties.powerConsumptionMw;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.implementor)), Integer.valueOf(HidlSupport.deepHashCode(this.description)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.version))), Integer.valueOf(HidlSupport.deepHashCode(this.uuid)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxSoundModels))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxKeyPhrases))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxUsers))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.recognitionModes))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.captureTransition))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxBufferMs))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.concurrentCapture))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.triggerInEvent))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.powerConsumptionMw))));
        }

        public final String toString() {
            return "{.implementor = " + this.implementor + ", .description = " + this.description + ", .version = " + this.version + ", .uuid = " + this.uuid + ", .maxSoundModels = " + this.maxSoundModels + ", .maxKeyPhrases = " + this.maxKeyPhrases + ", .maxUsers = " + this.maxUsers + ", .recognitionModes = " + this.recognitionModes + ", .captureTransition = " + this.captureTransition + ", .maxBufferMs = " + this.maxBufferMs + ", .concurrentCapture = " + this.concurrentCapture + ", .triggerInEvent = " + this.triggerInEvent + ", .powerConsumptionMw = " + this.powerConsumptionMw + "}";
        }

        public final void readFromParcel(HwParcel hwParcel) {
            readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(88L), 0L);
        }

        public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
            long j2 = j + 0;
            String string = hwBlob.getString(j2);
            this.implementor = string;
            hwParcel.readEmbeddedBuffer(string.getBytes().length + 1, hwBlob.handle(), j2 + 0, false);
            long j3 = j + 16;
            String string2 = hwBlob.getString(j3);
            this.description = string2;
            hwParcel.readEmbeddedBuffer(string2.getBytes().length + 1, hwBlob.handle(), j3 + 0, false);
            this.version = hwBlob.getInt32(j + 32);
            this.uuid.readEmbeddedFromParcel(hwParcel, hwBlob, j + 36);
            this.maxSoundModels = hwBlob.getInt32(j + 52);
            this.maxKeyPhrases = hwBlob.getInt32(j + 56);
            this.maxUsers = hwBlob.getInt32(j + 60);
            this.recognitionModes = hwBlob.getInt32(j + 64);
            this.captureTransition = hwBlob.getBool(j + 68);
            this.maxBufferMs = hwBlob.getInt32(j + 72);
            this.concurrentCapture = hwBlob.getBool(j + 76);
            this.triggerInEvent = hwBlob.getBool(j + 77);
            this.powerConsumptionMw = hwBlob.getInt32(j + 80);
        }

        public final void writeToParcel(HwParcel hwParcel) {
            HwBlob hwBlob = new HwBlob(88);
            writeEmbeddedToBlob(hwBlob, 0L);
            hwParcel.writeBuffer(hwBlob);
        }

        public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
            hwBlob.putString(0 + j, this.implementor);
            hwBlob.putString(16 + j, this.description);
            hwBlob.putInt32(32 + j, this.version);
            this.uuid.writeEmbeddedToBlob(hwBlob, 36 + j);
            hwBlob.putInt32(52 + j, this.maxSoundModels);
            hwBlob.putInt32(56 + j, this.maxKeyPhrases);
            hwBlob.putInt32(60 + j, this.maxUsers);
            hwBlob.putInt32(64 + j, this.recognitionModes);
            hwBlob.putBool(68 + j, this.captureTransition);
            hwBlob.putInt32(72 + j, this.maxBufferMs);
            hwBlob.putBool(76 + j, this.concurrentCapture);
            hwBlob.putBool(77 + j, this.triggerInEvent);
            hwBlob.putInt32(j + 80, this.powerConsumptionMw);
        }
    }

    /* loaded from: classes.dex */
    public static final class SoundModel {
        public int type = 0;
        public Uuid uuid = new Uuid();
        public Uuid vendorUuid = new Uuid();
        public ArrayList<Byte> data = new ArrayList<>();

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && obj.getClass() == SoundModel.class) {
                SoundModel soundModel = (SoundModel) obj;
                return this.type == soundModel.type && HidlSupport.deepEquals(this.uuid, soundModel.uuid) && HidlSupport.deepEquals(this.vendorUuid, soundModel.vendorUuid) && HidlSupport.deepEquals(this.data, soundModel.data);
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.type))), Integer.valueOf(HidlSupport.deepHashCode(this.uuid)), Integer.valueOf(HidlSupport.deepHashCode(this.vendorUuid)), Integer.valueOf(HidlSupport.deepHashCode(this.data)));
        }

        public final String toString() {
            return "{.type = " + SoundModelType.toString(this.type) + ", .uuid = " + this.uuid + ", .vendorUuid = " + this.vendorUuid + ", .data = " + this.data + "}";
        }

        public final void readFromParcel(HwParcel hwParcel) {
            readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(56L), 0L);
        }

        public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
            this.type = hwBlob.getInt32(j + 0);
            this.uuid.readEmbeddedFromParcel(hwParcel, hwBlob, j + 4);
            this.vendorUuid.readEmbeddedFromParcel(hwParcel, hwBlob, j + 20);
            long j2 = j + 40;
            int int32 = hwBlob.getInt32(8 + j2);
            HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 1, hwBlob.handle(), j2 + 0, true);
            this.data.clear();
            for (int i = 0; i < int32; i++) {
                this.data.add(Byte.valueOf(readEmbeddedBuffer.getInt8(i * 1)));
            }
        }

        public final void writeToParcel(HwParcel hwParcel) {
            HwBlob hwBlob = new HwBlob(56);
            writeEmbeddedToBlob(hwBlob, 0L);
            hwParcel.writeBuffer(hwBlob);
        }

        public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
            hwBlob.putInt32(j + 0, this.type);
            this.uuid.writeEmbeddedToBlob(hwBlob, 4 + j);
            this.vendorUuid.writeEmbeddedToBlob(hwBlob, 20 + j);
            int size = this.data.size();
            long j2 = j + 40;
            hwBlob.putInt32(8 + j2, size);
            hwBlob.putBool(12 + j2, false);
            HwBlob hwBlob2 = new HwBlob(size * 1);
            for (int i = 0; i < size; i++) {
                hwBlob2.putInt8(i * 1, this.data.get(i).byteValue());
            }
            hwBlob.putBlob(j2 + 0, hwBlob2);
        }
    }

    /* loaded from: classes.dex */
    public static final class Phrase {

        /* renamed from: id */
        public int f12id = 0;
        public int recognitionModes = 0;
        public ArrayList<Integer> users = new ArrayList<>();
        public String locale = new String();
        public String text = new String();

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && obj.getClass() == Phrase.class) {
                Phrase phrase = (Phrase) obj;
                return this.f12id == phrase.f12id && this.recognitionModes == phrase.recognitionModes && HidlSupport.deepEquals(this.users, phrase.users) && HidlSupport.deepEquals(this.locale, phrase.locale) && HidlSupport.deepEquals(this.text, phrase.text);
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.f12id))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.recognitionModes))), Integer.valueOf(HidlSupport.deepHashCode(this.users)), Integer.valueOf(HidlSupport.deepHashCode(this.locale)), Integer.valueOf(HidlSupport.deepHashCode(this.text)));
        }

        public final String toString() {
            return "{.id = " + this.f12id + ", .recognitionModes = " + this.recognitionModes + ", .users = " + this.users + ", .locale = " + this.locale + ", .text = " + this.text + "}";
        }

        public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
            this.f12id = hwBlob.getInt32(j + 0);
            this.recognitionModes = hwBlob.getInt32(j + 4);
            long j2 = j + 8;
            int int32 = hwBlob.getInt32(8 + j2);
            HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 4, hwBlob.handle(), j2 + 0, true);
            this.users.clear();
            for (int i = 0; i < int32; i++) {
                this.users.add(Integer.valueOf(readEmbeddedBuffer.getInt32(i * 4)));
            }
            long j3 = j + 24;
            String string = hwBlob.getString(j3);
            this.locale = string;
            hwParcel.readEmbeddedBuffer(string.getBytes().length + 1, hwBlob.handle(), j3 + 0, false);
            long j4 = j + 40;
            String string2 = hwBlob.getString(j4);
            this.text = string2;
            hwParcel.readEmbeddedBuffer(string2.getBytes().length + 1, hwBlob.handle(), j4 + 0, false);
        }

        public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
            hwBlob.putInt32(j + 0, this.f12id);
            hwBlob.putInt32(4 + j, this.recognitionModes);
            int size = this.users.size();
            long j2 = j + 8;
            hwBlob.putInt32(8 + j2, size);
            hwBlob.putBool(12 + j2, false);
            HwBlob hwBlob2 = new HwBlob(size * 4);
            for (int i = 0; i < size; i++) {
                hwBlob2.putInt32(i * 4, this.users.get(i).intValue());
            }
            hwBlob.putBlob(j2 + 0, hwBlob2);
            hwBlob.putString(24 + j, this.locale);
            hwBlob.putString(j + 40, this.text);
        }
    }

    /* loaded from: classes.dex */
    public static final class PhraseSoundModel {
        public SoundModel common = new SoundModel();
        public ArrayList<Phrase> phrases = new ArrayList<>();

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && obj.getClass() == PhraseSoundModel.class) {
                PhraseSoundModel phraseSoundModel = (PhraseSoundModel) obj;
                return HidlSupport.deepEquals(this.common, phraseSoundModel.common) && HidlSupport.deepEquals(this.phrases, phraseSoundModel.phrases);
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.common)), Integer.valueOf(HidlSupport.deepHashCode(this.phrases)));
        }

        public final String toString() {
            return "{.common = " + this.common + ", .phrases = " + this.phrases + "}";
        }

        public final void readFromParcel(HwParcel hwParcel) {
            readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(72L), 0L);
        }

        public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
            this.common.readEmbeddedFromParcel(hwParcel, hwBlob, j + 0);
            long j2 = j + 56;
            int int32 = hwBlob.getInt32(8 + j2);
            HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 56, hwBlob.handle(), j2 + 0, true);
            this.phrases.clear();
            for (int i = 0; i < int32; i++) {
                Phrase phrase = new Phrase();
                phrase.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 56);
                this.phrases.add(phrase);
            }
        }

        public final void writeToParcel(HwParcel hwParcel) {
            HwBlob hwBlob = new HwBlob(72);
            writeEmbeddedToBlob(hwBlob, 0L);
            hwParcel.writeBuffer(hwBlob);
        }

        public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
            this.common.writeEmbeddedToBlob(hwBlob, j + 0);
            int size = this.phrases.size();
            long j2 = j + 56;
            hwBlob.putInt32(8 + j2, size);
            hwBlob.putBool(12 + j2, false);
            HwBlob hwBlob2 = new HwBlob(size * 56);
            for (int i = 0; i < size; i++) {
                this.phrases.get(i).writeEmbeddedToBlob(hwBlob2, i * 56);
            }
            hwBlob.putBlob(j2 + 0, hwBlob2);
        }
    }

    /* loaded from: classes.dex */
    public static final class RecognitionConfig {
        public int captureHandle = 0;
        public int captureDevice = 0;
        public boolean captureRequested = false;
        public ArrayList<PhraseRecognitionExtra> phrases = new ArrayList<>();
        public ArrayList<Byte> data = new ArrayList<>();

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && obj.getClass() == RecognitionConfig.class) {
                RecognitionConfig recognitionConfig = (RecognitionConfig) obj;
                return this.captureHandle == recognitionConfig.captureHandle && this.captureDevice == recognitionConfig.captureDevice && this.captureRequested == recognitionConfig.captureRequested && HidlSupport.deepEquals(this.phrases, recognitionConfig.phrases) && HidlSupport.deepEquals(this.data, recognitionConfig.data);
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.captureHandle))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.captureDevice))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.captureRequested))), Integer.valueOf(HidlSupport.deepHashCode(this.phrases)), Integer.valueOf(HidlSupport.deepHashCode(this.data)));
        }

        public final String toString() {
            return "{.captureHandle = " + this.captureHandle + ", .captureDevice = " + AudioDevice.toString(this.captureDevice) + ", .captureRequested = " + this.captureRequested + ", .phrases = " + this.phrases + ", .data = " + this.data + "}";
        }

        public final void readFromParcel(HwParcel hwParcel) {
            readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(48L), 0L);
        }

        public final void readEmbeddedFromParcel(HwParcel hwParcel, HwBlob hwBlob, long j) {
            this.captureHandle = hwBlob.getInt32(j + 0);
            this.captureDevice = hwBlob.getInt32(j + 4);
            this.captureRequested = hwBlob.getBool(j + 8);
            long j2 = j + 16;
            int int32 = hwBlob.getInt32(j2 + 8);
            HwBlob readEmbeddedBuffer = hwParcel.readEmbeddedBuffer(int32 * 32, hwBlob.handle(), j2 + 0, true);
            this.phrases.clear();
            for (int i = 0; i < int32; i++) {
                PhraseRecognitionExtra phraseRecognitionExtra = new PhraseRecognitionExtra();
                phraseRecognitionExtra.readEmbeddedFromParcel(hwParcel, readEmbeddedBuffer, i * 32);
                this.phrases.add(phraseRecognitionExtra);
            }
            long j3 = j + 32;
            int int322 = hwBlob.getInt32(8 + j3);
            HwBlob readEmbeddedBuffer2 = hwParcel.readEmbeddedBuffer(int322 * 1, hwBlob.handle(), j3 + 0, true);
            this.data.clear();
            for (int i2 = 0; i2 < int322; i2++) {
                this.data.add(Byte.valueOf(readEmbeddedBuffer2.getInt8(i2 * 1)));
            }
        }

        public final void writeToParcel(HwParcel hwParcel) {
            HwBlob hwBlob = new HwBlob(48);
            writeEmbeddedToBlob(hwBlob, 0L);
            hwParcel.writeBuffer(hwBlob);
        }

        public final void writeEmbeddedToBlob(HwBlob hwBlob, long j) {
            hwBlob.putInt32(j + 0, this.captureHandle);
            hwBlob.putInt32(j + 4, this.captureDevice);
            hwBlob.putBool(j + 8, this.captureRequested);
            int size = this.phrases.size();
            long j2 = j + 16;
            hwBlob.putInt32(j2 + 8, size);
            hwBlob.putBool(j2 + 12, false);
            HwBlob hwBlob2 = new HwBlob(size * 32);
            for (int i = 0; i < size; i++) {
                this.phrases.get(i).writeEmbeddedToBlob(hwBlob2, i * 32);
            }
            hwBlob.putBlob(j2 + 0, hwBlob2);
            int size2 = this.data.size();
            long j3 = j + 32;
            hwBlob.putInt32(8 + j3, size2);
            hwBlob.putBool(j3 + 12, false);
            HwBlob hwBlob3 = new HwBlob(size2 * 1);
            for (int i2 = 0; i2 < size2; i2++) {
                hwBlob3.putInt8(i2 * 1, this.data.get(i2).byteValue());
            }
            hwBlob.putBlob(j3 + 0, hwBlob3);
        }
    }

    /* loaded from: classes.dex */
    public static final class Proxy implements ISoundTriggerHw {
        public IHwBinder mRemote;

        public Proxy(IHwBinder iHwBinder) {
            Objects.requireNonNull(iHwBinder);
            this.mRemote = iHwBinder;
        }

        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw, android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException unused) {
                return "[class or subclass of android.hardware.soundtrigger@2.0::ISoundTriggerHw]@Proxy";
            }
        }

        public final boolean equals(Object obj) {
            return HidlSupport.interfacesEqual(this, obj);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw
        public void getProperties(getPropertiesCallback getpropertiescallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.soundtrigger@2.0::ISoundTriggerHw");
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(1, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                int readInt32 = hwParcel2.readInt32();
                Properties properties = new Properties();
                properties.readFromParcel(hwParcel2);
                getpropertiescallback.onValues(readInt32, properties);
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw
        public void loadSoundModel(SoundModel soundModel, ISoundTriggerHwCallback iSoundTriggerHwCallback, int i, loadSoundModelCallback loadsoundmodelcallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.soundtrigger@2.0::ISoundTriggerHw");
            soundModel.writeToParcel(hwParcel);
            hwParcel.writeStrongBinder(iSoundTriggerHwCallback == null ? null : iSoundTriggerHwCallback.asBinder());
            hwParcel.writeInt32(i);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(2, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                loadsoundmodelcallback.onValues(hwParcel2.readInt32(), hwParcel2.readInt32());
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw
        public void loadPhraseSoundModel(PhraseSoundModel phraseSoundModel, ISoundTriggerHwCallback iSoundTriggerHwCallback, int i, loadPhraseSoundModelCallback loadphrasesoundmodelcallback) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.soundtrigger@2.0::ISoundTriggerHw");
            phraseSoundModel.writeToParcel(hwParcel);
            hwParcel.writeStrongBinder(iSoundTriggerHwCallback == null ? null : iSoundTriggerHwCallback.asBinder());
            hwParcel.writeInt32(i);
            HwParcel hwParcel2 = new HwParcel();
            try {
                this.mRemote.transact(3, hwParcel, hwParcel2, 0);
                hwParcel2.verifySuccess();
                hwParcel.releaseTemporaryStorage();
                loadphrasesoundmodelcallback.onValues(hwParcel2.readInt32(), hwParcel2.readInt32());
            } finally {
                hwParcel2.release();
            }
        }

        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw
        public int unloadSoundModel(int i) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.soundtrigger@2.0::ISoundTriggerHw");
            hwParcel.writeInt32(i);
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

        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw
        public int startRecognition(int i, RecognitionConfig recognitionConfig, ISoundTriggerHwCallback iSoundTriggerHwCallback, int i2) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.soundtrigger@2.0::ISoundTriggerHw");
            hwParcel.writeInt32(i);
            recognitionConfig.writeToParcel(hwParcel);
            hwParcel.writeStrongBinder(iSoundTriggerHwCallback == null ? null : iSoundTriggerHwCallback.asBinder());
            hwParcel.writeInt32(i2);
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

        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw
        public int stopRecognition(int i) throws RemoteException {
            HwParcel hwParcel = new HwParcel();
            hwParcel.writeInterfaceToken("android.hardware.soundtrigger@2.0::ISoundTriggerHw");
            hwParcel.writeInt32(i);
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

        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw, android.hidl.base.V1_0.IBase
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

        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw, android.hidl.base.V1_0.IBase
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
    public static abstract class Stub extends HwBinder implements ISoundTriggerHw {
        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw, android.hidl.base.V1_0.IBase
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw, android.hidl.base.V1_0.IBase
        public void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) {
        }

        @Override // android.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return "android.hardware.soundtrigger@2.0::ISoundTriggerHw";
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

        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw, android.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList("android.hardware.soundtrigger@2.0::ISoundTriggerHw", IBase.kInterfaceName));
        }

        @Override // android.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{91, -17, -64, 25, -53, -23, 73, 83, 102, 30, 44, -37, -107, -29, -49, 100, -11, -27, 101, -62, -108, 3, -31, -62, -38, -20, -62, -66, 68, -32, -91, 92}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, -48, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, -13, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
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
            if ("android.hardware.soundtrigger@2.0::ISoundTriggerHw".equals(str)) {
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
                    hwParcel.enforceInterface("android.hardware.soundtrigger@2.0::ISoundTriggerHw");
                    getProperties(new getPropertiesCallback() { // from class: android.hardware.soundtrigger.V2_0.ISoundTriggerHw.Stub.1
                        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw.getPropertiesCallback
                        public void onValues(int i3, Properties properties) {
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeInt32(i3);
                            properties.writeToParcel(hwParcel2);
                            hwParcel2.send();
                        }
                    });
                    return;
                case 2:
                    hwParcel.enforceInterface("android.hardware.soundtrigger@2.0::ISoundTriggerHw");
                    SoundModel soundModel = new SoundModel();
                    soundModel.readFromParcel(hwParcel);
                    loadSoundModel(soundModel, ISoundTriggerHwCallback.asInterface(hwParcel.readStrongBinder()), hwParcel.readInt32(), new loadSoundModelCallback() { // from class: android.hardware.soundtrigger.V2_0.ISoundTriggerHw.Stub.2
                        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw.loadSoundModelCallback
                        public void onValues(int i3, int i4) {
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeInt32(i3);
                            hwParcel2.writeInt32(i4);
                            hwParcel2.send();
                        }
                    });
                    return;
                case 3:
                    hwParcel.enforceInterface("android.hardware.soundtrigger@2.0::ISoundTriggerHw");
                    PhraseSoundModel phraseSoundModel = new PhraseSoundModel();
                    phraseSoundModel.readFromParcel(hwParcel);
                    loadPhraseSoundModel(phraseSoundModel, ISoundTriggerHwCallback.asInterface(hwParcel.readStrongBinder()), hwParcel.readInt32(), new loadPhraseSoundModelCallback() { // from class: android.hardware.soundtrigger.V2_0.ISoundTriggerHw.Stub.3
                        @Override // android.hardware.soundtrigger.V2_0.ISoundTriggerHw.loadPhraseSoundModelCallback
                        public void onValues(int i3, int i4) {
                            hwParcel2.writeStatus(0);
                            hwParcel2.writeInt32(i3);
                            hwParcel2.writeInt32(i4);
                            hwParcel2.send();
                        }
                    });
                    return;
                case 4:
                    hwParcel.enforceInterface("android.hardware.soundtrigger@2.0::ISoundTriggerHw");
                    int unloadSoundModel = unloadSoundModel(hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(unloadSoundModel);
                    hwParcel2.send();
                    return;
                case 5:
                    hwParcel.enforceInterface("android.hardware.soundtrigger@2.0::ISoundTriggerHw");
                    int readInt32 = hwParcel.readInt32();
                    RecognitionConfig recognitionConfig = new RecognitionConfig();
                    recognitionConfig.readFromParcel(hwParcel);
                    int startRecognition = startRecognition(readInt32, recognitionConfig, ISoundTriggerHwCallback.asInterface(hwParcel.readStrongBinder()), hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(startRecognition);
                    hwParcel2.send();
                    return;
                case 6:
                    hwParcel.enforceInterface("android.hardware.soundtrigger@2.0::ISoundTriggerHw");
                    int stopRecognition = stopRecognition(hwParcel.readInt32());
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(stopRecognition);
                    hwParcel2.send();
                    return;
                case 7:
                    hwParcel.enforceInterface("android.hardware.soundtrigger@2.0::ISoundTriggerHw");
                    int stopAllRecognitions = stopAllRecognitions();
                    hwParcel2.writeStatus(0);
                    hwParcel2.writeInt32(stopAllRecognitions);
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
