package android.view;

import android.content.res.Configuration;
import android.view.ViewRootImpl;
import java.text.BreakIterator;
import java.util.Locale;
/* loaded from: classes4.dex */
public final class AccessibilityIterators {

    /* loaded from: classes4.dex */
    public interface TextSegmentIterator {
        int[] following(int i);

        int[] preceding(int i);
    }

    /* loaded from: classes4.dex */
    public static abstract class AbstractTextSegmentIterator implements TextSegmentIterator {
        private final int[] mSegment = new int[2];
        protected String mText;

        public void initialize(String text) {
            this.mText = text;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public int[] getRange(int start, int end) {
            if (start < 0 || end < 0 || start == end) {
                return null;
            }
            int[] iArr = this.mSegment;
            iArr[0] = start;
            iArr[1] = end;
            return iArr;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class CharacterTextSegmentIterator extends AbstractTextSegmentIterator implements ViewRootImpl.ConfigChangedCallback {
        private static CharacterTextSegmentIterator sInstance;
        protected BreakIterator mImpl;
        private Locale mLocale;

        public static CharacterTextSegmentIterator getInstance(Locale locale) {
            if (sInstance == null) {
                sInstance = new CharacterTextSegmentIterator(locale);
            }
            return sInstance;
        }

        private CharacterTextSegmentIterator(Locale locale) {
            this.mLocale = locale;
            onLocaleChanged(locale);
            ViewRootImpl.addConfigCallback(this);
        }

        @Override // android.view.AccessibilityIterators.AbstractTextSegmentIterator
        public void initialize(String text) {
            super.initialize(text);
            this.mImpl.setText(text);
        }

        @Override // android.view.AccessibilityIterators.TextSegmentIterator
        public int[] following(int offset) {
            int textLegth = this.mText.length();
            if (textLegth <= 0 || offset >= textLegth) {
                return null;
            }
            int start = offset;
            if (start < 0) {
                start = 0;
            }
            while (!this.mImpl.isBoundary(start)) {
                start = this.mImpl.following(start);
                if (start == -1) {
                    return null;
                }
            }
            int end = this.mImpl.following(start);
            if (end == -1) {
                return null;
            }
            return getRange(start, end);
        }

        @Override // android.view.AccessibilityIterators.TextSegmentIterator
        public int[] preceding(int offset) {
            int textLegth = this.mText.length();
            if (textLegth <= 0 || offset <= 0) {
                return null;
            }
            int end = offset;
            if (end > textLegth) {
                end = textLegth;
            }
            while (!this.mImpl.isBoundary(end)) {
                end = this.mImpl.preceding(end);
                if (end == -1) {
                    return null;
                }
            }
            int start = this.mImpl.preceding(end);
            if (start == -1) {
                return null;
            }
            return getRange(start, end);
        }

        @Override // android.view.ViewRootImpl.ConfigChangedCallback
        public void onConfigurationChanged(Configuration globalConfig) {
            Locale locale = globalConfig.getLocales().get(0);
            if (locale != null && !this.mLocale.equals(locale)) {
                this.mLocale = locale;
                onLocaleChanged(locale);
            }
        }

        protected void onLocaleChanged(Locale locale) {
            this.mImpl = BreakIterator.getCharacterInstance(locale);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class WordTextSegmentIterator extends CharacterTextSegmentIterator {
        private static WordTextSegmentIterator sInstance;

        public static WordTextSegmentIterator getInstance(Locale locale) {
            if (sInstance == null) {
                sInstance = new WordTextSegmentIterator(locale);
            }
            return sInstance;
        }

        private WordTextSegmentIterator(Locale locale) {
            super(locale);
        }

        @Override // android.view.AccessibilityIterators.CharacterTextSegmentIterator
        protected void onLocaleChanged(Locale locale) {
            this.mImpl = BreakIterator.getWordInstance(locale);
        }

        @Override // android.view.AccessibilityIterators.CharacterTextSegmentIterator, android.view.AccessibilityIterators.TextSegmentIterator
        public int[] following(int offset) {
            int textLegth = this.mText.length();
            if (textLegth <= 0 || offset >= this.mText.length()) {
                return null;
            }
            int start = offset;
            if (start < 0) {
                start = 0;
            }
            while (!isLetterOrDigit(start) && !isStartBoundary(start)) {
                start = this.mImpl.following(start);
                if (start == -1) {
                    return null;
                }
            }
            int end = this.mImpl.following(start);
            if (end == -1 || !isEndBoundary(end)) {
                return null;
            }
            return getRange(start, end);
        }

        @Override // android.view.AccessibilityIterators.CharacterTextSegmentIterator, android.view.AccessibilityIterators.TextSegmentIterator
        public int[] preceding(int offset) {
            int textLegth = this.mText.length();
            if (textLegth <= 0 || offset <= 0) {
                return null;
            }
            int end = offset;
            if (end > textLegth) {
                end = textLegth;
            }
            while (end > 0 && !isLetterOrDigit(end - 1) && !isEndBoundary(end)) {
                end = this.mImpl.preceding(end);
                if (end == -1) {
                    return null;
                }
            }
            int start = this.mImpl.preceding(end);
            if (start == -1 || !isStartBoundary(start)) {
                return null;
            }
            return getRange(start, end);
        }

        private boolean isStartBoundary(int index) {
            return isLetterOrDigit(index) && (index == 0 || !isLetterOrDigit(index + (-1)));
        }

        private boolean isEndBoundary(int index) {
            return index > 0 && isLetterOrDigit(index + (-1)) && (index == this.mText.length() || !isLetterOrDigit(index));
        }

        private boolean isLetterOrDigit(int index) {
            if (index >= 0 && index < this.mText.length()) {
                int codePoint = this.mText.codePointAt(index);
                return Character.isLetterOrDigit(codePoint);
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class ParagraphTextSegmentIterator extends AbstractTextSegmentIterator {
        private static ParagraphTextSegmentIterator sInstance;

        ParagraphTextSegmentIterator() {
        }

        public static ParagraphTextSegmentIterator getInstance() {
            if (sInstance == null) {
                sInstance = new ParagraphTextSegmentIterator();
            }
            return sInstance;
        }

        /* JADX WARN: Code restructure failed: missing block: B:17:0x0028, code lost:
            return null;
         */
        @Override // android.view.AccessibilityIterators.TextSegmentIterator
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public int[] following(int offset) {
            int textLength = this.mText.length();
            if (textLength <= 0 || offset >= textLength) {
                return null;
            }
            int start = offset;
            if (start < 0) {
                start = 0;
            }
            while (start < textLength && this.mText.charAt(start) == '\n' && !isStartBoundary(start)) {
                start++;
            }
            int end = start + 1;
            while (end < textLength && !isEndBoundary(end)) {
                end++;
            }
            return getRange(start, end);
        }

        /* JADX WARN: Code restructure failed: missing block: B:17:0x002a, code lost:
            return null;
         */
        @Override // android.view.AccessibilityIterators.TextSegmentIterator
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public int[] preceding(int offset) {
            int textLength = this.mText.length();
            if (textLength <= 0 || offset <= 0) {
                return null;
            }
            int end = offset;
            if (end > textLength) {
                end = textLength;
            }
            while (end > 0 && this.mText.charAt(end - 1) == '\n' && !isEndBoundary(end)) {
                end--;
            }
            int start = end - 1;
            while (start > 0 && !isStartBoundary(start)) {
                start--;
            }
            return getRange(start, end);
        }

        private boolean isStartBoundary(int index) {
            return this.mText.charAt(index) != '\n' && (index == 0 || this.mText.charAt(index + (-1)) == '\n');
        }

        private boolean isEndBoundary(int index) {
            return index > 0 && this.mText.charAt(index + (-1)) != '\n' && (index == this.mText.length() || this.mText.charAt(index) == '\n');
        }
    }
}
