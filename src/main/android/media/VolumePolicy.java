package android.media;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class VolumePolicy implements Parcelable {
    public static final int A11Y_MODE_INDEPENDENT_A11Y_VOLUME = 1;
    public static final int A11Y_MODE_MEDIA_A11Y_VOLUME = 0;
    public final boolean doNotDisturbWhenSilent;
    public final int vibrateToSilentDebounce;
    public final boolean volumeDownToEnterSilent;
    public final boolean volumeUpToExitSilent;
    public static final VolumePolicy DEFAULT = new VolumePolicy(false, false, false, 400);
    public static final Parcelable.Creator<VolumePolicy> CREATOR = new Parcelable.Creator<VolumePolicy>() { // from class: android.media.VolumePolicy.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VolumePolicy createFromParcel(Parcel p) {
            return new VolumePolicy(p.readInt() != 0, p.readInt() != 0, p.readInt() != 0, p.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VolumePolicy[] newArray(int size) {
            return new VolumePolicy[size];
        }
    };

    public VolumePolicy(boolean volumeDownToEnterSilent, boolean volumeUpToExitSilent, boolean doNotDisturbWhenSilent, int vibrateToSilentDebounce) {
        this.volumeDownToEnterSilent = volumeDownToEnterSilent;
        this.volumeUpToExitSilent = volumeUpToExitSilent;
        this.doNotDisturbWhenSilent = doNotDisturbWhenSilent;
        this.vibrateToSilentDebounce = vibrateToSilentDebounce;
    }

    public String toString() {
        return "VolumePolicy[volumeDownToEnterSilent=" + this.volumeDownToEnterSilent + ",volumeUpToExitSilent=" + this.volumeUpToExitSilent + ",doNotDisturbWhenSilent=" + this.doNotDisturbWhenSilent + ",vibrateToSilentDebounce=" + this.vibrateToSilentDebounce + NavigationBarInflaterView.SIZE_MOD_END;
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.volumeDownToEnterSilent), Boolean.valueOf(this.volumeUpToExitSilent), Boolean.valueOf(this.doNotDisturbWhenSilent), Integer.valueOf(this.vibrateToSilentDebounce));
    }

    public boolean equals(Object o) {
        if (o instanceof VolumePolicy) {
            if (o == this) {
                return true;
            }
            VolumePolicy other = (VolumePolicy) o;
            return other.volumeDownToEnterSilent == this.volumeDownToEnterSilent && other.volumeUpToExitSilent == this.volumeUpToExitSilent && other.doNotDisturbWhenSilent == this.doNotDisturbWhenSilent && other.vibrateToSilentDebounce == this.vibrateToSilentDebounce;
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.volumeDownToEnterSilent ? 1 : 0);
        dest.writeInt(this.volumeUpToExitSilent ? 1 : 0);
        dest.writeInt(this.doNotDisturbWhenSilent ? 1 : 0);
        dest.writeInt(this.vibrateToSilentDebounce);
    }
}
