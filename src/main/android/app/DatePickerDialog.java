package android.app;

import android.content.Context;
import android.content.DialogInterface;
import android.p008os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import com.android.internal.C4057R;
import java.util.Calendar;
/* loaded from: classes.dex */
public class DatePickerDialog extends AlertDialog implements DialogInterface.OnClickListener, DatePicker.OnDateChangedListener {
    private static final String DAY = "day";
    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private final DatePicker mDatePicker;
    private OnDateSetListener mDateSetListener;
    private final DatePicker.ValidationCallback mValidationCallback;

    /* loaded from: classes.dex */
    public interface OnDateSetListener {
        void onDateSet(DatePicker datePicker, int i, int i2, int i3);
    }

    public DatePickerDialog(Context context) {
        this(context, 0, null, Calendar.getInstance(), -1, -1, -1);
    }

    public DatePickerDialog(Context context, int themeResId) {
        this(context, themeResId, null, Calendar.getInstance(), -1, -1, -1);
    }

    public DatePickerDialog(Context context, OnDateSetListener listener, int year, int month, int dayOfMonth) {
        this(context, 0, listener, null, year, month, dayOfMonth);
    }

    public DatePickerDialog(Context context, int themeResId, OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        this(context, themeResId, listener, null, year, monthOfYear, dayOfMonth);
    }

    private DatePickerDialog(Context context, int themeResId, OnDateSetListener listener, Calendar calendar, int year, int monthOfYear, int dayOfMonth) {
        super(context, resolveDialogTheme(context, themeResId));
        DatePicker.ValidationCallback validationCallback = new DatePicker.ValidationCallback() { // from class: android.app.DatePickerDialog.1
            @Override // android.widget.DatePicker.ValidationCallback
            public void onValidationChanged(boolean valid) {
                Button positive = DatePickerDialog.this.getButton(-1);
                if (positive != null) {
                    positive.setEnabled(valid);
                }
            }
        };
        this.mValidationCallback = validationCallback;
        Context themeContext = getContext();
        LayoutInflater inflater = LayoutInflater.from(themeContext);
        View view = inflater.inflate(C4057R.layout.date_picker_dialog, (ViewGroup) null);
        setView(view);
        setButton(-1, themeContext.getString(17039370), this);
        setButton(-2, themeContext.getString(17039360), this);
        setButtonPanelLayoutHint(1);
        if (calendar != null) {
            year = calendar.get(1);
            monthOfYear = calendar.get(2);
            dayOfMonth = calendar.get(5);
        }
        DatePicker datePicker = (DatePicker) view.findViewById(C4057R.C4059id.datePicker);
        this.mDatePicker = datePicker;
        datePicker.init(year, monthOfYear, dayOfMonth, this);
        datePicker.setValidationCallback(validationCallback);
        this.mDateSetListener = listener;
    }

    static int resolveDialogTheme(Context context, int themeResId) {
        if (themeResId == 0) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(16843948, outValue, true);
            return outValue.resourceId;
        }
        return themeResId;
    }

    @Override // android.widget.DatePicker.OnDateChangedListener
    public void onDateChanged(DatePicker view, int year, int month, int dayOfMonth) {
        this.mDatePicker.init(year, month, dayOfMonth, this);
    }

    public void setOnDateSetListener(OnDateSetListener listener) {
        this.mDateSetListener = listener;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case -2:
                cancel();
                return;
            case -1:
                if (this.mDateSetListener != null) {
                    this.mDatePicker.clearFocus();
                    OnDateSetListener onDateSetListener = this.mDateSetListener;
                    DatePicker datePicker = this.mDatePicker;
                    onDateSetListener.onDateSet(datePicker, datePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth());
                    return;
                }
                return;
            default:
                return;
        }
    }

    public DatePicker getDatePicker() {
        return this.mDatePicker;
    }

    public void updateDate(int year, int month, int dayOfMonth) {
        this.mDatePicker.updateDate(year, month, dayOfMonth);
    }

    @Override // android.app.Dialog
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, this.mDatePicker.getYear());
        state.putInt(MONTH, this.mDatePicker.getMonth());
        state.putInt(DAY, this.mDatePicker.getDayOfMonth());
        return state;
    }

    @Override // android.app.Dialog
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        int day = savedInstanceState.getInt(DAY);
        this.mDatePicker.init(year, month, day, this);
    }
}
