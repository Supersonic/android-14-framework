package com.android.server.p006am;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.BidiFormatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.server.backup.BackupAgentTimeoutParameters;
/* renamed from: com.android.server.am.AppErrorDialog */
/* loaded from: classes.dex */
public final class AppErrorDialog extends BaseErrorDialog implements View.OnClickListener {
    public static int ALREADY_SHOWING = -3;
    public static int BACKGROUND_USER = -2;
    public static int CANT_SHOW = -1;
    public final Handler mHandler;
    public final boolean mIsRestartable;
    public final ProcessRecord mProc;
    public final ActivityManagerGlobalLock mProcLock;
    public final AppErrorResult mResult;
    public final ActivityManagerService mService;

    /* renamed from: com.android.server.am.AppErrorDialog$Data */
    /* loaded from: classes.dex */
    public static class Data {
        public boolean isRestartableForService;
        public ProcessRecord proc;
        public boolean repeating;
        public AppErrorResult result;
        public int taskId;
    }

    public AppErrorDialog(Context context, ActivityManagerService activityManagerService, Data data) {
        super(context);
        CharSequence applicationLabel;
        Handler handler = new Handler() { // from class: com.android.server.am.AppErrorDialog.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                AppErrorDialog.this.setResult(message.what);
                AppErrorDialog.this.dismiss();
            }
        };
        this.mHandler = handler;
        Resources resources = context.getResources();
        this.mService = activityManagerService;
        this.mProcLock = activityManagerService.mProcLock;
        ProcessRecord processRecord = data.proc;
        this.mProc = processRecord;
        this.mResult = data.result;
        boolean z = false;
        if ((data.taskId != -1 || data.isRestartableForService) && Settings.Global.getInt(context.getContentResolver(), "show_restart_in_crash_dialog", 0) != 0) {
            z = true;
        }
        this.mIsRestartable = z;
        BidiFormatter bidiFormatter = BidiFormatter.getInstance();
        if (processRecord.getPkgList().size() == 1 && (applicationLabel = context.getPackageManager().getApplicationLabel(processRecord.info)) != null) {
            setTitle(resources.getString(data.repeating ? 17039646 : 17039645, bidiFormatter.unicodeWrap(applicationLabel.toString()), bidiFormatter.unicodeWrap(processRecord.info.processName)));
        } else {
            setTitle(resources.getString(data.repeating ? 17039651 : 17039650, bidiFormatter.unicodeWrap(processRecord.processName.toString())));
        }
        setCancelable(true);
        setCancelMessage(handler.obtainMessage(7));
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.setTitle("Application Error: " + processRecord.info.processName);
        attributes.privateFlags = attributes.privateFlags | 272;
        getWindow().setAttributes(attributes);
        if (processRecord.isPersistent()) {
            getWindow().setType(2010);
        }
        handler.sendMessageDelayed(handler.obtainMessage(6), BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
    }

    @Override // android.app.AlertDialog, android.app.Dialog
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        boolean z = true;
        LayoutInflater.from(context).inflate(17367096, (ViewGroup) ((FrameLayout) findViewById(16908331)), true);
        boolean z2 = this.mProc.mErrorState.getErrorReportReceiver() != null;
        TextView textView = (TextView) findViewById(16908758);
        textView.setOnClickListener(this);
        textView.setVisibility(this.mIsRestartable ? 0 : 8);
        TextView textView2 = (TextView) findViewById(16908757);
        textView2.setOnClickListener(this);
        textView2.setVisibility(z2 ? 0 : 8);
        ((TextView) findViewById(16908755)).setOnClickListener(this);
        ((TextView) findViewById(16908754)).setOnClickListener(this);
        if (Build.IS_USER || Settings.Global.getInt(context.getContentResolver(), "development_settings_enabled", 0) == 0 || Settings.Global.getInt(context.getContentResolver(), "show_mute_in_crash_dialog", 0) == 0) {
            z = false;
        }
        TextView textView3 = (TextView) findViewById(16908756);
        textView3.setOnClickListener(this);
        textView3.setVisibility(z ? 0 : 8);
        findViewById(16908937).setVisibility(0);
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        if (!this.mResult.mHasResult) {
            setResult(1);
        }
        super.dismiss();
    }

    public final void setResult(int i) {
        synchronized (this.mProcLock) {
            try {
                ActivityManagerService.boostPriorityForProcLockedSection();
                ProcessRecord processRecord = this.mProc;
                if (processRecord != null) {
                    processRecord.mErrorState.getDialogController().clearCrashDialogs(false);
                }
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterProcLockedSection();
        this.mResult.set(i);
        this.mHandler.removeMessages(6);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case 16908754:
                this.mHandler.obtainMessage(8).sendToTarget();
                return;
            case 16908755:
                this.mHandler.obtainMessage(1).sendToTarget();
                return;
            case 16908756:
                this.mHandler.obtainMessage(5).sendToTarget();
                return;
            case 16908757:
                this.mHandler.obtainMessage(2).sendToTarget();
                return;
            case 16908758:
                this.mHandler.obtainMessage(3).sendToTarget();
                return;
            default:
                return;
        }
    }
}
