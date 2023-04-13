package android.p008os;

import android.app.admin.DevicePolicyResources;
import android.media.AudioAttributes;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* renamed from: android.os.VibrationAttributes */
/* loaded from: classes3.dex */
public final class VibrationAttributes implements Parcelable {
    public static final Parcelable.Creator<VibrationAttributes> CREATOR = new Parcelable.Creator<VibrationAttributes>() { // from class: android.os.VibrationAttributes.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VibrationAttributes createFromParcel(Parcel p) {
            return new VibrationAttributes(p);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VibrationAttributes[] newArray(int size) {
            return new VibrationAttributes[size];
        }
    };
    public static final int FLAG_ALL_SUPPORTED = 15;
    public static final int FLAG_BYPASS_INTERRUPTION_POLICY = 1;
    public static final int FLAG_BYPASS_USER_VIBRATION_INTENSITY_OFF = 2;
    public static final int FLAG_INVALIDATE_SETTINGS_CACHE = 4;
    public static final int FLAG_PIPELINED_EFFECT = 8;
    private static final String TAG = "VibrationAttributes";
    public static final int USAGE_ACCESSIBILITY = 66;
    public static final int USAGE_ALARM = 17;
    public static final int USAGE_CLASS_ALARM = 1;
    public static final int USAGE_CLASS_FEEDBACK = 2;
    public static final int USAGE_CLASS_MASK = 15;
    public static final int USAGE_CLASS_MEDIA = 3;
    public static final int USAGE_CLASS_UNKNOWN = 0;
    public static final int USAGE_COMMUNICATION_REQUEST = 65;
    public static final int USAGE_FILTER_MATCH_ALL = -1;
    public static final int USAGE_HARDWARE_FEEDBACK = 50;
    public static final int USAGE_MEDIA = 19;
    public static final int USAGE_NOTIFICATION = 49;
    public static final int USAGE_PHYSICAL_EMULATION = 34;
    public static final int USAGE_RINGTONE = 33;
    public static final int USAGE_TOUCH = 18;
    public static final int USAGE_UNKNOWN = 0;
    private final int mFlags;
    private final int mOriginalAudioUsage;
    private final int mUsage;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.VibrationAttributes$Flag */
    /* loaded from: classes3.dex */
    public @interface Flag {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.VibrationAttributes$Usage */
    /* loaded from: classes3.dex */
    public @interface Usage {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.VibrationAttributes$UsageClass */
    /* loaded from: classes3.dex */
    public @interface UsageClass {
    }

    public static VibrationAttributes createForUsage(int usage) {
        return new Builder().setUsage(usage).build();
    }

    private VibrationAttributes(int usage, int audioUsage, int flags) {
        this.mUsage = usage;
        this.mOriginalAudioUsage = audioUsage;
        this.mFlags = flags & 15;
    }

    public int getUsageClass() {
        return this.mUsage & 15;
    }

    public int getUsage() {
        return this.mUsage;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public boolean isFlagSet(int flag) {
        return (this.mFlags & flag) > 0;
    }

    public int getAudioUsage() {
        int i = this.mOriginalAudioUsage;
        if (i != 0) {
            return i;
        }
        switch (this.mUsage) {
            case 17:
                return 4;
            case 18:
                return 13;
            case 19:
                return 1;
            case 33:
                return 6;
            case 49:
                return 5;
            case 65:
                return 2;
            case 66:
                return 11;
            default:
                return 0;
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mUsage);
        dest.writeInt(this.mOriginalAudioUsage);
        dest.writeInt(this.mFlags);
    }

    private VibrationAttributes(Parcel src) {
        this.mUsage = src.readInt();
        this.mOriginalAudioUsage = src.readInt();
        this.mFlags = src.readInt();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VibrationAttributes rhs = (VibrationAttributes) o;
        if (this.mUsage == rhs.mUsage && this.mOriginalAudioUsage == rhs.mOriginalAudioUsage && this.mFlags == rhs.mFlags) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mUsage), Integer.valueOf(this.mOriginalAudioUsage), Integer.valueOf(this.mFlags));
    }

    public String toString() {
        return "VibrationAttributes: Usage=" + usageToString() + " Audio Usage= " + AudioAttributes.usageToString(this.mOriginalAudioUsage) + " Flags=" + this.mFlags;
    }

    public String usageToString() {
        return usageToString(this.mUsage);
    }

    public static String usageToString(int usage) {
        switch (usage) {
            case 0:
                return "UNKNOWN";
            case 17:
                return "ALARM";
            case 18:
                return "TOUCH";
            case 19:
                return "MEDIA";
            case 33:
                return "RINGTONE";
            case 34:
                return "PHYSICAL_EMULATION";
            case 49:
                return DevicePolicyResources.Drawables.Source.NOTIFICATION;
            case 50:
                return "HARDWARE_FEEDBACK";
            case 65:
                return "COMMUNICATION_REQUEST";
            case 66:
                return "ACCESSIBILITY";
            default:
                return "unknown usage " + usage;
        }
    }

    /* renamed from: android.os.VibrationAttributes$Builder */
    /* loaded from: classes3.dex */
    public static final class Builder {
        private int mFlags;
        private int mOriginalAudioUsage;
        private int mUsage;

        public Builder() {
            this.mUsage = 0;
            this.mOriginalAudioUsage = 0;
            this.mFlags = 0;
        }

        public Builder(VibrationAttributes vib) {
            this.mUsage = 0;
            this.mOriginalAudioUsage = 0;
            this.mFlags = 0;
            if (vib != null) {
                this.mUsage = vib.mUsage;
                this.mOriginalAudioUsage = vib.mOriginalAudioUsage;
                this.mFlags = vib.mFlags;
            }
        }

        public Builder(AudioAttributes audio) {
            this.mUsage = 0;
            this.mOriginalAudioUsage = 0;
            this.mFlags = 0;
            setUsage(audio);
            setFlags(audio);
        }

        private void setUsage(AudioAttributes audio) {
            this.mOriginalAudioUsage = audio.getUsage();
            switch (audio.getUsage()) {
                case 1:
                case 14:
                    this.mUsage = 19;
                    return;
                case 2:
                case 3:
                case 12:
                case 16:
                    this.mUsage = 65;
                    return;
                case 4:
                    this.mUsage = 17;
                    return;
                case 5:
                case 7:
                case 8:
                case 9:
                case 10:
                    this.mUsage = 49;
                    return;
                case 6:
                    this.mUsage = 33;
                    return;
                case 11:
                    this.mUsage = 66;
                    return;
                case 13:
                    this.mUsage = 18;
                    return;
                case 15:
                default:
                    this.mUsage = 0;
                    return;
            }
        }

        private void setFlags(AudioAttributes audio) {
            if ((audio.getAllFlags() & 64) != 0) {
                this.mFlags |= 1;
            }
            if ((audio.getAllFlags() & 128) != 0) {
                this.mFlags |= 2;
            }
        }

        public VibrationAttributes build() {
            VibrationAttributes ans = new VibrationAttributes(this.mUsage, this.mOriginalAudioUsage, this.mFlags);
            return ans;
        }

        public Builder setUsage(int usage) {
            this.mOriginalAudioUsage = 0;
            this.mUsage = usage;
            return this;
        }

        public Builder setFlags(int flags, int mask) {
            int mask2 = mask & 15;
            this.mFlags = (this.mFlags & (~mask2)) | (flags & mask2);
            return this;
        }

        public Builder setFlags(int flags) {
            return setFlags(flags, 15);
        }
    }
}
