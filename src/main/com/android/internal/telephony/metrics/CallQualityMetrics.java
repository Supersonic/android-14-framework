package com.android.internal.telephony.metrics;

import android.telephony.CallQuality;
import android.telephony.CellSignalStrengthLte;
import android.telephony.SignalStrength;
import android.util.Pair;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.SignalStrengthController;
import com.android.internal.telephony.data.KeepaliveStatus;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyCallSession;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
/* loaded from: classes.dex */
public class CallQualityMetrics {
    private static final boolean IS_DEBUGGABLE = TelephonyUtils.IS_DEBUGGABLE;
    private static final String TAG = "CallQualityMetrics";
    private Pair<CallQuality, Integer> mBestSsWithBadDlQuality;
    private Pair<CallQuality, Integer> mBestSsWithBadUlQuality;
    private Pair<CallQuality, Integer> mBestSsWithGoodDlQuality;
    private Pair<CallQuality, Integer> mBestSsWithGoodUlQuality;
    private Phone mPhone;
    private Pair<CallQuality, Integer> mWorstSsWithBadDlQuality;
    private Pair<CallQuality, Integer> mWorstSsWithBadUlQuality;
    private Pair<CallQuality, Integer> mWorstSsWithGoodDlQuality;
    private Pair<CallQuality, Integer> mWorstSsWithGoodUlQuality;
    private ArrayList<Pair<CallQuality, Integer>> mUlSnapshots = new ArrayList<>();
    private ArrayList<Pair<CallQuality, Integer>> mDlSnapshots = new ArrayList<>();
    private ArrayList<TimestampedQualitySnapshot> mFullUplinkQuality = new ArrayList<>();
    private ArrayList<TimestampedQualitySnapshot> mFullDownlinkQuality = new ArrayList<>();
    private int mDlCallQualityState = 0;
    private int mUlCallQualityState = 0;
    private CallQuality mLastCallQuality = new CallQuality();

    private static boolean isGoodQuality(int i) {
        return i < 4;
    }

    public CallQualityMetrics(Phone phone) {
        this.mPhone = phone;
    }

    public void saveCallQuality(CallQuality callQuality) {
        if (callQuality.getUplinkCallQualityLevel() == 5 || callQuality.getDownlinkCallQualityLevel() == 5) {
            return;
        }
        int i = !isGoodQuality(callQuality.getUplinkCallQualityLevel()) ? 1 : 0;
        int i2 = !isGoodQuality(callQuality.getDownlinkCallQualityLevel()) ? 1 : 0;
        if (IS_DEBUGGABLE) {
            if (i != this.mUlCallQualityState) {
                addSnapshot(callQuality, this.mUlSnapshots);
            }
            if (i2 != this.mDlCallQualityState) {
                addSnapshot(callQuality, this.mDlSnapshots);
            }
        }
        updateTotalDurations(callQuality);
        updateMinAndMaxSignalStrengthSnapshots(i2, i, callQuality);
        this.mUlCallQualityState = i;
        this.mDlCallQualityState = i2;
        if (callQuality.getCallDuration() > this.mLastCallQuality.getCallDuration()) {
            this.mLastCallQuality = callQuality;
        }
    }

    private void updateTotalDurations(CallQuality callQuality) {
        this.mFullDownlinkQuality.add(new TimestampedQualitySnapshot(callQuality.getCallDuration(), callQuality.getDownlinkCallQualityLevel()));
        this.mFullUplinkQuality.add(new TimestampedQualitySnapshot(callQuality.getCallDuration(), callQuality.getUplinkCallQualityLevel()));
    }

    private void addSnapshot(CallQuality callQuality, ArrayList<Pair<CallQuality, Integer>> arrayList) {
        if (arrayList.size() < 5) {
            arrayList.add(Pair.create(callQuality, getLteSnr()));
        }
    }

