package com.android.server.policy;

import android.content.Context;
import android.os.Handler;
import android.util.Slog;
import com.android.server.LocalServices;
import com.android.server.policy.GlobalActionsProvider;
import com.android.server.policy.WindowManagerPolicy;
/* loaded from: classes2.dex */
public class GlobalActions implements GlobalActionsProvider.GlobalActionsListener {
    public final Context mContext;
    public boolean mDeviceProvisioned;
    public boolean mGlobalActionsAvailable;
    public final GlobalActionsProvider mGlobalActionsProvider;
    public boolean mKeyguardShowing;
    public LegacyGlobalActions mLegacyGlobalActions;
    public boolean mShowing;
    public final WindowManagerPolicy.WindowManagerFuncs mWindowManagerFuncs;
    public final Runnable mShowTimeout = new Runnable() { // from class: com.android.server.policy.GlobalActions.1
        @Override // java.lang.Runnable
        public void run() {
            GlobalActions.this.ensureLegacyCreated();
            GlobalActions.this.mLegacyGlobalActions.showDialog(GlobalActions.this.mKeyguardShowing, GlobalActions.this.mDeviceProvisioned);
        }
    };
    public final Handler mHandler = new Handler();

    public GlobalActions(Context context, WindowManagerPolicy.WindowManagerFuncs windowManagerFuncs) {
        this.mContext = context;
        this.mWindowManagerFuncs = windowManagerFuncs;
        GlobalActionsProvider globalActionsProvider = (GlobalActionsProvider) LocalServices.getService(GlobalActionsProvider.class);
        this.mGlobalActionsProvider = globalActionsProvider;
        if (globalActionsProvider != null) {
            globalActionsProvider.setGlobalActionsListener(this);
        } else {
            Slog.i("GlobalActions", "No GlobalActionsProvider found, defaulting to LegacyGlobalActions");
        }
    }

    public final void ensureLegacyCreated() {
        if (this.mLegacyGlobalActions != null) {
            return;
        }
        this.mLegacyGlobalActions = new LegacyGlobalActions(this.mContext, this.mWindowManagerFuncs, new Runnable() { // from class: com.android.server.policy.GlobalActions$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                GlobalActions.this.onGlobalActionsDismissed();
            }
        });
    }

    public void showDialog(boolean z, boolean z2) {
        GlobalActionsProvider globalActionsProvider = this.mGlobalActionsProvider;
        if (globalActionsProvider == null || !globalActionsProvider.isGlobalActionsDisabled()) {
            this.mKeyguardShowing = z;
            this.mDeviceProvisioned = z2;
            this.mShowing = true;
            if (this.mGlobalActionsAvailable) {
                this.mHandler.postDelayed(this.mShowTimeout, 5000L);
                this.mGlobalActionsProvider.showGlobalActions();
                return;
            }
            ensureLegacyCreated();
            this.mLegacyGlobalActions.showDialog(this.mKeyguardShowing, this.mDeviceProvisioned);
        }
    }

    @Override // com.android.server.policy.GlobalActionsProvider.GlobalActionsListener
    public void onGlobalActionsShown() {
        this.mHandler.removeCallbacks(this.mShowTimeout);
    }

    @Override // com.android.server.policy.GlobalActionsProvider.GlobalActionsListener
    public void onGlobalActionsDismissed() {
        this.mShowing = false;
    }

    @Override // com.android.server.policy.GlobalActionsProvider.GlobalActionsListener
    public void onGlobalActionsAvailableChanged(boolean z) {
        this.mGlobalActionsAvailable = z;
        if (!this.mShowing || z) {
            return;
        }
        ensureLegacyCreated();
        this.mLegacyGlobalActions.showDialog(this.mKeyguardShowing, this.mDeviceProvisioned);
    }
}
