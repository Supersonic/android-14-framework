package android.p008os.strictmode;
/* renamed from: android.os.strictmode.ServiceConnectionLeakedViolation */
/* loaded from: classes3.dex */
public final class ServiceConnectionLeakedViolation extends Violation {
    public ServiceConnectionLeakedViolation(Throwable originStack) {
        super(null);
        setStackTrace(originStack.getStackTrace());
    }
}
