package android.media;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class AudioMixerAttributes implements Parcelable {
    public static final Parcelable.Creator<AudioMixerAttributes> CREATOR = new Parcelable.Creator<AudioMixerAttributes>() { // from class: android.media.AudioMixerAttributes.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioMixerAttributes createFromParcel(Parcel p) {
            return new AudioMixerAttributes(p);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioMixerAttributes[] newArray(int size) {
            return new AudioMixerAttributes[size];
        }
    };
    public static final int MIXER_BEHAVIOR_BIT_PERFECT = 1;
    public static final int MIXER_BEHAVIOR_DEFAULT = 0;
    private final AudioFormat mFormat;
    private final int mMixerBehavior;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface MixerBehavior {
    }

    AudioMixerAttributes(AudioFormat format, int mixerBehavior) {
        this.mFormat = format;
        this.mMixerBehavior = mixerBehavior;
    }

    public AudioFormat getFormat() {
        return this.mFormat;
    }

    public int getMixerBehavior() {
        return this.mMixerBehavior;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private final AudioFormat mFormat;
        private int mMixerBehavior = 0;

        public Builder(AudioFormat format) {
            Objects.requireNonNull(format);
            this.mFormat = format;
        }

        public AudioMixerAttributes build() {
            AudioMixerAttributes ama = new AudioMixerAttributes(this.mFormat, this.mMixerBehavior);
            return ama;
        }

        public Builder setMixerBehavior(int mixerBehavior) {
            switch (mixerBehavior) {
                case 0:
                case 1:
                    this.mMixerBehavior = mixerBehavior;
                    return this;
                default:
                    throw new IllegalArgumentException("Invalid mixer behavior " + mixerBehavior);
            }
        }
    }

    public int hashCode() {
        return Objects.hash(this.mFormat, Integer.valueOf(this.mMixerBehavior));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioMixerAttributes that = (AudioMixerAttributes) o;
        if (this.mFormat.equals(that.mFormat) && this.mMixerBehavior == that.mMixerBehavior) {
            return true;
        }
        return false;
    }

    private String mixerBehaviorToString(int mixerBehavior) {
        switch (mixerBehavior) {
            case 0:
                return "default";
            case 1:
                return "bit-perfect";
            default:
                return "unknown";
        }
    }

    public String toString() {
        return new String("AudioMixerAttributes: format:" + this.mFormat.toString() + " mixer behavior:" + mixerBehaviorToString(this.mMixerBehavior));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mFormat, flags);
        dest.writeInt(this.mMixerBehavior);
    }

    private AudioMixerAttributes(Parcel in) {
        this.mFormat = (AudioFormat) in.readParcelable(AudioFormat.class.getClassLoader(), AudioFormat.class);
        this.mMixerBehavior = in.readInt();
    }
}
