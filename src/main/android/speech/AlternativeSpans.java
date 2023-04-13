package android.speech;

import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class AlternativeSpans implements Parcelable {
    public static final Parcelable.Creator<AlternativeSpans> CREATOR = new Parcelable.Creator<AlternativeSpans>() { // from class: android.speech.AlternativeSpans.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AlternativeSpans[] newArray(int size) {
            return new AlternativeSpans[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AlternativeSpans createFromParcel(Parcel in) {
            return new AlternativeSpans(in);
        }
    };
    private final List<AlternativeSpan> mSpans;

    public AlternativeSpans(List<AlternativeSpan> spans) {
        this.mSpans = spans;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) spans);
    }

    public List<AlternativeSpan> getSpans() {
        return this.mSpans;
    }

    public String toString() {
        return "AlternativeSpans { spans = " + this.mSpans + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AlternativeSpans that = (AlternativeSpans) o;
        return Objects.equals(this.mSpans, that.mSpans);
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mSpans);
        return _hash;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelableList(this.mSpans, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    AlternativeSpans(Parcel in) {
        ArrayList arrayList = new ArrayList();
        in.readParcelableList(arrayList, AlternativeSpan.class.getClassLoader(), AlternativeSpan.class);
        this.mSpans = arrayList;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) arrayList);
    }

    @Deprecated
    private void __metadata() {
    }
}
