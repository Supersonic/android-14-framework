package android.view.inputmethod;

import android.graphics.RectF;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class SelectGesture extends PreviewableHandwritingGesture implements Parcelable {
    public static final Parcelable.Creator<SelectGesture> CREATOR = new Parcelable.Creator<SelectGesture>() { // from class: android.view.inputmethod.SelectGesture.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SelectGesture createFromParcel(Parcel source) {
            return new SelectGesture(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SelectGesture[] newArray(int size) {
            return new SelectGesture[size];
        }
    };
    private RectF mArea;
    private int mGranularity;

    private SelectGesture(int granularity, RectF area, String fallbackText) {
        this.mType = 1;
        this.mArea = area;
        this.mGranularity = granularity;
        this.mFallbackText = fallbackText;
    }

    private SelectGesture(Parcel source) {
        this.mType = 1;
        this.mFallbackText = source.readString8();
        this.mGranularity = source.readInt();
        this.mArea = (RectF) source.readTypedObject(RectF.CREATOR);
    }

    public int getGranularity() {
        return this.mGranularity;
    }

    public RectF getSelectionArea() {
        return this.mArea;
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private RectF mArea;
        private String mFallbackText;
        private int mGranularity;

        public Builder setGranularity(int granularity) {
            this.mGranularity = granularity;
            return this;
        }

        public Builder setSelectionArea(RectF area) {
            this.mArea = area;
            return this;
        }

        public Builder setFallbackText(String fallbackText) {
            this.mFallbackText = fallbackText;
            return this;
        }

        public SelectGesture build() {
            RectF rectF = this.mArea;
            if (rectF == null || rectF.isEmpty()) {
                throw new IllegalArgumentException("Selection area must be set.");
            }
            if (this.mGranularity <= 0) {
                throw new IllegalArgumentException("Selection granularity must be set.");
            }
            return new SelectGesture(this.mGranularity, this.mArea, this.mFallbackText);
        }
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mGranularity), this.mArea, this.mFallbackText);
    }

    public boolean equals(Object o) {
        if (o instanceof SelectGesture) {
            SelectGesture that = (SelectGesture) o;
            if (this.mGranularity == that.mGranularity && Objects.equals(this.mFallbackText, that.mFallbackText)) {
                return Objects.equals(this.mArea, that.mArea);
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
        dest.writeInt(this.mGranularity);
        dest.writeTypedObject(this.mArea, flags);
    }
}
