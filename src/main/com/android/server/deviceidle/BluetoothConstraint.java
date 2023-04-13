package com.android.server.deviceidle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.DeviceIdleInternal;
/* loaded from: classes.dex */
public class BluetoothConstraint implements IDeviceIdleConstraint {
    public static final String TAG = BluetoothConstraint.class.getSimpleName();
    public final BluetoothManager mBluetoothManager;
    public final Context mContext;
    public final Handler mHandler;
    public final DeviceIdleInternal mLocalService;
    public volatile boolean mConnected = true;
    public volatile boolean mMonitoring = false;
    @VisibleForTesting
    final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.server.deviceidle.BluetoothConstraint.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.device.action.ACL_CONNECTED".equals(intent.getAction())) {
                BluetoothConstraint.this.mLocalService.exitIdle("bluetooth");
            } else {
                BluetoothConstraint.this.updateAndReportActiveLocked();
            }
        }
    };
    public final Runnable mTimeoutCallback = new Runnable() { // from class: com.android.server.deviceidle.BluetoothConstraint$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            BluetoothConstraint.this.lambda$new$0();
        }
    };

    public BluetoothConstraint(Context context, Handler handler, DeviceIdleInternal deviceIdleInternal) {
        this.mContext = context;
        this.mHandler = handler;
        this.mLocalService = deviceIdleInternal;
        this.mBluetoothManager = (BluetoothManager) context.getSystemService(BluetoothManager.class);
    }

    public synchronized void startMonitoring() {
        this.mConnected = true;
        this.mMonitoring = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(Message.obtain(handler, this.mTimeoutCallback), 1200000L);
        updateAndReportActiveLocked();
    }

    public synchronized void stopMonitoring() {
        this.mContext.unregisterReceiver(this.mReceiver);
        this.mHandler.removeCallbacks(this.mTimeoutCallback);
        this.mMonitoring = false;
    }

    /* renamed from: cancelMonitoringDueToTimeout */
    public final synchronized void lambda$new$0() {
        if (this.mMonitoring) {
            this.mMonitoring = false;
            this.mLocalService.onConstraintStateChanged(this, false);
        }
    }

    @GuardedBy({"this"})
    public final void updateAndReportActiveLocked() {
        boolean isBluetoothConnected = isBluetoothConnected(this.mBluetoothManager);
        if (isBluetoothConnected != this.mConnected) {
            this.mConnected = isBluetoothConnected;
            this.mLocalService.onConstraintStateChanged(this, this.mConnected);
        }
    }

    @VisibleForTesting
    public static boolean isBluetoothConnected(BluetoothManager bluetoothManager) {
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        return adapter != null && adapter.isEnabled() && bluetoothManager.getConnectedDevices(7).size() > 0;
    }
}