    private void updateMinAndMaxSignalStrengthSnapshots(int i, int i2, CallQuality callQuality) {
        Integer lteSnr = getLteSnr();
        if (lteSnr.equals(Integer.valueOf((int) KeepaliveStatus.INVALID_HANDLE))) {
            return;
        }
        if (i == 0) {
            if (this.mWorstSsWithGoodDlQuality == null || lteSnr.intValue() < ((Integer) this.mWorstSsWithGoodDlQuality.second).intValue()) {
                this.mWorstSsWithGoodDlQuality = Pair.create(callQuality, lteSnr);
            }
            if (this.mBestSsWithGoodDlQuality == null || lteSnr.intValue() > ((Integer) this.mBestSsWithGoodDlQuality.second).intValue()) {
                this.mBestSsWithGoodDlQuality = Pair.create(callQuality, lteSnr);
            }
        } else {
            if (this.mWorstSsWithBadDlQuality == null || lteSnr.intValue() < ((Integer) this.mWorstSsWithBadDlQuality.second).intValue()) {
                this.mWorstSsWithBadDlQuality = Pair.create(callQuality, lteSnr);
            }
            if (this.mBestSsWithBadDlQuality == null || lteSnr.intValue() > ((Integer) this.mBestSsWithBadDlQuality.second).intValue()) {
                this.mBestSsWithBadDlQuality = Pair.create(callQuality, lteSnr);
            }
        }
        if (i2 == 0) {
            if (this.mWorstSsWithGoodUlQuality == null || lteSnr.intValue() < ((Integer) this.mWorstSsWithGoodUlQuality.second).intValue()) {
                this.mWorstSsWithGoodUlQuality = Pair.create(callQuality, lteSnr);
            }
            if (this.mBestSsWithGoodUlQuality == null || lteSnr.intValue() > ((Integer) this.mBestSsWithGoodUlQuality.second).intValue()) {
                this.mBestSsWithGoodUlQuality = Pair.create(callQuality, lteSnr);
                return;
            }
            return;
        }
        if (this.mWorstSsWithBadUlQuality == null || lteSnr.intValue() < ((Integer) this.mWorstSsWithBadUlQuality.second).intValue()) {
            this.mWorstSsWithBadUlQuality = Pair.create(callQuality, lteSnr);
        }
        if (this.mBestSsWithBadUlQuality == null || lteSnr.intValue() > ((Integer) this.mBestSsWithBadUlQuality.second).intValue()) {
            this.mBestSsWithBadUlQuality = Pair.create(callQuality, lteSnr);
        }
    }

    private Integer getLteSnr() {
        SignalStrengthController signalStrengthController = this.mPhone.getDefaultPhone().getSignalStrengthController();
        Integer valueOf = Integer.valueOf((int) KeepaliveStatus.INVALID_HANDLE);
        if (signalStrengthController == null) {
            String str = TAG;
            Rlog.e(str, "getLteSnr: unable to get SSC for phone " + this.mPhone.getPhoneId());
            return valueOf;
        }
        SignalStrength signalStrength = signalStrengthController.getSignalStrength();
        if (signalStrength == null) {
            String str2 = TAG;
            Rlog.e(str2, "getLteSnr: unable to get SignalStrength for phone " + this.mPhone.getPhoneId());
            return valueOf;
        }
        for (CellSignalStrengthLte cellSignalStrengthLte : signalStrength.getCellSignalStrengths(CellSignalStrengthLte.class)) {
            int rssnr = cellSignalStrengthLte.getRssnr();
            if (rssnr != Integer.MAX_VALUE) {
                return Integer.valueOf(rssnr);
            }
        }
        return valueOf;
    }

    private static TelephonyProto$TelephonyCallSession.Event.SignalStrength toProto(int i) {
        TelephonyProto$TelephonyCallSession.Event.SignalStrength signalStrength = new TelephonyProto$TelephonyCallSession.Event.SignalStrength();
        signalStrength.lteSnr = i;
        return signalStrength;
    }

