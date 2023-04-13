package com.android.server.p006am;

import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
/* renamed from: com.android.server.am.BroadcastLoopers */
/* loaded from: classes.dex */
public class BroadcastLoopers {
    @GuardedBy({"sLoopers"})
    public static final ArraySet<Looper> sLoopers = new ArraySet<>();

    public static void addMyLooper() {
        Looper myLooper = Looper.myLooper();
        if (myLooper != null) {
            ArraySet<Looper> arraySet = sLoopers;
            synchronized (arraySet) {
                if (arraySet.add(myLooper)) {
                    Slog.w("BroadcastLoopers", "Found previously unknown looper " + myLooper.getThread());
                }
            }
        }
    }

    public static void waitForIdle(PrintWriter printWriter) {
        waitForCondition(printWriter, new BiConsumer() { // from class: com.android.server.am.BroadcastLoopers$$ExternalSyntheticLambda1
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                BroadcastLoopers.lambda$waitForIdle$1((Looper) obj, (CountDownLatch) obj2);
            }
        });
    }

    public static /* synthetic */ void lambda$waitForIdle$1(Looper looper, final CountDownLatch countDownLatch) {
        looper.getQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.android.server.am.BroadcastLoopers$$ExternalSyntheticLambda3
            @Override // android.os.MessageQueue.IdleHandler
            public final boolean queueIdle() {
                boolean countDown;
                countDown = countDownLatch.countDown();
                return countDown;
            }
        });
    }

    public static void waitForBarrier(PrintWriter printWriter) {
        waitForCondition(printWriter, new BiConsumer() { // from class: com.android.server.am.BroadcastLoopers$$ExternalSyntheticLambda0
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                BroadcastLoopers.lambda$waitForBarrier$3((Looper) obj, (CountDownLatch) obj2);
            }
        });
    }

    public static /* synthetic */ void lambda$waitForBarrier$3(Looper looper, final CountDownLatch countDownLatch) {
        new Handler(looper).post(new Runnable() { // from class: com.android.server.am.BroadcastLoopers$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                countDownLatch.countDown();
            }
        });
    }

    public static void waitForCondition(PrintWriter printWriter, BiConsumer<Looper, CountDownLatch> biConsumer) {
        CountDownLatch countDownLatch;
        ArraySet<Looper> arraySet = sLoopers;
        synchronized (arraySet) {
            int size = arraySet.size();
            countDownLatch = new CountDownLatch(size);
            for (int i = 0; i < size; i++) {
                Looper valueAt = sLoopers.valueAt(i);
                if (valueAt.getQueue().isIdle()) {
                    countDownLatch.countDown();
                } else {
                    biConsumer.accept(valueAt, countDownLatch);
                }
            }
        }
        long j = 0;
        while (countDownLatch.getCount() > 0) {
            long uptimeMillis = SystemClock.uptimeMillis();
            if (uptimeMillis >= 1000 + j) {
                logv("Waiting for " + countDownLatch.getCount() + " loopers to drain...", printWriter);
                j = uptimeMillis;
            }
            SystemClock.sleep(100L);
        }
        logv("Loopers drained!", printWriter);
    }

    public static void logv(String str, PrintWriter printWriter) {
        Slog.v("BroadcastLoopers", str);
        if (printWriter != null) {
            printWriter.println(str);
            printWriter.flush();
        }
    }
}
