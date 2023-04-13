package android.view.inputmethod;

import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class InlineSuggestionsResponse implements Parcelable {
    public static final Parcelable.Creator<InlineSuggestionsResponse> CREATOR = new Parcelable.Creator<InlineSuggestionsResponse>() { // from class: android.view.inputmethod.InlineSuggestionsResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InlineSuggestionsResponse[] newArray(int size) {
            return new InlineSuggestionsResponse[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InlineSuggestionsResponse createFromParcel(Parcel in) {
            return new InlineSuggestionsResponse(in);
        }
    };
    private final List<InlineSuggestion> mInlineSuggestions;

    public static InlineSuggestionsResponse newInlineSuggestionsResponse(List<InlineSuggestion> inlineSuggestions) {
        return new InlineSuggestionsResponse(inlineSuggestions);
    }

    public InlineSuggestionsResponse(List<InlineSuggestion> inlineSuggestions) {
        this.mInlineSuggestions = inlineSuggestions;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) inlineSuggestions);
    }

    public List<InlineSuggestion> getInlineSuggestions() {
        return this.mInlineSuggestions;
    }

    public String toString() {
        return "InlineSuggestionsResponse { inlineSuggestions = " + this.mInlineSuggestions + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlineSuggestionsResponse that = (InlineSuggestionsResponse) o;
        return Objects.equals(this.mInlineSuggestions, that.mInlineSuggestions);
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mInlineSuggestions);
        return _hash;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelableList(this.mInlineSuggestions, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    InlineSuggestionsResponse(Parcel in) {
        ArrayList arrayList = new ArrayList();
        in.readParcelableList(arrayList, InlineSuggestion.class.getClassLoader(), InlineSuggestion.class);
        this.mInlineSuggestions = arrayList;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) arrayList);
    }

    @Deprecated
    private void __metadata() {
    }
}
