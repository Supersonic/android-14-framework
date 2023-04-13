package com.android.internal.app.procstats;

import android.app.backup.FullBackup;
import android.app.blob.XmlTags;
import android.content.Context;
import android.media.AudioSystem;
import android.p008os.IncidentManager;
import android.p008os.UserHandle;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.accessibility.common.ShortcutConstants;
import com.android.internal.content.NativeLibraryHelper;
import java.io.PrintWriter;
import java.util.ArrayList;
/* loaded from: classes4.dex */
public final class DumpUtils {
    public static final String[] ADJ_MEM_NAMES_CSV;
    static final int[] ADJ_MEM_PROTO_ENUMS;
    static final String[] ADJ_MEM_TAGS;
    public static final String[] ADJ_SCREEN_NAMES_CSV;
    static final int[] ADJ_SCREEN_PROTO_ENUMS;
    static final String[] ADJ_SCREEN_TAGS;
    static final String CSV_SEP = "\t";
    private static final int[] PROCESS_STATS_STATE_TO_AGGREGATED_STATE;
    public static final String[] STATE_LABELS;
    public static final String STATE_LABEL_CACHED = "  (Cached)";
    public static final String STATE_LABEL_TOTAL = "     TOTAL";
    public static final String[] STATE_NAMES;
    public static final String[] STATE_NAMES_CSV;
    static final int[] STATE_PROTO_ENUMS;
    static final String[] STATE_TAGS;

