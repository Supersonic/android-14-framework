package com.android.server.p011pm;

import android.content.ComponentName;
import android.content.pm.DataLoaderParams;
import android.content.pm.InstallationFile;
import android.os.ParcelFileDescriptor;
import android.os.ShellCommand;
import android.service.dataloader.DataLoaderService;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import libcore.io.IoUtils;
/* renamed from: com.android.server.pm.PackageManagerShellCommandDataLoader */
/* loaded from: classes2.dex */
public class PackageManagerShellCommandDataLoader extends DataLoaderService {
    public static final char ARGS_DELIM = '&';
    public static final int INVALID_SHELL_COMMAND_ID = -1;
    public static final String PACKAGE = "android";
    public static final String SHELL_COMMAND_ID_PREFIX = "shellCommandId=";
    public static final String STDIN_PATH = "-";
    public static final String TAG = "PackageManagerShellCommandDataLoader";
    public static final int TOO_MANY_PENDING_SHELL_COMMANDS = 10;
    public static final String CLASS = PackageManagerShellCommandDataLoader.class.getName();
    public static final SecureRandom sRandom = new SecureRandom();
    public static final SparseArray<WeakReference<ShellCommand>> sShellCommands = new SparseArray<>();

    private static native void nativeInitialize();

    public static String getDataLoaderParamsArgs(ShellCommand shellCommand) {
        SparseArray<WeakReference<ShellCommand>> sparseArray;
        int nextInt;
        SparseArray<WeakReference<ShellCommand>> sparseArray2;
        nativeInitialize();
        SparseArray<WeakReference<ShellCommand>> sparseArray3 = sShellCommands;
        synchronized (sparseArray3) {
            for (int size = sparseArray3.size() - 1; size >= 0; size--) {
                SparseArray<WeakReference<ShellCommand>> sparseArray4 = sShellCommands;
                if (sparseArray4.valueAt(size).get() == null) {
                    sparseArray4.removeAt(size);
                }
            }
            if (sShellCommands.size() > 10) {
                Slog.e(TAG, "Too many pending shell commands: " + sparseArray.size());
            }
            do {
                nextInt = sRandom.nextInt(2147483646) + 1;
                sparseArray2 = sShellCommands;
            } while (sparseArray2.contains(nextInt));
            sparseArray2.put(nextInt, new WeakReference<>(shellCommand));
        }
        return SHELL_COMMAND_ID_PREFIX + nextInt;
    }

    public static DataLoaderParams getStreamingDataLoaderParams(ShellCommand shellCommand) {
        return DataLoaderParams.forStreaming(new ComponentName(PACKAGE, CLASS), getDataLoaderParamsArgs(shellCommand));
    }

    public static DataLoaderParams getIncrementalDataLoaderParams(ShellCommand shellCommand) {
        return DataLoaderParams.forIncremental(new ComponentName(PACKAGE, CLASS), getDataLoaderParamsArgs(shellCommand));
    }

    public static int extractShellCommandId(String str) {
        int indexOf = str.indexOf(SHELL_COMMAND_ID_PREFIX);
        if (indexOf < 0) {
            Slog.e(TAG, "Missing shell command id param.");
            return -1;
        }
        int i = indexOf + 15;
        int indexOf2 = str.indexOf(38, i);
        try {
            if (indexOf2 < 0) {
                return Integer.parseInt(str.substring(i));
            }
            return Integer.parseInt(str.substring(i, indexOf2));
        } catch (NumberFormatException e) {
            Slog.e(TAG, "Incorrect shell command id format.", e);
            return -1;
        }
    }

    @VisibleForTesting
    /* renamed from: com.android.server.pm.PackageManagerShellCommandDataLoader$Metadata */
    /* loaded from: classes2.dex */
    public static class Metadata {
        public static AtomicLong sGlobalSalt = new AtomicLong(new SecureRandom().nextLong());
        public final String mData;
        public final byte mMode;
        public final String mSalt;

        public static Long nextGlobalSalt() {
            return Long.valueOf(sGlobalSalt.incrementAndGet());
        }

        public static Metadata forStdIn(String str) {
            return new Metadata((byte) 0, str);
        }

        @VisibleForTesting
        public static Metadata forLocalFile(String str) {
            return new Metadata((byte) 1, str, nextGlobalSalt().toString());
        }

        public static Metadata forDataOnlyStreaming(String str) {
            return new Metadata((byte) 2, str);
        }

        public static Metadata forStreaming(String str) {
            return new Metadata((byte) 3, str);
        }

        public Metadata(byte b, String str) {
            this(b, str, null);
        }

        public Metadata(byte b, String str, String str2) {
            this.mMode = b;
            this.mData = str == null ? "" : str;
            this.mSalt = str2;
        }

