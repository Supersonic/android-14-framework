package android.text;

import com.android.internal.util.Preconditions;
import java.util.Locale;
/* loaded from: classes3.dex */
public interface InputFilter {
    CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4);

    /* loaded from: classes3.dex */
    public static class AllCaps implements InputFilter {
        private final Locale mLocale;

        public AllCaps() {
            this.mLocale = null;
        }

        public AllCaps(Locale locale) {
            Preconditions.checkNotNull(locale);
            this.mLocale = locale;
        }

        @Override // android.text.InputFilter
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            boolean copySpans;
            CharSequence upper;
            CharSequence wrapper = new CharSequenceWrapper(source, start, end);
            boolean lowerOrTitleFound = false;
            int length = end - start;
            int i = 0;
            while (i < length) {
                int cp = Character.codePointAt(wrapper, i);
                if (!Character.isLowerCase(cp) && !Character.isTitleCase(cp)) {
                    i += Character.charCount(cp);
                } else {
                    lowerOrTitleFound = true;
                    break;
                }
            }
            if (lowerOrTitleFound && (upper = TextUtils.toUpperCase(this.mLocale, wrapper, (copySpans = source instanceof Spanned))) != wrapper) {
                return copySpans ? new SpannableString(upper) : upper.toString();
            }
            return null;
        }

        /* loaded from: classes3.dex */
        private static class CharSequenceWrapper implements CharSequence, Spanned {
            private final int mEnd;
            private final int mLength;
            private final CharSequence mSource;
            private final int mStart;

            CharSequenceWrapper(CharSequence source, int start, int end) {
                this.mSource = source;
                this.mStart = start;
                this.mEnd = end;
                this.mLength = end - start;
            }

            @Override // java.lang.CharSequence
            public int length() {
                return this.mLength;
            }

            @Override // java.lang.CharSequence
            public char charAt(int index) {
                if (index < 0 || index >= this.mLength) {
                    throw new IndexOutOfBoundsException();
                }
                return this.mSource.charAt(this.mStart + index);
            }

            @Override // java.lang.CharSequence
            public CharSequence subSequence(int start, int end) {
                if (start < 0 || end < 0 || end > this.mLength || start > end) {
                    throw new IndexOutOfBoundsException();
                }
                CharSequence charSequence = this.mSource;
                int i = this.mStart;
                return new CharSequenceWrapper(charSequence, i + start, i + end);
            }

            @Override // java.lang.CharSequence
            public String toString() {
                return this.mSource.subSequence(this.mStart, this.mEnd).toString();
            }

            @Override // android.text.Spanned
            public <T> T[] getSpans(int start, int end, Class<T> type) {
                int i = this.mStart;
                return (T[]) ((Spanned) this.mSource).getSpans(i + start, i + end, type);
            }

            @Override // android.text.Spanned
            public int getSpanStart(Object tag) {
                return ((Spanned) this.mSource).getSpanStart(tag) - this.mStart;
            }

            @Override // android.text.Spanned
            public int getSpanEnd(Object tag) {
                return ((Spanned) this.mSource).getSpanEnd(tag) - this.mStart;
            }

            @Override // android.text.Spanned
            public int getSpanFlags(Object tag) {
                return ((Spanned) this.mSource).getSpanFlags(tag);
            }

            @Override // android.text.Spanned
            public int nextSpanTransition(int start, int limit, Class type) {
                int i = this.mStart;
                return ((Spanned) this.mSource).nextSpanTransition(i + start, i + limit, type) - this.mStart;
            }
        }
    }

    /* loaded from: classes3.dex */
    public static class LengthFilter implements InputFilter {
        private final int mMax;

        public LengthFilter(int max) {
            this.mMax = max;
        }

        @Override // android.text.InputFilter
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            int keep = this.mMax - (dest.length() - (dend - dstart));
            if (keep <= 0) {
                return "";
            }
            if (keep >= end - start) {
                return null;
            }
            int keep2 = keep + start;
            if (Character.isHighSurrogate(source.charAt(keep2 - 1)) && keep2 - 1 == start) {
                return "";
            }
            return source.subSequence(start, keep2);
        }

        public int getMax() {
            return this.mMax;
        }
    }
}
