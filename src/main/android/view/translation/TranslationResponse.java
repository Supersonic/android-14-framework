package android.view.translation;

import android.annotation.NonNull;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.SparseArray;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class TranslationResponse implements Parcelable {
    public static final Parcelable.Creator<TranslationResponse> CREATOR = new Parcelable.Creator<TranslationResponse>() { // from class: android.view.translation.TranslationResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TranslationResponse[] newArray(int size) {
            return new TranslationResponse[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TranslationResponse createFromParcel(Parcel in) {
            return new TranslationResponse(in);
        }
    };
    public static final int TRANSLATION_STATUS_CONTEXT_UNSUPPORTED = 2;
    public static final int TRANSLATION_STATUS_SUCCESS = 0;
    public static final int TRANSLATION_STATUS_UNKNOWN_ERROR = 1;
    private final boolean mFinalResponse;
    private final SparseArray<TranslationResponseValue> mTranslationResponseValues;
    private final int mTranslationStatus;
    private final SparseArray<ViewTranslationResponse> mViewTranslationResponses;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface TranslationStatus {
    }

    /* loaded from: classes4.dex */
    static abstract class BaseBuilder {
        @Deprecated
        public abstract Builder setTranslationStatus(int i);

        BaseBuilder() {
        }

        public Builder setTranslationResponseValue(int index, TranslationResponseValue value) {
            Objects.requireNonNull(value, "value should not be null");
            Builder builder = (Builder) this;
            if (builder.mTranslationResponseValues == null) {
                builder.setTranslationResponseValues(new SparseArray<>());
            }
            builder.mTranslationResponseValues.put(index, value);
            return builder;
        }

        public Builder setViewTranslationResponse(int index, ViewTranslationResponse response) {
            Objects.requireNonNull(response, "value should not be null");
            Builder builder = (Builder) this;
            if (builder.mViewTranslationResponses == null) {
                builder.setViewTranslationResponses(new SparseArray<>());
            }
            builder.mViewTranslationResponses.put(index, response);
            return builder;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static SparseArray<TranslationResponseValue> defaultTranslationResponseValues() {
        return new SparseArray<>();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static SparseArray<ViewTranslationResponse> defaultViewTranslationResponses() {
        return new SparseArray<>();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean defaultFinalResponse() {
        return true;
    }

    public static String translationStatusToString(int value) {
        switch (value) {
            case 0:
                return "TRANSLATION_STATUS_SUCCESS";
            case 1:
                return "TRANSLATION_STATUS_UNKNOWN_ERROR";
            case 2:
                return "TRANSLATION_STATUS_CONTEXT_UNSUPPORTED";
            default:
                return Integer.toHexString(value);
        }
    }

    TranslationResponse(int translationStatus, SparseArray<TranslationResponseValue> translationResponseValues, SparseArray<ViewTranslationResponse> viewTranslationResponses, boolean finalResponse) {
        this.mTranslationStatus = translationStatus;
        if (translationStatus != 0 && translationStatus != 1 && translationStatus != 2) {
            throw new IllegalArgumentException("translationStatus was " + translationStatus + " but must be one of: TRANSLATION_STATUS_SUCCESS(0), TRANSLATION_STATUS_UNKNOWN_ERROR(1), TRANSLATION_STATUS_CONTEXT_UNSUPPORTED(2" + NavigationBarInflaterView.KEY_CODE_END);
        }
        this.mTranslationResponseValues = translationResponseValues;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) translationResponseValues);
        this.mViewTranslationResponses = viewTranslationResponses;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) viewTranslationResponses);
        this.mFinalResponse = finalResponse;
    }

    public int getTranslationStatus() {
        return this.mTranslationStatus;
    }

    public SparseArray<TranslationResponseValue> getTranslationResponseValues() {
        return this.mTranslationResponseValues;
    }

    public SparseArray<ViewTranslationResponse> getViewTranslationResponses() {
        return this.mViewTranslationResponses;
    }

    public boolean isFinalResponse() {
        return this.mFinalResponse;
    }

    public String toString() {
        return "TranslationResponse { translationStatus = " + translationStatusToString(this.mTranslationStatus) + ", translationResponseValues = " + this.mTranslationResponseValues + ", viewTranslationResponses = " + this.mViewTranslationResponses + ", finalResponse = " + this.mFinalResponse + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mFinalResponse ? (byte) (0 | 8) : (byte) 0;
        dest.writeByte(flg);
        dest.writeInt(this.mTranslationStatus);
        dest.writeSparseArray(this.mTranslationResponseValues);
        dest.writeSparseArray(this.mViewTranslationResponses);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    TranslationResponse(Parcel in) {
        byte flg = in.readByte();
        boolean finalResponse = (flg & 8) != 0;
        int translationStatus = in.readInt();
        SparseArray<TranslationResponseValue> translationResponseValues = in.readSparseArray(TranslationResponseValue.class.getClassLoader());
        SparseArray<ViewTranslationResponse> viewTranslationResponses = in.readSparseArray(ViewTranslationResponse.class.getClassLoader());
        this.mTranslationStatus = translationStatus;
        if (translationStatus != 0 && translationStatus != 1 && translationStatus != 2) {
            throw new IllegalArgumentException("translationStatus was " + translationStatus + " but must be one of: TRANSLATION_STATUS_SUCCESS(0), TRANSLATION_STATUS_UNKNOWN_ERROR(1), TRANSLATION_STATUS_CONTEXT_UNSUPPORTED(2" + NavigationBarInflaterView.KEY_CODE_END);
        }
        this.mTranslationResponseValues = translationResponseValues;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) translationResponseValues);
        this.mViewTranslationResponses = viewTranslationResponses;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) viewTranslationResponses);
        this.mFinalResponse = finalResponse;
    }

    /* loaded from: classes4.dex */
    public static final class Builder extends BaseBuilder {
        private long mBuilderFieldsSet = 0;
        private boolean mFinalResponse;
        private SparseArray<TranslationResponseValue> mTranslationResponseValues;
        private int mTranslationStatus;
        private SparseArray<ViewTranslationResponse> mViewTranslationResponses;

        @Override // android.view.translation.TranslationResponse.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setTranslationResponseValue(int i, TranslationResponseValue translationResponseValue) {
            return super.setTranslationResponseValue(i, translationResponseValue);
        }

        @Override // android.view.translation.TranslationResponse.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setViewTranslationResponse(int i, ViewTranslationResponse viewTranslationResponse) {
            return super.setViewTranslationResponse(i, viewTranslationResponse);
        }

        public Builder(int translationStatus) {
            this.mTranslationStatus = translationStatus;
            if (translationStatus != 0 && translationStatus != 1 && translationStatus != 2) {
                throw new IllegalArgumentException("translationStatus was " + this.mTranslationStatus + " but must be one of: TRANSLATION_STATUS_SUCCESS(0), TRANSLATION_STATUS_UNKNOWN_ERROR(1), TRANSLATION_STATUS_CONTEXT_UNSUPPORTED(2" + NavigationBarInflaterView.KEY_CODE_END);
            }
        }

        @Override // android.view.translation.TranslationResponse.BaseBuilder
        @Deprecated
        public Builder setTranslationStatus(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mTranslationStatus = value;
            return this;
        }

        public Builder setTranslationResponseValues(SparseArray<TranslationResponseValue> value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mTranslationResponseValues = value;
            return this;
        }

        public Builder setViewTranslationResponses(SparseArray<ViewTranslationResponse> value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mViewTranslationResponses = value;
            return this;
        }

        public Builder setFinalResponse(boolean value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 8;
            this.mFinalResponse = value;
            return this;
        }

        public TranslationResponse build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 16;
            this.mBuilderFieldsSet = j;
            if ((j & 2) == 0) {
                this.mTranslationResponseValues = TranslationResponse.defaultTranslationResponseValues();
            }
            if ((this.mBuilderFieldsSet & 4) == 0) {
                this.mViewTranslationResponses = TranslationResponse.defaultViewTranslationResponses();
            }
            if ((this.mBuilderFieldsSet & 8) == 0) {
                this.mFinalResponse = TranslationResponse.defaultFinalResponse();
            }
            TranslationResponse o = new TranslationResponse(this.mTranslationStatus, this.mTranslationResponseValues, this.mViewTranslationResponses, this.mFinalResponse);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 16) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    @Deprecated
    private void __metadata() {
    }
}
