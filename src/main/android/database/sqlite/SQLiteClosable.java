package android.database.sqlite;

import java.io.Closeable;
/* loaded from: classes.dex */
public abstract class SQLiteClosable implements Closeable {
    private int mReferenceCount = 1;

    protected abstract void onAllReferencesReleased();

    @Deprecated
    protected void onAllReferencesReleasedFromContainer() {
        onAllReferencesReleased();
    }

    public void acquireReference() {
        synchronized (this) {
            int i = this.mReferenceCount;
            if (i <= 0) {
                throw new IllegalStateException("attempt to re-open an already-closed object: " + this);
            }
            this.mReferenceCount = i + 1;
        }
    }

    public void releaseReference() {
        boolean refCountIsZero;
        synchronized (this) {
            boolean z = true;
            int i = this.mReferenceCount - 1;
            this.mReferenceCount = i;
            if (i != 0) {
                z = false;
            }
            refCountIsZero = z;
        }
        if (refCountIsZero) {
            onAllReferencesReleased();
        }
    }

    @Deprecated
    public void releaseReferenceFromContainer() {
        boolean refCountIsZero;
        synchronized (this) {
            boolean z = true;
            int i = this.mReferenceCount - 1;
            this.mReferenceCount = i;
            if (i != 0) {
                z = false;
            }
            refCountIsZero = z;
        }
        if (refCountIsZero) {
            onAllReferencesReleasedFromContainer();
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        releaseReference();
    }
}
