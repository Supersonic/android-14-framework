package android.service.attention;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.service.attention.IAttentionService;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public abstract class AttentionService extends Service {
    public static final int ATTENTION_FAILURE_CAMERA_PERMISSION_ABSENT = 6;
    public static final int ATTENTION_FAILURE_CANCELLED = 3;
    public static final int ATTENTION_FAILURE_PREEMPTED = 4;
    public static final int ATTENTION_FAILURE_TIMED_OUT = 5;
    public static final int ATTENTION_FAILURE_UNKNOWN = 2;
    public static final int ATTENTION_SUCCESS_ABSENT = 0;
    public static final int ATTENTION_SUCCESS_PRESENT = 1;
    private static final String LOG_TAG = "AttentionService";
    public static final double PROXIMITY_UNKNOWN = -1.0d;
    public static final String SERVICE_INTERFACE = "android.service.attention.AttentionService";
    private final IAttentionService.Stub mBinder = new IAttentionService.Stub() { // from class: android.service.attention.AttentionService.1
        @Override // android.service.attention.IAttentionService
        public void checkAttention(IAttentionCallback callback) {
            Preconditions.checkNotNull(callback);
            AttentionService.this.onCheckAttention(new AttentionCallback(callback));
        }

        @Override // android.service.attention.IAttentionService
        public void cancelAttentionCheck(IAttentionCallback callback) {
            Preconditions.checkNotNull(callback);
            AttentionService.this.onCancelAttentionCheck(new AttentionCallback(callback));
        }

        @Override // android.service.attention.IAttentionService
        public void onStartProximityUpdates(IProximityUpdateCallback callback) {
            Objects.requireNonNull(callback);
            AttentionService.this.onStartProximityUpdates(new ProximityUpdateCallback(callback));
        }

        @Override // android.service.attention.IAttentionService
        public void onStopProximityUpdates() {
            AttentionService.this.onStopProximityUpdates();
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface AttentionFailureCodes {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface AttentionSuccessCodes {
    }

    public abstract void onCancelAttentionCheck(AttentionCallback attentionCallback);

    public abstract void onCheckAttention(AttentionCallback attentionCallback);

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return this.mBinder;
        }
        return null;
    }

    public void onStartProximityUpdates(ProximityUpdateCallback callback) {
        Slog.m90w(LOG_TAG, "Override this method.");
    }

    public void onStopProximityUpdates() {
        Slog.m90w(LOG_TAG, "Override this method.");
    }

    /* loaded from: classes3.dex */
    public static final class AttentionCallback {
        private final IAttentionCallback mCallback;

        private AttentionCallback(IAttentionCallback callback) {
            this.mCallback = callback;
        }

        public void onSuccess(int result, long timestamp) {
            try {
                this.mCallback.onSuccess(result, timestamp);
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        }

        public void onFailure(int error) {
            try {
                this.mCallback.onFailure(error);
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class ProximityUpdateCallback {
        private final WeakReference<IProximityUpdateCallback> mCallback;

        private ProximityUpdateCallback(IProximityUpdateCallback callback) {
            this.mCallback = new WeakReference<>(callback);
        }

        public void onProximityUpdate(double distance) {
            try {
                if (this.mCallback.get() != null) {
                    this.mCallback.get().onProximityUpdate(distance);
                }
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        }
    }
}
