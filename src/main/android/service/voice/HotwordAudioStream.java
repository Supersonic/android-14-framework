package android.service.voice;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.media.AudioFormat;
import android.media.AudioTimestamp;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import com.android.internal.util.AnnotationValidations;
import java.util.Arrays;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class HotwordAudioStream implements Parcelable {
    public static final String KEY_AUDIO_STREAM_COPY_BUFFER_LENGTH_BYTES = "android.service.voice.key.AUDIO_STREAM_COPY_BUFFER_LENGTH_BYTES";
    private final AudioFormat mAudioFormat;
    private final ParcelFileDescriptor mAudioStreamParcelFileDescriptor;
    private final byte[] mInitialAudio;
    private final PersistableBundle mMetadata;
    private final AudioTimestamp mTimestamp;
    private static final byte[] DEFAULT_INITIAL_EMPTY_AUDIO = new byte[0];
    public static final Parcelable.Creator<HotwordAudioStream> CREATOR = new Parcelable.Creator<HotwordAudioStream>() { // from class: android.service.voice.HotwordAudioStream.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HotwordAudioStream[] newArray(int size) {
            return new HotwordAudioStream[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HotwordAudioStream createFromParcel(Parcel in) {
            return new HotwordAudioStream(in);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public static AudioTimestamp defaultTimestamp() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PersistableBundle defaultMetadata() {
        return new PersistableBundle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] defaultInitialAudio() {
        return DEFAULT_INITIAL_EMPTY_AUDIO;
    }

    private String initialAudioToString() {
        return "length=" + this.mInitialAudio.length;
    }

    public Builder buildUpon() {
        return new Builder(this.mAudioFormat, this.mAudioStreamParcelFileDescriptor).setTimestamp(this.mTimestamp).setMetadata(this.mMetadata).setInitialAudio(this.mInitialAudio);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static abstract class BaseBuilder {
        BaseBuilder() {
        }

        public Builder setInitialAudio(byte[] value) {
            Objects.requireNonNull(value, "value should not be null");
            Builder builder = (Builder) this;
            builder.mBuilderFieldsSet |= 16;
            builder.mInitialAudio = value;
            return builder;
        }
    }

    HotwordAudioStream(AudioFormat audioFormat, ParcelFileDescriptor audioStreamParcelFileDescriptor, AudioTimestamp timestamp, PersistableBundle metadata, byte[] initialAudio) {
        this.mAudioFormat = audioFormat;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) audioFormat);
        this.mAudioStreamParcelFileDescriptor = audioStreamParcelFileDescriptor;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) audioStreamParcelFileDescriptor);
        this.mTimestamp = timestamp;
        this.mMetadata = metadata;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) metadata);
        this.mInitialAudio = initialAudio;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) initialAudio);
    }

    public AudioFormat getAudioFormat() {
        return this.mAudioFormat;
    }

    public ParcelFileDescriptor getAudioStreamParcelFileDescriptor() {
        return this.mAudioStreamParcelFileDescriptor;
    }

    public AudioTimestamp getTimestamp() {
        return this.mTimestamp;
    }

    public PersistableBundle getMetadata() {
        return this.mMetadata;
    }

    public byte[] getInitialAudio() {
        return this.mInitialAudio;
    }

    public String toString() {
        return "HotwordAudioStream { audioFormat = " + this.mAudioFormat + ", audioStreamParcelFileDescriptor = " + this.mAudioStreamParcelFileDescriptor + ", timestamp = " + this.mTimestamp + ", metadata = " + this.mMetadata + ", initialAudio = " + initialAudioToString() + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HotwordAudioStream that = (HotwordAudioStream) o;
        if (Objects.equals(this.mAudioFormat, that.mAudioFormat) && Objects.equals(this.mAudioStreamParcelFileDescriptor, that.mAudioStreamParcelFileDescriptor) && Objects.equals(this.mTimestamp, that.mTimestamp) && Objects.equals(this.mMetadata, that.mMetadata) && Arrays.equals(this.mInitialAudio, that.mInitialAudio)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mAudioFormat);
        return (((((((_hash * 31) + Objects.hashCode(this.mAudioStreamParcelFileDescriptor)) * 31) + Objects.hashCode(this.mTimestamp)) * 31) + Objects.hashCode(this.mMetadata)) * 31) + Arrays.hashCode(this.mInitialAudio);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mTimestamp != null ? (byte) (0 | 4) : (byte) 0;
        dest.writeByte(flg);
        dest.writeTypedObject(this.mAudioFormat, flags);
        dest.writeTypedObject(this.mAudioStreamParcelFileDescriptor, flags);
        AudioTimestamp audioTimestamp = this.mTimestamp;
        if (audioTimestamp != null) {
            dest.writeTypedObject(audioTimestamp, flags);
        }
        dest.writeTypedObject(this.mMetadata, flags);
        dest.writeByteArray(this.mInitialAudio);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    HotwordAudioStream(Parcel in) {
        byte flg = in.readByte();
        AudioFormat audioFormat = (AudioFormat) in.readTypedObject(AudioFormat.CREATOR);
        ParcelFileDescriptor audioStreamParcelFileDescriptor = (ParcelFileDescriptor) in.readTypedObject(ParcelFileDescriptor.CREATOR);
        AudioTimestamp timestamp = (flg & 4) == 0 ? null : (AudioTimestamp) in.readTypedObject(AudioTimestamp.CREATOR);
        PersistableBundle metadata = (PersistableBundle) in.readTypedObject(PersistableBundle.CREATOR);
        byte[] initialAudio = in.createByteArray();
        this.mAudioFormat = audioFormat;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) audioFormat);
        this.mAudioStreamParcelFileDescriptor = audioStreamParcelFileDescriptor;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) audioStreamParcelFileDescriptor);
        this.mTimestamp = timestamp;
        this.mMetadata = metadata;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) metadata);
        this.mInitialAudio = initialAudio;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) initialAudio);
    }

    /* loaded from: classes3.dex */
    public static final class Builder extends BaseBuilder {
        private AudioFormat mAudioFormat;
        private ParcelFileDescriptor mAudioStreamParcelFileDescriptor;
        private long mBuilderFieldsSet = 0;
        private byte[] mInitialAudio;
        private PersistableBundle mMetadata;
        private AudioTimestamp mTimestamp;

        @Override // android.service.voice.HotwordAudioStream.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setInitialAudio(byte[] bArr) {
            return super.setInitialAudio(bArr);
        }

        public Builder(AudioFormat audioFormat, ParcelFileDescriptor audioStreamParcelFileDescriptor) {
            this.mAudioFormat = audioFormat;
            AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) audioFormat);
            this.mAudioStreamParcelFileDescriptor = audioStreamParcelFileDescriptor;
            AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) audioStreamParcelFileDescriptor);
        }

        public Builder setAudioFormat(AudioFormat value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mAudioFormat = value;
            return this;
        }

        public Builder setAudioStreamParcelFileDescriptor(ParcelFileDescriptor value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mAudioStreamParcelFileDescriptor = value;
            return this;
        }

        public Builder setTimestamp(AudioTimestamp value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mTimestamp = value;
            return this;
        }

        public Builder setMetadata(PersistableBundle value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 8;
            this.mMetadata = value;
            return this;
        }

        public HotwordAudioStream build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 32;
            this.mBuilderFieldsSet = j;
            if ((j & 4) == 0) {
                this.mTimestamp = HotwordAudioStream.defaultTimestamp();
            }
            if ((this.mBuilderFieldsSet & 8) == 0) {
                this.mMetadata = HotwordAudioStream.defaultMetadata();
            }
            if ((this.mBuilderFieldsSet & 16) == 0) {
                this.mInitialAudio = HotwordAudioStream.defaultInitialAudio();
            }
            HotwordAudioStream o = new HotwordAudioStream(this.mAudioFormat, this.mAudioStreamParcelFileDescriptor, this.mTimestamp, this.mMetadata, this.mInitialAudio);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 32) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    @Deprecated
    private void __metadata() {
    }
}
