package android.widget;

import android.text.Editable;
import android.text.Selection;
import android.text.SpannedString;
import android.text.method.WordIterator;
import android.text.style.SpellCheckSpan;
import android.text.style.SuggestionSpan;
import android.util.Log;
import android.util.Range;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.text.BreakIterator;
import java.util.Locale;
/* loaded from: classes4.dex */
public class SpellChecker implements SpellCheckerSession.SpellCheckerSessionListener {
    public static final int AVERAGE_WORD_LENGTH = 7;
    private static final boolean DBG = false;
    public static final int MAX_NUMBER_OF_WORDS = 50;
    private static final int MAX_SENTENCE_LENGTH = 350;
    private static final int SPELL_PAUSE_DURATION = 400;
    private static final String TAG = SpellChecker.class.getSimpleName();
    private static final int USE_SPAN_RANGE = -1;
    public static final int WORD_ITERATOR_INTERVAL = 350;
    final int mCookie;
    private Locale mCurrentLocale;
    private int[] mIds;
    private int mLength;
    private SentenceIteratorWrapper mSentenceIterator;
    private SpellCheckSpan[] mSpellCheckSpans;
    SpellCheckerSession mSpellCheckerSession;
    private Runnable mSpellRunnable;
    private TextServicesManager mTextServicesManager;
    private final TextView mTextView;
    private SpellParser[] mSpellParsers = new SpellParser[0];
    private int mSpanSequenceCounter = 0;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public enum RemoveReason {
        REPLACE,
        OBSOLETE
    }