        public static Metadata fromByteArray(byte[] bArr) throws IOException {
            String str;
            String str2 = null;
            if (bArr == null || bArr.length < 5) {
                return null;
            }
            byte b = bArr[0];
            if (b == 1) {
                int i = ByteBuffer.wrap(bArr, 1, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
                String str3 = new String(bArr, 5, i, StandardCharsets.UTF_8);
                int i2 = 5 + i;
                str2 = new String(bArr, i2, bArr.length - i2, StandardCharsets.UTF_8);
                str = str3;
            } else {
                str = new String(bArr, 1, bArr.length - 1, StandardCharsets.UTF_8);
            }
            return new Metadata(b, str, str2);
        }

        @VisibleForTesting
        public byte[] toByteArray() {
            byte[] bytes = this.mData.getBytes(StandardCharsets.UTF_8);
            byte b = this.mMode;
            if (b == 1) {
                int length = bytes.length;
                byte[] bytes2 = this.mSalt.getBytes(StandardCharsets.UTF_8);
                byte[] bArr = new byte[length + 5 + bytes2.length];
                bArr[0] = this.mMode;
                ByteBuffer.wrap(bArr, 1, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(length);
                System.arraycopy(bytes, 0, bArr, 5, length);
                System.arraycopy(bytes2, 0, bArr, 5 + length, bytes2.length);
                return bArr;
            }
            byte[] bArr2 = new byte[bytes.length + 1];
            bArr2[0] = b;
            System.arraycopy(bytes, 0, bArr2, 1, bytes.length);
            return bArr2;
        }

        public byte getMode() {
            return this.mMode;
        }

        public String getData() {
            return this.mData;
        }
    }

    /* renamed from: com.android.server.pm.PackageManagerShellCommandDataLoader$DataLoader */
    /* loaded from: classes2.dex */
    public static class DataLoader implements DataLoaderService.DataLoader {
        public DataLoaderService.FileSystemConnector mConnector;
        public DataLoaderParams mParams;

        public DataLoader() {
            this.mParams = null;
            this.mConnector = null;
        }

        public boolean onCreate(DataLoaderParams dataLoaderParams, DataLoaderService.FileSystemConnector fileSystemConnector) {
            this.mParams = dataLoaderParams;
            this.mConnector = fileSystemConnector;
            return true;
        }

        public boolean onPrepareImage(Collection<InstallationFile> collection, Collection<String> collection2) {
            ShellCommand lookupShellCommand = PackageManagerShellCommandDataLoader.lookupShellCommand(this.mParams.getArguments());
            if (lookupShellCommand == null) {
                Slog.e(PackageManagerShellCommandDataLoader.TAG, "Missing shell command.");
                return false;
            }
            try {
                for (InstallationFile installationFile : collection) {
                    Metadata fromByteArray = Metadata.fromByteArray(installationFile.getMetadata());
                    if (fromByteArray == null) {
                        Slog.e(PackageManagerShellCommandDataLoader.TAG, "Invalid metadata for file: " + installationFile.getName());
                        return false;
                    }
                    byte mode = fromByteArray.getMode();
                    if (mode == 0) {
                        this.mConnector.writeData(installationFile.getName(), 0L, installationFile.getLengthBytes(), PackageManagerShellCommandDataLoader.getStdInPFD(lookupShellCommand));
                    } else if (mode != 1) {
                        Slog.e(PackageManagerShellCommandDataLoader.TAG, "Unsupported metadata mode: " + ((int) fromByteArray.getMode()));
                        return false;
                    } else {
                        ParcelFileDescriptor localFilePFD = PackageManagerShellCommandDataLoader.getLocalFilePFD(lookupShellCommand, fromByteArray.getData());
                        this.mConnector.writeData(installationFile.getName(), 0L, localFilePFD.getStatSize(), localFilePFD);
                        IoUtils.closeQuietly(localFilePFD);
                    }
                }
                return true;
            } catch (IOException e) {
                Slog.e(PackageManagerShellCommandDataLoader.TAG, "Exception while streaming files", e);
                return false;
            }
        }
    }

    public static ShellCommand lookupShellCommand(String str) {
        WeakReference<ShellCommand> weakReference;
        int extractShellCommandId = extractShellCommandId(str);
        if (extractShellCommandId == -1) {
            return null;
        }
        SparseArray<WeakReference<ShellCommand>> sparseArray = sShellCommands;
        synchronized (sparseArray) {
            weakReference = sparseArray.get(extractShellCommandId, null);
        }
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    public static ParcelFileDescriptor getStdInPFD(ShellCommand shellCommand) {
        try {
            return ParcelFileDescriptor.dup(shellCommand.getInFileDescriptor());
        } catch (IOException e) {
            Slog.e(TAG, "Exception while obtaining STDIN fd", e);
            return null;
        }
    }

    public static ParcelFileDescriptor getLocalFilePFD(ShellCommand shellCommand, String str) {
        return shellCommand.openFileForSystem(str, "r");
    }

    public static int getStdIn(ShellCommand shellCommand) {
        ParcelFileDescriptor stdInPFD = getStdInPFD(shellCommand);
        if (stdInPFD == null) {
            return -1;
        }
        return stdInPFD.detachFd();
    }

    public static int getLocalFile(ShellCommand shellCommand, String str) {
        ParcelFileDescriptor localFilePFD = getLocalFilePFD(shellCommand, str);
        if (localFilePFD == null) {
            return -1;
        }
        return localFilePFD.detachFd();
    }

    public DataLoaderService.DataLoader onCreateDataLoader(DataLoaderParams dataLoaderParams) {
        if (dataLoaderParams.getType() == 1) {
            return new DataLoader();
        }
        return null;
    }
}
