package android.hardware.soundtrigger;

import android.annotation.SystemApi;
import android.content.Context;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.AudioFormat;
import android.media.permission.Identity;
import android.media.soundtrigger_middleware.ISoundTriggerMiddlewareService;
import android.media.soundtrigger_middleware.SoundTriggerModuleDescriptor;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.ServiceSpecificException;
import android.system.OsConstants;
import android.util.Log;
import com.android.internal.logging.nano.MetricsProto;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
@SystemApi
/* loaded from: classes2.dex */
public class SoundTrigger {
    public static final int RECOGNITION_MODE_GENERIC = 8;
    public static final int RECOGNITION_MODE_USER_AUTHENTICATION = 4;
    public static final int RECOGNITION_MODE_USER_IDENTIFICATION = 2;
    public static final int RECOGNITION_MODE_VOICE_TRIGGER = 1;
    public static final int RECOGNITION_STATUS_ABORT = 1;
    public static final int RECOGNITION_STATUS_FAILURE = 2;
    public static final int RECOGNITION_STATUS_GET_STATE_RESPONSE = 3;
    public static final int RECOGNITION_STATUS_SUCCESS = 0;
    public static final int STATUS_ERROR = Integer.MIN_VALUE;
    public static final int STATUS_OK = 0;
    private static final String TAG = "SoundTrigger";
    public static final int STATUS_PERMISSION_DENIED = -OsConstants.EPERM;
    public static final int STATUS_NO_INIT = -OsConstants.ENODEV;
    public static final int STATUS_BAD_VALUE = -OsConstants.EINVAL;
    public static final int STATUS_DEAD_OBJECT = -OsConstants.EPIPE;
    public static final int STATUS_INVALID_OPERATION = -OsConstants.ENOSYS;
    public static final int STATUS_BUSY = -OsConstants.EBUSY;
    private static Object mServiceLock = new Object();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface RecognitionModes {
    }

    /* loaded from: classes2.dex */
    public interface StatusListener {
        void onModelUnloaded(int i);

        void onRecognition(RecognitionEvent recognitionEvent);

        void onResourcesAvailable();

        void onServiceDied();
    }

    private SoundTrigger() {
    }

