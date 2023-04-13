package android.telephony.ims;

import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes3.dex */
public final class RcsContactTerminatedReason implements Parcelable {
    public static final Parcelable.Creator<RcsContactTerminatedReason> CREATOR = new Parcelable.Creator<RcsContactTerminatedReason>() { // from class: android.telephony.ims.RcsContactTerminatedReason.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RcsContactTerminatedReason createFromParcel(Parcel in) {
            return new RcsContactTerminatedReason(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RcsContactTerminatedReason[] newArray(int size) {
            return new RcsContactTerminatedReason[size];
        }
    };
    private final Uri mContactUri;
    private final String mReason;

    public RcsContactTerminatedReason(Uri contact, String reason) {
        this.mContactUri = contact;
        this.mReason = reason;
    }

    private RcsContactTerminatedReason(Parcel in) {
        this.mContactUri = (Uri) in.readParcelable(Uri.class.getClassLoader(), Uri.class);
        this.mReason = in.readString();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.mContactUri, flags);
        out.writeString(this.mReason);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Uri getContactUri() {
        return this.mContactUri;
    }

    public String getReason() {
        return this.mReason;
    }
}
