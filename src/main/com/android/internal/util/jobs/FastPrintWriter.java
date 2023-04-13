package com.android.internal.util.jobs;

import android.p005os.IInstalld;
import android.util.Log;
import android.util.Printer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
/* loaded from: classes.dex */
public class FastPrintWriter extends PrintWriter {
    public final boolean mAutoFlush;
    public final int mBufferLen;
    public final ByteBuffer mBytes;
    public CharsetEncoder mCharset;
    public boolean mIoError;
    public final OutputStream mOutputStream;
    public int mPos;
    public final Printer mPrinter;
    public final String mSeparator;
    public final char[] mText;
    public final Writer mWriter;

    /* loaded from: classes.dex */
    public static class DummyWriter extends Writer {
        public DummyWriter() {
        }

        @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            throw new UnsupportedOperationException("Shouldn't be here");
        }

        @Override // java.io.Writer, java.io.Flushable
        public void flush() throws IOException {
            close();
        }

        @Override // java.io.Writer
        public void write(char[] cArr, int i, int i2) throws IOException {
            close();
        }
    }

    public FastPrintWriter(OutputStream outputStream) {
        this(outputStream, false, (int) IInstalld.FLAG_FORCE);
    }

    public FastPrintWriter(OutputStream outputStream, boolean z) {
        this(outputStream, z, (int) IInstalld.FLAG_FORCE);
    }

    public FastPrintWriter(OutputStream outputStream, boolean z, int i) {
        super(new DummyWriter(), z);
        if (outputStream == null) {
            throw new NullPointerException("out is null");
        }
        this.mBufferLen = i;
        this.mText = new char[i];
        this.mBytes = ByteBuffer.allocate(i);
        this.mOutputStream = outputStream;
        this.mWriter = null;
        this.mPrinter = null;
        this.mAutoFlush = z;
        this.mSeparator = System.lineSeparator();
        initDefaultEncoder();
    }

    public FastPrintWriter(Writer writer) {
        this(writer, false, (int) IInstalld.FLAG_FORCE);
    }

    public FastPrintWriter(Writer writer, boolean z) {
        this(writer, z, (int) IInstalld.FLAG_FORCE);
    }

    public FastPrintWriter(Writer writer, boolean z, int i) {
        super(new DummyWriter(), z);
        if (writer == null) {
            throw new NullPointerException("wr is null");
        }
        this.mBufferLen = i;
        this.mText = new char[i];
        this.mBytes = null;
        this.mOutputStream = null;
        this.mWriter = writer;
        this.mPrinter = null;
        this.mAutoFlush = z;
        this.mSeparator = System.lineSeparator();
        initDefaultEncoder();
    }

    public FastPrintWriter(Printer printer) {
        this(printer, 512);
    }

    public FastPrintWriter(Printer printer, int i) {
        super((Writer) new DummyWriter(), true);
        if (printer == null) {
            throw new NullPointerException("pr is null");
        }
        this.mBufferLen = i;
        this.mText = new char[i];
        this.mBytes = null;
        this.mOutputStream = null;
        this.mWriter = null;
        this.mPrinter = printer;
        this.mAutoFlush = true;
        this.mSeparator = System.lineSeparator();
        initDefaultEncoder();
    }

    public final void initEncoder(String str) throws UnsupportedEncodingException {
        try {
            CharsetEncoder newEncoder = Charset.forName(str).newEncoder();
            this.mCharset = newEncoder;
            newEncoder.onMalformedInput(CodingErrorAction.REPLACE);
            this.mCharset.onUnmappableCharacter(CodingErrorAction.REPLACE);
        } catch (Exception unused) {
            throw new UnsupportedEncodingException(str);
        }
    }

    @Override // java.io.PrintWriter
    public boolean checkError() {
        boolean z;
        flush();
        synchronized (((PrintWriter) this).lock) {
            z = this.mIoError;
        }
        return z;
    }

    @Override // java.io.PrintWriter
    public void clearError() {
        synchronized (((PrintWriter) this).lock) {
            this.mIoError = false;
        }
    }

    @Override // java.io.PrintWriter
    public void setError() {
        synchronized (((PrintWriter) this).lock) {
            this.mIoError = true;
        }
    }

    public final void initDefaultEncoder() {
        CharsetEncoder newEncoder = Charset.defaultCharset().newEncoder();
        this.mCharset = newEncoder;
        newEncoder.onMalformedInput(CodingErrorAction.REPLACE);
        this.mCharset.onUnmappableCharacter(CodingErrorAction.REPLACE);
    }

    public final void appendLocked(char c) throws IOException {
        int i = this.mPos;
        if (i >= this.mBufferLen - 1) {
            flushLocked();
            i = this.mPos;
        }
        this.mText[i] = c;
        this.mPos = i + 1;
    }

    public final void appendLocked(String str, int i, int i2) throws IOException {
        int i3 = this.mBufferLen;
        if (i2 > i3) {
            int i4 = i2 + i;
            while (i < i4) {
                int i5 = i + i3;
                appendLocked(str, i, i5 < i4 ? i3 : i4 - i);
                i = i5;
            }
            return;
        }
        int i6 = this.mPos;
        if (i6 + i2 > i3) {
            flushLocked();
            i6 = this.mPos;
        }
        str.getChars(i, i + i2, this.mText, i6);
        this.mPos = i6 + i2;
    }

    public final void appendLocked(char[] cArr, int i, int i2) throws IOException {
        int i3 = this.mBufferLen;
        if (i2 > i3) {
            int i4 = i2 + i;
            while (i < i4) {
                int i5 = i + i3;
                appendLocked(cArr, i, i5 < i4 ? i3 : i4 - i);
                i = i5;
            }
            return;
        }
        int i6 = this.mPos;
        if (i6 + i2 > i3) {
            flushLocked();
            i6 = this.mPos;
        }
        System.arraycopy(cArr, i, this.mText, i6, i2);
        this.mPos = i6 + i2;
    }

    public final void flushBytesLocked() throws IOException {
        int position;
        if (this.mIoError || (position = this.mBytes.position()) <= 0) {
            return;
        }
        this.mBytes.flip();
        this.mOutputStream.write(this.mBytes.array(), 0, position);
        this.mBytes.clear();
    }

    public final void flushLocked() throws IOException {
        int i = this.mPos;
        if (i > 0) {
            if (this.mOutputStream != null) {
                CharBuffer wrap = CharBuffer.wrap(this.mText, 0, i);
                CoderResult encode = this.mCharset.encode(wrap, this.mBytes, true);
                while (!this.mIoError) {
                    if (encode.isError()) {
                        throw new IOException(encode.toString());
                    }
                    if (!encode.isOverflow()) {
                        break;
                    }
                    flushBytesLocked();
                    encode = this.mCharset.encode(wrap, this.mBytes, true);
                }
                if (!this.mIoError) {
                    flushBytesLocked();
                    this.mOutputStream.flush();
                }
            } else {
                Writer writer = this.mWriter;
                if (writer != null) {
                    if (!this.mIoError) {
                        writer.write(this.mText, 0, i);
                        this.mWriter.flush();
                    }
                } else {
                    int length = this.mSeparator.length();
                    int i2 = this.mPos;
                    if (length >= i2) {
                        length = i2;
                    }
                    int i3 = 0;
                    while (i3 < length) {
                        char c = this.mText[(this.mPos - 1) - i3];
                        String str = this.mSeparator;
                        if (c != str.charAt((str.length() - 1) - i3)) {
                            break;
                        }
                        i3++;
                    }
                    int i4 = this.mPos;
                    if (i3 >= i4) {
                        this.mPrinter.println("");
                    } else {
                        this.mPrinter.println(new String(this.mText, 0, i4 - i3));
                    }
                }
            }
            this.mPos = 0;
        }
    }

    @Override // java.io.PrintWriter, java.io.Writer, java.io.Flushable
    public void flush() {
        synchronized (((PrintWriter) this).lock) {
            try {
                flushLocked();
                if (!this.mIoError) {
                    OutputStream outputStream = this.mOutputStream;
                    if (outputStream != null) {
                        outputStream.flush();
                    } else {
                        Writer writer = this.mWriter;
                        if (writer != null) {
                            writer.flush();
                        }
                    }
                }
            } catch (IOException e) {
                Log.w("FastPrintWriter", "Write failure", e);
                setError();
            }
        }
    }

    @Override // java.io.PrintWriter, java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        synchronized (((PrintWriter) this).lock) {
            try {
                flushLocked();
                OutputStream outputStream = this.mOutputStream;
                if (outputStream != null) {
                    outputStream.close();
                } else {
                    Writer writer = this.mWriter;
                    if (writer != null) {
                        writer.close();
                    }
                }
            } catch (IOException e) {
                Log.w("FastPrintWriter", "Write failure", e);
                setError();
            }
        }
    }

    @Override // java.io.PrintWriter
    public void print(char[] cArr) {
        synchronized (((PrintWriter) this).lock) {
            try {
                appendLocked(cArr, 0, cArr.length);
            } catch (IOException e) {
                Log.w("FastPrintWriter", "Write failure", e);
                setError();
            }
        }
    }

    @Override // java.io.PrintWriter
    public void print(char c) {
        synchronized (((PrintWriter) this).lock) {
            try {
                appendLocked(c);
            } catch (IOException e) {
                Log.w("FastPrintWriter", "Write failure", e);
                setError();
            }
        }
    }

    @Override // java.io.PrintWriter
    public void print(String str) {
        if (str == null) {
            str = "null";
        }
        synchronized (((PrintWriter) this).lock) {
            try {
                appendLocked(str, 0, str.length());
            } catch (IOException e) {
                Log.w("FastPrintWriter", "Write failure", e);
                setError();
            }
        }
    }

    @Override // java.io.PrintWriter
    public void print(int i) {
        if (i == 0) {
            print("0");
        } else {
            super.print(i);
        }
    }

    @Override // java.io.PrintWriter
    public void print(long j) {
        if (j == 0) {
            print("0");
        } else {
            super.print(j);
        }
    }

    @Override // java.io.PrintWriter
    public void println() {
        synchronized (((PrintWriter) this).lock) {
            try {
                String str = this.mSeparator;
                appendLocked(str, 0, str.length());
                if (this.mAutoFlush) {
                    flushLocked();
                }
            } catch (IOException e) {
                Log.w("FastPrintWriter", "Write failure", e);
                setError();
            }
        }
    }

    @Override // java.io.PrintWriter
    public void println(int i) {
        if (i == 0) {
            println("0");
        } else {
            super.println(i);
        }
    }

    @Override // java.io.PrintWriter
    public void println(long j) {
        if (j == 0) {
            println("0");
        } else {
            super.println(j);
        }
    }

    @Override // java.io.PrintWriter
    public void println(char[] cArr) {
        print(cArr);
        println();
    }

    @Override // java.io.PrintWriter
    public void println(char c) {
        print(c);
        println();
    }

    @Override // java.io.PrintWriter, java.io.Writer
    public void write(char[] cArr, int i, int i2) {
        synchronized (((PrintWriter) this).lock) {
            try {
                appendLocked(cArr, i, i2);
            } catch (IOException e) {
                Log.w("FastPrintWriter", "Write failure", e);
                setError();
            }
        }
    }

    @Override // java.io.PrintWriter, java.io.Writer
    public void write(int i) {
        synchronized (((PrintWriter) this).lock) {
            try {
                appendLocked((char) i);
            } catch (IOException e) {
                Log.w("FastPrintWriter", "Write failure", e);
                setError();
            }
        }
    }

    @Override // java.io.PrintWriter, java.io.Writer
    public void write(String str) {
        synchronized (((PrintWriter) this).lock) {
            try {
                appendLocked(str, 0, str.length());
            } catch (IOException e) {
                Log.w("FastPrintWriter", "Write failure", e);
                setError();
            }
        }
    }

    @Override // java.io.PrintWriter, java.io.Writer
    public void write(String str, int i, int i2) {
        synchronized (((PrintWriter) this).lock) {
            try {
                appendLocked(str, i, i2);
            } catch (IOException e) {
                Log.w("FastPrintWriter", "Write failure", e);
                setError();
            }
        }
    }

    @Override // java.io.PrintWriter, java.io.Writer, java.lang.Appendable
    public PrintWriter append(CharSequence charSequence, int i, int i2) {
        if (charSequence == null) {
            charSequence = "null";
        }
        String charSequence2 = charSequence.subSequence(i, i2).toString();
        write(charSequence2, 0, charSequence2.length());
        return this;
    }
}
