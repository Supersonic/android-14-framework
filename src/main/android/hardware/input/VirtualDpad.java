package android.hardware.input;

import android.annotation.SystemApi;
import android.companion.virtual.IVirtualDevice;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
@SystemApi
/* loaded from: classes2.dex */
public class VirtualDpad extends VirtualInputDevice {
    private final Set<Integer> mSupportedKeyCodes;

    @Override // android.hardware.input.VirtualInputDevice, java.io.Closeable, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() {
        super.close();
    }

    @Override // android.hardware.input.VirtualInputDevice
    public /* bridge */ /* synthetic */ int getInputDeviceId() {
        return super.getInputDeviceId();
    }

    public VirtualDpad(IVirtualDevice virtualDevice, IBinder token) {
        super(virtualDevice, token);
        this.mSupportedKeyCodes = Collections.unmodifiableSet(new HashSet(Arrays.asList(4, 19, 20, 21, 22, 23)));
    }

    public void sendKeyEvent(VirtualKeyEvent event) {
        try {
            if (!this.mSupportedKeyCodes.contains(Integer.valueOf(event.getKeyCode()))) {
                throw new IllegalArgumentException("Unsupported key code " + event.getKeyCode() + " sent to a VirtualDpad input device.");
            }
            this.mVirtualDevice.sendDpadKeyEvent(this.mToken, event);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
