package com.android.server.backup;

import android.app.IWallpaperManager;
import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupHelper;
import android.app.backup.FullBackup;
import android.app.backup.FullBackupDataOutput;
import android.app.backup.WallpaperBackupHelper;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArraySet;
import android.util.Slog;
import com.google.android.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.util.Set;
/* loaded from: classes.dex */
public class SystemBackupAgent extends BackupAgentHelper {
    public static final Set<String> sEligibleHelpersForNonSystemUser;
    public static final Set<String> sEligibleHelpersForProfileUser;
    public static final String WALLPAPER_IMAGE_DIR = Environment.getUserSystemDirectory(0).getAbsolutePath();
    public static final String WALLPAPER_IMAGE = new File(Environment.getUserSystemDirectory(0), "wallpaper").getAbsolutePath();
    public static final String WALLPAPER_INFO_DIR = Environment.getUserSystemDirectory(0).getAbsolutePath();
    public static final String WALLPAPER_INFO = new File(Environment.getUserSystemDirectory(0), "wallpaper_info.xml").getAbsolutePath();
    public int mUserId = 0;
    public boolean mIsProfileUser = false;

    @Override // android.app.backup.BackupAgent
    public void onFullBackup(FullBackupDataOutput fullBackupDataOutput) throws IOException {
    }

    static {
        ArraySet newArraySet = Sets.newArraySet(new String[]{"permissions", "notifications", "account_sync_settings", "app_locales"});
        sEligibleHelpersForProfileUser = newArraySet;
        sEligibleHelpersForNonSystemUser = SetUtils.union(newArraySet, Sets.newArraySet(new String[]{"account_manager", "usage_stats", "preferred_activities", "shortcut_manager"}));
    }

    public void onCreate(UserHandle userHandle, int i) {
        super.onCreate(userHandle, i);
        int identifier = userHandle.getIdentifier();
        this.mUserId = identifier;
        if (identifier != 0) {
            this.mIsProfileUser = ((UserManager) createContextAsUser(userHandle, 0).getSystemService(UserManager.class)).isProfile();
        }
        addHelperIfEligibleForUser("account_sync_settings", new AccountSyncSettingsBackupHelper(this, this.mUserId));
        addHelperIfEligibleForUser("preferred_activities", new PreferredActivityBackupHelper(this.mUserId));
        addHelperIfEligibleForUser("notifications", new NotificationBackupHelper(this.mUserId));
        addHelperIfEligibleForUser("permissions", new PermissionBackupHelper(this.mUserId));
        addHelperIfEligibleForUser("usage_stats", new UsageStatsBackupHelper(this.mUserId));
        addHelperIfEligibleForUser("shortcut_manager", new ShortcutBackupHelper(this.mUserId));
        addHelperIfEligibleForUser("account_manager", new AccountManagerBackupHelper(this.mUserId));
        addHelperIfEligibleForUser("slices", new SliceBackupHelper(this));
        addHelperIfEligibleForUser("people", new PeopleBackupHelper(this.mUserId));
        addHelperIfEligibleForUser("app_locales", new AppSpecificLocalesBackupHelper(this.mUserId));
        addHelperIfEligibleForUser("app_gender", new AppGrammaticalGenderBackupHelper(this.mUserId));
    }

    @Override // android.app.backup.BackupAgentHelper, android.app.backup.BackupAgent
    public void onRestore(BackupDataInput backupDataInput, int i, ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        addHelper("wallpaper", new WallpaperBackupHelper(this, new String[]{"/data/data/com.android.settings/files/wallpaper"}));
        addHelper("system_files", new WallpaperBackupHelper(this, new String[]{"/data/data/com.android.settings/files/wallpaper"}));
        super.onRestore(backupDataInput, i, parcelFileDescriptor);
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0053 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onRestoreFile(ParcelFileDescriptor parcelFileDescriptor, long j, int i, String str, String str2, long j2, long j3) throws IOException {
        boolean z;
        File file;
        File file2;
        IWallpaperManager service;
        Slog.i("SystemBackupAgent", "Restoring file domain=" + str + " path=" + str2);
        if (str.equals("r")) {
            z = true;
            if (str2.equals("wallpaper_info.xml")) {
                file = new File(WALLPAPER_INFO);
            } else if (str2.equals("wallpaper")) {
                file = new File(WALLPAPER_IMAGE);
            }
            file2 = file;
            if (file2 == null) {
                try {
                    Slog.w("SystemBackupAgent", "Skipping unrecognized system file: [ " + str + " : " + str2 + " ]");
                } catch (IOException unused) {
                    if (z) {
                        new File(WALLPAPER_IMAGE).delete();
                        new File(WALLPAPER_INFO).delete();
                        return;
                    }
                    return;
                }
            }
            FullBackup.restoreFile(parcelFileDescriptor, j, i, j2, j3, file2);
            if (z || (service = ServiceManager.getService("wallpaper")) == null) {
            }
            try {
                service.settingsRestored();
                return;
            } catch (RemoteException e) {
                Slog.e("SystemBackupAgent", "Couldn't restore settings\n" + e);
                return;
            }
        }
        z = false;
        file = null;
        file2 = file;
        if (file2 == null) {
        }
        FullBackup.restoreFile(parcelFileDescriptor, j, i, j2, j3, file2);
        if (z) {
        }
    }

    public final void addHelperIfEligibleForUser(String str, BackupHelper backupHelper) {
        if (isHelperEligibleForUser(str)) {
            addHelper(str, backupHelper);
        }
    }

    public final boolean isHelperEligibleForUser(String str) {
        if (this.mUserId == 0) {
            return true;
        }
        if (this.mIsProfileUser) {
            return sEligibleHelpersForProfileUser.contains(str);
        }
        return sEligibleHelpersForNonSystemUser.contains(str);
    }
}
