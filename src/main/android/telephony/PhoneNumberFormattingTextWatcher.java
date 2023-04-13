package android.telephony;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.style.TtsSpan;
import com.android.i18n.phonenumbers.AsYouTypeFormatter;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import java.util.Locale;
/* loaded from: classes3.dex */
public class PhoneNumberFormattingTextWatcher implements TextWatcher {
    private AsYouTypeFormatter mFormatter;
    private boolean mSelfChange;
    private boolean mStopFormatting;

    public PhoneNumberFormattingTextWatcher() {
        this(Locale.getDefault().getCountry());
    }

    public PhoneNumberFormattingTextWatcher(String countryCode) {
        this.mSelfChange = false;
        if (countryCode == null) {
            throw new IllegalArgumentException();
        }
        this.mFormatter = PhoneNumberUtil.getInstance().getAsYouTypeFormatter(countryCode);
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (!this.mSelfChange && !this.mStopFormatting && count > 0 && hasSeparator(s, start, count)) {
            stopFormatting();
        }
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!this.mSelfChange && !this.mStopFormatting && count > 0 && hasSeparator(s, start, count)) {
            stopFormatting();
        }
    }

    @Override // android.text.TextWatcher
    public synchronized void afterTextChanged(Editable s) {
        boolean z = true;
        if (this.mStopFormatting) {
            if (s.length() == 0) {
                z = false;
            }
            this.mStopFormatting = z;
        } else if (!this.mSelfChange) {
            String formatted = reformat(s, Selection.getSelectionEnd(s));
            if (formatted != null) {
                int rememberedPos = this.mFormatter.getRememberedPosition();
                this.mSelfChange = true;
                s.replace(0, s.length(), formatted, 0, formatted.length());
                if (formatted.equals(s.toString())) {
                    Selection.setSelection(s, rememberedPos);
                }
                this.mSelfChange = false;
            }
            TtsSpan[] ttsSpans = (TtsSpan[]) s.getSpans(0, s.length(), TtsSpan.class);
            for (TtsSpan ttsSpan : ttsSpans) {
                s.removeSpan(ttsSpan);
            }
            PhoneNumberUtils.ttsSpanAsPhoneNumber(s, 0, s.length());
        }
    }

    private String reformat(CharSequence s, int cursor) {
        int curIndex = cursor - 1;
        String formatted = null;
        this.mFormatter.clear();
        char lastNonSeparator = 0;
        boolean hasCursor = false;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (PhoneNumberUtils.isNonSeparator(c)) {
                if (lastNonSeparator != 0) {
                    formatted = getFormattedNumber(lastNonSeparator, hasCursor);
                    hasCursor = false;
                }
                lastNonSeparator = c;
            }
            if (i == curIndex) {
                hasCursor = true;
            }
        }
        if (lastNonSeparator != 0) {
            String formatted2 = getFormattedNumber(lastNonSeparator, hasCursor);
            return formatted2;
        }
        return formatted;
    }

    private String getFormattedNumber(char lastNonSeparator, boolean hasCursor) {
        return hasCursor ? this.mFormatter.inputDigitAndRememberPosition(lastNonSeparator) : this.mFormatter.inputDigit(lastNonSeparator);
    }

    private void stopFormatting() {
        this.mStopFormatting = true;
        this.mFormatter.clear();
    }

    private boolean hasSeparator(CharSequence s, int start, int count) {
        for (int i = start; i < start + count; i++) {
            char c = s.charAt(i);
            if (!PhoneNumberUtils.isNonSeparator(c)) {
                return true;
            }
        }
        return false;
    }
}
