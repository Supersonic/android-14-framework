package android.window;

import android.annotation.SystemApi;
import android.p008os.RemoteException;
@SystemApi
/* loaded from: classes4.dex */
public abstract class TaskFpsCallback {
    public abstract void onFpsReported(float f);

    private static void dispatchOnFpsReported(ITaskFpsCallback listener, float fps) {
        try {
            listener.onFpsReported(fps);
        } catch (RemoteException e) {
        }
    }
}
