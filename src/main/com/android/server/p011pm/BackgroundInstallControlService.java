package com.android.server.p011pm;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManagerInternal;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.IBackgroundInstallControlService;
import android.content.pm.InstallSourceInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArrayMap;
import android.util.SparseSetArray;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import com.android.server.SystemService;
import com.android.server.p011pm.permission.PermissionManagerServiceInternal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;
/* renamed from: com.android.server.pm.BackgroundInstallControlService */
/* loaded from: classes2.dex */
public class BackgroundInstallControlService extends SystemService {
    public SparseSetArray<String> mBackgroundInstalledPackages;
    public final BinderService mBinderService;
    public final Context mContext;
    public final File mDiskFile;
    public final Handler mHandler;
    public final SparseArrayMap<String, TreeSet<ForegroundTimeFrame>> mInstallerForegroundTimeFrames;
    public final PackageManager mPackageManager;
    public final PackageManagerInternal mPackageManagerInternal;
    public final PermissionManagerServiceInternal mPermissionManager;
    public final UsageStatsManagerInternal mUsageStatsManagerInternal;

    /* renamed from: com.android.server.pm.BackgroundInstallControlService$Injector */
    /* loaded from: classes2.dex */
    public interface Injector {
        Context getContext();

        File getDiskFile();

        Looper getLooper();

        PackageManager getPackageManager();

        PackageManagerInternal getPackageManagerInternal();

        PermissionManagerServiceInternal getPermissionManager();

        UsageStatsManagerInternal getUsageStatsManagerInternal();
    }

    public final boolean installedByAdb(String str) {
        return str == null;
    }

    public BackgroundInstallControlService(Context context) {
        this(new InjectorImpl(context));
    }

    @VisibleForTesting
    public BackgroundInstallControlService(Injector injector) {
        super(injector.getContext());
        this.mBackgroundInstalledPackages = null;
        this.mInstallerForegroundTimeFrames = new SparseArrayMap<>();
        this.mContext = injector.getContext();
        this.mPackageManager = injector.getPackageManager();
        this.mPackageManagerInternal = injector.getPackageManagerInternal();
        this.mPermissionManager = injector.getPermissionManager();
        this.mHandler = new EventHandler(injector.getLooper(), this);
        this.mDiskFile = injector.getDiskFile();
        UsageStatsManagerInternal usageStatsManagerInternal = injector.getUsageStatsManagerInternal();
        this.mUsageStatsManagerInternal = usageStatsManagerInternal;
        usageStatsManagerInternal.registerListener(new UsageStatsManagerInternal.UsageEventListener() { // from class: com.android.server.pm.BackgroundInstallControlService$$ExternalSyntheticLambda0
            @Override // android.app.usage.UsageStatsManagerInternal.UsageEventListener
            public final void onUsageEvent(int i, UsageEvents.Event event) {
                BackgroundInstallControlService.this.lambda$new$0(i, event);
            }
        });
        this.mBinderService = new BinderService(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, UsageEvents.Event event) {
        this.mHandler.obtainMessage(0, i, 0, event).sendToTarget();
    }

    /* renamed from: com.android.server.pm.BackgroundInstallControlService$BinderService */
    /* loaded from: classes2.dex */
    public static final class BinderService extends IBackgroundInstallControlService.Stub {
        public final BackgroundInstallControlService mService;

        public BinderService(BackgroundInstallControlService backgroundInstallControlService) {
            this.mService = backgroundInstallControlService;
        }

        public ParceledListSlice<PackageInfo> getBackgroundInstalledPackages(long j, int i) {
            if (!Build.IS_DEBUGGABLE) {
                return this.mService.getBackgroundInstalledPackages(j, i);
            }
            String str = SystemProperties.get("debug.transparency.bg-install-apps");
            if (TextUtils.isEmpty(str)) {
                return this.mService.getBackgroundInstalledPackages(j, i);
            }
            return this.mService.getMockBackgroundInstalledPackages(str);
        }
    }

    @VisibleForTesting
    public ParceledListSlice<PackageInfo> getBackgroundInstalledPackages(long j, int i) {
        List installedPackagesAsUser = this.mPackageManager.getInstalledPackagesAsUser(PackageManager.PackageInfoFlags.of(j), i);
        initBackgroundInstalledPackages();
        ListIterator listIterator = installedPackagesAsUser.listIterator();
        while (listIterator.hasNext()) {
            if (!this.mBackgroundInstalledPackages.contains(i, ((PackageInfo) listIterator.next()).packageName)) {
                listIterator.remove();
            }
        }
        return new ParceledListSlice<>(installedPackagesAsUser);
    }

    public ParceledListSlice<PackageInfo> getMockBackgroundInstalledPackages(String str) {
        String[] split = str.split(",");
        ArrayList arrayList = new ArrayList();
        for (String str2 : split) {
            try {
                arrayList.add(this.mPackageManager.getPackageInfo(str2, PackageManager.PackageInfoFlags.of(131072L)));
            } catch (PackageManager.NameNotFoundException unused) {
                Slog.w("BackgroundInstallControlService", "Package's PackageInfo not found " + str2);
            }
        }
        return new ParceledListSlice<>(arrayList);
    }

    /* renamed from: com.android.server.pm.BackgroundInstallControlService$EventHandler */
    /* loaded from: classes2.dex */
    public static class EventHandler extends Handler {
        public final BackgroundInstallControlService mService;

        public EventHandler(Looper looper, BackgroundInstallControlService backgroundInstallControlService) {
            super(looper);
            this.mService = backgroundInstallControlService;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                this.mService.handleUsageEvent((UsageEvents.Event) message.obj, message.arg1);
            } else if (i == 1) {
                this.mService.handlePackageAdd((String) message.obj, message.arg1);
            } else if (i == 2) {
                this.mService.handlePackageRemove((String) message.obj, message.arg1);
            } else {
                Slog.w("BackgroundInstallControlService", "Unknown message: " + message.what);
            }
        }
    }

