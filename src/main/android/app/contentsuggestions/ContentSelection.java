package android.app.contentsuggestions;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes.dex */
public final class ContentSelection implements Parcelable {
    public static final Parcelable.Creator<ContentSelection> CREATOR = new Parcelable.Creator<ContentSelection>() { // from class: android.app.contentsuggestions.ContentSelection.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentSelection createFromParcel(Parcel source) {
            return new ContentSelection(source.readString(), source.readBundle());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentSelection[] newArray(int size) {
            return new ContentSelection[size];
        }
    };
    private final Bundle mExtras;
    private final String mSelectionId;

    public ContentSelection(String selectionId, Bundle extras) {
        this.mSelectionId = selectionId;
        this.mExtras = extras;
    }

    public String getId() {
        return this.mSelectionId;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSelectionId);
        dest.writeBundle(this.mExtras);
    }
}
