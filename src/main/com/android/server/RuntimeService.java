package com.android.server;

import android.content.Context;
import android.os.Binder;
import android.util.proto.ProtoOutputStream;
import com.android.i18n.timezone.DebugInfo;
import com.android.i18n.timezone.I18nModuleDebug;
import com.android.internal.util.DumpUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class RuntimeService extends Binder {
    public final Context mContext;

    public RuntimeService(Context context) {
        this.mContext = context;
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        ProtoOutputStream protoOutputStream;
        if (DumpUtils.checkDumpAndUsageStatsPermission(this.mContext, "RuntimeService", printWriter)) {
            boolean hasOption = hasOption(strArr, "--proto");
            DebugInfo debugInfo = I18nModuleDebug.getDebugInfo();
            if (hasOption) {
                protoOutputStream = new ProtoOutputStream(fileDescriptor);
                reportTimeZoneInfoProto(debugInfo, protoOutputStream);
            } else {
                reportTimeZoneInfo(debugInfo, printWriter);
                protoOutputStream = null;
            }
            if (hasOption) {
                protoOutputStream.flush();
            }
        }
    }

    public static boolean hasOption(String[] strArr, String str) {
        for (String str2 : strArr) {
            if (str.equals(str2)) {
                return true;
            }
        }
        return false;
    }

    public static void reportTimeZoneInfo(DebugInfo debugInfo, PrintWriter printWriter) {
        printWriter.println("Core Library Debug Info: ");
        for (DebugInfo.DebugEntry debugEntry : debugInfo.getDebugEntries()) {
            printWriter.print(debugEntry.getKey());
            printWriter.print(": \"");
            printWriter.print(debugEntry.getStringValue());
            printWriter.println("\"");
        }
    }

    public static void reportTimeZoneInfoProto(DebugInfo debugInfo, ProtoOutputStream protoOutputStream) {
        for (DebugInfo.DebugEntry debugEntry : debugInfo.getDebugEntries()) {
            long start = protoOutputStream.start(2246267895809L);
            protoOutputStream.write(1138166333441L, debugEntry.getKey());
            protoOutputStream.write(1138166333442L, debugEntry.getStringValue());
            protoOutputStream.end(start);
        }
    }
}
