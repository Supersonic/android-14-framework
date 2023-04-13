package android.view.inputmethod;

import android.graphics.PointF;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class RemoveSpaceGesture extends HandwritingGesture implements Parcelable {
    public static final Parcelable.Creator<RemoveSpaceGesture> CREATOR = new Parcelable.Creator<RemoveSpaceGesture>() { // from class: android.view.inputmethod.RemoveSpaceGesture.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RemoveSpaceGesture createFromParcel(Parcel source) {
            return new RemoveSpaceGesture(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RemoveSpaceGesture[] newArray(int size) {
            return new RemoveSpaceGesture[size];
        }
    };
    private final PointF mEndPoint;
    private final PointF mStartPoint;

    private RemoveSpaceGesture(PointF startPoint, PointF endPoint, String fallbackText) {
        this.mType = 8;
        this.mStartPoint = startPoint;
        this.mEndPoint = endPoint;
        this.mFallbackText = fallbackText;
    }

    private RemoveSpaceGesture(Parcel source) {
        this.mType = 8;
        this.mStartPoint = (PointF) source.readTypedObject(PointF.CREATOR);
        this.mEndPoint = (PointF) source.readTypedObject(PointF.CREATOR);
        this.mFallbackText = source.readString8();
    }

    public PointF getStartPoint() {
        return this.mStartPoint;
    }

    public PointF getEndPoint() {
        return this.mEndPoint;
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private PointF mEndPoint;
        private String mFallbackText;
        private PointF mStartPoint;

        public Builder setPoints(PointF startPoint, PointF endPoint) {
            this.mStartPoint = startPoint;
            this.mEndPoint = endPoint;
            return this;
        }

        public Builder setFallbackText(String fallbackText) {
            this.mFallbackText = fallbackText;
            return this;
        }

        public RemoveSpaceGesture build() {
            if (this.mStartPoint == null || this.mEndPoint == null) {
                throw new IllegalArgumentException("Start and end points must be set.");
            }
            return new RemoveSpaceGesture(this.mStartPoint, this.mEndPoint, this.mFallbackText);
        }
    }

    public int hashCode() {
        return Objects.hash(this.mStartPoint, this.mEndPoint, this.mFallbackText);
    }

    public boolean equals(Object o) {
        if (o instanceof RemoveSpaceGesture) {
            RemoveSpaceGesture that = (RemoveSpaceGesture) o;
            return Objects.equals(this.mStartPoint, that.mStartPoint) && Objects.equals(this.mEndPoint, that.mEndPoint) && Objects.equals(this.mFallbackText, that.mFallbackText);
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mStartPoint, flags);
        dest.writeTypedObject(this.mEndPoint, flags);
        dest.writeString8(this.mFallbackText);
    }
}
