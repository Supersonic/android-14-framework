package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes3.dex */
public abstract class VopsSupportInfo implements Parcelable {
    public static final Parcelable.Creator<VopsSupportInfo> CREATOR = new Parcelable.Creator<VopsSupportInfo>() { // from class: android.telephony.VopsSupportInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VopsSupportInfo createFromParcel(Parcel in) {
            int type = in.readInt();
            switch (type) {
                case 3:
                    return LteVopsSupportInfo.createFromParcelBody(in);
                case 6:
                    return NrVopsSupportInfo.createFromParcelBody(in);
                default:
                    throw new RuntimeException("Bad VopsSupportInfo Parcel");
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VopsSupportInfo[] newArray(int size) {
            return new VopsSupportInfo[size];
        }
    };

    public abstract boolean equals(Object obj);

    public abstract int hashCode();

    public abstract boolean isEmergencyServiceFallbackSupported();

    public abstract boolean isEmergencyServiceSupported();

    public abstract boolean isVopsSupported();

    @Override // android.p008os.Parcelable
    public abstract void writeToParcel(Parcel parcel, int i);

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeToParcel(Parcel dest, int flags, int type) {
        dest.writeInt(type);
    }
}
