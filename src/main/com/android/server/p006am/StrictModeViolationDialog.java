package com.android.server.p006am;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
/* renamed from: com.android.server.am.StrictModeViolationDialog */
/* loaded from: classes.dex */
public final class StrictModeViolationDialog extends BaseErrorDialog {
    public final Handler mHandler;
    public final ProcessRecord mProc;
    public final AppErrorResult mResult;
    public final ActivityManagerService mService;

    public StrictModeViolationDialog(Context context, ActivityManagerService activityManagerService, AppErrorResult appErrorResult, ProcessRecord processRecord) {
        super(context);
        CharSequence applicationLabel;
        Handler handler = new Handler() { // from class: com.android.server.am.StrictModeViolationDialog.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                synchronized (StrictModeViolationDialog.this.mService.mProcLock) {
                    try {
                        ActivityManagerService.boostPriorityForProcLockedSection();
                        if (StrictModeViolationDialog.this.mProc != null) {
                            StrictModeViolationDialog.this.mProc.mErrorState.getDialogController().clearViolationDialogs();
                        }
                    } catch (Throwable th) {
                        ActivityManagerService.resetPriorityAfterProcLockedSection();
                        throw th;
                    }
                }
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                StrictModeViolationDialog.this.mResult.set(message.what);
                StrictModeViolationDialog.this.dismiss();
            }
        };
        this.mHandler = handler;
        Resources resources = context.getResources();
        this.mService = activityManagerService;
        this.mProc = processRecord;
        this.mResult = appErrorResult;
        if (processRecord.getPkgList().size() == 1 && (applicationLabel = context.getPackageManager().getApplicationLabel(processRecord.info)) != null) {
            setMessage(resources.getString(17041565, applicationLabel.toString(), processRecord.info.processName));
        } else {
            setMessage(resources.getString(17041566, processRecord.processName.toString()));
        }
        setCancelable(false);
        setButton(-1, resources.getText(17040140), handler.obtainMessage(0));
        if (processRecord.mErrorState.getErrorReportReceiver() != null) {
            setButton(-2, resources.getText(17041411), handler.obtainMessage(1));
        }
        getWindow().addPrivateFlags(256);
        Window window = getWindow();
        window.setTitle("Strict Mode Violation: " + processRecord.info.processName);
        handler.sendMessageDelayed(handler.obtainMessage(0), 60000L);
    }

    @Override // com.android.server.p006am.BaseErrorDialog
    public void closeDialog() {
        this.mHandler.obtainMessage(0).sendToTarget();
    }
}
