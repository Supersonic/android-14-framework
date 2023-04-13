package android.p008os.strictmode;
/* renamed from: android.os.strictmode.IntentReceiverLeakedViolation */
/* loaded from: classes3.dex */
public final class IntentReceiverLeakedViolation extends Violation {
    public IntentReceiverLeakedViolation(Throwable originStack) {
        super(null);
        setStackTrace(originStack.getStackTrace());
    }
}
