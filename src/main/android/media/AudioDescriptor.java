package android.media;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes2.dex */
public class AudioDescriptor implements Parcelable {
    public static final Parcelable.Creator<AudioDescriptor> CREATOR = new Parcelable.Creator<AudioDescriptor>() { // from class: android.media.AudioDescriptor.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioDescriptor createFromParcel(Parcel p) {
            return new AudioDescriptor(p);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AudioDescriptor[] newArray(int size) {
            return new AudioDescriptor[size];
        }
    };
    public static final int STANDARD_EDID = 1;
    public static final int STANDARD_NONE = 0;
    public static final int STANDARD_SADB = 2;
    public static final int STANDARD_VSADB = 3;
    private final byte[] mDescriptor;
    private final int mEncapsulationType;
    private final int mStandard;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface AudioDescriptorStandard {
    }

    @SystemApi
    public AudioDescriptor(int standard, int encapsulationType, byte[] descriptor) {
        this.mStandard = standard;
        this.mEncapsulationType = encapsulationType;
        this.mDescriptor = descriptor;
    }

    public int getStandard() {
        return this.mStandard;
    }

    public byte[] getDescriptor() {
        return this.mDescriptor;
    }

    public int getEncapsulationType() {
        return this.mEncapsulationType;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mStandard), Integer.valueOf(this.mEncapsulationType), Integer.valueOf(Arrays.hashCode(this.mDescriptor)));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioDescriptor that = (AudioDescriptor) o;
        if (this.mStandard == that.mStandard && this.mEncapsulationType == that.mEncapsulationType && Arrays.equals(this.mDescriptor, that.mDescriptor)) {
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("standard=" + this.mStandard);
        sb.append(", encapsulation type=" + this.mEncapsulationType);
        byte[] bArr = this.mDescriptor;
        if (bArr != null && bArr.length > 0) {
            sb.append(", descriptor=").append(Arrays.toString(this.mDescriptor));
        }
        sb.append("}");
        return sb.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStandard);
        dest.writeInt(this.mEncapsulationType);
        dest.writeByteArray(this.mDescriptor);
    }

    private AudioDescriptor(Parcel in) {
        this.mStandard = in.readInt();
        this.mEncapsulationType = in.readInt();
        this.mDescriptor = in.createByteArray();
    }
}
