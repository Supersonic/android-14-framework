package com.android.server.dreams;

import android.app.ActivityTaskManager;
import android.app.BroadcastOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.service.dreams.IDreamService;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.dreams.DreamController;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
/* loaded from: classes.dex */
public final class DreamController {
    public final ActivityTaskManager mActivityTaskManager;
    public final Intent mCloseNotificationShadeIntent;
    public final Context mContext;
    public DreamRecord mCurrentDream;
    public final Handler mHandler;
    public final Listener mListener;
    public static final String DREAMING_DELIVERY_GROUP_NAMESPACE = UUID.randomUUID().toString();
    public static final String DREAMING_DELIVERY_GROUP_KEY = UUID.randomUUID().toString();
    public final Intent mDreamingStartedIntent = new Intent("android.intent.action.DREAMING_STARTED").addFlags(1073741824);
    public final Intent mDreamingStoppedIntent = new Intent("android.intent.action.DREAMING_STOPPED").addFlags(1073741824);
    public final Bundle mDreamingStartedStoppedOptions = createDreamingStartedStoppedOptions();
    public boolean mSentStartBroadcast = false;
    public final ArrayList<DreamRecord> mPreviousDreams = new ArrayList<>();

    /* loaded from: classes.dex */
    public interface Listener {
        void onDreamStopped(Binder binder);
    }

    public DreamController(Context context, Handler handler, Listener listener) {
        this.mContext = context;
        this.mHandler = handler;
        this.mListener = listener;
        this.mActivityTaskManager = (ActivityTaskManager) context.getSystemService(ActivityTaskManager.class);
        Intent intent = new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        this.mCloseNotificationShadeIntent = intent;
        intent.putExtra("reason", "dream");
    }

    public final Bundle createDreamingStartedStoppedOptions() {
        BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
        makeBasic.setDeliveryGroupPolicy(1);
        makeBasic.setDeliveryGroupMatchingKey(DREAMING_DELIVERY_GROUP_NAMESPACE, DREAMING_DELIVERY_GROUP_KEY);
        makeBasic.setDeferUntilActive(true);
        return makeBasic.toBundle();
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("Dreamland:");
        if (this.mCurrentDream != null) {
            printWriter.println("  mCurrentDream:");
            printWriter.println("    mToken=" + this.mCurrentDream.mToken);
            printWriter.println("    mName=" + this.mCurrentDream.mName);
            printWriter.println("    mIsPreviewMode=" + this.mCurrentDream.mIsPreviewMode);
            printWriter.println("    mCanDoze=" + this.mCurrentDream.mCanDoze);
            printWriter.println("    mUserId=" + this.mCurrentDream.mUserId);
            printWriter.println("    mBound=" + this.mCurrentDream.mBound);
            printWriter.println("    mService=" + this.mCurrentDream.mService);
            printWriter.println("    mWakingGently=" + this.mCurrentDream.mWakingGently);
        } else {
            printWriter.println("  mCurrentDream: null");
        }
        printWriter.println("  mSentStartBroadcast=" + this.mSentStartBroadcast);
    }

