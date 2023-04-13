package android.content;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class LocusId implements Parcelable {
    public static final Parcelable.Creator<LocusId> CREATOR = new Parcelable.Creator<LocusId>() { // from class: android.content.LocusId.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LocusId createFromParcel(Parcel parcel) {
            return new LocusId(parcel.readString());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LocusId[] newArray(int size) {
            return new LocusId[size];
        }
    };
    private final String mId;

    public LocusId(String id) {
        this.mId = (String) Preconditions.checkStringNotEmpty(id, "id cannot be empty");
    }

    public String getId() {
        return this.mId;
    }

    public int hashCode() {
        int i = 1 * 31;
        String str = this.mId;
        int result = i + (str == null ? 0 : str.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LocusId other = (LocusId) obj;
        String str = this.mId;
        if (str == null) {
            if (other.mId != null) {
                return false;
            }
        } else if (!str.equals(other.mId)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "LocusId[" + getSanitizedId() + NavigationBarInflaterView.SIZE_MOD_END;
    }

    public void dump(PrintWriter pw) {
        pw.print("id:");
        pw.println(getSanitizedId());
    }

    private String getSanitizedId() {
        int size = this.mId.length();
        return size + "_chars";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mId);
    }
}
