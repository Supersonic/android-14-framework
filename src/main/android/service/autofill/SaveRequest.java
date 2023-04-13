package android.service.autofill;

import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class SaveRequest implements Parcelable {
    public static final Parcelable.Creator<SaveRequest> CREATOR = new Parcelable.Creator<SaveRequest>() { // from class: android.service.autofill.SaveRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SaveRequest createFromParcel(Parcel parcel) {
            return new SaveRequest(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SaveRequest[] newArray(int size) {
            return new SaveRequest[size];
        }
    };
    private final Bundle mClientState;
    private final ArrayList<String> mDatasetIds;
    private final ArrayList<FillContext> mFillContexts;

    public SaveRequest(ArrayList<FillContext> fillContexts, Bundle clientState, ArrayList<String> datasetIds) {
        this.mFillContexts = (ArrayList) Objects.requireNonNull(fillContexts, "fillContexts");
        this.mClientState = clientState;
        this.mDatasetIds = datasetIds;
    }

    private SaveRequest(Parcel parcel) {
        this(parcel.createTypedArrayList(FillContext.CREATOR), parcel.readBundle(), parcel.createStringArrayList());
    }

    public List<FillContext> getFillContexts() {
        return this.mFillContexts;
    }

    public Bundle getClientState() {
        return this.mClientState;
    }

    public List<String> getDatasetIds() {
        return this.mDatasetIds;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeTypedList(this.mFillContexts, flags);
        parcel.writeBundle(this.mClientState);
        parcel.writeStringList(this.mDatasetIds);
    }
}
