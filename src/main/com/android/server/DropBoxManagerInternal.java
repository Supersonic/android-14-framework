package com.android.server;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
/* loaded from: classes.dex */
public abstract class DropBoxManagerInternal {

    /* loaded from: classes.dex */
    public interface EntrySource extends Closeable {
        @Override // java.io.Closeable, java.lang.AutoCloseable
        default void close() throws IOException {
        }

        default long length() {
            return 0L;
        }

        void writeTo(FileDescriptor fileDescriptor) throws IOException;
    }

    public abstract void addEntry(String str, EntrySource entrySource, int i);
}
