package android.service.credentials;

import android.annotation.NonNull;
import android.app.slice.Slice;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
/* loaded from: classes3.dex */
public class CreateEntry implements Parcelable {
    public static final Parcelable.Creator<CreateEntry> CREATOR = new Parcelable.Creator<CreateEntry>() { // from class: android.service.credentials.CreateEntry.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CreateEntry createFromParcel(Parcel in) {
            return new CreateEntry(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CreateEntry[] newArray(int size) {
            return new CreateEntry[size];
        }
    };
    private final Slice mSlice;

    private CreateEntry(Parcel in) {
        this.mSlice = (Slice) in.readTypedObject(Slice.CREATOR);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mSlice, flags);
    }

    public CreateEntry(Slice slice) {
        this.mSlice = slice;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) slice);
    }

    public Slice getSlice() {
        return this.mSlice;
    }
}