    public TelephonyProto$TelephonyCallSession.Event.CallQualitySummary getCallQualitySummaryDl() {
        TelephonyProto$TelephonyCallSession.Event.CallQualitySummary callQualitySummary = new TelephonyProto$TelephonyCallSession.Event.CallQualitySummary();
        Pair<Integer, Integer> totalGoodAndBadQualityTimeMs = getTotalGoodAndBadQualityTimeMs(this.mFullDownlinkQuality);
        callQualitySummary.totalGoodQualityDurationInSeconds = ((Integer) totalGoodAndBadQualityTimeMs.first).intValue() / 1000;
        callQualitySummary.totalBadQualityDurationInSeconds = ((Integer) totalGoodAndBadQualityTimeMs.second).intValue() / 1000;
        callQualitySummary.totalDurationWithQualityInformationInSeconds = this.mLastCallQuality.getCallDuration() / 1000;
        Pair<CallQuality, Integer> pair = this.mWorstSsWithGoodDlQuality;
        if (pair != null) {
            callQualitySummary.snapshotOfWorstSsWithGoodQuality = TelephonyMetrics.toCallQualityProto((CallQuality) pair.first);
            callQualitySummary.worstSsWithGoodQuality = toProto(((Integer) this.mWorstSsWithGoodDlQuality.second).intValue());
        }
        Pair<CallQuality, Integer> pair2 = this.mBestSsWithGoodDlQuality;
        if (pair2 != null) {
            callQualitySummary.snapshotOfBestSsWithGoodQuality = TelephonyMetrics.toCallQualityProto((CallQuality) pair2.first);
            callQualitySummary.bestSsWithGoodQuality = toProto(((Integer) this.mBestSsWithGoodDlQuality.second).intValue());
        }
        Pair<CallQuality, Integer> pair3 = this.mWorstSsWithBadDlQuality;
        if (pair3 != null) {
            callQualitySummary.snapshotOfWorstSsWithBadQuality = TelephonyMetrics.toCallQualityProto((CallQuality) pair3.first);
            callQualitySummary.worstSsWithBadQuality = toProto(((Integer) this.mWorstSsWithBadDlQuality.second).intValue());
        }
        Pair<CallQuality, Integer> pair4 = this.mBestSsWithBadDlQuality;
        if (pair4 != null) {
            callQualitySummary.snapshotOfBestSsWithBadQuality = TelephonyMetrics.toCallQualityProto((CallQuality) pair4.first);
            callQualitySummary.bestSsWithBadQuality = toProto(((Integer) this.mBestSsWithBadDlQuality.second).intValue());
        }
        callQualitySummary.snapshotOfEnd = TelephonyMetrics.toCallQualityProto(this.mLastCallQuality);
        return callQualitySummary;
    }

