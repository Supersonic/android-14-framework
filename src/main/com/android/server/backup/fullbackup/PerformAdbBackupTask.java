package com.android.server.backup.fullbackup;

import android.app.backup.IFullBackupRestoreObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.ParcelFileDescriptor;
import android.util.Slog;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.backup.BackupRestoreTask;
import com.android.server.backup.OperationStorage;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.utils.BackupEligibilityRules;
import com.android.server.backup.utils.PasswordUtils;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes.dex */
public class PerformAdbBackupTask extends FullBackupTask implements BackupRestoreTask {
    public final boolean mAllApps;
    public final BackupEligibilityRules mBackupEligibilityRules;
    public final boolean mCompress;
    public final int mCurrentOpToken;
    public final String mCurrentPassword;
    public PackageInfo mCurrentTarget;
    public final boolean mDoWidgets;
    public final String mEncryptPassword;
    public final boolean mIncludeApks;
    public final boolean mIncludeObbs;
    public final boolean mIncludeShared;
    public final boolean mIncludeSystem;
    public final boolean mKeyValue;
    public final AtomicBoolean mLatch;
    public final OperationStorage mOperationStorage;
    public final ParcelFileDescriptor mOutputFile;
    public final ArrayList<String> mPackages;
    public final UserBackupManagerService mUserBackupManagerService;

    @Override // com.android.server.backup.BackupRestoreTask
    public void execute() {
    }

    @Override // com.android.server.backup.BackupRestoreTask
    public void operationComplete(long j) {
    }

    public PerformAdbBackupTask(UserBackupManagerService userBackupManagerService, OperationStorage operationStorage, ParcelFileDescriptor parcelFileDescriptor, IFullBackupRestoreObserver iFullBackupRestoreObserver, boolean z, boolean z2, boolean z3, boolean z4, String str, String str2, boolean z5, boolean z6, boolean z7, boolean z8, String[] strArr, AtomicBoolean atomicBoolean, BackupEligibilityRules backupEligibilityRules) {
        super(iFullBackupRestoreObserver);
        ArrayList<String> arrayList;
        this.mUserBackupManagerService = userBackupManagerService;
        this.mOperationStorage = operationStorage;
        this.mCurrentOpToken = userBackupManagerService.generateRandomIntegerToken();
        this.mLatch = atomicBoolean;
        this.mOutputFile = parcelFileDescriptor;
        this.mIncludeApks = z;
        this.mIncludeObbs = z2;
        this.mIncludeShared = z3;
        this.mDoWidgets = z4;
        this.mAllApps = z5;
        this.mIncludeSystem = z6;
        if (strArr == null) {
            arrayList = new ArrayList<>();
        } else {
            arrayList = new ArrayList<>(Arrays.asList(strArr));
        }
        this.mPackages = arrayList;
        this.mCurrentPassword = str;
        if (str2 == null || "".equals(str2)) {
            this.mEncryptPassword = str;
        } else {
            this.mEncryptPassword = str2;
        }
        this.mCompress = z7;
        this.mKeyValue = z8;
        this.mBackupEligibilityRules = backupEligibilityRules;
    }

    public final void addPackagesToSet(TreeMap<String, PackageInfo> treeMap, List<String> list) {
        for (String str : list) {
            if (!treeMap.containsKey(str)) {
                try {
                    treeMap.put(str, this.mUserBackupManagerService.getPackageManager().getPackageInfo(str, 134217728));
                } catch (PackageManager.NameNotFoundException unused) {
                    Slog.w("BackupManagerService", "Unknown package " + str + ", skipping");
                }
            }
        }
    }

    public final OutputStream emitAesBackupHeader(StringBuilder sb, OutputStream outputStream) throws Exception {
        byte[] randomBytes = this.mUserBackupManagerService.randomBytes(512);
        SecretKey buildPasswordKey = PasswordUtils.buildPasswordKey("PBKDF2WithHmacSHA1", this.mEncryptPassword, randomBytes, FrameworkStatsLog.WIFI_BYTES_TRANSFER);
        byte[] bArr = new byte[32];
        this.mUserBackupManagerService.getRng().nextBytes(bArr);
        byte[] randomBytes2 = this.mUserBackupManagerService.randomBytes(512);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "AES");
        cipher.init(1, secretKeySpec);
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
        sb.append("AES-256");
        sb.append('\n');
        sb.append(PasswordUtils.byteArrayToHex(randomBytes));
        sb.append('\n');
        sb.append(PasswordUtils.byteArrayToHex(randomBytes2));
        sb.append('\n');
        sb.append(FrameworkStatsLog.WIFI_BYTES_TRANSFER);
        sb.append('\n');
        Cipher cipher2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher2.init(1, buildPasswordKey);
        sb.append(PasswordUtils.byteArrayToHex(cipher2.getIV()));
        sb.append('\n');
        byte[] iv = cipher.getIV();
        byte[] encoded = secretKeySpec.getEncoded();
        byte[] makeKeyChecksum = PasswordUtils.makeKeyChecksum("PBKDF2WithHmacSHA1", secretKeySpec.getEncoded(), randomBytes2, FrameworkStatsLog.WIFI_BYTES_TRANSFER);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(iv.length + encoded.length + makeKeyChecksum.length + 3);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeByte(iv.length);
        dataOutputStream.write(iv);
        dataOutputStream.writeByte(encoded.length);
        dataOutputStream.write(encoded);
        dataOutputStream.writeByte(makeKeyChecksum.length);
        dataOutputStream.write(makeKeyChecksum);
        dataOutputStream.flush();
        sb.append(PasswordUtils.byteArrayToHex(cipher2.doFinal(byteArrayOutputStream.toByteArray())));
        sb.append('\n');
        return cipherOutputStream;
    }

    public final void finalizeBackup(OutputStream outputStream) {
        try {
            outputStream.write(new byte[1024]);
        } catch (IOException unused) {
            Slog.w("BackupManagerService", "Error attempting to finalize backup stream");
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:97:0x0261
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:81)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:47)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
        */
    @Override // java.lang.Runnable
    public void run() {
        /*
            Method dump skipped, instructions count: 1217
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.fullbackup.PerformAdbBackupTask.run():void");
    }

    @Override // com.android.server.backup.BackupRestoreTask
    public void handleCancel(boolean z) {
        PackageInfo packageInfo = this.mCurrentTarget;
        Slog.w("BackupManagerService", "adb backup cancel of " + packageInfo);
        if (packageInfo != null) {
            this.mUserBackupManagerService.tearDownAgentAndKill(this.mCurrentTarget.applicationInfo);
        }
        this.mOperationStorage.removeOperation(this.mCurrentOpToken);
    }
}
