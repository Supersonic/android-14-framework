package com.android.commands.hid;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.os.SomeArgs;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class Device {
    private static final int MSG_CLOSE_DEVICE = 5;
    private static final int MSG_OPEN_DEVICE = 1;
    private static final int MSG_SEND_GET_FEATURE_REPORT_REPLY = 3;
    private static final int MSG_SEND_REPORT = 2;
    private static final int MSG_SEND_SET_REPORT_REPLY = 4;
    private static final String TAG = "HidDevice";
    private static final byte UHID_EVENT_TYPE_SET_REPORT = 13;
    private static final byte UHID_EVENT_TYPE_UHID_OUTPUT = 6;
    private final Object mCond = new Object();
    private final SparseArray<byte[]> mFeatureReports;
    private final DeviceHandler mHandler;
    private final int mId;
    private final OutputStream mOutputStream;
    private final Map<ByteBuffer, byte[]> mOutputs;
    private int mResponseId;
    private final HandlerThread mThread;
    private long mTimeToSend;

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeCloseDevice(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeOpenDevice(String str, int i, int i2, int i3, int i4, byte[] bArr, DeviceCallback deviceCallback);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeSendGetFeatureReportReply(long j, int i, byte[] bArr);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeSendReport(long j, byte[] bArr);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeSendSetReportReply(long j, int i, boolean z);

    static {
        System.loadLibrary("hidcommand_jni");
    }

    public Device(int id, String name, int vid, int pid, int bus, byte[] descriptor, byte[] report, SparseArray<byte[]> featureReports, Map<ByteBuffer, byte[]> outputs) {
        this.mId = id;
        HandlerThread handlerThread = new HandlerThread("HidDeviceHandler");
        this.mThread = handlerThread;
        handlerThread.start();
        DeviceHandler deviceHandler = new DeviceHandler(handlerThread.getLooper());
        this.mHandler = deviceHandler;
        this.mFeatureReports = featureReports;
        this.mOutputs = outputs;
        this.mOutputStream = System.out;
        SomeArgs args = SomeArgs.obtain();
        args.argi1 = id;
        args.argi2 = vid;
        args.argi3 = pid;
        args.argi4 = bus;
        if (name != null) {
            args.arg1 = name;
        } else {
            args.arg1 = id + ":" + vid + ":" + pid;
        }
        args.arg2 = descriptor;
        args.arg3 = report;
        deviceHandler.obtainMessage(MSG_OPEN_DEVICE, args).sendToTarget();
        this.mTimeToSend = SystemClock.uptimeMillis();
    }

    public void sendReport(byte[] report) {
        Message msg = this.mHandler.obtainMessage(MSG_SEND_REPORT, report);
        this.mHandler.sendMessageAtTime(msg, this.mTimeToSend);
    }

    public void setGetReportResponse(byte[] report) {
        this.mFeatureReports.put(report[0], report);
    }

    public void sendSetReportReply(boolean success) {
        Message msg = this.mHandler.obtainMessage(MSG_SEND_SET_REPORT_REPLY, this.mResponseId, success ? 1 : 0);
        this.mHandler.sendMessageAtTime(msg, this.mTimeToSend);
    }

    public void addDelay(int delay) {
        this.mTimeToSend = Math.max(SystemClock.uptimeMillis(), this.mTimeToSend) + delay;
    }

    public void close() {
        Message msg = this.mHandler.obtainMessage(MSG_CLOSE_DEVICE);
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

        public DeviceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Device.MSG_OPEN_DEVICE /* 1 */:
                    SomeArgs args = (SomeArgs) msg.obj;
                    this.mPtr = Device.nativeOpenDevice((String) args.arg1, args.argi1, args.argi2, args.argi3, args.argi4, (byte[]) args.arg2, new DeviceCallback());
                    pauseEvents();
                    return;
                case Device.MSG_SEND_REPORT /* 2 */:
                    long j = this.mPtr;
                    if (j != 0) {
                        Device.nativeSendReport(j, (byte[]) msg.obj);
                        return;
                    } else {
                        Log.e(Device.TAG, "Tried to send report to closed device.");
                        return;
                    }
                case Device.MSG_SEND_GET_FEATURE_REPORT_REPLY /* 3 */:
                    long j2 = this.mPtr;
                    if (j2 != 0) {
                        Device.nativeSendGetFeatureReportReply(j2, msg.arg1, (byte[]) msg.obj);
                        return;
                    } else {
                        Log.e(Device.TAG, "Tried to send feature report reply to closed device.");
                        return;
                    }
                case Device.MSG_SEND_SET_REPORT_REPLY /* 4 */:
                    if (this.mPtr != 0) {
                        boolean success = msg.arg2 == Device.MSG_OPEN_DEVICE;
                        Device.nativeSendSetReportReply(this.mPtr, msg.arg1, success);
                        return;
                    }
                    Log.e(Device.TAG, "Tried to send set report reply to closed device.");
                    return;
                case Device.MSG_CLOSE_DEVICE /* 5 */:
                    long j3 = this.mPtr;
                    if (j3 != 0) {
                        Device.nativeCloseDevice(j3);
                        getLooper().quitSafely();
                        this.mPtr = 0L;
                    } else {
                        Log.e(Device.TAG, "Tried to close already closed device.");
                    }
                    synchronized (Device.this.mCond) {
                        Device.this.mCond.notify();
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

        public void onDeviceGetReport(int requestId, int reportId) {
            if (Device.this.mFeatureReports == null) {
                Log.e(Device.TAG, "Received GET_REPORT request for reportId=" + reportId + ", but 'feature_reports' section is not found");
                return;
            }
            byte[] report = (byte[]) Device.this.mFeatureReports.get(reportId);
            if (report == null) {
                Log.e(Device.TAG, "Requested feature report " + reportId + " is not specified");
            }
            Message msg = Device.this.mHandler.obtainMessage(Device.MSG_SEND_GET_FEATURE_REPORT_REPLY, requestId, 0, report);
            msg.setAsynchronous(true);
            Device.this.mHandler.sendMessageAtTime(msg, Device.this.mTimeToSend);
        }

        private void sendReportOutput(byte eventId, byte rtype, byte[] data) {
            JSONObject json = new JSONObject();
            try {
                json.put("eventId", (int) eventId);
                json.put("deviceId", Device.this.mId);
                json.put("reportType", (int) rtype);
                JSONArray dataArray = new JSONArray();
                for (int i = 0; i < data.length; i += Device.MSG_OPEN_DEVICE) {
                    dataArray.put(data[i] & 255);
                }
                json.put("reportData", dataArray);
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

        public void onDeviceSetReport(int id, byte rType, byte[] data) {
            Device.this.mResponseId = id;
            sendReportOutput(Device.UHID_EVENT_TYPE_SET_REPORT, rType, data);
        }

        public void onDeviceOutput(byte rtype, byte[] data) {
            sendReportOutput(Device.UHID_EVENT_TYPE_UHID_OUTPUT, rtype, data);
            if (Device.this.mOutputs == null) {
                Log.e(Device.TAG, "Received OUTPUT request, but 'outputs' section is not found");
                return;
            }
            byte[] response = (byte[]) Device.this.mOutputs.get(ByteBuffer.wrap(data));
            if (response == null) {
                Log.i(Device.TAG, "Requested response for output " + Arrays.toString(data) + " is not found");
                return;
            }
            Message msg = Device.this.mHandler.obtainMessage(Device.MSG_SEND_REPORT, response);
            msg.setAsynchronous(true);
            Device.this.mHandler.sendMessageAtTime(msg, Device.this.mTimeToSend);
        }

        public void onDeviceError() {
            Log.e(Device.TAG, "Device error occurred, closing /dev/uhid");
            Message msg = Device.this.mHandler.obtainMessage(Device.MSG_CLOSE_DEVICE);
            msg.setAsynchronous(true);
            msg.sendToTarget();
        }
    }
}
