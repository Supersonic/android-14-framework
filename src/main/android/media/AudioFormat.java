package android.media;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class AudioFormat implements Parcelable {
    public static final int AUDIO_FORMAT_HAS_PROPERTY_CHANNEL_INDEX_MASK = 8;
    public static final int AUDIO_FORMAT_HAS_PROPERTY_CHANNEL_MASK = 4;
    public static final int AUDIO_FORMAT_HAS_PROPERTY_ENCODING = 1;
    public static final int AUDIO_FORMAT_HAS_PROPERTY_NONE = 0;
    public static final int AUDIO_FORMAT_HAS_PROPERTY_SAMPLE_RATE = 2;
    @Deprecated
    public static final int CHANNEL_CONFIGURATION_DEFAULT = 1;
    @Deprecated
    public static final int CHANNEL_CONFIGURATION_INVALID = 0;
    @Deprecated
    public static final int CHANNEL_CONFIGURATION_MONO = 2;
    @Deprecated
    public static final int CHANNEL_CONFIGURATION_STEREO = 3;
    public static final int CHANNEL_INVALID = 0;
    public static final int CHANNEL_IN_2POINT0POINT2 = 6291468;
    public static final int CHANNEL_IN_2POINT1POINT2 = 7340044;
    public static final int CHANNEL_IN_3POINT0POINT2 = 6553612;
    public static final int CHANNEL_IN_3POINT1POINT2 = 7602188;
    public static final int CHANNEL_IN_5POINT1 = 1507340;
    public static final int CHANNEL_IN_BACK = 32;
    public static final int CHANNEL_IN_BACK_LEFT = 65536;
    public static final int CHANNEL_IN_BACK_PROCESSED = 512;
    public static final int CHANNEL_IN_BACK_RIGHT = 131072;
    public static final int CHANNEL_IN_CENTER = 262144;
    public static final int CHANNEL_IN_DEFAULT = 1;
    public static final int CHANNEL_IN_FRONT = 16;
    public static final int CHANNEL_IN_FRONT_BACK = 48;
    public static final int CHANNEL_IN_FRONT_PROCESSED = 256;
    public static final int CHANNEL_IN_LEFT = 4;
    public static final int CHANNEL_IN_LEFT_PROCESSED = 64;
    public static final int CHANNEL_IN_LOW_FREQUENCY = 1048576;
    public static final int CHANNEL_IN_MONO = 16;
    public static final int CHANNEL_IN_PRESSURE = 1024;
    public static final int CHANNEL_IN_RIGHT = 8;
    public static final int CHANNEL_IN_RIGHT_PROCESSED = 128;
    public static final int CHANNEL_IN_STEREO = 12;
    public static final int CHANNEL_IN_TOP_LEFT = 2097152;
    public static final int CHANNEL_IN_TOP_RIGHT = 4194304;
    public static final int CHANNEL_IN_VOICE_DNLINK = 32768;
    public static final int CHANNEL_IN_VOICE_UPLINK = 16384;
    public static final int CHANNEL_IN_X_AXIS = 2048;
    public static final int CHANNEL_IN_Y_AXIS = 4096;
    public static final int CHANNEL_IN_Z_AXIS = 8192;
    public static final int CHANNEL_OUT_13POINT_360RA = 30136348;
    public static final int CHANNEL_OUT_22POINT2 = 67108860;
    public static final int CHANNEL_OUT_5POINT1 = 252;
    public static final int CHANNEL_OUT_5POINT1POINT2 = 3145980;
    public static final int CHANNEL_OUT_5POINT1POINT4 = 737532;
    public static final int CHANNEL_OUT_5POINT1_SIDE = 6204;
    public static final int CHANNEL_OUT_6POINT1 = 1276;
    @Deprecated
    public static final int CHANNEL_OUT_7POINT1 = 1020;
    public static final int CHANNEL_OUT_7POINT1POINT2 = 3152124;
    public static final int CHANNEL_OUT_7POINT1POINT4 = 743676;
    public static final int CHANNEL_OUT_7POINT1_SURROUND = 6396;
    public static final int CHANNEL_OUT_9POINT1POINT4 = 202070268;
    public static final int CHANNEL_OUT_9POINT1POINT6 = 205215996;
    public static final int CHANNEL_OUT_BACK_CENTER = 1024;
    public static final int CHANNEL_OUT_BACK_LEFT = 64;
    public static final int CHANNEL_OUT_BACK_RIGHT = 128;
    public static final int CHANNEL_OUT_BOTTOM_FRONT_CENTER = 8388608;
    public static final int CHANNEL_OUT_BOTTOM_FRONT_LEFT = 4194304;
    public static final int CHANNEL_OUT_BOTTOM_FRONT_RIGHT = 16777216;
    public static final int CHANNEL_OUT_DEFAULT = 1;
    public static final int CHANNEL_OUT_FRONT_CENTER = 16;
    public static final int CHANNEL_OUT_FRONT_LEFT = 4;
    public static final int CHANNEL_OUT_FRONT_LEFT_OF_CENTER = 256;
    public static final int CHANNEL_OUT_FRONT_RIGHT = 8;
    public static final int CHANNEL_OUT_FRONT_RIGHT_OF_CENTER = 512;
    public static final int CHANNEL_OUT_FRONT_WIDE_LEFT = 67108864;
    public static final int CHANNEL_OUT_FRONT_WIDE_RIGHT = 134217728;
    public static final int CHANNEL_OUT_HAPTIC_A = 536870912;
    public static final int CHANNEL_OUT_HAPTIC_B = 268435456;
    public static final int CHANNEL_OUT_LOW_FREQUENCY = 32;
    public static final int CHANNEL_OUT_LOW_FREQUENCY_2 = 33554432;
    public static final int CHANNEL_OUT_MONO = 4;
    public static final int CHANNEL_OUT_QUAD = 204;
    public static final int CHANNEL_OUT_QUAD_SIDE = 6156;
    public static final int CHANNEL_OUT_SIDE_LEFT = 2048;
    public static final int CHANNEL_OUT_SIDE_RIGHT = 4096;
    public static final int CHANNEL_OUT_STEREO = 12;
    public static final int CHANNEL_OUT_SURROUND = 1052;
    public static final int CHANNEL_OUT_TOP_BACK_CENTER = 262144;
    public static final int CHANNEL_OUT_TOP_BACK_LEFT = 131072;
    public static final int CHANNEL_OUT_TOP_BACK_RIGHT = 524288;
    public static final int CHANNEL_OUT_TOP_CENTER = 8192;
    public static final int CHANNEL_OUT_TOP_FRONT_CENTER = 32768;
    public static final int CHANNEL_OUT_TOP_FRONT_LEFT = 16384;
    public static final int CHANNEL_OUT_TOP_FRONT_RIGHT = 65536;
    public static final int CHANNEL_OUT_TOP_SIDE_LEFT = 1048576;
    public static final int CHANNEL_OUT_TOP_SIDE_RIGHT = 2097152;
    public static final int ENCODING_AAC_ELD = 15;
    public static final int ENCODING_AAC_HE_V1 = 11;
    public static final int ENCODING_AAC_HE_V2 = 12;
    public static final int ENCODING_AAC_LC = 10;
    public static final int ENCODING_AAC_XHE = 16;
    public static final int ENCODING_AC3 = 5;
    public static final int ENCODING_AC4 = 17;
    public static final int ENCODING_DEFAULT = 1;
    public static final int ENCODING_DOLBY_MAT = 19;
    public static final int ENCODING_DOLBY_TRUEHD = 14;
    public static final int ENCODING_DRA = 28;
    public static final int ENCODING_DSD = 31;
    public static final int ENCODING_DTS = 7;
    public static final int ENCODING_DTS_HD = 8;
    public static final int ENCODING_DTS_HD_MA = 29;
    @Deprecated
    public static final int ENCODING_DTS_UHD = 27;
    public static final int ENCODING_DTS_UHD_P1 = 27;
    public static final int ENCODING_DTS_UHD_P2 = 30;
    public static final int ENCODING_E_AC3 = 6;
    public static final int ENCODING_E_AC3_JOC = 18;
    public static final int ENCODING_IEC61937 = 13;
    public static final int ENCODING_INVALID = 0;
    public static final int ENCODING_LEGACY_SHORT_ARRAY_THRESHOLD = 20;
    public static final int ENCODING_MP3 = 9;
    public static final int ENCODING_MPEGH_BL_L3 = 23;
    public static final int ENCODING_MPEGH_BL_L4 = 24;
    public static final int ENCODING_MPEGH_LC_L3 = 25;
    public static final int ENCODING_MPEGH_LC_L4 = 26;
    public static final int ENCODING_OPUS = 20;
    public static final int ENCODING_PCM_16BIT = 2;
    public static final int ENCODING_PCM_24BIT_PACKED = 21;
    public static final int ENCODING_PCM_32BIT = 22;
    public static final int ENCODING_PCM_8BIT = 3;
    public static final int ENCODING_PCM_FLOAT = 4;
    public static final int SAMPLE_RATE_UNSPECIFIED = 0;
    private final int mChannelCount;
    private final int mChannelIndexMask;
    private final int mChannelMask;
    private final int mEncoding;
    private final int mFrameSizeInBytes;
    private final int mPropertySetMask;
    private final int mSampleRate;
    public static final int SAMPLE_RATE_HZ_MIN = AudioSystem.SAMPLE_RATE_HZ_MIN;
    public static final int SAMPLE_RATE_HZ_MAX = AudioSystem.SAMPLE_RATE_HZ_MAX;
    public static final Parcelable.Creator<AudioFormat> CREATOR = new Parcelable.Creator<AudioFormat>() { // from class: android.media.AudioFormat.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioFormat createFromParcel(Parcel p) {
            return new AudioFormat(p);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioFormat[] newArray(int size) {
            return new AudioFormat[size];
        }
    };
    public static final int[] SURROUND_SOUND_ENCODING = {5, 6, 7, 8, 10, 14, 17, 18, 19, 23, 24, 25, 26, 27, 28, 29, 30};

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface ChannelOut {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Encoding {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface SurroundSoundEncoding {
    }

    public static String toLogFriendlyEncoding(int enc) {
        switch (enc) {
            case 0:
                return "ENCODING_INVALID";
            case 1:
            default:
                return "invalid encoding " + enc;
            case 2:
                return "ENCODING_PCM_16BIT";
            case 3:
                return "ENCODING_PCM_8BIT";
            case 4:
                return "ENCODING_PCM_FLOAT";
            case 5:
                return "ENCODING_AC3";
            case 6:
                return "ENCODING_E_AC3";
            case 7:
                return "ENCODING_DTS";
            case 8:
                return "ENCODING_DTS_HD";
            case 9:
                return "ENCODING_MP3";
            case 10:
                return "ENCODING_AAC_LC";
            case 11:
                return "ENCODING_AAC_HE_V1";
            case 12:
                return "ENCODING_AAC_HE_V2";
            case 13:
                return "ENCODING_IEC61937";
            case 14:
                return "ENCODING_DOLBY_TRUEHD";
            case 15:
                return "ENCODING_AAC_ELD";
            case 16:
                return "ENCODING_AAC_XHE";
            case 17:
                return "ENCODING_AC4";
            case 18:
                return "ENCODING_E_AC3_JOC";
            case 19:
                return "ENCODING_DOLBY_MAT";
            case 20:
                return "ENCODING_OPUS";
            case 21:
                return "ENCODING_PCM_24BIT_PACKED";
            case 22:
                return "ENCODING_PCM_32BIT";
            case 23:
                return "ENCODING_MPEGH_BL_L3";
            case 24:
                return "ENCODING_MPEGH_BL_L4";
            case 25:
                return "ENCODING_MPEGH_LC_L3";
            case 26:
                return "ENCODING_MPEGH_LC_L4";
            case 27:
                return "ENCODING_DTS_UHD_P1";
            case 28:
                return "ENCODING_DRA";
            case 29:
                return "ENCODING_DTS_HD_MA";
            case 30:
                return "ENCODING_DTS_UHD_P2";
            case 31:
                return "ENCODING_DSD";
        }
    }

    public static int inChannelMaskFromOutChannelMask(int outMask) throws IllegalArgumentException {
        if (outMask == 1) {
            throw new IllegalArgumentException("Illegal CHANNEL_OUT_DEFAULT channel mask for input.");
        }
        switch (channelCountFromOutChannelMask(outMask)) {
            case 1:
                return 16;
            case 2:
                return 12;
            default:
                throw new IllegalArgumentException("Unsupported channel configuration for input.");
        }
    }

    public static int channelCountFromInChannelMask(int mask) {
        return Integer.bitCount(mask);
    }

    public static int channelCountFromOutChannelMask(int mask) {
        return Integer.bitCount(mask);
    }

    public static int convertChannelOutMaskToNativeMask(int javaMask) {
        return javaMask >> 2;
    }

    public static int convertNativeChannelMaskToOutMask(int nativeMask) {
        return nativeMask << 2;
    }

    public static int getBytesPerSample(int audioFormat) {
        switch (audioFormat) {
            case 1:
            case 2:
            case 13:
                return 2;
            case 3:
                return 1;
            case 4:
            case 22:
                return 4;
            case 21:
                return 3;
            default:
                throw new IllegalArgumentException("Bad audio format " + audioFormat);
        }
    }

    public static boolean isValidEncoding(int audioFormat) {
        switch (audioFormat) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
                return true;
            default:
                return false;
        }
    }

    public static boolean isPublicEncoding(int audioFormat) {
        switch (audioFormat) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
                return true;
            default:
                return false;
        }
    }

    public static boolean isEncodingLinearPcm(int audioFormat) {
        switch (audioFormat) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 21:
            case 22:
                return true;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
                return false;
            default:
                throw new IllegalArgumentException("Bad audio format " + audioFormat);
        }
    }

    public static boolean isEncodingLinearFrames(int audioFormat) {
        switch (audioFormat) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 13:
            case 21:
            case 22:
                return true;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
                return false;
            default:
                throw new IllegalArgumentException("Bad audio format " + audioFormat);
        }
    }

    public static int[] filterPublicFormats(int[] formats) {
        if (formats == null) {
            return null;
        }
        int[] myCopy = Arrays.copyOf(formats, formats.length);
        int size = 0;
        for (int i = 0; i < myCopy.length; i++) {
            if (isPublicEncoding(myCopy[i])) {
                if (size != i) {
                    myCopy[size] = myCopy[i];
                }
                size++;
            }
        }
        return Arrays.copyOf(myCopy, size);
    }

    public AudioFormat() {
        throw new UnsupportedOperationException("There is no valid usage of this constructor");
    }

    private AudioFormat(int encoding, int sampleRate, int channelMask, int channelIndexMask) {
        this(15, encoding, sampleRate, channelMask, channelIndexMask);
    }

    private AudioFormat(int propertySetMask, int encoding, int sampleRate, int channelMask, int channelIndexMask) {
        this.mPropertySetMask = propertySetMask;
        int i = (propertySetMask & 1) != 0 ? encoding : 0;
        this.mEncoding = i;
        this.mSampleRate = (propertySetMask & 2) != 0 ? sampleRate : 0;
        this.mChannelMask = (propertySetMask & 4) != 0 ? channelMask : 0;
        this.mChannelIndexMask = (propertySetMask & 8) != 0 ? channelIndexMask : 0;
        int channelIndexCount = Integer.bitCount(getChannelIndexMask());
        int channelCount = channelCountFromOutChannelMask(getChannelMask());
        if (channelCount == 0) {
            channelCount = channelIndexCount;
        } else if (channelCount != channelIndexCount && channelIndexCount != 0) {
            channelCount = 0;
        }
        this.mChannelCount = channelCount;
        int frameSizeInBytes = 1;
        try {
            frameSizeInBytes = getBytesPerSample(i) * channelCount;
        } catch (IllegalArgumentException e) {
        }
        this.mFrameSizeInBytes = frameSizeInBytes != 0 ? frameSizeInBytes : 1;
    }

    public int getEncoding() {
        return this.mEncoding;
    }

    public int getSampleRate() {
        return this.mSampleRate;
    }

    public int getChannelMask() {
        return this.mChannelMask;
    }

    public int getChannelIndexMask() {
        return this.mChannelIndexMask;
    }

    public int getChannelCount() {
        return this.mChannelCount;
    }

    public int getFrameSizeInBytes() {
        return this.mFrameSizeInBytes;
    }

    public int getPropertySetMask() {
        return this.mPropertySetMask;
    }

    public String toLogFriendlyString() {
        return String.format("%dch %dHz %s", Integer.valueOf(this.mChannelCount), Integer.valueOf(this.mSampleRate), toLogFriendlyEncoding(this.mEncoding));
    }

    /* loaded from: classes2.dex */
    public static class Builder {
        private int mChannelIndexMask;
        private int mChannelMask;
        private int mEncoding;
        private int mPropertySetMask;
        private int mSampleRate;

        public Builder() {
            this.mEncoding = 0;
            this.mSampleRate = 0;
            this.mChannelMask = 0;
            this.mChannelIndexMask = 0;
            this.mPropertySetMask = 0;
        }

        public Builder(AudioFormat af) {
            this.mEncoding = 0;
            this.mSampleRate = 0;
            this.mChannelMask = 0;
            this.mChannelIndexMask = 0;
            this.mPropertySetMask = 0;
            this.mEncoding = af.mEncoding;
            this.mSampleRate = af.mSampleRate;
            this.mChannelMask = af.mChannelMask;
            this.mChannelIndexMask = af.mChannelIndexMask;
            this.mPropertySetMask = af.mPropertySetMask;
        }

        public AudioFormat build() {
            AudioFormat af = new AudioFormat(this.mPropertySetMask, this.mEncoding, this.mSampleRate, this.mChannelMask, this.mChannelIndexMask);
            return af;
        }

        public Builder setEncoding(int encoding) throws IllegalArgumentException {
            switch (encoding) {
                case 1:
                    this.mEncoding = 2;
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                    this.mEncoding = encoding;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid encoding " + encoding);
            }
            this.mPropertySetMask |= 1;
            return this;
        }

        public Builder setChannelMask(int channelMask) {
            if (channelMask == 0) {
                throw new IllegalArgumentException("Invalid zero channel mask");
            }
            if (this.mChannelIndexMask != 0 && Integer.bitCount(channelMask) != Integer.bitCount(this.mChannelIndexMask)) {
                throw new IllegalArgumentException("Mismatched channel count for mask " + Integer.toHexString(channelMask).toUpperCase());
            }
            this.mChannelMask = channelMask;
            this.mPropertySetMask |= 4;
            return this;
        }

        public Builder setChannelIndexMask(int channelIndexMask) {
            if (channelIndexMask == 0) {
                throw new IllegalArgumentException("Invalid zero channel index mask");
            }
            if (this.mChannelMask != 0 && Integer.bitCount(channelIndexMask) != Integer.bitCount(this.mChannelMask)) {
                throw new IllegalArgumentException("Mismatched channel count for index mask " + Integer.toHexString(channelIndexMask).toUpperCase());
            }
            this.mChannelIndexMask = channelIndexMask;
            this.mPropertySetMask |= 8;
            return this;
        }

        public Builder setSampleRate(int sampleRate) throws IllegalArgumentException {
            if ((sampleRate < AudioFormat.SAMPLE_RATE_HZ_MIN || sampleRate > AudioFormat.SAMPLE_RATE_HZ_MAX) && sampleRate != 0) {
                throw new IllegalArgumentException("Invalid sample rate " + sampleRate);
            }
            this.mSampleRate = sampleRate;
            this.mPropertySetMask |= 2;
            return this;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioFormat that = (AudioFormat) o;
        int i = this.mPropertySetMask;
        if (i != that.mPropertySetMask) {
            return false;
        }
        if (((i & 1) == 0 || this.mEncoding == that.mEncoding) && (((i & 2) == 0 || this.mSampleRate == that.mSampleRate) && (((i & 4) == 0 || this.mChannelMask == that.mChannelMask) && ((i & 8) == 0 || this.mChannelIndexMask == that.mChannelIndexMask)))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mPropertySetMask), Integer.valueOf(this.mSampleRate), Integer.valueOf(this.mEncoding), Integer.valueOf(this.mChannelMask), Integer.valueOf(this.mChannelIndexMask));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPropertySetMask);
        dest.writeInt(this.mEncoding);
        dest.writeInt(this.mSampleRate);
        dest.writeInt(this.mChannelMask);
        dest.writeInt(this.mChannelIndexMask);
    }

    private AudioFormat(Parcel in) {
        this(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt());
    }

    public String toString() {
        return new String("AudioFormat: props=" + this.mPropertySetMask + " enc=" + this.mEncoding + " chan=0x" + Integer.toHexString(this.mChannelMask).toUpperCase() + " chan_index=0x" + Integer.toHexString(this.mChannelIndexMask).toUpperCase() + " rate=" + this.mSampleRate);
    }

    public static String toDisplayName(int audioFormat) {
        switch (audioFormat) {
            case 5:
                return "Dolby Digital";
            case 6:
                return "Dolby Digital Plus";
            case 7:
                return "DTS";
            case 8:
                return "DTS HD";
            case 9:
            case 11:
            case 12:
            case 13:
            case 15:
            case 16:
            case 20:
            case 21:
            case 22:
            default:
                return "Unknown surround sound format";
            case 10:
                return "AAC";
            case 14:
                return "Dolby TrueHD";
            case 17:
                return "Dolby AC-4";
            case 18:
                return "Dolby Atmos in Dolby Digital Plus";
            case 19:
                return "Dolby MAT";
            case 23:
                return "MPEG-H 3D Audio baseline profile level 3";
            case 24:
                return "MPEG-H 3D Audio baseline profile level 4";
            case 25:
                return "MPEG-H 3D Audio low complexity profile level 3";
            case 26:
                return "MPEG-H 3D Audio low complexity profile level 4";
            case 27:
                return "DTS UHD Profile 1";
            case 28:
                return "DRA";
            case 29:
                return "DTS HD Master Audio";
            case 30:
                return "DTS UHD Profile 2";
        }
    }
}
