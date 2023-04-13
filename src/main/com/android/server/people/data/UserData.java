package com.android.server.people.data;

import android.os.Environment;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Slog;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;
/* loaded from: classes2.dex */
public class UserData {
    public static final String TAG = "UserData";
    public String mDefaultDialer;
    public String mDefaultSmsApp;
    public boolean mIsUnlocked;
    public Map<String, PackageData> mPackageDataMap = new ArrayMap();
    public final File mPerUserPeopleDataDir;
    public final ScheduledExecutorService mScheduledExecutorService;
    public final int mUserId;

    public UserData(int i, ScheduledExecutorService scheduledExecutorService) {
        this.mUserId = i;
        this.mPerUserPeopleDataDir = new File(Environment.getDataSystemCeDirectory(i), "people");
        this.mScheduledExecutorService = scheduledExecutorService;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public void forAllPackages(Consumer<PackageData> consumer) {
        for (PackageData packageData : this.mPackageDataMap.values()) {
            consumer.accept(packageData);
        }
    }

    public void setUserUnlocked() {
        this.mIsUnlocked = true;
    }

    public void setUserStopped() {
        this.mIsUnlocked = false;
    }

    public boolean isUnlocked() {
        return this.mIsUnlocked;
    }

    public void loadUserData() {
        this.mPerUserPeopleDataDir.mkdir();
        this.mPackageDataMap.putAll(PackageData.packagesDataFromDisk(this.mUserId, new UserData$$ExternalSyntheticLambda1(this), new UserData$$ExternalSyntheticLambda2(this), this.mScheduledExecutorService, this.mPerUserPeopleDataDir));
    }

    public /* synthetic */ PackageData lambda$getOrCreatePackageData$0(String str, String str2) {
        return createPackageData(str);
    }

    public PackageData getOrCreatePackageData(final String str) {
        return this.mPackageDataMap.computeIfAbsent(str, new Function() { // from class: com.android.server.people.data.UserData$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                PackageData lambda$getOrCreatePackageData$0;
                lambda$getOrCreatePackageData$0 = UserData.this.lambda$getOrCreatePackageData$0(str, (String) obj);
                return lambda$getOrCreatePackageData$0;
            }
        });
    }

    public PackageData getPackageData(String str) {
        return this.mPackageDataMap.get(str);
    }

    public void deletePackageData(String str) {
        PackageData remove = this.mPackageDataMap.remove(str);
        if (remove != null) {
            remove.onDestroy();
        }
    }

    public void setDefaultDialer(String str) {
        this.mDefaultDialer = str;
    }

    public PackageData getDefaultDialer() {
        String str = this.mDefaultDialer;
        if (str != null) {
            return getPackageData(str);
        }
        return null;
    }

    public void setDefaultSmsApp(String str) {
        this.mDefaultSmsApp = str;
    }

    public PackageData getDefaultSmsApp() {
        String str = this.mDefaultSmsApp;
        if (str != null) {
            return getPackageData(str);
        }
        return null;
    }

    public byte[] getBackupPayload() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        for (PackageData packageData : this.mPackageDataMap.values()) {
            try {
                byte[] backupPayload = packageData.getConversationStore().getBackupPayload();
                dataOutputStream.writeInt(backupPayload.length);
                dataOutputStream.write(backupPayload);
                dataOutputStream.writeUTF(packageData.getPackageName());
            } catch (IOException e) {
                Slog.e(TAG, "Failed to write conversations to backup payload.", e);
                return null;
            }
        }
        try {
            dataOutputStream.writeInt(-1);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e2) {
            Slog.e(TAG, "Failed to write conversations end token to backup payload.", e2);
            return null;
        }
    }

    public void restore(byte[] bArr) {
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bArr));
        try {
            for (int readInt = dataInputStream.readInt(); readInt != -1; readInt = dataInputStream.readInt()) {
                byte[] bArr2 = new byte[readInt];
                dataInputStream.readFully(bArr2, 0, readInt);
                getOrCreatePackageData(dataInputStream.readUTF()).getConversationStore().restore(bArr2);
            }
        } catch (IOException e) {
            Slog.e(TAG, "Failed to restore conversations from backup payload.", e);
        }
    }

    public final PackageData createPackageData(String str) {
        return new PackageData(str, this.mUserId, new UserData$$ExternalSyntheticLambda1(this), new UserData$$ExternalSyntheticLambda2(this), this.mScheduledExecutorService, this.mPerUserPeopleDataDir);
    }

    public final boolean isDefaultDialer(String str) {
        return TextUtils.equals(this.mDefaultDialer, str);
    }

    public final boolean isDefaultSmsApp(String str) {
        return TextUtils.equals(this.mDefaultSmsApp, str);
    }
}