    public void startDream(Binder binder, ComponentName componentName, boolean z, boolean z2, int i, PowerManager.WakeLock wakeLock, ComponentName componentName2, String str) {
        long j;
        Trace.traceBegin(131072L, "startDream");
        try {
            this.mContext.sendBroadcastAsUser(this.mCloseNotificationShadeIntent, UserHandle.ALL);
            Slog.i("DreamController", "Starting dream: name=" + componentName + ", isPreviewMode=" + z + ", canDoze=" + z2 + ", userId=" + i + ", reason='" + str + "'");
            DreamRecord dreamRecord = this.mCurrentDream;
            try {
                DreamRecord dreamRecord2 = new DreamRecord(binder, componentName, z, z2, i, wakeLock);
                this.mCurrentDream = dreamRecord2;
                if (dreamRecord != null) {
                    if (Objects.equals(dreamRecord.mName, dreamRecord2.mName)) {
                        stopDreamInstance(true, "restarting same dream", dreamRecord);
                    } else {
                        this.mPreviousDreams.add(dreamRecord);
                    }
                }
                this.mCurrentDream.mDreamStartTime = SystemClock.elapsedRealtime();
                MetricsLogger.visible(this.mContext, this.mCurrentDream.mCanDoze ? FrameworkStatsLog.EXCLUSION_RECT_STATE_CHANGED : 222);
                Intent intent = new Intent("android.service.dreams.DreamService");
                intent.setComponent(componentName);
                intent.addFlags(8388608);
                intent.putExtra("android.service.dream.DreamService.dream_overlay_component", componentName2);
                try {
                    if (!this.mContext.bindServiceAsUser(intent, this.mCurrentDream, 67108865, new UserHandle(i))) {
                        Slog.e("DreamController", "Unable to bind dream service: " + intent);
                        stopDream(true, "bindService failed");
                        Trace.traceEnd(131072L);
                        return;
                    }
                    DreamRecord dreamRecord3 = this.mCurrentDream;
                    dreamRecord3.mBound = true;
                    this.mHandler.postDelayed(dreamRecord3.mStopUnconnectedDreamRunnable, 5000L);
                    Trace.traceEnd(131072L);
                } catch (SecurityException e) {
                    Slog.e("DreamController", "Unable to bind dream service: " + intent, e);
                    stopDream(true, "unable to bind service: SecExp.");
                    Trace.traceEnd(131072L);
                }
            } catch (Throwable th) {
                th = th;
                j = 131072;
                Trace.traceEnd(j);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            j = 131072;
        }
    }

    public void stopDream(boolean z, String str) {
        stopPreviousDreams();
        stopDreamInstance(z, str, this.mCurrentDream);
    }

    public final void stopDreamInstance(boolean z, String str, DreamRecord dreamRecord) {
        String str2;
        if (dreamRecord == null) {
            return;
        }
        Trace.traceBegin(131072L, "stopDream");
        if (!z) {
            try {
                if (dreamRecord.mWakingGently) {
                    return;
                }
                if (dreamRecord.mService != null) {
                    dreamRecord.mWakingGently = true;
                    try {
                        dreamRecord.mStopReason = str;
                        dreamRecord.mService.wakeUp();
                        this.mHandler.postDelayed(dreamRecord.mStopStubbornDreamRunnable, 5000L);
                        return;
                    } catch (RemoteException unused) {
                    }
                }
            } finally {
                Trace.traceEnd(131072L);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Stopping dream: name=");
        sb.append(dreamRecord.mName);
        sb.append(", isPreviewMode=");
        sb.append(dreamRecord.mIsPreviewMode);
        sb.append(", canDoze=");
        sb.append(dreamRecord.mCanDoze);
        sb.append(", userId=");
        sb.append(dreamRecord.mUserId);
        sb.append(", reason='");
        sb.append(str);
        sb.append("'");
        if (dreamRecord.mStopReason == null) {
            str2 = "";
        } else {
            str2 = "(from '" + dreamRecord.mStopReason + "')";
        }
        sb.append(str2);
        Slog.i("DreamController", sb.toString());
        MetricsLogger.hidden(this.mContext, dreamRecord.mCanDoze ? FrameworkStatsLog.EXCLUSION_RECT_STATE_CHANGED : 222);
        MetricsLogger.histogram(this.mContext, dreamRecord.mCanDoze ? "dozing_minutes" : "dreaming_minutes", (int) ((SystemClock.elapsedRealtime() - dreamRecord.mDreamStartTime) / 60000));
        this.mHandler.removeCallbacks(dreamRecord.mStopUnconnectedDreamRunnable);
        this.mHandler.removeCallbacks(dreamRecord.mStopStubbornDreamRunnable);
        IDreamService iDreamService = dreamRecord.mService;
        if (iDreamService != null) {
            try {
                iDreamService.detach();
            } catch (RemoteException unused2) {
            }
            try {
                dreamRecord.mService.asBinder().unlinkToDeath(dreamRecord, 0);
            } catch (NoSuchElementException unused3) {
            }
            dreamRecord.mService = null;
        }
        if (dreamRecord.mBound) {
            this.mContext.unbindService(dreamRecord);
        }
        dreamRecord.releaseWakeLockIfNeeded();
        if (dreamRecord == this.mCurrentDream) {
            this.mCurrentDream = null;
            if (this.mSentStartBroadcast) {
                this.mContext.sendBroadcastAsUser(this.mDreamingStoppedIntent, UserHandle.ALL, null, this.mDreamingStartedStoppedOptions);
                this.mSentStartBroadcast = false;
            }
            this.mActivityTaskManager.removeRootTasksWithActivityTypes(new int[]{5});
            this.mListener.onDreamStopped(dreamRecord.mToken);
        }
    }

    public final void stopPreviousDreams() {
        if (this.mPreviousDreams.isEmpty()) {
            return;
        }
        Iterator<DreamRecord> it = this.mPreviousDreams.iterator();
        while (it.hasNext()) {
            stopDreamInstance(true, "stop previous dream", it.next());
            it.remove();
        }
    }

    public final void attach(IDreamService iDreamService) {
        try {
            iDreamService.asBinder().linkToDeath(this.mCurrentDream, 0);
            DreamRecord dreamRecord = this.mCurrentDream;
            iDreamService.attach(dreamRecord.mToken, dreamRecord.mCanDoze, dreamRecord.mIsPreviewMode, dreamRecord.mDreamingStartedCallback);
            DreamRecord dreamRecord2 = this.mCurrentDream;
            dreamRecord2.mService = iDreamService;
            if (dreamRecord2.mIsPreviewMode || this.mSentStartBroadcast) {
                return;
            }
            this.mContext.sendBroadcastAsUser(this.mDreamingStartedIntent, UserHandle.ALL, null, this.mDreamingStartedStoppedOptions);
            this.mSentStartBroadcast = true;
        } catch (RemoteException e) {
            Slog.e("DreamController", "The dream service died unexpectedly.", e);
            stopDream(true, "attach failed");
        }
    }

    /* loaded from: classes.dex */
    public final class DreamRecord implements IBinder.DeathRecipient, ServiceConnection {
        public boolean mBound;
        public final boolean mCanDoze;
        public boolean mConnected;
        public long mDreamStartTime;
        public final IRemoteCallback mDreamingStartedCallback;
        public final boolean mIsPreviewMode;
        public final ComponentName mName;
        public final Runnable mReleaseWakeLockIfNeeded;
        public IDreamService mService;
        public final Runnable mStopPreviousDreamsIfNeeded = new Runnable() { // from class: com.android.server.dreams.DreamController$DreamRecord$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                DreamController.DreamRecord.this.stopPreviousDreamsIfNeeded();
            }
        };
        public String mStopReason;
        public final Runnable mStopStubbornDreamRunnable;
        public final Runnable mStopUnconnectedDreamRunnable;
        public final Binder mToken;
        public final int mUserId;
        public PowerManager.WakeLock mWakeLock;
        public boolean mWakingGently;

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            if (!this.mBound || this.mConnected) {
                return;
            }
            Slog.w("DreamController", "Bound dream did not connect in the time allotted");
            DreamController.this.stopDream(true, "slow to connect");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1() {
            Slog.w("DreamController", "Stubborn dream did not finish itself in the time allotted");
            DreamController.this.stopDream(true, "slow to finish");
            this.mStopReason = null;
        }

        public DreamRecord(Binder binder, ComponentName componentName, boolean z, boolean z2, int i, PowerManager.WakeLock wakeLock) {
            Runnable runnable = new Runnable() { // from class: com.android.server.dreams.DreamController$DreamRecord$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    DreamController.DreamRecord.this.releaseWakeLockIfNeeded();
                }
            };
            this.mReleaseWakeLockIfNeeded = runnable;
            this.mStopUnconnectedDreamRunnable = new Runnable() { // from class: com.android.server.dreams.DreamController$DreamRecord$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    DreamController.DreamRecord.this.lambda$new$0();
                }
            };
            this.mStopStubbornDreamRunnable = new Runnable() { // from class: com.android.server.dreams.DreamController$DreamRecord$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    DreamController.DreamRecord.this.lambda$new$1();
                }
            };
            this.mDreamingStartedCallback = new IRemoteCallback.Stub() { // from class: com.android.server.dreams.DreamController.DreamRecord.1
                public void sendResult(Bundle bundle) {
                    DreamController.this.mHandler.post(DreamRecord.this.mStopPreviousDreamsIfNeeded);
                    DreamController.this.mHandler.post(DreamRecord.this.mReleaseWakeLockIfNeeded);
                }
            };
            this.mToken = binder;
            this.mName = componentName;
            this.mIsPreviewMode = z;
            this.mCanDoze = z2;
            this.mUserId = i;
            this.mWakeLock = wakeLock;
            if (wakeLock != null) {
                wakeLock.acquire();
            }
            DreamController.this.mHandler.postDelayed(runnable, 10000L);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            DreamController.this.mHandler.post(new Runnable() { // from class: com.android.server.dreams.DreamController$DreamRecord$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DreamController.DreamRecord.this.lambda$binderDied$2();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$binderDied$2() {
            this.mService = null;
            if (DreamController.this.mCurrentDream == this) {
                DreamController.this.stopDream(true, "binder died");
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, final IBinder iBinder) {
            DreamController.this.mHandler.post(new Runnable() { // from class: com.android.server.dreams.DreamController$DreamRecord$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    DreamController.DreamRecord.this.lambda$onServiceConnected$3(iBinder);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onServiceConnected$3(IBinder iBinder) {
            this.mConnected = true;
            if (DreamController.this.mCurrentDream == this && this.mService == null) {
                DreamController.this.attach(IDreamService.Stub.asInterface(iBinder));
            } else {
                releaseWakeLockIfNeeded();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            DreamController.this.mHandler.post(new Runnable() { // from class: com.android.server.dreams.DreamController$DreamRecord$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DreamController.DreamRecord.this.lambda$onServiceDisconnected$4();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onServiceDisconnected$4() {
            this.mService = null;
            if (DreamController.this.mCurrentDream == this) {
                DreamController.this.stopDream(true, "service disconnected");
            }
        }

        public void stopPreviousDreamsIfNeeded() {
            if (DreamController.this.mCurrentDream == this) {
                DreamController.this.stopPreviousDreams();
            }
        }

        public void releaseWakeLockIfNeeded() {
            PowerManager.WakeLock wakeLock = this.mWakeLock;
            if (wakeLock != null) {
                wakeLock.release();
                this.mWakeLock = null;
                DreamController.this.mHandler.removeCallbacks(this.mReleaseWakeLockIfNeeded);
            }
        }
    }
}