    public void handlePackageAdd(String str, int i) {
        try {
            ApplicationInfo applicationInfoAsUser = this.mPackageManager.getApplicationInfoAsUser(str, PackageManager.ApplicationInfoFlags.of(0L), i);
            try {
                InstallSourceInfo installSourceInfo = this.mPackageManager.getInstallSourceInfo(str);
                String installingPackageName = installSourceInfo.getInstallingPackageName();
                String initiatingPackageName = installSourceInfo.getInitiatingPackageName();
                long currentTimeMillis = System.currentTimeMillis() - (SystemClock.uptimeMillis() - applicationInfoAsUser.createTimestamp);
                if (installedByAdb(initiatingPackageName) || wasForegroundInstallation(installingPackageName, i, currentTimeMillis)) {
                    return;
                }
                initBackgroundInstalledPackages();
                this.mBackgroundInstalledPackages.add(i, str);
                writeBackgroundInstalledPackagesToDisk();
            } catch (PackageManager.NameNotFoundException unused) {
                Slog.w("BackgroundInstallControlService", "Package's installer not found " + str);
            }
        } catch (PackageManager.NameNotFoundException unused2) {
            Slog.w("BackgroundInstallControlService", "Package's appInfo not found " + str);
        }
    }

    public final boolean wasForegroundInstallation(String str, int i, long j) {
        TreeSet treeSet = (TreeSet) this.mInstallerForegroundTimeFrames.get(i, str);
        if (treeSet == null) {
            return false;
        }
        Iterator it = treeSet.iterator();
        while (it.hasNext()) {
            ForegroundTimeFrame foregroundTimeFrame = (ForegroundTimeFrame) it.next();
            if (foregroundTimeFrame.startTimeStampMillis <= j && (!foregroundTimeFrame.isDone() || j <= foregroundTimeFrame.endTimeStampMillis)) {
                return true;
            }
        }
        return false;
    }

    public void handlePackageRemove(String str, int i) {
        initBackgroundInstalledPackages();
        this.mBackgroundInstalledPackages.remove(i, str);
        writeBackgroundInstalledPackagesToDisk();
    }

