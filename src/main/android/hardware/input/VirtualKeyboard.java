package android.hardware.input;

import android.annotation.SystemApi;
import android.companion.virtual.IVirtualDevice;
import android.p008os.IBinder;
import android.p008os.RemoteException;
@SystemApi
/* loaded from: classes2.dex */
public class VirtualKeyboard extends VirtualInputDevice {
    private final int mUnsupportedKeyCode;

    @Override // android.hardware.input.VirtualInputDevice, java.io.Closeable, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() {
        super.close();
    }

    @Override // android.hardware.input.VirtualInputDevice
    public /* bridge */ /* synthetic */ int getInputDeviceId() {
        return super.getInputDeviceId();
    }

    public VirtualKeyboard(IVirtualDevice virtualDevice, IBinder token) {
        super(virtualDevice, token);
        this.mUnsupportedKeyCode = 23;
    }

    public void sendKeyEvent(VirtualKeyEvent event) {
        try {
            if (23 == event.getKeyCode()) {
                throw new IllegalArgumentException("Unsupported key code " + event.getKeyCode() + " sent to a VirtualKeyboard input device.");
            }
            this.mVirtualDevice.sendKeyEvent(this.mToken, event);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
