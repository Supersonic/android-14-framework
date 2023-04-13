package com.android.server.biometrics.log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.biometrics.IBiometricContextListener;
import android.hardware.biometrics.common.OperationContext;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;
import android.view.WindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.InstanceId;
import com.android.internal.statusbar.ISessionListener;
import com.android.internal.statusbar.IStatusBarService;
import com.android.server.biometrics.sensors.AuthSessionCoordinator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public final class BiometricContextProvider implements BiometricContext {
    public static BiometricContextProvider sInstance;
    public final AmbientDisplayConfiguration mAmbientDisplayConfiguration;
    public final AuthSessionCoordinator mAuthSessionCoordinator;
    public final Handler mHandler;
    public final WindowManager mWindowManager;
    public final Map<OperationContextExt, Consumer<OperationContext>> mSubscribers = new ConcurrentHashMap();
    public final Map<Integer, BiometricContextSessionInfo> mSession = new ConcurrentHashMap();
    public boolean mIsAod = false;
    public boolean mIsAwake = false;
    public int mDockState = 0;
    public int mFoldState = 0;
    @VisibleForTesting
    final BroadcastReceiver mDockStateReceiver = new BroadcastReceiver() { // from class: com.android.server.biometrics.log.BiometricContextProvider.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            BiometricContextProvider.this.mDockState = intent.getIntExtra("android.intent.extra.DOCK_STATE", 0);
        }
    };

    public static BiometricContextProvider defaultProvider(Context context) {
        synchronized (BiometricContextProvider.class) {
            if (sInstance == null) {
                try {
                    sInstance = new BiometricContextProvider(context, (WindowManager) context.getSystemService("window"), new AmbientDisplayConfiguration(context), IStatusBarService.Stub.asInterface(ServiceManager.getServiceOrThrow("statusbar")), null, new AuthSessionCoordinator());
                } catch (ServiceManager.ServiceNotFoundException e) {
                    throw new IllegalStateException("Failed to find required service", e);
                }
            }
        }
        return sInstance;
    }

    @VisibleForTesting
    public BiometricContextProvider(Context context, WindowManager windowManager, AmbientDisplayConfiguration ambientDisplayConfiguration, IStatusBarService iStatusBarService, Handler handler, AuthSessionCoordinator authSessionCoordinator) {
        this.mWindowManager = windowManager;
        this.mAmbientDisplayConfiguration = ambientDisplayConfiguration;
        this.mAuthSessionCoordinator = authSessionCoordinator;
        this.mHandler = handler;
        subscribeBiometricContextListener(iStatusBarService);
        subscribeDockState(context);
    }

    public final void subscribeBiometricContextListener(IStatusBarService iStatusBarService) {
        try {
            iStatusBarService.setBiometicContextListener(new IBiometricContextListener.Stub() { // from class: com.android.server.biometrics.log.BiometricContextProvider.2
                public void onDozeChanged(boolean z, boolean z2) {
                    boolean z3 = true;
                    boolean z4 = z && isAodEnabled();
                    if (BiometricContextProvider.this.mIsAod == z4 && BiometricContextProvider.this.mIsAwake == z2) {
                        z3 = false;
                    }
                    if (z3) {
                        BiometricContextProvider.this.mIsAod = z4;
                        BiometricContextProvider.this.mIsAwake = z2;
                        BiometricContextProvider.this.notifyChanged();
                    }
                }

                public void onFoldChanged(int i) {
                    BiometricContextProvider.this.mFoldState = i;
                }

                public final boolean isAodEnabled() {
                    return BiometricContextProvider.this.mAmbientDisplayConfiguration.alwaysOnEnabled(-2);
                }
            });
            iStatusBarService.registerSessionListener(3, new ISessionListener.Stub() { // from class: com.android.server.biometrics.log.BiometricContextProvider.3
                public void onSessionStarted(int i, InstanceId instanceId) {
                    BiometricContextProvider.this.mSession.put(Integer.valueOf(i), new BiometricContextSessionInfo(instanceId));
                }

                public void onSessionEnded(int i, InstanceId instanceId) {
                    BiometricContextSessionInfo biometricContextSessionInfo = (BiometricContextSessionInfo) BiometricContextProvider.this.mSession.remove(Integer.valueOf(i));
                    if (biometricContextSessionInfo == null || instanceId == null || biometricContextSessionInfo.getId() == instanceId.getId()) {
                        return;
                    }
                    Slog.w("BiometricContextProvider", "session id mismatch");
                }
            });
        } catch (RemoteException e) {
            Slog.e("BiometricContextProvider", "Unable to register biometric context listener", e);
        }
    }

    public final void subscribeDockState(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.DOCK_EVENT");
        context.registerReceiver(this.mDockStateReceiver, intentFilter);
    }

    @Override // com.android.server.biometrics.log.BiometricContext
    public OperationContextExt updateContext(OperationContextExt operationContextExt, boolean z) {
        return operationContextExt.update(this);
    }

    @Override // com.android.server.biometrics.log.BiometricContext
    public BiometricContextSessionInfo getKeyguardEntrySessionInfo() {
        return this.mSession.get(1);
    }

    @Override // com.android.server.biometrics.log.BiometricContext
    public BiometricContextSessionInfo getBiometricPromptSessionInfo() {
        return this.mSession.get(2);
    }

    @Override // com.android.server.biometrics.log.BiometricContext
    public boolean isAod() {
        return this.mIsAod;
    }

    @Override // com.android.server.biometrics.log.BiometricContext
    public boolean isAwake() {
        return this.mIsAwake;
    }

    @Override // com.android.server.biometrics.log.BiometricContext
    public boolean isDisplayOn() {
        return this.mWindowManager.getDefaultDisplay().getState() == 2;
    }

    @Override // com.android.server.biometrics.log.BiometricContext
    public int getDockedState() {
        return this.mDockState;
    }

    @Override // com.android.server.biometrics.log.BiometricContext
    public int getFoldState() {
        return this.mFoldState;
    }

    @Override // com.android.server.biometrics.log.BiometricContext
    public int getCurrentRotation() {
        return this.mWindowManager.getDefaultDisplay().getRotation();
    }

    @Override // com.android.server.biometrics.log.BiometricContext
    public void subscribe(OperationContextExt operationContextExt, Consumer<OperationContext> consumer) {
        this.mSubscribers.put(operationContextExt, consumer);
    }

    @Override // com.android.server.biometrics.log.BiometricContext
    public void unsubscribe(OperationContextExt operationContextExt) {
        this.mSubscribers.remove(operationContextExt);
    }

    @Override // com.android.server.biometrics.log.BiometricContext
    public AuthSessionCoordinator getAuthSessionCoordinator() {
        return this.mAuthSessionCoordinator;
    }

    public final void notifyChanged() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.android.server.biometrics.log.BiometricContextProvider$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricContextProvider.this.notifySubscribers();
                }
            });
        } else {
            notifySubscribers();
        }
    }

    public final void notifySubscribers() {
        this.mSubscribers.forEach(new BiConsumer() { // from class: com.android.server.biometrics.log.BiometricContextProvider$$ExternalSyntheticLambda1
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                BiometricContextProvider.this.lambda$notifySubscribers$0((OperationContextExt) obj, (Consumer) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifySubscribers$0(OperationContextExt operationContextExt, Consumer consumer) {
        consumer.accept(operationContextExt.update(this).toAidlContext());
    }

    public String toString() {
        return "[keyguard session: " + getKeyguardEntrySessionInfo() + ", bp session: " + getBiometricPromptSessionInfo() + ", isAod: " + isAod() + ", isAwake: " + isAwake() + ", isDisplayOn: " + isDisplayOn() + ", dock: " + getDockedState() + ", rotation: " + getCurrentRotation() + "]";
    }
}