    public void handleUsageEvent(UsageEvents.Event event, int i) {
        int i2 = event.mEventType;
        if ((i2 == 1 || i2 == 2 || i2 == 23) && isInstaller(event.mPackage, i)) {
            if (!this.mInstallerForegroundTimeFrames.contains(i, event.mPackage)) {
                this.mInstallerForegroundTimeFrames.add(i, event.mPackage, new TreeSet());
            }
            TreeSet treeSet = (TreeSet) this.mInstallerForegroundTimeFrames.get(i, event.mPackage);
            if (treeSet.size() == 0 || ((ForegroundTimeFrame) treeSet.last()).isDone()) {
                if (event.mEventType != 1) {
                    return;
                }
                treeSet.add(new ForegroundTimeFrame(event.mTimeStamp));
            }
            ((ForegroundTimeFrame) treeSet.last()).addEvent(event);
            if (treeSet.size() > 10) {
                treeSet.pollFirst();
            }
        }
    }

    @VisibleForTesting
    public void writeBackgroundInstalledPackagesToDisk() {
        AtomicFile atomicFile = new AtomicFile(this.mDiskFile);
        try {
            FileOutputStream startWrite = atomicFile.startWrite();
            try {
                ProtoOutputStream protoOutputStream = new ProtoOutputStream(startWrite);
                for (int i = 0; i < this.mBackgroundInstalledPackages.size(); i++) {
                    int keyAt = this.mBackgroundInstalledPackages.keyAt(i);
                    Iterator it = this.mBackgroundInstalledPackages.get(keyAt).iterator();
                    while (it.hasNext()) {
                        long start = protoOutputStream.start(2246267895809L);
                        protoOutputStream.write(1138166333441L, (String) it.next());
                        protoOutputStream.write(1120986464258L, keyAt + 1);
                        protoOutputStream.end(start);
                    }
                }
                protoOutputStream.flush();
                atomicFile.finishWrite(startWrite);
            } catch (Exception e) {
                Slog.e("BackgroundInstallControlService", "Failed to finish write to states protobuf.", e);
                atomicFile.failWrite(startWrite);
            }
        } catch (IOException e2) {
            Slog.e("BackgroundInstallControlService", "Failed to start write to states protobuf.", e2);
        }
    }

    @VisibleForTesting
    public void initBackgroundInstalledPackages() {
        if (this.mBackgroundInstalledPackages != null) {
            return;
        }
        this.mBackgroundInstalledPackages = new SparseSetArray<>();
        if (this.mDiskFile.exists()) {
            try {
                FileInputStream openRead = new AtomicFile(this.mDiskFile).openRead();
                ProtoInputStream protoInputStream = new ProtoInputStream(openRead);
                while (protoInputStream.nextField() != -1) {
                    if (protoInputStream.getFieldNumber() == 1) {
                        long start = protoInputStream.start(2246267895809L);
                        String str = null;
                        int i = -10000;
                        while (protoInputStream.nextField() != -1) {
                            int fieldNumber = protoInputStream.getFieldNumber();
                            if (fieldNumber == 1) {
                                str = protoInputStream.readString(1138166333441L);
                            } else if (fieldNumber == 2) {
                                i = protoInputStream.readInt(1120986464258L) - 1;
                            } else {
                                Slog.w("BackgroundInstallControlService", "Undefined field in proto: " + protoInputStream.getFieldNumber());
                            }
                        }
                        protoInputStream.end(start);
                        if (str != null && i != -10000) {
                            this.mBackgroundInstalledPackages.add(i, str);
                        } else {
                            Slog.w("BackgroundInstallControlService", "Fails to get packageName or UserId from proto file");
                        }
                    }
                }
                if (openRead != null) {
                    openRead.close();
                }
            } catch (IOException e) {
                Slog.w("BackgroundInstallControlService", "Error reading state from the disk", e);
            }
        }
    }

    @VisibleForTesting
    public SparseSetArray<String> getBackgroundInstalledPackages() {
        return this.mBackgroundInstalledPackages;
    }

    @VisibleForTesting
    public SparseArrayMap<String, TreeSet<ForegroundTimeFrame>> getInstallerForegroundTimeFrames() {
        return this.mInstallerForegroundTimeFrames;
    }

