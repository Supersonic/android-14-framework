package android.p008os.strictmode;
/* renamed from: android.os.strictmode.LeakedClosableViolation */
/* loaded from: classes3.dex */
public final class LeakedClosableViolation extends Violation {
    public LeakedClosableViolation(String message, Throwable allocationSite) {
        super(message);
        initCause(allocationSite);
    }

    public LeakedClosableViolation(String message) {
        super(message);
    }
}
