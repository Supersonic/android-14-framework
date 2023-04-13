package com.android.server.p006am;

import android.content.IntentFilter;
import android.util.Printer;
import android.util.proto.ProtoOutputStream;
import dalvik.annotation.optimization.NeverCompile;
import java.io.PrintWriter;
/* renamed from: com.android.server.am.BroadcastFilter */
/* loaded from: classes.dex */
public final class BroadcastFilter extends IntentFilter {
    public final boolean exported;
    public final String featureId;
    public final boolean instantApp;
    public final int owningUid;
    public final int owningUserId;
    public final String packageName;
    public final String receiverId;
    public final ReceiverList receiverList;
    public final String requiredPermission;
    public final boolean visibleToInstantApp;

    public BroadcastFilter(IntentFilter intentFilter, ReceiverList receiverList, String str, String str2, String str3, String str4, int i, int i2, boolean z, boolean z2, boolean z3) {
        super(intentFilter);
        this.receiverList = receiverList;
        this.packageName = str;
        this.featureId = str2;
        this.receiverId = str3;
        this.requiredPermission = str4;
        this.owningUid = i;
        this.owningUserId = i2;
        this.instantApp = z;
        this.visibleToInstantApp = z2;
        this.exported = z3;
    }

    @NeverCompile
    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        super.dumpDebug(protoOutputStream, 1146756268033L);
        String str = this.requiredPermission;
        if (str != null) {
            protoOutputStream.write(1138166333442L, str);
        }
        protoOutputStream.write(1138166333443L, Integer.toHexString(System.identityHashCode(this)));
        protoOutputStream.write(1120986464260L, this.owningUserId);
        protoOutputStream.end(start);
    }

    @NeverCompile
    public void dumpBrief(PrintWriter printWriter, String str) {
        dumpBroadcastFilterState(printWriter, str);
    }

    @NeverCompile
    public void dumpInReceiverList(PrintWriter printWriter, Printer printer, String str) {
        super.dump(printer, str);
        dumpBroadcastFilterState(printWriter, str);
    }

    @NeverCompile
    public void dumpBroadcastFilterState(PrintWriter printWriter, String str) {
        if (this.requiredPermission != null) {
            printWriter.print(str);
            printWriter.print("requiredPermission=");
            printWriter.println(this.requiredPermission);
        }
    }

    public String toString() {
        return "BroadcastFilter{" + Integer.toHexString(System.identityHashCode(this)) + ' ' + this.owningUid + "/u" + this.owningUserId + ' ' + this.receiverList + '}';
    }
}
