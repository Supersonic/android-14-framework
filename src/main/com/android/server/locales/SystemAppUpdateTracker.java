package com.android.server.locales;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.LocaleList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class SystemAppUpdateTracker {
    public final Context mContext;
    public final Object mFileLock;
    public final LocaleManagerService mLocaleManagerService;
    public final Set<String> mUpdatedApps;
    public final AtomicFile mUpdatedAppsFile;

    public SystemAppUpdateTracker(LocaleManagerService localeManagerService) {
        this(localeManagerService.mContext, localeManagerService, new AtomicFile(new File(Environment.getDataSystemDirectory(), "locale_manager_service_updated_system_apps.xml")));
    }

    @VisibleForTesting
    public SystemAppUpdateTracker(Context context, LocaleManagerService localeManagerService, AtomicFile atomicFile) {
        this.mFileLock = new Object();
        this.mUpdatedApps = new HashSet();
        this.mContext = context;
        this.mLocaleManagerService = localeManagerService;
        this.mUpdatedAppsFile = atomicFile;
    }

    public void init() {
        Slog.d("SystemAppUpdateTracker", "Loading the app info from storage. ");
        loadUpdatedSystemApps();
    }

    public final void loadUpdatedSystemApps() {
        if (!this.mUpdatedAppsFile.getBaseFile().exists()) {
            Slog.d("SystemAppUpdateTracker", "loadUpdatedSystemApps: File does not exist.");
            return;
        }
        FileInputStream fileInputStream = null;
        try {
            try {
                fileInputStream = this.mUpdatedAppsFile.openRead();
                readFromXml(fileInputStream);
            } catch (IOException | XmlPullParserException e) {
                Slog.e("SystemAppUpdateTracker", "loadUpdatedSystemApps: Could not parse storage file ", e);
            }
        } finally {
            IoUtils.closeQuietly(fileInputStream);
        }
    }

    public final void readFromXml(InputStream inputStream) throws XmlPullParserException, IOException {
        TypedXmlPullParser newFastPullParser = Xml.newFastPullParser();
        newFastPullParser.setInput(inputStream, StandardCharsets.UTF_8.name());
        XmlUtils.beginDocument(newFastPullParser, "system_apps");
        int depth = newFastPullParser.getDepth();
        while (XmlUtils.nextElementWithin(newFastPullParser, depth)) {
            if (newFastPullParser.getName().equals("package")) {
                String attributeValue = newFastPullParser.getAttributeValue((String) null, "name");
                if (!TextUtils.isEmpty(attributeValue)) {
                    this.mUpdatedApps.add(attributeValue);
                }
            }
        }
    }

    public void onPackageUpdateFinished(String str, int i) {
        try {
            if (this.mUpdatedApps.contains(str) || !isUpdatedSystemApp(str) || this.mLocaleManagerService.getInstallingPackageName(str) == null) {
                return;
            }
            try {
                int userId = UserHandle.getUserId(i);
                LocaleList applicationLocales = this.mLocaleManagerService.getApplicationLocales(str, userId);
                if (!applicationLocales.isEmpty()) {
                    this.mLocaleManagerService.notifyInstallerOfAppWhoseLocaleChanged(str, userId, applicationLocales);
                }
            } catch (RemoteException unused) {
                Slog.d("SystemAppUpdateTracker", "onPackageUpdateFinished: Error in fetching app locales");
            }
            updateBroadcastedAppsList(str);
        } catch (Exception e) {
            Slog.e("SystemAppUpdateTracker", "Exception in onPackageUpdateFinished.", e);
        }
    }

    public final void updateBroadcastedAppsList(String str) {
        synchronized (this.mFileLock) {
            this.mUpdatedApps.add(str);
            writeUpdatedAppsFileLocked();
        }
    }

    public final void writeUpdatedAppsFileLocked() {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = this.mUpdatedAppsFile.startWrite();
            writeToXmlLocked(fileOutputStream);
            this.mUpdatedAppsFile.finishWrite(fileOutputStream);
        } catch (IOException e) {
            this.mUpdatedAppsFile.failWrite(fileOutputStream);
            Slog.e("SystemAppUpdateTracker", "Failed to persist the updated apps list", e);
        }
    }

    public final void writeToXmlLocked(OutputStream outputStream) throws IOException {
        TypedXmlSerializer newFastSerializer = Xml.newFastSerializer();
        newFastSerializer.setOutput(outputStream, StandardCharsets.UTF_8.name());
        newFastSerializer.startDocument((String) null, Boolean.TRUE);
        newFastSerializer.startTag((String) null, "system_apps");
        for (String str : this.mUpdatedApps) {
            newFastSerializer.startTag((String) null, "package");
            newFastSerializer.attribute((String) null, "name", str);
            newFastSerializer.endTag((String) null, "package");
        }
        newFastSerializer.endTag((String) null, "system_apps");
        newFastSerializer.endDocument();
    }

    public final boolean isUpdatedSystemApp(String str) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = this.mContext.getPackageManager().getApplicationInfo(str, PackageManager.ApplicationInfoFlags.of(1048576L));
        } catch (PackageManager.NameNotFoundException unused) {
            Slog.d("SystemAppUpdateTracker", "isUpdatedSystemApp: Package not found " + str);
            applicationInfo = null;
        }
        return (applicationInfo == null || (applicationInfo.flags & 128) == 0) ? false : true;
    }

    @VisibleForTesting
    public Set<String> getUpdatedApps() {
        return this.mUpdatedApps;
    }
}
