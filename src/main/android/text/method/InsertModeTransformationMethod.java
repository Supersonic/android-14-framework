package android.text.method;

import android.graphics.Canvas;
import android.graphics.FontListParser;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.CallLog;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.OffsetMapping;
import android.text.style.ReplacementSpan;
import android.util.DisplayMetrics;
import android.util.MathUtils;
import android.util.TypedValue;
import android.view.View;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
/* loaded from: classes3.dex */
public class InsertModeTransformationMethod implements TransformationMethod, TextWatcher {
    private int mEnd;
    private final TransformationMethod mOldTransformationMethod;
    private final boolean mSingleLine;
    private int mStart;

    public InsertModeTransformationMethod(int offset, boolean singleLine, TransformationMethod oldTransformationMethod) {
        this(offset, offset, singleLine, oldTransformationMethod);
    }

    private InsertModeTransformationMethod(int start, int end, boolean singleLine, TransformationMethod oldTransformationMethod) {
        this.mStart = start;
        this.mEnd = end;
        this.mSingleLine = singleLine;
        this.mOldTransformationMethod = oldTransformationMethod;
    }

    public InsertModeTransformationMethod update(TransformationMethod oldTransformationMethod, boolean singleLine) {
        return new InsertModeTransformationMethod(this.mStart, this.mEnd, singleLine, oldTransformationMethod);
    }

    public TransformationMethod getOldTransformationMethod() {
        return this.mOldTransformationMethod;
    }

    private CharSequence getPlaceholderText(View view) {
        if (!this.mSingleLine) {
            return "\n\n";
        }
        SpannableString singleLinePlaceholder = new SpannableString("�");
        DisplayMetrics displayMetrics = view.getResources().getDisplayMetrics();
        int widthPx = (int) Math.ceil(TypedValue.applyDimension(1, 108.0f, displayMetrics));
        singleLinePlaceholder.setSpan(new SingleLinePlaceholderSpan(widthPx), 0, 1, 33);
        return singleLinePlaceholder;
    }

    @Override // android.text.method.TransformationMethod
    public CharSequence getTransformation(CharSequence source, View view) {
        CharSequence charSequence;
        TransformationMethod transformationMethod = this.mOldTransformationMethod;
        if (transformationMethod != null) {
            charSequence = transformationMethod.getTransformation(source, view);
            if (source instanceof Spannable) {
                Spannable spannable = (Spannable) source;
                spannable.setSpan(this.mOldTransformationMethod, 0, spannable.length(), 18);
            }
        } else {
            charSequence = source;
        }
        CharSequence placeholderText = getPlaceholderText(view);
        return new TransformedText(charSequence, placeholderText);
    }

