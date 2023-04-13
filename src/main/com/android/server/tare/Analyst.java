package com.android.server.tare;

import android.os.BatteryManagerInternal;
import android.os.RemoteException;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.server.LocalServices;
import com.android.server.p006am.BatteryStatsService;
import com.android.server.tare.Ledger;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class Analyst {
    public static final boolean DEBUG;
    @VisibleForTesting
    static final long MIN_REPORT_DURATION_FOR_RESET = 86400000;
    public static final String TAG;
    public final IBatteryStats mIBatteryStats;
    public int mPeriodIndex;
    public final Report[] mReports;

    static {
        String str = "TARE-" + Analyst.class.getSimpleName();
        TAG = str;
        DEBUG = InternalResourceService.DEBUG || Log.isLoggable(str, 3);
    }

    /* loaded from: classes2.dex */
    public static final class Report {
        public int cumulativeBatteryDischarge = 0;
        public int currentBatteryLevel = 0;
        public long cumulativeProfit = 0;
        public int numProfitableActions = 0;
        public long cumulativeLoss = 0;
        public int numUnprofitableActions = 0;
        public long cumulativeRewards = 0;
        public int numRewards = 0;
        public long cumulativePositiveRegulations = 0;
        public int numPositiveRegulations = 0;
        public long cumulativeNegativeRegulations = 0;
        public int numNegativeRegulations = 0;
        public long screenOffDurationMs = 0;
        public long screenOffDischargeMah = 0;
        public long bsScreenOffRealtimeBase = 0;
        public long bsScreenOffDischargeMahBase = 0;

        public final void clear() {
            this.cumulativeBatteryDischarge = 0;
            this.currentBatteryLevel = 0;
            this.cumulativeProfit = 0L;
            this.numProfitableActions = 0;
            this.cumulativeLoss = 0L;
            this.numUnprofitableActions = 0;
            this.cumulativeRewards = 0L;
            this.numRewards = 0;
            this.cumulativePositiveRegulations = 0L;
            this.numPositiveRegulations = 0;
            this.cumulativeNegativeRegulations = 0L;
            this.numNegativeRegulations = 0;
            this.screenOffDurationMs = 0L;
            this.screenOffDischargeMah = 0L;
            this.bsScreenOffRealtimeBase = 0L;
            this.bsScreenOffDischargeMahBase = 0L;
        }
    }

    public Analyst() {
        this(BatteryStatsService.getService());
    }

    @VisibleForTesting
    public Analyst(IBatteryStats iBatteryStats) {
        this.mPeriodIndex = 0;
        this.mReports = new Report[8];
        this.mIBatteryStats = iBatteryStats;
    }

    public List<Report> getReports() {
        ArrayList arrayList = new ArrayList(8);
        for (int i = 1; i <= 8; i++) {
            Report report = this.mReports[(this.mPeriodIndex + i) % 8];
            if (report != null) {
                arrayList.add(report);
            }
        }
        return arrayList;
    }

    public long getBatteryScreenOffDischargeMah() {
        Report[] reportArr;
        long j = 0;
        for (Report report : this.mReports) {
            if (report != null) {
                j += report.screenOffDischargeMah;
            }
        }
        return j;
    }

    public long getBatteryScreenOffDurationMs() {
        Report[] reportArr;
        long j = 0;
        for (Report report : this.mReports) {
            if (report != null) {
                j += report.screenOffDurationMs;
            }
        }
        return j;
    }

    public void loadReports(List<Report> list) {
        int size = list.size();
        this.mPeriodIndex = Math.max(0, Math.min(8, size) - 1);
        for (int i = 0; i < 8; i++) {
            if (i < size) {
                this.mReports[i] = list.get(i);
            } else {
                this.mReports[i] = null;
            }
        }
        Report report = this.mReports[this.mPeriodIndex];
        if (report != null) {
            report.bsScreenOffRealtimeBase = getLatestBatteryScreenOffRealtimeMs();
            report.bsScreenOffDischargeMahBase = getLatestScreenOffDischargeMah();
        }
    }

    public void noteBatteryLevelChange(int i) {
        Report[] reportArr = this.mReports;
        int i2 = this.mPeriodIndex;
        Report report = reportArr[i2];
        boolean z = false;
        boolean z2 = report != null && i >= 90 && report.currentBatteryLevel < i && report.cumulativeBatteryDischarge >= 25;
        boolean z3 = report != null && report.currentBatteryLevel < i && report.screenOffDurationMs >= 86400000;
        if (z2 || z3) {
            z = true;
        }
        if (z) {
            int i3 = (i2 + 1) % 8;
            this.mPeriodIndex = i3;
            Report report2 = reportArr[i3];
            if (report2 != null) {
                report2.clear();
                report2.currentBatteryLevel = i;
                report2.bsScreenOffRealtimeBase = getLatestBatteryScreenOffRealtimeMs();
                report2.bsScreenOffDischargeMahBase = getLatestScreenOffDischargeMah();
                return;
            }
        }
        Report report3 = reportArr[this.mPeriodIndex];
        if (report3 == null) {
            Report initializeReport = initializeReport();
            this.mReports[this.mPeriodIndex] = initializeReport;
            initializeReport.currentBatteryLevel = i;
            return;
        }
        int i4 = report3.currentBatteryLevel;
        if (i < i4) {
            report3.cumulativeBatteryDischarge += i4 - i;
            long latestBatteryScreenOffRealtimeMs = getLatestBatteryScreenOffRealtimeMs();
            long latestScreenOffDischargeMah = getLatestScreenOffDischargeMah();
            if (report3.bsScreenOffRealtimeBase > latestBatteryScreenOffRealtimeMs) {
                report3.bsScreenOffRealtimeBase = 0L;
                report3.bsScreenOffDischargeMahBase = 0L;
            }
            report3.screenOffDurationMs += latestBatteryScreenOffRealtimeMs - report3.bsScreenOffRealtimeBase;
            report3.screenOffDischargeMah += latestScreenOffDischargeMah - report3.bsScreenOffDischargeMahBase;
            report3.bsScreenOffRealtimeBase = latestBatteryScreenOffRealtimeMs;
            report3.bsScreenOffDischargeMahBase = latestScreenOffDischargeMah;
        }
        report3.currentBatteryLevel = i;
    }

    public void noteTransaction(Ledger.Transaction transaction) {
        Report[] reportArr = this.mReports;
        int i = this.mPeriodIndex;
        if (reportArr[i] == null) {
            reportArr[i] = initializeReport();
        }
        Report report = this.mReports[this.mPeriodIndex];
        int eventType = EconomicPolicy.getEventType(transaction.eventId);
        if (eventType == Integer.MIN_VALUE) {
            long j = transaction.delta;
            if (j != 0) {
                report.cumulativeRewards += j;
                report.numRewards++;
            }
        } else if (eventType == 0) {
            long j2 = transaction.delta;
            if (j2 > 0) {
                report.cumulativePositiveRegulations += j2;
                report.numPositiveRegulations++;
            } else if (j2 < 0) {
                report.cumulativeNegativeRegulations -= j2;
                report.numNegativeRegulations++;
            }
        } else if (eventType != 1073741824) {
        } else {
            long j3 = transaction.delta;
            long j4 = transaction.ctp;
            if ((-j3) > j4) {
                report.cumulativeProfit += (-j3) - j4;
                report.numProfitableActions++;
            } else if ((-j3) < j4) {
                report.cumulativeLoss += j4 + j3;
                report.numUnprofitableActions++;
            }
        }
    }

    public void tearDown() {
        int i = 0;
        while (true) {
            Report[] reportArr = this.mReports;
            if (i < reportArr.length) {
                reportArr[i] = null;
                i++;
            } else {
                this.mPeriodIndex = 0;
                return;
            }
        }
    }

    public final long getLatestBatteryScreenOffRealtimeMs() {
        try {
            return this.mIBatteryStats.computeBatteryScreenOffRealtimeMs();
        } catch (RemoteException unused) {
            return 0L;
        }
    }

    public final long getLatestScreenOffDischargeMah() {
        try {
            return this.mIBatteryStats.getScreenOffDischargeMah();
        } catch (RemoteException unused) {
            return 0L;
        }
    }

    public final Report initializeReport() {
        Report report = new Report();
        report.bsScreenOffRealtimeBase = getLatestBatteryScreenOffRealtimeMs();
        report.bsScreenOffDischargeMahBase = getLatestScreenOffDischargeMah();
        return report;
    }

    public final String padStringWithSpaces(String str, int i) {
        int max = Math.max(2, i - str.length()) >>> 1;
        return " ".repeat(max) + str + " ".repeat(max);
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        long batteryFullCharge = ((BatteryManagerInternal) LocalServices.getService(BatteryManagerInternal.class)).getBatteryFullCharge() / 1000;
        indentingPrintWriter.println("Reports:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.print("      Total Discharge");
        indentingPrintWriter.print(padStringWithSpaces("Profit (avg/action : avg/discharge)", 47));
        indentingPrintWriter.print(padStringWithSpaces("Loss (avg/action : avg/discharge)", 47));
        indentingPrintWriter.print(padStringWithSpaces("Rewards (avg/reward : avg/discharge)", 47));
        indentingPrintWriter.print(padStringWithSpaces("+Regs (avg/reg : avg/discharge)", 47));
        indentingPrintWriter.print(padStringWithSpaces("-Regs (avg/reg : avg/discharge)", 47));
        indentingPrintWriter.print(padStringWithSpaces("Bg drain estimate", 47));
        indentingPrintWriter.println();
        for (int i = 0; i < 8; i++) {
            Report report = this.mReports[((this.mPeriodIndex - i) + 8) % 8];
            if (report != null) {
                indentingPrintWriter.print("t-");
                indentingPrintWriter.print(i);
                indentingPrintWriter.print(":  ");
                indentingPrintWriter.print(padStringWithSpaces(Integer.toString(report.cumulativeBatteryDischarge), 15));
                if (report.numProfitableActions > 0) {
                    int i2 = report.cumulativeBatteryDischarge;
                    indentingPrintWriter.print(padStringWithSpaces(String.format("%s (%s : %s)", TareUtils.cakeToString(report.cumulativeProfit), TareUtils.cakeToString(report.cumulativeProfit / report.numProfitableActions), i2 > 0 ? TareUtils.cakeToString(report.cumulativeProfit / i2) : "N/A"), 47));
                } else {
                    indentingPrintWriter.print(padStringWithSpaces("N/A", 47));
                }
                if (report.numUnprofitableActions > 0) {
                    int i3 = report.cumulativeBatteryDischarge;
                    indentingPrintWriter.print(padStringWithSpaces(String.format("%s (%s : %s)", TareUtils.cakeToString(report.cumulativeLoss), TareUtils.cakeToString(report.cumulativeLoss / report.numUnprofitableActions), i3 > 0 ? TareUtils.cakeToString(report.cumulativeLoss / i3) : "N/A"), 47));
                } else {
                    indentingPrintWriter.print(padStringWithSpaces("N/A", 47));
                }
                if (report.numRewards > 0) {
                    int i4 = report.cumulativeBatteryDischarge;
                    indentingPrintWriter.print(padStringWithSpaces(String.format("%s (%s : %s)", TareUtils.cakeToString(report.cumulativeRewards), TareUtils.cakeToString(report.cumulativeRewards / report.numRewards), i4 > 0 ? TareUtils.cakeToString(report.cumulativeRewards / i4) : "N/A"), 47));
                } else {
                    indentingPrintWriter.print(padStringWithSpaces("N/A", 47));
                }
                if (report.numPositiveRegulations > 0) {
                    int i5 = report.cumulativeBatteryDischarge;
                    indentingPrintWriter.print(padStringWithSpaces(String.format("%s (%s : %s)", TareUtils.cakeToString(report.cumulativePositiveRegulations), TareUtils.cakeToString(report.cumulativePositiveRegulations / report.numPositiveRegulations), i5 > 0 ? TareUtils.cakeToString(report.cumulativePositiveRegulations / i5) : "N/A"), 47));
                } else {
                    indentingPrintWriter.print(padStringWithSpaces("N/A", 47));
                }
                if (report.numNegativeRegulations > 0) {
                    int i6 = report.cumulativeBatteryDischarge;
                    indentingPrintWriter.print(padStringWithSpaces(String.format("%s (%s : %s)", TareUtils.cakeToString(report.cumulativeNegativeRegulations), TareUtils.cakeToString(report.cumulativeNegativeRegulations / report.numNegativeRegulations), i6 > 0 ? TareUtils.cakeToString(report.cumulativeNegativeRegulations / i6) : "N/A"), 47));
                } else {
                    indentingPrintWriter.print(padStringWithSpaces("N/A", 47));
                }
                if (report.screenOffDurationMs > 0) {
                    indentingPrintWriter.print(padStringWithSpaces(String.format("%d mAh (%.2f%%/hr)", Long.valueOf(report.screenOffDischargeMah), Double.valueOf(((report.screenOffDischargeMah * 100.0d) * 3600000.0d) / (report.screenOffDurationMs * batteryFullCharge))), 47));
                } else {
                    indentingPrintWriter.print(padStringWithSpaces("N/A", 47));
                }
                indentingPrintWriter.println();
            }
        }
        indentingPrintWriter.decreaseIndent();
    }
}
