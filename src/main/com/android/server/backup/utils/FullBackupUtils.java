package com.android.server.backup.utils;

import android.os.ParcelFileDescriptor;
import android.util.Slog;
import com.android.server.backup.BackupAndRestoreFeatureFlags;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes.dex */
public class FullBackupUtils {
    public static void routeSocketDataToOutput(ParcelFileDescriptor parcelFileDescriptor, OutputStream outputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(parcelFileDescriptor.getFileDescriptor()));
        int fullBackupUtilsRouteBufferSizeBytes = BackupAndRestoreFeatureFlags.getFullBackupUtilsRouteBufferSizeBytes();
        byte[] bArr = new byte[fullBackupUtilsRouteBufferSizeBytes];
        while (true) {
            int readInt = dataInputStream.readInt();
            if (readInt <= 0) {
                return;
            }
            while (readInt > 0) {
                int read = dataInputStream.read(bArr, 0, readInt > fullBackupUtilsRouteBufferSizeBytes ? fullBackupUtilsRouteBufferSizeBytes : readInt);
                if (read < 0) {
                    Slog.e("BackupManagerService", "Unexpectedly reached end of file while reading data");
                    throw new EOFException();
                } else {
                    outputStream.write(bArr, 0, read);
                    readInt -= read;
                }
            }
        }
    }
}
