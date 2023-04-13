package android.service.autofill;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.autofill.Helper;
import java.util.Objects;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class NegationValidator extends InternalValidator {
    public static final Parcelable.Creator<NegationValidator> CREATOR = new Parcelable.Creator<NegationValidator>() { // from class: android.service.autofill.NegationValidator.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NegationValidator createFromParcel(Parcel parcel) {
            return new NegationValidator((InternalValidator) parcel.readParcelable(null, InternalValidator.class));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NegationValidator[] newArray(int size) {
            return new NegationValidator[size];
        }
    };
    private final InternalValidator mValidator;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NegationValidator(InternalValidator validator) {
        this.mValidator = (InternalValidator) Objects.requireNonNull(validator);
    }

    @Override // android.service.autofill.InternalValidator
    public boolean isValid(ValueFinder finder) {
        return !this.mValidator.isValid(finder);
    }

    public String toString() {
        return !Helper.sDebug ? super.toString() : "NegationValidator: [validator=" + this.mValidator + NavigationBarInflaterView.SIZE_MOD_END;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mValidator, flags);
    }
}
