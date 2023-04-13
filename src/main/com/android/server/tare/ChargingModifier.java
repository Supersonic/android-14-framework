package com.android.server.tare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
/* loaded from: classes2.dex */
public class ChargingModifier extends Modifier {
    public static final boolean DEBUG;
    public static final String TAG;
    public final ChargingTracker mChargingTracker = new ChargingTracker();
    public final InternalResourceService mIrs;

    static {
        String str = "TARE-" + ChargingModifier.class.getSimpleName();
        TAG = str;
        DEBUG = InternalResourceService.DEBUG || Log.isLoggable(str, 3);
    }

    public ChargingModifier(InternalResourceService internalResourceService) {
        this.mIrs = internalResourceService;
    }

    @Override // com.android.server.tare.Modifier
    public void setup() {
        this.mChargingTracker.startTracking(this.mIrs.getContext());
    }

    @Override // com.android.server.tare.Modifier
    public void tearDown() {
        this.mChargingTracker.stopTracking(this.mIrs.getContext());
    }

    @Override // com.android.server.tare.Modifier
    public long getModifiedCostToProduce(long j) {
        return modifyValue(j);
    }

    @Override // com.android.server.tare.Modifier
    public long getModifiedPrice(long j) {
        return modifyValue(j);
    }

    public final long modifyValue(long j) {
        if (this.mChargingTracker.mCharging) {
            return 0L;
        }
        return j;
    }

    @Override // com.android.server.tare.Modifier
    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.print("charging=");
        indentingPrintWriter.println(this.mChargingTracker.mCharging);
    }

    /* loaded from: classes2.dex */
    public final class ChargingTracker extends BroadcastReceiver {
        public volatile boolean mCharging;
        public boolean mIsSetup;

        public ChargingTracker() {
            this.mIsSetup = false;
        }

        public void startTracking(Context context) {
            if (this.mIsSetup) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.os.action.CHARGING");
            intentFilter.addAction("android.os.action.DISCHARGING");
            context.registerReceiver(this, intentFilter);
            this.mCharging = ((BatteryManager) context.getSystemService(BatteryManager.class)).isCharging();
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
            if ("android.os.action.CHARGING".equals(action)) {
                if (ChargingModifier.DEBUG) {
                    String str = ChargingModifier.TAG;
                    Slog.d(str, "Received charging intent, fired @ " + SystemClock.elapsedRealtime());
                }
                if (this.mCharging) {
                    return;
                }
                this.mCharging = true;
                ChargingModifier.this.mIrs.onDeviceStateChanged();
            } else if ("android.os.action.DISCHARGING".equals(action)) {
                if (ChargingModifier.DEBUG) {
                    Slog.d(ChargingModifier.TAG, "Disconnected from power.");
                }
                if (this.mCharging) {
                    this.mCharging = false;
                    ChargingModifier.this.mIrs.onDeviceStateChanged();
                }
            }
        }
    }
}
