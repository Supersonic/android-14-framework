package com.android.server.p006am;

import android.util.ArraySet;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.jobs.XmlUtils;
import java.io.PrintWriter;
/* renamed from: com.android.server.am.AppBindRecord */
/* loaded from: classes.dex */
public final class AppBindRecord {
    public final ProcessRecord attributedClient;
    public final ProcessRecord client;
    public final ArraySet<ConnectionRecord> connections = new ArraySet<>();
    public final IntentBindRecord intent;
    public final ServiceRecord service;

    public void dumpInIntentBind(PrintWriter printWriter, String str) {
        int size = this.connections.size();
        if (size > 0) {
            printWriter.println(str + "Per-process Connections:");
            for (int i = 0; i < size; i++) {
                printWriter.println(str + "  " + this.connections.valueAt(i));
            }
        }
    }

    public AppBindRecord(ServiceRecord serviceRecord, IntentBindRecord intentBindRecord, ProcessRecord processRecord, ProcessRecord processRecord2) {
        this.service = serviceRecord;
        this.intent = intentBindRecord;
        this.client = processRecord;
        this.attributedClient = processRecord2;
    }

    public String toString() {
        return "AppBindRecord{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.service.shortInstanceName + XmlUtils.STRING_ARRAY_SEPARATOR + this.client.processName + "}";
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1138166333441L, this.service.shortInstanceName);
        protoOutputStream.write(1138166333442L, this.client.processName);
        int size = this.connections.size();
        for (int i = 0; i < size; i++) {
            protoOutputStream.write(2237677961219L, Integer.toHexString(System.identityHashCode(this.connections.valueAt(i))));
        }
        protoOutputStream.end(start);
    }
}
