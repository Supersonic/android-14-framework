package com.android.server.p014wm;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import android.view.IWindow;
import android.view.SurfaceControl;
import com.android.server.p014wm.WindowToken;
/* renamed from: com.android.server.wm.ShellRoot */
/* loaded from: classes2.dex */
public class ShellRoot {
    public IWindow mAccessibilityWindow;
    public IBinder.DeathRecipient mAccessibilityWindowDeath;
    public IWindow mClient;
    public final IBinder.DeathRecipient mDeathRecipient;
    public final DisplayContent mDisplayContent;
    public final int mShellRootLayer;
    public SurfaceControl mSurfaceControl;
    public WindowToken mToken;
    public int mWindowType;

    public ShellRoot(IWindow iWindow, DisplayContent displayContent, final int i) {
        this.mSurfaceControl = null;
        this.mDisplayContent = displayContent;
        this.mShellRootLayer = i;
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() { // from class: com.android.server.wm.ShellRoot$$ExternalSyntheticLambda0
            @Override // android.os.IBinder.DeathRecipient
            public final void binderDied() {
                ShellRoot.this.lambda$new$0(i);
            }
        };
        this.mDeathRecipient = deathRecipient;
        try {
            iWindow.asBinder().linkToDeath(deathRecipient, 0);
            this.mClient = iWindow;
            if (i == 0) {
                this.mWindowType = 2034;
            } else if (i == 1) {
                this.mWindowType = 2038;
            } else {
                throw new IllegalArgumentException(i + " is not an acceptable shell root layer.");
            }
            WindowToken build = new WindowToken.Builder(displayContent.mWmService, iWindow.asBinder(), this.mWindowType).setDisplayContent(displayContent).setPersistOnEmpty(true).setOwnerCanManageAppTokens(true).build();
            this.mToken = build;
            SurfaceControl.Builder containerLayer = build.makeChildSurface(null).setContainerLayer();
            this.mSurfaceControl = containerLayer.setName("Shell Root Leash " + displayContent.getDisplayId()).setCallsite("ShellRoot").build();
            this.mToken.getPendingTransaction().show(this.mSurfaceControl);
        } catch (RemoteException e) {
            Slog.e("ShellRoot", "Unable to add shell root layer " + i + " on display " + displayContent.getDisplayId(), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i) {
        this.mDisplayContent.removeShellRoot(i);
    }

    public void clear() {
        IWindow iWindow = this.mClient;
        if (iWindow != null) {
            iWindow.asBinder().unlinkToDeath(this.mDeathRecipient, 0);
            this.mClient = null;
        }
        WindowToken windowToken = this.mToken;
        if (windowToken != null) {
            windowToken.removeImmediately();
            this.mToken = null;
        }
    }

    public SurfaceControl getSurfaceControl() {
        return this.mSurfaceControl;
    }

    public IWindow getClient() {
        return this.mClient;
    }

    public IBinder getAccessibilityWindowToken() {
        IWindow iWindow = this.mAccessibilityWindow;
        if (iWindow != null) {
            return iWindow.asBinder();
        }
        return null;
    }

    public void setAccessibilityWindow(IWindow iWindow) {
        IWindow iWindow2 = this.mAccessibilityWindow;
        if (iWindow2 != null) {
            iWindow2.asBinder().unlinkToDeath(this.mAccessibilityWindowDeath, 0);
        }
        this.mAccessibilityWindow = iWindow;
        if (iWindow != null) {
            try {
                this.mAccessibilityWindowDeath = new IBinder.DeathRecipient() { // from class: com.android.server.wm.ShellRoot$$ExternalSyntheticLambda1
                    @Override // android.os.IBinder.DeathRecipient
                    public final void binderDied() {
                        ShellRoot.this.lambda$setAccessibilityWindow$1();
                    }
                };
                this.mAccessibilityWindow.asBinder().linkToDeath(this.mAccessibilityWindowDeath, 0);
            } catch (RemoteException unused) {
                this.mAccessibilityWindow = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setAccessibilityWindow$1() {
        synchronized (this.mDisplayContent.mWmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mAccessibilityWindow = null;
                setAccessibilityWindow(null);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }
}
