package com.android.server.p011pm;

import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.StrictMode;
import android.os.SystemClock;
import android.p005os.IInstalld;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.server.p011pm.ShortcutService;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import libcore.io.IoUtils;
/* renamed from: com.android.server.pm.ShortcutBitmapSaver */
/* loaded from: classes2.dex */
public class ShortcutBitmapSaver {
    public final long SAVE_WAIT_TIMEOUT_MS = 5000;
    public final Executor mExecutor = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
    @GuardedBy({"mPendingItems"})
    public final Deque<PendingItem> mPendingItems = new LinkedBlockingDeque();
    public final Runnable mRunnable = new Runnable() { // from class: com.android.server.pm.ShortcutBitmapSaver$$ExternalSyntheticLambda1
        @Override // java.lang.Runnable
        public final void run() {
            ShortcutBitmapSaver.this.lambda$new$1();
        }
    };
    public final ShortcutService mService;

    /* renamed from: com.android.server.pm.ShortcutBitmapSaver$PendingItem */
    /* loaded from: classes2.dex */
    public static class PendingItem {
        public final byte[] bytes;
        public final long mInstantiatedUptimeMillis;
        public final ShortcutInfo shortcut;

        public PendingItem(ShortcutInfo shortcutInfo, byte[] bArr) {
            this.shortcut = shortcutInfo;
            this.bytes = bArr;
            this.mInstantiatedUptimeMillis = SystemClock.uptimeMillis();
        }

        public String toString() {
            return "PendingItem{size=" + this.bytes.length + " age=" + (SystemClock.uptimeMillis() - this.mInstantiatedUptimeMillis) + "ms shortcut=" + this.shortcut.toInsecureString() + "}";
        }
    }

    public ShortcutBitmapSaver(ShortcutService shortcutService) {
        this.mService = shortcutService;
    }

    public boolean waitForAllSavesLocked() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.mExecutor.execute(new Runnable() { // from class: com.android.server.pm.ShortcutBitmapSaver$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                countDownLatch.countDown();
            }
        });
        try {
            if (countDownLatch.await(5000L, TimeUnit.MILLISECONDS)) {
                return true;
            }
            this.mService.wtf("Timed out waiting on saving bitmaps.");
            return false;
        } catch (InterruptedException unused) {
            Slog.w("ShortcutService", "interrupted");
            return false;
        }
    }

    public String getBitmapPathMayWaitLocked(ShortcutInfo shortcutInfo) {
        if (waitForAllSavesLocked() && shortcutInfo.hasIconFile()) {
            return shortcutInfo.getBitmapPath();
        }
        return null;
    }

    public void removeIcon(ShortcutInfo shortcutInfo) {
        shortcutInfo.setIconResourceId(0);
        shortcutInfo.setIconResName(null);
        shortcutInfo.setBitmapPath(null);
        shortcutInfo.setIconUri(null);
        shortcutInfo.clearFlags(35340);
    }

    public void saveBitmapLocked(ShortcutInfo shortcutInfo, int i, Bitmap.CompressFormat compressFormat, int i2) {
        Icon icon = shortcutInfo.getIcon();
        Objects.requireNonNull(icon);
        Bitmap bitmap = icon.getBitmap();
        if (bitmap == null) {
            Log.e("ShortcutService", "Missing icon: " + shortcutInfo);
            return;
        }
        StrictMode.ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        try {
            try {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(threadPolicy).permitCustomSlowCalls().build());
                Bitmap shrinkBitmap = ShortcutService.shrinkBitmap(bitmap, i);
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(65536);
                    if (!shrinkBitmap.compress(compressFormat, i2, byteArrayOutputStream)) {
                        Slog.wtf("ShortcutService", "Unable to compress bitmap");
                    }
                    byteArrayOutputStream.flush();
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    byteArrayOutputStream.close();
                    StrictMode.setThreadPolicy(threadPolicy);
                    shortcutInfo.addFlags(2056);
                    if (icon.getType() == 5) {
                        shortcutInfo.addFlags(512);
                    }
                    PendingItem pendingItem = new PendingItem(shortcutInfo, byteArray);
                    synchronized (this.mPendingItems) {
                        this.mPendingItems.add(pendingItem);
                    }
                    this.mExecutor.execute(this.mRunnable);
                } finally {
                    if (shrinkBitmap != bitmap) {
                        shrinkBitmap.recycle();
                    }
                }
            } catch (IOException | OutOfMemoryError | RuntimeException e) {
                Slog.wtf("ShortcutService", "Unable to write bitmap to file", e);
                StrictMode.setThreadPolicy(threadPolicy);
            }
        } catch (Throwable th) {
            StrictMode.setThreadPolicy(threadPolicy);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        do {
        } while (processPendingItems());
    }

    public final boolean processPendingItems() {
        ShortcutInfo shortcutInfo;
        Throwable th;
        File file = null;
        try {
            synchronized (this.mPendingItems) {
                if (this.mPendingItems.size() == 0) {
                    return false;
                }
                PendingItem pop = this.mPendingItems.pop();
                shortcutInfo = pop.shortcut;
                try {
                    if (!shortcutInfo.isIconPendingSave()) {
                        if (shortcutInfo.getBitmapPath() == null) {
                            removeIcon(shortcutInfo);
                        }
                        shortcutInfo.clearFlags(IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES);
                        return true;
                    }
                    try {
                        ShortcutService.FileOutputStreamWithPath openIconFileForWrite = this.mService.openIconFileForWrite(shortcutInfo.getUserId(), shortcutInfo);
                        File file2 = openIconFileForWrite.getFile();
                        try {
                            openIconFileForWrite.write(pop.bytes);
                            IoUtils.closeQuietly(openIconFileForWrite);
                            shortcutInfo.setBitmapPath(file2.getAbsolutePath());
                            if (shortcutInfo.getBitmapPath() == null) {
                                removeIcon(shortcutInfo);
                            }
                            shortcutInfo.clearFlags(IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES);
                            return true;
                        } catch (Throwable th2) {
                            IoUtils.closeQuietly(openIconFileForWrite);
                            throw th2;
                        }
                    } catch (IOException | RuntimeException e) {
                        Slog.e("ShortcutService", "Unable to write bitmap to file", e);
                        if (0 != 0 && file.exists()) {
                            file.delete();
                        }
                        if (shortcutInfo.getBitmapPath() == null) {
                            removeIcon(shortcutInfo);
                        }
                        shortcutInfo.clearFlags(IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES);
                        return true;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (shortcutInfo != null) {
                        if (shortcutInfo.getBitmapPath() == null) {
                            removeIcon(shortcutInfo);
                        }
                        shortcutInfo.clearFlags(IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES);
                    }
                    throw th;
                }
            }
        } catch (Throwable th4) {
            shortcutInfo = null;
            th = th4;
        }
    }

    public void dumpLocked(PrintWriter printWriter, String str) {
        synchronized (this.mPendingItems) {
            int size = this.mPendingItems.size();
            printWriter.print(str);
            printWriter.println("Pending saves: Num=" + size + " Executor=" + this.mExecutor);
            for (PendingItem pendingItem : this.mPendingItems) {
                printWriter.print(str);
                printWriter.print("  ");
                printWriter.println(pendingItem);
            }
        }
    }
}
