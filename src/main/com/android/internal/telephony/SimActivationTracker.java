package com.android.internal.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class SimActivationTracker {
    private static final boolean VDBG = Rlog.isLoggable("SAT", 2);
    private Phone mPhone;
    private final BroadcastReceiver mReceiver;
    private final LocalLog mVoiceActivationStateLog = new LocalLog(8);
    private final LocalLog mDataActivationStateLog = new LocalLog(8);
    private int mVoiceActivationState = 0;
    private int mDataActivationState = 0;

    private static boolean isValidActivationState(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4;
    }

    private static String toString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 4 ? "invalid" : "restricted" : "deactivated" : "activated" : "activating" : "unknown";
    }

    public SimActivationTracker(Phone phone) {
        this.mPhone = phone;
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.SimActivationTracker.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (SimActivationTracker.VDBG) {
                    SimActivationTracker simActivationTracker = SimActivationTracker.this;
                    simActivationTracker.log("action: " + action);
                }
                if ("android.intent.action.SIM_STATE_CHANGED".equals(action) && "ABSENT".equals(intent.getStringExtra("ss"))) {
                    SimActivationTracker.this.log("onSimAbsent, reset activation state to UNKNOWN");
                    SimActivationTracker.this.setVoiceActivationState(0);
                    SimActivationTracker.this.setDataActivationState(0);
                }
            }
        };
        this.mReceiver = broadcastReceiver;
        this.mPhone.getContext().registerReceiver(broadcastReceiver, new IntentFilter("android.intent.action.SIM_STATE_CHANGED"));
    }

    public void setVoiceActivationState(int i) {
        if (!isValidActivationState(i) || 4 == i) {
            throw new IllegalArgumentException("invalid voice activation state: " + i);
        }
        log("setVoiceActivationState=" + i);
        this.mVoiceActivationState = i;
        this.mVoiceActivationStateLog.log(toString(i));
        this.mPhone.notifyVoiceActivationStateChanged(i);
    }

    public void setDataActivationState(int i) {
        if (!isValidActivationState(i)) {
            throw new IllegalArgumentException("invalid data activation state: " + i);
        }
        log("setDataActivationState=" + i);
        this.mDataActivationState = i;
        this.mDataActivationStateLog.log(toString(i));
        this.mPhone.notifyDataActivationStateChanged(i);
    }

    public int getVoiceActivationState() {
        return this.mVoiceActivationState;
    }

    public int getDataActivationState() {
        return this.mDataActivationState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        Rlog.d("SAT", "[" + this.mPhone.getPhoneId() + "]" + str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        printWriter.println(" mVoiceActivationState Log:");
        indentingPrintWriter.increaseIndent();
        this.mVoiceActivationStateLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        printWriter.println(" mDataActivationState Log:");
        indentingPrintWriter.increaseIndent();
        this.mDataActivationStateLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
    }

    public void dispose() {
        this.mPhone.getContext().unregisterReceiver(this.mReceiver);
    }
}
