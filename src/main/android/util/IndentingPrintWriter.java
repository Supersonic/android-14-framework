package android.util;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
/* loaded from: classes3.dex */
public class IndentingPrintWriter extends PrintWriter {
    private char[] mCurrentIndent;
    private int mCurrentLength;
    private boolean mEmptyLine;
    private StringBuilder mIndentBuilder;
    private char[] mSingleChar;
    private final String mSingleIndent;
    private final int mWrapLength;

    public IndentingPrintWriter(Writer writer) {
        this(writer, "  ", -1);
    }

    public IndentingPrintWriter(Writer writer, String singleIndent) {
        this(writer, singleIndent, null, -1);
    }

    public IndentingPrintWriter(Writer writer, String singleIndent, String prefix) {
        this(writer, singleIndent, prefix, -1);
    }

    public IndentingPrintWriter(Writer writer, String singleIndent, int wrapLength) {
        this(writer, singleIndent, null, wrapLength);
    }

    public IndentingPrintWriter(Writer writer, String singleIndent, String prefix, int wrapLength) {
        super(writer);
        StringBuilder sb = new StringBuilder();
        this.mIndentBuilder = sb;
        this.mEmptyLine = true;
        this.mSingleChar = new char[1];
        this.mSingleIndent = singleIndent;
        this.mWrapLength = wrapLength;
        if (prefix != null) {
            sb.append(prefix);
        }
    }

    @Deprecated
    public IndentingPrintWriter setIndent(String indent) {
        this.mIndentBuilder.setLength(0);
        this.mIndentBuilder.append(indent);
        this.mCurrentIndent = null;
        return this;
    }

    @Deprecated
    public IndentingPrintWriter setIndent(int indent) {
        this.mIndentBuilder.setLength(0);
        for (int i = 0; i < indent; i++) {
            increaseIndent();
        }
        return this;
    }

    public IndentingPrintWriter increaseIndent() {
        this.mIndentBuilder.append(this.mSingleIndent);
        this.mCurrentIndent = null;
        return this;
    }

    public IndentingPrintWriter decreaseIndent() {
        this.mIndentBuilder.delete(0, this.mSingleIndent.length());
        this.mCurrentIndent = null;
        return this;
    }

    public IndentingPrintWriter print(String key, Object value) {
        String string;
        if (value == null) {
            string = "null";
        } else if (value.getClass().isArray()) {
            if (value.getClass() == boolean[].class) {
                string = Arrays.toString((boolean[]) value);
            } else if (value.getClass() == byte[].class) {
                string = Arrays.toString((byte[]) value);
            } else if (value.getClass() == char[].class) {
                string = Arrays.toString((char[]) value);
            } else if (value.getClass() == double[].class) {
                string = Arrays.toString((double[]) value);
            } else if (value.getClass() == float[].class) {
                string = Arrays.toString((float[]) value);
            } else if (value.getClass() == int[].class) {
                string = Arrays.toString((int[]) value);
            } else if (value.getClass() == long[].class) {
                string = Arrays.toString((long[]) value);
            } else if (value.getClass() == short[].class) {
                string = Arrays.toString((short[]) value);
            } else {
                string = Arrays.toString((Object[]) value);
            }
        } else {
            string = String.valueOf(value);
        }
        print(key + "=" + string + " ");
        return this;
    }

    public IndentingPrintWriter printHexInt(String key, int value) {
        print(key + "=0x" + Integer.toHexString(value) + " ");
        return this;
    }

    @Override // java.io.PrintWriter
    public void println() {
        write(10);
    }

    @Override // java.io.PrintWriter, java.io.Writer
    public void write(int c) {
        char[] cArr = this.mSingleChar;
        cArr[0] = (char) c;
        write(cArr, 0, 1);
    }

    @Override // java.io.PrintWriter, java.io.Writer
    public void write(String s, int off, int len) {
        char[] buf = new char[len];
        s.getChars(off, len - off, buf, 0);
        write(buf, 0, len);
    }

    @Override // java.io.PrintWriter, java.io.Writer
    public void write(char[] buf, int offset, int count) {
        int indentLength = this.mIndentBuilder.length();
        int bufferEnd = offset + count;
        int lineStart = offset;
        int lineEnd = offset;
        while (lineEnd < bufferEnd) {
            int lineEnd2 = lineEnd + 1;
            char ch = buf[lineEnd];
            this.mCurrentLength++;
            if (ch == '\n') {
                maybeWriteIndent();
                super.write(buf, lineStart, lineEnd2 - lineStart);
                lineStart = lineEnd2;
                this.mEmptyLine = true;
                this.mCurrentLength = 0;
            }
            int i = this.mWrapLength;
            if (i > 0 && this.mCurrentLength >= i - indentLength) {
                if (!this.mEmptyLine) {
                    super.write(10);
                    this.mEmptyLine = true;
                    this.mCurrentLength = lineEnd2 - lineStart;
                } else {
                    maybeWriteIndent();
                    super.write(buf, lineStart, lineEnd2 - lineStart);
                    super.write(10);
                    this.mEmptyLine = true;
                    lineStart = lineEnd2;
                    this.mCurrentLength = 0;
                }
            }
            lineEnd = lineEnd2;
        }
        if (lineStart != lineEnd) {
            maybeWriteIndent();
            super.write(buf, lineStart, lineEnd - lineStart);
        }
    }

    private void maybeWriteIndent() {
        if (this.mEmptyLine) {
            this.mEmptyLine = false;
            if (this.mIndentBuilder.length() != 0) {
                if (this.mCurrentIndent == null) {
                    this.mCurrentIndent = this.mIndentBuilder.toString().toCharArray();
                }
                char[] cArr = this.mCurrentIndent;
                super.write(cArr, 0, cArr.length);
            }
        }
    }
}
