package android.text.style;
/* loaded from: classes3.dex */
public interface TabStopSpan extends ParagraphStyle {
    int getTabStop();

    /* loaded from: classes3.dex */
    public static class Standard implements TabStopSpan {
        private int mTabOffset;

        public Standard(int offset) {
            this.mTabOffset = offset;
        }

        @Override // android.text.style.TabStopSpan
        public int getTabStop() {
            return this.mTabOffset;
        }

        public String toString() {
            return "TabStopSpan.Standard{tabOffset=" + getTabStop() + '}';
        }
    }
}
