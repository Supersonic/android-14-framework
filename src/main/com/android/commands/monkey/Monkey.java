package com.android.commands.monkey;

import android.app.ActivityManager;
import android.app.IActivityController;
import android.app.IActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.SystemClock;
import android.view.IWindowManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
/* loaded from: classes.dex */
public class Monkey {
    private static final int DEBUG_ALLOW_ANY_RESTARTS = 0;
    private static final int DEBUG_ALLOW_ANY_STARTS = 0;
    private static final String TOMBSTONE_PREFIX = "tombstone_";
    public static Intent currentIntent;
    public static String currentPackage;
    private boolean mAbort;
    private IActivityManager mAm;
    private String[] mArgs;
    private String mCurArgData;
    MonkeyEventSource mEventSource;
    private boolean mGenerateHprof;
    private boolean mIgnoreCrashes;
    private boolean mIgnoreNativeCrashes;
    private boolean mIgnoreSecurityExceptions;
    private boolean mIgnoreTimeouts;
    private boolean mKillProcessAfterError;
    private String mMatchDescription;
    private boolean mMonitorNativeCrashes;
    private int mNextArg;
    private String mPkgBlacklistFile;
    private String mPkgWhitelistFile;
    private IPackageManager mPm;
    private String mReportProcessName;
    private boolean mSendNoEvents;
    private int mVerbose;
    private IWindowManager mWm;
    private static final File TOMBSTONES_PATH = new File("/data/tombstones");
    private static int NUM_READ_TOMBSTONE_RETRIES = 5;
    private boolean mCountEvents = true;
    private boolean mRequestAnrTraces = false;
    private boolean mRequestDumpsysMemInfo = false;
    private boolean mRequestAnrBugreport = false;
    private boolean mRequestWatchdogBugreport = false;
    private boolean mWatchdogWaiting = false;
    private boolean mRequestAppCrashBugreport = false;
    private boolean mGetPeriodicBugreport = false;
    private boolean mRequestPeriodicBugreport = false;
    private long mBugreportFrequency = 10;
    private boolean mRequestProcRank = false;
    private ArrayList<String> mMainCategories = new ArrayList<>();
    private HashMap<ComponentName, String> mMainApps = new HashMap<>();
    long mThrottle = 0;
    boolean mRandomizeThrottle = false;
    int mCount = 1000;
    long mSeed = 0;
    Random mRandom = null;
    long mDroppedKeyEvents = 0;
    long mDroppedPointerEvents = 0;
    long mDroppedTrackballEvents = 0;
    long mDroppedFlipEvents = 0;
    long mDroppedRotationEvents = 0;
    long mProfileWaitTime = 5000;
    long mDeviceSleepTime = 30000;
    boolean mRandomizeScript = false;
    boolean mScriptLog = false;
    private boolean mRequestBugreport = false;
    private String mSetupFileName = null;
    private ArrayList<String> mScriptFileNames = new ArrayList<>();
    private int mServerPort = -1;
    private HashSet<Long> mTombstones = null;
    float[] mFactors = new float[12];
    private MonkeyNetworkMonitor mNetworkMonitor = new MonkeyNetworkMonitor();
    private boolean mPermissionTargetSystem = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ActivityController extends IActivityController.Stub {
        private ActivityController() {
        }

        public boolean activityStarting(Intent intent, String pkg) {
            boolean allow = isActivityStartingAllowed(intent, pkg);
            if (Monkey.this.mVerbose > 0) {
                StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskWrites();
                Logger.out.println("    // " + (allow ? "Allowing" : "Rejecting") + " start of " + intent + " in package " + pkg);
                StrictMode.setThreadPolicy(savedPolicy);
            }
            Monkey.currentPackage = pkg;
            Monkey.currentIntent = intent;
            return allow;
        }

        private boolean isActivityStartingAllowed(Intent intent, String pkg) {
            if (MonkeyUtils.getPackageFilter().checkEnteringPackage(pkg)) {
                return true;
            }
            Set<String> categories = intent.getCategories();
            if (intent.getAction() == "android.intent.action.MAIN" && categories != null && categories.contains("android.intent.category.HOME")) {
                try {
                    ResolveInfo resolveInfo = Monkey.this.mPm.resolveIntent(intent, intent.getType(), 0L, ActivityManager.getCurrentUser());
                    String launcherPackage = resolveInfo.activityInfo.packageName;
                    if (pkg.equals(launcherPackage)) {
                        return true;
                    }
                } catch (RemoteException e) {
                    Logger.err.println("** Failed talking with package manager!");
                    return false;
                }
            }
            return false;
        }

        public boolean activityResuming(String pkg) {
            StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskWrites();
            Logger.out.println("    // activityResuming(" + pkg + ")");
            boolean allow = MonkeyUtils.getPackageFilter().checkEnteringPackage(pkg);
            if (!allow && Monkey.this.mVerbose > 0) {
                Logger.out.println("    // " + (allow ? "Allowing" : "Rejecting") + " resume of package " + pkg);
            }
            Monkey.currentPackage = pkg;
            StrictMode.setThreadPolicy(savedPolicy);
            return allow;
        }

        public boolean appCrashed(String processName, int pid, String shortMsg, String longMsg, long timeMillis, String stackTrace) {
            StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskWrites();
            Logger.err.println("// CRASH: " + processName + " (pid " + pid + ")");
            Logger.err.println("// Short Msg: " + shortMsg);
            Logger.err.println("// Long Msg: " + longMsg);
            Logger.err.println("// Build Label: " + Build.FINGERPRINT);
            Logger.err.println("// Build Changelist: " + Build.VERSION.INCREMENTAL);
            Logger.err.println("// Build Time: " + Build.TIME);
            Logger.err.println("// " + stackTrace.replace("\n", "\n// "));
            StrictMode.setThreadPolicy(savedPolicy);
            if (Monkey.this.mMatchDescription == null || shortMsg.contains(Monkey.this.mMatchDescription) || longMsg.contains(Monkey.this.mMatchDescription) || stackTrace.contains(Monkey.this.mMatchDescription)) {
                if (!Monkey.this.mIgnoreCrashes || Monkey.this.mRequestBugreport) {
                    synchronized (Monkey.this) {
                        if (!Monkey.this.mIgnoreCrashes) {
                            Monkey.this.mAbort = true;
                        }
                        if (Monkey.this.mRequestBugreport) {
                            Monkey.this.mRequestAppCrashBugreport = true;
                            Monkey.this.mReportProcessName = processName;
                        }
                    }
                    return !Monkey.this.mKillProcessAfterError;
                }
                return false;
            }
            return false;
        }

        public int appEarlyNotResponding(String processName, int pid, String annotation) {
            return 0;
        }

        public int appNotResponding(String processName, int pid, String processStats) {
            StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskWrites();
            Logger.err.println("// NOT RESPONDING: " + processName + " (pid " + pid + ")");
            Logger.err.println(processStats);
            StrictMode.setThreadPolicy(savedPolicy);
            if (Monkey.this.mMatchDescription == null || processStats.contains(Monkey.this.mMatchDescription)) {
                synchronized (Monkey.this) {
                    Monkey.this.mRequestAnrTraces = true;
                    Monkey.this.mRequestDumpsysMemInfo = true;
                    Monkey.this.mRequestProcRank = true;
                    if (Monkey.this.mRequestBugreport) {
                        Monkey.this.mRequestAnrBugreport = true;
                        Monkey.this.mReportProcessName = processName;
                    }
                }
                if (!Monkey.this.mIgnoreTimeouts) {
                    synchronized (Monkey.this) {
                        Monkey.this.mAbort = true;
                    }
                }
            }
            return Monkey.this.mKillProcessAfterError ? -1 : 1;
        }

        public int systemNotResponding(String message) {
            StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskWrites();
            Logger.err.println("// WATCHDOG: " + message);
            StrictMode.setThreadPolicy(savedPolicy);
            synchronized (Monkey.this) {
                if (Monkey.this.mMatchDescription == null || message.contains(Monkey.this.mMatchDescription)) {
                    if (!Monkey.this.mIgnoreCrashes) {
                        Monkey.this.mAbort = true;
                    }
                    if (Monkey.this.mRequestBugreport) {
                        Monkey.this.mRequestWatchdogBugreport = true;
                    }
                }
                Monkey.this.mWatchdogWaiting = true;
            }
            synchronized (Monkey.this) {
                while (Monkey.this.mWatchdogWaiting) {
                    try {
                        Monkey.this.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            return Monkey.this.mKillProcessAfterError ? -1 : 1;
        }
    }

    private void reportProcRank() {
        commandLineReport("procrank", "procrank");
    }

    private void reportAnrTraces() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
        }
        File[] recentTraces = new File("/data/anr/").listFiles();
        if (recentTraces != null) {
            File mostRecent = null;
            long mostRecentMtime = 0;
            for (File trace : recentTraces) {
                long mtime = trace.lastModified();
                if (mtime > mostRecentMtime) {
                    mostRecentMtime = mtime;
                    mostRecent = trace;
                }
            }
            if (mostRecent != null) {
                commandLineReport("anr traces", "cat " + mostRecent.getAbsolutePath());
            }
        }
    }

    private void reportDumpsysMemInfo() {
        commandLineReport("meminfo", "dumpsys meminfo");
    }

    private void commandLineReport(String reportName, String command) {
        Logger.err.println(reportName + ":");
        Runtime.getRuntime();
        Writer logOutput = null;
        try {
            Process p = Runtime.getRuntime().exec(command);
            if (this.mRequestBugreport) {
                logOutput = new BufferedWriter(new FileWriter(new File(Environment.getLegacyExternalStorageDirectory(), reportName), true));
            }
            InputStream inStream = p.getInputStream();
            InputStreamReader inReader = new InputStreamReader(inStream);
            BufferedReader inBuffer = new BufferedReader(inReader);
            while (true) {
                String s = inBuffer.readLine();
                if (s == null) {
                    break;
                } else if (this.mRequestBugreport) {
                    try {
                        logOutput.write(s);
                        logOutput.write("\n");
                    } catch (IOException e) {
                        while (inBuffer.readLine() != null) {
                        }
                        Logger.err.println(e.toString());
                    }
                } else {
                    Logger.err.println(s);
                }
            }
            int status = p.waitFor();
            Logger.err.println("// " + reportName + " status was " + status);
            if (logOutput != null) {
                logOutput.close();
            }
        } catch (Exception e2) {
            Logger.err.println("// Exception from " + reportName + ":");
            Logger.err.println(e2.toString());
        }
    }

    private void writeScriptLog(int count) {
        try {
            Writer output = new BufferedWriter(new FileWriter(new File(Environment.getLegacyExternalStorageDirectory(), "scriptlog.txt"), true));
            output.write("iteration: " + count + " time: " + MonkeyUtils.toCalendarTime(System.currentTimeMillis()) + "\n");
            output.close();
        } catch (IOException e) {
            Logger.err.println(e.toString());
        }
    }

    private void getBugreport(String reportName) {
        String bugreportName = (reportName + MonkeyUtils.toCalendarTime(System.currentTimeMillis())).replaceAll("[ ,:]", "_");
        commandLineReport(bugreportName + ".txt", "bugreport");
    }

    public static void main(String[] args) {
        Process.setArgV0("com.android.commands.monkey");
        Logger.err.println("args: " + Arrays.toString(args));
        int resultCode = new Monkey().run(args);
        System.exit(resultCode);
    }

    /* JADX WARN: Finally extract failed */
    private int run(String[] args) {
        for (String s : args) {
            if ("--wait-dbg".equals(s)) {
                Debug.waitForDebugger();
            }
        }
        this.mVerbose = 0;
        this.mCount = 1000;
        this.mSeed = 0L;
        this.mThrottle = 0L;
        this.mArgs = args;
        for (String a : args) {
            Logger.err.println(" arg: \"" + a + "\"");
        }
        this.mNextArg = 0;
        for (int i = 0; i < 12; i++) {
            this.mFactors[i] = 1.0f;
        }
        if (processOptions() && loadPackageLists()) {
            if (this.mMainCategories.size() == 0) {
                this.mMainCategories.add("android.intent.category.LAUNCHER");
                this.mMainCategories.add("android.intent.category.MONKEY");
            }
            if (this.mSeed == 0) {
                this.mSeed = System.currentTimeMillis() + System.identityHashCode(this);
            }
            if (this.mVerbose > 0) {
                Logger.out.println(":Monkey: seed=" + this.mSeed + " count=" + this.mCount);
                MonkeyUtils.getPackageFilter().dump();
                if (this.mMainCategories.size() != 0) {
                    Iterator<String> it = this.mMainCategories.iterator();
                    while (it.hasNext()) {
                        Logger.out.println(":IncludeCategory: " + it.next());
                    }
                }
            }
            if (!checkInternalConfiguration()) {
                return -2;
            }
            if (!getSystemInterfaces()) {
                return -3;
            }
            if (getMainApps()) {
                this.mRandom = new Random(this.mSeed);
                ArrayList<String> arrayList = this.mScriptFileNames;
                if (arrayList != null && arrayList.size() == 1) {
                    MonkeySourceScript monkeySourceScript = new MonkeySourceScript(this.mRandom, this.mScriptFileNames.get(0), this.mThrottle, this.mRandomizeThrottle, this.mProfileWaitTime, this.mDeviceSleepTime);
                    this.mEventSource = monkeySourceScript;
                    monkeySourceScript.setVerbose(this.mVerbose);
                    this.mCountEvents = false;
                } else {
                    ArrayList<String> arrayList2 = this.mScriptFileNames;
                    if (arrayList2 != null && arrayList2.size() > 1) {
                        String str = this.mSetupFileName;
                        if (str != null) {
                            this.mEventSource = new MonkeySourceRandomScript(str, this.mScriptFileNames, this.mThrottle, this.mRandomizeThrottle, this.mRandom, this.mProfileWaitTime, this.mDeviceSleepTime, this.mRandomizeScript);
                            this.mCount++;
                        } else {
                            this.mEventSource = new MonkeySourceRandomScript(this.mScriptFileNames, this.mThrottle, this.mRandomizeThrottle, this.mRandom, this.mProfileWaitTime, this.mDeviceSleepTime, this.mRandomizeScript);
                        }
                        this.mEventSource.setVerbose(this.mVerbose);
                        this.mCountEvents = false;
                    } else if (this.mServerPort != -1) {
                        try {
                            this.mEventSource = new MonkeySourceNetwork(this.mServerPort);
                            this.mCount = Integer.MAX_VALUE;
                        } catch (IOException e) {
                            Logger.out.println("Error binding to network socket.");
                            return -5;
                        }
                    } else {
                        if (this.mVerbose >= 2) {
                            Logger.out.println("// Seeded: " + this.mSeed);
                        }
                        MonkeySourceRandom monkeySourceRandom = new MonkeySourceRandom(this.mRandom, this.mMainApps, this.mThrottle, this.mRandomizeThrottle, this.mPermissionTargetSystem);
                        this.mEventSource = monkeySourceRandom;
                        monkeySourceRandom.setVerbose(this.mVerbose);
                        for (int i2 = 0; i2 < 12; i2++) {
                            float f = this.mFactors[i2];
                            if (f <= 0.0f) {
                                ((MonkeySourceRandom) this.mEventSource).setFactors(i2, f);
                            }
                        }
                        ((MonkeySourceRandom) this.mEventSource).generateActivity();
                    }
                }
                if (!this.mEventSource.validate()) {
                    return -5;
                }
                if (this.mGenerateHprof) {
                    signalPersistentProcesses();
                }
                this.mNetworkMonitor.start();
                try {
                    int crashedAtCycle = runMonkeyCycles();
                    new MonkeyRotationEvent(0, false).injectEvent(this.mWm, this.mAm, this.mVerbose);
                    this.mNetworkMonitor.stop();
                    synchronized (this) {
                        if (this.mRequestAnrTraces) {
                            reportAnrTraces();
                            this.mRequestAnrTraces = false;
                        }
                        if (this.mRequestAnrBugreport) {
                            Logger.out.println("Print the anr report");
                            getBugreport("anr_" + this.mReportProcessName + "_");
                            this.mRequestAnrBugreport = false;
                        }
                        if (this.mRequestWatchdogBugreport) {
                            Logger.out.println("Print the watchdog report");
                            getBugreport("anr_watchdog_");
                            this.mRequestWatchdogBugreport = false;
                        }
                        if (this.mRequestAppCrashBugreport) {
                            getBugreport("app_crash" + this.mReportProcessName + "_");
                            this.mRequestAppCrashBugreport = false;
                        }
                        if (this.mRequestDumpsysMemInfo) {
                            reportDumpsysMemInfo();
                            this.mRequestDumpsysMemInfo = false;
                        }
                        if (this.mRequestPeriodicBugreport) {
                            getBugreport("Bugreport_");
                            this.mRequestPeriodicBugreport = false;
                        }
                        if (this.mWatchdogWaiting) {
                            this.mWatchdogWaiting = false;
                            notifyAll();
                        }
                    }
                    if (this.mGenerateHprof) {
                        signalPersistentProcesses();
                        if (this.mVerbose > 0) {
                            Logger.out.println("// Generated profiling reports in /data/misc");
                        }
                    }
                    try {
                        this.mAm.setActivityController((IActivityController) null, true);
                        this.mNetworkMonitor.unregister(this.mAm);
                    } catch (RemoteException e2) {
                        int i3 = this.mCount;
                        if (crashedAtCycle >= i3) {
                            crashedAtCycle = i3 - 1;
                        }
                    }
                    if (this.mVerbose > 0) {
                        Logger.out.println(":Dropped: keys=" + this.mDroppedKeyEvents + " pointers=" + this.mDroppedPointerEvents + " trackballs=" + this.mDroppedTrackballEvents + " flips=" + this.mDroppedFlipEvents + " rotations=" + this.mDroppedRotationEvents);
                    }
                    this.mNetworkMonitor.dump();
                    if (crashedAtCycle < this.mCount - 1) {
                        Logger.err.println("** System appears to have crashed at event " + crashedAtCycle + " of " + this.mCount + " using seed " + this.mSeed);
                        return crashedAtCycle;
                    } else if (this.mVerbose > 0) {
                        Logger.out.println("// Monkey finished");
                        return 0;
                    } else {
                        return 0;
                    }
                } catch (Throwable th) {
                    new MonkeyRotationEvent(0, false).injectEvent(this.mWm, this.mAm, this.mVerbose);
                    throw th;
                }
            }
            return -4;
        }
        return -1;
    }

    private boolean processOptions() {
        if (this.mArgs.length < 1) {
            showUsage();
            return false;
        }
        try {
            Set<String> validPackages = new HashSet<>();
            while (true) {
                String opt = nextOption();
                if (opt != null) {
                    if (opt.equals("-s")) {
                        this.mSeed = nextOptionLong("Seed");
                    } else if (opt.equals("-p")) {
                        validPackages.add(nextOptionData());
                    } else if (opt.equals("-c")) {
                        this.mMainCategories.add(nextOptionData());
                    } else if (opt.equals("-v")) {
                        this.mVerbose++;
                    } else if (opt.equals("--ignore-crashes")) {
                        this.mIgnoreCrashes = true;
                    } else if (opt.equals("--ignore-timeouts")) {
                        this.mIgnoreTimeouts = true;
                    } else if (opt.equals("--ignore-security-exceptions")) {
                        this.mIgnoreSecurityExceptions = true;
                    } else if (opt.equals("--monitor-native-crashes")) {
                        this.mMonitorNativeCrashes = true;
                    } else if (opt.equals("--ignore-native-crashes")) {
                        this.mIgnoreNativeCrashes = true;
                    } else if (opt.equals("--kill-process-after-error")) {
                        this.mKillProcessAfterError = true;
                    } else if (opt.equals("--hprof")) {
                        this.mGenerateHprof = true;
                    } else if (opt.equals("--match-description")) {
                        this.mMatchDescription = nextOptionData();
                    } else if (opt.equals("--pct-touch")) {
                        this.mFactors[0] = (float) (-nextOptionLong("touch events percentage"));
                    } else if (opt.equals("--pct-motion")) {
                        this.mFactors[1] = (float) (-nextOptionLong("motion events percentage"));
                    } else if (opt.equals("--pct-trackball")) {
                        this.mFactors[3] = (float) (-nextOptionLong("trackball events percentage"));
                    } else if (opt.equals("--pct-rotation")) {
                        this.mFactors[4] = (float) (-nextOptionLong("screen rotation events percentage"));
                    } else if (opt.equals("--pct-syskeys")) {
                        this.mFactors[8] = (float) (-nextOptionLong("system (key) operations percentage"));
                    } else if (opt.equals("--pct-nav")) {
                        this.mFactors[6] = (float) (-nextOptionLong("nav events percentage"));
                    } else if (opt.equals("--pct-majornav")) {
                        this.mFactors[7] = (float) (-nextOptionLong("major nav events percentage"));
                    } else if (opt.equals("--pct-appswitch")) {
                        this.mFactors[9] = (float) (-nextOptionLong("app switch events percentage"));
                    } else if (opt.equals("--pct-flip")) {
                        this.mFactors[10] = (float) (-nextOptionLong("keyboard flip percentage"));
                    } else if (opt.equals("--pct-anyevent")) {
                        this.mFactors[11] = (float) (-nextOptionLong("any events percentage"));
                    } else if (opt.equals("--pct-pinchzoom")) {
                        this.mFactors[2] = (float) (-nextOptionLong("pinch zoom events percentage"));
                    } else if (opt.equals("--pct-permission")) {
                        this.mFactors[5] = (float) (-nextOptionLong("runtime permission toggle events percentage"));
                    } else if (opt.equals("--pkg-blacklist-file")) {
                        this.mPkgBlacklistFile = nextOptionData();
                    } else if (opt.equals("--pkg-whitelist-file")) {
                        this.mPkgWhitelistFile = nextOptionData();
                    } else if (opt.equals("--throttle")) {
                        this.mThrottle = nextOptionLong("delay (in milliseconds) to wait between events");
                    } else if (opt.equals("--randomize-throttle")) {
                        this.mRandomizeThrottle = true;
                    } else if (!opt.equals("--wait-dbg")) {
                        if (opt.equals("--dbg-no-events")) {
                            this.mSendNoEvents = true;
                        } else if (opt.equals("--port")) {
                            this.mServerPort = (int) nextOptionLong("Server port to listen on for commands");
                        } else if (opt.equals("--setup")) {
                            this.mSetupFileName = nextOptionData();
                        } else if (opt.equals("-f")) {
                            this.mScriptFileNames.add(nextOptionData());
                        } else if (opt.equals("--profile-wait")) {
                            this.mProfileWaitTime = nextOptionLong("Profile delay (in milliseconds) to wait between user action");
                        } else if (opt.equals("--device-sleep-time")) {
                            this.mDeviceSleepTime = nextOptionLong("Device sleep time(in milliseconds)");
                        } else if (opt.equals("--randomize-script")) {
                            this.mRandomizeScript = true;
                        } else if (opt.equals("--script-log")) {
                            this.mScriptLog = true;
                        } else if (opt.equals("--bugreport")) {
                            this.mRequestBugreport = true;
                        } else if (opt.equals("--periodic-bugreport")) {
                            this.mGetPeriodicBugreport = true;
                            this.mBugreportFrequency = nextOptionLong("Number of iterations");
                        } else if (opt.equals("--permission-target-system")) {
                            this.mPermissionTargetSystem = true;
                        } else if (opt.equals("-h")) {
                            showUsage();
                            return false;
                        } else {
                            Logger.err.println("** Error: Unknown option: " + opt);
                            showUsage();
                            return false;
                        }
                    }
                } else {
                    MonkeyUtils.getPackageFilter().addValidPackages(validPackages);
                    if (this.mServerPort == -1) {
                        String countStr = nextArg();
                        if (countStr == null) {
                            Logger.err.println("** Error: Count not specified");
                            showUsage();
                            return false;
                        }
                        try {
                            this.mCount = Integer.parseInt(countStr);
                        } catch (NumberFormatException e) {
                            Logger.err.println("** Error: Count is not a number: \"" + countStr + "\"");
                            showUsage();
                            return false;
                        }
                    }
                    return true;
                }
            }
        } catch (RuntimeException ex) {
            Logger.err.println("** Error: " + ex.toString());
            showUsage();
            return false;
        }
    }

    private static boolean loadPackageListFromFile(String fileName, Set<String> list) {
        BufferedReader reader = null;
        try {
            try {
                reader = new BufferedReader(new FileReader(fileName));
                while (true) {
                    String s = reader.readLine();
                    if (s != null) {
                        String s2 = s.trim();
                        if (s2.length() > 0 && !s2.startsWith("#")) {
                            list.add(s2);
                        }
                    } else {
                        try {
                            reader.close();
                            return true;
                        } catch (IOException ioe) {
                            Logger.err.println("" + ioe);
                            return true;
                        }
                    }
                }
            } catch (Throwable th) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ioe2) {
                        Logger.err.println("" + ioe2);
                    }
                }
                throw th;
            }
        } catch (IOException ioe3) {
            Logger.err.println("" + ioe3);
            if (reader == null) {
                return false;
            }
            try {
                reader.close();
                return false;
            } catch (IOException ioe4) {
                Logger.err.println("" + ioe4);
                return false;
            }
        }
    }

    private boolean loadPackageLists() {
        if ((this.mPkgWhitelistFile != null || MonkeyUtils.getPackageFilter().hasValidPackages()) && this.mPkgBlacklistFile != null) {
            Logger.err.println("** Error: you can not specify a package blacklist together with a whitelist or individual packages (via -p).");
            return false;
        }
        Set<String> validPackages = new HashSet<>();
        String str = this.mPkgWhitelistFile;
        if (str == null || loadPackageListFromFile(str, validPackages)) {
            MonkeyUtils.getPackageFilter().addValidPackages(validPackages);
            Set<String> invalidPackages = new HashSet<>();
            String str2 = this.mPkgBlacklistFile;
            if (str2 == null || loadPackageListFromFile(str2, invalidPackages)) {
                MonkeyUtils.getPackageFilter().addInvalidPackages(invalidPackages);
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean checkInternalConfiguration() {
        return true;
    }

    private boolean getSystemInterfaces() {
        IActivityManager service = ActivityManager.getService();
        this.mAm = service;
        if (service == null) {
            Logger.err.println("** Error: Unable to connect to activity manager; is the system running?");
            return false;
        }
        IWindowManager asInterface = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        this.mWm = asInterface;
        if (asInterface == null) {
            Logger.err.println("** Error: Unable to connect to window manager; is the system running?");
            return false;
        }
        IPackageManager asInterface2 = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        this.mPm = asInterface2;
        if (asInterface2 == null) {
            Logger.err.println("** Error: Unable to connect to package manager; is the system running?");
            return false;
        }
        try {
            this.mAm.setActivityController(new ActivityController(), true);
            this.mNetworkMonitor.register(this.mAm);
            return true;
        } catch (RemoteException e) {
            Logger.err.println("** Failed talking with activity manager!");
            return false;
        }
    }

    private boolean getMainApps() {
        try {
            int N = this.mMainCategories.size();
            for (int i = 0; i < N; i++) {
                Intent intent = new Intent("android.intent.action.MAIN");
                String category = this.mMainCategories.get(i);
                if (category.length() > 0) {
                    intent.addCategory(category);
                }
                List<ResolveInfo> mainApps = this.mPm.queryIntentActivities(intent, (String) null, 0L, ActivityManager.getCurrentUser()).getList();
                if (mainApps != null && mainApps.size() != 0) {
                    int i2 = 2;
                    if (this.mVerbose >= 2) {
                        Logger.out.println("// Selecting main activities from category " + category);
                    }
                    int NA = mainApps.size();
                    int a = 0;
                    while (a < NA) {
                        ResolveInfo r = mainApps.get(a);
                        String packageName = r.activityInfo.applicationInfo.packageName;
                        if (MonkeyUtils.getPackageFilter().checkEnteringPackage(packageName)) {
                            if (this.mVerbose >= i2) {
                                Logger.out.println("//   + Using main activity " + r.activityInfo.name + " (from package " + packageName + ")");
                            }
                            this.mMainApps.put(new ComponentName(packageName, r.activityInfo.name), category);
                        } else if (this.mVerbose >= 3) {
                            Logger.out.println("//   - NOT USING main activity " + r.activityInfo.name + " (from package " + packageName + ")");
                        }
                        a++;
                        i2 = 2;
                    }
                }
                Logger.err.println("// Warning: no activities found for category " + category);
            }
            if (this.mMainApps.size() == 0) {
                Logger.out.println("** No activities found to run, monkey aborted.");
                return false;
            }
            return true;
        } catch (RemoteException e) {
            Logger.err.println("** Failed talking with package manager!");
            return false;
        }
    }

    private int runMonkeyCycles() {
        boolean z;
        int eventCounter = 0;
        int cycleCounter = 0;
        boolean shouldReportAnrTraces = false;
        boolean shouldReportDumpsysMemInfo = false;
        boolean shouldAbort = false;
        boolean systemCrashed = false;
        while (!systemCrashed) {
            try {
                if (cycleCounter >= this.mCount) {
                    break;
                }
                synchronized (this) {
                    if (this.mRequestProcRank) {
                        reportProcRank();
                        this.mRequestProcRank = false;
                    }
                    if (this.mRequestAnrTraces) {
                        this.mRequestAnrTraces = false;
                        shouldReportAnrTraces = true;
                    }
                    if (this.mRequestAnrBugreport) {
                        getBugreport("anr_" + this.mReportProcessName + "_");
                        this.mRequestAnrBugreport = false;
                    }
                    if (this.mRequestWatchdogBugreport) {
                        Logger.out.println("Print the watchdog report");
                        getBugreport("anr_watchdog_");
                        this.mRequestWatchdogBugreport = false;
                    }
                    if (this.mRequestAppCrashBugreport) {
                        getBugreport("app_crash" + this.mReportProcessName + "_");
                        this.mRequestAppCrashBugreport = false;
                    }
                    if (this.mRequestPeriodicBugreport) {
                        getBugreport("Bugreport_");
                        this.mRequestPeriodicBugreport = false;
                    }
                    if (this.mRequestDumpsysMemInfo) {
                        this.mRequestDumpsysMemInfo = false;
                        shouldReportDumpsysMemInfo = true;
                    }
                    if (this.mMonitorNativeCrashes && checkNativeCrashes() && eventCounter > 0) {
                        Logger.out.println("** New native crash detected.");
                        if (this.mRequestBugreport) {
                            getBugreport("native_crash_");
                        }
                        if (!this.mAbort && this.mIgnoreNativeCrashes && !this.mKillProcessAfterError) {
                            z = false;
                            this.mAbort = z;
                        }
                        z = true;
                        this.mAbort = z;
                    }
                    if (this.mAbort) {
                        shouldAbort = true;
                    }
                    if (this.mWatchdogWaiting) {
                        this.mWatchdogWaiting = false;
                        notifyAll();
                    }
                }
                if (shouldReportAnrTraces) {
                    shouldReportAnrTraces = false;
                    reportAnrTraces();
                }
                if (shouldReportDumpsysMemInfo) {
                    shouldReportDumpsysMemInfo = false;
                    reportDumpsysMemInfo();
                }
                if (shouldAbort) {
                    Logger.out.println("** Monkey aborted due to error.");
                    Logger.out.println("Events injected: " + eventCounter);
                    return eventCounter;
                } else if (this.mSendNoEvents) {
                    eventCounter++;
                    cycleCounter++;
                } else {
                    if (this.mVerbose > 0 && eventCounter % 100 == 0 && eventCounter != 0) {
                        String calendarTime = MonkeyUtils.toCalendarTime(System.currentTimeMillis());
                        long systemUpTime = SystemClock.elapsedRealtime();
                        Logger.out.println("    //[calendar_time:" + calendarTime + " system_uptime:" + systemUpTime + "]");
                        Logger.out.println("    // Sending event #" + eventCounter);
                    }
                    MonkeyEvent ev = this.mEventSource.getNextEvent();
                    if (ev != null) {
                        int injectCode = ev.injectEvent(this.mWm, this.mAm, this.mVerbose);
                        if (injectCode == 0) {
                            Logger.out.println("    // Injection Failed");
                            if (ev instanceof MonkeyKeyEvent) {
                                this.mDroppedKeyEvents++;
                            } else if (ev instanceof MonkeyMotionEvent) {
                                this.mDroppedPointerEvents++;
                            } else if (ev instanceof MonkeyFlipEvent) {
                                this.mDroppedFlipEvents++;
                            } else if (ev instanceof MonkeyRotationEvent) {
                                this.mDroppedRotationEvents++;
                            }
                        } else if (injectCode == -1) {
                            systemCrashed = true;
                            Logger.err.println("** Error: RemoteException while injecting event.");
                        } else if (injectCode == -2 && ((systemCrashed = true ^ this.mIgnoreSecurityExceptions))) {
                            Logger.err.println("** Error: SecurityException while injecting event.");
                        }
                        if (!(ev instanceof MonkeyThrottleEvent)) {
                            eventCounter++;
                            if (this.mCountEvents) {
                                cycleCounter++;
                            }
                        }
                    } else if (this.mCountEvents) {
                        break;
                    } else {
                        cycleCounter++;
                        writeScriptLog(cycleCounter);
                        if (this.mGetPeriodicBugreport && cycleCounter % this.mBugreportFrequency == 0) {
                            this.mRequestPeriodicBugreport = true;
                        }
                    }
                }
            } catch (RuntimeException e) {
                Logger.error("** Error: A RuntimeException occurred:", e);
            }
        }
        Logger.out.println("Events injected: " + eventCounter);
        return eventCounter;
    }

    private void signalPersistentProcesses() {
        try {
            this.mAm.signalPersistentProcesses(10);
            synchronized (this) {
                wait(2000L);
            }
        } catch (RemoteException e) {
            Logger.err.println("** Failed talking with activity manager!");
        } catch (InterruptedException e2) {
        }
    }

    private boolean checkNativeCrashes() {
        String[] tombstones = TOMBSTONES_PATH.list();
        if (tombstones == null || tombstones.length == 0) {
            this.mTombstones = null;
            return false;
        }
        boolean result = false;
        HashSet<Long> newStones = new HashSet<>();
        for (String t : tombstones) {
            if (t.startsWith(TOMBSTONE_PREFIX)) {
                File file = TOMBSTONES_PATH;
                File f = new File(file, t);
                newStones.add(Long.valueOf(f.lastModified()));
                HashSet<Long> hashSet = this.mTombstones;
                if (hashSet == null || !hashSet.contains(Long.valueOf(f.lastModified()))) {
                    result = true;
                    waitForTombstoneToBeWritten(Paths.get(file.getPath(), t));
                    Logger.out.println("** New tombstone found: " + f.getAbsolutePath() + ", size: " + f.length());
                }
            }
        }
        this.mTombstones = newStones;
        return result;
    }

    private void waitForTombstoneToBeWritten(Path path) {
        boolean isWritten = false;
        int i = 0;
        while (true) {
            try {
                if (i >= NUM_READ_TOMBSTONE_RETRIES) {
                    break;
                }
                long size = Files.size(path);
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                }
                if (size <= 0 || Files.size(path) != size) {
                    i++;
                } else {
                    isWritten = true;
                    break;
                }
            } catch (IOException e2) {
                Logger.err.println("Failed to get tombstone file size: " + e2.toString());
            }
        }
        if (!isWritten) {
            Logger.err.println("Incomplete tombstone file.");
        }
    }

    private String nextOption() {
        int i = this.mNextArg;
        String[] strArr = this.mArgs;
        if (i >= strArr.length) {
            return null;
        }
        String arg = strArr[i];
        if (arg.startsWith("-")) {
            this.mNextArg++;
            if (arg.equals("--")) {
                return null;
            }
            if (arg.length() > 1 && arg.charAt(1) != '-') {
                if (arg.length() > 2) {
                    this.mCurArgData = arg.substring(2);
                    return arg.substring(0, 2);
                }
                this.mCurArgData = null;
                return arg;
            }
            this.mCurArgData = null;
            Logger.err.println("arg=\"" + arg + "\" mCurArgData=\"" + this.mCurArgData + "\" mNextArg=" + this.mNextArg + " argwas=\"" + this.mArgs[this.mNextArg - 1] + "\" nextarg=\"" + this.mArgs[this.mNextArg] + "\"");
            return arg;
        }
        return null;
    }

    private String nextOptionData() {
        String str = this.mCurArgData;
        if (str != null) {
            return str;
        }
        int i = this.mNextArg;
        String[] strArr = this.mArgs;
        if (i >= strArr.length) {
            return null;
        }
        String data = strArr[i];
        Logger.err.println("data=\"" + data + "\"");
        this.mNextArg++;
        return data;
    }

    private long nextOptionLong(String opt) {
        try {
            long result = Long.parseLong(nextOptionData());
            return result;
        } catch (NumberFormatException e) {
            Logger.err.println("** Error: " + opt + " is not a number");
            throw e;
        }
    }

    private String nextArg() {
        int i = this.mNextArg;
        String[] strArr = this.mArgs;
        if (i >= strArr.length) {
            return null;
        }
        String arg = strArr[i];
        this.mNextArg = i + 1;
        return arg;
    }

    private void showUsage() {
        StringBuffer usage = new StringBuffer();
        usage.append("usage: monkey [-p ALLOWED_PACKAGE [-p ALLOWED_PACKAGE] ...]\n");
        usage.append("              [-c MAIN_CATEGORY [-c MAIN_CATEGORY] ...]\n");
        usage.append("              [--ignore-crashes] [--ignore-timeouts]\n");
        usage.append("              [--ignore-security-exceptions]\n");
        usage.append("              [--monitor-native-crashes] [--ignore-native-crashes]\n");
        usage.append("              [--kill-process-after-error] [--hprof]\n");
        usage.append("              [--match-description TEXT]\n");
        usage.append("              [--pct-touch PERCENT] [--pct-motion PERCENT]\n");
        usage.append("              [--pct-trackball PERCENT] [--pct-syskeys PERCENT]\n");
        usage.append("              [--pct-nav PERCENT] [--pct-majornav PERCENT]\n");
        usage.append("              [--pct-appswitch PERCENT] [--pct-flip PERCENT]\n");
        usage.append("              [--pct-anyevent PERCENT] [--pct-pinchzoom PERCENT]\n");
        usage.append("              [--pct-permission PERCENT]\n");
        usage.append("              [--pkg-blacklist-file PACKAGE_BLACKLIST_FILE]\n");
        usage.append("              [--pkg-whitelist-file PACKAGE_WHITELIST_FILE]\n");
        usage.append("              [--wait-dbg] [--dbg-no-events]\n");
        usage.append("              [--setup scriptfile] [-f scriptfile [-f scriptfile] ...]\n");
        usage.append("              [--port port]\n");
        usage.append("              [-s SEED] [-v [-v] ...]\n");
        usage.append("              [--throttle MILLISEC] [--randomize-throttle]\n");
        usage.append("              [--profile-wait MILLISEC]\n");
        usage.append("              [--device-sleep-time MILLISEC]\n");
        usage.append("              [--randomize-script]\n");
        usage.append("              [--script-log]\n");
        usage.append("              [--bugreport]\n");
        usage.append("              [--periodic-bugreport]\n");
        usage.append("              [--permission-target-system]\n");
        usage.append("              COUNT\n");
        Logger.err.println(usage.toString());
    }
}
