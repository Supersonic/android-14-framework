package com.android.server.biometrics.sensors;

import android.content.pm.UserInfo;
import android.hardware.biometrics.IBiometricStateListener;
import android.hardware.biometrics.SensorPropertiesInternal;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserManager;
import android.util.Slog;
import com.android.server.biometrics.Utils;
import com.android.server.biometrics.sensors.BiometricServiceProvider;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class BiometricStateCallback<T extends BiometricServiceProvider<P>, P extends SensorPropertiesInternal> implements ClientMonitorCallback, IBinder.DeathRecipient {
    public final UserManager mUserManager;
    public final CopyOnWriteArrayList<IBiometricStateListener> mBiometricStateListeners = new CopyOnWriteArrayList<>();
    public List<T> mProviders = List.of();
    public int mBiometricState = 0;

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
    }

    public BiometricStateCallback(UserManager userManager) {
        this.mUserManager = userManager;
    }

    public synchronized void start(List<T> list) {
        this.mProviders = Collections.unmodifiableList(list);
        broadcastCurrentEnrollmentState(null);
    }

    public int getBiometricState() {
        return this.mBiometricState;
    }

    @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
    public void onClientStarted(BaseClientMonitor baseClientMonitor) {
        int i = this.mBiometricState;
        if (baseClientMonitor instanceof AuthenticationClient) {
            AuthenticationClient authenticationClient = (AuthenticationClient) baseClientMonitor;
            if (authenticationClient.isKeyguard()) {
                this.mBiometricState = 2;
            } else if (authenticationClient.isBiometricPrompt()) {
                this.mBiometricState = 3;
            } else {
                this.mBiometricState = 4;
            }
        } else if (baseClientMonitor instanceof EnrollClient) {
            this.mBiometricState = 1;
        } else {
            Slog.w("BiometricStateCallback", "Other authentication client: " + Utils.getClientName(baseClientMonitor));
            this.mBiometricState = 0;
        }
        Slog.d("BiometricStateCallback", "State updated from " + i + " to " + this.mBiometricState + ", client " + baseClientMonitor);
        notifyBiometricStateListeners(this.mBiometricState);
    }

    @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
    public void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z) {
        this.mBiometricState = 0;
        Slog.d("BiometricStateCallback", "Client finished, state updated to " + this.mBiometricState + ", client " + baseClientMonitor);
        if (baseClientMonitor instanceof EnrollmentModifier) {
            EnrollmentModifier enrollmentModifier = (EnrollmentModifier) baseClientMonitor;
            boolean hasEnrollmentStateChanged = enrollmentModifier.hasEnrollmentStateChanged();
            Slog.d("BiometricStateCallback", "Enrollment state changed: " + hasEnrollmentStateChanged);
            if (hasEnrollmentStateChanged) {
                notifyAllEnrollmentStateChanged(baseClientMonitor.getTargetUserId(), baseClientMonitor.getSensorId(), enrollmentModifier.hasEnrollments());
            }
        }
        notifyBiometricStateListeners(this.mBiometricState);
    }

    public final void notifyBiometricStateListeners(int i) {
        Iterator<IBiometricStateListener> it = this.mBiometricStateListeners.iterator();
        while (it.hasNext()) {
            try {
                it.next().onStateChanged(i);
            } catch (RemoteException e) {
                Slog.e("BiometricStateCallback", "Remote exception in biometric state change", e);
            }
        }
    }

    @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
    public void onBiometricAction(int i) {
        Iterator<IBiometricStateListener> it = this.mBiometricStateListeners.iterator();
        while (it.hasNext()) {
            try {
                it.next().onBiometricAction(i);
            } catch (RemoteException e) {
                Slog.e("BiometricStateCallback", "Remote exception in onBiometricAction", e);
            }
        }
    }

    public synchronized void registerBiometricStateListener(IBiometricStateListener iBiometricStateListener) {
        this.mBiometricStateListeners.add(iBiometricStateListener);
        broadcastCurrentEnrollmentState(iBiometricStateListener);
        try {
            iBiometricStateListener.asBinder().linkToDeath(this, 0);
        } catch (RemoteException e) {
            Slog.e("BiometricStateCallback", "Failed to link to death", e);
        }
    }

    public final synchronized void broadcastCurrentEnrollmentState(IBiometricStateListener iBiometricStateListener) {
        for (T t : this.mProviders) {
            for (SensorPropertiesInternal sensorPropertiesInternal : t.getSensorProperties()) {
                for (UserInfo userInfo : this.mUserManager.getAliveUsers()) {
                    boolean hasEnrollments = t.hasEnrollments(sensorPropertiesInternal.sensorId, userInfo.id);
                    if (iBiometricStateListener != null) {
                        notifyEnrollmentStateChanged(iBiometricStateListener, userInfo.id, sensorPropertiesInternal.sensorId, hasEnrollments);
                    } else {
                        notifyAllEnrollmentStateChanged(userInfo.id, sensorPropertiesInternal.sensorId, hasEnrollments);
                    }
                }
            }
        }
    }

    public final void notifyAllEnrollmentStateChanged(int i, int i2, boolean z) {
        Iterator<IBiometricStateListener> it = this.mBiometricStateListeners.iterator();
        while (it.hasNext()) {
            notifyEnrollmentStateChanged(it.next(), i, i2, z);
        }
    }

    public final void notifyEnrollmentStateChanged(IBiometricStateListener iBiometricStateListener, int i, int i2, boolean z) {
        try {
            iBiometricStateListener.onEnrollmentsChanged(i, i2, z);
        } catch (RemoteException e) {
            Slog.e("BiometricStateCallback", "Remote exception", e);
        }
    }

    public void binderDied(final IBinder iBinder) {
        Slog.w("BiometricStateCallback", "Callback binder died: " + iBinder);
        if (this.mBiometricStateListeners.removeIf(new Predicate() { // from class: com.android.server.biometrics.sensors.BiometricStateCallback$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$binderDied$0;
                lambda$binderDied$0 = BiometricStateCallback.lambda$binderDied$0(iBinder, (IBiometricStateListener) obj);
                return lambda$binderDied$0;
            }
        })) {
            Slog.w("BiometricStateCallback", "Removed dead listener for " + iBinder);
            return;
        }
        Slog.w("BiometricStateCallback", "No dead listeners found");
    }

    public static /* synthetic */ boolean lambda$binderDied$0(IBinder iBinder, IBiometricStateListener iBiometricStateListener) {
        return iBiometricStateListener.asBinder().equals(iBinder);
    }
}
