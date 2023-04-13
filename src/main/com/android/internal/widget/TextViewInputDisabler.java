package com.android.internal.widget;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.TextView;
/* loaded from: classes5.dex */
public class TextViewInputDisabler {
    private InputFilter[] mDefaultFilters;
    private InputFilter[] mNoInputFilters = {new InputFilter() { // from class: com.android.internal.widget.TextViewInputDisabler.1
        @Override // android.text.InputFilter
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            return "";
        }
    }};
    private TextView mTextView;

    public TextViewInputDisabler(TextView textView) {
        this.mTextView = textView;
        this.mDefaultFilters = textView.getFilters();
    }

    public void setInputEnabled(boolean enabled) {
        this.mTextView.setFilters(enabled ? this.mDefaultFilters : this.mNoInputFilters);
    }
}
