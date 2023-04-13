package com.android.server.location.gnss;

import android.app.StatsManager;
import android.content.Context;
import android.location.GnssStatus;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.connectivity.GpsBatteryStats;
import android.util.Base64;
import android.util.Log;
import android.util.StatsEvent;
import android.util.TimeUtils;
import com.android.internal.app.IBatteryStats;
import com.android.internal.location.nano.GnssLogsProto;
import com.android.internal.util.ConcurrentUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.location.gnss.hal.GnssNative;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public class GnssMetrics {
    public boolean[] mConstellationTypes;
    public final GnssNative mGnssNative;
    public GnssPowerMetrics mGnssPowerMetrics;
    public long mL5SvStatusReports;
    public long mL5SvStatusReportsUsedInFix;
    public Statistics mL5TopFourAverageCn0DbmHzReportsStatistics;
    public Statistics mLocationFailureReportsStatistics;
    public long mLogStartInElapsedRealtimeMs;
    public int mNumL5SvStatus;
    public int mNumL5SvStatusUsedInFix;
    public int mNumSvStatus;
    public int mNumSvStatusUsedInFix;
    public Statistics mPositionAccuracyMetersReportsStatistics;
    public final StatsManager mStatsManager;
    public long mSvStatusReports;
    public long mSvStatusReportsUsedInFix;
    public Statistics mTimeToFirstFixMilliSReportsStatistics;
    public Statistics mTopFourAverageCn0DbmHzReportsStatistics;
    public final Statistics mLocationFailureStatistics = new Statistics();
    public final Statistics mTimeToFirstFixSecStatistics = new Statistics();
    public final Statistics mPositionAccuracyMeterStatistics = new Statistics();
    public final Statistics mTopFourAverageCn0Statistics = new Statistics();
    public final Statistics mTopFourAverageCn0StatisticsL5 = new Statistics();

    public static boolean isL5Sv(float f) {
        double d = f;
        return d >= 1.164E9d && d <= 1.189E9d;
    }

    public GnssMetrics(Context context, IBatteryStats iBatteryStats, GnssNative gnssNative) {
        this.mGnssNative = gnssNative;
        this.mGnssPowerMetrics = new GnssPowerMetrics(iBatteryStats);
        reset();
        this.mLocationFailureReportsStatistics = new Statistics();
        this.mTimeToFirstFixMilliSReportsStatistics = new Statistics();
        this.mPositionAccuracyMetersReportsStatistics = new Statistics();
        this.mTopFourAverageCn0DbmHzReportsStatistics = new Statistics();
        this.mL5TopFourAverageCn0DbmHzReportsStatistics = new Statistics();
        this.mStatsManager = (StatsManager) context.getSystemService("stats");
        registerGnssStats();
    }

    public void logReceivedLocationStatus(boolean z) {
        if (!z) {
            this.mLocationFailureStatistics.addItem(1.0d);
            this.mLocationFailureReportsStatistics.addItem(1.0d);
            return;
        }
        this.mLocationFailureStatistics.addItem(0.0d);
        this.mLocationFailureReportsStatistics.addItem(0.0d);
    }

    public void logMissedReports(int i, int i2) {
        int max = (i2 / Math.max(1000, i)) - 1;
        if (max > 0) {
            for (int i3 = 0; i3 < max; i3++) {
                this.mLocationFailureStatistics.addItem(1.0d);
                this.mLocationFailureReportsStatistics.addItem(1.0d);
            }
        }
    }

    public void logTimeToFirstFixMilliSecs(int i) {
        double d = i;
        this.mTimeToFirstFixSecStatistics.addItem(d / 1000.0d);
        this.mTimeToFirstFixMilliSReportsStatistics.addItem(d);
    }

    public void logPositionAccuracyMeters(float f) {
        double d = f;
        this.mPositionAccuracyMeterStatistics.addItem(d);
        this.mPositionAccuracyMetersReportsStatistics.addItem(d);
    }

    public void logCn0(GnssStatus gnssStatus) {
        logCn0L5(gnssStatus);
        if (gnssStatus.getSatelliteCount() == 0) {
            this.mGnssPowerMetrics.reportSignalQuality(null);
            return;
        }
        int satelliteCount = gnssStatus.getSatelliteCount();
        float[] fArr = new float[satelliteCount];
        for (int i = 0; i < gnssStatus.getSatelliteCount(); i++) {
            fArr[i] = gnssStatus.getCn0DbHz(i);
        }
        Arrays.sort(fArr);
        this.mGnssPowerMetrics.reportSignalQuality(fArr);
        if (satelliteCount < 4) {
            return;
        }
        int i2 = satelliteCount - 4;
        double d = 0.0d;
        if (fArr[i2] > 0.0d) {
            while (i2 < satelliteCount) {
                d += fArr[i2];
                i2++;
            }
            double d2 = d / 4.0d;
            this.mTopFourAverageCn0Statistics.addItem(d2);
            this.mTopFourAverageCn0DbmHzReportsStatistics.addItem(d2 * 1000.0d);
        }
    }

    public void logSvStatus(GnssStatus gnssStatus) {
        for (int i = 0; i < gnssStatus.getSatelliteCount(); i++) {
            if (gnssStatus.hasCarrierFrequencyHz(i)) {
                this.mNumSvStatus++;
                this.mSvStatusReports++;
                boolean isL5Sv = isL5Sv(gnssStatus.getCarrierFrequencyHz(i));
                if (isL5Sv) {
                    this.mNumL5SvStatus++;
                    this.mL5SvStatusReports++;
                }
                if (gnssStatus.usedInFix(i)) {
                    this.mNumSvStatusUsedInFix++;
                    this.mSvStatusReportsUsedInFix++;
                    if (isL5Sv) {
                        this.mNumL5SvStatusUsedInFix++;
                        this.mL5SvStatusReportsUsedInFix++;
                    }
                }
            }
        }
    }

    public final void logCn0L5(GnssStatus gnssStatus) {
        if (gnssStatus.getSatelliteCount() == 0) {
            return;
        }
        ArrayList arrayList = new ArrayList(gnssStatus.getSatelliteCount());
        for (int i = 0; i < gnssStatus.getSatelliteCount(); i++) {
            if (isL5Sv(gnssStatus.getCarrierFrequencyHz(i))) {
                arrayList.add(Float.valueOf(gnssStatus.getCn0DbHz(i)));
            }
        }
        if (arrayList.size() < 4) {
            return;
        }
        Collections.sort(arrayList);
        double d = 0.0d;
        if (((Float) arrayList.get(arrayList.size() - 4)).floatValue() > 0.0d) {
            for (int size = arrayList.size() - 4; size < arrayList.size(); size++) {
                d += ((Float) arrayList.get(size)).floatValue();
            }
            double d2 = d / 4.0d;
            this.mTopFourAverageCn0StatisticsL5.addItem(d2);
            this.mL5TopFourAverageCn0DbmHzReportsStatistics.addItem(d2 * 1000.0d);
        }
    }

    public void logConstellationType(int i) {
        boolean[] zArr = this.mConstellationTypes;
        if (i >= zArr.length) {
            Log.e("GnssMetrics", "Constellation type " + i + " is not valid.");
            return;
        }
        zArr[i] = true;
    }

    public String dumpGnssMetricsAsProtoString() {
        GnssLogsProto.GnssLog gnssLog = new GnssLogsProto.GnssLog();
        if (this.mLocationFailureStatistics.getCount() > 0) {
            gnssLog.numLocationReportProcessed = this.mLocationFailureStatistics.getCount();
            gnssLog.percentageLocationFailure = (int) (this.mLocationFailureStatistics.getMean() * 100.0d);
        }
        if (this.mTimeToFirstFixSecStatistics.getCount() > 0) {
            gnssLog.numTimeToFirstFixProcessed = this.mTimeToFirstFixSecStatistics.getCount();
            gnssLog.meanTimeToFirstFixSecs = (int) this.mTimeToFirstFixSecStatistics.getMean();
            gnssLog.standardDeviationTimeToFirstFixSecs = (int) this.mTimeToFirstFixSecStatistics.getStandardDeviation();
        }
        if (this.mPositionAccuracyMeterStatistics.getCount() > 0) {
            gnssLog.numPositionAccuracyProcessed = this.mPositionAccuracyMeterStatistics.getCount();
            gnssLog.meanPositionAccuracyMeters = (int) this.mPositionAccuracyMeterStatistics.getMean();
            gnssLog.standardDeviationPositionAccuracyMeters = (int) this.mPositionAccuracyMeterStatistics.getStandardDeviation();
        }
        if (this.mTopFourAverageCn0Statistics.getCount() > 0) {
            gnssLog.numTopFourAverageCn0Processed = this.mTopFourAverageCn0Statistics.getCount();
            gnssLog.meanTopFourAverageCn0DbHz = this.mTopFourAverageCn0Statistics.getMean();
            gnssLog.standardDeviationTopFourAverageCn0DbHz = this.mTopFourAverageCn0Statistics.getStandardDeviation();
        }
        int i = this.mNumSvStatus;
        if (i > 0) {
            gnssLog.numSvStatusProcessed = i;
        }
        int i2 = this.mNumL5SvStatus;
        if (i2 > 0) {
            gnssLog.numL5SvStatusProcessed = i2;
        }
        int i3 = this.mNumSvStatusUsedInFix;
        if (i3 > 0) {
            gnssLog.numSvStatusUsedInFix = i3;
        }
        int i4 = this.mNumL5SvStatusUsedInFix;
        if (i4 > 0) {
            gnssLog.numL5SvStatusUsedInFix = i4;
        }
        if (this.mTopFourAverageCn0StatisticsL5.getCount() > 0) {
            gnssLog.numL5TopFourAverageCn0Processed = this.mTopFourAverageCn0StatisticsL5.getCount();
            gnssLog.meanL5TopFourAverageCn0DbHz = this.mTopFourAverageCn0StatisticsL5.getMean();
            gnssLog.standardDeviationL5TopFourAverageCn0DbHz = this.mTopFourAverageCn0StatisticsL5.getStandardDeviation();
        }
        gnssLog.powerMetrics = this.mGnssPowerMetrics.buildProto();
        gnssLog.hardwareRevision = SystemProperties.get("ro.boot.revision", "");
        String encodeToString = Base64.encodeToString(GnssLogsProto.GnssLog.toByteArray(gnssLog), 0);
        reset();
        return encodeToString;
    }

    public String dumpGnssMetricsAsText() {
        StringBuilder sb = new StringBuilder();
        sb.append("GNSS_KPI_START");
        sb.append('\n');
        sb.append("  KPI logging start time: ");
        TimeUtils.formatDuration(this.mLogStartInElapsedRealtimeMs, sb);
        sb.append("\n");
        sb.append("  KPI logging end time: ");
        TimeUtils.formatDuration(SystemClock.elapsedRealtime(), sb);
        sb.append("\n");
        sb.append("  Number of location reports: ");
        sb.append(this.mLocationFailureStatistics.getCount());
        sb.append("\n");
        if (this.mLocationFailureStatistics.getCount() > 0) {
            sb.append("  Percentage location failure: ");
            sb.append(this.mLocationFailureStatistics.getMean() * 100.0d);
            sb.append("\n");
        }
        sb.append("  Number of TTFF reports: ");
        sb.append(this.mTimeToFirstFixSecStatistics.getCount());
        sb.append("\n");
        if (this.mTimeToFirstFixSecStatistics.getCount() > 0) {
            sb.append("  TTFF mean (sec): ");
            sb.append(this.mTimeToFirstFixSecStatistics.getMean());
            sb.append("\n");
            sb.append("  TTFF standard deviation (sec): ");
            sb.append(this.mTimeToFirstFixSecStatistics.getStandardDeviation());
            sb.append("\n");
        }
        sb.append("  Number of position accuracy reports: ");
        sb.append(this.mPositionAccuracyMeterStatistics.getCount());
        sb.append("\n");
        if (this.mPositionAccuracyMeterStatistics.getCount() > 0) {
            sb.append("  Position accuracy mean (m): ");
            sb.append(this.mPositionAccuracyMeterStatistics.getMean());
            sb.append("\n");
            sb.append("  Position accuracy standard deviation (m): ");
            sb.append(this.mPositionAccuracyMeterStatistics.getStandardDeviation());
            sb.append("\n");
        }
        sb.append("  Number of CN0 reports: ");
        sb.append(this.mTopFourAverageCn0Statistics.getCount());
        sb.append("\n");
        if (this.mTopFourAverageCn0Statistics.getCount() > 0) {
            sb.append("  Top 4 Avg CN0 mean (dB-Hz): ");
            sb.append(this.mTopFourAverageCn0Statistics.getMean());
            sb.append("\n");
            sb.append("  Top 4 Avg CN0 standard deviation (dB-Hz): ");
            sb.append(this.mTopFourAverageCn0Statistics.getStandardDeviation());
            sb.append("\n");
        }
        sb.append("  Total number of sv status messages processed: ");
        sb.append(this.mNumSvStatus);
        sb.append("\n");
        sb.append("  Total number of L5 sv status messages processed: ");
        sb.append(this.mNumL5SvStatus);
        sb.append("\n");
        sb.append("  Total number of sv status messages processed, where sv is used in fix: ");
        sb.append(this.mNumSvStatusUsedInFix);
        sb.append("\n");
        sb.append("  Total number of L5 sv status messages processed, where sv is used in fix: ");
        sb.append(this.mNumL5SvStatusUsedInFix);
        sb.append("\n");
        sb.append("  Number of L5 CN0 reports: ");
        sb.append(this.mTopFourAverageCn0StatisticsL5.getCount());
        sb.append("\n");
        if (this.mTopFourAverageCn0StatisticsL5.getCount() > 0) {
            sb.append("  L5 Top 4 Avg CN0 mean (dB-Hz): ");
            sb.append(this.mTopFourAverageCn0StatisticsL5.getMean());
            sb.append("\n");
            sb.append("  L5 Top 4 Avg CN0 standard deviation (dB-Hz): ");
            sb.append(this.mTopFourAverageCn0StatisticsL5.getStandardDeviation());
            sb.append("\n");
        }
        sb.append("  Used-in-fix constellation types: ");
        int i = 0;
        while (true) {
            boolean[] zArr = this.mConstellationTypes;
            if (i >= zArr.length) {
                break;
            }
            if (zArr[i]) {
                sb.append(GnssStatus.constellationTypeToString(i));
                sb.append(" ");
            }
            i++;
        }
        sb.append("\n");
        sb.append("GNSS_KPI_END");
        sb.append("\n");
        GpsBatteryStats gpsBatteryStats = this.mGnssPowerMetrics.getGpsBatteryStats();
        if (gpsBatteryStats != null) {
            sb.append("Power Metrics");
            sb.append("\n");
            sb.append("  Time on battery (min): ");
            sb.append(gpsBatteryStats.getLoggingDurationMs() / 60000.0d);
            sb.append("\n");
            long[] timeInGpsSignalQualityLevel = gpsBatteryStats.getTimeInGpsSignalQualityLevel();
            if (timeInGpsSignalQualityLevel != null && timeInGpsSignalQualityLevel.length == 2) {
                sb.append("  Amount of time (while on battery) Top 4 Avg CN0 > 20.0 dB-Hz (min): ");
                sb.append(timeInGpsSignalQualityLevel[1] / 60000.0d);
                sb.append("\n");
                sb.append("  Amount of time (while on battery) Top 4 Avg CN0 <= 20.0 dB-Hz (min): ");
                sb.append(timeInGpsSignalQualityLevel[0] / 60000.0d);
                sb.append("\n");
            }
            sb.append("  Energy consumed while on battery (mAh): ");
            sb.append(gpsBatteryStats.getEnergyConsumedMaMs() / 3600000.0d);
            sb.append("\n");
        }
        sb.append("Hardware Version: ");
        sb.append(SystemProperties.get("ro.boot.revision", ""));
        sb.append("\n");
        return sb.toString();
    }

    public final void reset() {
        this.mLogStartInElapsedRealtimeMs = SystemClock.elapsedRealtime();
        this.mLocationFailureStatistics.reset();
        this.mTimeToFirstFixSecStatistics.reset();
        this.mPositionAccuracyMeterStatistics.reset();
        this.mTopFourAverageCn0Statistics.reset();
        resetConstellationTypes();
        this.mTopFourAverageCn0StatisticsL5.reset();
        this.mNumSvStatus = 0;
        this.mNumL5SvStatus = 0;
        this.mNumSvStatusUsedInFix = 0;
        this.mNumL5SvStatusUsedInFix = 0;
    }

    public void resetConstellationTypes() {
        this.mConstellationTypes = new boolean[8];
    }

    /* loaded from: classes.dex */
    public static class Statistics {
        public int mCount;
        public long mLongSum;
        public double mSum;
        public double mSumSquare;

        public synchronized void reset() {
            this.mCount = 0;
            this.mSum = 0.0d;
            this.mSumSquare = 0.0d;
            this.mLongSum = 0L;
        }

        public synchronized void addItem(double d) {
            this.mCount++;
            this.mSum += d;
            this.mSumSquare += d * d;
            this.mLongSum = (long) (this.mLongSum + d);
        }

        public synchronized int getCount() {
            return this.mCount;
        }

        public synchronized double getMean() {
            return this.mSum / this.mCount;
        }

        public synchronized double getStandardDeviation() {
            double d = this.mSum;
            int i = this.mCount;
            double d2 = d / i;
            double d3 = d2 * d2;
            double d4 = this.mSumSquare / i;
            if (d4 > d3) {
                return Math.sqrt(d4 - d3);
            }
            return 0.0d;
        }

        public synchronized long getLongSum() {
            return this.mLongSum;
        }
    }

    /* loaded from: classes.dex */
    public class GnssPowerMetrics {
        public final IBatteryStats mBatteryStats;
        public double mLastAverageCn0 = -100.0d;
        public int mLastSignalLevel = -1;

        public final int getSignalLevel(double d) {
            return d > 20.0d ? 1 : 0;
        }

        public GnssPowerMetrics(IBatteryStats iBatteryStats) {
            this.mBatteryStats = iBatteryStats;
        }

        public GnssLogsProto.PowerMetrics buildProto() {
            GnssLogsProto.PowerMetrics powerMetrics = new GnssLogsProto.PowerMetrics();
            GpsBatteryStats gpsBatteryStats = GnssMetrics.this.mGnssPowerMetrics.getGpsBatteryStats();
            if (gpsBatteryStats != null) {
                powerMetrics.loggingDurationMs = gpsBatteryStats.getLoggingDurationMs();
                powerMetrics.energyConsumedMah = gpsBatteryStats.getEnergyConsumedMaMs() / 3600000.0d;
                long[] timeInGpsSignalQualityLevel = gpsBatteryStats.getTimeInGpsSignalQualityLevel();
                long[] jArr = new long[timeInGpsSignalQualityLevel.length];
                powerMetrics.timeInSignalQualityLevelMs = jArr;
                System.arraycopy(timeInGpsSignalQualityLevel, 0, jArr, 0, timeInGpsSignalQualityLevel.length);
            }
            return powerMetrics;
        }

        public GpsBatteryStats getGpsBatteryStats() {
            try {
                return this.mBatteryStats.getGpsBatteryStats();
            } catch (RemoteException e) {
                Log.w("GnssMetrics", e);
                return null;
            }
        }

        public void reportSignalQuality(float[] fArr) {
            double d = 0.0d;
            if (fArr != null && fArr.length > 0) {
                for (int max = Math.max(0, fArr.length - 4); max < fArr.length; max++) {
                    d += fArr[max];
                }
                d /= Math.min(fArr.length, 4);
            }
            if (Math.abs(d - this.mLastAverageCn0) < 1.0d) {
                return;
            }
            int signalLevel = getSignalLevel(d);
            if (signalLevel != this.mLastSignalLevel) {
                FrameworkStatsLog.write(69, signalLevel);
                this.mLastSignalLevel = signalLevel;
            }
            try {
                this.mBatteryStats.noteGpsSignalQuality(signalLevel);
                this.mLastAverageCn0 = d;
            } catch (RemoteException e) {
                Log.w("GnssMetrics", e);
            }
        }
    }

    public final void registerGnssStats() {
        StatsPullAtomCallbackImpl statsPullAtomCallbackImpl = new StatsPullAtomCallbackImpl();
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.GNSS_STATS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, statsPullAtomCallbackImpl);
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.GNSS_POWER_STATS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, statsPullAtomCallbackImpl);
    }

    /* loaded from: classes.dex */
    public class StatsPullAtomCallbackImpl implements StatsManager.StatsPullAtomCallback {
        public StatsPullAtomCallbackImpl() {
        }

        public int onPullAtom(int i, List<StatsEvent> list) {
            if (i == 10074) {
                long longSum = GnssMetrics.this.mTopFourAverageCn0DbmHzReportsStatistics.getLongSum();
                long count = GnssMetrics.this.mL5TopFourAverageCn0DbmHzReportsStatistics.getCount();
                long longSum2 = GnssMetrics.this.mL5TopFourAverageCn0DbmHzReportsStatistics.getLongSum();
                GnssMetrics gnssMetrics = GnssMetrics.this;
                list.add(FrameworkStatsLog.buildStatsEvent(i, GnssMetrics.this.mLocationFailureReportsStatistics.getCount(), GnssMetrics.this.mLocationFailureReportsStatistics.getLongSum(), GnssMetrics.this.mTimeToFirstFixMilliSReportsStatistics.getCount(), GnssMetrics.this.mTimeToFirstFixMilliSReportsStatistics.getLongSum(), GnssMetrics.this.mPositionAccuracyMetersReportsStatistics.getCount(), GnssMetrics.this.mPositionAccuracyMetersReportsStatistics.getLongSum(), GnssMetrics.this.mTopFourAverageCn0DbmHzReportsStatistics.getCount(), longSum, count, longSum2, gnssMetrics.mSvStatusReports, gnssMetrics.mSvStatusReportsUsedInFix, gnssMetrics.mL5SvStatusReports, gnssMetrics.mL5SvStatusReportsUsedInFix));
                return 0;
            } else if (i == 10101) {
                GnssMetrics.this.mGnssNative.requestPowerStats();
                GnssPowerStats powerStats = GnssMetrics.this.mGnssNative.getPowerStats();
                if (powerStats == null) {
                    return 1;
                }
                double[] dArr = new double[10];
                double[] otherModesEnergyMilliJoule = powerStats.getOtherModesEnergyMilliJoule();
                if (otherModesEnergyMilliJoule.length < 10) {
                    System.arraycopy(otherModesEnergyMilliJoule, 0, dArr, 0, otherModesEnergyMilliJoule.length);
                } else {
                    System.arraycopy(otherModesEnergyMilliJoule, 0, dArr, 0, 10);
                }
                list.add(FrameworkStatsLog.buildStatsEvent(i, (long) powerStats.getElapsedRealtimeUncertaintyNanos(), (long) (powerStats.getTotalEnergyMilliJoule() * 1000.0d), (long) (powerStats.getSinglebandTrackingModeEnergyMilliJoule() * 1000.0d), (long) (powerStats.getMultibandTrackingModeEnergyMilliJoule() * 1000.0d), (long) (powerStats.getSinglebandAcquisitionModeEnergyMilliJoule() * 1000.0d), (long) (powerStats.getMultibandAcquisitionModeEnergyMilliJoule() * 1000.0d), (long) (dArr[0] * 1000.0d), (long) (dArr[1] * 1000.0d), (long) (dArr[2] * 1000.0d), (long) (dArr[3] * 1000.0d), (long) (dArr[4] * 1000.0d), (long) (dArr[5] * 1000.0d), (long) (dArr[6] * 1000.0d), (long) (dArr[7] * 1000.0d), (long) (dArr[8] * 1000.0d), (long) (dArr[9] * 1000.0d)));
                return 0;
            } else {
                throw new UnsupportedOperationException("Unknown tagId = " + i);
            }
        }
    }
}
