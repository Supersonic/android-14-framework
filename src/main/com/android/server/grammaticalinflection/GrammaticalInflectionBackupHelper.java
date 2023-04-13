package com.android.server.grammaticalinflection;

import android.app.backup.BackupManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.Log;
import android.util.SparseArray;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Clock;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class GrammaticalInflectionBackupHelper {
    public static final Duration STAGE_DATA_RETENTION_PERIOD = Duration.ofDays(3);
    public static final String TAG = "GrammaticalInflectionBackupHelper";
    public final SparseArray<StagedData> mCache = new SparseArray<>();
    public final Object mCacheLock = new Object();
    public final Clock mClock = Clock.systemUTC();
    public final GrammaticalInflectionService mGrammaticalGenderService;
    public final PackageManager mPackageManager;

    /* loaded from: classes.dex */
    public static class StagedData {
        public final long mCreationTimeMillis;
        public final HashMap<String, Integer> mPackageStates = new HashMap<>();

        public StagedData(long j) {
            this.mCreationTimeMillis = j;
        }
    }

    public GrammaticalInflectionBackupHelper(GrammaticalInflectionService grammaticalInflectionService, PackageManager packageManager) {
        this.mGrammaticalGenderService = grammaticalInflectionService;
        this.mPackageManager = packageManager;
    }

    public byte[] getBackupPayload(int i) {
        synchronized (this.mCacheLock) {
            cleanStagedDataForOldEntries();
        }
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (ApplicationInfo applicationInfo : this.mPackageManager.getInstalledApplicationsAsUser(PackageManager.ApplicationInfoFlags.of(0L), i)) {
            int applicationGrammaticalGender = this.mGrammaticalGenderService.getApplicationGrammaticalGender(applicationInfo.packageName, i);
            if (applicationGrammaticalGender != 0) {
                hashMap.put(applicationInfo.packageName, Integer.valueOf(applicationGrammaticalGender));
            }
        }
        if (hashMap.isEmpty()) {
            return null;
        }
        return convertToByteArray(hashMap);
    }

    public void stageAndApplyRestoredPayload(byte[] bArr, int i) {
        synchronized (this.mCacheLock) {
            cleanStagedDataForOldEntries();
            HashMap<String, Integer> readFromByteArray = readFromByteArray(bArr);
            if (readFromByteArray.isEmpty()) {
                return;
            }
            StagedData stagedData = new StagedData(this.mClock.millis());
            for (Map.Entry<String, Integer> entry : readFromByteArray.entrySet()) {
                if (isPackageInstalledForUser(entry.getKey(), i)) {
                    if (!hasSetBeforeRestoring(entry.getKey(), i)) {
                        this.mGrammaticalGenderService.setRequestedApplicationGrammaticalGender(entry.getKey(), i, entry.getValue().intValue());
                    }
                } else if (entry.getValue().intValue() != 0) {
                    stagedData.mPackageStates.put(entry.getKey(), entry.getValue());
                }
            }
            this.mCache.append(i, stagedData);
        }
    }

    public final boolean hasSetBeforeRestoring(String str, int i) {
        return this.mGrammaticalGenderService.getApplicationGrammaticalGender(str, i) != 0;
    }

    public void onPackageAdded(String str, int i) {
        int intValue;
        synchronized (this.mCacheLock) {
            int userId = UserHandle.getUserId(i);
            StagedData stagedData = this.mCache.get(userId);
            if (stagedData != null && stagedData.mPackageStates.containsKey(str) && (intValue = stagedData.mPackageStates.get(str).intValue()) != 0) {
                this.mGrammaticalGenderService.setRequestedApplicationGrammaticalGender(str, userId, intValue);
            }
        }
    }

    public void onPackageDataCleared() {
        notifyBackupManager();
    }

    public void onPackageRemoved() {
        notifyBackupManager();
    }

    public static void notifyBackupManager() {
        BackupManager.dataChanged(PackageManagerShellCommandDataLoader.PACKAGE);
    }

    public final byte[] convertToByteArray(HashMap<String, Integer> hashMap) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(hashMap);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            objectOutputStream.close();
            byteArrayOutputStream.close();
            return byteArray;
        } catch (IOException e) {
            Log.e(TAG, "cannot convert payload to byte array.", e);
            return null;
        }
    }

    public final HashMap<String, Integer> readFromByteArray(byte[] bArr) {
        HashMap<String, Integer> hashMap;
        Exception e;
        Throwable th;
        ObjectInputStream objectInputStream;
        HashMap<String, Integer> hashMap2 = new HashMap<>();
        try {
            try {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
                try {
                    objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    hashMap = (HashMap) objectInputStream.readObject();
                } catch (Throwable th2) {
                    th = th2;
                }
                try {
                    objectInputStream.close();
                    byteArrayInputStream.close();
                } catch (Throwable th3) {
                    th = th3;
                    byteArrayInputStream.close();
                    throw th;
                }
            } catch (IOException | ClassNotFoundException e2) {
                e = e2;
                Log.e(TAG, "cannot convert payload to HashMap.", e);
                e.printStackTrace();
                return hashMap;
            }
        } catch (IOException | ClassNotFoundException e3) {
            hashMap = hashMap2;
            e = e3;
            Log.e(TAG, "cannot convert payload to HashMap.", e);
            e.printStackTrace();
            return hashMap;
        }
        return hashMap;
    }

    public final void cleanStagedDataForOldEntries() {
        for (int i = 0; i < this.mCache.size(); i++) {
            int keyAt = this.mCache.keyAt(i);
            if (this.mCache.get(keyAt).mCreationTimeMillis < this.mClock.millis() - STAGE_DATA_RETENTION_PERIOD.toMillis()) {
                this.mCache.remove(keyAt);
            }
        }
    }

    public final boolean isPackageInstalledForUser(String str, int i) {
        PackageInfo packageInfo;
        try {
            packageInfo = this.mPackageManager.getPackageInfoAsUser(str, 0, i);
        } catch (PackageManager.NameNotFoundException unused) {
            packageInfo = null;
        }
        return packageInfo != null;
    }
}
