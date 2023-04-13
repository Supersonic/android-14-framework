package android.speech;

import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class AlternativeSpan implements Parcelable {
    public static final Parcelable.Creator<AlternativeSpan> CREATOR = new Parcelable.Creator<AlternativeSpan>() { // from class: android.speech.AlternativeSpan.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AlternativeSpan[] newArray(int size) {
            return new AlternativeSpan[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AlternativeSpan createFromParcel(Parcel in) {
            return new AlternativeSpan(in);
        }
    };
    private final List<String> mAlternatives;
    private final int mEndPosition;
    private final int mStartPosition;

    private void onConstructed() {
        Preconditions.checkArgumentNonnegative(this.mStartPosition, "The range start must be non-negative.");
        int i = this.mStartPosition;
        Preconditions.checkArgument(i < this.mEndPosition, "Illegal range [%d, %d), must be start < end.", Integer.valueOf(i), Integer.valueOf(this.mEndPosition));
        Preconditions.checkCollectionNotEmpty(this.mAlternatives, "List of alternative strings must not be empty.");
    }

    public AlternativeSpan(int startPosition, int endPosition, List<String> alternatives) {
        this.mStartPosition = startPosition;
        this.mEndPosition = endPosition;
        this.mAlternatives = alternatives;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) alternatives);
        onConstructed();
    }

    public int getStartPosition() {
        return this.mStartPosition;
    }

    public int getEndPosition() {
        return this.mEndPosition;
    }

    public List<String> getAlternatives() {
        return this.mAlternatives;
    }

    public String toString() {
        return "AlternativeSpan { startPosition = " + this.mStartPosition + ", endPosition = " + this.mEndPosition + ", alternatives = " + this.mAlternatives + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AlternativeSpan that = (AlternativeSpan) o;
        if (this.mStartPosition == that.mStartPosition && this.mEndPosition == that.mEndPosition && Objects.equals(this.mAlternatives, that.mAlternatives)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + this.mStartPosition;
        return (((_hash * 31) + this.mEndPosition) * 31) + Objects.hashCode(this.mAlternatives);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStartPosition);
        dest.writeInt(this.mEndPosition);
        dest.writeStringList(this.mAlternatives);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    AlternativeSpan(Parcel in) {
        int startPosition = in.readInt();
        int endPosition = in.readInt();
        List<String> alternatives = new ArrayList<>();
        in.readStringList(alternatives);
        this.mStartPosition = startPosition;
        this.mEndPosition = endPosition;
        this.mAlternatives = alternatives;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) alternatives);
        onConstructed();
    }

    @Deprecated
    private void __metadata() {
    }
}
