package com.android.server.p006am;

import android.app.ActivityManagerInternal;
import android.app.BroadcastOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.util.ProgressReporter;
import com.android.server.LocalServices;
import com.android.server.UiThread;
import java.util.List;
/* renamed from: com.android.server.am.PreBootBroadcaster */
/* loaded from: classes.dex */
public abstract class PreBootBroadcaster extends IIntentReceiver.Stub {
    public final Intent mIntent;
    public final ProgressReporter mProgress;
    public final boolean mQuiet;
    public final ActivityManagerService mService;
    public final List<ResolveInfo> mTargets;
    public final int mUserId;
    public int mIndex = 0;
    public Handler mHandler = new Handler(UiThread.get().getLooper(), null, true) { // from class: com.android.server.am.PreBootBroadcaster.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Context context = PreBootBroadcaster.this.mService.mContext;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
            int i = message.arg1;
            int i2 = message.arg2;
            int i3 = message.what;
            if (i3 != 1) {
                if (i3 != 2) {
                    return;
                }
                notificationManager.cancelAsUser("PreBootBroadcaster", 13, UserHandle.of(PreBootBroadcaster.this.mUserId));
                return;
            }
            CharSequence text = context.getText(17039667);
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.HelpTrampoline");
            intent.putExtra("android.intent.extra.TEXT", "help_url_upgrading");
            notificationManager.notifyAsUser("PreBootBroadcaster", 13, new Notification.Builder(PreBootBroadcaster.this.mService.mContext, SystemNotificationChannels.UPDATES).setSmallIcon(17303596).setWhen(0L).setOngoing(true).setTicker(text).setColor(context.getColor(17170460)).setContentTitle(text).setContentIntent(context.getPackageManager().resolveActivity(intent, 0) != null ? PendingIntent.getActivity(context, 0, intent, 67108864) : null).setVisibility(1).setProgress(i, i2, false).build(), UserHandle.of(PreBootBroadcaster.this.mUserId));
        }
    };

    public abstract void onFinished();

    public PreBootBroadcaster(ActivityManagerService activityManagerService, int i, ProgressReporter progressReporter, boolean z) {
        this.mService = activityManagerService;
        this.mUserId = i;
        this.mProgress = progressReporter;
        this.mQuiet = z;
        Intent intent = new Intent("android.intent.action.PRE_BOOT_COMPLETED");
        this.mIntent = intent;
        intent.addFlags(33554688);
        this.mTargets = activityManagerService.mContext.getPackageManager().queryBroadcastReceiversAsUser(intent, 1048576, UserHandle.of(i));
    }

    public void sendNext() {
        if (this.mIndex >= this.mTargets.size()) {
            this.mHandler.obtainMessage(2).sendToTarget();
            onFinished();
        } else if (!this.mService.isUserRunning(this.mUserId, 0)) {
            Slog.i("PreBootBroadcaster", "User " + this.mUserId + " is no longer running; skipping remaining receivers");
            this.mHandler.obtainMessage(2).sendToTarget();
            onFinished();
        } else {
            if (!this.mQuiet) {
                this.mHandler.obtainMessage(1, this.mTargets.size(), this.mIndex).sendToTarget();
            }
            List<ResolveInfo> list = this.mTargets;
            int i = this.mIndex;
            this.mIndex = i + 1;
            ResolveInfo resolveInfo = list.get(i);
            ComponentName componentName = resolveInfo.activityInfo.getComponentName();
            if (this.mProgress != null) {
                this.mProgress.setProgress(this.mIndex, this.mTargets.size(), this.mService.mContext.getString(17039663, resolveInfo.activityInfo.loadLabel(this.mService.mContext.getPackageManager())));
            }
            Slog.i("PreBootBroadcaster", "Pre-boot of " + componentName.toShortString() + " for user " + this.mUserId);
            EventLogTags.writeAmPreBoot(this.mUserId, componentName.getPackageName());
            this.mIntent.setComponent(componentName);
            ActivityManagerInternal activityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
            long bootTimeTempAllowListDuration = activityManagerInternal != null ? activityManagerInternal.getBootTimeTempAllowListDuration() : 10000L;
            BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
            makeBasic.setTemporaryAppAllowlist(bootTimeTempAllowListDuration, 0, 201, "");
            synchronized (this.mService) {
                try {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        this.mService.broadcastIntentLocked(null, null, null, this.mIntent, null, this, 0, null, null, null, null, null, -1, makeBasic.toBundle(), true, false, ActivityManagerService.MY_PID, 1000, Binder.getCallingUid(), Binder.getCallingPid(), this.mUserId);
                        ActivityManagerService.resetPriorityAfterLockedSection();
                    } catch (Throwable th) {
                        th = th;
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            }
        }
    }

    public void performReceive(Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2, int i2) {
        sendNext();
    }
}
