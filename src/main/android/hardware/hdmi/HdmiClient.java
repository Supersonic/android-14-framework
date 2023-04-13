package android.hardware.hdmi;

import android.annotation.SystemApi;
import android.hardware.hdmi.HdmiClient;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.IHdmiControlCallback;
import android.hardware.hdmi.IHdmiVendorCommandListener;
import android.p008os.Binder;
import android.p008os.RemoteException;
import android.util.Log;
import com.android.internal.util.FunctionalUtils;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes2.dex */
public abstract class HdmiClient {
    private static final String TAG = "HdmiClient";
    private static final int UNKNOWN_VENDOR_ID = 16777215;
    private IHdmiVendorCommandListener mIHdmiVendorCommandListener;
    final IHdmiControlService mService;

    /* loaded from: classes2.dex */
    public interface OnDeviceSelectedListener {
        void onDeviceSelected(int i, int i2);
    }

    abstract int getDeviceType();

    /* JADX INFO: Access modifiers changed from: package-private */
    public HdmiClient(IHdmiControlService service) {
        this.mService = service;
    }

    public void selectDevice(int logicalAddress, Executor executor, OnDeviceSelectedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null.");
        }
        if (executor == null) {
            throw new IllegalArgumentException("executor must not be null.");
        }
        try {
            this.mService.deviceSelect(logicalAddress, getCallbackWrapper(logicalAddress, executor, listener));
        } catch (RemoteException e) {
            Log.m109e(TAG, "failed to select device: ", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.hardware.hdmi.HdmiClient$1 */
    /* loaded from: classes2.dex */
    public class BinderC11531 extends IHdmiControlCallback.Stub {
        final /* synthetic */ Executor val$executor;
        final /* synthetic */ OnDeviceSelectedListener val$listener;
        final /* synthetic */ int val$logicalAddress;

        BinderC11531(Executor executor, OnDeviceSelectedListener onDeviceSelectedListener, int i) {
            this.val$executor = executor;
            this.val$listener = onDeviceSelectedListener;
            this.val$logicalAddress = i;
        }

        @Override // android.hardware.hdmi.IHdmiControlCallback
        public void onComplete(final int result) {
            final Executor executor = this.val$executor;
            final OnDeviceSelectedListener onDeviceSelectedListener = this.val$listener;
            final int i = this.val$logicalAddress;
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.hardware.hdmi.HdmiClient$1$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    executor.execute(new Runnable() { // from class: android.hardware.hdmi.HdmiClient$1$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            HdmiClient.OnDeviceSelectedListener.this.onDeviceSelected(r2, r3);
                        }
                    });
                }
            });
        }
    }

    private static IHdmiControlCallback getCallbackWrapper(int logicalAddress, Executor executor, OnDeviceSelectedListener listener) {
        return new BinderC11531(executor, listener, logicalAddress);
    }

    public HdmiDeviceInfo getActiveSource() {
        try {
            return this.mService.getActiveSource();
        } catch (RemoteException e) {
            Log.m109e(TAG, "getActiveSource threw exception ", e);
            return null;
        }
    }

    public void sendKeyEvent(int keyCode, boolean isPressed) {
        try {
            this.mService.sendKeyEvent(getDeviceType(), keyCode, isPressed);
        } catch (RemoteException e) {
            Log.m109e(TAG, "sendKeyEvent threw exception ", e);
        }
    }

    public void sendVolumeKeyEvent(int keyCode, boolean isPressed) {
        try {
            this.mService.sendVolumeKeyEvent(getDeviceType(), keyCode, isPressed);
        } catch (RemoteException e) {
            Log.m109e(TAG, "sendVolumeKeyEvent threw exception ", e);
            throw e.rethrowFromSystemServer();
        }
    }

    public void sendVendorCommand(int targetAddress, byte[] params, boolean hasVendorId) {
        try {
            this.mService.sendVendorCommand(getDeviceType(), targetAddress, params, hasVendorId);
        } catch (RemoteException e) {
            Log.m109e(TAG, "failed to send vendor command: ", e);
        }
    }

    public void setVendorCommandListener(HdmiControlManager.VendorCommandListener listener) {
        setVendorCommandListener(listener, 16777215);
    }

    public void setVendorCommandListener(HdmiControlManager.VendorCommandListener listener, int vendorId) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        if (this.mIHdmiVendorCommandListener != null) {
            throw new IllegalStateException("listener was already set");
        }
        try {
            IHdmiVendorCommandListener wrappedListener = getListenerWrapper(listener);
            this.mService.addVendorCommandListener(wrappedListener, vendorId);
            this.mIHdmiVendorCommandListener = wrappedListener;
        } catch (RemoteException e) {
            Log.m109e(TAG, "failed to set vendor command listener: ", e);
        }
    }

    private static IHdmiVendorCommandListener getListenerWrapper(final HdmiControlManager.VendorCommandListener listener) {
        return new IHdmiVendorCommandListener.Stub() { // from class: android.hardware.hdmi.HdmiClient.2
            @Override // android.hardware.hdmi.IHdmiVendorCommandListener
            public void onReceived(int srcAddress, int destAddress, byte[] params, boolean hasVendorId) {
                HdmiControlManager.VendorCommandListener.this.onReceived(srcAddress, destAddress, params, hasVendorId);
            }

            @Override // android.hardware.hdmi.IHdmiVendorCommandListener
            public void onControlStateChanged(boolean enabled, int reason) {
                HdmiControlManager.VendorCommandListener.this.onControlStateChanged(enabled, reason);
            }
        };
    }
}
