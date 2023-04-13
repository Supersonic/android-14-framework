package com.android.internal.util;

import android.app.AlarmManager;
import android.content.Context;
import android.p008os.Handler;
import android.p008os.Message;
/* loaded from: classes3.dex */
public class WakeupMessage implements AlarmManager.OnAlarmListener {
    private final AlarmManager mAlarmManager;
    protected final int mArg1;
    protected final int mArg2;
    protected final int mCmd;
    protected final String mCmdName;
    protected final Handler mHandler;
    protected final Object mObj;
    private final Runnable mRunnable;
    private boolean mScheduled;

    public WakeupMessage(Context context, Handler handler, String cmdName, int cmd, int arg1, int arg2, Object obj) {
        this.mAlarmManager = getAlarmManager(context);
        this.mHandler = handler;
        this.mCmdName = cmdName;
        this.mCmd = cmd;
        this.mArg1 = arg1;
        this.mArg2 = arg2;
        this.mObj = obj;
        this.mRunnable = null;
    }

    public WakeupMessage(Context context, Handler handler, String cmdName, int cmd, int arg1) {
        this(context, handler, cmdName, cmd, arg1, 0, null);
    }

    public WakeupMessage(Context context, Handler handler, String cmdName, int cmd, int arg1, int arg2) {
        this(context, handler, cmdName, cmd, arg1, arg2, null);
    }

    public WakeupMessage(Context context, Handler handler, String cmdName, int cmd) {
        this(context, handler, cmdName, cmd, 0, 0, null);
    }

    public WakeupMessage(Context context, Handler handler, String cmdName, Runnable runnable) {
        this.mAlarmManager = getAlarmManager(context);
        this.mHandler = handler;
        this.mCmdName = cmdName;
        this.mCmd = 0;
        this.mArg1 = 0;
        this.mArg2 = 0;
        this.mObj = null;
        this.mRunnable = runnable;
    }

    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService("alarm");
    }

    public synchronized void schedule(long when) {
        this.mAlarmManager.setExact(2, when, this.mCmdName, this, this.mHandler);
        this.mScheduled = true;
    }

    public synchronized void cancel() {
        if (this.mScheduled) {
            this.mAlarmManager.cancel(this);
            this.mScheduled = false;
        }
    }

    @Override // android.app.AlarmManager.OnAlarmListener
    public void onAlarm() {
        boolean stillScheduled;
        Message msg;
        synchronized (this) {
            stillScheduled = this.mScheduled;
            this.mScheduled = false;
        }
        if (stillScheduled) {
            Runnable runnable = this.mRunnable;
            if (runnable == null) {
                msg = this.mHandler.obtainMessage(this.mCmd, this.mArg1, this.mArg2, this.mObj);
            } else {
                msg = Message.obtain(this.mHandler, runnable);
            }
            this.mHandler.dispatchMessage(msg);
            msg.recycle();
        }
    }
}
