package com.android.server.logcat;

import android.app.ActivityManagerInternal;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.ILogd;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.logcat.ILogcatManagerService;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.ILogAccessDialogCallback;
import com.android.internal.util.ArrayUtils;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
/* loaded from: classes2.dex */
public final class LogcatManagerService extends SystemService {
    @VisibleForTesting
    static final int PENDING_CONFIRMATION_TIMEOUT_MILLIS;
    @VisibleForTesting
    static final int STATUS_EXPIRATION_TIMEOUT_MILLIS = 60000;
    public final Map<LogAccessClient, Integer> mActiveLogAccessCount;
    public ActivityManagerInternal mActivityManagerInternal;
    public final BinderService mBinderService;
    public final Supplier<Long> mClock;
    public final Context mContext;
    public final LogAccessDialogCallback mDialogCallback;
    public final Handler mHandler;
    public final Injector mInjector;
    public final Map<LogAccessClient, LogAccessStatus> mLogAccessStatus;
    public ILogd mLogdService;

    static {
        PENDING_CONFIRMATION_TIMEOUT_MILLIS = Build.IS_DEBUGGABLE ? 70000 : 400000;
    }

    /* loaded from: classes2.dex */
    public static final class LogAccessClient {
        public final String mPackageName;
        public final int mUid;

