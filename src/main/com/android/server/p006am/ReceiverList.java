package com.android.server.p006am;

import android.content.IIntentReceiver;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.PrintWriterPrinter;
import android.util.proto.ProtoOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: com.android.server.am.ReceiverList */
/* loaded from: classes.dex */
public final class ReceiverList extends ArrayList<BroadcastFilter> implements IBinder.DeathRecipient {
    public final ProcessRecord app;
    BroadcastRecord curBroadcast = null;
    boolean linkedToDeath = false;
    final ActivityManagerService owner;
    public final int pid;
    public final IIntentReceiver receiver;
    String stringName;
    public final int uid;
    public final int userId;

    @Override // java.util.AbstractList, java.util.Collection, java.util.List
    public boolean equals(Object obj) {
        return this == obj;
    }

    public ReceiverList(ActivityManagerService activityManagerService, ProcessRecord processRecord, int i, int i2, int i3, IIntentReceiver iIntentReceiver) {
        this.owner = activityManagerService;
        this.receiver = iIntentReceiver;
        this.app = processRecord;
        this.pid = i;
        this.uid = i2;
        this.userId = i3;
    }

    @Override // java.util.AbstractList, java.util.Collection, java.util.List
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        this.linkedToDeath = false;
        this.owner.unregisterReceiver(this.receiver);
    }

    public boolean containsFilter(IntentFilter intentFilter) {
        int size = size();
        for (int i = 0; i < size; i++) {
            if (IntentFilter.filterEquals(get(i), intentFilter)) {
                return true;
            }
        }
        return false;
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        ProcessRecord processRecord = this.app;
        if (processRecord != null) {
            processRecord.dumpDebug(protoOutputStream, 1146756268033L);
            protoOutputStream.write(1120986464265L, this.app.mReceivers.numberOfReceivers());
        }
        protoOutputStream.write(1120986464258L, this.pid);
        protoOutputStream.write(1120986464259L, this.uid);
        protoOutputStream.write(1120986464260L, this.userId);
        BroadcastRecord broadcastRecord = this.curBroadcast;
        if (broadcastRecord != null) {
            broadcastRecord.dumpDebug(protoOutputStream, 1146756268037L);
        }
        protoOutputStream.write(1133871366150L, this.linkedToDeath);
        int size = size();
        for (int i = 0; i < size; i++) {
            get(i).dumpDebug(protoOutputStream, 2246267895815L);
        }
        protoOutputStream.write(1138166333448L, Integer.toHexString(System.identityHashCode(this)));
        protoOutputStream.end(start);
    }

    public void dumpLocal(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print("app=");
        ProcessRecord processRecord = this.app;
        printWriter.print(processRecord != null ? processRecord.toShortString() : null);
        printWriter.print(" pid=");
        printWriter.print(this.pid);
        printWriter.print(" uid=");
        printWriter.print(this.uid);
        printWriter.print(" user=");
        printWriter.print(this.userId);
        if (this.app != null) {
            printWriter.print(" #receivers=");
            printWriter.print(this.app.mReceivers.numberOfReceivers());
        }
        printWriter.println();
        if (this.curBroadcast != null || this.linkedToDeath) {
            printWriter.print(str);
            printWriter.print("curBroadcast=");
            printWriter.print(this.curBroadcast);
            printWriter.print(" linkedToDeath=");
            printWriter.println(this.linkedToDeath);
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        PrintWriterPrinter printWriterPrinter = new PrintWriterPrinter(printWriter);
        dumpLocal(printWriter, str);
        String str2 = str + "  ";
        int size = size();
        for (int i = 0; i < size; i++) {
            BroadcastFilter broadcastFilter = get(i);
            printWriter.print(str);
            printWriter.print("Filter #");
            printWriter.print(i);
            printWriter.print(": BroadcastFilter{");
            printWriter.print(Integer.toHexString(System.identityHashCode(broadcastFilter)));
            printWriter.println('}');
            broadcastFilter.dumpInReceiverList(printWriter, printWriterPrinter, str2);
        }
    }

    @Override // java.util.AbstractCollection
    public String toString() {
        String str = this.stringName;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("ReceiverList{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        sb.append(this.pid);
        sb.append(' ');
        ProcessRecord processRecord = this.app;
        sb.append(processRecord != null ? processRecord.processName : "(unknown name)");
        sb.append('/');
        sb.append(this.uid);
        sb.append("/u");
        sb.append(this.userId);
        sb.append(this.receiver.asBinder() instanceof Binder ? " local:" : " remote:");
        sb.append(Integer.toHexString(System.identityHashCode(this.receiver.asBinder())));
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }
}
