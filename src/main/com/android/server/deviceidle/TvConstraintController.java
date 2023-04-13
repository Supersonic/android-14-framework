package com.android.server.deviceidle;

import android.content.Context;
import android.os.Handler;
import com.android.server.DeviceIdleInternal;
import com.android.server.LocalServices;
/* loaded from: classes.dex */
public class TvConstraintController implements ConstraintController {
    public final BluetoothConstraint mBluetoothConstraint;
    public final Context mContext;
    public final DeviceIdleInternal mDeviceIdleService;
    public final Handler mHandler;

    public TvConstraintController(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        DeviceIdleInternal deviceIdleInternal = (DeviceIdleInternal) LocalServices.getService(DeviceIdleInternal.class);
        this.mDeviceIdleService = deviceIdleInternal;
        this.mBluetoothConstraint = context.getPackageManager().hasSystemFeature("android.hardware.bluetooth") ? new BluetoothConstraint(context, handler, deviceIdleInternal) : null;
    }

    public void start() {
        BluetoothConstraint bluetoothConstraint = this.mBluetoothConstraint;
        if (bluetoothConstraint != null) {
            this.mDeviceIdleService.registerDeviceIdleConstraint(bluetoothConstraint, "bluetooth", 1);
        }
    }

    public void stop() {
        BluetoothConstraint bluetoothConstraint = this.mBluetoothConstraint;
        if (bluetoothConstraint != null) {
            this.mDeviceIdleService.unregisterDeviceIdleConstraint(bluetoothConstraint);
        }
    }
}
