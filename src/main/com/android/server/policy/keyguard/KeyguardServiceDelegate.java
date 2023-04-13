package com.android.server.policy.keyguard;

import android.app.ActivityTaskManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.WindowManagerPolicyConstants;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IKeyguardDrawnCallback;
import com.android.internal.policy.IKeyguardExitCallback;
import com.android.internal.policy.IKeyguardService;
import com.android.server.UiThread;
import com.android.server.p014wm.EventLogTags;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.policy.keyguard.KeyguardServiceDelegate;
import com.android.server.policy.keyguard.KeyguardStateMonitor;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public class KeyguardServiceDelegate {
    public final KeyguardStateMonitor.StateCallback mCallback;
    public final Context mContext;
    public DrawnListener mDrawnListenerWhenConnect;
    public KeyguardServiceWrapper mKeyguardService;
    public final KeyguardState mKeyguardState = new KeyguardState();
    public final ServiceConnection mKeyguardConnection = new ServiceConnectionC14601();
    public final Handler mHandler = UiThread.getHandler();

    /* loaded from: classes2.dex */
    public interface DrawnListener {
        void onDrawn();
    }

    /* loaded from: classes2.dex */
    public static final class KeyguardState {
        public boolean bootCompleted;
        public int currentUser;
        public boolean deviceHasKeyguard;
        public boolean dreaming;
        public boolean enabled;
        public boolean inputRestricted;
        public int interactiveState;
        public volatile boolean occluded;
        public int offReason;
        public int screenState;
        public boolean secure;
        public boolean showing;
        public boolean systemIsReady;

        public KeyguardState() {
            reset();
        }

        public final void reset() {
            this.showing = true;
            this.occluded = false;
            this.secure = true;
            this.deviceHasKeyguard = true;
            this.enabled = true;
            this.currentUser = -10000;
        }
    }

    /* loaded from: classes2.dex */
    public final class KeyguardShowDelegate extends IKeyguardDrawnCallback.Stub {
        public DrawnListener mDrawnListener;

        public KeyguardShowDelegate(DrawnListener drawnListener) {
            this.mDrawnListener = drawnListener;
        }

        public void onDrawn() throws RemoteException {
            DrawnListener drawnListener = this.mDrawnListener;
            if (drawnListener != null) {
                drawnListener.onDrawn();
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class KeyguardExitDelegate extends IKeyguardExitCallback.Stub {
        public WindowManagerPolicy.OnKeyguardExitResult mOnKeyguardExitResult;

        public KeyguardExitDelegate(WindowManagerPolicy.OnKeyguardExitResult onKeyguardExitResult) {
            this.mOnKeyguardExitResult = onKeyguardExitResult;
        }

        public void onKeyguardExitResult(boolean z) throws RemoteException {
            WindowManagerPolicy.OnKeyguardExitResult onKeyguardExitResult = this.mOnKeyguardExitResult;
            if (onKeyguardExitResult != null) {
                onKeyguardExitResult.onKeyguardExitResult(z);
            }
        }
    }

    public KeyguardServiceDelegate(Context context, KeyguardStateMonitor.StateCallback stateCallback) {
        this.mContext = context;
        this.mCallback = stateCallback;
    }

    public void bindService(Context context) {
        Intent intent = new Intent();
        ComponentName unflattenFromString = ComponentName.unflattenFromString(context.getApplicationContext().getResources().getString(17039954));
        intent.addFlags(256);
        intent.setComponent(unflattenFromString);
        if (context.bindServiceAsUser(intent, this.mKeyguardConnection, 1, this.mHandler, UserHandle.SYSTEM)) {
            return;
        }
        Log.v("KeyguardServiceDelegate", "*** Keyguard: can't bind to " + unflattenFromString);
        KeyguardState keyguardState = this.mKeyguardState;
        keyguardState.showing = false;
        keyguardState.secure = false;
        synchronized (keyguardState) {
            this.mKeyguardState.deviceHasKeyguard = false;
        }
    }

    /* renamed from: com.android.server.policy.keyguard.KeyguardServiceDelegate$1 */
    /* loaded from: classes2.dex */
    public class ServiceConnectionC14601 implements ServiceConnection {
        public ServiceConnectionC14601() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            KeyguardServiceDelegate keyguardServiceDelegate = KeyguardServiceDelegate.this;
            keyguardServiceDelegate.mKeyguardService = new KeyguardServiceWrapper(keyguardServiceDelegate.mContext, IKeyguardService.Stub.asInterface(iBinder), KeyguardServiceDelegate.this.mCallback);
            if (KeyguardServiceDelegate.this.mKeyguardState.systemIsReady) {
                KeyguardServiceDelegate.this.mKeyguardService.onSystemReady();
                if (KeyguardServiceDelegate.this.mKeyguardState.currentUser != -10000) {
                    KeyguardServiceDelegate keyguardServiceDelegate2 = KeyguardServiceDelegate.this;
                    keyguardServiceDelegate2.mKeyguardService.setCurrentUser(keyguardServiceDelegate2.mKeyguardState.currentUser);
                }
                if (KeyguardServiceDelegate.this.mKeyguardState.interactiveState == 2 || KeyguardServiceDelegate.this.mKeyguardState.interactiveState == 1) {
                    KeyguardServiceDelegate.this.mKeyguardService.onStartedWakingUp(0, false);
                }
                if (KeyguardServiceDelegate.this.mKeyguardState.interactiveState == 2) {
                    KeyguardServiceDelegate.this.mKeyguardService.onFinishedWakingUp();
                }
                if (KeyguardServiceDelegate.this.mKeyguardState.screenState == 2 || KeyguardServiceDelegate.this.mKeyguardState.screenState == 1) {
                    KeyguardServiceWrapper keyguardServiceWrapper = KeyguardServiceDelegate.this.mKeyguardService;
                    KeyguardServiceDelegate keyguardServiceDelegate3 = KeyguardServiceDelegate.this;
                    keyguardServiceWrapper.onScreenTurningOn(new KeyguardShowDelegate(keyguardServiceDelegate3.mDrawnListenerWhenConnect));
                }
                if (KeyguardServiceDelegate.this.mKeyguardState.screenState == 2) {
                    KeyguardServiceDelegate.this.mKeyguardService.onScreenTurnedOn();
                }
                KeyguardServiceDelegate.this.mDrawnListenerWhenConnect = null;
            }
            if (KeyguardServiceDelegate.this.mKeyguardState.bootCompleted) {
                KeyguardServiceDelegate.this.mKeyguardService.onBootCompleted();
            }
            if (KeyguardServiceDelegate.this.mKeyguardState.occluded) {
                KeyguardServiceDelegate keyguardServiceDelegate4 = KeyguardServiceDelegate.this;
                keyguardServiceDelegate4.mKeyguardService.setOccluded(keyguardServiceDelegate4.mKeyguardState.occluded, false);
            }
            if (!KeyguardServiceDelegate.this.mKeyguardState.enabled) {
                KeyguardServiceDelegate keyguardServiceDelegate5 = KeyguardServiceDelegate.this;
                keyguardServiceDelegate5.mKeyguardService.setKeyguardEnabled(keyguardServiceDelegate5.mKeyguardState.enabled);
            }
            if (KeyguardServiceDelegate.this.mKeyguardState.dreaming) {
                KeyguardServiceDelegate.this.mKeyguardService.onDreamingStarted();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            KeyguardServiceDelegate keyguardServiceDelegate = KeyguardServiceDelegate.this;
            keyguardServiceDelegate.mKeyguardService = null;
            keyguardServiceDelegate.mKeyguardState.reset();
            KeyguardServiceDelegate.this.mHandler.post(new Runnable() { // from class: com.android.server.policy.keyguard.KeyguardServiceDelegate$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    KeyguardServiceDelegate.ServiceConnectionC14601.lambda$onServiceDisconnected$0();
                }
            });
        }

        public static /* synthetic */ void lambda$onServiceDisconnected$0() {
            try {
                ActivityTaskManager.getService().setLockScreenShown(true, false);
            } catch (RemoteException unused) {
            }
        }
    }

    public boolean isShowing() {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            this.mKeyguardState.showing = keyguardServiceWrapper.isShowing();
        }
        return this.mKeyguardState.showing;
    }

    public boolean isTrusted() {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            return keyguardServiceWrapper.isTrusted();
        }
        return false;
    }

    public boolean hasKeyguard() {
        return this.mKeyguardState.deviceHasKeyguard;
    }

    public boolean isInputRestricted() {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            this.mKeyguardState.inputRestricted = keyguardServiceWrapper.isInputRestricted();
        }
        return this.mKeyguardState.inputRestricted;
    }

    public void verifyUnlock(WindowManagerPolicy.OnKeyguardExitResult onKeyguardExitResult) {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.verifyUnlock(new KeyguardExitDelegate(onKeyguardExitResult));
        }
    }

    public void setOccluded(boolean z, boolean z2) {
        if (this.mKeyguardService != null && z2) {
            EventLogTags.writeWmSetKeyguardOccluded(z ? 1 : 0, 0, 0, "setOccluded");
            this.mKeyguardService.setOccluded(z, false);
        }
        this.mKeyguardState.occluded = z;
    }

    public boolean isOccluded() {
        return this.mKeyguardState.occluded;
    }

    public void dismiss(IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence) {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.dismiss(iKeyguardDismissCallback, charSequence);
        }
    }

    public boolean isSecure(int i) {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            this.mKeyguardState.secure = keyguardServiceWrapper.isSecure(i);
        }
        return this.mKeyguardState.secure;
    }

    public void onDreamingStarted() {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.onDreamingStarted();
        }
        this.mKeyguardState.dreaming = true;
    }

    public void onDreamingStopped() {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.onDreamingStopped();
        }
        this.mKeyguardState.dreaming = false;
    }

    public void onStartedWakingUp(int i, boolean z) {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.onStartedWakingUp(i, z);
        }
        this.mKeyguardState.interactiveState = 1;
    }

    public void onFinishedWakingUp() {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.onFinishedWakingUp();
        }
        this.mKeyguardState.interactiveState = 2;
    }

    public void onScreenTurningOff() {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.onScreenTurningOff();
        }
        this.mKeyguardState.screenState = 3;
    }

    public void onScreenTurnedOff() {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.onScreenTurnedOff();
        }
        this.mKeyguardState.screenState = 0;
    }

    public void onScreenTurningOn(DrawnListener drawnListener) {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.onScreenTurningOn(new KeyguardShowDelegate(drawnListener));
        } else {
            Slog.w("KeyguardServiceDelegate", "onScreenTurningOn(): no keyguard service!");
            this.mDrawnListenerWhenConnect = drawnListener;
        }
        this.mKeyguardState.screenState = 1;
    }

    public void onScreenTurnedOn() {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.onScreenTurnedOn();
        }
        this.mKeyguardState.screenState = 2;
    }

    public void onStartedGoingToSleep(int i) {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.onStartedGoingToSleep(i);
        }
        this.mKeyguardState.offReason = WindowManagerPolicyConstants.translateSleepReasonToOffReason(i);
        this.mKeyguardState.interactiveState = 3;
    }

    public void onFinishedGoingToSleep(int i, boolean z) {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.onFinishedGoingToSleep(i, z);
        }
        this.mKeyguardState.interactiveState = 0;
    }

    public void setKeyguardEnabled(boolean z) {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.setKeyguardEnabled(z);
        }
        this.mKeyguardState.enabled = z;
    }

    public void onSystemReady() {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.onSystemReady();
        } else {
            this.mKeyguardState.systemIsReady = true;
        }
    }

    public void doKeyguardTimeout(Bundle bundle) {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.doKeyguardTimeout(bundle);
        }
    }

    public void setCurrentUser(int i) {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.setCurrentUser(i);
        }
        this.mKeyguardState.currentUser = i;
    }

    public void setSwitchingUser(boolean z) {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.setSwitchingUser(z);
        }
    }

    public void startKeyguardExitAnimation(long j) {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.startKeyguardExitAnimation(j, 0L);
        }
    }

    public void onBootCompleted() {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.onBootCompleted();
        }
        this.mKeyguardState.bootCompleted = true;
    }

    public void onShortPowerPressedGoHome() {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.onShortPowerPressedGoHome();
        }
    }

    public void dismissKeyguardToLaunch(Intent intent) {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.dismissKeyguardToLaunch(intent);
        }
    }

    public void onSystemKeyPressed(int i) {
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.onSystemKeyPressed(i);
        }
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1133871366145L, this.mKeyguardState.showing);
        protoOutputStream.write(1133871366146L, this.mKeyguardState.occluded);
        protoOutputStream.write(1133871366147L, this.mKeyguardState.secure);
        protoOutputStream.write(1159641169924L, this.mKeyguardState.screenState);
        protoOutputStream.write(1159641169925L, this.mKeyguardState.interactiveState);
        protoOutputStream.end(start);
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(str + "KeyguardServiceDelegate");
        String str2 = str + "  ";
        printWriter.println(str2 + "showing=" + this.mKeyguardState.showing);
        printWriter.println(str2 + "inputRestricted=" + this.mKeyguardState.inputRestricted);
        printWriter.println(str2 + "occluded=" + this.mKeyguardState.occluded);
        printWriter.println(str2 + "secure=" + this.mKeyguardState.secure);
        printWriter.println(str2 + "dreaming=" + this.mKeyguardState.dreaming);
        printWriter.println(str2 + "systemIsReady=" + this.mKeyguardState.systemIsReady);
        printWriter.println(str2 + "deviceHasKeyguard=" + this.mKeyguardState.deviceHasKeyguard);
        printWriter.println(str2 + "enabled=" + this.mKeyguardState.enabled);
        printWriter.println(str2 + "offReason=" + WindowManagerPolicyConstants.offReasonToString(this.mKeyguardState.offReason));
        printWriter.println(str2 + "currentUser=" + this.mKeyguardState.currentUser);
        printWriter.println(str2 + "bootCompleted=" + this.mKeyguardState.bootCompleted);
        printWriter.println(str2 + "screenState=" + screenStateToString(this.mKeyguardState.screenState));
        printWriter.println(str2 + "interactiveState=" + interactiveStateToString(this.mKeyguardState.interactiveState));
        KeyguardServiceWrapper keyguardServiceWrapper = this.mKeyguardService;
        if (keyguardServiceWrapper != null) {
            keyguardServiceWrapper.dump(str2, printWriter);
        }
    }

    public static String screenStateToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? Integer.toString(i) : "SCREEN_STATE_TURNING_OFF" : "SCREEN_STATE_ON" : "SCREEN_STATE_TURNING_ON" : "SCREEN_STATE_OFF";
    }

    public static String interactiveStateToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? Integer.toString(i) : "INTERACTIVE_STATE_GOING_TO_SLEEP" : "INTERACTIVE_STATE_AWAKE" : "INTERACTIVE_STATE_WAKING" : "INTERACTIVE_STATE_SLEEP";
    }
}
