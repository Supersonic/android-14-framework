package android.p008os;
/* renamed from: android.os.DeadSystemRuntimeException */
/* loaded from: classes3.dex */
public class DeadSystemRuntimeException extends RuntimeException {
    public DeadSystemRuntimeException() {
        super(new DeadSystemException());
    }
}
