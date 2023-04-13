package com.android.server.biometrics;

import android.content.Context;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.IBiometricAuthenticator;
import android.hardware.biometrics.IBiometricSensorReceiver;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
/* loaded from: classes.dex */
public abstract class BiometricSensor {

    /* renamed from: id */
    public final int f1133id;
    public final IBiometricAuthenticator impl;
    public final Context mContext;
    public int mCookie;
    public int mError;
    public int mSensorState;
    @BiometricManager.Authenticators.Types
    public int mUpdatedStrength;
    public final int modality;
    @BiometricManager.Authenticators.Types
    public final int oemStrength;

    public abstract boolean confirmationAlwaysRequired(int i);

    public abstract boolean confirmationSupported();

    public BiometricSensor(Context context, int i, int i2, @BiometricManager.Authenticators.Types int i3, IBiometricAuthenticator iBiometricAuthenticator) {
        this.mContext = context;
        this.f1133id = i;
        this.modality = i2;
        this.oemStrength = i3;
        this.impl = iBiometricAuthenticator;
        this.mUpdatedStrength = i3;
        goToStateUnknown();
    }

    public void goToStateUnknown() {
        this.mSensorState = 0;
        this.mCookie = 0;
        this.mError = 0;
    }

    public void goToStateWaitingForCookie(boolean z, IBinder iBinder, long j, int i, IBiometricSensorReceiver iBiometricSensorReceiver, String str, long j2, int i2, boolean z2) throws RemoteException {
        this.mCookie = i2;
        this.impl.prepareForAuthentication(z, iBinder, j, i, iBiometricSensorReceiver, str, j2, i2, z2);
        this.mSensorState = 1;
    }

    public void goToStateCookieReturnedIfCookieMatches(int i) {
        if (i == this.mCookie) {
            Slog.d("BiometricService/Sensor", "Sensor(" + this.f1133id + ") matched cookie: " + i);
            this.mSensorState = 2;
        }
    }

    public void startSensor() throws RemoteException {
        this.impl.startPreparedClient(this.mCookie);
        this.mSensorState = 3;
    }

    public void goToStateCancelling(IBinder iBinder, String str, long j) throws RemoteException {
        if (this.mSensorState != 4) {
            this.impl.cancelAuthenticationFromService(iBinder, str, j);
            this.mSensorState = 4;
        }
    }

    public void goToStoppedStateIfCookieMatches(int i, int i2) {
        if (i == this.mCookie) {
            Slog.d("BiometricService/Sensor", "Sensor(" + this.f1133id + ") now in STATE_STOPPED");
            this.mError = i2;
            this.mSensorState = 5;
        }
    }

    @BiometricManager.Authenticators.Types
    public int getCurrentStrength() {
        return this.mUpdatedStrength | this.oemStrength;
    }

    public int getSensorState() {
        return this.mSensorState;
    }

    public int getCookie() {
        return this.mCookie;
    }

    public void updateStrength(@BiometricManager.Authenticators.Types int i) {
        this.mUpdatedStrength = i;
        Slog.d("BiometricService/Sensor", ("updateStrength: Before(" + this + ")") + " After(" + this + ")");
    }

    public String toString() {
        return "ID(" + this.f1133id + "), oemStrength: " + this.oemStrength + ", updatedStrength: " + this.mUpdatedStrength + ", modality " + this.modality + ", state: " + this.mSensorState + ", cookie: " + this.mCookie;
    }
}
