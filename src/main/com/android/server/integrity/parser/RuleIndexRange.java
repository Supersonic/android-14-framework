package com.android.server.integrity.parser;
/* loaded from: classes.dex */
public class RuleIndexRange {
    public int mEndIndex;
    public int mStartIndex;

    public RuleIndexRange(int i, int i2) {
        this.mStartIndex = i;
        this.mEndIndex = i2;
    }

    public int getStartIndex() {
        return this.mStartIndex;
    }

    public int getEndIndex() {
        return this.mEndIndex;
    }

    public boolean equals(Object obj) {
        RuleIndexRange ruleIndexRange = (RuleIndexRange) obj;
        return this.mStartIndex == ruleIndexRange.getStartIndex() && this.mEndIndex == ruleIndexRange.getEndIndex();
    }

    public String toString() {
        return String.format("Range{%d, %d}", Integer.valueOf(this.mStartIndex), Integer.valueOf(this.mEndIndex));
    }
}
