package com.android.server.policy;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Handler;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public abstract class WakeGestureListener {
    public final Handler mHandler;
    public Sensor mSensor;
    public final SensorManager mSensorManager;
    public boolean mTriggerRequested;
    public final Object mLock = new Object();
    public final TriggerEventListener mListener = new TriggerEventListener() { // from class: com.android.server.policy.WakeGestureListener.1
        @Override // android.hardware.TriggerEventListener
        public void onTrigger(TriggerEvent triggerEvent) {
            synchronized (WakeGestureListener.this.mLock) {
                WakeGestureListener.this.mTriggerRequested = false;
                WakeGestureListener.this.mHandler.post(WakeGestureListener.this.mWakeUpRunnable);
            }
        }
    };
    public final Runnable mWakeUpRunnable = new Runnable() { // from class: com.android.server.policy.WakeGestureListener.2
        @Override // java.lang.Runnable
        public void run() {
            WakeGestureListener.this.onWakeUp();
        }
    };

    public abstract void onWakeUp();

    public WakeGestureListener(Context context, Handler handler) {
        SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
        this.mSensorManager = sensorManager;
        this.mHandler = handler;
        this.mSensor = sensorManager.getDefaultSensor(23);
    }

    public boolean isSupported() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mSensor != null;
        }
        return z;
    }

    public void requestWakeUpTrigger() {
        synchronized (this.mLock) {
            Sensor sensor = this.mSensor;
            if (sensor != null && !this.mTriggerRequested) {
                this.mTriggerRequested = true;
                this.mSensorManager.requestTriggerSensor(this.mListener, sensor);
            }
        }
    }

    public void cancelWakeUpTrigger() {
        synchronized (this.mLock) {
            Sensor sensor = this.mSensor;
            if (sensor != null && this.mTriggerRequested) {
                this.mTriggerRequested = false;
                this.mSensorManager.cancelTriggerSensor(this.mListener, sensor);
            }
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        synchronized (this.mLock) {
            printWriter.println(str + "WakeGestureListener");
            String str2 = str + "  ";
            printWriter.println(str2 + "mTriggerRequested=" + this.mTriggerRequested);
            printWriter.println(str2 + "mSensor=" + this.mSensor);
        }
    }
}
