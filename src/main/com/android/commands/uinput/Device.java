package com.android.commands.uinput;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.os.SomeArgs;
import java.io.IOException;
import java.io.OutputStream;
import org.json.JSONException;
import org.json.JSONObject;
import src.com.android.commands.uinput.InputAbsInfo;
/* loaded from: classes.dex */
public class Device {
    private static final int MSG_CLOSE_UINPUT_DEVICE = 2;
    private static final int MSG_INJECT_EVENT = 3;
    private static final int MSG_OPEN_UINPUT_DEVICE = 1;
    private static final String TAG = "UinputDevice";
    private final SparseArray<InputAbsInfo> mAbsInfo;
    private final Object mCond = new Object();
    private final SparseArray<int[]> mConfiguration;
    private final DeviceHandler mHandler;
    private final int mId;
    private final OutputStream mOutputStream;
    private final HandlerThread mThread;
    private long mTimeToSend;

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeCloseUinputDevice(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeConfigure(int i, int i2, int[] iArr);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeInjectEvent(long j, int i, int i2, int i3);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeOpenUinputDevice(String str, int i, int i2, int i3, int i4, int i5, String str2, DeviceCallback deviceCallback);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeSetAbsInfo(int i, int i2, Parcel parcel);

    static {
        System.loadLibrary("uinputcommand_jni");
    }

    public Device(int id, String name, int vid, int pid, int bus, SparseArray<int[]> configuration, int ffEffectsMax, SparseArray<InputAbsInfo> absInfo, String port) {
        this.mId = id;
        HandlerThread handlerThread = new HandlerThread("UinputDeviceHandler");
        this.mThread = handlerThread;
        handlerThread.start();
        DeviceHandler deviceHandler = new DeviceHandler(handlerThread.getLooper());
        this.mHandler = deviceHandler;
        this.mConfiguration = configuration;
        this.mAbsInfo = absInfo;
        this.mOutputStream = System.out;
        SomeArgs args = SomeArgs.obtain();
        args.argi1 = id;
        args.argi2 = vid;
        args.argi3 = pid;
        args.argi4 = bus;
        args.argi5 = ffEffectsMax;
        if (name == null) {
            args.arg1 = id + ":" + vid + ":" + pid;
        } else {
            args.arg1 = name;
        }
        if (port == null) {
            args.arg2 = "uinput:" + id + ":" + vid + ":" + pid;
        } else {
            args.arg2 = port;
        }
        deviceHandler.obtainMessage(MSG_OPEN_UINPUT_DEVICE, args).sendToTarget();
        this.mTimeToSend = SystemClock.uptimeMillis();
    }

    public void injectEvent(int[] events) {
        Message msg = this.mHandler.obtainMessage(MSG_INJECT_EVENT, events);
        this.mHandler.sendMessageAtTime(msg, this.mTimeToSend);
    }

    public void addDelay(int delay) {
        this.mTimeToSend = Math.max(SystemClock.uptimeMillis(), this.mTimeToSend) + delay;
    }

    public void close() {
        Message msg = this.mHandler.obtainMessage(MSG_CLOSE_UINPUT_DEVICE);
        this.mHandler.sendMessageAtTime(msg, Math.max(SystemClock.uptimeMillis(), this.mTimeToSend) + 1);
        try {
            synchronized (this.mCond) {
                this.mCond.wait();
            }
        } catch (InterruptedException e) {
        }
    }

    /* loaded from: classes.dex */
    private class DeviceHandler extends Handler {
        private int mBarrierToken;
        private long mPtr;

        DeviceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Device.MSG_OPEN_UINPUT_DEVICE /* 1 */:
                    SomeArgs args = (SomeArgs) msg.obj;
                    this.mPtr = Device.nativeOpenUinputDevice((String) args.arg1, args.argi1, args.argi2, args.argi3, args.argi4, args.argi5, (String) args.arg2, new DeviceCallback());
                    return;
                case Device.MSG_CLOSE_UINPUT_DEVICE /* 2 */:
                    long j = this.mPtr;
                    if (j != 0) {
                        Device.nativeCloseUinputDevice(j);
                        getLooper().quitSafely();
                        this.mPtr = 0L;
                    } else {
                        Log.e(Device.TAG, "Tried to close already closed device.");
                    }
                    Log.i(Device.TAG, "Device closed.");
                    synchronized (Device.this.mCond) {
                        Device.this.mCond.notify();
                    }
                    return;
                case Device.MSG_INJECT_EVENT /* 3 */:
                    if (this.mPtr != 0) {
                        int[] events = (int[]) msg.obj;
                        for (int pos = 0; pos + Device.MSG_CLOSE_UINPUT_DEVICE < events.length; pos += Device.MSG_INJECT_EVENT) {
                            Device.nativeInjectEvent(this.mPtr, events[pos], events[pos + Device.MSG_OPEN_UINPUT_DEVICE], events[pos + Device.MSG_CLOSE_UINPUT_DEVICE]);
                        }
                        return;
                    }
                    return;
                default:
                    throw new IllegalArgumentException("Unknown device message");
            }
        }

        public void pauseEvents() {
            getLooper();
            this.mBarrierToken = Looper.myQueue().postSyncBarrier();
        }

        public void resumeEvents() {
            getLooper();
            Looper.myQueue().removeSyncBarrier(this.mBarrierToken);
            this.mBarrierToken = 0;
        }
    }

    /* loaded from: classes.dex */
    private class DeviceCallback {
        private DeviceCallback() {
        }

        public void onDeviceOpen() {
            Device.this.mHandler.resumeEvents();
        }

        public void onDeviceConfigure(int handle) {
            for (int i = 0; i < Device.this.mConfiguration.size(); i += Device.MSG_OPEN_UINPUT_DEVICE) {
                int key = Device.this.mConfiguration.keyAt(i);
                int[] data = (int[]) Device.this.mConfiguration.get(key);
                Device.nativeConfigure(handle, key, data);
            }
            if (Device.this.mAbsInfo != null) {
                for (int i2 = 0; i2 < Device.this.mAbsInfo.size(); i2 += Device.MSG_OPEN_UINPUT_DEVICE) {
                    int key2 = Device.this.mAbsInfo.keyAt(i2);
                    InputAbsInfo info = (InputAbsInfo) Device.this.mAbsInfo.get(key2);
                    Parcel parcel = Parcel.obtain();
                    info.writeToParcel(parcel, 0);
                    parcel.setDataPosition(0);
                    Device.nativeSetAbsInfo(handle, key2, parcel);
                }
            }
        }

        public void onDeviceVibrating(int value) {
            JSONObject json = new JSONObject();
            try {
                json.put("reason", "vibrating");
                json.put("id", Device.this.mId);
                json.put("status", value);
                try {
                    Device.this.mOutputStream.write(json.toString().getBytes());
                    Device.this.mOutputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (JSONException e2) {
                throw new RuntimeException("Could not create JSON object ", e2);
            }
        }

        public void onDeviceError() {
            Log.e(Device.TAG, "Device error occurred, closing /dev/uinput");
            Message msg = Device.this.mHandler.obtainMessage(Device.MSG_CLOSE_UINPUT_DEVICE);
            msg.setAsynchronous(true);
            msg.sendToTarget();
        }
    }
}