    public SpellChecker(TextView textView) {
        this.mTextView = textView;
        int[] newUnpaddedIntArray = ArrayUtils.newUnpaddedIntArray(1);
        this.mIds = newUnpaddedIntArray;
        this.mSpellCheckSpans = new SpellCheckSpan[newUnpaddedIntArray.length];
        setLocale(textView.getSpellCheckerLocale());
        this.mCookie = hashCode();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetSession() {
        closeSession();
        TextServicesManager textServicesManagerForUser = this.mTextView.getTextServicesManagerForUser();
        this.mTextServicesManager = textServicesManagerForUser;
        if (this.mCurrentLocale == null || textServicesManagerForUser == null || this.mTextView.length() == 0 || !this.mTextServicesManager.isSpellCheckerEnabled() || this.mTextServicesManager.getCurrentSpellCheckerSubtype(true) == null) {
            this.mSpellCheckerSession = null;
        } else {
            SpellCheckerSession.SpellCheckerSessionParams params = new SpellCheckerSession.SpellCheckerSessionParams.Builder().setLocale(this.mCurrentLocale).setSupportedAttributes(27).build();
            this.mSpellCheckerSession = this.mTextServicesManager.newSpellCheckerSession(params, this.mTextView.getContext().getMainExecutor(), this);
        }
        for (int i = 0; i < this.mLength; i++) {
            this.mIds[i] = -1;
        }
        this.mLength = 0;
        TextView textView = this.mTextView;
        textView.removeMisspelledSpans((Editable) textView.getText());
    }

    private void setLocale(Locale locale) {
        this.mCurrentLocale = locale;
        resetSession();
        if (locale != null) {
            this.mSentenceIterator = new SentenceIteratorWrapper(BreakIterator.getSentenceInstance(locale));
        }
        this.mTextView.onLocaleChanged();
    }

    private boolean isSessionActive() {
        return this.mSpellCheckerSession != null;
    }

    public void closeSession() {
        SpellCheckerSession spellCheckerSession = this.mSpellCheckerSession;
        if (spellCheckerSession != null) {
            spellCheckerSession.close();
        }
        int length = this.mSpellParsers.length;
        for (int i = 0; i < length; i++) {
            this.mSpellParsers[i].stop();
        }
        Runnable runnable = this.mSpellRunnable;
        if (runnable != null) {
            this.mTextView.removeCallbacks(runnable);
        }
    }

    private int nextSpellCheckSpanIndex() {
        int i = 0;
        while (true) {
            int i2 = this.mLength;
            if (i < i2) {
                if (this.mIds[i] < 0) {
                    return i;
                }
                i++;
            } else {
                this.mIds = GrowingArrayUtils.append(this.mIds, i2, 0);
                this.mSpellCheckSpans = (SpellCheckSpan[]) GrowingArrayUtils.append(this.mSpellCheckSpans, this.mLength, new SpellCheckSpan());
                int i3 = this.mLength + 1;
                this.mLength = i3;
                return i3 - 1;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addSpellCheckSpan(Editable editable, int start, int end) {
        int index = nextSpellCheckSpanIndex();
        SpellCheckSpan spellCheckSpan = this.mSpellCheckSpans[index];
        editable.setSpan(spellCheckSpan, start, end, 33);
        spellCheckSpan.setSpellCheckInProgress(false);
        int[] iArr = this.mIds;
        int i = this.mSpanSequenceCounter;
        this.mSpanSequenceCounter = i + 1;
        iArr[index] = i;
    }

    public void onSpellCheckSpanRemoved(SpellCheckSpan spellCheckSpan) {
        for (int i = 0; i < this.mLength; i++) {
            if (this.mSpellCheckSpans[i] == spellCheckSpan) {
                this.mIds[i] = -1;
                return;
            }
        }
    }

    public void onSelectionChanged() {
        spellCheck();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onPerformSpellCheck() {
        int end = this.mTextView.length();
        spellCheck(0, end, true);
    }

    public void spellCheck(int start, int end) {
        spellCheck(start, end, false);
    }

    public void spellCheck(int start, int end, boolean forceCheckWhenEditingWord) {
        Locale locale;
        Locale locale2 = this.mTextView.getSpellCheckerLocale();
        boolean isSessionActive = isSessionActive();
        if (locale2 == null || (locale = this.mCurrentLocale) == null || !locale.equals(locale2)) {
            setLocale(locale2);
            start = 0;
            end = this.mTextView.getText().length();
        } else {
            TextServicesManager textServicesManager = this.mTextServicesManager;
            boolean spellCheckerActivated = textServicesManager != null && textServicesManager.isSpellCheckerEnabled();
            if (isSessionActive != spellCheckerActivated) {
                resetSession();
            }
        }
        if (isSessionActive) {
            int length = this.mSpellParsers.length;
            for (int i = 0; i < length; i++) {
                SpellParser spellParser = this.mSpellParsers[i];
                if (spellParser.isFinished()) {
                    spellParser.parse(start, end, forceCheckWhenEditingWord);
                    return;
                }
            }
            int i2 = length + 1;
            SpellParser[] newSpellParsers = new SpellParser[i2];
            System.arraycopy(this.mSpellParsers, 0, newSpellParsers, 0, length);
            this.mSpellParsers = newSpellParsers;
            SpellParser spellParser2 = new SpellParser();
            this.mSpellParsers[length] = spellParser2;
            spellParser2.parse(start, end, forceCheckWhenEditingWord);
        }
    }

    private void spellCheck() {
        spellCheck(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void spellCheck(boolean forceCheckWhenEditingWord) {
        boolean isNotEditing;
        if (this.mSpellCheckerSession == null) {
            return;
        }
        Editable editable = (Editable) this.mTextView.getText();
        int selectionStart = Selection.getSelectionStart(editable);
        int selectionEnd = Selection.getSelectionEnd(editable);
        TextInfo[] textInfos = new TextInfo[this.mLength];
        int textInfosCount = 0;
        int i = 0;
        while (true) {
            int textInfosCount2 = this.mLength;
            boolean z = false;
            if (i >= textInfosCount2) {
                break;
            }
            SpellCheckSpan spellCheckSpan = this.mSpellCheckSpans[i];
            if (this.mIds[i] >= 0 && !spellCheckSpan.isSpellCheckInProgress()) {
                int start = editable.getSpanStart(spellCheckSpan);
                int end = editable.getSpanEnd(spellCheckSpan);
                if (selectionStart == end + 1 && WordIterator.isMidWordPunctuation(this.mCurrentLocale, Character.codePointBefore(editable, end + 1))) {
                    isNotEditing = false;
                } else if (selectionEnd <= start || selectionStart > end) {
                    isNotEditing = true;
                } else {
                    if (selectionStart == end && selectionStart > 0 && isSeparator(Character.codePointBefore(editable, selectionStart))) {
                        z = true;
                    }
                    isNotEditing = z;
                }
                if (start >= 0 && end > start && (forceCheckWhenEditingWord || isNotEditing)) {
                    spellCheckSpan.setSpellCheckInProgress(true);
                    TextInfo textInfo = new TextInfo(editable, start, end, this.mCookie, this.mIds[i]);
                    textInfos[textInfosCount] = textInfo;
                    textInfosCount++;
                }
            }
            i++;
        }
        if (textInfosCount > 0) {
            if (textInfosCount < textInfos.length) {
                TextInfo[] textInfosCopy = new TextInfo[textInfosCount];
                System.arraycopy(textInfos, 0, textInfosCopy, 0, textInfosCount);
                textInfos = textInfosCopy;
            }
            this.mSpellCheckerSession.getSentenceSuggestions(textInfos, 5);
        }
    }

    private static boolean isSeparator(int codepoint) {
        int type = Character.getType(codepoint);
        return ((1 << type) & 1634758656) != 0;
    }

    private SpellCheckSpan onGetSuggestionsInternal(SuggestionsInfo suggestionsInfo, int offset, int length) {
        int start;
        int end;
        if (suggestionsInfo == null || suggestionsInfo.getCookie() != this.mCookie) {
            return null;
        }
        Editable editable = (Editable) this.mTextView.getText();
        int sequenceNumber = suggestionsInfo.getSequence();
        for (int k = 0; k < this.mLength; k++) {
            if (sequenceNumber == this.mIds[k]) {
                SpellCheckSpan spellCheckSpan = this.mSpellCheckSpans[k];
                int spellCheckSpanStart = editable.getSpanStart(spellCheckSpan);
                if (spellCheckSpanStart < 0) {
                    return null;
                }
                int attributes = suggestionsInfo.getSuggestionsAttributes();
                boolean isInDictionary = (attributes & 1) > 0;
                boolean looksLikeTypo = (attributes & 2) > 0;
                boolean looksLikeGrammarError = (attributes & 8) > 0;
                if (spellCheckSpanStart + offset + length > editable.length()) {
                    return spellCheckSpan;
                }
                if (!isInDictionary && (looksLikeTypo || looksLikeGrammarError)) {
                    createMisspelledSuggestionSpan(editable, suggestionsInfo, spellCheckSpan, offset, length);
                } else {
                    int spellCheckSpanEnd = editable.getSpanEnd(spellCheckSpan);
                    if (offset != -1 && length != -1) {
                        start = spellCheckSpanStart + offset;
                        end = start + length;
                    } else {
                        start = spellCheckSpanStart;
                        end = spellCheckSpanEnd;
                    }
                    if (spellCheckSpanStart >= 0 && spellCheckSpanEnd > spellCheckSpanStart && end > start) {
                        boolean visibleToAccessibility = this.mTextView.isVisibleToAccessibility();
                        CharSequence beforeText = visibleToAccessibility ? new SpannedString(editable) : null;
                        boolean spanRemoved = removeErrorSuggestionSpan(editable, start, end, RemoveReason.OBSOLETE);
                        if (visibleToAccessibility && spanRemoved) {
                            this.mTextView.sendAccessibilityEventTypeViewTextChanged(beforeText, start, end);
                        }
                    }
                }
                return spellCheckSpan;
            }
        }
        return null;
    }

    private static boolean removeErrorSuggestionSpan(Editable editable, int start, int end, RemoveReason reason) {
        boolean spanRemoved = false;
        SuggestionSpan[] spans = (SuggestionSpan[]) editable.getSpans(start, end, SuggestionSpan.class);
        for (SuggestionSpan span : spans) {
            if (editable.getSpanStart(span) == start && editable.getSpanEnd(span) == end && (span.getFlags() & 10) != 0) {
                editable.removeSpan(span);
                spanRemoved = true;
            }
        }
        return spanRemoved;
    }

    @Override // android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener
    public void onGetSuggestions(SuggestionsInfo[] results) {
        Editable editable = (Editable) this.mTextView.getText();
        for (SuggestionsInfo suggestionsInfo : results) {
            SpellCheckSpan spellCheckSpan = onGetSuggestionsInternal(suggestionsInfo, -1, -1);
            if (spellCheckSpan != null) {
                editable.removeSpan(spellCheckSpan);
            }
        }
        scheduleNewSpellCheck();
    }

    @Override // android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
        Editable editable = (Editable) this.mTextView.getText();
        for (SentenceSuggestionsInfo ssi : results) {
            if (ssi != null) {
                SpellCheckSpan spellCheckSpan = null;
                for (int j = 0; j < ssi.getSuggestionsCount(); j++) {
                    SuggestionsInfo suggestionsInfo = ssi.getSuggestionsInfoAt(j);
                    if (suggestionsInfo != null) {
                        int offset = ssi.getOffsetAt(j);
                        int length = ssi.getLengthAt(j);
                        SpellCheckSpan scs = onGetSuggestionsInternal(suggestionsInfo, offset, length);
                        if (spellCheckSpan == null && scs != null) {
                            spellCheckSpan = scs;
                        }
                    }
                }
                if (spellCheckSpan != null) {
                    editable.removeSpan(spellCheckSpan);
                }
            }
        }
        scheduleNewSpellCheck();
    }

    private void scheduleNewSpellCheck() {
        Runnable runnable = this.mSpellRunnable;
        if (runnable == null) {
            this.mSpellRunnable = new Runnable() { // from class: android.widget.SpellChecker.1
                @Override // java.lang.Runnable
                public void run() {
                    int length = SpellChecker.this.mSpellParsers.length;
                    for (int i = 0; i < length; i++) {
                        SpellParser spellParser = SpellChecker.this.mSpellParsers[i];
                        if (!spellParser.isFinished()) {
                            spellParser.parse();
                            return;
                        }
                    }
                }
            };
        } else {
            this.mTextView.removeCallbacks(runnable);
        }
        this.mTextView.postDelayed(this.mSpellRunnable, 400L);
    }

    private void createMisspelledSuggestionSpan(Editable editable, SuggestionsInfo suggestionsInfo, SpellCheckSpan spellCheckSpan, int offset, int length) {
        int start;
        int end;
        String[] suggestions;
        int spellCheckSpanStart = editable.getSpanStart(spellCheckSpan);
        int spellCheckSpanEnd = editable.getSpanEnd(spellCheckSpan);
        if (spellCheckSpanStart >= 0 && spellCheckSpanEnd > spellCheckSpanStart) {
            if (offset != -1 && length != -1) {
                start = spellCheckSpanStart + offset;
                end = start + length;
            } else {
                start = spellCheckSpanStart;
                end = spellCheckSpanEnd;
            }
            int suggestionsCount = suggestionsInfo.getSuggestionsCount();
            if (suggestionsCount > 0) {
                suggestions = new String[suggestionsCount];
                for (int i = 0; i < suggestionsCount; i++) {
                    suggestions[i] = suggestionsInfo.getSuggestionAt(i);
                }
            } else {
                suggestions = (String[]) ArrayUtils.emptyArray(String.class);
            }
            int suggestionsAttrs = suggestionsInfo.getSuggestionsAttributes();
            int flags = 0;
            if ((suggestionsAttrs & 16) == 0) {
                flags = 0 | 1;
            }
            if ((suggestionsAttrs & 2) != 0) {
                flags |= 2;
            }
            if ((suggestionsAttrs & 8) != 0) {
                flags |= 8;
            }
            SuggestionSpan suggestionSpan = new SuggestionSpan(this.mTextView.getContext(), suggestions, flags);
            boolean spanRemoved = removeErrorSuggestionSpan(editable, start, end, RemoveReason.REPLACE);
            boolean sendAccessibilityEvent = !spanRemoved && this.mTextView.isVisibleToAccessibility();
            CharSequence beforeText = sendAccessibilityEvent ? new SpannedString(editable) : null;
            editable.setSpan(suggestionSpan, start, end, 33);
            if (sendAccessibilityEvent) {
                this.mTextView.sendAccessibilityEventTypeViewTextChanged(beforeText, start, end);
            }
            this.mTextView.invalidateRegion(start, end, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class SentenceIteratorWrapper {
        private int mEndOffset;
        private BreakIterator mSentenceIterator;
        private int mStartOffset;

        SentenceIteratorWrapper(BreakIterator sentenceIterator) {
            this.mSentenceIterator = sentenceIterator;
        }

        public void setCharSequence(CharSequence sequence, int start, int end) {
            this.mStartOffset = Math.max(0, start);
            int min = Math.min(end, sequence.length());
            this.mEndOffset = min;
            this.mSentenceIterator.setText(sequence.subSequence(this.mStartOffset, min).toString());
        }

        public int preceding(int offset) {
            int result;
            int i = this.mStartOffset;
            if (offset >= i && (result = this.mSentenceIterator.preceding(offset - i)) != -1) {
                return this.mStartOffset + result;
            }
            return -1;
        }

        public int following(int offset) {
            int result;
            if (offset <= this.mEndOffset && (result = this.mSentenceIterator.following(offset - this.mStartOffset)) != -1) {
                return this.mStartOffset + result;
            }
            return -1;
        }

        public boolean isBoundary(int offset) {
            int i = this.mStartOffset;
            if (offset < i || offset > this.mEndOffset) {
                return false;
            }
            return this.mSentenceIterator.isBoundary(offset - i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class SpellParser {
        private boolean mForceCheckWhenEditingWord;
        private Object mRange;

        private SpellParser() {
            this.mRange = new Object();
        }

        public void parse(int start, int end, boolean forceCheckWhenEditingWord) {
            int parseEnd;
            this.mForceCheckWhenEditingWord = forceCheckWhenEditingWord;
            int max = SpellChecker.this.mTextView.length();
            if (end > max) {
                Log.m104w(SpellChecker.TAG, "Parse invalid region, from " + start + " to " + end);
                parseEnd = max;
            } else {
                parseEnd = end;
            }
            if (parseEnd > start) {
                setRangeSpan((Editable) SpellChecker.this.mTextView.getText(), start, parseEnd);
                parse();
            }
        }

        public boolean isFinished() {
            return ((Editable) SpellChecker.this.mTextView.getText()).getSpanStart(this.mRange) < 0;
        }

        public void stop() {
            removeRangeSpan((Editable) SpellChecker.this.mTextView.getText());
            this.mForceCheckWhenEditingWord = false;
        }

        private void setRangeSpan(Editable editable, int start, int end) {
            editable.setSpan(this.mRange, start, end, 33);
        }

        private void removeRangeSpan(Editable editable) {
            editable.removeSpan(this.mRange);
        }

        public void parse() {
            Editable editable = (Editable) SpellChecker.this.mTextView.getText();
            int textChangeStart = editable.getSpanStart(this.mRange);
            int textChangeEnd = editable.getSpanEnd(this.mRange);
            Range<Integer> sentenceBoundary = SpellChecker.this.detectSentenceBoundary(editable, textChangeStart, textChangeEnd);
            int sentenceStart = sentenceBoundary.getLower().intValue();
            int sentenceEnd = sentenceBoundary.getUpper().intValue();
            if (sentenceStart == sentenceEnd) {
                stop();
                return;
            }
            boolean scheduleOtherSpellCheck = false;
            if (sentenceEnd < textChangeEnd) {
                scheduleOtherSpellCheck = true;
            }
            int spellCheckEnd = sentenceEnd;
            int spellCheckStart = sentenceStart;
            boolean createSpellCheckSpan = true;
            int i = 0;
            while (true) {
                if (i >= SpellChecker.this.mLength) {
                    break;
                }
                SpellCheckSpan spellCheckSpan = SpellChecker.this.mSpellCheckSpans[i];
                if (SpellChecker.this.mIds[i] >= 0 && !spellCheckSpan.isSpellCheckInProgress()) {
                    int spanStart = editable.getSpanStart(spellCheckSpan);
                    int spanEnd = editable.getSpanEnd(spellCheckSpan);
                    if (spanEnd >= spellCheckStart && spellCheckEnd >= spanStart) {
                        if (spanStart <= spellCheckStart && spellCheckEnd <= spanEnd) {
                            createSpellCheckSpan = false;
                            break;
                        }
                        editable.removeSpan(spellCheckSpan);
                        spellCheckStart = Math.min(spanStart, spellCheckStart);
                        spellCheckEnd = Math.max(spanEnd, spellCheckEnd);
                    }
                }
                i++;
            }
            if (spellCheckEnd <= spellCheckStart) {
                Log.m104w(SpellChecker.TAG, "Trying to spellcheck invalid region, from " + sentenceStart + " to " + spellCheckEnd);
            } else if (createSpellCheckSpan) {
                SpellChecker.this.addSpellCheckSpan(editable, spellCheckStart, spellCheckEnd);
            }
            int sentenceStart2 = spellCheckEnd;
            if (scheduleOtherSpellCheck && sentenceStart2 != -1 && sentenceStart2 <= textChangeEnd) {
                setRangeSpan(editable, sentenceStart2, textChangeEnd);
            } else {
                removeRangeSpan(editable);
            }
            SpellChecker.this.spellCheck(this.mForceCheckWhenEditingWord);
        }

        private <T> void removeSpansAt(Editable editable, int offset, T[] spans) {
            for (T span : spans) {
                int start = editable.getSpanStart(span);
                if (start <= offset) {
                    int end = editable.getSpanEnd(span);
                    if (end >= offset) {
                        editable.removeSpan(span);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Range<Integer> detectSentenceBoundary(CharSequence sequence, int textChangeStart, int textChangeEnd) {
        int iteratorWindowStart = findSeparator(sequence, Math.max(0, textChangeStart - 350), Math.max(0, textChangeStart - 700));
        int iteratorWindowEnd = findSeparator(sequence, Math.min(textChangeStart + 700, textChangeEnd), Math.min(textChangeStart + 1050, sequence.length()));
        this.mSentenceIterator.setCharSequence(sequence, iteratorWindowStart, iteratorWindowEnd);
        int sentenceStart = this.mSentenceIterator.isBoundary(textChangeStart) ? textChangeStart : this.mSentenceIterator.preceding(textChangeStart);
        int sentenceEnd = this.mSentenceIterator.following(sentenceStart);
        if (sentenceEnd == -1) {
            sentenceEnd = iteratorWindowEnd;
        }
        if (sentenceEnd - sentenceStart <= 350) {
            while (sentenceEnd < textChangeEnd) {
                int nextEnd = this.mSentenceIterator.following(sentenceEnd);
                if (nextEnd == -1 || nextEnd - sentenceStart > 350) {
                    break;
                }
                sentenceEnd = nextEnd;
            }
        } else {
            int uncheckedLength = sentenceEnd - textChangeStart;
            if (uncheckedLength > 350) {
                sentenceEnd = findSeparator(sequence, textChangeStart + 350, sentenceEnd);
                sentenceStart = roundUpToWordStart(sequence, textChangeStart, sentenceStart);
            } else {
                sentenceStart = roundUpToWordStart(sequence, sentenceEnd - 350, sentenceStart);
            }
        }
        return new Range<>(Integer.valueOf(sentenceStart), Integer.valueOf(Math.max(sentenceStart, sentenceEnd)));
    }

    private int roundUpToWordStart(CharSequence sequence, int position, int frontBoundary) {
        if (isSeparator(sequence.charAt(position))) {
            return position;
        }
        int separator = findSeparator(sequence, position, frontBoundary);
        return separator != frontBoundary ? separator + 1 : frontBoundary;
    }

    private static int findSeparator(CharSequence sequence, int start, int end) {
        int step = start < end ? 1 : -1;
        for (int i = start; i != end; i += step) {
            if (isSeparator(sequence.charAt(i))) {
                return i;
            }
        }
        return end;
    }

    public static boolean haveWordBoundariesChanged(Editable editable, int start, int end, int spanStart, int spanEnd) {
        if (spanEnd != start && spanStart != end) {
            return true;
        }
        if (spanEnd == start && start < editable.length()) {
            int codePoint = Character.codePointAt(editable, start);
            boolean haveWordBoundariesChanged = Character.isLetterOrDigit(codePoint);
            return haveWordBoundariesChanged;
        } else if (spanStart == end && end > 0) {
            int codePoint2 = Character.codePointBefore(editable, end);
            boolean haveWordBoundariesChanged2 = Character.isLetterOrDigit(codePoint2);
            return haveWordBoundariesChanged2;
        } else {
            return false;
        }
    }
}