        public LogAccessClient(int i, String str) {
            this.mUid = i;
            this.mPackageName = str;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof LogAccessClient) {
                LogAccessClient logAccessClient = (LogAccessClient) obj;
                return this.mUid == logAccessClient.mUid && Objects.equals(this.mPackageName, logAccessClient.mPackageName);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mUid), this.mPackageName);
        }

        public String toString() {
            return "LogAccessClient{mUid=" + this.mUid + ", mPackageName=" + this.mPackageName + '}';
        }
    }

    /* loaded from: classes2.dex */
    public static final class LogAccessRequest {
        public final int mFd;
        public final int mGid;
        public final int mPid;
        public final int mUid;

        public LogAccessRequest(int i, int i2, int i3, int i4) {
            this.mUid = i;
            this.mGid = i2;
            this.mPid = i3;
            this.mFd = i4;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof LogAccessRequest) {
                LogAccessRequest logAccessRequest = (LogAccessRequest) obj;
                return this.mUid == logAccessRequest.mUid && this.mGid == logAccessRequest.mGid && this.mPid == logAccessRequest.mPid && this.mFd == logAccessRequest.mFd;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mUid), Integer.valueOf(this.mGid), Integer.valueOf(this.mPid), Integer.valueOf(this.mFd));
        }

        public String toString() {
            return "LogAccessRequest{mUid=" + this.mUid + ", mGid=" + this.mGid + ", mPid=" + this.mPid + ", mFd=" + this.mFd + '}';
        }
    }

    /* loaded from: classes2.dex */
    public static final class LogAccessStatus {
        public final List<LogAccessRequest> mPendingRequests;
        public int mStatus;

        public LogAccessStatus() {
            this.mStatus = 0;
            this.mPendingRequests = new ArrayList();
        }
    }

    /* loaded from: classes2.dex */
    public final class BinderService extends ILogcatManagerService.Stub {
        public BinderService() {
        }

        public void startThread(int i, int i2, int i3, int i4) {
            LogcatManagerService.this.mHandler.sendMessageAtTime(LogcatManagerService.this.mHandler.obtainMessage(0, new LogAccessRequest(i, i2, i3, i4)), ((Long) LogcatManagerService.this.mClock.get()).longValue());
        }

        public void finishThread(int i, int i2, int i3, int i4) {
            LogcatManagerService.this.mHandler.sendMessageAtTime(LogcatManagerService.this.mHandler.obtainMessage(3, new LogAccessRequest(i, i2, i3, i4)), ((Long) LogcatManagerService.this.mClock.get()).longValue());
        }
    }

    /* loaded from: classes2.dex */
    public final class LogAccessDialogCallback extends ILogAccessDialogCallback.Stub {
        public LogAccessDialogCallback() {
        }

        public void approveAccessForClient(int i, String str) {
            LogcatManagerService.this.mHandler.sendMessageAtTime(LogcatManagerService.this.mHandler.obtainMessage(1, new LogAccessClient(i, str)), ((Long) LogcatManagerService.this.mClock.get()).longValue());
        }

        public void declineAccessForClient(int i, String str) {
            LogcatManagerService.this.mHandler.sendMessageAtTime(LogcatManagerService.this.mHandler.obtainMessage(2, new LogAccessClient(i, str)), ((Long) LogcatManagerService.this.mClock.get()).longValue());
        }
    }

    public final ILogd getLogdService() {
        if (this.mLogdService == null) {
            this.mLogdService = this.mInjector.getLogdService();
        }
        return this.mLogdService;
    }

    /* loaded from: classes2.dex */
    public static class LogAccessRequestHandler extends Handler {
        public final LogcatManagerService mService;

        public LogAccessRequestHandler(Looper looper, LogcatManagerService logcatManagerService) {
            super(looper);
            this.mService = logcatManagerService;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                this.mService.onLogAccessRequested((LogAccessRequest) message.obj);
            } else if (i == 1) {
                this.mService.onAccessApprovedForClient((LogAccessClient) message.obj);
            } else if (i == 2) {
                this.mService.onAccessDeclinedForClient((LogAccessClient) message.obj);
            } else if (i == 3) {
                this.mService.onLogAccessFinished((LogAccessRequest) message.obj);
            } else if (i == 4) {
                this.mService.onPendingTimeoutExpired((LogAccessClient) message.obj);
            } else if (i != 5) {
            } else {
                this.mService.onAccessStatusExpired((LogAccessClient) message.obj);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class Injector {
        public Supplier<Long> createClock() {
            return new Supplier() { // from class: com.android.server.logcat.LogcatManagerService$Injector$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    return Long.valueOf(SystemClock.uptimeMillis());
                }
            };
        }

        public Looper getLooper() {
            return Looper.getMainLooper();
        }

        public ILogd getLogdService() {
            return ILogd.Stub.asInterface(ServiceManager.getService("logd"));
        }
    }

    public LogcatManagerService(Context context) {
        this(context, new Injector());
    }

    public LogcatManagerService(Context context, Injector injector) {
        super(context);
        this.mLogAccessStatus = new ArrayMap();
        this.mActiveLogAccessCount = new ArrayMap();
        this.mContext = context;
        this.mInjector = injector;
        this.mClock = injector.createClock();
        this.mBinderService = new BinderService();
        this.mDialogCallback = new LogAccessDialogCallback();
        this.mHandler = new LogAccessRequestHandler(injector.getLooper(), this);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        try {
            this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
            publishBinderService("logcat", this.mBinderService);
        } catch (Throwable th) {
            Slog.e("LogcatManagerService", "Could not start the LogcatManagerService.", th);
        }
    }

    @VisibleForTesting
    public LogAccessDialogCallback getDialogCallback() {
        return this.mDialogCallback;
    }

    @VisibleForTesting
    public ILogcatManagerService getBinderService() {
        return this.mBinderService;
    }

    public final LogAccessClient getClientForRequest(LogAccessRequest logAccessRequest) {
        String packageName = getPackageName(logAccessRequest);
        if (packageName == null) {
            return null;
        }
        return new LogAccessClient(logAccessRequest.mUid, packageName);
    }

    public final String getPackageName(LogAccessRequest logAccessRequest) {
        PackageManager packageManager = this.mContext.getPackageManager();
        if (packageManager == null) {
            Slog.e("LogcatManagerService", "PackageManager is null, declining the logd access");
            return null;
        }
        String[] packagesForUid = packageManager.getPackagesForUid(logAccessRequest.mUid);
        if (ArrayUtils.isEmpty(packagesForUid)) {
            Slog.e("LogcatManagerService", "Unknown calling package name, declining the logd access");
            return null;
        }
        ActivityManagerInternal activityManagerInternal = this.mActivityManagerInternal;
        if (activityManagerInternal != null) {
            int i = logAccessRequest.mPid;
            String packageNameByPid = activityManagerInternal.getPackageNameByPid(i);
            while (true) {
                if ((packageNameByPid == null || !ArrayUtils.contains(packagesForUid, packageNameByPid)) && i != -1) {
                    i = Process.getParentPid(i);
                    packageNameByPid = this.mActivityManagerInternal.getPackageNameByPid(i);
                }
            }
            if (packageNameByPid != null && ArrayUtils.contains(packagesForUid, packageNameByPid)) {
                return packageNameByPid;
            }
        }
        Arrays.sort(packagesForUid);
        String str = packagesForUid[0];
        if (str == null || str.isEmpty()) {
            Slog.e("LogcatManagerService", "Unknown calling package name, declining the logd access");
            return null;
        }
        return str;
    }

    public void onLogAccessRequested(LogAccessRequest logAccessRequest) {
        LogAccessClient clientForRequest = getClientForRequest(logAccessRequest);
        if (clientForRequest == null) {
            declineRequest(logAccessRequest);
            return;
        }
        LogAccessStatus logAccessStatus = this.mLogAccessStatus.get(clientForRequest);
        if (logAccessStatus == null) {
            logAccessStatus = new LogAccessStatus();
            this.mLogAccessStatus.put(clientForRequest, logAccessStatus);
        }
        int i = logAccessStatus.mStatus;
        if (i == 0) {
            logAccessStatus.mPendingRequests.add(logAccessRequest);
            processNewLogAccessRequest(clientForRequest);
        } else if (i == 1) {
            logAccessStatus.mPendingRequests.add(logAccessRequest);
        } else if (i == 2) {
            approveRequest(clientForRequest, logAccessRequest);
        } else if (i != 3) {
        } else {
            declineRequest(logAccessRequest);
        }
    }

    public final boolean shouldShowConfirmationDialog(LogAccessClient logAccessClient) {
        return this.mActivityManagerInternal.getUidProcessState(logAccessClient.mUid) == 2;
    }

    public final void processNewLogAccessRequest(LogAccessClient logAccessClient) {
        if (this.mActivityManagerInternal.getInstrumentationSourceUid(logAccessClient.mUid) != -1) {
            onAccessApprovedForClient(logAccessClient);
        } else if (!shouldShowConfirmationDialog(logAccessClient)) {
            onAccessDeclinedForClient(logAccessClient);
        } else {
            this.mLogAccessStatus.get(logAccessClient).mStatus = 1;
            Handler handler = this.mHandler;
            handler.sendMessageAtTime(handler.obtainMessage(4, logAccessClient), this.mClock.get().longValue() + PENDING_CONFIRMATION_TIMEOUT_MILLIS);
            Intent createIntent = createIntent(logAccessClient);
            createIntent.setFlags(268435456);
            createIntent.setComponent(new ComponentName("com.android.systemui", "com.android.systemui.logcat.LogAccessDialogActivity"));
            this.mContext.startActivityAsUser(createIntent, UserHandle.SYSTEM);
        }
    }

    public void onAccessApprovedForClient(LogAccessClient logAccessClient) {
        scheduleStatusExpiry(logAccessClient);
        LogAccessStatus logAccessStatus = this.mLogAccessStatus.get(logAccessClient);
        if (logAccessStatus != null) {
            for (LogAccessRequest logAccessRequest : logAccessStatus.mPendingRequests) {
                approveRequest(logAccessClient, logAccessRequest);
            }
            logAccessStatus.mStatus = 2;
            logAccessStatus.mPendingRequests.clear();
        }
    }

    public void onAccessDeclinedForClient(LogAccessClient logAccessClient) {
        scheduleStatusExpiry(logAccessClient);
        LogAccessStatus logAccessStatus = this.mLogAccessStatus.get(logAccessClient);
        if (logAccessStatus != null) {
            for (LogAccessRequest logAccessRequest : logAccessStatus.mPendingRequests) {
                declineRequest(logAccessRequest);
            }
            logAccessStatus.mStatus = 3;
            logAccessStatus.mPendingRequests.clear();
        }
    }

    public final void scheduleStatusExpiry(LogAccessClient logAccessClient) {
        this.mHandler.removeMessages(4, logAccessClient);
        this.mHandler.removeMessages(5, logAccessClient);
        Handler handler = this.mHandler;
        handler.sendMessageAtTime(handler.obtainMessage(5, logAccessClient), this.mClock.get().longValue() + 60000);
    }

    public void onPendingTimeoutExpired(LogAccessClient logAccessClient) {
        LogAccessStatus logAccessStatus = this.mLogAccessStatus.get(logAccessClient);
        if (logAccessStatus == null || logAccessStatus.mStatus != 1) {
            return;
        }
        onAccessDeclinedForClient(logAccessClient);
    }

    public void onAccessStatusExpired(LogAccessClient logAccessClient) {
        this.mLogAccessStatus.remove(logAccessClient);
    }

    public void onLogAccessFinished(LogAccessRequest logAccessRequest) {
        LogAccessClient clientForRequest = getClientForRequest(logAccessRequest);
        int intValue = this.mActiveLogAccessCount.getOrDefault(clientForRequest, 1).intValue() - 1;
        if (intValue == 0) {
            this.mActiveLogAccessCount.remove(clientForRequest);
        } else {
            this.mActiveLogAccessCount.put(clientForRequest, Integer.valueOf(intValue));
        }
    }

    public final void approveRequest(LogAccessClient logAccessClient, LogAccessRequest logAccessRequest) {
        try {
            getLogdService().approve(logAccessRequest.mUid, logAccessRequest.mGid, logAccessRequest.mPid, logAccessRequest.mFd);
            this.mActiveLogAccessCount.put(logAccessClient, Integer.valueOf(this.mActiveLogAccessCount.getOrDefault(logAccessClient, 0).intValue() + 1));
        } catch (RemoteException e) {
            Slog.e("LogcatManagerService", "Fails to call remote functions", e);
        }
    }

    public final void declineRequest(LogAccessRequest logAccessRequest) {
        try {
            getLogdService().decline(logAccessRequest.mUid, logAccessRequest.mGid, logAccessRequest.mPid, logAccessRequest.mFd);
        } catch (RemoteException e) {
            Slog.e("LogcatManagerService", "Fails to call remote functions", e);
        }
    }

    public Intent createIntent(LogAccessClient logAccessClient) {
        Intent intent = new Intent();
        intent.setFlags(268468224);
        intent.putExtra("android.intent.extra.PACKAGE_NAME", logAccessClient.mPackageName);
        intent.putExtra("android.intent.extra.UID", logAccessClient.mUid);
        intent.putExtra("EXTRA_CALLBACK", this.mDialogCallback.asBinder());
        return intent;
    }
}
