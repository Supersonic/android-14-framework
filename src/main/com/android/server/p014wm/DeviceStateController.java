package com.android.server.p014wm;

import android.content.Context;
import android.hardware.devicestate.DeviceStateManager;
import android.os.Handler;
import android.os.HandlerExecutor;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ArrayUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.DeviceStateController */
/* loaded from: classes2.dex */
public final class DeviceStateController implements DeviceStateManager.DeviceStateCallback {
    public final int mConcurrentDisplayDeviceState;
    public int mCurrentState;
    public final DeviceStateManager mDeviceStateManager;
    public final int[] mFoldedDeviceStates;
    public final int[] mHalfFoldedDeviceStates;
    public final boolean mMatchBuiltInDisplayOrientationToDefaultDisplay;
    public final int[] mOpenDeviceStates;
    public final int[] mRearDisplayDeviceStates;
    public final int[] mReverseRotationAroundZAxisStates;
    @GuardedBy({"this"})
    public final List<Consumer<DeviceState>> mDeviceStateCallbacks = new ArrayList();
    public DeviceState mCurrentDeviceState = DeviceState.UNKNOWN;

    /* renamed from: com.android.server.wm.DeviceStateController$DeviceState */
    /* loaded from: classes2.dex */
    public enum DeviceState {
        UNKNOWN,
        OPEN,
        FOLDED,
        HALF_FOLDED,
        REAR,
        CONCURRENT
    }

    public DeviceStateController(Context context, Handler handler) {
        DeviceStateManager deviceStateManager = (DeviceStateManager) context.getSystemService(DeviceStateManager.class);
        this.mDeviceStateManager = deviceStateManager;
        this.mOpenDeviceStates = context.getResources().getIntArray(17236113);
        this.mHalfFoldedDeviceStates = context.getResources().getIntArray(17236077);
        this.mFoldedDeviceStates = context.getResources().getIntArray(17236072);
        this.mRearDisplayDeviceStates = context.getResources().getIntArray(17236118);
        this.mConcurrentDisplayDeviceState = context.getResources().getInteger(17694813);
        this.mReverseRotationAroundZAxisStates = context.getResources().getIntArray(17236030);
        this.mMatchBuiltInDisplayOrientationToDefaultDisplay = context.getResources().getBoolean(17891739);
        if (deviceStateManager != null) {
            deviceStateManager.registerCallback(new HandlerExecutor(handler), this);
        }
    }

    public void registerDeviceStateCallback(Consumer<DeviceState> consumer) {
        synchronized (this) {
            this.mDeviceStateCallbacks.add(consumer);
        }
    }

    public boolean shouldReverseRotationDirectionAroundZAxis() {
        return ArrayUtils.contains(this.mReverseRotationAroundZAxisStates, this.mCurrentState);
    }

    public boolean shouldMatchBuiltInDisplayOrientationToReverseDefaultDisplay() {
        return this.mMatchBuiltInDisplayOrientationToDefaultDisplay;
    }

    public void onStateChanged(int i) {
        DeviceState deviceState;
        this.mCurrentState = i;
        if (ArrayUtils.contains(this.mHalfFoldedDeviceStates, i)) {
            deviceState = DeviceState.HALF_FOLDED;
        } else if (ArrayUtils.contains(this.mFoldedDeviceStates, i)) {
            deviceState = DeviceState.FOLDED;
        } else if (ArrayUtils.contains(this.mRearDisplayDeviceStates, i)) {
            deviceState = DeviceState.REAR;
        } else if (ArrayUtils.contains(this.mOpenDeviceStates, i)) {
            deviceState = DeviceState.OPEN;
        } else if (i == this.mConcurrentDisplayDeviceState) {
            deviceState = DeviceState.CONCURRENT;
        } else {
            deviceState = DeviceState.UNKNOWN;
        }
        DeviceState deviceState2 = this.mCurrentDeviceState;
        if (deviceState2 == null || !deviceState2.equals(deviceState)) {
            this.mCurrentDeviceState = deviceState;
            synchronized (this) {
                for (Consumer<DeviceState> consumer : this.mDeviceStateCallbacks) {
                    consumer.accept(this.mCurrentDeviceState);
                }
            }
        }
    }
}
