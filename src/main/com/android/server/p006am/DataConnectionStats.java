package com.android.server.p006am;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.RemoteException;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.android.internal.util.FrameworkStatsLog;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
/* renamed from: com.android.server.am.DataConnectionStats */
/* loaded from: classes.dex */
public class DataConnectionStats extends BroadcastReceiver {
    public final Context mContext;
    public final Handler mListenerHandler;
    public final PhoneStateListener mPhoneStateListener;
    public ServiceState mServiceState;
    public SignalStrength mSignalStrength;
    public int mSimState = 5;
    public int mDataState = 0;
    public int mNrState = 0;
    public final IBatteryStats mBatteryStats = BatteryStatsService.getService();

    public DataConnectionStats(Context context, Handler handler) {
        this.mContext = context;
        this.mListenerHandler = handler;
        this.mPhoneStateListener = new PhoneStateListenerImpl(new PhoneStateListenerExecutor(handler));
    }

    public void startMonitoring() {
        ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).listen(this.mPhoneStateListener, FrameworkStatsLog.DREAM_UI_EVENT_REPORTED);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        this.mContext.registerReceiver(this, intentFilter, null, this.mListenerHandler);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.SIM_STATE_CHANGED")) {
            updateSimState(intent);
            notePhoneDataConnectionState();
        }
    }

    public final void notePhoneDataConnectionState() {
        if (this.mServiceState == null) {
            return;
        }
        int i = this.mSimState;
        boolean z = ((i == 5 || i == 0) || isCdma()) && hasService() && this.mDataState == 2;
        NetworkRegistrationInfo networkRegistrationInfo = this.mServiceState.getNetworkRegistrationInfo(2, 1);
        int accessNetworkTechnology = networkRegistrationInfo != null ? networkRegistrationInfo.getAccessNetworkTechnology() : 0;
        if (this.mNrState == 3) {
            accessNetworkTechnology = 20;
        }
        try {
            this.mBatteryStats.notePhoneDataConnectionState(accessNetworkTechnology, z, this.mServiceState.getState(), this.mServiceState.getNrFrequencyRange());
        } catch (RemoteException e) {
            Log.w("DataConnectionStats", "Error noting data connection state", e);
        }
    }

    public final void updateSimState(Intent intent) {
        String stringExtra = intent.getStringExtra("ss");
        if ("ABSENT".equals(stringExtra)) {
            this.mSimState = 1;
        } else if ("READY".equals(stringExtra)) {
            this.mSimState = 5;
        } else if ("LOCKED".equals(stringExtra)) {
            String stringExtra2 = intent.getStringExtra("reason");
            if ("PIN".equals(stringExtra2)) {
                this.mSimState = 2;
            } else if ("PUK".equals(stringExtra2)) {
                this.mSimState = 3;
            } else {
                this.mSimState = 4;
            }
        } else {
            this.mSimState = 0;
        }
    }

    public final boolean isCdma() {
        SignalStrength signalStrength = this.mSignalStrength;
        return (signalStrength == null || signalStrength.isGsm()) ? false : true;
    }

    public final boolean hasService() {
        ServiceState serviceState = this.mServiceState;
        return (serviceState == null || serviceState.getState() == 1 || this.mServiceState.getState() == 3) ? false : true;
    }

    /* renamed from: com.android.server.am.DataConnectionStats$PhoneStateListenerExecutor */
    /* loaded from: classes.dex */
    public static class PhoneStateListenerExecutor implements Executor {
        public final Handler mHandler;

        public PhoneStateListenerExecutor(Handler handler) {
            this.mHandler = handler;
        }

        @Override // java.util.concurrent.Executor
        public void execute(Runnable runnable) {
            if (this.mHandler.post(runnable)) {
                return;
            }
            throw new RejectedExecutionException(this.mHandler + " is shutting down");
        }
    }

    /* renamed from: com.android.server.am.DataConnectionStats$PhoneStateListenerImpl */
    /* loaded from: classes.dex */
    public class PhoneStateListenerImpl extends PhoneStateListener {
        public PhoneStateListenerImpl(Executor executor) {
            super(executor);
        }

        @Override // android.telephony.PhoneStateListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            DataConnectionStats.this.mSignalStrength = signalStrength;
        }

        @Override // android.telephony.PhoneStateListener
        public void onServiceStateChanged(ServiceState serviceState) {
            DataConnectionStats.this.mServiceState = serviceState;
            DataConnectionStats.this.mNrState = serviceState.getNrState();
            DataConnectionStats.this.notePhoneDataConnectionState();
        }

        @Override // android.telephony.PhoneStateListener
        public void onDataConnectionStateChanged(int i, int i2) {
            DataConnectionStats.this.mDataState = i;
            DataConnectionStats.this.notePhoneDataConnectionState();
        }

        @Override // android.telephony.PhoneStateListener
        public void onDataActivity(int i) {
            DataConnectionStats.this.notePhoneDataConnectionState();
        }
    }
}
