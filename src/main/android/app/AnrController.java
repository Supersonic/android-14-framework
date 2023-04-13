package android.app;
/* loaded from: classes.dex */
public interface AnrController {
    long getAnrDelayMillis(String str, int i);

    boolean onAnrDelayCompleted(String str, int i);

    void onAnrDelayStarted(String str, int i);
}
