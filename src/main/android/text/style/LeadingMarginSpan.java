package android.text.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.p008os.Parcel;
import android.text.Layout;
import android.text.ParcelableSpan;
/* loaded from: classes3.dex */
public interface LeadingMarginSpan extends ParagraphStyle {

    /* loaded from: classes3.dex */
    public interface LeadingMarginSpan2 extends LeadingMarginSpan, WrapTogetherSpan {
        int getLeadingMarginLineCount();
    }

    void drawLeadingMargin(Canvas canvas, Paint paint, int i, int i2, int i3, int i4, int i5, CharSequence charSequence, int i6, int i7, boolean z, Layout layout);

    int getLeadingMargin(boolean z);

    /* loaded from: classes3.dex */
    public static class Standard implements LeadingMarginSpan, ParcelableSpan {
        private final int mFirst;
        private final int mRest;

        public Standard(int first, int rest) {
            this.mFirst = first;
            this.mRest = rest;
        }

        public Standard(int every) {
            this(every, every);
        }

        public Standard(Parcel src) {
            this.mFirst = src.readInt();
            this.mRest = src.readInt();
        }

        @Override // android.text.ParcelableSpan
        public int getSpanTypeId() {
            return getSpanTypeIdInternal();
        }

        @Override // android.text.ParcelableSpan
        public int getSpanTypeIdInternal() {
            return 10;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            writeToParcelInternal(dest, flags);
        }

        @Override // android.text.ParcelableSpan
        public void writeToParcelInternal(Parcel dest, int flags) {
            dest.writeInt(this.mFirst);
            dest.writeInt(this.mRest);
        }

        @Override // android.text.style.LeadingMarginSpan
        public int getLeadingMargin(boolean first) {
            return first ? this.mFirst : this.mRest;
        }

        @Override // android.text.style.LeadingMarginSpan
        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        }
    }
}
