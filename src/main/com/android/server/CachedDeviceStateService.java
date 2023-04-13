package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManagerInternal;
import android.os.PowerManager;
import android.util.Slog;
import com.android.internal.os.CachedDeviceState;
/* loaded from: classes.dex */
public class CachedDeviceStateService extends SystemService {
    public final BroadcastReceiver mBroadcastReceiver;
    public final CachedDeviceState mDeviceState;

    public CachedDeviceStateService(Context context) {
        super(context);
        this.mDeviceState = new CachedDeviceState();
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.CachedDeviceStateService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                action.hashCode();
                char c = 65535;
                switch (action.hashCode()) {
                    case -2128145023:
                        if (action.equals("android.intent.action.SCREEN_OFF")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1538406691:
                        if (action.equals("android.intent.action.BATTERY_CHANGED")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -1454123155:
                        if (action.equals("android.intent.action.SCREEN_ON")) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        CachedDeviceStateService.this.mDeviceState.setScreenInteractive(false);
                        return;
                    case 1:
                        CachedDeviceStateService.this.mDeviceState.setCharging(intent.getIntExtra("plugged", 0) != 0);
                        return;
                    case 2:
                        CachedDeviceStateService.this.mDeviceState.setScreenInteractive(true);
                        return;
                    default:
                        return;
                }
            }
        };
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishLocalService(CachedDeviceState.Readonly.class, this.mDeviceState.getReadonlyClient());
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (500 == i) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.setPriority(1000);
            getContext().registerReceiver(this.mBroadcastReceiver, intentFilter);
            this.mDeviceState.setCharging(queryIsCharging());
            this.mDeviceState.setScreenInteractive(queryScreenInteractive(getContext()));
        }
    }

    public final boolean queryIsCharging() {
        BatteryManagerInternal batteryManagerInternal = (BatteryManagerInternal) LocalServices.getService(BatteryManagerInternal.class);
        if (batteryManagerInternal != null) {
            return batteryManagerInternal.getPlugType() != 0;
        }
        Slog.wtf("CachedDeviceStateService", "BatteryManager null while starting CachedDeviceStateService");
        return true;
    }

    public final boolean queryScreenInteractive(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
        if (powerManager == null) {
            Slog.wtf("CachedDeviceStateService", "PowerManager null while starting CachedDeviceStateService");
            return false;
        }
        return powerManager.isInteractive();
    }
}
