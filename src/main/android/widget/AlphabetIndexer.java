package android.widget;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.util.SparseIntArray;
import java.text.Collator;
/* loaded from: classes4.dex */
public class AlphabetIndexer extends DataSetObserver implements SectionIndexer {
    private SparseIntArray mAlphaMap;
    protected CharSequence mAlphabet;
    private String[] mAlphabetArray;
    private int mAlphabetLength;
    private Collator mCollator;
    protected int mColumnIndex;
    protected Cursor mDataCursor;

    public AlphabetIndexer(Cursor cursor, int sortedColumnIndex, CharSequence alphabet) {
        int i;
        this.mDataCursor = cursor;
        this.mColumnIndex = sortedColumnIndex;
        this.mAlphabet = alphabet;
        int length = alphabet.length();
        this.mAlphabetLength = length;
        this.mAlphabetArray = new String[length];
        int i2 = 0;
        while (true) {
            i = this.mAlphabetLength;
            if (i2 >= i) {
                break;
            }
            this.mAlphabetArray[i2] = Character.toString(this.mAlphabet.charAt(i2));
            i2++;
        }
        this.mAlphaMap = new SparseIntArray(i);
        if (cursor != null) {
            cursor.registerDataSetObserver(this);
        }
        Collator collator = Collator.getInstance();
        this.mCollator = collator;
        collator.setStrength(0);
    }

    @Override // android.widget.SectionIndexer
    public Object[] getSections() {
        return this.mAlphabetArray;
    }

    public void setCursor(Cursor cursor) {
        Cursor cursor2 = this.mDataCursor;
        if (cursor2 != null) {
            cursor2.unregisterDataSetObserver(this);
        }
        this.mDataCursor = cursor;
        if (cursor != null) {
            cursor.registerDataSetObserver(this);
        }
        this.mAlphaMap.clear();
    }

    protected int compare(String word, String letter) {
        String firstLetter;
        if (word.length() == 0) {
            firstLetter = " ";
        } else {
            firstLetter = word.substring(0, 1);
        }
        return this.mCollator.compare(firstLetter, letter);
    }

    @Override // android.widget.SectionIndexer
    public int getPositionForSection(int sectionIndex) {
        SparseIntArray alphaMap = this.mAlphaMap;
        Cursor cursor = this.mDataCursor;
        if (cursor == null || this.mAlphabet == null || sectionIndex <= 0) {
            return 0;
        }
        int i = this.mAlphabetLength;
        if (sectionIndex >= i) {
            sectionIndex = i - 1;
        }
        int savedCursorPos = cursor.getPosition();
        int count = cursor.getCount();
        int start = 0;
        int end = count;
        char letter = this.mAlphabet.charAt(sectionIndex);
        String targetLetter = Character.toString(letter);
        int pos = alphaMap.get(letter, Integer.MIN_VALUE);
        if (Integer.MIN_VALUE != pos) {
            if (pos >= 0) {
                return pos;
            }
            end = -pos;
        }
        if (sectionIndex > 0) {
            int prevLetter = this.mAlphabet.charAt(sectionIndex - 1);
            int prevLetterPos = alphaMap.get(prevLetter, Integer.MIN_VALUE);
            if (prevLetterPos != Integer.MIN_VALUE) {
                start = Math.abs(prevLetterPos);
            }
        }
        int pos2 = (end + start) / 2;
        while (true) {
            if (pos2 >= end) {
                break;
            }
            cursor.moveToPosition(pos2);
            String curName = cursor.getString(this.mColumnIndex);
            if (curName == null) {
                if (pos2 == 0) {
                    break;
                }
                pos2--;
            } else {
                int diff = compare(curName, targetLetter);
                if (diff != 0) {
                    if (diff < 0) {
                        start = pos2 + 1;
                        if (start >= count) {
                            pos2 = count;
                            break;
                        }
                    } else {
                        end = pos2;
                    }
                    pos2 = (start + end) / 2;
                } else if (start == pos2) {
                    break;
                } else {
                    end = pos2;
                    pos2 = (start + end) / 2;
                }
            }
        }
        alphaMap.put(letter, pos2);
        cursor.moveToPosition(savedCursorPos);
        return pos2;
    }

    @Override // android.widget.SectionIndexer
    public int getSectionForPosition(int position) {
        int savedCursorPos = this.mDataCursor.getPosition();
        this.mDataCursor.moveToPosition(position);
        String curName = this.mDataCursor.getString(this.mColumnIndex);
        this.mDataCursor.moveToPosition(savedCursorPos);
        for (int i = 0; i < this.mAlphabetLength; i++) {
            char letter = this.mAlphabet.charAt(i);
            String targetLetter = Character.toString(letter);
            if (compare(curName, targetLetter) == 0) {
                return i;
            }
        }
        return 0;
    }

    @Override // android.database.DataSetObserver
    public void onChanged() {
        super.onChanged();
        this.mAlphaMap.clear();
    }

    @Override // android.database.DataSetObserver
    public void onInvalidated() {
        super.onInvalidated();
        this.mAlphaMap.clear();
    }
}
