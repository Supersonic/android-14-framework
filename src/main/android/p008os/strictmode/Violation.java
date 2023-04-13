package android.p008os.strictmode;
/* renamed from: android.os.strictmode.Violation */
/* loaded from: classes3.dex */
public abstract class Violation extends Throwable {
    private int mHashCode;
    private boolean mHashCodeValid;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Violation(String message) {
        super(message);
    }

    public int hashCode() {
        synchronized (this) {
            if (this.mHashCodeValid) {
                return this.mHashCode;
            }
            String message = getMessage();
            Throwable cause = getCause();
            int hashCode = ((((message != null ? message.hashCode() : getClass().hashCode()) * 37) + calcStackTraceHashCode(getStackTrace())) * 37) + (cause != null ? cause.toString().hashCode() : 0);
            this.mHashCodeValid = true;
            this.mHashCode = hashCode;
            return hashCode;
        }
    }

    @Override // java.lang.Throwable
    public synchronized Throwable initCause(Throwable cause) {
        this.mHashCodeValid = false;
        return super.initCause(cause);
    }

    @Override // java.lang.Throwable
    public void setStackTrace(StackTraceElement[] stackTrace) {
        super.setStackTrace(stackTrace);
        synchronized (this) {
            this.mHashCodeValid = false;
        }
    }

    @Override // java.lang.Throwable
    public synchronized Throwable fillInStackTrace() {
        this.mHashCodeValid = false;
        return super.fillInStackTrace();
    }

    private static int calcStackTraceHashCode(StackTraceElement[] stackTrace) {
        int hashCode = 17;
        if (stackTrace != null) {
            for (int i = 0; i < stackTrace.length; i++) {
                if (stackTrace[i] != null) {
                    hashCode = (hashCode * 37) + stackTrace[i].hashCode();
                }
            }
        }
        return hashCode;
    }
}
