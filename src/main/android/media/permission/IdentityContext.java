package android.media.permission;
/* loaded from: classes2.dex */
public class IdentityContext implements SafeCloseable {
    private static ThreadLocal<Identity> sThreadLocalIdentity = new ThreadLocal<>();
    private Identity mPrior = get();

    public static SafeCloseable create(Identity identity) {
        return new IdentityContext(identity);
    }

    public static Identity get() {
        return sThreadLocalIdentity.get();
    }

    public static Identity getNonNull() {
        Identity result = get();
        if (result == null) {
            throw new NullPointerException("Identity context is not set");
        }
        return result;
    }

    private IdentityContext(Identity identity) {
        set(identity);
    }

    @Override // android.media.permission.SafeCloseable, java.lang.AutoCloseable
    public void close() {
        set(this.mPrior);
    }

    private static void set(Identity identity) {
        sThreadLocalIdentity.set(identity);
    }
}