    @Override // android.text.method.TransformationMethod
    public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {
        TransformationMethod transformationMethod = this.mOldTransformationMethod;
        if (transformationMethod != null) {
            transformationMethod.onFocusChanged(view, sourceText, focused, direction, previouslyFocusedRect);
        }
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int i = this.mEnd;
        if (start > i) {
            return;
        }
        int diff = count - before;
        int i2 = this.mStart;
        if (start < i2) {
            if (start + before <= i2) {
                this.mStart = i2 + diff;
            } else {
                this.mStart = start;
            }
        }
        if (start + before <= i) {
            this.mEnd = i + diff;
        } else if (start < i) {
            this.mEnd = start + count;
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
    }

    /* loaded from: classes3.dex */
    public class TransformedText implements OffsetMapping, Spanned {
        private final CharSequence mOriginal;
        private final CharSequence mPlaceholder;
        private final Spanned mSpannedOriginal;
        private final Spanned mSpannedPlaceholder;

        TransformedText(CharSequence original, CharSequence placeholder) {
            this.mOriginal = original;
            if (original instanceof Spanned) {
                this.mSpannedOriginal = (Spanned) original;
            } else {
                this.mSpannedOriginal = null;
            }
            this.mPlaceholder = placeholder;
            if (placeholder instanceof Spanned) {
                this.mSpannedPlaceholder = (Spanned) placeholder;
            } else {
                this.mSpannedPlaceholder = null;
            }
        }

        @Override // android.text.method.OffsetMapping
        public int originalToTransformed(int offset, int strategy) {
            if (offset < 0) {
                return offset;
            }
            Preconditions.checkArgumentInRange(offset, 0, this.mOriginal.length(), CallLog.Calls.OFFSET_PARAM_KEY);
            if (offset == InsertModeTransformationMethod.this.mEnd && strategy == 1) {
                return offset;
            }
            if (offset < InsertModeTransformationMethod.this.mEnd) {
                return offset;
            }
            return this.mPlaceholder.length() + offset;
        }

        @Override // android.text.method.OffsetMapping
        public int transformedToOriginal(int offset, int strategy) {
            if (offset < 0) {
                return offset;
            }
            Preconditions.checkArgumentInRange(offset, 0, length(), CallLog.Calls.OFFSET_PARAM_KEY);
            if (offset < InsertModeTransformationMethod.this.mEnd) {
                return offset;
            }
            if (offset < InsertModeTransformationMethod.this.mEnd + this.mPlaceholder.length()) {
                return InsertModeTransformationMethod.this.mEnd;
            }
            return offset - this.mPlaceholder.length();
        }

        @Override // android.text.method.OffsetMapping
        public void originalToTransformed(OffsetMapping.TextUpdate textUpdate) {
            if (textUpdate.where > InsertModeTransformationMethod.this.mEnd) {
                textUpdate.where += this.mPlaceholder.length();
            } else if (textUpdate.where + textUpdate.before > InsertModeTransformationMethod.this.mEnd) {
                textUpdate.before += this.mPlaceholder.length();
                textUpdate.after += this.mPlaceholder.length();
            }
        }

        @Override // java.lang.CharSequence
        public int length() {
            return this.mOriginal.length() + this.mPlaceholder.length();
        }

        @Override // java.lang.CharSequence
        public char charAt(int index) {
            Preconditions.checkArgumentInRange(index, 0, length() - 1, FontListParser.ATTR_INDEX);
            if (index < InsertModeTransformationMethod.this.mEnd) {
                return this.mOriginal.charAt(index);
            }
            if (index < InsertModeTransformationMethod.this.mEnd + this.mPlaceholder.length()) {
                return this.mPlaceholder.charAt(index - InsertModeTransformationMethod.this.mEnd);
            }
            return this.mOriginal.charAt(index - this.mPlaceholder.length());
        }

        @Override // java.lang.CharSequence
        public CharSequence subSequence(int start, int end) {
            if (end < start || start < 0 || end > length()) {
                throw new IndexOutOfBoundsException();
            }
            if (start == end) {
                return "";
            }
            int placeholderLength = this.mPlaceholder.length();
            int seg1Start = Math.min(start, InsertModeTransformationMethod.this.mEnd);
            int seg1End = Math.min(end, InsertModeTransformationMethod.this.mEnd);
            int seg2Start = MathUtils.constrain(start - InsertModeTransformationMethod.this.mEnd, 0, placeholderLength);
            int seg2End = MathUtils.constrain(end - InsertModeTransformationMethod.this.mEnd, 0, placeholderLength);
            int seg3Start = Math.max(start - placeholderLength, InsertModeTransformationMethod.this.mEnd);
            int seg3End = Math.max(end - placeholderLength, InsertModeTransformationMethod.this.mEnd);
            return TextUtils.concat(this.mOriginal.subSequence(seg1Start, seg1End), this.mPlaceholder.subSequence(seg2Start, seg2End), this.mOriginal.subSequence(seg3Start, seg3End));
        }

        @Override // java.lang.CharSequence
        public String toString() {
            return String.valueOf(this.mOriginal.subSequence(0, InsertModeTransformationMethod.this.mEnd)) + ((Object) this.mPlaceholder) + ((Object) this.mOriginal.subSequence(InsertModeTransformationMethod.this.mEnd, this.mOriginal.length()));
        }

        @Override // android.text.Spanned
        public <T> T[] getSpans(int start, int end, Class<T> type) {
            Object[] objArr;
            Object[] objArr2;
            if (end < start) {
                return (T[]) ArrayUtils.emptyArray(type);
            }
            if (this.mSpannedOriginal != null) {
                int originalStart = transformedToOriginal(start, 1);
                int originalEnd = transformedToOriginal(end, 1);
                objArr = this.mSpannedOriginal.getSpans(originalStart, originalEnd, type);
            } else {
                objArr = null;
            }
            if (this.mSpannedPlaceholder != null && InsertModeTransformationMethod.intersect(start, end, InsertModeTransformationMethod.this.mEnd, InsertModeTransformationMethod.this.mEnd + this.mPlaceholder.length())) {
                int placeholderStart = Math.max(start - InsertModeTransformationMethod.this.mEnd, 0);
                int placeholderEnd = Math.min(end - InsertModeTransformationMethod.this.mEnd, this.mPlaceholder.length());
                objArr2 = this.mSpannedPlaceholder.getSpans(placeholderStart, placeholderEnd, type);
            } else {
                objArr2 = null;
            }
            return (T[]) ArrayUtils.concat(type, objArr, objArr2);
        }

        @Override // android.text.Spanned
        public int getSpanStart(Object tag) {
            int index;
            int index2;
            Spanned spanned = this.mSpannedOriginal;
            if (spanned != null && (index2 = spanned.getSpanStart(tag)) >= 0) {
                if (index2 < InsertModeTransformationMethod.this.mEnd) {
                    return index2;
                }
                return this.mPlaceholder.length() + index2;
            }
            Spanned spanned2 = this.mSpannedPlaceholder;
            if (spanned2 != null && (index = spanned2.getSpanStart(tag)) >= 0) {
                return InsertModeTransformationMethod.this.mEnd + index;
            }
            return -1;
        }

        @Override // android.text.Spanned
        public int getSpanEnd(Object tag) {
            int index;
            int index2;
            Spanned spanned = this.mSpannedOriginal;
            if (spanned != null && (index2 = spanned.getSpanEnd(tag)) >= 0) {
                if (index2 <= InsertModeTransformationMethod.this.mEnd) {
                    return index2;
                }
                return this.mPlaceholder.length() + index2;
            }
            Spanned spanned2 = this.mSpannedPlaceholder;
            if (spanned2 != null && (index = spanned2.getSpanEnd(tag)) >= 0) {
                return InsertModeTransformationMethod.this.mEnd + index;
            }
            return -1;
        }

        @Override // android.text.Spanned
        public int getSpanFlags(Object tag) {
            int flags;
            Spanned spanned = this.mSpannedOriginal;
            if (spanned != null && (flags = spanned.getSpanFlags(tag)) != 0) {
                return flags;
            }
            Spanned spanned2 = this.mSpannedPlaceholder;
            if (spanned2 != null) {
                return spanned2.getSpanFlags(tag);
            }
            return 0;
        }

        @Override // android.text.Spanned
        public int nextSpanTransition(int start, int limit, Class type) {
            if (limit <= start) {
                return limit;
            }
            Object[] spans = getSpans(start, limit, type);
            for (int i = 0; i < spans.length; i++) {
                int spanStart = getSpanStart(spans[i]);
                int spanEnd = getSpanEnd(spans[i]);
                if (start < spanStart && spanStart < limit) {
                    limit = spanStart;
                }
                if (start < spanEnd && spanEnd < limit) {
                    limit = spanEnd;
                }
            }
            return limit;
        }

        public int getHighlightStart() {
            return InsertModeTransformationMethod.this.mStart;
        }

        public int getHighlightEnd() {
            return InsertModeTransformationMethod.this.mEnd + this.mPlaceholder.length();
        }
    }

    /* loaded from: classes3.dex */
    public static class SingleLinePlaceholderSpan extends ReplacementSpan {
        private final int mWidth;

        SingleLinePlaceholderSpan(int width) {
            this.mWidth = width;
        }

        @Override // android.text.style.ReplacementSpan
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            return this.mWidth;
        }

        @Override // android.text.style.ReplacementSpan
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean intersect(int s1, int e1, int s2, int e2) {
        if (s1 > e2 || e1 < s2) {
            return false;
        }
        if (s1 != e1 && s2 != e2) {
            if (s1 == e2 || e1 == s2) {
                return false;
            }
            return true;
        }
        return true;
    }
}
