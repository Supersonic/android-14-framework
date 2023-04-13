package android.service.autofill;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import android.view.autofill.Helper;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class RequiredValidators extends InternalValidator {
    public static final Parcelable.Creator<RequiredValidators> CREATOR = new Parcelable.Creator<RequiredValidators>() { // from class: android.service.autofill.RequiredValidators.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RequiredValidators createFromParcel(Parcel parcel) {
            return new RequiredValidators((InternalValidator[]) parcel.readParcelableArray(null, InternalValidator.class));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RequiredValidators[] newArray(int size) {
            return new RequiredValidators[size];
        }
    };
    private static final String TAG = "RequiredValidators";
    private final InternalValidator[] mValidators;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RequiredValidators(InternalValidator[] validators) {
        this.mValidators = (InternalValidator[]) Preconditions.checkArrayElementsNotNull(validators, "validators");
    }

    @Override // android.service.autofill.InternalValidator
    public boolean isValid(ValueFinder finder) {
        InternalValidator[] internalValidatorArr;
        for (InternalValidator validator : this.mValidators) {
            boolean valid = validator.isValid(finder);
            if (Helper.sDebug) {
                Log.m112d(TAG, "isValid(" + validator + "): " + valid);
            }
            if (!valid) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return !Helper.sDebug ? super.toString() : "RequiredValidators: [validators=" + Arrays.toString(this.mValidators) + NavigationBarInflaterView.SIZE_MOD_END;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelableArray(this.mValidators, flags);
    }
}
