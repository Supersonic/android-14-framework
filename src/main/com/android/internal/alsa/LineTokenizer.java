package com.android.internal.alsa;
/* loaded from: classes4.dex */
public class LineTokenizer {
    public static final int kTokenNotFound = -1;
    private final String mDelimiters;

    public LineTokenizer(String delimiters) {
        this.mDelimiters = delimiters;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int nextToken(String line, int startIndex) {
        int len = line.length();
        int offset = startIndex;
        while (offset < len && this.mDelimiters.indexOf(line.charAt(offset)) != -1) {
            offset++;
        }
        if (offset < len) {
            return offset;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int nextDelimiter(String line, int startIndex) {
        int len = line.length();
        int offset = startIndex;
        while (offset < len && this.mDelimiters.indexOf(line.charAt(offset)) == -1) {
            offset++;
        }
        if (offset < len) {
            return offset;
        }
        return -1;
    }
}
