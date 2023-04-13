package android.view.translation;

import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArrayMap;
import android.view.autofill.AutofillId;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes4.dex */
public final class ViewTranslationRequest implements Parcelable {
    public static final Parcelable.Creator<ViewTranslationRequest> CREATOR = new Parcelable.Creator<ViewTranslationRequest>() { // from class: android.view.translation.ViewTranslationRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ViewTranslationRequest[] newArray(int size) {
            return new ViewTranslationRequest[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ViewTranslationRequest createFromParcel(Parcel in) {
            return new ViewTranslationRequest(in);
        }
    };
    public static final String ID_CONTENT_DESCRIPTION = "android:content_description";
    public static final String ID_TEXT = "android:text";
    private final AutofillId mAutofillId;
    private final Map<String, TranslationRequestValue> mTranslationRequestValues;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.view.translation.ViewTranslationRequest$Id */
    /* loaded from: classes4.dex */
    public @interface InterfaceC3720Id {
    }

    public TranslationRequestValue getValue(String key) {
        Objects.requireNonNull(key, "key should not be null");
        if (!this.mTranslationRequestValues.containsKey(key)) {
            throw new IllegalArgumentException("Request does not contain value for key=" + key);
        }
        return this.mTranslationRequestValues.get(key);
    }

    public Set<String> getKeys() {
        return this.mTranslationRequestValues.keySet();
    }

    public AutofillId getAutofillId() {
        return this.mAutofillId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Map<String, TranslationRequestValue> defaultTranslationRequestValues() {
        return Collections.emptyMap();
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private AutofillId mAutofillId;
        private long mBuilderFieldsSet = 0;
        private Map<String, TranslationRequestValue> mTranslationRequestValues;

        public Builder(AutofillId autofillId) {
            this.mAutofillId = autofillId;
            AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) autofillId);
        }

        public Builder(AutofillId autofillId, long virtualChildId) {
            AutofillId autofillId2 = new AutofillId(autofillId, virtualChildId, 0);
            this.mAutofillId = autofillId2;
            AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) autofillId2);
        }

        public Builder setValue(String key, TranslationRequestValue value) {
            if (this.mTranslationRequestValues == null) {
                setTranslationRequestValues(new ArrayMap());
            }
            this.mTranslationRequestValues.put(key, value);
            return this;
        }

        public ViewTranslationRequest build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 4;
            this.mBuilderFieldsSet = j;
            if ((j & 2) == 0) {
                this.mTranslationRequestValues = ViewTranslationRequest.defaultTranslationRequestValues();
            }
            ViewTranslationRequest o = new ViewTranslationRequest(this.mAutofillId, this.mTranslationRequestValues);
            return o;
        }

        Builder setTranslationRequestValues(Map<String, TranslationRequestValue> value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mTranslationRequestValues = value;
            return this;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 4) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    public ViewTranslationRequest(AutofillId autofillId, Map<String, TranslationRequestValue> translationRequestValues) {
        this.mAutofillId = autofillId;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) autofillId);
        this.mTranslationRequestValues = translationRequestValues;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) translationRequestValues);
    }

    public String toString() {
        return "ViewTranslationRequest { autofillId = " + this.mAutofillId + ", translationRequestValues = " + this.mTranslationRequestValues + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ViewTranslationRequest that = (ViewTranslationRequest) o;
        if (Objects.equals(this.mAutofillId, that.mAutofillId) && Objects.equals(this.mTranslationRequestValues, that.mTranslationRequestValues)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mAutofillId);
        return (_hash * 31) + Objects.hashCode(this.mTranslationRequestValues);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mAutofillId, flags);
        dest.writeMap(this.mTranslationRequestValues);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    ViewTranslationRequest(Parcel in) {
        AutofillId autofillId = (AutofillId) in.readTypedObject(AutofillId.CREATOR);
        Map<String, TranslationRequestValue> translationRequestValues = new LinkedHashMap<>();
        in.readMap(translationRequestValues, TranslationRequestValue.class.getClassLoader());
        this.mAutofillId = autofillId;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) autofillId);
        this.mTranslationRequestValues = translationRequestValues;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) translationRequestValues);
    }

    @Deprecated
    private void __metadata() {
    }
}
