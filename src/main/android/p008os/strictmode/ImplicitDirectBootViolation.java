package android.p008os.strictmode;
/* renamed from: android.os.strictmode.ImplicitDirectBootViolation */
/* loaded from: classes3.dex */
public final class ImplicitDirectBootViolation extends Violation {
    public ImplicitDirectBootViolation() {
        super("Implicitly relying on automatic Direct Boot filtering; request explicit filtering with PackageManager.MATCH_DIRECT_BOOT flags");
    }
}
