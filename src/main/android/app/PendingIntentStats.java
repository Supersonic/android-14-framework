package android.app;
/* loaded from: classes.dex */
public class PendingIntentStats {
    public final int count;
    public final int sizeKb;
    public final int uid;

    public PendingIntentStats(int uid, int count, int sizeKb) {
        this.uid = uid;
        this.count = count;
        this.sizeKb = sizeKb;
    }
}
