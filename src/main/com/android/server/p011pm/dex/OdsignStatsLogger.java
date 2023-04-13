package com.android.server.p011pm.dex;

import android.util.Slog;
import com.android.internal.art.ArtStatsLog;
import com.android.internal.os.BackgroundThread;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import libcore.io.IoUtils;
/* renamed from: com.android.server.pm.dex.OdsignStatsLogger */
/* loaded from: classes2.dex */
public class OdsignStatsLogger {
    public static void triggerStatsWrite() {
        BackgroundThread.getExecutor().execute(new Runnable() { // from class: com.android.server.pm.dex.OdsignStatsLogger$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                OdsignStatsLogger.writeStats();
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0064  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x00bc A[Catch: IOException -> 0x00fa, FileNotFoundException -> 0x0100, TryCatch #3 {FileNotFoundException -> 0x0100, IOException -> 0x00fa, blocks: (B:3:0x0006, B:5:0x0015, B:6:0x001a, B:8:0x0025, B:10:0x0033, B:13:0x0039, B:28:0x0066, B:29:0x007f, B:32:0x0084, B:34:0x009a, B:36:0x00a6, B:37:0x00bc, B:39:0x00c0, B:40:0x00d8, B:18:0x004a, B:21:0x0054, B:41:0x00f1), top: B:49:0x0006 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void writeStats() {
        String[] split;
        char c;
        try {
            String readFileAsString = IoUtils.readFileAsString("/data/misc/odsign/metrics/odsign-metrics.txt");
            if (!new File("/data/misc/odsign/metrics/odsign-metrics.txt").delete()) {
                Slog.w("OdsignStatsLogger", "Failed to delete metrics file");
            }
            for (String str : readFileAsString.split("\n")) {
                String[] split2 = str.split(" ");
                if (!str.isEmpty() && split2.length >= 1) {
                    String str2 = split2[0];
                    int hashCode = str2.hashCode();
                    if (hashCode != 890271774) {
                        if (hashCode == 1023928721 && str2.equals("comp_os_artifacts_check_record")) {
                            c = 0;
                            if (c == 0) {
                                if (c != 1) {
                                    Slog.w("OdsignStatsLogger", "Malformed metrics line '" + str + "'");
                                } else if (split2.length != 2) {
                                    Slog.w("OdsignStatsLogger", "Malformed odsign metrics line '" + str + "'");
                                } else {
                                    try {
                                        ArtStatsLog.write(548, Integer.parseInt(split2[1]));
                                    } catch (NumberFormatException unused) {
                                        Slog.w("OdsignStatsLogger", "Malformed odsign metrics line '" + str + "'");
                                    }
                                }
                            } else if (split2.length != 4) {
                                Slog.w("OdsignStatsLogger", "Malformed CompOS metrics line '" + str + "'");
                            } else {
                                ArtStatsLog.write(419, split2[1].equals("1"), split2[2].equals("1"), split2[3].equals("1"));
                            }
                        }
                        c = 65535;
                        if (c == 0) {
                        }
                    } else {
                        if (str2.equals("odsign_record")) {
                            c = 1;
                            if (c == 0) {
                            }
                        }
                        c = 65535;
                        if (c == 0) {
                        }
                    }
                }
                Slog.w("OdsignStatsLogger", "Empty metrics line");
            }
        } catch (FileNotFoundException unused2) {
        } catch (IOException e) {
            Slog.w("OdsignStatsLogger", "Reading metrics file failed", e);
        }
    }
}
