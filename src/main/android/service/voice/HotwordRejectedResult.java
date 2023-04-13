package android.service.voice;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes3.dex */
public final class HotwordRejectedResult implements Parcelable {
    public static final int CONFIDENCE_LEVEL_HIGH = 3;
    public static final int CONFIDENCE_LEVEL_LOW = 1;
    public static final int CONFIDENCE_LEVEL_MEDIUM = 2;
    public static final int CONFIDENCE_LEVEL_NONE = 0;
    public static final Parcelable.Creator<HotwordRejectedResult> CREATOR = new Parcelable.Creator<HotwordRejectedResult>() { // from class: android.service.voice.HotwordRejectedResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HotwordRejectedResult[] newArray(int size) {
            return new HotwordRejectedResult[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HotwordRejectedResult createFromParcel(Parcel in) {
            return new HotwordRejectedResult(in);
        }
    };
    private final int mConfidenceLevel;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ConfidenceLevel {
    }

    /* loaded from: classes3.dex */
    @interface HotwordConfidenceLevelValue {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int defaultConfidenceLevel() {
        return 0;
    }

    public static String confidenceLevelToString(int value) {
        switch (value) {
            case 0:
                return "CONFIDENCE_LEVEL_NONE";
            case 1:
                return "CONFIDENCE_LEVEL_LOW";
            case 2:
                return "CONFIDENCE_LEVEL_MEDIUM";
            case 3:
                return "CONFIDENCE_LEVEL_HIGH";
            default:
                return Integer.toHexString(value);
        }
    }

    HotwordRejectedResult(int confidenceLevel) {
        this.mConfidenceLevel = confidenceLevel;
        AnnotationValidations.validate((Class<? extends Annotation>) HotwordConfidenceLevelValue.class, (Annotation) null, confidenceLevel);
    }

    public int getConfidenceLevel() {
        return this.mConfidenceLevel;
    }

    public String toString() {
        return "HotwordRejectedResult { confidenceLevel = " + this.mConfidenceLevel + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HotwordRejectedResult that = (HotwordRejectedResult) o;
        if (this.mConfidenceLevel == that.mConfidenceLevel) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + this.mConfidenceLevel;
        return _hash;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mConfidenceLevel);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    HotwordRejectedResult(Parcel in) {
        int confidenceLevel = in.readInt();
        this.mConfidenceLevel = confidenceLevel;
        AnnotationValidations.validate((Class<? extends Annotation>) HotwordConfidenceLevelValue.class, (Annotation) null, confidenceLevel);
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private long mBuilderFieldsSet = 0;
        private int mConfidenceLevel;

        public Builder setConfidenceLevel(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mConfidenceLevel = value;
            return this;
        }

        public HotwordRejectedResult build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 2;
            this.mBuilderFieldsSet = j;
            if ((j & 1) == 0) {
                this.mConfidenceLevel = HotwordRejectedResult.defaultConfidenceLevel();
            }
            HotwordRejectedResult o = new HotwordRejectedResult(this.mConfidenceLevel);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 2) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    @Deprecated
    private void __metadata() {
    }
}
