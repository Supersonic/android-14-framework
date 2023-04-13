package android.app;
/* loaded from: classes.dex */
public class StackTrace extends Exception {
    public StackTrace(String message) {
        super(message);
    }

    public StackTrace(String message, Throwable innerStackTrace) {
        super(message, innerStackTrace);
    }
}
