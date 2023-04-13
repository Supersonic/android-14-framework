package android.app;

import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.StrictMode;
import com.android.internal.util.ExponentiallyBucketedHistogram;
import java.util.Iterator;
import java.util.LinkedList;
/* loaded from: classes.dex */
public class QueuedWork {
    private static final boolean DEBUG = false;
    private static final long DELAY = 100;
    private static final long MAX_WAIT_TIME_MILLIS = 512;
    private static final String LOG_TAG = QueuedWork.class.getSimpleName();
    private static final Object sLock = new Object();
    private static Object sProcessingWork = new Object();
    private static final LinkedList<Runnable> sFinishers = new LinkedList<>();
    private static Handler sHandler = null;
    private static LinkedList<Runnable> sWork = new LinkedList<>();
    private static boolean sCanDelay = true;
    private static final ExponentiallyBucketedHistogram mWaitTimes = new ExponentiallyBucketedHistogram(16);
    private static int mNumWaits = 0;

    private static Handler getHandler() {
        Handler handler;
        synchronized (sLock) {
            if (sHandler == null) {
                HandlerThread handlerThread = new HandlerThread("queued-work-looper", -2);
                handlerThread.start();
                sHandler = new QueuedWorkHandler(handlerThread.getLooper());
            }
            handler = sHandler;
        }
        return handler;
    }

    public static void addFinisher(Runnable finisher) {
        synchronized (sLock) {
            sFinishers.add(finisher);
        }
    }

    public static void removeFinisher(Runnable finisher) {
        synchronized (sLock) {
            sFinishers.remove(finisher);
        }
    }

    public static void waitToFinish() {
        Object obj;
        Runnable finisher;
        long startTime = System.currentTimeMillis();
        Handler handler = getHandler();
        synchronized (sLock) {
            if (handler.hasMessages(1)) {
                handler.removeMessages(1);
            }
            sCanDelay = false;
        }
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        try {
            processPendingWork();
            while (true) {
                try {
                    obj = sLock;
                    synchronized (obj) {
                        finisher = sFinishers.poll();
                    }
                    if (finisher == null) {
                        break;
                    }
                    finisher.run();
                } finally {
                    sCanDelay = true;
                }
            }
            synchronized (obj) {
                long waitTime = System.currentTimeMillis() - startTime;
                if (waitTime > 0 || 0 != 0) {
                    ExponentiallyBucketedHistogram exponentiallyBucketedHistogram = mWaitTimes;
                    exponentiallyBucketedHistogram.add(Long.valueOf(waitTime).intValue());
                    int i = mNumWaits + 1;
                    mNumWaits = i;
                    if (i % 1024 == 0 || waitTime > 512) {
                        exponentiallyBucketedHistogram.log(LOG_TAG, "waited: ");
                    }
                }
            }
        } finally {
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    public static void queue(Runnable work, boolean shouldDelay) {
        Handler handler = getHandler();
        synchronized (sLock) {
            sWork.add(work);
            if (shouldDelay && sCanDelay) {
                handler.sendEmptyMessageDelayed(1, DELAY);
            } else {
                handler.sendEmptyMessage(1);
            }
        }
    }

    public static boolean hasPendingWork() {
        boolean z;
        synchronized (sLock) {
            z = !sWork.isEmpty();
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void processPendingWork() {
        LinkedList<Runnable> work;
        synchronized (sProcessingWork) {
            synchronized (sLock) {
                work = sWork;
                sWork = new LinkedList<>();
                getHandler().removeMessages(1);
            }
            if (work.size() > 0) {
                Iterator<Runnable> it = work.iterator();
                while (it.hasNext()) {
                    Runnable w = it.next();
                    w.run();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class QueuedWorkHandler extends Handler {
        static final int MSG_RUN = 1;

        QueuedWorkHandler(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                QueuedWork.processPendingWork();
            }
        }
    }
}
