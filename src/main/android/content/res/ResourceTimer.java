package android.content.res;

import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.ParcelFileDescriptor;
import android.p008os.Process;
import android.p008os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.FrameworkStatsLog;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
/* loaded from: classes.dex */
public final class ResourceTimer {
    private static final String TAG = "ResourceTimer";
    private static Handler mHandler;
    private static int[] sApiMap;
    private static Config sConfig;
    private static int sCurrentPoint;
    private static ResourceTimer sManager;
    private static Timer[] sTimers;
    private static boolean sEnabled = true;
    private static boolean sIncrementalMetrics = true;
    private static boolean ENABLE_DEBUG = false;
    private static final Object sLock = new Object();
    private static final long sProcessStart = SystemClock.uptimeMillis();
    private static final long[] sPublicationPoints = {5, 60, 720};
    private static long sLastUpdated = 0;

    private static native int nativeEnableTimers(Config config);

    private static native int nativeGetTimers(Timer[] timerArr, boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Config {
        int maxBuckets;
        int maxLargest;
        int maxTimer;
        String[] timers;

        private Config() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Timer {
        int count;
        int[] largest;
        int maxtime;
        int mintime;
        int[] percentile;
        long total;

        private Timer() {
        }

        public String toString() {
            return TextUtils.formatSimple("%d:%d:%d:%d", Integer.valueOf(this.count), Long.valueOf(this.total), Integer.valueOf(this.mintime), Integer.valueOf(this.maxtime));
        }
    }

    private ResourceTimer() {
        throw new RuntimeException("ResourceTimer constructor");
    }

    public static void start() {
        synchronized (sLock) {
            if (sEnabled) {
                if (mHandler != null) {
                    return;
                }
                if (Looper.getMainLooper() == null) {
                    throw new RuntimeException("ResourceTimer started too early");
                }
                mHandler = new Handler(Looper.getMainLooper()) { // from class: android.content.res.ResourceTimer.1
                    @Override // android.p008os.Handler
                    public void handleMessage(Message msg) {
                        ResourceTimer.handleMessage(msg);
                    }
                };
                Config config = new Config();
                sConfig = config;
                nativeEnableTimers(config);
                sTimers = new Timer[sConfig.maxTimer];
                int i = 0;
                while (true) {
                    Timer[] timerArr = sTimers;
                    if (i >= timerArr.length) {
                        break;
                    }
                    timerArr[i] = new Timer();
                    sTimers[i].percentile = new int[sConfig.maxBuckets];
                    sTimers[i].largest = new int[sConfig.maxLargest];
                    i++;
                }
                sApiMap = new int[sConfig.maxTimer];
                for (int i2 = 0; i2 < sApiMap.length; i2++) {
                    if (sConfig.timers[i2].equals("GetResourceValue")) {
                        sApiMap[i2] = 1;
                    } else if (sConfig.timers[i2].equals("RetrieveAttributes")) {
                        sApiMap[i2] = 2;
                    } else {
                        sApiMap[i2] = 0;
                    }
                }
                sCurrentPoint = 0;
                startTimer();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void handleMessage(Message msg) {
        synchronized (sLock) {
            publish();
            startTimer();
        }
    }

    private static void startTimer() {
        long delay;
        int i = sCurrentPoint;
        long[] jArr = sPublicationPoints;
        if (i < jArr.length) {
            delay = jArr[i];
        } else {
            long repeated = jArr[jArr.length - 1];
            int prelude = jArr.length - 1;
            delay = (i - prelude) * repeated;
        }
        long delay2 = delay * 60000;
        if (ENABLE_DEBUG) {
            delay2 /= 60;
        }
        mHandler.sendEmptyMessageAtTime(0, sProcessStart + delay2);
    }

    private static void update(boolean reset) {
        nativeGetTimers(sTimers, reset);
        sLastUpdated = SystemClock.uptimeMillis();
    }

    private static void publish() {
        update(true);
        int i = 0;
        while (true) {
            Timer[] timerArr = sTimers;
            if (i < timerArr.length) {
                Timer timer = timerArr[i];
                if (timer.count > 0) {
                    Log.m108i(TAG, TextUtils.formatSimple("%s count=%d pvalues=%s", sConfig.timers[i], Integer.valueOf(timer.count), packedString(timer.percentile)));
                    int i2 = sApiMap[i];
                    if (i2 != 0) {
                        FrameworkStatsLog.write(517, i2, timer.count, timer.total, timer.percentile[0], timer.percentile[1], timer.percentile[2], timer.percentile[3], timer.largest[0], timer.largest[1], timer.largest[2], timer.largest[3], timer.largest[4]);
                    }
                }
                i++;
            } else {
                int i3 = sCurrentPoint;
                sCurrentPoint = i3 + 1;
                return;
            }
        }
    }

    private static String packedString(int[] a) {
        return Arrays.toString(a).replaceAll("[\\]\\[ ]", "");
    }

    public static void dumpTimers(ParcelFileDescriptor pfd, String[] args) {
        FileOutputStream fout = new FileOutputStream(pfd.getFileDescriptor());
        PrintWriter pw = new FastPrintWriter(fout);
        Object obj = sLock;
        synchronized (obj) {
            if (sEnabled && sConfig != null) {
                boolean refresh = Arrays.asList(args).contains("-refresh");
                synchronized (obj) {
                    update(refresh);
                    long runtime = sLastUpdated - sProcessStart;
                    char c = 0;
                    char c2 = 1;
                    pw.format("  config runtime=%d proc=%s\n", Long.valueOf(runtime), Process.myProcessName());
                    int i = 0;
                    while (true) {
                        Timer[] timerArr = sTimers;
                        if (i < timerArr.length) {
                            Timer t = timerArr[i];
                            if (t.count != 0) {
                                String name = sConfig.timers[i];
                                Object[] objArr = new Object[7];
                                objArr[c] = name;
                                objArr[c2] = Integer.valueOf(t.count);
                                objArr[2] = Long.valueOf(t.total / t.count);
                                objArr[3] = Integer.valueOf(t.mintime);
                                objArr[4] = Integer.valueOf(t.maxtime);
                                objArr[5] = packedString(t.percentile);
                                objArr[6] = packedString(t.largest);
                                pw.format("  stats timer=%s cnt=%d avg=%d min=%d max=%d pval=%s largest=%s\n", objArr);
                            }
                            i++;
                            c = 0;
                            c2 = 1;
                        }
                    }
                }
                pw.flush();
                return;
            }
            pw.println("  Timers are not enabled in this process");
            pw.flush();
        }
    }
}
