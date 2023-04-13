package android.view.inputmethod;

import android.p008os.RemoteException;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public class InputMethodManagerGlobal {
    public static boolean isImeTraceAvailable() {
        return IInputMethodManagerGlobalInvoker.isAvailable();
    }

    public static void startProtoDump(byte[] protoDump, int source, String where, Consumer<RemoteException> exceptionHandler) {
        IInputMethodManagerGlobalInvoker.startProtoDump(protoDump, source, where, exceptionHandler);
    }

    public static void startImeTrace(Consumer<RemoteException> exceptionHandler) {
        IInputMethodManagerGlobalInvoker.startImeTrace(exceptionHandler);
    }

    public static void stopImeTrace(Consumer<RemoteException> exceptionHandler) {
        IInputMethodManagerGlobalInvoker.stopImeTrace(exceptionHandler);
    }

    public static boolean isImeTraceEnabled() {
        return IInputMethodManagerGlobalInvoker.isImeTraceEnabled();
    }

    public static void removeImeSurface(Consumer<RemoteException> exceptionHandler) {
        IInputMethodManagerGlobalInvoker.removeImeSurface(exceptionHandler);
    }
}
