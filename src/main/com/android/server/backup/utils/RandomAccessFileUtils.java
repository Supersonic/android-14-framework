package com.android.server.backup.utils;

import android.util.Slog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
/* loaded from: classes.dex */
public final class RandomAccessFileUtils {
    public static RandomAccessFile getRandomAccessFile(File file) throws FileNotFoundException {
        return new RandomAccessFile(file, "rwd");
    }

    public static void writeBoolean(File file, boolean z) {
        try {
            RandomAccessFile randomAccessFile = getRandomAccessFile(file);
            randomAccessFile.writeBoolean(z);
            randomAccessFile.close();
        } catch (IOException e) {
            Slog.w("BackupManagerService", "Error writing file:" + file.getAbsolutePath(), e);
        }
    }
}
