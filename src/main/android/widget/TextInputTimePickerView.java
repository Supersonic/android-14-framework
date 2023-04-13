package android.widget;

import android.content.Context;
import android.p008os.LocaleList;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import com.android.internal.C4057R;
/* loaded from: classes4.dex */
public class TextInputTimePickerView extends RelativeLayout {

    /* renamed from: AM */
    private static final int f540AM = 0;
    public static final int AMPM = 2;
    public static final int HOURS = 0;
    public static final int MINUTES = 1;

    /* renamed from: PM */
    private static final int f541PM = 1;
    private final Spinner mAmPmSpinner;
    private final TextView mErrorLabel;
    private boolean mErrorShowing;
    private final EditText mHourEditText;
    private boolean mHourFormatStartsAtZero;
    private final TextView mHourLabel;
    private final TextView mInputSeparatorView;
    private boolean mIs24Hour;
    private OnValueTypedListener mListener;
    private final EditText mMinuteEditText;
    private final TextView mMinuteLabel;
    private boolean mTimeSet;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public interface OnValueTypedListener {
        void onValueChanged(int i, int i2);
    }

    public TextInputTimePickerView(Context context) {
        this(context, null);
    }

    public TextInputTimePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextInputTimePickerView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public TextInputTimePickerView(final Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle, defStyleRes);
        inflate(context, C4057R.layout.time_picker_text_input_material, this);
        EditText editText = (EditText) findViewById(C4057R.C4059id.input_hour);
        this.mHourEditText = editText;
        EditText editText2 = (EditText) findViewById(C4057R.C4059id.input_minute);
        this.mMinuteEditText = editText2;
        this.mInputSeparatorView = (TextView) findViewById(C4057R.C4059id.input_separator);
        this.mErrorLabel = (TextView) findViewById(C4057R.C4059id.label_error);
        this.mHourLabel = (TextView) findViewById(C4057R.C4059id.label_hour);
        this.mMinuteLabel = (TextView) findViewById(C4057R.C4059id.label_minute);
        editText.addTextChangedListener(new TextWatcher() { // from class: android.widget.TextInputTimePickerView.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (TextInputTimePickerView.this.parseAndSetHourInternal(editable.toString()) && editable.length() > 1) {
                    AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
                    if (!am.isEnabled()) {
                        TextInputTimePickerView.this.mMinuteEditText.requestFocus();
                    }
                }
            }
        });
        editText2.addTextChangedListener(new TextWatcher() { // from class: android.widget.TextInputTimePickerView.2
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                TextInputTimePickerView.this.parseAndSetMinuteInternal(editable.toString());
            }
        });
        Spinner spinner = (Spinner) findViewById(C4057R.C4059id.am_pm_spinner);
        this.mAmPmSpinner = spinner;
        String[] amPmStrings = TimePicker.getAmPmStrings(context);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(context, 17367049);
        adapter.add(TimePickerClockDelegate.obtainVerbatim(amPmStrings[0]));
        adapter.add(TimePickerClockDelegate.obtainVerbatim(amPmStrings[1]));
        spinner.setAdapter((SpinnerAdapter) adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: android.widget.TextInputTimePickerView.3
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    TextInputTimePickerView.this.mListener.onValueChanged(2, 0);
                } else {
                    TextInputTimePickerView.this.mListener.onValueChanged(2, 1);
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setListener(OnValueTypedListener listener) {
        this.mListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHourFormat(int maxCharLength) {
        this.mHourEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxCharLength)});
        this.mMinuteEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxCharLength)});
        LocaleList locales = this.mContext.getResources().getConfiguration().getLocales();
        this.mHourEditText.setImeHintLocales(locales);
        this.mMinuteEditText.setImeHintLocales(locales);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean validateInput() {
        String hourText;
        String minuteText;
        if (TextUtils.isEmpty(this.mHourEditText.getText())) {
            hourText = this.mHourEditText.getHint().toString();
        } else {
            hourText = this.mHourEditText.getText().toString();
        }
        if (TextUtils.isEmpty(this.mMinuteEditText.getText())) {
            minuteText = this.mMinuteEditText.getHint().toString();
        } else {
            minuteText = this.mMinuteEditText.getText().toString();
        }
        boolean inputValid = parseAndSetHourInternal(hourText) && parseAndSetMinuteInternal(minuteText);
        setError(inputValid ? false : true);
        return inputValid;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSeparator(String separatorText) {
        this.mInputSeparatorView.setText(separatorText);
    }

    private void setError(boolean enabled) {
        this.mErrorShowing = enabled;
        this.mErrorLabel.setVisibility(enabled ? 0 : 4);
        this.mHourLabel.setVisibility(enabled ? 4 : 0);
        this.mMinuteLabel.setVisibility(enabled ? 4 : 0);
    }

    private void setTimeSet(boolean timeSet) {
        this.mTimeSet = this.mTimeSet || timeSet;
    }

    private boolean isTimeSet() {
        return this.mTimeSet;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateTextInputValues(int localizedHour, int minute, int amOrPm, boolean is24Hour, boolean hourFormatStartsAtZero) {
        this.mIs24Hour = is24Hour;
        this.mHourFormatStartsAtZero = hourFormatStartsAtZero;
        this.mAmPmSpinner.setVisibility(is24Hour ? 4 : 0);
        if (amOrPm == 0) {
            this.mAmPmSpinner.setSelection(0);
        } else {
            this.mAmPmSpinner.setSelection(1);
        }
        if (isTimeSet()) {
            this.mHourEditText.setText(String.format("%d", Integer.valueOf(localizedHour)));
            this.mMinuteEditText.setText(String.format("%02d", Integer.valueOf(minute)));
        } else {
            this.mHourEditText.setHint(String.format("%d", Integer.valueOf(localizedHour)));
            this.mMinuteEditText.setHint(String.format("%02d", Integer.valueOf(minute)));
        }
        if (this.mErrorShowing) {
            validateInput();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean parseAndSetHourInternal(String input) {
        try {
            int hour = Integer.parseInt(input);
            if (isValidLocalizedHour(hour)) {
                this.mListener.onValueChanged(0, getHourOfDayFromLocalizedHour(hour));
                setTimeSet(true);
                return true;
            }
            int minHour = this.mHourFormatStartsAtZero ? 0 : 1;
            int maxHour = this.mIs24Hour ? 23 : minHour + 11;
            this.mListener.onValueChanged(0, getHourOfDayFromLocalizedHour(MathUtils.constrain(hour, minHour, maxHour)));
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean parseAndSetMinuteInternal(String input) {
        try {
            int minutes = Integer.parseInt(input);
            if (minutes >= 0 && minutes <= 59) {
                this.mListener.onValueChanged(1, minutes);
                setTimeSet(true);
                return true;
            }
            this.mListener.onValueChanged(1, MathUtils.constrain(minutes, 0, 59));
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidLocalizedHour(int localizedHour) {
        int minHour = !this.mHourFormatStartsAtZero ? 1 : 0;
        int maxHour = (this.mIs24Hour ? 23 : 11) + minHour;
        return localizedHour >= minHour && localizedHour <= maxHour;
    }

    private int getHourOfDayFromLocalizedHour(int localizedHour) {
        int hourOfDay = localizedHour;
        if (this.mIs24Hour) {
            if (!this.mHourFormatStartsAtZero && localizedHour == 24) {
                return 0;
            }
            return hourOfDay;
        }
        if (!this.mHourFormatStartsAtZero && localizedHour == 12) {
            hourOfDay = 0;
        }
        if (this.mAmPmSpinner.getSelectedItemPosition() == 1) {
            return hourOfDay + 12;
        }
        return hourOfDay;
    }
}
