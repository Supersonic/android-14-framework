package com.android.server.sensorprivacy;
/* loaded from: classes2.dex */
public class SensorState {
    public long mLastChange;
    public int mStateType;

    public static int enabledToState(boolean z) {
        return z ? 1 : 2;
    }

    public SensorState(int i) {
        this.mStateType = i;
        this.mLastChange = SensorPrivacyService.getCurrentTimeMillis();
    }

    public SensorState(int i, long j) {
        this.mStateType = i;
        this.mLastChange = Math.min(SensorPrivacyService.getCurrentTimeMillis(), j);
    }

    public SensorState(SensorState sensorState) {
        this.mStateType = sensorState.getState();
        this.mLastChange = sensorState.getLastChange();
    }

    public boolean setState(int i) {
        if (this.mStateType != i) {
            this.mStateType = i;
            this.mLastChange = SensorPrivacyService.getCurrentTimeMillis();
            return true;
        }
        return false;
    }

    public int getState() {
        return this.mStateType;
    }

    public long getLastChange() {
        return this.mLastChange;
    }

    public SensorState(boolean z) {
        this(enabledToState(z));
    }

    public boolean setEnabled(boolean z) {
        return setState(enabledToState(z));
    }

    public boolean isEnabled() {
        return getState() == 1;
    }
}
