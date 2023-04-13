package com.android.server.job.controllers.idle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.server.AppSchedulingModuleThread;
import com.android.server.job.JobSchedulerService;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class CarIdlenessTracker extends BroadcastReceiver implements IdlenessTracker {
    public static final boolean DEBUG;
    public IdlenessListener mIdleListener;
    public boolean mIdle = false;
    public boolean mGarageModeOn = false;
    public boolean mForced = false;
    public boolean mScreenOn = true;

    static {
        DEBUG = JobSchedulerService.DEBUG || Log.isLoggable("JobScheduler.CarIdlenessTracker", 3);
    }

    @Override // com.android.server.job.controllers.idle.IdlenessTracker
    public boolean isIdle() {
        return this.mIdle;
    }

    @Override // com.android.server.job.controllers.idle.IdlenessTracker
    public void startTracking(Context context, IdlenessListener idlenessListener) {
        this.mIdleListener = idlenessListener;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("com.android.server.jobscheduler.GARAGE_MODE_ON");
        intentFilter.addAction("com.android.server.jobscheduler.GARAGE_MODE_OFF");
        intentFilter.addAction("com.android.server.jobscheduler.FORCE_IDLE");
        intentFilter.addAction("com.android.server.jobscheduler.UNFORCE_IDLE");
        intentFilter.addAction("com.android.server.ACTION_TRIGGER_IDLE");
        context.registerReceiver(this, intentFilter, null, AppSchedulingModuleThread.getHandler());
    }

    @Override // com.android.server.job.controllers.idle.IdlenessTracker
    public void dump(PrintWriter printWriter) {
        printWriter.print("  mIdle: ");
        printWriter.println(this.mIdle);
        printWriter.print("  mGarageModeOn: ");
        printWriter.println(this.mGarageModeOn);
        printWriter.print("  mForced: ");
        printWriter.println(this.mForced);
        printWriter.print("  mScreenOn: ");
        printWriter.println(this.mScreenOn);
    }

    @Override // com.android.server.job.controllers.idle.IdlenessTracker
    public void dump(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        long start2 = protoOutputStream.start(1146756268034L);
        protoOutputStream.write(1133871366145L, this.mIdle);
        protoOutputStream.write(1133871366146L, this.mGarageModeOn);
        protoOutputStream.end(start2);
        protoOutputStream.end(start);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        logIfDebug("Received action: " + action);
        if (action.equals("com.android.server.jobscheduler.FORCE_IDLE")) {
            logIfDebug("Forcing idle...");
            setForceIdleState(true);
        } else if (action.equals("com.android.server.jobscheduler.UNFORCE_IDLE")) {
            logIfDebug("Unforcing idle...");
            setForceIdleState(false);
        } else if (action.equals("android.intent.action.SCREEN_ON")) {
            logIfDebug("Screen is on...");
            handleScreenOn();
        } else if (action.equals("android.intent.action.SCREEN_OFF")) {
            logIfDebug("Screen is off...");
            this.mScreenOn = false;
        } else if (action.equals("com.android.server.jobscheduler.GARAGE_MODE_ON")) {
            logIfDebug("GarageMode is on...");
            this.mGarageModeOn = true;
            updateIdlenessState();
        } else if (action.equals("com.android.server.jobscheduler.GARAGE_MODE_OFF")) {
            logIfDebug("GarageMode is off...");
            this.mGarageModeOn = false;
            updateIdlenessState();
        } else if (action.equals("com.android.server.ACTION_TRIGGER_IDLE")) {
            if (!this.mGarageModeOn) {
                logIfDebug("Idle trigger fired...");
                triggerIdleness();
                return;
            }
            logIfDebug("TRIGGER_IDLE received but not changing state; mIdle=" + this.mIdle + " mGarageModeOn=" + this.mGarageModeOn);
        }
    }

    public final void setForceIdleState(boolean z) {
        this.mForced = z;
        updateIdlenessState();
    }

    public final void updateIdlenessState() {
        boolean z = this.mForced || this.mGarageModeOn;
        if (this.mIdle != z) {
            logIfDebug("Device idleness changed. New idle=" + z);
            this.mIdle = z;
            this.mIdleListener.reportNewIdleState(z);
            return;
        }
        logIfDebug("Device idleness is the same. Current idle=" + z);
    }

    public final void triggerIdleness() {
        if (this.mIdle) {
            logIfDebug("Device is already idle");
        } else if (!this.mScreenOn) {
            logIfDebug("Device is going idle");
            this.mIdle = true;
            this.mIdleListener.reportNewIdleState(true);
        } else {
            logIfDebug("TRIGGER_IDLE received but not changing state: mIdle = " + this.mIdle + ", mScreenOn = " + this.mScreenOn);
        }
    }

    public final void handleScreenOn() {
        this.mScreenOn = true;
        if (this.mForced || this.mGarageModeOn) {
            logIfDebug("Screen is on, but device cannot exit idle");
        } else if (this.mIdle) {
            logIfDebug("Device is exiting idle");
            this.mIdle = false;
            this.mIdleListener.reportNewIdleState(false);
        } else {
            logIfDebug("Device is already non-idle");
        }
    }

    public static void logIfDebug(String str) {
        if (DEBUG) {
            Slog.v("JobScheduler.CarIdlenessTracker", str);
        }
    }
}