    /* loaded from: classes2.dex */
    public static final class ModuleProperties implements Parcelable {
        public static final int AUDIO_CAPABILITY_ECHO_CANCELLATION = 1;
        public static final int AUDIO_CAPABILITY_NOISE_SUPPRESSION = 2;
        public static final Parcelable.Creator<ModuleProperties> CREATOR = new Parcelable.Creator<ModuleProperties>() { // from class: android.hardware.soundtrigger.SoundTrigger.ModuleProperties.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ModuleProperties createFromParcel(Parcel in) {
                return ModuleProperties.fromParcel(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ModuleProperties[] newArray(int size) {
                return new ModuleProperties[size];
            }
        };
        private final int mAudioCapabilities;
        private final String mDescription;
        private final int mId;
        private final String mImplementor;
        private final int mMaxBufferMillis;
        private final int mMaxKeyphrases;
        private final int mMaxSoundModels;
        private final int mMaxUsers;
        private final int mPowerConsumptionMw;
        private final int mRecognitionModes;
        private final boolean mReturnsTriggerInEvent;
        private final String mSupportedModelArch;
        private final boolean mSupportsCaptureTransition;
        private final boolean mSupportsConcurrentCapture;
        private final UUID mUuid;
        private final int mVersion;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface AudioCapabilities {
        }

        public ModuleProperties(int id, String implementor, String description, String uuid, int version, String supportedModelArch, int maxSoundModels, int maxKeyphrases, int maxUsers, int recognitionModes, boolean supportsCaptureTransition, int maxBufferMs, boolean supportsConcurrentCapture, int powerConsumptionMw, boolean returnsTriggerInEvent, int audioCapabilities) {
            this.mId = id;
            this.mImplementor = (String) Objects.requireNonNull(implementor);
            this.mDescription = (String) Objects.requireNonNull(description);
            this.mUuid = UUID.fromString((String) Objects.requireNonNull(uuid));
            this.mVersion = version;
            this.mSupportedModelArch = (String) Objects.requireNonNull(supportedModelArch);
            this.mMaxSoundModels = maxSoundModels;
            this.mMaxKeyphrases = maxKeyphrases;
            this.mMaxUsers = maxUsers;
            this.mRecognitionModes = recognitionModes;
            this.mSupportsCaptureTransition = supportsCaptureTransition;
            this.mMaxBufferMillis = maxBufferMs;
            this.mSupportsConcurrentCapture = supportsConcurrentCapture;
            this.mPowerConsumptionMw = powerConsumptionMw;
            this.mReturnsTriggerInEvent = returnsTriggerInEvent;
            this.mAudioCapabilities = audioCapabilities;
        }

        public int getId() {
            return this.mId;
        }

        public String getImplementor() {
            return this.mImplementor;
        }

        public String getDescription() {
            return this.mDescription;
        }

        public UUID getUuid() {
            return this.mUuid;
        }

        public int getVersion() {
            return this.mVersion;
        }

        public String getSupportedModelArch() {
            return this.mSupportedModelArch;
        }

        public int getMaxSoundModels() {
            return this.mMaxSoundModels;
        }

        public int getMaxKeyphrases() {
            return this.mMaxKeyphrases;
        }

        public int getMaxUsers() {
            return this.mMaxUsers;
        }

        public int getRecognitionModes() {
            return this.mRecognitionModes;
        }

        public boolean isCaptureTransitionSupported() {
            return this.mSupportsCaptureTransition;
        }

        public int getMaxBufferMillis() {
            return this.mMaxBufferMillis;
        }

        public boolean isConcurrentCaptureSupported() {
            return this.mSupportsConcurrentCapture;
        }

        public int getPowerConsumptionMw() {
            return this.mPowerConsumptionMw;
        }

        public boolean isTriggerReturnedInEvent() {
            return this.mReturnsTriggerInEvent;
        }

        public int getAudioCapabilities() {
            return this.mAudioCapabilities;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static ModuleProperties fromParcel(Parcel in) {
            int id = in.readInt();
            String implementor = in.readString();
            String description = in.readString();
            String uuid = in.readString();
            int version = in.readInt();
            String supportedModelArch = in.readString();
            int maxSoundModels = in.readInt();
            int maxKeyphrases = in.readInt();
            int maxUsers = in.readInt();
            int recognitionModes = in.readInt();
            boolean supportsCaptureTransition = in.readByte() == 1;
            int maxBufferMs = in.readInt();
            boolean supportsConcurrentCapture = in.readByte() == 1;
            int powerConsumptionMw = in.readInt();
            boolean returnsTriggerInEvent = in.readByte() == 1;
            int audioCapabilities = in.readInt();
            return new ModuleProperties(id, implementor, description, uuid, version, supportedModelArch, maxSoundModels, maxKeyphrases, maxUsers, recognitionModes, supportsCaptureTransition, maxBufferMs, supportsConcurrentCapture, powerConsumptionMw, returnsTriggerInEvent, audioCapabilities);
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(getId());
            dest.writeString(getImplementor());
            dest.writeString(getDescription());
            dest.writeString(getUuid().toString());
            dest.writeInt(getVersion());
            dest.writeString(getSupportedModelArch());
            dest.writeInt(getMaxSoundModels());
            dest.writeInt(getMaxKeyphrases());
            dest.writeInt(getMaxUsers());
            dest.writeInt(getRecognitionModes());
            dest.writeByte(isCaptureTransitionSupported() ? (byte) 1 : (byte) 0);
            dest.writeInt(getMaxBufferMillis());
            dest.writeByte(isConcurrentCaptureSupported() ? (byte) 1 : (byte) 0);
            dest.writeInt(getPowerConsumptionMw());
            dest.writeByte(isTriggerReturnedInEvent() ? (byte) 1 : (byte) 0);
            dest.writeInt(getAudioCapabilities());
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !(obj instanceof ModuleProperties)) {
                return false;
            }
            ModuleProperties other = (ModuleProperties) obj;
            if (this.mId == other.mId && this.mImplementor.equals(other.mImplementor) && this.mDescription.equals(other.mDescription) && this.mUuid.equals(other.mUuid) && this.mVersion == other.mVersion && this.mSupportedModelArch.equals(other.mSupportedModelArch) && this.mMaxSoundModels == other.mMaxSoundModels && this.mMaxKeyphrases == other.mMaxKeyphrases && this.mMaxUsers == other.mMaxUsers && this.mRecognitionModes == other.mRecognitionModes && this.mSupportsCaptureTransition == other.mSupportsCaptureTransition && this.mMaxBufferMillis == other.mMaxBufferMillis && this.mSupportsConcurrentCapture == other.mSupportsConcurrentCapture && this.mPowerConsumptionMw == other.mPowerConsumptionMw && this.mReturnsTriggerInEvent == other.mReturnsTriggerInEvent && this.mAudioCapabilities == other.mAudioCapabilities) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = (1 * 31) + this.mId;
            return (((((((((((((((((((((((((((((result * 31) + this.mImplementor.hashCode()) * 31) + this.mDescription.hashCode()) * 31) + this.mUuid.hashCode()) * 31) + this.mVersion) * 31) + this.mSupportedModelArch.hashCode()) * 31) + this.mMaxSoundModels) * 31) + this.mMaxKeyphrases) * 31) + this.mMaxUsers) * 31) + this.mRecognitionModes) * 31) + (this.mSupportsCaptureTransition ? 1 : 0)) * 31) + this.mMaxBufferMillis) * 31) + (this.mSupportsConcurrentCapture ? 1 : 0)) * 31) + this.mPowerConsumptionMw) * 31) + (this.mReturnsTriggerInEvent ? 1 : 0)) * 31) + this.mAudioCapabilities;
        }

        public String toString() {
            return "ModuleProperties [id=" + getId() + ", implementor=" + getImplementor() + ", description=" + getDescription() + ", uuid=" + getUuid() + ", version=" + getVersion() + " , supportedModelArch=" + getSupportedModelArch() + ", maxSoundModels=" + getMaxSoundModels() + ", maxKeyphrases=" + getMaxKeyphrases() + ", maxUsers=" + getMaxUsers() + ", recognitionModes=" + getRecognitionModes() + ", supportsCaptureTransition=" + isCaptureTransitionSupported() + ", maxBufferMs=" + getMaxBufferMillis() + ", supportsConcurrentCapture=" + isConcurrentCaptureSupported() + ", powerConsumptionMw=" + getPowerConsumptionMw() + ", returnsTriggerInEvent=" + isTriggerReturnedInEvent() + ", audioCapabilities=" + getAudioCapabilities() + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    /* loaded from: classes2.dex */
    public static class SoundModel {
        public static final int TYPE_GENERIC_SOUND = 1;
        public static final int TYPE_KEYPHRASE = 0;
        public static final int TYPE_UNKNOWN = -1;
        private final byte[] mData;
        private final int mType;
        private final UUID mUuid;
        private final UUID mVendorUuid;
        private final int mVersion;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface SoundModelType {
        }

        public SoundModel(UUID uuid, UUID vendorUuid, int type, byte[] data, int version) {
            this.mUuid = (UUID) Objects.requireNonNull(uuid);
            this.mVendorUuid = vendorUuid != null ? vendorUuid : new UUID(0L, 0L);
            this.mType = type;
            this.mVersion = version;
            this.mData = data != null ? data : new byte[0];
        }

        public UUID getUuid() {
            return this.mUuid;
        }

        public int getType() {
            return this.mType;
        }

        public UUID getVendorUuid() {
            return this.mVendorUuid;
        }

        public int getVersion() {
            return this.mVersion;
        }

        public byte[] getData() {
            return this.mData;
        }

        public int hashCode() {
            int result = (1 * 31) + getVersion();
            return (((((((result * 31) + Arrays.hashCode(getData())) * 31) + getType()) * 31) + (getUuid() == null ? 0 : getUuid().hashCode())) * 31) + (getVendorUuid() != null ? getVendorUuid().hashCode() : 0);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !(obj instanceof SoundModel)) {
                return false;
            }
            SoundModel other = (SoundModel) obj;
            if (getType() != other.getType()) {
                return false;
            }
            if (getUuid() == null) {
                if (other.getUuid() != null) {
                    return false;
                }
            } else if (!getUuid().equals(other.getUuid())) {
                return false;
            }
            if (getVendorUuid() == null) {
                if (other.getVendorUuid() != null) {
                    return false;
                }
            } else if (!getVendorUuid().equals(other.getVendorUuid())) {
                return false;
            }
            if (Arrays.equals(getData(), other.getData()) && getVersion() == other.getVersion()) {
                return true;
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static final class Keyphrase implements Parcelable {
        public static final Parcelable.Creator<Keyphrase> CREATOR = new Parcelable.Creator<Keyphrase>() { // from class: android.hardware.soundtrigger.SoundTrigger.Keyphrase.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Keyphrase createFromParcel(Parcel in) {
                return Keyphrase.readFromParcel(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Keyphrase[] newArray(int size) {
                return new Keyphrase[size];
            }
        };
        private final int mId;
        private final Locale mLocale;
        private final int mRecognitionModes;
        private final String mText;
        private final int[] mUsers;

        public Keyphrase(int id, int recognitionModes, Locale locale, String text, int[] users) {
            this.mId = id;
            this.mRecognitionModes = recognitionModes;
            this.mLocale = (Locale) Objects.requireNonNull(locale);
            this.mText = (String) Objects.requireNonNull(text);
            this.mUsers = users != null ? users : new int[0];
        }

        public int getId() {
            return this.mId;
        }

        public int getRecognitionModes() {
            return this.mRecognitionModes;
        }

        public Locale getLocale() {
            return this.mLocale;
        }

        public String getText() {
            return this.mText;
        }

        public int[] getUsers() {
            return this.mUsers;
        }

        public static Keyphrase readFromParcel(Parcel in) {
            int[] users;
            int id = in.readInt();
            int recognitionModes = in.readInt();
            Locale locale = Locale.forLanguageTag(in.readString());
            String text = in.readString();
            int numUsers = in.readInt();
            if (numUsers < 0) {
                users = null;
            } else {
                int[] users2 = new int[numUsers];
                in.readIntArray(users2);
                users = users2;
            }
            return new Keyphrase(id, recognitionModes, locale, text, users);
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(getId());
            dest.writeInt(getRecognitionModes());
            dest.writeString(getLocale().toLanguageTag());
            dest.writeString(getText());
            if (getUsers() != null) {
                dest.writeInt(getUsers().length);
                dest.writeIntArray(getUsers());
                return;
            }
            dest.writeInt(-1);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public int hashCode() {
            int result = (1 * 31) + (getText() == null ? 0 : getText().hashCode());
            return (((((((result * 31) + getId()) * 31) + (getLocale() != null ? getLocale().hashCode() : 0)) * 31) + getRecognitionModes()) * 31) + Arrays.hashCode(getUsers());
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Keyphrase other = (Keyphrase) obj;
            if (getText() == null) {
                if (other.getText() != null) {
                    return false;
                }
            } else if (!getText().equals(other.getText())) {
                return false;
            }
            if (getId() != other.getId()) {
                return false;
            }
            if (getLocale() == null) {
                if (other.getLocale() != null) {
                    return false;
                }
            } else if (!getLocale().equals(other.getLocale())) {
                return false;
            }
            if (getRecognitionModes() == other.getRecognitionModes() && Arrays.equals(getUsers(), other.getUsers())) {
                return true;
            }
            return false;
        }

        public String toString() {
            return "Keyphrase [id=" + getId() + ", recognitionModes=" + getRecognitionModes() + ", locale=" + getLocale().toLanguageTag() + ", text=" + getText() + ", users=" + Arrays.toString(getUsers()) + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    /* loaded from: classes2.dex */
    public static final class KeyphraseSoundModel extends SoundModel implements Parcelable {
        public static final Parcelable.Creator<KeyphraseSoundModel> CREATOR = new Parcelable.Creator<KeyphraseSoundModel>() { // from class: android.hardware.soundtrigger.SoundTrigger.KeyphraseSoundModel.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public KeyphraseSoundModel createFromParcel(Parcel in) {
                return KeyphraseSoundModel.readFromParcel(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public KeyphraseSoundModel[] newArray(int size) {
                return new KeyphraseSoundModel[size];
            }
        };
        private final Keyphrase[] mKeyphrases;

        public KeyphraseSoundModel(UUID uuid, UUID vendorUuid, byte[] data, Keyphrase[] keyphrases, int version) {
            super(uuid, vendorUuid, 0, data, version);
            this.mKeyphrases = keyphrases != null ? keyphrases : new Keyphrase[0];
        }

        public KeyphraseSoundModel(UUID uuid, UUID vendorUuid, byte[] data, Keyphrase[] keyphrases) {
            this(uuid, vendorUuid, data, keyphrases, -1);
        }

        public Keyphrase[] getKeyphrases() {
            return this.mKeyphrases;
        }

        public static KeyphraseSoundModel readFromParcel(Parcel in) {
            UUID vendorUuid;
            UUID uuid = UUID.fromString(in.readString());
            int length = in.readInt();
            if (length < 0) {
                vendorUuid = null;
            } else {
                UUID vendorUuid2 = UUID.fromString(in.readString());
                vendorUuid = vendorUuid2;
            }
            int version = in.readInt();
            byte[] data = in.readBlob();
            Keyphrase[] keyphrases = (Keyphrase[]) in.createTypedArray(Keyphrase.CREATOR);
            return new KeyphraseSoundModel(uuid, vendorUuid, data, keyphrases, version);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(getUuid().toString());
            if (getVendorUuid() == null) {
                dest.writeInt(-1);
            } else {
                dest.writeInt(getVendorUuid().toString().length());
                dest.writeString(getVendorUuid().toString());
            }
            dest.writeInt(getVersion());
            dest.writeBlob(getData());
            dest.writeTypedArray(getKeyphrases(), flags);
        }

        public String toString() {
            return "KeyphraseSoundModel [keyphrases=" + Arrays.toString(getKeyphrases()) + ", uuid=" + getUuid() + ", vendorUuid=" + getVendorUuid() + ", type=" + getType() + ", data=" + (getData() == null ? 0 : getData().length) + ", version=" + getVersion() + NavigationBarInflaterView.SIZE_MOD_END;
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.SoundModel
        public int hashCode() {
            int result = super.hashCode();
            return (result * 31) + Arrays.hashCode(getKeyphrases());
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.SoundModel
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (super.equals(obj) && (obj instanceof KeyphraseSoundModel)) {
                KeyphraseSoundModel other = (KeyphraseSoundModel) obj;
                return Arrays.equals(getKeyphrases(), other.getKeyphrases());
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static class GenericSoundModel extends SoundModel implements Parcelable {
        public static final Parcelable.Creator<GenericSoundModel> CREATOR = new Parcelable.Creator<GenericSoundModel>() { // from class: android.hardware.soundtrigger.SoundTrigger.GenericSoundModel.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public GenericSoundModel createFromParcel(Parcel in) {
                return GenericSoundModel.fromParcel(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public GenericSoundModel[] newArray(int size) {
                return new GenericSoundModel[size];
            }
        };

        public GenericSoundModel(UUID uuid, UUID vendorUuid, byte[] data, int version) {
            super(uuid, vendorUuid, 1, data, version);
        }

        public GenericSoundModel(UUID uuid, UUID vendorUuid, byte[] data) {
            this(uuid, vendorUuid, data, -1);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static GenericSoundModel fromParcel(Parcel in) {
            UUID uuid = UUID.fromString(in.readString());
            UUID vendorUuid = null;
            int length = in.readInt();
            if (length >= 0) {
                vendorUuid = UUID.fromString(in.readString());
            }
            byte[] data = in.readBlob();
            int version = in.readInt();
            return new GenericSoundModel(uuid, vendorUuid, data, version);
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(getUuid().toString());
            if (getVendorUuid() == null) {
                dest.writeInt(-1);
            } else {
                dest.writeInt(getVendorUuid().toString().length());
                dest.writeString(getVendorUuid().toString());
            }
            dest.writeBlob(getData());
            dest.writeInt(getVersion());
        }

        public String toString() {
            return "GenericSoundModel [uuid=" + getUuid() + ", vendorUuid=" + getVendorUuid() + ", type=" + getType() + ", data=" + (getData() == null ? 0 : getData().length) + ", version=" + getVersion() + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    /* loaded from: classes2.dex */
    public static final class ModelParamRange implements Parcelable {
        public static final Parcelable.Creator<ModelParamRange> CREATOR = new Parcelable.Creator<ModelParamRange>() { // from class: android.hardware.soundtrigger.SoundTrigger.ModelParamRange.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ModelParamRange createFromParcel(Parcel in) {
                return new ModelParamRange(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ModelParamRange[] newArray(int size) {
                return new ModelParamRange[size];
            }
        };
        private final int mEnd;
        private final int mStart;

        public ModelParamRange(int start, int end) {
            this.mStart = start;
            this.mEnd = end;
        }

        private ModelParamRange(Parcel in) {
            this.mStart = in.readInt();
            this.mEnd = in.readInt();
        }

        public int getStart() {
            return this.mStart;
        }

        public int getEnd() {
            return this.mEnd;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public int hashCode() {
            int result = (1 * 31) + this.mStart;
            return (result * 31) + this.mEnd;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ModelParamRange other = (ModelParamRange) obj;
            if (this.mStart == other.mStart && this.mEnd == other.mEnd) {
                return true;
            }
            return false;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mStart);
            dest.writeInt(this.mEnd);
        }

        public String toString() {
            return "ModelParamRange [start=" + this.mStart + ", end=" + this.mEnd + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    /* loaded from: classes2.dex */
    public static class RecognitionEvent {
        public static final Parcelable.Creator<RecognitionEvent> CREATOR = new Parcelable.Creator<RecognitionEvent>() { // from class: android.hardware.soundtrigger.SoundTrigger.RecognitionEvent.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public RecognitionEvent createFromParcel(Parcel in) {
                return RecognitionEvent.fromParcel(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public RecognitionEvent[] newArray(int size) {
                return new RecognitionEvent[size];
            }
        };
        public final boolean captureAvailable;
        public final int captureDelayMs;
        public final AudioFormat captureFormat;
        public final int capturePreambleMs;
        public final int captureSession;
        public final byte[] data;
        public final boolean recognitionStillActive;
        public final int soundModelHandle;
        public final int status;
        public final boolean triggerInData;

        public RecognitionEvent(int status, int soundModelHandle, boolean captureAvailable, int captureSession, int captureDelayMs, int capturePreambleMs, boolean triggerInData, AudioFormat captureFormat, byte[] data) {
            this(status, soundModelHandle, captureAvailable, captureSession, captureDelayMs, capturePreambleMs, triggerInData, captureFormat, data, status == 3);
        }

        public RecognitionEvent(int status, int soundModelHandle, boolean captureAvailable, int captureSession, int captureDelayMs, int capturePreambleMs, boolean triggerInData, AudioFormat captureFormat, byte[] data, boolean recognitionStillActive) {
            this.status = status;
            this.soundModelHandle = soundModelHandle;
            this.captureAvailable = captureAvailable;
            this.captureSession = captureSession;
            this.captureDelayMs = captureDelayMs;
            this.capturePreambleMs = capturePreambleMs;
            this.triggerInData = triggerInData;
            this.captureFormat = (AudioFormat) Objects.requireNonNull(captureFormat);
            this.data = data != null ? data : new byte[0];
            this.recognitionStillActive = recognitionStillActive;
        }

        public boolean isCaptureAvailable() {
            return this.captureAvailable;
        }

        public AudioFormat getCaptureFormat() {
            return this.captureFormat;
        }

        public int getCaptureSession() {
            return this.captureSession;
        }

        public byte[] getData() {
            return this.data;
        }

        protected static RecognitionEvent fromParcel(Parcel in) {
            AudioFormat captureFormat;
            int status = in.readInt();
            int soundModelHandle = in.readInt();
            boolean captureAvailable = in.readByte() == 1;
            int captureSession = in.readInt();
            int captureDelayMs = in.readInt();
            int capturePreambleMs = in.readInt();
            boolean triggerInData = in.readByte() == 1;
            if (in.readByte() != 1) {
                captureFormat = null;
            } else {
                int sampleRate = in.readInt();
                int encoding = in.readInt();
                int channelMask = in.readInt();
                AudioFormat captureFormat2 = new AudioFormat.Builder().setChannelMask(channelMask).setEncoding(encoding).setSampleRate(sampleRate).build();
                captureFormat = captureFormat2;
            }
            byte[] data = in.readBlob();
            boolean recognitionStillActive = in.readBoolean();
            return new RecognitionEvent(status, soundModelHandle, captureAvailable, captureSession, captureDelayMs, capturePreambleMs, triggerInData, captureFormat, data, recognitionStillActive);
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.status);
            dest.writeInt(this.soundModelHandle);
            dest.writeByte(this.captureAvailable ? (byte) 1 : (byte) 0);
            dest.writeInt(this.captureSession);
            dest.writeInt(this.captureDelayMs);
            dest.writeInt(this.capturePreambleMs);
            dest.writeByte(this.triggerInData ? (byte) 1 : (byte) 0);
            if (this.captureFormat != null) {
                dest.writeByte((byte) 1);
                dest.writeInt(this.captureFormat.getSampleRate());
                dest.writeInt(this.captureFormat.getEncoding());
                dest.writeInt(this.captureFormat.getChannelMask());
            } else {
                dest.writeByte((byte) 0);
            }
            dest.writeBlob(this.data);
            dest.writeBoolean(this.recognitionStillActive);
        }

        public int hashCode() {
            int i = 1 * 31;
            boolean z = this.captureAvailable;
            int i2 = MetricsProto.MetricsEvent.AUTOFILL_SERVICE_DISABLED_APP;
            int result = i + (z ? 1231 : 1237);
            int result2 = ((((((result * 31) + this.captureDelayMs) * 31) + this.capturePreambleMs) * 31) + this.captureSession) * 31;
            if (!this.triggerInData) {
                i2 = 1237;
            }
            int result3 = result2 + i2;
            AudioFormat audioFormat = this.captureFormat;
            if (audioFormat != null) {
                result3 = (((((result3 * 31) + audioFormat.getSampleRate()) * 31) + this.captureFormat.getEncoding()) * 31) + this.captureFormat.getChannelMask();
            }
            return (((((result3 * 31) + Arrays.hashCode(this.data)) * 31) + this.soundModelHandle) * 31) + this.status + (this.recognitionStillActive ? MetricsProto.MetricsEvent.AUTOFILL_INVALID_PERMISSION : 1291);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            RecognitionEvent other = (RecognitionEvent) obj;
            if (this.captureAvailable != other.captureAvailable || this.captureDelayMs != other.captureDelayMs || this.capturePreambleMs != other.capturePreambleMs || this.captureSession != other.captureSession || !Arrays.equals(this.data, other.data) || this.recognitionStillActive != other.recognitionStillActive || this.soundModelHandle != other.soundModelHandle || this.status != other.status || this.triggerInData != other.triggerInData) {
                return false;
            }
            AudioFormat audioFormat = this.captureFormat;
            if (audioFormat == null) {
                if (other.captureFormat != null) {
                    return false;
                }
            } else if (other.captureFormat == null || audioFormat.getSampleRate() != other.captureFormat.getSampleRate() || this.captureFormat.getEncoding() != other.captureFormat.getEncoding() || this.captureFormat.getChannelMask() != other.captureFormat.getChannelMask()) {
                return false;
            }
            return true;
        }

        public String toString() {
            StringBuilder append = new StringBuilder().append("RecognitionEvent [status=").append(this.status).append(", soundModelHandle=").append(this.soundModelHandle).append(", captureAvailable=").append(this.captureAvailable).append(", captureSession=").append(this.captureSession).append(", captureDelayMs=").append(this.captureDelayMs).append(", capturePreambleMs=").append(this.capturePreambleMs).append(", triggerInData=").append(this.triggerInData).append(this.captureFormat == null ? "" : ", sampleRate=" + this.captureFormat.getSampleRate()).append(this.captureFormat == null ? "" : ", encoding=" + this.captureFormat.getEncoding()).append(this.captureFormat != null ? ", channelMask=" + this.captureFormat.getChannelMask() : "").append(", data=");
            byte[] bArr = this.data;
            return append.append(bArr == null ? 0 : bArr.length).append(", recognitionStillActive=").append(this.recognitionStillActive).append(NavigationBarInflaterView.SIZE_MOD_END).toString();
        }
    }

    /* loaded from: classes2.dex */
    public static class RecognitionConfig implements Parcelable {
        public static final Parcelable.Creator<RecognitionConfig> CREATOR = new Parcelable.Creator<RecognitionConfig>() { // from class: android.hardware.soundtrigger.SoundTrigger.RecognitionConfig.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public RecognitionConfig createFromParcel(Parcel in) {
                return RecognitionConfig.fromParcel(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public RecognitionConfig[] newArray(int size) {
                return new RecognitionConfig[size];
            }
        };
        public final boolean allowMultipleTriggers;
        public final int audioCapabilities;
        public final boolean captureRequested;
        public final byte[] data;
        public final KeyphraseRecognitionExtra[] keyphrases;

        public RecognitionConfig(boolean captureRequested, boolean allowMultipleTriggers, KeyphraseRecognitionExtra[] keyphrases, byte[] data, int audioCapabilities) {
            this.captureRequested = captureRequested;
            this.allowMultipleTriggers = allowMultipleTriggers;
            this.keyphrases = keyphrases != null ? keyphrases : new KeyphraseRecognitionExtra[0];
            this.data = data != null ? data : new byte[0];
            this.audioCapabilities = audioCapabilities;
        }

        public RecognitionConfig(boolean captureRequested, boolean allowMultipleTriggers, KeyphraseRecognitionExtra[] keyphrases, byte[] data) {
            this(captureRequested, allowMultipleTriggers, keyphrases, data, 0);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static RecognitionConfig fromParcel(Parcel in) {
            boolean captureRequested = in.readByte() == 1;
            boolean allowMultipleTriggers = in.readByte() == 1;
            KeyphraseRecognitionExtra[] keyphrases = (KeyphraseRecognitionExtra[]) in.createTypedArray(KeyphraseRecognitionExtra.CREATOR);
            byte[] data = in.readBlob();
            int audioCapabilities = in.readInt();
            return new RecognitionConfig(captureRequested, allowMultipleTriggers, keyphrases, data, audioCapabilities);
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(this.captureRequested ? (byte) 1 : (byte) 0);
            dest.writeByte(this.allowMultipleTriggers ? (byte) 1 : (byte) 0);
            dest.writeTypedArray(this.keyphrases, flags);
            dest.writeBlob(this.data);
            dest.writeInt(this.audioCapabilities);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public String toString() {
            return "RecognitionConfig [captureRequested=" + this.captureRequested + ", allowMultipleTriggers=" + this.allowMultipleTriggers + ", keyphrases=" + Arrays.toString(this.keyphrases) + ", data=" + Arrays.toString(this.data) + ", audioCapabilities=" + Integer.toHexString(this.audioCapabilities) + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    /* loaded from: classes2.dex */
    public static class ConfidenceLevel implements Parcelable {
        public static final Parcelable.Creator<ConfidenceLevel> CREATOR = new Parcelable.Creator<ConfidenceLevel>() { // from class: android.hardware.soundtrigger.SoundTrigger.ConfidenceLevel.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ConfidenceLevel createFromParcel(Parcel in) {
                return ConfidenceLevel.fromParcel(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ConfidenceLevel[] newArray(int size) {
                return new ConfidenceLevel[size];
            }
        };
        public final int confidenceLevel;
        public final int userId;

        public ConfidenceLevel(int userId, int confidenceLevel) {
            this.userId = userId;
            this.confidenceLevel = confidenceLevel;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static ConfidenceLevel fromParcel(Parcel in) {
            int userId = in.readInt();
            int confidenceLevel = in.readInt();
            return new ConfidenceLevel(userId, confidenceLevel);
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.userId);
            dest.writeInt(this.confidenceLevel);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public int hashCode() {
            int result = (1 * 31) + this.confidenceLevel;
            return (result * 31) + this.userId;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ConfidenceLevel other = (ConfidenceLevel) obj;
            if (this.confidenceLevel == other.confidenceLevel && this.userId == other.userId) {
                return true;
            }
            return false;
        }

        public String toString() {
            return "ConfidenceLevel [userId=" + this.userId + ", confidenceLevel=" + this.confidenceLevel + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    /* loaded from: classes2.dex */
    public static final class KeyphraseRecognitionExtra implements Parcelable {
        public static final Parcelable.Creator<KeyphraseRecognitionExtra> CREATOR = new Parcelable.Creator<KeyphraseRecognitionExtra>() { // from class: android.hardware.soundtrigger.SoundTrigger.KeyphraseRecognitionExtra.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public KeyphraseRecognitionExtra createFromParcel(Parcel in) {
                return KeyphraseRecognitionExtra.fromParcel(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public KeyphraseRecognitionExtra[] newArray(int size) {
                return new KeyphraseRecognitionExtra[size];
            }
        };
        public final int coarseConfidenceLevel;
        public final ConfidenceLevel[] confidenceLevels;

        /* renamed from: id */
        public final int f214id;
        public final int recognitionModes;

        public KeyphraseRecognitionExtra(int id, int recognitionModes, int coarseConfidenceLevel) {
            this(id, recognitionModes, coarseConfidenceLevel, new ConfidenceLevel[0]);
        }

        public KeyphraseRecognitionExtra(int id, int recognitionModes, int coarseConfidenceLevel, ConfidenceLevel[] confidenceLevels) {
            this.f214id = id;
            this.recognitionModes = recognitionModes;
            this.coarseConfidenceLevel = coarseConfidenceLevel;
            this.confidenceLevels = confidenceLevels != null ? confidenceLevels : new ConfidenceLevel[0];
        }

        public int getKeyphraseId() {
            return this.f214id;
        }

        public int getRecognitionModes() {
            return this.recognitionModes;
        }

        public int getCoarseConfidenceLevel() {
            return this.coarseConfidenceLevel;
        }

        public Collection<ConfidenceLevel> getConfidenceLevels() {
            return Arrays.asList(this.confidenceLevels);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static KeyphraseRecognitionExtra fromParcel(Parcel in) {
            int id = in.readInt();
            int recognitionModes = in.readInt();
            int coarseConfidenceLevel = in.readInt();
            ConfidenceLevel[] confidenceLevels = (ConfidenceLevel[]) in.createTypedArray(ConfidenceLevel.CREATOR);
            return new KeyphraseRecognitionExtra(id, recognitionModes, coarseConfidenceLevel, confidenceLevels);
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.f214id);
            dest.writeInt(this.recognitionModes);
            dest.writeInt(this.coarseConfidenceLevel);
            dest.writeTypedArray(this.confidenceLevels, flags);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public int hashCode() {
            int result = (1 * 31) + Arrays.hashCode(this.confidenceLevels);
            return (((((result * 31) + this.f214id) * 31) + this.recognitionModes) * 31) + this.coarseConfidenceLevel;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            KeyphraseRecognitionExtra other = (KeyphraseRecognitionExtra) obj;
            if (Arrays.equals(this.confidenceLevels, other.confidenceLevels) && this.f214id == other.f214id && this.recognitionModes == other.recognitionModes && this.coarseConfidenceLevel == other.coarseConfidenceLevel) {
                return true;
            }
            return false;
        }

        public String toString() {
            return "KeyphraseRecognitionExtra [id=" + this.f214id + ", recognitionModes=" + this.recognitionModes + ", coarseConfidenceLevel=" + this.coarseConfidenceLevel + ", confidenceLevels=" + Arrays.toString(this.confidenceLevels) + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    /* loaded from: classes2.dex */
    public static class KeyphraseRecognitionEvent extends RecognitionEvent implements Parcelable {
        public static final Parcelable.Creator<KeyphraseRecognitionEvent> CREATOR = new Parcelable.Creator<KeyphraseRecognitionEvent>() { // from class: android.hardware.soundtrigger.SoundTrigger.KeyphraseRecognitionEvent.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public KeyphraseRecognitionEvent createFromParcel(Parcel in) {
                return KeyphraseRecognitionEvent.fromParcelForKeyphrase(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public KeyphraseRecognitionEvent[] newArray(int size) {
                return new KeyphraseRecognitionEvent[size];
            }
        };
        public final KeyphraseRecognitionExtra[] keyphraseExtras;

        public KeyphraseRecognitionEvent(int status, int soundModelHandle, boolean captureAvailable, int captureSession, int captureDelayMs, int capturePreambleMs, boolean triggerInData, AudioFormat captureFormat, byte[] data, KeyphraseRecognitionExtra[] keyphraseExtras) {
            this(status, soundModelHandle, captureAvailable, captureSession, captureDelayMs, capturePreambleMs, triggerInData, captureFormat, data, keyphraseExtras, status == 3);
        }

        public KeyphraseRecognitionEvent(int status, int soundModelHandle, boolean captureAvailable, int captureSession, int captureDelayMs, int capturePreambleMs, boolean triggerInData, AudioFormat captureFormat, byte[] data, KeyphraseRecognitionExtra[] keyphraseExtras, boolean recognitionStillActive) {
            super(status, soundModelHandle, captureAvailable, captureSession, captureDelayMs, capturePreambleMs, triggerInData, captureFormat, data, recognitionStillActive);
            this.keyphraseExtras = keyphraseExtras != null ? keyphraseExtras : new KeyphraseRecognitionExtra[0];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static KeyphraseRecognitionEvent fromParcelForKeyphrase(Parcel in) {
            AudioFormat captureFormat;
            int status = in.readInt();
            int soundModelHandle = in.readInt();
            boolean captureAvailable = in.readByte() == 1;
            int captureSession = in.readInt();
            int captureDelayMs = in.readInt();
            int capturePreambleMs = in.readInt();
            boolean triggerInData = in.readByte() == 1;
            if (in.readByte() != 1) {
                captureFormat = null;
            } else {
                int sampleRate = in.readInt();
                int encoding = in.readInt();
                int channelMask = in.readInt();
                AudioFormat captureFormat2 = new AudioFormat.Builder().setChannelMask(channelMask).setEncoding(encoding).setSampleRate(sampleRate).build();
                captureFormat = captureFormat2;
            }
            byte[] data = in.readBlob();
            boolean recognitionStillActive = in.readBoolean();
            KeyphraseRecognitionExtra[] keyphraseExtras = (KeyphraseRecognitionExtra[]) in.createTypedArray(KeyphraseRecognitionExtra.CREATOR);
            return new KeyphraseRecognitionEvent(status, soundModelHandle, captureAvailable, captureSession, captureDelayMs, capturePreambleMs, triggerInData, captureFormat, data, keyphraseExtras, recognitionStillActive);
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.RecognitionEvent, android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.status);
            dest.writeInt(this.soundModelHandle);
            dest.writeByte(this.captureAvailable ? (byte) 1 : (byte) 0);
            dest.writeInt(this.captureSession);
            dest.writeInt(this.captureDelayMs);
            dest.writeInt(this.capturePreambleMs);
            dest.writeByte(this.triggerInData ? (byte) 1 : (byte) 0);
            if (this.captureFormat != null) {
                dest.writeByte((byte) 1);
                dest.writeInt(this.captureFormat.getSampleRate());
                dest.writeInt(this.captureFormat.getEncoding());
                dest.writeInt(this.captureFormat.getChannelMask());
            } else {
                dest.writeByte((byte) 0);
            }
            dest.writeBlob(this.data);
            dest.writeBoolean(this.recognitionStillActive);
            dest.writeTypedArray(this.keyphraseExtras, flags);
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.RecognitionEvent, android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.RecognitionEvent
        public int hashCode() {
            int result = super.hashCode();
            return (result * 31) + Arrays.hashCode(this.keyphraseExtras);
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.RecognitionEvent
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (super.equals(obj) && getClass() == obj.getClass()) {
                KeyphraseRecognitionEvent other = (KeyphraseRecognitionEvent) obj;
                return Arrays.equals(this.keyphraseExtras, other.keyphraseExtras);
            }
            return false;
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.RecognitionEvent
        public String toString() {
            return "KeyphraseRecognitionEvent [keyphraseExtras=" + Arrays.toString(this.keyphraseExtras) + ", status=" + this.status + ", soundModelHandle=" + this.soundModelHandle + ", captureAvailable=" + this.captureAvailable + ", captureSession=" + this.captureSession + ", captureDelayMs=" + this.captureDelayMs + ", capturePreambleMs=" + this.capturePreambleMs + ", triggerInData=" + this.triggerInData + (this.captureFormat == null ? "" : ", sampleRate=" + this.captureFormat.getSampleRate()) + (this.captureFormat == null ? "" : ", encoding=" + this.captureFormat.getEncoding()) + (this.captureFormat != null ? ", channelMask=" + this.captureFormat.getChannelMask() : "") + ", data=" + (this.data == null ? 0 : this.data.length) + ", recognitionStillActive=" + this.recognitionStillActive + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    /* loaded from: classes2.dex */
    public static class GenericRecognitionEvent extends RecognitionEvent implements Parcelable {
        public static final Parcelable.Creator<GenericRecognitionEvent> CREATOR = new Parcelable.Creator<GenericRecognitionEvent>() { // from class: android.hardware.soundtrigger.SoundTrigger.GenericRecognitionEvent.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public GenericRecognitionEvent createFromParcel(Parcel in) {
                return GenericRecognitionEvent.fromParcelForGeneric(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public GenericRecognitionEvent[] newArray(int size) {
                return new GenericRecognitionEvent[size];
            }
        };

        public GenericRecognitionEvent(int status, int soundModelHandle, boolean captureAvailable, int captureSession, int captureDelayMs, int capturePreambleMs, boolean triggerInData, AudioFormat captureFormat, byte[] data) {
            this(status, soundModelHandle, captureAvailable, captureSession, captureDelayMs, capturePreambleMs, triggerInData, captureFormat, data, status == 3);
        }

        public GenericRecognitionEvent(int status, int soundModelHandle, boolean captureAvailable, int captureSession, int captureDelayMs, int capturePreambleMs, boolean triggerInData, AudioFormat captureFormat, byte[] data, boolean recognitionStillActive) {
            super(status, soundModelHandle, captureAvailable, captureSession, captureDelayMs, capturePreambleMs, triggerInData, captureFormat, data, recognitionStillActive);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static GenericRecognitionEvent fromParcelForGeneric(Parcel in) {
            RecognitionEvent event = RecognitionEvent.fromParcel(in);
            return new GenericRecognitionEvent(event.status, event.soundModelHandle, event.captureAvailable, event.captureSession, event.captureDelayMs, event.capturePreambleMs, event.triggerInData, event.captureFormat, event.data, event.recognitionStillActive);
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.RecognitionEvent
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            RecognitionEvent recognitionEvent = (RecognitionEvent) obj;
            return super.equals(obj);
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.RecognitionEvent
        public String toString() {
            return "GenericRecognitionEvent ::" + super.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int handleException(Exception e) {
        Log.m103w(TAG, "Exception caught", e);
        if (e instanceof RemoteException) {
            return STATUS_DEAD_OBJECT;
        }
        if (e instanceof ServiceSpecificException) {
            switch (((ServiceSpecificException) e).errorCode) {
                case 1:
                    return STATUS_BUSY;
                case 2:
                    return STATUS_INVALID_OPERATION;
                case 3:
                    return STATUS_PERMISSION_DENIED;
                case 4:
                    return STATUS_DEAD_OBJECT;
                case 5:
                    return Integer.MIN_VALUE;
                default:
                    return Integer.MIN_VALUE;
            }
        } else if (e instanceof SecurityException) {
            return STATUS_PERMISSION_DENIED;
        } else {
            if (e instanceof IllegalStateException) {
                return STATUS_INVALID_OPERATION;
            }
            if ((e instanceof IllegalArgumentException) || (e instanceof NullPointerException)) {
                return STATUS_BAD_VALUE;
            }
            Log.m109e(TAG, "Escalating unexpected exception: ", e);
            throw new RuntimeException(e);
        }
    }

    public static int listModules(ArrayList<ModuleProperties> modules) {
        return 0;
    }

    @Deprecated
    public static int listModulesAsOriginator(ArrayList<ModuleProperties> modules, Identity originatorIdentity) {
        try {
            SoundTriggerModuleDescriptor[] descs = getService().listModulesAsOriginator(originatorIdentity);
            convertDescriptorsToModuleProperties(descs, modules);
            return 0;
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Deprecated
    public static int listModulesAsMiddleman(ArrayList<ModuleProperties> modules, Identity middlemanIdentity, Identity originatorIdentity) {
        try {
            SoundTriggerModuleDescriptor[] descs = getService().listModulesAsMiddleman(middlemanIdentity, originatorIdentity);
            convertDescriptorsToModuleProperties(descs, modules);
            return 0;
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private static void convertDescriptorsToModuleProperties(SoundTriggerModuleDescriptor[] descsIn, ArrayList<ModuleProperties> modulesOut) {
        modulesOut.clear();
        modulesOut.ensureCapacity(descsIn.length);
        for (SoundTriggerModuleDescriptor desc : descsIn) {
            modulesOut.add(ConversionUtil.aidl2apiModuleDescriptor(desc));
        }
    }

    private static SoundTriggerModule attachModule(int moduleId, StatusListener listener, Handler handler) {
        return null;
    }

    @Deprecated
    public static SoundTriggerModule attachModuleAsMiddleman(int moduleId, StatusListener listener, Handler handler, Identity middlemanIdentity, Identity originatorIdentity) {
        Looper looper = handler != null ? handler.getLooper() : Looper.getMainLooper();
        try {
            return new SoundTriggerModule(getService(), moduleId, listener, looper, middlemanIdentity, originatorIdentity);
        } catch (Exception e) {
            Log.m109e(TAG, "", e);
            return null;
        }
    }

    public static SoundTriggerModule attachModuleAsOriginator(int moduleId, StatusListener listener, Handler handler, Identity originatorIdentity) {
        Looper looper = handler != null ? handler.getLooper() : Looper.getMainLooper();
        try {
            return new SoundTriggerModule(getService(), moduleId, listener, looper, originatorIdentity);
        } catch (Exception e) {
            Log.m109e(TAG, "", e);
            return null;
        }
    }

    private static ISoundTriggerMiddlewareService getService() {
        ISoundTriggerMiddlewareService asInterface;
        synchronized (mServiceLock) {
            while (true) {
                try {
                    IBinder binder = ServiceManager.getServiceOrThrow(Context.SOUND_TRIGGER_MIDDLEWARE_SERVICE);
                    asInterface = ISoundTriggerMiddlewareService.Stub.asInterface(binder);
                } catch (Exception e) {
                    try {
                        Log.m109e(TAG, "Failed to bind to soundtrigger service", e);
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        }
        return asInterface;
    }
}
