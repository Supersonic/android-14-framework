package android.view;

import android.content.Context;
import com.android.internal.C4057R;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes4.dex */
public final class MotionPredictor {
    private final Context mContext;
    private final long mPtr;

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeGetNativeMotionPredictorFinalizer();

    private static native long nativeInitialize(int i);

    private static native boolean nativeIsPredictionAvailable(long j, int i, int i2);

    private static native MotionEvent nativePredict(long j, long j2);

    private static native void nativeRecord(long j, MotionEvent motionEvent);

    /* loaded from: classes4.dex */
    private static class RegistryHolder {
        public static final NativeAllocationRegistry REGISTRY = NativeAllocationRegistry.createMalloced(MotionPredictor.class.getClassLoader(), MotionPredictor.nativeGetNativeMotionPredictorFinalizer());

        private RegistryHolder() {
        }
    }

    public MotionPredictor(Context context) {
        this.mContext = context;
        int offsetNanos = context.getResources().getInteger(C4057R.integer.config_motionPredictionOffsetNanos);
        long nativeInitialize = nativeInitialize(offsetNanos);
        this.mPtr = nativeInitialize;
        RegistryHolder.REGISTRY.registerNativeAllocation(this, nativeInitialize);
    }

    public void record(MotionEvent event) {
        if (!isPredictionEnabled()) {
            return;
        }
        nativeRecord(this.mPtr, event);
    }

    public MotionEvent predict(long predictionTimeNanos) {
        if (!isPredictionEnabled()) {
            return null;
        }
        return nativePredict(this.mPtr, predictionTimeNanos);
    }

    private boolean isPredictionEnabled() {
        if (!this.mContext.getResources().getBoolean(C4057R.bool.config_enableMotionPrediction)) {
            return false;
        }
        return true;
    }

    public boolean isPredictionAvailable(int deviceId, int source) {
        return isPredictionEnabled() && nativeIsPredictionAvailable(this.mPtr, deviceId, source);
    }
}
