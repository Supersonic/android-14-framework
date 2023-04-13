package com.android.server;

import android.content.Context;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.ISystemUpdateManager;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class SystemUpdateManagerService extends ISystemUpdateManager.Stub {
    public final Context mContext;
    public final AtomicFile mFile;
    public int mLastStatus;
    public int mLastUid;
    public final Object mLock;

    public SystemUpdateManagerService(Context context) {
        Object obj = new Object();
        this.mLock = obj;
        this.mLastUid = -1;
        this.mLastStatus = 0;
        this.mContext = context;
        this.mFile = new AtomicFile(new File(Environment.getDataSystemDirectory(), "system-update-info.xml"));
        synchronized (obj) {
            loadSystemUpdateInfoLocked();
        }
    }

    public void updateSystemUpdateInfo(PersistableBundle persistableBundle) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.RECOVERY", "SystemUpdateManagerService");
        int i = persistableBundle.getInt("status", 0);
        if (i == 0) {
            Slog.w("SystemUpdateManagerService", "Invalid status info. Ignored");
            return;
        }
        int callingUid = Binder.getCallingUid();
        int i2 = this.mLastUid;
        if (i2 == -1 || i2 == callingUid || i != 1) {
            synchronized (this.mLock) {
                saveSystemUpdateInfoLocked(persistableBundle, callingUid);
            }
            return;
        }
        Slog.i("SystemUpdateManagerService", "Inactive updater reporting IDLE status. Ignored");
    }

    public Bundle retrieveSystemUpdateInfo() {
        Bundle loadSystemUpdateInfoLocked;
        if (this.mContext.checkCallingOrSelfPermission("android.permission.READ_SYSTEM_UPDATE_INFO") == -1 && this.mContext.checkCallingOrSelfPermission("android.permission.RECOVERY") == -1) {
            throw new SecurityException("Can't read system update info. Requiring READ_SYSTEM_UPDATE_INFO or RECOVERY permission.");
        }
        synchronized (this.mLock) {
            loadSystemUpdateInfoLocked = loadSystemUpdateInfoLocked();
        }
        return loadSystemUpdateInfoLocked;
    }

    public final Bundle loadSystemUpdateInfoLocked() {
        PersistableBundle persistableBundle = null;
        try {
            FileInputStream openRead = this.mFile.openRead();
            persistableBundle = readInfoFileLocked(Xml.resolvePullParser(openRead));
            if (openRead != null) {
                openRead.close();
            }
        } catch (FileNotFoundException unused) {
            Slog.i("SystemUpdateManagerService", "No existing info file " + this.mFile.getBaseFile());
        } catch (IOException e) {
            Slog.e("SystemUpdateManagerService", "Failed to read the info file:", e);
        } catch (XmlPullParserException e2) {
            Slog.e("SystemUpdateManagerService", "Failed to parse the info file:", e2);
        }
        if (persistableBundle == null) {
            return removeInfoFileAndGetDefaultInfoBundleLocked();
        }
        if (persistableBundle.getInt("version", -1) == -1) {
            Slog.w("SystemUpdateManagerService", "Invalid info file (invalid version). Ignored");
            return removeInfoFileAndGetDefaultInfoBundleLocked();
        }
        int i = persistableBundle.getInt("uid", -1);
        if (i == -1) {
            Slog.w("SystemUpdateManagerService", "Invalid info file (invalid UID). Ignored");
            return removeInfoFileAndGetDefaultInfoBundleLocked();
        }
        int i2 = persistableBundle.getInt("boot-count", -1);
        if (i2 == -1 || i2 != getBootCount()) {
            Slog.w("SystemUpdateManagerService", "Outdated info file. Ignored");
            return removeInfoFileAndGetDefaultInfoBundleLocked();
        }
        PersistableBundle persistableBundle2 = persistableBundle.getPersistableBundle("info-bundle");
        if (persistableBundle2 == null) {
            Slog.w("SystemUpdateManagerService", "Invalid info file (missing info). Ignored");
            return removeInfoFileAndGetDefaultInfoBundleLocked();
        }
        int i3 = persistableBundle2.getInt("status", 0);
        if (i3 == 0) {
            Slog.w("SystemUpdateManagerService", "Invalid info file (invalid status). Ignored");
            return removeInfoFileAndGetDefaultInfoBundleLocked();
        }
        this.mLastStatus = i3;
        this.mLastUid = i;
        return new Bundle(persistableBundle2);
    }

    public final void saveSystemUpdateInfoLocked(PersistableBundle persistableBundle, int i) {
        PersistableBundle persistableBundle2 = new PersistableBundle();
        persistableBundle2.putPersistableBundle("info-bundle", persistableBundle);
        persistableBundle2.putInt("version", 0);
        persistableBundle2.putInt("uid", i);
        persistableBundle2.putInt("boot-count", getBootCount());
        if (writeInfoFileLocked(persistableBundle2)) {
            this.mLastUid = i;
            this.mLastStatus = persistableBundle.getInt("status");
        }
    }

    public final PersistableBundle readInfoFileLocked(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return null;
            }
            if (next == 2 && "info".equals(typedXmlPullParser.getName())) {
                return PersistableBundle.restoreFromXml(typedXmlPullParser);
            }
        }
    }

    public final boolean writeInfoFileLocked(PersistableBundle persistableBundle) {
        FileOutputStream fileOutputStream = null;
        try {
            FileOutputStream startWrite = this.mFile.startWrite();
            try {
                TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
                resolveSerializer.startDocument((String) null, Boolean.TRUE);
                resolveSerializer.startTag((String) null, "info");
                persistableBundle.saveToXml(resolveSerializer);
                resolveSerializer.endTag((String) null, "info");
                resolveSerializer.endDocument();
                this.mFile.finishWrite(startWrite);
                return true;
            } catch (IOException | XmlPullParserException e) {
                e = e;
                fileOutputStream = startWrite;
                Slog.e("SystemUpdateManagerService", "Failed to save the info file:", e);
                if (fileOutputStream != null) {
                    this.mFile.failWrite(fileOutputStream);
                    return false;
                }
                return false;
            }
        } catch (IOException | XmlPullParserException e2) {
            e = e2;
        }
    }

    public final Bundle removeInfoFileAndGetDefaultInfoBundleLocked() {
        if (this.mFile.exists()) {
            Slog.i("SystemUpdateManagerService", "Removing info file");
            this.mFile.delete();
        }
        this.mLastStatus = 0;
        this.mLastUid = -1;
        Bundle bundle = new Bundle();
        bundle.putInt("status", 0);
        return bundle;
    }

    public final int getBootCount() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "boot_count", 0);
    }
}
