package com.android.server.p011pm;

import android.content.pm.PackageInfo;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.security.FileIntegrity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.ShortcutPackageItem */
/* loaded from: classes2.dex */
public abstract class ShortcutPackageItem {
    public final ShortcutPackageInfo mPackageInfo;
    public final String mPackageName;
    public final int mPackageUserId;
    @GuardedBy({"mLock"})
    public ShortcutBitmapSaver mShortcutBitmapSaver;
    public ShortcutUser mShortcutUser;
    public final Object mLock = new Object();
    public final Runnable mSaveShortcutPackageRunner = new Runnable() { // from class: com.android.server.pm.ShortcutPackageItem$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            ShortcutPackageItem.this.saveShortcutPackageItem();
        }
    };

    public abstract boolean canRestoreAnyVersion();

    public abstract int getOwnerUserId();

    public abstract File getShortcutPackageItemFile();

    public abstract void onRestored(int i);

    public abstract void saveToXml(TypedXmlSerializer typedXmlSerializer, boolean z) throws IOException, XmlPullParserException;

    @GuardedBy({"mLock"})
    public void scheduleSaveToAppSearchLocked() {
    }

    public void verifyStates() {
    }

    public ShortcutPackageItem(ShortcutUser shortcutUser, int i, String str, ShortcutPackageInfo shortcutPackageInfo) {
        this.mShortcutUser = shortcutUser;
        this.mPackageUserId = i;
        this.mPackageName = (String) Preconditions.checkStringNotEmpty(str);
        Objects.requireNonNull(shortcutPackageInfo);
        this.mPackageInfo = shortcutPackageInfo;
        this.mShortcutBitmapSaver = new ShortcutBitmapSaver(shortcutUser.mService);
    }

    public void replaceUser(ShortcutUser shortcutUser) {
        this.mShortcutUser = shortcutUser;
    }

    public ShortcutUser getUser() {
        return this.mShortcutUser;
    }

    public int getPackageUserId() {
        return this.mPackageUserId;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public ShortcutPackageInfo getPackageInfo() {
        return this.mPackageInfo;
    }

    public void refreshPackageSignatureAndSave() {
        if (this.mPackageInfo.isShadow()) {
            return;
        }
        this.mPackageInfo.refreshSignature(this.mShortcutUser.mService, this);
        scheduleSave();
    }

    public void attemptToRestoreIfNeededAndSave() {
        int canRestoreTo;
        if (this.mPackageInfo.isShadow()) {
            ShortcutService shortcutService = this.mShortcutUser.mService;
            if (shortcutService.isPackageInstalled(this.mPackageName, this.mPackageUserId)) {
                if (!this.mPackageInfo.hasSignatures()) {
                    shortcutService.wtf("Attempted to restore package " + this.mPackageName + "/u" + this.mPackageUserId + " but signatures not found in the restore data.");
                    canRestoreTo = 102;
                } else {
                    PackageInfo packageInfoWithSignatures = shortcutService.getPackageInfoWithSignatures(this.mPackageName, this.mPackageUserId);
                    packageInfoWithSignatures.getLongVersionCode();
                    canRestoreTo = this.mPackageInfo.canRestoreTo(shortcutService, packageInfoWithSignatures, canRestoreAnyVersion());
                }
                onRestored(canRestoreTo);
                this.mPackageInfo.setShadow(false);
                scheduleSave();
            }
        }
    }

    @GuardedBy({"mLock"})
    public void saveToFileLocked(File file, boolean z) {
        TypedXmlSerializer resolveSerializer;
        AtomicFile atomicFile = new AtomicFile(file);
        FileOutputStream fileOutputStream = null;
        try {
            FileOutputStream startWrite = atomicFile.startWrite();
            try {
                if (z) {
                    resolveSerializer = Xml.newFastSerializer();
                    resolveSerializer.setOutput(startWrite, StandardCharsets.UTF_8.name());
                } else {
                    resolveSerializer = Xml.resolveSerializer(startWrite);
                }
                resolveSerializer.startDocument((String) null, Boolean.TRUE);
                saveToXml(resolveSerializer, z);
                resolveSerializer.endDocument();
                startWrite.flush();
                atomicFile.finishWrite(startWrite);
                try {
                    FileIntegrity.setUpFsVerity(file);
                } catch (IOException e) {
                    Slog.e("ShortcutService", "Failed to verity-protect " + file, e);
                }
            } catch (IOException | XmlPullParserException e2) {
                e = e2;
                fileOutputStream = startWrite;
                Slog.e("ShortcutService", "Failed to write to file " + atomicFile.getBaseFile(), e);
                atomicFile.failWrite(fileOutputStream);
            }
        } catch (IOException | XmlPullParserException e3) {
            e = e3;
        }
    }

    public JSONObject dumpCheckin(boolean z) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("name", this.mPackageName);
        return jSONObject;
    }

    public void scheduleSave() {
        ShortcutService shortcutService = this.mShortcutUser.mService;
        Runnable runnable = this.mSaveShortcutPackageRunner;
        shortcutService.injectPostToHandlerDebounced(runnable, runnable);
    }

    public void saveShortcutPackageItem() {
        waitForBitmapSaves();
        File shortcutPackageItemFile = getShortcutPackageItemFile();
        synchronized (this.mLock) {
            shortcutPackageItemFile.getParentFile().mkdirs();
            saveToFileLocked(shortcutPackageItemFile, false);
            scheduleSaveToAppSearchLocked();
        }
    }

    public boolean waitForBitmapSaves() {
        boolean waitForAllSavesLocked;
        synchronized (this.mLock) {
            waitForAllSavesLocked = this.mShortcutBitmapSaver.waitForAllSavesLocked();
        }
        return waitForAllSavesLocked;
    }

    public void saveBitmap(ShortcutInfo shortcutInfo, int i, Bitmap.CompressFormat compressFormat, int i2) {
        synchronized (this.mLock) {
            this.mShortcutBitmapSaver.saveBitmapLocked(shortcutInfo, i, compressFormat, i2);
        }
    }

    public String getBitmapPathMayWait(ShortcutInfo shortcutInfo) {
        String bitmapPathMayWaitLocked;
        synchronized (this.mLock) {
            bitmapPathMayWaitLocked = this.mShortcutBitmapSaver.getBitmapPathMayWaitLocked(shortcutInfo);
        }
        return bitmapPathMayWaitLocked;
    }

    public void removeIcon(ShortcutInfo shortcutInfo) {
        synchronized (this.mLock) {
            this.mShortcutBitmapSaver.removeIcon(shortcutInfo);
        }
    }

    public void removeShortcutPackageItem() {
        synchronized (this.mLock) {
            getShortcutPackageItemFile().delete();
        }
    }
}
