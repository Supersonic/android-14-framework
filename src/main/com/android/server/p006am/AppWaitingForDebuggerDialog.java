package com.android.server.p006am;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
/* renamed from: com.android.server.am.AppWaitingForDebuggerDialog */
/* loaded from: classes.dex */
public final class AppWaitingForDebuggerDialog extends BaseErrorDialog {
    public CharSequence mAppName;
    public final Handler mHandler;
    public final ProcessRecord mProc;
    public final ActivityManagerService mService;

    @Override // com.android.server.p006am.BaseErrorDialog
    public void closeDialog() {
    }

    public AppWaitingForDebuggerDialog(ActivityManagerService activityManagerService, Context context, ProcessRecord processRecord) {
        super(context);
        Handler handler = new Handler() { // from class: com.android.server.am.AppWaitingForDebuggerDialog.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what != 1) {
                    return;
                }
                AppWaitingForDebuggerDialog appWaitingForDebuggerDialog = AppWaitingForDebuggerDialog.this;
                appWaitingForDebuggerDialog.mService.killAppAtUsersRequest(appWaitingForDebuggerDialog.mProc);
            }
        };
        this.mHandler = handler;
        this.mService = activityManagerService;
        this.mProc = processRecord;
        this.mAppName = context.getPackageManager().getApplicationLabel(processRecord.info);
        setCancelable(false);
        StringBuilder sb = new StringBuilder();
        CharSequence charSequence = this.mAppName;
        if (charSequence != null && charSequence.length() > 0) {
            sb.append("Application ");
            sb.append(this.mAppName);
            sb.append(" (process ");
            sb.append(processRecord.processName);
            sb.append(")");
        } else {
            sb.append("Process ");
            sb.append(processRecord.processName);
        }
        sb.append(" is waiting for the debugger to attach.");
        setMessage(sb.toString());
        setButton(-1, "Force Close", handler.obtainMessage(1, processRecord));
        setTitle("Waiting For Debugger");
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.setTitle("Waiting For Debugger: " + processRecord.info.processName);
        getWindow().setAttributes(attributes);
    }
}
