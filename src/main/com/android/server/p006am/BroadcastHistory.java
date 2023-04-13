package com.android.server.p006am;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.jobs.XmlUtils;
import dalvik.annotation.optimization.NeverCompile;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/* renamed from: com.android.server.am.BroadcastHistory */
/* loaded from: classes.dex */
public class BroadcastHistory {
    public final int MAX_BROADCAST_HISTORY;
    public final int MAX_BROADCAST_SUMMARY_HISTORY;
    public final BroadcastRecord[] mBroadcastHistory;
    public final Intent[] mBroadcastSummaryHistory;
    public final long[] mSummaryHistoryDispatchTime;
    public final long[] mSummaryHistoryEnqueueTime;
    public final long[] mSummaryHistoryFinishTime;
    public final ArrayList<BroadcastRecord> mPendingBroadcasts = new ArrayList<>();
    public int mHistoryNext = 0;
    public int mSummaryHistoryNext = 0;

    public final int ringAdvance(int i, int i2, int i3) {
        int i4 = i + i2;
        if (i4 < 0) {
            return i3 - 1;
        }
        if (i4 >= i3) {
            return 0;
        }
        return i4;
    }

    public BroadcastHistory(BroadcastConstants broadcastConstants) {
        int i = broadcastConstants.MAX_HISTORY_COMPLETE_SIZE;
        this.MAX_BROADCAST_HISTORY = i;
        int i2 = broadcastConstants.MAX_HISTORY_SUMMARY_SIZE;
        this.MAX_BROADCAST_SUMMARY_HISTORY = i2;
        this.mBroadcastHistory = new BroadcastRecord[i];
        this.mBroadcastSummaryHistory = new Intent[i2];
        this.mSummaryHistoryEnqueueTime = new long[i2];
        this.mSummaryHistoryDispatchTime = new long[i2];
        this.mSummaryHistoryFinishTime = new long[i2];
    }

    public void onBroadcastEnqueuedLocked(BroadcastRecord broadcastRecord) {
        this.mPendingBroadcasts.add(broadcastRecord);
    }

    public void onBroadcastFinishedLocked(BroadcastRecord broadcastRecord) {
        this.mPendingBroadcasts.remove(broadcastRecord);
        addBroadcastToHistoryLocked(broadcastRecord);
    }

    public void addBroadcastToHistoryLocked(BroadcastRecord broadcastRecord) {
        BroadcastRecord maybeStripForHistory = broadcastRecord.maybeStripForHistory();
        BroadcastRecord[] broadcastRecordArr = this.mBroadcastHistory;
        int i = this.mHistoryNext;
        broadcastRecordArr[i] = maybeStripForHistory;
        this.mHistoryNext = ringAdvance(i, 1, this.MAX_BROADCAST_HISTORY);
        Intent[] intentArr = this.mBroadcastSummaryHistory;
        int i2 = this.mSummaryHistoryNext;
        intentArr[i2] = maybeStripForHistory.intent;
        this.mSummaryHistoryEnqueueTime[i2] = maybeStripForHistory.enqueueClockTime;
        this.mSummaryHistoryDispatchTime[i2] = maybeStripForHistory.dispatchClockTime;
        this.mSummaryHistoryFinishTime[i2] = System.currentTimeMillis();
        this.mSummaryHistoryNext = ringAdvance(this.mSummaryHistoryNext, 1, this.MAX_BROADCAST_SUMMARY_HISTORY);
    }

    @NeverCompile
    public void dumpDebug(ProtoOutputStream protoOutputStream) {
        for (int i = 0; i < this.mPendingBroadcasts.size(); i++) {
            this.mPendingBroadcasts.get(i).dumpDebug(protoOutputStream, 2246267895815L);
        }
        int i2 = this.mHistoryNext;
        int i3 = i2;
        do {
            i3 = ringAdvance(i3, -1, this.MAX_BROADCAST_HISTORY);
            BroadcastRecord broadcastRecord = this.mBroadcastHistory[i3];
            if (broadcastRecord != null) {
                broadcastRecord.dumpDebug(protoOutputStream, 2246267895813L);
                continue;
            }
        } while (i3 != i2);
        int i4 = this.mSummaryHistoryNext;
        int i5 = i4;
        do {
            i5 = ringAdvance(i5, -1, this.MAX_BROADCAST_SUMMARY_HISTORY);
            Intent intent = this.mBroadcastSummaryHistory[i5];
            if (intent != null) {
                long start = protoOutputStream.start(2246267895814L);
                intent.dumpDebug(protoOutputStream, 1146756268033L, false, true, true, false);
                protoOutputStream.write(1112396529666L, this.mSummaryHistoryEnqueueTime[i5]);
                protoOutputStream.write(1112396529667L, this.mSummaryHistoryDispatchTime[i5]);
                protoOutputStream.write(1112396529668L, this.mSummaryHistoryFinishTime[i5]);
                protoOutputStream.end(start);
                continue;
            }
        } while (i5 != i4);
    }

