package android.service.assist.classification;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArraySet;
import com.android.internal.util.AnnotationValidations;
import java.util.ArrayList;
import java.util.Set;
@SystemApi
/* loaded from: classes3.dex */
public final class FieldClassificationResponse implements Parcelable {
    public static final Parcelable.Creator<FieldClassificationResponse> CREATOR = new Parcelable.Creator<FieldClassificationResponse>() { // from class: android.service.assist.classification.FieldClassificationResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FieldClassificationResponse[] newArray(int size) {
            return new FieldClassificationResponse[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FieldClassificationResponse createFromParcel(Parcel in) {
            return new FieldClassificationResponse(in);
        }
    };
    private final Set<FieldClassification> mClassifications;

    static Set<FieldClassification> unparcelClassifications(Parcel in) {
        ArrayList arrayList = new ArrayList();
        in.readParcelableList(arrayList, FieldClassification.class.getClassLoader(), FieldClassification.class);
        return new ArraySet(arrayList);
    }

    void parcelClassifications(Parcel dest, int flags) {
        dest.writeParcelableList(new ArrayList(this.mClassifications), flags);
    }

    public FieldClassificationResponse(Set<FieldClassification> classifications) {
        this.mClassifications = classifications;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) classifications);
    }

    public Set<FieldClassification> getClassifications() {
        return this.mClassifications;
    }

    public String toString() {
        return "FieldClassificationResponse { classifications = " + this.mClassifications + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        parcelClassifications(dest, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    FieldClassificationResponse(Parcel in) {
        Set<FieldClassification> classifications = unparcelClassifications(in);
        this.mClassifications = classifications;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) classifications);
    }

    @Deprecated
    private void __metadata() {
    }
}
