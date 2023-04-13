package android.app.contentsuggestions;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.List;
@SystemApi
/* loaded from: classes.dex */
public final class ClassificationsRequest implements Parcelable {
    public static final Parcelable.Creator<ClassificationsRequest> CREATOR = new Parcelable.Creator<ClassificationsRequest>() { // from class: android.app.contentsuggestions.ClassificationsRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ClassificationsRequest createFromParcel(Parcel source) {
            return new ClassificationsRequest(source.createTypedArrayList(ContentSelection.CREATOR), source.readBundle());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ClassificationsRequest[] newArray(int size) {
            return new ClassificationsRequest[size];
        }
    };
    private final Bundle mExtras;
    private final List<ContentSelection> mSelections;

    private ClassificationsRequest(List<ContentSelection> selections, Bundle extras) {
        this.mSelections = selections;
        this.mExtras = extras;
    }

    public List<ContentSelection> getSelections() {
        return this.mSelections;
    }

    public Bundle getExtras() {
        Bundle bundle = this.mExtras;
        return bundle == null ? new Bundle() : bundle;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mSelections);
        dest.writeBundle(this.mExtras);
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private Bundle mExtras;
        private final List<ContentSelection> mSelections;

        public Builder(List<ContentSelection> selections) {
            this.mSelections = selections;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        public ClassificationsRequest build() {
            return new ClassificationsRequest(this.mSelections, this.mExtras);
        }
    }
}
