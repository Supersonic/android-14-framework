package android.view.translation;

import android.annotation.NonNull;
import android.icu.util.ULocale;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class TranslationSpec implements Parcelable {
    public static final Parcelable.Creator<TranslationSpec> CREATOR = new Parcelable.Creator<TranslationSpec>() { // from class: android.view.translation.TranslationSpec.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TranslationSpec[] newArray(int size) {
            return new TranslationSpec[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TranslationSpec createFromParcel(Parcel in) {
            return new TranslationSpec(in);
        }
    };
    public static final int DATA_FORMAT_TEXT = 1;
    private final int mDataFormat;
    @Deprecated
    private final String mLanguage;
    private final ULocale mLocale;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface DataFormat {
    }

    void parcelLocale(Parcel dest, int flags) {
        dest.writeSerializable(this.mLocale);
    }

    static ULocale unparcelLocale(Parcel in) {
        return (ULocale) in.readSerializable(ULocale.class.getClassLoader(), ULocale.class);
    }

    @Deprecated
    public TranslationSpec(String language, int dataFormat) {
        this.mLanguage = language;
        this.mDataFormat = dataFormat;
        this.mLocale = new ULocale.Builder().setLanguage(language).build();
    }

    public TranslationSpec(ULocale locale, int dataFormat) {
        Objects.requireNonNull(locale);
        this.mLanguage = locale.getLanguage();
        this.mLocale = locale;
        this.mDataFormat = dataFormat;
    }

    @Deprecated
    public String getLanguage() {
        return this.mLanguage;
    }

    public ULocale getLocale() {
        return this.mLocale;
    }

    public int getDataFormat() {
        return this.mDataFormat;
    }

    public String toString() {
        return "TranslationSpec { language = " + this.mLanguage + ", locale = " + this.mLocale + ", dataFormat = " + this.mDataFormat + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TranslationSpec that = (TranslationSpec) o;
        if (Objects.equals(this.mLanguage, that.mLanguage) && Objects.equals(this.mLocale, that.mLocale) && this.mDataFormat == that.mDataFormat) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mLanguage);
        return (((_hash * 31) + Objects.hashCode(this.mLocale)) * 31) + this.mDataFormat;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mLanguage);
        parcelLocale(dest, flags);
        dest.writeInt(this.mDataFormat);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    TranslationSpec(Parcel in) {
        String language = in.readString();
        ULocale locale = unparcelLocale(in);
        int dataFormat = in.readInt();
        this.mLanguage = language;
        AnnotationValidations.validate(Deprecated.class, (Annotation) null, language);
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) language);
        this.mLocale = locale;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) locale);
        this.mDataFormat = dataFormat;
        AnnotationValidations.validate((Class<? extends Annotation>) DataFormat.class, (Annotation) null, dataFormat);
    }

    @Deprecated
    private void __metadata() {
    }
}
