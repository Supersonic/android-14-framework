package com.android.server.backup.restore;

import android.app.backup.IFullBackupRestoreObserver;
import android.content.p000pm.PackageManagerInternal;
import android.os.ParcelFileDescriptor;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import com.android.server.backup.OperationStorage;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.fullbackup.FullBackupObbConnection;
import com.android.server.backup.utils.BackupEligibilityRules;
import com.android.server.backup.utils.FullBackupRestoreObserverUtils;
import com.android.server.backup.utils.PasswordUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.InflaterInputStream;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes.dex */
public class PerformAdbRestoreTask implements Runnable {
    public final UserBackupManagerService mBackupManagerService;
    public final String mCurrentPassword;
    public final String mDecryptPassword;
    public final ParcelFileDescriptor mInputFile;
    public final AtomicBoolean mLatchObject;
    public final FullBackupObbConnection mObbConnection;
    public IFullBackupRestoreObserver mObserver;
    public final OperationStorage mOperationStorage;

    public PerformAdbRestoreTask(UserBackupManagerService userBackupManagerService, OperationStorage operationStorage, ParcelFileDescriptor parcelFileDescriptor, String str, String str2, IFullBackupRestoreObserver iFullBackupRestoreObserver, AtomicBoolean atomicBoolean) {
        this.mBackupManagerService = userBackupManagerService;
        this.mOperationStorage = operationStorage;
        this.mInputFile = parcelFileDescriptor;
        this.mCurrentPassword = str;
        this.mDecryptPassword = str2;
        this.mObserver = iFullBackupRestoreObserver;
        this.mLatchObject = atomicBoolean;
        this.mObbConnection = new FullBackupObbConnection(userBackupManagerService);
    }

