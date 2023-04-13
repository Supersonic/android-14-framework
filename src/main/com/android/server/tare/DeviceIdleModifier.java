package com.android.server.tare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.util.IndentingPrintWriter;
import android.util.Log;
/* loaded from: classes2.dex */
public class DeviceIdleModifier extends Modifier {
    public static final boolean DEBUG;
    public static final String TAG;
    public final DeviceIdleTracker mDeviceIdleTracker = new DeviceIdleTracker();
    public final InternalResourceService mIrs;
    public final PowerManager mPowerManager;

    static {
        String str = "TARE-" + DeviceIdleModifier.class.getSimpleName();
        TAG = str;
        DEBUG = InternalResourceService.DEBUG || Log.isLoggable(str, 3);
    }

    public DeviceIdleModifier(InternalResourceService internalResourceService) {
        this.mIrs = internalResourceService;
        this.mPowerManager = (PowerManager) internalResourceService.getContext().getSystemService(PowerManager.class);
    }

    @Override // com.android.server.tare.Modifier
    public void setup() {
        this.mDeviceIdleTracker.startTracking(this.mIrs.getContext());
    }

    @Override // com.android.server.tare.Modifier
    public void tearDown() {
        this.mDeviceIdleTracker.stopTracking(this.mIrs.getContext());
    }

    @Override // com.android.server.tare.Modifier
    public long getModifiedCostToProduce(long j) {
        double d;
        if (this.mDeviceIdleTracker.mDeviceIdle) {
            d = 1.2d;
        } else if (!this.mDeviceIdleTracker.mDeviceLightIdle) {
            return j;
        } else {
            d = 1.1d;
        }
        return (long) (j * d);
    }

    @Override // com.android.server.tare.Modifier
    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.print("idle=");
        indentingPrintWriter.println(this.mDeviceIdleTracker.mDeviceIdle);
        indentingPrintWriter.print("lightIdle=");
        indentingPrintWriter.println(this.mDeviceIdleTracker.mDeviceLightIdle);
    }

    /* loaded from: classes2.dex */
    public final class DeviceIdleTracker extends BroadcastReceiver {
        public volatile boolean mDeviceIdle;
        public volatile boolean mDeviceLightIdle;
        public boolean mIsSetup = false;

        public DeviceIdleTracker() {
        }

        public void startTracking(Context context) {
            if (this.mIsSetup) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
            intentFilter.addAction("android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED");
            context.registerReceiver(this, intentFilter);
            this.mDeviceIdle = DeviceIdleModifier.this.mPowerManager.isDeviceIdleMode();
            this.mDeviceLightIdle = DeviceIdleModifier.this.mPowerManager.isLightDeviceIdleMode();
            this.mIsSetup = true;
        }

        public void stopTracking(Context context) {
            if (this.mIsSetup) {
                context.unregisterReceiver(this);
                this.mIsSetup = false;
            }
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.os.action.DEVICE_IDLE_MODE_CHANGED".equals(action)) {
                if (this.mDeviceIdle != DeviceIdleModifier.this.mPowerManager.isDeviceIdleMode()) {
                    this.mDeviceIdle = DeviceIdleModifier.this.mPowerManager.isDeviceIdleMode();
                    DeviceIdleModifier.this.mIrs.onDeviceStateChanged();
                }
            } else if (!"android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED".equals(action) || this.mDeviceLightIdle == DeviceIdleModifier.this.mPowerManager.isLightDeviceIdleMode()) {
            } else {
                this.mDeviceLightIdle = DeviceIdleModifier.this.mPowerManager.isLightDeviceIdleMode();
                DeviceIdleModifier.this.mIrs.onDeviceStateChanged();
            }
        }
    }
}
