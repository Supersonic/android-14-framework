package com.android.server.p014wm;

import android.content.res.Configuration;
import android.os.Environment;
import android.os.LocaleList;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.p014wm.PackageConfigPersister;
import com.android.server.p014wm.PersisterQueue;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.wm.PackageConfigPersister */
/* loaded from: classes2.dex */
public class PackageConfigPersister {
    public static final String TAG = "PackageConfigPersister";
    public final ActivityTaskManagerService mAtm;
    public final PersisterQueue mPersisterQueue;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final SparseArray<HashMap<String, PackageConfigRecord>> mPendingWrite = new SparseArray<>();
    @GuardedBy({"mLock"})
    public final SparseArray<HashMap<String, PackageConfigRecord>> mModified = new SparseArray<>();

    public static File getUserConfigsDir(int i) {
        return new File(Environment.getDataSystemCeDirectory(i), "package_configs");
    }

    public PackageConfigPersister(PersisterQueue persisterQueue, ActivityTaskManagerService activityTaskManagerService) {
        this.mPersisterQueue = persisterQueue;
        this.mAtm = activityTaskManagerService;
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x00a7  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00bc  */
    @GuardedBy({"mLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void loadUserPackages(int i) {
        FileInputStream fileInputStream;
        char c;
        synchronized (this.mLock) {
            File userConfigsDir = getUserConfigsDir(i);
            File[] listFiles = userConfigsDir.listFiles();
            if (listFiles == null) {
                Slog.v(TAG, "loadPackages: empty list files from " + userConfigsDir);
                return;
            }
            for (File file : listFiles) {
                if (file.getName().endsWith("_config.xml")) {
                    try {
                        fileInputStream = new FileInputStream(file);
                        try {
                            TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(fileInputStream);
                            String str = null;
                            Integer num = null;
                            LocaleList localeList = null;
                            while (true) {
                                int next = resolvePullParser.next();
                                if (next == 1 || next == 3) {
                                    break;
                                }
                                String name = resolvePullParser.getName();
                                if (next == 2 && "config".equals(name)) {
                                    for (int attributeCount = resolvePullParser.getAttributeCount() - 1; attributeCount >= 0; attributeCount--) {
                                        String attributeName = resolvePullParser.getAttributeName(attributeCount);
                                        String attributeValue = resolvePullParser.getAttributeValue(attributeCount);
                                        int hashCode = attributeName.hashCode();
                                        if (hashCode == -1877165340) {
                                            if (attributeName.equals("package_name")) {
                                                c = 0;
                                                if (c != 0) {
                                                }
                                            }
                                            c = 65535;
                                            if (c != 0) {
                                            }
                                        } else if (hashCode != -601793174) {
                                            if (hashCode == 1912882019 && attributeName.equals("locale_list")) {
                                                c = 2;
                                                if (c != 0) {
                                                    str = attributeValue;
                                                } else if (c == 1) {
                                                    num = Integer.valueOf(Integer.parseInt(attributeValue));
                                                } else if (c == 2) {
                                                    localeList = LocaleList.forLanguageTags(attributeValue);
                                                }
                                            }
                                            c = 65535;
                                            if (c != 0) {
                                            }
                                        } else {
                                            if (attributeName.equals("night_mode")) {
                                                c = 1;
                                                if (c != 0) {
                                                }
                                            }
                                            c = 65535;
                                            if (c != 0) {
                                            }
                                        }
                                    }
                                }
                                XmlUtils.skipCurrentTag(resolvePullParser);
                            }
                            if (str != null) {
                                try {
                                    PackageConfigRecord findRecordOrCreate = findRecordOrCreate(this.mModified, str, i);
                                    findRecordOrCreate.mNightMode = num;
                                    findRecordOrCreate.mLocales = localeList;
                                } catch (Throwable th) {
                                    th = th;
                                    Throwable th2 = th;
                                    try {
                                        fileInputStream.close();
                                    } catch (Throwable th3) {
                                        th2.addSuppressed(th3);
                                    }
                                    throw th2;
                                    break;
                                }
                            }
                        } catch (Throwable th4) {
                            th = th4;
                        }
                    } catch (FileNotFoundException e) {
                        e = e;
                    } catch (IOException e2) {
                        e = e2;
                    } catch (XmlPullParserException e3) {
                        e = e3;
                    }
                    try {
                        fileInputStream.close();
                    } catch (FileNotFoundException e4) {
                        e = e4;
                        e.printStackTrace();
                    } catch (IOException e5) {
                        e = e5;
                        e.printStackTrace();
                    } catch (XmlPullParserException e6) {
                        e = e6;
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public void updateConfigIfNeeded(ConfigurationContainer configurationContainer, int i, String str) {
        synchronized (this.mLock) {
            PackageConfigRecord findRecord = findRecord(this.mModified, str, i);
            if (findRecord != null) {
                configurationContainer.applyAppSpecificConfig(findRecord.mNightMode, LocaleOverlayHelper.combineLocalesIfOverlayExists(findRecord.mLocales, this.mAtm.getGlobalConfiguration().getLocales()), findRecord.mGrammaticalGender);
            }
        }
    }

    @GuardedBy({"mLock"})
    public boolean updateFromImpl(String str, int i, PackageConfigurationUpdaterImpl packageConfigurationUpdaterImpl) {
        boolean z;
        LocaleList localeList;
        Integer num;
        synchronized (this.mLock) {
            PackageConfigRecord findRecord = findRecord(this.mModified, str, i);
            if (findRecord != null) {
                z = true;
            } else {
                findRecord = findRecordOrCreate(this.mModified, str, i);
                z = false;
            }
            boolean updateNightMode = updateNightMode(packageConfigurationUpdaterImpl.getNightMode(), findRecord);
            boolean updateLocales = updateLocales(packageConfigurationUpdaterImpl.getLocales(), findRecord);
            boolean updateGender = updateGender(packageConfigurationUpdaterImpl.getGrammaticalGender(), findRecord);
            if ((findRecord.mNightMode != null && !findRecord.isResetNightMode()) || (((localeList = findRecord.mLocales) != null && !localeList.isEmpty()) || ((num = findRecord.mGrammaticalGender) != null && num.intValue() != 0))) {
                if (updateNightMode || updateLocales || updateGender) {
                    PackageConfigRecord findRecord2 = findRecord(this.mPendingWrite, findRecord.mName, findRecord.mUserId);
                    if (findRecord2 == null) {
                        findRecord2 = findRecordOrCreate(this.mPendingWrite, findRecord.mName, findRecord.mUserId);
                    }
                    if (updateNightMode(findRecord.mNightMode, findRecord2) || updateLocales(findRecord.mLocales, findRecord2) || updateGender(findRecord.mGrammaticalGender, findRecord2)) {
                        this.mPersisterQueue.addItem(new WriteProcessItem(findRecord2), false);
                        return true;
                    }
                    return false;
                }
                return false;
            }
            removePackage(str, i);
            return z;
        }
    }

    public final boolean updateNightMode(Integer num, PackageConfigRecord packageConfigRecord) {
        if (num == null || num.equals(packageConfigRecord.mNightMode)) {
            return false;
        }
        packageConfigRecord.mNightMode = num;
        return true;
    }

    public final boolean updateLocales(LocaleList localeList, PackageConfigRecord packageConfigRecord) {
        if (localeList == null || localeList.equals(packageConfigRecord.mLocales)) {
            return false;
        }
        packageConfigRecord.mLocales = localeList;
        return true;
    }

    public final boolean updateGender(@Configuration.GrammaticalGender Integer num, PackageConfigRecord packageConfigRecord) {
        if (num == null || num.equals(packageConfigRecord.mGrammaticalGender)) {
            return false;
        }
        packageConfigRecord.mGrammaticalGender = num;
        return true;
    }

    @GuardedBy({"mLock"})
    public void removeUser(int i) {
        synchronized (this.mLock) {
            HashMap<String, PackageConfigRecord> hashMap = this.mModified.get(i);
            HashMap<String, PackageConfigRecord> hashMap2 = this.mPendingWrite.get(i);
            if ((hashMap != null && hashMap.size() != 0) || (hashMap2 != null && hashMap2.size() != 0)) {
                new HashMap(hashMap).forEach(new BiConsumer() { // from class: com.android.server.wm.PackageConfigPersister$$ExternalSyntheticLambda1
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        PackageConfigPersister.this.lambda$removeUser$0((String) obj, (PackageConfigPersister.PackageConfigRecord) obj2);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeUser$0(String str, PackageConfigRecord packageConfigRecord) {
        removePackage(packageConfigRecord.mName, packageConfigRecord.mUserId);
    }

    @GuardedBy({"mLock"})
    public void onPackageUninstall(String str, int i) {
        synchronized (this.mLock) {
            removePackage(str, i);
        }
    }

    @GuardedBy({"mLock"})
    public void onPackageDataCleared(String str, int i) {
        synchronized (this.mLock) {
            removePackage(str, i);
        }
    }

    public final void removePackage(String str, int i) {
        final PackageConfigRecord findRecord = findRecord(this.mPendingWrite, str, i);
        if (findRecord != null) {
            removeRecord(this.mPendingWrite, findRecord);
            this.mPersisterQueue.removeItems(new Predicate() { // from class: com.android.server.wm.PackageConfigPersister$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$removePackage$1;
                    lambda$removePackage$1 = PackageConfigPersister.lambda$removePackage$1(PackageConfigPersister.PackageConfigRecord.this, (PackageConfigPersister.WriteProcessItem) obj);
                    return lambda$removePackage$1;
                }
            }, WriteProcessItem.class);
        }
        PackageConfigRecord findRecord2 = findRecord(this.mModified, str, i);
        if (findRecord2 != null) {
            removeRecord(this.mModified, findRecord2);
            this.mPersisterQueue.addItem(new DeletePackageItem(i, str), false);
        }
    }

    public static /* synthetic */ boolean lambda$removePackage$1(PackageConfigRecord packageConfigRecord, WriteProcessItem writeProcessItem) {
        PackageConfigRecord packageConfigRecord2 = writeProcessItem.mRecord;
        return packageConfigRecord2.mName == packageConfigRecord.mName && packageConfigRecord2.mUserId == packageConfigRecord.mUserId;
    }

    public ActivityTaskManagerInternal.PackageConfig findPackageConfiguration(String str, int i) {
        synchronized (this.mLock) {
            PackageConfigRecord findRecord = findRecord(this.mModified, str, i);
            if (findRecord == null) {
                String str2 = TAG;
                Slog.w(str2, "App-specific configuration not found for packageName: " + str + " and userId: " + i);
                return null;
            }
            return new ActivityTaskManagerInternal.PackageConfig(findRecord.mNightMode, findRecord.mLocales, findRecord.mGrammaticalGender);
        }
    }

    public void dump(PrintWriter printWriter, int i) {
        printWriter.println("INSTALLED PACKAGES HAVING APP-SPECIFIC CONFIGURATIONS");
        printWriter.println("Current user ID : " + i);
        synchronized (this.mLock) {
            HashMap<String, PackageConfigRecord> hashMap = this.mModified.get(i);
            if (hashMap != null) {
                for (PackageConfigRecord packageConfigRecord : hashMap.values()) {
                    printWriter.println();
                    printWriter.println("    PackageName : " + packageConfigRecord.mName);
                    printWriter.println("        NightMode : " + packageConfigRecord.mNightMode);
                    printWriter.println("        Locales : " + packageConfigRecord.mLocales);
                }
            }
        }
    }

    /* renamed from: com.android.server.wm.PackageConfigPersister$PackageConfigRecord */
    /* loaded from: classes2.dex */
    public static class PackageConfigRecord {
        @Configuration.GrammaticalGender
        public Integer mGrammaticalGender;
        public LocaleList mLocales;
        public final String mName;
        public Integer mNightMode;
        public final int mUserId;

        public PackageConfigRecord(String str, int i) {
            this.mName = str;
            this.mUserId = i;
        }

        public boolean isResetNightMode() {
            return this.mNightMode.intValue() == 0;
        }

        public String toString() {
            return "PackageConfigRecord package name: " + this.mName + " userId " + this.mUserId + " nightMode " + this.mNightMode + " locales " + this.mLocales;
        }
    }

    public final PackageConfigRecord findRecordOrCreate(SparseArray<HashMap<String, PackageConfigRecord>> sparseArray, String str, int i) {
        HashMap<String, PackageConfigRecord> hashMap = sparseArray.get(i);
        if (hashMap == null) {
            hashMap = new HashMap<>();
            sparseArray.put(i, hashMap);
        }
        PackageConfigRecord packageConfigRecord = hashMap.get(str);
        if (packageConfigRecord != null) {
            return packageConfigRecord;
        }
        PackageConfigRecord packageConfigRecord2 = new PackageConfigRecord(str, i);
        hashMap.put(str, packageConfigRecord2);
        return packageConfigRecord2;
    }

    public final PackageConfigRecord findRecord(SparseArray<HashMap<String, PackageConfigRecord>> sparseArray, String str, int i) {
        HashMap<String, PackageConfigRecord> hashMap = sparseArray.get(i);
        if (hashMap == null) {
            return null;
        }
        return hashMap.get(str);
    }

    public final void removeRecord(SparseArray<HashMap<String, PackageConfigRecord>> sparseArray, PackageConfigRecord packageConfigRecord) {
        HashMap<String, PackageConfigRecord> hashMap = sparseArray.get(packageConfigRecord.mUserId);
        if (hashMap != null) {
            hashMap.remove(packageConfigRecord.mName);
        }
    }

    /* renamed from: com.android.server.wm.PackageConfigPersister$DeletePackageItem */
    /* loaded from: classes2.dex */
    public static class DeletePackageItem implements PersisterQueue.WriteQueueItem {
        public final String mPackageName;
        public final int mUserId;

        public DeletePackageItem(int i, String str) {
            this.mUserId = i;
            this.mPackageName = str;
        }

        @Override // com.android.server.p014wm.PersisterQueue.WriteQueueItem
        public void process() {
            File userConfigsDir = PackageConfigPersister.getUserConfigsDir(this.mUserId);
            if (userConfigsDir.isDirectory()) {
                AtomicFile atomicFile = new AtomicFile(new File(userConfigsDir, this.mPackageName + "_config.xml"));
                if (atomicFile.exists()) {
                    atomicFile.delete();
                }
            }
        }
    }

    /* renamed from: com.android.server.wm.PackageConfigPersister$WriteProcessItem */
    /* loaded from: classes2.dex */
    public class WriteProcessItem implements PersisterQueue.WriteQueueItem {
        public final PackageConfigRecord mRecord;

        public WriteProcessItem(PackageConfigRecord packageConfigRecord) {
            this.mRecord = packageConfigRecord;
        }

        @Override // com.android.server.p014wm.PersisterQueue.WriteQueueItem
        public void process() {
            FileOutputStream fileOutputStream;
            byte[] bArr;
            AtomicFile atomicFile;
            synchronized (PackageConfigPersister.this.mLock) {
                fileOutputStream = null;
                try {
                    bArr = saveToXml();
                } catch (Exception unused) {
                    bArr = null;
                }
                PackageConfigPersister packageConfigPersister = PackageConfigPersister.this;
                packageConfigPersister.removeRecord(packageConfigPersister.mPendingWrite, this.mRecord);
            }
            if (bArr == null) {
                return;
            }
            try {
                File userConfigsDir = PackageConfigPersister.getUserConfigsDir(this.mRecord.mUserId);
                if (!userConfigsDir.isDirectory() && !userConfigsDir.mkdirs()) {
                    Slog.e(PackageConfigPersister.TAG, "Failure creating tasks directory for user " + this.mRecord.mUserId + ": " + userConfigsDir);
                    return;
                }
                atomicFile = new AtomicFile(new File(userConfigsDir, this.mRecord.mName + "_config.xml"));
                try {
                    fileOutputStream = atomicFile.startWrite();
                    fileOutputStream.write(bArr);
                    atomicFile.finishWrite(fileOutputStream);
                } catch (IOException e) {
                    e = e;
                    if (fileOutputStream != null) {
                        atomicFile.failWrite(fileOutputStream);
                    }
                    Slog.e(PackageConfigPersister.TAG, "Unable to open " + atomicFile + " for persisting. " + e);
                }
            } catch (IOException e2) {
                e = e2;
                atomicFile = null;
            }
        }

        public final byte[] saveToXml() throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(byteArrayOutputStream);
            resolveSerializer.startDocument((String) null, Boolean.TRUE);
            resolveSerializer.startTag((String) null, "config");
            resolveSerializer.attribute((String) null, "package_name", this.mRecord.mName);
            Integer num = this.mRecord.mNightMode;
            if (num != null) {
                resolveSerializer.attributeInt((String) null, "night_mode", num.intValue());
            }
            LocaleList localeList = this.mRecord.mLocales;
            if (localeList != null) {
                resolveSerializer.attribute((String) null, "locale_list", localeList.toLanguageTags());
            }
            resolveSerializer.endTag((String) null, "config");
            resolveSerializer.endDocument();
            resolveSerializer.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }
}
