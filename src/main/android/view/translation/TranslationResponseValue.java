package android.view.translation;

import android.annotation.NonNull;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class TranslationResponseValue implements Parcelable {
    public static final Parcelable.Creator<TranslationResponseValue> CREATOR = new Parcelable.Creator<TranslationResponseValue>() { // from class: android.view.translation.TranslationResponseValue.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TranslationResponseValue[] newArray(int size) {
            return new TranslationResponseValue[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TranslationResponseValue createFromParcel(Parcel in) {
            return new TranslationResponseValue(in);
        }
    };
    public static final String EXTRA_DEFINITIONS = "android.view.translation.extra.DEFINITIONS";
    public static final int STATUS_ERROR = 1;
    public static final int STATUS_SUCCESS = 0;
    private final Bundle mExtras;
    private final int mStatusCode;
    private final CharSequence mText;
    private final CharSequence mTransliteration;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Status {
    }

    public static TranslationResponseValue forError() {
        return new TranslationResponseValue(1, null, Bundle.EMPTY, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static CharSequence defaultText() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Bundle defaultExtras() {
        return Bundle.EMPTY;
    }

    private boolean extrasEquals(Bundle other) {
        return Objects.equals(this.mExtras, other) || (this.mExtras.isEmpty() && other.isEmpty());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static CharSequence defaultTransliteration() {
        return null;
    }

    /* loaded from: classes4.dex */
    static abstract class BaseBuilder {
        BaseBuilder() {
        }
    }

    public static String statusToString(int value) {
        switch (value) {
            case 0:
                return "STATUS_SUCCESS";
            case 1:
                return "STATUS_ERROR";
            default:
                return Integer.toHexString(value);
        }
    }

    TranslationResponseValue(int statusCode, CharSequence text, Bundle extras, CharSequence transliteration) {
        this.mStatusCode = statusCode;
        if (statusCode != 0 && statusCode != 1) {
            throw new IllegalArgumentException("statusCode was " + statusCode + " but must be one of: STATUS_SUCCESS(0), STATUS_ERROR(1" + NavigationBarInflaterView.KEY_CODE_END);
        }
        this.mText = text;
        this.mExtras = extras;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) extras);
        this.mTransliteration = transliteration;
    }

    public int getStatusCode() {
        return this.mStatusCode;
    }

    public CharSequence getText() {
        return this.mText;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public CharSequence getTransliteration() {
        return this.mTransliteration;
    }

    public String toString() {
        return "TranslationResponseValue { statusCode = " + statusToString(this.mStatusCode) + ", text = " + ((Object) this.mText) + ", extras = " + this.mExtras + ", transliteration = " + ((Object) this.mTransliteration) + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TranslationResponseValue that = (TranslationResponseValue) o;
        if (this.mStatusCode == that.mStatusCode && Objects.equals(this.mText, that.mText) && extrasEquals(that.mExtras) && Objects.equals(this.mTransliteration, that.mTransliteration)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + this.mStatusCode;
        return (((((_hash * 31) + Objects.hashCode(this.mText)) * 31) + Objects.hashCode(this.mExtras)) * 31) + Objects.hashCode(this.mTransliteration);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mText != null ? (byte) (0 | 2) : (byte) 0;
        if (this.mTransliteration != null) {
            flg = (byte) (flg | 8);
        }
        dest.writeByte(flg);
        dest.writeInt(this.mStatusCode);
        CharSequence charSequence = this.mText;
        if (charSequence != null) {
            dest.writeCharSequence(charSequence);
        }
        dest.writeBundle(this.mExtras);
        CharSequence charSequence2 = this.mTransliteration;
        if (charSequence2 != null) {
            dest.writeCharSequence(charSequence2);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    TranslationResponseValue(Parcel in) {
        byte flg = in.readByte();
        int statusCode = in.readInt();
        CharSequence text = (flg & 2) == 0 ? null : in.readCharSequence();
        Bundle extras = in.readBundle();
        CharSequence transliteration = (flg & 8) == 0 ? null : in.readCharSequence();
        this.mStatusCode = statusCode;
        if (statusCode != 0 && statusCode != 1) {
            throw new IllegalArgumentException("statusCode was " + statusCode + " but must be one of: STATUS_SUCCESS(0), STATUS_ERROR(1" + NavigationBarInflaterView.KEY_CODE_END);
        }
        this.mText = text;
        this.mExtras = extras;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) extras);
        this.mTransliteration = transliteration;
    }

    /* loaded from: classes4.dex */
    public static final class Builder extends BaseBuilder {
        private long mBuilderFieldsSet = 0;
        private Bundle mExtras;
        private int mStatusCode;
        private CharSequence mText;
        private CharSequence mTransliteration;

        public Builder(int statusCode) {
            this.mStatusCode = statusCode;
            if (statusCode != 0 && statusCode != 1) {
                throw new IllegalArgumentException("statusCode was " + this.mStatusCode + " but must be one of: STATUS_SUCCESS(0), STATUS_ERROR(1" + NavigationBarInflaterView.KEY_CODE_END);
            }
        }

        public Builder setText(CharSequence value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mText = value;
            return this;
        }

        public Builder setExtras(Bundle value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mExtras = value;
            return this;
        }

        public Builder setTransliteration(CharSequence value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 8;
            this.mTransliteration = value;
            return this;
        }

        public TranslationResponseValue build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 16;
            this.mBuilderFieldsSet = j;
            if ((j & 2) == 0) {
                this.mText = TranslationResponseValue.defaultText();
            }
            if ((this.mBuilderFieldsSet & 4) == 0) {
                this.mExtras = TranslationResponseValue.defaultExtras();
            }
            if ((this.mBuilderFieldsSet & 8) == 0) {
                this.mTransliteration = TranslationResponseValue.defaultTransliteration();
            }
            TranslationResponseValue o = new TranslationResponseValue(this.mStatusCode, this.mText, this.mExtras, this.mTransliteration);
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
