package com.android.server.powerstats;

import android.content.Context;
import android.util.Slog;
import com.android.internal.util.FileRotator;
import com.android.server.backup.BackupManagerConstants;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;
/* loaded from: classes2.dex */
public class PowerStatsDataStorage {
    public static final String TAG = "PowerStatsDataStorage";
    public final File mDataStorageDir;
    public final String mDataStorageFilename;
    public final FileRotator mFileRotator;
    public final ReentrantLock mLock = new ReentrantLock();

    /* loaded from: classes2.dex */
    public interface DataElementReadCallback {
        void onReadDataElement(byte[] bArr);
    }

    /* loaded from: classes2.dex */
    public static class DataElement {
        public byte[] mData;

        public final byte[] toByteArray() throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(ByteBuffer.allocate(4).putInt(this.mData.length).array());
            byteArrayOutputStream.write(this.mData);
            return byteArrayOutputStream.toByteArray();
        }

        public byte[] getData() {
            return this.mData;
        }

        public DataElement(byte[] bArr) {
            this.mData = bArr;
        }

        public DataElement(InputStream inputStream) throws IOException {
            byte[] bArr = new byte[4];
            int read = inputStream.read(bArr);
            this.mData = new byte[0];
            if (read == 4) {
                int i = ByteBuffer.wrap(bArr).getInt();
                if (i > 0 && i < 32768) {
                    byte[] bArr2 = new byte[i];
                    this.mData = bArr2;
                    int read2 = inputStream.read(bArr2);
                    if (read2 == i) {
                        return;
                    }
                    throw new IOException("Invalid bytes read, expected: " + i + ", actual: " + read2);
                }
                throw new IOException("DataElement size is invalid: " + i);
            }
            throw new IOException("Did not read 4 bytes (" + read + ")");
        }
    }

    /* loaded from: classes2.dex */
    public static class DataReader implements FileRotator.Reader {
        public DataElementReadCallback mCallback;

        public DataReader(DataElementReadCallback dataElementReadCallback) {
            this.mCallback = dataElementReadCallback;
        }

        public void read(InputStream inputStream) throws IOException {
            while (inputStream.available() > 0) {
                this.mCallback.onReadDataElement(new DataElement(inputStream).getData());
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class DataRewriter implements FileRotator.Rewriter {
        public byte[] mActiveFileData = new byte[0];
        public byte[] mNewData;

        public void reset() {
        }

        public boolean shouldWrite() {
            return true;
        }

        public DataRewriter(byte[] bArr) {
            this.mNewData = bArr;
        }

        public void read(InputStream inputStream) throws IOException {
            byte[] bArr = new byte[inputStream.available()];
            this.mActiveFileData = bArr;
            inputStream.read(bArr);
        }

        public void write(OutputStream outputStream) throws IOException {
            outputStream.write(this.mActiveFileData);
            outputStream.write(this.mNewData);
        }
    }

    public PowerStatsDataStorage(Context context, File file, String str) {
        this.mDataStorageDir = file;
        this.mDataStorageFilename = str;
        if (!file.exists() && !file.mkdirs()) {
            Slog.wtf(TAG, "mDataStorageDir does not exist: " + file.getPath());
            this.mFileRotator = null;
            return;
        }
        File[] listFiles = file.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].getName().startsWith(this.mDataStorageFilename.substring(0, this.mDataStorageFilename.lastIndexOf(46))) && !listFiles[i].getName().startsWith(this.mDataStorageFilename)) {
                listFiles[i].delete();
            }
        }
        this.mFileRotator = new FileRotator(this.mDataStorageDir, this.mDataStorageFilename, (long) BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS, 172800000L);
    }

    public void write(byte[] bArr) {
        if (bArr == null || bArr.length <= 0) {
            return;
        }
        this.mLock.lock();
        try {
            try {
                long currentTimeMillis = System.currentTimeMillis();
                this.mFileRotator.rewriteActive(new DataRewriter(new DataElement(bArr).toByteArray()), currentTimeMillis);
                this.mFileRotator.maybeRotate(currentTimeMillis);
            } catch (IOException e) {
                String str = TAG;
                Slog.e(str, "Failed to write to on-device storage: " + e);
            }
        } finally {
            this.mLock.unlock();
        }
    }

    public void read(DataElementReadCallback dataElementReadCallback) throws IOException {
        this.mLock.lock();
        try {
            this.mFileRotator.readMatching(new DataReader(dataElementReadCallback), Long.MIN_VALUE, Long.MAX_VALUE);
        } finally {
            this.mLock.unlock();
        }
    }

    public void deleteLogs() {
        this.mLock.lock();
        try {
            File[] listFiles = this.mDataStorageDir.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].getName().startsWith(this.mDataStorageFilename.substring(0, this.mDataStorageFilename.lastIndexOf(46)))) {
                    listFiles[i].delete();
                }
            }
        } finally {
            this.mLock.unlock();
        }
    }
}
