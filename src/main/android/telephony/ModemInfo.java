package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes3.dex */
public class ModemInfo implements Parcelable {
    public static final Parcelable.Creator<ModemInfo> CREATOR = new Parcelable.Creator() { // from class: android.telephony.ModemInfo.1
        @Override // android.p008os.Parcelable.Creator
        public ModemInfo createFromParcel(Parcel in) {
            return new ModemInfo(in);
        }

        @Override // android.p008os.Parcelable.Creator
        public ModemInfo[] newArray(int size) {
            return new ModemInfo[size];
        }
    };
    public final boolean isDataSupported;
    public final boolean isVoiceSupported;
    public final int modemId;
    public final int rat;

    public ModemInfo(int modemId) {
        this(modemId, 0, true, true);
    }

    public ModemInfo(int modemId, int rat, boolean isVoiceSupported, boolean isDataSupported) {
        this.modemId = modemId;
        this.rat = rat;
        this.isVoiceSupported = isVoiceSupported;
        this.isDataSupported = isDataSupported;
    }

    public ModemInfo(Parcel in) {
        this.modemId = in.readInt();
        this.rat = in.readInt();
        this.isVoiceSupported = in.readBoolean();
        this.isDataSupported = in.readBoolean();
    }

    public String toString() {
        return "modemId=" + this.modemId + " rat=" + this.rat + " isVoiceSupported:" + this.isVoiceSupported + " isDataSupported:" + this.isDataSupported;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.modemId), Integer.valueOf(this.rat), Boolean.valueOf(this.isVoiceSupported), Boolean.valueOf(this.isDataSupported));
    }

    public boolean equals(Object o) {
        if (o != null && (o instanceof ModemInfo) && hashCode() == o.hashCode()) {
            if (this == o) {
                return true;
            }
            ModemInfo s = (ModemInfo) o;
            if (this.modemId != s.modemId || this.rat != s.rat || this.isVoiceSupported != s.isVoiceSupported || this.isDataSupported != s.isDataSupported) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.modemId);
        dest.writeInt(this.rat);
        dest.writeBoolean(this.isVoiceSupported);
        dest.writeBoolean(this.isDataSupported);
    }
}
