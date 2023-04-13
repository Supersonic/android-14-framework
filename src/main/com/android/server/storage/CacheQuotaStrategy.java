package com.android.server.storage;

import android.app.usage.CacheQuotaHint;
import android.app.usage.ICacheQuotaService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManagerInternal;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseLongArray;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.p011pm.Installer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class CacheQuotaStrategy implements RemoteCallback.OnResultListener {
    public final Context mContext;
    public final Installer mInstaller;
    public final Object mLock = new Object();
    public AtomicFile mPreviousValuesFile;
    public final ArrayMap<String, SparseLongArray> mQuotaMap;
    public ICacheQuotaService mRemoteService;
    public ServiceConnection mServiceConnection;
    public final UsageStatsManagerInternal mUsageStats;

    public CacheQuotaStrategy(Context context, UsageStatsManagerInternal usageStatsManagerInternal, Installer installer, ArrayMap<String, SparseLongArray> arrayMap) {
        Objects.requireNonNull(context);
        this.mContext = context;
        Objects.requireNonNull(usageStatsManagerInternal);
        this.mUsageStats = usageStatsManagerInternal;
        Objects.requireNonNull(installer);
        this.mInstaller = installer;
        Objects.requireNonNull(arrayMap);
        this.mQuotaMap = arrayMap;
        this.mPreviousValuesFile = new AtomicFile(new File(new File(Environment.getDataDirectory(), "system"), "cachequota.xml"));
    }

    public void recalculateQuotas() {
        createServiceConnection();
        ComponentName serviceComponentName = getServiceComponentName();
        if (serviceComponentName != null) {
            Intent intent = new Intent();
            intent.setComponent(serviceComponentName);
            this.mContext.bindServiceAsUser(intent, this.mServiceConnection, 1, UserHandle.CURRENT);
        }
    }

    public final void createServiceConnection() {
        if (this.mServiceConnection != null) {
            return;
        }
        this.mServiceConnection = new ServiceConnection() { // from class: com.android.server.storage.CacheQuotaStrategy.1
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, final IBinder iBinder) {
                AsyncTask.execute(new Runnable() { // from class: com.android.server.storage.CacheQuotaStrategy.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        synchronized (CacheQuotaStrategy.this.mLock) {
                            CacheQuotaStrategy.this.mRemoteService = ICacheQuotaService.Stub.asInterface(iBinder);
                            List unfulfilledRequests = CacheQuotaStrategy.this.getUnfulfilledRequests();
                            try {
                                CacheQuotaStrategy.this.mRemoteService.computeCacheQuotaHints(new RemoteCallback(CacheQuotaStrategy.this), unfulfilledRequests);
                            } catch (RemoteException e) {
                                Slog.w("CacheQuotaStrategy", "Remote exception occurred while trying to get cache quota", e);
                            }
                        }
                    }
                });
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                synchronized (CacheQuotaStrategy.this.mLock) {
                    CacheQuotaStrategy.this.mRemoteService = null;
                }
            }
        };
    }

    public final List<CacheQuotaHint> getUnfulfilledRequests() {
        CacheQuotaStrategy cacheQuotaStrategy = this;
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - 31449600000L;
        ArrayList arrayList = new ArrayList();
        List<UserInfo> users = ((UserManager) cacheQuotaStrategy.mContext.getSystemService(UserManager.class)).getUsers();
        PackageManager packageManager = cacheQuotaStrategy.mContext.getPackageManager();
        for (UserInfo userInfo : users) {
            List<UsageStats> queryUsageStatsForUser = cacheQuotaStrategy.mUsageStats.queryUsageStatsForUser(userInfo.id, 4, j, currentTimeMillis, false);
            if (queryUsageStatsForUser != null) {
                for (int i = 0; i < queryUsageStatsForUser.size(); i++) {
                    UsageStats usageStats = queryUsageStatsForUser.get(i);
                    try {
                        ApplicationInfo applicationInfoAsUser = packageManager.getApplicationInfoAsUser(usageStats.getPackageName(), 0, userInfo.id);
                        arrayList.add(new CacheQuotaHint.Builder().setVolumeUuid(applicationInfoAsUser.volumeUuid).setUid(applicationInfoAsUser.uid).setUsageStats(usageStats).setQuota(-1L).build());
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                }
            }
            cacheQuotaStrategy = this;
        }
        return arrayList;
    }

    public void onResult(Bundle bundle) {
        ArrayList parcelableArrayList = bundle.getParcelableArrayList("requests", CacheQuotaHint.class);
        pushProcessedQuotas(parcelableArrayList);
        writeXmlToFile(parcelableArrayList);
    }

    public final void pushProcessedQuotas(List<CacheQuotaHint> list) {
        for (CacheQuotaHint cacheQuotaHint : list) {
            long quota = cacheQuotaHint.getQuota();
            if (quota != -1) {
                try {
                    int uid = cacheQuotaHint.getUid();
                    this.mInstaller.setAppQuota(cacheQuotaHint.getVolumeUuid(), UserHandle.getUserId(uid), UserHandle.getAppId(uid), quota);
                    insertIntoQuotaMap(cacheQuotaHint.getVolumeUuid(), UserHandle.getUserId(uid), UserHandle.getAppId(uid), quota);
                } catch (Installer.InstallerException e) {
                    Slog.w("CacheQuotaStrategy", "Failed to set cache quota for " + cacheQuotaHint.getUid(), e);
                }
            }
        }
        disconnectService();
    }

    public final void insertIntoQuotaMap(String str, int i, int i2, long j) {
        SparseLongArray sparseLongArray = this.mQuotaMap.get(str);
        if (sparseLongArray == null) {
            sparseLongArray = new SparseLongArray();
            this.mQuotaMap.put(str, sparseLongArray);
        }
        sparseLongArray.put(UserHandle.getUid(i, i2), j);
    }

    public final void disconnectService() {
        ServiceConnection serviceConnection = this.mServiceConnection;
        if (serviceConnection != null) {
            this.mContext.unbindService(serviceConnection);
            this.mServiceConnection = null;
        }
    }

    public final ComponentName getServiceComponentName() {
        ServiceInfo serviceInfo;
        String servicesSystemSharedLibraryPackageName = this.mContext.getPackageManager().getServicesSystemSharedLibraryPackageName();
        if (servicesSystemSharedLibraryPackageName == null) {
            Slog.w("CacheQuotaStrategy", "could not access the cache quota service: no package!");
            return null;
        }
        Intent intent = new Intent("android.app.usage.CacheQuotaService");
        intent.setPackage(servicesSystemSharedLibraryPackageName);
        ResolveInfo resolveService = this.mContext.getPackageManager().resolveService(intent, 132);
        if (resolveService == null || (serviceInfo = resolveService.serviceInfo) == null) {
            Slog.w("CacheQuotaStrategy", "No valid components found.");
            return null;
        }
        return new ComponentName(serviceInfo.packageName, serviceInfo.name);
    }

    public final void writeXmlToFile(List<CacheQuotaHint> list) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = this.mPreviousValuesFile.startWrite();
            saveToXml(Xml.resolveSerializer(fileOutputStream), list, new StatFs(Environment.getDataDirectory().getAbsolutePath()).getAvailableBytes());
            this.mPreviousValuesFile.finishWrite(fileOutputStream);
        } catch (Exception e) {
            Slog.e("CacheQuotaStrategy", "An error occurred while writing the cache quota file.", e);
            this.mPreviousValuesFile.failWrite(fileOutputStream);
        }
    }

    public long setupQuotasFromFile() throws IOException {
        try {
            FileInputStream openRead = this.mPreviousValuesFile.openRead();
            try {
                Pair<Long, List<CacheQuotaHint>> readFromXml = readFromXml(openRead);
                if (openRead != null) {
                    openRead.close();
                }
                if (readFromXml == null) {
                    Slog.e("CacheQuotaStrategy", "An error occurred while parsing the cache quota file.");
                    return -1L;
                }
                pushProcessedQuotas((List) readFromXml.second);
                return ((Long) readFromXml.first).longValue();
            } catch (XmlPullParserException e) {
                throw new IllegalStateException(e.getMessage());
            }
        } catch (FileNotFoundException unused) {
            return -1L;
        }
    }

    @VisibleForTesting
    public static void saveToXml(TypedXmlSerializer typedXmlSerializer, List<CacheQuotaHint> list, long j) throws IOException {
        typedXmlSerializer.startDocument((String) null, Boolean.TRUE);
        typedXmlSerializer.startTag((String) null, "cache-info");
        typedXmlSerializer.attributeLong((String) null, "previousBytes", j);
        for (CacheQuotaHint cacheQuotaHint : list) {
            typedXmlSerializer.startTag((String) null, "quota");
            if (cacheQuotaHint.getVolumeUuid() != null) {
                typedXmlSerializer.attribute((String) null, "uuid", cacheQuotaHint.getVolumeUuid());
            }
            typedXmlSerializer.attributeInt((String) null, "uid", cacheQuotaHint.getUid());
            typedXmlSerializer.attributeLong((String) null, "bytes", cacheQuotaHint.getQuota());
            typedXmlSerializer.endTag((String) null, "quota");
        }
        typedXmlSerializer.endTag((String) null, "cache-info");
        typedXmlSerializer.endDocument();
    }

    public static Pair<Long, List<CacheQuotaHint>> readFromXml(InputStream inputStream) throws XmlPullParserException, IOException {
        TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(inputStream);
        int eventType = resolvePullParser.getEventType();
        while (eventType != 2 && eventType != 1) {
            eventType = resolvePullParser.next();
        }
        if (eventType == 1) {
            Slog.d("CacheQuotaStrategy", "No quotas found in quota file.");
            return null;
        } else if (!"cache-info".equals(resolvePullParser.getName())) {
            throw new IllegalStateException("Invalid starting tag.");
        } else {
            ArrayList arrayList = new ArrayList();
            try {
                long attributeLong = resolvePullParser.getAttributeLong((String) null, "previousBytes");
                int next = resolvePullParser.next();
                do {
                    if (next == 2 && "quota".equals(resolvePullParser.getName())) {
                        CacheQuotaHint requestFromXml = getRequestFromXml(resolvePullParser);
                        if (requestFromXml == null) {
                            continue;
                        } else {
                            arrayList.add(requestFromXml);
                        }
                    }
                    next = resolvePullParser.next();
                    continue;
                } while (next != 1);
                return new Pair<>(Long.valueOf(attributeLong), arrayList);
            } catch (NumberFormatException unused) {
                throw new IllegalStateException("Previous bytes formatted incorrectly; aborting quota read.");
            }
        }
    }

    @VisibleForTesting
    public static CacheQuotaHint getRequestFromXml(TypedXmlPullParser typedXmlPullParser) {
        try {
            String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "uuid");
            int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "uid");
            return new CacheQuotaHint.Builder().setVolumeUuid(attributeValue).setUid(attributeInt).setQuota(typedXmlPullParser.getAttributeLong((String) null, "bytes")).build();
        } catch (XmlPullParserException unused) {
            Slog.e("CacheQuotaStrategy", "Invalid cache quota request, skipping.");
            return null;
        }
    }
}