    /* JADX WARN: Removed duplicated region for block: B:91:0x0120 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:95:0x0162 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // java.lang.Runnable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void run() {
        InputStream parseBackupFileHeaderAndReturnTarStream;
        Slog.i("BackupManagerService", "--- Performing full-dataset restore ---");
        this.mObbConnection.establish();
        this.mObserver = FullBackupRestoreObserverUtils.sendStartRestore(this.mObserver);
        FileInputStream fileInputStream = null;
        try {
            try {
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception unused) {
        }
        if (this.mBackupManagerService.backupPasswordMatches(this.mCurrentPassword)) {
            FileInputStream fileInputStream2 = new FileInputStream(this.mInputFile.getFileDescriptor());
            try {
                parseBackupFileHeaderAndReturnTarStream = parseBackupFileHeaderAndReturnTarStream(fileInputStream2, this.mDecryptPassword);
            } catch (Exception unused2) {
                fileInputStream = fileInputStream2;
                Slog.e("BackupManagerService", "Unable to read restore input");
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        Slog.w("BackupManagerService", "Close of restore data pipe threw", e);
                        synchronized (this.mLatchObject) {
                            this.mLatchObject.set(true);
                            this.mLatchObject.notifyAll();
                        }
                        this.mObbConnection.tearDown();
                        this.mObserver = FullBackupRestoreObserverUtils.sendEndRestore(this.mObserver);
                        Slog.d("BackupManagerService", "Full restore pass complete.");
                        this.mBackupManagerService.getWakelock().release();
                        return;
                    }
                }
                this.mInputFile.close();
                synchronized (this.mLatchObject) {
                }
            } catch (Throwable th2) {
                th = th2;
                fileInputStream = fileInputStream2;
                Throwable th3 = th;
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e2) {
                        Slog.w("BackupManagerService", "Close of restore data pipe threw", e2);
                        synchronized (this.mLatchObject) {
                            this.mLatchObject.set(true);
                            this.mLatchObject.notifyAll();
                        }
                        this.mObbConnection.tearDown();
                        this.mObserver = FullBackupRestoreObserverUtils.sendEndRestore(this.mObserver);
                        Slog.d("BackupManagerService", "Full restore pass complete.");
                        this.mBackupManagerService.getWakelock().release();
                        throw th3;
                    }
                }
                this.mInputFile.close();
                synchronized (this.mLatchObject) {
                }
            }
            if (parseBackupFileHeaderAndReturnTarStream != null) {
                new FullRestoreEngineThread(new FullRestoreEngine(this.mBackupManagerService, this.mOperationStorage, null, this.mObserver, null, null, true, 0, true, new BackupEligibilityRules(this.mBackupManagerService.getPackageManager(), (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class), this.mBackupManagerService.getUserId(), this.mBackupManagerService.getContext(), 2)), parseBackupFileHeaderAndReturnTarStream).run();
                try {
                    fileInputStream2.close();
                    this.mInputFile.close();
                } catch (IOException e3) {
                    Slog.w("BackupManagerService", "Close of restore data pipe threw", e3);
                }
                synchronized (this.mLatchObject) {
                    this.mLatchObject.set(true);
                    this.mLatchObject.notifyAll();
                }
                this.mObbConnection.tearDown();
                this.mObserver = FullBackupRestoreObserverUtils.sendEndRestore(this.mObserver);
                Slog.d("BackupManagerService", "Full restore pass complete.");
                this.mBackupManagerService.getWakelock().release();
                return;
            }
            try {
                fileInputStream2.close();
                this.mInputFile.close();
            } catch (IOException e4) {
                Slog.w("BackupManagerService", "Close of restore data pipe threw", e4);
            }
            synchronized (this.mLatchObject) {
                this.mLatchObject.set(true);
                this.mLatchObject.notifyAll();
            }
        } else {
            Slog.w("BackupManagerService", "Backup password mismatch; aborting");
            try {
                this.mInputFile.close();
            } catch (IOException e5) {
                Slog.w("BackupManagerService", "Close of restore data pipe threw", e5);
            }
            synchronized (this.mLatchObject) {
                this.mLatchObject.set(true);
                this.mLatchObject.notifyAll();
            }
        }
        this.mObbConnection.tearDown();
        this.mObserver = FullBackupRestoreObserverUtils.sendEndRestore(this.mObserver);
        Slog.d("BackupManagerService", "Full restore pass complete.");
        this.mBackupManagerService.getWakelock().release();
    }

    public static void readFullyOrThrow(InputStream inputStream, byte[] bArr) throws IOException {
        int i = 0;
        while (i < bArr.length) {
            int read = inputStream.read(bArr, i, bArr.length - i);
            if (read <= 0) {
                throw new IOException("Couldn't fully read data");
            }
            i += read;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x0051, code lost:
        if (r8 != null) goto L13;
     */
    @VisibleForTesting
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static InputStream parseBackupFileHeaderAndReturnTarStream(InputStream inputStream, String str) throws IOException {
        boolean z;
        boolean z2;
        byte[] bArr = new byte[15];
        readFullyOrThrow(inputStream, bArr);
        boolean z3 = false;
        if (Arrays.equals("ANDROID BACKUP\n".getBytes("UTF-8"), bArr)) {
            String readHeaderLine = readHeaderLine(inputStream);
            int parseInt = Integer.parseInt(readHeaderLine);
            if (parseInt <= 5) {
                boolean z4 = parseInt == 1;
                boolean z5 = Integer.parseInt(readHeaderLine(inputStream)) != 0;
                String readHeaderLine2 = readHeaderLine(inputStream);
                if (!readHeaderLine2.equals("none")) {
                    if (str != null && str.length() > 0) {
                        inputStream = decodeAesHeaderAndInitialize(str, readHeaderLine2, z4, inputStream);
                    } else {
                        Slog.w("BackupManagerService", "Archive is encrypted but no password given");
                    }
                    z2 = z3;
                    z3 = z5;
                }
                z3 = true;
                z2 = z3;
                z3 = z5;
            } else {
                Slog.w("BackupManagerService", "Wrong header version: " + readHeaderLine);
                z2 = false;
            }
            boolean z6 = z3;
            z3 = z2;
            z = z6;
        } else {
            Slog.w("BackupManagerService", "Didn't read the right header magic");
            z = false;
        }
        if (z3) {
            return z ? new InflaterInputStream(inputStream) : inputStream;
        }
        Slog.w("BackupManagerService", "Invalid restore data; aborting.");
        return null;
    }

