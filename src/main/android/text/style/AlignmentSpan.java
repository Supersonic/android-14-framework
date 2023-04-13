package android.text.style;

import android.p008os.Parcel;
import android.text.Layout;
import android.text.ParcelableSpan;
/* loaded from: classes3.dex */
public interface AlignmentSpan extends ParagraphStyle {
    Layout.Alignment getAlignment();

    /* loaded from: classes3.dex */
    public static class Standard implements AlignmentSpan, ParcelableSpan {
        private final Layout.Alignment mAlignment;

        public Standard(Layout.Alignment align) {
            this.mAlignment = align;
        }

        public Standard(Parcel src) {
            this.mAlignment = Layout.Alignment.valueOf(src.readString());
        }

        @Override // android.text.ParcelableSpan
        public int getSpanTypeId() {
            return getSpanTypeIdInternal();
        }

        @Override // android.text.ParcelableSpan
        public int getSpanTypeIdInternal() {
            return 1;
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
            dest.writeString(this.mAlignment.name());
        }

        @Override // android.text.style.AlignmentSpan
        public Layout.Alignment getAlignment() {
            return this.mAlignment;
        }

        public String toString() {
            return "AlignmentSpan.Standard{alignment=" + getAlignment() + '}';
        }
    }
}
