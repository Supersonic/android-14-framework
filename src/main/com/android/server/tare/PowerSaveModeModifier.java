package com.android.server.tare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
/* loaded from: classes2.dex */
public class PowerSaveModeModifier extends Modifier {
    public static final boolean DEBUG;
    public static final String TAG;
    public final InternalResourceService mIrs;
    public final PowerSaveModeTracker mPowerSaveModeTracker = new PowerSaveModeTracker();

    static {
        String str = "TARE-" + PowerSaveModeModifier.class.getSimpleName();
        TAG = str;
        DEBUG = InternalResourceService.DEBUG || Log.isLoggable(str, 3);
    }

    public PowerSaveModeModifier(InternalResourceService internalResourceService) {
        this.mIrs = internalResourceService;
    }

    @Override // com.android.server.tare.Modifier
    public void setup() {
        this.mPowerSaveModeTracker.startTracking(this.mIrs.getContext());
    }

    @Override // com.android.server.tare.Modifier
    public void tearDown() {
        this.mPowerSaveModeTracker.stopTracking(this.mIrs.getContext());
    }

    @Override // com.android.server.tare.Modifier
    public long getModifiedCostToProduce(long j) {
        double d;
        if (this.mPowerSaveModeTracker.mPowerSaveModeEnabled) {
            d = 1.5d;
        } else if (!this.mPowerSaveModeTracker.mPowerSaveModeEnabled) {
            return j;
        } else {
            d = 1.25d;
        }
        return (long) (j * d);
    }

    @Override // com.android.server.tare.Modifier
    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.print("power save=");
        indentingPrintWriter.println(this.mPowerSaveModeTracker.mPowerSaveModeEnabled);
    }

    /* loaded from: classes2.dex */
    public final class PowerSaveModeTracker extends BroadcastReceiver {
        public boolean mIsSetup;
        public final PowerManager mPowerManager;
        public volatile boolean mPowerSaveModeEnabled;

        public PowerSaveModeTracker() {
            this.mIsSetup = false;
            this.mPowerManager = (PowerManager) PowerSaveModeModifier.this.mIrs.getContext().getSystemService(PowerManager.class);
        }

        public void startTracking(Context context) {
            if (this.mIsSetup) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
            context.registerReceiver(this, intentFilter);
            this.mPowerSaveModeEnabled = this.mPowerManager.isPowerSaveMode();
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
            if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(intent.getAction())) {
                boolean isPowerSaveMode = this.mPowerManager.isPowerSaveMode();
                if (PowerSaveModeModifier.DEBUG) {
                    String str = PowerSaveModeModifier.TAG;
                    Slog.d(str, "Power save mode changed to " + isPowerSaveMode + ", fired @ " + SystemClock.elapsedRealtime());
                }
                if (this.mPowerSaveModeEnabled != isPowerSaveMode) {
                    this.mPowerSaveModeEnabled = isPowerSaveMode;
                    PowerSaveModeModifier.this.mIrs.onDeviceStateChanged();
                }
            }
        }
    }
}
