package com.android.server.locales;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ILocaleManager;
import android.app.LocaleConfig;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.LocaleList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.content.PackageMonitor;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class LocaleManagerService extends SystemService {
    public ActivityManagerInternal mActivityManagerInternal;
    public ActivityTaskManagerInternal mActivityTaskManagerInternal;
    public LocaleManagerBackupHelper mBackupHelper;
    public final LocaleManagerBinderService mBinderService;
    public final Context mContext;
    public PackageManager mPackageManager;
    public final PackageMonitor mPackageMonitor;
    public final Object mWriteLock;

    public LocaleManagerService(Context context) {
        super(context);
        this.mWriteLock = new Object();
        this.mContext = context;
        this.mBinderService = new LocaleManagerBinderService();
        this.mActivityTaskManagerInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        this.mPackageManager = context.getPackageManager();
        HandlerThread handlerThread = new HandlerThread("LocaleManagerService", 10);
        handlerThread.start();
        final SystemAppUpdateTracker systemAppUpdateTracker = new SystemAppUpdateTracker(this);
        handlerThread.getThreadHandler().postAtFrontOfQueue(new Runnable() { // from class: com.android.server.locales.LocaleManagerService.1
            @Override // java.lang.Runnable
            public void run() {
                systemAppUpdateTracker.init();
            }
        });
        LocaleManagerBackupHelper localeManagerBackupHelper = new LocaleManagerBackupHelper(this, this.mPackageManager, handlerThread);
        this.mBackupHelper = localeManagerBackupHelper;
        LocaleManagerServicePackageMonitor localeManagerServicePackageMonitor = new LocaleManagerServicePackageMonitor(this.mBackupHelper, systemAppUpdateTracker, new AppUpdateTracker(context, this, localeManagerBackupHelper), this);
        this.mPackageMonitor = localeManagerServicePackageMonitor;
        localeManagerServicePackageMonitor.register(context, handlerThread.getLooper(), UserHandle.ALL, true);
    }

    @VisibleForTesting
    public LocaleManagerService(Context context, ActivityTaskManagerInternal activityTaskManagerInternal, ActivityManagerInternal activityManagerInternal, PackageManager packageManager, LocaleManagerBackupHelper localeManagerBackupHelper, PackageMonitor packageMonitor) {
        super(context);
        this.mWriteLock = new Object();
        this.mContext = context;
        this.mBinderService = new LocaleManagerBinderService();
        this.mActivityTaskManagerInternal = activityTaskManagerInternal;
        this.mActivityManagerInternal = activityManagerInternal;
        this.mPackageManager = packageManager;
        this.mBackupHelper = localeManagerBackupHelper;
        this.mPackageMonitor = packageMonitor;
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("locale", this.mBinderService);
        LocalServices.addService(LocaleManagerInternal.class, new LocaleManagerInternalImpl());
    }

    /* loaded from: classes.dex */
    public final class LocaleManagerInternalImpl extends LocaleManagerInternal {
        public LocaleManagerInternalImpl() {
        }

        @Override // com.android.server.locales.LocaleManagerInternal
        public byte[] getBackupPayload(int i) {
            checkCallerIsSystem();
            return LocaleManagerService.this.mBackupHelper.getBackupPayload(i);
        }

        @Override // com.android.server.locales.LocaleManagerInternal
        public void stageAndApplyRestoredPayload(byte[] bArr, int i) {
            LocaleManagerService.this.mBackupHelper.stageAndApplyRestoredPayload(bArr, i);
        }

        public final void checkCallerIsSystem() {
            if (Binder.getCallingUid() != 1000) {
                throw new SecurityException("Caller is not system.");
            }
        }
    }

    /* loaded from: classes.dex */
    public final class LocaleManagerBinderService extends ILocaleManager.Stub {
        public LocaleManagerBinderService() {
        }

        public void setApplicationLocales(String str, int i, LocaleList localeList, boolean z) throws RemoteException {
            LocaleManagerService.this.setApplicationLocales(str, i, localeList, z);
        }

        public LocaleList getApplicationLocales(String str, int i) throws RemoteException {
            return LocaleManagerService.this.getApplicationLocales(str, i);
        }

        public LocaleList getSystemLocales() throws RemoteException {
            return LocaleManagerService.this.getSystemLocales();
        }

        public void setOverrideLocaleConfig(String str, int i, LocaleConfig localeConfig) throws RemoteException {
            LocaleManagerService.this.setOverrideLocaleConfig(str, i, localeConfig);
        }

        public LocaleConfig getOverrideLocaleConfig(String str, int i) {
            return LocaleManagerService.this.getOverrideLocaleConfig(str, i);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
            new LocaleManagerShellCommand(LocaleManagerService.this.mBinderService).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }
    }

    public void setApplicationLocales(String str, int i, LocaleList localeList, boolean z) throws RemoteException, IllegalArgumentException {
        AppLocaleChangedAtomRecord appLocaleChangedAtomRecord = new AppLocaleChangedAtomRecord(Binder.getCallingUid());
        try {
            Objects.requireNonNull(str);
            Objects.requireNonNull(localeList);
            appLocaleChangedAtomRecord.setNewLocales(localeList.toLanguageTags());
            int handleIncomingUser = this.mActivityManagerInternal.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, 0, "setApplicationLocales", (String) null);
            if (!isPackageOwnedByCaller(str, handleIncomingUser, appLocaleChangedAtomRecord, null)) {
                enforceChangeConfigurationPermission(appLocaleChangedAtomRecord);
            }
            this.mBackupHelper.persistLocalesModificationInfo(handleIncomingUser, str, z, localeList.isEmpty());
            long clearCallingIdentity = Binder.clearCallingIdentity();
            setApplicationLocalesUnchecked(str, handleIncomingUser, localeList, appLocaleChangedAtomRecord);
            Binder.restoreCallingIdentity(clearCallingIdentity);
        } finally {
            logAppLocalesMetric(appLocaleChangedAtomRecord);
        }
    }

    public final void setApplicationLocalesUnchecked(String str, int i, LocaleList localeList, AppLocaleChangedAtomRecord appLocaleChangedAtomRecord) {
        Slog.d("LocaleManagerService", "setApplicationLocales: setting locales for package " + str + " and user " + i);
        appLocaleChangedAtomRecord.setPrevLocales(getApplicationLocalesUnchecked(str, i).toLanguageTags());
        if (this.mActivityTaskManagerInternal.createPackageConfigurationUpdater(str, i).setLocales(localeList).commit()) {
            notifyAppWhoseLocaleChanged(str, i, localeList);
            notifyInstallerOfAppWhoseLocaleChanged(str, i, localeList);
            notifyRegisteredReceivers(str, i, localeList);
            this.mBackupHelper.notifyBackupManager();
            appLocaleChangedAtomRecord.setStatus(1);
            return;
        }
        appLocaleChangedAtomRecord.setStatus(2);
    }

    public final void notifyRegisteredReceivers(String str, int i, LocaleList localeList) {
        this.mContext.sendBroadcastAsUser(createBaseIntent("android.intent.action.APPLICATION_LOCALE_CHANGED", str, localeList), UserHandle.of(i), "android.permission.READ_APP_SPECIFIC_LOCALES");
    }

    public void notifyInstallerOfAppWhoseLocaleChanged(String str, int i, LocaleList localeList) {
        String installingPackageName = getInstallingPackageName(str);
        if (installingPackageName != null) {
            Intent createBaseIntent = createBaseIntent("android.intent.action.APPLICATION_LOCALE_CHANGED", str, localeList);
            createBaseIntent.setPackage(installingPackageName);
            this.mContext.sendBroadcastAsUser(createBaseIntent, UserHandle.of(i));
        }
    }

    public final void notifyAppWhoseLocaleChanged(String str, int i, LocaleList localeList) {
        Intent createBaseIntent = createBaseIntent("android.intent.action.LOCALE_CHANGED", str, localeList);
        createBaseIntent.setPackage(str);
        createBaseIntent.addFlags(2097152);
        this.mContext.sendBroadcastAsUser(createBaseIntent, UserHandle.of(i));
    }

    public static Intent createBaseIntent(String str, String str2, LocaleList localeList) {
        return new Intent(str).putExtra("android.intent.extra.PACKAGE_NAME", str2).putExtra("android.intent.extra.LOCALE_LIST", localeList).addFlags(285212672);
    }

    public final boolean isPackageOwnedByCaller(String str, int i, AppLocaleChangedAtomRecord appLocaleChangedAtomRecord, AppSupportedLocalesChangedAtomRecord appSupportedLocalesChangedAtomRecord) {
        int packageUid = getPackageUid(str, i);
        if (packageUid < 0) {
            Slog.w("LocaleManagerService", "Unknown package " + str + " for user " + i);
            if (appLocaleChangedAtomRecord != null) {
                appLocaleChangedAtomRecord.setStatus(3);
            } else if (appSupportedLocalesChangedAtomRecord != null) {
                appSupportedLocalesChangedAtomRecord.setStatus(3);
            }
            throw new IllegalArgumentException("Unknown package: " + str + " for user " + i);
        }
        if (appLocaleChangedAtomRecord != null) {
            appLocaleChangedAtomRecord.setTargetUid(packageUid);
        } else if (appSupportedLocalesChangedAtomRecord != null) {
            appSupportedLocalesChangedAtomRecord.setTargetUid(packageUid);
        }
        return UserHandle.isSameApp(Binder.getCallingUid(), packageUid);
    }

    public final void enforceChangeConfigurationPermission(AppLocaleChangedAtomRecord appLocaleChangedAtomRecord) {
        try {
            this.mContext.enforceCallingOrSelfPermission("android.permission.CHANGE_CONFIGURATION", "setApplicationLocales");
        } catch (SecurityException e) {
            appLocaleChangedAtomRecord.setStatus(4);
            throw e;
        }
    }

    public LocaleList getApplicationLocales(String str, int i) throws RemoteException, IllegalArgumentException {
        Objects.requireNonNull(str);
        int handleIncomingUser = this.mActivityManagerInternal.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, 0, "getApplicationLocales", (String) null);
        if (!isPackageOwnedByCaller(str, handleIncomingUser, null, null) && !isCallerInstaller(str, handleIncomingUser) && (!isCallerFromCurrentInputMethod(handleIncomingUser) || !this.mActivityManagerInternal.isAppForeground(getPackageUid(str, handleIncomingUser)))) {
            enforceReadAppSpecificLocalesPermission();
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return getApplicationLocalesUnchecked(str, handleIncomingUser);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final LocaleList getApplicationLocalesUnchecked(String str, int i) {
        Slog.d("LocaleManagerService", "getApplicationLocales: fetching locales for package " + str + " and user " + i);
        ActivityTaskManagerInternal.PackageConfig applicationConfig = this.mActivityTaskManagerInternal.getApplicationConfig(str, i);
        if (applicationConfig == null) {
            Slog.d("LocaleManagerService", "getApplicationLocales: application config not found for " + str + " and user id " + i);
            return LocaleList.getEmptyLocaleList();
        }
        LocaleList localeList = applicationConfig.mLocales;
        return localeList != null ? localeList : LocaleList.getEmptyLocaleList();
    }

    public final boolean isCallerInstaller(String str, int i) {
        int packageUid;
        String installingPackageName = getInstallingPackageName(str);
        return installingPackageName != null && (packageUid = getPackageUid(installingPackageName, i)) >= 0 && UserHandle.isSameApp(Binder.getCallingUid(), packageUid);
    }

    public final boolean isCallerFromCurrentInputMethod(int i) {
        if (SystemProperties.getBoolean("i18n.feature.allow_ime_query_app_locale", true)) {
            String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "default_input_method", i);
            if (TextUtils.isEmpty(stringForUser)) {
                return false;
            }
            int packageUid = getPackageUid(ComponentName.unflattenFromString(stringForUser).getPackageName(), i);
            return packageUid >= 0 && UserHandle.isSameApp(Binder.getCallingUid(), packageUid);
        }
        return false;
    }

    public final void enforceReadAppSpecificLocalesPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_APP_SPECIFIC_LOCALES", "getApplicationLocales");
    }

    public final int getPackageUid(String str, int i) {
        try {
            return this.mPackageManager.getPackageUidAsUser(str, PackageManager.PackageInfoFlags.of(0L), i);
        } catch (PackageManager.NameNotFoundException unused) {
            return -1;
        }
    }

    public String getInstallingPackageName(String str) {
        try {
            return this.mContext.getPackageManager().getInstallSourceInfo(str).getInstallingPackageName();
        } catch (PackageManager.NameNotFoundException unused) {
            Slog.w("LocaleManagerService", "Package not found " + str);
            return null;
        }
    }

    public LocaleList getSystemLocales() throws RemoteException {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return getSystemLocalesUnchecked();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final LocaleList getSystemLocalesUnchecked() throws RemoteException {
        Configuration configuration = ActivityManager.getService().getConfiguration();
        LocaleList locales = configuration != null ? configuration.getLocales() : null;
        return locales == null ? LocaleList.getEmptyLocaleList() : locales;
    }

    public final void logAppLocalesMetric(AppLocaleChangedAtomRecord appLocaleChangedAtomRecord) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.APPLICATION_LOCALES_CHANGED, appLocaleChangedAtomRecord.mCallingUid, appLocaleChangedAtomRecord.mTargetUid, appLocaleChangedAtomRecord.mNewLocales, appLocaleChangedAtomRecord.mPrevLocales, appLocaleChangedAtomRecord.mStatus);
    }

    public void setOverrideLocaleConfig(String str, int i, LocaleConfig localeConfig) throws IllegalArgumentException {
        if (SystemProperties.getBoolean("i18n.feature.dynamic_locales_change", true)) {
            AppSupportedLocalesChangedAtomRecord appSupportedLocalesChangedAtomRecord = new AppSupportedLocalesChangedAtomRecord(Binder.getCallingUid());
            try {
                Objects.requireNonNull(str);
                int handleIncomingUser = this.mActivityManagerInternal.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, 0, "setOverrideLocaleConfig", (String) null);
                if (!isPackageOwnedByCaller(str, handleIncomingUser, null, appSupportedLocalesChangedAtomRecord)) {
                    enforceSetAppSpecificLocaleConfigPermission(appSupportedLocalesChangedAtomRecord);
                }
                long clearCallingIdentity = Binder.clearCallingIdentity();
                setOverrideLocaleConfigUnchecked(str, handleIncomingUser, localeConfig, appSupportedLocalesChangedAtomRecord);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } finally {
                logAppSupportedLocalesChangedMetric(appSupportedLocalesChangedAtomRecord);
            }
        }
    }

    public final void setOverrideLocaleConfigUnchecked(String str, int i, LocaleConfig localeConfig, AppSupportedLocalesChangedAtomRecord appSupportedLocalesChangedAtomRecord) {
        FileOutputStream fileOutputStream;
        synchronized (this.mWriteLock) {
            Slog.d("LocaleManagerService", "set the override LocaleConfig for package " + str + " and user " + i);
            File xmlFileNameForUser = getXmlFileNameForUser(str, i);
            if (localeConfig == null) {
                if (xmlFileNameForUser.exists()) {
                    Slog.d("LocaleManagerService", "remove the override LocaleConfig");
                    xmlFileNameForUser.delete();
                }
                appSupportedLocalesChangedAtomRecord.setOverrideRemoved(true);
                appSupportedLocalesChangedAtomRecord.setStatus(1);
            } else if (localeConfig.isSameLocaleConfig(getOverrideLocaleConfig(str, i))) {
                Slog.d("LocaleManagerService", "the same override, ignore it");
                appSupportedLocalesChangedAtomRecord.setSameAsPrevConfig(true);
            } else {
                LocaleList supportedLocales = localeConfig.getSupportedLocales();
                if (supportedLocales == null) {
                    supportedLocales = LocaleList.getEmptyLocaleList();
                }
                Slog.d("LocaleManagerService", "setOverrideLocaleConfig, localeList: " + supportedLocales.toLanguageTags());
                appSupportedLocalesChangedAtomRecord.setNumLocales(supportedLocales.size());
                AtomicFile atomicFile = new AtomicFile(xmlFileNameForUser);
                try {
                    fileOutputStream = atomicFile.startWrite();
                } catch (Exception e) {
                    e = e;
                    fileOutputStream = null;
                }
                try {
                    fileOutputStream.write(toXmlByteArray(supportedLocales));
                    atomicFile.finishWrite(fileOutputStream);
                    removeUnsupportedAppLocales(str, i, localeConfig);
                    try {
                        if (localeConfig.isSameLocaleConfig(LocaleConfig.fromContextIgnoringOverride(this.mContext.createPackageContext(str, 0)))) {
                            Slog.d("LocaleManagerService", "setOverrideLocaleConfig, same as the app's LocaleConfig");
                            appSupportedLocalesChangedAtomRecord.setSameAsResConfig(true);
                        }
                    } catch (PackageManager.NameNotFoundException unused) {
                        Slog.e("LocaleManagerService", "Unknown package name " + str);
                    }
                    appSupportedLocalesChangedAtomRecord.setStatus(1);
                    Slog.i("LocaleManagerService", "Successfully written to " + atomicFile);
                } catch (Exception e2) {
                    e = e2;
                    Slog.e("LocaleManagerService", "Failed to write file " + atomicFile, e);
                    if (fileOutputStream != null) {
                        atomicFile.failWrite(fileOutputStream);
                    }
                    appSupportedLocalesChangedAtomRecord.setStatus(2);
                }
            }
        }
    }

    public final void removeUnsupportedAppLocales(String str, int i, LocaleConfig localeConfig) {
        LocaleList applicationLocalesUnchecked = getApplicationLocalesUnchecked(str, i);
        ArrayList arrayList = new ArrayList();
        boolean z = false;
        for (int i2 = 0; i2 < applicationLocalesUnchecked.size(); i2++) {
            if (!localeConfig.containsLocale(applicationLocalesUnchecked.get(i2))) {
                Slog.i("LocaleManagerService", "reset the app locales");
                z = true;
            } else {
                arrayList.add(applicationLocalesUnchecked.get(i2));
            }
        }
        if (z) {
            try {
                setApplicationLocales(str, i, new LocaleList((Locale[]) arrayList.toArray(new Locale[arrayList.size()])), false);
            } catch (RemoteException | IllegalArgumentException e) {
                Slog.e("LocaleManagerService", "Could not set locales for " + str, e);
            }
        }
    }

    public final void enforceSetAppSpecificLocaleConfigPermission(AppSupportedLocalesChangedAtomRecord appSupportedLocalesChangedAtomRecord) {
        try {
            this.mContext.enforceCallingOrSelfPermission("android.permission.SET_APP_SPECIFIC_LOCALECONFIG", "setOverrideLocaleConfig");
        } catch (SecurityException e) {
            appSupportedLocalesChangedAtomRecord.setStatus(4);
            throw e;
        }
    }

    public LocaleConfig getOverrideLocaleConfig(String str, int i) {
        if (SystemProperties.getBoolean("i18n.feature.dynamic_locales_change", true)) {
            Objects.requireNonNull(str);
            File xmlFileNameForUser = getXmlFileNameForUser(str, this.mActivityManagerInternal.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, 0, "getOverrideLocaleConfig", (String) null));
            if (!xmlFileNameForUser.exists()) {
                Slog.i("LocaleManagerService", "getOverrideLocaleConfig, the file is not existed.");
                return null;
            }
            try {
                FileInputStream fileInputStream = new FileInputStream(xmlFileNameForUser);
                List<String> loadFromXml = loadFromXml(Xml.resolvePullParser(fileInputStream));
                Slog.i("LocaleManagerService", "getOverrideLocaleConfig, Loaded locales: " + loadFromXml);
                LocaleConfig localeConfig = new LocaleConfig(LocaleList.forLanguageTags(String.join(",", loadFromXml)));
                fileInputStream.close();
                return localeConfig;
            } catch (IOException | XmlPullParserException e) {
                Slog.e("LocaleManagerService", "Failed to parse XML configuration from " + xmlFileNameForUser, e);
                return null;
            }
        }
        return null;
    }

    public void deleteOverrideLocaleConfig(String str, int i) {
        File xmlFileNameForUser = getXmlFileNameForUser(str, i);
        if (xmlFileNameForUser.exists()) {
            Slog.d("LocaleManagerService", "Delete the override LocaleConfig.");
            xmlFileNameForUser.delete();
        }
    }

    public final byte[] toXmlByteArray(LocaleList localeList) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            TypedXmlSerializer newFastSerializer = Xml.newFastSerializer();
            newFastSerializer.setOutput(byteArrayOutputStream, StandardCharsets.UTF_8.name());
            newFastSerializer.startDocument((String) null, Boolean.TRUE);
            newFastSerializer.startTag((String) null, "locale-config");
            for (String str : new ArrayList(Arrays.asList(localeList.toLanguageTags().split(",")))) {
                newFastSerializer.startTag((String) null, "locale");
                newFastSerializer.attribute((String) null, "name", str);
                newFastSerializer.endTag((String) null, "locale");
            }
            newFastSerializer.endTag((String) null, "locale-config");
            newFastSerializer.endDocument();
            Slog.d("LocaleManagerService", "setOverrideLocaleConfig toXmlByteArray, output: " + byteArrayOutputStream.toString());
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            return byteArray;
        } catch (IOException unused) {
            return null;
        }
    }

    public final List<String> loadFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        ArrayList arrayList = new ArrayList();
        XmlUtils.beginDocument(typedXmlPullParser, "locale-config");
        int depth = typedXmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(typedXmlPullParser, depth)) {
            String name = typedXmlPullParser.getName();
            if ("locale".equals(name)) {
                arrayList.add(typedXmlPullParser.getAttributeValue((String) null, "name"));
            } else {
                Slog.w("LocaleManagerService", "Unexpected tag name: " + name);
                XmlUtils.skipCurrentTag(typedXmlPullParser);
            }
        }
        return arrayList;
    }

    public final File getXmlFileNameForUser(String str, int i) {
        File file = new File(Environment.getDataSystemDeDirectory(i), "locale_configs");
        return new File(file, str + ".xml");
    }

    public final void logAppSupportedLocalesChangedMetric(AppSupportedLocalesChangedAtomRecord appSupportedLocalesChangedAtomRecord) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.APP_SUPPORTED_LOCALES_CHANGED, appSupportedLocalesChangedAtomRecord.mCallingUid, appSupportedLocalesChangedAtomRecord.mTargetUid, appSupportedLocalesChangedAtomRecord.mNumLocales, appSupportedLocalesChangedAtomRecord.mOverrideRemoved, appSupportedLocalesChangedAtomRecord.mSameAsResConfig, appSupportedLocalesChangedAtomRecord.mSameAsPrevConfig, appSupportedLocalesChangedAtomRecord.mStatus);
    }
}
