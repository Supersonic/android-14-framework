package android.media.permission;
/* loaded from: classes2.dex */
class CompositeSafeCloseable implements SafeCloseable {
    private final SafeCloseable[] mChildren;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CompositeSafeCloseable(SafeCloseable... children) {
        this.mChildren = children;
    }

    @Override // android.media.permission.SafeCloseable, java.lang.AutoCloseable
    public void close() {
        for (int i = this.mChildren.length - 1; i >= 0; i--) {
            this.mChildren[i].close();
        }
    }
}
