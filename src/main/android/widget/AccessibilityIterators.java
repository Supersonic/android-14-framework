package android.widget;

import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.view.AccessibilityIterators;
/* loaded from: classes4.dex */
final class AccessibilityIterators {
    AccessibilityIterators() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class LineTextSegmentIterator extends AccessibilityIterators.AbstractTextSegmentIterator {
        protected static final int DIRECTION_END = 1;
        protected static final int DIRECTION_START = -1;
        private static LineTextSegmentIterator sLineInstance;
        protected Layout mLayout;

        LineTextSegmentIterator() {
        }

        public static LineTextSegmentIterator getInstance() {
            if (sLineInstance == null) {
                sLineInstance = new LineTextSegmentIterator();
            }
            return sLineInstance;
        }

        public void initialize(Spannable text, Layout layout) {
            this.mText = text.toString();
            this.mLayout = layout;
        }

        @Override // android.view.AccessibilityIterators.TextSegmentIterator
        public int[] following(int offset) {
            int currentLine;
            int textLegth = this.mText.length();
            if (textLegth <= 0 || offset >= this.mText.length()) {
                return null;
            }
            if (offset < 0) {
                currentLine = this.mLayout.getLineForOffset(0);
            } else {
                currentLine = this.mLayout.getLineForOffset(offset);
                if (getLineEdgeIndex(currentLine, -1) != offset) {
                    int nextLine = currentLine + 1;
                    currentLine = nextLine;
                }
            }
            if (currentLine >= this.mLayout.getLineCount()) {
                return null;
            }
            int start = getLineEdgeIndex(currentLine, -1);
            int end = getLineEdgeIndex(currentLine, 1) + 1;
            return getRange(start, end);
        }

        @Override // android.view.AccessibilityIterators.TextSegmentIterator
        public int[] preceding(int offset) {
            int currentLine;
            int textLegth = this.mText.length();
            if (textLegth <= 0 || offset <= 0) {
                return null;
            }
            if (offset > this.mText.length()) {
                currentLine = this.mLayout.getLineForOffset(this.mText.length());
            } else {
                currentLine = this.mLayout.getLineForOffset(offset);
                if (getLineEdgeIndex(currentLine, 1) + 1 != offset) {
                    int previousLine = currentLine - 1;
                    currentLine = previousLine;
                }
            }
            if (currentLine < 0) {
                return null;
            }
            int start = getLineEdgeIndex(currentLine, -1);
            int end = getLineEdgeIndex(currentLine, 1) + 1;
            return getRange(start, end);
        }

        protected int getLineEdgeIndex(int lineNumber, int direction) {
            int paragraphDirection = this.mLayout.getParagraphDirection(lineNumber);
            if (direction * paragraphDirection < 0) {
                return this.mLayout.getLineStart(lineNumber);
            }
            return this.mLayout.getLineEnd(lineNumber) - 1;
        }
    }

    /* loaded from: classes4.dex */
    static class PageTextSegmentIterator extends LineTextSegmentIterator {
        private static PageTextSegmentIterator sPageInstance;
        private final Rect mTempRect = new Rect();
        private TextView mView;

        PageTextSegmentIterator() {
        }

        public static PageTextSegmentIterator getInstance() {
            if (sPageInstance == null) {
                sPageInstance = new PageTextSegmentIterator();
            }
            return sPageInstance;
        }

        public void initialize(TextView view) {
            super.initialize((Spannable) view.getIterableTextForAccessibility(), view.getLayout());
            this.mView = view;
        }

        @Override // android.widget.AccessibilityIterators.LineTextSegmentIterator, android.view.AccessibilityIterators.TextSegmentIterator
        public int[] following(int offset) {
            int textLength = this.mText.length();
            if (textLength <= 0 || offset >= this.mText.length() || !this.mView.getGlobalVisibleRect(this.mTempRect)) {
                return null;
            }
            int start = Math.max(0, offset);
            int currentLine = this.mLayout.getLineForOffset(start);
            int currentLineTop = this.mLayout.getLineTop(currentLine);
            int pageHeight = (this.mTempRect.height() - this.mView.getTotalPaddingTop()) - this.mView.getTotalPaddingBottom();
            int nextPageStartY = currentLineTop + pageHeight;
            int lastLineTop = this.mLayout.getLineTop(this.mLayout.getLineCount() - 1);
            int currentPageEndLine = (nextPageStartY < lastLineTop ? this.mLayout.getLineForVertical(nextPageStartY) : this.mLayout.getLineCount()) - 1;
            int end = getLineEdgeIndex(currentPageEndLine, 1) + 1;
            return getRange(start, end);
        }

        @Override // android.widget.AccessibilityIterators.LineTextSegmentIterator, android.view.AccessibilityIterators.TextSegmentIterator
        public int[] preceding(int offset) {
            int textLength = this.mText.length();
            if (textLength <= 0 || offset <= 0 || !this.mView.getGlobalVisibleRect(this.mTempRect)) {
                return null;
            }
            int end = Math.min(this.mText.length(), offset);
            int currentLine = this.mLayout.getLineForOffset(end);
            int currentLineTop = this.mLayout.getLineTop(currentLine);
            int pageHeight = (this.mTempRect.height() - this.mView.getTotalPaddingTop()) - this.mView.getTotalPaddingBottom();
            int previousPageEndY = currentLineTop - pageHeight;
            int currentPageStartLine = previousPageEndY > 0 ? this.mLayout.getLineForVertical(previousPageEndY) : 0;
            if (end == this.mText.length() && currentPageStartLine < currentLine) {
                currentPageStartLine++;
            }
            int start = getLineEdgeIndex(currentPageStartLine, -1);
            return getRange(start, end);
        }
    }
}
