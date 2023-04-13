package android.hardware.input;

import android.annotation.SystemApi;
import android.companion.virtual.IVirtualDevice;
import android.graphics.PointF;
import android.p008os.IBinder;
import android.p008os.RemoteException;
@SystemApi
/* loaded from: classes2.dex */
public class VirtualMouse extends VirtualInputDevice {
    @Override // android.hardware.input.VirtualInputDevice, java.io.Closeable, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() {
        super.close();
    }

    @Override // android.hardware.input.VirtualInputDevice
    public /* bridge */ /* synthetic */ int getInputDeviceId() {
        return super.getInputDeviceId();
    }

    public VirtualMouse(IVirtualDevice virtualDevice, IBinder token) {
        super(virtualDevice, token);
    }

    public void sendButtonEvent(VirtualMouseButtonEvent event) {
        try {
            this.mVirtualDevice.sendButtonEvent(this.mToken, event);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void sendScrollEvent(VirtualMouseScrollEvent event) {
        try {
            this.mVirtualDevice.sendScrollEvent(this.mToken, event);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void sendRelativeEvent(VirtualMouseRelativeEvent event) {
        try {
            this.mVirtualDevice.sendRelativeEvent(this.mToken, event);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public PointF getCursorPosition() {
        try {
            return this.mVirtualDevice.getCursorPosition(this.mToken);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