    public TelephonyProto$TelephonyCallSession.Event.CallQualitySummary getCallQualitySummaryUl() {
        TelephonyProto$TelephonyCallSession.Event.CallQualitySummary callQualitySummary = new TelephonyProto$TelephonyCallSession.Event.CallQualitySummary();
        Pair<Integer, Integer> totalGoodAndBadQualityTimeMs = getTotalGoodAndBadQualityTimeMs(this.mFullUplinkQuality);
        callQualitySummary.totalGoodQualityDurationInSeconds = ((Integer) totalGoodAndBadQualityTimeMs.first).intValue() / 1000;
        callQualitySummary.totalBadQualityDurationInSeconds = ((Integer) totalGoodAndBadQualityTimeMs.second).intValue() / 1000;
        callQualitySummary.totalDurationWithQualityInformationInSeconds = this.mLastCallQuality.getCallDuration() / 1000;
        Pair<CallQuality, Integer> pair = this.mWorstSsWithGoodUlQuality;
        if (pair != null) {
            callQualitySummary.snapshotOfWorstSsWithGoodQuality = TelephonyMetrics.toCallQualityProto((CallQuality) pair.first);
            callQualitySummary.worstSsWithGoodQuality = toProto(((Integer) this.mWorstSsWithGoodUlQuality.second).intValue());
        }
        Pair<CallQuality, Integer> pair2 = this.mBestSsWithGoodUlQuality;
        if (pair2 != null) {
            callQualitySummary.snapshotOfBestSsWithGoodQuality = TelephonyMetrics.toCallQualityProto((CallQuality) pair2.first);
            callQualitySummary.bestSsWithGoodQuality = toProto(((Integer) this.mBestSsWithGoodUlQuality.second).intValue());
        }
        Pair<CallQuality, Integer> pair3 = this.mWorstSsWithBadUlQuality;
        if (pair3 != null) {
            callQualitySummary.snapshotOfWorstSsWithBadQuality = TelephonyMetrics.toCallQualityProto((CallQuality) pair3.first);
            callQualitySummary.worstSsWithBadQuality = toProto(((Integer) this.mWorstSsWithBadUlQuality.second).intValue());
        }
        Pair<CallQuality, Integer> pair4 = this.mBestSsWithBadUlQuality;
        if (pair4 != null) {
            callQualitySummary.snapshotOfBestSsWithBadQuality = TelephonyMetrics.toCallQualityProto((CallQuality) pair4.first);
            callQualitySummary.bestSsWithBadQuality = toProto(((Integer) this.mBestSsWithBadUlQuality.second).intValue());
        }
        callQualitySummary.snapshotOfEnd = TelephonyMetrics.toCallQualityProto(this.mLastCallQuality);
        return callQualitySummary;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TimestampedQualitySnapshot implements Comparable<TimestampedQualitySnapshot> {
        int mCallQualityLevel;
        int mTimestampMs;

        TimestampedQualitySnapshot(int i, int i2) {
            this.mTimestampMs = i;
            this.mCallQualityLevel = i2;
        }

        @Override // java.lang.Comparable
        public int compareTo(TimestampedQualitySnapshot timestampedQualitySnapshot) {
            return this.mTimestampMs - timestampedQualitySnapshot.mTimestampMs;
        }

        public String toString() {
            return "mTimestampMs=" + this.mTimestampMs + " mCallQualityLevel=" + this.mCallQualityLevel;
        }
    }

    private Pair<Integer, Integer> getTotalGoodAndBadQualityTimeMs(ArrayList<TimestampedQualitySnapshot> arrayList) {
        Collections.sort(arrayList);
        Iterator<TimestampedQualitySnapshot> it = arrayList.iterator();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        while (it.hasNext()) {
            TimestampedQualitySnapshot next = it.next();
            int i4 = next.mTimestampMs - i3;
            if (isGoodQuality(next.mCallQualityLevel)) {
                i += i4;
            } else {
                i2 += i4;
            }
            i3 = next.mTimestampMs;
        }
        return Pair.create(Integer.valueOf(i), Integer.valueOf(i2));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[CallQualityMetrics phone ");
        sb.append(this.mPhone.getPhoneId());
        sb.append(" mUlSnapshots: {");
        Iterator<Pair<CallQuality, Integer>> it = this.mUlSnapshots.iterator();
        while (it.hasNext()) {
            Pair<CallQuality, Integer> next = it.next();
            sb.append(" {cq=");
            sb.append(next.first);
            sb.append(" ss=");
            sb.append(next.second);
            sb.append("}");
        }
        sb.append("}");
        sb.append(" mDlSnapshots:{");
        Iterator<Pair<CallQuality, Integer>> it2 = this.mDlSnapshots.iterator();
        while (it2.hasNext()) {
            Pair<CallQuality, Integer> next2 = it2.next();
            sb.append(" {cq=");
            sb.append(next2.first);
            sb.append(" ss=");
            sb.append(next2.second);
            sb.append("}");
        }
        sb.append("}");
        sb.append(" ");
        Pair<Integer, Integer> totalGoodAndBadQualityTimeMs = getTotalGoodAndBadQualityTimeMs(this.mFullDownlinkQuality);
        Pair<Integer, Integer> totalGoodAndBadQualityTimeMs2 = getTotalGoodAndBadQualityTimeMs(this.mFullUplinkQuality);
        sb.append(" TotalDlGoodQualityTimeMs: ");
        sb.append(totalGoodAndBadQualityTimeMs.first);
        sb.append(" TotalDlBadQualityTimeMs: ");
        sb.append(totalGoodAndBadQualityTimeMs.second);
        sb.append(" TotalUlGoodQualityTimeMs: ");
        sb.append(totalGoodAndBadQualityTimeMs2.first);
        sb.append(" TotalUlBadQualityTimeMs: ");
        sb.append(totalGoodAndBadQualityTimeMs2.second);
        sb.append("]");
        return sb.toString();
    }
}
