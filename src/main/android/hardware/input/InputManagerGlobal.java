package android.hardware.input;

import android.hardware.input.IInputManager;
import android.p008os.IBinder;
import android.p008os.ServiceManager;
/* loaded from: classes2.dex */
public final class InputManagerGlobal {
    private static final String TAG = "InputManagerGlobal";
    private static InputManagerGlobal sInstance;
    private final IInputManager mIm;

    public InputManagerGlobal(IInputManager im) {
        this.mIm = im;
    }

    public static InputManagerGlobal getInstance() {
        InputManagerGlobal inputManagerGlobal;
        IBinder b;
        synchronized (InputManagerGlobal.class) {
            if (sInstance == null && (b = ServiceManager.getService("input")) != null) {
                sInstance = new InputManagerGlobal(IInputManager.Stub.asInterface(b));
            }
            inputManagerGlobal = sInstance;
        }
        return inputManagerGlobal;
    }

    public IInputManager getInputManagerService() {
        return this.mIm;
    }

    public static InputManagerGlobal resetInstance(IInputManager inputManagerService) {
        InputManagerGlobal inputManagerGlobal;
        synchronized (InputManager.class) {
            inputManagerGlobal = new InputManagerGlobal(inputManagerService);
            sInstance = inputManagerGlobal;
        }
        return inputManagerGlobal;
    }

    public static void clearInstance() {
        synchronized (InputManagerGlobal.class) {
            sInstance = null;
        }
    }
}
