package android.app;

import android.app.Activity;
import android.app.IScreenCaptureObserver;
import android.app.ScreenCaptureCallbackHandler;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.util.ArrayMap;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class ScreenCaptureCallbackHandler {
    private final IBinder mActivityToken;
    private final ScreenCaptureObserver mObserver;
    private final ArrayMap<Activity.ScreenCaptureCallback, ScreenCaptureRegistration> mScreenCaptureRegistrations;

    public ScreenCaptureCallbackHandler(IBinder activityToken) {
        ArrayMap<Activity.ScreenCaptureCallback, ScreenCaptureRegistration> arrayMap = new ArrayMap<>();
        this.mScreenCaptureRegistrations = arrayMap;
        this.mActivityToken = activityToken;
        this.mObserver = new ScreenCaptureObserver(arrayMap);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ScreenCaptureRegistration {
        Activity.ScreenCaptureCallback mCallback;
        Executor mExecutor;

        ScreenCaptureRegistration(Executor executor, Activity.ScreenCaptureCallback callback) {
            this.mExecutor = executor;
            this.mCallback = callback;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ScreenCaptureObserver extends IScreenCaptureObserver.Stub {
        ArrayMap<Activity.ScreenCaptureCallback, ScreenCaptureRegistration> mRegistrations;

        ScreenCaptureObserver(ArrayMap<Activity.ScreenCaptureCallback, ScreenCaptureRegistration> registrations) {
            this.mRegistrations = registrations;
        }

        @Override // android.app.IScreenCaptureObserver
        public void onScreenCaptured() {
            for (final ScreenCaptureRegistration registration : this.mRegistrations.values()) {
                registration.mExecutor.execute(new Runnable() { // from class: android.app.ScreenCaptureCallbackHandler$ScreenCaptureObserver$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ScreenCaptureCallbackHandler.ScreenCaptureRegistration.this.mCallback.onScreenCaptured();
                    }
                });
            }
        }
    }

    public void registerScreenCaptureCallback(Executor executor, Activity.ScreenCaptureCallback callback) {
        ScreenCaptureRegistration registration = new ScreenCaptureRegistration(executor, callback);
        synchronized (this.mScreenCaptureRegistrations) {
            if (this.mScreenCaptureRegistrations.containsKey(callback)) {
                throw new IllegalStateException("Capture observer already registered with the activity");
            }
            this.mScreenCaptureRegistrations.put(callback, registration);
            if (this.mScreenCaptureRegistrations.size() == 1) {
                try {
                    ActivityTaskManager.getService().registerScreenCaptureObserver(this.mActivityToken, this.mObserver);
                } catch (RemoteException e) {
                    e.rethrowFromSystemServer();
                }
            }
        }
    }

    public void unregisterScreenCaptureCallback(Activity.ScreenCaptureCallback callback) {
        synchronized (this.mScreenCaptureRegistrations) {
            if (!this.mScreenCaptureRegistrations.containsKey(callback)) {
                throw new IllegalStateException("Capture observer not registered with the activity");
            }
            this.mScreenCaptureRegistrations.remove(callback);
            if (this.mScreenCaptureRegistrations.size() == 0) {
                try {
                    ActivityTaskManager.getService().unregisterScreenCaptureObserver(this.mActivityToken, this.mObserver);
                } catch (RemoteException e) {
                    e.rethrowFromSystemServer();
                }
            }
        }
    }
}
