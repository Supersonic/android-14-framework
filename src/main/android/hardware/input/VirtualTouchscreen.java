package android.hardware.input;

import android.annotation.SystemApi;
import android.companion.virtual.IVirtualDevice;
import android.p008os.IBinder;
import android.p008os.RemoteException;
@SystemApi
/* loaded from: classes2.dex */
public class VirtualTouchscreen extends VirtualInputDevice {
    @Override // android.hardware.input.VirtualInputDevice, java.io.Closeable, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() {
        super.close();
    }

    @Override // android.hardware.input.VirtualInputDevice
    public /* bridge */ /* synthetic */ int getInputDeviceId() {
        return super.getInputDeviceId();
    }

    public VirtualTouchscreen(IVirtualDevice virtualDevice, IBinder token) {
        super(virtualDevice, token);
    }

    public void sendTouchEvent(VirtualTouchEvent event) {
        try {
            this.mVirtualDevice.sendTouchEvent(this.mToken, event);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
