package android.service.ambientcontext;

import android.annotation.SystemApi;
import android.app.Service;
import android.app.ambientcontext.AmbientContextEventRequest;
import android.content.Intent;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.RemoteCallback;
import android.service.ambientcontext.AmbientContextDetectionService;
import android.service.ambientcontext.IAmbientContextDetectionService;
import android.util.Slog;
import java.util.Objects;
import java.util.function.Consumer;
@SystemApi
/* loaded from: classes3.dex */
public abstract class AmbientContextDetectionService extends Service {
    public static final String SERVICE_INTERFACE = "android.service.ambientcontext.AmbientContextDetectionService";
    private static final String TAG = AmbientContextDetectionService.class.getSimpleName();

    public abstract void onQueryServiceStatus(int[] iArr, String str, Consumer<AmbientContextDetectionServiceStatus> consumer);

    public abstract void onStartDetection(AmbientContextEventRequest ambientContextEventRequest, String str, Consumer<AmbientContextDetectionResult> consumer, Consumer<AmbientContextDetectionServiceStatus> consumer2);

    public abstract void onStopDetection(String str);

    /* renamed from: android.service.ambientcontext.AmbientContextDetectionService$1 */
    /* loaded from: classes3.dex */
    class BinderC24301 extends IAmbientContextDetectionService.Stub {
        BinderC24301() {
        }

        @Override // android.service.ambientcontext.IAmbientContextDetectionService
        public void startDetection(AmbientContextEventRequest request, String packageName, final RemoteCallback detectionResultCallback, final RemoteCallback statusCallback) {
            Objects.requireNonNull(request);
            Objects.requireNonNull(packageName);
            Objects.requireNonNull(detectionResultCallback);
            Objects.requireNonNull(statusCallback);
            Consumer<AmbientContextDetectionResult> detectionResultConsumer = new Consumer() { // from class: android.service.ambientcontext.AmbientContextDetectionService$1$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AmbientContextDetectionService.BinderC24301.lambda$startDetection$0(RemoteCallback.this, (AmbientContextDetectionResult) obj);
                }
            };
            Consumer<AmbientContextDetectionServiceStatus> statusConsumer = new Consumer() { // from class: android.service.ambientcontext.AmbientContextDetectionService$1$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AmbientContextDetectionService.BinderC24301.lambda$startDetection$1(RemoteCallback.this, (AmbientContextDetectionServiceStatus) obj);
                }
            };
            AmbientContextDetectionService.this.onStartDetection(request, packageName, detectionResultConsumer, statusConsumer);
            Slog.m98d(AmbientContextDetectionService.TAG, "startDetection " + request);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$startDetection$0(RemoteCallback detectionResultCallback, AmbientContextDetectionResult result) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(AmbientContextDetectionResult.RESULT_RESPONSE_BUNDLE_KEY, result);
            detectionResultCallback.sendResult(bundle);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$startDetection$1(RemoteCallback statusCallback, AmbientContextDetectionServiceStatus status) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(AmbientContextDetectionServiceStatus.STATUS_RESPONSE_BUNDLE_KEY, status);
            statusCallback.sendResult(bundle);
        }

        @Override // android.service.ambientcontext.IAmbientContextDetectionService
        public void stopDetection(String packageName) {
            Objects.requireNonNull(packageName);
            AmbientContextDetectionService.this.onStopDetection(packageName);
        }

        @Override // android.service.ambientcontext.IAmbientContextDetectionService
        public void queryServiceStatus(int[] eventTypes, String packageName, final RemoteCallback callback) {
            Objects.requireNonNull(eventTypes);
            Objects.requireNonNull(packageName);
            Objects.requireNonNull(callback);
            Consumer<AmbientContextDetectionServiceStatus> consumer = new Consumer() { // from class: android.service.ambientcontext.AmbientContextDetectionService$1$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AmbientContextDetectionService.BinderC24301.lambda$queryServiceStatus$2(RemoteCallback.this, (AmbientContextDetectionServiceStatus) obj);
                }
            };
            AmbientContextDetectionService.this.onQueryServiceStatus(eventTypes, packageName, consumer);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$queryServiceStatus$2(RemoteCallback callback, AmbientContextDetectionServiceStatus response) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(AmbientContextDetectionServiceStatus.STATUS_RESPONSE_BUNDLE_KEY, response);
            callback.sendResult(bundle);
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return new BinderC24301();
        }
        return null;
    }
}
