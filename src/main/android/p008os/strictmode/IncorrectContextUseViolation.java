package android.p008os.strictmode;
/* renamed from: android.os.strictmode.IncorrectContextUseViolation */
/* loaded from: classes3.dex */
public final class IncorrectContextUseViolation extends Violation {
    public IncorrectContextUseViolation(String message, Throwable originStack) {
        super(message);
        initCause(originStack);
    }
}
