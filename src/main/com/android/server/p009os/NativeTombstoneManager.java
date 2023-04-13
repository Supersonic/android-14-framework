package com.android.server.p009os;

import android.app.ApplicationExitInfo;
import android.app.IParcelFileDescriptorRetriever;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.FileObserver;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.UserHandle;
import android.system.ErrnoException;
import android.system.Os;
import android.system.StructTimespec;
import android.util.Slog;
import android.util.SparseArray;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoParseException;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.BootReceiver;
import com.android.server.ServiceThread;
import com.android.server.p009os.NativeTombstoneManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import libcore.io.IoUtils;
/* renamed from: com.android.server.os.NativeTombstoneManager */
/* loaded from: classes2.dex */
public final class NativeTombstoneManager {
    public static final String TAG = "NativeTombstoneManager";
    public static final File TOMBSTONE_DIR = new File("/data/tombstones");
    public final Context mContext;
    public final Handler mHandler;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final SparseArray<TombstoneFile> mTombstones = new SparseArray<>();
    public final TombstoneWatcher mWatcher;

    public NativeTombstoneManager(Context context) {
        this.mContext = context;
        ServiceThread serviceThread = new ServiceThread(TAG + ":tombstoneWatcher", 10, true);
        serviceThread.start();
        this.mHandler = serviceThread.getThreadHandler();
        TombstoneWatcher tombstoneWatcher = new TombstoneWatcher();
        this.mWatcher = tombstoneWatcher;
        tombstoneWatcher.startWatching();
    }

