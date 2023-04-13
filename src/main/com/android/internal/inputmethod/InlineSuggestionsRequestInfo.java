package com.android.internal.inputmethod;

import android.annotation.NonNull;
import android.content.ComponentName;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.autofill.AutofillId;
import com.android.internal.util.AnnotationValidations;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class InlineSuggestionsRequestInfo implements Parcelable {
    public static final Parcelable.Creator<InlineSuggestionsRequestInfo> CREATOR = new Parcelable.Creator<InlineSuggestionsRequestInfo>() { // from class: com.android.internal.inputmethod.InlineSuggestionsRequestInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InlineSuggestionsRequestInfo[] newArray(int size) {
            return new InlineSuggestionsRequestInfo[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InlineSuggestionsRequestInfo createFromParcel(Parcel in) {
            return new InlineSuggestionsRequestInfo(in);
        }
    };
    private final AutofillId mAutofillId;
    private final ComponentName mComponentName;
    private final Bundle mUiExtras;

    public InlineSuggestionsRequestInfo(ComponentName componentName, AutofillId autofillId, Bundle uiExtras) {
        this.mComponentName = componentName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) componentName);
        this.mAutofillId = autofillId;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) autofillId);
        this.mUiExtras = uiExtras;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) uiExtras);
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public AutofillId getAutofillId() {
        return this.mAutofillId;
    }

    public Bundle getUiExtras() {
        return this.mUiExtras;
    }

    public String toString() {
        return "InlineSuggestionsRequestInfo { componentName = " + this.mComponentName + ", autofillId = " + this.mAutofillId + ", uiExtras = " + this.mUiExtras + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlineSuggestionsRequestInfo that = (InlineSuggestionsRequestInfo) o;
        if (Objects.equals(this.mComponentName, that.mComponentName) && Objects.equals(this.mAutofillId, that.mAutofillId) && Objects.equals(this.mUiExtras, that.mUiExtras)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mComponentName);
        return (((_hash * 31) + Objects.hashCode(this.mAutofillId)) * 31) + Objects.hashCode(this.mUiExtras);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mComponentName, flags);
        dest.writeTypedObject(this.mAutofillId, flags);
        dest.writeBundle(this.mUiExtras);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    InlineSuggestionsRequestInfo(Parcel in) {
        ComponentName componentName = (ComponentName) in.readTypedObject(ComponentName.CREATOR);
        AutofillId autofillId = (AutofillId) in.readTypedObject(AutofillId.CREATOR);
        Bundle uiExtras = in.readBundle();
        this.mComponentName = componentName;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) componentName);
        this.mAutofillId = autofillId;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) autofillId);
        this.mUiExtras = uiExtras;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) uiExtras);
    }

    @Deprecated
    private void __metadata() {
    }
}
