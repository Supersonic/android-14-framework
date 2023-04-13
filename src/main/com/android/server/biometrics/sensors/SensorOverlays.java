package com.android.server.biometrics.sensors;

import android.hardware.fingerprint.ISidefpsController;
import android.hardware.fingerprint.IUdfpsOverlay;
import android.hardware.fingerprint.IUdfpsOverlayController;
import android.hardware.fingerprint.IUdfpsOverlayControllerCallback;
import android.os.RemoteException;
import android.util.Slog;
import java.util.Optional;
/* loaded from: classes.dex */
public final class SensorOverlays {
    public final Optional<ISidefpsController> mSidefpsController;
    public final Optional<IUdfpsOverlay> mUdfpsOverlay;
    public final Optional<IUdfpsOverlayController> mUdfpsOverlayController;

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface OverlayControllerConsumer<T> {
        void accept(T t) throws RemoteException;
    }

    public SensorOverlays(IUdfpsOverlayController iUdfpsOverlayController, ISidefpsController iSidefpsController, IUdfpsOverlay iUdfpsOverlay) {
        this.mUdfpsOverlayController = Optional.ofNullable(iUdfpsOverlayController);
        this.mSidefpsController = Optional.ofNullable(iSidefpsController);
        this.mUdfpsOverlay = Optional.ofNullable(iUdfpsOverlay);
    }

    public void show(int i, int i2, final AcquisitionClient<?> acquisitionClient) {
        if (this.mSidefpsController.isPresent()) {
            try {
                this.mSidefpsController.get().show(i, i2);
            } catch (RemoteException e) {
                Slog.e("SensorOverlays", "Remote exception when showing the side-fps overlay", e);
            }
        }
        if (this.mUdfpsOverlayController.isPresent()) {
            try {
                this.mUdfpsOverlayController.get().showUdfpsOverlay(acquisitionClient.getRequestId(), i, i2, new IUdfpsOverlayControllerCallback.Stub() { // from class: com.android.server.biometrics.sensors.SensorOverlays.1
                    public void onUserCanceled() {
                        acquisitionClient.onUserCanceled();
                    }
                });
            } catch (RemoteException e2) {
                Slog.e("SensorOverlays", "Remote exception when showing the UDFPS overlay", e2);
            }
        }
        if (this.mUdfpsOverlay.isPresent()) {
            try {
                this.mUdfpsOverlay.get().show(acquisitionClient.getRequestId(), i, i2);
            } catch (RemoteException e3) {
                Slog.e("SensorOverlays", "Remote exception when showing the new UDFPS overlay", e3);
            }
        }
    }

    public void hide(int i) {
        if (this.mSidefpsController.isPresent()) {
            try {
                this.mSidefpsController.get().hide(i);
            } catch (RemoteException e) {
                Slog.e("SensorOverlays", "Remote exception when hiding the side-fps overlay", e);
            }
        }
        if (this.mUdfpsOverlayController.isPresent()) {
            try {
                this.mUdfpsOverlayController.get().hideUdfpsOverlay(i);
            } catch (RemoteException e2) {
                Slog.e("SensorOverlays", "Remote exception when hiding the UDFPS overlay", e2);
            }
        }
        if (this.mUdfpsOverlay.isPresent()) {
            try {
                this.mUdfpsOverlay.get().hide(i);
            } catch (RemoteException e3) {
                Slog.e("SensorOverlays", "Remote exception when hiding the new udfps overlay", e3);
            }
        }
    }

    public void ifUdfps(OverlayControllerConsumer<IUdfpsOverlayController> overlayControllerConsumer) {
        if (this.mUdfpsOverlayController.isPresent()) {
            try {
                overlayControllerConsumer.accept(this.mUdfpsOverlayController.get());
            } catch (RemoteException e) {
                Slog.e("SensorOverlays", "Remote exception using overlay controller", e);
            }
        }
    }
}