    public void onSystemReady() {
        registerForUserRemoval();
        registerForPackageRemoval();
        this.mHandler.post(new Runnable() { // from class: com.android.server.os.NativeTombstoneManager$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                NativeTombstoneManager.this.lambda$onSystemReady$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSystemReady$0() {
        File[] listFiles = TOMBSTONE_DIR.listFiles();
        for (int i = 0; listFiles != null && i < listFiles.length; i++) {
            if (listFiles[i].isFile()) {
                handleTombstone(listFiles[i]);
            }
        }
    }

    public final void handleTombstone(File file) {
        File file2;
        String name = file.getName();
        if (name.endsWith(".tmp")) {
            file.delete();
        } else if (name.startsWith("tombstone_")) {
            boolean endsWith = name.endsWith(".pb");
            if (endsWith) {
                file2 = file;
            } else {
                file2 = new File(file.getAbsolutePath() + ".pb");
            }
            Optional<TombstoneFile> handleProtoTombstone = handleProtoTombstone(file2, endsWith);
            BootReceiver.addTombstoneToDropBox(this.mContext, file, endsWith, handleProtoTombstone.isPresent() ? handleProtoTombstone.get().getProcessName() : "UNKNOWN");
        }
    }

    public final Optional<TombstoneFile> handleProtoTombstone(File file, boolean z) {
        String name = file.getName();
        if (!name.endsWith(".pb")) {
            String str = TAG;
            Slog.w(str, "unexpected tombstone name: " + file);
            return Optional.empty();
        }
        String substring = name.substring(10);
        try {
            int parseInt = Integer.parseInt(substring.substring(0, substring.length() - 3));
            if (parseInt < 0 || parseInt > 99) {
                String str2 = TAG;
                Slog.w(str2, "unexpected tombstone name: " + file);
                return Optional.empty();
            }
            try {
                ParcelFileDescriptor open = ParcelFileDescriptor.open(file, 805306368);
                Optional<TombstoneFile> parse = TombstoneFile.parse(open);
                if (!parse.isPresent()) {
                    IoUtils.closeQuietly(open);
                    return Optional.empty();
                }
                if (z) {
                    synchronized (this.mLock) {
                        TombstoneFile tombstoneFile = this.mTombstones.get(parseInt);
                        if (tombstoneFile != null) {
                            tombstoneFile.dispose();
                        }
                        this.mTombstones.put(parseInt, parse.get());
                    }
                }
                return parse;
            } catch (FileNotFoundException e) {
                String str3 = TAG;
                Slog.w(str3, "failed to open " + file, e);
                return Optional.empty();
            }
        } catch (NumberFormatException unused) {
            String str4 = TAG;
            Slog.w(str4, "unexpected tombstone name: " + file);
            return Optional.empty();
        }
    }

    public void purge(final Optional<Integer> optional, final Optional<Integer> optional2) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.os.NativeTombstoneManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                NativeTombstoneManager.this.lambda$purge$1(optional, optional2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$purge$1(Optional optional, Optional optional2) {
        synchronized (this.mLock) {
            for (int size = this.mTombstones.size() - 1; size >= 0; size--) {
                TombstoneFile valueAt = this.mTombstones.valueAt(size);
                if (valueAt.matches(optional, optional2)) {
                    valueAt.purge();
                    this.mTombstones.removeAt(size);
                }
            }
        }
    }

    public final void purgePackage(int i, boolean z) {
        Optional<Integer> of;
        int appId = UserHandle.getAppId(i);
        if (z) {
            of = Optional.empty();
        } else {
            of = Optional.of(Integer.valueOf(UserHandle.getUserId(i)));
        }
        purge(of, Optional.of(Integer.valueOf(appId)));
    }

    public final void purgeUser(int i) {
        purge(Optional.of(Integer.valueOf(i)), Optional.empty());
    }

    public final void registerForPackageRemoval() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiverForAllUsers(new BroadcastReceiver() { // from class: com.android.server.os.NativeTombstoneManager.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                int intExtra = intent.getIntExtra("android.intent.extra.UID", -10000);
                if (intExtra == -10000) {
                    return;
                }
                NativeTombstoneManager.this.purgePackage(intExtra, intent.getBooleanExtra("android.intent.extra.REMOVED_FOR_ALL_USERS", false));
            }
        }, intentFilter, null, this.mHandler);
    }

    public final void registerForUserRemoval() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        this.mContext.registerReceiverForAllUsers(new BroadcastReceiver() { // from class: com.android.server.os.NativeTombstoneManager.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -1);
                if (intExtra < 1) {
                    return;
                }
                NativeTombstoneManager.this.purgeUser(intExtra);
            }
        }, intentFilter, null, this.mHandler);
    }

    public void collectTombstones(final ArrayList<ApplicationExitInfo> arrayList, int i, final int i2, final int i3) {
        final CompletableFuture completableFuture = new CompletableFuture();
        if (UserHandle.isApp(i)) {
            final int userId = UserHandle.getUserId(i);
            final int appId = UserHandle.getAppId(i);
            this.mHandler.post(new Runnable() { // from class: com.android.server.os.NativeTombstoneManager$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    NativeTombstoneManager.this.lambda$collectTombstones$3(userId, appId, i2, arrayList, i3, completableFuture);
                }
            });
            try {
                completableFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$collectTombstones$3(int i, int i2, int i3, ArrayList arrayList, int i4, CompletableFuture completableFuture) {
        boolean z;
        synchronized (this.mLock) {
            int size = this.mTombstones.size();
            z = false;
            for (int i5 = 0; i5 < size; i5++) {
                TombstoneFile valueAt = this.mTombstones.valueAt(i5);
                if (valueAt.matches(Optional.of(Integer.valueOf(i)), Optional.of(Integer.valueOf(i2))) && (i3 == 0 || valueAt.mPid == i3)) {
                    int size2 = arrayList.size();
                    for (int i6 = 0; i6 < size2; i6++) {
                        ApplicationExitInfo applicationExitInfo = (ApplicationExitInfo) arrayList.get(i6);
                        if (valueAt.matches(applicationExitInfo)) {
                            applicationExitInfo.setNativeTombstoneRetriever(valueAt.getPfdRetriever());
                        }
                    }
                    if (arrayList.size() < i4) {
                        arrayList.add(valueAt.toAppExitInfo());
                        z = true;
                    }
                }
            }
        }
        if (z) {
            Collections.sort(arrayList, new Comparator() { // from class: com.android.server.os.NativeTombstoneManager$$ExternalSyntheticLambda3
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$collectTombstones$2;
                    lambda$collectTombstones$2 = NativeTombstoneManager.lambda$collectTombstones$2((ApplicationExitInfo) obj, (ApplicationExitInfo) obj2);
                    return lambda$collectTombstones$2;
                }
            });
        }
        completableFuture.complete(null);
    }

    public static /* synthetic */ int lambda$collectTombstones$2(ApplicationExitInfo applicationExitInfo, ApplicationExitInfo applicationExitInfo2) {
        int i = ((applicationExitInfo2.getTimestamp() - applicationExitInfo.getTimestamp()) > 0L ? 1 : ((applicationExitInfo2.getTimestamp() - applicationExitInfo.getTimestamp()) == 0L ? 0 : -1));
        if (i < 0) {
            return -1;
        }
        return i == 0 ? 0 : 1;
    }

    /* renamed from: com.android.server.os.NativeTombstoneManager$TombstoneFile */
    /* loaded from: classes2.dex */
    public static class TombstoneFile {
        public int mAppId;
        public String mCrashReason;
        public final ParcelFileDescriptor mPfd;
        public int mPid;
        public String mProcessName;
        public boolean mPurged = false;
        public final IParcelFileDescriptorRetriever mRetriever = new ParcelFileDescriptorRetriever();
        public long mTimestampMs;
        public int mUid;
        public int mUserId;

        public TombstoneFile(ParcelFileDescriptor parcelFileDescriptor) {
            this.mPfd = parcelFileDescriptor;
        }

        public boolean matches(Optional<Integer> optional, Optional<Integer> optional2) {
            if (this.mPurged) {
                return false;
            }
            if (!optional.isPresent() || optional.get().intValue() == this.mUserId) {
                return !optional2.isPresent() || optional2.get().intValue() == this.mAppId;
            }
            return false;
        }

        public boolean matches(ApplicationExitInfo applicationExitInfo) {
            return applicationExitInfo.getReason() == 5 && applicationExitInfo.getPid() == this.mPid && applicationExitInfo.getRealUid() == this.mUid && Math.abs(applicationExitInfo.getTimestamp() - this.mTimestampMs) <= 5000;
        }

        public String getProcessName() {
            return this.mProcessName;
        }

        public void dispose() {
            IoUtils.closeQuietly(this.mPfd);
        }

        public void purge() {
            if (this.mPurged) {
                return;
            }
            try {
                Os.ftruncate(this.mPfd.getFileDescriptor(), 0L);
            } catch (ErrnoException e) {
                Slog.e(NativeTombstoneManager.TAG, "Failed to truncate tombstone", e);
            }
            this.mPurged = true;
        }

        public static Optional<TombstoneFile> parse(ParcelFileDescriptor parcelFileDescriptor) {
            long j;
            ProtoInputStream protoInputStream = new ProtoInputStream(new FileInputStream(parcelFileDescriptor.getFileDescriptor()));
            int i = 0;
            String str = null;
            String str2 = "";
            String str3 = str2;
            int i2 = 0;
            while (protoInputStream.nextField() != -1) {
                try {
                    int fieldNumber = protoInputStream.getFieldNumber();
                    if (fieldNumber == 5) {
                        i2 = protoInputStream.readInt(1155346202629L);
                    } else if (fieldNumber != 15) {
                        if (fieldNumber == 7) {
                            i = protoInputStream.readInt(1155346202631L);
                        } else if (fieldNumber == 8) {
                            str3 = protoInputStream.readString(1138166333448L);
                        } else if (fieldNumber == 9 && str == null) {
                            str = protoInputStream.readString(2237677961225L);
                        }
                    } else if (str2.equals("")) {
                        long start = protoInputStream.start(2246267895823L);
                        while (true) {
                            if (protoInputStream.nextField() != -1) {
                                if (protoInputStream.getFieldNumber() == 1) {
                                    str2 = protoInputStream.readString(1138166333441L);
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        protoInputStream.end(start);
                    }
                } catch (IOException | ProtoParseException e) {
                    Slog.e(NativeTombstoneManager.TAG, "Failed to parse tombstone", e);
                    return Optional.empty();
                }
            }
            if (!UserHandle.isApp(i)) {
                Slog.e(NativeTombstoneManager.TAG, "Tombstone's UID (" + i + ") not an app, ignoring");
                return Optional.empty();
            }
            try {
                StructTimespec structTimespec = Os.fstat(parcelFileDescriptor.getFileDescriptor()).st_atim;
                j = (structTimespec.tv_sec * 1000) + (structTimespec.tv_nsec / 1000000);
            } catch (ErrnoException e2) {
                Slog.e(NativeTombstoneManager.TAG, "Failed to get timestamp of tombstone", e2);
                j = 0;
            }
            int userId = UserHandle.getUserId(i);
            int appId = UserHandle.getAppId(i);
            if (!str3.startsWith("u:r:untrusted_app")) {
                Slog.e(NativeTombstoneManager.TAG, "Tombstone has invalid selinux label (" + str3 + "), ignoring");
                return Optional.empty();
            }
            TombstoneFile tombstoneFile = new TombstoneFile(parcelFileDescriptor);
            tombstoneFile.mUserId = userId;
            tombstoneFile.mAppId = appId;
            tombstoneFile.mPid = i2;
            tombstoneFile.mUid = i;
            tombstoneFile.mProcessName = str != null ? str : "";
            tombstoneFile.mTimestampMs = j;
            tombstoneFile.mCrashReason = str2;
            return Optional.of(tombstoneFile);
        }

        public IParcelFileDescriptorRetriever getPfdRetriever() {
            return this.mRetriever;
        }

        public ApplicationExitInfo toAppExitInfo() {
            ApplicationExitInfo applicationExitInfo = new ApplicationExitInfo();
            applicationExitInfo.setPid(this.mPid);
            applicationExitInfo.setRealUid(this.mUid);
            applicationExitInfo.setPackageUid(this.mUid);
            applicationExitInfo.setDefiningUid(this.mUid);
            applicationExitInfo.setProcessName(this.mProcessName);
            applicationExitInfo.setReason(5);
            applicationExitInfo.setStatus(0);
            applicationExitInfo.setImportance(1000);
            applicationExitInfo.setPackageName("");
            applicationExitInfo.setProcessStateSummary(null);
            applicationExitInfo.setPss(0L);
            applicationExitInfo.setRss(0L);
            applicationExitInfo.setTimestamp(this.mTimestampMs);
            applicationExitInfo.setDescription(this.mCrashReason);
            applicationExitInfo.setSubReason(0);
            applicationExitInfo.setNativeTombstoneRetriever(this.mRetriever);
            return applicationExitInfo;
        }

        /* renamed from: com.android.server.os.NativeTombstoneManager$TombstoneFile$ParcelFileDescriptorRetriever */
        /* loaded from: classes2.dex */
        public class ParcelFileDescriptorRetriever extends IParcelFileDescriptorRetriever.Stub {
            public ParcelFileDescriptorRetriever() {
            }

            public ParcelFileDescriptor getPfd() {
                if (TombstoneFile.this.mPurged) {
                    return null;
                }
                try {
                    return ParcelFileDescriptor.open(new File("/proc/self/fd/" + TombstoneFile.this.mPfd.getFd()), 268435456);
                } catch (FileNotFoundException e) {
                    Slog.e(NativeTombstoneManager.TAG, "failed to reopen file descriptor as read-only", e);
                    return null;
                }
            }
        }
    }

    /* renamed from: com.android.server.os.NativeTombstoneManager$TombstoneWatcher */
    /* loaded from: classes2.dex */
    public class TombstoneWatcher extends FileObserver {
        public TombstoneWatcher() {
            super(NativeTombstoneManager.TOMBSTONE_DIR, (int) FrameworkStatsLog.NON_A11Y_TOOL_SERVICE_WARNING_REPORT);
        }

        @Override // android.os.FileObserver
        public void onEvent(int i, final String str) {
            NativeTombstoneManager.this.mHandler.post(new Runnable() { // from class: com.android.server.os.NativeTombstoneManager$TombstoneWatcher$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    NativeTombstoneManager.TombstoneWatcher.this.lambda$onEvent$0(str);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onEvent$0(String str) {
            if (str.endsWith(".tmp")) {
                return;
            }
            NativeTombstoneManager.this.handleTombstone(new File(NativeTombstoneManager.TOMBSTONE_DIR, str));
        }
    }
}
