package android.service.autofill;

import android.icu.text.DateFormat;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.view.autofill.Helper;
import android.widget.RemoteViews;
import java.util.Date;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class DateTransformation extends InternalTransformation implements Transformation, Parcelable {
    public static final Parcelable.Creator<DateTransformation> CREATOR = new Parcelable.Creator<DateTransformation>() { // from class: android.service.autofill.DateTransformation.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DateTransformation createFromParcel(Parcel parcel) {
            return new DateTransformation((AutofillId) parcel.readParcelable(null, AutofillId.class), (DateFormat) parcel.readSerializable(DateFormat.class.getClassLoader(), DateFormat.class));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DateTransformation[] newArray(int size) {
            return new DateTransformation[size];
        }
    };
    private static final String TAG = "DateTransformation";
    private final DateFormat mDateFormat;
    private final AutofillId mFieldId;

    public DateTransformation(AutofillId id, DateFormat dateFormat) {
        this.mFieldId = (AutofillId) Objects.requireNonNull(id);
        this.mDateFormat = (DateFormat) Objects.requireNonNull(dateFormat);
    }

    @Override // android.service.autofill.InternalTransformation
    public void apply(ValueFinder finder, RemoteViews parentTemplate, int childViewId) throws Exception {
        AutofillValue value = finder.findRawValueByAutofillId(this.mFieldId);
        if (value == null) {
            Log.m104w(TAG, "No value for id " + this.mFieldId);
        } else if (!value.isDate()) {
            Log.m104w(TAG, "Value for " + this.mFieldId + " is not date: " + value);
        } else {
            try {
                Date date = new Date(value.getDateValue());
                String transformed = this.mDateFormat.format(date);
                if (Helper.sDebug) {
                    Log.m112d(TAG, "Transformed " + date + " to " + transformed);
                }
                parentTemplate.setCharSequence(childViewId, "setText", transformed);
            } catch (Exception e) {
                Log.m104w(TAG, "Could not apply " + this.mDateFormat + " to " + value + ": " + e);
            }
        }
    }

    public String toString() {
        return !Helper.sDebug ? super.toString() : "DateTransformation: [id=" + this.mFieldId + ", format=" + this.mDateFormat + NavigationBarInflaterView.SIZE_MOD_END;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.mFieldId, flags);
        parcel.writeSerializable(this.mDateFormat);
    }
}
