package android.p008os.strictmode;
/* renamed from: android.os.strictmode.InstanceCountViolation */
/* loaded from: classes3.dex */
public class InstanceCountViolation extends Violation {
    private static final StackTraceElement[] FAKE_STACK = {new StackTraceElement("android.os.StrictMode", "setClassInstanceLimit", "StrictMode.java", 1)};
    private final long mInstances;

    public InstanceCountViolation(Class klass, long instances, int limit) {
        super(klass.toString() + "; instances=" + instances + "; limit=" + limit);
        setStackTrace(FAKE_STACK);
        this.mInstances = instances;
    }

    public long getNumberOfInstances() {
        return this.mInstances;
    }
}
