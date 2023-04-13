package android.service.autofill;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Slog;
import android.view.autofill.AutofillValue;
import android.view.autofill.Helper;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public final class TextValueSanitizer extends InternalSanitizer implements Sanitizer, Parcelable {
    public static final Parcelable.Creator<TextValueSanitizer> CREATOR = new Parcelable.Creator<TextValueSanitizer>() { // from class: android.service.autofill.TextValueSanitizer.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextValueSanitizer createFromParcel(Parcel parcel) {
            return new TextValueSanitizer((Pattern) parcel.readSerializable(Pattern.class.getClassLoader(), Pattern.class), parcel.readString());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextValueSanitizer[] newArray(int size) {
            return new TextValueSanitizer[size];
        }
    };
    private static final String TAG = "TextValueSanitizer";
    private final Pattern mRegex;
    private final String mSubst;

    public TextValueSanitizer(Pattern regex, String subst) {
        this.mRegex = (Pattern) Objects.requireNonNull(regex);
        this.mSubst = (String) Objects.requireNonNull(subst);
    }

    @Override // android.service.autofill.InternalSanitizer
    public AutofillValue sanitize(AutofillValue value) {
        if (value == null) {
            Slog.m90w(TAG, "sanitize() called with null value");
            return null;
        } else if (!value.isText()) {
            if (Helper.sDebug) {
                Slog.m98d(TAG, "sanitize() called with non-text value: " + value);
            }
            return null;
        } else {
            CharSequence text = value.getTextValue();
            try {
                Matcher matcher = this.mRegex.matcher(text);
                if (!matcher.matches()) {
                    if (Helper.sDebug) {
                        Slog.m98d(TAG, "sanitize(): " + this.mRegex + " failed for " + value);
                    }
                    return null;
                }
                CharSequence sanitized = matcher.replaceAll(this.mSubst);
                return AutofillValue.forText(sanitized);
            } catch (Exception e) {
                Slog.m90w(TAG, "Exception evaluating " + this.mRegex + "/" + this.mSubst + ": " + e);
                return null;
            }
        }
    }

    public String toString() {
        return !Helper.sDebug ? super.toString() : "TextValueSanitizer: [regex=" + this.mRegex + ", subst=" + this.mSubst + NavigationBarInflaterView.SIZE_MOD_END;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeSerializable(this.mRegex);
        parcel.writeString(this.mSubst);
    }
}
