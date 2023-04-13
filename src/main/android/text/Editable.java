package android.text;
/* loaded from: classes3.dex */
public interface Editable extends CharSequence, GetChars, Spannable, Appendable {
    @Override // java.lang.Appendable
    Editable append(char c);

    @Override // java.lang.Appendable
    Editable append(CharSequence charSequence);

    @Override // java.lang.Appendable
    Editable append(CharSequence charSequence, int i, int i2);

    void clear();

    void clearSpans();

    Editable delete(int i, int i2);

    InputFilter[] getFilters();

    Editable insert(int i, CharSequence charSequence);

    Editable insert(int i, CharSequence charSequence, int i2, int i3);

    Editable replace(int i, int i2, CharSequence charSequence);

    Editable replace(int i, int i2, CharSequence charSequence, int i3, int i4);

    void setFilters(InputFilter[] inputFilterArr);

    /* loaded from: classes3.dex */
    public static class Factory {
        private static Factory sInstance = new Factory();

        public static Factory getInstance() {
            return sInstance;
        }

        public Editable newEditable(CharSequence source) {
            return new SpannableStringBuilder(source);
        }
    }
}
