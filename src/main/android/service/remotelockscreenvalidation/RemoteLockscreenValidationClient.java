package android.service.remotelockscreenvalidation;

import android.content.ComponentName;
import android.content.Context;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public interface RemoteLockscreenValidationClient {
    void disconnect();

    boolean isServiceAvailable();

    void validateLockscreenGuess(byte[] bArr, IRemoteLockscreenValidationCallback iRemoteLockscreenValidationCallback);

    static RemoteLockscreenValidationClient create(Context context, ComponentName serviceComponent) {
        return new RemoteLockscreenValidationClientImpl(context, null, serviceComponent);
    }

    static RemoteLockscreenValidationClient create(Context context, Executor bgExecutor, ComponentName serviceComponent) {
        return new RemoteLockscreenValidationClientImpl(context, bgExecutor, serviceComponent);
    }
}
