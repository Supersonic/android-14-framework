package android.p008os.strictmode;
/* renamed from: android.os.strictmode.SqliteObjectLeakedViolation */
/* loaded from: classes3.dex */
public final class SqliteObjectLeakedViolation extends Violation {
    public SqliteObjectLeakedViolation(String message, Throwable originStack) {
        super(message);
        initCause(originStack);
    }
}
