package com.android.server.policy.keyguard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IKeyguardDrawnCallback;
import com.android.internal.policy.IKeyguardExitCallback;
import com.android.internal.policy.IKeyguardService;
import com.android.internal.policy.IKeyguardStateCallback;
import com.android.server.policy.keyguard.KeyguardStateMonitor;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public class KeyguardServiceWrapper implements IKeyguardService {
    public String TAG = "KeyguardServiceWrapper";
    public KeyguardStateMonitor mKeyguardStateMonitor;
    public IKeyguardService mService;

    public KeyguardServiceWrapper(Context context, IKeyguardService iKeyguardService, KeyguardStateMonitor.StateCallback stateCallback) {
        this.mService = iKeyguardService;
        this.mKeyguardStateMonitor = new KeyguardStateMonitor(context, iKeyguardService, stateCallback);
    }

    public void verifyUnlock(IKeyguardExitCallback iKeyguardExitCallback) {
        try {
            this.mService.verifyUnlock(iKeyguardExitCallback);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void setOccluded(boolean z, boolean z2) {
        try {
            this.mService.setOccluded(z, z2);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void addStateMonitorCallback(IKeyguardStateCallback iKeyguardStateCallback) {
        try {
            this.mService.addStateMonitorCallback(iKeyguardStateCallback);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void dismiss(IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence) {
        try {
            this.mService.dismiss(iKeyguardDismissCallback, charSequence);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onDreamingStarted() {
        try {
            this.mService.onDreamingStarted();
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onDreamingStopped() {
        try {
            this.mService.onDreamingStopped();
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onStartedGoingToSleep(int i) {
        try {
            this.mService.onStartedGoingToSleep(i);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onFinishedGoingToSleep(int i, boolean z) {
        try {
            this.mService.onFinishedGoingToSleep(i, z);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onStartedWakingUp(int i, boolean z) {
        try {
            this.mService.onStartedWakingUp(i, z);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onFinishedWakingUp() {
        try {
            this.mService.onFinishedWakingUp();
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onScreenTurningOn(IKeyguardDrawnCallback iKeyguardDrawnCallback) {
        try {
            this.mService.onScreenTurningOn(iKeyguardDrawnCallback);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onScreenTurnedOn() {
        try {
            this.mService.onScreenTurnedOn();
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onScreenTurningOff() {
        try {
            this.mService.onScreenTurningOff();
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onScreenTurnedOff() {
        try {
            this.mService.onScreenTurnedOff();
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void setKeyguardEnabled(boolean z) {
        try {
            this.mService.setKeyguardEnabled(z);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onSystemReady() {
        try {
            this.mService.onSystemReady();
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void doKeyguardTimeout(Bundle bundle) {
        int currentUser = this.mKeyguardStateMonitor.getCurrentUser();
        if (this.mKeyguardStateMonitor.isSecure(currentUser)) {
            this.mKeyguardStateMonitor.onShowingStateChanged(true, currentUser);
        }
        try {
            this.mService.doKeyguardTimeout(bundle);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void setSwitchingUser(boolean z) {
        try {
            this.mService.setSwitchingUser(z);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void setCurrentUser(int i) {
        this.mKeyguardStateMonitor.setCurrentUser(i);
        try {
            this.mService.setCurrentUser(i);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onBootCompleted() {
        try {
            this.mService.onBootCompleted();
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void startKeyguardExitAnimation(long j, long j2) {
        try {
            this.mService.startKeyguardExitAnimation(j, j2);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onShortPowerPressedGoHome() {
        try {
            this.mService.onShortPowerPressedGoHome();
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void dismissKeyguardToLaunch(Intent intent) {
        try {
            this.mService.dismissKeyguardToLaunch(intent);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public void onSystemKeyPressed(int i) {
        try {
            this.mService.onSystemKeyPressed(i);
        } catch (RemoteException e) {
            Slog.w(this.TAG, "Remote Exception", e);
        }
    }

    public IBinder asBinder() {
        return this.mService.asBinder();
    }

    public boolean isShowing() {
        return this.mKeyguardStateMonitor.isShowing();
    }

    public boolean isTrusted() {
        return this.mKeyguardStateMonitor.isTrusted();
    }

    public boolean isSecure(int i) {
        return this.mKeyguardStateMonitor.isSecure(i);
    }

    public boolean isInputRestricted() {
        return this.mKeyguardStateMonitor.isInputRestricted();
    }

    public void dump(String str, PrintWriter printWriter) {
        this.mKeyguardStateMonitor.dump(str, printWriter);
    }
}
