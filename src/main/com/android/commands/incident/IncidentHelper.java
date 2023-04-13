package com.android.commands.incident;

import android.util.Log;
import com.android.commands.incident.sections.PersistLogSection;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class IncidentHelper {
    private static final String TAG = "IncidentHelper";
    private static boolean sLog = false;
    private final List<String> mArgs;
    private ListIterator<String> mArgsIterator;

    private IncidentHelper(String[] args) {
        List<String> unmodifiableList = Collections.unmodifiableList(Arrays.asList(args));
        this.mArgs = unmodifiableList;
        this.mArgsIterator = unmodifiableList.listIterator();
    }

    private static void showUsage(PrintStream out) {
        out.println("This command is not designed to be run manually.");
        out.println("Usage:");
        out.println("  run [sectionName]");
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x007c, code lost:
        r0.run(java.lang.System.in, java.lang.System.out, r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0083, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void run(String[] args) throws ExecutionException {
        Section section = null;
        final List<String> sectionArgs = new ArrayList<>();
        while (true) {
            if (!this.mArgsIterator.hasNext()) {
                break;
            }
            String arg = this.mArgsIterator.next();
            if ("-l".equals(arg)) {
                sLog = true;
                Log.i(TAG, "Args: [" + String.join(",", args) + "]");
            } else if ("run".equals(arg)) {
                section = getSection(nextArgRequired());
                ListIterator<String> listIterator = this.mArgsIterator;
                Objects.requireNonNull(sectionArgs);
                listIterator.forEachRemaining(new Consumer() { // from class: com.android.commands.incident.IncidentHelper$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        sectionArgs.add((String) obj);
                    }
                });
            } else {
                log(5, TAG, "Error: Unknown argument: " + arg);
                return;
            }
        }
    }

    private static Section getSection(String name) throws IllegalArgumentException {
        if ("persisted_logs".equals(name)) {
            return new PersistLogSection();
        }
        throw new IllegalArgumentException("Section not found: " + name);
    }

    private String nextArgRequired() {
        if (!this.mArgsIterator.hasNext()) {
            throw new IllegalArgumentException("Arg required after \"" + this.mArgs.get(this.mArgsIterator.previousIndex()) + "\"");
        }
        return this.mArgsIterator.next();
    }

    public static void log(int priority, String tag, String msg) {
        System.err.println(tag + ": " + msg);
        if (sLog) {
            Log.println(priority, tag, msg);
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            showUsage(System.err);
            System.exit(0);
        }
        IncidentHelper incidentHelper = new IncidentHelper(args);
        try {
            incidentHelper.run(args);
        } catch (IllegalArgumentException e) {
            showUsage(System.err);
            System.err.println();
            e.printStackTrace(System.err);
            if (sLog) {
                Log.e(TAG, "Error: ", e);
            }
        } catch (Exception e2) {
            e2.printStackTrace(System.err);
            if (sLog) {
                Log.e(TAG, "Error: ", e2);
            }
            System.exit(1);
        }
    }
}
