package com.android.server.p014wm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.PowerManager;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.CrossWindowBlurListeners;
import android.view.ICrossWindowBlurEnabledListener;
import android.view.TunnelModeEnabledListener;
import com.android.server.SystemServerInitThreadPool$$ExternalSyntheticLambda0;
/* renamed from: com.android.server.wm.BlurController */
/* loaded from: classes2.dex */
public final class BlurController {
    public boolean mBlurDisabledSetting;
    public volatile boolean mBlurEnabled;
    public final Context mContext;
    public boolean mCriticalThermalStatus;
    public boolean mInPowerSaveMode;
    public final RemoteCallbackList<ICrossWindowBlurEnabledListener> mBlurEnabledListeners = new RemoteCallbackList<>();
    public final Object mLock = new Object();
    public boolean mTunnelModeEnabled = false;
    public TunnelModeEnabledListener mTunnelModeListener = new TunnelModeEnabledListener(new SystemServerInitThreadPool$$ExternalSyntheticLambda0()) { // from class: com.android.server.wm.BlurController.1
        public void onTunnelModeEnabledChanged(boolean z) {
            BlurController.this.mTunnelModeEnabled = z;
            BlurController.this.updateBlurEnabled();
        }
    };

    public BlurController(Context context, final PowerManager powerManager) {
        this.mContext = context;
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        context.registerReceiverForAllUsers(new BroadcastReceiver() { // from class: com.android.server.wm.BlurController.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(intent.getAction())) {
                    BlurController.this.mInPowerSaveMode = powerManager.isPowerSaveMode();
                    BlurController.this.updateBlurEnabled();
                }
            }
        }, intentFilter, null, null);
        this.mInPowerSaveMode = powerManager.isPowerSaveMode();
        context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("disable_window_blurs"), false, new ContentObserver(null) { // from class: com.android.server.wm.BlurController.3
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                super.onChange(z);
                BlurController blurController = BlurController.this;
                blurController.mBlurDisabledSetting = blurController.getBlurDisabledSetting();
                BlurController.this.updateBlurEnabled();
            }
        });
        this.mBlurDisabledSetting = getBlurDisabledSetting();
        powerManager.addThermalStatusListener(new PowerManager.OnThermalStatusChangedListener() { // from class: com.android.server.wm.BlurController$$ExternalSyntheticLambda0
            @Override // android.os.PowerManager.OnThermalStatusChangedListener
            public final void onThermalStatusChanged(int i) {
                BlurController.this.lambda$new$0(i);
            }
        });
        this.mCriticalThermalStatus = powerManager.getCurrentThermalStatus() >= 4;
        TunnelModeEnabledListener.register(this.mTunnelModeListener);
        updateBlurEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i) {
        this.mCriticalThermalStatus = i >= 4;
        updateBlurEnabled();
    }

    public boolean registerCrossWindowBlurEnabledListener(ICrossWindowBlurEnabledListener iCrossWindowBlurEnabledListener) {
        if (iCrossWindowBlurEnabledListener == null) {
            return false;
        }
        this.mBlurEnabledListeners.register(iCrossWindowBlurEnabledListener);
        return getBlurEnabled();
    }

    public void unregisterCrossWindowBlurEnabledListener(ICrossWindowBlurEnabledListener iCrossWindowBlurEnabledListener) {
        if (iCrossWindowBlurEnabledListener == null) {
            return;
        }
        this.mBlurEnabledListeners.unregister(iCrossWindowBlurEnabledListener);
    }

    public boolean getBlurEnabled() {
        return this.mBlurEnabled;
    }

    public final void updateBlurEnabled() {
        synchronized (this.mLock) {
            boolean z = (!CrossWindowBlurListeners.CROSS_WINDOW_BLUR_SUPPORTED || this.mBlurDisabledSetting || this.mInPowerSaveMode || this.mTunnelModeEnabled || this.mCriticalThermalStatus) ? false : true;
            if (this.mBlurEnabled == z) {
                return;
            }
            this.mBlurEnabled = z;
            notifyBlurEnabledChangedLocked(z);
        }
    }

    public final void notifyBlurEnabledChangedLocked(boolean z) {
        int beginBroadcast = this.mBlurEnabledListeners.beginBroadcast();
        while (beginBroadcast > 0) {
            beginBroadcast--;
            try {
                this.mBlurEnabledListeners.getBroadcastItem(beginBroadcast).onCrossWindowBlurEnabledChanged(z);
            } catch (RemoteException unused) {
            }
        }
        this.mBlurEnabledListeners.finishBroadcast();
    }

    public final boolean getBlurDisabledSetting() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "disable_window_blurs", 0) == 1;
    }
}