    public static String readHeaderLine(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder(80);
        while (true) {
            int read = inputStream.read();
            if (read < 0 || read == 10) {
                break;
            }
            sb.append((char) read);
        }
        return sb.toString();
    }

    public static InputStream attemptEncryptionKeyDecryption(String str, String str2, byte[] bArr, byte[] bArr2, int i, String str3, String str4, InputStream inputStream, boolean z) {
        CipherInputStream cipherInputStream = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKey buildPasswordKey = PasswordUtils.buildPasswordKey(str2, str, bArr, i);
            cipher.init(2, new SecretKeySpec(buildPasswordKey.getEncoded(), "AES"), new IvParameterSpec(PasswordUtils.hexToByteArray(str3)));
            byte[] doFinal = cipher.doFinal(PasswordUtils.hexToByteArray(str4));
            int i2 = doFinal[0] + 1;
            byte[] copyOfRange = Arrays.copyOfRange(doFinal, 1, i2);
            int i3 = i2 + 1;
            int i4 = doFinal[i2] + i3;
            byte[] copyOfRange2 = Arrays.copyOfRange(doFinal, i3, i4);
            int i5 = i4 + 1;
            if (Arrays.equals(PasswordUtils.makeKeyChecksum(str2, copyOfRange2, bArr2, i), Arrays.copyOfRange(doFinal, i5, doFinal[i4] + i5))) {
                cipher.init(2, new SecretKeySpec(copyOfRange2, "AES"), new IvParameterSpec(copyOfRange));
                cipherInputStream = new CipherInputStream(inputStream, cipher);
            } else if (z) {
                Slog.w("BackupManagerService", "Incorrect password");
            }
        } catch (InvalidAlgorithmParameterException e) {
            if (z) {
                Slog.e("BackupManagerService", "Needed parameter spec unavailable!", e);
            }
        } catch (InvalidKeyException unused) {
            if (z) {
                Slog.w("BackupManagerService", "Illegal password; aborting");
            }
        } catch (NoSuchAlgorithmException unused2) {
            if (z) {
                Slog.e("BackupManagerService", "Needed decryption algorithm unavailable!");
            }
        } catch (BadPaddingException unused3) {
            if (z) {
                Slog.w("BackupManagerService", "Incorrect password");
            }
        } catch (IllegalBlockSizeException unused4) {
            if (z) {
                Slog.w("BackupManagerService", "Invalid block size in encryption key");
            }
        } catch (NoSuchPaddingException unused5) {
            if (z) {
                Slog.e("BackupManagerService", "Needed padding mechanism unavailable!");
            }
        }
        return cipherInputStream;
    }

    public static InputStream decodeAesHeaderAndInitialize(String str, String str2, boolean z, InputStream inputStream) {
        InputStream inputStream2 = null;
        try {
            if (str2.equals("AES-256")) {
                byte[] hexToByteArray = PasswordUtils.hexToByteArray(readHeaderLine(inputStream));
                byte[] hexToByteArray2 = PasswordUtils.hexToByteArray(readHeaderLine(inputStream));
                int parseInt = Integer.parseInt(readHeaderLine(inputStream));
                String readHeaderLine = readHeaderLine(inputStream);
                String readHeaderLine2 = readHeaderLine(inputStream);
                inputStream2 = attemptEncryptionKeyDecryption(str, "PBKDF2WithHmacSHA1", hexToByteArray, hexToByteArray2, parseInt, readHeaderLine, readHeaderLine2, inputStream, false);
                if (inputStream2 == null && z) {
                    inputStream2 = attemptEncryptionKeyDecryption(str, "PBKDF2WithHmacSHA1And8bit", hexToByteArray, hexToByteArray2, parseInt, readHeaderLine, readHeaderLine2, inputStream, true);
                }
            } else {
                Slog.w("BackupManagerService", "Unsupported encryption method: " + str2);
            }
        } catch (IOException unused) {
            Slog.w("BackupManagerService", "Can't read input header");
        } catch (NumberFormatException unused2) {
            Slog.w("BackupManagerService", "Can't parse restore data header");
        }
        return inputStream2;
    }
}
