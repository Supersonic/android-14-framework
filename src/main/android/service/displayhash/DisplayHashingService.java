package android.service.displayhash;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.HardwareBuffer;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.RemoteCallback;
import android.service.displayhash.DisplayHashingService;
import android.service.displayhash.IDisplayHashingService;
import android.view.displayhash.DisplayHash;
import android.view.displayhash.DisplayHashResultCallback;
import android.view.displayhash.VerifiedDisplayHash;
import com.android.internal.util.function.HexConsumer;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.util.Map;
import java.util.function.BiConsumer;
@SystemApi
/* loaded from: classes3.dex */
public abstract class DisplayHashingService extends Service {
    public static final String EXTRA_INTERVAL_BETWEEN_REQUESTS = "android.service.displayhash.extra.INTERVAL_BETWEEN_REQUESTS";
    public static final String EXTRA_VERIFIED_DISPLAY_HASH = "android.service.displayhash.extra.VERIFIED_DISPLAY_HASH";
    @SystemApi
    public static final String SERVICE_INTERFACE = "android.service.displayhash.DisplayHashingService";
    private Handler mHandler;
    private DisplayHashingServiceWrapper mWrapper;

    public abstract void onGenerateDisplayHash(byte[] bArr, HardwareBuffer hardwareBuffer, Rect rect, String str, DisplayHashResultCallback displayHashResultCallback);

    public abstract Map<String, DisplayHashParams> onGetDisplayHashAlgorithms();

    public abstract int onGetIntervalBetweenRequestsMillis();

    public abstract VerifiedDisplayHash onVerifyDisplayHash(byte[] bArr, DisplayHash displayHash);

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mWrapper = new DisplayHashingServiceWrapper();
        this.mHandler = new Handler(Looper.getMainLooper(), null, true);
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return this.mWrapper;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void verifyDisplayHash(byte[] salt, DisplayHash displayHash, RemoteCallback callback) {
        VerifiedDisplayHash verifiedDisplayHash = onVerifyDisplayHash(salt, displayHash);
        Bundle data = new Bundle();
        data.putParcelable(EXTRA_VERIFIED_DISPLAY_HASH, verifiedDisplayHash);
        callback.sendResult(data);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getDisplayHashAlgorithms(RemoteCallback callback) {
        Map<String, DisplayHashParams> displayHashParams = onGetDisplayHashAlgorithms();
        Bundle data = new Bundle();
        for (Map.Entry<String, DisplayHashParams> entry : displayHashParams.entrySet()) {
            data.putParcelable(entry.getKey(), entry.getValue());
        }
        callback.sendResult(data);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getDurationBetweenRequestsMillis(RemoteCallback callback) {
        int durationBetweenRequestMillis = onGetIntervalBetweenRequestsMillis();
        Bundle data = new Bundle();
        data.putInt(EXTRA_INTERVAL_BETWEEN_REQUESTS, durationBetweenRequestMillis);
        callback.sendResult(data);
    }

    /* loaded from: classes3.dex */
    private final class DisplayHashingServiceWrapper extends IDisplayHashingService.Stub {
        private DisplayHashingServiceWrapper() {
        }

        @Override // android.service.displayhash.IDisplayHashingService
        public void generateDisplayHash(byte[] salt, HardwareBuffer buffer, Rect bounds, String hashAlgorithm, final RemoteCallback callback) {
            DisplayHashingService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new HexConsumer() { // from class: android.service.displayhash.DisplayHashingService$DisplayHashingServiceWrapper$$ExternalSyntheticLambda3
                @Override // com.android.internal.util.function.HexConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
                    ((DisplayHashingService) obj).onGenerateDisplayHash((byte[]) obj2, (HardwareBuffer) obj3, (Rect) obj4, (String) obj5, (DisplayHashingService.DisplayHashingServiceWrapper.displayhashDisplayHashResultCallbackC25281) obj6);
                }
            }, DisplayHashingService.this, salt, buffer, bounds, hashAlgorithm, new DisplayHashResultCallback() { // from class: android.service.displayhash.DisplayHashingService.DisplayHashingServiceWrapper.1
                @Override // android.view.displayhash.DisplayHashResultCallback
                public void onDisplayHashResult(DisplayHash displayHash) {
                    Bundle result = new Bundle();
                    result.putParcelable(DisplayHashResultCallback.EXTRA_DISPLAY_HASH, displayHash);
                    callback.sendResult(result);
                }

                @Override // android.view.displayhash.DisplayHashResultCallback
                public void onDisplayHashError(int errorCode) {
                    Bundle result = new Bundle();
                    result.putInt(DisplayHashResultCallback.EXTRA_DISPLAY_HASH_ERROR_CODE, errorCode);
                    callback.sendResult(result);
                }
            }));
        }

        @Override // android.service.displayhash.IDisplayHashingService
        public void verifyDisplayHash(byte[] salt, DisplayHash displayHash, RemoteCallback callback) {
            DisplayHashingService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: android.service.displayhash.DisplayHashingService$DisplayHashingServiceWrapper$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.function.QuadConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                    ((DisplayHashingService) obj).verifyDisplayHash((byte[]) obj2, (DisplayHash) obj3, (RemoteCallback) obj4);
                }
            }, DisplayHashingService.this, salt, displayHash, callback));
        }

        @Override // android.service.displayhash.IDisplayHashingService
        public void getDisplayHashAlgorithms(RemoteCallback callback) {
            DisplayHashingService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.displayhash.DisplayHashingService$DisplayHashingServiceWrapper$$ExternalSyntheticLambda2
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((DisplayHashingService) obj).getDisplayHashAlgorithms((RemoteCallback) obj2);
                }
            }, DisplayHashingService.this, callback));
        }

        @Override // android.service.displayhash.IDisplayHashingService
        public void getIntervalBetweenRequestsMillis(RemoteCallback callback) {
            DisplayHashingService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.displayhash.DisplayHashingService$DisplayHashingServiceWrapper$$ExternalSyntheticLambda1
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((DisplayHashingService) obj).getDurationBetweenRequestsMillis((RemoteCallback) obj2);
                }
            }, DisplayHashingService.this, callback));
        }
    }
}