    public final boolean isInstaller(String str, int i) {
        return this.mInstallerForegroundTimeFrames.contains(i, str) || this.mPermissionManager.checkPermission(str, "android.permission.INSTALL_PACKAGES", i) == 0;
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        onStart(false);
    }

    @VisibleForTesting
    public void onStart(boolean z) {
        if (!z) {
            publishBinderService("background_install_control", this.mBinderService);
        }
        this.mPackageManagerInternal.getPackageList(new PackageManagerInternal.PackageListObserver() { // from class: com.android.server.pm.BackgroundInstallControlService.1
            @Override // android.content.p000pm.PackageManagerInternal.PackageListObserver
            public void onPackageAdded(String str, int i) {
                BackgroundInstallControlService.this.mHandler.obtainMessage(1, UserHandle.getUserId(i), 0, str).sendToTarget();
            }

            @Override // android.content.p000pm.PackageManagerInternal.PackageListObserver
            public void onPackageRemoved(String str, int i) {
                BackgroundInstallControlService.this.mHandler.obtainMessage(2, UserHandle.getUserId(i), 0, str).sendToTarget();
            }
        });
    }

    /* renamed from: com.android.server.pm.BackgroundInstallControlService$ForegroundTimeFrame */
    /* loaded from: classes2.dex */
    public static final class ForegroundTimeFrame implements Comparable<ForegroundTimeFrame> {
        public final long startTimeStampMillis;
        public long endTimeStampMillis = 0;
        public final Set<Integer> activities = new ArraySet();

        @Override // java.lang.Comparable
        public int compareTo(ForegroundTimeFrame foregroundTimeFrame) {
            int compare = Long.compare(this.startTimeStampMillis, foregroundTimeFrame.startTimeStampMillis);
            return compare != 0 ? compare : Integer.compare(hashCode(), foregroundTimeFrame.hashCode());
        }

        public ForegroundTimeFrame(long j) {
            this.startTimeStampMillis = j;
        }

        public boolean isDone() {
            return this.endTimeStampMillis != 0;
        }

        public void addEvent(UsageEvents.Event event) {
            int i = event.mEventType;
            if (i == 1) {
                this.activities.add(Integer.valueOf(event.mInstanceId));
            } else if ((i == 2 || i == 23) && this.activities.contains(Integer.valueOf(event.mInstanceId))) {
                this.activities.remove(Integer.valueOf(event.mInstanceId));
                if (this.activities.size() == 0) {
                    this.endTimeStampMillis = event.mTimeStamp;
                }
            }
        }
    }

    /* renamed from: com.android.server.pm.BackgroundInstallControlService$InjectorImpl */
    /* loaded from: classes2.dex */
    public static final class InjectorImpl implements Injector {
        public final Context mContext;

        public InjectorImpl(Context context) {
            this.mContext = context;
        }

        @Override // com.android.server.p011pm.BackgroundInstallControlService.Injector
        public Context getContext() {
            return this.mContext;
        }

        @Override // com.android.server.p011pm.BackgroundInstallControlService.Injector
        public PackageManager getPackageManager() {
            return this.mContext.getPackageManager();
        }

        @Override // com.android.server.p011pm.BackgroundInstallControlService.Injector
        public PackageManagerInternal getPackageManagerInternal() {
            return (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        }

        @Override // com.android.server.p011pm.BackgroundInstallControlService.Injector
        public UsageStatsManagerInternal getUsageStatsManagerInternal() {
            return (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
        }

        @Override // com.android.server.p011pm.BackgroundInstallControlService.Injector
        public PermissionManagerServiceInternal getPermissionManager() {
            return (PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class);
        }

        @Override // com.android.server.p011pm.BackgroundInstallControlService.Injector
        public Looper getLooper() {
            ServiceThread serviceThread = new ServiceThread("BackgroundInstallControlService", -2, true);
            serviceThread.start();
            return serviceThread.getLooper();
        }

        @Override // com.android.server.p011pm.BackgroundInstallControlService.Injector
        public File getDiskFile() {
            return new File(new File(Environment.getDataSystemDirectory(), "bic"), "states");
        }
    }
}
