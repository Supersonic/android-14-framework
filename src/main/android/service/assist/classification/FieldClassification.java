package android.service.assist.classification;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArraySet;
import android.view.autofill.AutofillId;
import com.android.internal.util.AnnotationValidations;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/* loaded from: classes3.dex */
public final class FieldClassification implements Parcelable {
    public static final Parcelable.Creator<FieldClassification> CREATOR = new Parcelable.Creator<FieldClassification>() { // from class: android.service.assist.classification.FieldClassification.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FieldClassification[] newArray(int size) {
            return new FieldClassification[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FieldClassification createFromParcel(Parcel in) {
            return new FieldClassification(in);
        }
    };
    private final AutofillId mAutofillId;
    private final Set<String> mGroupHints;
    private final Set<String> mHints;

    public AutofillId getAutofillId() {
        return this.mAutofillId;
    }

    public Set<String> getHints() {
        return this.mHints;
    }

    @SystemApi
    public Set<String> getGroupHints() {
        return this.mGroupHints;
    }

    static Set<String> unparcelHints(Parcel in) {
        List<String> hints = new ArrayList<>();
        in.readStringList(hints);
        return new ArraySet(hints);
    }

    void parcelHints(Parcel dest, int flags) {
        dest.writeStringList(new ArrayList(this.mHints));
    }

    static Set<String> unparcelGroupHints(Parcel in) {
        List<String> groupHints = new ArrayList<>();
        in.readStringList(groupHints);
        return new ArraySet(groupHints);
    }

    void parcelGroupHints(Parcel dest, int flags) {
        dest.writeStringList(new ArrayList(this.mGroupHints));
    }

    public FieldClassification(AutofillId autofillId, Set<String> hints) {
        this(autofillId, hints, new ArraySet());
    }

    @SystemApi
    public FieldClassification(AutofillId autofillId, Set<String> hints, Set<String> groupHints) {
        this.mAutofillId = autofillId;
        this.mHints = hints;
        this.mGroupHints = groupHints;
    }

    public String toString() {
        return "FieldClassification { autofillId = " + this.mAutofillId + ", hints = " + this.mHints + ", groupHints = " + this.mGroupHints + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mAutofillId, flags);
        parcelHints(dest, flags);
        parcelGroupHints(dest, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    FieldClassification(Parcel in) {
        AutofillId autofillId = (AutofillId) in.readTypedObject(AutofillId.CREATOR);
        Set<String> hints = unparcelHints(in);
        Set<String> groupHints = unparcelGroupHints(in);
        this.mAutofillId = autofillId;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) autofillId);
        this.mHints = hints;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hints);
        this.mGroupHints = groupHints;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) groupHints);
    }

    @Deprecated
    private void __metadata() {
    }
}
