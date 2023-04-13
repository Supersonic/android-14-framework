package android.telephony.euicc;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes3.dex */
public final class EuiccInfo implements Parcelable {
    public static final Parcelable.Creator<EuiccInfo> CREATOR = new Parcelable.Creator<EuiccInfo>() { // from class: android.telephony.euicc.EuiccInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public EuiccInfo createFromParcel(Parcel in) {
            return new EuiccInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public EuiccInfo[] newArray(int size) {
            return new EuiccInfo[size];
        }
    };
    private final String osVersion;

    public String getOsVersion() {
        return this.osVersion;
    }

    public EuiccInfo(String osVersion) {
        this.osVersion = osVersion;
    }

    private EuiccInfo(Parcel in) {
        this.osVersion = in.readString();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.osVersion);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
