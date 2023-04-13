package android.view.inputmethod;

import android.graphics.PointF;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class InsertGesture extends HandwritingGesture implements Parcelable {
    public static final Parcelable.Creator<InsertGesture> CREATOR = new Parcelable.Creator<InsertGesture>() { // from class: android.view.inputmethod.InsertGesture.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InsertGesture createFromParcel(Parcel source) {
            return new InsertGesture(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InsertGesture[] newArray(int size) {
            return new InsertGesture[size];
        }
    };
    private PointF mPoint;
    private String mTextToInsert;

    private InsertGesture(String text, PointF point, String fallbackText) {
        this.mType = 2;
        this.mPoint = point;
        this.mTextToInsert = text;
        this.mFallbackText = fallbackText;
    }

    private InsertGesture(Parcel source) {
        this.mType = 2;
        this.mFallbackText = source.readString8();
        this.mTextToInsert = source.readString8();
        this.mPoint = (PointF) source.readTypedObject(PointF.CREATOR);
    }

    public String getTextToInsert() {
        return this.mTextToInsert;
    }

    public PointF getInsertionPoint() {
        return this.mPoint;
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private String mFallbackText;
        private PointF mPoint;
        private String mText;

        public Builder setTextToInsert(String text) {
            this.mText = text;
            return this;
        }

        public Builder setInsertionPoint(PointF point) {
            this.mPoint = point;
            return this;
        }

        public Builder setFallbackText(String fallbackText) {
            this.mFallbackText = fallbackText;
            return this;
        }

        public InsertGesture build() {
            if (this.mPoint == null) {
                throw new IllegalArgumentException("Insertion point must be set.");
            }
            if (this.mText == null) {
                throw new IllegalArgumentException("Text to insert must be set.");
            }
            return new InsertGesture(this.mText, this.mPoint, this.mFallbackText);
        }
    }

    public int hashCode() {
        return Objects.hash(this.mPoint, this.mTextToInsert, this.mFallbackText);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof InsertGesture) {
            InsertGesture that = (InsertGesture) o;
            if (Objects.equals(this.mFallbackText, that.mFallbackText) && Objects.equals(this.mTextToInsert, that.mTextToInsert)) {
                return Objects.equals(this.mPoint, that.mPoint);
            }
            return false;
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mFallbackText);
        dest.writeString8(this.mTextToInsert);
        dest.writeTypedObject(this.mPoint, flags);
    }
}
