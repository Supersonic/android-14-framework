package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.SubscriptionManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class SmsStorageMonitor extends Handler {
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    final CommandsInterface mCi;
    private final Context mContext;
    Phone mPhone;
    private final BroadcastReceiver mResultReceiver;
    private PowerManager.WakeLock mWakeLock;
    private int mMaxRetryCount = 1;
    private int mRetryDelay = GbaManager.REQUEST_TIMEOUT_MS;
    private int mRetryCount = 0;
    private boolean mIsWaitingResponse = false;
    private boolean mNeedNewReporting = false;
    private boolean mIsMemoryStatusReportingFailed = false;
    boolean mStorageAvailable = true;
    boolean mInitialStorageAvailableStatus = true;
    private boolean mMemoryStatusOverrideFlag = false;

    public SmsStorageMonitor(Phone phone) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.SmsStorageMonitor.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("android.intent.action.DEVICE_STORAGE_FULL".equals(action) || "android.intent.action.DEVICE_STORAGE_NOT_FULL".equals(action)) {
                    SmsStorageMonitor.this.mStorageAvailable = !"android.intent.action.DEVICE_STORAGE_FULL".equals(action);
                    SmsStorageMonitor smsStorageMonitor = SmsStorageMonitor.this;
                    smsStorageMonitor.sendMessage(smsStorageMonitor.obtainMessage(2));
                }
            }
        };
        this.mResultReceiver = broadcastReceiver;
        this.mPhone = phone;
        Context context = phone.getContext();
        this.mContext = context;
        CommandsInterface commandsInterface = phone.mCi;
        this.mCi = commandsInterface;
        createWakelock();
        commandsInterface.setOnIccSmsFull(this, 1, null);
        commandsInterface.registerForOn(this, 5, null);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.DEVICE_STORAGE_FULL");
        intentFilter.addAction("android.intent.action.DEVICE_STORAGE_NOT_FULL");
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void sendMemoryStatusOverride(boolean z) {
        if (!this.mMemoryStatusOverrideFlag) {
            this.mInitialStorageAvailableStatus = this.mStorageAvailable;
            this.mMemoryStatusOverrideFlag = true;
        }
        this.mStorageAvailable = z;
        if (z) {
            sendMessage(obtainMessage(2));
        }
    }

    public void clearMemoryStatusOverride() {
        this.mStorageAvailable = this.mInitialStorageAvailableStatus;
        this.mMemoryStatusOverrideFlag = false;
    }

    @VisibleForTesting
    public void setMaxRetries(int i) {
        this.mMaxRetryCount = i;
    }

    @VisibleForTesting
    public void setRetryDelayInMillis(int i) {
        this.mRetryDelay = i;
    }

    public void dispose() {
        this.mCi.unSetOnIccSmsFull(this);
        this.mCi.unregisterForOn(this);
        this.mContext.unregisterReceiver(this.mResultReceiver);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        boolean z = this.mStorageAvailable;
        int i = message.what;
        if (i == 1) {
            handleIccFull();
            return;
        }
        if (i == 2) {
            if (this.mIsWaitingResponse) {
                Rlog.v("SmsStorageMonitor1", "EVENT_REPORT_MEMORY_STATUS - deferred");
                this.mNeedNewReporting = true;
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("EVENT_REPORT_MEMORY_STATUS - report sms memory status");
            sb.append(z ? "(not full)" : "(full)");
            Rlog.v("SmsStorageMonitor1", sb.toString());
            removeMessages(4);
            this.mIsMemoryStatusReportingFailed = false;
            this.mRetryCount = 0;
            sendMemoryStatusReport(z);
        } else if (i != 3) {
            if (i == 4) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("EVENT_RETRY_MEMORY_STATUS_REPORTING - retry");
                sb2.append(z ? "(not full)" : "(full)");
                Rlog.v("SmsStorageMonitor1", sb2.toString());
                sendMemoryStatusReport(z);
            } else if (i == 5 && this.mIsMemoryStatusReportingFailed) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("EVENT_RADIO_ON - report failed sms memory status");
                sb3.append(z ? "(not full)" : "(full)");
                Rlog.v("SmsStorageMonitor1", sb3.toString());
                sendMemoryStatusReport(z);
            }
        } else {
            AsyncResult asyncResult = (AsyncResult) message.obj;
            this.mIsWaitingResponse = false;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("EVENT_REPORT_MEMORY_STATUS_DONE - ");
            sb4.append(asyncResult.exception == null ? "succeeded" : "failed");
            Rlog.v("SmsStorageMonitor1", sb4.toString());
            if (this.mNeedNewReporting) {
                StringBuilder sb5 = new StringBuilder();
                sb5.append("EVENT_REPORT_MEMORY_STATUS_DONE - report again now");
                sb5.append(z ? "(not full)" : "(full)");
                Rlog.v("SmsStorageMonitor1", sb5.toString());
                this.mNeedNewReporting = false;
                this.mRetryCount = 0;
                sendMemoryStatusReport(z);
            } else if (asyncResult.exception != null) {
                int i2 = this.mRetryCount;
                this.mRetryCount = i2 + 1;
                if (i2 < this.mMaxRetryCount) {
                    Rlog.v("SmsStorageMonitor1", "EVENT_REPORT_MEMORY_STATUS_DONE - retry in " + this.mRetryDelay + "ms");
                    sendMessageDelayed(obtainMessage(4), (long) this.mRetryDelay);
                    return;
                }
                Rlog.v("SmsStorageMonitor1", "EVENT_REPORT_MEMORY_STATUS_DONE - no retry anymore(pended)");
                this.mRetryCount = 0;
                this.mIsMemoryStatusReportingFailed = true;
            } else {
                this.mRetryCount = 0;
                this.mIsMemoryStatusReportingFailed = false;
            }
        }
    }

    private void sendMemoryStatusReport(boolean z) {
        IccSmsInterfaceManager iccSmsInterfaceManager;
        this.mIsWaitingResponse = true;
        if (this.mContext.getResources().getBoolean(17891801) && (iccSmsInterfaceManager = this.mPhone.getIccSmsInterfaceManager()) != null) {
            Rlog.d("SmsStorageMonitor1", "sendMemoryStatusReport: smsIfcMngr is available");
            if (iccSmsInterfaceManager.mDispatchersController.isIms() && z) {
                iccSmsInterfaceManager.mDispatchersController.reportSmsMemoryStatus(obtainMessage(3));
                return;
            }
        }
        this.mCi.reportSmsMemoryStatus(z, obtainMessage(3));
    }

    private void createWakelock() {
        PowerManager.WakeLock newWakeLock = ((PowerManager) this.mContext.getSystemService("power")).newWakeLock(1, "SmsStorageMonitor");
        this.mWakeLock = newWakeLock;
        newWakeLock.setReferenceCounted(true);
    }

    private void handleIccFull() {
        ComponentName defaultSimFullApplicationAsUser = SmsApplication.getDefaultSimFullApplicationAsUser(this.mContext, false, TelephonyUtils.getSubscriptionUserHandle(this.mContext, this.mPhone.getSubId()));
        Intent intent = new Intent("android.provider.Telephony.SIM_FULL");
        intent.setComponent(defaultSimFullApplicationAsUser);
        this.mWakeLock.acquire(5000L);
        SubscriptionManager.putPhoneIdAndSubIdExtra(intent, this.mPhone.getPhoneId());
        this.mContext.sendBroadcast(intent, "android.permission.RECEIVE_SMS");
    }

    public boolean isStorageAvailable() {
        return this.mStorageAvailable;
    }
}