    @NeverCompile
    public boolean dumpLocked(PrintWriter printWriter, String str, String str2, SimpleDateFormat simpleDateFormat, boolean z, boolean z2) {
        String str3;
        boolean z3;
        String str4;
        int i;
        int i2;
        String str5;
        String str6;
        String str7;
        BroadcastHistory broadcastHistory = this;
        String str8 = str2;
        printWriter.println("  Pending broadcasts:");
        String str9 = "    ";
        if (broadcastHistory.mPendingBroadcasts.isEmpty()) {
            printWriter.println("    <empty>");
        } else {
            for (int size = broadcastHistory.mPendingBroadcasts.size() - 1; size >= 0; size--) {
                printWriter.print("  Broadcast #");
                printWriter.print(size);
                printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                broadcastHistory.mPendingBroadcasts.get(size).dump(printWriter, "    ", simpleDateFormat);
            }
        }
        int i3 = broadcastHistory.mHistoryNext;
        int i4 = -1;
        boolean z4 = z2;
        int i5 = i3;
        int i6 = -1;
        boolean z5 = false;
        while (true) {
            i5 = broadcastHistory.ringAdvance(i5, i4, broadcastHistory.MAX_BROADCAST_HISTORY);
            BroadcastRecord broadcastRecord = broadcastHistory.mBroadcastHistory[i5];
            str3 = "]:";
            if (broadcastRecord == null) {
                str4 = "  #";
                i = i3;
            } else {
                i6++;
                int i7 = i3;
                if (str == null || str.equals(broadcastRecord.callerPackage)) {
                    if (!z5) {
                        if (z4) {
                            printWriter.println();
                        }
                        printWriter.println("  Historical broadcasts [" + str8 + "]:");
                        z4 = true;
                        z5 = true;
                    }
                    if (z) {
                        StringBuilder sb = new StringBuilder();
                        z3 = z4;
                        sb.append("  Historical Broadcast ");
                        sb.append(str8);
                        sb.append(" #");
                        printWriter.print(sb.toString());
                        printWriter.print(i6);
                        printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                        broadcastRecord.dump(printWriter, "    ", simpleDateFormat);
                        str4 = "  #";
                    } else {
                        z3 = z4;
                        printWriter.print("  #");
                        printWriter.print(i6);
                        printWriter.print(": ");
                        printWriter.println(broadcastRecord);
                        printWriter.print("    ");
                        str4 = "  #";
                        printWriter.println(broadcastRecord.intent.toShortString(false, true, true, false));
                        ComponentName componentName = broadcastRecord.targetComp;
                        if (componentName != null && componentName != broadcastRecord.intent.getComponent()) {
                            printWriter.print("    targetComp: ");
                            printWriter.println(broadcastRecord.targetComp.toShortString());
                        }
                        Bundle extras = broadcastRecord.intent.getExtras();
                        if (extras != null) {
                            printWriter.print("    extras: ");
                            printWriter.println(extras.toString());
                        }
                    }
                    z4 = z3;
                } else {
                    str4 = "  #";
                }
                i = i7;
            }
            if (i5 == i) {
                break;
            }
            i4 = -1;
            str8 = str2;
            i3 = i;
            broadcastHistory = this;
        }
        if (str == null) {
            String str10 = str4;
            int i8 = this.mSummaryHistoryNext;
            if (z) {
                i2 = i8;
                i6 = -1;
                z5 = false;
            } else {
                i2 = i8;
                int i9 = i6;
                while (i9 > 0 && i2 != i8) {
                    boolean z6 = z4;
                    i2 = ringAdvance(i2, -1, this.MAX_BROADCAST_SUMMARY_HISTORY);
                    if (this.mBroadcastHistory[i2] != null) {
                        i9--;
                    }
                    z4 = z6;
                }
                z4 = z4;
            }
            while (true) {
                i2 = ringAdvance(i2, -1, this.MAX_BROADCAST_SUMMARY_HISTORY);
                Intent intent = this.mBroadcastSummaryHistory[i2];
                if (intent != null) {
                    if (!z5) {
                        if (z4) {
                            printWriter.println();
                        }
                        printWriter.println("  Historical broadcasts summary [" + str8 + str3);
                        z4 = true;
                        z5 = true;
                    }
                    if (!z && i6 >= 50) {
                        printWriter.println("  ...");
                        break;
                    }
                    i6++;
                    printWriter.print(str10);
                    printWriter.print(i6);
                    printWriter.print(": ");
                    str5 = str10;
                    printWriter.println(intent.toShortString(false, true, true, false));
                    printWriter.print(str9);
                    str6 = str9;
                    str7 = str3;
                    TimeUtils.formatDuration(this.mSummaryHistoryDispatchTime[i2] - this.mSummaryHistoryEnqueueTime[i2], printWriter);
                    printWriter.print(" dispatch ");
                    TimeUtils.formatDuration(this.mSummaryHistoryFinishTime[i2] - this.mSummaryHistoryDispatchTime[i2], printWriter);
                    printWriter.println(" finish");
                    printWriter.print("    enq=");
                    printWriter.print(simpleDateFormat.format(new Date(this.mSummaryHistoryEnqueueTime[i2])));
                    printWriter.print(" disp=");
                    printWriter.print(simpleDateFormat.format(new Date(this.mSummaryHistoryDispatchTime[i2])));
                    printWriter.print(" fin=");
                    printWriter.println(simpleDateFormat.format(new Date(this.mSummaryHistoryFinishTime[i2])));
                    Bundle extras2 = intent.getExtras();
                    if (extras2 != null) {
                        printWriter.print("    extras: ");
                        printWriter.println(extras2.toString());
                    }
                } else {
                    str5 = str10;
                    str6 = str9;
                    str7 = str3;
                }
                if (i2 == i8) {
                    break;
                }
                str9 = str6;
                str3 = str7;
                str10 = str5;
                str8 = str2;
            }
        }
        return z4;
    }
}
