package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.FileUtils;
import android.os.MessageQueue;
import android.os.ParcelFileDescriptor;
import android.os.RecoverySystem;
import android.os.SystemProperties;
import android.provider.Downloads;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.text.TextUtils;
import android.util.AtomicFile;
import android.util.EventLog;
import android.util.Slog;
import android.util.Xml;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.jobs.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.p006am.DropboxRateLimiter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class BootReceiver extends BroadcastReceiver {
    public static final int LASTK_LOG_SIZE;
    public static final String[] LAST_KMSG_FILES;
    public static final int LOG_SIZE;
    public static final String[] MOUNT_DURATION_PROPS_POSTFIX;
    public static final File TOMBSTONE_TMP_DIR;
    public static final File lastHeaderFile;
    public static final DropboxRateLimiter sDropboxRateLimiter;
    public static final AtomicFile sFile;
    public static int sSentReports;

    static {
        LOG_SIZE = SystemProperties.getInt("ro.debuggable", 0) == 1 ? 98304 : 65536;
        LASTK_LOG_SIZE = SystemProperties.getInt("ro.debuggable", 0) == 1 ? 196608 : 65536;
        TOMBSTONE_TMP_DIR = new File("/data/tombstones");
        sFile = new AtomicFile(new File(Environment.getDataSystemDirectory(), "log-files.xml"), "log-files");
        lastHeaderFile = new File(Environment.getDataSystemDirectory(), "last-header.txt");
        MOUNT_DURATION_PROPS_POSTFIX = new String[]{"early", "default", "late"};
        LAST_KMSG_FILES = new String[]{"/sys/fs/pstore/console-ramoops", "/proc/last_kmsg"};
        sSentReports = 0;
        sDropboxRateLimiter = new DropboxRateLimiter();
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, Intent intent) {
        new Thread() { // from class: com.android.server.BootReceiver.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    BootReceiver.this.logBootEvents(context);
                } catch (Exception e) {
                    Slog.e("BootReceiver", "Can't log boot events", e);
                }
                try {
                    BootReceiver.this.removeOldUpdatePackages(context);
                } catch (Exception e2) {
                    Slog.e("BootReceiver", "Can't remove old update packages", e2);
                }
            }
        }.start();
        try {
            FileDescriptor open = Os.open("/sys/kernel/tracing/instances/bootreceiver/trace_pipe", OsConstants.O_RDONLY, FrameworkStatsLog.NON_A11Y_TOOL_SERVICE_WARNING_REPORT);
            IoThread.get().getLooper().getQueue().addOnFileDescriptorEventListener(open, 1, new MessageQueue.OnFileDescriptorEventListener() { // from class: com.android.server.BootReceiver.2
                public final int mBufferSize = 1024;
                public byte[] mTraceBuffer = new byte[1024];

                @Override // android.os.MessageQueue.OnFileDescriptorEventListener
                public int onFileDescriptorEvents(FileDescriptor fileDescriptor, int i) {
                    try {
                        if (Os.read(fileDescriptor, this.mTraceBuffer, 0, 1024) > 0 && new String(this.mTraceBuffer).indexOf("\n") != -1 && BootReceiver.sSentReports < 8) {
                            SystemProperties.set("dmesgd.start", "1");
                            BootReceiver.sSentReports++;
                        }
                        return 1;
                    } catch (Exception e) {
                        Slog.wtf("BootReceiver", "Error watching for trace events", e);
                        return 0;
                    }
                }
            });
        } catch (ErrnoException e) {
            Slog.wtf("BootReceiver", "Could not open /sys/kernel/tracing/instances/bootreceiver/trace_pipe", e);
        }
    }

    public final void removeOldUpdatePackages(Context context) {
        Downloads.removeAllDownloadsByPackage(context, "com.google.android.systemupdater", "com.google.android.systemupdater.SystemUpdateReceiver");
    }

    public static String getPreviousBootHeaders() {
        try {
            return FileUtils.readTextFile(lastHeaderFile, 0, null);
        } catch (IOException unused) {
            return null;
        }
    }

    public static String getCurrentBootHeaders() throws IOException {
        StringBuilder sb = new StringBuilder(512);
        sb.append("Build: ");
        sb.append(Build.FINGERPRINT);
        sb.append("\n");
        sb.append("Hardware: ");
        sb.append(Build.BOARD);
        sb.append("\n");
        sb.append("Revision: ");
        sb.append(SystemProperties.get("ro.revision", ""));
        sb.append("\n");
        sb.append("Bootloader: ");
        sb.append(Build.BOOTLOADER);
        sb.append("\n");
        sb.append("Radio: ");
        sb.append(Build.getRadioVersion());
        sb.append("\n");
        sb.append("Kernel: ");
        sb.append(FileUtils.readTextFile(new File("/proc/version"), 1024, "...\n"));
        sb.append("\n");
        return sb.toString();
    }

    public static String getBootHeadersToLogAndUpdate() throws IOException {
        String previousBootHeaders = getPreviousBootHeaders();
        String currentBootHeaders = getCurrentBootHeaders();
        try {
            FileUtils.stringToFile(lastHeaderFile, currentBootHeaders);
        } catch (IOException e) {
            Slog.e("BootReceiver", "Error writing " + lastHeaderFile, e);
        }
        if (previousBootHeaders == null) {
            return "isPrevious: false\n" + currentBootHeaders;
        }
        return "isPrevious: true\n" + previousBootHeaders;
    }

    public final void logBootEvents(Context context) throws IOException {
        String str;
        DropBoxManager dropBoxManager = (DropBoxManager) context.getSystemService("dropbox");
        String bootHeadersToLogAndUpdate = getBootHeadersToLogAndUpdate();
        String str2 = SystemProperties.get("ro.boot.bootreason", (String) null);
        String handleAftermath = RecoverySystem.handleAftermath(context);
        if (handleAftermath != null && dropBoxManager != null) {
            dropBoxManager.addText("SYSTEM_RECOVERY_LOG", bootHeadersToLogAndUpdate + handleAftermath);
        }
        if (str2 != null) {
            StringBuilder sb = new StringBuilder(512);
            sb.append("\n");
            sb.append("Boot info:\n");
            sb.append("Last boot reason: ");
            sb.append(str2);
            sb.append("\n");
            str = sb.toString();
        } else {
            str = "";
        }
        HashMap<String, Long> readTimestamps = readTimestamps();
        if (SystemProperties.getLong("ro.runtime.firstboot", 0L) == 0) {
            SystemProperties.set("ro.runtime.firstboot", Long.toString(System.currentTimeMillis()));
            if (dropBoxManager != null) {
                dropBoxManager.addText("SYSTEM_BOOT", bootHeadersToLogAndUpdate);
            }
            int i = LASTK_LOG_SIZE;
            String str3 = str;
            addLastkToDropBox(dropBoxManager, readTimestamps, bootHeadersToLogAndUpdate, str3, "/proc/last_kmsg", -i, "SYSTEM_LAST_KMSG");
            addLastkToDropBox(dropBoxManager, readTimestamps, bootHeadersToLogAndUpdate, str3, "/sys/fs/pstore/console-ramoops", -i, "SYSTEM_LAST_KMSG");
            addLastkToDropBox(dropBoxManager, readTimestamps, bootHeadersToLogAndUpdate, str3, "/sys/fs/pstore/console-ramoops-0", -i, "SYSTEM_LAST_KMSG");
            int i2 = LOG_SIZE;
            addFileToDropBox(dropBoxManager, readTimestamps, bootHeadersToLogAndUpdate, "/cache/recovery/log", -i2, "SYSTEM_RECOVERY_LOG");
            addFileToDropBox(dropBoxManager, readTimestamps, bootHeadersToLogAndUpdate, "/cache/recovery/last_kmsg", -i2, "SYSTEM_RECOVERY_KMSG");
            addAuditErrorsToDropBox(dropBoxManager, readTimestamps, bootHeadersToLogAndUpdate, -i2, "SYSTEM_AUDIT");
        } else if (dropBoxManager != null) {
            dropBoxManager.addText("SYSTEM_RESTART", bootHeadersToLogAndUpdate);
        }
        logFsShutdownTime();
        logFsMountTime();
        addFsckErrorsToDropBoxAndLogFsStat(dropBoxManager, readTimestamps, bootHeadersToLogAndUpdate, -LOG_SIZE, "SYSTEM_FSCK");
        logSystemServerShutdownTimeMetrics();
        writeTimestamps(readTimestamps);
    }

    @VisibleForTesting
    public static void resetDropboxRateLimiter() {
        sDropboxRateLimiter.reset();
    }

    public static void addTombstoneToDropBox(Context context, File file, boolean z, String str) {
        DropBoxManager dropBoxManager = (DropBoxManager) context.getSystemService(DropBoxManager.class);
        if (dropBoxManager == null) {
            Slog.e("BootReceiver", "Can't log tombstone: DropBoxManager not available");
            return;
        }
        DropboxRateLimiter.RateLimitResult shouldRateLimit = sDropboxRateLimiter.shouldRateLimit(z ? "SYSTEM_TOMBSTONE_PROTO_WITH_HEADERS" : "SYSTEM_TOMBSTONE", str);
        if (shouldRateLimit.shouldRateLimit()) {
            return;
        }
        HashMap<String, Long> readTimestamps = readTimestamps();
        try {
            if (z) {
                if (recordFileTimestamp(file, readTimestamps)) {
                    byte[] readAllBytes = Files.readAllBytes(file.toPath());
                    File createTempFile = File.createTempFile(file.getName(), ".tmp", TOMBSTONE_TMP_DIR);
                    Files.setPosixFilePermissions(createTempFile.toPath(), PosixFilePermissions.fromString("rw-rw----"));
                    try {
                        ProtoOutputStream protoOutputStream = new ProtoOutputStream(ParcelFileDescriptor.open(createTempFile, 805306368).getFileDescriptor());
                        protoOutputStream.write(1151051235329L, readAllBytes);
                        protoOutputStream.write(1120986464258L, shouldRateLimit.droppedCountSinceRateLimitActivated());
                        protoOutputStream.flush();
                        dropBoxManager.addFile("SYSTEM_TOMBSTONE_PROTO_WITH_HEADERS", createTempFile, 0);
                        createTempFile.delete();
                    } catch (FileNotFoundException e) {
                        Slog.e("BootReceiver", "failed to open for write: " + createTempFile, e);
                        throw e;
                    }
                }
            } else {
                addFileToDropBox(dropBoxManager, readTimestamps, getBootHeadersToLogAndUpdate() + shouldRateLimit.createHeader(), file.getPath(), LOG_SIZE, "SYSTEM_TOMBSTONE");
            }
        } catch (IOException e2) {
            Slog.e("BootReceiver", "Can't log tombstone", e2);
        }
        writeTimestamps(readTimestamps);
    }

    public static void addLastkToDropBox(DropBoxManager dropBoxManager, HashMap<String, Long> hashMap, String str, String str2, String str3, int i, String str4) throws IOException {
        int length = str.length() + 14 + str2.length();
        if (LASTK_LOG_SIZE + length > 196608) {
            i = 196608 > length ? -(196608 - length) : 0;
        }
        addFileWithFootersToDropBox(dropBoxManager, hashMap, str, str2, str3, i, str4);
    }

    public static void addFileToDropBox(DropBoxManager dropBoxManager, HashMap<String, Long> hashMap, String str, String str2, int i, String str3) throws IOException {
        addFileWithFootersToDropBox(dropBoxManager, hashMap, str, "", str2, i, str3);
    }

    public static void addFileWithFootersToDropBox(DropBoxManager dropBoxManager, HashMap<String, Long> hashMap, String str, String str2, String str3, int i, String str4) throws IOException {
        if (dropBoxManager == null || !dropBoxManager.isTagEnabled(str4)) {
            return;
        }
        File file = new File(str3);
        if (recordFileTimestamp(file, hashMap)) {
            String readTextFile = FileUtils.readTextFile(file, i, "[[TRUNCATED]]\n");
            String str5 = str + readTextFile + str2;
            if (str4.equals("SYSTEM_TOMBSTONE") && readTextFile.contains(">>> system_server <<<")) {
                addTextToDropBox(dropBoxManager, "system_server_native_crash", str5, str3, i);
            }
            if (str4.equals("SYSTEM_TOMBSTONE")) {
                FrameworkStatsLog.write(186);
            }
            addTextToDropBox(dropBoxManager, str4, str5, str3, i);
        }
    }

    public static boolean recordFileTimestamp(File file, HashMap<String, Long> hashMap) {
        long lastModified = file.lastModified();
        if (lastModified <= 0) {
            return false;
        }
        String path = file.getPath();
        if (hashMap.containsKey(path) && hashMap.get(path).longValue() == lastModified) {
            return false;
        }
        hashMap.put(path, Long.valueOf(lastModified));
        return true;
    }

    public static void addTextToDropBox(DropBoxManager dropBoxManager, String str, String str2, String str3, int i) {
        Slog.i("BootReceiver", "Copying " + str3 + " to DropBox (" + str + ")");
        dropBoxManager.addText(str, str2);
        EventLog.writeEvent(81002, str3, Integer.valueOf(i), str);
    }

    public static void addAuditErrorsToDropBox(DropBoxManager dropBoxManager, HashMap<String, Long> hashMap, String str, int i, String str2) throws IOException {
        String[] split;
        if (dropBoxManager == null || !dropBoxManager.isTagEnabled(str2)) {
            return;
        }
        Slog.i("BootReceiver", "Copying audit failures to DropBox");
        File file = new File("/proc/last_kmsg");
        long lastModified = file.lastModified();
        if (lastModified <= 0) {
            file = new File("/sys/fs/pstore/console-ramoops");
            lastModified = file.lastModified();
            if (lastModified <= 0) {
                file = new File("/sys/fs/pstore/console-ramoops-0");
                lastModified = file.lastModified();
            }
        }
        if (lastModified <= 0) {
            return;
        }
        if (hashMap.containsKey(str2) && hashMap.get(str2).longValue() == lastModified) {
            return;
        }
        hashMap.put(str2, Long.valueOf(lastModified));
        String readTextFile = FileUtils.readTextFile(file, i, "[[TRUNCATED]]\n");
        StringBuilder sb = new StringBuilder();
        for (String str3 : readTextFile.split("\n")) {
            if (str3.contains("audit")) {
                sb.append(str3 + "\n");
            }
        }
        Slog.i("BootReceiver", "Copied " + sb.toString().length() + " worth of audits to DropBox");
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append(sb.toString());
        dropBoxManager.addText(str2, sb2.toString());
    }

    public static void addFsckErrorsToDropBoxAndLogFsStat(DropBoxManager dropBoxManager, HashMap<String, Long> hashMap, String str, int i, String str2) throws IOException {
        boolean z = dropBoxManager != null && dropBoxManager.isTagEnabled(str2);
        Slog.i("BootReceiver", "Checking for fsck errors");
        File file = new File("/dev/fscklogs/log");
        if (file.lastModified() <= 0) {
            return;
        }
        String readTextFile = FileUtils.readTextFile(file, i, "[[TRUNCATED]]\n");
        Pattern compile = Pattern.compile("fs_stat,[^,]*/([^/,]+),(0x[0-9a-fA-F]+)");
        String[] split = readTextFile.split("\n");
        boolean z2 = false;
        int i2 = 0;
        int i3 = 0;
        for (String str3 : split) {
            if (str3.contains("FILE SYSTEM WAS MODIFIED") || str3.contains("[FSCK] Unreachable")) {
                z2 = true;
            } else if (str3.contains("fs_stat")) {
                Matcher matcher = compile.matcher(str3);
                if (matcher.find()) {
                    handleFsckFsStat(matcher, split, i2, i3);
                    i2 = i3;
                } else {
                    Slog.w("BootReceiver", "cannot parse fs_stat:" + str3);
                }
            }
            i3++;
        }
        if (z && z2) {
            addFileToDropBox(dropBoxManager, hashMap, str, "/dev/fscklogs/log", i, str2);
        }
        file.renameTo(new File("/dev/fscklogs/fsck"));
    }

    public static void logFsMountTime() {
        String[] strArr;
        int i;
        for (String str : MOUNT_DURATION_PROPS_POSTFIX) {
            int i2 = SystemProperties.getInt("ro.boottime.init.mount_all." + str, 0);
            if (i2 != 0) {
                str.hashCode();
                char c = 65535;
                switch (str.hashCode()) {
                    case 3314342:
                        if (str.equals("late")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 96278371:
                        if (str.equals("early")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 1544803905:
                        if (str.equals("default")) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        i = 12;
                        FrameworkStatsLog.write((int) FrameworkStatsLog.BOOT_TIME_EVENT_DURATION_REPORTED, i, i2);
                        break;
                    case 1:
                        i = 11;
                        FrameworkStatsLog.write((int) FrameworkStatsLog.BOOT_TIME_EVENT_DURATION_REPORTED, i, i2);
                        break;
                    case 2:
                        i = 10;
                        FrameworkStatsLog.write((int) FrameworkStatsLog.BOOT_TIME_EVENT_DURATION_REPORTED, i, i2);
                        break;
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0032  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void logSystemServerShutdownTimeMetrics() {
        String readTextFile;
        File file = new File("/data/system/shutdown-metrics.txt");
        String str = null;
        if (file.exists()) {
            try {
                readTextFile = FileUtils.readTextFile(file, 0, null);
            } catch (IOException e) {
                Slog.e("BootReceiver", "Problem reading " + file, e);
            }
            if (!TextUtils.isEmpty(readTextFile)) {
                String str2 = null;
                String str3 = null;
                String str4 = null;
                for (String str5 : readTextFile.split(",")) {
                    String[] split = str5.split(XmlUtils.STRING_ARRAY_SEPARATOR);
                    if (split.length != 2) {
                        Slog.e("BootReceiver", "Wrong format of shutdown metrics - " + readTextFile);
                    } else {
                        if (split[0].startsWith("shutdown_")) {
                            logTronShutdownMetric(split[0], split[1]);
                            if (split[0].equals("shutdown_system_server")) {
                                str4 = split[1];
                            }
                        }
                        if (split[0].equals("reboot")) {
                            str = split[1];
                        } else if (split[0].equals("reason")) {
                            str2 = split[1];
                        } else if (split[0].equals("begin_shutdown")) {
                            str3 = split[1];
                        }
                    }
                }
                logStatsdShutdownAtom(str, str2, str3, str4);
            }
            file.delete();
        }
        readTextFile = null;
        if (!TextUtils.isEmpty(readTextFile)) {
        }
        file.delete();
    }

    public static void logTronShutdownMetric(String str, String str2) {
        try {
            int parseInt = Integer.parseInt(str2);
            if (parseInt >= 0) {
                MetricsLogger.histogram((Context) null, str, parseInt);
            }
        } catch (NumberFormatException unused) {
            Slog.e("BootReceiver", "Cannot parse metric " + str + " int value - " + str2);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0037  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x005d  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x007f  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0043 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0065 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void logStatsdShutdownAtom(String str, String str2, String str3, String str4) {
        boolean z;
        long parseLong;
        if (str != null) {
            if (!str.equals("y")) {
                if (!str.equals("n")) {
                    Slog.e("BootReceiver", "Unexpected value for reboot : " + str);
                }
            } else {
                z = true;
                boolean z2 = z;
                if (str2 == null) {
                    Slog.e("BootReceiver", "No value received for shutdown reason");
                    str2 = "<EMPTY>";
                }
                String str5 = str2;
                long j = 0;
                if (str3 == null) {
                    try {
                        parseLong = Long.parseLong(str3);
                    } catch (NumberFormatException unused) {
                        Slog.e("BootReceiver", "Cannot parse shutdown start time: " + str3);
                    }
                    if (str4 != null) {
                        try {
                            j = Long.parseLong(str4);
                        } catch (NumberFormatException unused2) {
                            Slog.e("BootReceiver", "Cannot parse shutdown duration: " + str3);
                        }
                    } else {
                        Slog.e("BootReceiver", "No value received for shutdown duration");
                    }
                    FrameworkStatsLog.write(56, z2, str5, parseLong, j);
                }
                Slog.e("BootReceiver", "No value received for shutdown start time");
                parseLong = 0;
                if (str4 != null) {
                }
                FrameworkStatsLog.write(56, z2, str5, parseLong, j);
            }
        } else {
            Slog.e("BootReceiver", "No value received for reboot");
        }
        z = false;
        boolean z22 = z;
        if (str2 == null) {
        }
        String str52 = str2;
        long j2 = 0;
        if (str3 == null) {
        }
        parseLong = 0;
        if (str4 != null) {
        }
        FrameworkStatsLog.write(56, z22, str52, parseLong, j2);
    }

    public static void logFsShutdownTime() {
        File file;
        String[] strArr = LAST_KMSG_FILES;
        int length = strArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                file = null;
                break;
            }
            file = new File(strArr[i]);
            if (file.exists()) {
                break;
            }
            i++;
        }
        if (file == null) {
            return;
        }
        try {
            Matcher matcher = Pattern.compile("powerctl_shutdown_time_ms:([0-9]+):([0-9]+)", 8).matcher(FileUtils.readTextFile(file, -16384, null));
            if (matcher.find()) {
                FrameworkStatsLog.write((int) FrameworkStatsLog.BOOT_TIME_EVENT_DURATION_REPORTED, 9, Integer.parseInt(matcher.group(1)));
                FrameworkStatsLog.write((int) FrameworkStatsLog.BOOT_TIME_EVENT_ERROR_CODE_REPORTED, 2, Integer.parseInt(matcher.group(2)));
                Slog.i("BootReceiver", "boot_fs_shutdown," + matcher.group(1) + "," + matcher.group(2));
                return;
            }
            FrameworkStatsLog.write((int) FrameworkStatsLog.BOOT_TIME_EVENT_ERROR_CODE_REPORTED, 2, 4);
            Slog.w("BootReceiver", "boot_fs_shutdown, string not found");
        } catch (IOException e) {
            Slog.w("BootReceiver", "cannot read last msg", e);
        }
    }

    @VisibleForTesting
    public static int fixFsckFsStat(String str, int i, String[] strArr, int i2, int i3) {
        String str2;
        boolean z;
        int i4;
        if ((i & 1024) != 0) {
            Pattern compile = Pattern.compile("Pass ([1-9]E?):");
            Pattern compile2 = Pattern.compile("Inode [0-9]+ extent tree.*could be shorter");
            String str3 = "";
            boolean z2 = false;
            boolean z3 = false;
            boolean z4 = false;
            int i5 = i2;
            while (i5 < i3) {
                str2 = strArr[i5];
                if (str2.contains("FILE SYSTEM WAS MODIFIED") || str2.contains("[FSCK] Unreachable")) {
                    break;
                }
                if (str2.startsWith("Pass ")) {
                    Matcher matcher = compile.matcher(str2);
                    if (matcher.find()) {
                        str3 = matcher.group(1);
                    }
                    i4 = 1;
                } else if (str2.startsWith("Inode ")) {
                    if (!compile2.matcher(str2).find() || !str3.equals("1")) {
                        z = true;
                        break;
                    }
                    Slog.i("BootReceiver", "fs_stat, partition:" + str + " found tree optimization:" + str2);
                    i4 = 1;
                    z2 = true;
                } else if (str2.startsWith("[QUOTA WARNING]") && str3.equals("5")) {
                    Slog.i("BootReceiver", "fs_stat, partition:" + str + " found quota warning:" + str2);
                    if (!z2) {
                        z = false;
                        z3 = true;
                        break;
                    }
                    i4 = 1;
                    z3 = true;
                } else {
                    if (!str2.startsWith("Update quota info") || !str3.equals("5")) {
                        if (str2.startsWith("Timestamp(s) on inode") && str2.contains("beyond 2310-04-04 are likely pre-1970") && str3.equals("1")) {
                            Slog.i("BootReceiver", "fs_stat, partition:" + str + " found timestamp adjustment:" + str2);
                            int i6 = i5 + 1;
                            if (strArr[i6].contains("Fix? yes")) {
                                i5 = i6;
                            }
                            i4 = 1;
                            z4 = true;
                        } else {
                            str2 = str2.trim();
                            if (!str2.isEmpty() && !str3.isEmpty()) {
                                z = true;
                                break;
                            }
                        }
                    }
                    i4 = 1;
                }
                i5 += i4;
            }
            str2 = null;
            z = false;
            if (z) {
                if (str2 != null) {
                    Slog.i("BootReceiver", "fs_stat, partition:" + str + " fix:" + str2);
                }
            } else if (z3 && !z2) {
                Slog.i("BootReceiver", "fs_stat, got quota fix without tree optimization, partition:" + str);
            } else if ((z2 && z3) || z4) {
                Slog.i("BootReceiver", "fs_stat, partition:" + str + " fix ignored");
                return i & (-1025);
            }
        }
        return i;
    }

    public static void handleFsckFsStat(Matcher matcher, String[] strArr, int i, int i2) {
        String group = matcher.group(1);
        try {
            int fixFsckFsStat = fixFsckFsStat(group, Integer.decode(matcher.group(2)).intValue(), strArr, i, i2);
            if ("userdata".equals(group) || "data".equals(group)) {
                FrameworkStatsLog.write((int) FrameworkStatsLog.BOOT_TIME_EVENT_ERROR_CODE_REPORTED, 3, fixFsckFsStat);
            }
            Slog.i("BootReceiver", "fs_stat, partition:" + group + " stat:0x" + Integer.toHexString(fixFsckFsStat));
        } catch (NumberFormatException unused) {
            Slog.w("BootReceiver", "cannot parse fs_stat: partition:" + group + " stat:" + matcher.group(2));
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:55:0x00bd, code lost:
        if (r7 != false) goto L31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x0115, code lost:
        if (r7 != false) goto L31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x013b, code lost:
        if (r2 != false) goto L26;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static HashMap<String, Long> readTimestamps() {
        HashMap<String, Long> hashMap;
        boolean z;
        Throwable th;
        XmlPullParserException e;
        NullPointerException e2;
        IllegalStateException e3;
        IOException e4;
        FileInputStream openRead;
        TypedXmlPullParser resolvePullParser;
        int next;
        AtomicFile atomicFile = sFile;
        synchronized (atomicFile) {
            hashMap = new HashMap<>();
            boolean z2 = false;
            try {
                try {
                    try {
                        openRead = atomicFile.openRead();
                        try {
                            resolvePullParser = Xml.resolvePullParser(openRead);
                            while (true) {
                                next = resolvePullParser.next();
                                z = true;
                                if (next == 2 || next == 1) {
                                    break;
                                }
                            }
                        } finally {
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (!z) {
                            hashMap.clear();
                        }
                        throw th;
                    }
                } catch (FileNotFoundException unused) {
                } catch (IOException e5) {
                    z = false;
                    e4 = e5;
                } catch (IllegalStateException e6) {
                    z = false;
                    e3 = e6;
                } catch (NullPointerException e7) {
                    z = false;
                    e2 = e7;
                } catch (XmlPullParserException e8) {
                    z = false;
                    e = e8;
                }
                if (next != 2) {
                    throw new IllegalStateException("no start tag found");
                }
                int depth = resolvePullParser.getDepth();
                while (true) {
                    int next2 = resolvePullParser.next();
                    if (next2 == 1 || (next2 == 3 && resolvePullParser.getDepth() <= depth)) {
                        break;
                    } else if (next2 != 3 && next2 != 4) {
                        if (resolvePullParser.getName().equals("log")) {
                            hashMap.put(resolvePullParser.getAttributeValue((String) null, "filename"), Long.valueOf(resolvePullParser.getAttributeLong((String) null, "timestamp")));
                        } else {
                            Slog.w("BootReceiver", "Unknown tag: " + resolvePullParser.getName());
                            com.android.internal.util.XmlUtils.skipCurrentTag(resolvePullParser);
                        }
                    }
                }
                if (openRead != null) {
                    try {
                        openRead.close();
                    } catch (FileNotFoundException unused2) {
                        z2 = true;
                        Slog.i("BootReceiver", "No existing last log timestamp file " + sFile.getBaseFile() + "; starting empty");
                    } catch (IOException e9) {
                        e4 = e9;
                        Slog.w("BootReceiver", "Failed parsing " + e4);
                    } catch (IllegalStateException e10) {
                        e3 = e10;
                        Slog.w("BootReceiver", "Failed parsing " + e3);
                        if (!z) {
                            hashMap.clear();
                        }
                        return hashMap;
                    } catch (NullPointerException e11) {
                        e2 = e11;
                        Slog.w("BootReceiver", "Failed parsing " + e2);
                        if (!z) {
                            hashMap.clear();
                        }
                        return hashMap;
                    } catch (XmlPullParserException e12) {
                        e = e12;
                        Slog.w("BootReceiver", "Failed parsing " + e);
                    }
                }
            } catch (Throwable th3) {
                z = false;
                th = th3;
            }
        }
        return hashMap;
    }

    public static void writeTimestamps(HashMap<String, Long> hashMap) {
        AtomicFile atomicFile = sFile;
        synchronized (atomicFile) {
            try {
                try {
                    FileOutputStream startWrite = atomicFile.startWrite();
                    try {
                        TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
                        resolveSerializer.startDocument((String) null, Boolean.TRUE);
                        resolveSerializer.startTag((String) null, "log-files");
                        for (String str : hashMap.keySet()) {
                            resolveSerializer.startTag((String) null, "log");
                            resolveSerializer.attribute((String) null, "filename", str);
                            resolveSerializer.attributeLong((String) null, "timestamp", hashMap.get(str).longValue());
                            resolveSerializer.endTag((String) null, "log");
                        }
                        resolveSerializer.endTag((String) null, "log-files");
                        resolveSerializer.endDocument();
                        sFile.finishWrite(startWrite);
                    } catch (IOException e) {
                        Slog.w("BootReceiver", "Failed to write timestamp file, using the backup: " + e);
                        sFile.failWrite(startWrite);
                    }
                } catch (IOException e2) {
                    Slog.w("BootReceiver", "Failed to write timestamp file: " + e2);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
