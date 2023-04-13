package com.android.service.ims.presence;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import com.android.ims.internal.Logger;
import com.android.service.ims.TaskManager;
/* loaded from: classes.dex */
public class PresenceCapabilityTask extends PresenceTask {
    public static final String ACTION_TASK_TIMEOUT_ALARM = "com.android.service.ims.presence.task.timeout";
    static AlarmManager sAlarmManager = null;
    private Logger logger;
    PendingIntent mAlarmIntent;
    private Context mContext;
    private long mCreatedTimeStamp;
    public int mResultCode;
    private long mTimeout;
    boolean mTimerStarted;
    public boolean mWaitingForNotify;

    public PresenceCapabilityTask(Context context, int taskId, int cmdId, ContactCapabilityResponse listener, String[] contacts, long timeout) {
        super(taskId, cmdId, listener, contacts);
        this.logger = Logger.getLogger(getClass().getName());
        this.mContext = null;
        this.mAlarmIntent = null;
        this.mTimerStarted = false;
        this.mContext = context;
        this.mWaitingForNotify = false;
        this.mCreatedTimeStamp = System.currentTimeMillis();
        this.mTimeout = timeout;
        if (timeout <= 0) {
            this.mTimeout = 36000L;
        }
        if (listener != null) {
            startTimer();
        }
    }

    @Override // com.android.service.ims.presence.PresenceTask, com.android.service.ims.Task
    public String toString() {
        return super.toString() + " mCreatedTimeStamp=" + this.mCreatedTimeStamp + " mTimeout=" + this.mTimeout;
    }

    private void startTimer() {
        if (this.mContext == null) {
            this.logger.error("startTimer mContext is null");
            return;
        }
        Intent intent = new Intent(ACTION_TASK_TIMEOUT_ALARM);
        intent.setPackage(this.mContext.getPackageName());
        intent.putExtra("taskId", this.mTaskId);
        PendingIntent mAlarmIntent = PendingIntent.getBroadcast(this.mContext, 0, intent, 1140850688);
        if (sAlarmManager == null) {
            sAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        }
        long triggerAt = SystemClock.elapsedRealtime() + this.mTimeout;
        this.logger.debug("startTimer taskId=" + this.mTaskId + " mTimeout=" + this.mTimeout + " triggerAt=" + triggerAt + " mAlarmIntent=" + mAlarmIntent);
        sAlarmManager.setExact(2, triggerAt, mAlarmIntent);
        this.mTimerStarted = true;
    }

    public void cancelTimer() {
        AlarmManager alarmManager;
        if (this.mTimerStarted) {
            this.logger.debug("cancelTimer, taskId=" + this.mTaskId);
            PendingIntent pendingIntent = this.mAlarmIntent;
            if (pendingIntent != null && (alarmManager = sAlarmManager) != null) {
                alarmManager.cancel(pendingIntent);
            }
            this.mTimerStarted = false;
        }
    }

    public void onTimeout() {
        this.logger.debug("onTimeout, taskId=" + this.mTaskId);
        if (this.mListener != null) {
            this.mListener.onTimeout(this.mTaskId);
        }
        TaskManager.getDefault().removeTask(this.mTaskId);
    }

    public void setWaitingForNotify(boolean waitingForNotify) {
        this.mWaitingForNotify = waitingForNotify;
    }

    public boolean isWaitingForNotify() {
        return this.mWaitingForNotify;
    }

    public void onTerminated(String reason) {
        if (!this.mWaitingForNotify) {
            this.logger.debug("onTerminated mWaitingForNotify is false. task=" + this);
            return;
        }
        cancelTimer();
        if (this.mListener != null) {
            this.mListener.onFinish(this.mTaskId);
        }
        TaskManager.getDefault().removeTask(this.mTaskId);
    }
}