    static {
        STATE_NAMES = r1;
        String[] strArr = {"Persist", "Top", "BTopFgs", "Fgs", "ImpFg", "ImpBg", "Backup", "Service", "ServRst", "Receivr", "HeavyWt", "Home", "LastAct", "CchAct", "CchCAct", "CchEmty"};
        STATE_LABELS = r1;
        String[] strArr2 = {"Persistent", "       Top", "Bnd TopFgs", "       Fgs", "    Imp Fg", "    Imp Bg", "    Backup", "   Service", "Service Rs", "  Receiver", " Heavy Wgt", "    (Home)", "(Last Act)", " (Cch Act)", "(Cch CAct)", "(Cch Emty)"};
        STATE_NAMES_CSV = r1;
        String[] strArr3 = {"pers", "top", "btopfgs", "fgs", "impfg", "impbg", Context.BACKUP_SERVICE, "service", "service-rs", IncidentManager.URI_PARAM_RECEIVER_CLASS, "heavy", "home", "lastact", "cch-activity", "cch-aclient", "cch-empty"};
        STATE_TAGS = r1;
        String[] strArr4 = {"p", "t", XmlTags.ATTR_DESCRIPTION, "g", FullBackup.FILES_TREE_TOKEN, XmlTags.TAG_BLOB, XmlTags.ATTR_UID, XmlTags.TAG_SESSION, "x", "r", "w", "h", XmlTags.TAG_LEASEE, FullBackup.APK_TREE_TOKEN, "c", "e"};
        STATE_PROTO_ENUMS = r1;
        int[] iArr = {1, 2, 15, 16, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
        PROCESS_STATS_STATE_TO_AGGREGATED_STATE = r0;
        int[] iArr2 = {1, 2, 3, 4, 5, 6, 6, 6, 0, 7, 6, 8, 8, 8, 8, 8};
        ADJ_SCREEN_NAMES_CSV = new String[]{"off", "on"};
        ADJ_MEM_NAMES_CSV = new String[]{"norm", "mod", "low", "crit"};
        ADJ_SCREEN_TAGS = new String[]{AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, "1"};
        ADJ_SCREEN_PROTO_ENUMS = new int[]{1, 2};
        ADJ_MEM_TAGS = new String[]{"n", "m", XmlTags.TAG_LEASEE, "c"};
        ADJ_MEM_PROTO_ENUMS = new int[]{1, 2, 3, 4};
    }

    private DumpUtils() {
    }

    public static void printScreenLabel(PrintWriter pw, int offset) {
        switch (offset) {
            case -1:
                pw.print("     ");
                return;
            case 0:
                pw.print("SOff/");
                return;
            case 4:
                pw.print(" SOn/");
                return;
            default:
                pw.print("????/");
                return;
        }
    }

    public static void printScreenLabelCsv(PrintWriter pw, int offset) {
        switch (offset) {
            case -1:
                return;
            case 0:
                pw.print(ADJ_SCREEN_NAMES_CSV[0]);
                return;
            case 4:
                pw.print(ADJ_SCREEN_NAMES_CSV[1]);
                return;
            default:
                pw.print("???");
                return;
        }
    }

    public static void printMemLabel(PrintWriter pw, int offset, char sep) {
        switch (offset) {
            case -1:
                pw.print("    ");
                if (sep != 0) {
                    pw.print(' ');
                    return;
                }
                return;
            case 0:
                pw.print("Norm");
                if (sep != 0) {
                    pw.print(sep);
                    return;
                }
                return;
            case 1:
                pw.print(" Mod");
                if (sep != 0) {
                    pw.print(sep);
                    return;
                }
                return;
            case 2:
                pw.print(" Low");
                if (sep != 0) {
                    pw.print(sep);
                    return;
                }
                return;
            case 3:
                pw.print("Crit");
                if (sep != 0) {
                    pw.print(sep);
                    return;
                }
                return;
            default:
                pw.print("????");
                if (sep != 0) {
                    pw.print(sep);
                    return;
                }
                return;
        }
    }

    public static void printMemLabelCsv(PrintWriter pw, int offset) {
        if (offset >= 0) {
            if (offset <= 3) {
                pw.print(ADJ_MEM_NAMES_CSV[offset]);
            } else {
                pw.print("???");
            }
        }
    }

    public static void printPercent(PrintWriter pw, double fraction) {
        double fraction2 = fraction * 100.0d;
        if (fraction2 < 1.0d) {
            pw.print(String.format("%.2f", Double.valueOf(fraction2)));
        } else if (fraction2 < 10.0d) {
            pw.print(String.format("%.1f", Double.valueOf(fraction2)));
        } else {
            pw.print(String.format("%.0f", Double.valueOf(fraction2)));
        }
        pw.print("%");
    }

    public static void printProcStateTag(PrintWriter pw, int state) {
        printArrayEntry(pw, STATE_TAGS, printArrayEntry(pw, ADJ_MEM_TAGS, printArrayEntry(pw, ADJ_SCREEN_TAGS, state, 64), 16), 1);
    }

    public static void printProcStateTagProto(ProtoOutputStream proto, long screenId, long memId, long stateId, int state) {
        printProto(proto, stateId, STATE_PROTO_ENUMS, printProto(proto, memId, ADJ_MEM_PROTO_ENUMS, printProto(proto, screenId, ADJ_SCREEN_PROTO_ENUMS, state, 64), 16), 1);
    }

    public static void printAdjTag(PrintWriter pw, int state) {
        printArrayEntry(pw, ADJ_MEM_TAGS, printArrayEntry(pw, ADJ_SCREEN_TAGS, state, 4), 1);
    }

    public static void printProcStateAdjTagProto(ProtoOutputStream proto, long screenId, long memId, int state) {
        printProto(proto, memId, ADJ_MEM_PROTO_ENUMS, printProto(proto, screenId, ADJ_SCREEN_PROTO_ENUMS, state, 64), 16);
    }

    public static void printProcStateDurationProto(ProtoOutputStream proto, long fieldId, int procState, long duration) {
        long stateToken = proto.start(fieldId);
        printProto(proto, 1159641169923L, STATE_PROTO_ENUMS, procState, 1);
        proto.write(1112396529668L, duration);
        proto.end(stateToken);
    }

    public static void printProcStateTagAndValue(PrintWriter pw, int state, long value) {
        pw.print(',');
        printProcStateTag(pw, state);
        pw.print(ShortcutConstants.SERVICES_SEPARATOR);
        pw.print(value);
    }

    public static void printAdjTagAndValue(PrintWriter pw, int state, long value) {
        pw.print(',');
        printAdjTag(pw, state);
        pw.print(ShortcutConstants.SERVICES_SEPARATOR);
        pw.print(value);
    }

    public static long dumpSingleTime(PrintWriter pw, String prefix, long[] durations, int curState, long curStartTime, long now) {
        int i;
        long totalTime = 0;
        int printedScreen = -1;
        for (int iscreen = 0; iscreen < 8; iscreen += 4) {
            int printedMem = -1;
            int imem = 0;
            while (imem < 4) {
                int state = imem + iscreen;
                long time = durations[state];
                String running = "";
                if (curState == state) {
                    time += now - curStartTime;
                    if (pw != null) {
                        running = " (running)";
                    }
                }
                if (time != 0) {
                    if (pw != null) {
                        pw.print(prefix);
                        if (printedScreen == iscreen) {
                            i = -1;
                        } else {
                            i = iscreen;
                        }
                        printScreenLabel(pw, i);
                        printedScreen = iscreen;
                        printMemLabel(pw, printedMem != imem ? imem : -1, (char) 0);
                        printedMem = imem;
                        pw.print(": ");
                        TimeUtils.formatDuration(time, pw);
                        pw.println(running);
                    }
                    totalTime += time;
                }
                imem++;
            }
        }
        int iscreen2 = (totalTime > 0L ? 1 : (totalTime == 0L ? 0 : -1));
        if (iscreen2 != 0 && pw != null) {
            pw.print(prefix);
            pw.print("    TOTAL: ");
            TimeUtils.formatDuration(totalTime, pw);
            pw.println();
        }
        return totalTime;
    }

    public static void dumpAdjTimesCheckin(PrintWriter pw, String sep, long[] durations, int curState, long curStartTime, long now) {
        for (int iscreen = 0; iscreen < 8; iscreen += 4) {
            for (int imem = 0; imem < 4; imem++) {
                int state = imem + iscreen;
                long time = durations[state];
                if (curState == state) {
                    time += now - curStartTime;
                }
                if (time != 0) {
                    printAdjTagAndValue(pw, state, time);
                }
            }
        }
    }

    private static void dumpStateHeadersCsv(PrintWriter pw, String sep, int[] screenStates, int[] memStates, int[] procStates) {
        int NS = screenStates != null ? screenStates.length : 1;
        int NM = memStates != null ? memStates.length : 1;
        int NP = procStates != null ? procStates.length : 1;
        for (int is = 0; is < NS; is++) {
            for (int im = 0; im < NM; im++) {
                for (int ip = 0; ip < NP; ip++) {
                    pw.print(sep);
                    boolean printed = false;
                    if (screenStates != null && screenStates.length > 1) {
                        printScreenLabelCsv(pw, screenStates[is]);
                        printed = true;
                    }
                    if (memStates != null && memStates.length > 1) {
                        if (printed) {
                            pw.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                        }
                        printMemLabelCsv(pw, memStates[im]);
                        printed = true;
                    }
                    if (procStates != null && procStates.length > 1) {
                        if (printed) {
                            pw.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                        }
                        pw.print(STATE_NAMES_CSV[procStates[ip]]);
                    }
                }
            }
        }
    }

    public static void dumpProcessSummaryLocked(PrintWriter pw, String prefix, String header, ArrayList<ProcessState> procs, int[] screenStates, int[] memStates, int[] procStates, long now, long totalTime) {
        for (int i = procs.size() - 1; i >= 0; i--) {
            ProcessState proc = procs.get(i);
            proc.dumpSummary(pw, prefix, header, screenStates, memStates, procStates, now, totalTime);
        }
    }

    public static void dumpProcessListCsv(PrintWriter pw, ArrayList<ProcessState> procs, boolean sepScreenStates, int[] screenStates, boolean sepMemStates, int[] memStates, boolean sepProcStates, int[] procStates, long now) {
        pw.print("process");
        pw.print(CSV_SEP);
        pw.print("uid");
        pw.print(CSV_SEP);
        pw.print("vers");
        dumpStateHeadersCsv(pw, CSV_SEP, sepScreenStates ? screenStates : null, sepMemStates ? memStates : null, sepProcStates ? procStates : null);
        pw.println();
        for (int i = procs.size() - 1; i >= 0; i--) {
            ProcessState proc = procs.get(i);
            pw.print(proc.getName());
            pw.print(CSV_SEP);
            UserHandle.formatUid(pw, proc.getUid());
            pw.print(CSV_SEP);
            pw.print(proc.getVersion());
            proc.dumpCsv(pw, sepScreenStates, screenStates, sepMemStates, memStates, sepProcStates, procStates, now);
            pw.println();
        }
    }

    public static int printArrayEntry(PrintWriter pw, String[] array, int value, int mod) {
        int index = value / mod;
        if (index >= 0 && index < array.length) {
            pw.print(array[index]);
        } else {
            pw.print('?');
        }
        return value - (index * mod);
    }

    public static int printProto(ProtoOutputStream proto, long fieldId, int[] enums, int value, int mod) {
        int index = value / mod;
        if (index >= 0 && index < enums.length) {
            proto.write(fieldId, enums[index]);
        }
        return value - (index * mod);
    }

    public static String collapseString(String pkgName, String itemName) {
        if (itemName.startsWith(pkgName)) {
            int ITEMLEN = itemName.length();
            int PKGLEN = pkgName.length();
            if (ITEMLEN == PKGLEN) {
                return "";
            }
            if (ITEMLEN >= PKGLEN && itemName.charAt(PKGLEN) == '.') {
                return itemName.substring(PKGLEN);
            }
        }
        return itemName;
    }

    public static int aggregateCurrentProcessState(int curState) {
        int procStateIndex;
        int screenStateIndex = curState / 64;
        int procStateIndex2 = curState % 16;
        try {
            procStateIndex = PROCESS_STATS_STATE_TO_AGGREGATED_STATE[procStateIndex2];
        } catch (IndexOutOfBoundsException e) {
            procStateIndex = 0;
        }
        return (procStateIndex << 15) | screenStateIndex;
    }

    public static void printAggregatedProcStateTagProto(ProtoOutputStream proto, long screenId, long stateId, int state) {
        try {
            proto.write(stateId, state >> 15);
        } catch (IndexOutOfBoundsException e) {
            proto.write(stateId, 0);
        }
        try {
            proto.write(screenId, ADJ_SCREEN_PROTO_ENUMS[state & 15]);
        } catch (IndexOutOfBoundsException e2) {
            proto.write(screenId, 0);
        }
    }
}
