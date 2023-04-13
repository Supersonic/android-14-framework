package android.text;
/* loaded from: classes3.dex */
public interface TextWatcher extends NoCopySpan {
    void afterTextChanged(Editable editable);

    void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3);

    void onTextChanged(CharSequence charSequence, int i, int i2, int i3);
}
