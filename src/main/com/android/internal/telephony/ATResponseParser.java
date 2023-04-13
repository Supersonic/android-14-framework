package com.android.internal.telephony;
/* loaded from: classes.dex */
public class ATResponseParser {
    private String mLine;
    private int mNext = 0;
    private int mTokEnd;
    private int mTokStart;

    public ATResponseParser(String str) {
        this.mLine = str;
    }

    public boolean nextBoolean() {
        nextTok();
        int i = this.mTokEnd;
        int i2 = this.mTokStart;
        if (i - i2 > 1) {
            throw new ATParseEx();
        }
        char charAt = this.mLine.charAt(i2);
        if (charAt == '0') {
            return false;
        }
        if (charAt == '1') {
            return true;
        }
        throw new ATParseEx();
    }

    public int nextInt() {
        nextTok();
        int i = 0;
        for (int i2 = this.mTokStart; i2 < this.mTokEnd; i2++) {
            char charAt = this.mLine.charAt(i2);
            if (charAt < '0' || charAt > '9') {
                throw new ATParseEx();
            }
            i = (i * 10) + (charAt - '0');
        }
        return i;
    }

    public String nextString() {
        nextTok();
        return this.mLine.substring(this.mTokStart, this.mTokEnd);
    }

    public boolean hasMore() {
        return this.mNext < this.mLine.length();
    }

    private void nextTok() {
        int length = this.mLine.length();
        if (this.mNext == 0) {
            skipPrefix();
        }
        int i = this.mNext;
        if (i >= length) {
            throw new ATParseEx();
        }
        try {
            String str = this.mLine;
            this.mNext = i + 1;
            char skipWhiteSpace = skipWhiteSpace(str.charAt(i));
            if (skipWhiteSpace == '\"') {
                int i2 = this.mNext;
                if (i2 >= length) {
                    throw new ATParseEx();
                }
                String str2 = this.mLine;
                this.mNext = i2 + 1;
                char charAt = str2.charAt(i2);
                this.mTokStart = this.mNext - 1;
                while (charAt != '\"') {
                    int i3 = this.mNext;
                    if (i3 >= length) {
                        break;
                    }
                    String str3 = this.mLine;
                    this.mNext = i3 + 1;
                    charAt = str3.charAt(i3);
                }
                if (charAt != '\"') {
                    throw new ATParseEx();
                }
                int i4 = this.mNext;
                this.mTokEnd = i4 - 1;
                if (i4 < length) {
                    String str4 = this.mLine;
                    this.mNext = i4 + 1;
                    if (str4.charAt(i4) != ',') {
                        throw new ATParseEx();
                    }
                    return;
                }
                return;
            }
            int i5 = this.mNext - 1;
            this.mTokStart = i5;
            this.mTokEnd = i5;
            while (skipWhiteSpace != ',') {
                if (!Character.isWhitespace(skipWhiteSpace)) {
                    this.mTokEnd = this.mNext;
                }
                int i6 = this.mNext;
                if (i6 == length) {
                    return;
                }
                String str5 = this.mLine;
                this.mNext = i6 + 1;
                skipWhiteSpace = str5.charAt(i6);
            }
        } catch (StringIndexOutOfBoundsException unused) {
            throw new ATParseEx();
        }
    }

    private char skipWhiteSpace(char c) {
        int length = this.mLine.length();
        while (this.mNext < length && Character.isWhitespace(c)) {
            String str = this.mLine;
            int i = this.mNext;
            this.mNext = i + 1;
            c = str.charAt(i);
        }
        if (Character.isWhitespace(c)) {
            throw new ATParseEx();
        }
        return c;
    }

    private void skipPrefix() {
        int i;
        String str;
        this.mNext = 0;
        int length = this.mLine.length();
        do {
            i = this.mNext;
            if (i < length) {
                str = this.mLine;
                this.mNext = i + 1;
            } else {
                throw new ATParseEx("missing prefix");
            }
        } while (str.charAt(i) != ':');
    }
}
