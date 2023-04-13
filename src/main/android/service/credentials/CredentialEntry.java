package android.service.credentials;

import android.app.slice.Slice;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.util.Objects;
/* loaded from: classes3.dex */
public class CredentialEntry implements Parcelable {
    public static final Parcelable.Creator<CredentialEntry> CREATOR = new Parcelable.Creator<CredentialEntry>() { // from class: android.service.credentials.CredentialEntry.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CredentialEntry createFromParcel(Parcel in) {
            return new CredentialEntry(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CredentialEntry[] newArray(int size) {
            return new CredentialEntry[size];
        }
    };
    private final String mBeginGetCredentialOptionId;
    private final Slice mSlice;
    private final String mType;

    public CredentialEntry(String beginGetCredentialOptionId, String type, Slice slice) {
        this.mBeginGetCredentialOptionId = (String) Preconditions.checkStringNotEmpty(beginGetCredentialOptionId, "beginGetCredentialOptionId must not be null, or empty");
        this.mType = (String) Preconditions.checkStringNotEmpty(type, "type must not be null, or empty");
        this.mSlice = (Slice) Objects.requireNonNull(slice, "slice must not be null");
    }

    public CredentialEntry(BeginGetCredentialOption beginGetCredentialOption, Slice slice) {
        Objects.requireNonNull(beginGetCredentialOption, "beginGetCredentialOption must not be null");
        this.mBeginGetCredentialOptionId = (String) Preconditions.checkStringNotEmpty(beginGetCredentialOption.getId(), "Id in beginGetCredentialOption must not be null");
        this.mType = (String) Preconditions.checkStringNotEmpty(beginGetCredentialOption.getType(), "type in beginGetCredentialOption must not be null");
        this.mSlice = (Slice) Objects.requireNonNull(slice, "slice must not be null");
    }

    public CredentialEntry(String type, Slice slice) {
        this.mBeginGetCredentialOptionId = null;
        this.mType = (String) Objects.requireNonNull(type, "type must not be null");
        this.mSlice = (Slice) Objects.requireNonNull(slice, "slice must not be null");
    }

    private CredentialEntry(Parcel in) {
        Objects.requireNonNull(in, "parcel must not be null");
        this.mType = in.readString8();
        this.mSlice = (Slice) in.readTypedObject(Slice.CREATOR);
        this.mBeginGetCredentialOptionId = in.readString8();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mType);
        dest.writeTypedObject(this.mSlice, flags);
        dest.writeString8(this.mBeginGetCredentialOptionId);
    }

    public String getBeginGetCredentialOptionId() {
        return this.mBeginGetCredentialOptionId;
    }

    public String getType() {
        return this.mType;
    }

    public Slice getSlice() {
        return this.mSlice;
    }
}
