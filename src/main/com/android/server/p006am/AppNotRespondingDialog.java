package com.android.server.p006am;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.BidiFormatter;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.FrameworkStatsLog;
/* renamed from: com.android.server.am.AppNotRespondingDialog */
/* loaded from: classes.dex */
public final class AppNotRespondingDialog extends BaseErrorDialog implements View.OnClickListener {
    public final Data mData;
    public final Handler mHandler;
    public final ProcessRecord mProc;
    public final ActivityManagerService mService;

    public AppNotRespondingDialog(ActivityManagerService activityManagerService, Context context, Data data) {
        super(context);
        int i;
        String string;
        this.mHandler = new Handler() { // from class: com.android.server.am.AppNotRespondingDialog.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                MetricsLogger.action(AppNotRespondingDialog.this.getContext(), (int) FrameworkStatsLog.f88x54490b7, message.what);
                int i2 = message.what;
                if (i2 == 1) {
                    AppNotRespondingDialog.this.mService.killAppAtUsersRequest(AppNotRespondingDialog.this.mProc);
                } else if (i2 == 2 || i2 == 3) {
                    synchronized (AppNotRespondingDialog.this.mService) {
                        try {
                            ActivityManagerService.boostPriorityForLockedSection();
                            ProcessRecord processRecord = AppNotRespondingDialog.this.mProc;
                            ProcessErrorStateRecord processErrorStateRecord = processRecord.mErrorState;
                            r2 = message.what == 3 ? AppNotRespondingDialog.this.mService.mAppErrors.createAppErrorIntentLOSP(processRecord, System.currentTimeMillis(), null) : null;
                            synchronized (AppNotRespondingDialog.this.mService.mProcLock) {
                                ActivityManagerService.boostPriorityForProcLockedSection();
                                processErrorStateRecord.setNotResponding(false);
                                processErrorStateRecord.getDialogController().clearAnrDialogs();
                            }
                            ActivityManagerService.resetPriorityAfterProcLockedSection();
                            AppNotRespondingDialog.this.mService.mServices.scheduleServiceTimeoutLocked(processRecord);
                            if (AppNotRespondingDialog.this.mData.isContinuousAnr) {
                                AppNotRespondingDialog.this.mService.mInternal.rescheduleAnrDialog(AppNotRespondingDialog.this.mData);
                            }
                        } catch (Throwable th) {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                    ActivityManagerService.resetPriorityAfterLockedSection();
                }
                if (r2 != null) {
                    try {
                        AppNotRespondingDialog.this.getContext().startActivity(r2);
                    } catch (ActivityNotFoundException e) {
                        Slog.w("AppNotRespondingDialog", "bug report receiver dissappeared", e);
                    }
                }
                AppNotRespondingDialog.this.dismiss();
            }
        };
        this.mService = activityManagerService;
        ProcessRecord processRecord = data.proc;
        this.mProc = processRecord;
        this.mData = data;
        Resources resources = context.getResources();
        setCancelable(false);
        ApplicationInfo applicationInfo = data.aInfo;
        CharSequence charSequence = null;
        CharSequence loadLabel = applicationInfo != null ? applicationInfo.loadLabel(context.getPackageManager()) : null;
        if (processRecord.getPkgList().size() != 1 || (charSequence = context.getPackageManager().getApplicationLabel(processRecord.info)) == null) {
            if (loadLabel != null) {
                charSequence = processRecord.processName;
                i = 17039671;
            } else {
                loadLabel = processRecord.processName;
                i = 17039673;
            }
        } else if (loadLabel != null) {
            i = 17039670;
        } else {
            charSequence = processRecord.processName;
            i = 17039672;
            loadLabel = charSequence;
        }
        BidiFormatter bidiFormatter = BidiFormatter.getInstance();
        if (charSequence != null) {
            string = resources.getString(i, bidiFormatter.unicodeWrap(loadLabel.toString()), bidiFormatter.unicodeWrap(charSequence.toString()));
        } else {
            string = resources.getString(i, bidiFormatter.unicodeWrap(loadLabel.toString()));
        }
        setTitle(string);
        if (data.aboveSystem) {
            getWindow().setType(2010);
        }
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.setTitle("Application Not Responding: " + processRecord.info.processName);
        attributes.privateFlags = 272;
        getWindow().setAttributes(attributes);
    }

    @Override // android.app.AlertDialog, android.app.Dialog
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LayoutInflater.from(getContext()).inflate(17367095, (ViewGroup) ((FrameLayout) findViewById(16908331)), true);
        TextView textView = (TextView) findViewById(16908757);
        textView.setOnClickListener(this);
        textView.setVisibility(this.mProc.mErrorState.getErrorReportReceiver() != null ? 0 : 8);
        ((TextView) findViewById(16908755)).setOnClickListener(this);
        ((TextView) findViewById(16908759)).setOnClickListener(this);
        findViewById(16908937).setVisibility(0);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == 16908755) {
            this.mHandler.obtainMessage(1).sendToTarget();
        } else if (id == 16908757) {
            this.mHandler.obtainMessage(3).sendToTarget();
        } else if (id != 16908759) {
        } else {
            this.mHandler.obtainMessage(2).sendToTarget();
        }
    }

    @Override // com.android.server.p006am.BaseErrorDialog
    public void closeDialog() {
        this.mHandler.obtainMessage(1).sendToTarget();
    }

    /* renamed from: com.android.server.am.AppNotRespondingDialog$Data */
    /* loaded from: classes.dex */
    public static class Data {
        public final ApplicationInfo aInfo;
        public final boolean aboveSystem;
        public final boolean isContinuousAnr;
        public final ProcessRecord proc;

        public Data(ProcessRecord processRecord, ApplicationInfo applicationInfo, boolean z, boolean z2) {
            this.proc = processRecord;
            this.aInfo = applicationInfo;
            this.aboveSystem = z;
            this.isContinuousAnr = z2;
        }
    }
}
