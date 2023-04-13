package com.android.server.p006am;

import android.content.Intent;
import android.os.IBinder;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.proto.ProtoOutputStream;
import java.io.PrintWriter;
/* renamed from: com.android.server.am.IntentBindRecord */
/* loaded from: classes.dex */
public final class IntentBindRecord {
    public final ArrayMap<ProcessRecord, AppBindRecord> apps = new ArrayMap<>();
    public IBinder binder;
    public boolean doRebind;
    public boolean hasBound;
    public final Intent.FilterComparison intent;
    public boolean received;
    public boolean requested;
    public final ServiceRecord service;
    public String stringName;

    public void dumpInService(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print("intent={");
        printWriter.print(this.intent.getIntent().toShortString(false, true, false, false));
        printWriter.println('}');
        printWriter.print(str);
        printWriter.print("binder=");
        printWriter.println(this.binder);
        printWriter.print(str);
        printWriter.print("requested=");
        printWriter.print(this.requested);
        printWriter.print(" received=");
        printWriter.print(this.received);
        printWriter.print(" hasBound=");
        printWriter.print(this.hasBound);
        printWriter.print(" doRebind=");
        printWriter.println(this.doRebind);
        for (int i = 0; i < this.apps.size(); i++) {
            AppBindRecord valueAt = this.apps.valueAt(i);
            printWriter.print(str);
            printWriter.print("* Client AppBindRecord{");
            printWriter.print(Integer.toHexString(System.identityHashCode(valueAt)));
            printWriter.print(' ');
            printWriter.print(valueAt.client);
            printWriter.println('}');
            valueAt.dumpInIntentBind(printWriter, str + "  ");
        }
    }

    public IntentBindRecord(ServiceRecord serviceRecord, Intent.FilterComparison filterComparison) {
        this.service = serviceRecord;
        this.intent = filterComparison;
    }

    public long collectFlags() {
        long j = 0;
        for (int size = this.apps.size() - 1; size >= 0; size--) {
            ArraySet<ConnectionRecord> arraySet = this.apps.valueAt(size).connections;
            for (int size2 = arraySet.size() - 1; size2 >= 0; size2--) {
                j |= arraySet.valueAt(size2).getFlags();
            }
        }
        return j;
    }

    public String toString() {
        String str = this.stringName;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("IntentBindRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        if ((collectFlags() & 1) != 0) {
            sb.append("CR ");
        }
        sb.append(this.service.shortInstanceName);
        sb.append(':');
        Intent.FilterComparison filterComparison = this.intent;
        if (filterComparison != null) {
            filterComparison.getIntent().toShortString(sb, false, false, false, false);
        }
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        Intent.FilterComparison filterComparison = this.intent;
        if (filterComparison != null) {
            filterComparison.getIntent().dumpDebug(protoOutputStream, 1146756268033L, false, true, false, false);
        }
        IBinder iBinder = this.binder;
        if (iBinder != null) {
            protoOutputStream.write(1138166333442L, iBinder.toString());
        }
        protoOutputStream.write(1133871366147L, (collectFlags() & 1) != 0);
        protoOutputStream.write(1133871366148L, this.requested);
        protoOutputStream.write(1133871366149L, this.received);
        protoOutputStream.write(1133871366150L, this.hasBound);
        protoOutputStream.write(1133871366151L, this.doRebind);
        int size = this.apps.size();
        for (int i = 0; i < size; i++) {
            AppBindRecord valueAt = this.apps.valueAt(i);
            if (valueAt != null) {
                valueAt.dumpDebug(protoOutputStream, 2246267895816L);
            }
        }
        protoOutputStream.end(start);
    }
}
