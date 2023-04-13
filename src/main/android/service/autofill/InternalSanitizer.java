package android.service.autofill;

import android.p008os.Parcelable;
import android.view.autofill.AutofillValue;
/* loaded from: classes3.dex */
public abstract class InternalSanitizer implements Sanitizer, Parcelable {
    public abstract AutofillValue sanitize(AutofillValue autofillValue);
}
