package com.android.server.backup;

import android.content.Context;
import android.util.Slog;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.backup.utils.DataStreamCodec;
import com.android.server.backup.utils.DataStreamFileCodec;
import com.android.server.backup.utils.PasswordUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
/* loaded from: classes.dex */
public final class BackupPasswordManager {
    public final File mBaseStateDir;
    public final Context mContext;
    public String mPasswordHash;
    public byte[] mPasswordSalt;
    public int mPasswordVersion;
    public final SecureRandom mRng;

    public BackupPasswordManager(Context context, File file, SecureRandom secureRandom) {
        this.mContext = context;
        this.mRng = secureRandom;
        this.mBaseStateDir = file;
        loadStateFromFilesystem();
    }

    public boolean hasBackupPassword() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "hasBackupPassword");
        String str = this.mPasswordHash;
        return str != null && str.length() > 0;
    }

    public boolean backupPasswordMatches(String str) {
        return !hasBackupPassword() || passwordMatchesSaved(str);
    }

    public boolean setBackupPassword(String str, String str2) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "setBackupPassword");
        if (passwordMatchesSaved(str)) {
            try {
                getPasswordVersionFileCodec().serialize(2);
                this.mPasswordVersion = 2;
                if (str2 == null || str2.isEmpty()) {
                    return clearPassword();
                }
                try {
                    byte[] randomSalt = randomSalt();
                    String buildPasswordHash = PasswordUtils.buildPasswordHash("PBKDF2WithHmacSHA1", str2, randomSalt, FrameworkStatsLog.WIFI_BYTES_TRANSFER);
                    getPasswordHashFileCodec().serialize(new BackupPasswordHash(buildPasswordHash, randomSalt));
                    this.mPasswordHash = buildPasswordHash;
                    this.mPasswordSalt = randomSalt;
                    return true;
                } catch (IOException unused) {
                    Slog.e("BackupPasswordManager", "Unable to set backup password");
                    return false;
                }
            } catch (IOException unused2) {
                Slog.e("BackupPasswordManager", "Unable to write backup pw version; password not changed");
                return false;
            }
        }
        return false;
    }

    public final boolean usePbkdf2Fallback() {
        return this.mPasswordVersion < 2;
    }

    public final boolean clearPassword() {
        File passwordHashFile = getPasswordHashFile();
        if (passwordHashFile.exists() && !passwordHashFile.delete()) {
            Slog.e("BackupPasswordManager", "Unable to clear backup password");
            return false;
        }
        this.mPasswordHash = null;
        this.mPasswordSalt = null;
        return true;
    }

    public final void loadStateFromFilesystem() {
        try {
            this.mPasswordVersion = getPasswordVersionFileCodec().deserialize().intValue();
        } catch (IOException unused) {
            Slog.e("BackupPasswordManager", "Unable to read backup pw version");
            this.mPasswordVersion = 1;
        }
        try {
            BackupPasswordHash deserialize = getPasswordHashFileCodec().deserialize();
            this.mPasswordHash = deserialize.hash;
            this.mPasswordSalt = deserialize.salt;
        } catch (IOException unused2) {
            Slog.e("BackupPasswordManager", "Unable to read saved backup pw hash");
        }
    }

    public final boolean passwordMatchesSaved(String str) {
        return passwordMatchesSaved("PBKDF2WithHmacSHA1", str) || (usePbkdf2Fallback() && passwordMatchesSaved("PBKDF2WithHmacSHA1And8bit", str));
    }

    public final boolean passwordMatchesSaved(String str, String str2) {
        if (this.mPasswordHash == null) {
            return str2 == null || str2.equals("");
        } else if (str2 == null || str2.length() == 0) {
            return false;
        } else {
            return this.mPasswordHash.equalsIgnoreCase(PasswordUtils.buildPasswordHash(str, str2, this.mPasswordSalt, FrameworkStatsLog.WIFI_BYTES_TRANSFER));
        }
    }

    public final byte[] randomSalt() {
        byte[] bArr = new byte[64];
        this.mRng.nextBytes(bArr);
        return bArr;
    }

    public final DataStreamFileCodec<Integer> getPasswordVersionFileCodec() {
        return new DataStreamFileCodec<>(new File(this.mBaseStateDir, "pwversion"), new PasswordVersionFileCodec());
    }

    public final DataStreamFileCodec<BackupPasswordHash> getPasswordHashFileCodec() {
        return new DataStreamFileCodec<>(getPasswordHashFile(), new PasswordHashFileCodec());
    }

    public final File getPasswordHashFile() {
        return new File(this.mBaseStateDir, "pwhash");
    }

    /* loaded from: classes.dex */
    public static final class BackupPasswordHash {
        public String hash;
        public byte[] salt;

        public BackupPasswordHash(String str, byte[] bArr) {
            this.hash = str;
            this.salt = bArr;
        }
    }

    /* loaded from: classes.dex */
    public static final class PasswordVersionFileCodec implements DataStreamCodec<Integer> {
        public PasswordVersionFileCodec() {
        }

        @Override // com.android.server.backup.utils.DataStreamCodec
        public void serialize(Integer num, DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.write(num.intValue());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.server.backup.utils.DataStreamCodec
        public Integer deserialize(DataInputStream dataInputStream) throws IOException {
            return Integer.valueOf(dataInputStream.readInt());
        }
    }

    /* loaded from: classes.dex */
    public static final class PasswordHashFileCodec implements DataStreamCodec<BackupPasswordHash> {
        public PasswordHashFileCodec() {
        }

        @Override // com.android.server.backup.utils.DataStreamCodec
        public void serialize(BackupPasswordHash backupPasswordHash, DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeInt(backupPasswordHash.salt.length);
            dataOutputStream.write(backupPasswordHash.salt);
            dataOutputStream.writeUTF(backupPasswordHash.hash);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.server.backup.utils.DataStreamCodec
        public BackupPasswordHash deserialize(DataInputStream dataInputStream) throws IOException {
            byte[] bArr = new byte[dataInputStream.readInt()];
            dataInputStream.readFully(bArr);
            return new BackupPasswordHash(dataInputStream.readUTF(), bArr);
        }
    }
}
