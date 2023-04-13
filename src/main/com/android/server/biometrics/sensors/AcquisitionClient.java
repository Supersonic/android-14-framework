package com.android.server.biometrics.sensors;

import android.content.Context;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public abstract class AcquisitionClient<T> extends HalClientMonitor<T> implements ErrorConsumer {
    public boolean mAlreadyCancelled;
    public final PowerManager mPowerManager;
    public boolean mShouldSendErrorToClient;
    public final boolean mShouldVibrate;
    public static final VibrationAttributes HARDWARE_FEEDBACK_VIBRATION_ATTRIBUTES = VibrationAttributes.createForUsage(50);
    public static final VibrationEffect SUCCESS_VIBRATION_EFFECT = VibrationEffect.get(0);
    public static final VibrationEffect ERROR_VIBRATION_EFFECT = VibrationEffect.get(1);

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public boolean isInterruptable() {
        return true;
    }

    public abstract void stopHalOperation();

    public AcquisitionClient(Context context, Supplier<T> supplier, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, String str, int i2, int i3, boolean z, BiometricLogger biometricLogger, BiometricContext biometricContext) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i, str, i2, i3, biometricLogger, biometricContext);
        this.mShouldSendErrorToClient = true;
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mShouldVibrate = z;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
        try {
            getListener().onError(getSensorId(), getCookie(), 1, 0);
        } catch (RemoteException e) {
            Slog.e("Biometrics/AcquisitionClient", "Unable to send error", e);
        }
    }

    @Override // com.android.server.biometrics.sensors.ErrorConsumer
    public void onError(int i, int i2) {
        onErrorInternal(i, i2, true);
    }

    public void onUserCanceled() {
        Slog.d("Biometrics/AcquisitionClient", "onUserCanceled");
        onErrorInternal(10, 0, false);
        stopHalOperation();
    }

    public void onErrorInternal(int i, int i2, boolean z) {
        Slog.d("Biometrics/AcquisitionClient", "onErrorInternal code: " + i + ", finish: " + z);
        if (this.mShouldSendErrorToClient) {
            getLogger().logOnError(getContext(), getOperationContext(), i, i2, getTargetUserId());
            try {
                if (getListener() != null) {
                    this.mShouldSendErrorToClient = false;
                    getListener().onError(getSensorId(), getCookie(), i, i2);
                }
            } catch (RemoteException e) {
                Slog.w("Biometrics/AcquisitionClient", "Failed to invoke sendError", e);
            }
        }
        if (z) {
            ClientMonitorCallback clientMonitorCallback = this.mCallback;
            if (clientMonitorCallback == null) {
                Slog.e("Biometrics/AcquisitionClient", "Callback is null, perhaps the client hasn't been started yet?");
            } else {
                clientMonitorCallback.onClientFinished(this, false);
            }
        }
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void cancel() {
        if (this.mAlreadyCancelled) {
            Slog.w("Biometrics/AcquisitionClient", "Cancel was already requested");
            return;
        }
        stopHalOperation();
        this.mAlreadyCancelled = true;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void cancelWithoutStarting(ClientMonitorCallback clientMonitorCallback) {
        Slog.d("Biometrics/AcquisitionClient", "cancelWithoutStarting: " + this);
        try {
            if (getListener() != null) {
                getListener().onError(getSensorId(), getCookie(), 5, 0);
            }
        } catch (RemoteException e) {
            Slog.w("Biometrics/AcquisitionClient", "Failed to invoke sendError", e);
        }
        clientMonitorCallback.onClientFinished(this, true);
    }

    public void onAcquired(int i, int i2) {
        onAcquiredInternal(i, i2, true);
    }

    public final void onAcquiredInternal(int i, int i2, boolean z) {
        getLogger().logOnAcquired(getContext(), getOperationContext(), i, i2, getTargetUserId());
        Slog.v("Biometrics/AcquisitionClient", "Acquired: " + i + " " + i2 + ", shouldSend: " + z);
        if (i == 0) {
            notifyUserActivity();
        }
        try {
            if (getListener() == null || !z) {
                return;
            }
            getListener().onAcquired(getSensorId(), i, i2);
        } catch (RemoteException e) {
            Slog.w("Biometrics/AcquisitionClient", "Failed to invoke sendAcquired", e);
            this.mCallback.onClientFinished(this, false);
        }
    }

    public final void notifyUserActivity() {
        this.mPowerManager.userActivity(SystemClock.uptimeMillis(), 2, 0);
    }

    public final void vibrateSuccess() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Vibrator.class);
        if (vibrator == null || !this.mShouldVibrate) {
            return;
        }
        int myUid = Process.myUid();
        String opPackageName = getContext().getOpPackageName();
        VibrationEffect vibrationEffect = SUCCESS_VIBRATION_EFFECT;
        vibrator.vibrate(myUid, opPackageName, vibrationEffect, getClass().getSimpleName() + "::success", HARDWARE_FEEDBACK_VIBRATION_ATTRIBUTES);
    }
}
