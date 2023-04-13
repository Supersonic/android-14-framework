package com.android.server.location.settings;

import android.util.AtomicFile;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.Preconditions;
import com.android.server.location.settings.SettingsStore.VersionedSettings;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
/* loaded from: classes.dex */
public abstract class SettingsStore<T extends VersionedSettings> {
    @GuardedBy({"this"})
    public T mCache;
    public final AtomicFile mFile;
    @GuardedBy({"this"})
    public boolean mInitialized;

    /* loaded from: classes.dex */
    public interface VersionedSettings {
        int getVersion();
    }

    public abstract void onChange(T t, T t2);

    public abstract T read(int i, DataInput dataInput) throws IOException;

    public abstract void write(DataOutput dataOutput, T t) throws IOException;

    public SettingsStore(File file) {
        this.mFile = new AtomicFile(file);
    }

    public final synchronized void initializeCache() {
        if (!this.mInitialized) {
            if (this.mFile.exists()) {
                try {
                    DataInputStream dataInputStream = new DataInputStream(this.mFile.openRead());
                    try {
                        T read = read(dataInputStream.readInt(), dataInputStream);
                        this.mCache = read;
                        Preconditions.checkState(read.getVersion() < Integer.MAX_VALUE);
                        dataInputStream.close();
                    } catch (Throwable th) {
                        try {
                            dataInputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                } catch (IOException e) {
                    Log.e("LocationManagerService", "error reading location settings (" + this.mFile + "), falling back to defaults", e);
                }
            }
            if (this.mCache == null) {
                try {
                    T read2 = read(Integer.MAX_VALUE, new DataInputStream(new ByteArrayInputStream(new byte[0])));
                    this.mCache = read2;
                    Preconditions.checkState(read2.getVersion() < Integer.MAX_VALUE);
                } catch (IOException e2) {
                    throw new AssertionError(e2);
                }
            }
            this.mInitialized = true;
        }
    }

    public final synchronized T get() {
        initializeCache();
        return this.mCache;
    }

    public synchronized void update(Function<T, T> function) {
        initializeCache();
        T t = this.mCache;
        T apply = function.apply(t);
        Objects.requireNonNull(apply);
        T t2 = apply;
        if (t.equals(t2)) {
            return;
        }
        this.mCache = t2;
        Preconditions.checkState(t2.getVersion() < Integer.MAX_VALUE);
        writeLazily(t2);
        onChange(t, t2);
    }

    @VisibleForTesting
    public synchronized void flushFile() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        BackgroundThread.getExecutor().execute(new SettingsStore$$ExternalSyntheticLambda1(countDownLatch));
        countDownLatch.await();
    }

    @VisibleForTesting
    public synchronized void deleteFile() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BackgroundThread.getExecutor().execute(new Runnable() { // from class: com.android.server.location.settings.SettingsStore$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SettingsStore.this.lambda$deleteFile$0(countDownLatch);
            }
        });
        countDownLatch.await();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteFile$0(CountDownLatch countDownLatch) {
        this.mFile.delete();
        countDownLatch.countDown();
    }

    public final void writeLazily(final T t) {
        BackgroundThread.getExecutor().execute(new Runnable() { // from class: com.android.server.location.settings.SettingsStore$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                SettingsStore.this.lambda$writeLazily$1(t);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$writeLazily$1(VersionedSettings versionedSettings) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = this.mFile.startWrite();
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            dataOutputStream.writeInt(versionedSettings.getVersion());
            write(dataOutputStream, versionedSettings);
            this.mFile.finishWrite(fileOutputStream);
        } catch (IOException e) {
            this.mFile.failWrite(fileOutputStream);
            Log.e("LocationManagerService", "failure serializing location settings", e);
        } catch (Throwable th) {
            this.mFile.failWrite(fileOutputStream);
            throw th;
        }
    }
}
