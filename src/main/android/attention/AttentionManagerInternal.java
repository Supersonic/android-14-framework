package android.attention;
/* loaded from: classes.dex */
public abstract class AttentionManagerInternal {

    /* loaded from: classes.dex */
    public static abstract class AttentionCallbackInternal {
        public abstract void onFailure(int i);

        public abstract void onSuccess(int i, long j);
    }

    /* loaded from: classes.dex */
    public interface ProximityUpdateCallbackInternal {
        void onProximityUpdate(double d);
    }

    public abstract void cancelAttentionCheck(AttentionCallbackInternal attentionCallbackInternal);

    public abstract boolean checkAttention(long j, AttentionCallbackInternal attentionCallbackInternal);

    public abstract boolean isAttentionServiceSupported();

    public abstract boolean onStartProximityUpdates(ProximityUpdateCallbackInternal proximityUpdateCallbackInternal);

    public abstract void onStopProximityUpdates(ProximityUpdateCallbackInternal proximityUpdateCallbackInternal);
}
