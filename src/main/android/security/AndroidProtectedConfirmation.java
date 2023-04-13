package android.security;

import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.ServiceSpecificException;
import android.security.apc.IConfirmationCallback;
import android.security.apc.IProtectedConfirmation;
import android.util.Log;
/* loaded from: classes3.dex */
public class AndroidProtectedConfirmation {
    public static final int ERROR_ABORTED = 2;
    public static final int ERROR_CANCELED = 1;
    public static final int ERROR_IGNORED = 4;
    public static final int ERROR_OK = 0;
    public static final int ERROR_OPERATION_PENDING = 3;
    public static final int ERROR_SYSTEM_ERROR = 5;
    public static final int ERROR_UNIMPLEMENTED = 6;
    public static final int FLAG_UI_OPTION_INVERTED = 1;
    public static final int FLAG_UI_OPTION_MAGNIFIED = 2;
    private static final String TAG = "AndroidProtectedConfirmation";
    private IProtectedConfirmation mProtectedConfirmation = null;

    private synchronized IProtectedConfirmation getService() {
        if (this.mProtectedConfirmation == null) {
            this.mProtectedConfirmation = IProtectedConfirmation.Stub.asInterface(ServiceManager.getService("android.security.apc"));
        }
        return this.mProtectedConfirmation;
    }

    public int presentConfirmationPrompt(IConfirmationCallback listener, String promptText, byte[] extraData, String locale, int uiOptionsAsFlags) {
        try {
            getService().presentPrompt(listener, promptText, extraData, locale, uiOptionsAsFlags);
            return 0;
        } catch (RemoteException e) {
            Log.m103w(TAG, "Cannot connect to keystore", e);
            return 5;
        } catch (ServiceSpecificException e2) {
            return e2.errorCode;
        }
    }

    public int cancelConfirmationPrompt(IConfirmationCallback listener) {
        try {
            getService().cancelPrompt(listener);
            return 0;
        } catch (RemoteException e) {
            Log.m103w(TAG, "Cannot connect to keystore", e);
            return 5;
        } catch (ServiceSpecificException e2) {
            return e2.errorCode;
        }
    }

    public boolean isConfirmationPromptSupported() {
        try {
            return getService().isSupported();
        } catch (RemoteException e) {
            Log.m103w(TAG, "Cannot connect to keystore", e);
            return false;
        }
    }
}
