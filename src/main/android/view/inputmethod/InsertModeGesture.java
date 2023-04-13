package android.view.inputmethod;

import android.graphics.PointF;
import android.p008os.CancellationSignal;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class InsertModeGesture extends HandwritingGesture implements Parcelable {
    public static final Parcelable.Creator<InsertModeGesture> CREATOR = new Parcelable.Creator<InsertModeGesture>() { // from class: android.view.inputmethod.InsertModeGesture.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InsertModeGesture createFromParcel(Parcel source) {
            return new InsertModeGesture(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InsertModeGesture[] newArray(int size) {
            return new InsertModeGesture[size];
        }
    };
    private CancellationSignal mCancellationSignal;
    private PointF mPoint;

    private InsertModeGesture(PointF point, String fallbackText, CancellationSignal cancellationSignal) {
        this.mType = 128;
        this.mPoint = point;
        this.mFallbackText = fallbackText;
        this.mCancellationSignal = cancellationSignal;
    }

    private InsertModeGesture(Parcel source) {
        this.mType = 128;
        this.mFallbackText = source.readString8();
        this.mPoint = (PointF) source.readTypedObject(PointF.CREATOR);
    }

    public CancellationSignal getCancellationSignal() {
        return this.mCancellationSignal;
    }

    public PointF getInsertionPoint() {
        return this.mPoint;
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private CancellationSignal mCancellationSignal;
        private String mFallbackText;
        private PointF mPoint;

        public Builder setInsertionPoint(PointF point) {
            this.mPoint = point;
            return this;
        }

        public Builder setCancellationSignal(CancellationSignal cancellationSignal) {
            this.mCancellationSignal = cancellationSignal;
            return this;
        }

        public Builder setFallbackText(String fallbackText) {
            this.mFallbackText = fallbackText;
            return this;
        }

        public InsertModeGesture build() {
            if (this.mPoint == null) {
                throw new IllegalArgumentException("Insertion point must be set.");
            }
            if (this.mCancellationSignal == null) {
                throw new IllegalArgumentException("CancellationSignal must be set.");
            }
            return new InsertModeGesture(this.mPoint, this.mFallbackText, this.mCancellationSignal);
        }
    }

    public int hashCode() {
        return Objects.hash(this.mPoint, this.mFallbackText);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof InsertModeGesture) {
            InsertModeGesture that = (InsertModeGesture) o;
            if (Objects.equals(this.mFallbackText, that.mFallbackText)) {
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
        dest.writeTypedObject(this.mPoint, flags);
    }
}
