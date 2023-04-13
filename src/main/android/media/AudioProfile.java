package android.media;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public class AudioProfile implements Parcelable {
    public static final int AUDIO_ENCAPSULATION_TYPE_IEC61937 = 1;
    public static final int AUDIO_ENCAPSULATION_TYPE_NONE = 0;
    public static final int AUDIO_ENCAPSULATION_TYPE_PCM = 2;
    public static final Parcelable.Creator<AudioProfile> CREATOR = new Parcelable.Creator<AudioProfile>() { // from class: android.media.AudioProfile.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioProfile createFromParcel(Parcel p) {
            return new AudioProfile(p);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioProfile[] newArray(int size) {
            return new AudioProfile[size];
        }
    };
    private final int[] mChannelIndexMasks;
    private final int[] mChannelMasks;
    private final int mEncapsulationType;
    private final int mFormat;
    private final int[] mSamplingRates;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface EncapsulationType {
    }

    @SystemApi
    public AudioProfile(int format, int[] samplingRates, int[] channelMasks, int[] channelIndexMasks, int encapsulationType) {
        this.mFormat = format;
        this.mSamplingRates = samplingRates;
        this.mChannelMasks = channelMasks;
        this.mChannelIndexMasks = channelIndexMasks;
        this.mEncapsulationType = encapsulationType;
    }

    public int getFormat() {
        return this.mFormat;
    }

    public int[] getChannelMasks() {
        return this.mChannelMasks;
    }

    public int[] getChannelIndexMasks() {
        return this.mChannelIndexMasks;
    }

    public int[] getSampleRates() {
        return this.mSamplingRates;
    }

    public int getEncapsulationType() {
        return this.mEncapsulationType;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mFormat), Integer.valueOf(Arrays.hashCode(this.mSamplingRates)), Integer.valueOf(Arrays.hashCode(this.mChannelMasks)), Integer.valueOf(Arrays.hashCode(this.mChannelIndexMasks)), Integer.valueOf(this.mEncapsulationType));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioProfile that = (AudioProfile) o;
        if (this.mFormat == that.mFormat && hasIdenticalElements(this.mSamplingRates, that.mSamplingRates) && hasIdenticalElements(this.mChannelMasks, that.mChannelMasks) && hasIdenticalElements(this.mChannelIndexMasks, that.mChannelIndexMasks) && this.mEncapsulationType == that.mEncapsulationType) {
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append(AudioFormat.toLogFriendlyEncoding(this.mFormat));
        int[] iArr = this.mSamplingRates;
        if (iArr != null && iArr.length > 0) {
            sb.append(", sampling rates=").append(Arrays.toString(this.mSamplingRates));
        }
        int[] iArr2 = this.mChannelMasks;
        if (iArr2 != null && iArr2.length > 0) {
            sb.append(", channel masks=").append(toHexString(this.mChannelMasks));
        }
        int[] iArr3 = this.mChannelIndexMasks;
        if (iArr3 != null && iArr3.length > 0) {
            sb.append(", channel index masks=").append(Arrays.toString(this.mChannelIndexMasks));
        }
        sb.append(", encapsulation type=" + this.mEncapsulationType);
        sb.append("}");
        return sb.toString();
    }

    private static String toHexString(int[] ints) {
        if (ints == null || ints.length == 0) {
            return "";
        }
        return (String) Arrays.stream(ints).mapToObj(new IntFunction() { // from class: android.media.AudioProfile$$ExternalSyntheticLambda0
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                String format;
                format = String.format("0x%02X", Integer.valueOf(i));
                return format;
            }
        }).collect(Collectors.joining(", "));
    }

    private static boolean hasIdenticalElements(int[] array1, int[] array2) {
        int[] sortedArray1 = Arrays.copyOf(array1, array1.length);
        Arrays.sort(sortedArray1);
        int[] sortedArray2 = Arrays.copyOf(array2, array2.length);
        Arrays.sort(sortedArray2);
        return Arrays.equals(sortedArray1, sortedArray2);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mFormat);
        dest.writeIntArray(this.mSamplingRates);
        dest.writeIntArray(this.mChannelMasks);
        dest.writeIntArray(this.mChannelIndexMasks);
        dest.writeInt(this.mEncapsulationType);
    }

    private AudioProfile(Parcel in) {
        this.mFormat = in.readInt();
        this.mSamplingRates = in.createIntArray();
        this.mChannelMasks = in.createIntArray();
        this.mChannelIndexMasks = in.createIntArray();
        this.mEncapsulationType = in.readInt();
    }
}
