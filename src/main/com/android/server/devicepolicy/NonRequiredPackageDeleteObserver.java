package com.android.server.devicepolicy;

import android.content.pm.IPackageDeleteObserver;
import android.util.Log;
import android.util.Slog;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public final class NonRequiredPackageDeleteObserver extends IPackageDeleteObserver.Stub {
    public final CountDownLatch mLatch;
    public final AtomicInteger mPackageCount;
    public boolean mSuccess;

    public NonRequiredPackageDeleteObserver(int i) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        this.mPackageCount = atomicInteger;
        this.mLatch = new CountDownLatch(i);
        atomicInteger.set(i);
    }

    public void packageDeleted(String str, int i) {
        if (i != 1) {
            Slog.e("DevicePolicyManager", "Failed to delete package: " + str);
            this.mLatch.notifyAll();
            return;
        }
        if (this.mPackageCount.decrementAndGet() == 0) {
            this.mSuccess = true;
            Slog.i("DevicePolicyManager", "All non-required system apps with launcher icon, and all disallowed apps have been uninstalled.");
        }
        this.mLatch.countDown();
    }

    public boolean awaitPackagesDeletion() {
        try {
            this.mLatch.await(30L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.w("DevicePolicyManager", "Interrupted while waiting for package deletion", e);
            Thread.currentThread().interrupt();
        }
        return this.mSuccess;
    }
}
