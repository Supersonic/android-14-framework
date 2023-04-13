package com.android.server.integrity.parser;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
/* loaded from: classes.dex */
public abstract class RandomAccessObject {
    public abstract void close() throws IOException;

    public abstract int length();

    public abstract int read() throws IOException;

    public abstract int read(byte[] bArr, int i, int i2) throws IOException;

    public abstract void seek(int i) throws IOException;

    public static RandomAccessObject ofFile(File file) throws IOException {
        return new RandomAccessFileObject(file);
    }

    /* loaded from: classes.dex */
    public static class RandomAccessFileObject extends RandomAccessObject {
        public final int mLength;
        public final RandomAccessFile mRandomAccessFile;

        public RandomAccessFileObject(File file) throws IOException {
            long length = file.length();
            if (length > 2147483647L) {
                throw new IOException("Unsupported file size (too big) " + length);
            }
            this.mRandomAccessFile = new RandomAccessFile(file, "r");
            this.mLength = (int) length;
        }

        @Override // com.android.server.integrity.parser.RandomAccessObject
        public void seek(int i) throws IOException {
            this.mRandomAccessFile.seek(i);
        }

        @Override // com.android.server.integrity.parser.RandomAccessObject
        public int read() throws IOException {
            return this.mRandomAccessFile.read();
        }

        @Override // com.android.server.integrity.parser.RandomAccessObject
        public int read(byte[] bArr, int i, int i2) throws IOException {
            return this.mRandomAccessFile.read(bArr, i, i2);
        }

        @Override // com.android.server.integrity.parser.RandomAccessObject
        public void close() throws IOException {
            this.mRandomAccessFile.close();
        }

        @Override // com.android.server.integrity.parser.RandomAccessObject
        public int length() {
            return this.mLength;
        }
    }
}
