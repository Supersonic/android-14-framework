package android.speech;

import android.annotation.NonNull;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class RecognitionPart implements Parcelable {
    public static final int CONFIDENCE_LEVEL_HIGH = 5;
    public static final int CONFIDENCE_LEVEL_LOW = 1;
    public static final int CONFIDENCE_LEVEL_MEDIUM = 3;
    public static final int CONFIDENCE_LEVEL_MEDIUM_HIGH = 4;
    public static final int CONFIDENCE_LEVEL_MEDIUM_LOW = 2;
    public static final int CONFIDENCE_LEVEL_UNKNOWN = 0;
    public static final Parcelable.Creator<RecognitionPart> CREATOR = new Parcelable.Creator<RecognitionPart>() { // from class: android.speech.RecognitionPart.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RecognitionPart[] newArray(int size) {
            return new RecognitionPart[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RecognitionPart createFromParcel(Parcel in) {
            return new RecognitionPart(in);
        }
    };
    private final int mConfidenceLevel;
    private final String mFormattedText;
    private final String mRawText;
    private final long mTimestampMillis;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ConfidenceLevel {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String defaultFormattedText() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long defaultTimestampMillis() {
        return 0L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int defaultConfidenceLevel() {
        return 0;
    }

    private void onConstructed() {
        Preconditions.checkArgumentNonnegative(this.mTimestampMillis, "The timestamp must be non-negative.");
    }

    /* loaded from: classes3.dex */
    static abstract class BaseBuilder {
        BaseBuilder() {
        }

        public Builder setFormattedText(String value) {
            AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) value);
            Builder builder = (Builder) this;
            builder.checkNotUsed();
            builder.mBuilderFieldsSet |= 2;
            builder.mFormattedText = value;
            return builder;
        }
    }

    public static String confidenceLevelToString(int value) {
        switch (value) {
            case 0:
                return "CONFIDENCE_LEVEL_UNKNOWN";
            case 1:
                return "CONFIDENCE_LEVEL_LOW";
            case 2:
                return "CONFIDENCE_LEVEL_MEDIUM_LOW";
            case 3:
                return "CONFIDENCE_LEVEL_MEDIUM";
            case 4:
                return "CONFIDENCE_LEVEL_MEDIUM_HIGH";
            case 5:
                return "CONFIDENCE_LEVEL_HIGH";
            default:
                return Integer.toHexString(value);
        }
    }

    RecognitionPart(String rawText, String formattedText, long timestampMillis, int confidenceLevel) {
        this.mRawText = rawText;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) rawText);
        this.mFormattedText = formattedText;
        this.mTimestampMillis = timestampMillis;
        this.mConfidenceLevel = confidenceLevel;
        if (confidenceLevel != 0 && confidenceLevel != 1 && confidenceLevel != 2 && confidenceLevel != 3 && confidenceLevel != 4 && confidenceLevel != 5) {
            throw new IllegalArgumentException("confidenceLevel was " + confidenceLevel + " but must be one of: CONFIDENCE_LEVEL_UNKNOWN(0), CONFIDENCE_LEVEL_LOW(1), CONFIDENCE_LEVEL_MEDIUM_LOW(2), CONFIDENCE_LEVEL_MEDIUM(3), CONFIDENCE_LEVEL_MEDIUM_HIGH(4), CONFIDENCE_LEVEL_HIGH(5" + NavigationBarInflaterView.KEY_CODE_END);
        }
        onConstructed();
    }

    public String getRawText() {
        return this.mRawText;
    }

    public String getFormattedText() {
        return this.mFormattedText;
    }

    public long getTimestampMillis() {
        return this.mTimestampMillis;
    }

    public int getConfidenceLevel() {
        return this.mConfidenceLevel;
    }

    public String toString() {
        return "RecognitionPart { rawText = " + this.mRawText + ", formattedText = " + this.mFormattedText + ", timestampMillis = " + this.mTimestampMillis + ", confidenceLevel = " + confidenceLevelToString(this.mConfidenceLevel) + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecognitionPart that = (RecognitionPart) o;
        if (Objects.equals(this.mRawText, that.mRawText) && Objects.equals(this.mFormattedText, that.mFormattedText) && this.mTimestampMillis == that.mTimestampMillis && this.mConfidenceLevel == that.mConfidenceLevel) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mRawText);
        return (((((_hash * 31) + Objects.hashCode(this.mFormattedText)) * 31) + Long.hashCode(this.mTimestampMillis)) * 31) + this.mConfidenceLevel;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mFormattedText != null ? (byte) (0 | 2) : (byte) 0;
        dest.writeByte(flg);
        dest.writeString(this.mRawText);
        String str = this.mFormattedText;
        if (str != null) {
            dest.writeString(str);
        }
        dest.writeLong(this.mTimestampMillis);
        dest.writeInt(this.mConfidenceLevel);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    RecognitionPart(Parcel in) {
        byte flg = in.readByte();
        String rawText = in.readString();
        String formattedText = (flg & 2) == 0 ? null : in.readString();
        long timestampMillis = in.readLong();
        int confidenceLevel = in.readInt();
        this.mRawText = rawText;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) rawText);
        this.mFormattedText = formattedText;
        this.mTimestampMillis = timestampMillis;
        this.mConfidenceLevel = confidenceLevel;
        if (confidenceLevel != 0 && confidenceLevel != 1 && confidenceLevel != 2 && confidenceLevel != 3 && confidenceLevel != 4 && confidenceLevel != 5) {
            throw new IllegalArgumentException("confidenceLevel was " + confidenceLevel + " but must be one of: CONFIDENCE_LEVEL_UNKNOWN(0), CONFIDENCE_LEVEL_LOW(1), CONFIDENCE_LEVEL_MEDIUM_LOW(2), CONFIDENCE_LEVEL_MEDIUM(3), CONFIDENCE_LEVEL_MEDIUM_HIGH(4), CONFIDENCE_LEVEL_HIGH(5" + NavigationBarInflaterView.KEY_CODE_END);
        }
        onConstructed();
    }

    /* loaded from: classes3.dex */
    public static final class Builder extends BaseBuilder {
        private long mBuilderFieldsSet = 0;
        private int mConfidenceLevel;
        private String mFormattedText;
        private String mRawText;
        private long mTimestampMillis;

        @Override // android.speech.RecognitionPart.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setFormattedText(String str) {
            return super.setFormattedText(str);
        }

        public Builder(String rawText) {
            this.mRawText = rawText;
            AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) rawText);
        }

        public Builder setRawText(String value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mRawText = value;
            return this;
        }

        public Builder setTimestampMillis(long value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mTimestampMillis = value;
            return this;
        }

        public Builder setConfidenceLevel(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 8;
            this.mConfidenceLevel = value;
            return this;
        }

        public RecognitionPart build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 16;
            this.mBuilderFieldsSet = j;
            if ((j & 2) == 0) {
                this.mFormattedText = RecognitionPart.defaultFormattedText();
            }
            if ((this.mBuilderFieldsSet & 4) == 0) {
                this.mTimestampMillis = RecognitionPart.defaultTimestampMillis();
            }
            if ((this.mBuilderFieldsSet & 8) == 0) {
                this.mConfidenceLevel = RecognitionPart.defaultConfidenceLevel();
            }
            RecognitionPart o = new RecognitionPart(this.mRawText, this.mFormattedText, this.mTimestampMillis, this.mConfidenceLevel);
            return o;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 16) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    @Deprecated
    private void __metadata() {
    }
}
